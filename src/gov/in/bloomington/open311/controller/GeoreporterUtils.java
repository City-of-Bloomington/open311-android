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

/*
 * Business (Controller) utilization class
 */
public class GeoreporterUtils {
	/** Return string form from a stream input */
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
	
	/** return address from location coordinate */
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
	
	/** Return month correspondent to month's number */
	public static String getMonth(int month) {
		String monthString = null;
		switch (month) {
        case 1:  monthString = "January";
                 break;
        case 2:  monthString = "February";
                 break;
        case 3:  monthString = "March";
                 break;
        case 4:  monthString = "April";
                 break;
        case 5:  monthString = "May";
                 break;
        case 6:  monthString = "June";
                 break;
        case 7:  monthString = "July";
                 break;
        case 8:  monthString = "August";
                 break;
        case 9:  monthString = "September";
                 break;
        case 10: monthString = "October";
                 break;
        case 11: monthString = "November";
                 break;
        case 12: monthString = "December";
                 break;
        default: monthString = "Invalid month";
                 break;
    }
		return monthString;
	}
}
