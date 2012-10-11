/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.adapters.SavedReportsAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;

public class SavedReportsListFragment extends SherlockListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new SavedReportsAdapter(Open311.loadServiceRequests(getActivity()), getActivity()));
	}
}
