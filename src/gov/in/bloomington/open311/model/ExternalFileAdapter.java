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
import android.util.Log;

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
	    Log.d("externalfileadapter", "1 external file");
		try {
			inputstream = a.openFileInput(filename);
			result = new JSONArray(GeoreporterUtils.convertStreamToString(inputstream));
			Log.d("externalfileadapter", "2 external file");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			Log.d("externalfileadapter", "3 external file");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("externalfileadapter", "4 external file");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.d("externalfileadapter", "5 external file");
		}
		Log.d("externalfileadapter", "6 external file "+result);
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
