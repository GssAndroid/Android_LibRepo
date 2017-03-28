package com.globalsoft.ProductLib.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductColumnDB {
	
	//Product Column Related Database
    public static final String KEY_ROWID = "ID"; 
    public static final String KEY_TABLENAMES = "TABLENAME";
    public static final String KEY_COLUMNNAMES = "COLUMNNAMES";

    private static final String DATABASE_NAME = "ProductColumns";
    private static final String TABLE_NAME = "columnlists";
    private static final int DATABASE_VERSION = 1;
    
	//Product Column Related value    
    private static final String COLUMNLIST_TABLE_CREATE =
            "create table "+TABLE_NAME+" (id integer primary key autoincrement, " 
             + KEY_TABLENAMES +" text not null," 
             + KEY_COLUMNNAMES +" text not null);";
    
    private Cursor c;

    private  final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase sqlitedatabase;
    
    public ProductColumnDB(Context ctext){
    	context = ctext;    
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(COLUMNLIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			onCreate(db);
		}
	}//End of class DatabaseHelper 
    
    public void insertColumnsDetails(String tableName, String colNames){
    	try {
			boolean exits = checkAlrExistsTableName(tableName);
			SapGenConstants.showLog("tableName: "+tableName);
			SapGenConstants.showLog("exits: "+exits);
			if(exits){
				updateColumnValue(tableName, colNames);
			}else{
			    insertRow(tableName, colNames);
			}
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertColumnsDetails:"+e.toString());
		}finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    }//fn insertColumnsDetails
    
    public boolean checkAlrExistsTableName(String tableName){
    	boolean exists = false;
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select * from "+TABLE_NAME+" where "+KEY_TABLENAMES+"='"+tableName+"'", null);
			if(c != null && c.getCount() > 0){
				if (c.moveToFirst())
				{
				     do {
						 int index = c.getColumnIndex(KEY_TABLENAMES);
						 String tname = c.getString(index);
			     		 if(tableName.equals(tname)){
			     			 exists = true;
	     				 	 sqlitedatabase.close();
	     				 	 return exists;
			     		 }
				     } while (c.moveToNext());
				}
			}else{
				return exists;
			}
			c.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in checkAlrExistsTableName:"+e.toString());
		} finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return exists;
    }//fn checkAlrExistsTableName
    	
	public String readColumnNames(String tableName){
    	String columns = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select * from "+TABLE_NAME+" where "+KEY_TABLENAMES+"='"+tableName+"'", null);
			if (c.moveToFirst())
			{
			     do {
					 int index = c.getColumnIndex(KEY_COLUMNNAMES);
					 String cname = c.getString(index);
					 columns = cname;
					 return columns;
			     } while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in readColumnNames:"+e.toString());
		} finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return columns;
    }//fn readColumnNames
    
    public void updateColumnValue(String tableName, String colNames){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+TABLE_NAME+" set "
							+KEY_COLUMNNAMES+"='"+colNames+"' where "+KEY_TABLENAMES+"='"+tableName+"'");			
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in updateColumnValue:"+e.toString());
		} finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}  
    }//fn updateColumnValue
    
    public void closeDBHelper(){
    	try{
	    	if(DBHelper != null)
	    		DBHelper.close();
	    	if (c != null) {
	            c.close();
	            c = null;
	    	}
	    } catch (Exception e) {
			SapGenConstants.showErrorLog("Error in ProductColumnDB closeDBHelper:"+e.toString());
		}
    }//fn closeDBHelper  
    
    public long insertRow(String tableName, String colNames)
    {		
		DBHelper = new DatabaseHelper(context);
		sqlitedatabase = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
    	initialValues.put(KEY_TABLENAMES, tableName.toString().trim());
		initialValues.put(KEY_COLUMNNAMES, colNames.toString().trim());
		return sqlitedatabase.insert(TABLE_NAME, null, initialValues);
    }//fn insertRow
    
    public Cursor getAllRows()
    {        
        return sqlitedatabase.query(TABLE_NAME, new String[] {
                KEY_ROWID, KEY_TABLENAMES, KEY_COLUMNNAMES},
        null, null, null, null, null);
    }//fn getAllRows
    
    public void deleteRow(String tableName)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+KEY_TABLENAMES+"='"+tableName+"'");
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in deleteRow:"+e.toString());
		} finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    }//fn deleteRow
}