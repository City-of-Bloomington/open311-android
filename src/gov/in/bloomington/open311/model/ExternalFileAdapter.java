package gov.in.bloomington.open311.model;


import gov.in.bloomington.open311.controller.GeoreporterUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;

public class ExternalFileAdapter {
	
	public static boolean writeJSON(Activity a, String filename,JSONArray content) {
		boolean succes = false;
		String content_string = content.toString();
		FileOutputStream fos;
		try {
			fos = a.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content_string.getBytes());
			fos.close();
			succes = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return succes;
	}
	
	public static JSONArray readJSON(Activity a, String filename) {
		JSONArray result = null;
	    InputStream inputstream;
		try {
			inputstream = a.openFileInput(filename);
			result = new JSONArray(GeoreporterUtils.convertStreamToString(inputstream));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}
	
	public static JSONArray readJSONRaw(Activity a, int raw_resource) {
		JSONArray result = null;
		InputStream inputStream = a.getResources().openRawResource(raw_resource);		
		try {
			result = new JSONArray(GeoreporterUtils.convertStreamToString(inputStream));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}
}
