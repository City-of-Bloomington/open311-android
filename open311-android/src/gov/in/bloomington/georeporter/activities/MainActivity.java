/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.Preferences;
import gov.in.bloomington.georeporter.util.Util;

public class MainActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		JSONObject current_server = Preferences.getCurrentServer(this);
		//Log.i("current_server", current_server.toString());
		if (current_server == null) {
			Log.i("MainActivity onResume", "null");
    		startActivity(new Intent(this, SettingsActivity.class));
		}
		else {
			new EndpointLoader().execute(current_server);
		}
	}
	
	private class EndpointLoader extends AsyncTask<JSONObject, Void, Boolean> {
		ProgressDialog dialog;
		
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
			startActivity(new Intent(MainActivity.this, ReportActivity.class));
		}
	}
}
