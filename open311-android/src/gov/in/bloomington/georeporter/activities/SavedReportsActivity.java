/**
 * Activity to display all the saved reports
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.cityreporter.R;
import gov.in.bloomington.georeporter.fragments.SavedReportViewFragment;
import gov.in.bloomington.georeporter.fragments.SavedReportsListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SavedReportsActivity extends BaseFragmentActivity implements OnItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setTitle(R.string.menu_archive);
		SavedReportsListFragment listFragment = new SavedReportsListFragment();
		getSupportFragmentManager() .beginTransaction()
                        		    .add(android.R.id.content, listFragment)
                        		    .addToBackStack(null)
                        		    .commit();
		    
	}

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        SavedReportViewFragment fragment = SavedReportViewFragment.newInstance(position);
        getSupportFragmentManager() .beginTransaction()
                                    .replace(android.R.id.content, fragment)
                                    .addToBackStack(null)
                                    .commit();
    }
}
