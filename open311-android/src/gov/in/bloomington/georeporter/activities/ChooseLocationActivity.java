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
		mLocationManager.requestLocationUpdates(provider, 0, 0, new MapListener());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private class MapListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			mLocationManager.removeUpdates(this);
			
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
