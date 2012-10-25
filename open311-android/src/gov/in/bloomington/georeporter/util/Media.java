/**
 * Methods for effeciently handling images
 * 
 * Methods from Google's best practices for handling large bitmaps
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class Media {
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width  = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width  / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight, Context c) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    final String path = getPath(uri, c);
	    
	    // First decode with inJustDecodeBounds=true to check dimensions
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	private static String getPath(Uri uri, Context c) {
		String[] gallery = { MediaStore.Images.Media.DATA };
		Cursor cursor = c.getContentResolver().query(uri, gallery, null, null, null);
		cursor.moveToFirst();
		
		int index = cursor.getColumnIndex(gallery[0]);
		String path = cursor.getString(index);
		cursor.close();
		return path;
	}
}
