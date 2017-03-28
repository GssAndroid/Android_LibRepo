package com.globalsoft.DataBaseLib;

import android.provider.BaseColumns;

public class DBConstants {

	//DataBase name
	public static String DB_DATABASE_NAME = "GSS_LDB";
	public static final int DATABASE_VERSION = 1;
	
	//Table related constant
	public static String DB_TABLE_NAME = "";
	public static String DB_LIST_TABLE_CREATE = "";
	public static String DB_ERROR_TABLE_NAME = "error_table";
	public static String[] DBMetaArrayString;	
	    
	//Sales Order Related Constant
	
	public static String TABLE_SALESORDER_ITEM_LIST = "ZGSEVAST_SDITEM10";
			
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
    
    
    public static  String TABLE_SALESORDER_HEAD_LIST = "ZGSEVAST_SDHEADER10";
    
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
    
    public static String MATT_LIST_DB_TABLE_NAME = "ZGSEMMST_MTRL10A";
    public static String SO_MATNR_COLUMN_NAME = "MATNR";
	public static String SO_MAKTX_COLUMN_NAME = "MAKTX";
	public static String SO_MEINH_COLUMN_NAME = "MEINH";
	public static String SO_MSEHT_COLUMN_NAME = "MSEHT";
	public static String SO_GETPRICE_LIST_DB_TABLE_NAME = "ZGSEVAST_SDSMLTNRSLT12";
	public static String So_GETNETPRICE_LIST_DB_TABLE_NAME = "ZGSEVAST_SDSMLTNRSLT11";
	
    
    //Product Related Constant
	public static String PRODUCT_LIST_DB_TABLE_NAME = "ZGSEVMST_MTRL10";
	public static String MAKTX_COLUMN_NAME = "MAKTX";
	public static String MATNR_COLUMN_NAME = "MATNR";
	public static String KBETR_COLUMN_NAME = "KBETR";
	public static String ZZTEXT1_COLUMN_NAME = "ZZTEXT1";
	public static String ZZTEXT2_COLUMN_NAME = "ZZTEXT2";
	public static String ZZTEXT3_COLUMN_NAME = "ZZTEXT3";
	public static String ZZTEXT4_COLUMN_NAME = "ZZTEXT4";
	public static String MATKL_COLUMN_NAME = "MATKL";
	public static String KONWA_COLUMN_NAME = "KONWA";
	public static String KMEIN_COLUMN_NAME = "KMEIN";
	
    //Product Attachment01R Related Constant
	public static String PRODUCT_ATTA01R_LIST_DB_TABLE_NAME = "ZGSXCAST_ATTCHMNT01R";
	public static String ATTA01R_OBJECT_ID_COLUMN_NAME = "OBJECT_ID";
	public static String ATTA01R_ATTCHMNT_RFRNC_COLUMN_NAME = "ATTCHMNT_RFRNC";
	
	//Product Category Related Constant
	public static String PRODUCT_CATSEL_LIST_DB_TABLE_NAME = "ZGSEMMST_MATKLDSCRPTN10";
	public static String WGBEZ_COLUMN_NAME = "WGBEZ";
	
	//Customer Related Constant
	public static String CUSTOMER_LIST_DB_TABLE_NAME = "ZGSEMMST_MATKLDSCRPTN10";
	public static String SO_CUSTOMER_LIST_DB_TABLE_NAME = "ZGSEVDST_CSTMR10";
	public static String NAME1_COLUMN_NAME = "NAME1";
	public static String KUNNR_COLUMN_NAME = "KUNNR";
	public static String ORT01_COLUMN_NAME = "ORT01";
	public static String REGIO_COLUMN_NAME = "REGIO";
	public static String LAND1_COLUMN_NAME = "LAND1";
	
	//Product Get Price Related Constant
	public static String PRODUCT_GETPRICE_LIST_DB_TABLE_NAME = "ZGSEVAST_SDSMLTNRSLT12";
	public static String KWMENG_COLUMN_NAME = "KWMENG";
	public static String NETWR_COLUMN_NAME = "NETWR";
	
	//Product Get Net Price Related Constant
	public static String PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME = "ZGSEVAST_SDSMLTNRSLT11";
	public static String KUNAG_COLUMN_NAME = "KUNAG";
	
	//Customer Header realted Constant
	public static final String PO_HEAD_COL_KETDAT = "KETDAT";
	
	
	
	//DYNAMIC UI RELATED CONSTANTS
	//Table related constant
		/*public static String UICONF_TABLE_NAME = "";
		public static String UICONF_LIST_TABLE_CREATE = "";
		public static String[] UICONFMetaArrayString;	*/
		
		//Column field names
		public static String CNTXT2_COLUMN_NAME = "CNTXT2";
		public static String CNTXT3_COLUMN_NAME = "CNTXT3";
		public static String CNTXT4_COLUMN_NAME = "CNTXT4";
		public static String VALUE_COLUMN_NAME = "VALUE";
		public static String TRGTNAME_COLUMN_NAME = "TRGTNAME";
		public static String NAME_COLUMN_NAME = "NAME";
		public static String SEQNR_COLUMN_NAME = "SEQNR";
		public static String DATATYPE_COLUMN_NAME = "DATATYPE";
		public static final String UNIT_DATATYPE_TAG = "UNIT";
		public static final String NUMC_DATATYPE_TAG = "NUMC";
		
		//Dynamic UI Related Constants
	    public static final String COLUNM_NAME_SCR_TITLE = "SCRN-TITLE";
	    public static final String CNTXT4_ATTR_TAG = "FIELD-ATTR";
	    public static final String CNTXT4_LABE_TAG = "FIELD-LABE";
	    public static final String CNTXT4_HINT_TAG = "FIELD-HINT";
	    public static final String VALUE_DISPLAY_TAG = "DISPLAY";
	    public static final String VALUE_DISPLAY_TAPPABLE_TAG = "DISPLAY-TAPPABLE";
	    public static final String CHECKBOX_TAG = "CHECKBOX";
	    public static final String IMAGE_TAPPABLE_TAG = "IMAGE-TAPPABLE";
	    public static final String VALUE_SEARCHBAR_TAG = "SEARCHBAR";
	    public static final String VALUE_DESCRIBED_TAG = "DESCRIBED";
	    public static final String COLUMN_ALIGNEDR_TAG = "COLUMN-ALIGNED";
	    public static final String VALUE_EDITABLE_TAG = "EDITABLE";
	   // public static final String VALUE_IMAGE_TAG = "IMAGE-TAPPABLE";
	    public static final String VALUE_IMAGE_TAG = "IMAGE";
	    public static final String ACTION_FIELD_LABEL_TAG = "ACTION-FIELD-LABEL";
	   
	   
	    public static final String ICON_DELETE_ITEM_TAG = "ICON:DELETE-ITEM";
	    public static final String ITEM_DELETION_ICONTAG = "ITEM-DELETION-ICON";
	    public static final String ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG = "ACTION-FIELD-ICON:ICON_SHOPPING_CART";
	    public static final String CNTXT3_DETAIL_TABULAR_OVERVIEW_SCREEN_TAG = "TABULAR-OVERVIEW";
	    public static final String CURR_DATATYPE_TAG = "CURR";
	    public static final String QUAN_DATATYPE_TAG = "QUAN";
	    public static final String DATS_DATATYPE_TAG = "DATS";
	        
	    public static final String CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG = "LISTVW-MLN";
	    public static final String CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG = "LISTVW-1LN";
	    
	    public static final String CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG = "TBLVW-1LN";
	    public static final String CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG = "TBLVW-MLN";
	    
	    public static final String CNTXT4_SUBVIEW_TAG = "SUBVIEW";
	    
	    public static final String SEARCHBAR_TAG = "SEARCHBAR";   
	    public static final String ACTION_FIELD_ICON_ICON_SEARCH_TAG = "ACTION-FIELD-ICON:ICON_SEARCH";
	    
	    
	    //Product Catalog Dynamic UI Constant    
	    public static final String DEVICE_TYPE_OVERVIEW_S_TAG = "OVERVIEW-S";
	    public static final String DEVICE_TYPE_OVERVIEW_W_TAG = "OVERVIEW-W";
	    public static final String DEVICE_TYPE_OVRVW_W_MAIN_BLOCK_TAG = "OVRVW-W-MAIN-BLOCK";
	    public static final String DEVICE_TYPE_OVRVW_S_MAIN_BLOCK_TAG = "OVRVW-S-MAIN-BLOCK";
	    public static final String DEVICE_TYPE_OVRVW_W_HEADER_BLOCK_TAG = "OVRVW-W-HEADER-BLOCK";
	    public static final String DEVICE_TYPE_SMALL_HEADER = "OVRVW-S-HEADER-BLOCK";
	    public static final String DEVICE_TYPE_OVRVW_ACTION_BAR_TAG = "OVRVW-ACTION-BAR";
	    
	    public static final String DEVICE_TYPE_SMALL_DETAIL_TAG = "DETAIL-S";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_TAG = "DETAIL-W";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_SUMMARY_TAG = "DETAIL-SUMMARY";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_DETAILS_TAG = "DETAIL-DETAILS";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_ACTION_BAR_TAG = "DETAIL-ACTION-BAR";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_TAB_STRIP_TAG = "DETAIL-TAB-STRIP";
	    public static final String DEVICE_TYPE_WIDE_DETAIL_ACTION_TAB_LABEL_TAG = "ACTION-TAB-LABEL";
	    public static final String DEVICE_DETAIL_TAB_SUMMARY_LABEL_TAG = "DETAIL-TAB-SUMMARY";
	    public static final String DEVICE_DETAIL_TAB_IMAGES_LABEL_TAG = "DETAIL-TAB-IMAGES";
	    public static final String DEVICE_DETAIL_TAB_DETAILS_LABEL_TAG = "DETAIL-TAB-DETAILS";

	    public static final String DEVICE_TYPE_SMALL_SCART_TAG = "SHOPPING-CART-S";
	    public static final String DEVICE_TYPE_WIDE_SCART_TAG = "SHOPPING-CART-W";
	    public static final String DEVICE_TYPE_WIDE_SCART_TAG_ITEMS = "SHOPPING-CART-W-ITEMS";
	    public static final String DEVICE_TYPE_SMALL_SCART_TAG_ITEMS = "SHOPPING-CART-S-ITEM";
	    public static final String DEVICE_TYPE_CUSTOMER_SEARCH_BLOCK = "CUSTOMER-SEARCH-BLOCK";
	    public static final String DEVICE_TYPE_SHOPPING_CART_HEADER_BLOCK = "SHOPPING-CART-HEADER-BLOCK";
	    public static final String DEVICE_TYPE_ACTION_FIELD_ICON_ICON_SEARCH_AGAIN = "ACTION-FIELD-ICON:ICON_SEARCH_AGAIN";
	    public static final String DEVICE_TYPE_ACTION_FIELD_ICON_ICON_PRICE = "ACTION-FIELD-ICON:ICON_PRICE";    
	    public static final String DEVICE_TYPE_SHOPPING_CART_W_TOTAL = "SHOPPING-CART-W-TOTAL";
	    public static final String ACTION_SEARCH_ICON_TAG = "ACTION-FIELD-ICON:ICON_SEARCH";
	    public static final String ACTION_CHANGE_ICON_TAG = "ACTION-FIELD-ICON:ICON_SEARCH_AGAIN";
	  
	    
	    //Product Related Constant
		public static String PRODUCT_UICONF_TABLE_NAME = "POZGSSMWST_UICNFG01";
		
	    //SalesOrder Detail Screen Constants
		public static String TABLE_SALESORDER_CONTEXT_LIST  = "SOZGSSMWST_UICNFG01";
		public static final String SO_DEVICE_TYPE_WIDE_OV_TAG = "SO-OVERVIEW-W-MAIN-PART";
	    public static final String SO_SUMMARY_BLOCK = "SO-SUMMARY-BLOCK";
	    public static final String DEVICE_WIDE_DETAIL_LEFT_PART = "DETAIL-W-LEFT-PART";   
	    public static final String DEVICE_WIDE_DETAIL_RIGHT_PART = "DETAIL-W-RIGHT-PART";
	    public static final String DEVICE_OVERVIEW_SEARCH = "SO-ITEM-OVERVIEW-SEARCH";
	    public static final String ITEM_SUBVIEW_TBLVW = "SO-ITEM-OVERVIEW";   
	    public static final String ITEM_HEADER_SUBVIEW_BLOCK = "SO-HEADER-DATA-BLOCK";   
	    public static final String ITEM_SUBVIEW_BLOCK = "SO-ITEM-DATA-BLOCK";   
	    public static final String SO_DEVICE_OVERVIEW_EDIT_W = "SO-ITEM-OVERVIEW-EDIT";
	    public static final String CUSTOMER_SEARCH_TAG = "CUSTOMER-SEARCH";
	    public static final String SO_CUSTOMER_SEARCH_RESULT_TAG = "CUSTOMER-SEARCH-RESULT";
	    public static final String PRODUCT_SEARCH_TAG = "PRODUCT-SEARCH";
	    public static final String SO_PRODUCT_SEARCH_RESULT_TAG = "PRODUCT-SEARCH-RESULT";
	    public static final String PRODUCT_SEARCH_RESULT_TAG = "PRODUCT-SEARCH-RESULT-MAIN";
	    public static final String PRODUCT_SEARCH_RESULT_ACTION_TAG = "PRODUCT-SEARCH-RESULT-ACTION-BAR";
	    public static final String EDIT_ACTION_TAG = "SO-EDIT-ACTION-BAR";
	    public static final String HEADER_CREATE_TAG = "SO-HEADER-CREATE-COPY";
	    public static final String SO_DEVICE_TYPE_WIDE_OV_TOP_TAG = "SO-OVERVIEW-TOP-PART";
	    public static final String ACTION_COPY_ICON_TAG = "ACTION-FIELD-ICON:COPY_DOCUMENT";
	    public static final String ACTION_CREATE_ICON_TAG = "ACTION-FIELD-ICON:CREATE_DOCUMENT";
	    public static final String ACTION_DELETE_ICON_TAG = "ACTION-FIELD-ICON:ICON_DELETE_ITEM";
	    public static final String ACTION_DETAIL_TAG = "ACTION-FIELD-ICON:ICON_DETAIL";
	    public static final String ACTION_FIELD_ICON_TAG = "ACTION-DONE";
	    public static final String VALUE_DISPLAY_TAP_TAG = "DISPLAY_TAPPABLE";    
	    public static final String VALUE_DESCRIBED_TAP_TAG = "DESCRIBED_TAPPABLE";  
	    public static final String VALUE_ICON_TAG = "ICON";
	    public static final String SO_DEVICE_TYPE_WIDE_OV = "SO-OVERVIEW-W";
	    public static final String SO_MATT_DONE_FIELD_LABEL_TAG = "ACTION-DONE";
	    public static final String SO_CREATE_SEP_LINE = "IMAGE-DISPLAY";
	    public static final String SO_CREATE_TITLE_TAG = "SO-CREATE-W";
	    public static final String SO_CREATE_COPY_TITLE_TAG = "SO-CREATE-COPY-W";
	    public static final String SO_ITEM_NAVIGATION_ACTION_TAG = "SO-ITEM-DATA-ACTION-BAR";
	    public static final String SO_ITEM_NAVIGATION_ACTION_LEFT_TAG = "PREVIOUS-ITEM";
	    public static final String SO_ITEM_NAVIGATION_ACTION_RIGHT_TAG = "NEXT-ITEM";
	    
	    
	    //Sales order contants phone
	   // public static final String SO_DEVICE_TYPE_SMALL_OV_TAG = "SO-OVERVIEW-S-MAIN-LEFT-PART";
	    public static final String SO_DEVICE_TYPE_SMALL_OV_TAG = "SO-OVERVIEW-S-MAIN-PART";
	    public static final String SO_DEVICE_TYPE_OVERVIEW_SMALL_TITLE = "SO-OVERVIEW-S";
	    public static final String SO_NAVIGATION_DETAIL_TAG = "ACTION-FIELD-ICON:ICON_DETAIL";
	    public static final String SO_DEVICE_TYPE_ITEM_SMALL_OV_TAG = "SO-ITEM-OVERVIEW-S-LEFT-PART";
	    public static final String SO_DEVICE_TYPE_ITEM_EDIT = "SO-ITEM-OVERVIEW-EDIT-S";
	    public static final String SO_DEVICE_TYPE_ITEM_EDIT_LEFT_PART = "SO-ITEM-OVERVIEW-EDIT-S-LEFT";
	    public static final String SO_CREATE_COPY_TYPE_SMALL_OV_TAG = "SO-CREATE-COPY-S";
}
