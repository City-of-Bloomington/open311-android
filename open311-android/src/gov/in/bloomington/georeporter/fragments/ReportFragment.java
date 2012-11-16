/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.AttributeEntryActivity;
import gov.in.bloomington.georeporter.activities.ChooseLocationActivity;
import gov.in.bloomington.georeporter.activities.DataEntryActivity;
import gov.in.bloomington.georeporter.activities.MainActivity;
import gov.in.bloomington.georeporter.activities.SavedReportsActivity;
import gov.in.bloomington.georeporter.adapters.ServiceRequestAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.Media;
import gov.in.bloomington.georeporter.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;

public class ReportFragment extends SherlockFragment implements OnItemClickListener{
    /**
     * Request for handling Photo attachments to the Service Request
     */
    public static final int MEDIA_REQUEST      = 0;
    /**
     * Request for handling lat, long, and address
     */
    public static final int LOCATION_REQUEST   = 1;
    /**
     * Request to handle all the attributes
     */
    public static final int ATTRIBUTE_REQUEST  = 2;
    /**
     * Request to handle all the rest of the basic parameters.
     * ie. description, firstname, lastname, email, etc.
     */
    public static final int DATA_ENTRY_REQUEST = 3;
    
    private static final List<String> DATA_ENTRY_FIELDS = Arrays.asList(
        Open311.DESCRIPTION, Open311.FIRST_NAME, Open311.LAST_NAME, Open311.EMAIL, Open311.PHONE
    );
    
    
	private ServiceRequest mServiceRequest;
    private ListView       mListView;
	private Uri            mImageUri;
	
	/**
	 * @param sr
	 * @return
	 * ReportFragment
	 */
	public static ReportFragment newInstance(ServiceRequest sr) {
	    ReportFragment fragment = new ReportFragment();
	    Bundle args = new Bundle();
	    args.putString(ServiceRequest.SERVICE_REQUEST, sr.toString());
	    fragment.setArguments(args);
	    return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mServiceRequest = new ServiceRequest(getArguments().getString(ServiceRequest.SERVICE_REQUEST));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.fragment_report, container, false);
	    mListView = (ListView) v.findViewById(R.id.reportListView);
        mListView.setAdapter(new ServiceRequestAdapter(mServiceRequest, getActivity()));
        mListView.setOnItemClickListener(this);
        
        v.findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new PostServiceRequestTask().execute();
            }
        });
        v.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
	    return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString(ServiceRequest.SERVICE_REQUEST, mServiceRequest.toString());
	}
	
	/**
	 * Starts a seperate activity for each report field
	 * 
	 * Design background:
	 * We cannot fit all the text and controls onto a single screen.
	 * In addition, controls like the Camera and Map chooser must be in a
	 * seperate activity anyway.  This streamlines the process so each 
	 * report field is handled the same way.
	 */
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	    ServiceRequestAdapter adapter = (ServiceRequestAdapter) l.getAdapter();
	    
	    if (adapter.getItemViewType(position) != ServiceRequestAdapter.TYPE_HEADER) {
	        // TODO Figure out which type of dialog to draw
	        String labelKey = (String) adapter.getItem(position);
	        
	        if (labelKey.equals(Open311.MEDIA)) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setMessage(R.string.choose_media_source)
	                   .setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
	                       /**
	                        * Start the camera activity
	                        * 
	                        * To avoid differences in non-google-provided camera activities,
	                        * we should always tell the camera activity to explicitly save
	                        * the file in a Uri of our choosing.
	                        * 
	                        * The camera activity may, or may not, also save an image file 
	                        * in the gallery.  For now, I'm just not going to worry about
	                        * creating duplicate files on people's phones.  Users can clean
	                        * those up themselves, if they want.
	                        */
	                       public void onClick(DialogInterface dialog, int id) {
	                           mImageUri = Media.getOutputMediaFileUri(Media.MEDIA_TYPE_IMAGE);
	                           
	                           Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                           i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	                           startActivityForResult(i, MEDIA_REQUEST);
	                       }
	                   })
	                   .setNeutralButton(R.string.gallery, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	                           i.setType("image/*");
	                           startActivityForResult(i, MEDIA_REQUEST);
	                       }
	                   })
	                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                            dialog.cancel();
	                       }
	                   });
	            AlertDialog alert = builder.create();
	            alert.show();
	        }
	        else if (labelKey.equals(Open311.ADDRESS)) {
	            Intent i = new Intent(getActivity(), ChooseLocationActivity.class);
	            startActivityForResult(i, LOCATION_REQUEST);
	        }
	        else if (DATA_ENTRY_FIELDS.contains(labelKey)) {
	            TextView label = (TextView) v.findViewById(android.R.id.text1);
	            
	            Intent i = new Intent(getActivity(), DataEntryActivity.class);
	            i.putExtra(DataEntryActivity.KEY,    labelKey);
	            i.putExtra(DataEntryActivity.VALUE,  mServiceRequest.post_data.optString(labelKey));
                i.putExtra(DataEntryActivity.PROMPT, label.getText().toString());
	            startActivityForResult(i, DATA_ENTRY_REQUEST);
	        }
	        else {
	            // Create a chooser activity that can handle all attributes
	            // We'll need to send in the attribute definition
	            try {
                    JSONObject attribute = mServiceRequest.getAttribute(labelKey);
                    
                    // For datetime attributes, we'll just pop open a date picker dialog
                    String datatype = attribute.optString(Open311.DATATYPE, Open311.STRING);
                    if (datatype.equals(Open311.DATETIME)) {
                        DatePickerDialogFragment datePicker = new DatePickerDialogFragment(labelKey);
                        datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
                    }
                    // all other attribute types get a full seperate Activity
                    else {
                        Intent i = new Intent(getActivity(), AttributeEntryActivity.class);
                        i.putExtra(AttributeEntryActivity.ATTRIBUTE, attribute.toString());
                        startActivityForResult(i, ATTRIBUTE_REQUEST);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	        }
	    }
	}
	
	/**
	 * Reads data returned from activities and updates mServiceRequest
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    if (resultCode == Activity.RESULT_OK) {
	        try {
    	        switch (requestCode) {
                    case MEDIA_REQUEST:
                        // Determine if this is from the camera or gallery
                        Uri imageUri = (mImageUri != null) ? mImageUri : data.getData();
                        if (imageUri != null) {
                            mServiceRequest.post_data.put(Open311.MEDIA, imageUri.toString());
                            mImageUri = null; // Remember to wipe it out, so we don't confuse camera and gallery
                        }
                        break;
                        
                    case LOCATION_REQUEST:
                        int latitudeE6  = data.getIntExtra(Open311.LATITUDE,  0);
                        int longitudeE6 = data.getIntExtra(Open311.LONGITUDE, 0);
                        
                        mServiceRequest.post_data.put(Open311.LATITUDE , latitudeE6  / 1e6);
                        mServiceRequest.post_data.put(Open311.LONGITUDE, longitudeE6 / 1e6);
                        // Display the lat/long as text for now
                        // It will get replaced with the address when ReverseGeoCodingTask returns
                        new ReverseGeocodingTask().execute(new GeoPoint(latitudeE6, longitudeE6));
                        break;
                        
                    /**
                     * Case to handle all the text-based parameters
                     * description, firstname, lastname, etc.
                     */
                    case DATA_ENTRY_REQUEST:
                        String labelKey = data.getStringExtra(DataEntryActivity.KEY);
                        String val      = data.getStringExtra(DataEntryActivity.VALUE);
                        mServiceRequest.post_data.put(labelKey, val);
                        break;
                        
                    /**
                     * Case to handle all possible attributes
                     */
                    case ATTRIBUTE_REQUEST:
                        String code     = data.getStringExtra(Open311.CODE);
                        String datatype = data.getStringExtra(Open311.DATATYPE);
                        String value    = data.getStringExtra(AttributeEntryActivity.VALUE);
                        
                        String key = String.format("%s[%s]", AttributeEntryActivity.ATTRIBUTE, code);
                        
                        // Multivaluelist attributes will return a JSON string
                        // containg a JSONArray of values the user chose
                        if (datatype.equals(Open311.MULTIVALUELIST)) {
                            JSONArray array = new JSONArray(value);
                            mServiceRequest.post_data.put(key, array);
                        }
                        else {
                            mServiceRequest.post_data.put(key, value);
                        }
                        break;
    
                    default:
                        break;
                }
	        }
	        catch (JSONException e) {
                // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    
	    refreshAdapter();
	}
	
	private void refreshAdapter() {
        ServiceRequestAdapter a = (ServiceRequestAdapter) mListView.getAdapter();
        a.updateServiceRequest(mServiceRequest);
	}
	
	/**
	 * A basic date picker used for DateTime attributes
	 * 
	 * Pass in the attribute code that you want the user to enter a date for
	 */
	private class DatePickerDialogFragment extends SherlockDialogFragment implements OnDateSetListener {
	    private String mAttributeCode;
	    
	    /**
	     * @param code The attribute code to update in mServiceRequest
	     */
	    public DatePickerDialogFragment(String code) {
	        mAttributeCode = code;
	    }
	    
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        Calendar c = Calendar.getInstance();
	        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	    }

	    @Override
	    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	        Calendar c = Calendar.getInstance();
	        c.set(year, monthOfYear, dayOfMonth);
	        try {
	            String code = String.format("%s[%s]", AttributeEntryActivity.ATTRIBUTE, mAttributeCode);
	            String date = DateFormat.getDateFormat(getActivity()).format(c.getTime());
	            Log.i("ReportFragment", String.format("Saving %s, %s", code, date));
                mServiceRequest.post_data.put(code, date);
                refreshAdapter();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	    }
	}
	
	/**
	 * Task for using Google's Geocoder
	 * 
	 * Queries Google's geocode, updates the address in ServiceRequest,
	 * then refreshes the view so the user can see the change
	 */
	private class ReverseGeocodingTask extends AsyncTask<GeoPoint, Void, String> {
	    @Override
	    protected String doInBackground(GeoPoint... params) {
	        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
	        GeoPoint point = params[0];
	        double latitude  = point.getLatitudeE6()  / 1e6;
	        double longitude = point.getLongitudeE6() / 1e6;

	        List<Address> addresses = null;
	        try {
	            addresses = geocoder.getFromLocation(latitude, longitude, 1);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        if (addresses != null && addresses.size() > 0) {
	            Address address = addresses.get(0);
	            return String.format("%s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
	        }
	        return null;
	    }
	    
	    @Override
	    protected void onPostExecute(String address) {
	        if (address != null) {
	            try {
	                mServiceRequest.post_data.put(Open311.ADDRESS, address);
	                refreshAdapter();
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	        super.onPostExecute(address);
	    }
	}
	
	/**
	 * AsyncTask for sending the ServiceRequest to the endpoint
	 * 
	 * When finished the user will be sent to the Saved Reports screen
	 */
	private class PostServiceRequestTask extends AsyncTask<Void, Void, Boolean> {
	    private ProgressDialog mDialog;
	    
	    @Override
	    protected void onPreExecute() {
            super.onPreExecute();
            mDialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_posting_service), "", true);
	    }
	    
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONArray response = Open311.postServiceRequest(mServiceRequest, getActivity());
            if (response.length() > 0) {
                try {
                    mServiceRequest.service_request = response.getJSONObject(0);
                    return Open311.saveServiceRequest(getActivity(), mServiceRequest);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
	    
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if (!result) {
                Util.displayCrashDialog(getActivity(), getString(R.string.failure_posting_service));
            }
            else {
                Intent intent = new Intent(getActivity(), SavedReportsActivity.class);
                startActivity(intent);
            }
        }
	}

}