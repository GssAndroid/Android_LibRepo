package com.globalsoft.CalendarLib.Database;

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

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesProAppCrtConstraintsCP  extends ContentProvider {	
	
	private SalesProAppCrtConstraintsDB mDB;
	
	public static final String AUTHORITY = "com.globalsoft.CalendarLib.SalesProAppCrtConstraintsCP";
		
	//public static final int MATCH_ALL1 = 101;
	//public static final int MATCH_ALL4 = 104;
	public static final int MATCH_ALL1 = 102;
	public static final int MATCH_ALL2 = 103;
	public static final int MATCH_ALL3 = 105;
	
	//public static final int MATCH_ID1 = 201;
	//public static final int MATCH_ID4 = 204;
	public static final int MATCH_ID1 = 202;
	public static final int MATCH_ID2 = 203;
	public static final int MATCH_ID3 = 205;
	
	//private static final String OBJECTS_BASE_PATH1 = "objects1";
	//private static final String OBJECTS_BASE_PATH4 = "objects4";
	private static final String OBJECTS_BASE_PATH1 = "objects2";
	private static final String OBJECTS_BASE_PATH2 = "objects3";
	private static final String OBJECTS_BASE_PATH3 = "objects5";
	
    //public static final Uri CAT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH1);
	//public static final Uri STATUS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH4);
    public static final Uri APP_LIST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH1);
    public static final Uri APP_CUS_LIST_CAT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH2);
    public static final Uri APP_GAL_LIST_DET_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH3);
	
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/appointment-details";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/appointment-details";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
	static {
		/*sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1, MATCH_ALL1);
		sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH1 + "/#", MATCH_ID1);*/
		
		/*sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH4, MATCH_ALL4);
		sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH4 + "/#", MATCH_ID4);*/

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
		mDB = new SalesProAppCrtConstraintsDB(ctx);		
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
	        /*case MATCH_ALL1:
	            return CONTENT_TYPE;
	        case MATCH_ID1:
	            return CONTENT_ITEM_TYPE;*/
        	/*case MATCH_ALL4:
		        return CONTENT_TYPE;
		    case MATCH_ID4:
		        return CONTENT_ITEM_TYPE;*/
	        case MATCH_ALL1:
	            return CONTENT_TYPE;
	        case MATCH_ID1:
	            return CONTENT_ITEM_TYPE;
	        case MATCH_ALL2:
	            return CONTENT_TYPE;
	        case MATCH_ID2:
	            return CONTENT_ITEM_TYPE;
	        case MATCH_ALL3:
	            return CONTENT_TYPE;
	        case MATCH_ID3:
	            return CONTENT_ITEM_TYPE;
	        default:
	            return null;
        }
    }
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();        

        int uriType = sURIMatcher.match(uri);
        SapGenConstants.showLog("URI : "+uri+" : "+uriType+" : "+uri.getLastPathSegment());
        switch (uriType) {
	        /*case MATCH_ID1:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.TABLE_NAME);
	            queryBuilder.appendWhere(SalesProAppCrtConstraintsDB.COL_ID + "=" + uri.getLastPathSegment());
	            break;
	        case MATCH_ALL1:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.TABLE_NAME);
	            // no filter
	            break;*/
	       /* case MATCH_ID4:
		    	queryBuilder.setTables(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME);
		        queryBuilder.appendWhere(SalesProAppCrtConstraintsDB.STATUS_COL_ID + "=" + uri.getLastPathSegment());
		        break;
		    case MATCH_ALL4:
		    	queryBuilder.setTables(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME);
		        // no filter
		        break;*/
	        case MATCH_ID1:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME);
	            queryBuilder.appendWhere(SalesProAppCrtConstraintsDB.APP_LIST_COL_ID + "=" + uri.getLastPathSegment());
	            break;
	        case MATCH_ALL1:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME);
	            // no filter
	            break;
	        case MATCH_ID2:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME);
	            queryBuilder.appendWhere(SalesProAppCrtConstraintsDB.APP_CUS_LIST_COL_ID + "=" + uri.getLastPathSegment());
	            break;
	        case MATCH_ALL2:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME);
	            // no filter
	            break;
	        case MATCH_ID3:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME);
	            queryBuilder.appendWhere(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_ID + "=" + uri.getLastPathSegment());
	            break;
	        case MATCH_ALL3:
	        	queryBuilder.setTables(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME);
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
        if((uriType != MATCH_ALL1) && (uriType != MATCH_ALL2) && (uriType != MATCH_ALL3)){
        	throw new IllegalArgumentException("Invalid URI for insert");
        }
        
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        try {
            //long newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.TABLE_NAME, null, values);
            long newID = -1;
            /*if(uriType == MATCH_ALL1)
            	newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.TABLE_NAME, null, values);*/
            /*else if(uriType == MATCH_ALL4)
            	newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME, null, values);*/

            if(uriType == MATCH_ALL1)
            	newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME, null, values);
            else if(uriType == MATCH_ALL2)
            	newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME, null, values);
            else if(uriType == MATCH_ALL3)
            	newID = sqlDB.insertOrThrow(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME, null, values);
            
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
            SapGenConstants.showErrorLog("Ignoring constraint failure : "+e.toString());
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
	       /* case MATCH_ID1:
	            id = uri.getLastPathSegment();
	            modSelection = new StringBuilder(SalesProAppCrtConstraintsDB.COL_ID + "=" + id);
	
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.TABLE_NAME,
	                    values, modSelection.toString(), null);
	            break;
	        case MATCH_ALL1:
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.TABLE_NAME,
	                    values, selection, selectionArgs);
	            break;*/
        	/*case MATCH_ID4:
		        id = uri.getLastPathSegment();
		        modSelection = new StringBuilder(SalesProAppCrtConstraintsDB.STATUS_COL_ID + "=" + id);
		
		        if (!TextUtils.isEmpty(selection)) {
		            modSelection.append(" AND " + selection);
		        }
		
		        rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME,
		                values, modSelection.toString(), null);
		        break;
		    case MATCH_ALL4:
		        rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME,
		                values, selection, selectionArgs);
		        break;*/
	        case MATCH_ID1:
	            id = uri.getLastPathSegment();
	            modSelection = new StringBuilder(SalesProAppCrtConstraintsDB.APP_LIST_COL_ID + "=" + id);
	
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME,
	                    values, modSelection.toString(), null);
	            break;
	        case MATCH_ALL1:
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME,
	                    values, selection, selectionArgs);
	            break;
	        case MATCH_ID2:
	            id = uri.getLastPathSegment();
	            modSelection = new StringBuilder(SalesProAppCrtConstraintsDB.APP_CUS_LIST_COL_ID + "=" + id);
	
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME,
	                    values, modSelection.toString(), null);
	            break;
	        case MATCH_ALL2:
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME,
	                    values, selection, selectionArgs);
	            break;
	        case MATCH_ID3:
	            id = uri.getLastPathSegment();
	            modSelection = new StringBuilder(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_ID + "=" + id);
	
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME,
	                    values, modSelection.toString(), null);
	            break;
	        case MATCH_ALL3:
	            rowsAffected = sqlDB.update(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME,
	                    values, selection, selectionArgs);
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
	        /*case MATCH_ALL1:
	            rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.TABLE_NAME,
	                    selection, selectionArgs);
	            break;
	        case MATCH_ID1:
	            id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.TABLE_NAME, SalesProAppCrtConstraintsDB.COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.TABLE_NAME,
	                        selection + " and " + SalesProAppCrtConstraintsDB.COL_ID + "=" + id, selectionArgs);
	            }
	            break;*/

	       /*case MATCH_ALL4:
	            rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME,
	                    selection, selectionArgs);
	            break;
	       case MATCH_ID4:
	            id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME, SalesProAppCrtConstraintsDB.STATUS_COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.STATUS_TABLE_NAME,
	                        selection + " and " + SalesProAppCrtConstraintsDB.STATUS_COL_ID + "=" + id, selectionArgs);
	            }
	            break;*/
        	
	       case MATCH_ALL1:
	            rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME,
	                    selection, selectionArgs);
	            break;
	       case MATCH_ID1:
	            id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME, SalesProAppCrtConstraintsDB.APP_LIST_COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_LIST_TABLE_NAME,
	                        selection + " and " + SalesProAppCrtConstraintsDB.APP_LIST_COL_ID + "=" + id, selectionArgs);
	            }
	            break;
	       case MATCH_ALL2:
	            rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME,
	                    selection, selectionArgs);
	            break;
	       case MATCH_ID2:
	            id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME, SalesProAppCrtConstraintsDB.APP_CUS_LIST_COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_CUS_LIST_TABLE_NAME,
	                        selection + " and " + SalesProAppCrtConstraintsDB.APP_CUS_LIST_COL_ID + "=" + id, selectionArgs);
	            }
	            break;
	       case MATCH_ALL3:
	            rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME,
	                    selection, selectionArgs);
	            break;
	       case MATCH_ID3:
	            id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME, SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_TABLE_NAME,
	                        selection + " and " + SalesProAppCrtConstraintsDB.APP_GAL_LIST_DET_COL_ID + "=" + id, selectionArgs);
	            }
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }	
	
}//End of class SalesProAppCrtConstraintsCP