/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.models.ServiceRequest;
import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SavedReportViewFragment extends SherlockFragment {
    private static final String mTag = "SavedReportViewFragment";
    private static final String POSITION = "position";
    private JSONArray      mServiceRequests;
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
        
        mServiceRequests = Open311.loadServiceRequests(getActivity());
        try {
            String json = mServiceRequests.getJSONObject(mPosition).toString();
            Log.i("SavedReportViewFragment", json);
            mServiceRequest = new ServiceRequest(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_saved, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        refreshViewData();
        new RefreshFromServerTask().execute();
        super.onActivityCreated(savedInstanceState);
    }
    
    private void refreshViewData() {
        View v = getView();
        TextView textView;
        
        textView = (TextView) v.findViewById(R.id.service_name);
        textView.setText(mServiceRequest.service.optString(Open311.SERVICE_NAME));
        
        ImageView media = (ImageView) v.findViewById(R.id.media);
        media.setImageBitmap(mServiceRequest.getMediaBitmap(100, 100, getActivity()));
        
        textView = (TextView) v.findViewById(R.id.address);
        if (mServiceRequest.service_request.has(Open311.ADDRESS)) {
            textView.setText(mServiceRequest.service_request.optString(Open311.ADDRESS));
        }
        else if (mServiceRequest.post_data.has(Open311.ADDRESS_STRING)) {
            textView.setText(mServiceRequest.post_data.optString(Open311.ADDRESS_STRING));
        }
        
        textView = (TextView) v.findViewById(R.id.description);
        if (mServiceRequest.service_request.has(Open311.DESCRIPTION)) {
            textView.setText(mServiceRequest.service_request.optString(Open311.DESCRIPTION));
        }
        else if (mServiceRequest.post_data.has(Open311.DESCRIPTION)) {
            textView.setText(mServiceRequest.post_data.optString(Open311.DESCRIPTION));
        }
        
        textView = (TextView) v.findViewById(R.id.status);
        if (mServiceRequest.service_request.has(ServiceRequest.STATUS)) {
            textView.setText(mServiceRequest.service_request.optString(ServiceRequest.STATUS));
        }
        Log.i(mTag, "Refreshed View");
    }
    
    private class RefreshFromServerTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean tokenUpdated          = false;
            Boolean serviceRequestUpdated = false;
            
            if (!mServiceRequest.service_request.has(Open311.SERVICE_REQUEST_ID)) {
                tokenUpdated = fetchServiceRequestId();
            }
            
            if (mServiceRequest.service_request.has(Open311.SERVICE_REQUEST_ID)) {
                serviceRequestUpdated  = fetchServiceRequest();
            }
            return tokenUpdated || serviceRequestUpdated;
        }
        
        private Boolean fetchServiceRequest() {
            try {
                String request_id = mServiceRequest.service_request.getString(Open311.SERVICE_REQUEST_ID);
                return updateServiceRequest(Open311.loadStringFromUrl(mServiceRequest.getServiceRequestUrl(request_id)));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
        
        private Boolean fetchServiceRequestId() {
            try {
                String token = mServiceRequest.service_request.getString(Open311.TOKEN);
                return updateServiceRequest(Open311.loadStringFromUrl(mServiceRequest.getServiceRequestIdFromTokenUrl(token)));
            }
            catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
        
        private Boolean updateServiceRequest(String result) {
            if (result != null && result != "") {
                JSONArray response;
                try {
                    response = new JSONArray(result);
                    mServiceRequest.service_request = response.getJSONObject(0);
                    return true;
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
        
        @Override
        protected void onPostExecute(Boolean dataUpdated) {
            super.onPostExecute(dataUpdated);
            if (dataUpdated) {
                Log.i(mTag, "Data was updated from server");
                try {
                    mServiceRequests.put(mPosition, new JSONObject(mServiceRequest.toString()));
                    Open311.saveServiceRequests(getActivity(), mServiceRequests);
                    refreshViewData();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
