package com.globalsoft.SapQueueProcessorHelper.Utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorContentProvider;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBConstants;

public final class SapQueueProcessorHelperConstants {
	
	//Log Related Constants
    public static final String SAPQUEUEPROCESSORHELPER_TAG = "SapQueueProcessorHelper ";
    public static final String SAPQUEUEPROCESSORHELPER_ERRORTAG = "SapQueueProcessorHelper Error ";   
    public static final String SUB_FOLDER_PATH = "Sapqueueprocessor"+File.separator+"Logs";
    public static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory()+ File.separator+SUB_FOLDER_PATH+ File.separator;
    public static final int curr_date = Calendar.getInstance().get(Calendar.DATE);
    public static final String PATH_LOG_FILE = getPathLogFile();
    public static final String PATH_ERRLOG_FILE = curr_date+"_errorlog.txt";
    
	//QUEUE PARAMS CONSTANTS
    public static final String QUEUE_SOAPAPINAME = "SOAPAPINAME";
    public static final String QUEUE_APPLNAME = "APPLICATIONNAME";
    public static final String QUEUE_RESULTSOAPOBJ = "RESULTSOAPOBJ";
    public static final String QUEUE_REQUESTSOAPOBJ = "REQUESTSOAPOBJ";
    public static final String QUEUE_COLID = "COLID";
    public static final String QUEUE_APPREFID = "APPREFID";
    public static final String QUEUE_ALT_ID = "altid";
    
    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_APPREFID = "apprefid";
    public static final String COL_APPNAME = "appname";
    public static final String COL_PCKGNAME = "packagename";
    public static final String COL_CLASSNAME = "classname";
    public static final String COL_FUNCNAME = "apiname";
    public static final String COL_DATE = "queuedate";
    public static final String COL_SOAPDATA = "soapdata";
    public static final String COL_RESPONSE_SOAPDATA = "responsesoapdata";
    public static final String COL_PLATFORM = "platform";
    public static final String COL_STATUS = "status";
    public static final String COL_PROCESS_COUNT = "processcount";
    public static final String COL_PROCESS_TIME = "processstarttime";
   
    
    public static final int MAX_PROCESS_COUNT = 7;
    public static String dateVal = "";
    
    //Application Name Constants
    public static String APPLN_NAME_STR_MOBILEPRO = "MOBILEPRO";
    
    //QUEUE STATUS CONSTANTS
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_INPROCESS = 1;
    public static final int STATUS_PROCEEDEDBYSERVER = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_HIGHPRIORITY = 100;
    public static final int STATUS_RELAYEDBACKTOCLIENT = 3;
    
    static {
    	File logFolder = null;
    	try{
    		logFolder = new File(LOG_FILE_PATH);
    		if(!logFolder.exists())
    			logFolder.mkdirs();
    	}
    	catch(Exception sfg){}
    }    
    
    public static String getPathLogFile(){
    	String path = "";    	    	 
    	try {
			if(curr_date > 0 && curr_date < 10){
				path = "0"+curr_date+"_log.txt";
			}else{
				path = curr_date+"_log.txt";
			}
		} catch (Exception edd) {
			// TODO Auto-generated catch block
			edd.printStackTrace();
		}
    	return path;
    }//fn getPathLogFile
    
    public static void writeLogToFile(String filename, String text){
    	try{
    		String path = LOG_FILE_PATH+filename;    		
    		File logFile = new File(path);  
    	    File file = new File(path);
    	    Date lastModDate = new Date(file.lastModified());  
    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	    String oldDateandTime = sdf.format(lastModDate);
    	    //Log.e("file date : ", ""+oldDateandTime);
    	    String currentDateandTime = sdf.format(new Date());
    	    //Log.e("file currentDateandTime : ", ""+currentDateandTime);	    
    	    if (oldDateandTime.equalsIgnoreCase(currentDateandTime)) {
    			//Log.e("", "today date & time");
    		}else{
    	    	//Log.e("", "Past date & time");
    	    	file.delete();
    		}
    		
    		if (!logFile.exists()){
				try {
					logFile.createNewFile();
				} 
				catch (IOException e){
					Log.e(SapQueueProcessorHelperConstants.SAPQUEUEPROCESSORHELPER_TAG, e.toString());
	    		}	
    		}
    		
			if((logFile != null) && (logFile.exists())){
	    		BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
	    		buf.append(text);
	    		buf.newLine();
	    		buf.close();
    		}
    	}
    	catch(Exception sgh){
    		sgh.printStackTrace();
    	}
    }//fn writeLogToFile
    
    //Log related functions
    public static void showLog(String text){
    	Log.e(SapQueueProcessorHelperConstants.SAPQUEUEPROCESSORHELPER_TAG,text);
    	SapQueueProcessorHelperConstants.writeLogToFile(SapQueueProcessorHelperConstants.PATH_LOG_FILE, text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(SapQueueProcessorHelperConstants.SAPQUEUEPROCESSORHELPER_ERRORTAG,text);
    	SapQueueProcessorHelperConstants.writeLogToFile(SapQueueProcessorHelperConstants.PATH_ERRLOG_FILE, text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }
    
    /*************************************************************************
	 * 
	 * Offline Methods
	 * 
	 *******************************************************************************/
	
	
	/*public static String saveOfflineContentToQueueProcessor(Context ctx, String appName, String packageName, String appRefId, String altid, String className, String apiName, SoapObject soapObj, Long date){
		try {
			SapQueueProcessorHelperConstants.showLog("Inside saveOfflineContentToQueueProcessor ");
			byte[] soapBytes = null;
	        try{
	            if(soapObj != null){
	                soapBytes = SapQueueProcessorHelperConstants.getSerializableSoapObject(soapObj);
	                if(soapBytes != null){                   	

	    	        	SapQueueProcessorHelperConstants.showLog("appName : "+appName);
	                    ContentResolver resolver = ctx.getContentResolver();
	                    ContentValues val = new ContentValues();
	                    if((appRefId != null) && (!appRefId.equalsIgnoreCase(""))){
	                    	//val.put(SapQueueProcessorHelperConstants.COL_APPREFID, appRefId);
	                    	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID, appRefId);
	                    	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID, altid);
	                    }

	                    SapQueueProcessorHelperConstants.showLog("</br>Id :  appRefId: "+appRefId+" : "+appName+" : "+packageName);
	                    SapQueueProcessorHelperConstants.showLog("</br>Class Name : "+className+" : "+apiName);
	                   val.put(SapQueueProcessorHelperConstants.COL_APPNAME, appName);
	                    val.put(SapQueueProcessorHelperConstants.COL_PCKGNAME, packageName);
	                    val.put(SapQueueProcessorHelperConstants.COL_CLASSNAME, className);
	                    val.put(SapQueueProcessorHelperConstants.COL_FUNCNAME, apiName);
	                    val.put(SapQueueProcessorHelperConstants.COL_SOAPDATA, soapBytes);
	                	val.put(SapQueueProcessorHelperConstants.COL_DATE, date);
	                    
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME, appName);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME, packageName);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME, className);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME, apiName);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA, soapBytes);
	                	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_DATE, date);
	                    
	                    resolver.insert(SapQueueProcessorContentProvider.CONTENT_URI, val);
	                   
	                }
	                else
	                	SapQueueProcessorHelperConstants.showErrorLog("Soap byte Conversion is Null");
	            }
	            else
	            	SapQueueProcessorHelperConstants.showErrorLog("Offline Soap Object is Null");
	        }
	        catch(Exception sgh){
	        	SapQueueProcessorHelperConstants.showErrorLog("Error in saveOfflineContent : "+sgh.toString());
	        }
		} catch (Exception sgh) {
        	SapQueueProcessorHelperConstants.showErrorLog("Error in currentDate : "+sgh.toString());
    		return SapQueueProcessorHelperConstants.dateVal;
		}		
		return SapQueueProcessorHelperConstants.dateVal;
	}//fn currentDate
*/	
	 public static String sendOfflineContentToQueueProcessor(Context ctx, String appName, String packageName, String appRefId, String altid, String className, String apiName, SoapObject soapObj, Long date,String pltfrm){
		Uri uri =null;
		int count = -1;
	    int colId =-1;
	    String whereStr = null;
	    String[] whereParams = null;
	    Cursor cursor = null;
		try {
			SapQueueProcessorHelperConstants.showLog("Inside sendOfflineContentToQueueProcessor ");
			byte[] soapBytes = null;
	        try{
	            if(soapObj != null){
	                soapBytes = SapQueueProcessorHelperConstants.getSerializableSoapObject(soapObj);
	                if(soapBytes != null){                   	
	                	//CHECK IF THE ALTERNATIVE ID PRESENT ALREADY IN QP//IF YES THEN UPDATE IT
	                	uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
	                    
	                    ContentResolver resolver = ctx.getContentResolver();
	                    
	                    whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID+ " = ?";
	                    whereParams = new String[]{appRefId};	                   
	                    
	                    cursor = resolver.query(uri, null, whereStr, whereParams, null);

	                    SapQueueProcessorHelperConstants.showLog("log inside sendOfflineContentToQueueProcessor to check apprefid cursor count: "+ cursor.getCount());
	                    count = cursor.getCount();
	                  
	                    if(count>0){
	                    	 SapQueueProcessorHelperConstants.showLog("//CUSTOMER ID ALREADY EXISTS UPDATING THE EXISTING QUEUE ITEM//*********//");//CUSTOMER ID ALREADY EXISTS UPDATING THE EXISTING QUEUE ITEM//*********//
	                    	//UpdateCurrentQueueItem(ctx, appName, packageName, appRefId, altid, className, apiName,soapBytes,date,pltfrm);
	                    	
	                    	int rows = resolver.delete(uri, whereStr, whereParams);				
	        				SapQueueProcessorHelperConstants.showLog("Rows Deleted : "+rows);    
	                    	//UPDATED THE CURRENT QUEUED ITEM//*************//
	                    }//else{
	                    	SapQueueProcessorHelperConstants.showLog("appName : "+appName);
	 	                   // ContentResolver resolver = ctx.getContentResolver();
	 	                    ContentValues val = new ContentValues();	 	                   
	 	                    if(appRefId != ""){
	 	                    	SapQueueProcessorHelperConstants.showLog("appRefId : "+appRefId);
	 	                    	SapQueueProcessorHelperConstants.showLog("altid : "+altid);
	 	                    	//val.put(SapQueueProcessorHelperConstants.COL_APPREFID, appRefId);
	 	                    	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID, appRefId);
	 	                    	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID, altid);
	 	                    }
	 	                    SapQueueProcessorHelperConstants.showLog("</br>Id :  appRefId: "+appRefId+" : "+appName+" : "+packageName);
	 	                    SapQueueProcessorHelperConstants.showLog("</br>Class Name : "+className+" : "+apiName);
	 	                        	                   
	 	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME, appName);
	 	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME, packageName);
	 	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME, className);
	 	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME, apiName);
	 	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA, soapBytes);
	 	                	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_DATE, date);
	 	                	val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG, pltfrm);
	 	                	
	 	                    resolver.insert(SapQueueProcessorContentProvider.CONTENT_URI, val);
	                   // }
	                    //CHECK IF THE 
	                }
	                else
	                	SapQueueProcessorHelperConstants.showErrorLog("Soap byte Conversion is Null");
	            }
	            else
	            	SapQueueProcessorHelperConstants.showErrorLog("Offline Soap Object is Null");
	        }
	        catch(Exception sgh){
	        	SapQueueProcessorHelperConstants.showErrorLog("Error in sendOfflineContentToQueueProcessor : "+sgh.toString());
	        }
		} catch (Exception sgh) {
        	SapQueueProcessorHelperConstants.showErrorLog("Error in currentDate : "+sgh.toString());
    		return SapQueueProcessorHelperConstants.dateVal;
		}		
		return SapQueueProcessorHelperConstants.dateVal;
	}//fn currentDate
	
	private static void UpdateCurrentQueueItem(Context ctx, String appName, String packageName, String appRefId, String altid, String className, String apiName, byte[] soapbytes, Long date,String pltfrm) {
		Uri uri =null;
		int count = -1;
	    int colId =-1;
	    String whereStr = null;
	    String[] whereParams = null;
	    Cursor cursor = null;	    
	try{
		SapQueueProcessorHelperConstants.showLog("Updating the current Queue item: ");
		uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");	    
	    
	    whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID+ " = ? ";
	    whereParams = new String[]{appRefId}; 
	    
	    ContentValues updateContent = new ContentValues();
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID, altid);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME, appName);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME, packageName);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME, className);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME, apiName);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA, soapbytes);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_DATE, date);
	    updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG, pltfrm);
     	
       
        ContentResolver resolver = ctx.getContentResolver();
        int rows = resolver.update(uri, updateContent, whereStr, whereParams);   
        SapQueueProcessorHelperConstants.showLog("No of rows updated after update content of UpdateCurrentQueueItem : "+ rows);
	}catch (Exception sgh) {
    	SapQueueProcessorHelperConstants.showErrorLog("Error in UpdateCurrentQueueItem : "+sgh.toString());		
	}		 		
	}//UpdateCurrentQueueItem

	public static void InsertNotifyData(Context ctx,String appRefId, String altid, String errordesc,String notify){
		try {
			SapQueueProcessorHelperConstants.showLog("Inside InsertNotifyData ");			
	        try{	        		
	                    ContentResolver resolver = ctx.getContentResolver();
	                    ContentValues val = new ContentValues();	                    	                   
	                    SapQueueProcessorHelperConstants.showLog("appRefId : "+appRefId);
	                    SapQueueProcessorHelperConstants.showLog("altid : "+altid);
	                    	//val.put(SapQueueProcessorHelperConstants.COL_APPREFID, appRefId);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_APPREFID, appRefId);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID, altid);	                  
	                    SapQueueProcessorHelperConstants.showLog("</br>Id :  appRefId: "+appRefId+" : "+altid);	                             	                   
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_ERROR_DESCRIPTION, errordesc);
	                    val.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_NOTIFY_FLAG, notify);	                   
	                	
	                    resolver.insert(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI, val);	                   
	                    SapQueueProcessorHelperConstants.showLog("</br> Inserted Error description to Notification table");
	        }
	        catch(Exception sgh){
	        	SapQueueProcessorHelperConstants.showErrorLog("Error in InsertNotifyData : "+sgh.toString());
	        }
		} catch (Exception sgh) {
        	SapQueueProcessorHelperConstants.showErrorLog("Error in currentDate : "+sgh.toString());   		
		}				
	}//fn currentDate
	
	public static byte[] getSerializableSoapObject(SoapObject soapObj){
    	byte[] soapBytes = null;
    	try {
    		if(soapObj != null){    			
	    		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    		envelope.setOutputSoapObject(soapObj);
	    		
	    		XmlSerializer aSerializer = Xml.newSerializer();
	    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		try {
		    		aSerializer.setOutput(baos, "UTF-8");
		    		envelope.write(aSerializer);
		    		aSerializer.flush();
	    		} catch (Exception ewwe) {
	    			SapQueueProcessorHelperConstants.showErrorLog("Error on getSerializableSoapObject1 : "+ewwe.toString());
	    		}
	    		
	    		if(baos != null){
	    			soapBytes = baos.toByteArray();
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapQueueProcessorHelperConstants.showErrorLog("Error on getSerializableSoapObject2 : "+es.toString());
		}
    	return soapBytes;
    }//fn getSerializableSoapObject
    
    public static SoapObject getDeSerializableSoapObject(byte[] soapBytes){
    	SoapObject soapObj = null;
    	try {
    		if(soapBytes != null){
    			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    		try {
	    			 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    	         factory.setNamespaceAware(true);
	    	         XmlPullParser xpp = factory.newPullParser();
	    	         String soapEnvStr = new String(soapBytes);
	    	         xpp.setInput(new StringReader(soapEnvStr));
	    	         /*
 	    			 ByteArrayInputStream inputStream = new ByteArrayInputStream(soapBytes);
	    	         xpp.setInput(inputStream, "UTF-8");
	    	         */
	    	         envelope.parse(xpp);
	    	         soapObj = (SoapObject)envelope.bodyIn;

	    		} catch (Exception ewwe) {
	    			SapQueueProcessorHelperConstants.showErrorLog("Error on getDeSerializableSoapObject1 : "+ewwe.toString());
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapQueueProcessorHelperConstants.showErrorLog("Error on getDeSerializableSoapObject2 : "+es.toString());
		}
    	return soapObj;
    }//fn getDeSerializableSoapObject
    
    public static void updateSelectedRowStatus(Context ctx, int colId, String appName, int status){
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
                
                whereStr = SapQueueProcessorHelperConstants.COL_APPNAME + " = ? And "+SapQueueProcessorHelperConstants.COL_STATUS+ " = ?";
                whereParams = new String[]{appName, String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE)}; 
            }
            
            if(status < SapQueueProcessorHelperConstants.STATUS_IDLE)
                status = SapQueueProcessorHelperConstants.STATUS_IDLE;
            else if(status > SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY)
                status = SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY;
            
            ContentValues updateContent = new ContentValues();
            updateContent.put(SapQueueProcessorHelperConstants.COL_STATUS, status);
 
            ContentResolver resolver = ctx.getContentResolver();
            int rows = resolver.update(uri, updateContent, whereStr, whereParams);
            
            /*SapGenConstants.showLog("colId : "+colId);
            SapGenConstants.showLog("appName : "+appName);
            SapGenConstants.showLog("Row Updated : "+rows);*/
        }
        catch(Exception qsewe){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in updateSelectedRowStatus : "+qsewe.toString());
        }    
    }//fn updateSelectedRowStatus
    
    public static void updateSelectedRowStatusForServicePro(Context ctx, int colId, String appName, int status, String apiname){
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
                
                whereStr = SapQueueProcessorHelperConstants.COL_APPNAME + " = ? And "+SapQueueProcessorHelperConstants.COL_STATUS+ " = ? And "+
                		SapQueueProcessorHelperConstants.COL_FUNCNAME+ " = ?";
                whereParams = new String[]{appName, String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE), apiname}; 
            }
            
            SapQueueProcessorHelperConstants.showLog("status before : "+status);
            if(status < SapQueueProcessorHelperConstants.STATUS_IDLE)
                status = SapQueueProcessorHelperConstants.STATUS_IDLE;
            else if(status > SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY)
                status = SapQueueProcessorHelperConstants.STATUS_HIGHPRIORITY;
            SapQueueProcessorHelperConstants.showLog("status after : "+status);
            
            ContentValues updateContent = new ContentValues();
            updateContent.put(SapQueueProcessorHelperConstants.COL_STATUS, status);
            SapQueueProcessorHelperConstants.showLog("Status after sending to BG Service : "+ status);
            ContentResolver resolver = ctx.getContentResolver();
            int rows = resolver.update(uri, updateContent, whereStr, whereParams);   
            SapQueueProcessorHelperConstants.showLog("No of rows updated after sending to BG Service : "+ rows);
        }
        catch(Exception qsewe){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in updateSelectedRowStatus : "+qsewe.toString());
        }    
    }//fn updateSelectedRowStatus 

    //UPDATING THE NOTIFICATION FLAG//
    public static void updateSelectedNotifyStatus(Context ctx,String notifyflag,int colId){
        Uri uri = null;
        String whereStr = null;
        String[] whereParams = null;
        try{             
        	  SapQueueProcessorHelperConstants.showLog("colId inside  updateSelectedNotifyStatus: "+colId);
            uri = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI+"/"+colId);                             
            SapQueueProcessorHelperConstants.showLog("1");
            ContentValues updateContent = new ContentValues();
            updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_NOTIFY_FLAG, notifyflag);
            SapQueueProcessorHelperConstants.showLog("2");
            ContentResolver resolver = ctx.getContentResolver();
            int rows = resolver.update(uri, updateContent, null, null);
            SapQueueProcessorHelperConstants.showLog("3");
            SapQueueProcessorHelperConstants.showLog("Row Updated : "+rows);
            /*SapGenConstants.showLog("colId : "+colId);
            SapGenConstants.showLog("appName : "+appName);
            SapGenConstants.showLog("Row Updated : "+rows);*/
        }
        catch(Exception qsewe){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in updateSelectedRowStatus : "+qsewe.toString());
        }    
    }//fn updateSelectedRowStatus
    //UPDATED THE SELECTED NOTIFICATION STATUS//**********//
    
    //GET COLUMN ID TO UPDATE NOTIFICATION FLAG//**********************//
    public static int getColumnIdForPhonegap(Context ctx, String appName, String altIdStr){
    	 Uri uri = null;        
        int count = -1;
        int colId =-1;
        String whereStr = null;
        String[] whereParams = null;
        Cursor cursor = null;
        try{                      
            uri = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID+ " = ?";
            whereParams = new String[]{String.valueOf(altIdStr)};
            //String[] projection = new String[]{SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID}; 
            
            cursor = resolver.query(uri, null, whereStr, whereParams, null);

            SapQueueProcessorHelperConstants.showLog("log inside getColumnIdForPhonegap: "+ whereStr);
            //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
            cursor.moveToFirst();
            do{
				 int notifyindex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ID);
				 colId = cursor.getInt(notifyindex);				
				// responseList.add(soapBytes);
				 //SapQueueProcessorHelperConstants.showLog("responseStr : "+responseStr);										
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
            SapQueueProcessorHelperConstants.showLog("Cursor count for getting column ID: count : "+ count);
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getColumnIdForPhonegap : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return colId;
    }//fn getApplicationQueueCount
    //   //GET COLUMN ID TO UPDATE NOTIFICATION FLAG//**********************//
    
    //CHECK IF THE ALTERNATE ID ALREADY PRESENT IN DB TO ADD THE NOTIFICATION DATA TO TABLE//*********//
    public static boolean CheckforId(Context ctx,String altIdStr){
   	 Uri uri = null;        
       int count = -1;
       int colId =-1;
       String whereStr = null;
       String[] whereParams = null;
       Cursor cursor = null;
      boolean checkflag =false;
       try{                      
           uri = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI+"");
           
           ContentResolver resolver = ctx.getContentResolver();
           
           whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID+ " = ?";
           whereParams = new String[]{altIdStr};
           //String[] projection = new String[]{SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID}; 
           
           cursor = resolver.query(uri, null, whereStr, whereParams, null);

           SapQueueProcessorHelperConstants.showLog("log inside CheckforId cursor count: "+ cursor.getCount());
           //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
           
           if(cursor != null){
               count = cursor.getCount();
           }
           if(count>0){
        	   checkflag =true;
           }       
       }
       catch(Exception sfag){
       	SapQueueProcessorHelperConstants.showErrorLog("Error in CheckforId : "+sfag.toString());
       }
       finally{
           if(cursor != null)
               cursor.close();    
       }
       return checkflag;
   }//fn CheckforId
    //END OF CHECK IF THE ALTERNATE ID ALREADY PRESENT IN DB TO ADD THE NOTIFICATION DATA TO TABLE//*********//
    
    //CHECK FOR ERROR DESCRIPTION//***********************//
    public static void UpdateforErrorDesc(Context ctx,String altid,String errordescStr){
      	 Uri uri = null;        
          int count = -1;
          //int colId =-1;
          String whereStr = null;        
          String[] whereParams = null;
          Cursor cursor = null;
          try{                      
              uri = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI+"");
                                       
              whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID + " = ? ";
              whereParams = new String[]{altid}; 
              
              
              ContentValues updateContent = new ContentValues();
              updateContent.put(SapQueueProcessorDBConstants.QUEUEPRSSR_ERROR_DESCRIPTION, errordescStr);
              SapQueueProcessorHelperConstants.showLog("errordescStr after update content : "+ errordescStr);
              ContentResolver resolver = ctx.getContentResolver();
              int rows = resolver.update(uri, updateContent, whereStr, whereParams);   
              SapQueueProcessorHelperConstants.showLog("No of rows updated after update content : "+ rows);
          }
          catch(Exception sfag){
          	SapQueueProcessorHelperConstants.showErrorLog("Error in UpdateforErrorDesc : "+sfag.toString());
          }
          finally{
              if(cursor != null)
                  cursor.close();    
          }         
      }//fn CheckforErrorDesc
   
    //UPDATE THE ERROR DESCRIPTION TO SHOW NOTIFICATION IN PHONEGAP APP//***************************//
    public static String readErrorDescString(Context ctx,String alternateID){
   	 	Uri uri = null;        
       int count = -1;
       String errordesc= " ";
       String whereStr = null;
       String[] whereParams = null;
       Cursor cursor = null;
       try{                      
           uri = Uri.parse(SapQueueProcessorContentProvider.QP_NOTIFY_CONTENT_URI+"");
           
           ContentResolver resolver = ctx.getContentResolver();
           
           whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_E_ALT_ID+ " = ?";
           SapQueueProcessorHelperConstants.showLog("altIdStr: "+alternateID);
           whereParams = new String[]{String.valueOf(alternateID)};
           //String[] projection = new String[]{SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID}; 
           
           cursor = resolver.query(uri, null, whereStr, whereParams, null);

           SapQueueProcessorHelperConstants.showLog("log inside readErrorDescString: "+ whereStr);
           SapQueueProcessorHelperConstants.showLog("whereParams readErrorDescString: "+ whereParams);
           //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
           
           if(cursor != null){
               count = cursor.getCount();
           }
           cursor.moveToFirst();
           do{
				 int notifyindex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_ERROR_DESCRIPTION);
				 errordesc = cursor.getString(notifyindex);				
				// responseList.add(soapBytes);
				 //SapQueueProcessorHelperConstants.showLog("responseStr : "+responseStr);										
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
           SapQueueProcessorHelperConstants.showLog("Cursor count for getting column ID: count : "+ count);
       }
       catch(Exception sfag){
       	SapQueueProcessorHelperConstants.showErrorLog("Error in getColumnIdForPhonegap : "+sfag.toString());
       }
       finally{
           if(cursor != null)
               cursor.close();    
       }
       return errordesc;
   }//fn getApplicationQueueCount
    //END OF UPDATE THE ERROR DESCRIPTION TO SHOW NOTIFICATION IN PHONEGAP APP//***************************//
    
    public static int getApplicationQueueCount(Context ctx, String appName){
        int count = -1;
        Cursor cursor = null;
        try{
            if(appName == null)
                appName = "";
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = "( "+SapQueueProcessorHelperConstants.COL_APPNAME + " = ? OR "+ SapQueueProcessorHelperConstants.COL_APPNAME + " = ? ) And ("+SapQueueProcessorHelperConstants.COL_STATUS+ " != ? And "+SapQueueProcessorHelperConstants.COL_STATUS+ " != ? )";
            String[] selectionParams = new String[]{appName, APPLN_NAME_STR_MOBILEPRO, String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER ),  String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED )};
            String[] projection = new String[]{SapQueueProcessorHelperConstants.COL_ID}; 
            
            cursor = resolver.query(uri, projection, selection, selectionParams, null);

            SapQueueProcessorHelperConstants.showLog("log : "+ selection);
            SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getApplicationQueueCount : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return count;
    }//fn getApplicationQueueCount
       
    public static int getApplicationProcessingQueueCount(Context ctx, String appName){
        int count = -1;
        Cursor cursor = null;
        try{
            if(appName == null)
                appName = "";
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = "( "+SapQueueProcessorHelperConstants.COL_APPNAME + " = ? OR "+ SapQueueProcessorHelperConstants.COL_APPNAME + " = ? ) And ("+SapQueueProcessorHelperConstants.COL_STATUS+ " != ? )";
            String[] selectionParams = new String[]{appName, APPLN_NAME_STR_MOBILEPRO, String.valueOf(SapQueueProcessorHelperConstants.STATUS_INPROCESS )};
           // String[] projection = new String[]{SapQueueProcessorHelperConstants.COL_ID}; 
            
            cursor = resolver.query(uri, null, selection, selectionParams, null);

            SapQueueProcessorHelperConstants.showLog("log : "+ selection);
            SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getApplicationQueueCount : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return count;
    }//fn getApplicationQueueCount
    
    public static int getSubApplicationProcessingQueueCount(Context ctx, String appName){
        int count = -1;
        Cursor cursor = null;
        try{
            if(appName == null)
                appName = "";
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = SapQueueProcessorHelperConstants.COL_FUNCNAME +" = ?  And "+SapQueueProcessorHelperConstants.COL_STATUS+ " != ? ";
            String[] selectionParams = new String[]{appName, String.valueOf(SapQueueProcessorHelperConstants.STATUS_INPROCESS )};
           // String[] projection = new String[]{SapQueueProcessorHelperConstants.COL_ID}; 
            
            cursor = resolver.query(uri, null, selection, selectionParams, null);

            SapQueueProcessorHelperConstants.showLog("log : "+ selection);
            //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getApplicationQueueCount : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return count;
    }//fn getApplicationQueueCount
    
    public static SoapObject getResponseForPhonegap(Context ctx,String platfrmStr, String apprefIdstr){
        int count = -1;
        SoapObject soapObj = null;
        Cursor cursor = null;
        ArrayList responseList = new ArrayList();
        byte[] soapBytes;
        try{            
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG+ " = ? AND "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID+" = ?";
            String[] selectionParams = new String[]{platfrmStr,apprefIdstr};
            //String[] projection = new String[]{SapQueueProcessorHelperConstants.COL_RESPONSE_SOAPDATA}; 
            
            cursor = resolver.query(uri, null, selection, selectionParams, null);

            SapQueueProcessorHelperConstants.showLog("log : "+ selection);
            //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
            cursor.moveToFirst();
            do{
				 int responseIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA_RESPONSE);
				 soapBytes = cursor.getBlob(responseIndex);
				 soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
				// responseList.add(soapBytes);
				 //SapQueueProcessorHelperConstants.showLog("responseStr : "+responseStr);										
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
        }
        catch(Exception sfag){
        	SapQueueProcessorHelperConstants.showErrorLog("Error in getResponseForPhonegap : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return soapObj;
    }//fn getApplicationQueueCount
    
    
    //GETTING APPLICATION REFERENCE ID FROM DATABASE FOR PHONEGAP CONTACTS APP
    public static String getAppRefIdForPhonegap(Context ctx,String platform){
        int count = -1;
        SoapObject soapObj = null;
        Cursor cursor = null;
        ArrayList responseList = new ArrayList();
        String refIdStr = "";
        try{            
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG+ " = ? ";
            String[] selectionParams = new String[]{platform};
            //String[] projection = new String[]{SapQueueProcessorHelperConstants.COL_RESPONSE_SOAPDATA}; 
            
            cursor = resolver.query(uri, null, selection, selectionParams, null);

            SapQueueProcessorHelperConstants.showLog("log : "+ selection);
            //SapQueueProcessorHelperConstants.showLog("log : "+ appName+"      "+APPLN_NAME_STR_MOBILEPRO+"      "+String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER )+"      "+ String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED));
            
            if(cursor != null){
                count = cursor.getCount();
            }
            cursor.moveToFirst();
            do{
				 int responseIndex = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID);
				 refIdStr = cursor.getString(responseIndex);				 
				// responseList.add(soapBytes);
				 //SapQueueProcessorHelperConstants.showLog("responseStr : "+responseStr);										
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
        return refIdStr;
    }//fn getApplicationQueueCount
    
    //DELETE RELAYED BACK TO CLIENT DATA//***************//
    public static void deleteContactTableDataFromDB(Context ctx,String idStr){
    	try{
    		String selection = null;
    		String[] selectionParams = null;
    		Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
				
				//Get the Resolver
				ContentResolver resolver = ctx.getContentResolver();
				selection =  SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID  + " = ?"; 
				selectionParams = new String[]{idStr};		
				//Make the invocation. # of rows deleted will be sent back
				int rows = resolver.delete(uri, selection, selectionParams);				
				SapQueueProcessorHelperConstants.showLog("Rows Deleted : "+rows);    		
    	}
    	catch(Exception sggh){
    		SapQueueProcessorHelperConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllCategoryDataFromDB
}//End of class SapQueueProcessorHelperConstants
