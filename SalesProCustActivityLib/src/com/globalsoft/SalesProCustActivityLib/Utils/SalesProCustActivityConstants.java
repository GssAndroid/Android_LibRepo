package com.globalsoft.SalesProCustActivityLib.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.globalsoft.SalesProCustActivityLib.R;

public final class SalesProCustActivityConstants {
	
	//Log Related Constants
    public static final String SALESPROCUSACT_TAG = "SalesProCustActivityLib ";
    public static final String SALESPROCUSACT_ERRORTAG = "SalesProCustActivityLib Error "; 
    
    //Screen Related Constants
    public static final int CUST_DETAIL_SCREEN = 4;
    
    //Text Size related Constants
    public static final int TEXT_SIZE_LABEL = 20;
    public static final int TEXT_SIZE_BUTTON = 20;
    public static final int TEXT_SIZE_TABLE_HEADER = 16;
    public static final int TEXT_SIZE_TABLE_ROW = 18;
    
    //EditText Width Height parameters
    public static final int EDIT_TEXT_WIDTH = 250;
    public static final int EDIT_TEXT_HEIGHT = 60;
    public static final int EDIT_TEXT_RIGHTMARGIN = 10;
    
    public static int TITLE_DISPLAY_WIDTH = 300;
    public static int SCREEN_CHK_DISPLAY_WIDTH = 850;
    
    public static final String SOAP_SERVICE_URL = "http://75.99.152.10:8050/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/110/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://75.99.152.10:8050/sap/bc/srt/wsdl/bndg_E0A8AEE275F3AEF1AE7900188B47B426/wsdl11/allinone/ws_policy/document?sap-client=110";
    public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";
    public static String SOAP_RESP_MSG = "";
    public static String SOAP_ERR_TYPE = "";
    
	//Log related functions
    public static void showLog(String text){
    	Log.e(SalesProCustActivityConstants.SALESPROCUSACT_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(SalesProCustActivityConstants.SALESPROCUSACT_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    } 
    //Application Name Constants
    public static String APPLN_NAME_STR = "";  
    
    public static String getApplicationIdentityParameter(Context ctx){
    	String appParams = "";
    	try {
    		appParams = "DEVICE-ID:"+SalesProCustActivityConstants.getMobileIMEI(ctx)+":DEVICE-TYPE:ANDROID:APPLICATION-ID:"+SalesProCustActivityConstants.APPLN_NAME_STR;
			SalesProCustActivityConstants.showLog("App param: "+appParams);
		} 
    	catch (Exception e) {
    		SalesProCustActivityConstants.showErrorLog(e.toString());
		}
    	return appParams;
    }//fn getApplicationIdentityParameter
    
    public static String getMobileIMEI(Context ctx){
    	String imenoStr = "", wifiStr = "", buildIdStr = "", buildDetStr = "";
		try{
			TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imenoStr = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
        	SalesProCustActivityConstants.showLog("Mobile Imeno : "+imenoStr);
		}
		catch(Exception sgh){
    		SalesProCustActivityConstants.showErrorLog("Error on getMobileIMEI : "+sgh.toString());
    	}
		finally{
        	if((imenoStr == null) || (imenoStr.equalsIgnoreCase(""))){
        		try{
        			WifiManager wm = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        			wifiStr = wm.getConnectionInfo().getMacAddress();
        			//SalesProCustActivityConstants.showLog("Mobile Wifi Id : "+wifiStr);
        			 
        			 buildIdStr = "25" + //we make this look like a valid IMEI
		            	Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
		            	Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
		            	Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
		            	Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
		            	Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
		            	Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
		            	Build.USER.length()%10 ; //13 digits
		            //SalesProCustActivityConstants.showLog("Mobile Device Id : "+buildIdStr);
		            
		            buildDetStr = Build.FINGERPRINT+" : "+Build.HARDWARE;
		            //SalesProCustActivityConstants.showLog("Mobile Device Str : "+buildDetStr);
		            
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
		    		SalesProCustActivityConstants.showLog("Mobile Unique Id for Imeno : "+imenoStr);
        		}
        		catch(Exception sfsg){
        			SalesProCustActivityConstants.showErrorLog("Error on getMobileIMEI 2: "+sfsg.toString());
        		}
        	}
		}
		return imenoStr;
    }//fn getMobileIMEI        

    public static int getDisplayWidth(Context ctx){
        int dispwidth = 300;
    	try{
	    	Display display = ((WindowManager) ctx.getSystemService(ctx.WINDOW_SERVICE)).getDefaultDisplay();
	    	dispwidth = display.getWidth();
        }
        catch (Exception e) {
        	SalesProCustActivityConstants.showErrorLog(e.toString());
		}
    	finally{
    		if(dispwidth <= 0)
    			dispwidth = 300;
    	}
    	return dispwidth;
    }
    
    public static void setWindowTitleTheme(Context ctx){
    	try{
    		if(SalesProCustActivityConstants.TITLE_DISPLAY_WIDTH > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH)
    			ctx.setTheme(R.style.LargeTitleTheme);
    	}
    	catch(Exception sghh){
    		SalesProCustActivityConstants.showErrorLog("Error on setWindowTitleTheme : "+sghh.toString());
    	}
    }//fn setWindowTitleTheme
    
}//End of class SalesProCustActivityConstants    
