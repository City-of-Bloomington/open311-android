/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.util.Media;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
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
	public static final String SERVICE_CODE = "service_code";
	public static final String SERVICE_NAME = "service_name";
	// Global basic fields
	public static final String MEDIA        = "media";
	public static final String MEDIA_URL    = "media_url";
	public static final String LATITUDE     = "lat";
	public static final String LONGITUDE    = "long";
	public static final String ADDRESS      = "address";
	public static final String ADDRESS_STRING = "address_string";
	public static final String DESCRIPTION  = "description";
	// Personal Information fields
	public static final String EMAIL        = "email";
	public static final String DEVICE_ID    = "device_id";
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
	public  static final String SERVICE_REQUEST_ID = "service_request_id";
	public  static final String TOKEN              = "token";
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ssz";
	
    public static JSONObject                  sEndpoint;
	public static Boolean                     sReady = false;
	public static JSONArray                   sServiceList = null;
	public static HashMap<String, JSONObject> sServiceDefinitions;
	public static ArrayList<String>           sGroups;
	
	
	private static String mBaseUrl;
	private static String mJurisdiction;
	private static String mApiKey;
	
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
	 * @param current_server A single entry from /raw/available_servers
	 * @return
	 * Boolean
	 */
	public static Boolean setEndpoint(JSONObject current_server) {
		sReady         = false;
		mBaseUrl      = null;
		mJurisdiction = null;
		mApiKey       = null;
		sGroups       = new ArrayList<String>();
		sServiceList  = null;
		sServiceDefinitions = new HashMap<String, JSONObject>();
		
		try {
			mBaseUrl      = current_server.getString(URL);
			mJurisdiction = current_server.optString(JURISDICTION);
			mApiKey       = current_server.optString(API_KEY);
		} catch (JSONException e) {
			return false;
		}
		try {
			sServiceList = new JSONArray(loadStringFromUrl(getServiceListUrl()));
			
			// Go through all the services and pull out the seperate groups
			// Also, while we're running through, load any service_definitions
			String group = "";
			int len = sServiceList.length();
			for (int i=0; i<len; i++) {
				JSONObject s = sServiceList.getJSONObject(i);
				// Add groups to sGroups
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
		}
		catch (Exception e) {
		    e.printStackTrace();
		    return false;
		}
		sEndpoint = current_server;
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
		for (int i=0; i<len; i++) {
			try {
				JSONObject s = sServiceList.getJSONObject(i);
				if (s.optString("group").equals(group)) { services.add(s); }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return services;
	}
	
	/**
	 * @param service_code
	 * @return
	 * JSONObject
	 */
	public static JSONObject getServiceDefinition(String service_code) {
		try {
			return new JSONObject(loadStringFromUrl(getServiceDefinitionUrl(service_code)));
		}
		catch (Exception e) {
            // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * POST new service request data to the endpoint
	 * 
	 * The JSONObject should come from ServiceRequest.post_data
	 * 
     * In the JSON data:
     * All the keys should already be named correctly.  Attribute keys will
     * already be in the form of "attribute[code]".
     * Most attributes will just contain single values entered by the user;
     * however, MultiValueList attributes will be an array of the chosen values
     * We will need to iterate over MultiValueList values and add a seperate
     * pair to the POST for each value.
     * 
     * Media attributes will contain the URI to the image file.
     * 
	 * @param data JSON representation of user input
	 * @return JSONObject
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Open311Exception 
	 */
	public static JSONArray postServiceRequest(ServiceRequest sr, Context context, String mediaPath)
	        throws JSONException, ClientProtocolException, IOException, Open311Exception {
		HttpPost   request  = new HttpPost(mBaseUrl + "/requests.json");
		JSONArray  serviceRequests = null;
	    if (mediaPath != null) {
	        request.setEntity(prepareMultipartEntity(sr, context, mediaPath));
	    }
	    else {
	        request.setEntity(prepareUrlEncodedEntity(sr));
	    }
	    HttpResponse r = getClient().execute(request);
        String responseString = EntityUtils.toString(r.getEntity());
        
	    int status = r.getStatusLine().getStatusCode();
	    if (status == HttpStatus.SC_OK) {
	        serviceRequests = new JSONArray(responseString);		        
	    }
	    else {
	        // The server indicated some error.  See if they returned the
	        // error description as JSON
	        String dialogMessage;
	        try {
	            JSONArray errors = new JSONArray(responseString);
	            dialogMessage = errors.getJSONObject(0).getString(Open311.DESCRIPTION);
	        }
	        catch (JSONException e) {
	            switch (status) {
	                case 403:
	                    dialogMessage = context.getResources().getString(R.string.error_403);
	                    break;
	                default:
	                    dialogMessage = context.getResources().getString(R.string.failure_posting_service);
	            }
	        }
	        throw new Open311Exception(dialogMessage);
	    }
		return serviceRequests;
	}
	
	/**
	 * Prepares a POST that does not contain a media attachment
	 *  
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException UrlEncodedFormEntity
	 * @throws JSONException 
	 */
	private static UrlEncodedFormEntity prepareUrlEncodedEntity(ServiceRequest sr) throws UnsupportedEncodingException, JSONException {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        // This could cause a JSONException, but we let this one bubble up the stack
        // If we don't have a service_code, we don't have a valid POST
        pairs.add(new BasicNameValuePair(SERVICE_CODE, sr.service.getString(SERVICE_CODE)));
        
        if (mJurisdiction != null) {
            pairs.add(new BasicNameValuePair(JURISDICTION, mJurisdiction));
        }
        if (mApiKey != null) {
            pairs.add(new BasicNameValuePair(API_KEY, mApiKey));
        }
        
        JSONObject data = sr.post_data;
        Iterator<?>keys = data.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object o;
            try {
                o = data.get(key);
                // Add MULTIVALUELIST values
                if (o instanceof JSONArray) {
                    String k = key + "[]"; // Key name to POST multiple values
                    JSONArray values = (JSONArray) o;
                    int len = values.length();
                    for (int i=0; i<len; i++) {
                        try {
                            pairs.add(new BasicNameValuePair(k, values.getString(i)));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                // All other fields can just be plain key-value pairs
                else {
                    // Lat and Long need to be converted to string
                    if (o instanceof Double) {
                        o = Double.toString((Double)o);
                    }
                    pairs.add(new BasicNameValuePair(key, (String) o));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new UrlEncodedFormEntity(pairs);
	}
	
	/**
	 * Prepares a POST that includes a media attachment
	 * 
	 * @param data
	 * @param context
	 * @param mediaPath
	 * @return
	 * @throws UnsupportedEncodingException
	 * MultipartEntity
	 * @throws JSONException 
	 */
	private static MultipartEntity prepareMultipartEntity(ServiceRequest sr, Context context, String mediaPath) throws UnsupportedEncodingException, JSONException {
	    MultipartEntity post = new MultipartEntity();
        // This could cause a JSONException, but we let this one bubble up the stack
        // If we don't have a service_code, we don't have a valid POST
	    post.addPart(SERVICE_CODE, new StringBody(sr.service.getString(SERVICE_CODE)));
	    
        if (mJurisdiction != null) {
            post.addPart(JURISDICTION, new StringBody(mJurisdiction));
        }
        if (mApiKey != null) {
            post.addPart(API_KEY,      new StringBody(mApiKey));
        }
        JSONObject data = sr.post_data;
        Iterator<?>keys = data.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object o;
            try {
                o = data.get(key);
                // Attach media to the post
                // Do not read from the data object.
                // Instead, use the mediaPath that is passed in.
                // This relies on the fact that there can only be one media
                // attachment per ServiceRequest.
                if (key == MEDIA) {
                    final Bitmap media = Media.decodeSampledBitmap(mediaPath, Media.UPLOAD_WIDTH, Media.UPLOAD_HEIGHT, context);
                    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    media.compress(CompressFormat.PNG, 100, stream);
                    final byte[] binaryData = stream.toByteArray();
                    post.addPart(Open311.MEDIA, new ByteArrayBody(binaryData, Media.UPLOAD_FILENAME));
                }
                // Attach MULTIVALUELIST values
                else if (o instanceof JSONArray) {
                    String k = key + "[]"; // Key name to POST multiple values
                    JSONArray values = (JSONArray) o;
                    int len = values.length();
                    for (int i=0; i<len; i++) {
                        try {
                            post.addPart(k, new StringBody(values.getString(i)));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                // All other fields can be attached as plain key-value pairs
                else {
                    if (o instanceof Double) {
                        o = Double.toString((Double)o);
                    }
                    post.addPart(key, new StringBody((String) o));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return post;
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
		@SuppressWarnings("unused")
        int length;
		try {
			FileInputStream in = c.openFileInput(SAVED_REPORTS_FILE);
			while ((length = in.read(bytes)) != -1) {
				buffer.append(new String(bytes));
			}
			service_requests = new JSONArray(new String(buffer));
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
	 * @param c
	 * @param requests An array of JSON-serialized ServiceRequest objects
	 * void
	 */
	public static boolean saveServiceRequests(Context c, JSONArray requests) {
		String json = requests.toString();
		FileOutputStream out;
		try {
			out = c.openFileOutput(SAVED_REPORTS_FILE, Context.MODE_PRIVATE);
			out.write(json.getBytes());
			out.close();
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
	 * Adds a ServiceRequest to the collection of saved reports
	 * 
	 * Reports are stored as a file on the device internal storage
	 * The file is a serialized JSONArray of ServiceRequest objects.
	 * 
	 * @param report
	 * @return
	 * Boolean
	 */
	public static boolean saveServiceRequest(Context c, ServiceRequest sr) {
	    sr.endpoint = sEndpoint;
	    
        try {
            JSONObject report         = new JSONObject(sr.toString());
            JSONArray  saved_requests = loadServiceRequests(c);
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
	public static String loadStringFromUrl(String url)
			throws ClientProtocolException, IOException, IllegalStateException {
		HttpResponse r = getClient().execute(new HttpGet(url));
		String response = EntityUtils.toString(r.getEntity());
		
		return response;
	}
	
	
	/**
	 * @return
	 * String
	 */
	private static String getServiceListUrl() {
		return mBaseUrl + "/services.json?" + JURISDICTION + "=" + mJurisdiction;
	}
	
	/**
	 * @param service_code
	 * @return
	 * String
	 */
	private static String getServiceDefinitionUrl(String service_code) {
		return mBaseUrl + "/services/" + service_code + ".json?" + JURISDICTION + "=" + mJurisdiction;
	}
}
