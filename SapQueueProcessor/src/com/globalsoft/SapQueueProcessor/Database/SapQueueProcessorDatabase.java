package com.globalsoft.SapQueueProcessor.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapQueueProcessor.Utils.SapQueueProcessorConstants;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBConstants;

public class SapQueueProcessorDatabase extends SQLiteOpenHelper {
	
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "queueprocessor_db";
 
    private static final String CREATE_TABLE_QUEUEPRSSR = "CREATE TABLE "
            + SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR + " (" + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID + " text NOT NULL, "
    		+ SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPNAME + " text NOT NULL, " 
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PCKGNAME + " text NOT NULL, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_CLASSNAME + " text NOT NULL, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME + " text NOT NULL, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_ALT_ID + " text NOT NULL, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_DATE + " INTEGER, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA + " BLOB, " 
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_SOAPDATA_RESPONSE + " BLOB, " 
            + SapQueueProcessorDBConstants.QUEUEPRSSR_FLAG + " text NOT NULL, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+ " INTEGER NOT NULL DEFAULT 0, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_TIME + " INTEGER NOT NULL DEFAULT 0, "
            + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT + " INTEGER NOT NULL DEFAULT 0"+ ");";
    
    /*private static final String CREATE_TRIGGER_QUEUEPRSSR = "CREATE TRIGGER queueprocessor_trigger UPDATE OF "
			+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+
			" ON "+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR+" " +
			"BEGIN UPDATE "+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR+" SET "+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT+" = "
			+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT+"+1 "
			+"WHERE "+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS+" = "+SapQueueProcessorConstants.STATUS_SENDTOSERVER+" AND "
			+SapQueueProcessorDatabase.SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" >= 0;  END;";
    */
 	
    
    private static final String DB_SCHEMA = CREATE_TABLE_QUEUEPRSSR;

    public SapQueueProcessorDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA);
        //db.execSQL(CREATE_TRIGGER_QUEUEPRSSR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 if(oldVersion < newVersion){
    		 db.execSQL("DROP TABLE IF EXISTS " + SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR);
             onCreate(db);
             SapQueueProcessorConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }

    
}//End of class SapQueueProcessorDatabase
