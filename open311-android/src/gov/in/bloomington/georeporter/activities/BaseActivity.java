/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
    	switch (item.getItemId()) {
	    	case android.R.id.home:
	    		intent = new Intent(this, MainActivity.class);
	    		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(intent);
	    		return true;
	    		
	    	case R.id.menu_settings:
	    		intent = new Intent(this, SettingsActivity.class);
	    		startActivity(intent);
	    		return true;
	    		
	    	case R.id.menu_report:
	    		intent = new Intent(this, ReportActivity.class);
	    		startActivity(intent);
	    		return true;
	    		
	    	case R.id.menu_archive:
	    		intent = new Intent(this, SavedReportsActivity.class);
	    		startActivity(intent);
	    		return true;
	    	
	    	case R.id.menu_about:
	    	    intent = new Intent(this, AboutActivity.class);
	    	    startActivity(intent);
	    	    return true;
	    		
	    	default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
