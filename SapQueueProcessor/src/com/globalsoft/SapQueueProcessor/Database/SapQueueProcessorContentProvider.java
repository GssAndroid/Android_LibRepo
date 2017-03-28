package com.globalsoft.SapQueueProcessor.Database;

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

import com.globalsoft.SapQueueProcessor.Utils.SapQueueProcessorConstants;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SapQueueProcessorContentProvider extends ContentProvider {	
	
	private SapQueueProcessorDatabase mDB;
	
	public static final String AUTHORITY = "com.globalsoft.SapQueueProcessor.SapQueueProcessorContentProvider";
		
	public static final int MATCH_ALL = 101;
	public static final int MATCH_ID = 102;
	
	private static final String OBJECTS_BASE_PATH = "objects";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + OBJECTS_BASE_PATH);
	
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/queue-processor";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/queue-processor";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
	static {
		sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH, MATCH_ALL);
		sURIMatcher.addURI(AUTHORITY, OBJECTS_BASE_PATH + "/#", MATCH_ID);
	}
	
	@Override
	public boolean onCreate() {
		Context ctx = getContext();
		mDB = new SapQueueProcessorDatabase(ctx);	
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
	        case MATCH_ALL:
	            return CONTENT_TYPE;
	        case MATCH_ID:
	            return CONTENT_ITEM_TYPE;
	        default:
	            return null;
        }
    }
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR);

        int uriType = sURIMatcher.match(uri);
        SapQueueProcessorConstants.showLog("URI : "+uri+" : "+uriType+" : "+uri.getLastPathSegment());
        switch (uriType) {
	        case MATCH_ID:
	            queryBuilder.appendWhere(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID + "=" + uri.getLastPathSegment());
	            break;
	        case MATCH_ALL:
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
        if (uriType != MATCH_ALL) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        try {
            long newID = sqlDB.insertOrThrow(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR, null, values);
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
            SapQueueProcessorConstants.showErrorLog("Ignoring constraint failure : "+e.toString());
        }
        return null;
    }

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        int rowsAffected;
        String id = "";
        switch (uriType) {
	        case MATCH_ID:
	            id = uri.getLastPathSegment();
	            StringBuilder modSelection = new StringBuilder(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID + "=" + id);
	
	            if (!TextUtils.isEmpty(selection)) {
	                modSelection.append(" AND " + selection);
	            }
	
	            rowsAffected = sqlDB.update(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR,
	                    values, modSelection.toString(), null);
	            break;
	        case MATCH_ALL:
	            rowsAffected = sqlDB.update(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR,
	                    values, selection, selectionArgs);
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown or Invalid URI");
        }
        
        try {
			if((sqlDB != null) && (rowsAffected > 0)){
				if(!id.equalsIgnoreCase("")){
					int status = values.getAsInteger(SapQueueProcessorDBConstants.QUEUEPRSSR_COL_STATUS);
					//SapQueueProcessorConstants.showLog("Status on Update : "+status);
					if(status == SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER){
						String updateStmt = "UPDATE "+SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR+" SET "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT+" = "
						+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_PROCESS_COUNT+"+1 WHERE "+SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID+" = "+id+";";
						//SapQueueProcessorConstants.showLog("updateStmt : "+updateStmt);
						sqlDB.execSQL(updateStmt);
					}
				}
			}
		} catch (SQLException qe) {
			SapQueueProcessorConstants.showErrorLog("Error on update : "+qe.toString());
		}
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsAffected = 0;
        
        switch (uriType) {
	        case MATCH_ALL:
	            rowsAffected = sqlDB.delete(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR,
	                    selection, selectionArgs);
	            break;
	        case MATCH_ID:
	            String id = uri.getLastPathSegment();
	            if (TextUtils.isEmpty(selection)) {
	                rowsAffected = sqlDB.delete(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR, SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID + "=" + id, null);
	            } 
	            else {
	                rowsAffected = sqlDB.delete(SapQueueProcessorDBConstants.TABLE_QUEUEPRSSR,
	                        selection + " and " + SapQueueProcessorDBConstants.QUEUEPRSSR_COL_ID + "=" + id, selectionArgs);
	            }
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }	
	
}//End of class SapQueueProcessorContentProvider
