/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.util;

import gov.in.bloomington.cityreporter.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Util {
	/**
	 * Returns the content of a raw resource as a string
	 * 
	 * @param Context c
	 * @param AndroidResource resource
	 * @return String
	 */
	public static String file_get_contents(Context c, int resource) {
		InputStream in = c.getResources().openRawResource(resource);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder s = new StringBuilder();
		String l = null;
		try {
			while ((l = reader.readLine()) != null) {
				s.append(l);
			}
			in.close();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return s.toString();
	}
	
	/**
	 * Displays a modal dialog to the user
	 * 
	 * Use this function for errors that prevent the rest of the application
	 * from working.  We're not saying the app is really crashing, it's just
	 * that something occurred which leaves the app in an inconsistent state
	 *  
	 * @param c
	 * @param message
	 * void
	 */
	public static void displayCrashDialog(Context c, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Error")
			   .setMessage(message)
			   .setCancelable(false)
			   .setNegativeButton(R.string.button_accept_error, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
			   });
		builder.create();
		builder.show();
	}
}
