/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import java.io.File;

import gov.in.bloomington.open311.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class ReportActivity extends Activity implements OnClickListener  {
		private RelativeLayout r1;
		private ImageView img_photo;
		private EditText edt_newReport;
		private Button btn_send;
		private TextView failed;

		private String content;

		
		/** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.report);
	        
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
				
				/*intent = new Intent(ReportActivity.this, LocationMap.class);
				intent.putExtra("address", address);
				intent.putExtra("longitude", longitude);
				intent.putExtra("latitude", latitude);
				intent.putExtra("content", content);
	            startActivity(intent);*/
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
		    		    	AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		    				builder.setTitle("Please Choose Service");
		    				CharSequence[] group = new CharSequence[5];
		    				group[0] = "service 1";
		    				group[1] = "service 2";
		    				group[2] = "service 3";
		    				group[3] = "service 4";
		    				group[4] = "service 5";
		    	    		builder.setItems(group, new DialogInterface.OnClickListener() {
		    	    		    public void onClick(DialogInterface dialog, int nid) {
		    	        	    	Toast.makeText(ReportActivity.this, "Your report has been sent", Toast.LENGTH_LONG).show();
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

	    
	 
}
