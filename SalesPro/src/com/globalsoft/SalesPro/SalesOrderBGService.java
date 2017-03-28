package com.globalsoft.SalesPro;

import org.ksoap2.serialization.SoapObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.globalsoft.SalesOrderLib.SalesOrderCreationTablet;
import com.globalsoft.SalesOrderLib.SalesOrderErrorPersistant;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesOrderBGService extends Service {
		
	private String uniqId="",errType="", errorDesc="", apiName="",altidStr="";
	private int colIdVal = -1;
	private SalesOrderErrorPersistant errorDbObj = null;
	//private ProductErrorMessagePersistant errorDbObj = null;
	private boolean diagdispFlag = false;
	private PendingIntent contentIntent;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private Context context;
	private ProgressDialog progress;
	//private int dispwidth = 300;
	int count = 0;
	int i = 0;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
 
	private long fileSize = 0;
 
		
	@Override
	public void onCreate() {
		SapGenConstants.showLog("onCreate:SalesOrderBGService");
		context = getApplicationContext();
		progress = new ProgressDialog(context);
		progress.setCancelable(true);
		progress.setMessage("File downloading ...");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    progress.setIndeterminate(true);
	}

	@Override
	public void onDestroy() {
		SapGenConstants.showLog("onDestroy:SalesOrderBGService");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		SapGenConstants.showLog("onStart:SalesOrderBGService");
		try{
			mNotificationManager = SapGenConstants.notificationAlert(this.getApplicationContext());
			progress = new ProgressDialog(this);
    		//mNotificationManager.cancelAll();
			context = getApplicationContext();
    		int icon = R.drawable.notify;
    		CharSequence tickerText = "Hello";
    		long when = System.currentTimeMillis();
    		//ServiceProConstants.showLog("when:"+when);
    		notification = new Notification(icon, tickerText, when);
    		// prepare for a progress bar dialog
    		/**/
        	SapGenConstants.showLog("7");
    		
			getApplicationContext();
    		int colId = intent.getIntExtra(SapQueueProcessorHelperConstants.QUEUE_COLID, -1);
			String applRefId = intent.getStringExtra("APPREFID");
			String altIdStr= intent.getStringExtra("altid");
			String applName = intent.getStringExtra("APPLICATIONNAME");
			String soapAPIName = intent.getStringExtra("SOAPAPINAME");
			byte[] soapBytes = intent.getByteArrayExtra("RESULTSOAPOBJ");
			SoapObject soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
        	SapGenConstants.showLog("8");
			colIdVal = colId;
			uniqId =  applRefId;
			altidStr =  altIdStr;
			apiName = soapAPIName;
			SapGenConstants.showLog("colId:"+colIdVal);
			SapGenConstants.showLog("applRefId:"+applRefId);
			SapGenConstants.showLog("Alternative ID:"+altidStr);
			SapGenConstants.showLog("applName:"+applName);
			SapGenConstants.showLog("soapAPIName:"+soapAPIName);
			SapGenConstants.showLog("soapObj:"+soapObj.toString());
        	SapGenConstants.showLog("9");
			getSOCreateSoapResponse(soapObj);
        	SapGenConstants.showLog("10");
			//Toast.makeText(this, "Application Name:"+applName+"\n SOAPAPI Name:"+soapAPIName+"\n ResultSoapObj:"+soapObj.toString()+"\n", Toast.LENGTH_LONG).show();
		}
		catch(Exception sff){
        	SapGenConstants.showErrorLog("Error in BgProcess : "+sff.toString());
        }		
	}//
		
	public void getSOCreateSoapResponse(SoapObject soap){
		String taskErrorMsgStr="";
		boolean resMsgErr = false;
        if(soap != null){
            try{
            	SapGenConstants.soapResponse(this, soap, true);
            	taskErrorMsgStr = SapGenConstants.SOAP_RESP_MSG;
            	errorDesc = taskErrorMsgStr;
            	SapGenConstants.showLog("errorDesc"+errorDesc);
            	errType = SapGenConstants.SOAP_ERR_TYPE;
            	String soapMsg = soap.toString();
            	resMsgErr = SapGenConstants.getSoapResponseSucc_Err(soapMsg); 
            }
            catch(Exception sff){
            	SapGenConstants.showErrorLog("On gettingSoapResponse : "+sff.toString());
            }           
            try{
    			SapGenConstants.showLog("resMsgErr:"+resMsgErr);
            	if(resMsgErr==false){
            		try{
            			if(apiName.equals(SapGenConstants.PROD_CREATE_API_NAME)){
	            			if(colIdVal >=0){
	            				SapQueueProcessorHelperConstants.updateSelectedRowStatusForServicePro(this, colIdVal, SapGenConstants.APPLN_NAME_STR_SALESPRO, SapGenConstants.STATUS_COMPLETED, SapGenConstants.PROD_CREATE_API_NAME);
	            			}else{
	            				SapQueueProcessorHelperConstants.updateSelectedRowStatusForServicePro(this, 0, SapGenConstants.APPLN_NAME_STR_SALESPRO, SapGenConstants.STATUS_COMPLETED, SapGenConstants.PROD_CREATE_API_NAME);
	            			}
            			}
            		}
                    catch(Exception sff1){
                    	SapGenConstants.showErrorLog("Error in updating queue process database: "+sff1.toString());
                    }       
            	}
            	else{            		
            		CharSequence contentTitle = "SalesPro notifications";
            		CharSequence contentText = errorDesc;    		
            		
            		if(apiName.equals(SapGenConstants.PROD_CREATE_API_NAME)){
            			contentText = uniqId+":"+errorDesc;
            		}else{
            			contentText = uniqId+":"+errorDesc;
            		}           		
            		
            		int uniqueIdVal = (int) (Integer.parseInt(uniqId) + System.currentTimeMillis());
            		SapGenConstants.showLog("uniqueIdVal in else part:"+uniqueIdVal);
            		Intent notificationIntent = new Intent(Intent.ACTION_MAIN); 
            		SapGenConstants.showLog("0");
            		int displaywidth= SapGenConstants.getDisplayWidth(this);
            		notificationIntent.setClass(this.getApplicationContext(), SalesOrderCreationTablet.class);
      				/*if(displaywidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
      					notificationIntent.setClass(getApplicationContext(), SalesOrderCreationTablet.class);
      				}else{
      					notificationIntent.setClass(getApplicationContext(), SalesOrderCreation.class);
      				} 	*/	
            		SapGenConstants.showLog("1");
            		notificationIntent.putExtra(SapGenConstants.QUEUE_COLID, colIdVal);
            		notificationIntent.putExtra(SapGenConstants.QUEUE_ERR_APPREFID, uniqId);
            		notificationIntent.putExtra(SapGenConstants.QUEUE_ERR_MSG, errorDesc);            		            		
            		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		notificationIntent.putExtra("fromNotification", true);
            		notificationIntent.putExtra("custID", altidStr);
            		notificationIntent.setAction("action_id_" + uniqueIdVal);
            		contentIntent = PendingIntent.getActivity(context, uniqueIdVal, notificationIntent, 0);
            		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);            		
            		mNotificationManager.notify(colIdVal, notification);  
            		
            		boolean apiExists = false;
            		if(errorDbObj == null)
	        			errorDbObj = new SalesOrderErrorPersistant(this.getApplicationContext());
            		if(errorDbObj != null){
            			apiExists = errorDbObj.checkTrancIdApiExists(uniqId.trim(), apiName.trim());
				    	if(!apiExists){
				    		//errorDbObj.insertErrorMsgDetails(tracId, errType, errorDesc, apiName);
				    		errorDbObj.insertErrorMsgDetails(uniqId, errType, errorDesc, apiName);
				    	} 
				    	else{
				    		errorDbObj.updateValue(uniqId, errType, errorDesc, apiName);
				    	}	            		
            		}
            		if(errorDbObj != null)
	        			errorDbObj.closeDBHelper();	
            	}
            }
            catch(Exception asf){
            	SapGenConstants.showErrorLog("Bg process in inserting databases : "+asf.toString());            	
            }
        }
    }//fn soapResponse

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
