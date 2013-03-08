/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Jaakko Rajaniemi <jaakko.rajaniemi@hel.fi>
 */
package gov.in.bloomington.georeporter.util;

import gov.in.bloomington.georeporter.models.Open311;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class Open311XmlParser {
	
	// XML tags
	private static final String SERVICES 			= "services";
	private static final String SERVICE  			= "service";
	private static final String REQUEST				= "request";
	public  static final String ATTRIBUTE   		= "attribute";
	private static final String SERVICE_REQUESTS 	= "service_requests";
	private static final String SERVICE_DEFINITION  = "service_definition";
	private static final String ERRORS  			= "errors";
	private static final String ERROR  				= "error";
	
	private static final String ns = null;
	private XmlPullParser parser;
	
	public Open311XmlParser() {
        parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        }
        catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
    
	/**
	 * Converts an Open311 Service List from XML to JSON
	 * 
	 * @param xml
	 * @return JSONArray
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public JSONArray parseServices(String xml) throws XmlPullParserException, IOException, JSONException {
    	InputStream is;
        is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        parser.setInput(is, null);
        parser.nextTag();
        return parseServices(parser);
    }

    /**
     * Converts an Open311 Service Definition from XML to JSON
     * 
     * @param xml
     * @return JSONObject
     * @throws XmlPullParserException 
     * @throws IOException 
     * @throws JSONException 
     */
    public JSONObject parseServiceDefinition(String xml) throws XmlPullParserException, IOException, JSONException {
    	InputStream is;
		is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        parser.setInput(is, null);
        parser.nextTag();
        return parseServiceDefinition(parser);
    }
    
    /**
     * Converts an Open311 Service Request List from XML to JSON
     * 
     * @param xml
     * @return JSONArray
     * @throws XmlPullParserException 
     * @throws IOException 
     * @throws JSONException 
     */
    public JSONArray parseRequests(String xml) throws XmlPullParserException, IOException, JSONException {
    	InputStream is;
		is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        parser.setInput(is, null);
        parser.nextTag();
        return parseRequests(parser);
    }
    
    /**
     * Converts an Open311 error response from XML to JSON
     * 
     * @param xml
     * @return JSONArray
     * @throws XmlPullParserException 
     * @throws IOException 
     * @throws JSONException 
     */
    public JSONArray parseErrors(String xml) throws XmlPullParserException, IOException, JSONException {
    	InputStream is;
		is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        parser.setInput(is, null);
        parser.nextTag();
        return parseErrors(parser);
    }
    
    /**
     * 
     * @param parser
     * @return JSONArray
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONArray parseServices(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        JSONArray ja = new JSONArray();
        parser.require(XmlPullParser.START_TAG, ns, SERVICES);
        
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(SERVICE)) {
            	ja.put(parseService(parser));
            } else {
                skip(parser);
            }
        }  
        return ja;
    }

    /**
     * 
     * @param parser
     * @return JSONObject
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseServiceDefinition(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
    	JSONObject jo                 = new JSONObject();
    	String     service_code       = null;
    	JSONArray  service_definition = new JSONArray();
    	
        parser.require(XmlPullParser.START_TAG, ns, SERVICE_DEFINITION);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if      (name.equals(Open311.ATTRIBUTES))   { service_definition = parseAttributes(parser); }
            else if (name.equals(Open311.SERVICE_CODE)) { service_code       = readElement(parser, Open311.SERVICE_CODE); }
            else { skip(parser); }
        }
        
        jo.put(Open311.SERVICE_CODE, service_code);
        jo.put(Open311.ATTRIBUTES,service_definition);
        return jo;
    }
    
    /**
     * 
     * @param parser
     * @return JSONObject
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseService(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        parser.require(XmlPullParser.START_TAG, ns, SERVICE);
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
        	if      (name.equals(Open311.SERVICE_CODE)) { jo.put(Open311.SERVICE_CODE, readElement(parser, Open311.SERVICE_CODE)); }
        	else if (name.equals(Open311.SERVICE_NAME)) { jo.put(Open311.SERVICE_NAME, readElement(parser, Open311.SERVICE_NAME)); }
        	else if (name.equals(Open311.DESCRIPTION))  { jo.put(Open311.DESCRIPTION,  readElement(parser, Open311.DESCRIPTION)); }
        	else if (name.equals(Open311.GROUP))        { jo.put(Open311.GROUP,        readElement(parser, Open311.GROUP)); }
        	else if (name.equals(Open311.METADATA))     { jo.put(Open311.METADATA, Boolean.parseBoolean(readElement(parser, Open311.METADATA))); }
        	else { skip(parser); }
        }
        return jo;
    }
    
    /**
     * 
     * @param parser
     * @return JSONArray
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONArray parseAttributes(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
    	parser.require(XmlPullParser.START_TAG, ns, Open311.ATTRIBUTES);
        JSONArray ja = new JSONArray();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
        	if (name.equals(ATTRIBUTE)) {
        		JSONObject attr = parseAttribute(parser);
        		ja.put(attr);
        	}
        }
        return ja;
    }
	
    /**
     * 
     * @param parser
     * @return JSONObject
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseAttribute(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
    	parser.require(XmlPullParser.START_TAG, ns, ATTRIBUTE);
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
        	if      (name.equals(Open311.DATATYPE))    { jo.put(Open311.DATATYPE,    readElement(parser, Open311.DATATYPE)); }
        	else if (name.equals(Open311.DESCRIPTION)) { jo.put(Open311.DESCRIPTION, readElement(parser, Open311.DESCRIPTION)); }
            else if (name.equals(Open311.CODE))        { jo.put(Open311.CODE,        readElement(parser, Open311.CODE)); }
            else if (name.equals(Open311.ORDER))       { jo.put(Open311.ORDER,    Integer.parseInt    (readElement(parser, Open311.ORDER))); }
            else if (name.equals(Open311.VARIABLE))    { jo.put(Open311.VARIABLE, Boolean.parseBoolean(readElement(parser, Open311.VARIABLE))); }
            else if (name.equals(Open311.REQUIRED))    { jo.put(Open311.REQUIRED, Boolean.parseBoolean(readElement(parser, Open311.REQUIRED))); }
            else if (name.equals(Open311.VALUES))      { jo.put(Open311.VALUES, parseValues(parser)); }
		    else { skip(parser); }
        }
        return jo;
    }
    /**
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONArray parseValues(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
	    JSONArray ja = new JSONArray();
	    parser.require(XmlPullParser.START_TAG, ns, Open311.VALUES);
	    
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals(Open311.VALUE)) {
	        	ja.put(parseValue(parser));
	        }
	        else {
	            skip(parser);
	        }
	    }  
	    return ja;
	}
    
    /**
     * 
     * @param parser
     * @return JSONObject
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseValue(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        parser.require(XmlPullParser.START_TAG, ns, Open311.VALUE);
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
        	if      (name.equals(Open311.NAME)) { jo.put(Open311.NAME, readElement(parser, Open311.NAME)); }
        	else if (name.equals(Open311.KEY))  { jo.put(Open311.KEY,  readElement(parser, Open311.KEY)); }
            else { skip(parser); }
        }
        return jo;
    }

    /**
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONArray parseRequests(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        JSONArray ja = new JSONArray();
        parser.require(XmlPullParser.START_TAG, ns, SERVICE_REQUESTS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(REQUEST)) {
                ja.put(parseRequest(parser));
            }
            else {
                skip(parser);
            }
        }  
        return ja;
    }
    
    /**
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseRequest(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        parser.require(XmlPullParser.START_TAG, ns, REQUEST);
    	JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if      (name.equals(Open311.SERVICE_CODE))       { jo.put(Open311.SERVICE_CODE,       readElement(parser, Open311.SERVICE_CODE)); }
            else if (name.equals(Open311.SERVICE_REQUEST_ID)) { jo.put(Open311.SERVICE_REQUEST_ID, readElement(parser, Open311.SERVICE_REQUEST_ID)); }
            else if (name.equals(Open311.TOKEN))              { jo.put(Open311.TOKEN,              readElement(parser, Open311.TOKEN)); }
            else if (name.equals(Open311.ACCOUNT_ID))         { jo.put(Open311.ACCOUNT_ID,         readElement(parser, Open311.ACCOUNT_ID)); }
            else if (name.equals(Open311.LATITUDE))           { jo.put(Open311.LATITUDE,           readElement(parser, Open311.LATITUDE)); }
            else if (name.equals(Open311.LONGITUDE))          { jo.put(Open311.LONGITUDE,          readElement(parser, Open311.LONGITUDE)); }
            else if (name.equals(Open311.DESCRIPTION))        { jo.put(Open311.DESCRIPTION,        readElement(parser, Open311.DESCRIPTION)); }
            else if (name.equals(Open311.SERVICE_NOTICE))     { jo.put(Open311.SERVICE_NOTICE,     readElement(parser, Open311.SERVICE_NOTICE)); }
            else if (name.equals(Open311.STATUS_NOTES))       { jo.put(Open311.STATUS_NOTES,       readElement(parser, Open311.STATUS_NOTES)); }
            else if (name.equals(Open311.STATUS))             { jo.put(Open311.STATUS,             readElement(parser, Open311.STATUS)); }
        	else if (name.equals(Open311.REQUESTED_DATETIME)) { jo.put(Open311.REQUESTED_DATETIME, readElement(parser, Open311.REQUESTED_DATETIME)); }
            else if (name.equals(Open311.UPDATED_DATETIME))   { jo.put(Open311.UPDATED_DATETIME,   readElement(parser, Open311.UPDATED_DATETIME)); }
            else if (name.equals(Open311.EXPECTED_DATETIME))  { jo.put(Open311.EXPECTED_DATETIME,  readElement(parser, Open311.EXPECTED_DATETIME)); }
            else if (name.equals(Open311.AGENCY_RESPONSIBLE)) { jo.put(Open311.AGENCY_RESPONSIBLE, readElement(parser, Open311.AGENCY_RESPONSIBLE)); }
            else { skip(parser); }
        }
        return jo;
    }

    /**
     * 
     * @param parser
     * @return JSONArray
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONArray parseErrors(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        JSONArray ja = new JSONArray();
        parser.require(XmlPullParser.START_TAG, ns, ERRORS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(ERROR)) {
            	ja.put(parseError(parser));
            } else {
                skip(parser);
            }
        }  
        return ja;
    }
    
    /**
     * 
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws JSONException 
     */
    private JSONObject parseError(XmlPullParser parser) throws XmlPullParserException, IOException, JSONException {
        parser.require(XmlPullParser.START_TAG, ns, ERROR);
    	JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if      (name.equals(Open311.CODE))        { jo.put(Open311.CODE,        readElement(parser, Open311.CODE)); }
            else if (name.equals(Open311.DESCRIPTION)) { jo.put(Open311.DESCRIPTION, readElement(parser, Open311.DESCRIPTION)); }
            else { skip(parser); }
        }
        return jo;
    }
    
    /**
     * 
     * @param parser
     * @param element
     * @return String
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readElement(XmlPullParser parser, String element) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, element);
        String service_code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, element);
        return service_code;
    }
    
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
    }
}
