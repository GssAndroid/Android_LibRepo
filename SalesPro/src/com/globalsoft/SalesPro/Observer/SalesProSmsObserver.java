package com.globalsoft.SalesPro.Observer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;

import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesPro.SalesProSmsActivity;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

public class SalesProSmsObserver extends ContentObserver {
	
    private Context mContext;
    
    private String contactId = "", contactName = "", customerName = "";
    private String smsBodyStr = "", phoneNoStr = "";
    private long smsDatTime = System.currentTimeMillis();
    
	public SalesProSmsObserver(Handler handler, Context ctx) {
		super(handler);
		mContext = ctx;
	}
	
	public boolean deliverSelfNotifications() {
		return true;
	}

	public void onChange(boolean selfChange) {
		try{
			SalesOrderProConstants.showLog("Notification on SMS observer");
	        Cursor sms_sent_cursor = mContext.getContentResolver().query(SalesOrderProConstants.SMS_STATUS_URI, null, null, null, null);
	        if (sms_sent_cursor != null) {
		        if (sms_sent_cursor.moveToFirst()) {
		        	String protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
		        	SalesOrderProConstants.showLog("protocol : " + protocol);
		        	if(protocol == null){
		        		/*
		        		String[] colNames = sms_sent_cursor.getColumnNames();		        		
		        		if(colNames != null){
		        			for(int k=0; k<colNames.length; k++){
		        				SalesOrderProConstants.showLog("colNames["+k+"] : " + colNames[k]);
		        			}
		        		}
		        		*/
		        		int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
		        		SalesOrderProConstants.showLog("SMS Type : " + type);
		        		if(type == 2){
		        			/*
			        		SalesOrderProConstants.showLog("Id : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("_id")));
			        		SalesOrderProConstants.showLog("Thread Id : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("thread_id")));
			        		SalesOrderProConstants.showLog("Address : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")));
			        		SalesOrderProConstants.showLog("Person : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("person")));
			        		SalesOrderProConstants.showLog("Date : " + sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date")));
			        		SalesOrderProConstants.showLog("Read : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("read")));
			        		SalesOrderProConstants.showLog("Status : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("status")));
			        		SalesOrderProConstants.showLog("Type : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("type")));
			        		SalesOrderProConstants.showLog("Rep Path Present : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("reply_path_present")));
			        		SalesOrderProConstants.showLog("Subject : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("subject")));
			        		SalesOrderProConstants.showLog("Body : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")));
			        		SalesOrderProConstants.showLog("Err Code : " + sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("error_code")));
			        		*/
		        			smsBodyStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")).trim();
							phoneNoStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")).trim();
							smsDatTime = sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"));
							
							SalesOrderProConstants.showLog("SMS Content : "+smsBodyStr);
							SalesOrderProConstants.showLog("SMS Phone No : "+phoneNoStr);
							SalesOrderProConstants.showLog("SMS Time : "+smsDatTime);
							
							if(phoneNoStr != null)
								getSapContactDetails();
		        		}
		        	}
		        }
	        }
	        else
	        	SalesOrderProConstants.showLog("Send Cursor is Empty");
		}
		catch(Exception sggh){
			SalesOrderProConstants.showErrorLog("Error on onChange : "+sggh.toString());
		}
		super.onChange(selfChange);
	}//fn onChange
	
	private void retrieveContactRecord(String phoneNo) {
		try{
			contactId = "";
			contactName = "";
			Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNo));
			String[] projection = new String[] { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME };
			String selection = null;
			String[] selectionArgs = null;
			String sortOrder = ContactsContract.PhoneLookup.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
			ContentResolver cr = mContext.getContentResolver();
			if(cr != null){
				Cursor resultCur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
				if(resultCur != null){
					while (resultCur.moveToNext()) {                   
						contactId = resultCur.getString(resultCur.getColumnIndex(ContactsContract.PhoneLookup._ID));       
						contactName = resultCur.getString(resultCur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)); 
						SalesOrderProConstants.showLog("Contact Id : "+contactId);
						SalesOrderProConstants.showLog("Contact Name : "+contactName);
						break;
					}
					resultCur.close();
				}
			}
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in loadContactRecord : "+sfg.toString()); 
		}
	}//fn retrieveContactRecord
	
	
	private void getSapContactDetails(){
		ContactProSAPCPersistent contactProCusDbObj = null;
		try{
			String l_name = phoneNoStr.trim();
			
			if((l_name != null) && (!l_name.equalsIgnoreCase(""))){
				retrieveContactRecord(l_name);
				if((contactId != null) && (!contactId.equalsIgnoreCase(""))){
					customerName = getCustomerDetails(contactId);
					SalesOrderProConstants.showLog("Retrieved Contact Id : "+contactId+" : "+customerName);
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
	                	Intent intent = new Intent(mContext, SalesProSmsActivity.class);
	            		intent.putExtra("contactId", ContactsConstants.CONTACTSAPID);
	            		intent.putExtra("customerId", ContactsConstants.ONTACTSAPCUSID);
	            		intent.putExtra("contactFName", ContactsConstants.CONTACTSAPCUSFNAME);
	            		intent.putExtra("contactLName", ContactsConstants.CONTACTSAPCUSLNAME);
	            		intent.putExtra("smsPhoneNo", phoneNoStr);
	            		intent.putExtra("smsBody", smsBodyStr);
	            		intent.putExtra("smsDateTime", smsDatTime);
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
	
	private String getCustomerDetails(String contactId){
    	String strOrgType = "", strOrgName = "";
    	try{
			// Get value for Organization
	        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	     	String[] orgWhereParams = new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
	     	Cursor orgCur = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
	                    null, orgWhere, orgWhereParams, null);
	     	if (orgCur.moveToNext()) { 
	     		strOrgType = "";
	     		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
	     		strOrgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
	     		if(strOrgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_WORK))){
	     			strOrgName = orgName;
		 	    }     		
	     		
	     		if(strOrgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_OTHER))){
	     			if(strOrgName == null){
	         			strOrgName = orgName;      
	     			}		
		 	    }     	
	     	} 
	     	orgCur.close();
    	}
    	catch(Exception sgg){
    		SalesOrderProConstants.showErrorLog("Error in getCustomerDetails : "+sgg.toString());
    	}
    	finally{
	     	if(strOrgName == null || strOrgName.length() == 0){
	     		strOrgName = "";
			}
    	}
     	return strOrgName;
    }//fn getCustomerDetails
	
}//End of class SalesProSmsObserver
