package gov.in.bloomington.open311.model;


import gov.in.bloomington.open311.controller.GeoreporterUtils;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;

public class ExternalFileAdapter {
	public static JSONArray readJSON(Activity a, int raw_resource) {
		JSONArray availableServers = null;
		InputStream inputStream = a.getResources().openRawResource(raw_resource);		
		try {
			availableServers = new JSONArray(GeoreporterUtils.convertStreamToString(inputStream));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return availableServers;
	}
}
