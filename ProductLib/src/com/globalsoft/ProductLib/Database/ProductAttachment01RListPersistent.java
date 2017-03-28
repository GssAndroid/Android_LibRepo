package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductAttachment01RListPersistent {

	public static String PROD_ATTA01R_DATABASE_NAME = "ProductAttachment01RList";
	public static String PROD_ATTA01R_TABLE_NAME = "productattachment01R_lists";
	private Cursor c;	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase sqlitedatabase;
	
	public static String OBJECT_ID_COLUMN_NAME = "OBJECT_ID";
	public static String ATTCHMNT_RFRNC_COLUMN_NAME = "ATTCHMNT_RFRNC";
	
	public ProductAttachment01RListPersistent(Context ctext){
		context = ctext;    
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, PROD_ATTA01R_DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+PROD_ATTA01R_TABLE_NAME);
			onCreate(db);
		}
	}//End of class DatabaseHelper 
	
	public static void setProductAtta01TableContent(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productATTA01RMetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productATTA01RMetaArrayString = new String[0];
			}        	
			ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE =
			        "create table "+ProductAttachment01RListPersistent.PROD_ATTA01R_TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productATTA01RMetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE += ProductDBConstants.productATTA01RMetaArrayString[j]+" text not null, ";
				}else{
					ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE += ProductDBConstants.productATTA01RMetaArrayString[j]+" text not null";
				}
			}        	
			ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE += ");";			
			//SapGenConstants.showLog("PROD_ATTA01R_LIST_TABLE_CREATE : "+ProductDBConstants.PROD_ATTA01R_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setProductAtta01TableContent:"+e.toString());
		}    	
	}//fn setProductAtta01TableContent
	
	public void insertProductAtta01Details(ArrayList productAtta01ListArray){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			for (int j = 0; j < productAtta01ListArray.size(); j++) {
				String[] arry = (String[]) productAtta01ListArray.get(j);
				insertRow(ProductDBConstants.productATTA01RMetaArrayString, arry);		
			} 
			//insertRow(fieldName, values);		    		    
			sqlitedatabase.close();	
			DBHelper.close();		
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertProductAtta01Details:"+e.toString());
		}
	}//fn insertProductAtta01Details
			
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
		return sqlitedatabase.insert(PROD_ATTA01R_TABLE_NAME, null, initialValues);
	}//fn insertRow
	
	public Cursor getAllRows(){        
		return sqlitedatabase.query(PROD_ATTA01R_TABLE_NAME, ProductDBConstants.productATTA01RMetaArrayString, null, null, null, null, null);
	}//fn getAllRows
	
	/*public static ArrayList readProductListDataFromDB(Context ctx){
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
			cursor = sqlitedatabase.rawQuery("select * from "+ PROD_ATTA01R_TABLE_NAME, null);	
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productArrList;
			}
			
			int[] dbIndex = new int[ProductDBConstants.productATTA01RMetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productATTA01RMetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productATTA01RMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productATTA01RMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productATTA01RMetaArrayString[j], dbValues[j]);	
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
*/	
	
	public String getImageUrl(String id){
    	String url = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select "+ATTCHMNT_RFRNC_COLUMN_NAME+" from "+PROD_ATTA01R_TABLE_NAME+" where "+OBJECT_ID_COLUMN_NAME+"='"+id+"'", null);
			if(c != null && c.getCount() > 0){
				if (c.moveToFirst())
				{
					url = c.getString(0);
					SapGenConstants.showLog("url : "+url);
				}
			}
			sqlitedatabase.close();
			c.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getImageUrl:"+e.toString());
		}   
    	return url;
    }//fn getImageUrl
	
	public ArrayList getAllImageUrl(String id){
    	String url = "";
    	Cursor cursor = null;
    	ArrayList imageURLArrList = new ArrayList();
    	try {
    		if(imageURLArrList != null && imageURLArrList.size() > 0){
    			imageURLArrList.clear();
    		}
    		int[] dbIndex = new int[2];
    		String[] dbValues = new String[2];
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			cursor = sqlitedatabase.rawQuery("select "+ATTCHMNT_RFRNC_COLUMN_NAME+" from "+PROD_ATTA01R_TABLE_NAME+" where "+OBJECT_ID_COLUMN_NAME+"='"+id+"'", null);
			if(cursor != null){
				SapGenConstants.showLog("No of UI Labels Records : "+cursor.getCount());
				if(cursor.getCount() == 0){
					return imageURLArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(ATTCHMNT_RFRNC_COLUMN_NAME);			
				cursor.moveToFirst();			
				do{
					dbValues[0] = cursor.getString(dbIndex[0]);	
					url = dbValues[0].toString().trim();
					if(url != null){
						SapGenConstants.showLog("urlValue : "+url);
						imageURLArrList.add(url);
					}
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			}			
			sqlitedatabase.close();
			cursor.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getAllImageUrl:"+e.toString());
		}   
    	return imageURLArrList;
    }//fn getAllImageUrl
	
	public void clearProductAtta01ListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+PROD_ATTA01R_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+PROD_ATTA01R_TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
			DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearProductAtta01ListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearProductAtta01ListTable
	
}