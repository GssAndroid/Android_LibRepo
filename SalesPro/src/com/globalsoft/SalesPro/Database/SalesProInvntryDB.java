package com.globalsoft.SalesPro.Database;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SalesProInvntryDB extends SQLiteOpenHelper {
	
	private static final int DB_VERSION = 4;
    private static final String DB_NAME = "invntry_db";
    
    
    private static final String CREATE_TABLE_INV_LIST_EMP = "CREATE TABLE IF NOT EXISTS "
            + SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP + " (" + SalesProInvntryDBConstants.IL_SER_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesProInvntryDBConstants.IL_SER_COL_MATNR + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SER_COL_MAKTX + " text NOT NULL, "
    		+ SalesProInvntryDBConstants.IL_SER_COL_MEINH + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SER_COL_MSEHT +  " text NOT NULL"+ ");";
    
    
    
    private static final String CREATE_TABLE_SELECTED_IN_LIST = "CREATE TABLE IF NOT EXISTS "
            + SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST + " (" + SalesProInvntryDBConstants.IL_SEL_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ SalesProInvntryDBConstants.IL_SEL_COL_MAKTX + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SEL_COL_MEINS + " text NOT NULL, "
    		+ SalesProInvntryDBConstants.IL_SEL_COL_LABST + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SEL_COL_UMLME + " text NOT NULL, "
    		+ SalesProInvntryDBConstants.IL_SEL_COL_INSME + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SEL_COL_MATNR + " text NOT NULL, "
    		+ SalesProInvntryDBConstants.IL_SEL_COL_WERKS_TEXT + " text NOT NULL, " +SalesProInvntryDBConstants.IL_SEL_COL_LGOBE + " text NOT NULL, " + SalesProInvntryDBConstants.IL_SEL_COL_WERKS + " text NOT NULL, "
    	    		+ SalesProInvntryDBConstants.IL_SEL_COL_LGORT +  " text NOT NULL"+ ");";
    
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_INV_LIST_EMP; 
    private static final String DB_SCHEMA2 = CREATE_TABLE_SELECTED_IN_LIST;
    
    
    public SalesProInvntryDB(Context context) {
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
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP);
    		 db.execSQL("DROP TABLE IF EXISTS " + SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST);
    		 
             onCreate(db);
             SalesOrderProConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }
}//end of SalesProPriceListDB

