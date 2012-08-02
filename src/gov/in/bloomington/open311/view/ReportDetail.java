/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAPI;
import gov.in.bloomington.open311.controller.GeoreporterUtils;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

/*
 * presentation (view) class to display and perform function regarding detail report information
 */
public class ReportDetail extends Activity {
	private JSONObject jo_reports;
	private String jurisdiction_id;
	private String date_time;
	private String report_service;
	private String service_request_id;
	private Thread thread_report;
	
	private TextView txt_report_service;
	private TextView txt_report_date;
	private TextView txt_report_status;
	private TextView txt_report_location;
	private TextView txt_assigned_department;
	private ProgressDialog pd;
	private GeoreporterAPI geoApi;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);
        
        txt_report_service = (TextView) findViewById(R.id.txt_report_service);
        txt_report_date = (TextView) findViewById(R.id.txt_report_date);
        txt_report_status = (TextView) findViewById(R.id.txt_report_status);
        txt_report_location = (TextView) findViewById(R.id.txt_report_location);
        txt_assigned_department = (TextView) findViewById(R.id.txt_assigned_department);
        
        Bundle extras = getIntent().getExtras();
        try {
			jo_reports = new JSONObject(extras.getString("report"));
			jurisdiction_id = jo_reports.getString("jurisdiction_id");
			service_request_id = jo_reports.getString("service_request_id");
			date_time = jo_reports.getString("date_time");
			report_service = jo_reports.getString("report_service");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        geoApi = new GeoreporterAPI(this);
        
        pd = ProgressDialog.show(ReportDetail.this, "", "Retrieving Report Details...", true, false);
        thread_report = new Thread() {
    		public void run() {	
    			//check whether user connected to the internet
    	    	if (geoApi.isConnected()) {
    	    		//make the first alert dialog for services group
    	    		service_handler.post(service_get_report_detail);
        	    	
    	    	}
    	    	//if user is not connected to the internet
    	    	else {
    	    		service_handler.post(service_notconnected);
    	    	}
    		}
        };
        thread_report.start();
    }
    
	/** handler for thread_report */
	private Handler service_handler = new Handler();
	
	/** updates display component with runnable */
	final Runnable service_get_report_detail = new Runnable() {
	    public void run() {
	        service_update_group_in_ui();
	    }
	};
	/** updates not connected message with runnable */
    final Runnable service_notconnected = new Runnable() {
        public void run() {
            service_update_notconnected_in_ui();
        }
    };
	/** performs function in ui for succesfull scenario */
	private void service_update_group_in_ui() {
		JSONObject jo_service_request;
		try {
			jo_service_request = geoApi.getServiceRequests(jurisdiction_id, service_request_id).getJSONObject(0);
			txt_report_service.setText(report_service);
			txt_report_date.setText(date_time);
			txt_report_status.setText(jo_service_request.getString("status"));
			txt_assigned_department.setText(jo_service_request.getString("agency_responsible"));
			if (!jo_service_request.getString("address").equals("null")) {
				txt_report_location.setText(jo_service_request.getString("address"));
			}
			if (jo_service_request.getString("address").equals("null") && !jo_service_request.getString("lat").equals("null") && !jo_service_request.getString("long").equals("null") ) {
				txt_report_location.setText(GeoreporterUtils.getFromLocation(jo_service_request.getDouble("lat"), jo_service_request.getDouble("long"), 2));
			}
			else {
				txt_report_location.setText(jo_service_request.getString("NOT AVAILABLE"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pd.dismiss();
	}
	/** performs function in ui for failure scenario */
	private void service_update_notconnected_in_ui(){
		pd.dismiss();
    	Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
    }

}