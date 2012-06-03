package gov.in.bloomington.open311.controller;

import gov.in.bloomington.open311.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;

public class ServerItem {
	public static JSONArray retreiveServers(Activity a) {
		JSONArray availableServers = null;
		try {
			InputStream inputStream = a.getResources().openRawResource(R.raw.available_servers);
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
