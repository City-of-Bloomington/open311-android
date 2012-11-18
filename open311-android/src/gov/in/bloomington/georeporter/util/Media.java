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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
	public static Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight, Context c) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    final String path = uri.toString();
	    
	    // First decode with inJustDecodeBounds=true to check dimensions
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
}
