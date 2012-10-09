/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonalInfoAdapter extends BaseAdapter {
	private Resources         mResources;
	private String            mPackageName;
	private JSONObject        mPersonalInfo;
	private LayoutInflater    mInflater;
	
	public static final String[] FIELDS = { "first_name", "last_name", "email", "phone" };
	
	public PersonalInfoAdapter(JSONObject d, Context c) {
		mPersonalInfo = d;
		mInflater = LayoutInflater.from(c);
		
		mResources   = c.getResources();
		mPackageName = c.getPackageName();
	}

	@Override
	public int getCount() {
		return FIELDS.length;
	}

	@Override
	public Object getItem(int position) {
		return mPersonalInfo.opt(FIELDS[position]);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ViewHolder {
		public TextView label;
		public TextView input;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_2, null);
			holder = new ViewHolder();
			holder.label = (TextView)convertView.findViewById(android.R.id.text1);
			holder.input = (TextView)convertView.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String fieldname = FIELDS[position];
		holder.label.setText(mResources.getIdentifier(fieldname, "string", mPackageName));
		holder.input.setText(mPersonalInfo.optString(fieldname));
		return convertView;
	}
}
