package com.globalsoft.SapLibActivity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalsoft.SapLibActivity.Contraints.GalleryImageDetails;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
 
public class GenActivityGallery extends Activity {
	
	private Gallery gallery;
	private ImageView galleryImgView;
	
	private GalleryImageDetails imageObj;
	private long imageId = -1;
	private String imagePath = "";
	private int selectedPos = -1;
	
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //setContentView(R.layout.actgallery);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.actgallery); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText("Image Gallery for Activities");

		int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
		if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
        
        SapGenConstants.showLog("imagesVect size : "+SapGenConstants.galleryVect.size());
        
        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView parent, View v, int position, long id) {
        		SapGenConstants.showLog("Selected Position : "+position);
        		showSelectedGalleryImage(position);
        	}
		});
        
        galleryImgView = (ImageView)findViewById(R.id.galleryimageview);
        galleryImgView.setOnClickListener(galleryViewListener);
    }
    
    
    private OnClickListener galleryViewListener = new OnClickListener(){
        public void onClick(View v) {
        	if(selectedPos >= 0)
        		showDeleteImageDialog(selectedPos);
        }
    };
    
    private void showSelectedGalleryImage(int pos){
    	try{
    		selectedPos = pos;
    		if(galleryImgView != null){
	    		if(SapGenConstants.galleryVect != null){
	    			if(pos < SapGenConstants.galleryVect.size()){
	    				imageObj = (GalleryImageDetails) SapGenConstants.galleryVect.elementAt(pos);
			        	if(imageObj != null){
			        		imageId = imageObj.getImageId();
			        		imagePath = imageObj.getImageFullPath();
			            	Bitmap oldBitmap = imageObj.getImageBitmap();
			            	if(oldBitmap != null){
			            		int orgWidth = oldBitmap.getWidth();
			                    int orgHeight = oldBitmap.getHeight();
			                    int newWidth = 360;
			                    int newHeight = 480;
			                    float xScale = newWidth/orgWidth;
			                    float yScale = newHeight/orgHeight;
			                     
			                    Matrix matrix = new Matrix();
			                    matrix.postScale(xScale, yScale);
			                    Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, orgWidth, orgHeight, matrix, true);
			            		if(newBitmap != null)
			            			galleryImgView.setImageBitmap(newBitmap);
			            		else
			            			galleryImgView.setImageBitmap(oldBitmap);
			            	}
			        	}
	    			}
	    		}
    		}
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error on showSelectedGalleryImage : "+sfg.toString());
    	}
    }//fn showSelectedGalleryImage
    
    
    private void showDeleteImageDialog(final int pos){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you really want to delete this image?");
		builder.setCancelable(false)
	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   if((imagePath != null) && (!imagePath.equalsIgnoreCase(""))){
	        		   deleteSelectedImage(pos);
	        	   }
	           }
	       })
	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
    }//fn showDeleteImageDialog
    
    /*
    private void showDeletionDialog(final int pos){
    	try{
    		SapGenConstants.showLog("pos :"+pos+" : "+SapGenConstants.galleryVect.size());
    		if(SapGenConstants.galleryVect != null){
    			if(pos < SapGenConstants.galleryVect.size()){
		    		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		    		View layout = inflater.inflate(R.layout.gallery_custom_dialog,
		    		                               (ViewGroup) findViewById(R.id.layout_root));
		
		    		TextView text = (TextView) layout.findViewById(R.id.dialogtext);
		    		text.setText("Do you really want to delete this image?");
		    		ImageView image = (ImageView) layout.findViewById(R.id.dialogimage);
		    		
		    		imageObj = (GalleryImageDetails) SapGenConstants.galleryVect.elementAt(pos);
		        	if(imageObj != null){
		        		imageId = imageObj.getImageId();
		        		imagePath = imageObj.getImageFullPath();
		            	Bitmap bm = imageObj.getImageBitmap();
		            	if(bm != null)
		            		image.setImageBitmap(bm);
		        	}
		
		        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    		builder.setView(layout);
		    		//builder.setMessage("Do you really want to delete this image?")
		    		builder.setCancelable(false)
		    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	        	   if(imageId >= 0){
		    	        		   deleteSelectedImage(pos);
		    	        	   }
		    	        	   if((imagePath != null) && (!imagePath.equalsIgnoreCase(""))){
		    	        		   deleteSelectedImage(pos);
		    	        	   }
		    	           }
		    	       })
		    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                dialog.cancel();
		    	           }
		    	       });
		    		AlertDialog alertDialog = builder.create();
					alertDialog.show();
    			}
    		}
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error on showDeletionDialog : "+sgh.toString());
    	}
    }//fn showDeletionDialog
    */
    
    private void deleteSelectedImage(int pos){
    	try{
			if(pos < SapGenConstants.galleryVect.size())
				SapGenConstants.galleryVect.removeElementAt(pos);
			/*
			ContentResolver cr = this.getContentResolver();
			int rows = cr.delete(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, android.provider.BaseColumns._ID + "=?", new String[]{ Long.toString(imageId) } );
			SapGenConstants.showLog("No of rows deleted : "+rows);*/
			
			SapGenConstants.showLog("In deletion filePath : "+imagePath);
			File newImgFile = new File(imagePath);
			if(newImgFile != null){
				SapGenConstants.showLog("Inside Not null");
				if(newImgFile.exists()){
					SapGenConstants.showLog("Inside exists");
					boolean delVal = newImgFile.delete();
					SapGenConstants.showLog("File Deleted : "+delVal);
				}
			}
    	}
    	catch(Exception asf){
    		SapGenConstants.showErrorLog("Error on deleteSelectedImage : "+asf.toString());
    	}
    	finally{
    		try {
				if(galleryImgView != null)
					galleryImgView.setImageBitmap(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		gallery.setAdapter(new ImageAdapter(this));
    	}
    }//fn deleteSelectedImage
    
    
    public class ImageAdapter extends BaseAdapter {
    	int mGalleryItemBackground;
        private Context myContext;
 
        /** Simple Constructor saving the 'parent' context. */
        public ImageAdapter(Context c) { 
        	this.myContext = c; 
        	TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
    	}
 
        /** Returns the amount of images we have defined. */
        public int getCount() { return SapGenConstants.galleryVect.size(); }
 
        /* Use the array-Positions as unique IDs */
        public Object getItem(int position) { return position; }
        public long getItemId(int position) { return position; }
 
        /** Returns a new ImageView to
         * be displayed, depending on
         * the position passed. */
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(this.myContext);
 
            try {
            	imageObj = (GalleryImageDetails) SapGenConstants.galleryVect.elementAt(position);
            	if(imageObj != null){
	            	Bitmap bm = imageObj.getImageBitmap();
	            	if(bm != null)
	            		i.setImageBitmap(bm);
            	}
            } catch (Exception sse) {
            	SapGenConstants.showLog("Image Exception : "+sse.toString());
            }
           
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setLayoutParams(new Gallery.LayoutParams(150, 120));
            //i.setLayoutParams(new Gallery.LayoutParams(136, 88));
            
            // The preferred Gallery item background
            i.setBackgroundResource(mGalleryItemBackground);
            return i;
        }
    }
    
}//End of class GenActivityGallery