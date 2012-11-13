/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.AttributeEntryActivity;
import gov.in.bloomington.georeporter.activities.ChooseLocationActivity;
import gov.in.bloomington.georeporter.activities.DataEntryActivity;
import gov.in.bloomington.georeporter.adapters.ServiceRequestAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.Media;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.android.maps.GeoPoint;

public class ReportFragment extends SherlockListFragment {
    public static final int DATA_ENTRY_REQUEST = 0;
    public static final int MEDIA_REQUEST      = 1;
    public static final int LOCATION_REQUEST   = 2;
    public static final int ATTRIBUTE_REQUEST  = 3;
    
	private ServiceRequest mServiceRequest;
	private Uri mImageUri;
	
	/**
	 * @param sr
	 * @return
	 * ReportFragment
	 */
	public static ReportFragment newInstance(ServiceRequest sr) {
	    ReportFragment fragment = new ReportFragment();
	    Bundle args = new Bundle();
	    args.putString(Open311.SERVICE_REQUEST, sr.toString());
	    fragment.setArguments(args);
	    return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mServiceRequest = new ServiceRequest(getArguments().getString(Open311.SERVICE_REQUEST));
	    setListAdapter(new ServiceRequestAdapter(mServiceRequest, getActivity()));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString(Open311.SERVICE_REQUEST, mServiceRequest.toString());
	}
	
	/**
	 * Starts a seperate activity for each report field
	 * 
	 * The id (same as position) of the item clicked should be passed as the
	 * requestCode in startActivityForResult().  That way we can use the 
	 * request code inside of onActivityResult to update the correct data 
	 * in mServiceRequest.
	 * 
	 * Design background:
	 * We cannot fit all the text and controls onto a single screen.
	 * In addition, controls like the Camera and Map chooser must be in a
	 * seperate activity anyway.  This streamlines the process so each 
	 * report field is handled the same way.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    
	    if (getListAdapter().getItemViewType(position) != ServiceRequestAdapter.TYPE_HEADER) {
	        // TODO Figure out which type of dialog to draw
	        String labelKey = (String) getListAdapter().getItem(position);
	        
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
	        else if (labelKey.equals(Open311.DESCRIPTION)) {
	            Intent i = new Intent(getActivity(), DataEntryActivity.class);
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
                        String c = "gov.in.bloomington.georeporter.activities.AttributeEntryActivity";
                        Intent i = new Intent(getActivity(), Class.forName(c));
                        i.putExtra(AttributeEntryActivity.ATTRIBUTE, attribute.toString());
                        startActivityForResult(i, ATTRIBUTE_REQUEST);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
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
	        switch (requestCode) {
                case MEDIA_REQUEST:
                    // Determine if this is from the camera or gallery
                    Uri imageUri = (mImageUri != null) ? mImageUri : data.getData();
                    if (imageUri != null) {
                        try {
                            mServiceRequest.post_data.put(Open311.MEDIA, imageUri.toString());
                            mImageUri = null; // Remember to wipe it out, so we don't confuse camera and gallery
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                    
                case LOCATION_REQUEST:
                    int latitudeE6  = data.getIntExtra(Open311.LATITUDE,  0);
                    int longitudeE6 = data.getIntExtra(Open311.LONGITUDE, 0);
                    
                    try {
                        mServiceRequest.post_data.put(Open311.LATITUDE , latitudeE6  / 1e6);
                        mServiceRequest.post_data.put(Open311.LONGITUDE, longitudeE6 / 1e6);
                        
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // Display the lat/long as text for now
                    // It will get replaced with the address when ReverseGeoCodingTask returns
                    new ReverseGeocodingTask().execute(new GeoPoint(latitudeE6, longitudeE6));
                    break;
                    
                case DATA_ENTRY_REQUEST:
                    break;
                    
                case ATTRIBUTE_REQUEST:
                    String code     = data.getStringExtra(Open311.CODE);
                    String datatype = data.getStringExtra(Open311.DATATYPE);
                    break;

                default:
                    break;
            }
	    }
	    
	    refreshAdapter();
	}
	
	private void refreshAdapter() {
        ServiceRequestAdapter a = (ServiceRequestAdapter) getListAdapter();
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
                mServiceRequest.post_data.put(mAttributeCode, DateFormat.getDateFormat(getActivity()).format(c.getTime()));
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
	                Log.i("ReverseGeocodingTask", "Updated adapter with address " + address);
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	        super.onPostExecute(address);
	    }
	}

}