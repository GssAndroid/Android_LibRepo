package com.globalsoft.SalesOrderLib.Database;



import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;




import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SalesOrderDB extends SQLiteOpenHelper {
	private static final int DB_VERSION = 4;
    private static final String DB_NAME = "salesorder_db";
                
    private static final String CREATE_TABLE_SALES_ORDER = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SALESORDER_LIST + " (" + SalesOrderDBConstants.SO_MAIN_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
            + SalesOrderDBConstants.SO_MAIN_COL_VBELN + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_POSNR + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_MAIN_COL_MATNR + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_KWMENG +  " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_MAIN_COL_VRKME + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_NETWR + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_MAIN_COL_WAERK + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_ABGRU_TEXT +  " text NOT NULL, "
   			+ SalesOrderDBConstants.SO_MAIN_COL_FAKSP_TEXT + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_GBSTA_TEXT + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_MAIN_COL_LFSTA_TEXT + " text NOT NULL, " + SalesOrderDBConstants.SO_MAIN_COL_FKSTA_TEXT +  " text NOT NULL, "+  SalesOrderDBConstants.SO_MAIN_COL_ARKTX +  " text NOT NULL "+");";
    
    
    private static final String CREATE_TABLE_SO_HEAD_OP_LIST = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST + " (" + SalesOrderDBConstants.SO_HEAD_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesOrderDBConstants.SO_HEAD_COL_VBELN + " text NOT NULL, " + SalesOrderDBConstants.SO_HEAD_COL_KUNAG + " text , "
    		+ SalesOrderDBConstants.SO_HEAD_COL_NAME1A + " text , " + SalesOrderDBConstants.SO_HEAD_COL_LAND1A + " text , "
    		+ SalesOrderDBConstants.SO_HEAD_COL_REGIOA + " text , " + SalesOrderDBConstants.SO_HEAD_COL_ORT01A + " text , "
    		+ SalesOrderDBConstants.SO_HEAD_COL_STRASA + " text , " + SalesOrderDBConstants.SO_HEAD_COL_TELF1A + " text , " + SalesOrderDBConstants.SO_HEAD_COL_TELF2A + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRA + " text , " + SalesOrderDBConstants.SO_HEAD_COL_PARNR +" text NOT NULL, "  + SalesOrderDBConstants.SO_HEAD_COL_NAME1PK + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_LAND1P + " text , " + SalesOrderDBConstants.SO_HEAD_COL_REGIOP + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_ORT01P + " text , " + SalesOrderDBConstants.SO_HEAD_COL_STRASP + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_TELF1P + " text , " + SalesOrderDBConstants.SO_HEAD_COL_TELF2P + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRP + " text , " +SalesOrderDBConstants.SO_HEAD_COL_AUDAT + " text NOT NULL, " + SalesOrderDBConstants.SO_HEAD_COL_WAERK + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_NETWR + " text , " + SalesOrderDBConstants.SO_HEAD_COL_AUGRU_TEXT + " text , " + SalesOrderDBConstants.SO_HEAD_COL_GBSTK_TEXT + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_ABSTK_TEXT + " text , " + SalesOrderDBConstants.SO_HEAD_COL_LFSTK_TEXT + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_CMGST_TEXT + " text , " + SalesOrderDBConstants.SO_HEAD_COL_SPSTG_TEXT + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_LIFSK_TEXT + " text , " + SalesOrderDBConstants.SO_HEAD_COL_FKSTK_TEXT + " text , "
    	    + SalesOrderDBConstants.SO_HEAD_COL_FAKSK_TEXT + " text , " +SalesOrderDBConstants.SO_HEAD_COL_ZZSTATUS_SUMMARY + " text , " + SalesOrderDBConstants.SO_HEAD_COL_KETDAT + " text , "
    	 	+ SalesOrderDBConstants.SO_HEAD_COL_BSTKD + " text , "+ SalesOrderDBConstants.SO_HEAD_COL_BSTDK + " text " +  ");";
    
    
    private static final String CREATE_TABLE_SO_CUST_LIST = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST + " (" + SalesOrderDBConstants.SO_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesOrderDBConstants.SO_CUST_COL_KUNNR + " text NOT NULL, " +  SalesOrderDBConstants.SO_CUST_COL_NAME1  + " text, "
    		+ SalesOrderDBConstants.SO_CUST_COL_LAND1 + " text NOT NULL, " + SalesOrderDBConstants.SO_CUST_COL_REGIO + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_CUST_COL_ORT01 + " text NOT NULL, " 
    		+ SalesOrderDBConstants.SO_CUST_COL_STRAS + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_CUST_COL_TELF1 + " text NOT NULL , " + SalesOrderDBConstants.SO_CUST_COL_TELF2  + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_CUST_COL_SMTP_ADDR +  " text   " +");";
    
    private static final String CREATE_TABLE_MATT_LIST_EMP = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_MATT_LIST + " (" + SalesOrderDBConstants.SO_MATT_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesOrderDBConstants.SO_MATT_COL_MATNR + " text NOT NULL, " + SalesOrderDBConstants.SO_MATT_COL_MAKTX + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_MATT_COL_MEINH + " text, " + SalesOrderDBConstants.SO_MATT_COL_MSEHT +  " text NOT NULL " + ");";
    
    private static final String CREATE_TABLE_HEAD_PRICE_LIST_EMP = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST + " (" + SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesOrderDBConstants.SO_HEAD_PRICE_COL_KUNAG + " text NOT NULL, " + SalesOrderDBConstants.SO_HEAD_PRICE_COL_KETDAT + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_HEAD_PRICE_COL_NETWR + " text, " + SalesOrderDBConstants.SO_HEAD_PRICE_COL_WAERK +  " text NOT NULL " + ");";
    
    private static final String CREATE_TABLE_ITEM_PRICE_LIST_EMP = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST + " (" + SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesOrderDBConstants.SO_ITEM_PRICE_COL_POSNR + " text NOT NULL, " + SalesOrderDBConstants.SO_ITEM_PRICE_COL_MATNR + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_ITEM_PRICE_COL_KWMENG + " text, " + SalesOrderDBConstants.SO_ITEM_PRICE_COL_VRKME + " text NOT NULL, " 
    		+SalesOrderDBConstants.SO_ITEM_PRICE_COL_NETWR + " text, " + SalesOrderDBConstants.SO_ITEM_PRICE_COL_WAERK + " text NOT NULL " + ");";
    
    private static final String CREATE_TABLE_SO_CREATE_TABLE_LIST = "CREATE TABLE IF NOT EXISTS "
            + SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST + " (" + SalesOrderDBConstants.SO_CREATE_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
            + SalesOrderDBConstants.SO_CREATE_COL_KUNNR + " text NOT NULL, "+ SalesOrderDBConstants.SO_CREATE_COL_ALT_ID + " text NOT NULL, " + SalesOrderDBConstants.SO_CREATE_COL_ARKTX + " text NOT NULL, " + SalesOrderDBConstants.SO_CREATE_COL_MATNR + " text NOT NULL, "
    		+ SalesOrderDBConstants.SO_CREATE_COL_KWMENG + " text, " + SalesOrderDBConstants.SO_CREATE_COL_VRKME + " text NOT NULL, " 
    		+SalesOrderDBConstants.SO_CREATE_COL_NETWR + " text, " + SalesOrderDBConstants.SO_CREATE_COL_WAERK + " text NOT NULL " + ");";
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_SALES_ORDER; 
    private static final String DB_SCHEMA2 = CREATE_TABLE_SO_HEAD_OP_LIST;
    private static final String DB_SCHEMA3 = CREATE_TABLE_SO_CUST_LIST;
    private static final String DB_SCHEMA4 = CREATE_TABLE_MATT_LIST_EMP;
    private static final String DB_SCHEMA5 = CREATE_TABLE_HEAD_PRICE_LIST_EMP;
    private static final String DB_SCHEMA6 = CREATE_TABLE_ITEM_PRICE_LIST_EMP;
    private static final String DB_SCHEMA7 = CREATE_TABLE_SO_CREATE_TABLE_LIST;
   
    
    
    public SalesOrderDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA1);
        db.execSQL(DB_SCHEMA2);
        db.execSQL(DB_SCHEMA3);
        db.execSQL(DB_SCHEMA4);
        db.execSQL(DB_SCHEMA5);
        db.execSQL(DB_SCHEMA6);  
        db.execSQL(DB_SCHEMA7); 
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 if(oldVersion < newVersion){
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SALESORDER_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_MATT_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST);    
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST); 
             onCreate(db);
             SalesOrderConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }
    
    
}