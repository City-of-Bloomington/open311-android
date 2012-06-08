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
		String server_name = pref.getString("server_name", "");
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
}
