/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.fragments.PersonalInfoFragment;
import gov.in.bloomington.georeporter.fragments.ServersFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockListFragment;

public class SettingsActivity extends BaseFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(R.string.menu_settings);
		
		Tab tab;
		tab = actionBar.newTab()
				.setText(R.string.tab_servers)
				.setTabListener(new TabListener<ServersFragment>(this, "servers", ServersFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab()
				.setText(R.string.tab_personal_info)
				.setTabListener(new TabListener<PersonalInfoFragment>(this, "personalInfo", PersonalInfoFragment.class));
		actionBar.addTab(tab);
	}
	
	/**
	 * Copy of implementation from Android developer docs
	 * 
	 * http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
	 * This version has been slightly modified to work with Sherlock
	 */
	public static class TabListener<T extends SherlockListFragment> implements ActionBar.TabListener {
	    private SherlockListFragment mFragment;
	    private final SettingsActivity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    /** Constructor used each time a new tab is created.
	      * @param settingsActivity  The host Activity, used to instantiate the fragment
	      * @param tag  The identifier tag for the fragment
	      * @param clz  The fragment's Class, used to instantiate the fragment
	      */
	    public TabListener(SettingsActivity settingsActivity, String tag, Class<T> clz) {
	        mActivity = settingsActivity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */

	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = (SherlockListFragment) SherlockListFragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}}
