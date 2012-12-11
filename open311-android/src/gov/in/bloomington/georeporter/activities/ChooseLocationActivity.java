/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ChooseLocationActivity extends MapActivity {
	private MapView mMap;
	private LocationManager mLocationManager;
	
	public static final int DEFAULT_ZOOM = 17;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_chooser);
		mMap = (MapView)findViewById(R.id.mapview);
		mMap.getController().setZoom(DEFAULT_ZOOM);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = mLocationManager.getBestProvider(c, true);
		
		if (!mLocationManager.isProviderEnabled(provider)) {
		    provider = LocationManager.NETWORK_PROVIDER;
		}
		mLocationManager.requestLocationUpdates(provider, 0, 0, new MapListener());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private class MapListener implements LocationListener {
        private static final int TWO_MINUTES = 1000 * 60 * 2;
	    private Location mCurrentBestLocation;
	    
		@Override
		public void onLocationChanged(Location location) {
		    Log.i("MapListener", "Received a new location");
		    if (isBetterLocation(location, mCurrentBestLocation)) {
		        mLocationManager.removeUpdates(this);
		    }
			
			GeoPoint p = new GeoPoint(
				(int)(location.getLatitude()  * 1e6),
				(int)(location.getLongitude() * 1e6)
			);
			mMap.getController().animateTo(p);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

		/**
		 * Determines whether one Location reading is better than the current Location fix
		 * @param location  The new Location that you want to evaluate
		 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
		 */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		    if (currentBestLocation == null) {
		        // A new location is always better than no location
		        return true;
		    }

		    // Check whether the new location fix is newer or older
		    long timeDelta = location.getTime() - currentBestLocation.getTime();
		    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		    boolean isNewer = timeDelta > 0;

		    // If it's been more than two minutes since the current location, use the new location
		    // because the user has likely moved
		    if (isSignificantlyNewer) {
		        return true;
		    // If the new location is more than two minutes older, it must be worse
		    } else if (isSignificantlyOlder) {
		        return false;
		    }

		    // Check whether the new location fix is more or less accurate
		    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		    boolean isLessAccurate = accuracyDelta > 0;
		    boolean isMoreAccurate = accuracyDelta < 0;
		    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		    // Check if the old and new location are from the same provider
		    boolean isFromSameProvider = isSameProvider(location.getProvider(),
		            currentBestLocation.getProvider());

		    // Determine location quality using a combination of timeliness and accuracy
		    if (isMoreAccurate) {
		        return true;
		    } else if (isNewer && !isLessAccurate) {
		        return true;
		    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
		        return true;
		    }
		    return false;
		}

		/**
		 * Checks whether two providers are the same
		 */
		private boolean isSameProvider(String provider1, String provider2) {
		    if (provider1 == null) {
		      return provider2 == null;
		    }
		    return provider1.equals(provider2);
		}
	}
	
	/**
	 * OnClick handler for the submit button
	 * 
	 * Reads the lat/long at the center of the map and returns
	 * them to the activity that opened the map
	 * 
	 * void
	 */
	public void submit(View v) {
		GeoPoint center = mMap.getMapCenter();
		
		Intent result = new Intent();
		result.putExtra(Open311.LATITUDE,  center.getLatitudeE6());
		result.putExtra(Open311.LONGITUDE, center.getLongitudeE6());
		setResult(RESULT_OK, result);
		finish();
	}
	
	/**
	 * OnClick handler for the cancel button
	 * 
	 * void
	 */
	public void cancel(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}
