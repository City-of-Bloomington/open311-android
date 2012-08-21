/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.view;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAPI;
import gov.in.bloomington.open311.controller.GeoreporterUtils;
import gov.in.bloomington.open311.controller.ServicesItem;
import gov.in.bloomington.open311.model.ExternalFileAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
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

/*
 * presentation (view) class to display and perform function regarding new report creation
 */
public class NewReport extends Activity implements OnClickListener  {
		//for threading
		private Thread threadService;
		private JSONArray jarAttributes;
		private int id;
		private int lastId = 0;
		private int i;
		private int nRb;
		private RelativeLayout r0;
		private String serverName;
		
		
		private ImageView imgPhoto;
		private EditText edtNewReport;
		private EditText edtFirstName;
		private EditText edtLastName;
		private EditText edtEmail;
		private EditText edtPhone;
		private Button btnSend;
		private TextView failed;
		private Button btnService;
		private Button btnPicture;
		private Button btnLocation;

		private String content;
		Bitmap photo = null;
		private static final int CAMERA_REQUEST = 1888; 
		private static final int GALLERY_REQUEST = 1889; 
		
		//for location
		private LocationManager lm;
		private LocationListener ll;
		private Location loc;
		private double longitude;
		private double latitude;
		
		//for send report
		private String serverJurisdictionId;
		private String serviceCode;
		private String serviceName;
		private String firstName;
		private String lastName;
		private String email;
		private String phone;
		private String deviceId;
		private String attributeContent;
		private JSONArray jarServicesGlobal;
		private JSONArray reply;
		
		SharedPreferences pref;
		private GeoreporterAPI geoApi;
		private ExternalFileAdapter extFileAdapt;
		
		/** Called when the activity is first created. */
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.report);
	        
	      //for acquiring location
		    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			ll = new MyLocationListener();
			
			//if GPS available, use GPS -- first priority
			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
			}
			//if network provider available, use network provider
			if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))  {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
			}
	        
	        //for click listener
		    imgPhoto = (ImageView) findViewById(R.id.img_photo);
		    imgPhoto.setOnClickListener((OnClickListener) this);
		    
		    edtNewReport = (EditText) findViewById(R.id.edt_newReport);
		    edtNewReport.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (edtNewReport.getText().toString().equals("Fill your report here")) {
			        	edtNewReport.setText(""); 
					}
					 edtNewReport.setTextColor(Color.BLACK);
				}
			});
		    
		    btnSend = (Button) findViewById(R.id.btn_submit);
		    btnSend.setOnClickListener((OnClickListener)this);
		    
			failed = (TextView) findViewById(R.id.txt_SendingFailed);
			failed.setVisibility(TextView.GONE);
			
			btnService = (Button) findViewById(R.id.btn_service);
			btnService.setOnClickListener((OnClickListener)this);
			
			btnPicture = (Button) findViewById(R.id.btn_picture);
			btnPicture.setOnClickListener((OnClickListener)this);
			
			btnLocation = (Button) findViewById(R.id.btn_location);
			btnLocation.setOnClickListener((OnClickListener)this);
			
			extFileAdapt = new ExternalFileAdapter();
	    }
	    
	    /** return function that will be initiate when click is perform to display component */
	    public void onClick(View v) {
			// TODO Auto-generated method stub
		    content = edtNewReport.getText().toString();

			switch (v.getId()) {
			case R.id.btn_location:
				Intent intent = new Intent(NewReport.this, LocationMap.class);
				intent.putExtra("longitude", longitude);
				intent.putExtra("latitude", latitude);
	            startActivity(intent);
	            break;
			
			case R.id.btn_picture:
				AlertDialog.Builder builder = new AlertDialog.Builder(NewReport.this);
				builder.setMessage("Choose Source of Picture")
				       .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
				               startActivityForResult(cameraIntent, CAMERA_REQUEST);
				           }
				       })
				       .setNeutralButton("Gallery", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				   			   startActivityForResult(i, GALLERY_REQUEST);
				           }
				       })
				       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				    	    	dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
	            break;
	        
			case R.id.btn_submit:			
				    	    
				String service_request_id = null;
				
				//check whether report is filled
	    	    if (!content.equals("") && !content.equals("Fill your report here")) {
	    	    	firstName = edtFirstName.getText().toString();
	    	    	lastName = edtLastName.getText().toString();
	    	    	email = edtEmail.getText().toString();
	    	    	phone = edtPhone.getText().toString();
	    	    	
	    	    	//to get device id
	    	    	TelephonyManager telephonyManager;                                             
	    	        telephonyManager  =  ( TelephonyManager )getSystemService( Context.TELEPHONY_SERVICE );
	    	        deviceId = telephonyManager.getDeviceId(); 
	    	    	ServicesItem serviceI = new ServicesItem();
	    	        if (serviceI.hasAttribute(jarServicesGlobal, serviceCode)) {
	    	        
		    	        List<NameValuePair> attribute = new ArrayList<NameValuePair>();
		    	        
		    	        id = 101;
		    	        for (i=0;i<jarAttributes.length();i++) {
		    	        	attributeContent = "";
		    	        	
	    		            //check datatype of input
	    		            try {
								if (jarAttributes.getJSONObject(i).getString("type").equals("text")) {
									//if datatype == text
									EditText edt_contentAttribute = (EditText) findViewById(++id);
									attributeContent = edt_contentAttribute.getText().toString();
									Log.d("new report", "1 edit text "+ attributeContent);
								}
								else if (jarAttributes.getJSONObject(i).getString("type").equals("singlevaluelist")) {
									//if datatype == singlevaluelist
									JSONArray jar_value = jarAttributes.getJSONObject(i).getJSONArray("values");
									nRb = jar_value.length();
									id++; // for radio group
									
									for (int j=0; j< nRb; j++) {
										RadioButton rb_contentAttribute = (RadioButton) findViewById(++id);
										if (rb_contentAttribute.isChecked()) {
											attributeContent = rb_contentAttribute.getText().toString();
										}
									}
									Log.d("new report", "2 radio button "+ attributeContent);
								}
								else if (jarAttributes.getJSONObject(i).getString("type").equals("multivaluelist")) {
									//if datatype == multivaluelist
									boolean isfirst = true;
									JSONArray jar_value = jarAttributes.getJSONObject(i).getJSONArray("values");
									int n_cb = jar_value.length();
									
									for (int j=0; j< n_cb; j++) {
										CheckBox cb_contentAttribute = (CheckBox) findViewById(++id);
										if (cb_contentAttribute.isChecked()) {
											if (isfirst) {
												attributeContent = cb_contentAttribute.getText().toString();
												isfirst = false;
											}
											else 
												attributeContent = attributeContent + ", " +  cb_contentAttribute.getText().toString();
										}
									}
									Log.d("new report", "3 checkbox "+ attributeContent);
								}
								attribute.add(new BasicNameValuePair(jarAttributes.getJSONObject(i).getString("name"), attributeContent));
								id++;
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    		}
		    	        if (photo == null) {
		    	        	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		    				pairs.add(new BasicNameValuePair("jurisdiction_id", serverJurisdictionId));
		    		        pairs.add(new BasicNameValuePair("serviceCode", serviceCode));
		    		        pairs.add(new BasicNameValuePair("lat", latitude+""));
		    		        pairs.add(new BasicNameValuePair("long", longitude+""));
		    		        pairs.add(new BasicNameValuePair("attribute", attribute.toString()));
		    		        pairs.add(new BasicNameValuePair("email", email));
		    		        pairs.add(new BasicNameValuePair("deviceId", deviceId));
		    		        pairs.add(new BasicNameValuePair("firstName", firstName));
		    		        pairs.add(new BasicNameValuePair("lastName", lastName));
		    		        pairs.add(new BasicNameValuePair("phone", phone));
		    		        pairs.add(new BasicNameValuePair("description", content));
		    		        reply = geoApi.sendReport(pairs);
		    	        	//reply = geoApi.sendReport(NewReport.this, serverJurisdictionId, serviceCode, latitude, longitude, true, attribute, email, deviceId, firstName, lastName, phone, content);
		    	        	
		    	        }
		    	        else { 
		    	        	//for picture
		    	        	final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    	        	photo.compress(CompressFormat.JPEG, 50, bos);
		    	        	final byte[] data = bos.toByteArray();
		    	        	final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			    	        try {
			    	        	entity.addPart("media", new ByteArrayBody(data,"photo.jpg"));
								entity.addPart("jurisdiction_id", new StringBody(serverJurisdictionId));
			    	            entity.addPart("serviceCode", new StringBody(serviceCode));
			    	            entity.addPart("lat", new StringBody(latitude+""));
			    	            entity.addPart("long", new StringBody(longitude+""));
			    	            entity.addPart("attribute", new StringBody(attribute.toString()));
			    	            entity.addPart("email", new StringBody(email));
			    	            entity.addPart("deviceId", new StringBody(deviceId));
			    	            entity.addPart("firstName", new StringBody(firstName));
			    	            entity.addPart("lastName", new StringBody(lastName));
			    	            entity.addPart("phone", new StringBody(phone));
			    	            entity.addPart("description", new StringBody(content));
		    	        	} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    	            reply = geoApi.sendReportWithPicture(entity);
		    	        	//reply = geoApi.sendReportWithPicture(NewReport.this, photo, serverJurisdictionId, serviceCode, latitude, longitude, true, attribute, email, deviceId, firstName, lastName, phone, content);
		    	        }
		    	    }
	    	        else {
	    	        	if (photo == null) {
	    	        		final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		    				pairs.add(new BasicNameValuePair("jurisdiction_id", serverJurisdictionId));
		    		        pairs.add(new BasicNameValuePair("serviceCode", serviceCode));
		    		        pairs.add(new BasicNameValuePair("lat", latitude+""));
		    		        pairs.add(new BasicNameValuePair("long", longitude+""));
		    		        pairs.add(new BasicNameValuePair("email", email));
		    		        pairs.add(new BasicNameValuePair("deviceId", deviceId));
		    		        pairs.add(new BasicNameValuePair("firstName", firstName));
		    		        pairs.add(new BasicNameValuePair("lastName", lastName));
		    		        pairs.add(new BasicNameValuePair("phone", phone));
		    		        pairs.add(new BasicNameValuePair("description", content));
		    		        reply = geoApi.sendReport(pairs);
	    	        		//reply = geoApi.sendReport(NewReport.this, serverJurisdictionId, serviceCode, latitude, longitude, false, null, email, deviceId, firstName, lastName, phone, content);
	    	        	}
	    	        	else {
	    	        		//for picture
		    	        	final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    	        	photo.compress(CompressFormat.JPEG, 50, bos);
		    	        	final byte[] data = bos.toByteArray();
		    	        	final MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			    	        try {
			    	        	entity.addPart("media", new ByteArrayBody(data,"photo.jpg"));
								entity.addPart("jurisdiction_id", new StringBody(serverJurisdictionId));
			    	            entity.addPart("serviceCode", new StringBody(serviceCode));
			    	            entity.addPart("lat", new StringBody(latitude+""));
			    	            entity.addPart("long", new StringBody(longitude+""));
			    	            entity.addPart("email", new StringBody(email));
			    	            entity.addPart("deviceId", new StringBody(deviceId));
			    	            entity.addPart("firstName", new StringBody(firstName));
			    	            entity.addPart("lastName", new StringBody(lastName));
			    	            entity.addPart("phone", new StringBody(phone));
			    	            entity.addPart("description", new StringBody(content));
		    	        	} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		    	            reply = geoApi.sendReportWithPicture(entity);
	    	        		
	    	        		//reply = geoApi.sendReportWithPicture(NewReport.this, photo, serverJurisdictionId, serviceCode, latitude, longitude, false, null, email, deviceId, firstName, lastName, phone, content);
	    	        	}
	    	        }
	    	        	
	    	        
	    	        try {
						service_request_id = reply.getJSONObject(0).getString("service_request_id");
						
						if (service_request_id != null) {
		    	        	Toast.makeText(getApplicationContext(), "Report has been sent with service request id :"+service_request_id, Toast.LENGTH_LONG).show();
		    	        	
		    	        	JSONArray ja_savedreports;
		    	        	if (extFileAdapt.readJSON(NewReport.this, "reports") == null) {
		    	        		ja_savedreports = new JSONArray();
		    	        	}
		    	        	else {
		    	        		ja_savedreports = extFileAdapt.readJSON(NewReport.this, "reports");
		    	        	}
		    	        	
		    	        	
		    	        	JSONObject object = new JSONObject();
		    	        	try {
		    	        		Time today = new Time(Time.getCurrentTimezone());
		    	        		today.setToNow();
		    	        		
		    	        		String datetime;
		    	        		GeoreporterUtils georeporterU = new GeoreporterUtils();
		    	        		datetime = georeporterU.getMonth(today.month)+" "+today.monthDay+", "+today.year+" "+today.format("%k:%M:%S");
		    	        		
		    	        		
		    	        		object.put("service_request_id", service_request_id);
		    	        		object.put("jurisdiction_id", serverJurisdictionId);
		    	        		object.put("report_service", serviceName);
		    	        		object.put("date_time", datetime);
		    	        		object.put("server_name", serverName);
		    	        	} catch (JSONException e) {
		    	        		e.printStackTrace();
		    	        	}
		    	        	
		    	        	ja_savedreports.put(object);
		    	        	
		    	        	extFileAdapt = new ExternalFileAdapter();
		    	        	extFileAdapt.writeJSON(NewReport.this, "reports", ja_savedreports);
		    	        	//switch to my report screen
		    				switchTabInActivity(2);
		    	        }
		    	        else {
		    	        	Toast.makeText(getApplicationContext(), "Sending unsuccessful", Toast.LENGTH_LONG).show();
		    	        }
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    	        
	    	    }    
	    	   //if report content hasn't been filled yet
	    	    else 
	    	    	Toast.makeText(this, "Please fill your report first", Toast.LENGTH_SHORT).show();
	    	    
				break;
			
			case R.id.btn_service:
				//for showing group, services, and attributes
		        threadService = new Thread() {
					public void run() {	
						
				      //get the current server
						pref = getSharedPreferences("server",0);
						JSONObject server;
						
						try {
							server = new JSONObject(pref.getString("selectedServer", ""));
							serverName = server.getString("name");
							serverJurisdictionId = server.getString("jurisdiction_id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//check whether user connected to the internet
				    	if (geoApi.isConnected()) {
				    		//make the first alert dialog for services group
				    		service_handler.post(service_update_group);
			    	    	
				    	}
				    	//if user is not connected to the internet
				    	else {
				    		service_handler.post(service_update_notconnected);
				    	}
					}
		        };
		        threadService.start();
				break;
			}
		}
	    
	    /** response as a result activities: camera and gallery */
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (requestCode == CAMERA_REQUEST) {  
	        	if (resultCode == Activity.RESULT_OK) {
	        		imgPhoto.setVisibility(ImageView.VISIBLE);
	        		btnPicture.setText("Change Picture");
		            photo = (Bitmap) data.getExtras().get("data"); 
		            imgPhoto.setImageBitmap(photo);
	        	}
	        }
	        else if (requestCode == GALLERY_REQUEST) {
		        if(resultCode == RESULT_OK){  
		        	imgPhoto.setVisibility(ImageView.VISIBLE);
	        		btnPicture.setText("Change Picture");
		            Uri selectedImage = data.getData();
					try {
						photo = extFileAdapt.decodeUri(selectedImage, this);
			            imgPhoto.setImageBitmap(photo);
			            
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		        }
		    }
	    }  

	    /** called each time new report page is the focused tab or resume from display sleep */
	    @Override
		protected void onResume (){
			super.onResume();
			
			//for Shared Preferences
	        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	        edtFirstName = (EditText) findViewById(R.id.edt_firstname);
	        edtFirstName.setText(preferences.getString("firstname", ""));
	        
	        edtLastName = (EditText) findViewById(R.id.edt_lastname);
	        edtLastName.setText(preferences.getString("lastname", ""));
	        
	        edtEmail = (EditText) findViewById(R.id.edt_email);
	        edtEmail.setText(preferences.getString("email", ""));
	        
	        edtPhone = (EditText) findViewById(R.id.edt_phone);
	        edtPhone.setText(preferences.getString("phone", ""));
	        
	        geoApi = new GeoreporterAPI(NewReport.this);
	    }
	    
	    /** handler for threadService */
	    final Handler service_handler = new Handler();

	    
	    /** display service group with runnable */
	    final Runnable service_update_group = new Runnable() {
	        public void run() {
	            sUpdateGroupInUi();
	        }
	    };
	    
	    /** update not connected message using runnable */
	    final Runnable service_update_notconnected = new Runnable() {
	        public void run() {
	            sUpdateNotConnectedInUi();
	        }
	    };
	    
	    /** display service group dialogbox */
	    private void sUpdateGroupInUi() {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(NewReport.this);
			builder.setTitle("Available Service Groups from "+serverName);
			//final JSONArray jar_services = GeoreporterAPI.getServices(NewReport.this);
			try {
				final JSONArray jar_services = new JSONArray(pref.getString("ServerService", ""));
				jarServicesGlobal = jar_services;
				ServicesItem serviceI = new ServicesItem();
				final CharSequence[] group = serviceI.getGroup(jar_services);
				builder.setItems(group, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int nid) {
				    	displaySDialogbox(group, nid, jar_services);
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
	    }
	    
	    /** display toast for unsuccessful request */
	    private void sUpdateNotConnectedInUi(){
	    	Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
	    }
	    
	    /** display second service group dialogbox (detail service) and manage view displayed */
	    private void displaySDialogbox(CharSequence[] group, int nid, final JSONArray jar_services) {
	    	//make the second alert dialog for services in selected group
	    	AlertDialog.Builder builder = new AlertDialog.Builder(NewReport.this);
			builder.setTitle(group[nid]+" Services");
			final ServicesItem serviceI = new ServicesItem();
			final CharSequence[] services = serviceI.getServicesByGroup(jar_services, group[nid]);
    		builder.setItems(services, new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int nid) {
    		    	
    		    	//set the serviceCode - for report posting
					serviceCode = serviceI.getServiceCode(jar_services, services[nid]);
					serviceName = services[nid]+"";
    		    	
    		    	btnService.setText(serviceI.getServiceDescription(jar_services, services[nid]));
    		    	
		    		//remove previous view
    		    	
    		    	r0 = (RelativeLayout) findViewById(R.id.r0);
    		    	
		    		if (lastId==id) {
    		    		for (int i=100; i<=lastId; i++) {
    		    			r0.removeView(findViewById(i));
    		    		}
    		    		lastId = 0;
	    			}
    		    	
    		    	//check whether the following service has attribute
    		    	if (serviceI.hasAttribute(jar_services, serviceCode)) {
    		    		//display the attribute
    		    		
    		    		//fetch the atrribute
    		    		JSONObject jo_attributes_service = geoApi.getServiceAttribute(serviceI.getServiceCode(jar_services, services[nid]));
    		    		
    		    		try {
    		    			jarAttributes = jo_attributes_service.getJSONArray("attributes");
    		    			
	    		    		id = 100;
	    		    		nRb = 0;
	    		    		for (i=0;i<jarAttributes.length();i++) {
	    		    			
	    		    			//print the text label
	    		    			displayTextview();
	    		    			
    	    		            //chekck datatype of input
    	    		            if (jarAttributes.getJSONObject(i).getString("datatype").equals("text")) {
    	    		            	//if datatype == text
    	    		            	displayEdittext();
    	    		            }
    	    		            else if (jarAttributes.getJSONObject(i).getString("datatype").equals("singlevaluelist")) {
    	    		            	//if datatype == singlevaluelist
    	    		            	dsiplayRadioButton();
    	    		            }
    	    		            else if (jarAttributes.getJSONObject(i).getString("datatype").equals("multivaluelist")) {
    	    		            	//if datatype == multivaluelist
    	    		            	displayComboBox();
    	    		            }
    	    		            lastId = id;
	    		    		}
	    		    		
	    		    		//add first name etc at the right place
	    		            TextView txt_picture = (TextView) findViewById(R.id.txt_picture);
	    		            RelativeLayout.LayoutParams txt_picture_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    		            txt_picture_params.addRule(RelativeLayout.BELOW, id-nRb);
	    		            txt_picture_params.setMargins(15, 20, 0, 0);
	    		            txt_picture.setLayoutParams(txt_picture_params);
    		            
    		    		} catch (JSONException e) {
    		    			// TODO Auto-generated catch block
    		    			e.printStackTrace();
    		    		}
    		    		
    		    	}
    		    	else {
    		    		//add first name etc at the right place
    		    		TextView txt_picture= (TextView) findViewById(R.id.txt_picture);
    		            RelativeLayout.LayoutParams txt_picture_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    		            txt_picture_params.addRule(RelativeLayout.BELOW, R.id.edt_newReport);
    		            txt_picture_params.setMargins(15, 20, 0, 0);
    		            txt_picture.setLayoutParams(txt_picture_params);
    		    	}
    		    }
    		});
    		AlertDialog alert = builder.create();
    		alert.show();
	    }
	    
	    /** display textview if required by selected service */
	    private void displayTextview() {
	        // Back in the UI thread -- update our UI elements based on the data in mResults
	    	TextView txt_attribute= new TextView(NewReport.this);
            try {
				txt_attribute.setText(jarAttributes.getJSONObject(i).getString("description"));
	            txt_attribute.setTextColor(Color.BLACK);
	            txt_attribute.setId(++id);
	            RelativeLayout.LayoutParams txt_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	            if (i==0) {
	            	txt_params.addRule(RelativeLayout.BELOW, R.id.edt_newReport);
	            	txt_params.setMargins(15, 15, 15, 0);
	            }
	            else {
	            	txt_params.addRule(RelativeLayout.BELOW, id-nRb-1);
	            	txt_params.setMargins(15, 0, 15, 0);
	            }
	            txt_attribute.setLayoutParams(txt_params);
	            r0.addView(txt_attribute);
	        	nRb = 0;
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    /** display edittext if required by selected service */
	    private void displayEdittext() {
	    	EditText edt_attribute = new EditText(NewReport.this);
            edt_attribute.setId(++id);
            RelativeLayout.LayoutParams edt_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            edt_params.addRule(RelativeLayout.BELOW, id-nRb-1);
            edt_params.setMargins(15, 0, 15, 0);
            edt_attribute.setLayoutParams(edt_params);
            r0.addView(edt_attribute);
        	nRb = 0;
	    }
	    
	    /** display radiobutton if required by selected service */
	    private void dsiplayRadioButton() {
	    	JSONArray jar_value;
			try {
				jar_value = jarAttributes.getJSONObject(i).getJSONArray("values");
	        	nRb = jar_value.length();
	        	RadioButton[] rb = new RadioButton[nRb];
	            RadioGroup rg = new RadioGroup(NewReport.this); 
	            rg.setOrientation(RadioGroup.VERTICAL);
	            rg.setId(++id);
	            for(int j=0; j<nRb; j++){
	                rb[j]  = new RadioButton(NewReport.this);
	                rb[j].setId(++id);
	                rb[j].setText(jar_value.getJSONObject(j).getString("name"));
	                //rb[j].setText(jar_value.getString(j));
	                rb[j].setTextColor(Color.BLACK);
	                rg.addView(rb[j]); 
	            }
	            RelativeLayout.LayoutParams rg_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	            rg_params.addRule(RelativeLayout.BELOW, id-jar_value.length()-1);
	            rg_params.setMargins(15, 0, 15, 0);
	            rg.setLayoutParams(rg_params);
	            r0.addView(rg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	    /** display combobox if required by selected service */
	    private void displayComboBox() {
	    	JSONArray jar_value;
			try {
				jar_value = jarAttributes.getJSONObject(i).getJSONArray("values");
	        	int n_cb = jar_value.length();
	        	CheckBox[] cb = new CheckBox[n_cb];
	        	nRb = 0;
	            for(int j=0; j<n_cb; j++){
	                cb[j]  = new CheckBox(NewReport.this);
	                cb[j].setId(++id);
	                cb[j].setText(jar_value.getJSONObject(j).getString("name"));
	                //cb[j].setText(jar_value.getString(j));
	                cb[j].setTextColor(Color.BLACK);
	                r0.addView(cb[j]);
	                RelativeLayout.LayoutParams cb_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		            cb_params.addRule(RelativeLayout.BELOW, id-1);
		            cb_params.setMargins(15, 0, 15, 0);
		            cb[j].setLayoutParams(cb_params);
	            }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    /** display another desired tab */
		public void switchTabInActivity(int indexTabToSwitchTo){
			Main ParentActivity;
			ParentActivity = (Main) this.getParent();
			ParentActivity.switchTab(indexTabToSwitchTo);
		}
	    
	    /*
	     * listen (fetch) current user location information
	     */
		private class MyLocationListener implements LocationListener {
			/** update variable latitude and longitude when location changed */
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
