/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

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

import android.util.Log;

/*
 * Business (Controller) utilization class
 */
public class GeoreporterUtils {
	/** Return string form from a stream input */
	public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
      }
	
	/** return address from location coordinate */
	public String getFromLocation(final double lat, final double lon, final int maxResults) {
    	final String urlStr = "http://maps.google.com/maps/geo?q=" + lat + "," + lon + "&output=json&sensor=false";
		String response = "";
		String results = "";
		final HttpClient client = new DefaultHttpClient();
		
		try {
			final HttpResponse httpR = client.execute(new HttpGet(urlStr));
			final HttpEntity entity = httpR.getEntity();

			final BufferedReader bufR = new BufferedReader(new InputStreamReader(entity.getContent()));

			//String buff = null;
			final StringBuffer str = new StringBuffer();
			while (bufR.readLine() != null) {
				str.append(bufR.readLine());
				//response += bufR.readLine();
			}
			response = str.toString();
		} catch (IOException e) {
			Log.e("GeoreporterUtils getFromLocation", e.toString());
		}

		JSONArray responseArray = null;
		try {
			final JSONObject jsonObject = new JSONObject(response);
			responseArray = jsonObject.getJSONArray("Placemark");
			
			for(int i = 0; i < responseArray.length() && i < maxResults-1; i++) {

				final JSONObject jsl = responseArray.getJSONObject(i);

				final String addressLine = jsl.getString("address");
				results = addressLine;
					
			}
			
		} catch (JSONException e) {
			Log.e("GeoreporterUtils getFromLocation", e.toString());
		}
		
		return results;
	}
	
	/** Return month correspondent to month's number */
	public String getMonth(final int month) {
//		String monthString = null;
		final String[] monthList = {"January", "February", "March", "April", "May", "June", "July", 
				"August", "September", "October", "November", "December"};
		return monthList[month];
	}
}
