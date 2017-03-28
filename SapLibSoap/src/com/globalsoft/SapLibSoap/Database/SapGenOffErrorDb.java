package com.globalsoft.SapLibSoap.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SapGenOffErrorDb   extends SQLiteOpenHelper {
	
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "offline_db";

    public static final String TABLE_OFFERROR = "error_table";
    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_APPREFID = "apprefid";
    public static final String COL_APPNAME = "appname";
    public static final String COL_FUNCNAME = "apiname";
    public static final String COL_ERRTYPE = "errortype";
    public static final String COL_ERRDESC = "errordesc";

 
    private static final String CREATE_TABLE_OFFERRORTABLE = "CREATE TABLE "
            + TABLE_OFFERROR + " (" + COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
            + COL_APPREFID + " INTEGER NOT NULL DEFAULT -1, "
    		+ COL_APPNAME + " text NOT NULL, " + COL_FUNCNAME + " text NOT NULL, "
            + COL_ERRTYPE + " text NOT NULL, "+ COL_ERRDESC + " text NOT NULL"+ ");";
    
    
    private static final String DB_SCHEMA = CREATE_TABLE_OFFERRORTABLE;

    public SapGenOffErrorDb(Context context) {
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
    		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFERROR);
             onCreate(db);
             SapGenConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }

    
}//End of class SapGenOffErrorDb


