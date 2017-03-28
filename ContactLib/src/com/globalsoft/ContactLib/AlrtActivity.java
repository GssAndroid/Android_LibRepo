package com.globalsoft.ContactLib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.view.Window;

import com.globalsoft.ContactLib.Constraints.ContactProContactCreationKeyOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProInputConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Service.ContactService;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class AlrtActivity extends Activity {
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private ArrayList updationForAddExitCus = new ArrayList();
	private String selectedContactId = null;
	private String lastId = null;
	private String strNewContactSAPCusFName = "";
	private String strNewContactSAPCusLName = "";
    private String strNewContactStreetName = "";
    private String strNewContactCityName = "";
    private String strNewContactStateName = "";
    private static String strContactSAPIDValue = "";
	private static String strContactSAPCusIDValue = "";
	private static String strContactSAPCusFNameValue = "";
	private static String strContactSAPCusLNameValue = "";
    private String strAddContactSAPCusFName = "";
	private String strAddContactSAPCusLName = "";
	private String strAddContactSAPCusEmailHome = "";
	private String strAddContactSAPCusEmailWork = "";
	private String strAddContactSAPCusEmailOther = "";
	private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private ContactProContactCreationOpConstraints catObj;
    private ContactProSAPCPersistent contactProCusDbObj = null;
    private ProgressDialog pdialog = null;
    private SoapObject resultSoap = null;    
    final Handler service_Handler = new Handler();
    String[] optionListForTime = {"1min","10mins","One hour"};    
    private boolean queueProcess = false;
    private boolean internetAccess = false;
    
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {     
        	queueProcess = this.getIntent().getBooleanExtra("queueProcess", false);
        	if(queueProcess == false){
            	updationForAddExitCus = this.getIntent().getParcelableArrayListExtra("updationForAddExitCus");
            	ContactsConstants.showLog("updationForAddExitCus :"+updationForAddExitCus.size());
            	startAlert();        		
        	}else{
        		String FName = this.getIntent().getStringExtra("FName");
        		String LName = this.getIntent().getStringExtra("LName");
        		selectedContactId = this.getIntent().getStringExtra("ContactId");        		
        		cusList = this.getIntent().getParcelableArrayListExtra("cusList");
        		sapCusData = this.getIntent().getParcelableArrayListExtra("sapCusData");
        		ContactsConstants.showLog("FName : "+FName);
        		ContactsConstants.showLog("LName : "+LName);
        		ContactsConstants.showLog("selectedContactId : "+selectedContactId);        		
        		createListFile(FName, LName);
        	}
        } catch (Exception de) {
        	ContactsConstants.showErrorLog("Error in Activity: "+de.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }
    }
    
    public void startAlert(){  
    	try{
    		if(alertDialog != null){
				alertDialog.dismiss();
				alertDialog = null;
			}
    		builder = new AlertDialog.Builder(this);
			int timing = getTiming();
			int opt = 0;
			if(timing == 60000){
				opt = 0;
			}
			else if(timing == 600000){
				opt = 1;
			}
			else if(timing == 3600000){
				opt = 2;
			}
			ContactsConstants.showLog("opt:"+opt);
			builder.setTitle("Contact sync: Further input needed");
			builder.setSingleChoiceItems(optionListForTime, opt, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
	                // Do something useful withe the position of the selected radio button	
	                if(selectedPosition == 0){
	                	ContactsConstants.POPUPALERTTIME = 60000;
	                }
	                else if(selectedPosition == 1){
	                	ContactsConstants.POPUPALERTTIME = 600000;
	                }
	                else if(selectedPosition == 2){
	                	ContactsConstants.POPUPALERTTIME = 3600000;
	                }
				}
	        });
			builder.setPositiveButton("Now", new AlertDialog.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					startUpdationFun();
				}
				});
			builder.setNegativeButton("Later", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {    					
					dialog.dismiss();
					Intent intent = new Intent(AlrtActivity.this, ContactService.class);
					//valueName - Service staring or stopping
					//if true - start service, false - stop service if already running service.
					
		    		//timerChange - Scheduling for timer task
					//if false - first time calling, true - rescheduling timer task.
					intent.putExtra("valueName", "true"); 
					intent.putExtra("timerChange", "true"); 
		    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startService(intent);
					finish(); 
				}
				});
			alertDialog = builder.create();
	  		alertDialog.show();
	  		ContactsConstants.showLog("ContactsConstants.POPUPALERTTIME:"+ContactsConstants.POPUPALERTTIME);
    	}
    	catch(Exception qw){
    		ContactsConstants.showErrorLog("Error in startAlert:"+qw.toString());
    		//stopService();
    		if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
    	}  
    }//fn startAlert
    
    private int getTiming(){
    	return ContactsConstants.POPUPALERTTIME;
    }//fn getTiming
    
    private void startUpdationFun(){
    	try{
	    	if(updationForAddExitCus.size() > 0){
	    		lastId = (String) updationForAddExitCus.get(0);
				initSoapConnectionForAddContactSynToSAP();
	    	}
	    	else{
	    		service_Handler.post(response_call);
	    	}
	    }
		catch(Exception qw){
			ContactsConstants.showErrorLog("Error in startUpdationFun:"+qw.toString());
			//stopService();
			if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
		}  
    }//fn startUpdationFun
    
    final Runnable response_call = new Runnable(){
	    public void run()
	    {
	    	try{	    		
	    		if(updationForAddExitCus.size() > 0){
	    			startUpdationFun();
	        	}
	    		else{
	    			if(pdialog != null){
	    				pdialog.dismiss();
	    				pdialog = null;
	    				ContactsConstants.START_ALRACTIVITY = false;
		    			Intent intent = new Intent(AlrtActivity.this, ContactService.class);
		    			//valueName - Service staring or stopping
		    			//if true - start service, false - stop service if already running service.
		    			
		        		//timerChange - Scheduling for timer task
		    			//if false - first time calling, true - rescheduling timer task.
						intent.putExtra("valueName", "false"); 
						intent.putExtra("timerChange", "false"); 
			    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startService(intent);
						finish(); 
	    			}
	    			else{
		    			Intent intent = new Intent(AlrtActivity.this, ContactService.class);
		    			//valueName - Service staring or stopping
		    			//if true - start service, false - stop service if already running service.
		    			
		        		//timerChange - Scheduling for timer task
		    			//if false - first time calling, true - rescheduling timer task.
						intent.putExtra("valueName", "false"); 
						intent.putExtra("timerChange", "false"); 
			    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startService(intent);
						finish(); 
	    			}
	    		}		    		
	    	} catch(Exception e){
	    		ContactsConstants.showLog("Error in response_call:"+e.toString());
	    		//stopService();
	    		if(!queueProcess){
            		stopService();
            	}else{
            		activityFinish();
            	}
	    	}	    	
	    }	    
    };
    
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in stopProgressDialog:"+ce.toString());
			//stopService();
			if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
		}
    }//fn stopProgressDialog
    
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
    		}
    		strAddContactSAPCusEmailHome = strEmailHome;
    		strAddContactSAPCusEmailWork = strEmailWork;
    		strAddContactSAPCusEmailOther = strEmailOther;
    				
    		// Get value for ImType & instants
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
    		}
            
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
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }
    }//fn initSoapConnectionForAddContactSynToSAP
	
	private void startNetworkConnectionForAddContactSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {  
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.CONTACTPRO_WAIT_TEXTS),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTPForAddContactSynToSAP(envelopeCE, url); 
                    			} catch (Exception e) {  }
             				}
                    	}.start();
                    }
                });    		                 				
		} catch (Exception ae) {
			ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactSynToSAP:"+ae.toString());
			//stopService();
			if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
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
                	//stopService();
                	if(!queueProcess){
                		stopService();
                	}else{
                		activityFinish();
                	}
                }
            }
        }
        catch (Throwable e) {
        	ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactSynToSAP:"+e.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }
        finally {                     
        	ContactsConstants.showLog("========END OF LOG========");
        	stopProgressDialog();
        	//updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
        	this.runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
                }
            });
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
		                                	//stopService();
		                                	if(!queueProcess){
		                                		stopService();
		                                	}else{
		                                		activityFinish();
		                                	}
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
	                                	//stopService();
	                                	if(!queueProcess){
	                                		stopService();
	                                	}else{
	                                		activityFinish();
	                                	}
	                                }	                   
	                                if(!queueProcess){
	                                	updationForAddExitCus.remove(selectedContactId);
		                    			service_Handler.post(response_call);
	                            	}else{
	                            		activityFinish();
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
	                                ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForAddContactSynToSAP:"+sff.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }         
        finally{
        	if(listDisplay){        		
        		createListFile(strNewContactSAPCusFName.toString().trim(), strNewContactSAPCusLName.toString().trim());
    		}        	
        }
    }//End of updateReportsConfirmResponseForAddContactSynToSAP fn
    
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
       	 	//stopService();
	       	if(!queueProcess){
	     		stopService();
	     	}else{
	     		activityFinish();
	     	}
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
    		}
            
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
            
            internetAccess = SapGenConstants.checkConnectivityAvailable(AlrtActivity.this);
            if(internetAccess){
            	startNetworkConnectionForAddContactAlrExitCusSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, contactId, request);
            }else{
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = String.valueOf(contactId)+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, String.valueOf(contactId),newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
            }
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactAlrExitCusSynToSAP:"+asd.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }
    }//fn initSoapConnectionForAddContactAlrExitCusSynToSAP
	
	private void startNetworkConnectionForAddContactAlrExitCusSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url, final int contactId, final SoapObject request){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.CONTACTPRO_WAIT_TEXTS),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTPForAddContactAlrExitCusSynToSAP(envelopeCE, url, contactId, request);  
                    			} catch (Exception e) { 
                    				//stopService();
                    				if(!queueProcess){
                    	        		stopService();
                    	        	}else{
                    	        		activityFinish();
                    	        	}
                    			}
             				}
                    	}.start();
                    }
                });    		     		 
		} catch (Exception ae) {
			ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactAlrExitCusSynToSAP:"+ae.toString());
			//stopService();
			if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
		}
    }//fn startNetworkConnectionForAddContactAlrExitCusSynToSAP
    
    private void getSOAPViaHTTPForAddContactAlrExitCusSynToSAP(SoapSerializationEnvelope envelopeCE, String url, final int contactId, final SoapObject request){		
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
                	//stopService();
                	if(!queueProcess){
                		stopService();
                	}else{
                		activityFinish();
                	}
                }
            }
        }
        catch (Throwable e) {
        	ContactsConstants.showErrorLog("Error in getSOAPViaHTTPForAddContactAlrExitCusSynToSAP:"+e.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        }
        finally {                     
        	ContactsConstants.showLog("========END OF LOG========");       
        	stopProgressDialog();
        	//updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(resultSoap, contactId);
        	this.runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(resultSoap, contactId, request);
                }
            });
        }
    }//fn getSOAPViaHTTPForAddContactAlrExitCusSynToSAP
    
    public void updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(SoapObject soap, int contactId, SoapObject request){		
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
                    				
                    				/*ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 		                               
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
		                                	//stopService();
		                                	if(!queueProcess){
		                                		stopService();
		                                	}else{
		                                		activityFinish();
		                                	}
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
	                                	//stopService();
	                                	if(!queueProcess){
	                                		stopService();
	                                	}else{
	                                		activityFinish();
	                                	}
	                                }*/
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
	                                ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                        ContactsConstants.showLog("Inside J  "+j);
	                    }
	                }
	            }//for
        	}else{
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = String.valueOf(contactId)+now.toString();
        		//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, String.valueOf(contactId),newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);	        	
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP:"+sff.toString());
        	//stopService();
        	if(!queueProcess){
        		stopService();
        	}else{
        		activityFinish();
        	}
        } 
        finally{   	
        	if(!queueProcess){
	        	updationForAddExitCus.remove(selectedContactId);
				service_Handler.post(response_call);
        	}else{
        		activityFinish();
        	}
        }
    }//End of updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP fn
    
    private void activityFinish(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;    			
			}
			finish();
		} catch (Exception eee) {
			ContactsConstants.showErrorLog("Error in activityFinish:"+eee.toString());
		}
    }//activityFinish
    
    private void stopService(){
    	try{
    		if(pdialog != null){
    			pdialog.dismiss();
    			pdialog = null;    			
    		}
			Intent intent = new Intent(AlrtActivity.this, ContactService.class);
			//valueName - Service staring or stopping
			//if true - start service, false - stop service if already running service.
			
    		//timerChange - Scheduling for timer task
			//if false - first time calling, true - rescheduling timer task.
			intent.putExtra("valueName", "false"); 
			intent.putExtra("timerChange", "false"); 
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(intent);
			finish(); 
	    }
	    catch(Exception sff){
	    	ContactsConstants.showErrorLog("Error in stopService:"+sff.toString());
	    	finish(); 
	    }     	
    }//fn stopService   
    
}
