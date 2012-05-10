package android.georeporter.view;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.georeporter.R;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class home_new_report extends Activity implements OnClickListener {
	
	private RelativeLayout r1;
	private ImageView img_photo;
	private EditText edt_newReport;
	private Button btn_send;
	private TextView failed;

	private Intent intent;
	private String content;
	private double longitude;
	private double latitude;
	private String address;
	
	//for location
	private LocationManager lm;
	private LocationListener ll;
	private Location loc;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_new_report);
        
      //for acquiring location
	    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new MyLocationListener2();
		
		//if GPS available, use GPS -- first priority
		if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		}
		//if network provider available, use network provider
		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))  {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
		}
        
        //for click listener
		r1 = (RelativeLayout) findViewById(R.id.r1);
	    r1.setOnClickListener((OnClickListener) this);
		
	    img_photo = (ImageView) findViewById(R.id.img_photo);
	    //LaporAPI.setImage(img_photo, "data/lapor/", "photo.jpg", false, false, false, this.getApplicationContext());
	    img_photo.setOnClickListener((OnClickListener) this);
	    
	    edt_newReport = (EditText) findViewById(R.id.edt_newReport);
	    edt_newReport.setOnClickListener((OnClickListener) this);
	    
	    btn_send = (Button) findViewById(R.id.btn_submit);
	    btn_send.setOnClickListener((OnClickListener)this);
	    
		failed = (TextView) findViewById(R.id.txt_SendingFailed);
		failed.setVisibility(TextView.GONE);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
	    content = edt_newReport.getText().toString();

		switch (v.getId()) {
		case R.id.r1:
			
			intent = new Intent(home_new_report.this, location_map.class);
			intent.putExtra("address", address);
			intent.putExtra("longitude", longitude);
			intent.putExtra("latitude", latitude);
			intent.putExtra("content", content);
            startActivity(intent);
            break;
		
		case R.id.img_photo:
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
			camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(android.os.Environment.getExternalStorageDirectory(),"data/lapor/photo.jpg")));
	        this.startActivityForResult(camera, 34);
            break;
        
		case R.id.edt_newReport:
			if (edt_newReport.getText().toString().equals("Fill your report here...")) {
	        	edt_newReport.setText(""); 
			}
	        edt_newReport.setTextColor(Color.BLACK);
	        break;
	        
		case R.id.btn_submit:			
			    	    
    	    if (!content.equals("") && !content.equals("Fill your report here...")) {
    	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Please Choose Service Groups");
				CharSequence[] group = new CharSequence[5];
				group[0] = "service groups 1";
				group[1] = "service groups 2";
				group[2] = "service groups 3";
				group[3] = "service groups 4";
				group[4] = "service groups 5";
	    		builder.setItems(group, new DialogInterface.OnClickListener() {
	    		    public void onClick(DialogInterface dialog, int nid) {
	    		    	AlertDialog.Builder builder = new AlertDialog.Builder(home_new_report.this);
	    				builder.setTitle("Please Choose Service");
	    				CharSequence[] group = new CharSequence[5];
	    				group[0] = "service 1";
	    				group[1] = "service 2";
	    				group[2] = "service 3";
	    				group[3] = "service 4";
	    				group[4] = "service 5";
	    	    		builder.setItems(group, new DialogInterface.OnClickListener() {
	    	    		    public void onClick(DialogInterface dialog, int nid) {
	    	        	    	Toast.makeText(home_new_report.this, "Your report has been sent", Toast.LENGTH_LONG).show();
	    	    		    }
	    	    		});
	    	    		AlertDialog alert = builder.create();
	    	    		alert.show();
	    		    }
	    		});
	    		AlertDialog alert = builder.create();
	    		alert.show();
    	    }
    	   
    	    else 
    	    	Toast.makeText(this, "Please fill your report first", Toast.LENGTH_SHORT).show();
    	    
			break;
		}
	}

    @Override
    public void onDestroy() {
        System.gc();
	// Remove the listener you previously added
		lm.removeUpdates(ll);
		super.onDestroy();
	}
    
 // Our own LocationListener class 
	private class MyLocationListener2 implements LocationListener {

		public void onLocationChanged(Location location) {
			if (location != null) {
				loc = location;
				latitude = loc.getLatitude();
				longitude = loc.getLongitude();
			}

		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
		

	}
    
}