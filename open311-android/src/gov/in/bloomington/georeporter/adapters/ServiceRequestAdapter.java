/**
 * A ListView Adapter for displaying a single service request
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.AttributeEntryActivity;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import java.util.ArrayList;
import java.util.Arrays;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceRequestAdapter extends BaseAdapter {
	private ServiceRequest mServiceRequest;
	private static LayoutInflater mLayoutInflater;
	
	public static final int TYPE_HEADER = 0;
	public static final int TYPE_ITEM   = 1;
	public static final int TYPE_MEDIA  = 2;
	
	/**
	 * A key string for every item in the listview
	 * We use this to define the order of fields in the listview
	 */
	public ArrayList<String> labels = new ArrayList<String>(Arrays.asList(
		Open311.SERVICE_NAME,
		Open311.MEDIA,
		Open311.ADDRESS,
		Open311.DESCRIPTION
	));
	/**
	 * The int positions of labels that are supposed to be headers
	 */
	private ArrayList<Integer> headers = new ArrayList<Integer>(Arrays.asList(0));
	
	/**
	 * @param key
	 * void
	 */
	private void addHeader(String key) {
	    labels.add(key);
	    headers.add(labels.size() - 1);
	}
	
	/**
	 * Prepares display strings from the ServiceRequest
	 *
	 * Reads all the neccessary information from the ServiceRequest into the
	 * local variables used for display.  The actual raw data the user enters
	 * will still be stored in the ServiceRequest.  Later, the ServiceRequest
	 * will be handed off to Open311 for submitting to the endpoint.
	 * 
	 * @param sr
	 * @param c
	 */
	public ServiceRequestAdapter(ServiceRequest sr, Context c) {
		mServiceRequest = sr;
		mLayoutInflater = LayoutInflater.from(c);
		
		if (sr.hasAttributes()) {
			try {
				// Add a section header for the attributes
				JSONArray attributes = sr.service_definition.getJSONArray(Open311.ATTRIBUTES);
                addHeader(Open311.ATTRIBUTES);
				
				// Loop over all the attributes and add each attribute code
				// to the labels list
                int len = attributes.length();
				for (int i=0; i<len; i++) {
					JSONObject a = attributes.getJSONObject(i);
					labels.add(a.getString(Open311.CODE));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates the display with new data
	 * 
	 * Call this function whenever the user changes data in the ServiceRequest
	 * 
	 * @param sr
	 * void
	 */
	public void updateServiceRequest(ServiceRequest sr) {
	    mServiceRequest = sr;
	    super.notifyDataSetChanged();
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (labels.get(position) == Open311.MEDIA) {
			return TYPE_MEDIA;
		}
		return headers.contains(position) ? TYPE_HEADER : TYPE_ITEM;
	}
	
	@Override
	public int getCount() {
		return labels.size();
	}

	@Override
	public Object getItem(int position) {
		return labels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class HeaderViewHolder {
		TextView title;
	}
	
	private static class MediaViewHolder {
		ImageView image;
	}
	
    private static class ItemViewHolder {
        TextView label, displayValue;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        String labelKey = labels.get(position);
        
		switch (getItemViewType(position)) {
			case TYPE_HEADER:
				HeaderViewHolder header;
				
				if (convertView == null) {
					convertView = mLayoutInflater.inflate(R.layout.list_item_header, null);
					header = new HeaderViewHolder();
					header.title = (TextView)convertView;
					convertView.setTag(header);
				}
				else {
					header = (HeaderViewHolder)convertView.getTag();
				}
				
				if (labelKey.equals(Open311.SERVICE_NAME)) {
				    header.title.setText(mServiceRequest.service.optString(Open311.DESCRIPTION));
				}
				else if (labelKey.equals(Open311.ATTRIBUTES)) {
				    header.title.setText(convertView.getResources().getString(R.string.report_attributes));
				}
				break;
				
            case TYPE_MEDIA:
                MediaViewHolder media;
                
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.list_item_media, null);
                    media = new MediaViewHolder();
                    media.image = (ImageView)convertView.findViewById(R.id.media);
                    convertView.setTag(media);
                }
                else {
                    media = (MediaViewHolder)convertView.getTag();
                }
                
                String m = mServiceRequest.post_data.optString(Open311.MEDIA);
                if (!m.equals("")) {
                    Uri imageUri = Uri.parse(m);
                    if (imageUri != null) {
                        media.image.setImageURI(imageUri);
                    }
                }
                
                break;
                
			case TYPE_ITEM:
				ItemViewHolder item;
				
				if (convertView == null) {
					convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
					item = new ItemViewHolder();
					item.label        = (TextView)convertView.findViewById(android.R.id.text1);
					item.displayValue = (TextView)convertView.findViewById(android.R.id.text2);
					convertView.setTag(item);
				}
				else {
					item = (ItemViewHolder)convertView.getTag();
				}
				
				String label        = "";
				String displayValue = "";
				
				if (labelKey.equals(Open311.ADDRESS)) {
				    // TODO display user input as a string
				    // I'm still not sure of how best to store post_data
				    // Need to build some dialogs and try and POST to the endpoint
				    
	                // Display the address, if we have it already, otherwise
				    // show the lat/long. Users are going to be choosing the
				    // lat/long with a map. Since we're looking up the address
				    // in an Async call to Google, there's going to be a delay
				    // between when we have the lat/long and when we have an
				    // address.
				    label = convertView.getResources().getString(R.string.location);
				    displayValue = mServiceRequest.post_data.optString(Open311.ADDRESS);
				    if (displayValue.equals("")) {
				        double latitude  = mServiceRequest.post_data.optDouble(Open311.LATITUDE);
				        double longitude = mServiceRequest.post_data.optDouble(Open311.LONGITUDE);
				        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
				            displayValue = String.format("%f, %f", latitude, longitude);
				        }
				    }
				}
				else if (labelKey.equals(Open311.DESCRIPTION)) {
                    label = convertView.getResources().getString(R.string.report_description);
                    displayValue = mServiceRequest.post_data.optString(Open311.DESCRIPTION);
                }
				else {
				    // For each attribute, display what the user has entered.
				    // We'll need to do some custom formatting depending on the
				    // attribute type
				    label              = mServiceRequest.getAttributeDescription(labelKey);
				    String type        = mServiceRequest.getAttributeDatatype   (labelKey);
                    String code        = String.format("%s[%s]", AttributeEntryActivity.ATTRIBUTE, labelKey);
                    String chosenValue = mServiceRequest.post_data.optString(code);
				    
                    if (!chosenValue.equals("")) {
                        if (type.equals(Open311.SINGLEVALUELIST) || type.equals(Open311.MULTIVALUELIST)) {
                            try {
                                JSONArray attributeValues = mServiceRequest.getAttributeValues(labelKey);
                                int len = attributeValues.length();
                                
            				    if (type.equals(Open311.SINGLEVALUELIST)) {
            				        // chosenValue will contain the attribute.value.key.
            				        // Display the attribute.value.name
            				        displayValue = mServiceRequest.getAttributeValueName(labelKey, chosenValue);
            				    }
            				    else if (type.equals(Open311.MULTIVALUELIST)) {
            				        // chosenValue will contain a JSONArray of keys.
            				        // Display the names of these keys as a comma-
            				        // separated list
                                    ArrayList<String> names = new ArrayList<String>();
            				        JSONArray keys          = new JSONArray(chosenValue);
            				        len     = keys.length();
            				        for (int i=0; i<len; i++) {
            				            names.add(mServiceRequest.getAttributeValueName(labelKey, keys.getString(i)));
            				        }
            				        displayValue = TextUtils.join(", ", names);
                                }
                            }
                            catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
    				    else {
    				        displayValue = chosenValue;
    				    }
                    }
				}
				item.label       .setText(label);
				item.displayValue.setText(displayValue);
				break;
		}
		return convertView;
	}
}
