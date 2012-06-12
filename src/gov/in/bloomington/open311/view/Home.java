/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.open311.view;

import gov.in.bloomington.open311.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import org.json.JSONObject;
import org.json.JSONException;

public class Home extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	@Override
	protected void onResume (){
		super.onResume();
		SharedPreferences pref = getSharedPreferences("server",0);
		try {
			JSONObject server = new JSONObject(pref.getString("selectedServer", ""));
			String server_name = server.getString("name");
			
			ImageView img_splash = (ImageView) findViewById(R.id.splash);
			
			if (server_name.equals("Bloomington, IN"))
				img_splash.setImageResource(R.drawable.bloomington);
			else if (server_name.equals("Baltimore, MD"))
				img_splash.setImageResource(R.drawable.baltimore);
			else if (server_name.equals("Boston, MA"))
				img_splash.setImageResource(R.drawable.boston);
			else 
				img_splash.setImageResource(R.drawable.splash);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
