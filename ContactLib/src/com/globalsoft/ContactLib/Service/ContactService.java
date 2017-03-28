package com.globalsoft.ContactLib.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.util.Log;

import com.globalsoft.ContactLib.AlrtActivity;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationKeyOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProInputConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProOutputConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ContactService extends Service {
	private static final String TAG = "ContactService";
	final Handler service_Handler = new Handler();
	private ContactProSAPCPersistent contactProCusDbObj = null;
	private String selectedContactId = null;
	private String lastId = null;
	private static String strContactSAPIDValue = "";
	private static String strContactSAPCusIDValue = "";
	private static String strContactSAPCusFNameValue = "";
	private static String strContactSAPCusLNameValue = "";
	public String strNewContactSAPCusFName = "";
    public String strNewContactSAPCusLName = "";
    private String strNewContactStreetName = "";
    private String strNewContactCityName = "";
    private String strNewContactStateName = "";
    private SoapObject resultSoap = null;
    
    private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private ContactProContactCreationOpConstraints catObj;
    private ArrayList updationForEdit = new ArrayList();
    private ArrayList updationForAdd = new ArrayList();
    private ArrayList updationForAddExitCus = new ArrayList();
    private boolean postflag = false;
    private boolean syncflag = true;
    private boolean syncalertshow = true;
    
    //For Adding 
    private String strAddContactSAPCusFName = "";
	private String strAddContactSAPCusLName = "";
	private String strAddContactSAPCusEmailHome = "";
	private String strAddContactSAPCusEmailWork = "";
	private String strAddContactSAPCusEmailOther = "";
	
	//For Timer 
	private TimerTask scanTask; 
	private final Handler handler = new Handler(); 
	private Timer t = new Timer();  
	private AlertDialog alertDialog = null;
	private boolean timerFlag = true; 
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");		
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {	
		Log.e(TAG, "onStart");
		String valueName = intent.getStringExtra("valueName");
		String timerChange = intent.getStringExtra("timerChange");
		Log.d(TAG, "valueName:"+valueName);
		Log.d(TAG, "timerChange:"+timerChange);
		if(timerChange.trim().equalsIgnoreCase("false")){
			if(valueName.trim().equalsIgnoreCase("false")){
				stoptimer();
				stopSelf();
			}
			else{
				startUpdates();		
			}
			ContactsConstants.showLog("dochangetimer fun not calling");
		}
		else{
			dochangetimer();
			ContactsConstants.showLog("dochangetimer fun calling");
		}
	}
	
	//if chanced timer means, we are reschedule the timer task.
	public void dochangetimer(){
    	try{     	
    		ContactsConstants.showLog("ContactsConstants.POPUPALERTTIME:"+ContactsConstants.POPUPALERTTIME);
    		if(scanTask!=null){ 
    			scanTask.cancel(); 
    		}
	        scanTask = new TimerTask() { 
	        	public void run() { 
	        		ContactsConstants.showLog("TIMER: Timer func calling!");  
                	syncflag = true;
            		syncalertshow = true;
                	showUpdateAlert();
	        	}; 
	        };
	        t.schedule(scanTask, ContactsConstants.POPUPALERTTIME); 	        
    	}
    	catch(Exception qw){
    		ContactsConstants.showErrorLog("Error in dochangetimer:"+qw.toString());
    	}  
    }//fn dochangetimer 
		
	private void startUpdates(){
		try{		
			startUpdationInit();
			Thread t = new Thread() 
			{
	            public void run() 
				{
	            	try {    					            		
	        			startUpdationFun();    					            		
					} catch (Exception e) {
						ContactsConstants.showLog("Error in service calling function:"+e.toString());
					}	    		
				}    		                		            
	        };
	        t.start();
		} catch (Exception e) {  
			ContactsConstants.showErrorLog("Error in startUpdates:"+e.toString());
		}					
	}//fn startUpdates
    
    private void startUpdationInit(){
    	if(contactProCusDbObj == null){
			contactProCusDbObj = new ContactProSAPCPersistent(this);
		}       	
		/*String idValue[] = contactProCusDbObj.getChangesNeedForEdit();
		if(idValue != null && idValue.length != 0){
			for(int i=0;i<idValue.length;i++){
				ContactsConstants.showLog("idValue:"+idValue[i]);
				String id = idValue[i];
				updationForEdit.add(id);				
			}
		}
		
		String idValueForAdd[] = contactProCusDbObj.getChangesNeedForAdd();
		if(idValueForAdd != null && idValueForAdd.length != 0){
			for(int i=0;i<idValueForAdd.length;i++){
				ContactsConstants.showLog("idValueForAdd:"+idValueForAdd[i]);
				String id = idValueForAdd[i];
				updationForAdd.add(id);
			}		
		}*/		
    }//fn startUpdationInit
    
    private void startUpdationFun(){
    	if(updationForEdit.size() > 0){
    		selectedContactId = (String) updationForEdit.get(0);
    		postflag = false;
    		initSoapConnectionForContactEditSynToSAP();
    	}
    	else if(updationForAdd.size() > 0){
    		lastId = (String) updationForAdd.get(0);
    		postflag = false;
			initSoapConnectionForAddContactSynToSAP();
    	}
    	else{
    		postflag = true;
    		service_Handler.post(response_call);
    	}
    }//fn startUpdationFun
    
    public void showUpdateAlert(){
    	if(ContactsConstants.START_ALRACTIVITY == true){    		
    		ContactsConstants.showLog("START_ALRACTIVITY:"+ContactsConstants.START_ALRACTIVITY); 
    		if(updationForAddExitCus.size() > 0){
		    	ContactsConstants.showLog("Before callAlrtActivity!"); 
				callAlrtActivity();
				ContactsConstants.showLog("After callAlrtActivity!"); 
	    	}
    		else{
    			stoptimer();
    			stopSelf();
    		}
    	}
    	else{
    		ContactsConstants.showLog("START_ALRACTIVITY:"+ContactsConstants.START_ALRACTIVITY);
    	}
    }//fn showUpdateAlert    
            
    private void callAlrtActivity(){
    	Intent intent = new Intent(this, AlrtActivity.class);	
    	intent.putStringArrayListExtra("updationForAddExitCus", updationForAddExitCus);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.getApplicationContext().startActivity(intent); 
    }
    
    public void dostarttimer(){
    	try{     	
    		scanTask = new TimerTask() { 
            public void run() { 
                handler.post(new Runnable() { 
                    public void run() { 
                    	ContactsConstants.showLog("TIMER: Timer started");  
                    	syncflag = true;
                		syncalertshow = true;
                    	showUpdateAlert();
                    } 
               }); 
            }};
	        t.schedule(scanTask, 300, ContactsConstants.POPUPALERTTIME);
    	}
    	catch(Exception qw){
    		ContactsConstants.showErrorLog("Error in dostarttimer:"+qw.toString());
    	}  
    }//fn dostarttimer 
           
    public void stoptimer(){  
    	try{
	       if(scanTask!=null){ 
	    	   ContactsConstants.showLog("TIMER: Timer canceled"); 
	    	   scanTask.cancel(); 
	       } 
    	}
    	catch(Exception qw){
    		ContactsConstants.showErrorLog("Error in stoptimer:"+qw.toString());
    	}  
    }//fn stoptimer
           
    final Runnable response_call = new Runnable(){
	    public void run()
	    {
	    	if(postflag){
		    	try{	    		
		    		if(updationForAddExitCus.size() > 0){
		    			syncflag = true;
		    			dostarttimer();
		        	}
		    		else{
		    			syncflag = true;
			    		syncalertshow = true;
			    		timerFlag = true;
			    		stopSelf();
		    		}		    		
		    	} catch(Exception e){
		    		ContactsConstants.showLog("Error in activity calling function:"+e.toString());
		    	}
	    	}
	    	else{
		    	startUpdationFun();
	    	}
	    }	    
    };
    
    private void initSoapConnectionForContactEditSynToSAP(){   
    	SoapSerializationEnvelope envelopeC = null;
        try{        	     
        	String strOrgName = "", strOrgTitle = "", strOrgType = "", 
    		strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "", 
    		strEmailHome = "", strEmailWork = "", strEmailOther = "", strImId = "", strImType = "",
    		strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "",
    		strContactSAPID = "", strContactSAPCusID = "", strContactSAPCusFName = "", strContactSAPCusLName = "";
    		
    		ContentResolver cr = getContentResolver();
    		
    		// Get value for Organization
    		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, String.valueOf(selectedContactId)); 
    		strOrgName = OrgDetails[0];
    		strOrgType = OrgDetails[1];
    		strOrgTitle = OrgDetails[2];

         	// Get value for Phone no
         	String[] PhNos = ContactsConstants.getContactPhDetails(this, String.valueOf(selectedContactId));
         	strPhoneHome = PhNos[0];
         	strPhoneMob = PhNos[1];
         	strPhoneWork = PhNos[2];
         	strPhoneOther = PhNos[3];
            
            // Get value for Emails
         	String[] Emails = ContactsConstants.getContactEmailsDetails(this, String.valueOf(selectedContactId));
         	strEmailHome = Emails[0];
         	strEmailWork = Emails[1];
         	strEmailOther = Emails[2];
    				    		
    		// Get value for ImType & instants
         	String[] Msg_Details = ContactsConstants.getContactMsgDetails(this, String.valueOf(selectedContactId));
         	strImId = Msg_Details[0];
         	strImType = Msg_Details[1];
    	 
    	 	// Get value for Address Details
		 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, String.valueOf(selectedContactId));         	
         	strStreet = AddressDetails[0];
         	strCity = AddressDetails[1];
         	strRegion = AddressDetails[2];
         	strPostalCode = AddressDetails[3];
         	strCountry = AddressDetails[4];
         	strAddType = AddressDetails[5];
         	/*
    		// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(selectedContactId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
         		if(orgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_WORK))){
         			strOrgType = orgType;//2 is a others
         			strOrgName = orgName;
             		strOrgTitle = orgTitle;
    	 	    }     		
         	} 
         	orgCur.close();
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgName == null || strOrgName.length() == 0){
         		strOrgName = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}

    		// Get value for Phone no
    		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    						null, 
    						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    						new String[]{String.valueOf(selectedContactId)}, null);
            while (pCur.moveToNext()) {
            	String phValue = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            	String phType = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            	if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME))){
            		strPhoneHome = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
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
    	    
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
    							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
    							null,
    							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
    							new String[]{String.valueOf(selectedContactId)}, null); 
    		ContactsConstants.showLog("emailCur size:"+emailCur.getCount());
    		ContactsConstants.showLog("selectedContactId:"+selectedContactId);
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
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	    ContactsConstants.showLog("emailCur email:"+email+"  "+emailType);
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
    				
    		// Get value for ImType & instants
    		String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    	 	String[] imWhereParams = new String[]{String.valueOf(selectedContactId), 
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
    		
    	 	// Get value for Address Details
    		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    		String[] addrWhereParams = new String[]{String.valueOf(selectedContactId), 
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
    		if(strStreet.length() == 0 && strCity.length() == 0 && strRegion.length() == 0 && strPostalCode.length() == 0 && strCountry.length() == 0){    			 
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
         	 */
    		if(contactProCusDbObj == null){
    			contactProCusDbObj = new ContactProSAPCPersistent(this);
    		}
    		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
    		contactProCusDbObj.closeDBHelper();
    		strContactSAPID = ContactsConstants.CONTACTSAPID;
    		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;
	     	
    		if(strContactSAPID.length() != 0){    			
    			// Get value for Givenname and middlename
        		Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, 
        						null, 
        						ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID +" = ?", 
        						new String[]{String.valueOf(selectedContactId)}, null);
                //if (nameCur.moveToFirst()) {
                while(nameCur.moveToNext()) {
                	String givenNameValue = nameCur.getString(
                			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                	String middleNameValue = nameCur.getString(
                			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));            	
        	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
        	 	    strContactSAPCusFName = givenNameValue;
       	 	    	strContactSAPCusLName = middleNameValue;
       	 	    	
       	 	    	if(middleNameValue != null){
       	 	    		break;
       	 	    	}
                } 
                nameCur.close();
                if(strContactSAPCusFName == null || strContactSAPCusFName.length() == 0){
                	strContactSAPCusFName = "";
        		}
                if(strContactSAPCusLName == null || strContactSAPCusLName.length() == 0){
                	strContactSAPCusLName = "";
        		}
        		
        		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 
                //------------------------------------------------------ First Names 
                if(strContactSAPCusFName.length() != 0) 
                { 
                    String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
            		.withSelection(selectPhone, phoneArgs) 
            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strContactSAPCusFName+" "+strContactSAPCusLName+"                    "+ContactsConstants.CONTACTSAPID).build()); 
                }
                else{
                	if(strEmailHome.length() != 0){
                    	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
                		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
                		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
                		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
                		.withSelection(selectPhone, phoneArgs) 
                		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strEmailHome+"                    "+ContactsConstants.CONTACTSAPID).build());
                    }
                	else if(strEmailWork.length() != 0){
                    	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
                		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
                		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
                		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
                		.withSelection(selectPhone, phoneArgs) 
                		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strEmailWork+"                    "+ContactsConstants.CONTACTSAPID).build());
                    }
                	else if(strEmailOther.length() != 0){
                    	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
                		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
                		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
                		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
                		.withSelection(selectPhone, phoneArgs) 
                		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strEmailOther+"                    "+ContactsConstants.CONTACTSAPID).build());
                    }
                	else{
                    	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
                		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
                		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
                		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
                		.withSelection(selectPhone, phoneArgs) 
                		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Unknown"+"                    "+ContactsConstants.CONTACTSAPID).build());
                	}
                }
                try {
        			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        		} catch (RemoteException e) {
        			// TODO Auto-generated catch block
        			ContactsConstants.showErrorLog("Error in initSoapConnectionForContactEditSynToSAP for getting contacts from sap:"+e.toString());
        		} catch (OperationApplicationException e) {
        			// TODO Auto-generated catch block
        			ContactsConstants.showErrorLog("Error in initSoapConnectionForContactEditSynToSAP for getting contacts from sap:"+e.toString());
        		}	
        		
	        	ContactsConstants.showLog("initSoapConnection from if");
	        	SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME);    
	            envelopeC = null;
	            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            
	            ContactProInputConstraints C0[];
	            C0 = new ContactProInputConstraints[5];
	            for(int k=0; k<5; k++){
	                C0[k] = new ContactProInputConstraints(); 
	            }                        
	            
	            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
	            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
	            C0[2].Cdata = "EVENT[.]CONTACT-MAINTAIN[.]VERSION[.]0";             
	            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTCRTN20[.]PARNR[.]NAME_FIRST[.]NAME_LAST[.]FNCTN[.]TELF_WORK[.]TELF_MOBILE[.]TELF_PRSNL[.]TELF_OTHR[.]EMAIL_WORK[.]EMAIL_PRSNL[.]EMAIL_OTHR[.]IM_TYPE[.]IM_ID[.]STRASP[.]ORT01P[.]REGIOP[.]PSTLZP[.]LAND1P[.]KUNNR[.]KUNNR_NAME1";
	            C0[4].Cdata = "ZGSXCAST_CNTCTCRTN20[.]"+strContactSAPID+"[.]"+strContactSAPCusFName+"[.]"+strContactSAPCusLName+"[.]"+strOrgTitle+"[.]"+strPhoneWork+"[.]"+strPhoneMob+"[.]"+strPhoneHome+"[.]"+strPhoneOther+"[.]"+strEmailWork+"[.]"+strEmailHome+"[.]"+strEmailOther+"[.]"+strImType+"[.]"+strImId+"[.]"+strStreet+"[.]"+strCity+"[.]"+strRegion+"[.]"+strPostalCode+"[.]"+strCountry+"[.]"+strContactSAPCusID+"[.]"+strOrgName;
	            
	            strContactSAPIDValue = strContactSAPID;
	            strContactSAPCusIDValue = strContactSAPCusID;
	            strContactSAPCusFNameValue = strContactSAPCusFName;
	            strContactSAPCusLNameValue = strContactSAPCusLName;
	            
	            Vector listVect = new Vector();
	            for(int k1=0; k1<C0.length; k1++){
	                listVect.addElement(C0[k1]);
	            }
	                                 
	            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
	            envelopeC.setOutputSoapObject(request);        
	            ContactsConstants.showLog(request.toString());
	            
	            startNetworkConnectionForContactEditSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
    		}
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForContactEditSynToSAP for getting contacts from sap:"+asd.toString());
        }
    }//fn initSoapConnectionForContactEditSynToSAP
	
	private void startNetworkConnectionForContactEditSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {		
			getSOAPViaHTTPForContactEditSynToSAP(envelopeCE, url);                    				
		} catch (Exception ae) {
			ContactsConstants.showErrorLog(ae.toString());
		}
    }//fn startNetworkConnectionForContactEditSynToSAP
    
    private void getSOAPViaHTTPForContactEditSynToSAP(SoapSerializationEnvelope envelopeCE, String url){		
        try {
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	ContactsConstants.showErrorLog("Data handling error : "+ex2);
            	ContactsConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                ContactsConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	ContactsConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	ContactsConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
            ContactsConstants.showLog("output:"+resultSoap.toString());	
        }
        catch (Throwable e) {
        	ContactsConstants.showErrorLog("Error in getSOAPViaHTTPForContactEditSynToSAP:"+e.toString());
        }
        finally {                     
        	ContactsConstants.showLog("========END OF LOG========"); 
        	updateReportsConfirmResponseForEditSynToSAP(resultSoap);
        	/*((Activity) this.getApplicationContext()).runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForEditSynToSAP(resultSoap);
                }
            });*/
        }
    }//fn getSOAPViaHTTPForContactEditSynToSAP
    
    public void updateReportsConfirmResponseForEditSynToSAP(SoapObject soap){		
        try{ 
        	if(soap != null){
        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
    			ContactProOutputConstraints category = null;
	    			            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[37];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                ContactsConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCTCRTN20")){
	                                if(category != null)
	                                    category = null;
	                                    
	                                category = new ContactProOutputConstraints(resArray);	                              
	                                
	                            	if(j == 3){
	                            	}	                                                        
	                            }	                           
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            ContactsConstants.showLog("********************************");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                ContactsConstants.showLog(taskErrorMsgStr);
	                                //ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                        ContactsConstants.showLog("Inside J  "+j);
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForEditSynToSAP:"+sff.toString());
        } 
        finally{   	           	
        	if(strContactSAPCusFNameValue == null || strContactSAPCusFNameValue.length() == 0){
        		strContactSAPCusFNameValue = "";
        	}
        	if(strContactSAPCusLNameValue == null || strContactSAPCusLNameValue.length() == 0){
        		strContactSAPCusLNameValue = "";
        	}
        	if(contactProCusDbObj == null){
    			contactProCusDbObj = new ContactProSAPCPersistent(this);
    		}
        	
        	ContactsConstants.showLog("Updating data to local databases : "+selectedContactId);
			//updating data to local device DB
			contactProCusDbObj.update_data(String.valueOf(selectedContactId), strContactSAPIDValue.trim(), 
					strContactSAPCusIDValue.trim(), strContactSAPCusFNameValue.trim(), 
					strContactSAPCusLNameValue.trim());
			contactProCusDbObj.closeDBHelper();
			
			ContactsConstants.showLog("strContactSAPIDValue : "+strContactSAPIDValue.trim());
			ContactsConstants.showLog("strContactSAPCusIDValue : "+strContactSAPCusIDValue.trim());
			ContactsConstants.showLog("strContactSAPCusFNameValue : "+strContactSAPCusFNameValue.trim());
			ContactsConstants.showLog("strContactSAPCusLNameValue : "+strContactSAPCusLNameValue.trim());
			
			for(int v = 0;v<updationForEdit.size();v++){
				ContactsConstants.showLog("before deletion for updationForEdit vector : "+(String)updationForEdit.get(v));
			}
			updationForEdit.remove(selectedContactId);
			for(int v = 0;v<updationForEdit.size();v++){
				ContactsConstants.showLog("After deletion for updationForEdit vector : "+(String)updationForEdit.get(v));
			}
			service_Handler.post(response_call);
        }
    }//End of updateReportsConfirmResponseForEditSynToSAP fn	
    
    private void initSoapConnectionForAddContactSynToSAP(){   
    	SoapSerializationEnvelope envelopeC = null;
        try{        	     
        	String strOrgName = "", strOrgTitle = "", strOrgType = "", 
    		strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "", 
    		strEmailHome = "", strEmailWork = "", strEmailOther = "", strImId = "", strImType = "",
    		strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "",
    		strContactSAPID = "", strContactSAPCusID = "", strContactSAPCusFName = "", strContactSAPCusLName = "";
    		
    		ContentResolver cr = getContentResolver();
    		
    		// Get value for Organization
    		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, String.valueOf(lastId)); 
    		strOrgName = OrgDetails[0];
    		strOrgType = OrgDetails[1];
    		strOrgTitle = OrgDetails[2];

         	// Get value for Phone no
         	String[] PhNos = ContactsConstants.getContactPhDetails(this, String.valueOf(lastId));
         	strPhoneHome = PhNos[0];
         	strPhoneMob = PhNos[1];
         	strPhoneWork = PhNos[2];
         	strPhoneOther = PhNos[3];
            
            // Get value for Emails
         	String[] Emails = ContactsConstants.getContactEmailsDetails(this, String.valueOf(lastId));
         	strEmailHome = Emails[0];
         	strEmailWork = Emails[1];
         	strEmailOther = Emails[2];
    				    		
    		// Get value for ImType & instants
         	String[] Msg_Details = ContactsConstants.getContactMsgDetails(this, String.valueOf(lastId));
         	strImId = Msg_Details[0];
         	strImType = Msg_Details[1];
    	 
    	 	// Get value for Address Details
		 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, String.valueOf(lastId));         	
         	strStreet = AddressDetails[0];
         	strCity = AddressDetails[1];
         	strRegion = AddressDetails[2];
         	strPostalCode = AddressDetails[3];
         	strCountry = AddressDetails[4];
         	strAddType = AddressDetails[5];
         	
    		/*// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(lastId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
         		if(orgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_WORK))){
         			strOrgType = orgType;//2 is a others
         			strOrgName = orgName;
             		strOrgTitle = orgTitle;
    	 	    }     		
         	} 
         	if(strOrgTitle == null){
         		strOrgTitle = "";
         	}         		
         	orgCur.close();
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgName == null || strOrgName.length() == 0){
         		strOrgName = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}

    		// Get value for Phone no
    		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    						null, 
    						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    						new String[]{String.valueOf(lastId)}, null);
            while (pCur.moveToNext()) {
            	String phValue = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            	String phType = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            	if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME))){
            		strPhoneHome = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
    	 	    	strPhoneOther = phValue;
    	 	    }
    	 	    ContactsConstants.showLog("ph:"+phValue+"  "+phType);
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
            
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
    							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
    							null,
    							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
    							new String[]{String.valueOf(lastId)}, null); 
    		ContactsConstants.showLog("emailCur size:"+emailCur.getCount());
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
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	    ContactsConstants.showLog("emailCur email:"+email+"  "+emailType);
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
    		}*/    		
    				
    		/*// Get value for ImType & instants
    		String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    	 	String[] imWhereParams = new String[]{String.valueOf(lastId), 
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
    	 	
    	 	ContactsConstants.showLog("lastId:"+lastId);
    	 	// Get value for Address Details
    		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    		String[] addrWhereParams = new String[]{String.valueOf(lastId), 
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
    		if(strStreet.length() == 0 && strCity.length() == 0 && strRegion.length() == 0 && strPostalCode.length() == 0 && strCountry.length() == 0){    			 
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
    		}*/
            
         	strAddContactSAPCusEmailHome = strEmailHome;
    		strAddContactSAPCusEmailWork = strEmailWork;
    		strAddContactSAPCusEmailOther = strEmailOther;
    	 	strNewContactStreetName = strStreet.toString().trim();
    	    strNewContactCityName = strCity.toString().trim();
    	    strNewContactStateName = strRegion.toString().trim();
    	    
            String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] nameWhereParams = new String[]{String.valueOf(lastId), 
         		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
         	Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
                        null, nameWhere, nameWhereParams, null);
         	if (nameCur.moveToNext()) { 
         		String givenNameValue = nameCur.getString(
    	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
    	    	String middleNameValue = nameCur.getString(
    	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
    	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
    	 	    strContactSAPCusFName = givenNameValue;
  	 	    	strContactSAPCusLName = middleNameValue;
         	} 
         	nameCur.close();
         	strNewContactSAPCusFName = strContactSAPCusFName;
         	strNewContactSAPCusLName = strContactSAPCusLName;
         	if(strNewContactSAPCusFName == null || strNewContactSAPCusFName.length() == 0){
         		strNewContactSAPCusFName = "";
    		}
         	if(strNewContactSAPCusLName == null || strNewContactSAPCusLName.length() == 0){
         		strNewContactSAPCusLName = "";
    		}
         	strAddContactSAPCusFName = strContactSAPCusFName;
         	strAddContactSAPCusLName = strContactSAPCusLName;
    		    		
         	selectedContactId = lastId;
        	ContactsConstants.showLog("initSoapConnection");
        	SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME);    
            envelopeC = null;
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            ContactProInputConstraints C0[];
            C0 = new ContactProInputConstraints[5];
            for(int k=0; k<5; k++){
                C0[k] = new ContactProInputConstraints(); 
            }                        
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]CONTACT-MAINTAIN[.]VERSION[.]0";             
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTCRTN20[.]PARNR[.]NAME_FIRST[.]NAME_LAST[.]FNCTN[.]TELF_WORK[.]TELF_MOBILE[.]TELF_PRSNL[.]TELF_OTHR[.]EMAIL_WORK[.]EMAIL_PRSNL[.]EMAIL_OTHR[.]IM_TYPE[.]IM_ID[.]STRASP[.]ORT01P[.]REGIOP[.]PSTLZP[.]LAND1P[.]KUNNR[.]KUNNR_NAME1";
            C0[4].Cdata = "ZGSXCAST_CNTCTCRTN20[.]"+strContactSAPID+"[.]"+strContactSAPCusFName+"[.]"+strContactSAPCusLName+"[.]"+strOrgTitle+"[.]"+strPhoneWork+"[.]"+strPhoneMob+"[.]"+strPhoneHome+"[.]"+strPhoneOther+"[.]"+strEmailWork+"[.]"+strEmailHome+"[.]"+strEmailOther+"[.]"+strImType+"[.]"+strImId+"[.]"+strStreet+"[.]"+strCity+"[.]"+strRegion+"[.]"+strPostalCode+"[.]"+strCountry+"[.]"+strContactSAPCusID+"[.]"+strOrgName;
                                                
            strContactSAPIDValue = strContactSAPID;
            strContactSAPCusIDValue = strContactSAPCusID;
            strContactSAPCusFNameValue = strContactSAPCusFName;
            strContactSAPCusLNameValue = strContactSAPCusLName;
            
            Vector listVect = new Vector();
            for(int k1=0; k1<C0.length; k1++){
                listVect.addElement(C0[k1]);
            }
                                 
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
            envelopeC.setOutputSoapObject(request);        
            ContactsConstants.showLog(request.toString());
            
            startNetworkConnectionForAddContactSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactSynToSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForAddContactSynToSAP
	
	private void startNetworkConnectionForAddContactSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {  
    		getSOAPViaHTTPForAddContactSynToSAP(envelopeCE, url);                    				
		} catch (Exception ae) {
			ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactSynToSAP:"+ae.toString());
		}
    }//fn startNetworkConnectionForContactEditSynToSAP
    
    private void getSOAPViaHTTPForAddContactSynToSAP(SoapSerializationEnvelope envelopeCE, String url){		
        try {
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	ContactsConstants.showErrorLog("Data handling error : "+ex2);
            	ContactsConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                ContactsConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	ContactsConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                ContactsConstants.showErrorDialog(ctx, extStr.toString());
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	ContactsConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactSynToSAP:"+e.toString());
        }
        finally {                     
        	ContactsConstants.showLog("========END OF LOG========");
        	updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
        	/*((Activity) this.getApplicationContext()).runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
                }
            });*/
        }
    }//End of startNetworkConnectionForAddContactSynToSAP fn
    
    public void updateReportsConfirmResponseForAddContactSynToSAP(SoapObject soap){	
    	boolean listDisplay = false;
        try{ 
        	if(soap != null){
        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
        		ContactProContactCreationOpConstraints category = null;
        		ContactProContactCreationKeyOpConstraints categoryKey = null;
	           	            	       
        		if(cusList != null){
	            	cusList.clear();
	            	sapCusData.clear();
	            }
        		
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[37];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                ContactsConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CMPNYCNTCTDATA01")){
	                                if(category != null)
	                                    category = null;
	                                    
	                                category = new ContactProContactCreationOpConstraints(resArray);	
	                                if(category != null){
	                                	cusList.add(category.getKunnr().trim()+", "+category.getName1().trim()+", "+category.getOrto1k().trim()+", "+category.getRegiok().trim()+", "+category.getLand1k().trim());
	                                }
	                                if(category != null)
	                                	sapCusData.add(category);
	                    			listDisplay = true;
	                    			//ContactsConstants.showLog("cusList size : "+cusList.size());
	                            }	  
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCTKEY")){
	                                if(categoryKey != null)
	                                	categoryKey = null;
	                                    
	                                categoryKey = new ContactProContactCreationKeyOpConstraints(resArray);	
	                                
	                    			ContactsConstants.showLog("cusListKey getKunnr : "+categoryKey.getKunnr());
	                    			ContactsConstants.showLog("cusListKey getParnr : "+categoryKey.getParnr());

	                    			if(contactProCusDbObj == null){
	                        			contactProCusDbObj = new ContactProSAPCPersistent(this);
	                        		}
                    				//updatind data to local device DB
                    				contactProCusDbObj.update_row_data(lastId, lastId, categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
                    						strNewContactSAPCusLName.toString().trim());		
                    				contactProCusDbObj.closeDBHelper();
                    				                    				                    				
                    				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 		                               
	                                //------------------------------------------------------ First Names 
	                                if(strAddContactSAPCusFName.length() != 0) 
	                                { 
		                                String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
	                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
	                            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
	                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
	                            		.withSelection(selectPhone, phoneArgs) 
	                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusFName+" "+strAddContactSAPCusLName+"                    "+categoryKey.getParnr()).build()); 
	                            	}
	                                else{
	                                	if(strAddContactSAPCusEmailHome.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailHome+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else if(strAddContactSAPCusEmailWork.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailWork+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else if(strAddContactSAPCusEmailOther.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailOther+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else{
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(selectedContactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Unknown"+"                    "+categoryKey.getParnr()).build());
	                                	}		                                	
	                                	try 
		                                { 
		                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
		                                	if(ress != null){
		                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                        		
		                                	}		                                	
		                                } 
		                                catch (Exception e) 
		                                { 
		                                	ContactsConstants.showErrorLog("Error in ressDISPLAY_NAME:"+e.toString());
		                                }
	                                }
	                                try 
	                                { 
	                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
	                                	if(ress != null){
	                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                            		
	                                	}		                                	
	                                } 
	                                catch (Exception e) 
	                                { 
	                                	ContactsConstants.showErrorLog("Error in ressDISPLAY_NAME:"+e.toString());
	                                }	                               
	                                updationForAdd.remove(selectedContactId);
	                    			service_Handler.post(response_call);
	                            }	  
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            ContactsConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                ContactsConstants.showLog(taskErrorMsgStr);
	                                //ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForAddContactSynToSAP:"+sff.toString());
        }         
        finally{
        	if(listDisplay){
        		updationForAddExitCus.add(selectedContactId);
        		updationForAdd.remove(selectedContactId);
        		service_Handler.post(response_call);
        		/*if(timerFlag && scanTask == null){
        			//dostarttimer();
        			updationForAddExitCus.add(selectedContactId);
        		}
        		else if(timerFlag && scanTask != null){
        			
        		}
        		else{
        			createListFile(strNewContactSAPCusFName.toString().trim(), strNewContactSAPCusLName.toString().trim());
        		}*/
    		}
        }
    }//End of updateReportsConfirmResponseForAddContactSynToSAP fn
    
    //boolean res = false;
    private void createListFile(String fname, String lname) { 
    	try{
    		String customerDetails = "Pick customer id for "+fname+" "+ lname;
    		if(strNewContactStreetName.length() > 0){
    			customerDetails += ", "+strNewContactStreetName;
    		}
    		if(strNewContactCityName.length() > 0){
    			customerDetails += ", "+strNewContactCityName;
    		}
    		if(strNewContactStateName.length() > 0){
    			customerDetails += ", "+strNewContactStateName;
    		}
    	       	   
    		AlertDialog dlg = null;    		
    		ContactsConstants.showLog("cusList.size : "+cusList.size());
	    	String[] listArray = new String[cusList.size()];
	    	cusList.toArray(listArray);
	    	int cusSize = cusList.size();
	    	if(cusSize > 1){
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		        builder.setTitle(customerDetails); 
		        builder.setSingleChoiceItems(listArray, -1, null); 
		        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		            public void onClick(DialogInterface dialog, int whichButton) { 
		            	int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition(); 
		            	ContactsConstants.showLog("selectedPosition: "+selectedPosition);  
		            	if(selectedPosition >= 0){
		            		ContactsConstants.showLog("selectedPosition: "+cusList.get(selectedPosition));
			            	catObj = (ContactProContactCreationOpConstraints)sapCusData.get(selectedPosition);
			                
			            	ContactsConstants.showLog("getKunnr: "+catObj.getKunnr());
			            	ContactsConstants.showLog("getName1: "+catObj.getName1());
			            	dialog.dismiss();
			            	initSoapConnectionForAddContactAlrExitCusSynToSAP(Integer.parseInt(selectedContactId), catObj.getKunnr(), catObj.getName1());
		            	}		            			            	
		            } 
		        }); 
		        dlg = builder.create();  
		        dlg.show();  
	    	}
	    	else{
	    		catObj = (ContactProContactCreationOpConstraints)sapCusData.get(0);                
            	ContactsConstants.showLog("getKunnr: "+catObj.getKunnr());
            	ContactsConstants.showLog("getName1: "+catObj.getName1());
            	initSoapConnectionForAddContactAlrExitCusSynToSAP(Integer.parseInt(selectedContactId), catObj.getKunnr(), catObj.getName1());
            }	        
    	}
        catch(Exception qw){
       	 	ContactsConstants.showErrorLog("Error in createListFile:"+qw.toString());
        }      
    }//End of createListFile fn
    
    private void initSoapConnectionForAddContactAlrExitCusSynToSAP(int contactId, String strContactSAPCusIDValue, String strOrgNameValue){   
    	SoapSerializationEnvelope envelopeC = null;
        try{        	     
        	String strOrgName = "", strOrgTitle = "", strOrgType = "", 
    		strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "", 
    		strEmailHome = "", strEmailWork = "", strEmailOther = "", strImId = "", strImType = "",
    		strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "",
    		strContactSAPID = "", strContactSAPCusID = "", strContactSAPCusFName = "", strContactSAPCusLName = "";
    		
        	strContactSAPCusID = strContactSAPCusIDValue;
        	strOrgName = strOrgNameValue;
        	
    		ContentResolver cr = getContentResolver();
    		
    		// Get value for Organization
    		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, String.valueOf(contactId)); 
    		strOrgName = OrgDetails[0];
    		strOrgType = OrgDetails[1];
    		strOrgTitle = OrgDetails[2];

         	// Get value for Phone no
         	String[] PhNos = ContactsConstants.getContactPhDetails(this, String.valueOf(contactId));
         	strPhoneHome = PhNos[0];
         	strPhoneMob = PhNos[1];
         	strPhoneWork = PhNos[2];
         	strPhoneOther = PhNos[3];
            
            // Get value for Emails
         	String[] Emails = ContactsConstants.getContactEmailsDetails(this, String.valueOf(contactId));
         	strEmailHome = Emails[0];
         	strEmailWork = Emails[1];
         	strEmailOther = Emails[2];
    				    		
    		// Get value for ImType & instants
         	String[] Msg_Details = ContactsConstants.getContactMsgDetails(this, String.valueOf(contactId));
         	strImId = Msg_Details[0];
         	strImType = Msg_Details[1];
    	 
    	 	// Get value for Address Details
		 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, String.valueOf(contactId));         	
         	strStreet = AddressDetails[0];
         	strCity = AddressDetails[1];
         	strRegion = AddressDetails[2];
         	strPostalCode = AddressDetails[3];
         	strCountry = AddressDetails[4];
         	strAddType = AddressDetails[5];
         	
    		/*// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
         		if(orgType.equals(String.valueOf(ContactsContract.CommonDataKinds.Organization.TYPE_WORK))){
         			strOrgType = orgType;//2 is a others
         			//strOrgName = orgName;
             		strOrgTitle = orgTitle;
    	 	    }     		
         	}     		
         	orgCur.close();
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}

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
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
    	 	    if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
    	 	    	strPhoneOther = phValue;
    	 	    }
    	 	    ContactsConstants.showLog("ph:"+phValue+"  "+phType);
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
    	    
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
    							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
    							null,
    							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
    							new String[]{String.valueOf(contactId)}, null); 
    		ContactsConstants.showLog("emailCur size:"+emailCur.getCount());
    		ContactsConstants.showLog("selectedContactId:"+selectedContactId);
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
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	    ContactsConstants.showLog("emailCur email:"+email+"  "+emailType);
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
    		if(strStreet.length() == 0 && strCity.length() == 0 && strRegion.length() == 0 && strPostalCode.length() == 0 && strCountry.length() == 0){    			 
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
    		}*/
            
            String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] nameWhereParams = new String[]{String.valueOf(contactId), 
         		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
         	Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
                        null, nameWhere, nameWhereParams, null);
         	if (nameCur.moveToNext()) { 
         		String givenNameValue = nameCur.getString(
    	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
    	    	String middleNameValue = nameCur.getString(
    	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
    	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
    	 	    strContactSAPCusFName = givenNameValue;
  	 	    	strContactSAPCusLName = middleNameValue;
         	} 
         	nameCur.close();
         	strNewContactSAPCusFName = strContactSAPCusFName;
         	strNewContactSAPCusLName = strContactSAPCusLName;
         	if(strNewContactSAPCusFName == null || strNewContactSAPCusFName.length() == 0){
         		strNewContactSAPCusFName = "";
    		}
         	if(strNewContactSAPCusLName == null || strNewContactSAPCusLName.length() == 0){
         		strNewContactSAPCusLName = "";
    		}
         	
         	strAddContactSAPCusFName = strContactSAPCusFName;
         	strAddContactSAPCusLName = strContactSAPCusLName;
         	strAddContactSAPCusEmailHome = strEmailHome;
    		strAddContactSAPCusEmailWork = strEmailWork;
    		strAddContactSAPCusEmailOther = strEmailOther;
	     	
        	ContactsConstants.showLog("initSoapConnection");
        	SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME);    
            envelopeC = null;
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            ContactProInputConstraints C0[];
            C0 = new ContactProInputConstraints[5];
            for(int k=0; k<5; k++){
                C0[k] = new ContactProInputConstraints(); 
            }                        
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]CONTACT-MAINTAIN[.]VERSION[.]0";             
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTCRTN20[.]PARNR[.]NAME_FIRST[.]NAME_LAST[.]FNCTN[.]TELF_WORK[.]TELF_MOBILE[.]TELF_PRSNL[.]TELF_OTHR[.]EMAIL_WORK[.]EMAIL_PRSNL[.]EMAIL_OTHR[.]IM_TYPE[.]IM_ID[.]STRASP[.]ORT01P[.]REGIOP[.]PSTLZP[.]LAND1P[.]KUNNR[.]KUNNR_NAME1";
            C0[4].Cdata = "ZGSXCAST_CNTCTCRTN20[.]"+strContactSAPID+"[.]"+strContactSAPCusFName+"[.]"+strContactSAPCusLName+"[.]"+strOrgTitle+"[.]"+strPhoneWork+"[.]"+strPhoneMob+"[.]"+strPhoneHome+"[.]"+strPhoneOther+"[.]"+strEmailWork+"[.]"+strEmailHome+"[.]"+strEmailOther+"[.]"+strImType+"[.]"+strImId+"[.]"+strStreet+"[.]"+strCity+"[.]"+strRegion+"[.]"+strPostalCode+"[.]"+strCountry+"[.]"+strContactSAPCusID+"[.]"+strOrgName;
                                                
            strContactSAPIDValue = strContactSAPID;
            strContactSAPCusIDValue = strContactSAPCusID;
            strContactSAPCusFNameValue = strContactSAPCusFName;
            strContactSAPCusLNameValue = strContactSAPCusLName;
            
            Vector listVect = new Vector();
            for(int k1=0; k1<C0.length; k1++){
                listVect.addElement(C0[k1]);
            }
                                 
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
            envelopeC.setOutputSoapObject(request);        
            ContactsConstants.showLog(request.toString());
            
            startNetworkConnectionForAddContactAlrExitCusSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, contactId);
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactAlrExitCusSynToSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForAddContactAlrExitCusSynToSAP
	
	private void startNetworkConnectionForAddContactAlrExitCusSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url, final int contactId){
    	try {
    		 getSOAPViaHTTPForAddContactAlrExitCusSynToSAP(envelopeCE, url, contactId);  
		} catch (Exception ae) {
			ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactAlrExitCusSynToSAP:"+ae.toString());
		}
    }//fn startNetworkConnectionForAddContactAlrExitCusSynToSAP
    
    private void getSOAPViaHTTPForAddContactAlrExitCusSynToSAP(SoapSerializationEnvelope envelopeCE, String url, final int contactId){		
        try {
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	ContactsConstants.showErrorLog("Data handling error : "+ex2);
            	ContactsConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                ContactsConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	ContactsConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                /*((Activity) this.getApplicationContext()).runOnUiThread(new Runnable() {
                    public void run() {
                    	ContactsConstants.showErrorDialog(ctx, extStr.toString());
                    }
                });*/
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	ContactsConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	ContactsConstants.showErrorLog("Error in getSOAPViaHTTPForAddContactAlrExitCusSynToSAP:"+e.toString());
        }
        finally {                     
        	ContactsConstants.showLog("========END OF LOG========");  
        	updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(resultSoap, contactId);
        	/*((Activity) this.getApplicationContext()).runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(resultSoap, contactId);
                }
            });*/
        }
    }//fn getSOAPViaHTTPForAddContactAlrExitCusSynToSAP
    
    public void updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(SoapObject soap, int contactId){		
    	try{ 
        	if(soap != null){
        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
        		ContactProContactCreationOpConstraints category = null;
        		ContactProContactCreationKeyOpConstraints categoryKey = null;
	            	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[37];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                ContactsConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CMPNYCNTCTDATA01")){
	                                if(category != null)
	                                    category = null;
	                                    
	                                category = new ContactProContactCreationOpConstraints(resArray);	
	                                if(category != null)
	                                	cusList.add(category.getKunnr().trim()+", "+category.getName1().trim()+", "+category.getOrto1k().trim()+", "+category.getRegiok().trim()+", "+category.getLand1k().trim());	    
	                                
	                                if(category != null)
	                                	sapCusData.add(category);

	                    			ContactsConstants.showLog("cusList getKunnr : "+category.getKunnr());
	                    			ContactsConstants.showLog("cusList getName1 : "+category.getName1());
	                            }	  
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCTKEY")){
	                                if(categoryKey != null)
	                                	categoryKey = null;
	                                    
	                                categoryKey = new ContactProContactCreationKeyOpConstraints(resArray);		                                	                       
	                                
	                                ContactsConstants.showLog("selectedContactId: "+selectedContactId);
	                    			ContactsConstants.showLog("cusListKey getKunnr : "+categoryKey.getKunnr());
	                    			ContactsConstants.showLog("cusListKey getParnr : "+categoryKey.getParnr());
	                    			
	                    			if(contactProCusDbObj == null){
	                        			contactProCusDbObj = new ContactProSAPCPersistent(this);
	                        		}
	                    			//update data to local device DB                    				
                    				contactProCusDbObj.update_row_data(String.valueOf(selectedContactId), String.valueOf(selectedContactId), categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
                    						strNewContactSAPCusLName.toString().trim());		
                    				contactProCusDbObj.closeDBHelper();
                    				
                    				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 		                               
	                                //------------------------------------------------------ First Names 
	                                if(strAddContactSAPCusFName.length() != 0) 
	                                { 
		                                String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
	                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
	                            		String[] phoneArgs = new String[]{String.valueOf(contactId)}; 
	                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
	                            		.withSelection(selectPhone, phoneArgs) 
	                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusFName+" "+strAddContactSAPCusLName+"                    "+categoryKey.getParnr()).build()); 
	                            	}
	                                else{
	                                	if(strAddContactSAPCusEmailHome.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(contactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailHome+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else if(strAddContactSAPCusEmailWork.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(contactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailWork+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else if(strAddContactSAPCusEmailOther.length() != 0){
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(contactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusEmailOther+"                    "+categoryKey.getParnr()).build());
		                                }
	                                	else{
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(contactId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Unknown"+"                    "+categoryKey.getParnr()).build());
	                                	}		                                	
	                                	try 
		                                { 
		                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
		                                	if(ress != null){
		                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                        		
		                                	}		                                	
		                                } 
		                                catch (Exception e) 
		                                { 
		                                	ContactsConstants.showErrorLog(e.toString());
		                                }
	                                }
	                                try 
	                                { 
	                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
	                                	if(ress != null){
	                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                            		
	                                	}		                                	
	                                } 
	                                catch (Exception e) 
	                                { 
	                                	ContactsConstants.showErrorLog(e.toString());
	                                }
	                            }	  
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            ContactsConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                ContactsConstants.showLog(taskErrorMsgStr);
	                                //ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                        ContactsConstants.showLog("Inside J  "+j);
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP:"+sff.toString());
        } 
        finally{   	
            updationForAdd.remove(selectedContactId);
			service_Handler.post(response_call);
        }
    }//End of updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP fn	
}
