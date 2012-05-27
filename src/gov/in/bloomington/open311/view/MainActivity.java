/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
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

public class MainActivity extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, HomeActivity.class);
        spec = tabHost.newTabSpec("home").setIndicator(res.getString(R.string.home), res.getDrawable(R.drawable.ic_menu_home)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, ReportActivity.class);
        spec = tabHost.newTabSpec("report").setIndicator(res.getString(R.string.report), res.getDrawable(R.drawable.ic_menu_notifications)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MyReportsActivity.class);
        spec = tabHost.newTabSpec("my_reports").setIndicator(res.getString(R.string.my_reports), res.getDrawable(R.drawable.ic_menu_home)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MyServersActivity.class);
        spec = tabHost.newTabSpec("my_servers").setIndicator(res.getString(R.string.my_servers), res.getDrawable(R.drawable.ic_menu_star)).setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.preferences:
    		Intent i = new Intent(MainActivity.this, MyPreferencesActivity.class);
    		startActivity(i);
    		break;
    	}
    	return true;
    }
}