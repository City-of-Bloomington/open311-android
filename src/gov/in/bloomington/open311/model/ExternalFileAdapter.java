package gov.in.bloomington.open311.model;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;

public class ExternalFileAdapter {
	public static JSONArray readJSON(Activity a, int raw_resource) {
		JSONArray availableServers = null;
		try {
			InputStream inputStream = a.getResources().openRawResource(raw_resource);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder = new StringBuilder();
			String line = buffer.readLine();
			while (line != null) {
				builder.append(line);
				line = buffer.readLine();
			}
			try {
				availableServers = new JSONArray(builder.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 finally {     
		}
		
		return availableServers;
	}
}
