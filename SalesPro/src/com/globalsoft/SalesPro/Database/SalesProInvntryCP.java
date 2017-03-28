package com.globalsoft.SalesPro.Database;




import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SalesProInvntryCP extends ContentProvider{
	
	private SalesProInvntryDB mDB;
	
	public static final String AUTHORITY = "com.globalsoft.SalesPro.SalesProInvntryCP";
	
	public static final int MATCH_ALL1 = 5001;
	public static final int MATCH_ALL2 = 5002;
	
	public static final int MATCH_ID1 = 70021;
	public static final int MATCH_ID2 = 70022;
	
	private static final String OBJECTS_BASE_PATH1 = "objects1";
	private static final String OBJECTS_BASE_PATH2 = "objects2";
	
	
	 public static final Uri IL_SER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH1);
	 public static final Uri IL_SEL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH2);
	    
	 public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/invntrysrch-details";
     public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/invntrysrch-details";
	    
	 private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 
	 static {
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1, MATCH_ALL1);
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1 + "/#", MATCH_ID1);
			
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2, MATCH_ALL2);
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2 + "/#", MATCH_ID2);
		}
		
		@Override
		public boolean onCreate() {
			Context ctx = getContext();
			mDB = new SalesProInvntryDB(ctx);		
			return true;
		}
	 
	 @Override
		public String getType(Uri uri) {
	        int uriType = sURIMatcher.match(uri);
	        if((uriType == MATCH_ALL1) || (uriType == MATCH_ALL2)){
	        	return CONTENT_TYPE;
	        }
	        else if((uriType == MATCH_ID1) || (uriType == MATCH_ID2)){
	        	return CONTENT_ITEM_TYPE;
	        }
	        else
	        	return null;
	        /*
	        switch (uriType) {
		        case MATCH_ALL1:
		            return CONTENT_TYPE;
		        case MATCH_ID1:
		            return CONTENT_ITEM_TYPE;
		        default:
		            return null;
	        }
	        */
	    }
		
	 
	 @Override
		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

	        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	        int uriType = sURIMatcher.match(uri);
	        SalesOrderProConstants.showLog("URI : "+uri+" : "+uriType+" : "+uri.getLastPathSegment());
	        switch (uriType) {
		        case MATCH_ID1:
		            queryBuilder.setTables(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP);
		            queryBuilder.appendWhere(SalesProInvntryDBConstants.IL_SER_COL_ID + "=" + uri.getLastPathSegment());
		            break;
		        case MATCH_ALL1:
		        	 queryBuilder.setTables(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP);
		            // no filter
		            break;
		        case MATCH_ID2:
		            queryBuilder.setTables(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST);
		            queryBuilder.appendWhere(SalesProInvntryDBConstants.IL_SEL_COL_ID + "=" + uri.getLastPathSegment());
		            break;
		        case MATCH_ALL2:
		        	 queryBuilder.setTables(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST);
		            // no filter
		            break;
		            
		        default:
		            throw new IllegalArgumentException("Unknown URI");   
	        }     
	        
	        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
	                projection, selection, selectionArgs, null, null, sortOrder);
	        cursor.setNotificationUri(getContext().getContentResolver(), uri);
	        return cursor;
	    }
	 
	 
	 @Override
		public Uri insert(Uri uri, ContentValues values) {
		 int uriType = sURIMatcher.match(uri);
		 SalesOrderProConstants.showLog("uriType : "+uriType+" : "+MATCH_ALL1+" : "+MATCH_ALL2);
	        if((uriType != MATCH_ALL1) && (uriType != MATCH_ALL2)){
	        	throw new IllegalArgumentException("Invalid URI for insert");
	        }
	        
	        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
	        try {
	            long newID = -1;
	            if(uriType == MATCH_ALL1)
	            	newID = sqlDB.insertOrThrow(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP, null, values);
	            else if(uriType == MATCH_ALL2)
	            	newID = sqlDB.insertOrThrow(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, null, values);
	        	
	            
	            if (newID > 0) {
	                Uri newUri = ContentUris.withAppendedId(uri, newID);
	                getContext().getContentResolver().notifyChange(uri, null);
	                return newUri;
	            } 
	            else {
	                throw new SQLException("Failed to insert row into " + uri);
	            }
	        } 
	        catch (SQLiteConstraintException e) {
	        	SalesOrderProConstants.showErrorLog("Ignoring constraint failure : "+e.toString());
	        }
	        return null;
	    }
     
	 
	 @Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	        int uriType = sURIMatcher.match(uri);
	        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

	        int rowsAffected;
	        String id = "";
	        StringBuilder modSelection = null;
	        switch (uriType) {
		        case MATCH_ID1:
		            id = uri.getLastPathSegment();
		            modSelection = new StringBuilder(SalesProInvntryDBConstants.IL_SER_COL_ID + "=" + id);
		
		            if (!TextUtils.isEmpty(selection)) {
		                modSelection.append(" AND " + selection);
		            }
		
		            rowsAffected = sqlDB.update(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP, values, modSelection.toString(), null);
		            break;
		        case MATCH_ALL1:
		            rowsAffected = sqlDB.update(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, values, selection, selectionArgs);
		            break;
		        case MATCH_ID2:
		        	id = uri.getLastPathSegment();
		            modSelection = new StringBuilder(SalesProInvntryDBConstants.IL_SEL_COL_ID + "=" + id);
		
		            if (!TextUtils.isEmpty(selection)) {
		                modSelection.append(" AND " + selection);
		            }
		
		            rowsAffected = sqlDB.update(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, values, modSelection.toString(), null);
		            break;
		        case MATCH_ALL2:
		        	rowsAffected = sqlDB.update(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, values, selection, selectionArgs);
		            break;
		            
		        default:
		            throw new IllegalArgumentException("Unknown or Invalid URI");
	        }
	        
	        getContext().getContentResolver().notifyChange(uri, null);
	        return rowsAffected;
	    }
	 
	 
	 @Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
	        int uriType = sURIMatcher.match(uri);
	        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
	        int rowsAffected = 0;
	        String id = "";
	        switch (uriType) {
		        case MATCH_ALL1:
		            rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP, selection, selectionArgs);
		            break;
		        case MATCH_ID1:
		            id = uri.getLastPathSegment();
		            if (TextUtils.isEmpty(selection)) {
		                rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP, SalesProInvntryDBConstants.IL_SER_COL_ID + "=" + id, null);
		            } 
		            else {
		                rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SEARCH_IN_LIST_EMP,
		                        selection + " and " + SalesProInvntryDBConstants.IL_SER_COL_ID + "=" + id, selectionArgs);
		            }
		            break;
		        case MATCH_ALL2:
		        	rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, selection, selectionArgs);
		            break;
		        case MATCH_ID2:
		        	id = uri.getLastPathSegment();
		            if (TextUtils.isEmpty(selection)) {
		                rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST, SalesProInvntryDBConstants.IL_SEL_COL_ID + "=" + id, null);
		            } 
		            else {
		                rowsAffected = sqlDB.delete(SalesProInvntryDBConstants.TABLE_SELECTED_IN_LIST,
		                        selection + " and " + SalesProInvntryDBConstants.IL_SEL_COL_ID + "=" + id, selectionArgs);
		            }
		            break;
		            
		        default:
		            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	        }
	        
	        getContext().getContentResolver().notifyChange(uri, null);
	        return rowsAffected;
	    }	   
	        
}//end of PriceListCP

