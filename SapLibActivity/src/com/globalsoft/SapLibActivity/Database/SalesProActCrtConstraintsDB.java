package com.globalsoft.SapLibActivity.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesProActCrtConstraintsDB extends SQLiteOpenHelper {
	
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "activity_db";
    
    //Categories Table Definitions
    public static final String TABLE_NAME = "act_crt_table";
    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_PROCESSTYPE = "PROCESS_TYPE";
    public static final String COL_CATEGORY = "CATEGORY";
    public static final String COL_PDESC20 = "P_DESCRIPTION_20";
    public static final String COL_DESC = "DESCRIPTION";

    private static final String CREATE_TABLE_NAME = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " (" + COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ COL_PROCESSTYPE + " text NOT NULL, " + COL_CATEGORY + " text NOT NULL, "
            + COL_PDESC20 + " text NOT NULL, "+ COL_DESC + " text NOT NULL"+ ");";
    
    //Status Table Definitions
    public static final String STATUS_TABLE_NAME = "act_status_table";   
    public static final String STATUS_COL_ID = BaseColumns._ID; 
    public static final String COL_STATUS = "STATUS";
    public static final String COL_TXT30 = "TXT30";
    public static final String COL_ZZSTATUS_ICON = "ZZSTATUS_ICON";
    public static final String COL_ZZSTATUS_POSTSETACTION = "ZZSTATUS_POSTSETACTION";
    
    private static final String CREATE_STATUS_TABLE_NAME = "CREATE TABLE IF NOT EXISTS "
            + STATUS_TABLE_NAME + " (" + STATUS_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ COL_STATUS + " text NOT NULL, " + COL_TXT30 + " text NOT NULL, "+ COL_ZZSTATUS_ICON + " text NOT NULL, "+ COL_ZZSTATUS_POSTSETACTION + " text "+ ");";
    
    //Activity list Table Definitions
    public static final String ACT_LIST_TABLE_NAME = "act_list_table";
    public static final String ACT_LIST_COL_ID = BaseColumns._ID;
    public static final String ACT_COL_KUNNR = "KUNNR";
    public static final String ACT_COL_KUNNR_NAME = "KUNNR_NAME";
    public static final String ACT_COL_PARNR = "PARNR";
    public static final String ACT_COL_PARNR_NAME = "PARNR_NAME";
    public static final String ACT_COL_OBJECT_ID = "OBJECT_ID";
    public static final String ACT_COL_PROCESS_TYPE = "PROCESS_TYPE";
    public static final String ACT_COL_DESCRIPTION = "DESCRIPTION";
    public static final String ACT_COL_TEXT = "TEXT";
    public static final String ACT_COL_DATE_FROM = "DATE_FROM";
    public static final String ACT_COL_DATE_TO = "DATE_TO";
    public static final String ACT_COL_TIME_FROM = "TIME_FROM";
    public static final String ACT_COL_TIME_TO = "TIME_TO";
    public static final String ACT_COL_TIMEZONE_FROM = "TIMEZONE_FROM";
    public static final String ACT_COL_DURATION_SEC = "DURATION_SEC";
    public static final String ACT_COL_CATEGORY = "CATEGORY";
    public static final String ACT_COL_STATUS = "STATUS";
    public static final String ACT_COL_STATUS_TXT30 = "STATUS_TXT30";
    public static final String ACT_COL_STATUS_REASON = "STATUS_REASON";
    public static final String ACT_COL_DOCUMENTTYPE_DESCRIPTION = "DOCUMENTTYPE_DESCRIPTION";
    public static final String ACT_COL_POSTING_DATE = "POSTING_DATE";

    private static final String CREATE_ACT_LIST_TABLE_NAME = "CREATE TABLE IF NOT EXISTS "
            + ACT_LIST_TABLE_NAME + " (" + ACT_LIST_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ ACT_COL_KUNNR + " text NOT NULL, " + ACT_COL_KUNNR_NAME + " text NOT NULL, "
    		+ ACT_COL_PARNR + " text NOT NULL, " + ACT_COL_PARNR_NAME + " text NOT NULL, "
    		+ ACT_COL_OBJECT_ID + " text NOT NULL, " + ACT_COL_PROCESS_TYPE + " text NOT NULL, "
    		+ ACT_COL_DESCRIPTION + " text NOT NULL, " + ACT_COL_TEXT + " text NOT NULL, "
    		+ ACT_COL_DATE_FROM + " text NOT NULL, " + ACT_COL_DATE_TO + " text NOT NULL, "
    		+ ACT_COL_TIME_FROM + " text NOT NULL, " + ACT_COL_TIME_TO + " text NOT NULL, "
    		+ ACT_COL_TIMEZONE_FROM + " text NOT NULL, " + ACT_COL_DURATION_SEC + " text NOT NULL, "
    		+ ACT_COL_CATEGORY + " text NOT NULL, " + ACT_COL_STATUS + " text NOT NULL, "
    		+ ACT_COL_STATUS_TXT30 + " text NOT NULL, " + ACT_COL_STATUS_REASON + " text NOT NULL, "
            + ACT_COL_DOCUMENTTYPE_DESCRIPTION + " text NOT NULL, "+ ACT_COL_POSTING_DATE + " text NOT NULL"+ ");";
    
    //Activity Customer details list Table Definitions
    public static final String ACT_CUS_LIST_TABLE_NAME = "act_cus_list_table";
    public static final String ACT_CUS_LIST_COL_ID = BaseColumns._ID;
    public static final String ACT_CUS_COL_OBJECT_ID = "OBJECT_ID";
    public static final String ACT_CUS_COL_PROCESS_TYPE = "PROCESS_TYPE";
    public static final String ACT_CUS_COL_KUNNR = "KUNNR";
    public static final String ACT_CUS_COL_KUNNR_NAME = "KUNNR_NAME";
    public static final String ACT_CUS_COL_PARNR = "PARNR";
    public static final String ACT_CUS_COL_PARNR_NAME = "PARNR_NAME";

    private static final String CREATE_ACT_CUS_LIST_TABLE_NAME = "CREATE TABLE IF NOT EXISTS "
            + ACT_CUS_LIST_TABLE_NAME + " (" + ACT_CUS_LIST_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ ACT_CUS_COL_OBJECT_ID + " text NOT NULL, " + ACT_CUS_COL_PROCESS_TYPE + " text NOT NULL, "
    		+ ACT_CUS_COL_KUNNR + " text NOT NULL, " + ACT_CUS_COL_KUNNR_NAME + " text NOT NULL, "
            + ACT_CUS_COL_PARNR + " text NOT NULL, "+ ACT_CUS_COL_PARNR_NAME + " text NOT NULL"+ ");";
    
    //Activity Gallery details list Table Definitions
    public static final String ACT_GAL_LIST_DET_TABLE_NAME = "act_gal_list_det_table";
    public static final String ACT_GAL_LIST_DET_COL_ID = BaseColumns._ID;
    public static final String ACT_GAL_LIST_DET_COL_OBJECT_ID = "OBJECT_ID";
    public static final String ACT_GAL_LIST_DET_COL_GALLERY_UID = "GALLERY_UID";
    public static final String ACT_GAL_LIST_DET_COL_GALLERY_COUNT = "GALLERY_COUNT";
    public static final String ACT_GAL_LIST_DET_COL_EVENT_ID = "EVENT_ID";

    private static final String CREATE_ACT_GAL_LIST_DET_TABLE_NAME = "CREATE TABLE IF NOT EXISTS "
            + ACT_GAL_LIST_DET_TABLE_NAME + " (" + ACT_GAL_LIST_DET_COL_ID + " integer PRIMARY KEY AUTOINCREMENT, " 
    		+ ACT_GAL_LIST_DET_COL_OBJECT_ID + " text NOT NULL, "
            + ACT_GAL_LIST_DET_COL_GALLERY_UID + " text NOT NULL, "
            + ACT_GAL_LIST_DET_COL_GALLERY_COUNT + " text NOT NULL, "
    		+ ACT_GAL_LIST_DET_COL_EVENT_ID + " text NOT NULL"+ ");";
    
    private static final String DB_SCHEMA1 = CREATE_TABLE_NAME; 
    private static final String DB_SCHEMA2 = CREATE_STATUS_TABLE_NAME;
    private static final String DB_SCHEMA3 = CREATE_ACT_LIST_TABLE_NAME;
    private static final String DB_SCHEMA4 = CREATE_ACT_CUS_LIST_TABLE_NAME;
    private static final String DB_SCHEMA5 = CREATE_ACT_GAL_LIST_DET_TABLE_NAME;

    public SalesProActCrtConstraintsDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_SCHEMA1);
        db.execSQL(DB_SCHEMA2);
        db.execSQL(DB_SCHEMA3);
        db.execSQL(DB_SCHEMA4);
        db.execSQL(DB_SCHEMA5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	 if(oldVersion < newVersion){
    		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    		 db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE_NAME);
    		 db.execSQL("DROP TABLE IF EXISTS " + CREATE_ACT_LIST_TABLE_NAME);
    		 db.execSQL("DROP TABLE IF EXISTS " + CREATE_ACT_CUS_LIST_TABLE_NAME);
    		 db.execSQL("DROP TABLE IF EXISTS " + CREATE_ACT_GAL_LIST_DET_TABLE_NAME);
             onCreate(db);
             SapGenConstants.showLog("Upgrading database. Existing contents will be lost. ["
                     + oldVersion + "]->[" + newVersion + "]");
         }      
    }

    
}//End of class SalesProActCrtConstraintsDB
