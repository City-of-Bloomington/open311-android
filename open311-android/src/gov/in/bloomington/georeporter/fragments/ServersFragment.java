/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import java.util.Arrays;
import java.util.List;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.MainActivity;
import gov.in.bloomington.georeporter.adapters.ServersAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.Preferences;
import gov.in.bloomington.georeporter.util.Util;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ServersFragment extends Fragment implements OnItemClickListener, OnCreateContextMenuListener {
    private int       mNumAvailableServers = 0;
    private JSONArray mCustomServers       = null;
    private ListView  mListView;
    
    private static final List<String> FORMAT_CHOICES = Arrays.asList(Open311.JSON, Open311.XML);
    
    /**
     * Available and Custom servers combined into one array
     */
	private JSONArray mServers = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.fragment_servers, container, false);
	    
	    mListView = (ListView) v.findViewById(R.id.serversListView);
	    mListView.setOnItemClickListener(this);
	    mListView.setOnCreateContextMenuListener(this);
	    v.findViewById(R.id.addServerButton).setOnClickListener(new OnClickListener() {
	    	LayoutInflater inflater = getActivity().getLayoutInflater();
	        final View dialogLayout = inflater.inflate(R.layout.dialog_add_server, null);
	        
            @Override
            public void onClick(View v) {
                // Remove the previous instance of the dialog from the parent view
				if (dialogLayout.getParent() != null) {
					((ViewGroup) dialogLayout.getParent()).removeView(dialogLayout);
				}
				
                final Spinner format = (Spinner) dialogLayout.findViewById(R.id.format);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, FORMAT_CHOICES);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                format.setAdapter(adapter);
                
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogLayout)
                       .setTitle(R.string.button_add_server)
                       .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveNewServer(dialogLayout); // Update the Preferences data
                                refresh();                   // Redraw the screen from Preferences
                                dialog.dismiss();            // Close the dialog
                            }
                       })
                       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                       });
                builder.show();
            }
        });
	    refresh();
	    return v;
	}
	
	/**
	 * Adds a new server JSONObject to Preferences
	 * 
	 * This only updates the Preferences data.  You still need make sure
	 * that the screen actually reflects this change.
	 * 
	 * @param dialog
	 */
	private void saveNewServer(View dialog) {
        final EditText     name           = (EditText)     dialog.findViewById(R.id.name);
        final EditText     url            = (EditText)     dialog.findViewById(R.id.url);
        final EditText     jurisdiction   = (EditText)     dialog.findViewById(R.id.jurisdiction);
        final EditText     api_key        = (EditText)     dialog.findViewById(R.id.api_key);
        final ToggleButton supports_media = (ToggleButton) dialog.findViewById(R.id.supports_media);
        final Spinner      format         = (Spinner)      dialog.findViewById(R.id.format);
        
        JSONObject server = new JSONObject();
        try {
            server.put(Open311.NAME,           name        .getText().toString());
            server.put(Open311.URL,            url         .getText().toString());
            server.put(Open311.JURISDICTION,   jurisdiction.getText().toString());
            server.put(Open311.API_KEY,        api_key     .getText().toString());
            server.put(Open311.SUPPORTS_MEDIA, supports_media.isChecked());
            server.put(Open311.FORMAT,         format.getSelectedItem());
            
            mCustomServers.put(server);
            Preferences.setCustomServers(mCustomServers, getActivity());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Reloads server data and updates the ListView
	 * 
	 * Reads servers from both available_servers and Preferences.custom_servers.
	 * The list will have available_servers first, the custom_servers.
	 * Remember that the custom_servers are editable, while the available_servers are not.
	 */
	private void refresh() {
        try {
            mServers = new JSONArray(Util.file_get_contents(getActivity(), R.raw.available_servers));
            mNumAvailableServers = mServers.length();
            
            mCustomServers = Preferences.getCustomServers(getActivity());
            int len = mCustomServers.length();
            for (int i=0; i<len; i++) {
                mServers.put(mCustomServers.getJSONObject(i));
            }
        } catch (JSONException e) {
            Util.displayCrashDialog(getActivity(), "Could not load endpoints from json");
        }
        mListView.setAdapter(new ServersAdapter(mServers, getActivity()));
	}
	
	/**
	 * Sets the current server
	 * 
	 * When the user touches one of the servers, set that
	 * one to be the current_server and send them to MainActivity
	 */
	@Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    
	    // Only show the delete menu for the custom servers.
	    // Servers from available_servers cannot be deleted.
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    if (info.position >= mNumAvailableServers) {
	        MenuInflater inflater = getActivity().getMenuInflater();
	        inflater.inflate(R.menu.context_listitem, menu);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        switch (item.getItemId()) {
            case R.id.menu_delete:
                int position = info.position - mNumAvailableServers;
                mCustomServers.remove(position);
                Preferences.setCustomServers(mCustomServers, getActivity());
                refresh();
                return true;
                
            default:
                return super.onContextItemSelected(item);
        }
	}
}
