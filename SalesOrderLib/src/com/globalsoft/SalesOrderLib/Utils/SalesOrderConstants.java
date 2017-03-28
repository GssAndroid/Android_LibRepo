package com.globalsoft.SalesOrderLib.Utils;

import java.util.ArrayList;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Toast;

public final class SalesOrderConstants {
	
	//Log Related Constants
    public static final String SALESORDER_TAG = "SalesOrderLib ";    
   public static String SO_PACKAGE_NAME = "com.globalsoft.SalesPro"; 
    public static final String SALESORDER_ERRORTAG = "SalesOrderLib Error "; 
    public static String APPLN_BGSERVICE_NAME = "com.globalsoft.SalesPro.SalesOrderBGService";
    public static String SO_CALLING_APP_NAME = SapGenConstants.APPLN_NAME_STR_SALESPRO;
    public static final int STATUS_HIGHPRIORITY = 100;
    
	//Log related functions
    public static void showLog(String text){
    	Log.e(SalesOrderConstants.SALESORDER_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(SalesOrderConstants.SALESORDER_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    } 
    
    public static SpannableString getUnderlinedString(String normalStr){
    	SpannableString content = null;    	
    	try {
    		if((normalStr != null) && (!normalStr.equalsIgnoreCase(""))){
				content = new SpannableString(normalStr);
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
    		}
		} catch (Exception aasd) {
			SalesOrderConstants.showErrorLog("Error in getUnderlinedString : "+aasd.toString());
		}
    	return content;
    }//fn getUnderlinedString
    
    
    
    //Application Name Constants
    public static String APPLN_NAME_STR = "";  
    
    //Preference Related Constant
  	public static String PREFS_NAME_FOR_SALES_ORDER	 = "salesorderPrefs";
  	public static String PREFS_KEY_SALES_ORDER_FOR_MYSELF_GET = "SALES-ORDER-FOR-MYSELF-GET";
  	//public static String PREFS_KEY_VAN_STOCK_FOR_COLLEAGUE_GET = "VAN-STOCK-FOR-COLLEAGUE-GET";
  	
    //Stock List Related Constants
    public static ArrayList stocksItemMainArrList = new ArrayList();
    public static ArrayList stocksSelectdItemMainArrList = new ArrayList();
    public static ArrayList stocksMatrNoArrList = new ArrayList();
    public static ArrayList CustomerMatrArrList = new ArrayList();
    public static ArrayList soHeadArrList = new ArrayList();
    public static ArrayList soItemArrList = new ArrayList();
    public static ArrayList metaheadlistArray = new ArrayList();
    public static ArrayList metaOflineitemlistArray = new ArrayList();
    
  //pagination constants
  	public static int page_size = 10;
  	 public static int so_item_page_size = 2;
  	
  	public static int SL_current_page = 1;
  	public static int SL_previous_page = 0;
  	public static int SL_total_page = 0;
  	public static int SL_total_record = 0;	
    
  	
}//End of class SalesOrderProConstants    
