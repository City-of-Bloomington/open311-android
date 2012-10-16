package gov.in.bloomington.georeporter.util;

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
        List entries = new ArrayList();
        JSONArray ja = new JSONArray();

        parser.require(XmlPullParser.START_TAG, ns, "services");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("service")) {
            	Entry e = readService(parser);
            	try {
            		JSONObject jo = new JSONObject().put("service_code", e.service_code);
            		
            		jo.put("service_name", e.service_name);
            		jo.put("group", e.group);
            		jo.put("description", e.description);
            		jo.put("metadata", e.metadata);
            		ja.put(jo);
            	} catch (Exception ex) {
            		
            	}
            	entries.add(e);
            } else {
                skip(parser);
            }
        }  
        return ja;
    }
    public static class Entry {
        public final String service_code;
        public final String service_name;
        public final String description;
        public final String group;
        public final String metadata;

        private Entry(String service_code, String service_name, String description, String group, String metadata) {
            this.service_code = service_code;
            this.service_name = service_name;
            this.description = description;
            this.group = group;
            this.metadata = metadata;
        }
    }
      
    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readService(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        String service_code = null;
        String service_name = null;
        String description = null;
        String group = null;
        String metadata = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("service_code")) {
            	service_code = readServiceCode(parser);
            } else if (name.equals("service_name")) {
            	service_name = readServiceName(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("group")) {
                group = readGroup(parser);
		    } else if (name.equals("metadata")) {
		        metadata = readMetadata(parser);
		    }else {
                skip(parser);
            }
        }
        return new Entry(service_code, service_name, description,group, metadata);
    }

    private JSONArray parseRequests(XmlPullParser parser) throws XmlPullParserException, IOException {
        JSONArray ja = new JSONArray();

        parser.require(XmlPullParser.START_TAG, ns, "service_requests");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("request")) {
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
        parser.require(XmlPullParser.START_TAG, ns, "request");

        String service_request_id;
    	String media;
    	String media_url;
    	String latitude;
    	String longitude;
    	String address;
    	String description;
    	String email;
    	String devide_id;
    	String first_name;
    	String last_name;
    	String phone;
    	JSONObject jo = new JSONObject();
    	try {
    		
    	
    		

	        while (parser.next() != XmlPullParser.END_TAG) {
	            if (parser.getEventType() != XmlPullParser.START_TAG) {
	                continue;
	            }
	            String name = parser.getName();
	            if (name.equals("service_code")) {
	            	jo.put("service_code", readServiceCode(parser));
	            } else if (name.equals("service_request_id")) {
	            	jo.put("service_request_id", readServiceRequestId(parser));
	            } else if (name.equals("lat")) {
	            	jo.put("latitude", readLatitude(parser));
	            } else if (name.equals("long")) {
	            	jo.put("longitude", readLongitude(parser));
	            } else if (name.equals("description")) {
	            	jo.put("description", readDescription(parser));
	            } else if (name.equals("service_notice")) {
	            	jo.put("service_notice", readServiceNotice(parser));
	            } else if (name.equals("requested_datetime")) {
	            	jo.put("requested_datetime", readRequestedDatetime(parser));
			    }else {
	                skip(parser);
	            }
	        }
    	} catch (Exception ex) {
    		
    	}
        return jo;
    }
    
    // Processes title tags in the feed.
    private String readServiceCode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service_code");
        String service_code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "service_code");
        return service_code;
    }

    // Processes title tags in the feed.
    private String readServiceRequestId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service_request_id");
        String service_request_id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "service_request_id");
        return service_request_id;
    }
    
    // Processes summary tags in the feed.
    private String readServiceName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service_name");
        String service_name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "service_name");
        return service_name;
    }
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private String readGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "group");
        String group = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "group");
        return group;
    }

    private String readMetadata(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "metadata");
        String metadata = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "metadata");
        return metadata;
    }
    // Processes title tags in the feed.
    private String readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        String latitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return latitude;
    }
    // Processes title tags in the feed.
    private String readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "long");
        String longitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "long");
        return longitude;
    }       
    // Processes title tags in the feed.
    private String readServiceNotice(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service_notice");
        String service_notice = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "service_notice");
        return service_notice;
    }
    // Processes title tags in the feed.
    private String readRequestedDatetime(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "requested_datetime");
        String requested_datetime = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "requested_datetime");
        return requested_datetime;
    }
    
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            Log.i("Open311XmlParser readText",result);
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
