/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.fragments.ChooseGroupFragment;
import gov.in.bloomington.georeporter.fragments.ChooseGroupFragment.OnGroupSelectedListener;
import gov.in.bloomington.georeporter.fragments.ChooseServiceFragment;
import gov.in.bloomington.georeporter.fragments.ChooseServiceFragment.OnServiceSelectedListener;
import gov.in.bloomington.georeporter.fragments.ReportFragment;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.Util;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class ReportActivity extends BaseActivity
							implements OnGroupSelectedListener,
									   OnServiceSelectedListener {
	
	public static final int CHOOSE_LOCATION_REQUEST = 1;
	private ActionBar      mActionBar;
	private ReportFragment mReportFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActionBar = getActionBar();
		mActionBar.setTitle(R.string.menu_report);
		
		if (Open311.sReady) {
	        if (Open311.sGroups.size() > 1) {
	            ChooseGroupFragment chooseGroup = new ChooseGroupFragment();
	            getFragmentManager() .beginTransaction()
	                                 .add(android.R.id.content, chooseGroup)
	                                 .addToBackStack(null)
	                                 .commit();
	        }
	        else {
	            onGroupSelected(Open311.sGroups.get(0));
	        }
		}
		else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
		}
	}
	
	@Override
	public void onGroupSelected(String group) {
		ChooseServiceFragment chooseService = new ChooseServiceFragment();
		chooseService.setServices(Open311.getServices(group, this));
		getFragmentManager() .beginTransaction()
							 .replace(android.R.id.content, chooseService)
							 .addToBackStack(null)
							 .commit();
	}
	
	@Override
	public void onServiceSelected(JSONObject service) {
		mActionBar.setTitle(service.optString(Open311.SERVICE_NAME));
		new ServiceDefinitionLoader(this).execute(service);
	}
	
	private class ServiceDefinitionLoader extends AsyncTask<JSONObject, Void, Boolean> {
		private ProgressDialog dialog;
		private JSONObject service;
		private JSONObject service_definition;
		private Context context;
		
		private ServiceDefinitionLoader(Context c) {
			this.context = c;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(context, getString(R.string.dialog_loading_services), "", true);
		}
		
		@Override
		protected Boolean doInBackground(JSONObject... s) {
			service = s[0];
			if (service.optBoolean(Open311.METADATA)) {
				try {
					service_definition = Open311.getServiceDefinition(service.getString(Open311.SERVICE_CODE), context);
					return true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			else {
				return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (!result) {
				Util.displayCrashDialog(ReportActivity.this, getString(R.string.failure_loading_services));
			}
			else {
				ServiceRequest sr = new ServiceRequest(service, service_definition, context);
				mReportFragment = ReportFragment.newInstance(sr);
				
				getFragmentManager().beginTransaction()
									.replace(android.R.id.content, mReportFragment)
									.addToBackStack(null)
									.commit();
			}
		}
	}
}
