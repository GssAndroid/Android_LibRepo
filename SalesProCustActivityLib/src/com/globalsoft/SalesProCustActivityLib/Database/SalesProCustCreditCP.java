package com.globalsoft.SalesProCustActivityLib.Database;


import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;

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

public class SalesProCustCreditCP extends ContentProvider{
	
	private CustCreditDB mDB;
	
	public static final String AUTHORITY = "com.globalsoft.SalesPro.SalesProCustCreditCP";
	
	public static final int MATCH_ALL1 = 10007;
	public static final int MATCH_ALL2 = 10008;
	public static final int MATCH_ALL3 = 10009;
	
	public static final int MATCH_ID1 = 6788;
	public static final int MATCH_ID2 = 6789;
	public static final int MATCH_ID3 = 6790;
	
	private static final String OBJECTS_BASE_PATH1 = "objects1";
	private static final String OBJECTS_BASE_PATH2 = "objects2";
	private static final String OBJECTS_BASE_PATH3 = "objects3";
	
	
	 public static final Uri CUS_SER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH1);
	 public static final Uri CUS_SEL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH2);
	 public static final Uri CUS_CNTX_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH3);
	  
	 public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/cust-details";
     public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/cust-details";
	    
	 private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 
	 static {
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1, MATCH_ALL1);
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1 + "/#", MATCH_ID1);
			
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2, MATCH_ALL2);
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2 + "/#", MATCH_ID2);
			
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH3, MATCH_ALL3);
			sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH3 + "/#", MATCH_ID3);
		}
		
		@Override
		public boolean onCreate() {
			Context ctx = getContext();
			mDB = new CustCreditDB(ctx);		
			return true;
		}
	 
	 @Override
		public String getType(Uri uri) {
	        int uriType = sURIMatcher.match(uri);
	        if((uriType == MATCH_ALL1) || (uriType == MATCH_ALL2) || (uriType == MATCH_ALL3)){
	        	return CONTENT_TYPE;
	        }
	        else if((uriType == MATCH_ID1) || (uriType == MATCH_ID2) || (uriType == MATCH_ID3)){
	        	return CONTENT_ITEM_TYPE;
	        }
	        else
	        	return null;
	    }

		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

	        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	        int uriType = sURIMatcher.match(uri);
	        SalesProCustActivityConstants.showLog("URI : "+uri+" : "+uriType+" : "+uri.getLastPathSegment());
	        switch (uriType) {
		        case MATCH_ID1:
		            queryBuilder.setTables(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_);
		            queryBuilder.appendWhere(CustCreditDBConstants.CUS_SER_COL_ID + "=" + uri.getLastPathSegment());
		            break;
		        case MATCH_ALL1:
		        	 queryBuilder.setTables(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_);
		            // no filter
		            break;
		        case MATCH_ID2:
		            queryBuilder.setTables(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST);
		            queryBuilder.appendWhere(CustCreditDBConstants.CUS_SEL_COL_ID + "=" + uri.getLastPathSegment());
		            break;
		        case MATCH_ALL2:
		        	 queryBuilder.setTables(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST);
		            // no filter
		            break;
		        case MATCH_ID3:
		            queryBuilder.setTables(CustCreditDBConstants.TABLE_CUS_CNTX);
		            queryBuilder.appendWhere(CustCreditDBConstants.CUS_CNTX_COL_ID + "=" + uri.getLastPathSegment());
		            break;
		        case MATCH_ALL3:
		        	 queryBuilder.setTables(CustCreditDBConstants.TABLE_CUS_CNTX);
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
		 SalesProCustActivityConstants.showLog("uriType : "+uriType+" : "+MATCH_ALL1+" : "+MATCH_ALL2);
	        if((uriType != MATCH_ALL1) && (uriType != MATCH_ALL2) && (uriType != MATCH_ALL3)){
	        	throw new IllegalArgumentException("Invalid URI for insert");
	        }
	        
	        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
	        try {
	            long newID = -1;
	            if(uriType == MATCH_ALL1)
	            	newID = sqlDB.insertOrThrow(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_, null, values);
	            else if(uriType == MATCH_ALL2)
	            	newID = sqlDB.insertOrThrow(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, null, values);
	            else if(uriType == MATCH_ALL3)
	            	newID = sqlDB.insertOrThrow(CustCreditDBConstants.TABLE_CUS_CNTX, null, values);
	        	
	            
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
	        	SalesProCustActivityConstants.showErrorLog("Ignoring constraint failure : "+e.toString());
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
		            modSelection = new StringBuilder(CustCreditDBConstants.CUS_SER_COL_ID + "=" + id);
		
		            if (!TextUtils.isEmpty(selection)) {
		                modSelection.append(" AND " + selection);
		            }
		
		            rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_, values, modSelection.toString(), null);
		            break;
		        case MATCH_ALL1:
		            rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, values, selection, selectionArgs);
		            break;
		        case MATCH_ID2:
		        	id = uri.getLastPathSegment();
		            modSelection = new StringBuilder(CustCreditDBConstants.CUS_SEL_COL_ID + "=" + id);
		
		            if (!TextUtils.isEmpty(selection)) {
		                modSelection.append(" AND " + selection);
		            }
		
		            rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, values, modSelection.toString(), null);
		            break;
		        case MATCH_ALL2:
		        	rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, values, selection, selectionArgs);
		            break;
		        case MATCH_ID3:
		        	id = uri.getLastPathSegment();
		            modSelection = new StringBuilder(CustCreditDBConstants.CUS_CNTX_COL_ID + "=" + id);
		
		            if (!TextUtils.isEmpty(selection)) {
		                modSelection.append(" AND " + selection);
		            }
		
		            rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_CUS_CNTX, values, modSelection.toString(), null);
		            break;
		        case MATCH_ALL3:
		        	rowsAffected = sqlDB.update(CustCreditDBConstants.TABLE_CUS_CNTX, values, selection, selectionArgs);
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
		            rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_, selection, selectionArgs);
		            break;
		        case MATCH_ID1:
		            id = uri.getLastPathSegment();
		            if (TextUtils.isEmpty(selection)) {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_, CustCreditDBConstants.CUS_SER_COL_ID + "=" + id, null);
		            } 
		            else {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SEARCH_CUS_LIST_,
		                        selection + " and " + CustCreditDBConstants.CUS_SER_COL_ID + "=" + id, selectionArgs);
		            }
		            break;
		        case MATCH_ALL2:
		        	rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, selection, selectionArgs);
		            break;
		        case MATCH_ID2:
		        	id = uri.getLastPathSegment();
		            if (TextUtils.isEmpty(selection)) {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST, CustCreditDBConstants.CUS_SEL_COL_ID + "=" + id, null);
		            } 
		            else {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_SELECTED_CUS_LIST,
		                        selection + " and " + CustCreditDBConstants.CUS_SEL_COL_ID + "=" + id, selectionArgs);
		            }
		            break;
		        case MATCH_ALL3:
		        	rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_CUS_CNTX, selection, selectionArgs);
		            break;
		        case MATCH_ID3:
		        	id = uri.getLastPathSegment();
		            if (TextUtils.isEmpty(selection)) {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_CUS_CNTX, CustCreditDBConstants.CUS_CNTX_COL_ID + "=" + id, null);
		            } 
		            else {
		                rowsAffected = sqlDB.delete(CustCreditDBConstants.TABLE_CUS_CNTX,
		                        selection + " and " + CustCreditDBConstants.CUS_CNTX_COL_ID + "=" + id, selectionArgs);
		            }
		            break;		            
		        default:
		            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
	        }
	        
	        getContext().getContentResolver().notifyChange(uri, null);
	        return rowsAffected;
	    }	   
	        
}//end of PriceListCP