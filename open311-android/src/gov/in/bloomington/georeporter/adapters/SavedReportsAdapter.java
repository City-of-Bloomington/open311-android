/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SavedReportsAdapter extends BaseAdapter {
	private JSONArray mServiceRequests;
	private static LayoutInflater mInflater;
	
	private DateFormat       mDateFormat;
	private SimpleDateFormat mISODate;
	
	@SuppressLint("SimpleDateFormat")
    public SavedReportsAdapter(JSONArray serviceRequests, Context c) {
		mServiceRequests = serviceRequests;
		mInflater = LayoutInflater.from(c);
		mDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
		mISODate = new SimpleDateFormat(Open311.DATETIME_FORMAT);
	}

	@Override
	public int getCount() {
		return (mServiceRequests == null) ? 0 : mServiceRequests.length();
	}

	@Override
	public ServiceRequest getItem(int position) {
		JSONObject o = mServiceRequests.optJSONObject(position);
		return new ServiceRequest(o.toString());
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private static class ViewHolder {
		TextView serviceName, status, date, address, endpoint;
		ImageView media;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_saved_reports, null);
			holder = new ViewHolder();
			holder.serviceName = (TextView) convertView.findViewById(R.id.service_name);
			holder.status      = (TextView) convertView.findViewById(R.id.status);
			holder.date        = (TextView) convertView.findViewById(R.id.date);
			holder.address     = (TextView) convertView.findViewById(R.id.address);
            holder.endpoint    = (TextView) convertView.findViewById(R.id.endpoint);
			holder.media       = (ImageView)convertView.findViewById(R.id.media);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
        ServiceRequest sr = getItem(position);
		try {
			holder.serviceName.setText(sr.service        .getString(Open311.SERVICE_NAME));
			holder.endpoint   .setText(sr.endpoint       .getString(Open311.NAME));
			holder.address    .setText(sr.post_data      .optString(Open311.ADDRESS_STRING));
			holder.status     .setText(sr.service_request.optString(ServiceRequest.STATUS));
            holder.date       .setText(mDateFormat.format(mISODate.parse(sr.post_data.optString(ServiceRequest.REQUESTED_DATETIME))));
            holder.media.setImageBitmap(sr.getMediaBitmap(80, 80, mInflater.getContext()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
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
