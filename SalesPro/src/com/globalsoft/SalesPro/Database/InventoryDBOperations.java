package com.globalsoft.SalesPro.Database;

import java.util.ArrayList;

import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProPriceConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProStockConstraints;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class InventoryDBOperations {
	private static SQLiteDatabase sqlitedatabase;
	private static SalesProInvntryDB mDB;
	
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
    		
    		Uri uri = Uri.parse(SalesProInvntryCP.IL_SER_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MATNR);
			dbIndex[1] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MAKTX);
			dbIndex[2] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MEINH);
			dbIndex[3] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MSEHT);			
			dbIndex[4] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_ID);
			
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
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList ();
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProInvntryCP.IL_SER_CONTENT_URI+"");
			ContentResolver resolver = ctx.getContentResolver();
			
			String sa1 = "%"+SelStr1+"%"; // contains an "input String"
    		SalesOrderProConstants.showLog("String value : "+sa1);
    		cursor = resolver.query(uri, null,SalesProInvntryDBConstants.IL_SER_COL_MATNR+ " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
						
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MATNR);
			dbIndex[1] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MAKTX);
			dbIndex[2] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MEINH);
			dbIndex[3] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MSEHT);			
			dbIndex[4] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_ID);
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
	 
    /*public static ArrayList readAllSelctdDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProPriceConstraints serCategory = null;
    	ArrayList stocksSerArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[8];
    		String[] dbValues = new String[8];
    		int colId = -1;
    		
    		if(stocksSerArrList != null)
    			stocksSerArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProInvntryCP.IL_SEL_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksSerArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MAKTX);
			dbIndex[1] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MEINS);
			dbIndex[2] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LABST);
			dbIndex[3] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_UMLME);
			dbIndex[4] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_INSME);
			dbIndex[5] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MATNR);
			dbIndex[6] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS_TEXT);
			dbIndex[7] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGOBE);
			dbIndex[8] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS);
			dbIndex[9] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGORT);
			dbIndex[10] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_ID);
			
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
	//new read function for selectd data offline
    public static ArrayList readAllSelctdidDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProStockConstraints stkCategory = null;
    	ArrayList stocksArrList = new ArrayList ();
    	
    	try{
    		int[] dbIndex = new int[11];
    		String[] dbValues = new String[11];
    		int colId = -1;
    		
    		if(stocksArrList != null)
    			stocksArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProInvntryCP.IL_SEL_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return stocksArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MAKTX);
			dbIndex[1] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MEINS);
			dbIndex[2] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LABST);
			dbIndex[3] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_UMLME);
			dbIndex[4] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_INSME);
			dbIndex[5] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MATNR);
			dbIndex[6] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS_TEXT);
			dbIndex[7] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGOBE);
			dbIndex[8] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS);
			dbIndex[9] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGORT);
			dbIndex[10] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_ID);			
			
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
				
				stkCategory = new SalesOrdProStockConstraints(dbValues);
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
	   
    public static ArrayList readAllSelctdIDDataFromDB(Context ctx,ArrayList stockArrList){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesOrdProMattConstraints serCategory = null;
    	SalesOrdProStockConstraints stkDeatailCategory = null;
    	ArrayList stocksSerArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[15];
    		String[] dbValues = new String[15];
    		int colId = -1,i=0,sizearr;
    		sizearr=stockArrList.size();
    		String SelcItem="",delimiter=",",delim1="'";

    		if(stocksSerArrList != null)
    			stocksSerArrList.clear();	    		
    		
    		mDB = new SalesProInvntryDB(ctx);	
            SQLiteDatabase sqlDB = mDB.getWritableDatabase();
			
			for(i=0;i<sizearr;i++){
				serCategory =(SalesOrdProMattConstraints)stockArrList.get(i);
        		SelcItem+=delim1+serCategory.getMaterialNo().toString()+delim1+delimiter;	        		
			}
			if(i==sizearr)
				SelcItem = SelcItem.substring(0, SelcItem.length() - 1);
			
			try {
				//get the selected rows from database
				cursor = sqlDB.rawQuery("select * from " +SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST+" where "+SalesProInvntryDBConstants.IL_SEL_COL_MATNR+" in ("+SelcItem+")",null);
		    } catch (Exception ee) {
		    	SalesOrderProConstants.showErrorLog("Error in rawQuery : "+ee.toString());
		    }
							
		    SalesOrderProConstants.showLog("select * from " +SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST+" where "+SalesProInvntryDBConstants.IL_SEL_COL_MATNR+" in ("+SelcItem+")");
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
		
		    if(cursor.getCount() == 0){
		    	return stocksSerArrList;
		    }
		
			dbIndex[0] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MAKTX);
			dbIndex[1] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MEINS);
			dbIndex[2] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LABST);
			dbIndex[3] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_UMLME);
			dbIndex[4] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_INSME);
			dbIndex[5] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MATNR);
			dbIndex[6] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS_TEXT);
			dbIndex[7] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGOBE);
			dbIndex[8] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_WERKS);
			dbIndex[9] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_LGORT);
			dbIndex[10] = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_ID);
			
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
				
				SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
				
				stkDeatailCategory = new SalesOrdProStockConstraints(dbValues);
				if(stocksSerArrList != null)
					stocksSerArrList.add(stkDeatailCategory);
								
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
	    	
	    	val.put(SalesProInvntryDBConstants.IL_SER_COL_MATNR, mattNoStr );
	    	val.put(SalesProInvntryDBConstants.IL_SER_COL_MAKTX, mattDescStr);
	    	val.put(SalesProInvntryDBConstants.IL_SER_COL_MEINH, mattUnitStr);
	    	val.put(SalesProInvntryDBConstants.IL_SER_COL_MSEHT,mattUnitDescStr);
	    	resolver.insert(SalesProInvntryCP.IL_SER_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SalesOrderProConstants.showErrorLog("Error in insertSerchdDataInToDB : "+sgh.toString());
    	}
    }//fn insertSerchdDataInToDB
    
    public static void insertselctdListDataInToDB(Context ctx, SalesOrdProStockConstraints stkcategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_MAKTX, stkcategory.getMattDesc());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_MEINS, stkcategory.getMeasureUnit());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_LABST, stkcategory.getStock());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_UMLME, stkcategory.getStockInTransfer());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_INSME, stkcategory.getStockInQualityInsp());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_MATNR, stkcategory.getMaterialNo());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_WERKS_TEXT, stkcategory.getPlantDesc());	    	
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_LGOBE, stkcategory.getStorageLocationDesc());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_WERKS, stkcategory.getPlant());
	    	val.put(SalesProInvntryDBConstants.IL_SEL_COL_LGORT, stkcategory.getStorageLocation());
	    	
	    	resolver.insert(SalesProInvntryCP.IL_SEL_CONTENT_URI, val);
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
    
    public static void deleteIdTableDataFromDB(Context ctx, Uri selUri,String idStr){
    	try{
    		if(selUri != null){
    			Uri uri = Uri.parse(selUri.toString());    
    		    //Get the Resolver
    		    ContentResolver resolver = ctx.getContentResolver();  
    		    String delWhere = SalesProInvntryDBConstants.IL_SER_COL_MATNR + " = ?"; 
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
    		    String delWhere = SalesProInvntryDBConstants.IL_SEL_COL_MATNR + " = ?"; 
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
     *     	Compare Related Functions
     *********************************************************************************************/
    public static ArrayList getDBlist(Context ctx){
    	 ArrayList  resArray = new ArrayList();
    	 Cursor cursor = null;
    	 try {
    		String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;	    		
            Uri uri = Uri.parse(SalesProInvntryCP.IL_SER_CONTENT_URI+"");				
			ContentResolver resolver = ctx.getContentResolver();				
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
			SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());				
			cursor.moveToFirst();				
		    do {
		    	int index0 = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SER_COL_MATNR);
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
    				    	
	    	 Uri uri = Uri.parse(SalesProInvntryCP.IL_SEL_CONTENT_URI+"");				
	    	 ContentResolver resolver = ctx.getContentResolver();				
	    	 cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
	    	 SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());				
	    	 cursor.moveToFirst();
			
		     do {
		    	 int index0 = cursor.getColumnIndex(SalesProInvntryDBConstants.IL_SEL_COL_MATNR);
	     		 String MaterialNo = cursor.getString(index0);		     		 
	     		 resselctdArray.add(MaterialNo);
	     		 SalesOrderProConstants.showLog("resArray:"+ resselctdArray);
	     		 cursor.moveToNext();
		     } while (!cursor.isAfterLast());
			    
			 SalesOrderProConstants.showLog("resArray:"+resselctdArray.size());				
			 cursor.close();
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
