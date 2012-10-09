/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import gov.in.bloomington.georeporter.adapters.ServicesAdapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ChooseServiceFragment extends SherlockListFragment {
	private static OnServiceSelectedListener mListener;
	private static ArrayList<JSONObject> mServices;
	
	public interface OnServiceSelectedListener {
		public void onServiceSelected(JSONObject service);
	}
	
	public void setServices(ArrayList<JSONObject> services) {
		mServices = services;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setListAdapter(new ServicesAdapter(mServices, activity));
		mListener = (OnServiceSelectedListener)activity;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		mListener.onServiceSelected(mServices.get(position));
	}
	
}
