package com.globalsoft.SalesOrderLib.Database;

import android.provider.BaseColumns;

public class SalesOrderDBConstants{
		
 	public static final String COLUNM_NAME_SCR_TITLE = "SCRN-TITLE";
    public static final String CNTXT3_DETAIL_SCREEN_TAG = "LISTVW-1LN";
    public static final String CNTXT4_ATTR_TAG = "FIELD-ATTR";
    public static final String CNTXT3_LABE_TAG = "FIELD-LABE";
    public static final String VALUE_DISPLAY_TAG = "DISPLAY";
    public static final String VALUE_DESCRIBED_TAG = "DESCRIBED";
    public static final String VALUE_HEADING_TAG = "FIELD-HDNG";
	        	
	public static final String TABLE_SALESORDER_LIST = "so_table";
	
	public static final String SO_MAIN_COL_ID = BaseColumns._ID;
    public static final String SO_MAIN_COL_VBELN = "VBELN";
    public static final String SO_MAIN_COL_POSNR = "POSNR";
    public static final String SO_MAIN_COL_MATNR= "MATNR";
    public static final String SO_MAIN_COL_KWMENG= "KWMENG";
    public static final String SO_MAIN_COL_VRKME = "VRKME";
    public static final String SO_MAIN_COL_NETWR = "NETWR";
    public static final String SO_MAIN_COL_WAERK= "WAERK";
    public static final String SO_MAIN_COL_ABGRU_TEXT= "ABGRU_TEXT";
    public static final String SO_MAIN_COL_FAKSP_TEXT = "FAKSP_TEXT";
    public static final String SO_MAIN_COL_GBSTA_TEXT = "GBSTA_TEXT";
    public static final String SO_MAIN_COL_LFSTA_TEXT = "LFSTA_TEXT";
    public static final String SO_MAIN_COL_FKSTA_TEXT = "FKSTA_TEXT";
    public static final String SO_MAIN_COL_ARKTX = "ARKTX";
    
    
    public static final String TABLE_SO_HEAD_OP_LIST = "soheadopt_table";
    
    public static final String SO_HEAD_COL_ID = BaseColumns._ID;
    public static final String SO_HEAD_COL_VBELN = "VBELN";
    public static final String SO_HEAD_COL_KUNAG = "KUNAG";
    public static final String SO_HEAD_COL_NAME1A= "NAME1A";
    public static final String SO_HEAD_COL_LAND1A= "LAND1A";
    public static final String SO_HEAD_COL_REGIOA = "REGIOA";
    public static final String SO_HEAD_COL_ORT01A = "ORT01A";
    public static final String SO_HEAD_COL_STRASA= "STRASA";
    public static final String SO_HEAD_COL_TELF1A= "TELF1A";
    public static final String SO_HEAD_COL_TELF2A = "TELF2A";
    public static final String SO_HEAD_COL_SMTP_ADDRA = "SMTP_ADDRA";
    public static final String SO_HEAD_COL_PARNR = "PARNR";
    public static final String SO_HEAD_COL_NAME1PK = "NAME1PK";
    public static final String SO_HEAD_COL_LAND1P = "LAND1P";
    public static final String SO_HEAD_COL_REGIOP= "REGIOP";
    public static final String SO_HEAD_COL_ORT01P= "ORT01P";
    public static final String SO_HEAD_COL_STRASP= "STRASP";
    public static final String SO_HEAD_COL_TELF1P = "TELF1P";
    public static final String SO_HEAD_COL_TELF2P = "TELF2P";
    public static final String SO_HEAD_COL_SMTP_ADDRP= "SMTP_ADDRP";
    public static final String SO_HEAD_COL_AUDAT= "AUDAT";
    public static final String SO_HEAD_COL_WAERK = "WAERK";
    public static final String SO_HEAD_COL_NETWR= "NETWR";
    public static final String SO_HEAD_COL_AUGRU_TEXT = "AUGRU_TEXT";
    public static final String SO_HEAD_COL_GBSTK_TEXT = "GBSTK_TEXT";
    public static final String SO_HEAD_COL_ABSTK_TEXT = "ABSTK_TEXT";
    public static final String SO_HEAD_COL_LFSTK_TEXT= "LFSTK_TEXT";
    public static final String SO_HEAD_COL_CMGST_TEXT= "CMGST_TEXT";
    public static final String SO_HEAD_COL_SPSTG_TEXT= "SPSTG_TEXT";
    public static final String SO_HEAD_COL_LIFSK_TEXT = "LIFSK_TEXT";
    public static final String SO_HEAD_COL_FKSTK_TEXT = "FKSTK_TEXT";
    public static final String SO_HEAD_COL_FAKSK_TEXT= "FAKSK_TEXT";
    public static final String SO_HEAD_COL_ZZSTATUS_SUMMARY= "ZZSTATUS_SUMMARY";
    public static final String SO_HEAD_COL_KETDAT = "KETDAT";
    public static final String SO_HEAD_COL_BSTKD = "BSTKD";
    public static final String SO_HEAD_COL_BSTDK = "BSTDK";
    
    public static final String TABLE_SO_CUSTOMER_LIST = "socustlist_table";
    
    public static final String SO_COL_ID = BaseColumns._ID;
    public static final String SO_CUST_COL_KUNNR = "KUNNR";
    public static final String SO_CUST_COL_NAME1 = "NAME1";
    public static final String SO_CUST_COL_LAND1 = "LAND1";
    public static final String SO_CUST_COL_REGIO = "REGIO";
    public static final String SO_CUST_COL_ORT01 = "ORT01";
    public static final String SO_CUST_COL_STRAS = "STRAS";
    public static final String SO_CUST_COL_TELF1 = "TELF1";
    public static final String SO_CUST_COL_TELF2 = "TELF2";
    public static final String SO_CUST_COL_SMTP_ADDR= "SMTP_ADDR";
    
    public static final String TABLE_SO_MATT_LIST = "so_matt_table";
    public static final String SO_MATT_COL_ID = BaseColumns._ID;
    public static final String SO_MATT_COL_MATNR = "MATNR";
    public static final String SO_MATT_COL_MAKTX = "MAKTX";
    public static final String SO_MATT_COL_MEINH = "MEINH";
    public static final String SO_MATT_COL_MSEHT = "MSEHT";
    
    public static final String TABLE_SO_HEAD_PRICE_LIST = "so_headprice_table";
    public static final String SO_HEAD_PRICE_COL_ID = BaseColumns._ID;
    public static final String SO_HEAD_PRICE_COL_KUNAG = "KUNAG";
    public static final String SO_HEAD_PRICE_COL_KETDAT= "KETDAT";
    public static final String SO_HEAD_PRICE_COL_NETWR = "NETWR";
    public static final String SO_HEAD_PRICE_COL_WAERK = "WAERK";
    
    public static final String TABLE_SO_ITEM_PRICE_LIST = "so_itemprice_table";
    public static final String SO_ITEM_PRICE_COL_ID = BaseColumns._ID;
    public static final String SO_ITEM_PRICE_COL_POSNR = "POSNR";
    public static final String SO_ITEM_PRICE_COL_MATNR = "MATNR";
    public static final String SO_ITEM_PRICE_COL_KWMENG= "KWMENG";
    public static final String SO_ITEM_PRICE_COL_VRKME = "VRKME";
    public static final String SO_ITEM_PRICE_COL_NETWR = "NETWR";
    public static final String SO_ITEM_PRICE_COL_WAERK = "WAERK";
    
    public static final String TABLE_SO_CREATE_TABLE_LIST = "so_create_table";
    public static final String SO_CREATE_COL_ID = BaseColumns._ID;
    public static final String SO_CREATE_COL_KUNNR = "KUNNR";
    public static final String SO_CREATE_COL_ARKTX = "ARKTX";
    public static final String SO_CREATE_COL_MATNR= "MATNR";
    public static final String SO_CREATE_COL_KWMENG = "KWMENG";
    public static final String SO_CREATE_COL_VRKME = "VRKME";
    public static final String SO_CREATE_COL_NETWR = "NETWR";
    public static final String SO_CREATE_COL_WAERK = "WAERK";
    public static final String SO_CREATE_COL_ALT_ID= "alternateid";
}//SalesOrderDBConstants
