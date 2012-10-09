/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.activities.ChooseLocationActivity;
import gov.in.bloomington.georeporter.activities.MainActivity;
import gov.in.bloomington.georeporter.dialogs.DatePickerDialogFragment;
import gov.in.bloomington.georeporter.models.Open311;
import gov.in.bloomington.georeporter.tasks.ReverseGeocodingTask;
import gov.in.bloomington.georeporter.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;

public class ReportFragment extends SherlockFragment {
	public static final int CHOOSE_LOCATION_REQUEST = 1;
	
	private JSONObject mService, mDefinition;
	private JSONArray  mAttributes;
	private HashMap<String, View> mAttributeViews;
	
	private EditText   mLocationView, mDescription;
	private Double     mLatitude, mLongitude;
	
	/**
	 * Initialize this report with a service
	 * 
	 * This should be called before adding this fragment to the stack
	 * Since fragments cannot have constructors, you must call
	 * this function immediately after instantiating this fragment.
	 * 
	 * @param service
	 * void
	 */
	public void setService(JSONObject service) {
		mService = service;
		if (mService.optBoolean(Open311.METADATA)) {
			mDefinition     = Open311.sServiceDefinitions.get(mService.opt(Open311.SERVICE_CODE));
			mAttributes     = mDefinition.optJSONArray(Open311.ATTRIBUTES);
			mAttributeViews = new HashMap<String, View>(mAttributes.length());
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("service", mService.toString());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			try {
				JSONObject s = new JSONObject(savedInstanceState.getString("service"));
				setService(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		View v = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_report, container, false);
		mLocationView = (EditText) v.findViewById(R.id.address_string);
		mDescription  = (EditText) v.findViewById(R.id.description);
		
		// Register onClick handlers for all the clickables in the layout
		v.findViewById(R.id.mapChooserButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ChooseLocationActivity.class);
				startActivityForResult(i, CHOOSE_LOCATION_REQUEST);
			}
		});
		v.findViewById(R.id.button_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		v.findViewById(R.id.button_submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit(v);
			}
		});
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		TextView service_description = (TextView) getView().findViewById(R.id.service_description);
		service_description.setText(mService.optString(Open311.DESCRIPTION));
		
		// Inflate all the views for the service attributes
		if (mService.optBoolean(Open311.METADATA)) {
			LinearLayout layout     = (LinearLayout) getView().findViewById(R.id.attributes);
			
			int len = mAttributes.length();
			for (int i=0; i<len; i++) {
				JSONObject a;
				try {
					a = mAttributes.getJSONObject(i);
					
					View v = loadViewForAttribute(a, savedInstanceState);
					if (v != null) {
						String description = a.getString(Open311.DESCRIPTION);
						TextView label = (TextView) v.findViewById(R.id.label);
						label.setText(description);
						
						// Store the reference to this view
						// We'll need to grab data from it when we submit
						mAttributeViews.put(a.getString(Open311.CODE), v);
						
						layout.addView(v);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	/**
	 * Callback from ChooseLocationActivity
	 * 
	 * Intent data should have latitude and longitude
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == CHOOSE_LOCATION_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				int latitudeE6  = data.getIntExtra(Open311.LATITUDE,  0);
				int longitudeE6 = data.getIntExtra(Open311.LONGITUDE, 0);
				
				String latitude  = Double.toString(latitudeE6  / 1e6);
				String longitude = Double.toString(longitudeE6 / 1e6);
				// Display the lat/long as text for now
				// It will get replaced with the address when ReverseGeoCodingTask returns
				mLocationView.setText(String.format("%s, %s", latitude, longitude));
				
				new ReverseGeocodingTask(getActivity(), mLocationView).execute(new GeoPoint(latitudeE6, longitudeE6));
			}
		}
	}
	
	/**
	 * Inflates the appropriate view for each datatype
	 * 
	 * @param attribute
	 * @param savedInstanceState
	 * @return
	 * View
	 */
	private View loadViewForAttribute(JSONObject attribute, Bundle savedInstanceState) {
		LayoutInflater inflater = getLayoutInflater(savedInstanceState);
		String         datatype = attribute.optString(Open311.DATATYPE, Open311.STRING);

		if (datatype.equals(Open311.STRING) || datatype.equals(Open311.NUMBER) || datatype.equals(Open311.TEXT)) {
			View v = inflater.inflate(R.layout.list_item_report_attributes_string, null);
			EditText input = (EditText) v.findViewById(R.id.input);
			
			if (datatype.equals(Open311.NUMBER)) {
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			if (datatype.equals(Open311.TEXT)) {
				input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			}
			return v;
		}
		else if (datatype.equals(Open311.DATETIME)) {
			View v = inflater.inflate(R.layout.list_item_report_attributes_datetime, null);
			TextView input = (TextView) v.findViewById(R.id.input);
			input.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SherlockDialogFragment picker = new DatePickerDialogFragment(v);
					picker.show(getActivity().getSupportFragmentManager(), "datePicker");
				}
			});
			return v;
		}
		else if (datatype.equals(Open311.SINGLEVALUELIST) || datatype.equals(Open311.MULTIVALUELIST)) {
			JSONArray values = attribute.optJSONArray(Open311.VALUES);
			int len = values.length();
			
			if (datatype.equals(Open311.SINGLEVALUELIST)) {
				View v = inflater.inflate(R.layout.list_item_report_attributes_singlevaluelist, null);
				RadioGroup input = (RadioGroup) v.findViewById(R.id.input);
				for (int i=0; i<len; i++) {
					JSONObject value = values.optJSONObject(i);
					RadioButton button = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
					button.setText(value.optString(Open311.KEY));
					input.addView(button);
				}
				return v;
			}
			else if (datatype.equals(Open311.MULTIVALUELIST)) {
				View v = inflater.inflate(R.layout.list_item_report_attributes_multivaluelist, null);
				LinearLayout input = (LinearLayout) v.findViewById(R.id.input);
				for (int i=0; i<len; i++) {
					JSONObject value = values.optJSONObject(i);
					CheckBox checkbox = (CheckBox) inflater.inflate(R.layout.checkbox, null);
					checkbox.setText(value.optString(Open311.KEY));
					input.addView(checkbox);
				}
				return v;
			}
		}
		return null;
	}

	/**
	 * Reads in all the values from the ReportFragment view
	 * POSTs the report to the server
	 * Sends the user to the saved report screen
	 * 
	 * @param v
	 * void
	 */
	public void submit(View v) {
		HashMap<String, String> post;
		try {
			post = generatePost(v);
			new ServiceRequestPost().execute(post);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads in all the values from the view layout
	 * 
	 * @param v
	 * @return
	 * HashMap<String,String>
	 * @throws JSONException 
	 */
	private HashMap<String, String> generatePost(View v) throws JSONException {
		HashMap<String, String> post = new HashMap<String, String>();
		post.put(Open311.SERVICE_CODE, mService.getString(Open311.SERVICE_CODE));
		post.put(Open311.ADDRESS,      mLocationView.getText().toString());
		post.put(Open311.DESCRIPTION,  mDescription .getText().toString());
		if (mLatitude != null && mLongitude != null) {
			post.put(Open311.LATITUDE,  mLatitude.toString());
			post.put(Open311.LONGITUDE, mLongitude.toString());
		}
		if (mService.optBoolean(Open311.METADATA)) {
			int len = mAttributes.length();
			for (int i=0; i<len; i++) {
				JSONObject attribute = mAttributes.getJSONObject(i);
				String     code      = attribute.getString(Open311.CODE);
				String     datatype  = attribute.optString(Open311.DATATYPE, Open311.STRING);
				String     key       = String.format("attribute[%s]", code);
				
				if (datatype.equals(Open311.STRING) || datatype.equals(Open311.NUMBER) || datatype.equals(Open311.TEXT)) {
					EditText input = (EditText) mAttributeViews.get(code);
					post.put(key, input.getText().toString());
				}
				else if (datatype.equals(Open311.DATETIME)) {
					// TODO read date string from text field and add it to POST
					TextView input = (TextView) mAttributeViews.get(code);
					DateFormat df = DateFormat.getInstance();
					try {
						Date date = df.parse(input.getText().toString());
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
						post.put(key, format.format(date));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (datatype.equals(Open311.SINGLEVALUELIST) || datatype.equals(Open311.MULTIVALUELIST)) {
					ViewGroup input = (ViewGroup) mAttributeViews.get(code);
					JSONArray values = attribute.optJSONArray(Open311.VALUES);
					int l = values.length();
					for (int j=0; j<l; j++) {
						CompoundButton b = (CompoundButton) input.getChildAt(j);
						if (b.isChecked()) {
							String value = values.getJSONObject(j).getString(Open311.NAME);
							if (datatype.equals(Open311.SINGLEVALUELIST)) {
								post.put(key, value);
								break;
							}
							else {
								post.put(key + "[]", value);
							}
						}
					}
				}
			}
		}
		return post;
	}
	
	private class ServiceRequestPost extends AsyncTask<HashMap<String, String>, Void, Boolean> {
		ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.dialog_loading_services), "", true);
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {
			Boolean result = false;
			
			JSONArray response = Open311.postServiceRequest(params[0]);
			if (response != null && response.length()>0) {
				result = Open311.saveServiceRequest(getActivity(), response);
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (!result) {
				Util.displayCrashDialog(getActivity(), "Failed to post report to server");
			}
			else {
				// TODO send them to the saved reports activity
			}
			super.onPostExecute(result);
		}
	}
}
