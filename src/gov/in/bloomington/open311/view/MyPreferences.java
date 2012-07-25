/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */

package gov.in.bloomington.open311.view;

import gov.in.bloomington.open311.R;

import java.util.Map;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/*
 * presentation (view) class to display and perform function regarding preferences (user data)
 */
public class MyPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Map<String, ?> map = preferences.getAll();
		for (Map.Entry<String, ?> entry:map.entrySet()) {
			String key = entry.getKey();
			getPreferenceScreen().findPreference(key).setSummary(preferences.getString(key, ""));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences p, String key) {
		getPreferenceScreen().findPreference(key).setSummary(p.getString(key, ""));
	}
}
