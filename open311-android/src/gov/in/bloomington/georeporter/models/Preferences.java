/**
 * A static helper class for interacting with SharedPreferences
 * 
 * This class should handle all interactions with SharedPreferences
 *
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

import gov.in.bloomington.cityreporter.R;
import gov.in.bloomington.georeporter.util.Util;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	private static final String SETTINGS       = "settings";
	private static final String PERSONAL_INFO  = "personal_info";
	
	private static SharedPreferences mSettings = null;
	
	private static void loadSettings(Context c) {
		if (mSettings == null) {
			mSettings = c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		}
	}
	
	/**
	 * Returns the personal_info fields stored in settings
	 * 
	 * This should always return a valid JSONObject.
	 * The JSONObject may be empty, but it needs to be 
	 * ready for the user to start filling out the fields.
	 * 
	 * @param c
	 * @return
	 * JSONObject
	 */
	public static JSONObject getPersonalInfo(Context c) {
		Preferences.loadSettings(c);
		try {
			return new JSONObject(mSettings.getString(PERSONAL_INFO, "{}"));
		} catch (JSONException e) {
			return null;
		}
	}
	
    /**
     * Writes the personal info fields to disk
     * 
     * @param personal_info
     * @param c
     * void
     */
	public static void setPersonalInfo(JSONObject personal_info, Context c) {
        Preferences.loadSettings(c);
        
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(PERSONAL_INFO, personal_info.toString());
        editor.commit();
    }
    
    /**
     * Returns the current_server stored in app_state
     *
     * Upstream Compatibility Notice:
     * This version is forked from GeoReporter, which is a multi server app.
     * In order to maintain compatibility with upstream changes.
     *
     * This method must remain to minimize changes from upstream.
     * It is modified here to always return the first entry in available_servers
     *
     * @param context
     * @return JSONObject
     */
    public static JSONObject getCurrentServer(Context context) {
        try {
            JSONArray available_servers = new JSONArray(Util.file_get_contents(context, R.raw.available_servers));
            return available_servers.getJSONObject(0);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
	
    /**
     * Saves the name of the current server back into Preferences.app_state
     *
     * Upstream Compatibility Notice:
     * This version is forked from GeoReporter, which is a multi server app.
     * In order to maintain compatibility with upstream changes.
     *
     * This method must remain to minimize changes from upstream.
     * 
     * @param server
     * @param c
     * void
     */
    public static void setCurrentServer(JSONObject server, Context c) {
        // NOOP
    }
}
