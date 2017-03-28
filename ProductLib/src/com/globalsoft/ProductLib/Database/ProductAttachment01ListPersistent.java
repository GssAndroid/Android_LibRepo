package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductAttachment01ListPersistent {

	public static String PROD_ATTA01_DATABASE_NAME = "ProductAttachment01List";
	public static String PROD_ATTA01_TABLE_NAME = "productattachment01_lists";
	private Cursor c;	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase sqlitedatabase;
	
	/*public static String MAKTX_COLUMN_NAME = "MAKTX";
	public static String MATNR_COLUMN_NAME = "MATNR";
	public static String KBETR_COLUMN_NAME = "KBETR";
	public static String ZZTEXT1_COLUMN_NAME = "ZZTEXT1";*/
	
	public ProductAttachment01ListPersistent(Context ctext){
		context = ctext;    
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, PROD_ATTA01_DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+PROD_ATTA01_TABLE_NAME);
			onCreate(db);
		}
	}//End of class DatabaseHelper 
	
	public static void setTableContent(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productATTA01MetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productATTA01MetaArrayString = new String[0];
			}        	
			ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE =
			        "create table "+ProductAttachment01ListPersistent.PROD_ATTA01_TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productATTA01MetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE += ProductDBConstants.productATTA01MetaArrayString[j]+" text not null, ";
				}else{
					ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE += ProductDBConstants.productATTA01MetaArrayString[j]+" text not null";
				}
			}        	
			ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE += ");";			
			//SapGenConstants.showLog("PROD_ATTA01_LIST_TABLE_CREATE : "+ProductDBConstants.PROD_ATTA01_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setTableContent:"+e.toString());
		}    	
	}//fn setTableContent
	
	public void insertProductDetails(String[] fieldName, String[] values){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			insertRow(fieldName, values);		    		    
			sqlitedatabase.close();	
			DBHelper.close();		
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertProductDetails:"+e.toString());
		}
	}//fn insertProductDetails
			
	public void closeDBHelper(){
		try{
			if(DBHelper != null)
				DBHelper.close();
			if (c != null) {
			    c.close();
			    c = null;
			}
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in DataPersistent closeDBHelper:"+e.toString());
		}
	}//fn closeDBHelper     
	
	public long insertRow(String[] fieldName, String[] values){	
		ContentValues initialValues = new ContentValues();
		for(int i=0; i<values.length; i++){
			initialValues.put(fieldName[i].toString().trim(), values[i].toString().trim());			
		}       
		return sqlitedatabase.insert(PROD_ATTA01_TABLE_NAME, null, initialValues);
	}//fn insertRow
	
	public Cursor getAllRows(){        
		return sqlitedatabase.query(PROD_ATTA01_TABLE_NAME, ProductDBConstants.productATTA01MetaArrayString, null, null, null, null, null);
	}//fn getAllRows
	
	public static ArrayList readProductListDataFromDB(Context ctx){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	try{    		    		
    		if(productArrList != null)
    			productArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			cursor = sqlitedatabase.rawQuery("select * from "+ PROD_ATTA01_TABLE_NAME, null);	
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productArrList;
			}
			
			int[] dbIndex = new int[ProductDBConstants.productATTA01MetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productATTA01MetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productATTA01MetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productATTA01MetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productATTA01MetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductListDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
			DBHelper.close();
    	}
    	return productArrList;
    }//fn readProductListDataFromDB
	
	public void clearProductListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+PROD_ATTA01_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+PROD_ATTA01_TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
			DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearSearchedListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearSearchedListTable

	public void dropProductListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			DBHelper.onUpgrade(sqlitedatabase, 1, 1);
			/*c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+PROD_ATTA01_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				SapGenConstants.showLog("1");
				sqlitedatabase.execSQL("DROP TABLE IF EXISTS "+PROD_ATTA01_TABLE_NAME);
			} else{
				SapGenConstants.showLog("2");
			}
			c.close();*/
			sqlitedatabase.close();
			DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in dropProductListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn dropProductListTable
	
}