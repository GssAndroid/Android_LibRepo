package com.globalsoft.SalesPro.Database;

import java.util.ArrayList;

import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProPriceConstraints;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class PriceListDBOperations {
	 private static SalesProPriceListDB mDB;
	 private static SQLiteDatabase sqlitedatabase;
	 
	 public static ArrayList readAllSerchDataFromDB(Context ctx){
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
	    		
	    		Uri uri = Uri.parse(PriceListCP.PL_SER_CONTENT_URI+"");
				
				ContentResolver resolver = ctx.getContentResolver();
				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MATNR);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MAKTX);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MEINH);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MSEHT);
				
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[4]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					
					dbValues[10] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProMattConstraints(dbValues);
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSerchDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksArrList;
	    }//fn readAllSerchDataFromDB
	    
	 public static ArrayList readAllSerchIdDataFromDB(Context ctx,String SelStr1){
	    	Cursor cursor = null;
	    	
	    	SalesOrdProMattConstraints stkCategory = null;
	    	ArrayList stocksArrList = new ArrayList ();
	    	
	    	try{
	    		int[] dbIndex = new int[12];
	    		String[] dbValues = new String[12];
	    		int colId = -1;
	    		
	    		if(stocksArrList != null)
	    			stocksArrList.clear();
	    		
	    		Uri uri = Uri.parse(PriceListCP.PL_SER_CONTENT_URI+"");
				ContentResolver resolver = ctx.getContentResolver(); //Get the Resolver
	    						
	    		String sa1 = "%"+SelStr1+"%"; // contains an "input String"
	    		SalesOrderProConstants.showLog("String value : "+sa1);
	    		cursor = resolver.query(uri, null,PriceListDBConstants.PL_SER_COL_MATNR + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
	    						
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MATNR);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MAKTX);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MEINH);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MSEHT);
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[4]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					dbValues[5] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProMattConstraints(dbValues);//insert all values of selected rows into constraint object
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSerchIdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksArrList;
	    }//fn readAllSerchDataFromDB
	    
	    public static ArrayList readAllSelctdDataFromDB(Context ctx){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProPriceConstraints serCategory = null;
	    	ArrayList stocksSerArrList = new ArrayList();
	    	try{
	    		int[] dbIndex = new int[11];
	    		String[] dbValues = new String[11];
	    		int colId = -1;
	    		
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		
	    		Uri uri = Uri.parse(PriceListCP.PL_SEL_CONTENT_URI+"");				
				ContentResolver resolver = ctx.getContentResolver();				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MAKTX);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KBETR);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KONWA);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KPEIN);
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KMEIN);
				dbIndex[5] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MATNR);
				dbIndex[6] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT);
				dbIndex[7] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT);
				dbIndex[8] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL);
				dbIndex[9] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP);
				dbIndex[10] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[10]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					dbValues[5] = cursor.getString(dbIndex[5]);
					dbValues[6] = cursor.getString(dbIndex[6]);
					dbValues[7] = cursor.getString(dbIndex[4]);
					dbValues[8] = cursor.getString(dbIndex[5]);
					dbValues[9] = cursor.getString(dbIndex[6]);
					dbValues[10] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					serCategory = new SalesOrdProPriceConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
	    
	    /*public static ArrayList readAllSelctdIdDataFromDB(Context ctx,ArrayList selMattVector){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] projection=null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProPriceConstraints serCategory = null;
	    	SalesOrdProMattConstraints serCategory1 = null;
	    	ArrayList stocksSerArrList = new ArrayList();	    	
	    	String SelcItem="",delimiter=",",delim1="'";
	    			
	    	try{
	    		int[] dbIndex = new int[11];
	    		String[] dbValues = new String[11];
	    		int colId = -1;
	    		int sizearr=selMattVector.size();
	    		SalesOrderProConstants.showLog("sizearr : "+sizearr);
	    		String str=null;
	    		int i=0;
	    		
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		mDB = new SalesProPriceListDB(ctx);	
	    		
				SQLiteDatabase sqlDB = mDB.getWritableDatabase();
			
				
				for(i=0;i<sizearr-1;i++){
					serCategory=(SalesOrdProPriceConstraints)selMattVector.get(i);
					String item=serCategory.getMaterialNo().toString();
	        		SelcItem+=delim1+item+delim1+delimiter;	        			      		
				}
				
				if(i==sizearr)
					SelcItem = SelcItem.substring(0, SelcItem.length() - 1);//deletes the last character from the string "SelcItem"
				
				try {					
					cursor = sqlDB.rawQuery("select * from " + PriceListDBConstants.TABLE_SELECTED_MAT_LIST+" where "+PriceListDBConstants.PL_SEL_COL_MATNR+" in ("+SelcItem+")",null);
				} catch (Exception ee) {
					SalesOrderProConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesOrderProConstants.showLog("select * from " + PriceListDBConstants.TABLE_SELECTED_MAT_LIST+" where "+PriceListDBConstants.PL_SEL_COL_MATNR+" in ("+SelcItem+")");
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){					
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MAKTX);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KBETR);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KONWA);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KPEIN);
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KMEIN);
				dbIndex[5] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MATNR);
				dbIndex[6] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT);
				dbIndex[7] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT);
				dbIndex[8] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL);
				dbIndex[9] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP);
				dbIndex[10] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[10]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					dbValues[5] = cursor.getString(dbIndex[5]);
					dbValues[6] = cursor.getString(dbIndex[6]);
					dbValues[7] = cursor.getString(dbIndex[4]);
					dbValues[8] = cursor.getString(dbIndex[5]);
					dbValues[9] = cursor.getString(dbIndex[6]);
					dbValues[10] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					serCategory = new SalesOrdProPriceConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
					
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		    		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
*/	   
	    public static ArrayList readAllSelctdIdDataFromDB(Context ctx,ArrayList selMattVector){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] projection=null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProPriceConstraints priceCatgry=null;
	    	SalesOrdProMattConstraints serCategory = null;
	    	ArrayList stocksSerArrList = new ArrayList();	    	
	    	String SelcItem="",delimiter=",",delim1="'";
	    			
	    	try{
	    		int[] dbIndex = new int[18];
	    		String[] dbValues = new String[18];
	    		int colId = -1;
	    		int sizearr=selMattVector.size();
	    		
	    		String str=null;
	    		int i=0;
	    		
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		mDB = new SalesProPriceListDB(ctx);	
	    		
				SQLiteDatabase sqlDB = mDB.getWritableDatabase();
				
				for(i=0;i<sizearr;i++){
					serCategory = (SalesOrdProMattConstraints)selMattVector.get(i);
	        		SelcItem+=delim1+serCategory.getMaterialNo().toString().trim()+delim1+delimiter;
				}
				
				if(i==sizearr)
					SelcItem = SelcItem.substring(0, SelcItem.length() - 1);//deletes the last character from the string "SelcItem"
				
				try {					
					cursor = sqlDB.rawQuery("select * from " + PriceListDBConstants.TABLE_SELECTED_MAT_LIST+" where "+PriceListDBConstants.PL_SEL_COL_MATNR+" in ("+SelcItem+")",null);
				} catch (Exception ee) {
					SalesOrderProConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesOrderProConstants.showLog("select * from " + PriceListDBConstants.TABLE_SELECTED_MAT_LIST+" where "+PriceListDBConstants.PL_SEL_COL_MATNR+" in ("+SelcItem+")");
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){					
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MAKTX);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KBETR);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KONWA);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KPEIN);
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KMEIN);
				dbIndex[5] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MATNR);
				dbIndex[6] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT);
				dbIndex[7] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT);
				dbIndex[8] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL);
				dbIndex[9] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP);
				dbIndex[10] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[10]);
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
					dbValues[10] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]+":"+dbValues[2]+":"+dbValues[3]+":"+dbValues[4]);
					SalesOrderProConstants.showLog(dbValues[5]+" : "+dbValues[6]+":"+dbValues[7]+":"+dbValues[8]+":"+dbValues[9]);
					
					priceCatgry = new SalesOrdProPriceConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(priceCatgry);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
				
				
					
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		    		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
	    
	    public static ArrayList readAllSelctdidDataFromDB(Context ctx){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProPriceConstraints stkCategory = null;
	    	ArrayList stocksArrList = new ArrayList ();
	    	try{
	    		int[] dbIndex = new int[11];
	    		String[] dbValues = new String[11];
	    		int colId = -1;
	    		
	    		if(stocksArrList != null)
	    			stocksArrList.clear();
	    		
	    		Uri uri = Uri.parse(PriceListCP.PL_SEL_CONTENT_URI+"");
	    		 //Get the Resolver
				ContentResolver resolver = ctx.getContentResolver();				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MAKTX);
				dbIndex[1] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KBETR);
				dbIndex[2] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KONWA);
				dbIndex[3] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KPEIN);
				dbIndex[4] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KMEIN);
				dbIndex[5] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MATNR);
				dbIndex[6] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT);
				dbIndex[7] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT);
				dbIndex[8] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_KSCHL);
				dbIndex[9] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_PLTYP);
				dbIndex[10] = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[10]);
					dbValues[0] = cursor.getString(dbIndex[0]);
					dbValues[1] = cursor.getString(dbIndex[1]);
					dbValues[2] = cursor.getString(dbIndex[2]);
					dbValues[3] = cursor.getString(dbIndex[3]);
					dbValues[4] = cursor.getString(dbIndex[4]);
					dbValues[5] = cursor.getString(dbIndex[5]);
					dbValues[6] = cursor.getString(dbIndex[6]);
					dbValues[7] = cursor.getString(dbIndex[4]);
					dbValues[8] = cursor.getString(dbIndex[5]);
					dbValues[9] = cursor.getString(dbIndex[6]);
					dbValues[10] = String.valueOf(colId);
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProPriceConstraints(dbValues);
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesOrderProConstants.showErrorLog("Error in readAllSelctdidDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksArrList;
	    }//fn readAllSerchDataFromDB
	    
	    /*********************************************************************************************
	     *     	Database insert Related Functions
	     *********************************************************************************************/
	    
	    public static void insertSerchdDataInToDB(Context ctx, SalesOrdProMattConstraints stkCategory){
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
		    	
		    	val.put(PriceListDBConstants.PL_SER_COL_MATNR, mattNoStr );
		    	val.put(PriceListDBConstants.PL_SER_COL_MAKTX, mattDescStr);
		    	val.put(PriceListDBConstants.PL_SER_COL_MEINH, mattUnitStr);
		    	val.put(PriceListDBConstants.PL_SER_COL_MSEHT,mattUnitDescStr);
		    	resolver.insert(PriceListCP.PL_SER_CONTENT_URI, val);
	    	}
	    	catch(Exception sgh){
	    		SalesOrderProConstants.showErrorLog("Error in insertSerchdDataInToDB : "+sgh.toString());
	    	}
	    }//fn insertSerchdDataInToDB
	    
	    
	    
	    public static void insertselctdListDataInToDB(Context ctx, SalesOrdProPriceConstraints serCategory){
	    	try{
		    	ContentResolver resolver = ctx.getContentResolver();
		    	ContentValues val = new ContentValues();
		    	
		    	val.put(PriceListDBConstants.PL_SEL_COL_MAKTX, serCategory.getMattDesc());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KBETR, serCategory.getRateAmount());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KONWA, serCategory.getRateunit());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KPEIN, serCategory.getCondPricingUnit());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KMEIN, serCategory.getConditionUnit());
		    	val.put(PriceListDBConstants.PL_SEL_COL_MATNR, serCategory.getMaterialNo());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KSCHL_TEXT, serCategory.getKSCHLText());
		    	
		    	val.put(PriceListDBConstants.PL_SEL_COL_PLTYP_TEXT, serCategory.getPLTYPText());
		    	val.put(PriceListDBConstants.PL_SEL_COL_KSCHL, serCategory.getConditionType());
		    	val.put(PriceListDBConstants.PL_SEL_COL_PLTYP, serCategory.getPriceListType());
		    	
		    	resolver.insert(PriceListCP.PL_SEL_CONTENT_URI, val);
	    	}
	    	catch(Exception sgh){
	    		SalesOrderProConstants.showErrorLog("Error in insertselctdListDataInToDB : "+sgh.toString());
	    	}
	    }//fn insertselctdListDataInToDB   
	    
	    
	    /*********************************************************************************************
	     *     	Database deletion Related Functions
	     *********************************************************************************************/
	    //Delete all data from a selected table based on the specified Uri identifying a table
	    public static void deleteAllTableDataFromDB(Context ctx, Uri selUri){
	    	try{
	    		if(selUri != null){
		    		Uri uri = Uri.parse(selUri.toString());
					
					//Get the Resolver
					ContentResolver resolver = ctx.getContentResolver();
					
					//Make the invocation. # of rows deleted will be sent back
					int rows = resolver.delete(uri, null, null);
					
					SalesOrderProConstants.showLog("Rows Deleted : "+rows);
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesOrderProConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteAllCategoryDataFromDB
	    
	    public static void deleteSerchIdTableDataFromDB(Context ctx, Uri selUri,String idStr){
	    	try{
	    		if(selUri != null){
	    			Uri uri = Uri.parse(selUri.toString());    
	    		    //Get the Resolver
	    		    ContentResolver resolver = ctx.getContentResolver();  
	    		    String delWhere = PriceListDBConstants.PL_SER_COL_MATNR + " = ?"; 
	    		    String[] delWhereParams = new String[]{idStr};
	    		    //Make the invocation. # of rows deleted will be sent back
	    		    resolver.delete(uri, delWhere, delWhereParams);
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesOrderProConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteIdTableDataFromDB
	    
	    public static void deleteIdselctdTableDataFromDB(Context ctx,Uri selUri,String idStr1){
	    	try{
	    		if(selUri != null){
	    			Uri uri = Uri.parse(selUri.toString());    
	    		    //Get the Resolver
	    		    ContentResolver resolver = ctx.getContentResolver();  
	    		    String delWhere = PriceListDBConstants.PL_SEL_COL_MATNR + " = ?"; 
	    		    String[] delWhereParams = new String[]{idStr1};
	    		    //Make the invocation. # of rows deleted will be sent back
	    		    resolver.delete(uri, delWhere, delWhereParams);	    		
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesOrderProConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteIdTableDataFromDB
	    
	    /*********************************************************************************************
	     *     	Compare and Getting Selected ID Related Functions
	     *********************************************************************************************/
	    public static ArrayList getDBlist(Context ctx){
	    	 ArrayList  resArray = new ArrayList();
	    	 Cursor cursor = null;
	    	try {
	    		String selection = null;
		    	String[] selectionParams = null;
		    	String orderBy = null;
	    		
                Uri uri = Uri.parse(PriceListCP.PL_SER_CONTENT_URI+"");				
				ContentResolver resolver = ctx.getContentResolver();				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				cursor.moveToFirst();
				
			     do {
			    	 int index0 = cursor.getColumnIndex(PriceListDBConstants.PL_SER_COL_MATNR);
		     		 String MATNR = cursor.getString(index0);		
		     		 resArray.add(MATNR);
		     		 SalesOrderProConstants.showLog("resArray:"+ resArray);
		     		 cursor.moveToNext();
			     } while (!cursor.isAfterLast());
				    
				SalesOrderProConstants.showLog("resArray:"+resArray.size());				
				cursor.close();
	    	} catch (Exception e) {
	    		SalesOrderProConstants.showErrorLog("Error in getDBlist:"+e.toString());
				
			}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
			return resArray;   
	    }//fn getDBlist 
	    
	    public static ArrayList getDBselctdIdlist(Context ctx){
	    	 ArrayList  resselctdArray = new ArrayList();
	    	 Cursor cursor = null;
	    	try {
	    		String selection = null;
		    	String[] selectionParams = null;
		    	String orderBy = null;	    		
		    	
		    	Uri uri = Uri.parse(PriceListCP.PL_SEL_CONTENT_URI+"");				
				ContentResolver resolver = ctx.getContentResolver();				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				cursor.moveToFirst();				
			     do {
			    	 int index0 = cursor.getColumnIndex(PriceListDBConstants.PL_SEL_COL_MATNR);//get index of "material number" field
		     		 String MaterialNo = cursor.getString(index0);	//String value of field	     
		     		 resselctdArray.add(MaterialNo);//add each field value in column to arraylist
		     		 SalesOrderProConstants.showLog("resArray:"+ resselctdArray);
		     		 cursor.moveToNext();
			     } while (!cursor.isAfterLast());				    
				 SalesOrderProConstants.showLog("resArray:"+resselctdArray.size());
	    	} catch (Exception e) {
	    		SalesOrderProConstants.showErrorLog("Error in getDBselctdIdlist:"+e.toString());
				
			}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
			return resselctdArray;   
	    }//fn getDBlist 

}//endof PriceListDBOperations
