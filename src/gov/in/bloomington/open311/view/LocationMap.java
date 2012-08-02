/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.view;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterUtils;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/*
 * presentation (view) class to display and perform function of location & map page
 */
public class LocationMap extends MapActivity {
	private Thread threadadr;
	
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;
	
	private String address = "";
	private Double latitude;
	private Double longitude;
	private int latitudeE6;
	private int longitudeE6;
	private TextView addresst;
	private TextView coordinate;
	     
		/** Called when activity first created */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	         
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.location_map);
	        
	        //acquiring location, content
	        Bundle extras = getIntent().getExtras();
			if (extras == null) {
				return;
			}
			latitude = extras.getDouble("latitude");
			longitude = extras.getDouble("longitude");
			if (longitude != null && latitude != null) {
				threadadr = new Thread() {
	    			public void run() {
	    				while (address.equals(""))
	    				address = GeoreporterUtils.getFromLocation(latitude, longitude, 2);  
	    				mHandler.post(mUpdateResults);
	    			}
	    		};
	    		threadadr.start();
				
		        coordinate = (TextView) findViewById(R.id.txt_latLongt);
		        coordinate.setText("( "+latitude+", "+longitude+" )");
		        
		        latitudeE6 = (int) (latitude * 1e6);
			    longitudeE6 = (int) (longitude * 1e6);

			}
			
	        mapView = (MapView) findViewById(R.id.mapview);      
	        mapView.setBuiltInZoomControls(true);
	         
	         
	        GeoPoint point = new GeoPoint(latitudeE6, longitudeE6);
	        // create an overlay that shows our current location
			myLocationOverlay = new MyLocationOverlay(this, mapView);
			//myLocationOverlay.drawMyLocation(canvas, mapView, lastFix, initial_point, when);
			
			// add this overlay to the MapView and refresh it
			mapView.getOverlays().add(myLocationOverlay);
			mapView.postInvalidate();
	        
	        MapController mapController = mapView.getController(); 
	        mapController.animateTo(point);
	        mapController.setZoom(14); 
	    }
	 
	    /** Called everytime LocationMap is the focused tab or display is resumed */
	    @Override
		protected void onResume() {
			super.onResume();
			// when our activity resumes, we want to register for location updates
			myLocationOverlay.enableMyLocation();
		}

	    /** Called everytime MyServers is paused */
		@Override
		protected void onPause() {
			super.onPause();
			// when our activity pauses, we want to remove listening for location updates
			myLocationOverlay.disableMyLocation();
		}
	    
		/** decide whether display can be routed or not */
	    @Override
	    protected boolean isRouteDisplayed() {
	        return false;
	    }
	    
	    /** handler for the UI thread */
	    final Handler mHandler = new Handler();

	    /** update result using runnable */
	    final Runnable mUpdateResults = new Runnable() {
	        public void run() {
	            updateResultsInUi();
	        }
	    };
	    
	    /** perform action in the UI */
	    private void updateResultsInUi() {

	        // Back in the UI thread -- update our UI elements based on the data in mResults
	    	addresst = (TextView) findViewById(R.id.txt_address);  
	        addresst.setText(address);			
	    }
	    
	    
}

