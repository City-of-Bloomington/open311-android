/**
 * Model for working with all the information about a single Service Request
 * 
 * Includes the service information to query the endpoint for fresh data.
 * Includes service definition information.
 * Includes the raw data the user entered.
 * Includes a cache of data from endpoint.
 * 
 * Serialize this object by calling toString, which will return JSON.
 * Restore this object's state by passing a JSON String to the constructor. 
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import gov.in.bloomington.georeporter.util.Media;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

public class ServiceRequest {
    public static final String ENDPOINT           = "endpoint";
	public static final String SERVICE            = "service";
	public static final String SERVICE_DEFINITION = "service_definition";
	public static final String SERVICE_REQUEST    = "service_request";
	public static final String POST_DATA          = "post_data";
    // MetaData fields
    public static final String STATUS             = "status";
    public static final String REQUESTED_DATETIME = "requested_datetime";
    public static final String UPDATED_DATETIME   = "updated_datetime";
	
	/**
	 * The JSON definition from raw/available_servers.json
	 */
	public JSONObject endpoint;
	/**
	 * The JSON for a single service from GET Service List
	 */
	public JSONObject service;
	/**
	 * The JSON response from GET Service Definition
	 */
	public JSONObject service_definition;
	/**
	 * The JSON response from GET Service Request
	 */
	public JSONObject service_request;
	/**
	 * The data that gets sent to POST Service Request
	 * 
	 * JSON property names will be the code from service_definition.
	 * Most JSON properties will just contain single values entered by the user.
	 * Media will contain the URI to the image file.
     * MultiValueList attributes will an array of the chosen values.
	 */
	public JSONObject post_data;
	
	/**
	 * Creates a new, empty ServiceRequest
	 * 
	 * This does not load any user-submitted data and should only be
	 * used for initial startup. Subsequent loads should be done using the
	 * JSON String version
	 * 
	 * @param s A single service from GET Service List
	 * @param def The service definition for the chosen service
	 */
	public ServiceRequest(JSONObject s, JSONObject def, Context c) {
		service   = s;
		service_definition = def;
		
		post_data = new JSONObject();
		
        // Read in the personal info fields from Preferences
		JSONObject personalInfo = Preferences.getPersonalInfo(c);
        Iterator<?>keys = personalInfo.keys();
        while (keys.hasNext()) {
            try {
                String key   = (String)keys.next();
                String value = personalInfo.getString(key);
                if (value != "") {
                    post_data.put(key, value);
                }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}
	
	/**
	 * Loads an existing ServiceRequest from a JSON String
	 * 
	 * We will be serializing to a file as JSON Strings.  This will include
	 * any data a user has already entered and any new information from the
	 * endpoint.
	 * All createView() methods should use this constructor, since they might
	 * be restoring from saveInstanceState()
	 * 
	 * @param json
	 */
	public ServiceRequest(String json) {
		try {
			JSONObject sr = new JSONObject(json);
			if (sr.has(ENDPOINT))           endpoint           = sr.getJSONObject(ENDPOINT);
			if (sr.has(SERVICE))            service            = sr.getJSONObject(SERVICE);
			if (sr.has(SERVICE_DEFINITION)) service_definition = sr.getJSONObject(SERVICE_DEFINITION);
            if (sr.has(POST_DATA))          post_data          = sr.getJSONObject(POST_DATA);
			if (sr.has(SERVICE_REQUEST))    service_request    = sr.getJSONObject(SERVICE_REQUEST);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Serializes all the data as a single JSON string
	 */
	@Override
	public String toString() {
		JSONObject sr = new JSONObject();
		try {
			sr.put(SERVICE, service);
			if (endpoint           != null) sr.put(ENDPOINT,           endpoint);
			if (service_definition != null) sr.put(SERVICE_DEFINITION, service_definition);
            if (post_data          != null) sr.put(POST_DATA,          post_data);
			if (service_request    != null) sr.put(SERVICE_REQUEST,    service_request);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sr.toString();
	}
	
	/**
	 * @return boolean
	 */
	public boolean hasAttributes() {
		return service.optBoolean(Open311.METADATA);
	}
	
	/**
	 * @param code
	 * @throws JSONException
     * @return JSONObject
	 */
	public JSONObject getAttribute(String code) throws JSONException {
	    JSONObject attribute = null;
	    
        JSONArray attributes = service_definition.optJSONArray(Open311.ATTRIBUTES);
        int len = attributes.length();
        for (int i=0; i<len; i++) {
            JSONObject a = attributes.getJSONObject(i);
            if (a.optString(Open311.CODE).equals(code)) {
                attribute = a;
                break;
            }
        }
        return attribute;
	}
	
	/**
	 * Returns the attribute description based on the attribute code
	 * 
	 * Returns an empty string if it cannot find the requested attribute
	 * 
	 * @param code
	 * @return String
	 */
	public String getAttributeDescription(String code) {
	    String description = "";
	    try {
            JSONObject a = getAttribute(code);
            description = a.optString(Open311.DESCRIPTION);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	    return description;
	}
	
	/**
	 * Returns the attribute datatype based on the attribute code
	 * 
	 * If it cannot determine the datatype, it returns "string" as the default
	 * 
	 * @param code
	 * @return
	 * String
	 */
	public String getAttributeDatatype(String code) {
	    String type = Open311.STRING;
	    try {
            JSONObject a = getAttribute(code);
            type = a.optString(Open311.DATATYPE, Open311.STRING);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return type;
	}
	
	/**
	 * Returns the values for an attribute
	 * 
	 * If it cannot determine the attribute, it returns an empty JSONArray
	 * 
	 * @param code
	 * @return
	 * JSONArray
	 */
	public JSONArray getAttributeValues(String code) {
	    JSONArray values = new JSONArray();
	    try {
            JSONObject a = getAttribute(code);
            values = a.getJSONArray(Open311.VALUES);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	    return values;
	}
	
	/**
	 * Returns the name from a single value in an attribute
	 * 
	 * @param code The attribute code
	 * @param key The value key
	 * @return String
	 */
	public String getAttributeValueName(String code, String key) {
	    JSONArray values = getAttributeValues(code);
	    int len = values.length();
	    try {
    	    for (int i=0; i<len; i++) {
    	        JSONObject v = values.getJSONObject(i);
    	        String k = v.getString(Open311.KEY);
    	        if (k.equals(key)) {
    	            return v.getString(Open311.NAME);
    	        }
    	    }
	    } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * @param code
	 * @return boolean
	 */
	public boolean isAttributeRequired(String code) {
	    try {
            JSONObject a = getAttribute(code);
            if (a.opt(Open311.REQUIRED).equals(Open311.TRUE)) {
                return true;
            }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return false;
	}
	
	/**
	 * Returns the URL for getting fresh information from the endpoint
	 * 
	 * @param request_id 
	 * @return String
	 * @throws JSONException
	 */
	public String getServiceRequestUrl(String request_id) throws JSONException {
	    String baseUrl      = endpoint.getString(Open311.URL);
	    String jurisdiction = endpoint.optString(Open311.JURISDICTION);
	    return String.format("%s/requests/%s.json?%s=%s", baseUrl, request_id, Open311.JURISDICTION, jurisdiction);
	}
	
	/**
	 * Returns the URL for getting a service_request_id from a token
	 * 
	 * @param token
	 * @return String
	 * @throws JSONException
	 */
	public String getServiceRequestIdFromTokenUrl(String token) throws JSONException {
        String baseUrl      = endpoint.getString(Open311.URL);
        String jurisdiction = endpoint.optString(Open311.JURISDICTION);
        return String.format("%s/tokens/%s.json?%s=%s", baseUrl, token, Open311.JURISDICTION, jurisdiction);
	}
	
	/**
	 * Returns a bitmap of the user's attached media
	 * 
	 * It seems we cannot use Uri's directly, without running out of memory.
	 * This will safely generate a small bitmap ready to attach to an ImageView
	 * 
	 * @param width
	 * @param height
	 * @param context
	 * @return Bitmap
	 */
	public Bitmap getMediaBitmap(int width, int height, Context context) {
        String m = post_data.optString(Open311.MEDIA);
        if (!m.equals("")) {
            Uri imageUri = Uri.parse(m);
            if (imageUri != null) {
                String path = Media.getRealPathFromUri(imageUri, context);
                return Media.decodeSampledBitmap(path, width, height, context);
            }
        }
	    return null;
	}
}
