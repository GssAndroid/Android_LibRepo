package com.globalsoft.SalesPro.Database;

import android.provider.BaseColumns;

public class PriceListDBConstants {

	 //Categories Table Definitions
    public static final String TABLE_SEARCH_LIST_EMP = "pl_serchlist_table";
    public static final String TABLE_SELECTED_MAT_LIST = "pl_selctdlist_table";
    
    
  //pricelist Table Definitions
    public static final String PL_SER_COL_ID = BaseColumns._ID;
    public static final String PL_SER_COL_MATNR = "MATNR";
    public static final String PL_SER_COL_MAKTX = "MAKTX";
    public static final String PL_SER_COL_MEINH = "MEINH";
    public static final String PL_SER_COL_MSEHT = "MSEHT";
    
    
    
    //pricelist Serial Table Definitions
    public static final String PL_SEL_COL_ID = BaseColumns._ID;
    public static final String PL_SEL_COL_MAKTX = "MAKTX";
    public static final String PL_SEL_COL_KBETR = "KBETR";
    public static final String PL_SEL_COL_KONWA= "KONWA";
    public static final String PL_SEL_COL_KPEIN= "KPEIN";
    public static final String PL_SEL_COL_KMEIN = "KMEIN";
    public static final String PL_SEL_COL_MATNR = "MATNR";
    public static final String PL_SEL_COL_KSCHL_TEXT= "KSCHL_TEXT";
    public static final String PL_SEL_COL_PLTYP_TEXT= "PLTYP_TEXT";
    public static final String PL_SEL_COL_KSCHL = "KSCHL";
    public static final String PL_SEL_COL_PLTYP = "PLTYP";
    
   
    
    
    
}//end of PriceListDBConstants
