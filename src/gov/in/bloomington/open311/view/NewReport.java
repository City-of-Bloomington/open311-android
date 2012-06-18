/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAPI;
import gov.in.bloomington.open311.controller.ServicesItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewReport extends Activity implements OnClickListener  {
		private RelativeLayout r1;
		private ImageView img_photo;
		private EditText edt_newReport;
		private EditText edt_firstName;
		private EditText edt_lastName;
		private EditText edt_email;
		private EditText edt_phone;
		private Button btn_send;
		private TextView failed;

		private String content;
		private static final int CAMERA_REQUEST = 1888; 
		
		/** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.report);
	        
	        //for click listener
			r1 = (RelativeLayout) findViewById(R.id.r1);
		    r1.setOnClickListener((OnClickListener) this);
			
		    img_photo = (ImageView) findViewById(R.id.img_photo);
		    img_photo.setOnClickListener((OnClickListener) this);
		    
		    edt_newReport = (EditText) findViewById(R.id.edt_newReport);
		    edt_newReport.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (edt_newReport.getText().toString().equals("Fill your report here")) {
			        	edt_newReport.setText(""); 
					}
					 edt_newReport.setTextColor(Color.BLACK);
				}
			});
		    
		    btn_send = (Button) findViewById(R.id.btn_submit);
		    btn_send.setOnClickListener((OnClickListener)this);
		    
			failed = (TextView) findViewById(R.id.txt_SendingFailed);
			failed.setVisibility(TextView.GONE);
			
			//get the current server
			SharedPreferences pref = getSharedPreferences("server",0);
			JSONObject server;
			String server_name = null;
			try {
				server = new JSONObject(pref.getString("selectedServer", ""));
				server_name = server.getString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//check whether user connected to the internet
	    	if (GeoreporterAPI.isConnected(NewReport.this)) {
	    		//make the first alert dialog for services group
    	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Available Service Groups from "+server_name);
				final JSONArray jar_services = GeoreporterAPI.streamServices(NewReport.this);
				final CharSequence[] group = ServicesItem.getGroup(jar_services);
	    		builder.setItems(group, new DialogInterface.OnClickListener() {
	    		    public void onClick(DialogInterface dialog, int nid) {
	    		    	
	    		    	//make the second alert dialog for services in selected group
	    		    	AlertDialog.Builder builder = new AlertDialog.Builder(NewReport.this);
	    				builder.setTitle(group[nid]+" Services");
	    				final CharSequence[] services = ServicesItem.getServicesByGroup(jar_services, group[nid]);
	    	    		builder.setItems(services, new DialogInterface.OnClickListener() {
	    	    		    public void onClick(DialogInterface dialog, int nid) {
	    	    		    	
	    	    		    	//check whether the following service has attribute
	    	    		    	if (ServicesItem.hasAttribute(jar_services, services[nid])) {
	    	    		    		//display the attribute
	    	    		    		
	    	    		    		//fetch the atrribute
	    	    		    		JSONObject jo_attributes_service = GeoreporterAPI.getServiceAttribute(NewReport.this, ServicesItem.getServiceCode(jar_services, services[nid]));
	    	    		    		RelativeLayout r0 = (RelativeLayout) findViewById(R.id.r0);
	    	    		    		try {
	    	    		    			JSONArray jar_attributes = jo_attributes_service.getJSONArray("attributes");
	    	    		    			
		    	    		    		int id = 100;
		    	    		    		int n_rb = 0;
		    	    		    		for (int i=0;i<jar_attributes.length();i++) {
		    	    		    			//print the text label
		    	    		    			TextView txt_attribute= new TextView(NewReport.this);
			    	    		            txt_attribute.setText(jar_attributes.getJSONObject(i).getString("description"));
			    	    		            txt_attribute.setTextColor(Color.BLACK);
			    	    		            txt_attribute.setId(++id);
			    	    		            RelativeLayout.LayoutParams txt_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			    	    		            if (i==0) {
			    	    		            	txt_params.addRule(RelativeLayout.BELOW, R.id.r1);
			    	    		            	txt_params.setMargins(15, 15, 15, 0);
			    	    		            }
			    	    		            else {
			    	    		            	txt_params.addRule(RelativeLayout.BELOW, id-n_rb-1);
			    	    		            	txt_params.setMargins(15, 0, 15, 0);
			    	    		            }
			    	    		            txt_attribute.setLayoutParams(txt_params);
			    	    		            r0.addView(txt_attribute);
		    	    		            	n_rb = 0;
			    	    		            
			    	    		            //chekck datatype of input
			    	    		            if (jar_attributes.getJSONObject(i).getString("datatype").equals("text")) {
			    	    		            	//if datatype == text
			    	    		            	EditText edt_attribute = new EditText(NewReport.this);
				    	    		            edt_attribute.setId(++id);
				    	    		            RelativeLayout.LayoutParams edt_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				    	    		            edt_params.addRule(RelativeLayout.BELOW, id-n_rb-1);
				    	    		            edt_params.setMargins(15, 0, 15, 0);
				    	    		            edt_attribute.setLayoutParams(edt_params);
				    	    		            r0.addView(edt_attribute);
			    	    		            	n_rb = 0;
			    	    		            }
			    	    		            else if (jar_attributes.getJSONObject(i).getString("datatype").equals("singlevaluelist")) {
			    	    		            	//if datatype == singlevaluelist
			    	    		            	JSONArray jar_value = jar_attributes.getJSONObject(i).getJSONArray("values");
			    	    		            	n_rb = jar_value.length();
			    	    		            	RadioButton[] rb = new RadioButton[n_rb];
			    	    		                RadioGroup rg = new RadioGroup(NewReport.this); 
			    	    		                rg.setOrientation(RadioGroup.HORIZONTAL);
			    	    		                rg.setId(++id);
			    	    		                for(int j=0; j<jar_value.length(); j++){
			    	    		                    rb[j]  = new RadioButton(NewReport.this);
			    	    		                    rb[j].setId(++id);
			    	    		                    rb[j].setText(jar_value.getJSONObject(j).getString("name"));
			    	    		                    rb[j].setTextColor(Color.BLACK);
			    	    		                    rg.addView(rb[j]); 
			    	    		                }
			    	    		                RelativeLayout.LayoutParams rg_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				    	    		            rg_params.addRule(RelativeLayout.BELOW, id-jar_value.length()-1);
				    	    		            rg_params.setMargins(15, 0, 15, 0);
				    	    		            rg.setLayoutParams(rg_params);
			    	    		                r0.addView(rg);
			    	    		            }
			    	    		            else if (jar_attributes.getJSONObject(i).getString("datatype").equals("multivaluelist")) {
			    	    		            	//if datatype == multivaluelist
			    	    		            	JSONArray jar_value = jar_attributes.getJSONObject(i).getJSONArray("values");
			    	    		            	int n_cb = jar_value.length();
			    	    		            	CheckBox[] cb = new CheckBox[n_cb];
			    	    		            	n_rb = 0;
			    	    		                for(int j=0; j<jar_value.length(); j++){
			    	    		                    cb[j]  = new CheckBox(NewReport.this);
			    	    		                    cb[j].setId(++id);
			    	    		                    cb[j].setText(jar_value.getJSONObject(j).getString("name"));
			    	    		                    cb[j].setTextColor(Color.BLACK);
			    	    		                    r0.addView(cb[j]);
			    	    		                    RelativeLayout.LayoutParams cb_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					    	    		            cb_params.addRule(RelativeLayout.BELOW, id-1);
					    	    		            cb_params.setMargins(15, 0, 15, 0);
					    	    		            cb[j].setLayoutParams(cb_params);
			    	    		                
			    	    		                }
			    	    		                
			    	    		            }
		    	    		    		}
		    	    		    		
		    	    		            TextView txt_firstname= (TextView) findViewById(R.id.txt_firstname);
		    	    		            RelativeLayout.LayoutParams txt_firstname_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		    	    		            txt_firstname_params.addRule(RelativeLayout.BELOW, id-n_rb);
		    	    		            txt_firstname_params.setMargins(15, 20, 0, 0);
		    	    		            txt_firstname.setLayoutParams(txt_firstname_params);
	    	    		            
	    	    		    		} catch (JSONException e) {
	    	    		    			// TODO Auto-generated catch block
	    	    		    			e.printStackTrace();
	    	    		    		}
	    	    		    		
	    	    		    	}
	    	    		    	else
	    	    		    		//post the report
	    	    		    		Toast.makeText(NewReport.this, "Your report has been sent", Toast.LENGTH_LONG).show();
	    	    		    }
	    	    		});
	    	    		AlertDialog alert = builder.create();
	    	    		alert.show();
	    		    }
	    		});
	    		AlertDialog alert = builder.create();
	    		alert.show();
	    	}
	    	//if user is not connected to the internet
	    	else {
	    		Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
	    	}
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
		        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
	            break;
	        
			case R.id.btn_submit:			
				    	    
				
				
				//check whether report is filled
	    	    if (!content.equals("") && !content.equals("Fill your report here")) {
	    	    	
	    	    }
	    	   //if report content hasn't been filled yet
	    	    else 
	    	    	Toast.makeText(this, "Please fill your report first", Toast.LENGTH_SHORT).show();
	    	    
				break;
			}
		}
	    
	    
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (requestCode == CAMERA_REQUEST) {  
	        	if (resultCode == Activity.RESULT_OK) {
		            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
		            img_photo.setImageBitmap(photo);
	        	}
	        }  
	    }  

	    @Override
		protected void onResume (){
			super.onResume();
			
			//for Shared Preferences
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        edt_firstName = (EditText) findViewById(R.id.edt_firstname);
	        edt_firstName.setText(preferences.getString("firstname", ""));
	        
	        edt_lastName = (EditText) findViewById(R.id.edt_lastname);
	        edt_lastName.setText(preferences.getString("lastname", ""));
	        
	        edt_email = (EditText) findViewById(R.id.edt_email);
	        edt_email.setText(preferences.getString("email", ""));
	        
	        edt_phone = (EditText) findViewById(R.id.edt_phone);
	        edt_phone.setText(preferences.getString("phone", ""));
		}	    
	 
}
