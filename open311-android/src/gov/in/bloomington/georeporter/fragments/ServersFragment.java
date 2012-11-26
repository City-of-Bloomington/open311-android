/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.MainActivity;
import gov.in.bloomington.georeporter.adapters.ServersAdapter;
import gov.in.bloomington.georeporter.models.Preferences;
import gov.in.bloomington.georeporter.util.Util;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ServersFragment extends SherlockListFragment {
	JSONArray mServers = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			mServers = new JSONArray(Util.file_get_contents(getActivity(), R.raw.available_servers));
		} catch (JSONException e) {
			Util.displayCrashDialog(getActivity(), "Could not load endpoints from json");
		}
		setListAdapter(new ServersAdapter(mServers, getActivity()));
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		JSONObject current_server = null;
		try {
			current_server = mServers.getJSONObject(position);
		} catch (JSONException e) {
			// We'll just pass null to Preferences, which will wipe current_server
			// Once they get sent to Home, home will realize there isn't 
			// a current_server and send them back here
		}
		Preferences.setCurrentServer(current_server, getActivity());
		
		Intent i = new Intent(getActivity(), MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
}
