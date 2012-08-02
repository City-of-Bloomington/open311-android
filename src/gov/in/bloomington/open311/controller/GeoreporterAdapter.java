/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.open311.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

/*
 * business (controller) class to manage adapter for dynamic listview in open311-android
 */
public class GeoreporterAdapter extends BaseAdapter {

	private final transient Activity activity;
    private final transient JSONArray data;
    private final transient String type;
    private final transient LayoutInflater inflater;

    public GeoreporterAdapter(final Activity act, final JSONArray jsonData, final String adapterType) {
        super();
    	activity = act;
        data=jsonData;
        type = adapterType;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public int getCount() {
		int count;
		// TODO Auto-generated method stub
		if (data == null) {
			count = 0;
		}
		else  {
			count = data.length();
		}
		return count;
	}

	public Object getItem(final int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(final int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public static class ViewHolder{
		public TextView txtReportService;
        public TextView txtDateServer;
        public TextView txtCityState;
        public TextView txtUrl;
        public RadioButton rbServer;
    }

	/** Return appropriate view adapter. There are two kind of view here: 'report' and 'server'*/
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		View georeporterView=convertView;
        ViewHolder holder;
        if ("report".equals(type)) {
        	if(convertView==null){
        		georeporterView = inflater.inflate(R.layout.my_report_item, null);
        		
        		holder=new ViewHolder();
        		holder.txtReportService=(TextView)georeporterView.findViewById(R.id.txt_report_service);
	            holder.txtDateServer=(TextView)georeporterView.findViewById(R.id.txt_date_server);
	            georeporterView.setTag(holder);
        	}
        	else {
        		holder=(ViewHolder)georeporterView.getTag();
        	}
        	
        	String report_service = null;
			String date_server = null;
    		try {
    			report_service = data.getJSONObject(position).getString("report_service");
    			date_server = data.getJSONObject(position).getString("date_time")+": "+data.getJSONObject(position).getString("server_name");
    			
        	} catch (JSONException e) {
				// TODO Auto-generated catch block
        		Log.e("GeoreporterAdapter getView server", e.toString());
			}
    		holder.txtReportService.setText(report_service);
        	holder.txtDateServer.setText(date_server);
        	
        }
        
        else if ("server".equals(type)) {
        	if(convertView==null){
        		georeporterView = inflater.inflate(R.layout.server_item, null);
        		
        		holder=new ViewHolder();
        		holder.txtCityState=(TextView)georeporterView.findViewById(R.id.txt_city_state);
	            holder.txtUrl=(TextView)georeporterView.findViewById(R.id.txt_url);
	            holder.rbServer = (RadioButton) georeporterView.findViewById(R.id.rb);
	            georeporterView.setTag(holder);
        	}
        	else {
        		holder=(ViewHolder)georeporterView.getTag();
        	}
        	String city_state = null;
    		String url = null;
			try {
				city_state = data.getJSONObject(position).getString("name");
				url = data.getJSONObject(position).getString("url");
				final SharedPreferences pref = activity.getSharedPreferences("server",0);
				
				final JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
				final String server_name = server.getString("name");
				
				if (city_state.equals(server_name)) {
					holder.rbServer.setChecked(true);
				}
				else {
					holder.rbServer.setChecked(false);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("GeoreporterAdapter getView report", e.toString());
			}
    		
    	
    		holder.txtCityState.setText(city_state);
    		holder.txtUrl.setText(url);
        	
        }
        return georeporterView;
	}
	

}

