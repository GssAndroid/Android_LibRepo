package com.globalsoft.SalesPro.Database;

import android.provider.BaseColumns;

public class CustCreditDBConstants {

	 //Categories Table Definitions
    public static final String TABLE_SEARCH_CUS_LIST_ = "cus_serchlist_table";
    public static final String TABLE_SELECTED_CUS_LIST = "cus_selctdlist_table";
    
    
  //pricelist Table Definitions
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
    
    
    
    
    //pricelist Serial Table Definitions
    public static final String CUS_SEL_COL_ID = BaseColumns._ID;
    public static final String CUS_SEL_COL_NAME1 = "NAME1";
    public static final String CUS_SEL_COL_KBETR = "CRBLB";
    public static final String CUS_SEL_COL_KONWA= "CTLPC";
    public static final String CUS_SEL_COL_KPEIN= "KLIMK";
    public static final String CUS_SEL_COL_KMEIN = "OBLIG";
    public static final String CUS_SEL_COL_MATNR = "WAERS";
    public static final String CUS_SEL_COL_KSCHL_TEXT= "KLPRZ";
    public static final String CUS_SEL_COL_PLTYP_TEXT= "DBRTG";
    public static final String CUS_SEL_COL_KSCHL = "KUNNR";
    public static final String CUS_SEL_COL_PLTYP = "CTLPC_TEXT";
    public static final String CUS_SEL_COL_ORT01= "ORT01";
    public static final String CUS_SEL_COL_REGIO = "REGIO";
    public static final String CUS_SEL_COL_LAND1 = "LAND1";
    
    
   
    
    
    
}//end of PriceListDBConstants
