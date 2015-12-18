/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Jaakko Rajaniemi <jaakko.rajaniemi@hel.fi>
 */
package gov.in.bloomington.georeporter.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import gov.in.bloomington.georeporter.models.Open311;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

public class Open311Parser {
	private String mFormat = Open311.JSON;
	
	public Open311Parser(String format) {
		mFormat = format;
	}

	/**
	 * @see <a href="http://wiki.open311.org/GeoReport_v2/#get-service-list">open311 GET Service List</a>
	 * @param response parse open311 Services
	 * @return JSONArray
	 */
	public JSONArray parseServices(String response) {
        try {
    		if (mFormat.equals(Open311.JSON)) {
    		    return new JSONArray(response);
    		}
    		else {
        		Open311XmlParser mParser = new Open311XmlParser();
        		return mParser.parseServices(response);	
         	}
        }
        catch (JSONException | XmlPullParserException  | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
	}

	/**
	 * @see <a href="http://wiki.open311.org/GeoReport_v2/#get-service-definition">open311 GET Service Definition</a>
	 * @param xml parse open311 ServiceDefinition
	 * @return JSONObject
	 */
	public JSONObject parseServiceDefinition(String xml) {
	    try {
    		if (mFormat.equals(Open311.JSON)) {
    		    return new JSONObject(xml);
    		} else {
    			Open311XmlParser mParser= new Open311XmlParser();
    			return mParser.parseServiceDefinition(xml);	
         	}
	    }
        catch (JSONException | XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return null;
	}
	
	/**
	 * @see <a href="http://wiki.open311.org/GeoReport_v2/#post-service-request">open311 POST Service Request</a>
	 * @param xml parse open311 Request
	 * @return JSONArray
	 */
	public JSONArray parseRequests(String xml) {
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser mParser= new Open311XmlParser();
	    		return mParser.parseRequests(xml);	
	     	}
		}
        catch (JSONException  | XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * @see <a href="http://wiki.open311.org/GeoReport_v2/#errors">open311 Errors</a>
	 * @param xml parse open311 Errors
	 * @return JSONArray
	 */
	public JSONArray parseErrors(String xml) {
		try {
			if (mFormat.equals(Open311.JSON)){
	    		return new JSONArray(xml);
			} else {
	    		Open311XmlParser mParser= new Open311XmlParser();
	    		return mParser.parseErrors(xml);	
	     	}
		}
        catch (JSONException | XmlPullParserException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}
}
