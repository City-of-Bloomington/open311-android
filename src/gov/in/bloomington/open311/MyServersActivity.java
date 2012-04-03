/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MyServersActivity extends Activity {
	private JSONArray availableServers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_servers);
		
		try {
			InputStream inputStream = getResources().openRawResource(R.raw.available_servers);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder = new StringBuilder();
			String line = buffer.readLine();
			while (line != null) {
				builder.append(line);
				line = buffer.readLine();
			}
			try {
				availableServers = new JSONArray(builder.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			
		}
		
	}
	
	/**
	 * Open a dialog for the user to enter a custom Open311 endpoint
	 * 
	 * @param v
	 */
	public void openCustomServerDialog(View v) {
		
	}
	
	/**
	 * Open the list of available servers and let the user choose one
	 * 
	 * @param v
	 */
	public void openAvailableServersDialog(View v) {
		final String[] serverNames = new String[availableServers.length()];
		for (int i=0; i<availableServers.length(); i++) {
			JSONObject server;
			try {
				server = availableServers.getJSONObject(i);
				serverNames[i] = server.getString("name");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Available Servers")
			   .setCancelable(true)
			   .setItems(serverNames, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Add the selected server to MyServers
						Toast.makeText(getApplicationContext(), serverNames[which], Toast.LENGTH_SHORT).show();
					}
				})
			   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
			   });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
