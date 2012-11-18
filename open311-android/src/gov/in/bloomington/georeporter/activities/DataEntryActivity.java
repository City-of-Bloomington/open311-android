/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.activities;

import gov.in.bloomington.georeporter.R;
import gov.in.bloomington.georeporter.models.Open311;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataEntryActivity extends BaseActivity {
    public static final String KEY    = "key";
    public static final String VALUE  = "value";
    public static final String PROMPT = "prompt";
    
    private LinearLayout mLayout;
    private EditText     mInput;
    private String       mKey;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LayoutInflater inflater = getLayoutInflater();
        
        setContentView(R.layout.activity_data_entry);
                mLayout = (LinearLayout) findViewById(R.id.attribute_layout);
        TextView prompt = (TextView)     findViewById(R.id.prompt);
                mInput  = (EditText) inflater.inflate(R.layout.attribute_entry_string, null);
        
        Intent i = getIntent();
            mKey =     i.getStringExtra(KEY);
        prompt.setText(i.getStringExtra(PROMPT));
        mInput.setText(i.getStringExtra(VALUE));
        
        if (mKey.equals(Open311.DESCRIPTION)) {
            mInput.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        if (mKey.equals(Open311.FIRST_NAME) || mKey.equals(Open311.LAST_NAME)) {
            mInput.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        if (mKey.equals(Open311.EMAIL)) {
            mInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if (mKey.equals(Open311.PHONE)) {
            mInput.setInputType(InputType.TYPE_CLASS_PHONE);
            
        }
        
        mLayout.addView(mInput);
        
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
    
    /**
     * OnClick handler for the submit button
     * 
     * @param v
     * void
     */
    public void submit(View v) {
        Intent result = new Intent();
        result.putExtra(KEY,   mKey);
        result.putExtra(VALUE, mInput.getText().toString());
        
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
