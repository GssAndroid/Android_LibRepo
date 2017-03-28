package com.globalsoft.SapQueueProcessorHelper.Database;

import android.provider.BaseColumns;

public class SapQueueProcessorDBConstants {
	//SapQueueProcessor Database Related Constants

	 //Categories Table Definitions
    public static final String TABLE_QUEUEPRSSR = "queueprocessor_table";	   
    
    //Queue Processor Table Definitions
    public static final String QUEUEPRSSR_COL_ID = BaseColumns._ID;
    public static final String QUEUEPRSSR_COL_APPREFID = "apprefid";
    public static final String QUEUEPRSSR_COL_APPNAME = "appname";
    public static final String QUEUEPRSSR_COL_PCKGNAME = "packagename";
    public static final String QUEUEPRSSR_COL_CLASSNAME = "classname";
    public static final String QUEUEPRSSR_COL_FUNCNAME = "apiname";
    public static final String QUEUEPRSSR_COL_DATE = "queuedate";
    public static final String QUEUEPRSSR_COL_SOAPDATA = "soapdata";
    public static final String QUEUEPRSSR_COL_STATUS = "status";
    public static final String QUEUEPRSSR_COL_PROCESS_TIME = "processtime";
    public static final String QUEUEPRSSR_COL_PROCESS_COUNT = "processcount";
    public static final String QUEUEPRSSR_COL_PROCESS_ALT_ID = "altid";
    public static final String QUEUEPRSSR_FLAG = "platform";
    public static final String QUEUEPRSSR_COL_SOAPDATA_RESPONSE = "response";
    
    
    public static final String TABLE_NOTIFY_QUEUEPRSSR = "notify_queueprocessor_table";
    public static final String QUEUEPRSSR_COL_E_ID = BaseColumns._ID;
    public static final String QUEUEPRSSR_COL_NOTIFY_FLAG = "notifyFlag";
    public static final String QUEUEPRSSR_COL_E_APPREFID = "applrefid";
    public static final String QUEUEPRSSR_COL_E_ALT_ID = "altid";
    public static final String QUEUEPRSSR_ERROR_DESCRIPTION = "errorDesc";
    
    
}//End of class SapQueueProcessorDBConstants
