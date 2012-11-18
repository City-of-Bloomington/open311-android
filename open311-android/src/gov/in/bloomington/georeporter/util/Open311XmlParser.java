package gov.in.bloomington.georeporter.util;

import gov.in.bloomington.georeporter.models.Open311;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class Open311XmlParser {
	
	// XML tags
	private static final String SERVICES 			= "services";
	private static final String SERVICE  			= "service";
	private static final String REQUEST				= "request";
	public  static final String ATTRIBUTE   		= "attribute";
	private static final String SERVICE_REQUESTS 	= "service_requests";
	private static final String SERVICE_DEFINITION  = "service_definition";
	
	private static final String ns = null;
	private XmlPullParser parser;
	
	public Open311XmlParser(){
		try {
            parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			
    	} catch (Exception e) {
    	}
	}
    public JSONArray parseServices(String xml) throws XmlPullParserException, IOException {
    	InputStream is;
    	try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            parser.setInput(is, null);
            parser.nextTag();
            return parseServices(parser);
    	} catch (Exception e) {
    		
    	} finally {
        	//is.close();
        }
		return null;
    }

    public JSONObject parseServiceDefinition(String xml) throws XmlPullParserException, IOException {
    	InputStream is;
    	try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            parser.setInput(is, null);
            parser.nextTag();
            return parseServiceDefinition(parser);
    	} catch (Exception e) {
    		
    	} finally {
        	//is.close();
        }
		return null;
    }
    
    public JSONArray parseRequests(String xml) throws XmlPullParserException, IOException {
    	InputStream is;
    	try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            parser.setInput(is, null);
            parser.nextTag();
            return parseRequests(parser);
    	} catch (Exception e) {
    		
    	} finally {
        	//is.close();
        }
		return null;
    }
    private JSONArray parseServices(XmlPullParser parser) throws XmlPullParserException, IOException {
        JSONArray ja = new JSONArray();
        parser.require(XmlPullParser.START_TAG, ns, SERVICES);
        
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(SERVICE)) {
            	JSONObject service = readService(parser);
            	ja.put(service);
            } else {
                skip(parser);
            }
        }  
        return ja;
    }

    private JSONObject parseServiceDefinition(XmlPullParser parser) throws XmlPullParserException, IOException {
    	JSONObject jo = new JSONObject();
    	String service_code = null;
    	JSONArray service_definition = new JSONArray();
        parser.require(XmlPullParser.START_TAG, ns, SERVICE_DEFINITION);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(Open311.ATTRIBUTES)) {
            	service_definition = parseAttributes(parser);
            } else if (name.equals(Open311.SERVICE_CODE)) {
            	service_code = readElement(parser, Open311.SERVICE_CODE);
            } else {
                skip(parser);
            }
        }
        
        try {
        	jo.put(Open311.SERVICE_CODE, service_code);
        	jo.put(Open311.ATTRIBUTES,service_definition);
    	} catch(Exception ex){
    		
    	}
        return jo;
    }
    
    private JSONObject readService(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, SERVICE);
        String service_code = null;
        String service_name = null;
        String description = null;
        String group = null;
        String metadata = null;
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            try {
            	if (name.equals(Open311.SERVICE_CODE)) {
	            	service_code = readElement(parser, Open311.SERVICE_CODE);
	            	jo.put(Open311.SERVICE_CODE, service_code);
	            } else if (name.equals(Open311.SERVICE_NAME)) {
	            	service_name = readElement(parser, Open311.SERVICE_NAME);
	            	jo.put(Open311.SERVICE_NAME, service_name);
	            } else if (name.equals(Open311.DESCRIPTION)) {
	                description = readElement(parser, Open311.DESCRIPTION);
	                jo.put(Open311.DESCRIPTION, description);
	            } else if (name.equals(Open311.GROUP)) {
	            	group = readElement(parser, Open311.GROUP);
	            	jo.put(Open311.GROUP, group);
			    } else if (name.equals(Open311.METADATA)) {
			        metadata = readElement(parser, Open311.METADATA);
			        jo.put(Open311.METADATA, metadata);
			    }else {
	                skip(parser);
	            }
		    } catch (Exception ex) {
				
			}
        }
        return jo;
    }
    private JSONArray parseAttributes(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, Open311.ATTRIBUTES);
        JSONArray ja = new JSONArray();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            try {
            	if (name.equals(ATTRIBUTE)) {
            		JSONObject attr = parseAttribute(parser);
            		ja.put(attr);
            	}
            } catch(Exception ex){
            	
            }
        }
        return ja;
    }
	            	
    private JSONObject parseAttribute(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, ATTRIBUTE);
        String variable = null;
        String datatype = null;
        String description = null;
        JSONArray values = null;
        String required = null;
        String order = null;
        String code = null;
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            try {
            	if (name.equals(Open311.VARIABLE)) {
            		variable = readElement(parser, Open311.VARIABLE);
	            	jo.put(Open311.VARIABLE, variable);
	            } else if (name.equals(Open311.DATATYPE)) {
	            	datatype = readElement(parser, Open311.DATATYPE);
	            	jo.put(Open311.DATATYPE, datatype);
	            } else if (name.equals(Open311.DESCRIPTION)) {
	                description = readElement(parser, Open311.DESCRIPTION);
	                jo.put(Open311.DESCRIPTION, description);
	            } else if (name.equals(Open311.VALUES)) {
	            	values = readValues(parser);
	            	jo.put(Open311.VALUES, values);
			    } else if (name.equals(Open311.REQUIRED)) {
			        required = readElement(parser, Open311.REQUIRED);
			        jo.put(Open311.REQUIRED, required);
			    } else if (name.equals(Open311.ORDER)) {
			        order = readElement(parser, Open311.ORDER);
			        jo.put(Open311.ORDER, order);
			    } else if (name.equals(Open311.CODE)) {
			        code = readElement(parser, Open311.CODE);
			        jo.put(Open311.CODE, code);
			    } else {
	                skip(parser);
	            }
		    } catch (Exception ex) {
				
			}
        }
        return jo;
    }
    
    private JSONArray readValues(XmlPullParser parser) throws XmlPullParserException, IOException {
	    JSONArray ja = new JSONArray();
	    parser.require(XmlPullParser.START_TAG, ns, Open311.VALUES);
	    
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals(Open311.VALUE)) {
	        	JSONObject value = readValue(parser);
	        	ja.put(value);
	        } else {
	            skip(parser);
	        }
	    }  
	    return ja;
	}
 
    private JSONObject readValue(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, Open311.VALUE);
        String key = null;
        String value = null;
        JSONObject jo = new JSONObject();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            try {
            	if (name.equals(Open311.NAME)) {
            		value = readElement(parser, Open311.NAME);
	            	jo.put(Open311.NAME, value);
	            } else if (name.equals(Open311.KEY)) {
	            	key = readElement(parser, Open311.KEY);
	            	jo.put(Open311.KEY, key);
	            }else {
	                skip(parser);
	            }
		    } catch (Exception ex) {
				
			}
        }
        //return new Service(service_code, service_name, description,group, metadata);
        return jo;
    }

    private JSONArray parseRequests(XmlPullParser parser) throws XmlPullParserException, IOException {
        JSONArray ja = new JSONArray();

        parser.require(XmlPullParser.START_TAG, ns, SERVICE_REQUESTS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(REQUEST)) {
            	JSONObject jo = readRequest(parser);
            	try {
            		ja.put(jo);
            	} catch (Exception ex) {
            		
            	}
            } else {
                skip(parser);
            }
        }  
        return ja;
    }
    private JSONObject readRequest(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, REQUEST);
    	JSONObject jo = new JSONObject();
    	try {
	        while (parser.next() != XmlPullParser.END_TAG) {
	            if (parser.getEventType() != XmlPullParser.START_TAG) {
	                continue;
	            }
	            String name = parser.getName();
	            if (name.equals(Open311.SERVICE_CODE)) {
	            	jo.put(Open311.SERVICE_CODE, readElement(parser, Open311.SERVICE_CODE));
	            } else if (name.equals(Open311.SERVICE_REQUEST_ID)) {
	            	jo.put(Open311.SERVICE_REQUEST_ID, readElement(parser, Open311.SERVICE_REQUEST_ID));
	            } else if (name.equals(Open311.LATITUDE)) {
	            	jo.put(Open311.LATITUDE, readElement(parser,Open311.LATITUDE));
	            } else if (name.equals(Open311.LONGITUDE)) {
	            	jo.put(Open311.LONGITUDE, readElement(parser, Open311.LONGITUDE));
	            } else if (name.equals(Open311.DESCRIPTION)) {
	            	jo.put(Open311.DESCRIPTION, readElement(parser, Open311.DESCRIPTION));
	            } else if (name.equals(Open311.SERVICE_NOTICE)) {
	            	jo.put(Open311.SERVICE_NOTICE, readElement(parser, Open311.SERVICE_NOTICE));
	            } else if (name.equals(Open311.REQUESTED_DATETIME)) {
	            	jo.put(Open311.REQUESTED_DATETIME, readElement(parser, Open311.REQUESTED_DATETIME));
			    }else {
	                skip(parser);
	            }
	        }
    	} catch (Exception ex) {
    		//TODO
    	}
        return jo;
    }
    
    // Processes title tags in the feed.
    private String readElement(XmlPullParser parser, String element) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, element);
        String service_code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, element);
        return service_code;
    }
    
    // For the tags title and summary, extracts their text values.
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
