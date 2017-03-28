package com.globalsoft.SapQueueProcessor.Utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public final class SapQueueProcessorConstants {
	//private static Context ctx;
	//Log Related Constants
    public static final String SAPQUEUEPROCESSOR_TAG = "SapQueueProcessor ";
    public static final String SAPQUEUEPROCESSOR_ERRORTAG = "SapQueueProcessor Error ";     
   
    //Mobile Related Constants
    public static String SAPQUEUEPROCESSOR_MOBILE_IMEI = "";
   
    //SOAP CONSTANTS   
    public static final String SOAP_SERVICE_URL = "http://75.99.152.10:8050/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/110/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://75.99.152.10:8050/sap/bc/srt/wsdl/bndg_E0A8AEE275F3AEF1AE7900188B47B426/wsdl11/allinone/ws_policy/document?sap-client=110";
    public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";
    /*
    //QUEUE STATUS CONSTANTS
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_INPROCESS = 1;
    public static final int STATUS_SENDTOSERVER = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_HIGHPRIORITY = 100;
    
    //QUEUE PARAMS CONSTANTS
    public static final String QUEUE_SOAPAPINAME = "SOAPAPINAME";
    public static final String QUEUE_APPLNAME = "APPLICATIONNAME";
    public static final String QUEUE_RESULTSOAPOBJ = "RESULTSOAPOBJ";
    public static final String QUEUE_REQUESTSOAPOBJ = "REQUESTSOAPOBJ";
    public static final String QUEUE_COLID = "COLID";
    public static final String QUEUE_APPREFID = "APPREFID";
    
    public static final int MAX_PROCESS_COUNT = 3;*/
    
    //Application Name Constants
    public static String APPLN_NAME_STR = "SAPQUEUEPROCESSOR";
    
    
    //Logging in to File Constants
    public static final int curr_date = Calendar.getInstance().get(Calendar.DATE);
    public static final String SUB_FOLDER_PATH = "Sapqueueprocessor"+File.separator+"Logs";
    public static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory()+ File.separator+SUB_FOLDER_PATH+ File.separator;
    //public static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory()+ File.separator;
    public static final String PATH_LOG_FILE = getPathLogFile();
    public static final String PATH_ERRLOG_FILE = curr_date+"_errorlog.txt";
    public static StringBuffer logStringBuffer = new StringBuffer();  
       
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
					Log.e(SapQueueProcessorConstants.SAPQUEUEPROCESSOR_TAG, e.toString());
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
    	Log.e(SapQueueProcessorConstants.SAPQUEUEPROCESSOR_TAG,text);
    	SapQueueProcessorConstants.writeLogToFile(SapQueueProcessorConstants.PATH_LOG_FILE, text);
    }
    
    /*public static void CallReciever(Context ctx){
    	String text = "30";
		 int integersec = Integer.parseInt(text.toString());
		 Intent intent2 = new Intent(ctx,SapQueueProcessorNWReceiver.class);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 234324243, intent2, 0);
		    AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
		        + (integersec * 1000), pendingIntent);    			  
		    Toast.makeText(ctx, "Alarm set in " + integersec + " seconds",
		        Toast.LENGTH_LONG).show();      
    }//
*/    public static void showErrorLog(String text){
    	Log.e(SapQueueProcessorConstants.SAPQUEUEPROCESSOR_ERRORTAG,text);
    	SapQueueProcessorConstants.writeLogToFile(SapQueueProcessorConstants.PATH_ERRLOG_FILE, text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }
        
    public static String getMobileIMEI(Context ctx){
    	String imei = "";
    	try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imei = mTelephonyMgr.getDeviceId(); 
			//imei = "000000000000000";
			//imei = "358472040687476";
			//imei = "358444040351783";
			//imei = "99000032030454";
			//imei = "990000320304548";
			SapQueueProcessorConstants.showLog("MobileIMEI: "+imei);
		} 
    	catch (Exception e) {
    		SapQueueProcessorConstants.showErrorLog("Error in getMobileIMEI : "+e.toString());
		}
    	finally{
    		if((imei == null)|| (imei.equalsIgnoreCase(""))){
    			imei = "000000000000000";
    		}
    	}
    	return imei;
    }//fn getMobileIMEI
    
    public static int getDisplayWidth(Context ctx){
        int dispwidth = 300;
    	try{
	    	Display display = ((WindowManager) ctx.getSystemService(ctx.WINDOW_SERVICE)).getDefaultDisplay();
	    	dispwidth = display.getWidth();
        }
        catch (Exception e) {
        	SapQueueProcessorConstants.showErrorLog("Error in getDisplayWidth : "+e.toString());
		}
    	finally{
    		if(dispwidth <= 0)
    			dispwidth = 300;
    	}
    	return dispwidth;
    }//fn getDisplayWidth
    
    
    public static String getApplicationIdentityParameter(Context ctx){
    	String appParams = "";
    	try {
    		appParams = "DEVICE-ID:"+SapQueueProcessorConstants.getMobileIMEI(ctx)+":DEVICE-TYPE:ANDROID:APPLICATION-ID:"+SapQueueProcessorConstants.APPLN_NAME_STR;
    		SapQueueProcessorConstants.showLog("App param: "+appParams);
		} 
    	catch (Exception e) {
    		SapQueueProcessorConstants.showErrorLog("Error in getApplicationIdentityParameter : "+e.toString());
		}
    	return appParams;
    }//fn getApplicationIdentityParameter
    
    public static String getSystemDateFormat(Context ctx, String format, String dateStr){
    	String formatedDateStr = "";
    	try {
    		SimpleDateFormat curFormater = new SimpleDateFormat(format); 
        	Date dateObj = curFormater.parse(dateStr); 
        	java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(ctx.getApplicationContext());
        	formatedDateStr = dateFormat.format(dateObj);
		} 
    	catch (Exception ea) {
    		SapQueueProcessorConstants.showErrorLog("Error in getSystemDateFormat : "+ea.toString());
    		formatedDateStr = dateStr;
		}
    	return formatedDateStr;
    }//fn getSystemDateFormat
    
    public static String getSystemDateFormatString(Context ctx, String dateStr){
    	String newDateStr = null;
    	try{
    		Time strTime = new Time();
			strTime.parse3339(dateStr);
			long millis = strTime.normalize(false);
			if(millis > 0){
				Date dateObj = new Date(millis);
				
				java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(ctx.getApplicationContext());
				newDateStr = dateFormat.format(dateObj);
				//SapQueueProcessorConstants.showLog("Formatted Date  : "+newDateStr);
			}
    	}
    	catch(Exception sfg){
    		SapQueueProcessorConstants.showErrorLog("Error in getSystemDateFormatString : "+sfg.toString());
    	}
    	finally{
    		if((newDateStr == null) || (newDateStr.equalsIgnoreCase(""))){
    			newDateStr = dateStr;
    		}
    	}
		return newDateStr;
    }//fn getSystemDateFormatString
    
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
	    			SapQueueProcessorConstants.showErrorLog("Error on getSerializableSoapObject1 : "+ewwe.toString());
	    		}
	    		
	    		if(baos != null){
	    			soapBytes = baos.toByteArray();
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapQueueProcessorConstants.showErrorLog("Error on getSerializableSoapObject2 : "+es.toString());
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
	    	         envelope.parse(xpp);
	    	         soapObj = (SoapObject)envelope.bodyIn;

	    		} catch (Exception ewwe) {
	    			SapQueueProcessorConstants.showErrorLog("Error on getDeSerializableSoapObject1 : "+ewwe.toString());
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapQueueProcessorConstants.showErrorLog("Error on getDeSerializableSoapObject2 : "+es.toString());
		}
    	return soapObj;
    }//fn getDeSerializableSoapObject

}//End of class SapQueueProcessorConstants    
