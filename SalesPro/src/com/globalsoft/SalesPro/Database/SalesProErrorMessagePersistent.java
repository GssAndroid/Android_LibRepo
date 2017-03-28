package com.globalsoft.SalesPro.Database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

public class SalesProErrorMessagePersistent {
	// Task Related Database
    public static final String KEY_ROWID = "ID";
    public static final String KEY_TRANCID = "TRANCID";
    public static final String KEY_ERRTYPE = "ERRTYPE";
    public static final String KEY_ERRDESC = "ERRDESC";        
    public static final String KEY_APINAME = "APINAME";
    private static final String DATABASE_NAME = "ERRORDB";
    private static final String DATABASE_TABLE = "errorlist";
    private static final int DATABASE_VERSION = 1;
    private Cursor c;
    
    //Error Message Related value    
    public static final String ERRLIST_TABLE = "errorlist";
    private static final String ERRLIST_TABLE_CREATE =
            "create table "+ERRLIST_TABLE+" (id integer primary key autoincrement, " 
             + SalesProErrorMessagePersistent.KEY_TRANCID+" text not null,"
             + SalesProErrorMessagePersistent.KEY_ERRTYPE+" text not null,"
             + SalesProErrorMessagePersistent.KEY_ERRDESC+" text not null," 
             + SalesProErrorMessagePersistent.KEY_APINAME+" text not null);";

    private  final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase sqlitedatabase;
    
    public SalesProErrorMessagePersistent(Context ctext){
    	context = ctext;    
    }
    
    public void insertErrorMsgDetails(String tracId, String errType, String errorDesc, String apiName){
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
		    insertRow(tracId, errType, errorDesc, apiName);
			sqlitedatabase.close();
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in insertTaskDetails:"+e.toString());
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
	    	SalesOrderProConstants.showErrorLog("Error in SalesProErrorMessagePersistent closeDBHelper:"+e.toString());
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
			SalesOrderProConstants.showErrorLog("Error in checkTaskExists:"+e.toString());
		}   
    	return rowcount;
    }//fn checkTableRow
    
    public ArrayList checkErrorTrancIdApiExists(){
    	ArrayList errorTaskIdForStatus = new ArrayList();
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_APINAME+"='"+ContactsConstants.CONTACT_MAINTAIN_API+"'", null);
			//ServiceProConstants.showLog("getCount:"+c.getCount());
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_TRANCID);
					 String tranc_no = c.getString(index);					 
					 //ServiceProConstants.showLog("tranc_no:"+tranc_no);
					 errorTaskIdForStatus.add(tranc_no);
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in checkErrorTrancIdApiExists:"+e.toString());
		}   
    	return errorTaskIdForStatus;
    }//fn checkErrorTrancIdApiExists
    
    public boolean checkTrancIdApiExists(String id, String apiName){
    	boolean idexists = false;
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			//int idValue = Integer.parseInt(id);
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_TRANCID+"='"+id+"' and "+KEY_APINAME+"='"+apiName+"'", null);
			//ServiceProConstants.showLog("select * from "+DATABASE_TABLE+" where "+KEY_TRANCID+"="+idValue+" and "+KEY_APINAME+"='"+apiName+"'");
			//ServiceProConstants.showLog("getCount:"+c.getCount());
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_TRANCID);
					 String tranc_no = c.getString(index);
					 int index1 = c.getColumnIndex(KEY_APINAME);
					 String apiNameStr = c.getString(index1);
					 /*ServiceProConstants.showLog("tranc_no:"+tranc_no);
					 ServiceProConstants.showLog("apiNameStr"+apiNameStr);*/
		     		 if(id.equals(tranc_no) && apiName.equals(apiNameStr)){
		     			 idexists = true;
		     			 //ServiceProConstants.showLog("Id value:"+c.getInt(index));
     				 	 sqlitedatabase.close();
     				 	 return idexists;
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in checkTrancIdApiExists:"+e.toString());
		}   
    	return idexists;
    }//fn checkTrancIdExists
    
    public String getErrorMsg(String id, String apiName){
    	String msg = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			//int idValue = Integer.parseInt(id);
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_TRANCID+"='"+id+"' and "+KEY_APINAME+"='"+apiName+"'", null);
			//ServiceProConstants.showLog("select * from "+DATABASE_TABLE+" where "+KEY_TRANCID+"="+idValue+" and "+KEY_APINAME+"='"+apiName+"'");
			//ServiceProConstants.showLog("getCount:"+c.getCount());
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_TRANCID);
					 String tranc_no = c.getString(index);
					 int index1 = c.getColumnIndex(KEY_APINAME);
					 String apiNameStr = c.getString(index1);
					 /*ServiceProConstants.showLog("tranc_no:"+tranc_no);
					 ServiceProConstants.showLog("apiNameStr"+apiNameStr);*/
		     		 if(id.equals(tranc_no) && apiName.equals(apiNameStr)){
		     			 int indexMsg = c.getColumnIndex(KEY_ERRDESC);
						 String errorMsg = String.valueOf(c.getString(indexMsg));
						 if(errorMsg != null && errorMsg.length() > 0){
							 msg = errorMsg;
						 }
		     		 }
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in checkTrancIdApiExists:"+e.toString());
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
        	System.out.println("Table before creation.");
            db.execSQL(ERRLIST_TABLE_CREATE); 
            System.out.println("Table after creation.");
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
        initialValues.put(KEY_TRANCID, tracId.toString().trim());
        initialValues.put(KEY_ERRTYPE, errType.toString().trim());
        initialValues.put(KEY_ERRDESC, errorDesc.trim());
        initialValues.put(KEY_APINAME, apiName.trim());
        return sqlitedatabase.insert(DATABASE_TABLE, null, initialValues);
    }//fn insertRow
    
    public Cursor getAllRows()
    {        
        return sqlitedatabase.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TRANCID, KEY_ERRTYPE, KEY_ERRDESC, KEY_APINAME},
        null, null, null, null, null);
    }//fn getAllRows
    
    public void updateValue(String tracId, String errType, String errorDesc, String apiName){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+DATABASE_TABLE+" set "+KEY_TRANCID+"='"+tracId+"' ,"
							+KEY_ERRTYPE+"='"+errType+"' ,"
							+KEY_ERRDESC+"='"+errorDesc+"', "
							+KEY_APINAME+"='"+apiName+"' where "+KEY_TRANCID+"='"+tracId+"' and "+KEY_APINAME+"='"+apiName+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
			System.out.println("Error update data:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn update_data
    
    public String[] getIdList()
    {   	
    	String[] resArray = null;
    	try {
    		DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			resArray = new String[c.getCount()];
			int i=0;
			if (c.moveToFirst())
			{
			     do {
			    	 int index0 = c.getColumnIndex(KEY_TRANCID);
		     		 String KEY_TRANCID = c.getString(index0);		     		 
		     		 resArray[i] = KEY_TRANCID;		     		 
		     		 i++;
			     } while (c.moveToNext());
			}
			sqlitedatabase.close();
			c.close();
    	} catch (Exception e) {
    		SalesOrderProConstants.showErrorLog("Error in getIdList:"+e.toString());
			sqlitedatabase.close();
		}
		return resArray;   
    }//fn getIdList 
    
    public void deleteRow(String tracId)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+ERRLIST_TABLE+" WHERE "+KEY_TRANCID+"='"+tracId+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
    		SalesOrderProConstants.showErrorLog("Error in deleteRow:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn deleteRow
    
    public void deleteRowForStatus(String tracId, String apiName)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+ERRLIST_TABLE+" WHERE "+KEY_TRANCID+"='"+tracId+"' and "+KEY_APINAME+"='"+apiName+"'");
			sqlitedatabase.close();
    	} catch (Exception e) {
    		SalesOrderProConstants.showErrorLog("Error in deleteRowForStatus:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn deleteRowForStatus
    
}//End of class SalesProErrorMessagePersistent