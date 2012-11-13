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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AttributeEntryActivity extends BaseActivity {
    public static final String ATTRIBUTE = "attribute";
    
    private JSONObject   mAttribute;
    private String       mCode;
    private String       mDatatype;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_data_entry);
        LinearLayout layout = (LinearLayout) findViewById(R.id.attribute_layout);
        TextView     prompt = (TextView)     findViewById(R.id.prompt);
        
        Intent i = getIntent();
        try {
            mAttribute = new JSONObject(i.getStringExtra(ATTRIBUTE));
            mCode      = mAttribute.getString(Open311.CODE);
            mDatatype  = mAttribute.optString(Open311.DATATYPE, Open311.STRING);
            
            prompt.setText(mAttribute.getString(Open311.DESCRIPTION));
            layout.addView(loadAttributeEntryView());
            
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
            View v = inflater.inflate(R.layout.attribute_entry_string, null);
            EditText input = (EditText) v.findViewById(R.id.input);

            if (mDatatype.equals(Open311.NUMBER)) {
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if (mDatatype.equals(Open311.TEXT)) {
                input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
            return v;
        }
        else if (mDatatype.equals(Open311.SINGLEVALUELIST) || mDatatype.equals(Open311.MULTIVALUELIST)) {
            JSONArray values = mAttribute.optJSONArray(Open311.VALUES);
            int len = values.length();

            if (mDatatype.equals(Open311.SINGLEVALUELIST)) {
                View v = inflater.inflate(R.layout.attribute_entry_singlevaluelist, null);
                RadioGroup input = (RadioGroup) v.findViewById(R.id.input);
                for (int i=0; i<len; i++) {
                    JSONObject value = values.optJSONObject(i);
                    RadioButton button = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
                    button.setText(value.optString(Open311.KEY));
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
                    checkbox.setText(value.optString(Open311.KEY));
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
     * Send the entered data back to ReportFragment
     * 
     * @param v
     * void
     */
    public void submit(View v) {
        Intent result = new Intent();
        result.putExtra(Open311.CODE,     mCode);
        result.putExtra(Open311.DATATYPE, mDatatype);
        // TODO read user input from the view and add to the intent
        // We need to figure out how we're going to be storing the user's
        // input inside of a ServiceRequest.
        // Take a look at what we're doing with ServiceRequest.post_data
        
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
