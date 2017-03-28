package org.apache.cordova.constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Constants {
	
	//Log Related Constants
    public static final String GENERAL_TAG = "Sales Orders ";
    public static final String GENERAL_ERRORTAG = "Sales Order Error ";     
    
	//Log related functions
    public static void showLog(String text){
    	Log.e(Constants.GENERAL_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(Constants.GENERAL_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }
    
    public static String getMobileIMEI(Context ctx){
    	String imenoStr = "", wifiStr = "", buildIdStr = "", buildDetStr = "";
		try{
			TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imenoStr = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
			//imenoStr = "358472040687476";
			//imenoStr = "000000000000000";
			//imenoStr = "4BB99698EE13BC9F79448C030D67744C";
			Constants.showLog("Mobile Imeno : "+imenoStr);
		}
		catch(Exception sgh){
			Constants.showErrorLog("Error on getMobileIMEI : "+sgh.toString());
    	}
		finally{
        	if((imenoStr == null) || (imenoStr.equalsIgnoreCase(""))){
        		try{
        			String android_id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);    
        			Constants.showLog("Emulator/device"+android_id);
        			if (android_id == null) {     
        				// Emulator!    
        				Constants.showLog("Emulator!"+android_id);
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
        			Constants.showLog("Mobile Unique Id for Imeno : "+imenoStr);
        		}
        		catch(Exception sfsg){
        			Constants.showErrorLog("Error on getMobileIMEI 2: "+sfsg.toString());
        		}
        	}
		}
		return imenoStr;
    }//fn getMobileIMEI
}//
