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
import android.util.Log;

/*
 * dataAccess (model) class to connect with external file I/O process
 */
public class ExternalFileAdapter {
	
	/** Write JSON to external file */
	public boolean writeJSON(final Activity act, final String filename,final JSONArray content) {
		boolean succes = false;
		final String content_string = content.toString();
		FileOutputStream fos;
		try {
			fos = act.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content_string.getBytes());
			fos.close();
			succes = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter writeJSON", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter writeJSON", e.toString()); 
		}
		return succes;
	}
	
	/** Read JSON from external file */
	public JSONArray readJSON(final Activity act, final String filename) {
		JSONArray result = null;
	    InputStream inputstream;
		try {
			inputstream = act.openFileInput(filename);
			final GeoreporterUtils georeporterU = new GeoreporterUtils();
			result = new JSONArray(georeporterU.convertStreamToString(inputstream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter readJSON", e.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter readJSON", e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter readJSON", e.toString());
		}
		return result;
	}
	
	/** Read JSON from raw resource */
	public JSONArray readJSONRaw(final Activity act, final int raw_resource) {
		JSONArray result = null;
		final InputStream inputStream = act.getResources().openRawResource(raw_resource);		
		try {
			final GeoreporterUtils georeporterU = new GeoreporterUtils();
			result = new JSONArray(georeporterU.convertStreamToString(inputStream));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter readJSONRaw", e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("ExternalFileAdapter readJSONRaw", e.toString());
		}
		
		return result;
	}
	
	/** Decode Bitmap from URI */
	 public Bitmap decodeUri(final Uri selectedImage, final Activity act) throws FileNotFoundException {

	        // Decode image size
	        final BitmapFactory.Options opt = new BitmapFactory.Options();
	        opt.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(act.getContentResolver().openInputStream(selectedImage), null, opt);

	        // Find the correct scale value. It should be the power of 2.
	        int width_tmp = opt.outWidth, height_tmp = opt.outHeight;
	        int scale = 1;
	        while (true) {
	        if (width_tmp / 2 < 70 || height_tmp / 2 < 70) { // 70 = REQUIRED SIZE 
	            break;
	        }
	        width_tmp /= 2;
	        height_tmp /= 2;
	        scale *= 2;
	        }

	        // Decode with inSampleSize
	        final BitmapFactory.Options opt2 = new BitmapFactory.Options();
	        opt2.inSampleSize = scale;
	        return BitmapFactory.decodeStream(act.getContentResolver().openInputStream(selectedImage), null, opt2);

	    }
}
