/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/AGPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import gov.in.bloomington.open311.R;
import gov.in.bloomington.open311.controller.GeoreporterAdapter;
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

public class MyReports extends Activity {
	
	private ListView list_report;
	private GeoreporterAdapter adapter;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reports);
		
		list_report=(ListView)findViewById(R.id.list);
		adapter = new GeoreporterAdapter(MyReports.this,"report");
		
		list_report.setAdapter(adapter);
		
		list_report.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MyReports.this);
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
			intent = new Intent(MyReports.this, ReportDetail.class);
            startActivity(intent);

		}
		});
	}
}
