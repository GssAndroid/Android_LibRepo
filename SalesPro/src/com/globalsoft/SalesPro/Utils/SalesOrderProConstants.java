package com.globalsoft.SalesPro.Utils;

import java.io.InputStream;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.SalesPro.R;
import com.globalsoft.SalesPro.Observer.SalesProSmsObserver;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public final class SalesOrderProConstants {
	
	//Log Related Constants
    public static final String SALESORDPRO_TAG = "SalesPro ";
    public static final String SALESORDPRO_ERRORTAG = "SalesPro Error ";     
   
    //Mobile Related Constants
    public static String SALESORDPRO_MOBILE_IMEI = "";
    private static AlertDialog alertDialog;
	private static AlertDialog.Builder builder;
    
    //Method call Constants
    public static int TASKS_DOWNLOAD_METHOD = 0;
    public static int TASKS_UPDATE_METHOD = 1;
    
    //REPORTS SORT CONSTANTS
    public static int REPORTS1_SORT_DATE = 0;
    public static int REPORTS1_SORT_SERVORD = 1;
    public static int REPORTS1_SORT_CUSTOMER = 2;    
  
    //Screen Related Constants
    public static final int MAIN_SCREEN = 1;
    public static final int TASK_DETAIL_SCREEN = 2;
    public static final int SO_CREATION_SCREEN = 3;
    public static final int CUST_DETAIL_SCREEN = 4;
    public static final int PRICELIST_DETAIL_SCREEN = 5;
    public static final int PRICEVIEWLIST_DETAIL_SCREEN = 6;
    public static final int STOCKLIST_DETAIL_SCREEN = 7;
    public static final int STOCKVIEW_DETAIL_SCREEN = 8;
    public static final int SALESORD_ITEM_SCREEN = 9;
    public static final int SALESORD_DETAIL_SCREEN = 10;
    public static final int SALESORD_CRT_CUSTSEL_SCREEN = 11;
    public static final int SALESORD_CRT_MATTSEL_SCREEN = 12;
    public static final int PRICEVIEWLIST_DETAIL_SCREEN_TABLET = 13;
    public static final int STOCKLIST_DETAIL_SCREEN_TABLET = 14;
    
    public static final int ERPCONTACTS_LAUNCH_SCREEN = 121;
    public static final int CUSTCRDINFO_LAUNCH_SCREEN = 122;
    public static final int SALESORDLIST_LAUNCH_SCREEN = 123;
    public static final int PRICELIST_LAUNCH_SCREEN = 124;
    public static final int QUOTATION_LAUNCH_SCREEN = 125;
    public static final int STOCKLIST_LAUNCH_SCREEN = 126;
    public static final int PRICELIST_LAUNCH_SCREEN_TABLET = 127;
    public static final int SALESPRO_EMAIL_SCREEN_PHONE = 128;
    
    //Contact Screen Related Constants
    public static final int SAPDETAIL_SCREEN = 102;
    public static final int SOCREATION_SCREEN = 103;
	public static final int SOCUSTACTIVITY_SCREEN = 104;
    public static final int SOCUSTACTCREATE_SCREEN = 105;
    public static final int ALERTDISP_SCREENFORUPDATES = 106;
    public static final int ALERTDISP_SCREENCUSSEL = 107;
    public static final int SAPABOUT_SCREEN = 108;
    
  //Preference Related Constant
  	public static String PREFS_NAME_FOR_PRICE_STOCK = "PriceListPrefs";
  	public static String PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET = "PRICE-LIST-FOR-MYSELF-GET";
  	
  	public static String PREFS_NAME_FOR_PRICE_DETAIL_STOCK = "PriceListDetailPrefs";
  	public static String PREFS_KEY_PRICE_LIST_DETAIL_FOR_MYSELF_GET = "PRICE-LIST-DETAIL-FOR-MYSELF-GET";
  	public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;
  	//public static String PREFS_KEY_VAN_STOCK_FOR_COLLEAGUE_GET = "VAN-STOCK-FOR-COLLEAGUE-GET";
  	
  	
    //SOAP CONSTANTS   
    /*
    public static final String SOAP_SERVICE_URL = "http://8.25.131.6:8051/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/111/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://8.25.131.6:8051/sap/bc/srt/wsdl/bndg_E1501D51C34940F18FF0005056860CF1/wsdl11/allinone/ws_policy/document?sap-client=111";
    public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";
    */
    
   //commented on 07-10-2013
    public static final String SOAP_SERVICE_URL = "http://75.99.152.10:8050/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/110/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00";
    public static final String SOAP_ACTION_URL = "http://75.99.152.10:8050/sap/bc/srt/wsdl/bndg_E0A8AEE275F3AEF1AE7900188B47B426/wsdl11/allinone/ws_policy/document?sap-client=110";
    public static final String SOAP_TYPE_FNAME = "ZGssmwfmHndlEvntrqst00";   
    public static final String SOAP_SERVICE_NAMESPACE = "urn:sap-com:document:sap:soap:functions:mc-style";
    public static final String SOAP_INPUTPARAM_NAME = "DpistInpt";
    public static final String SOAP_NOTATION_DELIMITER = "NOTATION:ZML:VERSION:0:DELIMITER:[.]";
  	 //commented on 07-10-2013
    public static String SOAP_RESP_MSG = "";
    public static String SOAP_ERR_TYPE = "";
    /*public static  String SOAP_SERVICE_URL = "";
  	public static  String SOAP_ACTION_URL = "";
  	
  	public static  String SOAP_TYPE_FNAME = "";
  	public static  String SOAP_SERVICE_NAMESPACE = "";
  	public static  String SOAP_INPUTPARAM_NAME = "";
  	public static  String SOAP_NOTATION_DELIMITER = "";
  	*/
    
    //Application Name Constants
    public static String APPLN_NAME_STR = "SALESPRO";
        
    //Sms Observer related constants
    public static SalesProSmsObserver smsSentObserver = null;
    public static final Uri SMS_STATUS_URI = Uri.parse("content://sms");
    
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
    
    //Log related functions
    public static void showLog(String text){
    	Log.e(SalesOrderProConstants.SALESORDPRO_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(SalesOrderProConstants.SALESORDPRO_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }
    
    public static void setWindowTitleTheme(Context ctx){
    	try{
    		if(SalesOrderProConstants.TITLE_DISPLAY_WIDTH > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH)
    			ctx.setTheme(R.style.LargeTitleTheme);
    	}
    	catch(Exception sghh){
    		SalesOrderProConstants.showErrorLog("Error on setWindowTitleTheme : "+sghh.toString());
    	}
    }//fn setWindowTitleTheme
    
   /* public static void SapUrlConstants(Context myContext) {
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
       
  	  	    SOAP_SERVICE_URL = serviceUrl.text();
  	  	    SOAP_ACTION_URL = ActionUrl.text();
  	  	    SOAP_TYPE_FNAME = TypeFname.text();
  	  	    SOAP_SERVICE_NAMESPACE = ServiceNamespace.text();
  	  	    SOAP_INPUTPARAM_NAME = InputParamName.text();
  	  	    SOAP_NOTATION_DELIMITER = NotationDelimiter.text();
  		}catch(Exception sfg){
  			SalesOrderProConstants.showErrorLog("Error in SapGenConstants constructor : "+sfg.toString());
		}
  	    
  	}//SapUrlConstants
*/    
   /* public static void cancelAction(Context ctx){
    	
   	 try{   		     	       	
        	LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
    		  View layout;
    		  
    		  layout = inflater.inflate(R.layout.sapid_edittext_dialog,
    				  (ViewGroup)ctx.findViewById(R.id.linerll));		        		       		  
    		  
    		  sapIdET = (EditText) layout.findViewById(R.id.sapET);    		 			 
    		  builder = new AlertDialog.Builder(ctx).setTitle("Preparing mail with GDID");	        		  	        		 
    		  builder.setInverseBackgroundForced(true);
    		  View view=inflater.inflate(R.layout.email_dialog_layout,null);
    		  builder.setCustomTitle(view);	     		 
    		  builder.setView(layout); 	        		
    		  builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
    			  public void onClick(DialogInterface dialog, int id) {     			   
    			   //showEmailActivity();
    		  }
    			});
    		  
    		  alertDialog = builder.create();    		  
    		  alertDialog.show();
    		 
    	}catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in showAlert : "+sfg.toString());
    	}
   }//cancelAction
   */
    
    
    
    /*
    public static String getMobileIMEI(Context ctx){
    	String imei = "";
    	try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imei = mTelephonyMgr.getDeviceId(); 
			SalesOrderProConstants.showLog("MobileIMEI: "+imei);
			//imei = "990000320304548";
			//imei = "358444040351783";
		} 
    	catch (Exception e) {
    		SalesOrderProConstants.showErrorLog(e.toString());
		}
    	finally{
    		if((imei == null)|| (imei.equalsIgnoreCase(""))){
    			imei = "000000000000000";
    		}
    	}
    	return imei;
    }//fn getMobileIMEI
    */    
    
    public static SpannableString getUnderlinedString(String normalStr){
    	SpannableString content = null;    	
    	try {
    		if((normalStr != null) && (!normalStr.equalsIgnoreCase(""))){
				content = new SpannableString(normalStr);
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
    		}
		} catch (Exception aasd) {
			SalesOrderProConstants.showErrorLog("Error in getUnderlinedString : "+aasd.toString());
		}
    	return content;
    }//fn getUnderlinedString
    
    
    
    public static String getMobileIMEI(Context ctx){
    	String imenoStr = "", wifiStr = "", buildIdStr = "", buildDetStr = "";
		try{
			TelephonyManager TelephonyMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imenoStr = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
			//imenoStr="100000000000000";
        	SalesOrderProConstants.showLog("Mobile Imeno : "+imenoStr);
		}
		catch(Exception sgh){
    		SalesOrderProConstants.showErrorLog("Error on getMobileIMEI : "+sgh.toString());
    	}
		finally{
        	if((imenoStr == null) || (imenoStr.equalsIgnoreCase(""))){
        		try{
        			WifiManager wm = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        			wifiStr = wm.getConnectionInfo().getMacAddress();
        			//SalesOrderProConstants.showLog("Mobile Wifi Id : "+wifiStr);
        			 
        			 buildIdStr = "25" + //we make this look like a valid IMEI
		            	Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
		            	Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
		            	Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
		            	Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
		            	Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
		            	Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
		            	Build.USER.length()%10 ; //13 digits
		            //SalesOrderProConstants.showLog("Mobile Device Id : "+buildIdStr);
		            
		            buildDetStr = Build.FINGERPRINT+" : "+Build.HARDWARE;
		            //SalesOrderProConstants.showLog("Mobile Device Str : "+buildDetStr);
		            
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
		    		SalesOrderProConstants.showLog("Mobile Unique Id for Imeno : "+imenoStr);
        		}
        		catch(Exception sfsg){
        			SalesOrderProConstants.showErrorLog("Error on getMobileIMEI 2: "+sfsg.toString());
        		}
        	}
		}
		return imenoStr;
    }//fn getMobileIMEI
    
    public static String getApplicationIdentityParameter(Context ctx){
    	String appParams = "";
    	try {
    		appParams = "DEVICE-ID:"+SalesOrderProConstants.getMobileIMEI(ctx)+":DEVICE-TYPE:ANDROID:APPLICATION-ID:"+SalesOrderProConstants.APPLN_NAME_STR;
			SalesOrderProConstants.showLog("App param: "+appParams);
		} 
    	catch (Exception e) {
    		SalesOrderProConstants.showErrorLog(e.toString());
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
    		SalesOrderProConstants.showErrorLog(e.toString());
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
	    		//SalesOrderProConstants.showLog("Internet Connection Not Present");
	    		isConnAvail = false;
	    	}
    	}
    	catch(Exception sgg){
    		SalesOrderProConstants.showErrorLog("Error on checkConnectivityAvailable : "+sgg.toString());
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
    		SalesOrderProConstants.showErrorLog(e.toString());
    		formatedDateStr = dateStr;
		}
    	return formatedDateStr;
    }//fn getSystemDateFormat
    
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
		                SalesOrderProConstants.showLog("TimeZone :"+TimeZone.getTimeZone(TZ[i]).getDisplayName());
		            }
		        }
			}
		} 
    	catch (Exception e) {
    		SalesOrderProConstants.showErrorLog(e.toString());
		}
    	return tzoneadapter;
    }//fn getTimeZoneAdapter
    
    public static void soapResponse(Context ctx, SoapObject soap, boolean offline){
		String taskErrorMsgStr="", errorDesc="", errType="";
        if(soap != null){
        	//ServiceProConstants.showLog("Count : "+soap.getPropertyCount());
            try{ 
                String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[37];
                int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;
                for (int i = 0; i < soap.getPropertyCount(); i++) {                
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    //ServiceProConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
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
	                                //ServiceProConstants.showLog("Msg:"+taskErrorMsgStr);
	                                if(!offline)
	                                	SalesOrderProConstants.showErrorDialog(ctx, taskErrorMsgStr);
	                            	errorDesc = taskErrorMsgStr;	
	                            	SOAP_RESP_MSG = errorDesc; 
                                }
	                            int typeFstIndex = errorMsg.indexOf("Type=");
	                            if(typeFstIndex > 0){
	                            	int typeLstIndex = errorMsg.indexOf(";", typeFstIndex);
	                                String taskErrorTypeMsgStr = errorMsg.substring((typeFstIndex+"Type=".length()), typeLstIndex);
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
            	SalesOrderProConstants.showErrorLog("On soapResponse : "+sff.toString());
            }
        }
    }//fn soapResponse
    
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
	    	SalesOrderProConstants.showErrorLog("Error in getSoapResponseSucc_Err : "+sffe.toString());
	    }
    	return resMsgErr;
    }//fn getSoapResponseSucc_Err
    
}//End of class SalesOrderProConstants    
