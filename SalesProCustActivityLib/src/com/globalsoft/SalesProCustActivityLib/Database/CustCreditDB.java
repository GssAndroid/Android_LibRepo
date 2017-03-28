package com.globalsoft.SalesProCustActivityLib.Database;

import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CustCreditDB extends SQLiteOpenHelper {
	
	private static final int DB_VERSION = 4;
    private static final String DB_NAME = "custlist_db";
    
    private static final String CREATE_TABLE_SEARCHED_CUS_LIST = "CREATE TABLE IF NOT EXISTS "
            + CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_ + " (" 
    		+ CustCreditDBConstants.CUS_SER_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ CustCreditDBConstants.CUS_SER_COL_KUNNR + " text NOT NULL, " 
            + CustCreditDBConstants.CUS_SER_COL_NAME1 + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_LAND1 + " text NOT NULL, " 
            + CustCreditDBConstants.CUS_SER_COL_REGIO + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_ORT01 + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SER_COL_STRAS + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_TELF1 + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_TELF2 +  " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_SMTP_ADDR +  " text NOT NULL" +");";
    

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
    
    private static final String CREATE_TABLE_CUS_CNTX = "CREATE TABLE IF NOT EXISTS "
            + CustCreditDBConstants.TABLE_CUS_CNTX + " (" 
    		+ CustCreditDBConstants.CUS_CNTX_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ CustCreditDBConstants.CUS_CNTX_COL_CNTXT2 + " text NOT NULL, " 
            + CustCreditDBConstants.CUS_CNTX_COL_CNTXT3 + " text NOT NULL, "
            + CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_CNTX_COL_NAME + " text NOT NULL, " 
            + CustCreditDBConstants.CUS_CNTX_COL_DPNDNCY + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_CNTX_COL_SEQNR + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_CNTX_COL_TYPE + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_CNTX_COL_SIGN + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_CNTX_COL_OPTN +  " text NOT NULL, "
    	    + CustCreditDBConstants.CUS_CNTX_COL_VALUE +  " text NOT NULL, "
    	    + CustCreditDBConstants.CUS_CNTX_COL_VALUE_HIGH +  " text NOT NULL, "
    	    + CustCreditDBConstants.CUS_CNTX_COL_TRGTNAME +  " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_CNTX_COL_TRGTVALUE +  " text NOT NULL" +");";
        
    private static final String CREATE_TABLE_SELECTED_CUS_LIST = "CREATE TABLE IF NOT EXISTS "
            + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST + " (" + CustCreditDBConstants.CUS_SEL_COL_ID + " integer NOT NULL PRIMARY KEY AUTOINCREMENT, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_NAME1 + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_CRBLB + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_CTLPC + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_KLIMK + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_OBLIG + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_WAERS + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_KLPRZ + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_DBRTG + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_KUNNR + " text NOT NULL, "
    	    + CustCreditDBConstants.CUS_SEL_COL_CTLPC_TEXT +  " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_ORT01 + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_REGIO + " text NOT NULL, "
    	    		+ CustCreditDBConstants.CUS_SEL_COL_LAND1 +  " text NOT NULL "+ ");" ;
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_SEARCHED_CUS_LIST;
    private static final String DB_SCHEMA2 = CREATE_TABLE_SELECTED_CUS_LIST;
    private static final String DB_SCHEMA3 = CREATE_TABLE_CUS_CNTX;
    
    public CustCreditDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA1);
        db.execSQL(DB_SCHEMA2);
        db.execSQL(DB_SCHEMA3);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 if(oldVersion < newVersion){
    		 db.execSQL("DROP TABLE IF EXISTS " + CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_);
    		 db.execSQL("DROP TABLE IF EXISTS " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST);
    		 db.execSQL("DROP TABLE IF EXISTS " + CustCreditDBConstants.TABLE_CUS_CNTX); 		 
             onCreate(db);
             SalesProCustActivityConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }
}//end of SalesProPriceListDB