package com.globalsoft.SapQueueProcessorHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;

import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorContentProvider;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBConstants;
import com.globalsoft.SapQueueProcessorHelper.Receiver.SapQueueProcessorNWReceiver;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;


public class SapQueueProcessorMainService extends Service {

	private int colId = -1,processcount =-1;
	private SoapObject soapObj = null;
	private String appRefId = "", appNameStr = "", packNameStr = "", classNameStr = "", apiNameStr = "",altid = "",platform="",msgType="",msgStr=""; 
	private long processStartTime = 0;
	private boolean reScheduleFlag = false;	
	private int counter;
	private HashMap<String, String> QueueDetail = new HashMap<String, String>();
	private PendingIntent contentIntent;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private Context ctx;
	private ArrayList<Integer> idList = new ArrayList<Integer> ();
	
	private static SQLiteDatabase sqlitedatabase;
	
	
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	public void onDestroy() {
		super.onDestroy();
		reScheduleFlag = false;
	}
	
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		ctx = getApplicationContext();
		processStartTime = System.currentTimeMillis();
		//SapQueueProcessorHelperConstants.showLog("</br>"+new Date(processStartTime));
		SapQueueProcessorHelperConstants.showLog("</br>processStartTime in onStart  : "+processStartTime);
		SapGenConstants.CountService++;
		//deleteUnknownDB();
		readALLData();//ADDED BY SOWMYA
		//deleteAllNavigatedData();
		deleteAllRescheduledData();
		deleteAlloldData();
		updateProcessStartTime();
		readAllDataFromDB();
	}//	onStart

	

	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	private void sendResultToCorrespondingApp(SoapObject resultSoap){
		try{
			if(resultSoap != null){
				SapQueueProcessorHelperConstants.showLog("</br> beginning packNameStr: "+packNameStr+"  appRefId:"+appRefId+"  classNameStr:"+classNameStr);
				SapQueueProcessorHelperConstants.showLog("</br> beginning packNameStr: "+SapGenConstants.packagename+"  appRefId:"+SapGenConstants.apprefid+"  classNameStr:"+SapGenConstants.classname );
				if((!SapGenConstants.packagename.equalsIgnoreCase("")) && (!SapGenConstants.appname.equalsIgnoreCase(""))){//ALTERED BY SOWMYA
	            	//if(SapGenConstants.platform.equalsIgnoreCase("phonegap")){
					if(!packNameStr.equalsIgnoreCase(null)){
	            		UpdateResultSoaptoDB(resultSoap);	            		
	            		Intent sendIntent = new Intent();
	            		sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            	SapGenConstants.showLog("</br></br>packNameStr: "+SapGenConstants.packagename+"  classNameStr:"+SapGenConstants.classname );//ALTERED BY SOWMYA
		            	SapGenConstants.showLog("1");
						ComponentName  cn = new ComponentName(SapGenConstants.packagename, SapGenConstants.classname );
		            	SapGenConstants.showLog("2");
						sendIntent.setComponent(cn);
		            	SapGenConstants.showLog("3");
						/*SapQueueProcessorHelperConstants.showLog("</br>packNameStr: "+SapGenConstants.packagename+"  appRefId:"+appRefId+"  classNameStr:"+classNameStr);
						SapQueueProcessorHelperConstants.showLog("</br>colId: "+colId+"  appRefId:"+appRefId+"  appNameStr:"+appNameStr);
						SapQueueProcessorHelperConstants.showLog("</br>apiNameStr: "+SapGenConstants.apiname);//ALTERED BY SOWMYA
*/						sendIntent.putExtra("fromNotification", true);		
						sendIntent.putExtra("Platform", SapGenConstants.platform);		
						sendIntent.putExtra("MessageType", msgType);						
						sendIntent.putExtra("ColumnId", SapGenConstants.colId);
						sendIntent.putExtra("ApprefId", appRefId);
						sendIntent.putExtra("MsgStr", SapGenConstants.SOAP_RESP_MSG);
						sendIntent.putExtra("AltIdStr", SapGenConstants.altID);
						sendIntent.putExtra("SendNotflag", SapGenConstants.NotificationFlag);
						
						//sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_APPLNAME, SapGenConstants.appname);//ALTERED BY SOWMYA
						//sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_SOAPAPINAME, SapGenConstants.apiname); //ALTERED BY SOWMYA
						//sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_RESULTSOAPOBJ, SapQueueProcessorHelperConstants.getSerializableSoapObject(resultSoap));
		            	SapGenConstants.showLog("4");
						/*if(resultSoap != null)
							sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_REQUESTSOAPOBJ, SapQueueProcessorHelperConstants.getSerializableSoapObject(soapObj));*/
		            	SapGenConstants.showLog("5");
		            	//if()
		            	startActivity(sendIntent);
	            	}else{
	            		Intent sendIntent = new Intent();
		            	SapGenConstants.showLog("</br></br>packNameStr: "+SapGenConstants.packagename+"  classNameStr:"+SapGenConstants.classname );//ALTERED BY SOWMYA		            	
						ComponentName  cn = new ComponentName(SapGenConstants.packagename, SapGenConstants.classname );		            	
						sendIntent.setComponent(cn);		            	
						SapQueueProcessorHelperConstants.showLog("</br>packNameStr: "+SapGenConstants.packagename+"  appRefId:"+appRefId+"  classNameStr:"+classNameStr);
						SapQueueProcessorHelperConstants.showLog("</br>colId: "+colId+"  appRefId:"+appRefId+"  appNameStr:"+appNameStr);
						SapQueueProcessorHelperConstants.showLog("</br>apiNameStr: "+SapGenConstants.apiname);//ALTERED BY SOWMYA
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_COLID, colId);
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_APPREFID, appRefId);
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_ALT_ID, SapGenConstants.altID );
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_APPLNAME, SapGenConstants.appname);//ALTERED BY SOWMYA
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_SOAPAPINAME, SapGenConstants.apiname); //ALTERED BY SOWMYA
						sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_RESULTSOAPOBJ, SapQueueProcessorHelperConstants.getSerializableSoapObject(resultSoap));
		            
						if(soapObj != null)
							sendIntent.putExtra(SapQueueProcessorHelperConstants.QUEUE_REQUESTSOAPOBJ, SapQueueProcessorHelperConstants.getSerializableSoapObject(soapObj));		            	
		            	startService(sendIntent);
	            	}
					
					/*if(startService(sendIntent) != null){
		            	SapGenConstants.showLog("Service running");
					}else{
						SapGenConstants.showLog("Service not running");
					}*/
	            	SapGenConstants.showLog("6");
	            	
				}
				else
					SapQueueProcessorHelperConstants.showLog("</br>Package name or Class name Missing ");
			}
			else{				
				SapQueueProcessorHelperConstants.showLog("</br> beginning packNameStr: "+packNameStr+"  appRefId:"+appRefId+"  classNameStr:"+classNameStr);				
				SapQueueProcessorHelperConstants.showLog("</br>Error in soap response resultSoap is null");
			}	
		}
		catch(Exception asf){
			SapQueueProcessorHelperConstants.showErrorLog("</br>Error in sendResultToCorrespondingApp : "+asf.toString());
		}
		finally{	
			UpdateStatus();
			idList= getIdfromQPTable(ctx);
			int index = idList.size()-1;
			int colid = idList.get(index);
			SapQueueProcessorHelperConstants.showLog("SapGenConstants.colId: "+SapGenConstants.colId);
			if(SapGenConstants.colId==colid){
				//setAlarm(900);
			}else{
				readAllDataFromDB();
			}
			
				/*//GET ALL IDS FROM QP//
		
				idList= getIdfromQPTable(ctx);
				SapQueueProcessorHelperConstants.showLog("idList size"+idList.size());
				int index = idList.size()-1;
				int colid = idList.get(index);
				if(SapGenConstants.colId<=colid && processcount==0 ){
					readAllDataFromDB();					
				}else if(SapGenConstants.colId<=colid){
					processRescheduleData();					
				}else{
					setAlarm(900);
				}
				*/
		}
	}//fn sendResultToCorrespondingApp
	
	/*private void deleteUnknownDB() {
		File dbFile = getDatabasePath("webview.db");
		SapQueueProcessorHelperConstants.showLog("Absolute path of webview database : "+dbFile.getAbsolutePath());
		String name = dbFile.getPath();
		boolean delStatus = ctx.deleteDatabase(name);
		SapQueueProcessorHelperConstants.showLog("delStatus : "+delStatus);
	}//
*/	
	public void UpdateStatus(){
		try{
			if(reScheduleFlag){
				if(colId >= 0)
					updateSelectedRowStatus(colId, "", SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER);
				
				//readAllDataFromDB();
			}
			else{
				if(colId >= 0){
					updateRescheduleProcessTime(colId);
					//processRescheduleData();
				}
			}
		}
		catch(Exception asf){}		
	}//UpdateStatus
	
	
	private void UpdateResultSoaptoDB(SoapObject resultSoap2) {
		Uri uri =null;
		String whereStr=null;
		String[] whereParams=null;
		byte[] soapBytes = null;
		try{
			 soapBytes = SapQueueProcessorHelperConstants.getSerializableSoapObject(resultSoap2);
			 uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			
			ContentValues updateContent = new ContentValues();
			updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA_RESPONSE, soapBytes);					
			ContentResolver resolver = this.getContentResolver();
			
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG + " = ? ";
			whereParams = new String[]{"Cphonegap"}; 			
			
			int rows = resolver.update(uri, updateContent, whereStr, whereParams);
			SapGenConstants.showLog("Rows updated for phoengap"+rows);
		}catch(Exception asf){
			SapQueueProcessorHelperConstants.showErrorLog("</br>Error in UpdateResultSoaptoDB : "+asf.toString());
		}					      		
	}//

	private void setAlarm(int time){
		try{
			 //ALARM   	
			SapQueueProcessorHelperConstants.showLog("</br></br>Alarm is set for	: "+time+"Seconds");
		   	int integersec =time;
				 //int integersec = Integer.parseInt(text.toString());
		   	SapGenConstants.showErrorLog("1");
				 Intent intent2 = new Intent(ctx,SapQueueProcessorNWReceiver.class);
				    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
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
	
	 private boolean isMyServiceRunning(Class<?> serviceClass) {
	        ActivityManager manager = (ActivityManager)SapQueueProcessorMainService.this.getSystemService(Context.ACTIVITY_SERVICE);
	        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	            if (serviceClass.getName().equals(service.service.getClassName())) {
	                return true;
	            }
	        }
	        return false;
	    }//
	 
    SoapObject resultSoap = null;
   // final Handler handler = new Handler();
    SoapSerializationEnvelope envelopeC = null;
    private void initQueueSoapConnection(){        
        final StartNetworkTask soapTask = new StartNetworkTask(this,true);
        try{             
            if(soapObj != null){ 
            	envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);         
	            envelopeC.setOutputSoapObject(soapObj); 
	            SapQueueProcessorHelperConstants.showLog(soapObj.toString());
	            resultSoap = soapTask.execute(envelopeC).get();
	            SapGenConstants.showLog("executed the request");
	            if(resultSoap != null){
	            	SapGenConstants.showLog("Inside resultsoap if statment");
	            	SapGenConstants.soapResponse(SapQueueProcessorMainService.this, resultSoap, false);
	            	 msgStr = SapGenConstants.SOAP_RESP_MSG;
	            	 msgType = SapGenConstants.SOAP_ERR_TYPE;
	            	SapQueueProcessorHelperConstants.showLog("Soap Result : "+resultSoap.toString());
	            	SapQueueProcessorHelperConstants.showLog("</br><span style='color: red;'> Message : "+msgStr+"</span>");
	            	SapQueueProcessorHelperConstants.showLog("</br><span style='color: red;'> Type : "+msgType+"</span>");
	            	if(!msgType.equalsIgnoreCase("S")){
	            		UpdateNotifyTableWithResponse();
	            	}else if(msgType.equalsIgnoreCase("S")) {
	            		SapGenConstants.NotificationFlag = true;
	            	}
	            	sendResultToCorrespondingApp(resultSoap);
	            	//UpdateStatus();
	    		}else{
	            	SapQueueProcessorHelperConstants.showLog("</br>Soap Result : resultSoap is null");
					updateSelectedRowStatus(SapGenConstants.colId, "", SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER);
	    		}
            	/*Thread t = new Thread() { ALTERED BY SOWMYA
    	            public void run() {
            			try{
            				envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);         
            	            envelopeC.setOutputSoapObject(soapObj); 
            	            resultSoap = soapTask.execute(envelopeC).get();
            			} catch (Exception e) {
            				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
            			}        			
                        handler.post(handlerFnName);
    				}
    			};
    	        t.start();*///ALTERED BY SOWMYA
            }
            else
            	SapQueueProcessorHelperConstants.showLog("</br>SoabOject is null");
        }
        catch(Exception asd){
        	SapQueueProcessorHelperConstants.showErrorLog("</br>Error in initQueueSoapConnection : "+asd.toString());
        }
    }//fn initQueueSoapConnection
	
    private void UpdateNotifyTableWithResponse() {
		try{
			
			SapQueueProcessorHelperConstants.showLog("SapGenConstants.altID: "+SapGenConstants.altID);
			//Check if the Alternat id present in the table//
			boolean checkAltID = SapQueueProcessorHelperConstants.CheckforId(ctx,SapGenConstants.altID);	
			SapQueueProcessorHelperConstants.showLog("checkAltID: "+checkAltID);
			if(!checkAltID){
				SapQueueProcessorHelperConstants.InsertNotifyData(ctx,appRefId,SapGenConstants.altID,msgStr,"");
				SapGenConstants.NotificationFlag = true;
			}else{
				//update the error message to the current alternative id//
				String errordesc = SapQueueProcessorHelperConstants.readErrorDescString(ctx,SapGenConstants.altID);
				SapQueueProcessorHelperConstants.showLog("errordesc: "+errordesc);
				if(errordesc.equalsIgnoreCase(msgStr)){
					SapQueueProcessorHelperConstants.showLog("//*****//Error descriptions are Same//***//");
					SapGenConstants.NotificationFlag = false;
				}else{
					//update the new error description here//**************//
					SapQueueProcessorHelperConstants.showLog("//*****//Updating new Error descriptions//***//");
					SapGenConstants.NotificationFlag = true;
					SapQueueProcessorHelperConstants.UpdateforErrorDesc(ctx,SapGenConstants.altID,errordesc);
					SapQueueProcessorHelperConstants.showLog("//*****//Updated new Error description.//***//");
				}
			}		
		} catch(Exception asd2){
        	SapQueueProcessorHelperConstants.showErrorLog("</br>Error in UpdateNotifyTableWithResponse : "+asd2.toString());
        }    	
	}//UpdateNotifyTableWithResponse

	private void readALLData() {
		Cursor cursor = null;    	
    	int rowCount = 0;		
    	try{   	
    		resetContentValues();
    		SapQueueProcessorHelperConstants.showLog("All data read from DB	: ");
    		Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");			
			ContentResolver resolver = this.getContentResolver();													
			cursor = resolver.query(uri, null, null, null, null);			
			rowCount = cursor.getCount();
			SapQueueProcessorHelperConstants.showLog("rowCount in readALLData	: "+rowCount);
			
			if(rowCount <= 0){
				return;
			}
			
			int idIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID);
			int apprefIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID);
			int appnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME);
			int packnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME);
			int classnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME);
			int funcnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME);
			int valueIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA);
			int statusIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS);
			int processcount = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT);
			
			
			cursor.moveToFirst();
			do{
				colId = cursor.getInt(idIndex);
				appRefId = cursor.getString(apprefIndex);
				appNameStr = cursor.getString(appnameIndex);
				packNameStr = cursor.getString(packnameIndex);
				classNameStr = cursor.getString(classnameIndex);
				apiNameStr = cursor.getString(funcnameIndex);
				int processcount2 = cursor.getInt(processcount);
				
				int status = cursor.getInt(statusIndex);
				byte[] soapBytes = cursor.getBlob(valueIndex);
				
				SapQueueProcessorHelperConstants.showLog("Id : "+colId+" appRefId: "+appRefId+" : "+appNameStr+" : "+packNameStr+" Status: "+status+"Process Count:"+processcount2);
				SapQueueProcessorHelperConstants.showLog("Class Name : "+classNameStr+" : "+apiNameStr);
				
				cursor.moveToNext();
				//break;
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in readALLData : "+sfg.toString());
    		resetContentValues();
    	}    	   			
	}//
    
    private void deleteAllNavigatedData() {
		Uri uri = null;
		String whereStr = null;
		String[] whereParams = null;
    	try{
    		uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI.toString());
			
			//Get the Resolver
			ContentResolver resolver = this.getContentResolver();
			
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_NOTIFY_FLAG + " = ? ";
			whereParams = new String[]{"true"}; 
			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, whereStr, whereParams);
			
			SapQueueProcessorHelperConstants.showLog("</br>deleteAllNavigatedData Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in deleteAllRescheduledData : "+sggh.toString());
    	}
		
	}//deleteAllNavigatedData
    
	/*final Runnable handlerFnName = new Runnable(){
	    public void run(){
	    	try{
	    		if(resultSoap != null){
	            	SapGenConstants.soapResponse(SapQueueProcessorMainService.this, resultSoap, false);
	            	String msgStr = SapGenConstants.SOAP_RESP_MSG;
	            	String msgType = SapGenConstants.SOAP_ERR_TYPE;
	            	//SapQueueProcessorHelperConstants.showLog("Soap Result : "+resultSoap.toString());
	            	SapQueueProcessorHelperConstants.showLog("</br><span style='color: red;'> Message : "+msgStr+"</span>");
	            	SapQueueProcessorHelperConstants.showLog("</br><span style='color: red;'> Type : "+msgType+"</span>");
	            	sendResultToCorrespondingApp(resultSoap);
	    		}else{
	            	SapQueueProcessorHelperConstants.showLog("</br>Soap Result : resultSoap is null");
					updateSelectedRowStatus(colId, "", SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER);
	    		}
	    	} catch(Exception edd){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+edd.toString());
	    	}
	    }	    
    };*/
    
    private void deleteAlloldData(){
    	Uri uri = null;
		String whereStr = null;
		String[] whereParams = null;
    	try{
    		uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI.toString());
			
			//Get the Resolver
			ContentResolver resolver = this.getContentResolver();
			SapQueueProcessorHelperConstants.showLog("STATUS_COMPLETED : "+SapQueueProcessorHelperConstants.STATUS_COMPLETED);
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " = ? AND "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME+ " < ?";
			whereParams = new String[]{String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED), String.valueOf(processStartTime)}; 
			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, whereStr, whereParams);
			
			SapQueueProcessorHelperConstants.showLog("</br>AlloldData Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in deleteAlloldData : "+sggh.toString());
    	}
    }//fn deleteAlloldData
       
    private void deleteAllRescheduledData(){
    	Uri uri = null,uri2 = null;
		String whereStr = null,whereStr1 =null;
		String[] whereParams = null,whereParams1 =null;
		Cursor cursor = null;
		String altid ="";
		ArrayList<String> altidList = new ArrayList<String>();
    	try{
    		uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI.toString());
			
			//Get the Resolver
			ContentResolver resolver = this.getContentResolver();
			
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " = ? AND "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT+ " > ?";
			whereParams = new String[]{String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER), String.valueOf(SapQueueProcessorHelperConstants.MAX_PROCESS_COUNT)}; 
			//CODE BLOCK TO GET THE DELETED ROW DETAILS TO MAP THE DETAILS TO NOTIFICATION TABLE//
			 cursor = resolver.query(uri, null, whereStr, whereParams, null);
			 if(cursor.getCount() == 0){
					return;
				}           
			 cursor.moveToFirst();
             do{
 				 int altidindex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID);
 				 altid = cursor.getString(altidindex);				
 				 altidList.add(altid);
 				 SapQueueProcessorHelperConstants.showLog("altidList : "+altidList);										
 				cursor.moveToNext();
 			}while(!cursor.isAfterLast());      
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, whereStr, whereParams);
			SapQueueProcessorHelperConstants.showLog("</br>AllRescheduledData Rows Deleted : "+rows);
			
			//DELETE THE ROWS IN NOTIFICATION TABLE//********************//
			uri2 = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver2 = this.getContentResolver();
						
			for(int i = 0 ; i < altidList.size();i++)
			{
				whereStr1 = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID + " = ? ";
				whereParams1 = new String[]{String.valueOf(altidList.get(i).toString())};
				int rows2 =resolver2.delete(uri2,whereStr1,whereParams1);
				SapQueueProcessorHelperConstants.showLog("</br>AllNotification Table Rows Deleted : "+rows2);
			}
			//DELETE THE ROWS IN NOTIFICATION TABLE//********************//
    	}
    	catch(Exception sggh){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in deleteAllRescheduledData : "+sggh.toString());
    	}
    }//fn deleteAllRescheduledData
    
    private void updateProcessStartTime(){
		Uri uri = null;
		String whereStr = null;
		String[] whereParams = null;
		try{
			uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			
			ContentValues updateContent = new ContentValues();
			updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME, processStartTime);					
			ContentResolver resolver = this.getContentResolver();
			
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " = ? ";
			whereParams = new String[]{String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER)}; 			
			
			int rows = resolver.update(uri, updateContent, whereStr, whereParams);
			
			SapQueueProcessorHelperConstants.showLog("</br>ProcessStartTime Row Updated : "+rows);
		}
		catch(Exception qsewe){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in updateProcessStartTime : "+qsewe.toString());
    	}	
	}//fn updateProcessStartTime
    

	private void updateRescheduleProcessTime(int colId){
		Uri uri = null;
		String whereStr = null;
		String[] whereParams = null;
		try{
			if(colId > 0){
				uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"/"+colId);
				
				ContentValues updateContent = new ContentValues();				
				updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME, processStartTime);								
				updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS, SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER);
				
				ContentResolver resolver = this.getContentResolver();
				int rows = resolver.update(uri, updateContent, whereStr, whereParams);
				
				SapQueueProcessorHelperConstants.showLog("</br>RescheduleProcessTime Row Updated : "+rows);
			}
		}			
		catch(Exception qsewe){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in updateRescheduleProcessTime : "+qsewe.toString());
    	}	
	}//fn updateRescheduleProcessTime
    
    
	private void updateSelectedRowStatus(int colId, String appName, int status){
		Uri uri = null;
		String whereStr = null;
		String[] whereParams = null;
		try{
			if(colId > 0)
				uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"/"+colId);
			else{
				uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
				if(appName == null)
					appName = "";
				
				/*whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME + " = ? And "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " = ?";
				whereParams = new String[]{appName, String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE)}; */
				whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME + " = ? And "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " = ? OR"+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " = ?";
				whereParams = new String[]{appName, String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE), String.valueOf(SapQueueProcessorHelperConstants.STATUS_INPROCESS)};
			}
			
			if(status < SapQueueProcessorHelperConstants.STATUS_IDLE)
				status = SapQueueProcessorHelperConstants.STATUS_IDLE;
			else if(status > SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY)
				status = SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY;
			
			ContentValues updateContent = new ContentValues();
			updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS, status);
			
			ContentResolver resolver = this.getContentResolver();
			int rows = resolver.update(uri, updateContent, whereStr, whereParams);
			
			//SapQueueProcessorHelperConstants.showLog("updateSelectedRowStatus Row Updated : "+rows);
		}
		catch(Exception qsewe){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in updateSelectedRowStatus : "+qsewe.toString());
    	}	
	}//fn updateSelectedRowStatus
	
	
	private void readAllDataFromDB(){
		File dbFile = getDatabasePath("queueprocessor_db");
		SapQueueProcessorHelperConstants.showLog("Absolute path of database : "+dbFile.getAbsolutePath());
		
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	reScheduleFlag = false;
    	int rowCount = 0;
    	try{
    		resetContentValues();
    		
    		Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			
			ContentResolver resolver = this.getContentResolver();			
			
			selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " = ? OR "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " = ? OR "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " = ?";
			selectionParams = new String[]{String.valueOf(SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY), String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE), String.valueOf(SapQueueProcessorHelperConstants.STATUS_INPROCESS)}; 
			
			String orderBy = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+" desc, "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" asc limit 1";
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			SapQueueProcessorHelperConstants.showLog("No of rows read : "+cursor.getCount());
			SapQueueProcessorHelperConstants.showLog("selection	: "+selection);
			SapQueueProcessorHelperConstants.showLog("selectionParams	: "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY)+"  "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE));
			SapQueueProcessorHelperConstants.showLog("orderBy	: "+orderBy);

			rowCount = cursor.getCount();
			if(rowCount <= 0){
				return;
			}
			
			int idIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID);
			int apprefIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID);
			int appnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME);
			int packnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME);
			int classnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME);
			int funcnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME);
			int valueIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA);
			int altidIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID);
			int pltfrmIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG);
			int plrcesscountIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT);
			
			
			cursor.moveToFirst();
			do{
				colId = cursor.getInt(idIndex);
				appRefId = cursor.getString(apprefIndex);
				appNameStr = cursor.getString(appnameIndex);
				packNameStr = cursor.getString(packnameIndex);
				classNameStr = cursor.getString(classnameIndex);
				apiNameStr = cursor.getString(funcnameIndex);
				altid = cursor.getString(altidIndex);
				byte[] soapBytes = cursor.getBlob(valueIndex);
				platform =  cursor.getString(pltfrmIndex);
				processcount =  cursor.getInt(plrcesscountIndex);
				
				SapQueueProcessorHelperConstants.showLog("</br>Id : "+colId+" appRefId: "+appRefId+" : "+appNameStr+" : "+packNameStr);
				SapGenConstants.colId = colId;
				SapGenConstants.apprefid = appRefId;
				SapGenConstants.appname = appNameStr;
				SapGenConstants.packagename = packNameStr;
				SapGenConstants.apiname = apiNameStr;
				SapGenConstants.classname = classNameStr;
				SapGenConstants.altID = altid;
				SapGenConstants.platform = platform;
				SapQueueProcessorHelperConstants.showLog("</br>Class Name : "+classNameStr+" : "+apiNameStr+":"+SapGenConstants.platform);
				
				if(soapBytes != null)
					soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in readSoapFromDB : "+sfg.toString());
    		resetContentValues();
    	}
    	finally{
			if(cursor != null)
				cursor.close();			
			
			if(rowCount > 0){
				if(colId > 0)
					updateSelectedRowStatus(colId, "", SapQueueProcessorHelperConstants.STATUS_INPROCESS);
				
				initQueueSoapConnection();
			}
			else{
				SapQueueProcessorHelperConstants.showLog("</br>End of Normal Queue Processing .......");
				SapQueueProcessorHelperConstants.showLog("</br>******************************************");				 
				processRescheduleData();								    
			}
    	}
    }//fn readAllDataFromDB

	private void processRescheduleData(){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	reScheduleFlag = true;
    	int rowCount = 0;
		SapQueueProcessorHelperConstants.showLog("</br> processRescheduleData .......");
    	try{
    		resetContentValues();
    		
    		Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			
			ContentResolver resolver = this.getContentResolver();

			SapQueueProcessorHelperConstants.showLog("</br>SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME: "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME);
			SapQueueProcessorHelperConstants.showLog("</br>processStartTime in processRescheduleData  : "+processStartTime);
			
			selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " =? AND "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME+ " <=?";
			selectionParams = new String[]{String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER), String.valueOf(processStartTime)}; 
			
			String orderBy = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" asc limit 1";
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			SapQueueProcessorHelperConstants.showLog("selection	: "+selection);
			SapQueueProcessorHelperConstants.showLog("selectionParams	: "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER)+"  "+String.valueOf(processStartTime));
			SapQueueProcessorHelperConstants.showLog("orderBy	: "+orderBy);
			rowCount = cursor.getCount();
			
			if(rowCount <= 0){
				return;
			}
			
			int idIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID);
			int apprefIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID);
			int appnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME);
			int packnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME);
			int classnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME);
			int funcnameIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME);
			int valueIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA);
			int altidIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID);
			int pltfrmIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG);
			
			cursor.moveToFirst();
			do{
				colId = cursor.getInt(idIndex);
				appRefId = cursor.getString(apprefIndex);
				appNameStr = cursor.getString(appnameIndex);
				packNameStr = cursor.getString(packnameIndex);
				classNameStr = cursor.getString(classnameIndex);
				apiNameStr = cursor.getString(funcnameIndex);
				byte[] soapBytes = cursor.getBlob(valueIndex);
				altid = cursor.getString(altidIndex);
				platform =  cursor.getString(pltfrmIndex);
				
				SapGenConstants.colId = colId;
				SapGenConstants.apprefid = appRefId;///*ALTERED BY SOWMYA
				SapGenConstants.appname = appNameStr;
				SapGenConstants.packagename = packNameStr;
				SapGenConstants.apiname = apiNameStr;
				SapGenConstants.classname = classNameStr;//ALTERED BY SOWMYA
				SapGenConstants.platform = platform;
				SapGenConstants.altID = altid;
				
				SapQueueProcessorHelperConstants.showLog("</br>Id : "+colId+" appRefId: "+appRefId+" : "+appNameStr+" : "+packNameStr);
				SapQueueProcessorHelperConstants.showLog("</br>Class Name : "+classNameStr+" : "+apiNameStr+":"+SapGenConstants.platform);
				
				if(soapBytes != null)
					soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
				
				cursor.moveToNext();
				//break;
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapQueueProcessorHelperConstants.showErrorLog("</br>Error in processRescheduleData : "+sfg.toString());
    		resetContentValues();
    	}
    	finally{
			if(cursor != null)
				cursor.close();			
			
			if(rowCount > 0){
				SapQueueProcessorHelperConstants.showLog("processRescheduleData .......row count"+rowCount);
				if(colId > 0)
					updateSelectedRowStatus(colId, "", SapQueueProcessorHelperConstants.STATUS_INPROCESS);
				
				initQueueSoapConnection();
			}
			else{
				SapQueueProcessorHelperConstants.showLog("processRescheduleData .......row count is 0");
				setAlarm(900);
				SapQueueProcessorHelperConstants.showLog("</br>End of ReScheduled Queue Processing .......");
				SapQueueProcessorHelperConstants.showLog("</br>******************************************");
				
				SapGenConstants.showLog("SapGenConstants.CountService"+SapGenConstants.CountService);
				if(SapGenConstants.CountService==1){
		           	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		           	String key = String.valueOf(SapGenConstants.CountService);
		           	QueueDetail.put(key, "1");
		           	}else if(SapGenConstants.CountService==2){
		               	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		            	String key = String.valueOf(SapGenConstants.CountService);
			           	QueueDetail.put(key, "15");
		              	}  else if(SapGenConstants.CountService==3){
		                   	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		                	String key = String.valueOf(SapGenConstants.CountService);
				           	QueueDetail.put(key, "1");
		                  	}  else if(SapGenConstants.CountService==4){
		                       	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		                    	String key = String.valueOf(SapGenConstants.CountService);
		    		           	QueueDetail.put(key, "1");
		                      	}  else if(SapGenConstants.CountService==5){
		                           	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		                        	String key = String.valueOf(SapGenConstants.CountService);
		        		           	QueueDetail.put(key, "1");
		                          	}  else if(SapGenConstants.CountService==6){
		                               	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		                            	String key = String.valueOf(SapGenConstants.CountService);
		            		           	QueueDetail.put(key, "1");
		                              	}   else if(SapGenConstants.CountService==7){
		                                   	SapQueueProcessorHelperConstants.showLog("SapGenConstants.CountService: "+SapGenConstants.CountService);
		                                	String key = String.valueOf(SapGenConstants.CountService);
		                		           	QueueDetail.put(key, "1");
		                                  	}   	        	        	
			}
    	}
    }//
		
	private int getApplicationQueueCount(String appName){
		int count = -1;
		Cursor cursor = null;
		try{
			if(appName == null)
				appName = "";
			
			Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			
			ContentResolver resolver = this.getContentResolver();
			
			String selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME + " = ?";
			String[] selectionParams = new String[]{appName}; 
			String[] projection = new String[]{SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID}; 
			
			cursor = resolver.query(uri, projection, selection, selectionParams, null);
			
			if(cursor != null){
				count = cursor.getCount();
			}
		}
		catch(Exception sfag){
			SapQueueProcessorHelperConstants.showErrorLog("</br>Error in getApplicationQueueCount : "+sfag.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
		}
		return count;
	}//fn getApplicationQueueCount	
	
	private void resetContentValues(){
		try{
			colId = -1;
			soapObj = null;
			appRefId = appNameStr = packNameStr = classNameStr = apiNameStr = altid ="";
			SapGenConstants.packagename = SapGenConstants.apprefid = SapGenConstants.apiname = SapGenConstants.classname = SapGenConstants.appname = SapGenConstants.altID =" ";
		}
		catch(Exception sfg){
			SapQueueProcessorHelperConstants.showErrorLog("</br>Error in resetContentValues : "+sfg.toString());
		}
	}//fn resetContentValues
	
	//GET ALL IDS OF TASKS FROM QP DATABASE TABLE***********//
	public static ArrayList<Integer> getIdfromQPTable(Context ctx){
        int count = -1;      
        Cursor cursor = null;
        ArrayList<Integer> responseList = new ArrayList<Integer>();
        int  refIdStr = -1;
        try{            
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();                          
            cursor = resolver.query(uri, null, null, null, null);
          
            //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
            cursor.moveToFirst();
            do{
				 int responseIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID);
				 refIdStr = cursor.getInt(responseIndex);				 
				 responseList.add(refIdStr);
				 SapQueueProcessorHelperConstants.showLog("refIdStr : "+refIdStr);										
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getAppRefIdForPhonegap : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return responseList;
    }//fn getApplicationQueueCount
}//End of class SapQueueProcessorMainService
