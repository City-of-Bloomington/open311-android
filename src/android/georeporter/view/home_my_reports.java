package android.georeporter.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.georeporter.R;
import android.georeporter.controller.georeporter_adapter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class home_my_reports extends Activity {
	
	private ListView list_report;
	private georeporter_adapter adapter;
	Intent intent;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_my_reports);
        
        list_report=(ListView)findViewById(R.id.list);
		adapter = new georeporter_adapter(home_my_reports.this,"report");
		
		list_report.setAdapter(adapter);
		
		list_report.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(home_my_reports.this);
				builder.setMessage("Delete Report on Phone?")
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
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
			intent = new Intent(home_my_reports.this, report_detail.class);
            startActivity(intent);

		}
		});
    }

}