package com.globalsoft.SapLibActivity.Contraints;

import java.io.Serializable;

import android.graphics.Bitmap;

public final class GalleryImageDetails implements Serializable{
	
	private long imageId;
	private String imageFullPath;
	private Bitmap imageBitmap;
	
	public GalleryImageDetails(){		
	}
	
	public GalleryImageDetails(Bitmap bmp, long id, String path){
		imageId = id;
		imageFullPath = path;
		imageBitmap = bmp;
	}
	
	public void setImageBitmap(Bitmap bmp){
		this.imageBitmap = bmp;
	}//fn setImageBitmap
	
	public void setImageId(long id){
		this.imageId = id;
	}//fn setImageBitmap
	
	public void setImageFullPath(String fullPath){
		this.imageFullPath = fullPath;
	}//fn setImageBitmap
	
	public Bitmap getImageBitmap(){
		return this.imageBitmap;
	}
	
	public long getImageId(){
		return this.imageId;
	}
	
	public String getImageFullPath(){
		return this.imageFullPath;
	}

}//End of class GalleryImageDetails
