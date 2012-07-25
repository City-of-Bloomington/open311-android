/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.view;

import gov.in.bloomington.open311.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

/*
 * presentation (view) class which contain all other page open311-android in tabhost 
 */
public class Main extends TabActivity  {
	TabHost tabHost;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, Home.class);
        spec = tabHost.newTabSpec("home").setIndicator(res.getString(R.string.home), res.getDrawable(R.drawable.ic_menu_home)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, NewReport.class);
        spec = tabHost.newTabSpec("new_report").setIndicator(res.getString(R.string.report), res.getDrawable(R.drawable.ic_menu_notifications)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MyReports.class);
        spec = tabHost.newTabSpec("my_reports").setIndicator(res.getString(R.string.my_reports), res.getDrawable(R.drawable.ic_menu_myreports)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MyServers.class);
        spec = tabHost.newTabSpec("my_servers").setIndicator(res.getString(R.string.my_servers), res.getDrawable(R.drawable.ic_menu_servers)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
    }

    /** display preference menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }

    /** perform action when preference item selected */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.preferences:
    		Intent i = new Intent(Main.this, MyPreferences.class);
    		startActivity(i);
    		break;
    	}
    	return true;
    }
    
    /** let tabs switch to each other internally */
    public void switchTab(int tab){
        tabHost.setCurrentTab(tab);
    }

}