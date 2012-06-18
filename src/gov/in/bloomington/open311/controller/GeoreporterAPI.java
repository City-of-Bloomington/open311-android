package gov.in.bloomington.open311.controller;

import gov.in.bloomington.open311.model.ConnectionDispatcher;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class GeoreporterAPI {
	public static boolean isConnected(Activity a) {
		HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;

        //get server URL from shared preferences
        SharedPreferences pref = a.getSharedPreferences("server",0);
		JSONObject server;
		boolean is_connected = false;
		try {
			server = new JSONObject(pref.getString("selectedServer", ""));
			String server_url = server.getString("url");
			HttpPost post1 = new HttpPost(server_url);
		    
		    response = client.execute(post1);

			if (response!=null) {

	        	is_connected = true;;
	        }
	        else {

	        	is_connected = false;
	        }
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			is_connected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			is_connected = false;
		}
        
        return is_connected;
	}
	
	public static JSONArray streamServices(Activity a) {
		SharedPreferences pref = a.getSharedPreferences("server",0);
		JSONArray services_list = null;
		JSONObject server;
		try {
			server = new JSONObject(pref.getString("selectedServer", ""));
			String server_url = server.getString("url");
			services_list = ConnectionDispatcher.getJSONArray(server_url+"/services.json");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return services_list;
	}
	
	public static JSONObject getServiceAttribute(Activity a, String service_code) {
		SharedPreferences pref = a.getSharedPreferences("server",0);
		JSONObject services_attribute = null;
		JSONObject server;
		try {
			server = new JSONObject(pref.getString("selectedServer", ""));
			String server_url = server.getString("url");
			Log.d("georeporter_api", "tes 1"+server_url+"/services/"+service_code+".json");
			services_attribute = ConnectionDispatcher.getJSONObject(server_url+"/services/"+service_code+".json");
			
			Log.d("georeporter_api", "tes 2"+services_attribute.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return services_attribute;
	}
}
