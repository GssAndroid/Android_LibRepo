package com.globalsoft.ContactLib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.globalsoft.ContactLib.Constraints.ContactProContactCreationKeyOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProInputConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SapLibActivity.CrtGenActivity;
import com.globalsoft.SapLibSoap.Constraints.ContactProVectSerializable;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ContactsList extends ListActivity implements TextWatcher {
	
	private ListView listView;
	private ProgressDialog pdialog = null;    
    private String[] custDetArr;
	final Handler contactsData_Handler = new Handler();
	private Button goBtn;
	private EditText searchET;
	private String searchStr = "";
	private static Vector contactVect = new Vector();
	private static Vector allContactVect = new Vector();
	private ArrayList nativeContactsVect = new ArrayList();
	Vector selContactVect = new Vector(); 
	private ArrayList selIndexVal = new ArrayList();
	private ArrayList contactsArrySelctdList;
	private ContactProSAPCPersistent contactProCusDbObj = null;
	private ArrayList contactIds = new ArrayList();

    public static final int INTENT_ACTION_EDIT_4_ADD_CONTACT = 4;
	public String strNewContactSAPCusFName = "";
    public String strNewContactSAPCusLName = "";
    public int lastId;
	boolean internetAccess = false;
    private String taskErrorMsgStr="";
	private SoapObject resultSoap = null;
    private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private static ArrayList cusListKey = new ArrayList();    
    private String appCall = "";
    private static int respType = 0;
    private final Handler ntwrkHandler = new Handler();
    private StartNetworkTask soapTask = null;
	private SoapObject requestSoapObj = null;
    private String strNewContactStreetName = "";
    private String strNewContactCityName = "";
    private String strNewContactStateName = "";
    private ContactProContactCreationOpConstraints catObj;  
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.contactslist); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText("Contacts");

		ContactsConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
		if(ContactsConstants.APPLN_NAME_STR == null || ContactsConstants.APPLN_NAME_STR.length() == 0){
			ContactsConstants.APPLN_NAME_STR = "SALESPRO";
		}
		
		int dispwidth = SapGenConstants.getDisplayWidth(this);	
		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
        initLayout();
    }
    
    private void initLayout(){
		try{
			goBtn = (Button) findViewById(R.id.showContactsBtn);
			goBtn.setOnClickListener(goBtnListener); 
			searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			listView = (ListView)findViewById(android.R.id.list);
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listView.setItemsCanFocus(false);
			getContacts();
		}
		catch(Exception sfg){
			ContactsConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
    
    public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		searchItemCall(s.toString());
	} 
	
	private void searchItemCall(final String searchStr){
		if(pdialog != null)
			pdialog = null;		
		
		if(pdialog == null){
			pdialog = ProgressDialog.show(ContactsList.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				searchItemsAction(searchStr);
        			} catch (Exception e) {
        				ContactsConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
        			}
        			contactsData_Handler.post(searchCall);
        			stopProgressDialog();
				}
			};
	        t.start();	
		}
	}//fn searchItemCall
	
	final Runnable searchCall = new Runnable(){
	    public void run()
	    {
	    	try{
				System.out.println("contactVect.size():"+contactVect.size());	
				prefillContactsData();
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
    };	
	
	private void searchItemsAction(String match){  
        try{       
            searchStr = match;
            String mattStr = "";
            String strValue = null;
            boolean errornotify = false;
            if((allContactVect != null) && (allContactVect.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){             
                    contactVect.clear();
                    for(int i = 0; i < allContactVect.size(); i++){ 
                    	strValue = null;
                        mattStr = "";                        
                        String name = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getContactName();
                        String contactId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getContactDeviceId();
                        String sapDevId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getSapId();
        	    		String sapCustomerId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getCustomerId();
        	    		String fName = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getFName();
        	    		String lName = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getLName();
        	    		String orgNameSel = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getOrgName();        				
                        
                        if(name != null){
                            mattStr = name.trim().toLowerCase();
                            match = match.toLowerCase();
                            if((mattStr.indexOf(match) >= 0)){
                            	contactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
                            }
                        }
                    }//for 
                }
                else{
                    contactVect.clear();
                    for(int i = 0; i < allContactVect.size(); i++){  
                    	String name = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getContactName();
                        String contactId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getContactDeviceId();
                        String sapDevId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getSapId();
        	    		String sapCustomerId = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getCustomerId();
        	    		String fName = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getFName();
        	    		String lName = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getLName();
        	    		String orgNameSel = (String)((ContactSAPDetails)allContactVect.elementAt(i)).getOrgName();        				
                        
                        if(name != null){
                        	contactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
                        }
                    }
                }
            }//if
            else
                return;
        }//try
        catch(Exception we){
            we.printStackTrace();
        }
    }//fn searchItemsAction  
    
    //getting device contacts list
  	public void getContacts(){
  		try {
  			if(allContactVect.size() > 0){
  				allContactVect.removeAllElements();
  				allContactVect.clear();
  			}
  			if(contactVect.size() > 0){
  				contactVect.removeAllElements();
  				contactVect.clear();
  			}		
  			if(pdialog != null)
      			pdialog = null;			
      		
  			if(pdialog == null){
  				pdialog = ProgressDialog.show(ContactsList.this, "", getString(R.string.CONTACTPRO_WAIT),true);
  				
  				Thread t = new Thread() 
      			{
      	            public void run() 
      				{
              			try{
              				gettingContacts();
              			} catch (Exception e) {
              				ContactsConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
              			}
              			contactsData_Handler.post(contactsCall);
      				}
      			};
      	        t.start();	
  			}  			
  		} catch (IllegalArgumentException sffe) {
  			ContactsConstants.showErrorLog("Error in getContacts:"+sffe.toString());
  		}
  	}//fn getContacts
  	
  	private void gettingContacts(){
		try {
			Cursor C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
			while (C.moveToNext()) {                   
				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
				if(contactProCusDbObj == null){
					contactProCusDbObj = new ContactProSAPCPersistent(ContactsList.this);
				}
				if(contactProCusDbObj.checkContactExists(contactId)){
					contactProCusDbObj.getContactDetails(contactId);
		    		String sapDevId = ContactsConstants.CONTACTSAPID;
		    		String sapCustomerId = ContactsConstants.ONTACTSAPCUSID;
		    		String fName = ContactsConstants.CONTACTSAPCUSFNAME;
		    		String lName = ContactsConstants.CONTACTSAPCUSLNAME;
		    		String orgNameSel = getOrgName(contactId);
					allContactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
					contactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
				}else{
					addDataToLocalDB(Integer.parseInt(contactId));
					contactProCusDbObj.getContactDetails(contactId);
					String sapDevId = ContactsConstants.CONTACTSAPID;
		    		String sapCustomerId = ContactsConstants.ONTACTSAPCUSID;
		    		String fName = ContactsConstants.CONTACTSAPCUSFNAME;
		    		String lName = ContactsConstants.CONTACTSAPCUSLNAME;
		    		String orgNameSel = getOrgName(contactId);
					allContactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
					contactVect.addElement(new ContactSAPDetails(""+name, ""+contactId, ""+sapDevId, ""+sapCustomerId, ""+fName, ""+lName, ""+orgNameSel));
				}
	    		contactProCusDbObj.closeDBHelper();
			}
			C.close();
			ContactsConstants.showLog("contactVect.size gettingContacts:"+contactVect.size());
		} catch (IllegalArgumentException sffee) {
			ContactsConstants.showErrorLog("Error in getContacts:"+sffee.toString());
		}
	}//fn gettingContacts		
	
	final Runnable contactsCall = new Runnable(){
	    public void run()
	    {
	    	try{
	    		ContactsConstants.showLog("contactVect.size in contactsCall:"+contactVect.size());
				stopProgressDialog();
				prefillContactsData();
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
	};
    
    private void prefillContactsData(){
    	try{
    		if(contactVect != null){
    			if(contactVect.size() > 0){
    	    		custDetArr = getContactsList();
    	    		if(custDetArr != null){    	    			
    	            	listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, custDetArr){ 
    	            	    @Override 
    	            	    public View getView(int position, View convertView, ViewGroup parent) { 
    	            	        TextView textView = (TextView) super.getView(position, convertView, parent);     	            	 
    	            	        textView.setTextColor(ContactsList.this.getResources().getColor(R.color.bluelabel));    	            	 
    	            	        return textView; 
    	            	    } 
    	            	}); 
    	    		}
    			}else{
    				String[] EMPTY_ARRAY = new String[0];
    				listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, EMPTY_ARRAY));
    			}
    		}
    	}
    	catch(Exception adf){
    		ContactsConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
    
    private String[] getContactsList(){
    	String choices[] = null;
        try{
        	if(contactIds != null){
        		contactIds.clear();
        	}
        	
            if(contactVect != null){
                String name = "";                
                int arrSize = contactVect.size();
                choices = new String[arrSize];                
                for(int h=0; h<arrSize; h++){
                	name = (String)((ContactSAPDetails)contactVect.elementAt(h)).getContactName();
                	//String contactid = (String)((ContactSAPDetails)contactVect.elementAt(h)).getContactId();
                	contactIds.add(String.valueOf(h));
                	String cname = "";
    				int namefind = name.indexOf("                    ");
    				if(namefind != -1){
    				    String name_append = name.substring(0,namefind);
    				    cname = name_append;
    				}
    				else{
    					String[] tokens = name.split(" ");
    					if(tokens.length >= 3){
    						String name_append = tokens[0]+" "+tokens[1];
    						cname = name_append;
    					}
    					else{
    						cname = name;
    					}            	
    				}
                    choices[h] = cname;
                }
            }
        }
        catch(Exception sfg){
        	ContactsConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        System.out.println("Size of choices : "+choices.length);
        return choices;
    }//fn getContactsList
    
    private OnClickListener goBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	try{
	        	if(contactsArrySelctdList != null)
	        		contactsArrySelctdList.clear();
	        	else
	        		contactsArrySelctdList = new ArrayList();    
	        	
	        	if(selContactVect.size() > 0){
	        		selContactVect.removeAllElements();
	        		selContactVect.clear();
	  			}        	
	        	
	        	if(selIndexVal.size() > 0){
	        		selIndexVal.clear();
	  			}	        	
	        	
	        	Thread t = new Thread() 
      			{
      	            public void run() 
      				{
              			try{
              				gettingSelectedContacts();
              			} catch (Exception e) {
              				ContactsConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
              			}
              			contactsData_Handler.post(callactcrt);
      				}
      			};
      	        t.start();		        	
	        }
	        catch(Exception sfg){
	        	ContactsConstants.showErrorLog("Error in goBtnListener : "+sfg.toString());
	        }
        }
    };
  
    private void gettingSelectedContacts(){
    	try{
    		if(listView != null){
    			int len = listView.getCount();
    			ContactsConstants.showLog("List Count : "+len);
    			SparseBooleanArray checked = listView.getCheckedItemPositions();
    			ContactsConstants.showLog("Checked List Count : "+checked.size());
    			String mattIdStr = "";
    			int size=checked.size();	        		
    			
    			for (int i = 0; i < len; i++){
    				if (checked.get(i)) {
    					String item = (String)listView.getAdapter().getItem(i);
    					ContactsConstants.showLog("Selected item : "+item);
    					int pos = Integer.parseInt((String)contactIds.get(i));
    					String name = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getContactName();
    					String id = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getContactDeviceId();
    					String sapid = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getSapId();
    					String cusid = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getCustomerId();
    					String fname = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getFName();
    					String lname = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getLName();
    					String orgname = (String)((ContactSAPDetails)contactVect.elementAt(pos)).getOrgName();
    					
    					ContactsConstants.showLog("Selected name : "+name);
    					ContactsConstants.showLog("Selected id : "+id);
    					ContactsConstants.showLog("Selected sapid : "+sapid);
    					ContactsConstants.showLog("Selected cusid : "+cusid);
    					ContactsConstants.showLog("Selected fname : "+fname);
    					ContactsConstants.showLog("Selected lname : "+lname);
    					ContactsConstants.showLog("Selected orgname : "+orgname);
    					ContactSAPDetails obj = new ContactSAPDetails(name, id, sapid, cusid, fname, lname, orgname);
    					selContactVect.addElement(obj);
    					selIndexVal.add(id);
    					
    					if(item != null){
    	            		contactsArrySelctdList.add(item);
    	            	}
    				}
    			}
    		}    		
    	}
    	catch(Exception adf){
    		ContactsConstants.showErrorLog("Error in gettingSelectedContacts : "+adf.getMessage());
    	}
    }//fn gettingSelectedContacts	
    
    private void showActCrtScreen(){
    	try{
    		ContactProVectSerializable vectObj1 = new ContactProVectSerializable(selContactVect);
    		Intent intent = new Intent(ContactsList.this, CrtGenActivity.class);
			intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
    		intent.putExtra("selContacts", vectObj1);
			startActivityForResult(intent,SapGenConstants.SOCUSTACTCREATE_SCREEN);
			finish();
    	}
    	catch(Exception adf){
    		ContactsConstants.showErrorLog("Error in showActCrtScreen : "+adf.getMessage());
    	}
    }//fn showActCrtScreen
	
	final Runnable callactcrt = new Runnable(){
	    public void run()
	    {
	    	try{
	    		if(contactsArrySelctdList.size() > 0){
	    			//showActCrtScreen();
	    			collectNativeContacts();
	    		}
	    		else
	    			ContactsConstants.showErrorDialog(ContactsList.this, "Please select contacts!");
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
	};
	
	private String getOrgName(String contactId){
    	String strOrgName = "";
		// Get value for Organization
    	try{
	        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	     	String[] orgWhereParams = new String[]{contactId, 
	     		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
	     	Cursor orgCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
	                    null, orgWhere, orgWhereParams, null);
	     	if (orgCur.moveToNext()) { 
	     		strOrgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
	     		//ContactsConstants.showLog("(getOrgName) Company Name : "+strOrgName);
	     	} 
	     	orgCur.close();
	     	if(strOrgName == null || strOrgName.length() == 0){
	     		strOrgName = "";
			}
	    }
	    catch(Exception qw){
	   	 	ContactsConstants.showErrorLog("Error in onChange:"+qw.toString());
	    } 
     	return strOrgName;
    }//fn getOrgName
    
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			ContactsConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
	private void collectNativeContacts(){
    	try {
    		if(selContactVect != null && selContactVect.size() > 0){
    			for (int i = 0; i < selContactVect.size(); i++){
		    		String sapid = (String)((ContactSAPDetails)selContactVect.elementAt(i)).getSapId();
		    		String cusid = (String)((ContactSAPDetails)selContactVect.elementAt(i)).getCustomerId();
	    			String id = (String)((ContactSAPDetails)selContactVect.elementAt(i)).getContactDeviceId();	
	    			ContactsConstants.showLog("sapid : "+sapid);
					ContactsConstants.showLog("id : "+id);
					ContactsConstants.showLog("cusid : "+cusid);
		    		if(cusid == null || cusid.length() == 0){
			    		nativeContactsVect.add(id);
		    		}
    			}
    		}
			ContactsConstants.showLog("nativeContactsVect.size() : "+nativeContactsVect.size());
    		if(nativeContactsVect.size() > 0){
    			synToSAPForNewContact();				
    		}else{
    			showActCrtScreen();
    		}
		} catch (Exception ce) {
			ContactsConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
	
	private void synToSAPForNewContact(){
		try{
			if(nativeContactsVect != null && nativeContactsVect.size() > 0){
	    		lastId = Integer.parseInt((String) nativeContactsVect.get(0));
	    		boolean chekOrgValue = checkOrg(lastId);
             	AlertDialog alertDialog;
        		if(!chekOrgValue){
        			alertDialog = new AlertDialog.Builder(this)
        			.setMessage("Please enter your Organization name")
        			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
        				public void onClick(DialogInterface dialog, int which) {
        					Intent i = new Intent(Intent.ACTION_EDIT);
            	        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, lastId); 
            	        	i.setData(contactUri); 
            	        	startActivityForResult(i, INTENT_ACTION_EDIT_4_ADD_CONTACT);
        				}
        				})        			
        				.create();
        				alertDialog.show();
        		}
        		else{
        			ContactsConstants.showLog("INTENT_ACTION_ADD_CONTACT");
        			initSoapConnectionForAddContactSynToSAP();
        		}        		
			}else{
				showActCrtScreen();
			}
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("synToSAPForNewContact: "+ce.toString());
		}
	}//fn synToSAPForNewContact
    
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
    		    		
        	ContactsConstants.showLog("initSoapConnection for new contact");
        	SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME);    
            envelopeC = null;
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            ContactProInputConstraints C0[];
            C0 = new ContactProInputConstraints[5];
            for(int k=0; k<5; k++){
                C0[k] = new ContactProInputConstraints(); 
            }                        
            
            ContactsConstants.showLog("strOrgTitle : "+strOrgTitle+" : "+strOrgName);
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]CONTACT-MAINTAIN[.]VERSION[.]0";             
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTCRTN20[.]PARNR[.]NAME_FIRST[.]NAME_LAST[.]FNCTN[.]TELF_WORK[.]TELF_MOBILE[.]TELF_PRSNL[.]TELF_OTHR[.]EMAIL_WORK[.]EMAIL_PRSNL[.]EMAIL_OTHR[.]IM_TYPE[.]IM_ID[.]STRASP[.]ORT01P[.]REGIOP[.]PSTLZP[.]LAND1P[.]KUNNR[.]KUNNR_NAME1";
            C0[4].Cdata = "ZGSXCAST_CNTCTCRTN20[.]"+strContactSAPID+"[.]"+strContactSAPCusFName+"[.]"+strContactSAPCusLName+"[.]"+strOrgTitle+"[.]"+strPhoneWork+"[.]"+strPhoneMob+"[.]"+strPhoneHome+"[.]"+strPhoneOther+"[.]"+strEmailWork+"[.]"+strEmailHome+"[.]"+strEmailOther+"[.]"+strImType+"[.]"+strImId+"[.]"+strStreet+"[.]"+strCity+"[.]"+strRegion+"[.]"+strPostalCode+"[.]"+strCountry+"[.]"+strContactSAPCusID+"[.]"+strOrgName;
                                                            
            Vector listVect = new Vector();
            for(int k1=0; k1<C0.length; k1++){
                listVect.addElement(C0[k1]);
            }
                                 
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
            envelopeC.setOutputSoapObject(request);        
            ContactsConstants.showLog(request.toString());
            
            internetAccess = SapGenConstants.checkConnectivityAvailable(ContactsList.this);
            if(internetAccess){
	            respType = ContactsConstants.RESP_TYPE_ADD_SYNC_2_SAP;
	            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, String.valueOf(lastId), request);
            }else{
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = String.valueOf(lastId)+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, String.valueOf(lastId),newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
	        	addDataToLocalDB(lastId);
	        	nativeContactsVect.remove(String.valueOf(lastId));
            	synToSAPForNewContact();
            }
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactSynToSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForAddContactSynToSAP
        
    private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, String contactId, SoapObject request){
    	try{
    		requestSoapObj = request;
            soapTask = new StartNetworkTask(ctx);
    		Thread t = new Thread() {
	            public void run() {
        			try{
        				resultSoap = null;
        	            resultSoap = soapTask.execute(envelopeC).get();
        			} catch (Exception e) {
        				SapGenConstants.showErrorLog("Error in Handler : "+e.toString());
        			}        			
                    handler.post(handlerFnName);
				}
			};
	        t.start();
    	}
    	catch(Exception asgg){
    		SapGenConstants.showErrorLog("Error in doThreadNetworkAction : "+asgg.toString());
    	}
    }//fn doThreadNetworkAction  
    
    final Runnable getNetworkResponseRunnable = new Runnable(){
	    public void run(){
	    	try{
	    		if(resultSoap != null){
	    			if(respType == ContactsConstants.RESP_TYPE_ADD_SYNC_2_SAP){
	    				updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
	    			}	
	    		}
	    	} catch(Exception edd){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+edd.toString());
	    	}
	    }	    
    };
    
    public void updateReportsConfirmResponseForAddContactSynToSAP(SoapObject soap){	
    	boolean listDisplay = false, errorflag = false, resMsgErr = false;
        try{ 
        	if(soap != null){
        		ContactsConstants.soapResponse(this, soap, false);
            	taskErrorMsgStr = ContactsConstants.SOAP_RESP_MSG;
            	//taskErrorType = ContactsConstants.SOAP_ERR_TYPE;
            	ContactsConstants.showLog("On soap response : "+soap.toString());
            	String soapMsg = soap.toString();
            	resMsgErr = ContactsConstants.getSoapResponseSucc_Err(soapMsg);   
            	ContactsConstants.showLog("resMsgErr : "+resMsgErr);
            	if(!resMsgErr){
	        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
	        		ContactProContactCreationOpConstraints category = null;
	        		ContactProContactCreationKeyOpConstraints categoryKey = null;
		            if(cusList != null){
		            	cusList.clear();
		            	sapCusData.clear();
		            }
		            if(cusListKey != null)
		            	cusListKey.clear();
		            	            
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
	
		                    			ContactsConstants.showLog("cusList getKunnr : "+category.getKunnr());
		                    			ContactsConstants.showLog("cusList getName1 : "+category.getName1());
		                    			listDisplay = true;
		                            }	  
		                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCTKEY")){
		                                if(categoryKey != null)
		                                	categoryKey = null;
		                                    
		                                categoryKey = new ContactProContactCreationKeyOpConstraints(resArray);	
		                                if(cusListKey != null)
		                                	cusListKey.add(categoryKey);	                       
		                                
		                    			ContactsConstants.showLog("cusListKey getKunnr : "+categoryKey.getKunnr());
		                    			ContactsConstants.showLog("cusListKey getParnr : "+categoryKey.getParnr());
	
		                    			if(contactProCusDbObj == null){
		                        			contactProCusDbObj = new ContactProSAPCPersistent(this);
		                        		}
		                    			/*//inserting data to local device DB
	                    				contactProCusDbObj.insertContactDetails(String.valueOf(lastId), categoryKey.getParnr().toString().trim(), 
	                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
	                    						strNewContactSAPCusLName.toString().trim());
	                    				contactProCusDbObj.closeDBHelper();	 */ 
	                    				
	                    				//updatind data to local device DB
	                    				contactProCusDbObj.update_row_data(String.valueOf(lastId), String.valueOf(lastId), categoryKey.getParnr().toString().trim(), 
	                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
	                    						strNewContactSAPCusLName.toString().trim());
	                    				contactProCusDbObj.closeDBHelper(); 
	                    				
	                    				ContactsConstants.showLog("index id : "+selIndexVal.indexOf(String.valueOf(lastId)));
	                    				int pos = selIndexVal.indexOf(String.valueOf(lastId));	                    				
	                    				String name = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactName();
	                					String id = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactDeviceId();
	                					String sapid = categoryKey.getParnr().toString().trim();
	                					String cusid = categoryKey.getKunnr().toString().trim();
	                					String fname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getFName();
	                					String lname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getLName();
	                					String orgname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getOrgName();
	                					
	                					ContactsConstants.showLog("Selected name : "+name);
	                					ContactsConstants.showLog("Selected id : "+id);
	                					ContactsConstants.showLog("Selected sapid : "+sapid);
	                					ContactsConstants.showLog("Selected cusid : "+cusid);
	                					ContactsConstants.showLog("Selected fname : "+fname);
	                					ContactsConstants.showLog("Selected lname : "+lname);
	                					ContactsConstants.showLog("Selected orgname : "+orgname);
	                					ContactSAPDetails obj = new ContactSAPDetails(name, id, sapid, cusid, fname, lname, orgname);
	                					selContactVect.set(pos, obj);
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
	        		errorflag = true;
	        		ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	        	}
        	}else{
        		errorflag = true;
        		addDataToLocalDB(lastId);
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForAddContactSynToSAP:"+sff.toString());
        	errorflag = true;
        } 
        finally{   	 
        	stopProgressDialog();
        	if(!errorflag){
	        	if(listDisplay){
	    			createListFile(strNewContactSAPCusFName.toString().trim(), strNewContactSAPCusLName.toString().trim());
	        	}
	        	else{
	        		ContactsConstants.showLog("cusList size in updateReportsConfirmResponseForAddContactSynToSAP: "+cusList.size());
	        		nativeContactsVect.remove(String.valueOf(lastId));
	        		synToSAPForNewContact();
	        	}
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
			            	initSoapConnectionForAddContactAlrExitCusSynToSAP(lastId, catObj.getKunnr(), catObj.getName1());
		            	}
		            } 
		        }); 
		        builder.show(); 
	    	}
	    	else{
	    		catObj = (ContactProContactCreationOpConstraints)sapCusData.get(0);                
            	ContactsConstants.showLog("getKunnr: "+catObj.getKunnr());
            	ContactsConstants.showLog("getName1: "+catObj.getName1());
            	initSoapConnectionForAddContactAlrExitCusSynToSAP(lastId, catObj.getKunnr(), catObj.getName1());
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
                                                            
            Vector listVect = new Vector();
            for(int k1=0; k1<C0.length; k1++){
                listVect.addElement(C0[k1]);
            }
                                 
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
            envelopeC.setOutputSoapObject(request);        
            ContactsConstants.showLog(request.toString());
                       
            internetAccess = SapGenConstants.checkConnectivityAvailable(ContactsList.this);
            if(internetAccess){
            	startNetworkConnectionForAddContactAlrExitCusSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, contactId, request);
            }else{
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = String.valueOf(lastId)+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, String.valueOf(contactId),newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
            	nativeContactsVect.remove(String.valueOf(lastId));
        		synToSAPForNewContact();
            }
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactAlrExitCusSynToSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForAddContactAlrExitCusSynToSAP
	
	private void startNetworkConnectionForAddContactAlrExitCusSynToSAP(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url, final int contactId, final SoapObject request){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.CONTACTPRO_WAIT),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTPForAddContactAlrExitCusSynToSAP(envelopeCE, url, contactId, request);
                    				sleep(2000);
                    			} catch (Exception e) {  }
             				}
                    	}.start();
                    }
                });
		} catch (Exception ae) {
			ContactsConstants.showErrorLog("Error in startNetworkConnectionForAddContactAlrExitCusSynToSAP:"+ae.toString());
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
                this.runOnUiThread(new Runnable() {
                    public void run() {
                    	ContactsConstants.showErrorDialog(ctx, extStr.toString());
                    }
                });
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                    ContactsConstants.showLog("Result: "+resultSoap.toString());
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
            stopProgressDialog();
            this.runOnUiThread(new Runnable() {
                public void run() {
                	updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(resultSoap, contactId, request);
                }
            });
        }
    }//fn getSOAPViaHTTPForAddContactAlrExitCusSynToSAP
    
    public void updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP(final SoapObject soap, int contactId, SoapObject request){		
    	try{ 
        	if(soap != null){
        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
        		ContactProContactCreationOpConstraints category = null;
        		ContactProContactCreationKeyOpConstraints categoryKey = null;
	            if(cusList != null){
	            	cusList.clear();
	            	sapCusData.clear();
	            }
	            if(cusListKey != null)
	            	cusListKey.clear();
	            	            
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
	                                if(cusListKey != null)
	                                	cusListKey.add(categoryKey);	                       
	                                
	                    			ContactsConstants.showLog("cusListKey getKunnr : "+categoryKey.getKunnr());
	                    			ContactsConstants.showLog("cusListKey getParnr : "+categoryKey.getParnr());

	                    			if(contactProCusDbObj == null){
	                        			contactProCusDbObj = new ContactProSAPCPersistent(this);
	                        		}
	                    			/*//inserting data to local device DB
                    				contactProCusDbObj.insertContactDetails(String.valueOf(contactId), categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
                    						strNewContactSAPCusLName.toString().trim());
                    				contactProCusDbObj.closeDBHelper();   */ 
                    				
                    				//updatind data to local device DB
                    				contactProCusDbObj.update_row_data(String.valueOf(lastId), String.valueOf(lastId), categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
                    						strNewContactSAPCusLName.toString().trim());
                    				contactProCusDbObj.closeDBHelper(); 
                    				
                    				ContactsConstants.showLog("index id : "+selIndexVal.indexOf(String.valueOf(lastId)));
                    				int pos = selIndexVal.indexOf(String.valueOf(lastId));	                    				
                    				String name = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactName();
                					String id = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactDeviceId();
                					String sapid = categoryKey.getParnr().toString().trim();
                					String cusid = categoryKey.getKunnr().toString().trim();
                					String fname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getFName();
                					String lname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getLName();
                					String orgname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getOrgName();
                					
                					ContactsConstants.showLog("Selected name : "+name);
                					ContactsConstants.showLog("Selected id : "+id);
                					ContactsConstants.showLog("Selected sapid : "+sapid);
                					ContactsConstants.showLog("Selected cusid : "+cusid);
                					ContactsConstants.showLog("Selected fname : "+fname);
                					ContactsConstants.showLog("Selected lname : "+lname);
                					ContactsConstants.showLog("Selected orgname : "+orgname);
                					ContactSAPDetails obj = new ContactSAPDetails(name, id, sapid, cusid, fname, lname, orgname);
                					selContactVect.set(pos, obj);
	                            }	  
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            ContactsConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);	                                
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
        } 
        finally{   
			ContactsConstants.showLog("cusList size : "+cusList.size());
            stopProgressDialog();	        
            nativeContactsVect.remove(String.valueOf(lastId));    
    		synToSAPForNewContact();
        }
    }//End of updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP fn
    
    //If Internet is not available, so data adding to local databases with empty SAP contatId and customerId
    private void addDataToLocalDB(int localContactId){
    	String strContactSAPCusFName = "", strContactSAPCusLName = "";
		String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
     	String[] nameWhereParams = new String[]{String.valueOf(localContactId), 
     		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
     	Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
                    null, nameWhere, nameWhereParams, null);
     	while(nameCur.moveToNext()) { 
     		String givenNameValue = nameCur.getString(
	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	    	String middleNameValue = nameCur.getString(
	    			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
	 	    ContactsConstants.showLog("givenNameValue from else"+givenNameValue+"  "+middleNameValue);
	 	    strContactSAPCusFName = givenNameValue;
	 	    	strContactSAPCusLName = middleNameValue;
     	} 
     	nameCur.close();
     	strNewContactSAPCusFName = strContactSAPCusFName;
     	strNewContactSAPCusLName = strContactSAPCusLName;
     	
     	ContactsConstants.showLog("strNewContactSAPCusFName:"+strNewContactSAPCusFName);
     	ContactsConstants.showLog("strNewContactSAPCusLName:"+strNewContactSAPCusLName);
     	
     	if(contactProCusDbObj == null){
			contactProCusDbObj = new ContactProSAPCPersistent(this);
		} 
     	
     	if(!contactProCusDbObj.checkContactExists(String.valueOf(localContactId))){
    		//inserting data to local device DB
    		contactProCusDbObj.insertContactDetails(String.valueOf(localContactId), "", 
    				"", strNewContactSAPCusFName.toString().trim(), 
    				strNewContactSAPCusLName.toString().trim());
    		contactProCusDbObj.closeDBHelper();
        }//End of addDataToLocalDB fn     		
    }        
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) { 
    	super.onActivityResult(reqCode, resultCode, data); 
    	ContactsConstants.showLog("resultCode:   "+resultCode);
    	ContactsConstants.showLog("reqCode:   "+reqCode);
    	if(reqCode == INTENT_ACTION_EDIT_4_ADD_CONTACT){
    		if (resultCode == Activity.RESULT_OK) { 
    			AlertDialog alertDialog;
    			boolean chekOrgValue = checkOrg(lastId);
        		if(!chekOrgValue){
        			alertDialog = new AlertDialog.Builder(this)
        			.setMessage("Please enter your Organization name")
        			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
        				public void onClick(DialogInterface dialog, int which) {
        					Intent i = new Intent(Intent.ACTION_EDIT);
            	        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, lastId); 
            	        	i.setData(contactUri); 
            	        	startActivityForResult(i, INTENT_ACTION_EDIT_4_ADD_CONTACT);
        				}
        				})        			
        				.create();
        				alertDialog.show();
        		}
        		else{
        			ContactsConstants.showLog("INTENT_ACTION_EDIT_4_ADD_CONTACT");
        			initSoapConnectionForAddContactSynToSAP();
        		}     			    	        
    		}
    	}  
    } 
    
    private boolean checkOrg(int contactId){
		ContentResolver cr = getContentResolver();
		String strOrgType = "", strOrgName = "", strOrgTitle = "", strCountry = "";
		// Get value for Organization
        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
     	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
     		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
     	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                    null, orgWhere, orgWhereParams, null);
     	if (orgCur.moveToNext()) { 
     		String orgName = "", orgTitle = "", orgType = "";
     		try{
	     		orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
	     		orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
	     		
	     		ContactsConstants.showLog("(checkOrg) Company Name : "+orgName);
	     		
	     		if(ContactsContract.CommonDataKinds.Organization.TYPE != null){
	     			orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
	     			ContactsConstants.showLog("(checkOrg) Company Type : "+orgType);
		     		strOrgType = orgType;
	     			strOrgName = orgName;
	         		strOrgTitle = orgTitle;
	     		}
     		}
     		catch(Exception sfg){
     			ContactsConstants.showErrorLog("Error on checkOrg : "+sfg.toString());
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
         		strCountry = country;
	 	    }     	
	 		
	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK))){
	 			if(strCountry == null){
	 				strCountry = country;
	 			}
	 		}
	 	} 		
	 	addrCur.close();
	 	
	 	if(strOrgName == null || strOrgName.length() == 0){
	 		strOrgName = "";
		}	 	
	 	if(strCountry == null || strCountry.length() == 0){
			strCountry = "";
		}
	 	
	 	ContactsConstants.showLog("Id:   "+contactId);
	 	ContactsConstants.showLog(strOrgName+"   "+strCountry);
	 	ContactsConstants.showLog(strOrgName.length()+"   "+strCountry.length());
     	//if(strOrgName.length() != 0 && strCountry.length() != 0){
     	if(strOrgName.length() != 0){
     		return true;
     	}
     	else{
     		return false;
     	}		
	}//fn checkOrg
}//End of class ContactsList