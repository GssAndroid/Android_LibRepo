package com.globalsoft.CalendarLib.Database;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.globalsoft.CalendarLib.Contraints.SalesProAppCrtConstraints;
import com.globalsoft.CalendarLib.Contraints.SalesProAppCustomersConstraints;
import com.globalsoft.SapLibSoap.Constraints.SalesProActOutputConstraints;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class AppDBOperations {
	
	/*********************************************************************************************
     *     	Database Related Functions
     *********************************************************************************************/    
	    	
	public static ArrayList readAllAppDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActOutputConstraints category = null;
    	ArrayList appList = new ArrayList();
    	try{
    		int[] dbIndex = new int[20];
    		String[] dbValues = new String[20];
    		int colId = -1;
    		
    		if(appList != null)
    			appList.clear();
    		
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return appList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_KUNNR);
			dbIndex[1] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_KUNNR_NAME);
			dbIndex[2] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_PARNR);
			dbIndex[3] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_PARNR_NAME);
			dbIndex[4] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID);
			dbIndex[5] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_PROCESS_TYPE);
			dbIndex[6] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_DESCRIPTION);
			dbIndex[7] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_TEXT);
			dbIndex[8] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_DATE_FROM);			
			dbIndex[9] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_DATE_TO);
			dbIndex[10] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_TIME_FROM);
			dbIndex[11] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_TIME_TO);
			dbIndex[12] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_TIMEZONE_FROM);
			dbIndex[13] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_DURATION_SEC);
			dbIndex[14] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_CATEGORY);
			dbIndex[15] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_STATUS);
			dbIndex[16] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_STATUS_TXT30);
			dbIndex[17] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_STATUS_REASON);
			dbIndex[18] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_DOCUMENTTYPE_DESCRIPTION);			
			dbIndex[19] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_POSTING_DATE);	
			
			cursor.moveToFirst();
			
			do{
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
				
				category = new SalesProActOutputConstraints(dbValues);
				if(category != null)
					appList.add(category);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllAppDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return appList;
    }//fn readAllAppDataFromDB
	
	public static ArrayList readAllAppIdFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActOutputConstraints category = null;
    	ArrayList idList = new ArrayList();
    	try{
    		int[] dbIndex = new int[1];
    		String[] dbValues = new String[1];
    		
    		if(idList != null)
    			idList.clear();
    		
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return idList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);				
				if(dbValues[0] != null)
					idList.add(dbValues[0]);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllAppIdFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return idList;
    }//fn readAllAppIdFromDB
	
	public static ArrayList readAllGallAppIdFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActOutputConstraints category = null;
    	ArrayList idList = new ArrayList();
    	try{
    		int[] dbIndex = new int[1];
    		String[] dbValues = new String[1];
    		
    		if(idList != null)
    			idList.clear();
    		
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return idList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				if(dbValues[0] != null)
					idList.add(dbValues[0]);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllGallAppIdFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return idList;
    }//fn readAllGallAppIdFromDB
	
	public static String getUniqueId(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String msg = "";
		try{
			int[] dbIndex = new int[10];
			String[] dbValues = new String[10];
						
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				return msg;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT);
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				
				String objId = dbValues[0];
				if(id.equals(objId)){
					 String uniqId = dbValues[1];
					 if(uniqId != null && uniqId.length() > 0){
						 msg = uniqId;
						 return msg;
					 }
	     		 }	     		 
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in getUniqueId : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return msg;
	}//fn getUniqueId
	
	public static String getEventId(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String msg = "";
		try{
			int[] dbIndex = new int[10];
			String[] dbValues = new String[10];
						
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			if(cursor.getCount() == 0){
				return null;
			}			
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT);
			dbIndex[3] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_EVENT_ID);
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);

				SapGenConstants.showLog("msg: "+dbValues[0]);
				SapGenConstants.showLog("msg: "+dbValues[1]);
				SapGenConstants.showLog("msg: "+dbValues[2]);
				SapGenConstants.showLog("msg: "+dbValues[3]);
				String objId = dbValues[0];
				if(id.equals(objId)){
					 String eveId = dbValues[3];
					 if(eveId != null && eveId.length() > 0){
						 msg = eveId;
     					 SapGenConstants.showLog("msg: "+msg);
						 return msg;
					 }
	     		 }	     		 
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in getEventId : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return msg;
	}//fn getEventId
	
	public static String getGalImgCount(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String msg = "";
		try{
			int[] dbIndex = new int[10];
			String[] dbValues = new String[10];
						
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			if(cursor.getCount() == 0){
				return msg;
			}
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT);
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				
				String objId = dbValues[0];
				if(id.equals(objId)){
					 String imgCount = dbValues[2];
					 if(imgCount != null && imgCount.length() > 0){
						 msg = imgCount;
						 return msg;
					 }
	     		 }	     		 
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in getGalImgCount : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return msg;
	}//fn getGalImgCount
	
	public static void updateGalleryUniqueIdValue(Context ctx, String id, String guid){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID, guid.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGalleryUniqueIdValue : "+sggh.toString());
    	}
    }//fn updateGalleryUniqueIdValue
	
	public static void updateGallerySizeValue(Context ctx, String objId, String gsize){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{objId};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT, gsize.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGallerySizeValue : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue

	public static void updateGallerySizeValueUsingUniqId(Context ctx, String uid, String gsize){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID + " = ?";
	     	String[] updateWhereParams = new String[]{uid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT, gsize.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGallerySizeValue : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue
	
	public static void updateEventId(Context ctx, String id, String eventId){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_EVENT_ID, eventId.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateEventId : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue
	
	public static void updateObjectIdValueToGal(Context ctx, String id, String guid){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID + " = ?";
	     	String[] updateWhereParams = new String[]{guid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID, id.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateObjectIdValueToGal : "+sggh.toString());
    	}
    }//fn updateObjectIdValueToGal
	
	public static void updateObjectIdValueToAct(Context ctx, String id, String guid){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{guid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID, id.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateObjectIdValueToAct : "+sggh.toString());
    	}
    }//fn updateObjectIdValueToAct
	
	public static ArrayList readAllAppCusDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProAppCustomersConstraints customers = null;
    	ArrayList cusList = new ArrayList();
    	try{
    		int[] dbIndex = new int[6];
    		String[] dbValues = new String[6];
    		int colId = -1;
    		
    		if(cusList != null)
    			cusList.clear();
    		
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_CUS_LIST_CAT_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return cusList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_PROCESS_TYPE);
			dbIndex[2] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR);
			dbIndex[3] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR_NAME);
			dbIndex[4] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR);
			dbIndex[5] = cursor.getColumnIndex(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR_NAME);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				
				customers = new SalesProAppCustomersConstraints(dbValues);
				if(cusList != null)
					cusList.add(customers);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllActCusDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return cusList;
    }//fn readAllAppCusDataFromDB
	
	public static boolean checkObjectIdExists(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		boolean idexists = false;
		try{						
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				idexists = false;
			}else{
				idexists = true;
			}
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in checkObjectIdExists : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return idexists;
	}//fn checkObjectIdExists
	
	public static boolean checkCustomerIdExists(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		boolean idexists = false;
		try{						
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_CUS_LIST_CAT_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				idexists = false;
			}else{
				idexists = true;
			}
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in checkObjectIdExists : "+sfg.toString());
		}
		finally{
			if(cursor != null)
				cursor.close();		
		}
		return idexists;
	}//fn checkObjectIdExists
    
	/*public static void insertCategoryDataInToDB(Context ctx, SalesProAppCrtConstraints custActCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.COL_PROCESSTYPE, custActCategory.getProcessType());
	    	val.put(SalesProAppCrtConstraintsDB.COL_CATEGORY, custActCategory.getCategory());
	    	val.put(SalesProAppCrtConstraintsDB.COL_PDESC20, custActCategory.getPDescription());
	    	val.put(SalesProAppCrtConstraintsDB.COL_DESC, custActCategory.getDescription());
	    	resolver.insert(SalesProAppCrtConstraintsCP.CAT_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertCategoryDataInToDB
*/        
	/*public static void insertStatusDataInToDB(Context ctx, SalesProActStatusConstraints statusCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.COL_STATUS, statusCategory.getStatus());
	    	val.put(SalesProAppCrtConstraintsDB.COL_TXT30, statusCategory.getStatusDesc());
	    	resolver.insert(SalesProAppCrtConstraintsCP.STATUS_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertStatusDataInToDB : "+sgh.toString());
    	}
    }//fn insertStatusDataInToDB    
*/  
	public static void insertAppCategoryDataInToDB(Context ctx, SalesProActOutputConstraints category){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_KUNNR, category.getKunnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_KUNNR_NAME, category.getKunnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PARNR, category.getParnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PARNR_NAME, category.getParnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID, category.getObjectId().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PROCESS_TYPE, category.getProcessType().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DESCRIPTION, category.getDescription().toString().trim());	    	
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TEXT, category.getText().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DATE_FROM, category.getDateFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DATE_TO, category.getDateTo().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIME_FROM, category.getTimeFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIME_TO, category.getTimeTo().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIMEZONE_FROM, category.getTimezoneFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DURATION_SEC, category.getDurationSec().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_CATEGORY, category.getCategory().toString().trim());	    	
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_STATUS, category.getStatus().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_STATUS_TXT30, category.getStatusTxt30().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_STATUS_REASON, category.getStatusReason().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DOCUMENTTYPE_DESCRIPTION, category.getDocumenttypeDesc().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_POSTING_DATE, category.getPostingDate().toString().trim());
	    	
	    	resolver.insert(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertAppCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertAppCategoryDataInToDB    
	
	public static void updateAppCategoryDataInToDB(Context ctx, String tracId, SalesProActOutputConstraints category){
    	try{
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{tracId};
	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_KUNNR, category.getKunnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_KUNNR_NAME, category.getKunnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PARNR, category.getParnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PARNR_NAME, category.getParnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_OBJECT_ID, category.getObjectId().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_PROCESS_TYPE, category.getProcessType().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DESCRIPTION, category.getDescription().toString().trim());	    	
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TEXT, category.getText().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DATE_FROM, category.getDateFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DATE_TO, category.getDateTo().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIME_FROM, category.getTimeFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIME_TO, category.getTimeTo().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_TIMEZONE_FROM, category.getTimezoneFrom().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_DURATION_SEC, category.getDurationSec().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_CATEGORY, category.getCategory().toString().trim());	    	
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_STATUS, category.getStatus().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_COL_STATUS_TXT30, category.getStatusTxt30().toString().trim());    
	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateAppCategoryDataInToDB : "+sggh.toString());
    	}
    }//fn updateAppCategoryDataInToDB
	  
	public static void insertAppCusCategoryDataInToDB(Context ctx, SalesProAppCustomersConstraints customers){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID, customers.getObjectId().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_PROCESS_TYPE, customers.getProcessType().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR, customers.getKunnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR_NAME, customers.getKunnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR, customers.getParnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR_NAME, customers.getParnrName().toString().trim());
	    	
	    	resolver.insert(SalesProAppCrtConstraintsCP.APP_CUS_LIST_CAT_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertAPPCusCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertAppCusCategoryDataInToDB
	
	public static void updateAppCusCategoryDataInToDB(Context ctx, String tracId, SalesProAppCustomersConstraints customers){
    	try{
			Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_CUS_LIST_CAT_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{tracId};
	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID, customers.getObjectId().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR, customers.getKunnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_KUNNR_NAME, customers.getKunnrName().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR, customers.getParnr().toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_PARNR_NAME, customers.getParnrName().toString().trim());
	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateActCusCategoryDataInToDB : "+sggh.toString());
    	}
    }//fn updateAppCusCategoryDataInToDB
	  
	public static void insertAppGalListDetDataInToDB(Context ctx, String objId, String uniqId, String galcount, String eventId){
  	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProAppCrtConstraintsDB.APP_CUS_COL_OBJECT_ID, objId.toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_UID, uniqId.toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_GALLERY_COUNT, galcount.toString().trim());
	    	val.put(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_EVENT_ID, eventId.toString().trim());
	    	
	    	resolver.insert(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI, val);
  	}
  	catch(Exception sgh){
  		SapGenConstants.showErrorLog("Error in insertAppGalListDetDataInToDB : "+sgh.toString());
  	}
  }//fn insertAppGalListDetDataInToDB
	
	/*public static void deleteAllCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.CAT_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllCategoryDataFromDB
        
	public static void deleteAllStatusDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.STATUS_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllStatusDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllStatusDataFromDB	
*/	
	public static void deleteAllAppCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_LIST_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllAppCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllAppCategoryDataFromDB
	
	public static void deleteAllAppCusCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_CUS_LIST_CAT_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllAppCusCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllAppCusCategoryDataFromDB
	
	public static void deleteGallRowForEmptyID(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String delWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?  OR " + SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] delWhereParams = new String[]{null,""};
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, delWhere, delWhereParams);	
			SapGenConstants.showLog("Rows Deleted : "+rows);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteGallRowForEmptyID : "+sggh.toString());
    	}
    }//fn deleteGallRowForEmptyID
	
	public static void deleteGallRowByGivnenID(Context ctx, String id){
    	try{
    		Uri uri = Uri.parse(SalesProAppCrtConstraintsCP.APP_GAL_LIST_DET_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String delWhere = SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] delWhereParams = new String[]{id};
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, delWhere, delWhereParams);	
			SapGenConstants.showLog("Rows Deleted : "+rows);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteGallRowByGivnenID : "+sggh.toString());
    	}
    }//fn deleteGallRowByGivnenID
    
    /*********************************************************************************************
     *          End of Database Related Functions
     *********************************************************************************************/

}//endof AppDBOperations