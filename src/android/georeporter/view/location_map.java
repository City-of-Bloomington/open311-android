package android.georeporter.view;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.georeporter.R;
import android.georeporter.controller.maps;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class location_map extends MapActivity {
	private Thread threadadr;
	
	private String content;
	private String address;
	private Double latitude;
	private Double longitude;
	private int latitudeE6;
	private int longitudeE6;
	private MapView mapView;
	private TextView addresst;
	private TextView coordinate;
	     
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	         
	        super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.location_map);
	        
	        //acquiring location, content
	        Bundle extras = getIntent().getExtras();
			if (extras == null) {
				return;
			}
			content = extras.getString("content");
			address = extras.getString("address");
			latitude = extras.getDouble("latitude");
			longitude = extras.getDouble("longitude");
			if (longitude != null && latitude != null) {
				if (address == null) {
					threadadr = new Thread() {
		    			public void run() {
		    				while (address == null || address == "")
		    				address = maps.getFromLocation(latitude, longitude, 2);  
		    				mHandler.post(mUpdateResults);
		    			}
		    		};
		    		threadadr.start();
				}
				else {
			        addresst = (TextView) findViewById(R.id.txt_address);  
			        addresst.setText(address);
				}
		        
		        coordinate = (TextView) findViewById(R.id.txt_latLongt);
		        coordinate.setText("( "+latitude+", "+longitude+" )");
		        
		        latitudeE6 = (int) (latitude * 1e6);
			    longitudeE6 = (int) (longitude * 1e6);

			}
			
	        mapView = (MapView) findViewById(R.id.mapview);      
	        mapView.setBuiltInZoomControls(true);
	         
	         
	        List<Overlay> mapOverlays = mapView.getOverlays();
	        Drawable drawable = this.getResources().getDrawable(R.drawable.icon_location);
	        CustomItemizedOverlay2 itemizedOverlay = new CustomItemizedOverlay2(drawable, this.getApplicationContext());
	        GeoPoint point = new GeoPoint(latitudeE6, longitudeE6);

	        if (!content.equals("")) {
	        	OverlayItem overlayitem = new OverlayItem(point, "GeoReporter", "Your position");
	        	itemizedOverlay.addOverlay(overlayitem);
		        mapOverlays.add(itemizedOverlay);
	        } else {
	        	OverlayItem overlayitem = new OverlayItem(point, "GeoReporter", "Your position");
	        	itemizedOverlay.addOverlay(overlayitem);
		        mapOverlays.add(itemizedOverlay);
	        }
	        MapController mapController = mapView.getController(); 
	        mapController.animateTo(point);
	        mapController.setZoom(14); 

	    }
	 
	    @Override
	    protected boolean isRouteDisplayed() {
	        return false;
	    }
	    
	 // Need handler for callbacks to the UI thread
	    final Handler mHandler = new Handler();

	    // Create runnable for posting
	    final Runnable mUpdateResults = new Runnable() {
	        public void run() {
	            updateResultsInUi();
	        }
	    };
	    
	    
	    private void updateResultsInUi() {

	        // Back in the UI thread -- update our UI elements based on the data in mResults
	    	addresst = (TextView) findViewById(R.id.txt_address);  
	        addresst.setText(address);			
	    }
	    
	    
	    public class CustomItemizedOverlay2 extends ItemizedOverlay<OverlayItem> {	    
	 	   private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

	 	   private Context context;

	 	   public CustomItemizedOverlay2(Drawable defaultMarker) {
	 	        super(boundCenterBottom(defaultMarker));
	 	   }

	 	   public CustomItemizedOverlay2(Drawable defaultMarker, Context context) {
	 	        this(defaultMarker);
	 	        this.context = context;
	 	   }
	 	 
	 	   @Override
	 	   protected OverlayItem createItem(int i) {
	 	      return mapOverlays.get(i);
	 	   }
	 	 
	 	   @Override
	 	   public int size() {
	 	      return mapOverlays.size();
	 	   }
	 	    
	 	   @Override
	 	   protected boolean onTap(int index) {
	 	      OverlayItem item = mapOverlays.get(index);
	 	      Toast.makeText(context, item.getSnippet(), Toast.LENGTH_SHORT).show();
	 	      return true;
	 	   }
	 	    
	 	   public void addOverlay(OverlayItem overlay) {
	 	      mapOverlays.add(overlay);
	 	       this.populate();
	 	   }
	 	 
	 	}
		
}
