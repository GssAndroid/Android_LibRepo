package com.globalsoft.SalesPro.Database;

import java.util.ArrayList;

import com.globalsoft.SalesPro.Constraints.CustomerListOpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProCustConstraints;


import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class CustCreditDBOperations {
	
	 public static ArrayList readAllSerchDataFromDB(Context ctx){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProCustConstraints stkCategory = null;
	    	ArrayList stocksArrList = new ArrayList();
	    	try{
	    		int[] dbIndex = new int[11];
	    		String[] dbValues = new String[11];
	    		int colId = -1;
	    		
	    		if(stocksArrList != null)
	    			stocksArrList.clear();
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SER_CONTENT_URI+"");
				
				ContentResolver resolver = ctx.getContentResolver();
				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_KUNNR);
				dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_NAME1);
				dbIndex[2] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_LAND1);
				dbIndex[3] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_REGIO);
				dbIndex[4] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_ORT01);
				dbIndex[5] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_STRAS);
				dbIndex[6] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_TELF1);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_TELF2);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_SMTP_ADDR);
				
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_ID);
				
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
					
					SalesOrderProConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProCustConstraints(dbValues);
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
	    
	    
	    public static ArrayList readAllSelctdDataFromDB(Context ctx){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	CustomerListOpConstraints serCategory = null;
	    	ArrayList stocksSerArrList = new ArrayList();
	    	try{
	    		int[] dbIndex = new int[8];
	    		String[] dbValues = new String[8];
	    		int colId = -1;
	    		
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SEL_CONTENT_URI+"");
				
				ContentResolver resolver = ctx.getContentResolver();
				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesOrderProConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_NAME1);
				dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KBETR);
				dbIndex[2] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KONWA);
				dbIndex[3] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KPEIN);
				dbIndex[4] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KMEIN);
				dbIndex[5] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_MATNR);
				dbIndex[6] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KSCHL_TEXT);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_PLTYP_TEXT);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KSCHL);
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_PLTYP);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ORT01);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_REGIO);
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_LAND1);
				
				dbIndex[10] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ID);
				
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
					
					serCategory = new CustomerListOpConstraints(dbValues);
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
	    
	    
	   
	    
	    /*********************************************************************************************
	     *     	Database insert Related Functions
	     *********************************************************************************************/
	    
	    public static void insertSerchdDataInToDB(Context ctx, SalesOrdProCustConstraints stkCategory){
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
		    	
		    	val.put(CustCreditDBConstants.CUS_SER_COL_KUNNR, custno );
		    	val.put(CustCreditDBConstants.CUS_SER_COL_NAME1, custname);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_LAND1, custcountry);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_REGIO,custregn);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_ORT01, custcity );
		    	val.put(CustCreditDBConstants.CUS_SER_COL_STRAS, custstreet);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_TELF1, custtelno1);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_TELF2,custtelno2);
		    	val.put(CustCreditDBConstants.CUS_SER_COL_SMTP_ADDR,custemail);
		    	
		    	resolver.insert(SalesProCustCreditCP.CUS_SER_CONTENT_URI, val);
	    	}
	    	catch(Exception sgh){
	    		SalesOrderProConstants.showErrorLog("Error in insertSerchdDataInToDB : "+sgh.toString());
	    	}
	    }//fn insertSerchdDataInToDB
	    
	    
	    
	    public static void insertselctdListDataInToDB(Context ctx, CustomerListOpConstraints serCategory){
	    	try{
		    	ContentResolver resolver = ctx.getContentResolver();
		    	ContentValues val = new ContentValues();
		    	
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_NAME1, serCategory.getName());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KBETR, serCategory.getBlockedByCreditMgmt());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KONWA, serCategory.getRiskCategory());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KPEIN, serCategory.getCustCreditLimit());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KMEIN, serCategory.getCreditExposure());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_MATNR, serCategory.getCurrency());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_PLTYP_TEXT, serCategory.getCreditLimitUsed());
		    	
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KSCHL, serCategory.getRating());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_PLTYP, serCategory.getCustomerNo1());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_ORT01, serCategory.getRiskClassName());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_REGIO, serCategory.getCity());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_LAND1, serCategory.getRegion());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_LAND1, serCategory.getCountry());
		    	
		    	resolver.insert(SalesProCustCreditCP.CUS_SEL_CONTENT_URI, val);
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

}//endof PriceListDBOperations
