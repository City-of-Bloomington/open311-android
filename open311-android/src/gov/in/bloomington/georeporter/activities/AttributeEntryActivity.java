/**
 * An activity for the user to enter data for a single attribute
 * 
 * We'll be using this same activity for all attributes, but we only work with
 * one attribute at a time.
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;

import gov.in.bloomington.georeporter.util.json.JSONArray;
import gov.in.bloomington.georeporter.util.json.JSONException;
import gov.in.bloomington.georeporter.util.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AttributeEntryActivity extends BaseActivity {
    public static final String ATTRIBUTE = "attribute";
    public static final String VALUE     = "value";
    
    private JSONObject   mAttribute;
    private String       mCode;
    private String       mDatatype;
    private LinearLayout mLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_data_entry);
                    mLayout = (LinearLayout) findViewById(R.id.attribute_layout);
        TextView     prompt = (TextView)     findViewById(R.id.prompt);
        
        Intent i = getIntent();
        try {
            mAttribute = new JSONObject(i.getStringExtra(ATTRIBUTE));
            mCode      = mAttribute.getString(Open311.CODE);
            mDatatype  = mAttribute.optString(Open311.DATATYPE, Open311.STRING);
            
            prompt.setText(mAttribute.getString(Open311.DESCRIPTION));
            mLayout.addView(loadAttributeEntryView());
            
            if (mDatatype.equals(Open311.STRING) || mDatatype.equals(Open311.NUMBER) || mDatatype.equals(Open311.TEXT)) {
                this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        } catch (JSONException e) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    
    /**
     * Inflates the custom views for each attribute datatype
     * 
     * Each of the views should create a view with +id/input.  That way, we can
     * pull the user's data entry out when they hit the submit button.
     * 
     * Note: DateTime attributes are not handled here
     * It was easier to directly open a datePicker dialog in ReportFragment.
     * Look in ReportFragment for DateTime attribute handling.
     * 
     * @return
     * View
     */
    private View loadAttributeEntryView() {
        LayoutInflater inflater = getLayoutInflater();
        
        if (mDatatype.equals(Open311.STRING) || mDatatype.equals(Open311.NUMBER) || mDatatype.equals(Open311.TEXT)) {
            EditText input = (EditText) inflater.inflate(R.layout.attribute_entry_string, null);

            if (mDatatype.equals(Open311.NUMBER)) {
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if (mDatatype.equals(Open311.TEXT)) {
                input.setInputType(
                  	 InputType.TYPE_CLASS_TEXT
              		|InputType.TYPE_TEXT_FLAG_MULTI_LINE
              		|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
              		|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
              		|InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
          		);
                input.setMaxLines(6);
                input.setHorizontallyScrolling(false);
            }
            return input;
        }
        else if (mDatatype.equals(Open311.SINGLEVALUELIST) || mDatatype.equals(Open311.MULTIVALUELIST)) {
            /**
             * Each value object has a key and a name: {key:"", name:""}
             * We want to display the name to the user, but need to POST
             * the key to the endpoint.
             * 
             * We rely on the order to keep track of which value is which
             */
            JSONArray values = mAttribute.optJSONArray(Open311.VALUES);
            int len = values.length();

            if (mDatatype.equals(Open311.SINGLEVALUELIST)) {
                View v = inflater.inflate(R.layout.attribute_entry_singlevaluelist, null);
                RadioGroup input = (RadioGroup) v.findViewById(R.id.input);
                for (int i=0; i<len; i++) {
                    JSONObject value = values.optJSONObject(i);
                    RadioButton button = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
                    button.setText(value.optString(Open311.NAME));
                    input.addView(button);
                }
                return v;
            }
            else if (mDatatype.equals(Open311.MULTIVALUELIST)) {
                View v = inflater.inflate(R.layout.attribute_entry_multivaluelist, null);
                LinearLayout input = (LinearLayout) v.findViewById(R.id.input);
                for (int i=0; i<len; i++) {
                    JSONObject value = values.optJSONObject(i);
                    CheckBox checkbox = (CheckBox) inflater.inflate(R.layout.checkbox, null);
                    checkbox.setText(value.optString(Open311.NAME));
                    input.addView(checkbox);
                }
                return v;
            }
        }
        return null;
    }

    /**
     * OnClick handler for the submit button
     * 
     * Send the entered data back to ReportFragment.
     * Multivaluelist data should be sent as a serialized JSONArray.
     * All the other datatypes should be sent as a plain string.
     * 
     * @param v
     * void
     */
    public void submit(View v) {
        Intent result = new Intent();
        result.putExtra(Open311.CODE,     mCode);
        result.putExtra(Open311.DATATYPE, mDatatype);
        
        if (mDatatype.equals(Open311.STRING) || mDatatype.equals(Open311.NUMBER) || mDatatype.equals(Open311.TEXT)) {
            EditText input = (EditText) mLayout.findViewById(R.id.input);
            result.putExtra(VALUE, input.getText().toString());
        }
        else if (mDatatype.equals(Open311.SINGLEVALUELIST) || mDatatype.equals(Open311.MULTIVALUELIST)) {
            /**
             * Each value object has a key and a name: { key:"", name:"" }
             * We want to display the name to the user, but need to POST
             * the key to the endpoint.
             * 
             * We rely on the order to keep track of which value is which
             */
            JSONArray values = mAttribute.optJSONArray(Open311.VALUES);

            try {
                if (mDatatype.equals(Open311.SINGLEVALUELIST)) {
                    RadioGroup  input = (RadioGroup)  mLayout.findViewById(R.id.input);
                    
                    int count = input.getChildCount();
                    for (int i = 0; i < count; i++) {
                        RadioButton b = (RadioButton) input.getChildAt(i);
                        if (b.isChecked()) {
                            result.putExtra(VALUE, values.getJSONObject(i).getString(Open311.KEY));
                        }
                    }
                }
                else if (mDatatype.equals(Open311.MULTIVALUELIST)){
                    JSONArray submittedValues = new JSONArray();
                    
                    LinearLayout input = (LinearLayout) mLayout.findViewById(R.id.input);
                    int count = input.getChildCount();
                    for (int i=0; i < count; i++) {
                        CheckBox checkbox = (CheckBox) input.getChildAt(i);
                        if (checkbox.isChecked()) {
                            submittedValues.put(values.getJSONObject(i).getString(Open311.KEY));
                        }
                    }
                    result.putExtra(VALUE, submittedValues.toString());
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                cancel(v);
                return;
            }
        }
        else {
            // Unknown datatype
            cancel(v);
            return;
        }
        
        setResult(RESULT_OK, result);
        finish();
    }
    
    /**
     * OnClick handler for the cancel button
     * 
     * @param v
     * void
     */
    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
