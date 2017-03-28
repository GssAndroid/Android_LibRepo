package com.globalsoft.SapLibSoap.Utils;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Document;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.format.Time;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.globalsoft.SapLibSoap.R;
import com.globalsoft.SapLibSoap.Utils.Base64.InputStream;

public final class SapGenConstants {
	
	//Log Related Constants
    public static final String SAPGENERAL_TAG = "SalesPro ";
    public static final String SAPGENERAL_ERRORTAG = "SalesPro Error ";     
   
    //Mobile Related Constants
    public static String SAPGENERAL_MOBILE_IMEI = "";
    public static String MOBILE_IMEI = "";
    public static String EMULATOR_IMEI = "000000000000000";
    public static String SAP_GROUP_NAME = "SAPContacts";
    public static String SAP_RESPONSE_FULLSETS = "FULL-SETS";
    public static String SAP_RESPONSE_DELTASETS = "DELTA-SETS";
    public static String SAP_RESPONSE_DELTA_ROWS_ONLY = "DELTA-ROWS-ONLY";
       
    
    /*//Dynamic UI Related Constants
    public static final String COLUNM_NAME_SCR_TITLE = "SCRN-TITLE";
    public static final String CNTXT4_ATTR_TAG = "FIELD-ATTR";
    public static final String CNTXT4_LABE_TAG = "FIELD-LABE";
    public static final String CNTXT4_HINT_TAG = "FIELD-HINT";
    public static final String VALUE_DISPLAY_TAG = "DISPLAY";
    public static final String VALUE_SEARCHBAR_TAG = "SEARCHBAR";
    public static final String VALUE_DESCRIBED_TAG = "DESCRIBED";
    public static final String CNTXT3_DETAIL_TABULAR_OVERVIEW_SCREEN_TAG = "TABULAR-OVERVIEW";
    public static final String CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG = "TBLVW-MLN";
    public static final String CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG = "LISTVW-MLN";
    public static final String CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG = "LISTVW-1LN";
    public static final String CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG = "TBLVW-1LN";
    public static final String DEVICE_TYPE_SMALL_OV_TAG = "OVERVIEW-S";
    public static final String DEVICE_TYPE_WIDE_OV_TAG = "OVERVIEW-W";
    public static final String DEVICE_TYPE_SMALL_DETAIL_TAG = "DETAIL-S";
    public static final String DEVICE_TYPE_WIDE_DETAIL_TAG = "DETAIL-W";*/
    
    //Dynamic Table Name
    public static String PROD_UICONF_TABLE_NAME_STR = "productUiconf_lists";
    
    //TimeZone Constants
    public static ArrayAdapter tzoneadapter;
    public static String tz_str[] = null;
    //For Timer 
    public static int TIMER_CONST = 3000; // 10000 is 10secs or 30000 is 30secs or 60000 is 1 min
    
    //Soap response related constants
    public static final int RESP_TYPE_GET = 901;
    public static final int RESP_TYPE_UPDATE = 902;
    
    //Action Type Constants
    public static final int ACTION_GEN_ACTIVITY = 9001;
    public static final int LIB_ACTION_GALLERY_SCREEN = 9002;
    
    //Application Name Constants
    public static String APPLN_NAME_STR_MOBILEPRO = "MOBILEPRO";    
    public static String APPLN_NAME_STR_SALESPRO = "SALESPRO";    
    
    //API NAME
    public static String APPOINTMENT_GNRC = "APPOINTMENT-SYNC";
    
    //Screen Related Constants
    public static final int CAMERA_PIC_REQUEST = 1001;
    public static final int SAPDETAIL_SCREEN = 102;
    public static final int SOCREATION_SCREEN = 103; 
	public static final int SOCUSTACTIVITY_SCREEN = 104;
	public static final int SOCUSTACTCREATE_SCREEN = 105;   
    public static final int SO_CREATION_SCREEN = 3;   
    public static final int SALESORD_ITEM_SCREEN = 7;
    public static final int SALESORD_DETAIL_SCREEN = 8;
    public static final int SALESORD_CRT_CUSTSEL_SCREEN = 9;
    public static final int SALESORD_CRT_MATTSEL_SCREEN = 10;
    public static final int SALESORD_MATT_EDIT_SCREEN = 15;
    public static final int SO_CREATION_SCREEN_TABLET = 16;
    public static final int SO_OV_SCREEN_TABLET = 17;
    public static final int SO_OV_CNTXT_SCREEN_TABLET = 18;
    public static final int SO_PING = 19;
    public static final int CUSTCRDINFO_LAUNCH_SCREEN = 122;
    public static final int CONTACTLISTCLONE_LAUNCH_SCREEN = 1221;
    public static final int SETTINGS_SCREEN = 1222;
    public static final int EMAIL_SEND_ACTION = 1223;
    public static int FIRST_LANUCH_SCR = 0;
    public static int DIOG_FLAG = 0;
    public static final int RESP_TYPE_GET_STATUS = 101;    
    public static final int RESP_TYPE_GET_TASK = 108;  
    public static final int RESP_TYPE_GET_PROD_CNTX = 109;
    public static final int RESP_TYPE_GET_PROD_LIST = 110;

    public static final int RESP_TYPE_GET_CUST_LIST = 111;
    
    public static int FIRST_LANUCH_SCR_FOR_PROD_CAT = 0;
    public static int QUEUEPROCESSOR_FORST_LAUNCH = 0;
    public static final int PRODUCT_CAT_MATT_DETAIL_SCREEN_PH = 1200;
    public static final int PRODUCT_CAT_SCART_SCREEN_TBL = 1201;
    public static final int PRODUCT_CAT_GET_PRICEVALUE = 1202;
    public static final int PRODUCT_CAT_PLACE_ORDER = 1203;
    public static final int PRODUCT_MAIN_SCREEN_TABLET = 1204;
    public static final int PRODUCT_MAIN_SCREEN_PHONE = 1205;
    
    public static String SOAP_RESP_MSG = "";
    public static String SO_SOAP_RESP_MSG = "";
    public static String SOAP_ERR_TYPE = ""; 

    public static String title_offline = " - (Offline)";
    
    
   
    //Contacts Settings Related Constants
    public static boolean enterValue = false;
    public static boolean enterNpersValue = false;
    public static boolean internetConn = false;
    public static boolean QueueserviceStatus = false;
    public static boolean serviceStop = false;
    public static boolean setQpFlag =false;
    public static String platformphongap ="";
    public static String Cplatformphongap ="";
    public static String SOplatformphongap ="";
    public static int ContactID = -1;
    public static String AlternateID = "";
    public static String ContactRefIdStr = "";
    public static String SORefIdStr = "";
    public static boolean NotificationFlag = false;
    public static final int TEXT_SIZE_TABLE_ROW_PHONE = 12;
    
    //Task Sort Constants    
    public static int TASK_SORT_NAME = 1;   
    public static int TASK_SORT_DO = 2;
    public static int TASK_SORT_CNAME = 3;
    public static int TASK_SORT_DATE = 4;   
    
    //ContactsList Related Constants
    public static Vector selContactVect = new Vector();
    public static ArrayList selContactIdArr = new ArrayList();
    public static ArrayList CustDetailsArr = new ArrayList();
    public static ArrayList DiagnosisDetailsArr = new ArrayList();
    
    //Image Gallery Related Constants
    public static Vector galleryVect = new Vector();
    
    //Calendar Related Constants
    // @formatter:off
    public static final String[] TIME_ZONES = new String[] {
            "UTC",
            "America/Los_Angeles",
            "Asia/Beirut",
            "Pacific/Auckland" };
    // @formatter:on

    //Preference Related Constant
  	public static String PREFS_NAME_FOR_SALES_ORDER = "salesorderPrefs";
  	public static String PREFS_KEY_SALES_ORDER_FOR_MYSELF_GET = "SALES-ORDER-FOR-MYSELF-GET";
  	public static Elements serviceUrl, ActionUrl, TypeFname, ServiceNamespace, InputParamName, NotationDelimiter;
  	
	public static String PREFS_NAME_FOR_ACTIVITY = "activityrPrefs";
  	public static String PREFS_KEY_ACTIVITY_FOR_MYSELF_GET = "ACTIVITY-FOR-MYSELF-GET";
  	public static String PROD_CREATE_API_NAME="SALES-ORDER-CREATE";
  	public static String APPLN_PACKAGE_NAME = "com.globalsoft.ProductLib"; 
    public static String APPLN_BGSERVICE_NAME = "com.globalsoft.ProductLib.ProductBGService";
  	/*public static  String SOAP_SERVICE_URL = "";
  	public static  String SOAP_ACTION_URL = "";  	
  	public static  String SOAP_TYPE_FNAME = "";
  	public static  String SOAP_SERVICE_NAMESPACE = "";
  	public static  String SOAP_INPUTPARAM_NAME = "";
  	public static  String SOAP_NOTATION_DELIMITER = "";*/
    
    //SOAP CONSTANTS  
    /*public static final String SOAP_SERVICE_URL = "http://8.25.131.6:8051/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/111/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://8.25.131.6:8051/sap/bc/srt/wsdl/bndg_E1501D51C34940F18FF0005056860CF1/wsdl11/allinone/ws_policy/document?sap-client=111";
   */
    
  	//gss webservice url
  	//altered 23-08-2013
    public static final String SOAP_SERVICE_URL = "http://75.99.152.10:8050/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/110/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://75.99.152.10:8050/sap/bc/srt/wsdl/bndg_E0A8AEE275F3AEF1AE7900188B47B426/wsdl11/allinone/ws_policy/document?sap-client=110";
  	
  	
  	//drive medical
    /*public static final String SOAP_SERVICE_URL = "http://mobilesap.drivemedical.com:8000/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/001/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00_binding";
    public static final String SOAP_ACTION_URL = "http://mobilesap.drivemedical.com:8000/sap/bc/srt/wsdl/srvc_005056AB5D711EE390A967BE84A08FE9/wsdl11/allinone/ws_policy/document?sap-client=001";
    */
    //altered 23-08-2013
    public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";
    
  	
    //public static final String SOAP_SERVICE_URL = "http://TCW-MAS02.ThompsonCreek.com:8200/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/200/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00_bndng";
    //public static final String SOAP_ACTION_URL = "http://tcw-mas02.thompsoncreek.com:8200/sap/bc/srt/wsdl/srvc_005056B400141EE29F8516514BA8E787/wsdl11/allinone/ws_policy/document?sap-client=200";
    
  	 //altered 23-08-2013
    /*public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";*/
  	 //altered 23-08-2013
  	
  	/*public static void SapUrlConstants(Context myContext) {
  		try{
  			AssetManager mngr = myContext.getAssets();
  	  	    InputStream inputstrm;
  	  	    inputstrm = mngr.open("url_file.xml"); 
  	  	    int size = inputstrm.available();    		     
  	  	    byte[] buffer = new byte[size];   		
  	  	    inputstrm.read(buffer);   		
  	  	    inputstrm.close();

  	  	    String text = new String(buffer);    		    		    	
  	  	    Document doc = Jsoup.parse(text);    		        
  	  	    serviceUrl = doc.getElementsByTag("SOAP_SERVICE_URL");
  	  	    ActionUrl = doc.getElementsByTag("SOAP_ACTION_URL");
  	  	    TypeFname = doc.getElementsByTag("SOAP_TYPE_FNAME");
  	  	    ServiceNamespace = doc.getElementsByTag("SOAP_SERVICE_NAMESPACE");
  	  	    InputParamName = doc.getElementsByTag("SOAP_INPUTPARAM_NAME");
  	  	    NotationDelimiter = doc.getElementsByTag("SOAP_NOTATION_DELIMITER");    
  	  	    //assigning values to variables
       
  	  	    SapGenConstants.SOAP_SERVICE_URL = serviceUrl.text();
  	  	    SapGenConstants.SOAP_ACTION_URL = ActionUrl.text();
  	  	    SapGenConstants.SOAP_TYPE_FNAME = TypeFname.text();
  	  	    SapGenConstants.SOAP_SERVICE_NAMESPACE = ServiceNamespace.text();
  	  	    SapGenConstants.SOAP_INPUTPARAM_NAME = InputParamName.text();
  	  	    SapGenConstants.SOAP_NOTATION_DELIMITER = NotationDelimiter.text();
  		}catch(Exception sfg){
  			SapGenConstants.showErrorLog("Error in SapGenConstants constructor : "+sfg.toString());
		}
  	    
  	}//SapUrlConstants
*/  	
    
    //Text Size related Constants
    public static final int TEXT_SIZE_LABEL = 20;
    public static final int TEXT_SIZE_BUTTON = 20;
    public static final int TEXT_SIZE_TABLE_HEADER = 16;
    public static final int TEXT_SIZE_TABLE_ROW = 18;
    public static final int TEXT_SIZE_BIG_LABEL = 22;
    
    //EditText Width Height parameters
    public static final int EDIT_TEXT_WIDTH = 250;
    public static final int EDIT_TEXT_HEIGHT = 60;
    public static final int EDIT_TEXT_RIGHTMARGIN = 10;
    
    public static int TITLE_DISPLAY_WIDTH = 300;
    public static int SCREEN_CHK_DISPLAY_WIDTH = 850;
    
    
    //QUEUE STATUS CONSTANTS
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_INPROCESS = 1;
    public static int ACTIVITY_FLAG = 0;
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
    public static final String QUEUE_ERR_APPREFID = "ERRAPPREFID";
    public static final String QUEUE_ERR_MSG = "ERRMSG";
    public static final String COL_APPNAME = "appname";
    public static final String COL_STATUS = "status";
    public static final String COL_ID = BaseColumns._ID;
    
    public static  String apprefid = " ";
    public static  String appname = " ";
    public static  String packagename = " ";
    public static  String apiname = " ";
    public static  String classname = " ";
    public static  String ApprefID = "";
    public static  int colId = -1;
    public static  String altID = " ";
    public static int CountService = 0;
    public static String  platform = "";
    public static  String appreffidStr = " ";
    public static  int MIN_STR_CHAR = 0;
    
    //Log related functions
    public static void showLog(String text){
    	Log.e(SapGenConstants.SAPGENERAL_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(SapGenConstants.SAPGENERAL_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }
    
    public static void setWindowTitleTheme(Context ctx){
    	try{
    		if(SapGenConstants.TITLE_DISPLAY_WIDTH > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
    			ctx.setTheme(R.style.LargeTitleTheme);
    	}
    	catch(Exception sghh){
    		SapGenConstants.showErrorLog("Error on setWindowTitleTheme : "+sghh.toString());
    	}
    }//fn setWindowTitleTheme
        
    public static String getMobileIMEI(Context ctx){
    	String imenoStr = "", wifiStr = "", buildIdStr = "", buildDetStr = "";
		try{
			TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imenoStr = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
			//imenoStr = "358472040687476";
			//imenoStr = "000000000000000";
			SapGenConstants.showLog("Mobile Imeno : "+imenoStr);
		}
		catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error on getMobileIMEI : "+sgh.toString());
    	}
		finally{
        	if((imenoStr == null) || (imenoStr.equalsIgnoreCase(""))){
        		try{
        			String android_id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);    
        			SapGenConstants.showLog("Emulator/device"+android_id);
        			if (android_id == null) {     
        				// Emulator!    
        				SapGenConstants.showLog("Emulator!"+android_id);
        				imenoStr = "000000000000000";
        			}
        			else {  
	        			WifiManager wm = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
	        			wifiStr = wm.getConnectionInfo().getMacAddress();
	        			//SapGenConstants.showLog("Mobile Wifi Id : "+wifiStr);
	        			 
	        			 buildIdStr = "25" + //we make this look like a valid IMEI
			            	Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
			            	Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
			            	Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
			            	Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
			            	Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
			            	Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
			            	Build.USER.length()%10 ; //13 digits
			            //SapGenConstants.showLog("Mobile Device Id : "+buildIdStr);
			            
			            buildDetStr = Build.FINGERPRINT+" : "+Build.HARDWARE;
			            //SapGenConstants.showLog("Mobile Device Str : "+buildDetStr);
			            
			            String m_szLongID = wifiStr + buildIdStr+ buildDetStr;
			        	MessageDigest m = null;
			    		try {
			    			m = MessageDigest.getInstance("MD5");
			    		} catch (NoSuchAlgorithmException e) {
			    			e.printStackTrace();
			    		} 
			    		m.update(m_szLongID.getBytes(),0,m_szLongID.length());
			    		byte p_md5Data[] = m.digest();
			    		
			    		imenoStr = "";
			    		for (int i=0;i<p_md5Data.length;i++) {
			    			int b =  (0xFF & p_md5Data[i]);
			    			// if it is a single digit, make sure it have 0 in front (proper padding)
			    			if (b <= 0xF) imenoStr+="0";
			    			// add number to string
			    			imenoStr+=Integer.toHexString(b); 
			    		}
			    		
			    		imenoStr = imenoStr.toUpperCase();
        			}
			    		SapGenConstants.showLog("Mobile Unique Id for Imeno : "+imenoStr);
        		}
        		catch(Exception sfsg){
        			SapGenConstants.showErrorLog("Error on getMobileIMEI 2: "+sfsg.toString());
        		}
        	}
		}
		return imenoStr;
    }//fn getMobileIMEI
    
    
    public static void stopNotificationAlert(int id){
    	try{
    		mNotificationManager.cancel(id);   		
	    }
	    catch(Exception sff){
	    	SapGenConstants.showErrorLog("Error in stopNotificationAlert : "+sff.toString());
	    }
    }//fn stopNotificationAlert
    
    static NotificationManager mNotificationManager = null;
    public static NotificationManager notificationAlert(Context ctx){
    	try{
	    	String ns = Context.NOTIFICATION_SERVICE;
			mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
	    }
	    catch(Exception sff){
	    	SapGenConstants.showErrorLog("Error in notificationAlert : "+sff.toString());
	    }
    	return mNotificationManager;
    }//fn notificationAlert
    
    public static boolean getSoapResponseSucc_Err(String soapMsg){
    	boolean resMsgErr = false;
    	try{
        	if((soapMsg.indexOf("Type=A") > 0) || (soapMsg.indexOf("Type=E") > 0) || (soapMsg.indexOf("Type=X") > 0)){
        		resMsgErr = true;
            }else if(soapMsg.indexOf("Type=S") > 0){
            	resMsgErr = false;
            }   
	    }
	    catch(Exception sffe){
	    	SapGenConstants.showErrorLog("Error in getSoapResponseSucc_Err : "+sffe.toString());
	    }
    	return resMsgErr;
    }//fn getSoapResponseSucc_Err
    
    public static SpannableString getUnderlinedString(String normalStr){
    	SpannableString content = null;    	
    	try {
    		if((normalStr != null) && (!normalStr.equalsIgnoreCase(""))){
				content = new SpannableString(normalStr);
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
    		}
		} catch (Exception aasd) {
			SapGenConstants.showErrorLog("Error in getUnderlinedString : "+aasd.toString());
		}
    	return content;
    }//fn getUnderlinedString
    
    public static String getApplicationIdentityParameterDiagnosis(Context ctx, String appNameStr){
    	String appParams = "";
    	try {
    		appParams = "DEVICE-ID:"+SapGenConstants.getMobileIMEI(ctx)+":DEVICE-TYPE:ANDROID:APPLICATION-ID:"+appNameStr+":MODE:D";
			SapGenConstants.showLog("App param: "+appParams);
		} 
    	catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
		}
    	return appParams;
    }//fn getApplicationIdentityParameter
   
    public static String getApplicationIdentityParameter(Context ctx, String appNameStr){
    	String appParams = "";
    	try {
    		appParams = "DEVICE-ID:"+SapGenConstants.getMobileIMEI(ctx)+":DEVICE-TYPE:ANDROID:APPLICATION-ID:"+appNameStr;
			SapGenConstants.showLog("App param: "+appParams);
		} 
    	catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
		}
    	return appParams;
    }//fn getApplicationIdentityParameter
    
    public static int getDisplayWidth(Context ctx){
        int dispwidth = 300;
    	try{
	    	Display display = ((WindowManager) ctx.getSystemService(ctx.WINDOW_SERVICE)).getDefaultDisplay();
	    	dispwidth = display.getWidth();
        }
        catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
		}
    	finally{
    		if(dispwidth <= 0)
    			dispwidth = 300;
    	}
    	return dispwidth;
    }
    
    //check for internet connection
    public static boolean checkConnectivityAvailable(Context ctx) { 
    	boolean isConnAvail = false;
    	try{
	    	ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService (Context.CONNECTIVITY_SERVICE);
	    	// ARE WE CONNECTED TO THE NET
	    	if (conMgr.getActiveNetworkInfo() != null
	    	&& conMgr.getActiveNetworkInfo().isAvailable()
	    	&& conMgr.getActiveNetworkInfo().isConnected()) {
	    		isConnAvail = true;
	    	} else {
	    		//SapGenConstants.showLog("Internet Connection Not Present");
	    		isConnAvail = false;
	    	}
    	}
    	catch(Exception sgg){
    		SapGenConstants.showErrorLog("Error on checkConnectivityAvailable : "+sgg.toString());
    	}
    	return isConnAvail;
    }//fn checkConnectivityAvailable
    
    
    public static String getSystemDateFormat(Context ctx, String format, String dateStr){
    	String formatedDateStr = "";
    	try {
    		SimpleDateFormat curFormater = new SimpleDateFormat(format); 
        	Date dateObj = curFormater.parse(dateStr); 
        	java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(ctx.getApplicationContext());
        	formatedDateStr = dateFormat.format(dateObj);
		} 
    	catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
    		formatedDateStr = dateStr;
		}
    	return formatedDateStr;
    }//fn getSystemDateFormat
    /*
    public static String getSapDateOrTimeFormat(long dateL, boolean dateFlag){
        String dateStr = "0000-00-00";
        try{
            if(dateL > 0){
                Date dateT = new Date(dateL);
                if(dateT != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateT);
                    int month = calendar.get(Calendar.MONTH)+1;
                    if(dateFlag == true)
                        dateStr = calendar.get(Calendar.YEAR)+"-"+String.valueOf(month)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
                    else
                        dateStr = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
                }
                else{
                	if(dateFlag == false)
                		dateStr = "00:00";
                }
            }
            else{
            	if(dateFlag == false)
            		dateStr = "00:00";
            }
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in getSapDateOrTimeFormat : "+asd.toString());
        }
        return dateStr;
    }//fn getSapDateOrTimeFormat
    */
    
    public static String[] getSapDateOrTimeFormat(long dateL){
        String[] dateTimeArr = new String[]{"0000-00-00", "00:00"};
        try{
            if(dateL > 0){
                Date dateT = new Date(dateL);
                SapGenConstants.showLog("Date : "+dateT.toString()+" : "+System.currentTimeMillis());
                if(dateT != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dateL); //.setTime(dateT);
                    
                    int month = calendar.get(Calendar.MONTH)+1;
                    
                    dateTimeArr[0] = calendar.get(Calendar.YEAR)+"-"+String.valueOf(month)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
                    dateTimeArr[1] = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND); 
                }
            }
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in getSapDateOrTimeFormat : "+asd.toString());
        }
        return dateTimeArr;
    }//fn getSapDateOrTimeFormat
    
    public static ArrayAdapter getTimeZoneAdapter(Context ctx){
    	ArrayAdapter <CharSequence> tzoneadapter = null;
    	try {
    		tzoneadapter = new ArrayAdapter <CharSequence> (ctx, android.R.layout.simple_spinner_item );
    		tzoneadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		String[] TZ = TimeZone.getAvailableIDs();
	        ArrayList<String> TZ1 = new ArrayList<String>();
			if(tzoneadapter != null){
		        for(int i = 0; i < TZ.length; i++) {
		            if(!(TZ1.contains(TimeZone.getTimeZone(TZ[i]).getDisplayName()))) {
		                TZ1.add(TimeZone.getTimeZone(TZ[i]).getDisplayName());
		                tzoneadapter.add(TimeZone.getTimeZone(TZ[i]).getDisplayName());
		                
		                long smsDatTime = System.currentTimeMillis();
		    			Date l_date = new Date(smsDatTime);
		        		Calendar c = Calendar.getInstance(); 
		        		TimeZone timeZone = TimeZone.getTimeZone(TZ[i]);
		        		SapGenConstants.showLog("timez_offset :" + timeZone.getOffset(l_date.getTime()));
		                
		                SapGenConstants.showLog("TimeZone :"+TimeZone.getTimeZone(TZ[i]).getDisplayName());
		            }
		        }
			}
		} 
    	catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
		}
    	return tzoneadapter;
    }//fn getTimeZoneAdapter
    
    
    public static String getImageAsEncodedDataStr(Bitmap imgBmp){
    	String encodedSignImageStr = "";
    	try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();   
			imgBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //imgBmp is the bitmap object    
			byte[] b = baos.toByteArray();
			encodedSignImageStr = Base64.encodeBytes(b);
			baos.close();
			SapGenConstants.showLog("encodedSignImageStr : "+encodedSignImageStr);
		} catch (Exception eaa) {
			SapGenConstants.showErrorLog("Error on getImageAsEncodedDataStr : "+eaa.toString());
		}
    	return encodedSignImageStr;
    }//fn getImageAsEncodedDataStr
    
    public static boolean getSoapResponseMessageType(String soapMsg){
    	boolean resMsgErr = false;
    	try{
    		if((soapMsg != null) && (!soapMsg.equalsIgnoreCase(""))){
	        	if((soapMsg.indexOf("Type=A") > 0) || (soapMsg.indexOf("Type=E") > 0) || (soapMsg.indexOf("Type=X") > 0)){
	        		resMsgErr = true;
	            }else if(soapMsg.indexOf("Type=S") > 0){
	            	resMsgErr = false;
	            }   
    		}
	    }
	    catch(Exception sffe){
	    	SapGenConstants.showErrorLog("Error in getSoapResponseMessageType : "+sffe.toString());
	    }
    	return resMsgErr;
    }//fn getSoapResponseMessageType
    
    public static boolean getUpdateServerResponse(Context ctx, SoapObject soap){	
    	boolean errMsgFlag = false;
    	String soapMsgStr = "", taskErrorMsgStr = "";
        try{ 
        	if(soap != null){  
        		soapMsgStr = soap.toString();
        		String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[37];
                int propsCount = 0, errCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;   
                
                for (int i = 0; i < soap.getPropertyCount(); i++) {  
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    SapGenConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
                        	SapGenConstants.showLog(j+" : "+pii.getProperty(j).toString());
                            if(j >= 3){
                                result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                firstIndex = firstIndex + 3;
                                result = result.substring(firstIndex);
                                //SapTasksConstants.Log(result);
                                
                                resC = 0;
                                indexA = 0;
                                indexB = result.indexOf(delimeter);
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    //SapTasksConstants.Log(resC+" : "+res);
                                    resArray[resC] = res;
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    resC++;
                                }
                                int endIndex = result.lastIndexOf(';');
                                resArray[resC] = result.substring(indexA,endIndex);
                                //SapTasksConstants.Log(resC+" : "+resArray[resC]);
                            }
                            else if(j < 2){
                                String errorMsg = pii.getProperty(j).toString();
                                SapGenConstants.showLog("Inside J == "+j+" : "+propsCount+" : "+errorMsg);
                                int errorFstIndex = errorMsg.indexOf("Message=");
                                if(errorFstIndex > 0){
                                	int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
                                    taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex).trim();
                                    SapGenConstants.showErrorDialog(ctx, taskErrorMsgStr);
                                    /*
                                    if((errorMsg.indexOf("Type=A") > 0) || (errorMsg.indexOf("Type=E") > 0) || (errorMsg.indexOf("Type=S") > 0)){
                                        int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
                                        if(taskErrorMsgStr.equalsIgnoreCase(""))
                                            taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex).trim();
                                    }
                                    
                                    errCount = propsCount-2;
                                                                            
                                    SapGenConstants.showErrorLog(taskErrorMsgStr);
                                    
                                    if(errCount < 0)
                                        errCount = 0;
                                    System.out.println("errCount : "+errCount+" : "+j);    
                                    if(j == errCount){
                                    	SapGenConstants.showErrorDialog(this, taskErrorMsgStr);
                                        diagdispFlag = true;
                                    }
                                    */
                                }
                            }
                        }
                    }
                }
        	}
        }
        catch(Exception sff){
            SapGenConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{
        	try{
        		errMsgFlag = SapGenConstants.getSoapResponseMessageType(soapMsgStr);
        		/*
        		taskErrorMsgStr = taskErrorMsgStr.trim();
        		SapGenConstants.showLog("Inside Finally : "+taskErrorMsgStr);
                if(taskErrorMsgStr.equalsIgnoreCase("")){
                	onClose();
                }
                else if(taskErrorMsgStr.indexOf("Maintained") >= 0){
                	if(diagdispFlag == false)
                		SapGenConstants.showErrorDialog(this, taskErrorMsgStr);
                	
                	onClose();
                }
                */
            }
            catch(Exception asf){}
        	return errMsgFlag;
        }
    }//fn updateServerResponse 
        
    public static String[] getContactOrgDetails(Context ctx, String contactId){
		String strOrgName = "", strOrgTitle = "", strOrgType = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = "";
         		try {
					strOrgName = orgName;
					strOrgTitle = orgTitle;
					orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
					strOrgType = orgType;
				} catch (Exception sse) {
					SapGenConstants.showErrorLog("Error in getContactOrgDetails1:"+sse.getMessage());
					strOrgType = orgType;//2 is a others
					strOrgName = orgName;
					strOrgTitle = orgTitle;
				}     		
         	} 
         	orgCur.close();
         	
         	if(strOrgName == null || strOrgName.length() == 0){
    	 		strOrgName = "";
    		}
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}
         	result[0] = strOrgName;
         	result[1] = strOrgType;
         	result[2] = strOrgTitle;
		} 
    	catch (Exception ssee) {
    		SapGenConstants.showErrorLog("Error in getContactOrgDetails2:"+ssee.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactOrgDetails
	
	public static String[] getContactPhDetails(Context ctx, String contactId){
		String strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "";
		String[] result = new String[4];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Phone no
    		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    						null, 
    						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    						new String[]{String.valueOf(contactId)}, null);
            while (pCur.moveToNext()) {
            	String phValue = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            	String phType = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            	if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME))){
            		strPhoneHome = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
    	 	    	strPhoneOther = phValue;
    	 	    }
            } 
            pCur.close();
            if(strPhoneHome == null || strPhoneHome.length() == 0){
            	strPhoneHome = "";
    		}
            if(strPhoneMob == null || strPhoneMob.length() == 0){
            	strPhoneMob = "";
    		}
            if(strPhoneWork == null || strPhoneWork.length() == 0){
            	strPhoneWork = "";
    		}
            if(strPhoneOther == null || strPhoneOther.length() == 0){
            	strPhoneOther = "";
    		}
         	result[0] = strPhoneHome;
         	result[1] = strPhoneMob;
         	result[2] = strPhoneWork;
         	result[3] = strPhoneOther;
		} 
    	catch (Exception ssq) {
    		SapGenConstants.showErrorLog("Error in getContactPhDetails:"+ssq.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
		}
    	return result;
    }//fn getContactPhDetails
	
	public static String[] getContactEmailsDetails(Context ctx, String contactId){
		String strEmailHome = "", strEmailWork = "", strEmailOther = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{String.valueOf(contactId)}, null); 
    		while (emailCur.moveToNext()) { 
    		    // This would allow you get several email addresses
    	            // if the email addresses were stored in an array
    		    String email = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    	 	    String emailType = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_HOME))){
    	 	    	strEmailHome = email;
    	 	    }
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	}
    		emailCur.close();
    		if(strEmailHome == null || strEmailHome.length() == 0){
    			strEmailHome = "";
    		}
    		if(strEmailWork == null || strEmailWork.length() == 0){
    			strEmailWork = "";
    		}
    		if(strEmailOther == null || strEmailOther.length() == 0){
    			strEmailOther = "";
    		}
         	result[0] = strEmailHome;
         	result[1] = strEmailWork;
         	result[2] = strEmailOther;
		} 
    	catch (Exception ssqw) {
    		SapGenConstants.showErrorLog("Error in getContactEmailsDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactEmailsDetails
	
	public static String[] getContactAddressDetails(Context ctx, String contactId){
		String strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "";
		String[] result = new String[6];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Address Details
    		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    		String[] addrWhereParams = new String[]{String.valueOf(contactId), 
    			ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
    		Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
    	                null, addrWhere, addrWhereParams, null); 
    		while(addrCur.moveToNext()) {
    	 		String street = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
    	 		String city = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
    	 		String region = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
    	 		String postalCode = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
    	 		String country = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
    	 		String type = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME))){
             		strStreet = street;
             		strCity = city;
             		strRegion = region;
             		strPostalCode = postalCode;
             		strCountry = country;
             		strAddType = type;// 1 is home
    	 	    }     	
    	 	} 
    		addrCur.close();
    		if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
    	 	    	 	
    	 	addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
	                null, addrWhere, addrWhereParams, null);
    		if(strStreet.length() <= 0 && strCity.length() <= 0 && strRegion.length() <= 0 && strPostalCode.length() <= 0 && strCountry.length() <= 0){  
    			while(addrCur.moveToNext()) {
	    	 		String street = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	    	 		String city = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	    	 		String region = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	    	 		String postalCode = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	    	 		String country = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	    	 		String type = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
	    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK))){
	             		strStreet = street;
	             		strCity = city;
	             		strRegion = region;
	             		strPostalCode = postalCode;
	             		strCountry = country;
	             		strAddType = type;// 1 is home
	    	 	    }     	
	    	 	}     			  			
    		}
    		addrCur.close();
    		
    	 	if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
         	result[0] = strStreet;
         	result[1] = strCity;
         	result[2] = strRegion;
         	result[3] = strPostalCode;
         	result[4] = strCountry;
         	result[5] = strAddType;
		} 
    	catch (Exception ssqw) {
    		SapGenConstants.showErrorLog("Error in getContactAddressDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
         	result[4] = "";
         	result[5] = "";
		}
    	return result;
    }//fn getContactAddressDetails
    
    public static String getSystemDateFormatString(Context ctx, String dateStr){
    	String newDateStr = null;
    	try{
    		Time strTime = new Time();
			strTime.parse3339(dateStr);
			//strTime.parse(dateStr);
			long millis = strTime.normalize(false);
			if(millis > 0){
				Date dateObj = new Date(millis);
				
				java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(ctx.getApplicationContext());
				newDateStr = dateFormat.format(dateObj);
				//ServiceProConstants.showLog("Formatted Date  : "+newDateStr);
			}
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in getSystemDateFormatString : "+sfg.toString());
    	}
    	finally{
    		if((newDateStr == null) || (newDateStr.equalsIgnoreCase(""))){
    			newDateStr = dateStr;
    		}
    	}
		return newDateStr;
    }//fn getSystemDateFormatString
    
    public static String getDateFormatForSAP(int mYear_dp, int mMonth_dp, int mDay_dp, int mHour_dp, int mMinute_dp){
		String taskDateStrValue = "";  
    	try {                      
        	// converting the datestring from the picker to a long: 
        	Calendar cal_date = Calendar.getInstance(); 
        	cal_date.set(Calendar.DAY_OF_MONTH, mDay_dp); 
        	cal_date.set(Calendar.MONTH, mMonth_dp-1); 
        	cal_date.set(Calendar.YEAR, mYear_dp); 
        	cal_date.set(Calendar.HOUR_OF_DAY, mHour_dp);
        	cal_date.set(Calendar.MINUTE, mMinute_dp);
        	Long lDate = cal_date.getTime().getTime(); 
        	//ServiceProConstants.showLog("lDate "+ lDate);
        	taskDateStrValue = SapGenConstants.getTaskDateStringFormat(lDate, true).trim();
		} 
    	catch (Exception e) {
    		SapGenConstants.showErrorLog(e.toString());
		}
    	return taskDateStrValue;
    }//fn getDateFormatForSAP
    
    public static String getTaskDateStringFormat(long dateL, boolean noHourFlag){
        String dateStr = "0000-00-00";
        try{
            if(dateL > 0){
                Date dateT = new Date(dateL);
                if(dateT != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateT);
                    int month = calendar.get(Calendar.MONTH)+1;
                    if(noHourFlag == true){
                    	String monthVal = String.valueOf(month);
                    	if(monthVal.length() == 1){
                    		monthVal = "0"+monthVal;
                    	}
                    	String dayVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                    	if(dayVal.length() == 1){
                    		dayVal = "0"+dayVal;
                    	}
                        dateStr = calendar.get(Calendar.YEAR)+"-"+monthVal+"-"+dayVal+" ";
                    }
                    else{
                    	String monthVal = String.valueOf(month);
                    	if(monthVal.length() == 1){
                    		monthVal = "0"+monthVal;
                    	}
                    	String dayVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                    	if(dayVal.length() == 1){
                    		dayVal = "0"+dayVal;
                    	}
                        dateStr = calendar.get(Calendar.YEAR)+"-"+monthVal+"-"+dayVal+" ";
                        dateStr += calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
                    }
                }
            }
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in getTaskDateStringFormat : "+asd.toString());
        }
        return dateStr;
    }//fn getTaskDateStringFormat
    
    public static String getMonthValue(int month_value){
    	String month_dec = null;
    	if(month_value == 1){
    		month_dec = "Jan";
    	}
    	else if(month_value == 2){
    		month_dec = "Feb";
    	}
    	else if(month_value == 3){
    		month_dec = "Mar";
    	}
    	else if(month_value == 4){
    		month_dec = "Apr";
    	}
    	else if(month_value == 5){
    		month_dec = "May";
    	}
    	else if(month_value == 6){
    		month_dec = "Jun";
    	}
    	else if(month_value == 7){
    		month_dec = "Jul";
    	}
    	else if(month_value == 8){
    		month_dec = "Aug";
    	}
    	else if(month_value == 9){
    		month_dec = "Sep";
    	}
    	else if(month_value == 10){
    		month_dec = "Oct";
    	}
    	else if(month_value == 11){
    		month_dec = "Nov";
    	}
    	else if(month_value == 12){
    		month_dec = "Dec";
    	}
    	return month_dec;
    }//fn getMonthValue    

    public static void soapResponse(Context ctx, SoapObject soap, boolean offline){
		String taskErrorMsgStr="", errorDesc="", errType="";
        if(soap != null){
        	//ServiceProConstants.showLog("Count : "+soap.getPropertyCount());
            try{ 
                String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[50];
                int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;
                for (int i = 0; i < soap.getPropertyCount(); i++) {                
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    //ServiceProConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
                            if(j > 0){
                                result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                firstIndex = firstIndex + 3;
                                result = result.substring(firstIndex);
                                //SapTasksConstants.Log(result);
                                
                                resC = 0;
                                indexA = 0;
                                indexB = result.indexOf(delimeter);
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    resArray[resC] = res;
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    resC++;
                                }
                                int endIndex = result.lastIndexOf(';');
                                resArray[resC] = result.substring(indexA,endIndex);
                            }
                            else if(j == 0){
                                String errorMsg = pii.getProperty(j).toString();
                                //ServiceProConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                            	int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SapGenConstants.showLog("Msg:"+taskErrorMsgStr);
	                                /*if(!offline)
	                                	ServiceProConstants.showErrorDialog(ctx, taskErrorMsgStr);*/
	                            	//errorDesc = taskErrorMsgStr;	
	                            	SOAP_RESP_MSG = taskErrorMsgStr; 
                                }
	                            int typeFstIndex = errorMsg.indexOf("Type=");
	                            if(typeFstIndex > 0){
	                            	int typeLstIndex = errorMsg.indexOf(";", typeFstIndex);
	                                String taskErrorTypeMsgStr = errorMsg.substring((typeFstIndex+"Type=".length()), typeLstIndex);
	                                //SOAP_RESP_MSG = taskErrorTypeMsgStr;
	                                //ServiceProConstants.showLog("Type:"+taskErrorTypeMsgStr);
	                                errType = taskErrorTypeMsgStr;
	                            	SOAP_ERR_TYPE = errType;
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception sff){
            	SapGenConstants.showErrorLog("On soapResponse : "+sff.toString());
            }
        }
    }//fn soapResponse
    
	/*************************************************************************
	 * 
	 * Offline Data Processing thro QueueProcessor functions
	 * 
	 *******************************************************************************/
	
    public static boolean fileExists(String URLName){
        try {
        	HttpURLConnection.setFollowRedirects(false);
        	// note : you may also need
        	//        HttpURLConnection.setInstanceFollowRedirects(false)
        	HttpURLConnection con =
        			(HttpURLConnection) new URL(URLName).openConnection();
        	con.setRequestMethod("HEAD");
        	return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception seff) {
        	SapGenConstants.showErrorLog("URLName : "+URLName);
        	SapGenConstants.showErrorLog("On fileExists : "+seff.toString());
        	return false;
        }
     }//fn fileExists
    
	/*public static void saveOfflineContentToQueueProcessor(Context ctx, String appName, String packageName, String appRefId, String className, String apiName, SoapObject soapObj){
        byte[] soapBytes = null;
        try{
            if(soapObj != null){
                soapBytes = SapGenConstants.getSerializableSoapObject(soapObj);
                if(soapBytes != null){
                    ContentResolver resolver = ctx.getContentResolver();
                    ContentValues val = new ContentValues();
                    if((appRefId != null) && (!appRefId.equalsIgnoreCase(""))){
                    	int refId = Integer.parseInt(appRefId);
                    	 val.put(SapQueueProcessorContentProvider.COL_APPREFID, appRefId);
                    }
                    val.put(SapQueueProcessorContentProvider.COL_APPNAME, appName);
                    val.put(SapQueueProcessorContentProvider.COL_PCKGNAME, packageName);
                    val.put(SapQueueProcessorContentProvider.COL_CLASSNAME, className);
                    val.put(SapQueueProcessorContentProvider.COL_FUNCNAME, apiName);
                    val.put(SapQueueProcessorContentProvider.COL_SOAPDATA, soapBytes);
                    resolver.insert(SapQueueProcessorContentProvider.CONTENT_URI, val);
                }
                else
                	SapGenConstants.showErrorLog("Soap byte Conversion is Null");
            }
            else
            	SapGenConstants.showErrorLog("Offline Soap Object is Null");
        }
        catch(Exception sgh){
        	SapGenConstants.showErrorLog("Error in saveOfflineContent : "+sgh.toString());
        }
    }//fn saveOfflineContentToQueueProcessor
	
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
	    			SapGenConstants.showErrorLog("Error on getSerializableSoapObject1 : "+ewwe.toString());
	    		}
	    		
	    		if(baos != null){
	    			soapBytes = baos.toByteArray();
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapGenConstants.showErrorLog("Error on getSerializableSoapObject2 : "+es.toString());
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
	    	         
 	    			 ByteArrayInputStream inputStream = new ByteArrayInputStream(soapBytes);
	    	         xpp.setInput(inputStream, "UTF-8");
	    	         
	    	         envelope.parse(xpp);
	    	         soapObj = (SoapObject)envelope.bodyIn;

	    		} catch (Exception ewwe) {
	    			SapGenConstants.showErrorLog("Error on getDeSerializableSoapObject1 : "+ewwe.toString());
	    		}
    		}
		} 
    	catch (Exception es) {
    		SapGenConstants.showErrorLog("Error on getDeSerializableSoapObject2 : "+es.toString());
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
                
                whereStr = SapGenConstants.COL_APPNAME + " = ? And "+SapGenConstants.COL_STATUS+ " = ?";
                whereParams = new String[]{appName, String.valueOf(SapGenConstants.STATUS_IDLE)}; 
            }
            
            //SapGenConstants.showLog("status before : "+status);
            if(status < SapGenConstants.STATUS_IDLE)
                status = SapGenConstants.STATUS_IDLE;
            else if(status > SapGenConstants.STATUS_HIGHPRIORITY)
                status = SapGenConstants.STATUS_HIGHPRIORITY;
            //SapGenConstants.showLog("status after : "+status);
            
            ContentValues updateContent = new ContentValues();
            updateContent.put(SapGenConstants.COL_STATUS, status);
 
            ContentResolver resolver = ctx.getContentResolver();
            int rows = resolver.update(uri, updateContent, whereStr, whereParams);
            
            SapGenConstants.showLog("colId : "+colId);
            SapGenConstants.showLog("appName : "+appName);
            SapGenConstants.showLog("Row Updated : "+rows);
        }
        catch(Exception qsewe){
        	SapGenConstants.showErrorLog("Error in updateSelectedRowStatus : "+qsewe.toString());
        }    
    }//fn updateSelectedRowStatus

    public static int getApplicationQueueCount(Context ctx, String appName){
        int count = -1;
        Cursor cursor = null;
        try{
            if(appName == null)
                appName = "";
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = SapGenConstants.COL_APPNAME + " = ? And ("+SapGenConstants.COL_STATUS+ " != ? And "+SapGenConstants.COL_STATUS+ " != ? )";
            String[] selectionParams = new String[]{appName,  String.valueOf(SapGenConstants.STATUS_SENDTOSERVER ),  String.valueOf(SapGenConstants.STATUS_COMPLETED )};
            String[] projection = new String[]{SapGenConstants.COL_ID}; 
            
            cursor = resolver.query(uri, projection, selection, selectionParams, null);
            
            if(cursor != null){
                count = cursor.getCount();
            }
        }
        catch(Exception sfag){
        	SapGenConstants.showErrorLog("Error in getApplicationQueueCount : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return count;
    }//fn getApplicationQueueCount
*/    
}//End of class SapGenConstants    

