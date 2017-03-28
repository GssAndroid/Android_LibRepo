package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductShoppingCartListPersistent {
	
	// Cart Related Database
	public static final String KEY_ROWID = "ID";
	public static final String KEY_MATID = "MATID";
	public static final String KEY_QTY = "QTY";
	private static final String DATABASE_NAME = "ProductSCart";
	private static final String DATABASE_TABLE = "ProductSCartList";
	private static final int DATABASE_VERSION = 1;
	private Cursor c;
	
	private  final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase sqlitedatabase;
	
    private static final String DATABASE_TABLE_CREATE =
            "create table "+DATABASE_TABLE+" (id integer primary key autoincrement, "   
             + KEY_MATID +" text not null, "
             + KEY_QTY +" text not null);";
    
	public ProductShoppingCartListPersistent(Context ctext){
		context = ctext;    
	}
	
	public void insertMId(String matId, String qty){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
		    insertRow(matId, qty);
			sqlitedatabase.close();
			closeDBHelper();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertTaskDetails:"+e.toString());
			closeDBHelper();
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
			SapGenConstants.showErrorLog("Error in ProductShoppingCartListPersistent closeDBHelper:"+e.toString());
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
			closeDBHelper();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in checkTaskExists:"+e.toString());
			closeDBHelper();
		}   
		return rowcount;
	}//fn checkTableRow
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
	    DatabaseHelper(Context context)
	    {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	
	    public void onCreate(SQLiteDatabase db)
	    {
	        db.execSQL(DATABASE_TABLE_CREATE);
	    }
	
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	    {
	    	db.execSQL("DROP TABLE IF EXISTS titles");
	        onCreate(db);
	    }
	}//End of class DatabaseHelper 
	
	public long insertRow(String matId, String qty)
	{		
	    ContentValues initialValues = new ContentValues();
	    initialValues.put(KEY_MATID, matId.toString().trim());
	    initialValues.put(KEY_QTY, qty.toString().trim());
	    return sqlitedatabase.insert(DATABASE_TABLE, null, initialValues);
	}//fn insertRow
	
	public Cursor getAllRows()
	{        
	    return sqlitedatabase.query(DATABASE_TABLE, new String[] {
	            KEY_ROWID, KEY_MATID, KEY_QTY},
	    null, null, null, null, null);
	}//fn getAllRows
    
    public void deleteCart(String mId)
    {   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("DELETE FROM "+DATABASE_TABLE+" WHERE "+KEY_MATID+"='"+mId+"'");
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in deleteCart:"+e.toString());
			sqlitedatabase.close();
			closeDBHelper();
		}   
    }//fn deleteCart
	
	public ArrayList getCartIdList(){
    	ArrayList cartArrList = new ArrayList();
    	try {
    		if(cartArrList != null && cartArrList.size() > 0){
    			cartArrList.clear();
    		}
    		DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			if(c.getCount() == 0){
				return cartArrList;
			}
			if(c != null){
				if (c.moveToFirst())
				{
				     do {
				    	 int index0 = c.getColumnIndex(KEY_MATID);
			     		 String KEY_MATID = c.getString(index0);		     		 
			     		 if(KEY_MATID != null){
			     			 //SapGenConstants.showLog("KEY_MATID : "+KEY_MATID);
			     			 cartArrList.add(KEY_MATID);
			     		 }
				     } while (c.moveToNext());
				}
			}
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in getCartIdList:"+e.toString());
			sqlitedatabase.close();
			closeDBHelper();
		}
		return cartArrList;   
	}//fn getCartIdList
	
	public ArrayList getCartIdNQtyList(){
    	ArrayList<HashMap<String, String>> cartArrList = new ArrayList<HashMap<String, String>>();
    	try {
    		if(cartArrList != null && cartArrList.size() > 0){
    			cartArrList.clear();
    		}
    		DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();			
			c = sqlitedatabase.rawQuery("select * from "+DATABASE_TABLE, null);
			if(c.getCount() == 0){
				return cartArrList;
			}
    		HashMap<String, String> matMap = null;
			if(c != null){
				if (c.moveToFirst())
				{
				     do {
				    	 matMap = new HashMap<String, String>();
				    	 int index0 = c.getColumnIndex(KEY_MATID);
				    	 int index1 = c.getColumnIndex(KEY_QTY);
			     		 String KEY_MATID = c.getString(index0).toString().trim();
				    	 String KEY_QTY = c.getString(index1).toString().trim();		     		 
			     		 if(KEY_MATID != null){
			     			 SapGenConstants.showLog("KEY_MATID : "+KEY_MATID);
			     			 if(KEY_QTY != null && KEY_QTY.length() > 0){
				     			 SapGenConstants.showLog("KEY_QTY : "+KEY_QTY);
				     			 matMap.put(KEY_MATID, KEY_QTY);
			     			 }else{
				     			 SapGenConstants.showLog("KEY_QTY : ");
				     			 matMap.put(KEY_MATID, "");
			     			 }
			     			 cartArrList.add(matMap);
			     		 }
				     } while (c.moveToNext());
				}
			}
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in getCartIdNQtyList:"+e.toString());
			sqlitedatabase.close();
			closeDBHelper();
		}
		return cartArrList;   
	}//fn getCartIdNQtyList
	
	public void clearListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DATABASE_TABLE+"'", null);    
			if (c.getCount() > 0){         
				sqlitedatabase.execSQL("DELETE FROM "+DATABASE_TABLE);    
			} 			
			c.close();
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearListTable:"+e.toString());
			sqlitedatabase.close();
			closeDBHelper();
		}   
    }//fn clearListTable
    
    public void updateQtyValue(String idVal, String qtyVal){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			sqlitedatabase.execSQL("update "+DATABASE_TABLE+" set "
							+KEY_QTY+"='"+qtyVal+"' where "+KEY_MATID+"='"+idVal+"'");
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in updateQtyValue:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn updateQtyValue

}//End of class ProductShoppingCartListPersistent