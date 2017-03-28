package com.globalsoft.ProductLib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductLibIndex extends Activity {	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	   
	    requestWindowFeature(Window.FEATURE_NO_TITLE); 	    
		int dispwidth = SapGenConstants.getDisplayWidth(this);	    	
		finish();	                
        Intent i = new Intent();        
        if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
            i.setClass(this, ProductMainScreenForTablet.class);
		}else{
	        i.setClass(this, ProductMainScreenForPhone.class);
		}
		startActivity(i);
	}
}