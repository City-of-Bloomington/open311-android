/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.adapters.ServiceRequestAdapter;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;

public class SavedReportViewFragment extends SherlockFragment {
    private static final String POSITION = "position";
    private ServiceRequest mServiceRequest;
    private int            mPosition;
    
    public static SavedReportViewFragment newInstance(int position) {
        SavedReportViewFragment fragment = new SavedReportViewFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(POSITION);
        
        JSONArray serviceRequests = Open311.loadServiceRequests(getActivity());
        try {
            String json = serviceRequests.getJSONObject(mPosition).toString();
            mServiceRequest = new ServiceRequest(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report_saved, container, false);
        ListView listView = (ListView) v.findViewById(R.id.reportListView);
        listView.setAdapter(new ServiceRequestAdapter(mServiceRequest, getActivity()));
        return v;
    }
}
