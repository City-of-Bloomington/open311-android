/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SavedReportsAdapter extends BaseAdapter {
	private JSONArray mServiceRequests;
	private static LayoutInflater mInflater;
	
	public SavedReportsAdapter(JSONArray serviceRequests, Context c) {
		mServiceRequests = serviceRequests;
		mInflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return (mServiceRequests == null) ? 0 : mServiceRequests.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return mServiceRequests.optJSONObject(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ViewHolder {
		TextView name, description;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		JSONObject report = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_2, null);
			holder = new ViewHolder();
			holder.name        = (TextView)convertView.findViewById(android.R.id.text1);
			holder.description = (TextView)convertView.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		//TODO display some information from the report
		// For right now, just display some generic stuff
		JSONObject endpoint, service;
		try {
			endpoint = report.getJSONObject(ServiceRequest.ENDPOINT);
			service  = report.getJSONObject(ServiceRequest.SERVICE);
			holder.name       .setText(endpoint.getString(Open311.NAME));
			holder.description.setText(service .getString(Open311.SERVICE_NAME));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	/**
	 * 
	 * @param serviceRequests
	 * void
	 */
	public void updateSavedReports(JSONArray serviceRequests) {
	    mServiceRequests = serviceRequests;
	    super.notifyDataSetChanged();
	}
}
