package com.globalsoft.SalesOrderLib.Database;



import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
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

public class SalesOrderCP extends ContentProvider{
		private SalesOrderDB mDB;		
		public static final String AUTHORITY = "com.globalsoft.SalesPro.SalesOrderCP";
		
		public static final int MATCH_ALL1 = 603080;
		public static final int MATCH_ALL2 = 87960;
		public static final int MATCH_ALL3 = 786543;
		public static final int MATCH_ALL4 = 85432;
		public static final int MATCH_ALL5 = 234573;
		public static final int MATCH_ALL6 = 645098;
		public static final int MATCH_ALL7 = 756189;
		
		
		public static final int MATCH_ID1 = 247689;
		public static final int MATCH_ID2 = 564356;
		public static final int MATCH_ID3 = 789067;
		public static final int MATCH_ID4 = 76548;
		public static final int MATCH_ID5 = 43526;
		public static final int MATCH_ID6 = 490324;
		public static final int MATCH_ID7 = 501435;
		
		
		private static final String OBJECTS_BASE_PATH1 = "objects1";
		private static final String OBJECTS_BASE_PATH2 = "objects2";
		private static final String OBJECTS_BASE_PATH3 = "objects3";
		private static final String OBJECTS_BASE_PATH4 = "objects4";
		private static final String OBJECTS_BASE_PATH5 = "objects5";
		private static final String OBJECTS_BASE_PATH6 = "objects6";
		private static final String OBJECTS_BASE_PATH7 = "objects7";
		
		

		 public static final Uri SO_MAIN_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH1);
		 public static final Uri SO_HEAD_OP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH2);
		 public static final Uri SO_CUST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH3);
		 public static final Uri SO_MATT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH4);
		 public static final Uri SO_HEAD_PRICE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH5);
		 public static final Uri SO_ITEM_PRICE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH6);
		 public static final Uri SO_CREATE_SCREEN_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH7);
				 
		    
		 public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/soitem-details";
	     public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/soheadop-details";
		    
		 private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		 
		 static {
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1, MATCH_ALL1);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1 + "/#", MATCH_ID1);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2, MATCH_ALL2);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH2 + "/#", MATCH_ID2);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH3, MATCH_ALL3);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH3 + "/#", MATCH_ID3);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH4, MATCH_ALL4);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH4 + "/#", MATCH_ID4);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH5, MATCH_ALL5);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH5 + "/#", MATCH_ID5);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH6, MATCH_ALL6);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH6 + "/#", MATCH_ID6);
				
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH7, MATCH_ALL7);
				sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH7 + "/#", MATCH_ID7);
								
			}
			
			@Override
			public boolean onCreate() {
				Context ctx = getContext();
				mDB = new SalesOrderDB(ctx);		
				return true;
			}
		 
		 @Override
			public String getType(Uri uri) {
		        int uriType = sURIMatcher.match(uri);
		        
		        if((uriType == MATCH_ALL1) || (uriType == MATCH_ALL2) ||(uriType == MATCH_ALL3) || (uriType == MATCH_ALL4) || (uriType == MATCH_ALL5) || (uriType == MATCH_ALL6) || (uriType == MATCH_ALL7)){
		        	return CONTENT_TYPE;
		        }
		        else if((uriType == MATCH_ID1)|| (uriType == MATCH_ID2) || (uriType == MATCH_ID3) || (uriType == MATCH_ID4)|| (uriType == MATCH_ID5) || (uriType == MATCH_ID6) || (uriType == MATCH_ID7)){
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
		        SalesOrderConstants.showLog("URI : "+uri+" : "+uriType+" : "+uri.getLastPathSegment());
		        switch (uriType) {
			        case MATCH_ID1:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SALESORDER_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_MAIN_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL1:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SALESORDER_LIST);
			            // no filter
			            break;
			        case MATCH_ID2:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_HEAD_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL2:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST);
			            // no filter
			            break;
			            
			        case MATCH_ID3:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL3:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST);
			            // no filter
			            break;
			            
			        case MATCH_ID4:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_MATT_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_MATT_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL4:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_MATT_LIST);
			            // no filter
			            break;
			        case MATCH_ID5:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL5:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST);
			            // no filter
			            break;
			            
			        case MATCH_ID6:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL6:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST);
			            // no filter
			            break;		
			            
			        case MATCH_ID7:
			            queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST);
			            queryBuilder.appendWhere(SalesOrderDBConstants.SO_CREATE_COL_ID + "=" + uri.getLastPathSegment());
			            break;
			        case MATCH_ALL7:
			        	 queryBuilder.setTables(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST);
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
			 //SalesOrderConstants.showLog("uriType : "+uriType+" : "+MATCH_ALL1+" : ");
		        if((uriType != MATCH_ALL1) && (uriType != MATCH_ALL2) && (uriType != MATCH_ALL3) && (uriType != MATCH_ALL4) && (uriType != MATCH_ALL5) && (uriType != MATCH_ALL6) && (uriType != MATCH_ALL7) ){
		        	throw new IllegalArgumentException("Invalid URI for insert");
		        }
		        
		        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
		        try {
		            long newID = -1;
		            if(uriType == MATCH_ALL1)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SALESORDER_LIST, null, values);
		            else if(uriType == MATCH_ALL2)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST, null, values);
		            else if(uriType == MATCH_ALL3)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST, null, values);
		            else if(uriType == MATCH_ALL4)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_MATT_LIST, null, values);
		            else if(uriType == MATCH_ALL5)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST, null, values);
		            else if(uriType == MATCH_ALL6)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST, null, values);
		            else if(uriType == MATCH_ALL7)
		            	newID = sqlDB.insertOrThrow(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, null, values);
		          
		            
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
		        	SalesOrderConstants.showErrorLog("Ignoring constraint failure : "+e.toString());
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
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_MAIN_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SALESORDER_LIST, values, modSelection.toString(), null);
			            break;
			            
			        case MATCH_ALL1:
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SALESORDER_LIST, values, selection, selectionArgs);
			            break;
			        case MATCH_ID2:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_HEAD_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL2:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST, values, selection, selectionArgs);
			            break;
			        case MATCH_ID3:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL3:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST, values, selection, selectionArgs);
			            break;
			            
			        case MATCH_ID4:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_MATT_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_MATT_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL4:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_MATT_LIST, values, selection, selectionArgs);
			            break;
			            
			        case MATCH_ID5:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL5:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST, values, selection, selectionArgs);
			            break;
			            
			        case MATCH_ID6:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL6:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST, values, selection, selectionArgs);
			            break;		
			            
			        case MATCH_ID7:
			        	id = uri.getLastPathSegment();
			            modSelection = new StringBuilder(SalesOrderDBConstants.SO_CREATE_COL_ID + "=" + id);
			
			            if (!TextUtils.isEmpty(selection)) {
			                modSelection.append(" AND " + selection);
			            }
			
			            rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, values, modSelection.toString(), null);
			            break;
			        case MATCH_ALL7:
			        	rowsAffected = sqlDB.update(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, values, selection, selectionArgs);
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
			            rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SALESORDER_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID1:
			            id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SALESORDER_LIST, SalesOrderDBConstants.SO_MAIN_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SALESORDER_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_MAIN_COL_ID + "=" + id, selectionArgs);
			            }
			            break;
			        
			        case MATCH_ALL2:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID2:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST, SalesOrderDBConstants.SO_HEAD_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_OP_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_HEAD_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			        case MATCH_ALL3:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID3:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST, SalesOrderDBConstants.SO_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CUSTOMER_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			            
			        case MATCH_ALL4:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_MATT_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID4:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_MATT_LIST, SalesOrderDBConstants.SO_MATT_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_MATT_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_MATT_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			            
			        case MATCH_ALL5:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID5:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST, SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_HEAD_PRICE_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_HEAD_PRICE_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			            
			        case MATCH_ALL6:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID6:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST, SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_ITEM_PRICE_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_ITEM_PRICE_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			         
			        case MATCH_ALL7:
			        	rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, selection, selectionArgs);
			            break;
			        case MATCH_ID7:
			        	id = uri.getLastPathSegment();
			            if (TextUtils.isEmpty(selection)) {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST, SalesOrderDBConstants.SO_CREATE_COL_ID + "=" + id, null);
			            } 
			            else {
			                rowsAffected = sqlDB.delete(SalesOrderDBConstants.TABLE_SO_CREATE_TABLE_LIST,
			                        selection + " and " + SalesOrderDBConstants.SO_CREATE_COL_ID + "=" + id, selectionArgs);
			            }
			            break;   
			        default:
			            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		        }
		        
		        getContext().getContentResolver().notifyChange(uri, null);
		        return rowsAffected;
		    }	   
	
}//SalesOrderCP
