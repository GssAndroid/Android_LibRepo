package com.globalsoft.SalesPro.Receiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesPro.SalesProPhoneActivity;
import com.globalsoft.SalesPro.Constraints.SalesProPhoneLogRecord;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SalesPro.Utils.TimeUtilsStatic;

public class SalesProReceiver extends BroadcastReceiver {
	
	public static int mLastState = TelephonyManager.CALL_STATE_IDLE;
    public static SalesProPhoneLogRecord mRecord;
    public static List<SalesProPhoneLogRecord> mQueryRecords = new ArrayList<SalesProPhoneLogRecord>();
    public static final int TIME_DIFF_THRESHOLD = 1500;
    private Context mContext;
    private String contactId = "", contactName = "", customerName = "";
    
	public void onReceive(Context context, Intent intent) {
		try{
			mContext = context;
			MyPhoneStateListener phoneListener=new MyPhoneStateListener();
			TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
		catch(Exception sgh){
			SalesOrderProConstants.showErrorLog("Error in Init : "+sgh.toString());
		}
	}

	public class MyPhoneStateListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber){
			SalesProPhoneLogRecord mQueryRecord;
			try{
				switch(state){
					case TelephonyManager.CALL_STATE_IDLE:
						SalesOrderProConstants.showLog("IDLE");
						if (mLastState != TelephonyManager.CALL_STATE_IDLE) {
		  		    		SalesProPhoneLogRecord lQueryRecord = null;
		  		    		int lSize = mQueryRecords.size();
		  		    		SalesOrderProConstants.showLog("Size : "+lSize);
		  		    		for (int i = 0; i < lSize; ++i) {
		  		    			if (mQueryRecords.get(i).aplr_number.compareTo(incomingNumber)==0) {
		  		    				SalesOrderProConstants.showLog("query-all-match : "+mQueryRecords.get(i).aplr_number + ":" + mQueryRecords.get(i).aplr_time.toString());
		  		    				lQueryRecord = mQueryRecords.get(i);
		  		    				mRecord = getLastCallLog(mQueryRecords.get(i));
		  		    				break;
		  		    			}
		  		    		}
		  		    		
		  		    		List<SalesProPhoneLogRecord> lQueryRecords = new ArrayList<SalesProPhoneLogRecord>();
		  		    		if (lQueryRecord!=null) {
			  		    		for (int i = 0; i < lSize; ++i) {
			  		    			if (mQueryRecords.get(i).aplr_number.compareTo(lQueryRecord.aplr_number)!=0) {
			  		    				lQueryRecords.add(mQueryRecords.get(i));
			  		    			} 
			  		    		}
			  		    		mQueryRecords = lQueryRecords;
			  		    		lQueryRecords.clear();
		  		    		}
		  		    		
		  		    		mLastState = TelephonyManager.CALL_STATE_IDLE;
		  		    		
		  		    		if(mRecord != null){
		  		    			SalesOrderProConstants.showLog("PhoneIntentReceiver : "+mRecord.aplr_name + ":" + mRecord.aplr_number + ":" + mRecord.aplr_dataId + ":" + mRecord.aplr_duration + ":" + mRecord.aplr_id + ":" + mRecord.aplr_type);
		  		    			if((mRecord.aplr_name != null) && (mRecord.aplr_type == android.provider.CallLog.Calls.INCOMING_TYPE || mRecord.aplr_type == android.provider.CallLog.Calls.OUTGOING_TYPE))
		  		    				getSapContactDetails(mRecord);
		  		    			
		  		    		}
		  		    	}
		  		    	mLastState = TelephonyManager.CALL_STATE_IDLE;
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						SalesOrderProConstants.showLog("OFFHOOK");
						mLastState = TelephonyManager.CALL_STATE_OFFHOOK;
		  		    	mQueryRecord = new SalesProPhoneLogRecord();
		  		    	mQueryRecord.aplr_number = incomingNumber;
		  		    	mQueryRecord.aplr_time = new Date(System.currentTimeMillis());
		  		    	mQueryRecords.add(mQueryRecord);
		  		    	break;
					case TelephonyManager.CALL_STATE_RINGING:
						SalesOrderProConstants.showLog("RINGING");
						mLastState = TelephonyManager.CALL_STATE_RINGING;
		  		    	mQueryRecord = new SalesProPhoneLogRecord();
		  		    	mQueryRecord.aplr_number = incomingNumber;
		  		    	mQueryRecord.aplr_time = new Date(System.currentTimeMillis());
		  		    	mQueryRecords.add(mQueryRecord);
						break;
				}
			}
			catch(Exception sgsh){
				SalesOrderProConstants.showErrorLog("Error in PhoneListener : "+sgsh.toString());
			}
		} 
	}//End of class MyPhoneStateListener
	
	private SalesProPhoneLogRecord getLastCallLog(SalesProPhoneLogRecord pQueryRecord) {
    	SalesProPhoneLogRecord lRecord = null;
    	try{
	    	//query all all call logs
	    	while (true) {
		    	Cursor l_cur = mContext.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
		    			null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
		    	
		    	if(l_cur != null){
			    	//retrieve the information: number, name, time, type
			    	int l_idCol = l_cur.getColumnIndex(android.provider.CallLog.Calls._ID);
			    	int l_numberCol = l_cur.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
			    	int l_nameCol = l_cur.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME);
			    	int l_timeCol = l_cur.getColumnIndex(android.provider.CallLog.Calls.DATE);
			    	int l_typeCol = l_cur.getColumnIndex(android.provider.CallLog.Calls.TYPE);
			    	int l_durationCol = l_cur.getColumnIndex(android.provider.CallLog.Calls.DURATION);
			    	int l_logCount = 0;
			    	
			    	if (l_cur.moveToFirst()) {
						String l_number = l_cur.getString(l_numberCol);
						String l_name = l_cur.getString(l_nameCol);
						Date l_time = new Date(l_cur.getLong(l_timeCol));
						int l_type = l_cur.getInt(l_typeCol);
						int l_duration = l_cur.getInt(l_durationCol);
						int l_id = l_cur.getInt(l_idCol);
						SalesOrderProConstants.showLog("Date : "+l_time.getDate()+" : "+l_time.getDay()+" : "+l_time.getHours()+" : "+l_time.getTime()+" : "+l_time.toGMTString()+" : "+l_time.getTimezoneOffset());
						SalesOrderProConstants.showLog("Results : "+l_number+" : "+l_name+" : "+l_time+" : "+l_timeCol+" : "+l_type+" : "+l_duration+" : "+l_id);
						SalesOrderProConstants.showLog("log-time : "+l_time + ":" + String.valueOf(TimeUtilsStatic.DateToMilli(l_time)));
						SalesOrderProConstants.showLog("query-time : "+pQueryRecord.aplr_time + ":" + String.valueOf(TimeUtilsStatic.DateToMilli(pQueryRecord.aplr_time)));
						if (Math.abs(TimeUtilsStatic.DateToMilli(l_time) - TimeUtilsStatic.DateToMilli(pQueryRecord.aplr_time)) < TIME_DIFF_THRESHOLD) {
							lRecord = new SalesProPhoneLogRecord(l_number, l_name, l_time, l_type, l_duration, l_id, l_logCount);
							++l_logCount;
							break;
						} else {
							SystemClock.sleep(1000);
						}
			    	}
		    	}
	    	}//while
    	}
    	catch(Exception sfsg){
			SalesOrderProConstants.showErrorLog("Error in getLastCallLog : "+sfsg.toString()); 
		}
    	return lRecord;
    }//fn getLastCallLog
	
	private void retrieveContactRecord(String dispName) {
		try{
			contactId = "";
			contactName = "";
			
			//Uri uri = ContactsContract.Contacts.CONTENT_URI;
			Uri uri = ContactsContract.Data.CONTENT_URI;
			String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Organization.COMPANY, ContactsContract.CommonDataKinds.Organization.TYPE};
			String selection = ContactsContract.Contacts.DISPLAY_NAME + " = '"+ (dispName) + "'";
			String[] selectionArgs = null;
			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
			ContentResolver cr = mContext.getContentResolver();
			if(cr != null){
				Cursor resultCur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
				if(resultCur != null){
					while (resultCur.moveToNext()) {                   
						contactId = resultCur.getString(resultCur.getColumnIndex(ContactsContract.Contacts._ID));       
						contactName = resultCur.getString(resultCur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
						SalesOrderProConstants.showLog("Contact Id : "+contactId);
						SalesOrderProConstants.showLog("Contact Name : "+contactName);
						String orgName = resultCur.getString(resultCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
			     		String orgType = resultCur.getString(resultCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
			     		if(orgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_WORK))){
			     			customerName = orgName;
				 	    }     		
			     		
			     		if(orgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_OTHER))){
			     			if(customerName == null){
			     				customerName = orgName;     
			     			}		
				 	    } 
			     		
			     		SalesOrderProConstants.showLog("orgName : "+orgName);
			     		SalesOrderProConstants.showLog("orgType : "+orgType);
					}
					resultCur.close();
				}
			}
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in loadContactRecord : "+sfg.toString()); 
		}
	}//fn retrieveContactRecord
	
	private void getSapContactDetails(SalesProPhoneLogRecord mRecord){
		ContactProSAPCPersistent contactProCusDbObj = null;
		try{
			String l_name = mRecord.aplr_name.trim();
			
			if((l_name != null) && (!l_name.equalsIgnoreCase(""))){
				retrieveContactRecord(l_name);
				if((contactId != null) && (!contactId.equalsIgnoreCase(""))){
					SalesOrderProConstants.showLog("Retrieved Contact Id : "+contactId);
					if(contactProCusDbObj == null){
						contactProCusDbObj = new ContactProSAPCPersistent(mContext);
					}
            		contactProCusDbObj.getContactDetails(String.valueOf(contactId));
	                contactProCusDbObj.closeDBHelper();
	                SalesOrderProConstants.showLog("SAP Contact Id : "+ContactsConstants.CONTACTSAPID);
	                SalesOrderProConstants.showLog("SAP Customer Id : "+ContactsConstants.ONTACTSAPCUSID);
	                SalesOrderProConstants.showLog("SAP Customer FName : "+ContactsConstants.CONTACTSAPCUSFNAME);
	                SalesOrderProConstants.showLog("SAP Customer LName : "+ContactsConstants.CONTACTSAPCUSLNAME);
	                if((ContactsConstants.ONTACTSAPCUSID != null) && (!ContactsConstants.ONTACTSAPCUSID.equalsIgnoreCase(""))){
	                	SalesOrderProConstants.showLog("Before Intent");
	                	Intent intent = new Intent(mContext, SalesProPhoneActivity.class);
	            		intent.putExtra("contactId", ContactsConstants.CONTACTSAPID);
	            		intent.putExtra("customerId", ContactsConstants.ONTACTSAPCUSID);
	            		intent.putExtra("contactFName", ContactsConstants.CONTACTSAPCUSFNAME);
	            		intent.putExtra("contactLName", ContactsConstants.CONTACTSAPCUSLNAME);
	            		intent.putExtra("mRecord", mRecord);
	            		intent.putExtra("customerName", customerName);
	            		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	            		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            		SalesOrderProConstants.showLog("Before Start Activity");
	            		mContext.startActivity(intent);
	                }
				}
			}
		}
		catch(Exception dghh){
			SalesOrderProConstants.showErrorLog("Error in getSapContactDetails : "+dghh.toString());
		}
	}//fn getSapContactDetails
	
}//End of class SalesProReceiver