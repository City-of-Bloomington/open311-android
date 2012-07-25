/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Fransiska Putri Wina Hadiwidjana <fransiskapw@gmail.com>
 */

package gov.in.bloomington.open311.model;


import gov.in.bloomington.open311.controller.GeoreporterUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/*
 * dataAccess (model) class to connect with external file I/O process
 */
public class ExternalFileAdapter {
	
	/** Write JSON to external file */
	public static boolean writeJSON(Activity a, String filename,JSONArray content) {
		boolean succes = false;
		String content_string = content.toString();
		FileOutputStream fos;
		try {
			fos = a.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content_string.getBytes());
			fos.close();
			succes = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return succes;
	}
	
	/** Read JSON from external file */
	public static JSONArray readJSON(Activity a, String filename) {
		JSONArray result = null;
	    InputStream inputstream;
		try {
			inputstream = a.openFileInput(filename);
			result = new JSONArray(GeoreporterUtils.convertStreamToString(inputstream));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	/** Read JSON from raw resource */
	public static JSONArray readJSONRaw(Activity a, int raw_resource) {
		JSONArray result = null;
		InputStream inputStream = a.getResources().openRawResource(raw_resource);		
		try {
			result = new JSONArray(GeoreporterUtils.convertStreamToString(inputStream));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return result;
	}
	
	/** Decode Bitmap from URI */
	 public static Bitmap decodeUri(Uri selectedImage, Activity act) throws FileNotFoundException {

	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(act.getContentResolver().openInputStream(selectedImage), null, o);

	        // The new size we want to scale to
	        final int REQUIRED_SIZE = 70;

	        // Find the correct scale value. It should be the power of 2.
	        int width_tmp = o.outWidth, height_tmp = o.outHeight;
	        int scale = 1;
	        while (true) {
	        if (width_tmp / 2 < REQUIRED_SIZE
	            || height_tmp / 2 < REQUIRED_SIZE)
	            break;
	        width_tmp /= 2;
	        height_tmp /= 2;
	        scale *= 2;
	        }

	        // Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        return BitmapFactory.decodeStream(act.getContentResolver().openInputStream(selectedImage), null, o2);

	    }
}
