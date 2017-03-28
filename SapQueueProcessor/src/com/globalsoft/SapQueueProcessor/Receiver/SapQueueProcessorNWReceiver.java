package com.globalsoft.SapQueueProcessor.Receiver;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.Handler;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessor.GSMService;
import com.globalsoft.SapQueueProcessorHelper.SapQueueProcessorMainService;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorContentProvider;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBOperations;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SapQueueProcessorNWReceiver  extends BroadcastReceiver {
	
    private Context mContext;
    int processCount =0,rowcount=0, size=0,i=0;
    int k1 =0,k2 =0,k3=0,k4=0,k5=0,k6=0,k7=0;
    int countservice=0;
  //For Timer 
  	private TimerTask scanTask; 
  	private final Handler handler = new Handler(); 
  	private Timer t = new Timer();  
  	private AlertDialog alertDialog = null;
  	private boolean timerFlag = true; 
  	ArrayList<Integer> procescountList = new ArrayList<Integer>();
      
    public void onReceive(Context context, Intent intent) {
		try{			
			mContext = context;
			SapQueueProcessorHelperConstants.showLog("1");
			ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
			SapQueueProcessorHelperConstants.showLog("2");
	    	// ARE WE CONNECTED TO THE NET
	    	if (conMgr.getActiveNetworkInfo() != null
	    	&& conMgr.getActiveNetworkInfo().isAvailable()
	    	&& conMgr.getActiveNetworkInfo().isConnected()) {
	    		SapQueueProcessorHelperConstants.showLog("3");
	    		//rowcount = SapQueueProcessorDBOperations.getrowCount(mContext);
	    		if(SapGenConstants.CountService!=0){
	    			SapGenConstants.CountService=0;
	    		}	
	    		Thread t = new Thread() {
    	            public void run() {
            			try{
            				startGSMService();
            			} catch (Exception e) {
            				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
            			}        			                        
    				}
    			};
    	        t.start();	    		
	    		
	    		/*if(rowcount!=0){
	    			//processCount = SapQueueProcessorDBOperations.getProcessCount(mContext,1);
	    			procescountList=SapQueueProcessorDBOperations.getAllProcessCount(mContext);		    		
		    		size = procescountList.size();
		    		SapQueueProcessorHelperConstants.showLog("procescountList : "+procescountList);
		    		startBackgroundService();
		    		//setprocessAlarm();
		    			//dostarttimer();					
	    		}	*/    		    		    		
	    	} 
	    	else {
	    		//SapQueueProcessorHelperConstants.showLog("No Network Stop the Background Service!");
	    		stopBackgroundService();
	    		mContext.getContentResolver().registerContentObserver (SapQueueProcessorContentProvider.CONTENT_URI, true, new SapQueueProcessorDBContentObserver());
	    	}
		}
		catch(Exception sgh){
			SapQueueProcessorHelperConstants.showErrorLog("Error in onReceive : "+sgh.toString());
		}
	}//fn onReceive   
    
    private void startGSMService(){
    	try{     		   		
    		SapGenConstants.QueueserviceStatus=isMyServiceRunning(GSMService.class);
    		SapQueueProcessorHelperConstants.showLog("servStatus: "+SapGenConstants.QueueserviceStatus);
    		if(SapGenConstants.QueueserviceStatus){
    			stopGSMService();
    			Intent serIntent = new Intent(mContext, GSMService.class);  
        		mContext.startService(serIntent);         	
    		}else{
    			Intent serIntent = new Intent(mContext, GSMService.class);  
        		mContext.startService(serIntent); 
    		}
    	}
    	catch(Exception sgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in startBackgroundService : "+sgh.toString());
    	}
    }//fn startBackgroundService
    
    private void startBackgroundService(){
    	try{     		   		
    		SapGenConstants.QueueserviceStatus=isMyServiceRunning(SapQueueProcessorMainService.class);
    		SapQueueProcessorHelperConstants.showLog("servStatus: "+SapGenConstants.QueueserviceStatus);
    		if(SapGenConstants.QueueserviceStatus){
    			stopBackgroundService();
    			Intent serIntent = new Intent(mContext, SapQueueProcessorMainService.class);  
        		mContext.startService(serIntent);         		
    		}else{
    			Intent serIntent = new Intent(mContext, SapQueueProcessorMainService.class);  
        		mContext.startService(serIntent); 
    		}
    	}
    	catch(Exception sgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in startBackgroundService : "+sgh.toString());
    	}
    }//fn startBackgroundService
       
    private void stopBackgroundService(){
    	try{
    		Intent serIntent = new Intent(mContext, SapQueueProcessorMainService.class);
    		mContext.stopService(serIntent);
    	}
    	catch(Exception ssgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in stopBackgroundService : "+ssgh.toString());
    	}
    }//fn stopBackgroundService
    
    private void stopGSMService(){
    	try{
    		Intent serIntent = new Intent(mContext, GSMService.class);
    		mContext.stopService(serIntent);
    	}
    	catch(Exception ssgh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in stopBackgroundService : "+ssgh.toString());
    	}
    }//fn stopBackgroundService
	
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }//
        
    private class SapQueueProcessorDBContentObserver extends ContentObserver {

        public SapQueueProcessorDBContentObserver() {
            super(null);
            SapQueueProcessorHelperConstants.showLog("DB Content Observer Constructor Called");
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            SapQueueProcessorHelperConstants.showLog("DB Content Observer OnChange Called");
        }

    }//End of class SapQueueProcessorDBContentObserver
    
   
}//End of class SapQueueProcessorNWReceiver

