package com.globalsoft.SapQueueProcessor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.SapQueueProcessorMainService;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class About extends Activity {   
	
	private Button qlbtn;      
	private WebView wv;	 
	private boolean AlertStr;
	private static final int MENU_LOGSLIST = Menu.FIRST;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.aboutview);	 					                
	        wv = (WebView) findViewById(R.id.wv);
	        wv.getSettings().setJavaScriptEnabled(true);
	        wv.loadUrl("file:///android_asset/about.html"); 

	        qlbtn = (Button) findViewById(R.id.qlbtn);
	        qlbtn.setOnClickListener(qlbtnListener); 
	        startGSMService();
	    } catch (Exception de) {
	    	SapGenConstants.showErrorLog("Error in About screen: "+de.toString());
	    }
    }
      

    private void startGSMService(){
    	try{     		   		
    		SapGenConstants.QueueserviceStatus=isMyServiceRunning(GSMService.class);
    		SapQueueProcessorHelperConstants.showLog("servStatus: "+SapGenConstants.QueueserviceStatus);
    		if(SapGenConstants.QueueserviceStatus){
    			stopBackgroundService();
    			Intent serIntent = new Intent(this, GSMService.class);  
        		this.startService(serIntent);       		
    		}else{
    			Intent serIntent = new Intent(this, GSMService.class);  
        		this.startService(serIntent); 
    		}
    	}
    	catch(Exception sgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in startBackgroundService : "+sgh.toString());
    	}
    }//fn startBackgroundService
   
    private void stopBackgroundService(){
    	try{
    		Intent serIntent = new Intent(this, GSMService.class);
    		this.stopService(serIntent);
    	}
    	catch(Exception ssgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in stopBackgroundService : "+ssgh.toString());
    	}
    }//fn stopBackgroundService
    
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }//
    
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_LOGSLIST, 0, getString(R.string.LOG_TEXTS));		
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		  super.onPrepareOptionsMenu(menu);
		  return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_LOGSLIST: {
				gotoLogsList();
		        break;
			}	
	    }
		return super.onOptionsItemSelected(item);
	}
	
	private void gotoLogsList(){
		try {
    		SapGenConstants.showLog("loglist btn clicked!");
        	Intent intent = new Intent(this, LogsList.class);
			startActivity(intent);
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog("Error in gotoLogsList:"+e.getMessage());
		}
	}//fn gotoColleagueList
	
    //queue list btn click listener
    private OnClickListener qlbtnListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	try{
        		SapGenConstants.showLog("queuelist btn clicked!");
        		Intent intent = new Intent(About.this, QueueList.class);
    			startActivity(intent);
        	}
        	catch(Exception e){
        		SapGenConstants.showErrorLog("Error in qlbtnListener"+e.toString());
        	}
        }
    };
}