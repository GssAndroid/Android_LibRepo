package com.globalsoft.ProductLib;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	public ImageView bmImage;
	public ImageLoader imageLoader;
	public Bitmap mIcon11 = null;      
	Context ctx = null;

    public DownloadImageTask(ImageView bmImage, Context ctx1) {
        this.bmImage = bmImage;
        ctx = ctx1;
		imageLoader = new ImageLoader(ctx);
    }

    protected Bitmap doInBackground(String... urls) {
        final String urldisplay = urls[0];
        try {
			try{
				Bitmap b = imageLoader.getCatchBmap(urldisplay);
				if(b != null){
					mIcon11 = b;
				}else{ 
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
	                if(mIcon11 != null){
	                	imageLoader.setCatchBmap(urldisplay, mIcon11);
	                }
				}
			} catch (Exception e) {
				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
			}   				            
        } catch (Exception rfr) {
        	mIcon11 = null;
			SapGenConstants.showErrorLog("Error in doInBackground : "+rfr.toString());
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
    	try{
	    	if(result != null){
	            bmImage.setImageBitmap(result);
	            
	            /*// Get scaling factor to fit the max possible width of the ImageView
	            float scalingFactor = this.getBitmapScalingFactor(result);
	            // Create a new bitmap with the scaling factor
	            Bitmap newBitmap = this.ScaleBitmap(result, scalingFactor);
	            // Set the bitmap as the ImageView source
	            bmImage.setImageBitmap(newBitmap);	*/            
	    	}else{
	            bmImage.setImageResource(R.drawable.default_img);
	    	}
	    } catch (Exception rfr) {
			SapGenConstants.showErrorLog("Error in doInBackground : "+rfr.toString());
	    }
    }
    
    private float getBitmapScalingFactor(Bitmap bm) {
        // Get display width from device
        int displayWidth = SapGenConstants.getDisplayWidth(ctx);

        // Get margin to use it for calculating to max width of the ImageView
        LinearLayout.LayoutParams layoutParams = 
            (LinearLayout.LayoutParams)this.bmImage.getLayoutParams();
        int leftMargin = layoutParams.leftMargin;
        int rightMargin = layoutParams.rightMargin;

        // Calculate the max width of the imageView
        int imageViewWidth = displayWidth - (leftMargin + rightMargin);

        // Calculate scaling factor and return it
        return ( (float) imageViewWidth / (float) bm.getWidth() );
    }
    
    private Bitmap ScaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }
}
