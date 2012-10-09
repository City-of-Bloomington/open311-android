/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.adapters.GroupsAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ChooseGroupFragment extends SherlockListFragment {
	OnGroupSelectedListener mListener;
	
	public interface OnGroupSelectedListener {
		public void onGroupSelected(String group);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setListAdapter(new GroupsAdapter(getActivity()));
		mListener = (OnGroupSelectedListener) activity;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mListener.onGroupSelected(Open311.sGroups.get(position));
	}
}
