package gov.in.bloomington.open311.model;

import gov.in.bloomington.open311.controller.GeoreporterUtils;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;

public class ConnectionDispatcher {
	public static JSONArray get(String URLWithParam) {
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
}
