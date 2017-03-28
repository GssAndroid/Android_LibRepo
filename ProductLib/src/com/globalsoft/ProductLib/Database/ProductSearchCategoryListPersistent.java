package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductSearchCategoryListPersistent {

	public static String PROD_SRCCAT_DATABASE_NAME = "ProductSrcCatList";
	public static String PROD_SRCCAT_TABLE_NAME = "productSrcCat_lists";
	private Cursor c;	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase sqlitedatabase;
	
	public static String MATKL_COLUMN_NAME = "MATKL";
	public static String WGBEZ_COLUMN_NAME = "WGBEZ";
	
	public ProductSearchCategoryListPersistent(Context ctext){
		context = ctext;    
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, PROD_SRCCAT_DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+PROD_SRCCAT_TABLE_NAME);
			onCreate(db);
		}
	}//End of class DatabaseHelper 
	
	public static void setProductSrcCatTableContent(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productSRCCATMetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productSRCCATMetaArrayString = new String[0];
			}        	
			ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE =
			        "create table "+ProductSearchCategoryListPersistent.PROD_SRCCAT_TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productSRCCATMetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE += ProductDBConstants.productSRCCATMetaArrayString[j]+" text not null, ";
				}else{
					ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE += ProductDBConstants.productSRCCATMetaArrayString[j]+" text not null";
				}
			}        	
			ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE += ");";			
			//SapGenConstants.showLog("PROD_SRCCAT_LIST_TABLE_CREATE : "+ProductDBConstants.PROD_SRCCAT_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setProductSrcCatTableContent:"+e.toString());
		}    	
	}//fn setProductSrcCatTableContent
	
	public static void setColumnName(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productSRCCATMetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productSRCCATMetaArrayString = new String[0];
			}        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productSRCCATMetaArrayString[j] = metaNamesArray.get(j).toString().trim();				
			}        	
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setColumnName:"+e.toString());
		}    	
	}//fn setColumnName
	
	public void insertProductSrcCatDetails(ArrayList searchCatList){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			//SapGenConstants.showLog("fieldName:"+fieldName.length+"  values:"+values.length);
			for (int j = 0; j < searchCatList.size(); j++) {
				String[] arry = (String[]) searchCatList.get(j);
				insertRow(ProductDBConstants.productSRCCATMetaArrayString, arry);		
			}    		    
			sqlitedatabase.close();	
			DBHelper.close();		
		} catch (Exception e) {		    		    
			sqlitedatabase.close();	
			SapGenConstants.showErrorLog("Error in insertProductSrcCatDetails:"+e.toString());
		}
	}//fn insertProductSrcCatDetails
			
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
		return sqlitedatabase.insert(PROD_SRCCAT_TABLE_NAME, null, initialValues);
	}//fn insertRow
	
	public Cursor getAllRows(){        
		return sqlitedatabase.query(PROD_SRCCAT_TABLE_NAME, ProductDBConstants.productSRCCATMetaArrayString, null, null, null, null, null);
	}//fn getAllRows
	
	public static ArrayList readProductSrcCatListDataFromDB(Context ctx){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productSrcCatArrList = new ArrayList<HashMap<String, String>>();
    	try{    		    		
    		if(productSrcCatArrList != null)
    			productSrcCatArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			cursor = sqlitedatabase.rawQuery("select * from "+ PROD_SRCCAT_TABLE_NAME, null);
			//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE KEY_ROWID >= 1 and KEY_ROWID <= 10", null);
			//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE MATNR like '%1400%'", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productSrcCatArrList;
			}
			SapGenConstants.showLog("productMetaArrayString.length : "+ProductDBConstants.productSRCCATMetaArrayString.length);
			
			int[] dbIndex = new int[ProductDBConstants.productSRCCATMetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productSRCCATMetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productSRCCATMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productSRCCATMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productSRCCATMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productSrcCatArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductSrcCatListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
    	}
    	return productSrcCatArrList;
    }//fn readProductSrcCatListDataFromDB
	
	public void clearProductSrcCatConfListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+PROD_SRCCAT_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+PROD_SRCCAT_TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
			DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearProductSrcCatConfListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearProductSrcCatConfListTable
	
	public boolean checkTable(){
		boolean isExits = false;
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+PROD_SRCCAT_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				isExits = true;
			} 						
			c.close();
			sqlitedatabase.close();
			return isExits;
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in checkTable:"+e.toString());
			sqlitedatabase.close();
		} finally{
			return isExits;
		}  
	}//fn checkTable
}