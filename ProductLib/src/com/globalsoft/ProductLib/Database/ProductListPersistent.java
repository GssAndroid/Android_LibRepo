package com.globalsoft.ProductLib.Database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductListPersistent {

	public static String DATABASE_NAME = "ProductList";
	public static String TABLE_NAME = "product_lists";
	private Cursor c;	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase sqlitedatabase;
	
	public static String MAKTX_COLUMN_NAME = "MAKTX";
	public static String MATNR_COLUMN_NAME = "MATNR";
	public static String KBETR_COLUMN_NAME = "KBETR";
	public static String ZZTEXT1_COLUMN_NAME = "ZZTEXT1";
	public static String ZZTEXT2_COLUMN_NAME = "ZZTEXT2";
	public static String ZZTEXT3_COLUMN_NAME = "ZZTEXT3";
	public static String ZZTEXT4_COLUMN_NAME = "ZZTEXT4";
	public static String MATKL_COLUMN_NAME = "MATKL";
	public static String KONWA_COLUMN_NAME = "KONWA";
	public static String KMEIN_COLUMN_NAME = "KMEIN";
	
	public ProductListPersistent(Context ctext){
		context = ctext;    
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(ProductDBConstants.PROD_LIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
			onCreate(db);
		}
	}//End of class DatabaseHelper 
	
	public static void setTableContent(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productMetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productMetaArrayString = new String[0];
			}        	
			ProductDBConstants.PROD_LIST_TABLE_CREATE =
			        "create table "+ProductListPersistent.TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productMetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					ProductDBConstants.PROD_LIST_TABLE_CREATE += ProductDBConstants.productMetaArrayString[j]+" text not null, ";
				}else{
					ProductDBConstants.PROD_LIST_TABLE_CREATE += ProductDBConstants.productMetaArrayString[j]+" text not null";
				}
			}        	
			ProductDBConstants.PROD_LIST_TABLE_CREATE += ");";			
			//SapGenConstants.showLog("PROD_LIST_TABLE_CREATE : "+ProductDBConstants.PROD_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setTableContent:"+e.toString());
		}    	
	}//fn setTableContent
	
	public static void setColumnName(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				ProductDBConstants.productMetaArrayString = new String[metaNamesArray.size()];
			}else{
				ProductDBConstants.productMetaArrayString = new String[0];
			}        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				ProductDBConstants.productMetaArrayString[j] = metaNamesArray.get(j).toString().trim();				
			}        	
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setColumnName:"+e.toString());
		}    	
	}//fn setColumnName
	
	public void insertProductDetails(ArrayList productListArray){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			for (int j = 0; j < productListArray.size(); j++) {
				String[] arry = (String[]) productListArray.get(j);
				insertRow(ProductDBConstants.productMetaArrayString, arry);
			}					    		    
			sqlitedatabase.close();	
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
		return sqlitedatabase.insert(TABLE_NAME, null, initialValues);
	}//fn insertRow
	
	public Cursor getAllRows(){        
		return sqlitedatabase.query(TABLE_NAME, ProductDBConstants.productMetaArrayString, null, null, null, null, null);
	}//fn getAllRows
	
	public static ArrayList readProductListDataFromDB(Context ctx){
		Cursor cursor = null, c = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	try{    		
    		/*c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"'", null);    
			if (c.getCount()>0){*/
	    		if(productArrList != null)
	    			productArrList.clear();
				    		
				DBHelper = new DatabaseHelper(ctx);
				sqlitedatabase = DBHelper.getWritableDatabase();	
				cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME, null);
				//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE KEY_ROWID >= 1 and KEY_ROWID <= 10", null);
				//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE MATNR like '%1400%'", null);
				SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return productArrList;
				}
				SapGenConstants.showLog("productMetaArrayString.length : "+ProductDBConstants.productMetaArrayString.length);
				
				int[] dbIndex = new int[ProductDBConstants.productMetaArrayString.length];
	    		String[] dbValues = new String[ProductDBConstants.productMetaArrayString.length];
				
	    		for (int j = 0; j < ProductDBConstants.productMetaArrayString.length; j++) {
	    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productMetaArrayString[j]);	
	    		}
	    					
	    		cursor.moveToFirst();
	    		HashMap<String, String> stockMap = null;
				do{
	    			stockMap = new HashMap<String, String>();
					for (int j = 0; j < dbIndex.length; j++) {
		    			dbValues[j] = cursor.getString(dbIndex[j]);
		                stockMap.put(ProductDBConstants.productMetaArrayString[j], dbValues[j]);	
					}										
					if(stockMap != null){
						productArrList.add(stockMap);	
					}			
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
			/*}else{
				return productArrList;
			}*/
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
    	}
    	return productArrList;
    }//fn readProductListDataFromDB
	
	public static ArrayList readProductListDataFromDBBySearch(Context ctx, String key){
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
			//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE KEY_ROWID >= 1 and KEY_ROWID <= 10", null);	

			cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE MATNR like '%"+key+"%' and KEY_ROWID >= 1 and KEY_ROWID <= 10", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productArrList;
			}
			SapGenConstants.showLog("productMetaArrayString.length : "+ProductDBConstants.productMetaArrayString.length);
			
			int[] dbIndex = new int[ProductDBConstants.productMetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productMetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductListDataFromDBBySearch : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
    	}
    	return productArrList;
    }//fn readProductListDataFromDBBySearch

	public static ArrayList readProductSrcCatFiltersListDataFromDB(Context ctx, ArrayList selMattVector){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productSrcCatArrList = new ArrayList<HashMap<String, String>>();
    	String selcItem="",delimiter=",",delim1="'";
    	String idVal = null;
    	
    	try{    		    		
    		if(productSrcCatArrList != null)
    			productSrcCatArrList.clear();
    		
    		int sizeArr = selMattVector.size();
    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			int i = 0;
			for(i = 0; i < sizeArr; i++){
				idVal = (String)selMattVector.get(i);
				selcItem += delim1 + idVal + delim1 + delimiter;
			}
			SapGenConstants.showLog("sizeArr: "+sizeArr);
			
			if(i == sizeArr)
				selcItem = selcItem.substring(0, selcItem.length() - 1);//deletes the last character from the string "SelcItem"
			SapGenConstants.showLog("select * from "+ TABLE_NAME+" where "+ProductListPersistent.MATKL_COLUMN_NAME+" in ("+selcItem+")");
			cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME+" where "+ProductListPersistent.MATKL_COLUMN_NAME+" in ("+selcItem+")", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productSrcCatArrList;
			}
			SapGenConstants.showLog("productMetaArrayString.length : "+ProductDBConstants.productMetaArrayString.length);
			
			int[] dbIndex = new int[ProductDBConstants.productMetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productMetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productSrcCatArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductSrcCatFiltersListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
    	}
    	return productSrcCatArrList;
    }//fn readProductSrcCatFiltersListDataFromDB
	
	public static ArrayList readAllFieldDataBySelctdIdFromDB(Context ctx, String idstr){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productFieldsArrList = new ArrayList<HashMap<String, String>>();
    	try{    		    		
    		if(productFieldsArrList != null)
    			productFieldsArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			//cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE KEY_ROWID >= 1 and KEY_ROWID <= 10", null);	

			cursor = sqlitedatabase.rawQuery("select * from "+ TABLE_NAME +" WHERE "+MATNR_COLUMN_NAME+"='"+idstr+"'", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productFieldsArrList;
			}
			SapGenConstants.showLog("productMetaArrayString.length : "+ProductDBConstants.productMetaArrayString.length);
			
			int[] dbIndex = new int[ProductDBConstants.productMetaArrayString.length];
    		String[] dbValues = new String[ProductDBConstants.productMetaArrayString.length];
			
    		for (int j = 0; j < ProductDBConstants.productMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(ProductDBConstants.productMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(ProductDBConstants.productMetaArrayString[j], dbValues[j]);	
	                SapGenConstants.showLog("Value : "+ProductDBConstants.productMetaArrayString[j]+"  "+dbValues[j]);	    			
				}										
				if(stockMap != null){
					productFieldsArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readProductListDataFromDBBySearch : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
    	}
    	return productFieldsArrList;
    }//fn readAllFieldDataBySelctdIdFromDB
	
	public void clearProductListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
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
			sqlitedatabase.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in dropProductListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn dropProductListTable
	
	public boolean checkTable(){
		boolean isExits = false;
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				isExits = true;
			} 						
			c.close();
			sqlitedatabase.close();
			return isExits;
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in checkTable:"+e.toString());
			sqlitedatabase.close();
			return isExits;
		}   
	}//fn checkTable
	
}