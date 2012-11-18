package gov.in.bloomington.georeporter.util;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.MainActivity;
import gov.in.bloomington.georeporter.activities.ReportActivity;
import gov.in.bloomington.georeporter.activities.SavedReportsActivity;
import gov.in.bloomington.georeporter.activities.SettingsActivity;
import gov.in.bloomington.georeporter.models.Open311;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
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
