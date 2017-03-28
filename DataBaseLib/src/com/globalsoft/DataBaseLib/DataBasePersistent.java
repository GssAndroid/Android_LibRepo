package com.globalsoft.DataBaseLib;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class DataBasePersistent {
	private Cursor c;	
	private final Context context;
	private static DatabaseHelper DBHelper;
	private static SQLiteDatabase sqlitedatabase;
	static String checlistcolumnName = "";
	static boolean internetAccess =false;
	
	
	public DataBasePersistent(Context ctext, String tableName){
		context = ctext;   
		DBConstants.DB_TABLE_NAME= " ";
		DBConstants.DB_TABLE_NAME = tableName;		
		//DBConstants.PRODUCT_LIST_DB_TABLE_NAME = tableName;
	}	
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context)
		{
			super(context, DBConstants.DB_DATABASE_NAME, null, 1);
		}
	
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DBConstants.DB_LIST_TABLE_CREATE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{			
			db.execSQL("DROP TABLE IF EXISTS "+DBConstants.DB_TABLE_NAME);					    
		}
	}//End of class DatabaseHelper 
	
	public static void setTableContent(ArrayList metaNamesArray){
    	try {    		
			if(metaNamesArray.size() > 0){
				DBConstants.DBMetaArrayString = new String[metaNamesArray.size()];
			}else{
				DBConstants.DBMetaArrayString = new String[0];
			}        	
			DBConstants.DB_LIST_TABLE_CREATE =
			        "create table "+DBConstants.DB_TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				DBConstants.DBMetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					DBConstants.DB_LIST_TABLE_CREATE += DBConstants.DBMetaArrayString[j]+" text not null, ";
				}else{
					DBConstants.DB_LIST_TABLE_CREATE += DBConstants.DBMetaArrayString[j]+" text not null";
				}
			}        	
			DBConstants.DB_LIST_TABLE_CREATE += ");";			
			//SapGenConstants.showLog("DB_LIST_TABLE_CREATE : "+DBConstants.DB_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setTableContent:"+e.toString());
		}    	
	}//fn setTableContent
	
	public static void setColumnName(ArrayList metaNamesArray){
    	try {
			if(metaNamesArray.size() > 0){
				DBConstants.DBMetaArrayString = new String[metaNamesArray.size()];
			}else{
				DBConstants.DBMetaArrayString = new String[0];
			}        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				DBConstants.DBMetaArrayString[j] = metaNamesArray.get(j).toString().trim();				
			}        	
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setColumnName:"+e.toString());
		}    	
	}//fn setColumnName
	
	public void insertDetails(ArrayList productListArray){
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			for (int j = 0; j < productListArray.size(); j++) {
				String[] arry = (String[]) productListArray.get(j);
				insertRow(DBConstants.DBMetaArrayString, arry);
			}					    
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertDetails:"+e.toString());
		}
    	finally{	
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
			closeDBHelper();
    	}
	}//fn insertDetails
			
	public static void setProductUIConfTableContent(ArrayList metaNamesArray){
    	try {  		
			if(metaNamesArray.size() > 0){
				DBConstants.DBMetaArrayString = new String[metaNamesArray.size()];
			}else{
				DBConstants.DBMetaArrayString = new String[0];
			}        	
			DBConstants.DB_LIST_TABLE_CREATE =
			        "create table "+DBConstants.DB_TABLE_NAME+ " ( " 
			         + "KEY_ROWID integer primary key autoincrement, ";        	
			for (int j = 0; j < metaNamesArray.size(); j++) {
				DBConstants.DBMetaArrayString[j] = metaNamesArray.get(j).toString().trim();
				if(j != metaNamesArray.size()-1){
					DBConstants.DB_LIST_TABLE_CREATE += DBConstants.DBMetaArrayString[j]+" text not null, ";
				}else{
					DBConstants.DB_LIST_TABLE_CREATE += DBConstants.DBMetaArrayString[j]+" text not null";
				}
			}        	
			DBConstants.DB_LIST_TABLE_CREATE += ");";			
			SapGenConstants.showLog("DB_LIST_TABLE_CREATE : "+DBConstants.DB_LIST_TABLE_CREATE);
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in setProductUIConfTableContent:"+e.toString());
		}    	
	}//fn setProductUIConfTableContent
	
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
		return sqlitedatabase.insert(DBConstants.DB_TABLE_NAME, null, initialValues);
	}//fn insertRow
		
	///UI RELATED METHOD
	public void insertProductUIConfDetails(ArrayList uiConfList){
		try {			
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();
			for (int j = 0; j < uiConfList.size(); j++) {
				String[] arry = (String[]) uiConfList.get(j);
				insertRow(DBConstants.DBMetaArrayString, arry);		
			}  
			//insertRow(fieldName, values);		    		    
			sqlitedatabase.close();	
			if(DBHelper != null)
				DBHelper.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in insertProductUIConfDetails:"+e.toString());
		}
	}//fn insertProductUIConfDetails
	
	public Cursor getAllRows(){        
		return sqlitedatabase.query(DBConstants.DB_TABLE_NAME, DBConstants.DBMetaArrayString, null, null, null, null, null);
	}//fn getAllRows
	
	public String getTitle(String contxt2, String contxt4){
    	String title = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select "+DBConstants.VALUE_COLUMN_NAME+" from "+DBConstants.DB_TABLE_NAME+" where "+DBConstants.CNTXT2_COLUMN_NAME+"='"+contxt2+"' and "+DBConstants.CNTXT4_COLUMN_NAME+"='"+contxt4+"'", null);
			//SapGenConstants.showLog("select "+VALUE_COLUMN_NAME+" from "+DB_TABLE_NAME+" where "+CNTXT2_COLUMN_NAME+"='"+contxt2+"' and "+CNTXT3_COLUMN_NAME+"='"+contxt3+"' and "+CNTXT4_COLUMN_NAME+"='"+contxt4+"'");
			//SapGenConstants.showLog("c.getCount() : "+c.getCount());
			if(c != null && c.getCount() > 0){
				if (c.moveToFirst())
				{
					title = c.getString(0);
					//SapGenConstants.showLog("title : "+title);
				}
			}else{
				title = "";
			}
			internetAccess = SapGenConstants.checkConnectivityAvailable(context);
			if(!internetAccess){
				title += SapGenConstants.title_offline;
			}
			sqlitedatabase.close();
			c.close();
			if(DBHelper != null)
				DBHelper.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getTitle:"+e.toString());
		}   
    	return title;
    }//fn getTitle
	
	public ArrayList readListDataFromDB(Context ctx){
		Cursor cursor = null, c = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	try{    		
    		if(productArrList != null)
    			productArrList.clear();
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME, null);
			SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());			

			if(cursor.getCount() == 0){
				return productArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);

			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];

    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}

    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
			closeDBHelper();
    	}
    	return productArrList;
    }//fn readListDataFromDB
	
	public ArrayList readListDataOrderByFromDB(Context ctx, String column, boolean flag){
		Cursor cursor = null, c = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	try{    		
    		if(productArrList != null)
    			productArrList.clear();
    		
    		if(flag){
				orderBy =  " ORDER BY "+column+" DESC";
			}else{
				orderBy =  " ORDER BY "+column+" ASC";
			}
    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME + orderBy, null);
			SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME + orderBy);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());

			if(cursor.getCount() == 0){
				return productArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);

			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];

    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}

    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readListDataOrderByFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			closeDBHelper();
    	}
    	return productArrList;
    }//fn readListDataOrderByFromDB
	
	//newly added
	public void setTableName_ColumnName(Context ctx, String tableName,ArrayList metanamesArray){
		String columnLists = "";
		DBHelper = new DatabaseHelper(ctx);
		sqlitedatabase = DBHelper.getWritableDatabase();
		ArrayList metaListArray = new ArrayList();	
		try {
			if(tableName != null && tableName.length() > 0){
				DBConstants.DB_TABLE_NAME = tableName;
			}				
						
			if(metanamesArray != null && metanamesArray.size() > 0){
				SapGenConstants.showLog("columnLists: "+metanamesArray);
				
				if(metanamesArray != null && metanamesArray.size() > 0){
					for(int i=0; i<metanamesArray.size(); i++){
						if( i == (metanamesArray.size() - 1)){
							columnLists += metanamesArray.get(i).toString().trim();
						}else{
							columnLists += metanamesArray.get(i).toString().trim()+":";
						}
					}
				}
				if(metaListArray != null && metaListArray.size() > 0){
					metaListArray.clear();
				}				
				String[] separated = columnLists.split(":");
				if(separated != null && separated.length > 0){
					for(int i=0; i<separated.length; i++){
						SapGenConstants.showLog("  "+separated[i]);
						metaListArray.add(separated[i].toString().trim());
					}
				}
				DataBasePersistent.setColumnName(metaListArray);	
			}else{
				SapGenConstants.showLog("No Data Available for product list meta data!");
			}
			sqlitedatabase.close();	
			closeDBHelper();
		} catch (Exception sgghr) {
			SapGenConstants.showErrorLog("On setTableName_ColumnName : "+sgghr.toString());
		}
	}//fn setTableName_ColumnName
	
	//newly added
	public void getColumns(){
		ArrayList colmsArrList = new ArrayList();
		DBHelper = new DatabaseHelper(context);
		sqlitedatabase = DBHelper.getReadableDatabase();			
		Cursor cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, null, null, null, null, null);
		SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME);
		SapGenConstants.showLog("No of columns Category Records : "+cursor.getCount());
		String[] columnNames = cursor.getColumnNames();
		DBConstants.DBMetaArrayString = columnNames;
		 SapGenConstants.showLog("columnNames size:"+columnNames.length);	
	}//gelColumns
	
	//DYNAMIC UI RELATED METHODS
	public static ArrayList readAllTabletLablesFromDB(Context ctx, String contxt2, String contxt3){
    	Cursor cursor = null, cursor2 = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String colValue = "", valueStr = "";
    	String labelValue = "";
    	ArrayList lablesArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[3];
    		String[] dbValues = new String[3];
    		int[] dbIndex1 = new int[3];
    		String[] dbValues1 = new String[3];
    		
    		if(lablesArrList != null)
    			lablesArrList.clear();
			
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI Labels Records : "+cursor.getCount());
			if(cursor.getCount() == 0){
				return lablesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);	
			dbIndex[1] = cursor.getColumnIndex(DBConstants.DATATYPE_COLUMN_NAME);
			dbIndex[2] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);
			cursor.moveToFirst();			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);		
				dbValues[1] = cursor.getString(dbIndex[1]);	
				dbValues[2] = cursor.getString(dbIndex[2]);
				colValue = dbValues[0];			
				valueStr = dbValues[2];
				selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.NAME_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ?";
				selectionParams = new String[]{DBConstants.CNTXT4_LABE_TAG, colValue, contxt2};
				cursor2 = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
						null, orderBy);
				
				labelValue = "";
				if(cursor2.getCount() == 0){
					labelValue = "";
				}else{					
					dbIndex1[0] = cursor2.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);			
					cursor2.moveToFirst();
					dbValues1[0] = cursor2.getString(dbIndex1[0]);								
					if(dbValues1[0] != null)
						labelValue = dbValues1[0].toString().trim();
				}
				
				String strVal = "";
				if(colValue.indexOf("ZGS") >= 0){
					String[] separated = colValue.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = colValue;
				}
				
				String typeVal = dbValues[1].toString().trim();
				if(typeVal != null && typeVal.length() > 0){
					strVal += "::" + typeVal;
				}else{
					strVal += "::" + "";
				}
				
				if(valueStr != null && valueStr.length() > 0){
					strVal += "::" + valueStr;
				}else{
					strVal += "::" + "";
				}				
				
				labelValue += "::"+strVal;
				SapGenConstants.showLog("labelValue : "+labelValue);
				if(labelValue != null)
					lablesArrList.add(labelValue);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllTabletLablesFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return lablesArrList;
    }//fn readAllTabletLablesFromDB
	
	public static ArrayList readAllPossibleTabletLablesFromDB(Context ctx, String contxt2, String contxt3){
    	Cursor cursor = null, cursor2 = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String colValue = "";
    	String labelValue = "";
    	ArrayList lablesArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[2];
    		String[] dbValues = new String[2];
    		int[] dbIndex1 = new int[2];
    		String[] dbValues1 = new String[2];
    		
    		if(lablesArrList != null)
    			lablesArrList.clear();
			
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI Labels Records : "+cursor.getCount());
			if(cursor.getCount() == 0){
				return lablesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);			
			cursor.moveToFirst();			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				colValue = dbValues[0];			
				selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.NAME_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ?";
				selectionParams = new String[]{DBConstants.CNTXT4_LABE_TAG, colValue, contxt2};
				cursor2 = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
						null, orderBy);
				
				labelValue = "";
				if(cursor2.getCount() == 0){
					labelValue = "";
				}else{					
					dbIndex1[0] = cursor2.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);			
					cursor2.moveToFirst();
					dbValues1[0] = cursor2.getString(dbIndex1[0]);								
					if(dbValues1[0] != null)
						labelValue = dbValues1[0].toString().trim();
				}

				SapGenConstants.showLog("labelValue : "+labelValue);
				if(labelValue != null)
					lablesArrList.add(labelValue);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllPossibleTabletLablesFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return lablesArrList;
    }//fn readAllPossibleTabletLablesFromDB
	
	public static HashMap readAllLablesFromDB(Context ctx, String contxt2, String contxt3){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String colValue = "";
    	String labelValue = "";
    	HashMap<String, String> labelMap = new HashMap<String, String>();
		int[] dbIndex = new int[2];
		String[] dbValues = new String[2];
		
    	try{    		    		
    		if(labelMap != null)
    			labelMap.clear();
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = "("+DBConstants.CNTXT4_COLUMN_NAME + " = ? or "+DBConstants.CNTXT4_COLUMN_NAME + " = ? ) and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?";
			selectionParams = new String[]{DBConstants.CNTXT4_LABE_TAG, DBConstants.CNTXT4_HINT_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI labels Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return labelMap;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);		
			dbIndex[1] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);		
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);	
				SapGenConstants.showLog(dbValues[0]+"  "+dbValues[1]);
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("ZGS") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				
				labelMap.put(strVal, dbValues[1]);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllLablesFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return labelMap;		
    }//fn readAllLablesFromDB
	
	public static HashMap readAllPHLablesFromDB(Context ctx, String contxt2){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String colValue = "";
    	String labelValue = "";
    	HashMap<String, String> labelMap = new HashMap<String, String>();
		int[] dbIndex = new int[2];
		String[] dbValues = new String[2];
		
    	try{    		    		
    		if(labelMap != null)
    			labelMap.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ?";
			selectionParams = new String[]{DBConstants.CNTXT4_LABE_TAG, contxt2};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
						
			if(cursor.getCount() == 0){
				return labelMap;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);		
			dbIndex[1] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);		
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);	
				SapGenConstants.showLog(dbValues[0]+"  "+dbValues[1]);
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("ZGS") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				
				labelMap.put(strVal, dbValues[1]);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllPHLablesFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return labelMap;		
    }//fn readAllPHLablesFromDB
	
	public static ArrayList readAllValuesFromDB(Context ctx, String contxt2, String contxt3){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		int[] dbIndex = new int[3];
		String[] dbValues = new String[3];
		
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.VALUE_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_DISPLAY_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI values Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);			
			dbIndex[1] = cursor.getColumnIndex(DBConstants.DATATYPE_COLUMN_NAME);	
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);	
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("ZGS") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				
				String typeVal = dbValues[1].toString().trim();
				if(typeVal != null && typeVal.length() > 0){
					strVal += "::" + typeVal;
				}else{
					strVal += "::" + "";
				}
				
				ValuesArrList.add(strVal.toString().trim());
				SapGenConstants.showLog(strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllValuesFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}
		return ValuesArrList;		
	}//fn readAllValuesFromDB
	
	public static ArrayList readAllValuesOrderFromDB(Context ctx, String contxt2, String contxt3){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		ArrayList ValuesOrdArrList = new ArrayList();
		ArrayList ValuesOrdArrNewList;
		int[] dbIndex = new int[4];
		String[] dbValues = new String[4];
		boolean addFlag = false;	
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			if(ValuesOrdArrList != null)
				ValuesOrdArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			//selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.VALUE_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			//selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_DISPLAY_TAG, contxt2, contxt3};
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI values Records :"+contxt2+" "+contxt3+" "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);		
			dbIndex[1] = cursor.getColumnIndex(DBConstants.SEQNR_COLUMN_NAME);	
			dbIndex[2] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);	
			dbIndex[3] = cursor.getColumnIndex(DBConstants.TRGTNAME_COLUMN_NAME);
			
			cursor.moveToFirst();
			int init = 100, i, j, ii, jj;
			i = init;
			j = init + 100;
			int flag = 0;
			do{
				int add = 1;
				ValuesOrdArrNewList = new ArrayList();
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);	
				dbValues[2] = cursor.getString(dbIndex[2]);	
				dbValues[3] = cursor.getString(dbIndex[3]);	
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("ZGS") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}

				String fieldVal = dbValues[2].toString().trim();
				if(fieldVal != null && fieldVal.length() > 0){
					strVal += "::" + fieldVal;
				}else{
					strVal += "::" + "";
				}
				
				String trgValStr = dbValues[3].toString().trim();
				String trgVal = trgValStr.replaceAll("[\\#\\@\\(\\)]","");
				if(trgVal != null && trgVal.length() > 0){
					if(trgVal.indexOf("ZGS") >= 0){
						String[] separated = trgVal.split("-");
						if(separated != null && separated.length > 0){
							trgVal = separated[1];
						}
					}else{
						trgVal = nameVal;
					}

					if(trgValStr.indexOf("(") >= 0){
						strVal += ":: ("+trgVal+")";
					}else{
						strVal += "::" + trgVal;
					}
				}else{
					strVal += "::" + "";
				}
				
				i = Integer.parseInt(dbValues[1].toString().trim());				
				ii = i;
				int length = (int)(Math.log10(i)+1);
				SapGenConstants.showLog("i leng: 3 - 1 times x 10"+length);						
				jj = firstDigit(i);
				if(length > 0){
					length = length - 1;				
					for(int z = 0; z < length; z++){
						jj = jj * 10;
						add = add * 10;
					}
				}
				int btw = jj + add;
				SapGenConstants.showLog("ii: "+i+" jj:  "+btw+" length:  "+length);
				if(ii < btw){				
					if(flag == btw){
						if(!ValuesOrdArrList.contains(strVal.toString().trim())){
							ValuesOrdArrList.add(strVal.toString().trim());
						}
					}else{
						if(ValuesOrdArrList != null && ValuesOrdArrList.size() > 0){
							ValuesOrdArrNewList = (ArrayList)ValuesOrdArrList.clone();
							ValuesArrList.add(ValuesOrdArrNewList);
							ValuesOrdArrList.clear();
							if(!ValuesOrdArrList.contains(strVal.toString().trim())){
								ValuesOrdArrList.add(strVal.toString().trim());
							}
						}else{
							if(!ValuesOrdArrList.contains(strVal.toString().trim())){
								ValuesOrdArrList.add(strVal.toString().trim());
							}
						}
					}
					flag = btw;
				}			
				SapGenConstants.showLog("strVal: "+strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
			SapGenConstants.showLog("addFlag: "+addFlag);
			if(ValuesOrdArrList != null && ValuesOrdArrList.size() > 0){
				ValuesOrdArrNewList = (ArrayList)ValuesOrdArrList.clone();
				ValuesArrList.add(ValuesOrdArrNewList);
			}
			ValuesOrdArrList.clear();	
			//ValuesOrdArrNewList.clear();
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllValuesOrderFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}			
		checkvalue(ValuesArrList);	
		return ValuesArrList;		
	}//fn readAllValuesOrderFromDB
	
	/*public static ArrayList readAllValuesOrderSubViewFromDB(Context ctx, String contxt2, String contxt3, String contxt4){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		ArrayList ValuesOrdArrList = new ArrayList();
		ArrayList ValuesOrdArrNewList;
		int[] dbIndex = new int[4];
		String[] dbValues = new String[4];
		boolean addFlag = false;
		//String[] EMPTY_ARRAY = new String[0];		
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			if(ValuesOrdArrList != null)
				ValuesOrdArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			//selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.VALUE_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			//selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_DISPLAY_TAG, contxt2, contxt3};
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{contxt4, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI values Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);		
			dbIndex[1] = cursor.getColumnIndex(DBConstants.SEQNR_COLUMN_NAME);	
			dbIndex[2] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);	
			dbIndex[3] = cursor.getColumnIndex(DBConstants.TRGTNAME_COLUMN_NAME);
			
			cursor.moveToFirst();
			int init = 100, i, j, ii, jj;
			i = init;
			j = init + 100;
			int flag = 0;
			do{
				int add = 1;
				ValuesOrdArrNewList = new ArrayList();
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);	
				dbValues[2] = cursor.getString(dbIndex[2]);	
				dbValues[3] = cursor.getString(dbIndex[3]);	
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				strVal = nameVal;
				
				i = Integer.parseInt(dbValues[1].toString().trim());				
				ii = i;
				int length = (int)(Math.log10(i)+1);
				SapGenConstants.showLog("i leng: 3 - 1 times x 10"+length);						
				jj = firstDigit(i);
				if(length > 0){
					length = length - 1;				
					for(int z = 0; z < length; z++){
						jj = jj * 10;
						add = add * 10;
					}
				}
				int btw = jj + add;
				SapGenConstants.showLog("ii: "+i+" jj:  "+btw+" length:  "+length);
				if(ii < btw){				
					if(flag == btw){
						if(!ValuesOrdArrList.contains(strVal.toString().trim())){
							ValuesOrdArrList.add(strVal.toString().trim());
						}
					}else{
						if(ValuesOrdArrList != null && ValuesOrdArrList.size() > 0){
							ValuesOrdArrNewList = (ArrayList)ValuesOrdArrList.clone();
							ValuesArrList.add(ValuesOrdArrNewList);
							ValuesOrdArrList.clear();
							if(!ValuesOrdArrList.contains(strVal.toString().trim())){
								ValuesOrdArrList.add(strVal.toString().trim());
							}
						}else{
							if(!ValuesOrdArrList.contains(strVal.toString().trim())){
								ValuesOrdArrList.add(strVal.toString().trim());
							}
						}
					}
					flag = btw;
				}			
				SapGenConstants.showLog("strVal: "+strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
			SapGenConstants.showLog("addFlag: "+addFlag);
			SapGenConstants.showLog("ValuesOrdArrList.size(): "+ValuesOrdArrList.size());
			if(ValuesOrdArrList != null && ValuesOrdArrList.size() > 0){
				ValuesOrdArrNewList = (ArrayList)ValuesOrdArrList.clone();
				ValuesArrList.add(ValuesOrdArrNewList);
			}
			ValuesOrdArrList.clear();	
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllValuesOrderSubViewFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}			
		checkvalue(ValuesArrList);	
		return ValuesArrList;		
	}//fn readAllValuesOrderSubViewFromDB
*/		
	private static void checkvalue(ArrayList ValuesArrList){
		if(ValuesArrList != null && ValuesArrList.size() > 0){
			SapGenConstants.showLog("valArr : "+ValuesArrList.size());
			for(int i1 = 0; i1 < ValuesArrList.size(); i1++){
				ArrayList list = (ArrayList) ValuesArrList.get(i1);
				if(list != null){
					SapGenConstants.showLog("list : "+list.size());
					if(list.size() == 1){
						String name = list.get(0).toString().trim();	
						SapGenConstants.showLog("name : "+name);	
					}else if (list.size() > 1){	
						for(int l = 0; l < list.size(); l++){
							String name = list.get(l).toString().trim();
							SapGenConstants.showLog("name append : "+name);
						}				
					}
				}
			}
		}
	}
		
	/*public static ArrayList readAllValuesOrderByFromDB(Context ctx, String contxt2, String contxt3, String column, boolean flag){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		int[] dbIndex = new int[2];
		String[] dbValues = new String[2];
		
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			if(flag){
				orderBy =  column+" DESC";
			}else{
				orderBy =  column+" ASC";
			}
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.VALUE_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_DISPLAY_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI values Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);			
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("-") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				
				ValuesArrList.add(strVal.toString().trim());
				SapGenConstants.showLog(strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllValuesOrderByFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}
		return ValuesArrList;		
	}//fn readAllValuesOrderByFromDB
*/	
	public static ArrayList readAllPossibleValuesFromDB(Context ctx, String contxt2, String contxt3){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		int[] dbIndex = new int[3];
		String[] dbValues = new String[3];
		
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ? and "+DBConstants.CNTXT3_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, contxt2, contxt3};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of UI values Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);		
			dbIndex[1] = cursor.getColumnIndex(DBConstants.VALUE_COLUMN_NAME);		
			dbIndex[2] = cursor.getColumnIndex(DBConstants.DATATYPE_COLUMN_NAME);			
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("-") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				strVal += "::"+dbValues[1];
				
				
				String typeVal = dbValues[2].toString().trim();
				if(typeVal != null && typeVal.length() > 0){
					strVal += "::" + typeVal;
				}else{
					strVal += "::" + "";
				}				
				
				ValuesArrList.add(strVal.toString().trim());
				SapGenConstants.showLog(strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllPossibleValuesFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}
		return ValuesArrList;		
	}//fn readAllPossibleValuesFromDB
	
	public static ArrayList readAllPHValuesFromDB(Context ctx, String contxt2){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String colValue = "";
		String labelValue = "";
		//HashMap<String, String> labelMap = new HashMap<String, String>();
		ArrayList ValuesArrList = new ArrayList();
		int[] dbIndex = new int[2];
		String[] dbValues = new String[2];
		
		try{    		    		
			if(ValuesArrList != null)
				ValuesArrList.clear();
			    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			selection = DBConstants.CNTXT4_COLUMN_NAME + " = ? and "+DBConstants.VALUE_COLUMN_NAME + " = ? and "+DBConstants.CNTXT2_COLUMN_NAME + " = ?"; 
			selectionParams = new String[]{DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_DISPLAY_TAG, contxt2};
			cursor = sqlitedatabase.query(DBConstants.DB_TABLE_NAME, null, selection, selectionParams, null,
					null, orderBy);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return ValuesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(DBConstants.NAME_COLUMN_NAME);			
			cursor.moveToFirst();
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				
				String nameVal = dbValues[0].toString().trim();
				String strVal = "";
				if(nameVal.indexOf("-") >= 0){
					String[] separated = nameVal.split("-");
					if(separated != null && separated.length > 0){
						strVal = separated[1];
					}
				}else{
					strVal = nameVal;
				}
				
				ValuesArrList.add(strVal.toString().trim());
				SapGenConstants.showLog(strVal.toString().trim());
				cursor.moveToNext();
			}while(!cursor.isAfterLast());				
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in readAllPHValuesFromDB : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();	
			if(DBHelper != null)
				DBHelper.close();
		}
		return ValuesArrList;		
	}//fn readAllPHValuesFromDB
	
	/*public void clearProductUIConfListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DBConstants.DB_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+DBConstants.DB_TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearProductUIConfListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearProductUIConfListTable
*/	
	private static int firstDigit(int x) {
	    while (x > 9) {
	        x /= 10;
	    }
	    return x;
	}//
	
	public static ArrayList readSrcCatFiltersListDataFromDB(Context ctx, ArrayList selMattVector, String srcColumnName){
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
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME+" where "+srcColumnName+" in ("+selcItem+")", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productSrcCatArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);
			
			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];
			
    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productSrcCatArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readSrcCatFiltersListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return productSrcCatArrList;
    }//fn readSrcCatFiltersListDataFromDB
	
	public static ArrayList readAllFieldDataBySelctdIdFromDB(Context ctx, String idstr, String srcColumnName){
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
			SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME +" WHERE "+srcColumnName+"='"+idstr+"'");
			
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME +" WHERE "+srcColumnName+"='"+idstr+"'", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productFieldsArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);
			
			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];
			
    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
	                SapGenConstants.showLog("Value : "+DBConstants.DBMetaArrayString[j]+"  "+dbValues[j]);	    			
				}										
				if(stockMap != null){
					productFieldsArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllFieldDataBySelctdIdFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return productFieldsArrList;
    }//fn readAllFieldDataBySelctdIdFromDB
	
	public void clearListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DBConstants.DB_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+DBConstants.DB_TABLE_NAME);    
			} 			
			c.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearListTable:"+e.toString());
		}   
    	finally{	
			if(DBHelper != null)
				DBHelper.close();
			sqlitedatabase.close();
			closeDBHelper();
    	}
    }//fn clearListTable

	/*public void dropListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			DBHelper.onUpgrade(sqlitedatabase, 1, 1);
			sqlitedatabase.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in dropListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn dropListTable
*/	
	public boolean checkTable(){
		boolean isExits = false;
		try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DBConstants.DB_TABLE_NAME+"'", null);    
			SapGenConstants.showLog("cursor count "+c.getCount());
			if (c.getCount() > 0){         
				isExits = true;
			} 					
			SapGenConstants.showLog("Table Exits"+isExits);
			c.close();
			sqlitedatabase.close();
    		if(DBHelper != null){
    			DBHelper.close();
    		}
			return isExits;
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in checkTable:"+e.toString());
			sqlitedatabase.close();
    		if(DBHelper != null){
    			DBHelper.close();
    		}
			return isExits;
		}   
	}//fn checkTable	
	
	public String getImageUrl(String id){
    	String url = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select "+DBConstants.ATTA01R_ATTCHMNT_RFRNC_COLUMN_NAME+" from "+DBConstants.DB_TABLE_NAME+" where "+DBConstants.ATTA01R_OBJECT_ID_COLUMN_NAME+"='"+id+"'", null);
			if(c != null && c.getCount() > 0){
				if (c.moveToFirst())
				{
					url = c.getString(0);
					SapGenConstants.showLog("url : "+url);
				}
			}
			sqlitedatabase.close();
			c.close();
			if(DBHelper != null)
				DBHelper.close();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getImageUrl:"+e.toString());
		}     
    	finally{	
			sqlitedatabase.close();
			closeDBHelper();
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
			cursor = sqlitedatabase.rawQuery("select "+DBConstants.ATTA01R_ATTCHMNT_RFRNC_COLUMN_NAME+" from "+DBConstants.DB_TABLE_NAME+" where "+DBConstants.ATTA01R_OBJECT_ID_COLUMN_NAME+"='"+id+"'", null);
			if(cursor != null){
				SapGenConstants.showLog("No of UI Labels Records : "+cursor.getCount());
				if(cursor.getCount() == 0){
					return imageURLArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(DBConstants.ATTA01R_ATTCHMNT_RFRNC_COLUMN_NAME);			
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
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getAllImageUrl:"+e.toString());
		}   
    	finally{		
			if(DBHelper != null)
				DBHelper.close();
			sqlitedatabase.close();
			cursor.close();
			closeDBHelper();
    	}
    	return imageURLArrList;
    }//fn getAllImageUrl

	public static ArrayList readDataSelectedIdListDataFromDB(Context ctx, ArrayList selMattVector, String srcColumnName){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	String selcItem="",delimiter=",",delim1="'";
    	String idVal = null;    	
    	try{    		    		
    		if(productArrList != null)
    			productArrList.clear();
    		
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
			SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME+" where "+srcColumnName+" in ("+selcItem+")");
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME+" where "+srcColumnName+" in ("+selcItem+")", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);
			
			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];
			
    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readDataSelectedIdListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	}
    	return productArrList;
    }//fn readDataSelectedIdListDataFromDB

	public ArrayList readDataSelectedStrListDataFromDB(Context ctx, String str, String srcColumnName){
		Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	ArrayList<HashMap<String, String>> productArrList = new ArrayList<HashMap<String, String>>();
    	String selcItem="",delimiter=",",delim1="'";
    	String idVal = null;
    	
    	try{    		    		
    		if(productArrList != null)
    			productArrList.clear();
    		    		
			DBHelper = new DatabaseHelper(ctx);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			
			SapGenConstants.showLog("select * from "+ DBConstants.DB_TABLE_NAME+" where "+srcColumnName+" LIKE '%"+str+"%'");
			cursor = sqlitedatabase.rawQuery("select * from "+ DBConstants.DB_TABLE_NAME+" where "+srcColumnName+" LIKE '%"+str+"%'", null);
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return productArrList;
			}
			SapGenConstants.showLog("DBMetaArrayString.length : "+DBConstants.DBMetaArrayString.length);
			
			int[] dbIndex = new int[DBConstants.DBMetaArrayString.length];
    		String[] dbValues = new String[DBConstants.DBMetaArrayString.length];
			
    		for (int j = 0; j < DBConstants.DBMetaArrayString.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(DBConstants.DBMetaArrayString[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(DBConstants.DBMetaArrayString[j], dbValues[j]);	
				}										
				if(stockMap != null){
					productArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readDataSelectedStrListDataFromDB : "+sfg.toString());
			sqlitedatabase.close();
			closeDBHelper();
    	}
    	finally{
			if(cursor != null)
				cursor.close();	
			sqlitedatabase.close();
			closeDBHelper();
    	}
    	return productArrList;
    }//fn readDataSelectedStrListDataFromDB
	
	public void creatTable(Context ctx){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			SapGenConstants.showLog("DB_LIST_TABLE_CREATE : "+DBConstants.DB_LIST_TABLE_CREATE);
			sqlitedatabase.execSQL(DBConstants.DB_LIST_TABLE_CREATE);  
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in creatTable:"+e.toString());
		}   
    	finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
			closeDBHelper();
    	}
    }//fn creatTable
	
	public void clearProductUIConfListTable(){   	
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='"+DBConstants.DB_TABLE_NAME+"'", null);    
			if (c.getCount()>0){         
				sqlitedatabase.execSQL("DELETE FROM "+DBConstants.DB_TABLE_NAME);    
			} 			
			c.close();
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in clearProductUIConfListTable:"+e.toString());
			sqlitedatabase.close();
		}   
    }//fn clearProductUIConfListTable
	
	public void dropTable(Context ctx){
		boolean isExits = false;	
    	try {
    		DBHelper = new DatabaseHelper(ctx);
    		sqlitedatabase = DBHelper.getWritableDatabase();
			SapGenConstants.showLog("1");
			isExits = checkTable();	
			SapGenConstants.showLog("2");
			SapGenConstants.showLog("DROP TABLE IF EXISTS "+DBConstants.DB_TABLE_NAME);	
			SapGenConstants.showLog("3:"+isExits);
			if(isExits)
				sqlitedatabase.execSQL("DROP TABLE IF EXISTS "+DBConstants.DB_TABLE_NAME); 	
			SapGenConstants.showLog("4:"+isExits);
			sqlitedatabase.close();
			closeDBHelper();
    	} catch (Exception e) {
    		SapGenConstants.showErrorLog("Error in dropTable:"+e.toString());
		}  
    	finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
			closeDBHelper();
    	}
    }//fn dropTable

	public String getParticularField(String requireFieldName, String matchColumnName, String matchColumnValue){
    	String value = "";
    	try {
			DBHelper = new DatabaseHelper(context);
			sqlitedatabase = DBHelper.getWritableDatabase();	
			c = sqlitedatabase.rawQuery("select "+requireFieldName+" from "+DBConstants.DB_TABLE_NAME+" where "+matchColumnName+"='"+matchColumnValue+"'", null);
			if(c != null && c.getCount() > 0){
				if (c.moveToFirst())
				{
					value = c.getString(0);
				}
			}else{
				value = "";
			}
			sqlitedatabase.close();
			closeDBHelper();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getParticularField:"+e.toString());
		}   
    	finally{		
			sqlitedatabase.close();
			if(DBHelper != null)
				DBHelper.close();
			closeDBHelper();
    	}
    	return value;
    }//fn getParticularField
}