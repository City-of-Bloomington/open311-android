/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.tasks;

import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gov.in.bloomington.georeporter.util.json.JSONException;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;

public class ReverseGeocodingTask extends AsyncTask<GeoPoint, Void, String> {
    Context mContext;
    ServiceRequest mServiceRequest;

    /**
     * Updates the given view when Google returns an address
     * 
     * @param context
     * @param view
     */
    public ReverseGeocodingTask(Context context, ServiceRequest sr) {
        super();
        mContext        = context;
        mServiceRequest = sr;
    }

    @Override
    protected String doInBackground(GeoPoint... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
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
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    	}
    	super.onPostExecute(address);
    }
}
