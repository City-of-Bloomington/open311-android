package android.georeporter.view;

import android.georeporter.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;

public class home extends TabActivity implements OnClickListener {
	//for tabs
	private TabHost mTabHost; // The activity TabHost
	private TabHost.TabSpec spec; // Resusable TabSpec for each tab
	
	private Intent intent;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        //for tabs
	    mTabHost = getTabHost();  
	    
	    // for home_index
	    intent = new Intent().setClass(this, home_index.class);
	    spec = mTabHost.newTabSpec("tab_home_index").setIndicator("Home").setContent(intent);
	    mTabHost.addTab(spec);

	    // for home_new_report
	    intent = new Intent().setClass(this, home_new_report.class);
	    spec = mTabHost.newTabSpec("tab_home_new_report").setIndicator("New Report").setContent(intent);
	    mTabHost.addTab(spec);
	    
	    // for home_my_reports
	    intent = new Intent().setClass(this, home_my_reports.class);
	    spec = mTabHost.newTabSpec("tab_home_my_reports").setIndicator("My Reports").setContent(intent);
	    mTabHost.addTab(spec);

	    // for home_servers
	    intent = new Intent().setClass(this, home_servers.class);
	    spec = mTabHost.newTabSpec("tab_haome_report_to").setIndicator("Report To").setContent(intent);
	    mTabHost.addTab(spec);

	    mTabHost.setCurrentTab(0);
	    
	    mTabHost.setCurrentTab(0);
	    
    }

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}