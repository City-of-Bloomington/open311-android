/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.adapters.ServiceRequestAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ReportFragment extends SherlockListFragment {
	ServiceRequest mServiceRequest;
	
	/**
	 * @param sr
	 * @return
	 * ReportFragment
	 */
	public static ReportFragment newInstance(ServiceRequest sr) {
	    ReportFragment fragment = new ReportFragment();
	    Bundle args = new Bundle();
	    args.putString(Open311.SERVICE_REQUEST, sr.toString());
	    fragment.setArguments(args);
	    return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mServiceRequest = new ServiceRequest(getArguments().getString(Open311.SERVICE_REQUEST));
	    setListAdapter(new ServiceRequestAdapter(mServiceRequest, getActivity()));
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    // TODO Auto-generated method stub
	    super.onListItemClick(l, v, position, id);
	}
}