/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import org.json.JSONObject;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.fragments.ChooseGroupFragment;
import gov.in.bloomington.georeporter.fragments.ChooseServiceFragment;
import gov.in.bloomington.georeporter.fragments.ReportFragment;
import gov.in.bloomington.georeporter.fragments.ChooseGroupFragment.OnGroupSelectedListener;
import gov.in.bloomington.georeporter.fragments.ChooseServiceFragment.OnServiceSelectedListener;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;

public class ReportActivity extends BaseFragmentActivity
							implements OnGroupSelectedListener,
									   OnServiceSelectedListener {
	
	public static final int CHOOSE_LOCATION_REQUEST = 1;
	private ActionBar      mActionBar;
	private ReportFragment mReportFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActionBar = getSupportActionBar();
		mActionBar.setTitle(R.string.menu_report);
		
		ChooseGroupFragment chooseGroup = new ChooseGroupFragment();
		getSupportFragmentManager() .beginTransaction()
									.add(android.R.id.content, chooseGroup)
									.addToBackStack(null)
									.commit();
	}
	
	@Override
	public void onGroupSelected(String group) {
		ChooseServiceFragment chooseService = new ChooseServiceFragment();
		chooseService.setServices(Open311.getServices(group));
		getSupportFragmentManager() .beginTransaction()
									.replace(android.R.id.content, chooseService)
									.addToBackStack(null)
									.commit();
	}
	
	@Override
	public void onServiceSelected(JSONObject service) {
		mActionBar.setTitle(service.optString(Open311.SERVICE_NAME));
		
		ServiceRequest sr = new ServiceRequest(service);
		mReportFragment = ReportFragment.newInstance(sr);
		
		getSupportFragmentManager() .beginTransaction()
									.replace(android.R.id.content, mReportFragment)
									.addToBackStack(null)
									.commit();
	}
}
