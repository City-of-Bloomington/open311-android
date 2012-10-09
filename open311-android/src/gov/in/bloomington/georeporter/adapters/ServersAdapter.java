/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class ServersAdapter extends BaseAdapter {
	private JSONArray mServers;
	private static LayoutInflater mInflater;
	private String mCurrentServerName;
	
	public ServersAdapter(JSONArray d, Context c) {
		mServers  = d;
		mInflater = LayoutInflater.from(c);
		
		JSONObject currentServer = Preferences.getCurrentServer(c);
		mCurrentServerName = currentServer==null ? "" : currentServer.optString("name");
	}

	@Override
	public int getCount() {
		return (mServers == null) ? 0 : mServers.length();
	}

	@Override
	public JSONObject getItem(int position) {
		return mServers.optJSONObject(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ViewHolder {
		public TextView name, url;
		public RadioButton radio;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_servers, null);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(android.R.id.text1);
			holder.url  = (TextView)convertView.findViewById(android.R.id.text2);
			holder.radio = (RadioButton)convertView.findViewById(R.id.radio);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String name = mServers.optJSONObject(position).optString("name");
		String url  = mServers.optJSONObject(position).optString("url");
		if (name.equals(mCurrentServerName)) {
			holder.radio.setChecked(true);
		}
		else {
			holder.radio.setChecked(false);
		}
		holder.name.setText(name);
		holder.url .setText(url);
		return convertView;
	}
}