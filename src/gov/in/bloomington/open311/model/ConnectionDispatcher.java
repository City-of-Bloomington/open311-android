package gov.in.bloomington.open311.model;

import gov.in.bloomington.open311.controller.GeoreporterUtils;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnectionDispatcher {
	public static JSONArray getJSONArray(String URLWithParam) {
    	JSONArray kosong = new JSONArray(); 
		
		HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        try{
            HttpGet get = new HttpGet(URLWithParam);
            response = client.execute(get);
            /*Checking response */
            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
                String str = GeoreporterUtils.convertStreamToString(in);
                JSONArray json1 = new JSONArray(str);
                return json1;
            }
            else {
            	return kosong;
            }

        }
        catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
        	return kosong;
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	return kosong;
        }
	}
	
	public static JSONObject getJSONObject(String URLWithParam) {
    	JSONObject kosong = new JSONObject(); 
		
		HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        try{
            HttpGet get = new HttpGet(URLWithParam);
            response = client.execute(get);
            /*Checking response */
            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
                String str = GeoreporterUtils.convertStreamToString(in);
                JSONObject json1 = new JSONObject(str);
                return json1;
            }
            else {
            	return kosong;
            }

        }
        catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
        	return kosong;
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	return kosong;
        }
	}
	
	public static JSONObject post(String URL,List<NameValuePair> pairs) {
    	JSONObject kosong = new JSONObject(); 

		HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        try{
            HttpPost post1 = new HttpPost(URL);
            post1.setEntity(new UrlEncodedFormEntity(pairs));
            
            response = client.execute(post1);
            //int statusCode = response.getStatusLine().getStatusCode();
            
            /*Checking response */
            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
                String str = GeoreporterUtils.convertStreamToString(in);
                JSONObject json1 = new JSONObject(str);
                
                return json1;

        }
        }
        catch (ClientProtocolException e) {
        	// TODO Auto-generated catch block
        	return kosong;
        }
        catch(Exception e){
        	// TODO Auto-generated catch block
        	return kosong;
        }
        
        return kosong;
	}
}
