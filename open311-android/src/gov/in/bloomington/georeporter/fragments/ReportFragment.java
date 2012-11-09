/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import org.json.JSONException;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.adapters.ServiceRequestAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.Media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ReportFragment extends SherlockListFragment {
    public static final String POSITION = "position";
    public static final int DATA_ENTRY_REQUEST = 0;
    public static final int MEDIA_REQUEST      = 2;
    
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
	    
	    // TODO Figure out which type od dialog to draw
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
	}
	
	/**
	 * Reads data returned from activities and updates mServiceRequest
	 * 
	 * The resultCode will be the id (position) of the mServiceRequestAdapter
	 * row.  Use this id to update each type of data as needed.
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
                    
                case DATA_ENTRY_REQUEST:
                    break;

                default:
                    break;
            }
	    }
	    
	    setListAdapter(new ServiceRequestAdapter(mServiceRequest, getActivity()));
	}
}