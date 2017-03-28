package com.globalsoft.SalesPro.Receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesPro.SalesProSmsActivity;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

public class SalesProSmsReceiver  extends BroadcastReceiver {
	
    private Context mContext;
    private Bundle mBundle;
    
    private String contactId = "", contactName = "", customerName = "";
    private String smsBodyStr = "", phoneNoStr = "";
    private long smsDatTime = System.currentTimeMillis();
    
    public void onReceive(Context context, Intent intent) {
		try{
			mContext = context;
			mBundle = intent.getExtras();  
			
		    if (mBundle != null){
		    	getSMSDetails();
		    }
		    else
		    	SalesOrderProConstants.showLog("Bundle is Empty!");
		}
		catch(Exception sgh){
			SalesOrderProConstants.showErrorLog("Error in Init : "+sgh.toString());
		}
	}//fn onReceive

	private void getSMSDetails(){	     
	    SmsMessage[] msgs = null;
		try{
			Object[] pdus = (Object[]) mBundle.get("pdus");
			if(pdus != null){
				msgs = new SmsMessage[pdus.length];
				SalesOrderProConstants.showLog("pdus length : "+pdus.length);
				for(int k=0; k<msgs.length; k++){
					msgs[k] = SmsMessage.createFromPdu((byte[])pdus[k]);  
					/*
					SalesOrderProConstants.showLog("getDisplayMessageBody : "+msgs[k].getDisplayMessageBody());
					SalesOrderProConstants.showLog("getDisplayOriginatingAddress : "+msgs[k].getDisplayOriginatingAddress());
					SalesOrderProConstants.showLog("getMessageBody : "+msgs[k].getMessageBody());
					SalesOrderProConstants.showLog("getOriginatingAddress : "+msgs[k].getOriginatingAddress());
					SalesOrderProConstants.showLog("getProtocolIdentifier : "+msgs[k].getProtocolIdentifier());
					SalesOrderProConstants.showLog("getStatus : "+msgs[k].getStatus());
					SalesOrderProConstants.showLog("getStatusOnIcc : "+msgs[k].getStatusOnIcc());
					SalesOrderProConstants.showLog("getStatusOnSim : "+msgs[k].getStatusOnSim());
					*/
					smsBodyStr = msgs[k].getMessageBody().trim();
					phoneNoStr = msgs[k].getOriginatingAddress().trim();
					smsDatTime = msgs[k].getTimestampMillis();
					
					SalesOrderProConstants.showLog("SMS Content : "+smsBodyStr);
					SalesOrderProConstants.showLog("SMS Phone No : "+phoneNoStr);
					SalesOrderProConstants.showLog("SMS Time : "+smsDatTime);
				}
			}
			
			if(phoneNoStr != null)
				getSapContactDetails();
		}
		catch(Exception sfgh){
			SalesOrderProConstants.showLog("Error in getSMSDetails : "+sfgh.toString());
		}
	}//fn getSMSDetails
	
	
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
	
}//End of class SalesProSmsReceiver

