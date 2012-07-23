/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import org.json.JSONArray;
import org.json.JSONException;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAdapter;
import gov.in.bloomington.open311.model.ExternalFileAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyServers extends Activity {
	private JSONArray servers;
	private ListView list_services;
	private GeoreporterAdapter adapter;
	Intent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_servers);
	}

	@Override
	protected void onResume() {
		super.onResume();
		list_services=(ListView)findViewById(R.id.list);

		servers = ExternalFileAdapter.readJSONRaw(MyServers.this, R.raw.available_servers);

		adapter = new GeoreporterAdapter(MyServers.this, servers, "server");
		list_services.setAdapter(adapter);
		list_services.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		list_services.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
				SharedPreferences pref = getSharedPreferences("server",0);
				SharedPreferences.Editor editor = pref.edit();
				try {
					editor.putString("selectedServer", servers.getString(position));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				editor.putBoolean("justChanged", true);
				editor.commit();

				//switch to home screen
				switchTabInActivity(0);
			}
		});
	}

	public void switchTabInActivity(int indexTabToSwitchTo){
		Main ParentActivity;
		ParentActivity = (Main) this.getParent();
		ParentActivity.switchTab(indexTabToSwitchTo);
	}
}
