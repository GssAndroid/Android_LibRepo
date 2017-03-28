package com.globalsoft.SalesPro.Database;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SalesProPriceListDB extends SQLiteOpenHelper {
	
	private static final int DB_VERSION = 4;
    private static final String DB_NAME = "pricelist_db";
    
    
    private static final String CREATE_TABLE_PRICE_LIST_EMP = "CREATE TABLE IF NOT EXISTS "
            + PriceListDBConstants.TABLE_SEARCH_LIST_EMP + " (" + PriceListDBConstants.PL_SER_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ PriceListDBConstants.PL_SER_COL_MATNR + " text NOT NULL, " + PriceListDBConstants.PL_SER_COL_MAKTX + " text NOT NULL, "
    		+ PriceListDBConstants.PL_SER_COL_MEINH + " text NOT NULL, " + PriceListDBConstants.PL_SER_COL_MSEHT +  " text NOT NULL"+ ");";
    
    
    
    private static final String CREATE_TABLE_SELECTED_MAT_LIST = "CREATE TABLE IF NOT EXISTS "
            + PriceListDBConstants.TABLE_SELECTED_MAT_LIST + " (" + PriceListDBConstants.PL_SEL_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ PriceListDBConstants.PL_SEL_COL_MAKTX + " text NOT NULL, " + PriceListDBConstants.PL_SEL_COL_KBETR + " text NOT NULL, "
    		+ PriceListDBConstants.PL_SEL_COL_KONWA + " text NOT NULL, " + PriceListDBConstants.PL_SEL_COL_KPEIN + " text NOT NULL, "
    		+ PriceListDBConstants.PL_SEL_COL_KMEIN + " text NOT NULL, " + PriceListDBConstants.PL_SEL_COL_MATNR + " text NOT NULL, "
    		+ PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT + " text NOT NULL, " +PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT + " text NOT NULL, " + PriceListDBConstants.PL_SEL_COL_KSCHL + " text NOT NULL, "
    	    		+ PriceListDBConstants.PL_SEL_COL_PLTYP +  " text NOT NULL"+ ");";
    
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_PRICE_LIST_EMP; 
    private static final String DB_SCHEMA2 = CREATE_TABLE_SELECTED_MAT_LIST;
    
    
    public SalesProPriceListDB(Context context) {
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
    		 db.execSQL("DROP TABLE IF EXISTS " + PriceListDBConstants.TABLE_SEARCH_LIST_EMP);
    		 db.execSQL("DROP TABLE IF EXISTS " + PriceListDBConstants.TABLE_SELECTED_MAT_LIST);
    		 
             onCreate(db);
             SalesOrderProConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }
}//end of SalesProPriceListDB
