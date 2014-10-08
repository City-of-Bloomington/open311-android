/**
 * Activity to display all the saved reports
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.fragments.SavedReportViewFragment;
import gov.in.bloomington.georeporter.fragments.SavedReportsListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SavedReportsActivity extends BaseActivity implements OnItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setTitle(R.string.menu_archive);
		SavedReportsListFragment listFragment = new SavedReportsListFragment();
		getFragmentManager().beginTransaction()
                        	.add(android.R.id.content, listFragment)
                        	.addToBackStack(null)
                        	.commit();
		    
	}

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        SavedReportViewFragment fragment = SavedReportViewFragment.newInstance(position);
        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, fragment)
                            .addToBackStack(null)
                            .commit();
    }
}
