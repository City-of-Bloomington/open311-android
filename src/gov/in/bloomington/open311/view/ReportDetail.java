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
	private JSONObject joReports;
	private String jurisdictionId;
	private String dateTime;
	private String reportService;
	private String sRequestId;
	private Thread threadReport;
	
	private TextView txtReportService;
	private TextView txtReportDate;
	private TextView txtReportStatus;
	private TextView txtReportLocation;
	private TextView txtAssignedDepartment;
	private ProgressDialog pd;
	private GeoreporterAPI geoApi;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);
        
        txtReportService = (TextView) findViewById(R.id.txt_report_service);
        txtReportDate = (TextView) findViewById(R.id.txt_report_date);
        txtReportStatus = (TextView) findViewById(R.id.txt_report_status);
        txtReportLocation = (TextView) findViewById(R.id.txt_report_location);
        txtAssignedDepartment = (TextView) findViewById(R.id.txt_assigned_department);
        
        Bundle extras = getIntent().getExtras();
        try {
			joReports = new JSONObject(extras.getString("report"));
			jurisdictionId = joReports.getString("jurisdiction_id");
			sRequestId = joReports.getString("service_request_id");
			dateTime = joReports.getString("date_time");
			reportService = joReports.getString("report_service");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        geoApi = new GeoreporterAPI(this);
        
        pd = ProgressDialog.show(ReportDetail.this, "", "Retrieving Report Details...", true, false);
        threadReport = new Thread() {
    		public void run() {	
    			//check whether user connected to the internet
    	    	if (geoApi.isConnected()) {
    	    		//make the first alert dialog for services group
    	    		sHandler.post(service_get_report_detail);
        	    	
    	    	}
    	    	//if user is not connected to the internet
    	    	else {
    	    		sHandler.post(service_notconnected);
    	    	}
    		}
        };
        threadReport.start();
    }
    
	/** handler for threadReport */
	private Handler sHandler = new Handler();
	
	/** updates display component with runnable */
	final Runnable service_get_report_detail = new Runnable() {
	    public void run() {
	        sUpdateGroupInUi();
	    }
	};
	/** updates not connected message with runnable */
    final Runnable service_notconnected = new Runnable() {
        public void run() {
            sUpdateNotConnectedInUi();
        }
    };
	/** performs function in ui for succesfull scenario */
	private void sUpdateGroupInUi() {
		JSONObject jo_service_request;
		try {
			jo_service_request = geoApi.getServiceRequests(jurisdictionId, sRequestId).getJSONObject(0);
			txtReportService.setText(reportService);
			txtReportDate.setText(dateTime);
			txtReportStatus.setText(jo_service_request.getString("status"));
			txtAssignedDepartment.setText(jo_service_request.getString("agency_responsible"));
			if (!jo_service_request.getString("address").equals("null")) {
				txtReportLocation.setText(jo_service_request.getString("address"));
			}
			if (jo_service_request.getString("address").equals("null") && !jo_service_request.getString("lat").equals("null") && !jo_service_request.getString("long").equals("null") ) {
				GeoreporterUtils georeporterU = new GeoreporterUtils();
				txtReportLocation.setText(georeporterU.getFromLocation(jo_service_request.getDouble("lat"), jo_service_request.getDouble("long"), 2));
			}
			else {
				txtReportLocation.setText(jo_service_request.getString("NOT AVAILABLE"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pd.dismiss();
	}
	/** performs function in ui for failure scenario */
	private void sUpdateNotConnectedInUi(){
		pd.dismiss();
    	Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
    }

}