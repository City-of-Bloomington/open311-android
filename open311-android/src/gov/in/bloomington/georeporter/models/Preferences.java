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

import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	private static final String SETTINGS       = "settings";
	private static final String PERSONAL_INFO  = "personal_info";
	
	private static final String APP_STATE      = "app_state";
	private static final String CURRENT_SERVER = "current_server";
	
	private static SharedPreferences mSettings = null;
	private static SharedPreferences mState    = null;
	
	private static void loadSettings(Context c) {
		if (mSettings == null) {
			mSettings = c.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		}
	}
	
	private static void loadState(Context c) {
		if (mState == null) {
			mState = c.getSharedPreferences(APP_STATE, Context.MODE_PRIVATE);
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
	 * Returns the current_server stored in app_state
	 * 
	 * This may return null, meaning there is no current_server chosen.
	 * 
	 * @param c
	 * @return
	 * JSONObject
	 */
	public static JSONObject getCurrentServer(Context c) {
		Preferences.loadState(c);
		try {
			return new JSONObject(mState.getString(CURRENT_SERVER, ""));
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static void setPersonalInfo(JSONObject personal_info, Context c) {
		Preferences.loadSettings(c);
		
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putString(PERSONAL_INFO, personal_info.toString());
		editor.commit();
	}
	
	/**
	 * Saves current_server back into Preferences.app_state
	 * 
	 * Passing null for the server will unset the current_server
	 * 
	 * @param server
	 * @param c
	 * void
	 */
	public static void setCurrentServer(JSONObject server, Context c) {
		Preferences.loadState(c);
		
		SharedPreferences.Editor editor = mState.edit();
		if (server != null) {
			editor.putString(CURRENT_SERVER, server.toString());
		}
		else {
			editor.remove(CURRENT_SERVER);
		}
		editor.commit();
	}
}
