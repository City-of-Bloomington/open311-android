/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

/*
 * Business (Controller) class which act as an interface for interacting with service item
 */
public class ServicesItem {
	
	/** Return service group from JSON Array of services */
	public  CharSequence[] getGroup (final JSONArray ja_services) {
		//assumption: servers order by group
		final List<String> group_list = new ArrayList<String>();
//		final String grp = "group";
		 
		int n_group = 1;
		try {
			group_list.add(ja_services.getJSONObject(0).getString("group"));
			for (int i=1; i<ja_services.length();i++) {
				if (!ja_services.getJSONObject(i).getString("group").equals(group_list.get(n_group-1))) {
					n_group++;
					group_list.add(ja_services.getJSONObject(i).getString("group"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("ServiceItem getGroup", e.toString());
		}
		return group_list.toArray(new CharSequence[group_list.size()]);
		
	}
	
	/** Return service in a particular group */
	public  CharSequence[] getServicesByGroup(final JSONArray ja_services, final CharSequence group) {
		//assumption: servers order by group
		final List<String> services_list = new ArrayList<String>();
		boolean is_started = false; //whether it started to find the corresponding group
		boolean equals = false; //whether current group is equal with the one corresponding group
		int iterator = 0;
		do {
			try {
				if (ja_services.getJSONObject(iterator).getString("group").equals(group)) {
					is_started = true;
					services_list.add(ja_services.getJSONObject(iterator).getString("service_name"));
					equals = true;
				}
				else {
					equals = false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("ServiceItem getServicesByGroup", e.toString());
			}
			iterator++;
		} while (iterator<ja_services.length() && !(is_started && !equals));
		
		return services_list.toArray(new CharSequence[services_list.size()]);
	}
	
	/** Check wheter a service has attribute*/
	public  boolean hasAttribute (final JSONArray ja_services, final String service_code) {
		boolean has_atribute = false;
		boolean equals = false; //whether current service is the one we looking for
		int iterator = 0;
		do {
			try {
				if (ja_services.getJSONObject(iterator).getString("service_code").equals(service_code)) {
					equals = true;
					has_atribute = ja_services.getJSONObject(iterator).getBoolean("metadata");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("ServiceItem hasAttribute", e.toString());
			}
			iterator++;
		} while (iterator<ja_services.length() && !equals);
		return has_atribute;
	}
	
	/** Return service code from a service which its name is known */
	public  String getServiceCode (final JSONArray ja_services, final CharSequence service) {
		String service_code = null;
		boolean equals = false; //whether current service is the one we looking for
		int iterator = 0;
		do {
			try {
				if (ja_services.getJSONObject(iterator).getString("service_name").equals(service)) {
					equals = true;
					service_code = ja_services.getJSONObject(iterator).getString("service_code");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("ServiceItem getSeviceCode", e.toString());
			}
			iterator++;
		} while (iterator<ja_services.length() && !equals);
		return service_code;
	}
	
	/** Return service code from a service which its description is known */
	public  String getServiceDescription (final JSONArray ja_services, final CharSequence service) {
		String service_code = null;
		boolean equals = false; //whether current service is the one we looking for
		int iterator = 0;
		do {
			try {
				if (ja_services.getJSONObject(iterator).getString("service_name").equals(service)) {
					equals = true;
					service_code = ja_services.getJSONObject(iterator).getString("description");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("ServiceItem getSeviceDescription", e.toString());
			}
			iterator++;
		} while (iterator<ja_services.length() && !equals);
		return service_code;
	}
	
	
}
