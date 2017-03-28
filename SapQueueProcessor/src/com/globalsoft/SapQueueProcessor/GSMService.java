package com.globalsoft.SapQueueProcessor;


import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.AppLocationService;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.SapQueueProcessorMainService;
import com.globalsoft.SapQueueProcessorHelper.Receiver.SapQueueProcessorNWReceiver;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class GSMService extends Service{

	private ProgressDialog pdialog = null;
	Context ctx;
	private boolean internetAccess=false;
	AppLocationService appLocationService;
 	private double latitide,longitude;
 	private final Handler ntwrkHandler = new Handler();
 	private StartNetworkTask soapTask = null;
 	private SoapObject resultSoap=null;
 	private boolean alarmflag=false;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	public void onDestroy() {
		super.onDestroy();		
	}
	
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SapQueueProcessorHelperConstants.showLog("</br>GSM Service has started");
		pingConnection();
	}

	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	private void pingConnection() {
		try{
			ctx = getApplicationContext();
			//internetAccess = SapGenConstants.checkConnectivityAvailable(ctx);	
			//if(internetAccess){
			appLocationService = new AppLocationService(
					ctx);
				Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
				if (nwLocation != null) {
					latitide = nwLocation.getLatitude();
					 longitude = nwLocation.getLongitude();
					Toast.makeText(
							getApplicationContext(),
							"Mobile Location (NW): \nLatitude: " + latitide
									+ "\nLongitude: " + longitude,
							Toast.LENGTH_LONG).show();
				} /*else {
					showSettingsAlert("NETWORK");
				}*/
				initPingConnection();
				
				//CHECK IF QUEUE PROCESSOR IS RUNNING								
			//}
		}catch(Exception eft){
			SapGenConstants.showErrorLog("Error in pingConnection : "+eft.toString());
		}				
	}//

	public void SetQueueProcessor() {
		//SET QUEUE PROCESSOR FLAG TO TRUE OR FALSE				
		//SapGenConstants.setQpFlag=true;     				
		//if(SapGenConstants.setQpFlag==true){
			startBackgroundService();
		//}
	}//
        
	private void startBackgroundService(){
    	try{        		
    		Intent serIntent;
    		SapGenConstants.QueueserviceStatus=isMyServiceRunning(SapQueueProcessorMainService.class);
    		SapGenConstants.showLog("servStatus: "+SapGenConstants.QueueserviceStatus);
    		if(SapGenConstants.QueueserviceStatus){
    			if(alarmflag){
    				
    			}else{
    				SapQueueProcessorHelperConstants.showLog("</br>Queue Processor is already running and Alarm of 15min has been Set");
    			}
   			
    			Thread t = new Thread() {
    	            public void run() {
            			try{
            				setAlarm(900);
            			} catch (Exception e) {
            				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
            			}        			                        
    				}
    			};
    	        t.start();
    		}else{
    			SapQueueProcessorHelperConstants.showLog("</br>Queue Processor is Enabled and the Queue Processor Main Service has been Started");
    			 serIntent = new Intent(ctx, SapQueueProcessorMainService.class);  
    			ctx.startService(serIntent); 
    			//Set timer here for 15min for QueueProcessor main service
    		}
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in startBackgroundService : "+sgh.toString());
    	}
    }//fn startBackgroundService
	

	private void setAlarm(int time){
		try{
			 //ALARM  
			alarmflag =true;
			SapQueueProcessorHelperConstants.showLog("</br></br>Alarm is set for	: "+time+"Seconds");
		   	int integersec =time;
				 //int integersec = Integer.parseInt(text.toString());
		   	SapGenConstants.showErrorLog("1");
				 Intent intent2 = new Intent(GSMService.this,SapQueueProcessorNWReceiver.class);
				    PendingIntent pendingIntent = PendingIntent.getBroadcast(GSMService.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
				    AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);		   
				    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				        + (integersec * 1000), pendingIntent);   
					SapGenConstants.showErrorLog("2");
					//SapQueueProcessorHelperConstants.showLog("Alarm is set for	: "+integersec+"Seconds");
				 /*   Toast.makeText(this, "Alarm set in " + integersec + " seconds",
				        Toast.LENGTH_LONG).show();  */
					SapGenConstants.showErrorLog("3");
		}catch(Exception sgh2){
    		SapGenConstants.showErrorLog("Error in setAlarm : "+sgh2.toString());
    	}
	   	
	   }///
	
	 private void stopBackgroundService(){
	    	try{
	    		Intent serIntent = new Intent(ctx, SapQueueProcessorMainService.class);
	    		ctx.stopService(serIntent);
	    	}
	    	catch(Exception ssgh){
	    		SapGenConstants.showErrorLog("Error in stopBackgroundService : "+ssgh.toString());
	    	}
	    }//fn stopBackgroundService
		
	 
	private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }//
	
	 private void initPingConnection(){  
		 SapQueueProcessorHelperConstants.showLog("</br></br>GSM Pinging the Server");
	        SoapSerializationEnvelope envelopeC = null;
	        try{
	            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
	            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            
	            SapGenIpConstraints C0[];
	            C0 = new SapGenIpConstraints[4];           
	            for(int i=0; i<C0.length; i++){
	                C0[i] = new SapGenIpConstraints(); 
	            }	                        
	            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this,SapGenConstants.APPLN_NAME_STR_SALESPRO)+":GLTTD:"+latitide+":GLNGTD:"+longitude+":";
	            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
	            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
	            C0[2].Cdata = "EVENT[.]PING-SERVER[.]VERSION[.]0";
	           // C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+CustomerStr;
	        
	            Vector listVect = new Vector();
	            for(int k=0; k<C0.length; k++){
	                listVect.addElement(C0[k]);
	            }        
	            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
	            envelopeC.setOutputSoapObject(request);                    
	            SapGenConstants.showLog(request.toString());

	           // respType = SOAP_CONN_CUSTOMER;
	            doThreadNetworkAction(ctx.getApplicationContext(), ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
	        }
	        catch(Exception asd){
	        	SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
	        }
	    }//fn initPriceSoapConnection
	 
	 final Runnable getNetworkResponseRunnable = new Runnable(){
		    public void run(){
		    	try{		    				    	
		    			if(pdialog != null)
	            			pdialog = null;	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(ctx.getApplicationContext(), "", "Sending Geo Location of device to SAP,Please wait..",true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				
	    	            				//updateServerResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
		    		
		    	} catch(Exception e){
		    		e.printStackTrace();
		    	}
		    }	    
	    };
	    
	 private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, SoapObject request){
	    	try{
	    		soapTask = new StartNetworkTask(ctx.getApplicationContext(),false);
	    		Thread t = new Thread() {
		            public void run() {
	        			try{	        				
	        	            resultSoap = soapTask.execute(envelopeC).get();
	        	            SetQueueProcessor();
	        			} catch (Exception e) {
	        				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
	        			}        			
	                    handler.post(handlerFnName);
					}
				};
		        t.start();
	    	}
	    	catch(Exception asgg){
	    		SapGenConstants.showErrorLog("Error in doThreadNetworkAction : "+asgg.toString());
	    	}
	    }//fn doThreadNetworkAction   
	    
	 
}//GSMService
