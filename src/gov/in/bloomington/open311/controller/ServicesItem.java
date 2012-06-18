package gov.in.bloomington.open311.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;



public class ServicesItem {
	public static CharSequence[] getGroup (JSONArray ja_services) {
		//assumption: servers order by group
		List<String> group_list = new ArrayList<String>();
		
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
			e.printStackTrace();
		}
		final CharSequence[] group = group_list.toArray(new CharSequence[group_list.size()]);
		return group;
		
	}
	
	public static CharSequence[] getServicesByGroup(JSONArray ja_services, CharSequence group) {
		//assumption: servers order by group
		List<String> services_list = new ArrayList<String>();
		boolean is_started = false; //whether it started to find the corresponding group
		boolean equals = false; //whether current group is equal with the one corresponding group
		int i = 0;
		do {
			try {
				if (ja_services.getJSONObject(i).getString("group").equals(group)) {
					is_started = true;
					services_list.add(ja_services.getJSONObject(i).getString("service_name"));
					equals = true;
				}
				else {
					equals = false;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		} while (i<ja_services.length() && !(is_started && !equals));
		
		final CharSequence[] services = services_list.toArray(new CharSequence[services_list.size()]);
		return services;
	}
	
	public static boolean hasAttribute (JSONArray ja_services, CharSequence service) {
		boolean has_atribute = false;
		boolean equals = false; //whether current service is the one we looking for
		int i = 0;
		do {
			try {
				if (ja_services.getJSONObject(i).getString("service_name").equals(service)) {
					equals = true;
					has_atribute = ja_services.getJSONObject(i).getBoolean("metadata");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		} while (i<ja_services.length() && !equals);
		return has_atribute;
	}
	
	public static String getServiceCode (JSONArray ja_services, CharSequence service) {
		String service_code = null;
		boolean equals = false; //whether current service is the one we looking for
		int i = 0;
		do {
			try {
				if (ja_services.getJSONObject(i).getString("service_name").equals(service)) {
					equals = true;
					service_code = ja_services.getJSONObject(i).getString("service_code");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		} while (i<ja_services.length() && !equals);
		return service_code;
	}
	
	
}
