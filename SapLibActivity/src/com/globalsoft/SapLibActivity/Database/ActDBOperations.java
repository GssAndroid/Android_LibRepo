package com.globalsoft.SapLibActivity.Database;

import java.util.ArrayList;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.globalsoft.SapLibActivity.Contraints.SalesProActCrtConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActCustomersConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActStatusConstraints;
import com.globalsoft.SapLibSoap.Constraints.SalesProActOutputConstraints;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapLibActivity.Database.SalesProActCrtConstraintsCP;

public class ActDBOperations {
	
	/*********************************************************************************************
     *     	Database Related Functions
     *********************************************************************************************/    
	public static ArrayList readAllCategoryDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActCrtConstraints custActCategory = null;
    	ArrayList categoryArrList = new ArrayList();
    	try{
    		int idIndex = -1, prTypeIndex = -1, catIndex = -1, pDesc20Index = -1, descIndex = -1; 
    		int colId = -1;
    		String colIdStr = "", prTypeStr = "", categoryStr = "", pDesc20Str = "", descStr = "";
    		
    		if(categoryArrList != null)
    			categoryArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.CAT_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return categoryArrList;
			}
			
			idIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_ID);
			prTypeIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_PROCESSTYPE);
			catIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_CATEGORY);
			pDesc20Index = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_PDESC20);
			descIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_DESC);
			
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(idIndex);
				prTypeStr = cursor.getString(prTypeIndex);
				categoryStr = cursor.getString(catIndex);
				pDesc20Str = cursor.getString(pDesc20Index);
				descStr = cursor.getString(descIndex);
				
				SapGenConstants.showLog("Id : "+colId+" : "+prTypeStr+" : "+categoryStr);
				SapGenConstants.showLog("Desc : "+pDesc20Str+" : "+descStr);
								
				custActCategory = new SalesProActCrtConstraints(prTypeStr, categoryStr, pDesc20Str, descStr);
				if(custActCategory != null)
					categoryArrList.add(custActCategory);
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllCategoryDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
			
			//updateCategorySelection();
    	}
    	return categoryArrList;
    }//fn readAllCategoryDataFromDB    
    
	public static ArrayList readAllStatusDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActStatusConstraints statusCategory = null;
    	ArrayList statusArrList = new ArrayList();
    	try{
    		int idIndex = -1, statusIndex = -1, descIndex = -1, statusiconIndex = -1, statusActionIndex = -1; 
    		int colId = -1;
    		String colIdStr = "", statusStr = "", txtDescStr = "",statusiconStr = "",statusPostActionStr = "";
    		
    		if(statusArrList != null)
    			statusArrList.clear();
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.STATUS_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Status Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return statusArrList;
			}
			
			idIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_ID);
			statusIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_STATUS);
			descIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_TXT30);
			statusiconIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_ZZSTATUS_ICON);
			statusActionIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_ZZSTATUS_POSTSETACTION);
			cursor.moveToFirst();
			
			do{
				colId = cursor.getInt(idIndex);
				statusStr = cursor.getString(statusIndex);
				txtDescStr = cursor.getString(descIndex);
				statusiconStr = cursor.getString(statusiconIndex);
				statusPostActionStr = cursor.getString(statusActionIndex);
				
				SapGenConstants.showLog("Id : "+colId+" : "+statusStr+" : "+txtDescStr);
								
				statusCategory = new SalesProActStatusConstraints(statusStr, txtDescStr, statusiconStr, statusPostActionStr );
				if(statusCategory != null)
					statusArrList.add(statusCategory);
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllStatusDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
			
			//updateStatusSelection();
    	}
    	return statusArrList;
    }//fn readAllStatusDataFromDB
	
	public static String getStatusDescFromDB(Context ctx, String status){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActStatusConstraints statusCategory = null;
    	String statusDesc = "";
    	try{
    		int idIndex = -1, statusIndex = -1, descIndex = -1; 
    		int colId = -1;
    		String colIdStr = "", statusStr = "", txtDescStr = "";
    		    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.STATUS_CONTENT_URI+"");		
    		selection = SalesProActCrtConstraintsDB.COL_STATUS + " = ?";
			selectionParams = new String[]{status};			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Status Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return statusDesc;
			}
			
			descIndex = cursor.getColumnIndex(SalesProActCrtConstraintsDB.COL_TXT30);
			
			cursor.moveToFirst();
			
			do{
				statusDesc = cursor.getString(descIndex);								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in getStatusDescFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return statusDesc;
    }//fn getStatusDescFromDB
	
	public static ArrayList readAllActDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActOutputConstraints category = null;
    	ArrayList actList = new ArrayList();
    	try{
    		int[] dbIndex = new int[20];
    		String[] dbValues = new String[20];
    		int colId = -1;
    		
    		if(actList != null)
    			actList.clear();
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return actList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_KUNNR);
			dbIndex[1] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_KUNNR_NAME);
			dbIndex[2] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_PARNR);
			dbIndex[3] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_PARNR_NAME);
			dbIndex[4] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID);
			dbIndex[5] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_PROCESS_TYPE);
			dbIndex[6] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_DESCRIPTION);
			dbIndex[7] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_TEXT);
			dbIndex[8] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_DATE_FROM);			
			dbIndex[9] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_DATE_TO);
			dbIndex[10] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_TIME_FROM);
			dbIndex[11] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_TIME_TO);
			dbIndex[12] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_TIMEZONE_FROM);
			dbIndex[13] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_DURATION_SEC);
			dbIndex[14] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_CATEGORY);
			dbIndex[15] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_STATUS);
			dbIndex[16] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_STATUS_TXT30);
			dbIndex[17] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_STATUS_REASON);
			dbIndex[18] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_DOCUMENTTYPE_DESCRIPTION);			
			dbIndex[19] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_POSTING_DATE);	
			
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
					actList.add(category);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllActDataFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return actList;
    }//fn readAllActDataFromDB
	
	public static ArrayList readAllActIdFromDB(Context ctx){
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
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return idList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);				
				if(dbValues[0] != null)
					idList.add(dbValues[0]);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllActIdFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return idList;
    }//fn readAllActIdFromDB
	
	public static ArrayList readAllGallActIdFromDB(Context ctx){
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
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return idList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				if(dbValues[0] != null)
					idList.add(dbValues[0]);
								
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in readAllGallActIdFromDB : "+sfg.toString());
    	}
    	finally{
			if(cursor != null)
				cursor.close();		
    	}
    	return idList;
    }//fn readAllGallActIdFromDB
	
	public static String getUniqueId(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		String msg = "";
		try{
			int[] dbIndex = new int[10];
			String[] dbValues = new String[10];
						
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
						
			if(cursor.getCount() == 0){
				return msg;
			}
			
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT);
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
						
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			if(cursor.getCount() == 0){
				return null;
			}			
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT);
			dbIndex[3] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_EVENT_ID);
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
						
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
			selectionParams = new String[]{id};			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			if(cursor.getCount() == 0){
				return msg;
			}
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID);
			dbIndex[2] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT);
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
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID, guid.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGalleryUniqueIdValue : "+sggh.toString());
    	}
    }//fn updateGalleryUniqueIdValue
	
	public static void updateGallerySizeValue(Context ctx, String objId, String gsize){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{objId};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT, gsize.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGallerySizeValue : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue

	public static void updateGallerySizeValueUsingUniqId(Context ctx, String uid, String gsize){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID + " = ?";
	     	String[] updateWhereParams = new String[]{uid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT, gsize.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateGallerySizeValue : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue
	
	public static void updateEventId(Context ctx, String id, String eventId){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{id};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_EVENT_ID, eventId.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateEventId : "+sggh.toString());
    	}
    }//fn updateGallerySizeValue
	
	public static void updateObjectIdValueToGal(Context ctx, String id, String guid){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID + " = ?";
	     	String[] updateWhereParams = new String[]{guid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID, id.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateObjectIdValueToGal : "+sggh.toString());
    	}
    }//fn updateObjectIdValueToGal
	
	public static void updateObjectIdValueToAct(Context ctx, String id, String guid){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI+"");				
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{guid};	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID, id.toString().trim());	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateObjectIdValueToAct : "+sggh.toString());
    	}
    }//fn updateObjectIdValueToAct
	
	public static ArrayList readAllActCusDataFromDB(Context ctx){
    	Cursor cursor = null;
    	String selection = null;
    	String[] selectionParams = null;
    	String orderBy = null;
    	SalesProActCustomersConstraints customers = null;
    	ArrayList cusList = new ArrayList();
    	try{
    		int[] dbIndex = new int[6];
    		String[] dbValues = new String[6];
    		int colId = -1;
    		
    		if(cusList != null)
    			cusList.clear();
    		
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_CUS_LIST_CAT_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();			
			cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
			SapGenConstants.showLog("No of Category Records : "+cursor.getCount());
			
			if(cursor.getCount() == 0){
				return cusList;
			}
		    
			dbIndex[0] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID);
			dbIndex[1] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_PROCESS_TYPE);
			dbIndex[2] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR);
			dbIndex[3] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR_NAME);
			dbIndex[4] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR);
			dbIndex[5] = cursor.getColumnIndex(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR_NAME);
			
			cursor.moveToFirst();
			
			do{
				dbValues[0] = cursor.getString(dbIndex[0]);
				dbValues[1] = cursor.getString(dbIndex[1]);
				dbValues[2] = cursor.getString(dbIndex[2]);
				dbValues[3] = cursor.getString(dbIndex[3]);
				dbValues[4] = cursor.getString(dbIndex[4]);
				dbValues[5] = cursor.getString(dbIndex[5]);
				
				customers = new SalesProActCustomersConstraints(dbValues);
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
    }//fn readAllActCusDataFromDB
	
	public static boolean checkObjectIdExists(Context ctx, String id){
		Cursor cursor = null;
		String selection = null;
		String[] selectionParams = null;
		String orderBy = null;
		boolean idexists = false;
		try{						
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID + " = ?";
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
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_CUS_LIST_CAT_CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();
			selection = SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID + " = ?";
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
    
	public static void insertCategoryDataInToDB(Context ctx, SalesProActCrtConstraints custActCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.COL_PROCESSTYPE, custActCategory.getProcessType());
	    	val.put(SalesProActCrtConstraintsDB.COL_CATEGORY, custActCategory.getCategory());
	    	val.put(SalesProActCrtConstraintsDB.COL_PDESC20, custActCategory.getPDescription());
	    	val.put(SalesProActCrtConstraintsDB.COL_DESC, custActCategory.getDescription());
	    	resolver.insert(SalesProActCrtConstraintsCP.CAT_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertCategoryDataInToDB
        
	public static void insertStatusDataInToDB(Context ctx, SalesProActStatusConstraints statusCategory){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.COL_STATUS, statusCategory.getStatus());
	    	val.put(SalesProActCrtConstraintsDB.COL_TXT30, statusCategory.getStatusDesc());
	    	val.put(SalesProActCrtConstraintsDB.COL_ZZSTATUS_ICON, statusCategory.getStatusIcon());
	    	val.put(SalesProActCrtConstraintsDB.COL_ZZSTATUS_POSTSETACTION, statusCategory.getStatusPstAction());
	    	
	    	resolver.insert(SalesProActCrtConstraintsCP.STATUS_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertStatusDataInToDB : "+sgh.toString());
    	}
    }//fn insertStatusDataInToDB    
  
	public static void insertActCategoryDataInToDB(Context ctx, SalesProActOutputConstraints category){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_KUNNR, category.getKunnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_KUNNR_NAME, category.getKunnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PARNR, category.getParnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PARNR_NAME, category.getParnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID, category.getObjectId().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PROCESS_TYPE, category.getProcessType().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DESCRIPTION, category.getDescription().toString().trim());	    	
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TEXT, category.getText().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DATE_FROM, category.getDateFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DATE_TO, category.getDateTo().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIME_FROM, category.getTimeFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIME_TO, category.getTimeTo().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIMEZONE_FROM, category.getTimezoneFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DURATION_SEC, category.getDurationSec().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_CATEGORY, category.getCategory().toString().trim());	    	
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_STATUS, category.getStatus().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_STATUS_TXT30, category.getStatusTxt30().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_STATUS_REASON, category.getStatusReason().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DOCUMENTTYPE_DESCRIPTION, category.getDocumenttypeDesc().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_POSTING_DATE, category.getPostingDate().toString().trim());
	    	
	    	resolver.insert(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertActCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertActCategoryDataInToDB    
	
	public static void updateActCategoryDataInToDB(Context ctx, String tracId, SalesProActOutputConstraints category){
    	try{
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{tracId};
	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_KUNNR, category.getKunnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_KUNNR_NAME, category.getKunnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PARNR, category.getParnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PARNR_NAME, category.getParnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_OBJECT_ID, category.getObjectId().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_PROCESS_TYPE, category.getProcessType().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DESCRIPTION, category.getDescription().toString().trim());	    	
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TEXT, category.getText().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DATE_FROM, category.getDateFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DATE_TO, category.getDateTo().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIME_FROM, category.getTimeFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIME_TO, category.getTimeTo().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_TIMEZONE_FROM, category.getTimezoneFrom().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_DURATION_SEC, category.getDurationSec().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_CATEGORY, category.getCategory().toString().trim());	    	
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_STATUS, category.getStatus().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_COL_STATUS_TXT30, category.getStatusTxt30().toString().trim());    
	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateActCategoryDataInToDB : "+sggh.toString());
    	}
    }//fn updateActCategoryDataInToDB
	  
	public static void insertActCusCategoryDataInToDB(Context ctx, SalesProActCustomersConstraints customers){
    	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID, customers.getObjectId().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_PROCESS_TYPE, customers.getProcessType().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR, customers.getKunnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR_NAME, customers.getKunnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR, customers.getParnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR_NAME, customers.getParnrName().toString().trim());
	    	
	    	resolver.insert(SalesProActCrtConstraintsCP.ACT_CUS_LIST_CAT_CONTENT_URI, val);
    	}
    	catch(Exception sgh){
    		SapGenConstants.showErrorLog("Error in insertActCusCategoryDataInToDB : "+sgh.toString());
    	}
    }//fn insertActCusCategoryDataInToDB
	
	public static void updateActCusCategoryDataInToDB(Context ctx, String tracId, SalesProActCustomersConstraints customers){
    	try{
			Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_CUS_LIST_CAT_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String updateWhere = SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID + " = ?";
	     	String[] updateWhereParams = new String[]{tracId};
	     	
			//Make the invocation. # of rows deleted will be sent back
	    	ContentValues val = new ContentValues();
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID, customers.getObjectId().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR, customers.getKunnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_KUNNR_NAME, customers.getKunnrName().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR, customers.getParnr().toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_PARNR_NAME, customers.getParnrName().toString().trim());
	    	
			resolver.update(uri, val, updateWhere, updateWhereParams);	
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in updateActCusCategoryDataInToDB : "+sggh.toString());
    	}
    }//fn updateActCusCategoryDataInToDB
	  
	public static void insertActGalListDetDataInToDB(Context ctx, String objId, String uniqId, String galcount, String eventId){
  	try{
	    	ContentResolver resolver = ctx.getContentResolver();
	    	ContentValues val = new ContentValues();
			
	    	val.put(SalesProActCrtConstraintsDB.ACT_CUS_COL_OBJECT_ID, objId.toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_UID, uniqId.toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_GALLERY_COUNT, galcount.toString().trim());
	    	val.put(SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_EVENT_ID, eventId.toString().trim());
	    	
	    	resolver.insert(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI, val);
  	}
  	catch(Exception sgh){
  		SapGenConstants.showErrorLog("Error in insertActGalListDetDataInToDB : "+sgh.toString());
  	}
  }//fn insertActGalListDetDataInToDB
	
	public static void deleteAllCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.CAT_CONTENT_URI.toString());			
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
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.STATUS_CONTENT_URI.toString());			
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
	
	public static void deleteAllActCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_LIST_CONTENT_URI.toString());			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllActCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllActCategoryDataFromDB
	
	public static void deleteAllActCusCategoryDataFromDB(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_CUS_LIST_CAT_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, null, null);			
			SapGenConstants.showLog("Rows Deleted : "+rows);
    	}
    	catch(Exception sggh){
    		SapGenConstants.showErrorLog("Error in deleteAllActCusCategoryDataFromDB : "+sggh.toString());
    	}
    }//fn deleteAllActCusCategoryDataFromDB
	
	public static void deleteGallRowForEmptyID(Context ctx){
    	try{
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String delWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?  OR " + SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
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
    		Uri uri = Uri.parse(SalesProActCrtConstraintsCP.ACT_GAL_LIST_DET_CONTENT_URI.toString());
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();		
			String delWhere = SalesProActCrtConstraintsDB.ACT_GAL_LIST_DET_COL_OBJECT_ID + " = ?";
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

}//endof ActDBOperations