package com.globalsoft.SalesPro;

import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SalesProIndex  extends Activity {
	protected int _splashTime = 2000; 	
	private Thread splashTread;
	private TextView tv;
	private ImageButton emailButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
	    SalesOrderProConstants.TITLE_DISPLAY_WIDTH = SalesOrderProConstants.getDisplayWidth(this);
	    SalesOrderProConstants.setWindowTitleTheme(this);
	    //setContentView(R.layout.splash);	
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.splash); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.app_name));		
		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}    	
	    final SalesProIndex sPlashScreen = this; 	    
	    // thread for displaying the SplashScreen
		splashTread = new Thread() {
	        @Override
	        public void run() {
	            try {	            	
	            	synchronized(this){
	            		
	            		wait(_splashTime);
	            		
	            	}	            	
	            } catch(InterruptedException e) {} 
	            finally {
	            	try{
	            		yield();
	            	}
	            	catch(Exception dgh){}
	                finish();	                
	                Intent i = new Intent();
	                i.setClass(sPlashScreen, SalesProActivity.class);
	        		startActivity(i);	                
	            }
	        }
	    };	    
	    splashTread.start();
	}
	
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	try{
		    	synchronized(splashTread){
		    		splashTread.notifyAll();
		    	}
	    	}
        	catch(Exception dgh){}
	    }
	    return true;
	}
}