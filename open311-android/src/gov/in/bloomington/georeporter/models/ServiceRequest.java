/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceRequest {
	public static final String SERVICE            = "service";
	public static final String SERVICE_DEFINITION = "service_definition";
	
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
	 */
	public JSONObject post_data;
	
	/**
	 * Creates a new, empty ServiceRequest
	 * 
	 * This does not incorporate any user submitted data and should only be
	 * used for initial startup. Subsequent loads should be done using the
	 * JSON String version
	 * 
	 * @param s A single service from GET Service List
	 */
	public ServiceRequest(JSONObject s) {
		service = s;
		post_data = new JSONObject();
		
		if (service.optBoolean(Open311.METADATA)) {
			try {
				service_definition = Open311.getServiceDefinition(service.getString(Open311.SERVICE_CODE));
			} catch (JSONException e) {
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
			if (sr.has(SERVICE))                 service            = sr.getJSONObject(SERVICE);
			if (sr.has(SERVICE_DEFINITION))      service_definition = sr.getJSONObject(SERVICE_DEFINITION);
			if (sr.has(Open311.SERVICE_REQUEST)) service_request    = sr.getJSONObject(Open311.SERVICE_REQUEST);
			if (sr.has("post_data"))             post_data          = sr.getJSONObject("post_data");
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
			if (service_definition != null) sr.put(SERVICE_DEFINITION,      service_definition);
			if (service_request    != null) sr.put(Open311.SERVICE_REQUEST, service_request);
			if (post_data          != null) sr.put("post_data",             post_data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sr.toString();
	}
	
	/**
	 * @return
	 * boolean
	 */
	public boolean hasAttributes() {
		return service.optBoolean(Open311.METADATA);
	}
	
	/**
	 * @param code
	 * @return
	 * @throws JSONException
	 * JSONObject
	 */
	private JSONObject getAttribute(String code) throws JSONException {
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
	 * @return
	 * String
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
}
