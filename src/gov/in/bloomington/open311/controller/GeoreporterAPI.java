/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/*
 * Business (controller) class which contain function regarding interaction with open311 server
 */
public class GeoreporterAPI {
	private transient final String serverUrl;
	private transient final String apiKey;
	
	/** Constructor */
	public GeoreporterAPI(final Activity act) {
		super();
		final SharedPreferences pref = act.getSharedPreferences("server",0);
		String serverUrlLocal="";
		String apiKeyLocal="";
		try {
			final JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
			serverUrlLocal = server.getString("url");
			apiKeyLocal = server.getString("api_key");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI constructor", e.toString());
		}
		serverUrl = serverUrlLocal;
		apiKey = apiKeyLocal;
	}
	
	
	/**Check whether connection to server is established or not.
	 * Un-established connection can be occur when there's no internet connection or the server is not available  */
	public boolean isConnected() {
		final HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;

        //get server URL from shared preferences
		boolean is_connected = false;
		try {
			final HttpPost post1 = new HttpPost(serverUrl);
			Log.d("GeoreporterAPI", "GeoreporterAPI "+serverUrl);
		    response = client.execute(post1);

			if (response==null) {
	        	is_connected = false;
	        }
	        else {
	        	is_connected = true;
	        }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI isConnected", e.toString());
			is_connected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI isConnected", e.toString());
			is_connected = false;
		}
        
        return is_connected;
	}
	
	/**Get JSON Array of services from current server */
	public JSONArray getServices() {
		JSONArray services_list = null;
		try {
			final HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
	        HttpResponse response;
	        final HttpGet get = new HttpGet(serverUrl+"/services.json");
            response = client.execute(get);
            /*Checking response */
            if(response!=null){
                final InputStream inputS = response.getEntity().getContent(); //Get the data in the entity
                final GeoreporterUtils georeporterU = new GeoreporterUtils();
                final String str = georeporterU.convertStreamToString(inputS);
                services_list = new JSONArray(str);
            }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI getServices", e.toString());
		}
		 catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
			 Log.e("GeoreporterAPI getServices", e.toString());
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	Log.e("GeoreporterAPI getServices", e.toString());
        }
		return services_list;
	}
	
	/**Get service attribute from a particular service */
	public JSONObject getServiceAttribute(final String serviceCode) {
		JSONObject servicesAttribute = null;
		try {
			final HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
	        HttpResponse response;
	        
	        final HttpGet get = new HttpGet(serverUrl+"/services/"+serviceCode+".json");
            response = client.execute(get);
            /*Checking response */
            if(response!=null){
                final InputStream inputS = response.getEntity().getContent(); //Get the data in the entity
                final GeoreporterUtils georeporterU = new GeoreporterUtils();
                final String str = georeporterU.convertStreamToString(inputS);
                servicesAttribute = new JSONObject(str);
            }
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI getServiceAttribute", e.toString());
		}
		catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
			Log.e("GeoreporterAPI getServiceAttribute", e.toString());
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	Log.e("GeoreporterAPI getServiceAttribute", e.toString());
        }
		return servicesAttribute;
	}
	
	/**Send report to a particular server */
	//public JSONArray sendReport(final String jurisdiction_id, final String service_code, final Double latitude,final Double longitude, final boolean hasattribute, final List<NameValuePair> attribute, final String email, final String device_id, final String first_name, final String last_name, final String phone, final String description) {
	public JSONArray sendReport(final List<NameValuePair> pairs) {
		JSONArray reply = new JSONArray(); 
		final HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
		try {
			/*final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("jurisdiction_id", jurisdiction_id));
	        pairs.add(new BasicNameValuePair("service_code", service_code));
	        pairs.add(new BasicNameValuePair("lat", latitude.toString()));
	        pairs.add(new BasicNameValuePair("long", longitude.toString()));
	        if (hasattribute) {
	        	pairs.add(new BasicNameValuePair("attribute", attribute.toString()));
	        }
	        pairs.add(new BasicNameValuePair("email", email));
	        pairs.add(new BasicNameValuePair("device_id", device_id));
	        pairs.add(new BasicNameValuePair("first_name", first_name));
	        pairs.add(new BasicNameValuePair("last_name", last_name));
	        pairs.add(new BasicNameValuePair("phone", phone));
	        pairs.add(new BasicNameValuePair("description", description));*/
			
			pairs.add(new BasicNameValuePair("api_key", apiKey));
			
	        final HttpPost post1 = new HttpPost(serverUrl+"/requests.json");
            post1.setEntity(new UrlEncodedFormEntity(pairs));
            
            response = client.execute(post1);
	        
            if(response!=null){
                final InputStream inputS = response.getEntity().getContent(); //Get the data in the entity
                final GeoreporterUtils georeporterU = new GeoreporterUtils();
                final String str = georeporterU.convertStreamToString(inputS);
                reply = new JSONArray(str);
                //service_request_id = reply.getJSONObject(0).getString("service_request_id");
            }
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReport", e.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReport", e.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReport_", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReport_", e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReport_", e.toString());
		}
		
		
		return reply;
	}
	
	/**Send report to a particular server with picture*/
	//public JSONArray sendReportWithPicture (final Bitmap bmp, final String jurisdiction_id, final String service_code, final Double latitude,final Double longitude, final boolean hasattribute, final List<NameValuePair> attribute, final String email, final String device_id, final String first_name, final String last_name, final String phone, final String description) {
	public JSONArray sendReportWithPicture(final MultipartEntity entity) {
		/*final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	bmp.compress(CompressFormat.JPEG, 50, bos);
    	final byte[] data = bos.toByteArray();*/
		
		JSONArray reply = new JSONArray(); 
		final HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        
		try {
			/*final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        	entity.addPart("media", new ByteArrayBody(data,"photo.jpg"));
			entity.addPart("jurisdiction_id", new StringBody(jurisdiction_id));
            entity.addPart("service_code", new StringBody(service_code));
            entity.addPart("lat", new StringBody(latitude.toString()));
            entity.addPart("long", new StringBody(longitude.toString()));
            if (hasattribute) {
            	entity.addPart("attribute", new StringBody(attribute.toString()));
            }
            entity.addPart("email", new StringBody(email));
            entity.addPart("device_id", new StringBody(device_id));
            entity.addPart("first_name", new StringBody(first_name));
            entity.addPart("last_name", new StringBody(last_name));
            entity.addPart("phone", new StringBody(phone));
            entity.addPart("description", new StringBody(description));*/
			
			entity.addPart("api_key", new StringBody(apiKey));
			
	        final HttpPost post1 = new HttpPost(serverUrl+"/requests.json");
            post1.setEntity(entity);
            
            response = client.execute(post1);
	        
            if(response!=null){
                final InputStream inputS = response.getEntity().getContent(); //Get the data in the entity
                final GeoreporterUtils georeporterU = new GeoreporterUtils();
                final String str = georeporterU.convertStreamToString(inputS);
                reply = new JSONArray(str);
                Log.d("georeporter API", reply.toString());
                //service_request_id = reply.getJSONObject(0).getString("service_request_id");
            }
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReportWithPicture", e.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReportWithPicture", e.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReportWithPicture_", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReportWithPicture_", e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI sendReportWithPicture_", e.toString());
		}
		
		
		return reply;
	}
	
	/**Get a particular service request */
	public JSONArray getServiceRequests(final String jurisdiction_id, final String serviceReqId) {
		JSONArray services_request = null;
		try {
			final HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
	        HttpResponse response;
	        final HttpGet get = new HttpGet(serverUrl+"/requests.json?jurisdiction_id="+jurisdiction_id+"&service_request_id="+serviceReqId);
            response = client.execute(get);
            /*Checking response */
            if(response!=null){
                final InputStream inputStream = response.getEntity().getContent(); //Get the data in the entity
                final GeoreporterUtils georeporterU = new GeoreporterUtils();
                final String str = georeporterU.convertStreamToString(inputStream);
                services_request = new JSONArray(str);
            }
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("GeoreporterAPI getServiceRequests", e.toString());
		}
		 catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
			 Log.e("GeoreporterAPI getServiceRequests", e.toString());
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	Log.e("GeoreporterAPI getServiceRequests", e.toString());
        }
		return services_request;
	}
	
}


