package gov.in.bloomington.open311.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.open311.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class GeoreporterAdapter extends BaseAdapter {

	private Activity activity;
    private JSONArray data;
    private String type;
    private static LayoutInflater inflater=null;

	
    public GeoreporterAdapter(Activity a, JSONArray d, String t) {
        activity = a;
        data=d;
        type = t;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public int getCount() {
		// TODO Auto-generated method stub
			if (data == null)
				return 0;
			else 
				return data.length();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public static class ViewHolder{
		public TextView txt_report_service;
        public TextView txt_date_server;
        public TextView txt_city_state;
        public TextView txt_url;
        public RadioButton rb_server;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
        	if (type.equals("report"))
        		vi = inflater.inflate(R.layout.my_report_item, null);
        	else if (type.equals("server"))
        		vi = inflater.inflate(R.layout.server_item, null);

            holder=new ViewHolder();
            if (type.equals("report")) {
	            holder.txt_report_service=(TextView)vi.findViewById(R.id.txt_report_service);
	            holder.txt_date_server=(TextView)vi.findViewById(R.id.txt_date_server);
            }
            else if (type.equals("server")){
            	holder.txt_city_state=(TextView)vi.findViewById(R.id.txt_city_state);
	            holder.txt_url=(TextView)vi.findViewById(R.id.txt_url);
	            holder.rb_server = (RadioButton) vi.findViewById(R.id.rb);
            }
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        	
        	if (type.equals("server")) {
        		String city_state = null;
        		String url = null;
				try {
					city_state = data.getJSONObject(position).getString("name");
					url = data.getJSONObject(position).getString("url");
					SharedPreferences pref = activity.getSharedPreferences("server",0);
					
					JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
					String server_name = server.getString("name");
					
					if (city_state.equals(server_name)) 
						holder.rb_server.setChecked(true);
					else
						holder.rb_server.setChecked(false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	
        		holder.txt_city_state.setText(city_state);
        		holder.txt_url.setText(url);
        	}
        	
        	else if (type.equals("report")) {
        		String report_service = null;
    			String date_server = null;
        		try {
        			report_service = data.getJSONObject(position).getString("report_service");
        			date_server = data.getJSONObject(position).getString("date_time")+": "+data.getJSONObject(position).getString("server_name");
        			
	        	} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		holder.txt_report_service.setText(report_service);
            	holder.txt_date_server.setText(date_server);
        	}

        	return vi;
	}
	

}

