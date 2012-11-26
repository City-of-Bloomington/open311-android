/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import gov.in.bloomington.georeporter.models.Open311;

import java.util.ArrayList;

import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServicesAdapter extends BaseAdapter {
	private static LayoutInflater mInflater;
	private static ArrayList<JSONObject> mServices;
	
	public ServicesAdapter(ArrayList<JSONObject> services, Context c) {
		mServices = services;
		mInflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return mServices.size();
	}

	@Override
	public JSONObject getItem(int position) {
		return mServices.get(position);
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
		JSONObject service = getItem(position);
		
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
		holder.name       .setText(service.optString(Open311.SERVICE_NAME));
		holder.description.setText(service.optString(Open311.DESCRIPTION));
		return convertView;
	}

}
