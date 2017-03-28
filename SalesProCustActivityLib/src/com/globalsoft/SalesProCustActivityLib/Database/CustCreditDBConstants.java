package com.globalsoft.SalesProCustActivityLib.Database;

import android.provider.BaseColumns;

public class CustCreditDBConstants {

	//categories table definitions
    public static final String TABLE_SEARCH_CUS_LIST_ = "cus_serchlist_table";
    public static final String TABLE_SELECTED_CUS_LIST = "cus_selctdlist_table";
    public static final String TABLE_CUS_CNTX = "cus_cntx_table";    

    public static final String COLUNM_NAME_SCR_TITLE = "SCRN-TITLE";
    public static final String CNTXT3_DETAIL_SCREEN_TAG = "LISTVW-1LN";
    public static final String CNTXT4_ATTR_TAG = "FIELD-ATTR";
    public static final String CNTXT3_LABE_TAG = "FIELD-LABE";
    public static final String VALUE_DISPLAY_TAG = "DISPLAY";
    public static final String VALUE_DESCRIBED_TAG = "DESCRIBED";
        
    //customer search table definitions
    public static final String CUS_SER_COL_ID = BaseColumns._ID;
    public static final String CUS_SER_COL_KUNNR = "KUNNR";
    public static final String CUS_SER_COL_NAME1 = "NAME1";
    public static final String CUS_SER_COL_LAND1 = "LAND1";
    public static final String CUS_SER_COL_REGIO = "REGIO";
    public static final String CUS_SER_COL_ORT01 = "ORT01";
    public static final String CUS_SER_COL_STRAS = "STRAS";
    public static final String CUS_SER_COL_TELF1 = "TELF1";
    public static final String CUS_SER_COL_TELF2 = "TELF2";
    public static final String CUS_SER_COL_SMTP_ADDR= "SMTP_ADDR";
    
    //customer context table definitions
    public static final String CUS_CNTX_COL_ID = BaseColumns._ID;
    public static final String CUS_CNTX_COL_CNTXT2 = "CNTXT2";
    public static final String CUS_CNTX_COL_CNTXT3 = "CNTXT3";
    public static final String CUS_CNTX_COL_CNTXT4 = "CNTXT4";
    public static final String CUS_CNTX_COL_NAME = "NAME";
    public static final String CUS_CNTX_COL_DPNDNCY = "DPNDNCY";
    public static final String CUS_CNTX_COL_SEQNR = "SEQNR";
    public static final String CUS_CNTX_COL_TYPE = "TYPE";
    public static final String CUS_CNTX_COL_SIGN = "SIGN";
    public static final String CUS_CNTX_COL_OPTN = "OPTN";
    public static final String CUS_CNTX_COL_VALUE = "VALUE";
    public static final String CUS_CNTX_COL_VALUE_HIGH = "VALUE_HIGH";
    public static final String CUS_CNTX_COL_TRGTNAME = "TRGTNAME";
    public static final String CUS_CNTX_COL_TRGTVALUE = "TRGTVALUE";
    
    //customer selected table definitions
    public static final String CUS_SEL_COL_ID = BaseColumns._ID;
    public static final String CUS_SEL_COL_NAME1 = "NAME1";
    public static final String CUS_SEL_COL_CRBLB = "CRBLB";
    public static final String CUS_SEL_COL_CTLPC= "CTLPC";
    public static final String CUS_SEL_COL_KLIMK= "KLIMK";
    public static final String CUS_SEL_COL_OBLIG = "OBLIG";
    public static final String CUS_SEL_COL_WAERS = "WAERS";
    public static final String CUS_SEL_COL_KLPRZ= "KLPRZ";
    public static final String CUS_SEL_COL_DBRTG = "DBRTG";
    public static final String CUS_SEL_COL_KUNNR = "KUNNR";
    public static final String CUS_SEL_COL_CTLPC_TEXT= "CTLPC_TEXT";
    public static final String CUS_SEL_COL_ORT01= "ORT01";
    public static final String CUS_SEL_COL_REGIO = "REGIO";
    public static final String CUS_SEL_COL_LAND1 = "LAND1";
    
}//end of CustCreditDBConstants