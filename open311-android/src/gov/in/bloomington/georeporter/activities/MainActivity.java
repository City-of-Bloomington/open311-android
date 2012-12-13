/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.Preferences;
import gov.in.bloomington.georeporter.util.Util;

import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {
    private static String SPLASH_IMAGE = "splash_image";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		JSONObject current_server = Preferences.getCurrentServer(this);
		
		if (current_server == null) {
    		startActivity(new Intent(this, SettingsActivity.class));
		}
		else {
			new EndpointLoader().execute(current_server);
			
			try {
                getSupportActionBar().setTitle(current_server.getString(Open311.NAME));
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
            String imageName = current_server.optString(SPLASH_IMAGE);
            if (imageName != "") {
                ImageView splash = (ImageView) findViewById(R.id.splash);
                splash.setImageResource(getResources().getIdentifier(imageName, "drawable", getPackageName()));
            }
		}
	}
	
	/**
	 * OnClick handler for activity_main layout
	 * 
	 * @param v
	 * void
	 */
	public void onTouchImage(View v) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
	}
	
	private class EndpointLoader extends AsyncTask<JSONObject, Void, Boolean> {
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(MainActivity.this, getString(R.string.dialog_loading_services), "", true);
		}
		
		@Override
		protected Boolean doInBackground(JSONObject... server) {
			return Open311.setEndpoint(server[0]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (!result) {
				Util.displayCrashDialog(MainActivity.this, getString(R.string.failure_loading_services));
			}
			super.onPostExecute(result);
		}
	}
}
