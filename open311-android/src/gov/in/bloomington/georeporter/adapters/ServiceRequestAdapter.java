/**
 * A ListView Adapter for displaying a single service request
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceRequestAdapter extends BaseAdapter {
	private ServiceRequest mServiceRequest;
	private static LayoutInflater mLayoutInflater;
	
	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM   = 1;
	private static final int TYPE_MEDIA  = 2;
	
	// A key string for every item in the listview
	// We use this to define the order of fields in the listview
	private static final List<String>  labels    = Arrays.asList(
		Open311.SERVICE_NAME,
		Open311.MEDIA,
		Open311.ADDRESS,
		Open311.DESCRIPTION
	);
	// The int positions of labels that are supposed to be headers
	private static final List<Integer> headers = Arrays.asList(0);
	
	/**
	 * Prepare display strings from the ServiceRequest
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
				labels.add(Open311.ATTRIBUTES);
				int len = attributes.length();
				headers.add(len - 1);
				
				// Loop over all the attributes and add each attribute code
				// to the labels list
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
					header.title = (TextView)convertView.findViewById(R.id.title);
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
                }
                else {
                    media = (MediaViewHolder)convertView.getTag();
                }
                // TODO populate chosen image
                break;
                
			case TYPE_ITEM:
				ItemViewHolder item;
				
				if (convertView == null) {
					convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
					item = new ItemViewHolder();
					item.label        = (TextView)convertView.findViewById(android.R.id.text1);
					item.displayValue = (TextView)convertView.findViewById(android.R.id.text2);
				}
				else {
					item = (ItemViewHolder)convertView.getTag();
				}
				
				// TODO Populate view strings
				String label        = "";
				String displayValue = "";
				
				if (labelKey.equals(Open311.ADDRESS)) {
	                // Display the address, if we have it already, otherwise
				    // show the lat/long. Users are going to be choosing the
				    // lat/long with a map. Since we're looking up the address
				    // in an Async call to Google, there's going to be a delay
				    // between when we have the lat/long and when we have an
				    // address.
				    label = convertView.getResources().getString(R.string.location);
				    displayValue = mServiceRequest.service_request.optString(Open311.ADDRESS);
				    if (displayValue.equals("")) {
				        double latitude  = mServiceRequest.service_request.optDouble(Open311.LATITUDE);
				        double longitude = mServiceRequest.service_request.optDouble(Open311.LONGITUDE);
				        if (latitude != Double.NaN && longitude != Double.NaN) {
				            displayValue = String.format("%f, %f", latitude, longitude);
				        }
				    }
				}
				else if (labelKey.equals(Open311.DESCRIPTION)) {
				    // Just show whatever the user typed
                    label = convertView.getResources().getString(R.string.report_description);
                    displayValue = mServiceRequest.service_request.optString(Open311.DESCRIPTION);
                }
				else {
				    // For each attribute, display what the user has entered.
				    // We'll need to do some custom formatting depending on the
				    // attribute type
				    label = mServiceRequest.getAttributeDescription(labelKey);
				    String type = mServiceRequest.getAttributeDatatype(labelKey);
				    if (type.equals(Open311.SINGLEVALUELIST)) {
				        
				    }
				    else if (type.equals(Open311.MULTIVALUELIST)) {
                        
                    }
				    else {
				        
				    }
				}
				item.label       .setText(label);
				item.displayValue.setText(displayValue);
				break;
		}
		return convertView;
	}
}
