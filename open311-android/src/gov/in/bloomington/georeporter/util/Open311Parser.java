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
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.util.Log;

public class Open311Parser {
	String mFormat= "json";

	public Open311Parser(String format){
		mFormat = format;
	}
	public JSONArray parseServices(String xml) throws XmlPullParserException, IOException {
		Log.i("Open311Parser format",mFormat);
		Log.i("Open311Parser c",xml);
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser oparser= new Open311XmlParser();
	    		return oparser.parseServices(xml);	
	     	}
		} catch (Exception ex) {
			Log.i("Open311Parser",ex.getMessage());
			return null;
		}
	}

	public JSONArray parseRequests(String xml) throws XmlPullParserException, IOException {
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser oparser= new Open311XmlParser();
	    		return oparser.parseRequests(xml);	
	     	}
		} catch (Exception ex) {
			return null;
		}
	}
}
