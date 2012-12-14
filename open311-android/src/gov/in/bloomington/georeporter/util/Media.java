/**
 * Methods for effeciently handling images
 * 
 * We need to save the full size images the user takes from the camera.
 * However, we want to downsize them before POST to the endpoint.
 * We store the Uri to a chosen image in the ServiceRequest,
 * then, when the user is ready to POST, we create a smaller bitmap
 * from the full size image and add the small bitmap to the multipart/form-data
 * 
 * Google recommendations for using the camera and grabbing pictures
 * http://developer.android.com/guide/topics/media/camera.html#saving-media
 *  
 * Methods from Google's best practices for handling large bitmaps
 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 * 
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;

public class Media {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    
    public static final int    UPLOAD_WIDTH  = 640;
    public static final int    UPLOAD_HEIGHT = 480;
    public static final String UPLOAD_FILENAME = "media.png"; 
    
    private static final String APP_NAME = "GeoReporter";
    
    /**
     * Create a file Uri for saving an image or video
     * 
     * @param type
     * @return
     * Uri
     */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    
    /**
     * Create a File for saving an image or video
     * 
     * @param type
     * @return
     * File
     */
    @SuppressLint("SimpleDateFormat")
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_NAME);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(APP_NAME, "failed to create media directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    
	/**
	 * Calculate a the sample size value based on a target width and height
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * int
	 */
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
	
	/**
	 * @param uri
	 * @param reqWidth
	 * @param reqHeight
	 * @param c
	 * @return
	 * Bitmap
	 */
	public static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight, Context c) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    
	    // First decode with inJustDecodeBounds=true to check dimensions
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * Returns the raw, absolute path for an internal Uri
	 *
	 * For images chosen from the gallery, the Uri must be looked up in the
	 * media database in order to get the actual file path.
	 * Taking a fresh image using the camera, the app must have created it's
	 * own, new file path for the image.  Fresh camera images will not be in 
	 * the media database.
	 * 
	 * Thread Warning:
	 * Converting from Uri to a real path requires a database cursor.
	 * This function cannot be called from an AsyncTask, as it does its own
	 * background processing.  It must be called from the main UI thread.
	 * 
	 * @param uri
	 * @param c
	 * @return
	 * String
	 */
	public static String getRealPathFromUri(Uri uri, Context c) {
	    // Check in the media database
	    try {
    	    String[] proj = { MediaStore.Images.Media.DATA };
    	    CursorLoader loader = new CursorLoader(c, uri, proj, null, null, null);
    	    Cursor cursor = loader.loadInBackground();
    	    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	    cursor.moveToFirst();
    	    return cursor.getString(index);
	    }
	    // Otherwise it must be a fresh camera image
	    // Just use the raw path from the Uri
	    catch (Exception e) {
	        return uri.getPath();
	    }
	}
}
