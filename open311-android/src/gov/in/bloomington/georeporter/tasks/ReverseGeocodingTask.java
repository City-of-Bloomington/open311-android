/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.EditText;

public class ReverseGeocodingTask extends AsyncTask<GeoPoint, Void, String> {
    Context mContext;
    EditText mLocationDisplay;

    /**
     * Updates the given view when Google returns an address
     * 
     * @param context
     * @param view
     */
    public ReverseGeocodingTask(Context context, EditText view) {
        super();
        mContext = context;
        mLocationDisplay = view;
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
    protected void onPostExecute(String result) {
    	if (result != null) {
    		mLocationDisplay.setText(result);
    	}
    	super.onPostExecute(result);
    }
}
