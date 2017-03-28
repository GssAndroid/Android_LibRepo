package com.globalsoft.SalesPro.Database;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustCreditDB extends SQLiteOpenHelper {
	
	private static final int DB_VERSION = 4;
    private static final String DB_NAME = "custlist_db";
    
    
    private static final String CREATE_TABLE_SEARCHED_CUS_LIST = "CREATE TABLE IF NOT EXISTS "
            + CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_ + " (" + CustCreditDBConstants.CUS_SER_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ CustCreditDBConstants.CUS_SER_COL_KUNNR + " text NOT NULL, " + CustCreditDBConstants.CUS_SER_COL_NAME1 + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_LAND1 + " text NOT NULL, " + CustCreditDBConstants.CUS_SER_COL_REGIO +  " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_ORT01 + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SER_COL_STRAS + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SER_COL_TELF1 + " text NOT NULL, " + CustCreditDBConstants.CUS_SER_COL_TELF2 +  " text NOT NULL"
    		+ CustCreditDBConstants.CUS_SER_COL_SMTP_ADDR +  " text NOT NULL" +");";
    		
    
    
    
    private static final String CREATE_TABLE_SELECTED_CUS_LIST = "CREATE TABLE IF NOT EXISTS "
            + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST + " (" + CustCreditDBConstants.CUS_SEL_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_NAME1 + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_KBETR + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_KONWA + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_KPEIN + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_KMEIN + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_MATNR + " text NOT NULL, "
    		+ CustCreditDBConstants.CUS_SEL_COL_KSCHL_TEXT + " text NOT NULL, " + CustCreditDBConstants.CUS_SEL_COL_PLTYP_TEXT + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_KSCHL + " text NOT NULL, "
    	    + CustCreditDBConstants.CUS_SEL_COL_PLTYP +  " text NOT NULL"+ 
    		CustCreditDBConstants.CUS_SEL_COL_ORT01 + " text NOT NULL, " 
    		+ CustCreditDBConstants.CUS_SEL_COL_REGIO + " text NOT NULL, "
    	    		+ CustCreditDBConstants.CUS_SEL_COL_LAND1 +  " text NOT NULL"+   ");";
    
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_SEARCHED_CUS_LIST; 
    private static final String DB_SCHEMA2 = CREATE_TABLE_SELECTED_CUS_LIST;
    
    
    public CustCreditDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA1);
        db.execSQL(DB_SCHEMA2);
        
    }
    
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 if(oldVersion < newVersion){
    		 db.execSQL("DROP TABLE IF EXISTS " + CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_);
    		 db.execSQL("DROP TABLE IF EXISTS " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST);
    		 
             onCreate(db);
             SalesOrderProConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }
}//end of SalesProPriceListDB

