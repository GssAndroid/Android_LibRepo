package com.globalsoft.SalesOrderLib.Database;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.globalsoft.DataBaseLib.DBConstants;

import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProCustConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProPriceHeadConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProPriceItemConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrderCntxtOpConstraints;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;


public class SalesOrderDBOperations {
	 private static SalesOrderDB mDB;
	 private static SQLiteDatabase sqlitedatabase;
	 	 	 		 	 
	 /*public static String readSOSearchBarFromDB(Context ctx, String cntx2){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	String scrTitle = "";
	    	try{
	    		int[] dbIndex = new int[2];
	    		String[] dbValues = new String[2];    		    		
	    		Uri uri = Uri.parse(SalesOrderCP.SO_CNTXT_MAIN_CONTENT_URI+"");			
				ContentResolver resolver = ctx.getContentResolver();
				selection = SalesOrderDBConstants.SO_CNTX_COL_CNTXT4 + " = ? and "+SalesOrderDBConstants.SO_CNTX_COL_NAME + " = ?"; 
				selectionParams = new String[]{cntx2, UIDBConstants.VALUE_SEARCHBAR_TAG};			
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
				SalesOrderConstants.showLog("No of Records for StatusList : "+cursor.getCount());
							
				if(cursor.getCount() == 0){
					return scrTitle;
				}
				
				dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CNTX_COL_VALUE);
				cursor.moveToFirst();
				dbValues[0] = cursor.getString(dbIndex[0]);								
				if(dbValues[0] != null)
					scrTitle = dbValues[0].toString().trim();	
				
				mDB = new CustCreditDB(ctx);	
				SQLiteDatabase sqlDB = mDB.getWritableDatabase();
				Cursor ti = sqlDB.rawQuery("PRAGMA table_info("+CustCreditDBConstants.TABLE_CUS_CNTX+")", null);
				if ( ti.moveToFirst() ) {
				    do {
				        System.out.println("col: " + ti.getString(1));
				    } while (ti.moveToNext());
				}
				mDB.close();
				sqlDB.close();
	    	}
	    	catch(Exception sfg){
	    		SalesOrderConstants.showErrorLog("Error in readSOSearchBarFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return scrTitle;
	    }//fn readSOSearchBarFromDB
*/	 
	 
	public static ArrayList readAllItemDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProItemOpConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[14];
    		String[] dbValues = new String[14];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_MAIN_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_VBELN);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_POSNR);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_MATNR);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_KWMENG);
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_VRKME);
			dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_NETWR);
			dbIndex[6] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_WAERK);
			dbIndex[7] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_ABGRU_TEXT);
			dbIndex[8] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_FAKSP_TEXT);
			dbIndex[9] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_GBSTA_TEXT);
			dbIndex[10] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_LFSTA_TEXT);
			dbIndex[11] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_FKSTA_TEXT);
			dbIndex[12] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_ARKTX);
			
			dbIndex[13] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[12]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				dbValues[6] = cursor.getString(dbIndex[6]);
				dbValues[7] = cursor.getString(dbIndex[7]);
				dbValues[8] = cursor.getString(dbIndex[8]);
				dbValues[9] = cursor.getString(dbIndex[9]);
				dbValues[10] = cursor.getString(dbIndex[10]);
				dbValues[11] = cursor.getString(dbIndex[11]);
				dbValues[12] = cursor.getString(dbIndex[12]);
				
				dbValues[13] = String.valueOf(colId);
				
				//SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProItemOpConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllSerchDataFromDB : "+sfg.toString());
    	}
    
			if(cursor != null)
				cursor.close();		
    	
    	return stocksArrList;
	}	

	 public static ArrayList readAllSelctdItemIDFromDB(Context ctx,String SelStr){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] projection=null;
	    	String[] selectionParams = null;
	    	String orderBy = null;	   
	    	//SalesOrdProItemOpConstraints serCategory = null;
	    	ArrayList<String> stocksSerArrList = new ArrayList<String>();	 
	    	//String[] stocksSerArrList = null;
	    	String SelcItem="",delimiter=",",delim1="'";
	    			
	    	try{
	    		int[] dbIndex = new int[5];
	    		String[] dbValues = new String[5];
	    		int colId = -1;
	    		
	    		
	    		String str=null;
	    		int i=0;
	    		
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		mDB = new SalesOrderDB(ctx);
	    		
				SQLiteDatabase sqlDB = mDB.getWritableDatabase();
				
				try {					
					cursor = sqlDB.rawQuery("select * from " + SalesOrderDBConstants.TABLE_SALESORDER_LIST + " where " + SalesOrderDBConstants.SO_MAIN_COL_VBELN + "=" +SelStr,null);
				} catch (Exception ee) {
					SalesOrderConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesOrderConstants.showLog("select * from " + SalesOrderDBConstants.TABLE_SALESORDER_LIST+" where "+SalesOrderDBConstants.SO_MAIN_COL_VBELN+ "=" +SelStr+")");
				SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){					
					return stocksSerArrList;
				}
				
				
				dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_MATNR);				
				
				dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_ID);
				
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[1]);
					dbValues[0] = cursor.getString(dbIndex[0]);
										
					dbValues[1] = String.valueOf(colId);
					
					SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]);
					/*serCategory = new SalesOrdProItemOpConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);*/
					
					stocksSerArrList.add(dbValues[0]);
					//stocksSerArrList=dbValues;
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
					
	    	}
	    	catch(Exception sfg){
	    		SalesOrderConstants.showErrorLog("Error in readAllSelctdItemIDFromDB : "+sfg.toString());
	    	}
	    	
				if(cursor != null)
					cursor.close();		    		
	    	
	    	return stocksSerArrList;
	    }//fn readAllSelctdItemIDFromDB
	 
	 public static String[] readAllSelctdCustIdDataFromDB(Context ctx,String SelStr,String matno){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] projection=null;
	    	String[] selectionParams = null;
	    	String[] customerList = null;
	    	//ArrayList<String> customerList = new ArrayList<String>();
	    	String orderBy = null;	   
	    	SalesOrdProItemOpConstraints serCategory1 = null;
	    	//ArrayList customerList = new ArrayList();	    	
	    	String SelcItem="",delimiter=",",delim1="'";
	    			
	    	try{
	    		int[] dbIndex = new int[6];
	    		String[] dbValues = new String[6];
	    		int colId = -1;
	    		
	    		
	    		String str=null;
	    		int i=0;
	    		
	    		/*Uri uri = Uri.parse(SalesOrderCP.SO_MAIN_CONTENT_URI+"");
				ContentResolver resolver = ctx.getContentResolver(); */
				
				
	    		/*if(customerList != null)
	    			customerList.clear();*/
	    		mDB = new SalesOrderDB(ctx);	
	    		
				SQLiteDatabase sqlDB = mDB.getWritableDatabase();
				String sa1 = "%"+matno+"%";
				try {		
					//cursor = resolver.query(uri, null, SalesOrderDBConstants.SO_MAIN_COL_MATNR + " LIKE ? ", new String[] { sa1 } + " AND " + SalesOrderDBConstants.SO_MAIN_COL_VBELN+ " = '" + SelStr + "'", null);
					cursor = sqlDB.rawQuery("select * from " + SalesOrderDBConstants.TABLE_SALESORDER_LIST + " where " + SalesOrderDBConstants.SO_MAIN_COL_VBELN + "=" +SelStr+ " AND " +SalesOrderDBConstants.SO_MAIN_COL_MATNR + " LIKE ? ", new String[] { sa1 });
			//cursor = resolver.query(uri, null, SalesOrderDBConstants.SO_MAIN_COL_VBELN + "=" +SelStr+ " AND " +SalesOrderDBConstants.SO_MAIN_COL_MATNR + " LIKE ? "+sa1,null);
				} catch (Exception ee) {
					SalesOrderConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesOrderConstants.showLog("select * from " + SalesOrderDBConstants.TABLE_SALESORDER_LIST+" where "+SalesOrderDBConstants.SO_MAIN_COL_VBELN+ "=" +SelStr+")");
				SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){					
					return customerList;
				}
				
				
				dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_MATNR);
				dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_KWMENG);
				dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_VRKME);
				dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_NETWR);
				dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MAIN_COL_WAERK);
				
				dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_ID);
				
				
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[5]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					
					
					dbValues[5] = String.valueOf(colId);
					
					
					SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]);
					//serCategory1 = new SalesOrdProItemOpConstraints(dbValues);
					/*if(customerList != null)
						customerList.add(dbValues);*/
					
					customerList=dbValues;
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
					
	    	}
	    	catch(Exception sfg){
	    		SalesOrderConstants.showErrorLog("Error in readAllSelctdCustIdDataFromDB : "+sfg.toString());
	    	}
	    	
				if(cursor != null)
					cursor.close();		    		
	    	
	    	return customerList;
	    }//fn readAllSelctdCustIdDataFromDB
	 
	
	public static ArrayList readSalesOrderCustDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProCustConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[10];
    		String[] dbValues = new String[10];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_CUST_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_KUNNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_NAME1);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_LAND1);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_REGIO);
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_ORT01);
			dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_STRAS);
			dbIndex[6] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_TELF1);
			dbIndex[7] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_TELF2);
			dbIndex[8] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_SMTP_ADDR);
			
			dbIndex[9] = cursor.getColumnIndex(SalesOrderDBConstants.SO_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[9]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				dbValues[6] = cursor.getString(dbIndex[6]);
				dbValues[7] = cursor.getString(dbIndex[7]);
				dbValues[8] = cursor.getString(dbIndex[8]);
				
				dbValues[9] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProCustConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllSerchDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	
	public static ArrayList readAllmattDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_MATT_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MATNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MAKTX);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MEINH);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MSEHT);
			
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[4]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				
				dbValues[10] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProMattConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllmattDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
    
	public static ArrayList readAllmattIdDataFromDB(Context ctx,String SelStr1){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		mDB = new SalesOrderDB(ctx);	
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_MATT_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			//cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderConstants.showLog("String value : "+sa1);
    		//cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME2 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_MATT_COL_MATNR + " LIKE ? ", new String[] { sa1 }, null);
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MATNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MAKTX);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MEINH);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MSEHT);
			
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[4]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				
				dbValues[10] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProMattConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllmattDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static String[] readmattDataForListViewFromDB(Context ctx,String SelStr1){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints stkCategory = null;
    	//ArrayList stocksArrList = new ArrayList();
    	String[] stocksArrList = null;
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		mDB = new SalesOrderDB(ctx);	
    		/*if(stocksArrList != null)
    			stocksArrList.clear();*/
    		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_MATT_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			//cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderConstants.showLog("String value : "+sa1);
    		//cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME2 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_MATT_COL_MATNR + " LIKE ? ", new String[] { sa1 }, null);
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MATNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MAKTX);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MEINH);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MSEHT);
			
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[4]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				
				dbValues[10] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				/*stkCategory = new SalesOrdProMattConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				*/
				stocksArrList=dbValues;
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllmattDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	
	public static ArrayList readAllmattIdFromDB(Context ctx,String SelStr1){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints stkCategory = null;
    	ArrayList<String> stocksArrList = new ArrayList<String>();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		mDB = new SalesOrderDB(ctx);	
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_MATT_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			//cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderConstants.showLog("String value : "+sa1);
    		//cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME2 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_MATT_COL_MATNR + " LIKE ? ", new String[] { sa1 }, null);
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_MATNR);
			
			
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_MATT_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[1]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				
				dbValues[1] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				/*stkCategory = new SalesOrdProMattConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				*/
				stocksArrList.add(dbValues[0]);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllmattIdFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static ArrayList readAllHeadPriceDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProPriceHeadConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_HEAD_PRICE_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_PRICE_COL_KUNAG);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_PRICE_COL_KETDAT);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_PRICE_COL_NETWR);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_PRICE_COL_WAERK);
			
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[4]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				
				dbValues[10] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProPriceHeadConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllHeadPriceDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static ArrayList readAllCreateItemDataFromDB(Context ctx,String SelStr1){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;    	
    	ArrayList stocksArrList = new ArrayList();
    	try{    		
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		//DBHelper = new DatabaseHelper(context);
    		/*sqlitedatabase = mDB.getReadableDatabase();			
    		 cursor = sqlitedatabase.query(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, null, null, null, null, null, null);
    		SapGenConstants.showLog("select * from "+ SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST);
    		SapGenConstants.showLog("No of columns Category Records : "+cursor.getCount());
    		String[] columnNames = cursor.getColumnNames();
    		SapGenConstants.showLog("columnNames length : "+columnNames.length);*/
    		Uri uri = Uri.parse(SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			//cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderConstants.showLog("String value : "+sa1);
    		//cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME2 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CREATE_COL_ALT_ID + " LIKE ? ", new String[] { sa1 }, null);
			String[] columnNames = cursor.getColumnNames();
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			SalesOrderConstants.showLog("columnNames lenght : "+columnNames.length);
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			int[] dbIndex = new int[columnNames.length];
    		String[] dbValues = new String[columnNames.length];
			
    		for (int j = 0; j < columnNames.length; j++) {
    			dbIndex[j] = cursor.getColumnIndex(columnNames[j]);	
    		}
    					
    		cursor.moveToFirst();
    		HashMap<String, String> stockMap = null;
			do{
    			stockMap = new HashMap<String, String>();
    			//for(int k=0;k<cursor.)
				for (int j = 0; j < dbIndex.length; j++) {
	    			dbValues[j] = cursor.getString(dbIndex[j]);
	                stockMap.put(columnNames[j], dbValues[j]);	
				}										
				if(stockMap != null){
					SalesOrderConstants.showLog("stockMap value : "+stockMap);
					stocksArrList.add(stockMap);	
				}			
				cursor.moveToNext();
			}while(!cursor.isAfterLast());	
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllItemPriceDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static ArrayList readAllItemPriceDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProPriceItemConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_ITEM_PRICE_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_POSNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_MATNR);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_KWMENG);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_VRKME);
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_NETWR);
			dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_WAERK);
			
			dbIndex[6] = cursor.getColumnIndex(SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[4]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				
				dbValues[10] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProPriceItemConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllItemPriceDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static ArrayList readAllCustomerIdDataFromDB(Context ctx,String SelStr1){
    	Cursor cursor = null;
    	
    	SalesOrdProCustConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList ();
    	
    	try{
    		int[] dbIndex = new int[12];
    		String[] dbValues = new String[12];
    		int colId = -1;
    		mDB = new SalesOrderDB(ctx);	
    		
			SQLiteDatabase sqlDB = mDB.getWritableDatabase();
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_CUST_CONTENT_URI+"");
			ContentResolver resolver = ctx.getContentResolver(); //Get the Resolver
    						
    		String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderConstants.showLog("String value : "+sa1);
    		//cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME2 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		cursor = resolver.query(uri, null,SalesOrderDBConstants.SO_CUST_COL_NAME1 + " LIKE ? ", new String[] { sa1 }, null);
    		SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_KUNNR);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_NAME1);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_LAND1);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_REGIO);
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_ORT01);
			dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_STRAS);
			dbIndex[6] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_TELF1);
			dbIndex[7] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_TELF2);
			dbIndex[8] = cursor.getColumnIndex(SalesOrderDBConstants.SO_CUST_COL_SMTP_ADDR);
			
			dbIndex[9] = cursor.getColumnIndex(SalesOrderDBConstants.SO_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[9]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				dbValues[6] = cursor.getString(dbIndex[6]);
				dbValues[7] = cursor.getString(dbIndex[7]);
				dbValues[8] = cursor.getString(dbIndex[8]);
				
				dbValues[9] = String.valueOf(colId);
				
				SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProCustConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllSerchIdDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
    }//fn readAllSerchDataFromDB
	
	public static ArrayList readAllHeadOutputDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProHeadOpConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[36];
    		String[] dbValues = new String[36];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesOrderCP.SO_HEAD_OP_CONTENT_URI+"");
			
			ContentResolver resolver = ctx.getContentResolver();
			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
			
			SalesOrderConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_VBELN);
			dbIndex[1] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_KUNAG);
			dbIndex[2] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_NAME1A);
			dbIndex[3] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_LAND1A);
			dbIndex[4] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_REGIOA);
			dbIndex[5] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_ORT01A);
			dbIndex[6] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_STRASA);
			dbIndex[7] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_TELF1A);
			dbIndex[8] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_TELF2A);
			dbIndex[9] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRA);
			dbIndex[10] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_PARNR);
			dbIndex[11] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_NAME1PK);
			
			dbIndex[12] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_LAND1P);
			dbIndex[13] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_REGIOP);
			dbIndex[14] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_ORT01P);
			dbIndex[15] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_STRASP);
			dbIndex[16] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_TELF1P);
			dbIndex[17] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_TELF2P);
			dbIndex[18] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRP);
			dbIndex[19] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_AUDAT);
			dbIndex[20] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_WAERK);
			dbIndex[21] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_NETWR);
			dbIndex[22] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_AUGRU_TEXT);
			dbIndex[23] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_GBSTK_TEXT);
			
			dbIndex[24] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_ABSTK_TEXT);
			dbIndex[25] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_LFSTK_TEXT);
			dbIndex[26] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_CMGST_TEXT);
			dbIndex[27] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_SPSTG_TEXT);
			dbIndex[28] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_LIFSK_TEXT);
			dbIndex[29] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_FKSTK_TEXT);
			dbIndex[30] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_FAKSK_TEXT);
			dbIndex[31] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_ZZSTATUS_SUMMARY);
			dbIndex[32] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_KETDAT);
			dbIndex[33] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_BSTKD);
			dbIndex[34] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_BSTDK);
			
			
			dbIndex[35] = cursor.getColumnIndex(SalesOrderDBConstants.SO_HEAD_COL_ID);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(dbIndex[35]);
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				dbValues[6] = cursor.getString(dbIndex[6]);
				dbValues[7] = cursor.getString(dbIndex[7]);
				dbValues[8] = cursor.getString(dbIndex[8]);
				dbValues[9] = cursor.getString(dbIndex[9]);
				dbValues[10] = cursor.getString(dbIndex[10]);
				dbValues[11] = cursor.getString(dbIndex[11]);
				
				dbValues[12] = cursor.getString(dbIndex[12]);
				dbValues[13] = cursor.getString(dbIndex[13]);
				dbValues[14] = cursor.getString(dbIndex[14]);
				dbValues[15] = cursor.getString(dbIndex[15]);
				dbValues[16] = cursor.getString(dbIndex[16]);
				dbValues[17] = cursor.getString(dbIndex[17]);
				dbValues[18] = cursor.getString(dbIndex[18]);
				dbValues[19] = cursor.getString(dbIndex[19]);
				dbValues[20] = cursor.getString(dbIndex[20]);
				dbValues[21] = cursor.getString(dbIndex[21]);
				dbValues[22] = cursor.getString(dbIndex[22]);
				dbValues[23] = cursor.getString(dbIndex[23]);
				
				dbValues[24] = cursor.getString(dbIndex[24]);
				dbValues[25] = cursor.getString(dbIndex[25]);
				dbValues[26] = cursor.getString(dbIndex[26]);
				dbValues[27] = cursor.getString(dbIndex[27]);
				dbValues[28] = cursor.getString(dbIndex[28]);
				dbValues[29] = cursor.getString(dbIndex[29]);
				dbValues[30] = cursor.getString(dbIndex[30]);
				dbValues[31] = cursor.getString(dbIndex[31]);
				dbValues[32] = cursor.getString(dbIndex[32]);
				dbValues[33] = cursor.getString(dbIndex[33]);
				dbValues[34] = cursor.getString(dbIndex[34]);
				
				
				
				dbValues[35] = String.valueOf(colId);
				
				//SalesOrderConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkCategory = new SalesOrdProHeadOpConstraints(dbValues);
				if(stocksArrList != null)
					stocksArrList.add(stkCategory);
				
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in readAllSerchDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return stocksArrList;
	}	

	
	/*public static void insertSOCntxListDataInToDB(Context ctx, SalesOrderCntxtOpConstraints cntxcategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    			        
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_CNTXT2, cntxcategory.getCntxt2().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_CNTXT3, cntxcategory.getCntxt3().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_CNTXT4, cntxcategory.getCntxt4().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_NAME, cntxcategory.getName().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_DPNDNCY, cntxcategory.getDpndncy().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_SEQNR, cntxcategory.getSeqNR().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_TYPE, cntxcategory.getType().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_SIGN, cntxcategory.getSign().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_OPTN, cntxcategory.getOptn().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_VALUE, cntxcategory.getValue().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_VALUE_HIGH, cntxcategory.getValueHigh().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_TRGTNAME, cntxcategory.getTrgtName().toString().trim());
	    	val.put(SalesOrderDBConstants.SO_CNTX_COL_TRGTVALUE, cntxcategory.getTrgtValue().toString().trim());
	    	
	    	resolver.insert(SalesOrderCP.SO_CNTXT_MAIN_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertSOCntxListDataInToDB : "+sgh.toString());
    	}
    }//fn insertCntxListDataInToDB
*/	
	
	
	public static void insertSalesOrderItemDataInToDB(Context ctx, SalesOrdProItemOpConstraints stkCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String docno = "", soldtoparty = "", matrno = "", qtysales = "", salesunit="",netvaldoc="",sdoccurr="",reasfrreject="",
	    	billngblckresn="",ovralstatus="",delvrstatus="",billngstatus="",itemdesc=""; 
	    	
	    	docno = stkCategory.getDocumentNo();
	    	soldtoparty = stkCategory.getSoldToParty();
	    	matrno = stkCategory.getMaterialNo();
	    	qtysales = stkCategory.getCummOrdQtySales();
	    	salesunit = stkCategory.getSalesUnit();
	    	netvaldoc = stkCategory.getNetValDocCurr1();
	    	sdoccurr = stkCategory.getSDDocCurr();
	    	reasfrreject = stkCategory.getReasonForRjct();
	    	billngblckresn = stkCategory.getBillingBlkReason();
	    	ovralstatus = stkCategory.getOverallStatus();
	    	delvrstatus = stkCategory.getDeliveryStatus();
	    	billngstatus = stkCategory.getBillingStatus();
	    	itemdesc = stkCategory.getItemDescription();	    	
	    	
	    	if(docno == null)
	    		docno = "";
	    	
	    	if(soldtoparty == null)
	    		soldtoparty = "";
	    	
	    	if(matrno == null)
	    		matrno = "";
	    	
	    	if(qtysales == null)
	    		qtysales = "";
	    	
	    	if(salesunit == null)
	    		salesunit = "";
	    	
	    	if(netvaldoc == null)
	    		netvaldoc = "";
	    	
	    	if(sdoccurr == null)
	    		sdoccurr = "";
	    	
	    	if(reasfrreject == null)
	    		reasfrreject = "";
	    	
	    	if(billngblckresn == null)
	    		billngblckresn = "";
	    	
	    	if(ovralstatus == null)
	    		ovralstatus = "";
	    	
	    	if(delvrstatus == null)
	    		delvrstatus = "";
	    	
	    	if(billngstatus == null)
	    		billngstatus = "";
	    	
	    	if(itemdesc == null)
	    		itemdesc = "";
	    	
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_VBELN, docno );
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_POSNR, soldtoparty);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_MATNR, matrno);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_KWMENG,qtysales);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_VRKME, salesunit );
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_NETWR, netvaldoc);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_WAERK, sdoccurr);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_ABGRU_TEXT, reasfrreject);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_FAKSP_TEXT, billngblckresn);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_GBSTA_TEXT, ovralstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_LFSTA_TEXT, delvrstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_FKSTA_TEXT, billngstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_ARKTX, itemdesc);
	    	
	    	resolver.insert(SalesOrderCP.SO_MAIN_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertSalesOrderItemDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB	

    public static void updateSalesOrderItemDataInToDB(Context ctx, String id, SalesOrdProItemOpConstraints stkCategory){
    	try{
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesOrderDBConstants.SO_MAIN_COL_VBELN + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	String docno = "", soldtoparty = "", matrno = "", qtysales = "", salesunit="",netvaldoc="",sdoccurr="",reasfrreject="",
	    	billngblckresn="",ovralstatus="",delvrstatus="",billngstatus="",itemdesc=""; 
	    	
	    	//docno = stkCategory.getDocumentNo();
	    	soldtoparty = stkCategory.getSoldToParty();
	    	matrno = stkCategory.getMaterialNo();
	    	qtysales = stkCategory.getCummOrdQtySales();
	    	salesunit = stkCategory.getSalesUnit();
	    	netvaldoc = stkCategory.getNetValDocCurr1();
	    	sdoccurr = stkCategory.getSDDocCurr();
	    	reasfrreject = stkCategory.getReasonForRjct();
	    	billngblckresn = stkCategory.getBillingBlkReason();
	    	ovralstatus = stkCategory.getOverallStatus();
	    	delvrstatus = stkCategory.getDeliveryStatus();
	    	billngstatus = stkCategory.getBillingStatus();
	    	itemdesc = stkCategory.getItemDescription();	    	
	    	
	    	/*if(docno == null)
	    		docno = "";*/
	    	
	    	if(soldtoparty == null)
	    		soldtoparty = "";
	    	
	    	if(matrno == null)
	    		matrno = "";
	    	
	    	if(qtysales == null)
	    		qtysales = "";
	    	
	    	if(salesunit == null)
	    		salesunit = "";
	    	
	    	if(netvaldoc == null)
	    		netvaldoc = "";
	    	
	    	if(sdoccurr == null)
	    		sdoccurr = "";
	    	
	    	if(reasfrreject == null)
	    		reasfrreject = "";
	    	
	    	if(billngblckresn == null)
	    		billngblckresn = "";
	    	
	    	if(ovralstatus == null)
	    		ovralstatus = "";
	    	
	    	if(delvrstatus == null)
	    		delvrstatus = "";
	    	
	    	if(billngstatus == null)
	    		billngstatus = "";
	    	
	    	if(itemdesc == null)
	    		itemdesc = "";
	    	
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_POSNR, soldtoparty);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_MATNR, matrno);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_KWMENG,qtysales);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_VRKME, salesunit );
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_NETWR, netvaldoc);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_WAERK, sdoccurr);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_ABGRU_TEXT, reasfrreject);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_FAKSP_TEXT, billngblckresn);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_GBSTA_TEXT, ovralstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_LFSTA_TEXT, delvrstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_FKSTA_TEXT, billngstatus);
	    	val.put(SalesOrderDBConstants.SO_MAIN_COL_ARKTX, itemdesc);   	
			resolver.update(SalesOrderCP.SO_MAIN_CONTENT_URI, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateSalesOrderItemDataInToDB : "+sggh.toString());
    	}
    }//fn updateSalesOrderItemDataInToDB
    
    public static boolean checkItemIdExists(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		boolean idexists = false;
		try{								
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesOrderDBConstants.SO_MAIN_COL_VBELN + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(SalesOrderCP.SO_MAIN_CONTENT_URI, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				idexists = false;
			}else{
				idexists = true;
			}
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in checkItemIdExists : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return idexists;
	}//fn checkItemIdExists
    
	public static void insertmattDataInToDB(Context ctx, SalesOrdProMattConstraints stkCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String mattNoStr = "", mattDescStr = "", mattUnitStr = "", mattUnitDescStr = ""; 
	    	mattNoStr = stkCategory.getMaterialNo();
	    	mattDescStr = stkCategory.getMaterialDesc();
	    	mattUnitStr = stkCategory.getMaterialUnit();
	    	mattUnitDescStr = stkCategory.getMaterialUnitDesc();
	    	
	    	if(mattNoStr == null)
	    		mattNoStr = "";
	    	
	    	if(mattDescStr == null)
	    		mattDescStr = "";
	    	
	    	if(mattUnitStr == null)
	    		mattUnitStr = "";
	    	
	    	if(mattUnitDescStr == null)
	    		mattUnitDescStr = "";
	    	
	    	val.put(SalesOrderDBConstants.SO_MATT_COL_MATNR, mattNoStr );
	    	val.put(SalesOrderDBConstants.SO_MATT_COL_MAKTX, mattDescStr);
	    	val.put(SalesOrderDBConstants.SO_MATT_COL_MEINH, mattUnitStr);
	    	val.put(SalesOrderDBConstants.SO_MATT_COL_MSEHT, mattUnitDescStr);
	    	
	    	resolver.insert(SalesOrderCP.SO_MATT_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertmattDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB
	
	public static void insertHeadPriceDataInToDB(Context ctx, SalesOrdProPriceHeadConstraints stkCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String soldtopartyStr = "", reqstdateStr = "", netvalStr = "", docStr = ""; 
	    	soldtopartyStr = stkCategory.getSoldToParty();
	    	reqstdateStr = stkCategory.getReqstdDate();
	    	netvalStr = stkCategory.getNetValue();
	    	docStr = stkCategory.getDocCurrency();
	    	
	    	if(soldtopartyStr == null)
	    		soldtopartyStr = "";
	    	
	    	if(reqstdateStr == null)
	    		reqstdateStr = "";
	    	
	    	if(netvalStr == null)
	    		netvalStr = "";
	    	
	    	if(docStr == null)
	    		docStr = "";
	    	
	    	val.put(SalesOrderDBConstants.SO_HEAD_PRICE_COL_KUNAG, soldtopartyStr );
	    	val.put(SalesOrderDBConstants.SO_HEAD_PRICE_COL_KETDAT, reqstdateStr);
	    	val.put(SalesOrderDBConstants.SO_HEAD_PRICE_COL_NETWR, netvalStr);
	    	val.put(SalesOrderDBConstants.SO_HEAD_PRICE_COL_WAERK, docStr);
	    	
	    	resolver.insert(SalesOrderCP.SO_HEAD_PRICE_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertHeadPriceDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB
	
	public static void insertItemPriceDataInToDB(Context ctx, SalesOrdProPriceItemConstraints stkCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String itemnostr = "", mattnoStr = "", cummordrStr = "", getsalesunitStr = "",netvaluestr="",docurncyStr=""; 
	    	itemnostr = stkCategory.getItemNo();
	    	mattnoStr = stkCategory.getMaterialNo();
	    	cummordrStr = stkCategory.getCummOrder();
	    	getsalesunitStr = stkCategory.getSalesUnit();
	    	netvaluestr = stkCategory.getNetValue();
	    	docurncyStr = stkCategory.getDocCurrency();
	    	
	    	if(itemnostr == null)
	    		itemnostr = "";
	    	
	    	if(mattnoStr == null)
	    		mattnoStr = "";
	    	
	    	if(cummordrStr == null)
	    		cummordrStr = "";
	    	
	    	if(getsalesunitStr == null)
	    		getsalesunitStr = "";
	    	
	    	if(netvaluestr == null)
	    		netvaluestr = "";
	    	
	    	if(docurncyStr == null)
	    		docurncyStr = "";
	    	
	    	
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_POSNR, itemnostr );
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_MATNR, mattnoStr);
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_KWMENG, cummordrStr);
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_VRKME, getsalesunitStr);
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_NETWR, netvaluestr);
	    	val.put(SalesOrderDBConstants.SO_ITEM_PRICE_COL_WAERK, docurncyStr);
	    	
	    	
	    	resolver.insert(SalesOrderCP.SO_ITEM_PRICE_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertItemPriceDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB
	
	public static void insertCreateScreenDataInToDB(Context ctx, ArrayList createItemList, HashMap qtylist,String custid,String altidStr){
    	try{
    		 HashMap<String, String> itemMap = null; 
    		 int itemSize = createItemList.size();
    		 SalesOrderConstants.showLog("itemSize : "+itemSize);
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String itemnostr = "", mattnoStr = "", qtyrStr = "", unitStr = "",priceStr="",priceUnitStr=""; 
	    	for(int k = 0; k < itemSize; k++){
	    		itemMap = (HashMap<String, String>) createItemList.get(k);   
	    		itemnostr = (String) itemMap.get(DBConstants.SO_MAIN_COL_ARKTX);
	    		mattnoStr = (String) itemMap.get(DBConstants.SO_MAIN_COL_MATNR);
	    		qtyrStr = (String)qtylist.get(mattnoStr); 
	    		//qtyrStr = (String) itemMap.get(DBConstants.SO_MAIN_COL_KWMENG);
	    		unitStr = (String) itemMap.get(DBConstants.SO_MAIN_COL_VRKME);
	    		priceStr = (String) itemMap.get(DBConstants.SO_MAIN_COL_NETWR);
	    		priceUnitStr = (String) itemMap.get(DBConstants.SO_MAIN_COL_WAERK);	    		
	    		
	    		val.put(SalesOrderDBConstants.SO_CREATE_COL_KUNNR, custid);
	    		val.put(SalesOrderDBConstants.SO_CREATE_COL_ARKTX, itemnostr );
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_MATNR, mattnoStr);
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_KWMENG, qtyrStr);
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_VRKME, unitStr);
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_NETWR, priceStr);
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_WAERK, priceUnitStr);
		    	val.put(SalesOrderDBConstants.SO_CREATE_COL_ALT_ID, altidStr);
		    	resolver.insert(SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI, val);
        	}//	 
	    	
	    	
	    	/*val.put(SalesOrderDBConstants.SO_CREATE_COL_ARKTX, itemnostr );
	    	val.put(SalesOrderDBConstants.SO_CREATE_COL_MATNR, mattnoStr);
	    	val.put(SalesOrderDBConstants.SO_CREATE_COL_KWMENG, qtyrStr);
	    	val.put(SalesOrderDBConstants.SO_CREATE_COL_VRKME, unitStr);
	    	val.put(SalesOrderDBConstants.SO_CREATE_COL_NETWR, priceStr);
	    	val.put(SalesOrderDBConstants.SO_CREATE_COL_WAERK, priceUnitStr);*/
	    	
	    	
	    	// resolver.insert(SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI, val);
	    	
	    	
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertCreateScreenDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB
	
	public static void insertCustomerDataInToDB(Context ctx, SalesOrdProCustConstraints stkCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	String custno = "", custname = "", custcountry = "", custregn = "", custcity="",custstreet="",custtelno1="",custtelno2="",custemail=""; 
	    	
	    	custno = stkCategory.getCustomerNo();
	    	custname = stkCategory.getName();
	    	custcountry = stkCategory.getCountry();
	    	custregn = stkCategory.getRegion();
	    	custcity = stkCategory.getCity();
	    	custstreet = stkCategory.getStreet();
	    	custtelno1 = stkCategory.getTelNo1();
	    	custtelno2 = stkCategory.getTelNo2();
	    	custemail = stkCategory.getEmail();
	    	
	    	
	    	if(custno == null)
	    		custno = "";
	    	
	    	if(custname == null)
	    		custname = "";
	    	
	    	if(custcountry == null)
	    		custcountry = "";
	    	
	    	if(custregn == null)
	    		custregn = "";
	    	
	    	if(custcity == null)
	    		custcity = "";
	    	
	    	if(custstreet == null)
	    		custstreet = "";
	    	
	    	if(custtelno1 == null)
	    		custtelno1 = "";
	    	
	    	if(custtelno2 == null)
	    		custtelno2 = "";
	    	
	    	if(custemail == null)
	    		custemail = "";
	    	
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_KUNNR, custno);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_NAME1, custname);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_LAND1, custcountry);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_REGIO, custregn);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_ORT01, custcity);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_STRAS, custstreet);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_TELF1, custtelno1);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_TELF2, custtelno2);
	    	val.put(SalesOrderDBConstants.SO_CUST_COL_SMTP_ADDR, custemail);
	    	
	    	resolver.insert(SalesOrderCP.SO_CUST_CONTENT_URI, val);
    	}
    	catch(Exception sgh8){
    		SalesOrderConstants.showErrorLog("Error in insertCustomerDataInToDB : "+sgh8.toString());
    	}
    }//fn insertCustomerDataInToDB
    
    
    public static void insertSalesOrderHeadOutputDataInToDB(Context ctx, SalesOrdProHeadOpConstraints serCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_VBELN, serCategory.getDocumentNo());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_KUNAG, serCategory.getSoldToParty());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NAME1A, serCategory.getSPName());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LAND1A, serCategory.getSPCountry());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_REGIOA, serCategory.getSPRegion());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ORT01A, serCategory.getSPCity());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_STRASA, serCategory.getSPStreet());
	    	
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF1A, serCategory.getSPTel1());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF2A, serCategory.getSPTel2());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRA, serCategory.getSPEmail());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_PARNR, serCategory.getCPNo());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NAME1PK, serCategory.getCPName());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LAND1P, serCategory.getCPCountry());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_REGIOP, serCategory.getCPRegion());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ORT01P, serCategory.getCPCity());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_STRASP, serCategory.getCPStreet());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF1P, serCategory.getCPTel1());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF2P, serCategory.getCPTel2());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRP, serCategory.getCPEmail());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_AUDAT, serCategory.getDocDate());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_WAERK, serCategory.getSDDocCurrency());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NETWR, serCategory.getNetValDocCurr());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_AUGRU_TEXT, serCategory.getOrdRcjtReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_GBSTK_TEXT, serCategory.getDocStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ABSTK_TEXT, serCategory.getRejectStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LFSTK_TEXT, serCategory.getDeliveryStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_CMGST_TEXT, serCategory.getCreditCheckStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SPSTG_TEXT, serCategory.getBlockedStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LIFSK_TEXT, serCategory.getDeliveryBlockReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_FKSTK_TEXT, serCategory.getBillingStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_FAKSK_TEXT, serCategory.getBillingBlockReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ZZSTATUS_SUMMARY, serCategory.getStatusSummary());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_KETDAT, serCategory.getRequiredDate());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_BSTKD, serCategory.getPurchaseOrderNo());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_BSTDK, serCategory.getPurchaseOrderDate());
	    	
	    	
	    	resolver.insert(SalesOrderCP.SO_HEAD_OP_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderConstants.showErrorLog("Error in insertSalesOrderHeadOutputDataInToDB : "+sgh.toString());
    	}
    }//fn insertSalesOrderHeadOutputDataInToDB
    
    public static void updateSODataValue(Context ctx, String id, SalesOrdProHeadOpConstraints serCategory){
    	try{
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesOrderDBConstants.SO_HEAD_COL_VBELN + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_KUNAG, serCategory.getSoldToParty());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NAME1A, serCategory.getSPName());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LAND1A, serCategory.getSPCountry());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_REGIOA, serCategory.getSPRegion());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ORT01A, serCategory.getSPCity());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_STRASA, serCategory.getSPStreet());	    	
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF1A, serCategory.getSPTel1());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF2A, serCategory.getSPTel2());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRA, serCategory.getSPEmail());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_PARNR, serCategory.getCPNo());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NAME1PK, serCategory.getCPName());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LAND1P, serCategory.getCPCountry());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_REGIOP, serCategory.getCPRegion());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ORT01P, serCategory.getCPCity());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_STRASP, serCategory.getCPStreet());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF1P, serCategory.getCPTel1());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_TELF2P, serCategory.getCPTel2());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SMTP_ADDRP, serCategory.getCPEmail());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_AUDAT, serCategory.getDocDate());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_WAERK, serCategory.getSDDocCurrency());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_NETWR, serCategory.getNetValDocCurr());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_AUGRU_TEXT, serCategory.getOrdRcjtReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_GBSTK_TEXT, serCategory.getDocStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ABSTK_TEXT, serCategory.getRejectStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LFSTK_TEXT, serCategory.getDeliveryStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_CMGST_TEXT, serCategory.getCreditCheckStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_SPSTG_TEXT, serCategory.getBlockedStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_LIFSK_TEXT, serCategory.getDeliveryBlockReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_FKSTK_TEXT, serCategory.getBillingStatus());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_FAKSK_TEXT, serCategory.getBillingBlockReason());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_ZZSTATUS_SUMMARY, serCategory.getStatusSummary());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_KETDAT, serCategory.getRequiredDate());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_BSTKD, serCategory.getPurchaseOrderNo());
	    	val.put(SalesOrderDBConstants.SO_HEAD_COL_BSTDK, serCategory.getPurchaseOrderDate());   	
			resolver.update(SalesOrderCP.SO_HEAD_OP_CONTENT_URI, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateSODataValue : "+sggh.toString());
    	}
    }//fn updateSODataValue
    
    public static boolean checkIdExists(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		boolean idexists = false;
		try{								
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesOrderDBConstants.SO_HEAD_COL_VBELN + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(SalesOrderCP.SO_HEAD_OP_CONTENT_URI, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				idexists = false;
			}else{
				idexists = true;
			}
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in checkIdExists : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return idexists;
	}//fn checkIdExists
	
    public static void deleteAllTableDataFromDB(Context ctx, Uri selUri){
    	try{
    		if(selUri != null){
	    		Uri uri = Uri.parse(selUri.toString());
				
				//Get the Resolver
				ContentResolver resolver = ctx.getContentResolver();
				
				//Make the invocation. # of rows deleted will be sent back
				int rows = resolver.delete(uri, null, null);
				
				SalesOrderConstants.showLog("Rows Deleted : "+rows);
    		}
    	}
    	catch(Exception sggh){
    		SalesOrderConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllCategoryDataFromDB
    
    public static void deleteCustTableDataFromDB(Context ctx, Uri selUri,String idStr){
    	try{
    		String selection = null;
    		String[] selectionParams = null;
    		if(selUri != null){
	    		Uri uri = Uri.parse(selUri.toString());
				
				//Get the Resolver
				ContentResolver resolver = ctx.getContentResolver();
				selection = SalesOrderDBConstants.SO_CREATE_COL_KUNNR + " = ?"; 
				selectionParams = new String[]{idStr};		
				//Make the invocation. # of rows deleted will be sent back
				int rows = resolver.delete(uri, selection, selectionParams);
				
				SalesOrderConstants.showLog("Rows Deleted : "+rows);
    		}
    	}
    	catch(Exception sggh){
    		SalesOrderConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllCategoryDataFromDB
    
    public static void deleteSOCntxDataFromDB(Context ctx, Uri selUri){
    	try{
    		
    		if(selUri != null){
    			Uri uri = Uri.parse(selUri.toString());    
    		    //Get the Resolver
    		    ContentResolver resolver = ctx.getContentResolver();  
    		    //Make the invocation. # of rows deleted will be sent back
    		   int j= resolver.delete(uri, null, null);//Get the count of deleted rows
    		}
    	}
    	catch(Exception sggh){
    		SalesOrderConstants.showErrorLog("Error in deleteSOCntxDataFromDB : "+sggh.toString());
    	}
    }//fn deleteCntxDataFromDB
    
    
    }//SalesOrderDBOperations
