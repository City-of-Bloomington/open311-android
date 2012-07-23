/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAPI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class Home extends Activity implements OnClickListener {
	
	private String server_name;
	private ProgressDialog pd;
	private Thread thread_service;
	private boolean pd_shown = false;
	private boolean need_to_load; //need to load server = TRUE if just created or server just changed
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		need_to_load = true;
	}
	
	@Override
	protected void onResume (){
		super.onResume();
		
		//update server information
		SharedPreferences pref = getSharedPreferences("server",0);
		try {
			JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
			server_name = server.getString("name");
			Log.d("home", "home "+server_name);
			Log.d("home", "home "+need_to_load);
			need_to_load = need_to_load || pref.getBoolean("justChanged", false);
			Log.d("home", "home "+need_to_load);
			
			if (!pd_shown && need_to_load) {
				pd = ProgressDialog.show(Home.this, "", "Fetching Server Information...", true, false);
		        pd_shown = true;
				thread_service = new Thread() {
		    		public void run() {	
		    			//check whether user connected to the internet
		    	    	if (GeoreporterAPI.isConnected(Home.this)) {
		    	    		//make the first alert dialog for services group
		    	    		service_handler.post(service_get_report_detail);
		    	    	}
		    	    	//if user is not connected to the internet
		    	    	else {
		    	    		service_handler.post(service_notconnected);
		    	    	}
		    		}
		        };
		        thread_service.start();
			}
			
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		//update image
				ImageView img_splash = (ImageView) findViewById(R.id.img_splash);
				if (server_name.equals("Bloomington, IN"))
					img_splash.setImageResource(R.drawable.bloomington);
				else if (server_name.equals("Baltimore, MD"))
					img_splash.setImageResource(R.drawable.baltimore);
				else if (server_name.equals("Boston, MA"))
					img_splash.setImageResource(R.drawable.boston);
				else 
					img_splash.setImageResource(R.drawable.splash);
				//click listener
				img_splash.setOnClickListener((OnClickListener)this);
	}
	
	//handler for updating topic list
	private Handler service_handler = new Handler();
	
	
	// Create runnable for posting
	//for updating textview
	final Runnable service_get_report_detail = new Runnable() {
	    public void run() {
	        service_update_group_in_ui();
	    }
	};
	
	//for updating not connected message
    final Runnable service_notconnected = new Runnable() {
        public void run() {
            service_update_notconnected_in_ui();
        }
    };
    
    private void service_update_group_in_ui() {
    	//get service
    	JSONArray jar_services = GeoreporterAPI.getServices(Home.this);
    	SharedPreferences pref = getSharedPreferences("server",0);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.putString("ServerService", jar_services.toString());
		//condition adjustment
    	pd_shown = false;
    	need_to_load = false;
		editor.putBoolean("justChanged", false);
		editor.commit();
		
		pd.dismiss();
    	switchTabInActivity(1);
    }
    
    private void service_update_notconnected_in_ui() {
    	pd.dismiss();
    	pd_shown = false;
    	Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
    }
    
    public void switchTabInActivity(int indexTabToSwitchTo){
		Main ParentActivity;
		ParentActivity = (Main) this.getParent();
		ParentActivity.switchTab(indexTabToSwitchTo);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.img_splash:
				switchTabInActivity(1);
				break;
		}
	}
}
