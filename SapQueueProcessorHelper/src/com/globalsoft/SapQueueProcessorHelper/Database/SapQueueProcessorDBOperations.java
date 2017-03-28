package com.globalsoft.SapQueueProcessorHelper.Database;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public  class SapQueueProcessorDBOperations {
	private static Context mContext;
	
	 public static void deleteCusIdData(Context ctx,String custId){
	    	Uri uri = null;
			String whereStr = null;
			String[] whereParams = null;
	    	uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI.toString());
			
			//Get the Resolver
			ContentResolver resolver = ctx.getContentResolver();
			
			whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_APPREFID + " = ?";
			whereParams = new String[]{String.valueOf(custId)}; 
			
			//Make the invocation. # of rows deleted will be sent back
			int rows = resolver.delete(uri, whereStr, whereParams);
			
			SapQueueProcessorHelperConstants.showLog("</br>AlloldData Rows Deleted : "+rows);
	    }//
	 
	   public static  int getrowCount(Context ctx){
		   //mContext = mContext.getApplicationContext();
	    	Cursor cursor = null;    	
	    	Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");			
			ContentResolver resolver = ctx.getContentResolver();													
			cursor = resolver.query(uri, null, null, null, null);			
			int rowCount = cursor.getCount();
			SapQueueProcessorHelperConstants.showLog("rowCount in readALLData	: "+rowCount);
			/*int processcount = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT);
			int processcount2 = cursor.getInt(processcount);*/
			return rowCount;
	    }//
	    
	    public static int getProcessCount(Context ctx,int procount){	    	
	    	int processcount2 =-1;
	    	String selection=null;
	    	String[] selectionParams =null;
	    	try{
	    		Cursor cursor = null;    	
	        	Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");			
	    		ContentResolver resolver = ctx.getContentResolver();
	    		String orderBy =SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" asc limit 1";
	    		selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT + " = ?";
	    		selectionParams = new String[]{String.valueOf(procount)}; 
	    		cursor = resolver.query(uri, null, selection, selectionParams, orderBy);			
	    		int rowCount = cursor.getCount();
	    		SapQueueProcessorHelperConstants.showLog("rowCount in getProcessCount	: "+rowCount);
	    		int processcount = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT);
	    		cursor.moveToFirst();
	    		 processcount2 = cursor.getInt(processcount);
	    		 SapQueueProcessorHelperConstants.showLog("processcount2 in getProcessCount	: "+processcount2);
	    	}catch(Exception sgh){
	    		SapQueueProcessorHelperConstants.showErrorLog("Error in getProcessCount : "+sgh.toString());
			}   	
			return processcount2;
	    }//
	    
	    public static int getAppStatus(Context ctx,String apiname){	    	
	    	int processcount2 =-1;
	    	String selection=null,whereStr=null;
	    	String[] selectionParams =null,whereParams =null;
	    	try{
	    		Cursor cursor = null;    	
	        	Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");			
	    		ContentResolver resolver = ctx.getContentResolver();
	    		String orderBy =SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" asc limit 1";
	    		whereStr = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_FUNCNAME + " = ?";
				whereParams = new String[]{String.valueOf(apiname)}; 
				
	    		selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS + " = ?";
	    		//selectionParams = new String[]{String.valueOf(procount)}; 
	    		cursor = resolver.query(uri, whereParams, selection, null, orderBy);			
	    		int rowCount = cursor.getCount();
	    		SapQueueProcessorHelperConstants.showLog("rowCount in getProcessCount	: "+rowCount);
	    		int processcount = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS);
	    		cursor.moveToFirst();
	    		 processcount2 = cursor.getInt(processcount);
	    		 SapQueueProcessorHelperConstants.showLog("processcount2 in getProcessCount	: "+processcount2);
	    	}catch(Exception sgh){
	    		SapQueueProcessorHelperConstants.showErrorLog("Error in getProcessCount : "+sgh.toString());
			}   	
			return processcount2;
	    }//
	    
	    	   
	    public static ArrayList<Integer> getAllProcessCount(Context ctx){	    	
	    	int processcount2 =-1;
	    	ArrayList<Integer> processcountList = new ArrayList<Integer>();
	    	String selection=null;
	    	String[] selectionParams =null;
	    	try{
	    		Cursor cursor = null;    	
	        	Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");			
	    		ContentResolver resolver = ctx.getContentResolver();
	    		/*String orderBy =SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" asc limit 1";
	    		selection = SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT + " = ?";
	    		selectionParams = new String[]{String.valueOf(procount)}; */
	    		cursor = resolver.query(uri, null, null, null, null);			
	    		int rowCount = cursor.getCount();
	    		SapQueueProcessorHelperConstants.showLog("rowCount in getProcessCount	: "+rowCount);
	    		int processcount = cursor.getColumnIndex(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT);
	    		cursor.moveToFirst();
				
				do{
					 processcount2 = cursor.getInt(processcount);
					 processcountList.add(processcount2);
					 SapQueueProcessorHelperConstants.showLog("processcount2 : "+processcount2);										
					cursor.moveToNext();
				}while(!cursor.isAfterLast());
	    		/*cursor.moveToFirst();
	    		 processcount2 = cursor.getInt(processcount);*/
	    		 SapQueueProcessorHelperConstants.showLog("processcount2 in getProcessCount	: "+processcount2);
	    	}catch(Exception sgh){
	    		SapQueueProcessorHelperConstants.showErrorLog("Error in getProcessCount : "+sgh.toString());
			}   	
			return processcountList;
	    }//
	    
}//End of class SapQueueProcessorDBOperations
