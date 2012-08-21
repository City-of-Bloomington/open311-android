/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
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

/*
 * presentation (view) class to display and perform function regarding server information
 */
public class MyServers extends Activity {
	private JSONArray servers;
	private ListView listServices;
	private GeoreporterAdapter adapter;
	Intent intent;
	private ExternalFileAdapter extFileAdapt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_servers);
//		Log.d("MyServers", "MyServers 1");
	}

	/** Called everytime MyServers is the focused tab or display is resumed */
	@Override
	protected void onResume() {
		super.onResume();
		listServices=(ListView)findViewById(R.id.list);

		extFileAdapt = new ExternalFileAdapter();
		servers = extFileAdapt.readJSONRaw(MyServers.this, R.raw.available_servers);
//		Log.d("MyServers", "MyServers 2"+servers.toString());
		adapter = new GeoreporterAdapter(MyServers.this, servers, "server");
//		Log.d("MyServers", "MyServers 3");
		listServices.setAdapter(adapter);
		listServices.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listServices.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
				SharedPreferences pref = getSharedPreferences("server",0);
				SharedPreferences.Editor editor = pref.edit();
				try {
					editor.putString("selectedServer", servers.getString(position));
//					Log.d("MyServers", "servers: "+servers.getString(position));
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

	/** display another desired tab */
	public void switchTabInActivity(int indexTabToSwitchTo){
		Main ParentActivity;
		ParentActivity = (Main) this.getParent();
		ParentActivity.switchTab(indexTabToSwitchTo);
	}
}
