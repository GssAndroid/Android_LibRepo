package com.globalsoft.SalesProCustActivityLib.Database;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.globalsoft.SalesProCustActivityLib.Constraints.CustomerCntxOpConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.CustomerListOpConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.SalesOrdProCustConstraints;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;

public class CustCreditDBOperations {

	private static SQLiteDatabase sqlitedatabase;
	private static CustCreditDB mDB;
	
	public static String readScreenTitleFromDB(Context ctx, String cntx2){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String scrTitle = "";
    	try{
    		int[] dbIndex = new int[2];
    		String[] dbValues = new String[2];    		    		
    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_CNTX_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT2 + " = ? and "+CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ?"; 
			selectionParams = new String[]{cntx2, CustCreditDBConstants.COLUNM_NAME_SCR_TITLE};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SalesProCustActivityConstants.showLog("No of Records for StatusList : "+cursor.getCount());
						
			if(cursor.getCount() == 0){
				return scrTitle;
			}
			
			dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_VALUE);
			cursor.moveToFirst();
			dbValues[0] = cursor.getString(dbIndex[0]);								
			if(dbValues[0] != null)
				scrTitle = dbValues[0].toString().trim();	
			
			/*mDB = new CustCreditDB(ctx);	
			SQLiteDatabase sqlDB = mDB.getWritableDatabase();
			Cursor ti = sqlDB.rawQuery("PRAGMA table_info("+CustCreditDBConstants.TABLE_CUS_CNTX+")", null);
			if ( ti.moveToFirst() ) {
			    do {
			        System.out.println("col: " + ti.getString(1));
			    } while (ti.moveToNext());
			}
			mDB.close();
			sqlDB.close();*/
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in readScreenTitleFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return scrTitle;
    }//fn readScreenTitleFromDB
	
	public static int readRecordCountFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	int rowCount = 0;
    	try{  		    		
    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_CNTX_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ? "; 
			selectionParams = new String[]{CustCreditDBConstants.CNTXT4_ATTR_TAG};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SalesProCustActivityConstants.showLog("No of Records for StatusList : "+cursor.getCount());
			rowCount = cursor.getCount();
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in readRecordCountFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return rowCount;
    }//fn readRecordCountFromDB
	
	public static ArrayList readAllLablesFromDB(Context ctx){
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
    		
    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_CNTX_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ? "; 
			selectionParams = new String[]{CustCreditDBConstants.CNTXT4_ATTR_TAG};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);		
			SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return lablesArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_NAME);			
			cursor.moveToFirst();			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				colValue = dbValues[0];		
				//SalesProCustActivityConstants.showLog("colValue : "+colValue);
				selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ? and "+CustCreditDBConstants.CUS_CNTX_COL_NAME + " = ?";
				selectionParams = new String[]{CustCreditDBConstants.CNTXT3_LABE_TAG, colValue};			
				cursor2 = resolver.query(uri, null, selection, selectionParams, orderBy);	
				labelValue = "";
				if(cursor2.getCount() == 0){
					labelValue = "";
				}else{					
					dbIndex1[0] = cursor2.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_VALUE);			
					cursor2.moveToFirst();
					dbValues1[0] = cursor2.getString(dbIndex1[0]);								
					if(dbValues1[0] != null)
						labelValue = dbValues1[0].toString().trim();
				}

				SalesProCustActivityConstants.showLog("labelValue : "+labelValue);
				if(labelValue != null)
					lablesArrList.add(labelValue);
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in readAllLablesFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return lablesArrList;
    }//fn readAllLablesFromDB
	
	public static ArrayList readAllFieldValueFromDB(Context ctx){
    	Cursor cursor = null, cursor2 = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	String colValue = "", valueData = "", desVal = "";
    	String fieldValue = "", nameVal = "";
    	ArrayList fieldValueArrList = new ArrayList();
    	try{
    		int[] dbIndex = new int[2];
    		String[] dbValues = new String[2];
    		int[] dbIndex1 = new int[2];
    		String[] dbValues1 = new String[2];
    		
    		if(fieldValueArrList != null)
    			fieldValueArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_CNTX_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ? "; 
			selectionParams = new String[]{CustCreditDBConstants.CNTXT4_ATTR_TAG};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);		
			SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return fieldValueArrList;
			}
			
			dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_NAME);
			dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_VALUE);			
			cursor.moveToFirst();			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);	
				dbValues[1] = cursor.getString(dbIndex[1]);
				//colValue = dbValues[0];
				if(dbValues[0] != null){
					nameVal = dbValues[0].toString().trim();
					String strVal = "";
					if(nameVal.indexOf("-") >= 0){
						String[] separated = nameVal.split("-");
						if(separated != null && separated.length > 0){
							strVal = separated[1];
						}
					}else{
						strVal = nameVal;
					}
					colValue = strVal;
				}
				
				valueData = dbValues[1];	
				//SalesProCustActivityConstants.showLog("colValue : "+colValue);
				//SalesProCustActivityConstants.showLog("valueData : "+valueData);
				//SalesProCustActivityConstants.showLog("valueData : "+CustCreditDBConstants.VALUE_DESCRIBED_TAG);
				fieldValue = "";
				if(valueData.equals(CustCreditDBConstants.VALUE_DESCRIBED_TAG)){
					fieldValue = colValue;
					selection = CustCreditDBConstants.CUS_CNTX_COL_CNTXT4 + " = ? and "+
								CustCreditDBConstants.CUS_CNTX_COL_NAME + " = ?";
					selectionParams = new String[]{CustCreditDBConstants.CNTXT4_ATTR_TAG, nameVal};			
					cursor2 = resolver.query(uri, null, selection, selectionParams, orderBy);	
					//SalesProCustActivityConstants.showLog("cursor2 : "+cursor2.getCount());
					desVal = "";
					if(cursor2.getCount() == 0){
						desVal = "";
					}else{					
						dbIndex1[0] = cursor2.getColumnIndex(CustCreditDBConstants.CUS_CNTX_COL_TRGTNAME);			
						cursor2.moveToFirst();
						dbValues1[0] = cursor2.getString(dbIndex1[0]);								
						if(dbValues1[0] != null){
							//desVal = dbValues1[0].toString().trim();
							//SalesProCustActivityConstants.showLog("dbValues1 : "+dbValues1[0].toString().trim());
							String nameVal1 = dbValues1[0].toString().trim();
							String strVal = "";
							if(nameVal1.indexOf("-") >= 0){
								String[] separated = nameVal1.split("-");
								if(separated != null && separated.length > 0){
									strVal = separated[1];
								}
							}else{
								strVal = nameVal1;
							}
							desVal = strVal;
						}						
					}
					if(desVal != null && desVal.length() > 0){
						fieldValue = fieldValue+":"+desVal;
					}
				}else{
					fieldValue = colValue;
				}				
				if(fieldValue != null){
					SalesProCustActivityConstants.showLog("fieldValue : "+fieldValue);
					fieldValueArrList.add(fieldValue);
				}
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in readAllFieldValueFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return fieldValueArrList;
    }//fn readAllFieldValueFromDB
	
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
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
				
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProCustConstraints(dbValues);
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSerchDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksArrList;
	    }//fn readAllSerchDataFromDB
	    
	 public static ArrayList readAllSelctdDataFromDB(Context ctx,ArrayList selMattVector){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	CustomerListOpConstraints serCategory = null;
	    	ArrayList stocksSerArrList = new ArrayList();
	    	try{
	    		int[] dbIndex = new int[20];
	    		String[] dbValues = new String[20];	    		
	    		//int arrsize=dbValues.length();
	    		int colId = -1,i=0;
	    		int sizearr=selMattVector.size();
	    		/*delimiter=",",delim1="'";
	    		int sizearr=selMattVector.size();*/
	    		String SelcItem="";
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		
	    		for(i=0;i<sizearr;i++){
	        		SelcItem+=selMattVector.get(i).toString().trim();
				}
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SEL_CONTENT_URI+"");
			
				ContentResolver resolver = ctx.getContentResolver();
				 selection = CustCreditDBConstants.CUS_SER_COL_KUNNR +" = ?";
		        selectionParams = new String[]{SelcItem};
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
             /*SQLiteDatabase sqlDB = mDB.getWritableDatabase();
             SelcItem=idstr.toString().trim();*/
				/*for(i=0;i<sizearr;i++){
	        		SelcItem+=delim1+selMattVector.get(i).toString()+delim1+delimiter;
	        		
	        		Log.e("info","selected string : "+SelcItem);
				}
				if(i==sizearr)
					SelcItem = SelcItem.substring(0, SelcItem.length() - 1);*/
				
				/*try {
					cursor = sqlDB.rawQuery("select * from " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST+" where "+CustCreditDBConstants.CUS_SEL_COL_KUNNR+ "='"+SelcItem+"')",null);
				} catch (Exception ee) {
					SalesProCustActivityConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesProCustActivityConstants.showLog("select * from " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST+" where "+CustCreditDBConstants.CUS_SEL_COL_KUNNR+"="+SelcItem+")");*/
				if(cursor.getCount() == 0){
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_NAME1);
				dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CRBLB);
				dbIndex[2] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC);
				dbIndex[3] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLIMK);
				dbIndex[4] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_OBLIG);
				dbIndex[5] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_WAERS);
				dbIndex[6] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLPRZ);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_DBRTG);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KUNNR);
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC_TEXT);
				dbIndex[10] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ORT01);
				dbIndex[11] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_REGIO);
				dbIndex[12] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_LAND1);
				
				dbIndex[13] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[13]);
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					serCategory = new CustomerListOpConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
	    /*public static ArrayList readAllSelctdDataFromDB(Context ctx){
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
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
				
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					serCategory = new CustomerListOpConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
*/	    
	    public static ArrayList readAllSelctdidDataFromDB(Context ctx){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	CustomerListOpConstraints stkCategory = null;
	    	ArrayList stocksArrList = new ArrayList ();
	    	//String[] pricelistString = new String[20];
	    	try{
	    		int[] dbIndex = new int[20];
	    		String[] dbValues = new String[20];
	    		int colId = -1;
	    		
	    		if(stocksArrList != null)
	    			stocksArrList.clear();
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SEL_CONTENT_URI+"");
				
				ContentResolver resolver = ctx.getContentResolver();
				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
				
				if(cursor.getCount() == 0){
					return stocksArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_NAME1);
				dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CRBLB);
				dbIndex[2] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC);
				dbIndex[3] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLIMK);
				dbIndex[4] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_OBLIG);
				dbIndex[5] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_WAERS);
				dbIndex[6] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLPRZ);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_DBRTG);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KUNNR);
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC_TEXT);
				dbIndex[10] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ORT01);
				dbIndex[11] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_REGIO);
				dbIndex[12] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_LAND1);
				
				dbIndex[13] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ID);
				
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[13]);
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new CustomerListOpConstraints(dbValues);
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					//pricelistString = (String[])stocksArrList.ToArray(typeof(pricelistString));
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSelctdidDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksArrList;
	    }//fn readAllSerchDataFromDB
	    
	    public static ArrayList readAllSelctdDataFromDB(Context ctx,String idstr){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	CustomerListOpConstraints serCategory = null;
	    	ArrayList stocksSerArrList = new ArrayList();
	    	try{
	    		int[] dbIndex = new int[20];
	    		String[] dbValues = new String[20];
	    		//int arrsize=dbValues.length();
	    		int colId = -1,i=0;
	    		/*delimiter=",",delim1="'";
	    		int sizearr=selMattVector.size();*/
	    		String SelcItem="";
	    		if(stocksSerArrList != null)
	    			stocksSerArrList.clear();
	    		
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SEL_CONTENT_URI+"");
			
				ContentResolver resolver = ctx.getContentResolver();
				 selection = CustCreditDBConstants.CUS_SER_COL_KUNNR +" = ?";
 		        selectionParams = new String[]{idstr};
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
                /*SQLiteDatabase sqlDB = mDB.getWritableDatabase();
                SelcItem=idstr.toString().trim();*/
				/*for(i=0;i<sizearr;i++){
	        		SelcItem+=delim1+selMattVector.get(i).toString()+delim1+delimiter;
	        		
	        		Log.e("info","selected string : "+SelcItem);
				}
				if(i==sizearr)
					SelcItem = SelcItem.substring(0, SelcItem.length() - 1);*/
				
				/*try {
					cursor = sqlDB.rawQuery("select * from " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST+" where "+CustCreditDBConstants.CUS_SEL_COL_KUNNR+ "='"+SelcItem+"')",null);
				} catch (Exception ee) {
					SalesProCustActivityConstants.showErrorLog("Error in rawQuery : "+ee.toString());
				}
								
				SalesProCustActivityConstants.showLog("select * from " + CustCreditDBConstants.TABLE_SELECTED_CUS_LIST+" where "+CustCreditDBConstants.CUS_SEL_COL_KUNNR+"="+SelcItem+")");*/
				if(cursor.getCount() == 0){
					return stocksSerArrList;
				}
				
				dbIndex[0] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_NAME1);
				dbIndex[1] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CRBLB);
				dbIndex[2] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC);
				dbIndex[3] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLIMK);
				dbIndex[4] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_OBLIG);
				dbIndex[5] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_WAERS);
				dbIndex[6] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KLPRZ);
				dbIndex[7] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_DBRTG);
				dbIndex[8] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_KUNNR);
				dbIndex[9] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_CTLPC_TEXT);
				dbIndex[10] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ORT01);
				dbIndex[11] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_REGIO);
				dbIndex[12] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_LAND1);
				
				dbIndex[13] = cursor.getColumnIndex(CustCreditDBConstants.CUS_SEL_COL_ID);
				
				cursor.moveToFirst();
				
				do{
					colId = cursor.getInt(dbIndex[13]);
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					serCategory = new CustomerListOpConstraints(dbValues);
					if(stocksSerArrList != null)
						stocksSerArrList.add(serCategory);
					
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSelctdDataFromDB : "+sfg.toString());
	    	}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
	    	return stocksSerArrList;
	    }//fn readAllSelctdDataFromDB
	    
	    public static ArrayList readAllSerchIdDataFromDB(Context ctx,String SelStr1){
	    	Cursor cursor = null;
	    	String selection = null;
	    	String[] selectionParams = null;
	    	String orderBy = null;
	    	SalesOrdProCustConstraints stkCategory = null;
	    	ArrayList stocksArrList = new ArrayList ();
	    	try{
	    		int[] dbIndex = new int[11];
	    		String[] dbValues = new String[11];
	    		int colId = -1;
	    		
	    		if(stocksArrList != null)
	    			stocksArrList.clear();
	    		
	    		Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SER_CONTENT_URI+"");
				ContentResolver resolver = ctx.getContentResolver();
				
				String sa1 = "%"+SelStr1+"%"; // contains an "input String"
	    		SalesProCustActivityConstants.showLog("String value : "+sa1);
	    		cursor = resolver.query(uri, null,CustCreditDBConstants.CUS_SER_COL_NAME1 + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
				
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
					
					SalesProCustActivityConstants.showLog("Id : "+colId+" : "+dbValues[0]+" : "+dbValues[1]);
					
					stkCategory = new SalesOrdProCustConstraints(dbValues);
					if(stocksArrList != null)
						stocksArrList.add(stkCategory);
					
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    	}
	    	catch(Exception sfg){
	    		SalesProCustActivityConstants.showErrorLog("Error in readAllSerchIdDataFromDB : "+sfg.toString());
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
	    	       
	    public static void insertCntxListDataInToDB(Context ctx, CustomerCntxOpConstraints cntxcategory){
	    	try{
		    	ContentResolver resolver = ctx.getContentResolver();
		    	ContentValues val = new ContentValues();
		    			        
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_CNTXT2, cntxcategory.getCntxt2().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_CNTXT3, cntxcategory.getCntxt3().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_CNTXT4, cntxcategory.getCntxt4().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_NAME, cntxcategory.getName().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_DPNDNCY, cntxcategory.getDpndncy().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_SEQNR, cntxcategory.getSeqNR().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_TYPE, cntxcategory.getType().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_SIGN, cntxcategory.getSign().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_OPTN, cntxcategory.getOptn().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_VALUE, cntxcategory.getValue().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_VALUE_HIGH, cntxcategory.getValueHigh().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_TRGTNAME, cntxcategory.getTrgtName().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_CNTX_COL_TRGTVALUE, cntxcategory.getTrgtValue().toString().trim());
		    	
		    	resolver.insert(SalesProCustCreditCP.CUS_CNTX_CONTENT_URI, val);
	    	}
	    	catch(Exception sgh){
	    		SalesProCustActivityConstants.showErrorLog("Error in insertCntxListDataInToDB : "+sgh.toString());
	    	}
	    }//fn insertCntxListDataInToDB
	    
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
	    		SalesProCustActivityConstants.showErrorLog("Error in insertSerchdDataInToDB : "+sgh.toString());
	    	}
	    }//fn insertSerchdDataInToDB
	    
	    
	    
	    public static void insertselctdListDataInToDB(Context ctx, CustomerListOpConstraints serCategory){
	    	try{
		    	ContentResolver resolver = ctx.getContentResolver();
		    	ContentValues val = new ContentValues();
		    	
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_NAME1, serCategory.getName().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_CRBLB, serCategory.getBlockedByCreditMgmt().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_CTLPC, serCategory.getRiskCategory().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KLIMK, serCategory.getCustCreditLimit().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_OBLIG, serCategory.getCreditExposure().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_WAERS, serCategory.getCurrency().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KLPRZ, serCategory.getCreditLimitUsed().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_DBRTG, serCategory.getRating().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_KUNNR, serCategory.getCustomerNo1().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_CTLPC_TEXT, serCategory.getRiskClassName().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_ORT01, serCategory.getCity().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_REGIO, serCategory.getRegion().toString().trim());
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_LAND1, serCategory.getCountry().toString().trim());
		    	
		    	/*val.put(CustCreditDBConstants.CUS_SEL_COL_NAME1, serCategory.getName());
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
		    	val.put(CustCreditDBConstants.CUS_SEL_COL_LAND1, serCategory.getCountry());*/
		    	
		    	resolver.insert(SalesProCustCreditCP.CUS_SEL_CONTENT_URI, val);
	    	}
	    	catch(Exception sgh){
	    		SalesProCustActivityConstants.showErrorLog("Error in insertselctdListDataInToDB : "+sgh.toString());
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
					
					SalesProCustActivityConstants.showLog("Rows Deleted : "+rows);
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesProCustActivityConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteAllCategoryDataFromDB
	    
	    public static void deleteIdTableDataFromDB(Context ctx, Uri selUri,String idStr){
	    	try{
	    		if(selUri != null){
	    			Uri uri = Uri.parse(selUri.toString());    
	    		    //Get the Resolver
	    		    ContentResolver resolver = ctx.getContentResolver();  
	    		    String delWhere = CustCreditDBConstants.CUS_SER_COL_KUNNR +" = ?";
	    		        String[] delWhereParams = new String[]{idStr};
	    		        SalesProCustActivityConstants.showLog("value of idStr : " +idStr);
	    		    //Make the invocation. # of rows deleted will be sent back
	    		   int k= resolver.delete(uri, delWhere, delWhereParams);
	    		   SalesProCustActivityConstants.showLog("value of k : " +k);
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesProCustActivityConstants.showErrorLog("Error in deleteIdTableDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteIdTableDataFromDB
	    
	    public static void deleteIdselctdTableDataFromDB(Context ctx,Uri selUri,String idStr1){
	    	try{
	    		
	    		if(selUri != null){
	    			Uri uri = Uri.parse(selUri.toString());    
	    		    //Get the Resolver
	    		    ContentResolver resolver = ctx.getContentResolver();  
	    		    String delWhere = CustCreditDBConstants.CUS_SER_COL_KUNNR+ " = ?"; 
	    		        String[] delWhereParams = new String[]{idStr1};
	    		    //Make the invocation. # of rows deleted will be sent back
	    		   int j= resolver.delete(uri, delWhere, delWhereParams);//Get the count of deleted rows
	    		    SalesProCustActivityConstants.showLog("value of k : " +j);
	    		}
	    	}
	    	catch(Exception sggh){
	    		SalesProCustActivityConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteIdTableDataFromDB
	    
	    public static void deleteCntxDataFromDB(Context ctx, Uri selUri){
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
	    		SalesProCustActivityConstants.showErrorLog("Error in deleteCntxDataFromDB : "+sggh.toString());
	    	}
	    }//fn deleteCntxDataFromDB
	    
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
	    		
                Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SER_CONTENT_URI+"");				
				ContentResolver resolver = ctx.getContentResolver();				
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());
				
				cursor.moveToFirst();				
			    do {
			    	int index0 = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_KUNNR);
			    	String KUNNR = cursor.getString(index0).toString().trim();	
			    	resArray.add(KUNNR);
			    	SalesProCustActivityConstants.showLog("KUNNR: "+KUNNR);
			    	SalesProCustActivityConstants.showLog("resArray: "+resArray);
			    	cursor.moveToNext();
			    } while (!cursor.isAfterLast());
				     
				SalesProCustActivityConstants.showLog("resArray:"+resArray.size());
				cursor.close();
	    	} catch (Exception e) {
	    		SalesProCustActivityConstants.showErrorLog("Error in getDBlist:"+e.toString());				
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
	    				    	
		    	Uri uri = Uri.parse(SalesProCustCreditCP.CUS_SEL_CONTENT_URI+"");				
				ContentResolver resolver = ctx.getContentResolver();
				cursor = resolver.query(uri, null, selection, selectionParams, orderBy);
				
				SalesProCustActivityConstants.showLog("No of Category Records : "+cursor.getCount());				
				cursor.moveToFirst();
				
			    do {
			    	 int index0 = cursor.getColumnIndex(CustCreditDBConstants.CUS_SER_COL_KUNNR);
		     		 String customerNo = cursor.getString(index0).toString();
		     		 resselctdArray.add(customerNo);
		     		 SalesProCustActivityConstants.showLog("resArray: "+resselctdArray);
		     		 cursor.moveToNext();
			    } while (!cursor.isAfterLast());				    
				SalesProCustActivityConstants.showLog("resArray: "+resselctdArray.size());
				
				cursor.close();
	    	} catch (Exception e) {
	    		SalesProCustActivityConstants.showErrorLog("Error in getDBselctdIdlist:"+e.toString());				
			}
	    	finally{
				if(cursor != null)
					cursor.close();		
	    	}
			return resselctdArray;   
	    }//fn getDBselctdIdlist 

}//endof PriceListDBOperations
