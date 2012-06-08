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
import gov.in.bloomington.open311.controller.ServerItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	protected void onResume (){
		super.onResume();
		list_services=(ListView)findViewById(R.id.list);

        servers = ServerItem.retreiveServers(MyServers.this);
        
		adapter = new GeoreporterAdapter(MyServers.this, servers, "server");
		list_services.setAdapter(adapter);
		list_services.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		list_services.setOnItemClickListener(new OnItemClickListener()
		{

		public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long id) {
			// TODO Auto-generated method stub
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MyServers.this);
			try {
				builder.setMessage("Report to "+servers.getJSONObject(position).getString("name").toString() +" ?")
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   
				        	   String selected_server_name = null, 
				   					selected_server_url = null,
				   					selected_server_jurisdiction_id = null, 
				   					selected_server_api_key = null;
				   			boolean selected_server_supports_media = false; 
				   			
				   			SharedPreferences pref = getSharedPreferences("server",0);
				   			SharedPreferences.Editor editor = pref.edit();
				   			
				   			try {
				   				selected_server_name = servers.getJSONObject(position).getString("name").toString();
				                   editor.putString("server_name", selected_server_name);
				                   
				   				selected_server_url = servers.getJSONObject(position).getString("url").toString();
				   				editor.putString("server_url", selected_server_url);
				   				
				   				selected_server_supports_media = servers.getJSONObject(position).getBoolean("supports_media");
				   				editor.putBoolean("server_supports_media", selected_server_supports_media);
				   				
				   				selected_server_jurisdiction_id = servers.getJSONObject(position).getString("jurisdiction_id").toString();
				   				editor.putString("server_jurisdiction_id", selected_server_jurisdiction_id);
				   				
				   				selected_server_api_key = servers.getJSONObject(position).getString("api_key").toString();
				   				editor.putString("server_api_key", selected_server_api_key);
				   				
				                   editor.commit();

				   				
				   			} catch (JSONException e) {
				   				// TODO Auto-generated catch block
				   				e.printStackTrace();
				   			}
				        	   
				        	   //switch to home screen
				        	   switchTabInActivity(0);
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				    	    	dialog.cancel();
				           }
				       });
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AlertDialog alert = builder.create();
			alert.show();

		}
		});
    }
    
    public void switchTabInActivity(int indexTabToSwitchTo){
        Main ParentActivity;
        ParentActivity = (Main) this.getParent();
        ParentActivity.switchTab(indexTabToSwitchTo);
    }
    
    
	
}
