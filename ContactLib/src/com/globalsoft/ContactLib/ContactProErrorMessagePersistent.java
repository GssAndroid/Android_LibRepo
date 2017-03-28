package com.globalsoft.ContactLib;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.ContactLib.Utils.ContactsConstants;

public class ContactProErrorMessagePersistent {
	// Task Related Database
    public static final String KEY_ROWID = "ID";
    public static final String KEY_ID = "CONTACT_ID";
    public static final String KEY_ERRTYPE = "ERRTYPE";
    public static final String KEY_ERRDESC = "ERRDESC";        
    public static final String KEY_APINAME = "APINAME";
    private static final String DATABASE_NAME = "ERROR_TABLE";
    private static final String DATABASE_TABLE = "errorlist";
    private static final int DATABASE_VERSION = 1;
    private Cursor c;

    private  final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase sqlitedatabase;
    
    private static final String ERRLIST_TABLE_CREATE =
            "create table "+DATABASE_TABLE+" (id integer primary key autoincrement, " 
             + KEY_ID+" text not null,"
             + KEY_ERRTYPE+" text not null,"
             + KEY_ERRDESC+" text not null," 
             + KEY_APINAME+" text not null);";
    
    public ContactProErrorMessagePersistent(Context ctext){
    	context = ctext;    
    }
    
    public void insertErrorMsgDetails(String tracId, String errType, String errorDesc, String apiName){
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
		    insertRow(tracId, errType, errorDesc, apiName);
			sqlitedatabase.close();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in insertTaskDetails:"+e.toString());
		}
    }//fn insertErrorMsgDetails
    
    public void closeDBHelper(){
    	try{
	    	if(DBHelper != null)
	    		DBHelper.close();
	    	if (c != null) {
	            c.close();
	            c = null;
	    	}
	    } catch (Exception e) {
			ContactsConstants.showErrorLog("Error in ContactProErrorMessagePersistent closeDBHelper:"+e.toString());
		}
    }//fn closeDBHelper       
    
    public int checkTableRow(){
    	int rowcount = 0;
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			rowcount = c.getCount();
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in checkTaskExists:"+e.toString());
		}   
    	return rowcount;
    }//fn checkTableRow
    
    /*public ArrayList checkErrorTrancIdApiExists(){
    	ArrayList errorTaskIdForStatus = new ArrayList();
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_APINAME+"='"+ContactsConstants.STATUS_UPDATE_API+"'", null);
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_ID);
					 String tranc_no = c.getString(index);					 
					 errorTaskIdForStatus.add(tranc_no);
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in checkErrorTrancIdApiExists:"+e.toString());
		}   
    	return errorTaskIdForStatus;
    }//fn checkErrorTrancIdApiExists
*/    
    public boolean checkTrancIdApiExists(String id, String apiName){
    	boolean idexists = false;
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_ID+"='"+id+"' and "+KEY_APINAME+"='"+apiName+"'", null);
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_ID);
					 String tranc_no = c.getString(index);
					 int index1 = c.getColumnIndex(KEY_APINAME);
					 String apiNameStr = c.getString(index1);
		     		 if(id.equals(tranc_no) && apiName.equals(apiNameStr)){
		     			 idexists = true;
     				 	 sqlitedatabase.close();
     				 	 return idexists;
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in checkTrancIdApiExists:"+e.toString());
		}   
    	return idexists;
    }//fn checkTrancIdExists
    
    public String getErrorMsg(String id, String apiName){
    	String msg = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_ID+"='"+id+"' and "+KEY_APINAME+"='"+apiName+"'", null);
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_ID);
					 String tranc_no = c.getString(index);
					 int index1 = c.getColumnIndex(KEY_APINAME);
					 String apiNameStr = c.getString(index1);
		     		 if(id.equals(tranc_no) && apiName.equals(apiNameStr)){
		     			 int indexMsg = c.getColumnIndex(KEY_ERRDESC);
						 String errorMsg = String.valueOf(c.getString(indexMsg));
						 if(errorMsg != null && errorMsg.length() > 0){
							 msg = errorMsg;
							 return msg;
						 }
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in checkTrancIdApiExists:"+e.toString());
		}   
    	finally{
    		return msg;
    	}    	
    }//fn getErrorMsg
       
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
    	DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(ERRLIST_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }//End of class DatabaseHelper 
    
    public long insertRow(String tracId, String errType, String errorDesc, String apiName)
    {		
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID, tracId.toString().trim());
        initialValues.put(KEY_ERRTYPE, errType.toString().trim());
        initialValues.put(KEY_ERRDESC, errorDesc.trim());
        initialValues.put(KEY_APINAME, apiName.trim());
        return sqlitedatabase.insert(DATABASE_TABLE, null, initialValues);
    }//fn insertRow
    
    public Cursor getAllRows()
    {        
        return sqlitedatabase.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_ID, KEY_ERRTYPE, KEY_ERRDESC, KEY_APINAME},
        null, null, null, null, null);
    }//fn getAllRows
    
    public void updateValue(String tracId, String errType, String errorDesc, String apiName){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+DATABASE_TABLE+" set "+KEY_ID+"='"+tracId+"' ,"
							+KEY_ERRTYPE+"='"+errType+"' ,"
							+KEY_ERRDESC+"='"+errorDesc+"', "
							+KEY_APINAME+"='"+apiName+"' where "+KEY_ID+"='"+tracId+"' and "+KEY_APINAME+"='"+apiName+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
			System.out.println("Error update data:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn update_data
    
    public ArrayList getIdList()
    {   	
    	ArrayList resArray = new ArrayList();
    	try {
    		DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			int i=0;
			if (c.moveToFirst())
			{
			     do {
			    	 int index0 = c.getColumnIndex(KEY_ID);
		     		 String KEY_ID = c.getString(index0);	
		     		 resArray.add(KEY_ID);	
		     		 i++;
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
    	} catch (Exception e) {
    		ContactsConstants.showErrorLog("Error in getIdList:"+e.toString());
			sqlitedatabase.close();
		}
		return resArray;   
    }//fn getIdList 
    
    public void deleteRow(String tracId)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+DATABASE_TABLE+" WHERE "+KEY_ID+"='"+tracId+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
    		ContactsConstants.showErrorLog("Error in deleteRow:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn deleteRow
    
    public void deleteRowForStatus(String tracId, String apiName)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+DATABASE_TABLE+" WHERE "+KEY_ID+"='"+tracId+"' and "+KEY_APINAME+"='"+apiName+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
    		ContactsConstants.showErrorLog("Error in deleteRowForStatus:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn deleteRowForStatus
    
}//End of class ContactProErrorMessagePersistent