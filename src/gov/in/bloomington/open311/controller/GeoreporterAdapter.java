package gov.in.bloomington.open311.controller;

//import org.json.JSONArray;

import gov.in.bloomington.open311.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GeoreporterAdapter extends BaseAdapter {

	private Activity activity;
    //private JSONArray data;
    private String type;
    private static LayoutInflater inflater=null;

	
    public GeoreporterAdapter(Activity a, String t) {
        activity = a;
        //data=d;
        type = t;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public int getCount() {
		// TODO Auto-generated method stub
		//return data.length();
		return 3;
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
//        public Button btn_refute;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
        	if (type.equals("report"))
        		vi = inflater.inflate(R.layout.my_report_item, null);
        	else 
        		vi = inflater.inflate(R.layout.server_item, null);

            holder=new ViewHolder();
            holder.txt_report_service=(TextView)vi.findViewById(R.id.txt_report_service);
            holder.txt_date_server=(TextView)vi.findViewById(R.id.txt_date_server);
            
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();
        
        	//holder.txt_report_service.setText("Report Service");
        	//holder.txt_date_server.setText("Date - Time: server reported to");
        	
        	return vi;
	}
	

}
