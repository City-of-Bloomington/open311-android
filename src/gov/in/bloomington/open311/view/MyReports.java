/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAdapter;
import gov.in.bloomington.open311.model.ExternalFileAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/*
 * presentation (view) class to display and perform function regarding user's report information
 */
public class MyReports extends Activity {
	
	private ListView list_report;
	private GeoreporterAdapter adapter;
	private JSONArray ja_reports;
	Intent intent;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reports);
	}
	
	/** Called everytime MyServers is the focused tab or display is resumed */
	@Override
	protected void onResume (){
		super.onResume();
		list_report=(ListView)findViewById(R.id.list);
		ja_reports = ExternalFileAdapter.readJSON(MyReports.this, "reports");
		//ja_reports = ExternalFileAdapter.readJSONRaw(MyReports.this, R.raw.available_servers);
		adapter = new GeoreporterAdapter(MyReports.this, ja_reports, "report");
		
		list_report.setAdapter(adapter);
		
		list_report.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MyReports.this);
				builder.setMessage("Delete Report on Phone?")
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   //delete corresponding json object from jsonarray 
				        	   
				        	   //first - convert jsonarray into array
				        	   ArrayList<JSONObject> list = new ArrayList<JSONObject>();     
				        	   if (ja_reports != null) { 
				        	      for (int i=0;i<ja_reports.length();i++){ 
				        	       try {
									list.add(ja_reports.getJSONObject(i));
				        	       } catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				        	      } 
				        	   }
				        	   
				        	   //second - remove object from the list array
				        	   list.remove(position);
				        	   
				        	   //third - set the list array as the json array
				        	   ja_reports = new JSONArray(list);
				        	   adapter = new GeoreporterAdapter(MyReports.this, ja_reports, "report");
				        	   list_report.setAdapter(adapter);
				        	   ExternalFileAdapter.writeJSON(MyReports.this, "reports", ja_reports);
				        	   
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				    	    	dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				return false;
			}
		
		});
		
		list_report.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				intent = new Intent(MyReports.this, ReportDetail.class);
				try {
					intent.putExtra("report", ja_reports.getJSONObject(position).toString());
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
		}); 
	}
}
