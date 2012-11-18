/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

import gov.in.bloomington.georeporter.util.Open311Parser;
import gov.in.bloomington.georeporter.util.Open311XmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

public class Open311 {
	/**
	 * Constants for Open311 keys
	 * 
	 * I'm tired of making typos in key names
	 */
	// Global required fields
	public static final String JURISDICTION = "jurisdiction_id";
	public static final String API_KEY      = "api_key";
	public static final String FORMAT      = "format";
	public static final String SERVICE_CODE = "service_code";
	public static final String SERVICE_NAME = "service_name";
	// Global basic fields
	public static final String MEDIA        = "media";
	public static final String MEDIA_URL    = "media_url";
	public static final String LATITUDE     = "lat";
	public static final String LONGITUDE    = "long";
	public static final String ADDRESS      = "address_string";
	public static final String DESCRIPTION  = "description";
	public static final String SERVICE_NOTICE = "service_notice";
	public static final String GROUP = "group";
	public static final String REQUESTED_DATETIME = "requested_datetime";
	// Personal Information fields
	public static final String EMAIL        = "email";
	public static final String DEVICE_ID    = "devide_id";
	public static final String FIRST_NAME   = "first_name";
	public static final String LAST_NAME    = "last_name";
	public static final String PHONE        = "phone";
	// Custom field definition in service_definition
	public static final String METADATA     = "metadata";
	public static final String ATTRIBUTES   = "attributes";
	public static final String VARIABLE     = "variable";
	public static final String CODE         = "code";
	public static final String ORDER        = "order";
	public static final String VALUES       = "values";
	public static final String VALUE       = "value";
	public static final String KEY          = "key";
	public static final String NAME         = "name";
	public static final String REQUIRED     = "required";
	public static final String DATATYPE     = "datatype";
	public static final String STRING       = "string";
	public static final String NUMBER       = "number";
	public static final String DATETIME     = "datetime";
	public static final String TEXT         = "text";
	public static final String SINGLEVALUELIST = "singlevaluelist";
	public static final String MULTIVALUELIST  = "multivaluelist";
	// Key names from /res/raw/available_servers.json
	public static final String URL            = "url";
	public static final String SUPPORTS_MEDIA = "supports_media";
	// Key names for the saved reports file
	private static final String SAVED_REPORTS_FILE = "service_requests";
	public  static final String SERVER             = "server";
	public  static final String SERVICE_REQUEST    = "service_request";
	public  static final String SERVICE_REQUEST_ID = "service_request_id";
	public  static final String TOKEN              = "token";
	// Key names for formats
	public 	static final String JSON = "json";
	public  static final String XML = "xml";

	
	
	public static Boolean                     sReady = false;
	public static JSONArray                   sServiceList = null;
	public static HashMap<String, JSONObject> sServiceDefinitions;
	public static ArrayList<String>           sGroups;
	
	private static JSONObject mEndpoint;
	private static String mBaseUrl;
	private static String mJurisdiction;
	private static String mApiKey;
	public static String mFormat = "json";

	private static DefaultHttpClient mClient = null;
	private static final int TIMEOUT = 3000;
	
	
	private static Open311 mInstance;
	private Open311() {}
	public static synchronized Open311 getInstance() {
		if (mInstance == null) {
			mInstance = new Open311();
		}
		return mInstance;
	}
	
	/**
	 * Lazy load an Http client
	 * 
	 * @return
	 * DefaultHttpClient
	 */
	private static DefaultHttpClient getClient() {
		if (mClient == null) {
			mClient = new DefaultHttpClient();
			mClient.getParams().setParameter(CoreProtocolPNames  .HTTP_CONTENT_CHARSET, "UTF-8");
			mClient.getParams().setParameter(CoreProtocolPNames  .PROTOCOL_VERSION,     HttpVersion.HTTP_1_1);
			mClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,           TIMEOUT);
			mClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,   TIMEOUT);
		}
		return mClient;
	}
	
	/**
	 * Loads all the service information from the endpoint
	 * 
	 * Endpoints will have a service_list, plus, for each
	 * service, there may be a service_definition.
	 * To make the user experience smoother, we are downloading
	 * and saving all the possible service information at once.
	 * 
	 * Returns false if there was a problem
	 * 
	 * @param current_server
	 * @return
	 * Boolean
	 */
	public static Boolean setEndpoint(JSONObject current_server) {
		sReady         = false;
		mBaseUrl      = null;
		mJurisdiction = null;
		mApiKey       = null;
		mFormat       = null;
		sGroups       = new ArrayList<String>();
		sServiceList  = null;
		sServiceDefinitions = new HashMap<String, JSONObject>();
		
		try {
			mBaseUrl      = current_server.getString(URL);
			mJurisdiction = current_server.optString(JURISDICTION);
			mApiKey       = current_server.optString(API_KEY);
			mFormat       = current_server.optString(FORMAT);
			Log.i("setEndpoint", mFormat);
		} catch (JSONException e) {
			return false;
		}
		try {
			Log.i("Open311 getServiceListUrl",getServiceListUrl());
			//sServiceList = new JSONArray(loadStringFromUrl(getServiceListUrl()));
			//Log.i("Open 311 bloo",sServiceList.toString());
			Open311Parser mParser= new Open311Parser(mFormat);
			sServiceList = mParser.parseServices(loadStringFromUrl(getServiceListUrl()));
			if (sServiceList == null) return false; 
			Log.i("Open 311 ned",sServiceList.toString());
			// Go through all the services and pull out the seperate groups
			// Also, while we're running through, load any service_definitions
			String group = "";
			int len = sServiceList.length();
			for (int i=0; i<len; i++) {
				JSONObject s = sServiceList.getJSONObject(i);
				// Add groups to mGroups
				group = s.optString("group");
				if (group != "" && !sGroups.contains(group)) { sGroups.add(group); }
				
				// Add Service Definitions to mServiceDefinitions
				if (s.optString("metadata") == "true") {
					String code = s.optString(SERVICE_CODE);
					JSONObject definition = getServiceDefinition(code);
					if (definition != null) {
						sServiceDefinitions.put(code, definition);
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		mEndpoint = current_server;
		sReady    = true;
		return sReady;
	}
	
	
	/**
	 * Returns the services for a given group
	 * 
	 * @param group
	 * @return
	 * ArrayList<JSONObject>
	 */
	public static ArrayList<JSONObject> getServices(String group) {
		ArrayList<JSONObject> services = new ArrayList<JSONObject>();
		int len = sServiceList.length();
		Log.i("Open 311 len",sServiceList.toString());
		Log.i("Open 311 len","len");
		for (int i=0; i<len; i++) {
			try {
				JSONObject s = sServiceList.getJSONObject(i);
				Log.i("Open 311 getServices",group);
				if (s.optString("group").equals(group)) { services.add(s); }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("services" , services.toString());
		return services;
	}
	
	/**
	 * @param service_code
	 * @return
	 * JSONObject
	 */
	public static JSONObject getServiceDefinition(String service_code) {
		try {
			Open311Parser mParser= new Open311Parser(mFormat);
			return mParser.parseServiceDefinition(loadStringFromUrl(getServiceDefinitionUrl(service_code)));
			//return new JSONObject(loadStringFromUrl(getServiceDefinitionUrl(service_code)));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * POST new service request data to the endpoint
	 * 
     * In the JSON data:
     * Attribute names will be the code from service_definition.
     * Most attributes will just contain single values entered by the user.
     * MultiValueList attributes will an array of the chosen values.
     * Media attributes will contain the URI to the image file.
     * 
	 * @param data JSON representation of user input
	 * @return
	 * JSONObject
	 */
	public static JSONArray postServiceRequest(JSONObject data) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (mJurisdiction != null) {
            pairs.add(new BasicNameValuePair(JURISDICTION, mJurisdiction));
        }
        if (mApiKey != null) {
            pairs.add(new BasicNameValuePair(API_KEY, mApiKey));
        }
        
        Iterator<?>keys = data.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object o;
            try {
                o = data.get(key);
                if (key.equals(MEDIA)) {
                    // TODO create an image bytestream for media
                }
                else if (o instanceof JSONArray) {
                    // MultiValueSelect
                    // TODO create entries for each of the chosen values 
                }
                else {
                    // TODO just add the value to pairs.
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       
		
		HttpPost  request  = new HttpPost(mBaseUrl + "/requests." + mFormat);
		JSONArray response = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse r = getClient().execute(request);
			String str = EntityUtils.toString(r.getEntity());
			Log.i("Open311 EntityUtils.toString", str);
			Open311Parser oparser= new Open311Parser(mFormat);
			response = oparser.parseRequests(str);
			//response = new JSONArray(EntityUtils.toString(r.getEntity()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return response;
	}
	
	/**
	 * Reads the saved reports file into a JSONArray
	 * 
	 * Reports are stored as a file on the device internal storage
	 * The file is a serialized JSONArray of reports.
	 * 
	 * @return
	 * JSONArray
	 */
	public static JSONArray loadServiceRequests(Context c) {
		JSONArray service_requests = new JSONArray();
		
		StringBuffer buffer = new StringBuffer("");
		byte[] bytes = new byte[1024];
		int length;
		try {
			File file = new File (c.getFilesDir(), SAVED_REPORTS_FILE);
			FileInputStream in = new FileInputStream(file); // Here
			while ((length = in.read(bytes)) != -1) {
				buffer.append(new String(bytes));
			}
			//out = c
			//FileInputStream in = c.openFileInput(SAVED_REPORTS_FILE);
			//while ((length = in.read(bytes)) != -1) {
			//	buffer.append(new String(bytes));
			//}
			String str = new String(buffer);
			Log.i("Open 311 loadServiceRequests",str);
			service_requests = new JSONArray(str);
		} catch (FileNotFoundException e) {
			Log.w("Open311.loadServiceRequests", "Saved Reports File does not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return service_requests;
	}
	
	/**
	 * Writes the stored reports back out the file
	 *
	 * Requests will be saved as JSON in the format of
	 * [
	 * 	{ server:          { },
	 * 	  service_request: { }
	 * 	},
	 *  { server:          { },
	 *    service_request: { }
	 *  }
	 * ]
	 * server: a copy of the endpoint information so we have
	 * enough information to make requests for up-to-date information
	 * 
	 * service_request: a cache of all the information from the report.
	 * This gets updated as we see new information from the server
	 * 
	 * @param c
	 * @param requests
	 * void
	 */
	private static boolean saveServiceRequests(Context c, JSONArray requests) {
		String json = requests.toString();
		//FileOutputStream out;
		try {
			File file = new File (c.getFilesDir(), SAVED_REPORTS_FILE);
			Writer out = new OutputStreamWriter(new FileOutputStream(file)); // Here
		    try
		    {
		        out.write(json);
		    } finally {
		        out.close();
		    }
			//out = c.openFileOutput(SAVED_REPORTS_FILE, Context.MODE_PRIVATE);
			//out.write(json.getBytes());
			//out.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Adds a service_request to the collection of saved reports
	 * 
	 * Reports are stored as a file on the device internal storage
	 * The file is a serialized JSONArray of reports.
	 * 
	 * Reports are in the form of:
	 *  { server:          { },
	 *    service_request: { }
	 *  }
	 * server: a copy of the endpoint information so we have
	 * enough information to make requests for up-to-date information
	 * 
	 * service_request: the json data returned by the server,
	 * obtained by calling GET Service Request
	 *  
	 * @param report
	 * @return
	 * Boolean
	 */
	public static boolean saveServiceRequest(Context c, JSONArray request) {
		JSONObject report = new JSONObject();
		try {
			report.put(SERVER, mEndpoint);
			report.put(SERVICE_REQUEST, request.getJSONObject(0));
			
			JSONArray saved_requests = loadServiceRequests(c);
			saved_requests.put(report);
			return saveServiceRequests(c, saved_requests);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Returns the response content from an HTTP request
	 * 
	 * @param url
	 * @return
	 * String
	 */
	private static String loadStringFromUrl(String url)
			throws ClientProtocolException, IOException, IllegalStateException {
		HttpResponse r = getClient().execute(new HttpGet(url));
		String response = EntityUtils.toString(r.getEntity());
		
		return response;
	}

	private static String loadXmlServices(){
	String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+"<services>"
			+"    <service>"
			+"        <service_code>001</service_code>"
			+"        <service_name>Afval</service_name>"
			+"        <description>Afval</description>"
			+"        <metadata>false</metadata>"
			+"        <type>realtime</type>"
			+"        <keywords>Afval</keywords>"
			+"        <group>Afval</group>"
			+"    </service>"
			+"    <service>"
			+"        <service_code>002</service_code>"
			+"        <service_name>Verlichting</service_name>"
			+"        <description>Verlichting</description>"
			+"        <metadata>false</metadata>"
			+"        <type>realtime</type>"
			+"        <keywords>Verlichting</keywords>"
			+"        <group>Verlichting</group>"
			+"    </service>"
			+"</services>";
	return str;
}
	private static String loadXmlServiceDefinition(){
		String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"	
			+"<service_definition>"
			+"<service_code>DMV66</service_code>"	
			+"<attributes>"
			+"<attribute>"
			+"	<variable>true</variable>"
			+"	<code>WHISHETN</code>"
			+"	<datatype>singlevaluelist</datatype>"
			+"	<required>true</required>"
			+"	<datatype_description></datatype_description>"		
			+"	<order>1</order>"	
			+"	<description>What is the ticket/tag/DL number?</description>"
			+"	<values>"
			+"		<value>"
			+"			<key>123</key>"
			+"			<name>Ford</name>"
			+"		</value>"
			+"		<value>"
			+"			<key>124</key>"
			+"			<name>Chrysler</name>"
			+"		</value>"			
			+"	</values>"
			+"</attribute>"	
			+"</attributes>"
			+"</service_definition>";
		return str;
	}
	
	/**
	 * @return
	 * String
	 */
	private static String getServiceListUrl() {
		return mBaseUrl + "/services." + mFormat + "?" + JURISDICTION + "=" + mJurisdiction;
	}
	
	/**
	 * @param service_code
	 * @return
	 * String
	 */
	private static String getServiceDefinitionUrl(String service_code) {
		return mBaseUrl + "/services/" + service_code + "." + mFormat + "?" + JURISDICTION + "=" + mJurisdiction;
	}
}
