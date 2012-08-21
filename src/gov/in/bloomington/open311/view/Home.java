/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
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

/*
 * presentation (view) class to display and perform default tabhost page (home)
 */
public class Home extends Activity implements OnClickListener {
	
	private transient ProgressDialog progressd;
	private transient Thread threadService;
	private transient boolean pdShown = false;
	private transient boolean needToLoad; //need to load server = TRUE if just created or server just changed
	private transient GeoreporterAPI geoApi;
	
	/** Called when the activity first created */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		needToLoad = true;
	}
	
	/** Called everytime Home is the focused tab or display is resumed */
	@Override
	protected void onResume (){
		
		String serverName = "";
		
		super.onResume();
		
		geoApi = new GeoreporterAPI(this);
		
		//update server information
		final SharedPreferences pref = getSharedPreferences("server",0);
		try {
			final JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
			serverName = server.getString("name");
			
			needToLoad = needToLoad || pref.getBoolean("justChanged", false);
			
			if (!pdShown && needToLoad) {
				progressd = ProgressDialog.show(Home.this, "", "Fetching Server Information...", true, false);
		        pdShown = true;
				threadService = new Thread() {
		    		public void run() {	
		    			//check whether user connected to the internet
		    	    	if (geoApi.isConnected()) {
		    	    		sHandler.post(sGetReportDetail);
		    	    	}
		    	    	//if user is not connected to the internet
		    	    	else {
		    	    		sHandler.post(sNotConnected);
		    	    	}
		    		}
		        };
		        threadService.start();
			}
			
		}
		catch (JSONException e) {
			Log.e("Home onResume", e.toString());
		}
		
		//update image
		final ImageView img_splash = (ImageView) findViewById(R.id.img_splash);
		if ("Bloomington, IN".equals(serverName)) {
			img_splash.setImageResource(R.drawable.bloomington);
		}
		else if ("Bloomington, IN".equals(serverName)) {
			img_splash.setImageResource(R.drawable.baltimore);
		}
		else if ("Boston, MA".equals(serverName)) {
			img_splash.setImageResource(R.drawable.boston);
		}
		else { 
			img_splash.setImageResource(R.drawable.splash);
		}
		//click listener
		img_splash.setOnClickListener((OnClickListener)this);
	}
	
	/** handler for updating topic list */
	private final transient Handler sHandler = new Handler();
	
	/** get report detail using runnable */
	private final transient Runnable sGetReportDetail = new Runnable() {
	    public void run() {
	        sUpdateGroupInUi();
	    }
	};
	
	/** display not connected message using runnable */
    private final transient Runnable sNotConnected = new Runnable() {
        public void run() {
            sUpdateNotConnectedInUi();
        }
    };
    
    /** get report detail in UI */
    private void sUpdateGroupInUi() {
    	//get service
    	final JSONArray jar_services = geoApi.getServices();
    	final SharedPreferences pref = getSharedPreferences("server",0);
		final SharedPreferences.Editor editor = pref.edit();
		
		editor.putString("ServerService", jar_services.toString());
		//condition adjustment
    	pdShown = false;
    	needToLoad = false;
		editor.putBoolean("justChanged", false);
		editor.commit();
		
		Log.d("home", "home connected");
		
		progressd.dismiss();
    	switchTabInActivity(1);
    }
    
    /** display toast if not connected */
    private void sUpdateNotConnectedInUi() {
    	progressd.dismiss();
    	pdShown = false;
    	Toast.makeText(getApplicationContext(), "No internet connection or the server URL is not vaild", Toast.LENGTH_LONG).show();
    }
    
    /** switch to other desired activity */
    public void switchTabInActivity(final int iTabToSwitchTo){
		Main ParentActivity;
		ParentActivity = (Main) this.getParent();
		ParentActivity.switchTab(iTabToSwitchTo);
	}
    
    /** perform action when display item clicked */
	public void onClick(final View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.img_splash) {
			switchTabInActivity(1);
		}
	}
}
