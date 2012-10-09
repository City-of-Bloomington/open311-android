/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import gov.in.bloomington.georeporter.models.Open311;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupsAdapter extends BaseAdapter {
	private static LayoutInflater mInflater;
	
	public GroupsAdapter(Context c) {
		mInflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return (Open311.sGroups == null) ? 0 : Open311.sGroups.size();
	}

	@Override
	public String getItem(int position) {
		return Open311.sGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ViewHolder {
		public TextView name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(getItem(position));
		return convertView;
	}

}
