package gov.in.bloomington.open311.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeoreporterUtils {
	public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
      }
	
	public static String getFromLocation(double lat, double lon, int maxResults) {
    	String urlStr = "http://maps.google.com/maps/geo?q=" + lat + "," + lon + "&output=json&sensor=false";
		String response = "";
		String results = "";
		HttpClient client = new DefaultHttpClient();
		
		try {
			HttpResponse hr = client.execute(new HttpGet(urlStr));
			HttpEntity entity = hr.getEntity();

			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

			String buff = null;
			while ((buff = br.readLine()) != null)
				response += buff;
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSONArray responseArray = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			responseArray = jsonObject.getJSONArray("Placemark");
		} catch (JSONException e) {
			return results;
		}
		
		for(int i = 0; i < responseArray.length() && i < maxResults-1; i++) {

			try {
				JSONObject jsl = responseArray.getJSONObject(i);

				String addressLine = jsl.getString("address");
				results = addressLine;
				
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
		}

		return results;
	}
}
