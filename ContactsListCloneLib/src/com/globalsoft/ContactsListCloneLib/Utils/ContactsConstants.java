package com.globalsoft.ContactsListCloneLib.Utils;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.globalsoft.SapLibSoap.Utils.ToastExpander;

public final class ContactsConstants {

	//Log Related Constants
    public static final String CONTACTPRO_TAG = "ContactLib ";
    public static final String CONTACTPRO_ERRORTAG = "ContactLib Error "; 
    
    //CONTACT DETAILS CONTSTANTS
    public static String CONTACTSAPID = "";
    public static String ONTACTSAPCUSID = "";
    public static String CONTACTSAPCUSFNAME = "";
    public static String CONTACTSAPCUSLNAME = "";
    public static String CONTACTFLAGVALUE = "";
    public static boolean VIEWUPDATIONCALLFLAG = true;
    public static boolean VIEWREFRESHCALLFLAG = false;
    
    public static String APPL_NAME_CALL_INTER_VIEW = "InterActionsView";
    public static String APPL_NAME_CALL_ACTIVITY_CRE = "ActivityCreation";
    public static String APPL_NAME_CALL_SO_CRE = "SOCreation";
    public static String APPL_NAME_CALL_SO_VIEW = "SOView";
    public static String APPL_NAME_CALL_CUST_VIEW = "CustomerView";
    
    //Application Name Constants
    public static String APPLN_NAME_STR = "";
    public static String APPLN_PACKAGE_NAME = "com.globalsoft.SalesPro"; 
    public static String APPLN_BGSERVICE_NAME = "com.globalsoft.SalesPro.Service.BackgroundService";
    
    //Service Related Constants
    public static boolean START_ALRACTIVITY = true;
    public static int POPUPALERTTIME = 60000;
    
    //Soap response related constants
    public static final int RESP_TYPE_GET_EMP_CONT = 801;
    public static final int RESP_TYPE_GET_UPDATE_CHECK_FROM_SAP = 802;
    public static final int RESP_TYPE_GET_EDIT_2_SAP = 803;
    public static final int RESP_TYPE_ADD_SYNC_2_SAP = 804;
    public static final int RESP_TYPE_ADD_ALREXITCUS_SYNC_2_SAP = 805;
    
    //QUEUE PARAMS CONSTANTS
    public static final String QUEUE_COLID = "COLID";
    public static final String QUEUE_APPREFID = "APPREFID";
    public static final String QUEUE_SOAPAPINAME = "SOAPAPINAME";
    public static final String QUEUE_APPLNAME = "APPLICATIONNAME";
    public static final String QUEUE_RESULTSOAPOBJ = "RESULTSOAPOBJ";
    public static final String QUEUE_REQUESTSOAPOBJ = "REQUESTSOAPOBJ";    
    public static final String QUEUE_ERR_APPREFID = "ERRAPPREFID";
    public static final String QUEUE_ERR_MSG = "ERRMSG";
    public static String QUEUE_NOTIFICATION = "QUEUENOTIFY";
    public static final String COL_APPNAME = "appname";
    public static final String COL_STATUS = "status";
    public static final String COL_ID = BaseColumns._ID;

    //QUEUE STATUS CONSTANTS
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_INPROCESS = 1;
    //public static final int STATUS_SENDTOSERVER = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_HIGHPRIORITY = 100;
    
    public static String SOAP_RESP_MSG = "";
    public static String SOAP_ERR_TYPE = ""; 
    
    public static final String CONTACT_MAINTAIN_API = "CONTACT-MAINTAIN";
    public static final String PHONE_ACTIVITY_TELEPHONE_CALL_API = "ACTIVITY-FOR-TELEPHONE-CALL-CREATE";
    public static final String PHONE_ACTIVITY_SMS_MESSAGE_API = "ACTIVITY-FOR-SMS-MESSAGE-CREATE";
    
    //For Timer 
    public static int TIMER_CONST = 3000; // 10000 is 10secs or 30000 is 30secs or 60000 is 1 min
    
    //Contact SORT CONSTANTS
    public static int CONTACT_SORT_NAME = 0;
	//Log related functions
    public static void showLog(String text){
    	Log.e(ContactsConstants.CONTACTPRO_TAG,text);
    }
    
    public static void showErrorLog(String text){
    	Log.e(ContactsConstants.CONTACTPRO_ERRORTAG,text);
    }
    
    public static void showErrorDialog(Context ctx, String text){
    	Toast aToast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
    	ToastExpander.showFor(aToast, 5000); 
    }
    
    /*public static void showErrorDialog(Context ctx, String text){
    	Toast.makeText(ctx, text, 2000).show();
    }  */  
	
	public static String[] getContactOrgDetails(Context ctx, String contactId){
		String strOrgName = "", strOrgTitle = "", strOrgType = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = "";
         		try {
					strOrgName = orgName;
					strOrgTitle = orgTitle;
					orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
					strOrgType = orgType;
				} catch (Exception sse) {
					ContactsConstants.showErrorLog("Error in getContactOrgDetails1:"+sse.getMessage());
					strOrgType = orgType;//2 is a others
					strOrgName = orgName;
					strOrgTitle = orgTitle;
				}     		
         	} 
         	orgCur.close();
         	
         	if(strOrgName == null || strOrgName.length() == 0){
    	 		strOrgName = "";
    		}
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}
         	result[0] = strOrgName;
         	result[1] = strOrgType;
         	result[2] = strOrgTitle;
		} 
    	catch (Exception ssee) {
    		ContactsConstants.showErrorLog("Error in getContactOrgDetails2:"+ssee.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactOrgDetails
	
	public static String[] getContactPhDetails(Context ctx, String contactId){
		String strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "";
		String[] result = new String[4];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Phone no
    		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    						null, 
    						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    						new String[]{String.valueOf(contactId)}, null);
            while (pCur.moveToNext()) {
            	String phValue = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            	String phType = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            	if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME))){
            		strPhoneHome = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
    	 	    	strPhoneOther = phValue;
    	 	    }
            } 
            pCur.close();
            if(strPhoneHome == null || strPhoneHome.length() == 0){
            	strPhoneHome = "";
    		}
            if(strPhoneMob == null || strPhoneMob.length() == 0){
            	strPhoneMob = "";
    		}
            if(strPhoneWork == null || strPhoneWork.length() == 0){
            	strPhoneWork = "";
    		}
            if(strPhoneOther == null || strPhoneOther.length() == 0){
            	strPhoneOther = "";
    		}
         	result[0] = strPhoneHome;
         	result[1] = strPhoneMob;
         	result[2] = strPhoneWork;
         	result[3] = strPhoneOther;
		} 
    	catch (Exception ssq) {
    		ContactsConstants.showErrorLog("Error in getContactPhDetails:"+ssq.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
		}
    	return result;
    }//fn getContactPhDetails
	
	public static String[] getContactEmailsDetails(Context ctx, String contactId){
		String strEmailHome = "", strEmailWork = "", strEmailOther = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{String.valueOf(contactId)}, null); 
    		while (emailCur.moveToNext()) { 
    		    // This would allow you get several email addresses
    	            // if the email addresses were stored in an array
    		    String email = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    	 	    String emailType = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_HOME))){
    	 	    	strEmailHome = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	}
    		emailCur.close();
    		if(strEmailHome == null || strEmailHome.length() == 0){
    			strEmailHome = "";
    		}
    		if(strEmailWork == null || strEmailWork.length() == 0){
    			strEmailWork = "";
    		}
    		if(strEmailOther == null || strEmailOther.length() == 0){
    			strEmailOther = "";
    		}
         	result[0] = strEmailHome;
         	result[1] = strEmailWork;
         	result[2] = strEmailOther;
		} 
    	catch (Exception ssqw) {
    		ContactsConstants.showErrorLog("Error in getContactEmailsDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactEmailsDetails
	
	public static String[] getContactMsgDetails(Context ctx, String contactId){
		String strImId = "", strImType = "";
		String[] result = new String[2];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for ImType & instants
    		String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    	 	String[] imWhereParams = new String[]{String.valueOf(contactId), 
    	 	    ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}; 
    	 	Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI, 
    	            null, imWhere, imWhereParams, null); 
    	 	if (imCur.moveToFirst()) { 
    	 	    String imName = imCur.getString(
    	                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
    	 	    String imType;
    	 	    imType = imCur.getString(
    	                 imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
    	 	    
    	 	   strImId = imName;
    	 	   strImType = imType;
    	 	} 
    	 	imCur.close();
    	 	if(strImId == null || strImId.length() == 0){
    	 		strImId = "";
    		}
    	 	if(strImType == null || strImType.length() == 0){
    	 		strImType = "";
    		}
         	result[0] = strImId;
         	result[1] = strImType;
		} 
    	catch (Exception ssqw) {
    		ContactsConstants.showErrorLog("Error in getContactMsgDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
		}
    	return result;
    }//fn getContactMsgDetails
	
	public static String[] getContactAddressDetails(Context ctx, String contactId){
		String strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "";
		String[] result = new String[6];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Address Details
    		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    		String[] addrWhereParams = new String[]{String.valueOf(contactId), 
    			ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
    		Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
    	                null, addrWhere, addrWhereParams, null); 
    		while(addrCur.moveToNext()) {
    	 		String street = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
    	 		String city = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
    	 		String region = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
    	 		String postalCode = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
    	 		String country = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
    	 		String type = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME))){
             		strStreet = street;
             		strCity = city;
             		strRegion = region;
             		strPostalCode = postalCode;
             		strCountry = country;
             		strAddType = type;// 1 is home
    	 	    }     	
    	 	} 
    		addrCur.close();
    		if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
    	 	    	 	
    	 	addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
	                null, addrWhere, addrWhereParams, null);
    		if(strStreet.length() <= 0 && strCity.length() <= 0 && strRegion.length() <= 0 && strPostalCode.length() <= 0 && strCountry.length() <= 0){  
    			while(addrCur.moveToNext()) {
	    	 		String street = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	    	 		String city = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	    	 		String region = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	    	 		String postalCode = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	    	 		String country = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	    	 		String type = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
	    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK))){
	             		strStreet = street;
	             		strCity = city;
	             		strRegion = region;
	             		strPostalCode = postalCode;
	             		strCountry = country;
	             		strAddType = type;// 1 is home
	    	 	    }     	
	    	 	}     			  			
    		}
    		addrCur.close();
    		
    	 	if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
         	result[0] = strStreet;
         	result[1] = strCity;
         	result[2] = strRegion;
         	result[3] = strPostalCode;
         	result[4] = strCountry;
         	result[5] = strAddType;
		} 
    	catch (Exception ssqw) {
    		ContactsConstants.showErrorLog("Error in getContactAddressDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
         	result[4] = "";
         	result[5] = "";
		}
    	return result;
    }//fn getContactAddressDetails
	
	/*public static void saveOfflineContentToQueueProcessor(Context ctx, String appRefId, String className, String apiName, SoapObject soapObj){
        byte[] soapBytes = null;
        try{
            if(soapObj != null){
                soapBytes = ContactsConstants.getSerializableSoapObject(soapObj);
                if(soapBytes != null){
                    ContentResolver resolver = ctx.getContentResolver();
                    ContentValues val = new ContentValues();
                    if((appRefId != null) && (!appRefId.equalsIgnoreCase(""))){
                    	 val.put(SapQueueProcessorContentProvider.COL_APPREFID, appRefId);
                    }
                    val.put(SapQueueProcessorContentProvider.COL_APPNAME, ContactsConstants.APPLN_NAME_STR);
                    val.put(SapQueueProcessorContentProvider.COL_PCKGNAME, ContactsConstants.APPLN_PACKAGE_NAME);
                    val.put(SapQueueProcessorContentProvider.COL_CLASSNAME, className);
                    val.put(SapQueueProcessorContentProvider.COL_FUNCNAME, apiName);
                    val.put(SapQueueProcessorContentProvider.COL_SOAPDATA, soapBytes);
                    resolver.insert(SapQueueProcessorContentProvider.CONTENT_URI, val);
                }
                else
                	ContactsConstants.showErrorLog("Soap byte Conversion is Null");
            }
            else
            	ContactsConstants.showErrorLog("Offline Soap Object is Null");
        }
        catch(Exception sgh){
        	ContactsConstants.showErrorLog("Error in saveOfflineContent : "+sgh.toString());
        }
    }//fn saveOfflineContentToQueueProcessor
	
	public static byte[] getSerializableSoapObject(SoapObject soapObj){
    	byte[] soapBytes = null;
    	try {
    		if(soapObj != null){
	    		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    		envelope.setOutputSoapObject(soapObj);
	    		
	    		XmlSerializer aSerializer = Xml.newSerializer();
	    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		try {
		    		aSerializer.setOutput(baos, "UTF-8");
		    		envelope.write(aSerializer);
		    		aSerializer.flush();
	    		} catch (Exception ewwe) {
	    			ContactsConstants.showErrorLog("Error on getSerializableSoapObject1 : "+ewwe.toString());
	    		}
	    		
	    		if(baos != null){
	    			soapBytes = baos.toByteArray();
	    		}
    		}
		} 
    	catch (Exception es) {
    		ContactsConstants.showErrorLog("Error on getSerializableSoapObject2 : "+es.toString());
		}
    	return soapBytes;
    }//fn getSerializableSoapObject
	
	public static SoapObject getDeSerializableSoapObject(byte[] soapBytes){
    	SoapObject soapObj = null;
    	try {
    		if(soapBytes != null){
    			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    		try {
	    			 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    	         factory.setNamespaceAware(true);
	    	         XmlPullParser xpp = factory.newPullParser();
	    	         String soapEnvStr = new String(soapBytes);
	    	         xpp.setInput(new StringReader(soapEnvStr));
	    	         envelope.parse(xpp);
	    	         soapObj = (SoapObject)envelope.bodyIn;

	    		} catch (Exception ewwe) {
	    			ContactsConstants.showErrorLog("Error on getDeSerializableSoapObject1 : "+ewwe.toString());
	    		}
    		}
		} 
    	catch (Exception es) {
    		ContactsConstants.showErrorLog("Error on getDeSerializableSoapObject2 : "+es.toString());
		}
    	return soapObj;
    }//fn getDeSerializableSoapObject
	
	public static void updateSelectedRowStatus(Context ctx, int colId, String appName, int status){
        Uri uri = null;
        String whereStr = null;
        String[] whereParams = null;
        try{
            if(colId > 0)
                uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"/"+colId);
            else{
                uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
                if(appName == null)
                    appName = "";
                
                whereStr = ContactsConstants.COL_APPNAME + " = ? And "+ContactsConstants.COL_STATUS+ " = ?";
                whereParams = new String[]{appName, String.valueOf(ContactsConstants.STATUS_IDLE)}; 
            }
            
            //ServiceProConstants.showLog("status before : "+status);
            if(status < ContactsConstants.STATUS_IDLE)
                status = ContactsConstants.STATUS_IDLE;
            else if(status > ContactsConstants.STATUS_HIGHPRIORITY)
                status = ContactsConstants.STATUS_HIGHPRIORITY;
            //ServiceProConstants.showLog("status after : "+status);
            
            ContentValues updateContent = new ContentValues();
            updateContent.put(ContactsConstants.COL_STATUS, status);
 
            ContentResolver resolver = ctx.getContentResolver();
            int rows = resolver.update(uri, updateContent, whereStr, whereParams);
        }
        catch(Exception qsewe){
        	ContactsConstants.showErrorLog("Error in updateSelectedRowStatus : "+qsewe.toString());
        }    
    }//fn updateSelectedRowStatus
	
	public static int getApplicationQueueCount(Context ctx, String appName){
        int count = -1;
        Cursor cursor = null;
        try{
            if(appName == null)
                appName = "";
            
            Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
            
            ContentResolver resolver = ctx.getContentResolver();
            
            String selection = ContactsConstants.COL_APPNAME + " = ? And ("+ContactsConstants.COL_STATUS+ " != ? And "+ContactsConstants.COL_STATUS+ " != ? )";
            String[] selectionParams = new String[]{appName,  String.valueOf(ContactsConstants.STATUS_SENDTOSERVER ),  String.valueOf(ContactsConstants.STATUS_COMPLETED )};
            String[] projection = new String[]{ContactsConstants.COL_ID}; 
            
            cursor = resolver.query(uri, projection, selection, selectionParams, null);
            
            if(cursor != null){
                count = cursor.getCount();
            }
        }
        catch(Exception sfag){
        	ContactsConstants.showErrorLog("Error in getApplicationQueueCount : "+sfag.toString());
        }
        finally{
            if(cursor != null)
                cursor.close();    
        }
        return count;
    }//fn getApplicationQueueCount
*/	
	public static void soapResponse(Context ctx, SoapObject soap, boolean offline){
		String taskErrorMsgStr="", errorDesc="", errType="";
        if(soap != null){
        	//ServiceProConstants.showLog("Count : "+soap.getPropertyCount());
            try{ 
                String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[50];
                int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;
                for (int i = 0; i < soap.getPropertyCount(); i++) {                
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    //ServiceProConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
                            if(j > 0){
                                result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                firstIndex = firstIndex + 3;
                                result = result.substring(firstIndex);
                                //SapTasksConstants.Log(result);
                                
                                resC = 0;
                                indexA = 0;
                                indexB = result.indexOf(delimeter);
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    resArray[resC] = res;
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    resC++;
                                }
                                int endIndex = result.lastIndexOf(';');
                                resArray[resC] = result.substring(indexA,endIndex);
                            }
                            else if(j == 0){
                                String errorMsg = pii.getProperty(j).toString();
                                //ServiceProConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                            	int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                //ServiceProConstants.showLog("Msg:"+taskErrorMsgStr);
	                                if(!offline)
	                                	ContactsConstants.showErrorDialog(ctx, taskErrorMsgStr);
	                            	errorDesc = taskErrorMsgStr;	
	                            	SOAP_RESP_MSG = errorDesc; 
                                }
	                            int typeFstIndex = errorMsg.indexOf("Type=");
	                            if(typeFstIndex > 0){
	                            	int typeLstIndex = errorMsg.indexOf(";", typeFstIndex);
	                                String taskErrorTypeMsgStr = errorMsg.substring((typeFstIndex+"Type=".length()), typeLstIndex);
	                                //ServiceProConstants.showLog("Type:"+taskErrorTypeMsgStr);
	                                errType = taskErrorTypeMsgStr;
	                            	SOAP_ERR_TYPE = errType;
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception sff){
            	ContactsConstants.showErrorLog("On soapResponse : "+sff.toString());
            }
        }
    }//fn soapResponse
	
	public static boolean getSoapResponseSucc_Err(String soapMsg){
    	boolean resMsgErr = false;
    	try{
        	if((soapMsg.indexOf("Type=A") > 0) || (soapMsg.indexOf("Type=E") > 0) || (soapMsg.indexOf("Type=X") > 0)){
        		resMsgErr = true;
            }else if(soapMsg.indexOf("Type=S") > 0){
            	resMsgErr = false;
            }   
	    }
	    catch(Exception sffe){
	    	ContactsConstants.showErrorLog("Error in getSoapResponseSucc_Err : "+sffe.toString());
	    }
    	return resMsgErr;
    }//fn getSoapResponseSucc_Err
    
}//End of class ContactsConstants    