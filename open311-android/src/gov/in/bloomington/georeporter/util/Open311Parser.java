package gov.in.bloomington.georeporter.util;

import gov.in.bloomington.georeporter.models.Open311;

import java.io.IOException;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONObject;
import android.util.Log;

public class Open311Parser {
	String mFormat= Open311.JSON;

	public Open311Parser(String format){
		mFormat = format;
	}
	public JSONArray parseServices(String xml) throws IOException {
		Log.i("Open311Parser c",xml);
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser mParser= new Open311XmlParser();
	    		return mParser.parseServices(xml);	
	     	}
		} catch (Exception ex) {
			Log.i("Open311Parser",ex.getMessage());
			return null;
		}
	}

	public JSONObject parseServiceDefinition(String xml) throws IOException {
		Log.i("Open311Parser c",xml);
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONObject(xml);
			} else {
	    		Open311XmlParser mParser= new Open311XmlParser();
	    		return mParser.parseServiceDefinition(xml);	
	     	}
		} catch (Exception ex) {
			Log.i("Open311Parser",ex.getMessage());
			return null;
		}
	}
	
	public JSONArray parseRequests(String xml) throws IOException {
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser mParser= new Open311XmlParser();
	    		return mParser.parseRequests(xml);	
	     	}
		} catch (Exception ex) {
			return null;
		}
	}
}
