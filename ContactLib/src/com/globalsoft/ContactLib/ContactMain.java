package com.globalsoft.ContactLib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.SapLibActivity.ActivityListForPhone;
import com.globalsoft.SapLibActivity.ActivityListforTablet;
import com.globalsoft.SapLibActivity.CrtGenActivity;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationKeyOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProInputConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProOutputConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Map.MyLocation;
import com.globalsoft.ContactLib.Map.MyLocation.LocationResult;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesOrderLib.SalesOrderCreation;
import com.globalsoft.SalesOrderLib.SalesOrderCreationTablet;
import com.globalsoft.SalesOrderLib.SalesOrderListActivity;
import com.globalsoft.SalesOrderLib.SalesOrderMainScreenTablet;
import com.globalsoft.SalesProCustActivityLib.CustomerListActivity;
import com.globalsoft.SalesProCustActivityLib.SalesProCustActivity;
import com.globalsoft.SapLibSoap.Constraints.ContactProVectSerializable;
import com.globalsoft.SapLibSoap.Item.ContactDetails;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartAddTask;
import com.globalsoft.SapLibSoap.SoapConnection.StartEmailTask;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.SoapConnection.StartOrgTask;
import com.globalsoft.SapLibSoap.SoapConnection.StartPhTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ContactMain extends ListActivity implements TextWatcher {
	private static Vector contactVect = new Vector();
	private static Vector allContactVect = new Vector();
	private ListView listView;
	private EditText searchET;
	private String searchStr = "";
	private static final int MENU_ADD_CONTACT = Menu.FIRST;
	private static final int MENU_SORT_BY_NAME = Menu.FIRST+2;
	private static final int MENU_SETTINGS = Menu.FIRST+3;
	private static final int REFRESH = Menu.FIRST+4;
	public static final int INTENT_ACTION_VIEW = 1;
    public static final int INTENT_ACTION_ADD_CONTACT = 2;
    public static final int INTENT_ACTION_EDIT = 3;	
    public static final int INTENT_ACTION_EDIT_4_ADD_CONTACT = 4;
    public static String viewAction = "";
    public int lastId;
	private ProgressDialog pdialog = null;
	private SoapObject resultSoap = null;
	private String requestContactId = null;
	private SoapObject requestSoapObj = null;
	private static ArrayList contactList = new ArrayList();
	public static final String PREFS_NAME = "ContactProPrefs";
	public static final String PREFS_KEY = "SAP_CONTACT_LOCAL_DB";
	private String errMsg="";
	private ContactProSAPCPersistent contactProCusDbObj = null;
	private static ContactProErrorMessagePersistent errorDbObj = null;
	private ImageView addimg;
	
	int selectedContactId = 0;
	String selectedContactName = "";
	boolean internetAccess = false;
	private boolean sortContactNameFlag = false;
	private int sortIndex = -1;
	private String orgNameSel = "";
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private boolean userCheck = false ,flag_pref2 = false;
	Vector selContactVect = new Vector(); 
	public static String strContactSAPIDValue = "";
	public static String strContactSAPCusIDValue = "";
	public static String strContactSAPCusFNameValue = "";
	public static String strContactSAPCusLNameValue = "";
	
	private String strAddContactSAPCusFName = "";
	private String strAddContactSAPCusLName = "";
	private String strAddContactSAPCusEmailHome = "";
	private String strAddContactSAPCusEmailWork = "";
	private String strAddContactSAPCusEmailOther = "";

    private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private static ArrayList cusListKey = new ArrayList();    
    
    public String strNewContactSAPCusFName = "";
    public String strNewContactSAPCusLName = "";
    private String strNewContactStreetName = "";
    private String strNewContactCityName = "";
    private String strNewContactStateName = "";
    private ContactProContactCreationOpConstraints catObj;  
    private MyContentObserver observer = new MyContentObserver(new Handler());
    boolean contLocal = false;
    boolean contAlertFlag = false;
    
    private String appCall = "" ,contactsListType="";
    
    final Handler contactsData_Handler = new Handler();
    final Handler contactsSearchData_Handler = new Handler();
    final Handler updateData_Handler = new Handler();

    final Handler contactsDataGetting_Handler = new Handler();
    
	private String lat = "", lon = "", geoloc = "";
    
    //Selected contact value
    String strContactSAPCusFName_Value = "", strContactSAPCusLName_Value = "", strOrgTitle_Value = "", 
    	strPhoneWork_Value = "", strPhoneMob_Value = "", strPhoneHome_Value = "", strPhoneOther_Value = "", 
    	strEmailWork_Value = "", strEmailHome_Value = "", strEmailOther_Value = "", strImType_Value = "", 
    	strImId_Value = "", strStreet_Value = "", strCity_Value = "", strRegion_Value = "", strPostalCode_Value = "", 
    	strCountry_Value = "", strContactSAPCusID_Value = "", strOrgName_Value = "" ,finalresult="";
    private int dispwidth = 300,contactsListCount=0;
    
    private static int respType = 0;
    private final Handler ntwrkHandler = new Handler();
    private StartNetworkTask soapTask = null;
    private String taskErrorMsgStr="", taskErrorType="";
    private String groupID = null;
    private String appOptions = "";
    private MyContactListAdapterForPhone adpterForPhone = null;
    private MyContactListAdapterForTablet adpterForTablet = null;
        
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			//SapGenConstants.setWindowTitleTheme(this);
			//setContentView(R.layout.contactmain);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.contactmain); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText("Contacts");

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			SharedPreferences settings2 = getSharedPreferences(ContactsConstants.PREFS_NAME_FOR_CONTACTS, 0);      
			flag_pref2 = settings2.getBoolean(ContactsConstants.PREFS_KEY_CONTACTS_FOR_MYSELF_GET, false);
			
			ContactsConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
			appOptions = this.getIntent().getStringExtra("app_name_options");
			 
			if(ContactsConstants.APPLN_NAME_STR == null || ContactsConstants.APPLN_NAME_STR.length() == 0){
				ContactsConstants.APPLN_NAME_STR = SapGenConstants.APPLN_NAME_STR_MOBILEPRO;
			}			
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);
			listviewcall();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
			internetAccess = SapGenConstants.checkConnectivityAvailable(ContactMain.this);
			System.out.println("internetAccess:"+internetAccess);
			dispwidth = SapGenConstants.getDisplayWidth(this);
			this.getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, observer); 
			
			//groupRelatedFunc();
			
			sortItems();
			searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			addimg = (ImageView) findViewById(R.id.addimg);
			addimg.setOnClickListener(addimglistener);
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);      
			boolean flag_pref = settings.getBoolean(PREFS_KEY, false);
			if(!flag_pref){
				if(internetAccess){
					ContactsConstants.showLog("Data from SAP!");	
					if(contactProCusDbObj == null){
						contactProCusDbObj = new ContactProSAPCPersistent(this);
					}					
					contAlertFlag = true;
					initSoapConnection();
				}
				else{
					getContacts();	
					ContactsConstants.showLog("We can't communicate with SAP. Please try again later!");
				}								
			}
			else{
				getContacts();	
				ContactsConstants.showLog("Data from local DataBase!");
			}
		}
		catch (Exception de) {
        	System.out.println("Error in oncreate contactMain:"+de.toString());
        }
	}
		
	private OnClickListener addimglistener = new OnClickListener(){
		public void onClick(View v) {
			gotoAddContact();
        }
    };
    
    private OnClickListener refreshimglistener = new OnClickListener(){
		public void onClick(View v) {
			refreshContacts();
        }
    };

	//clearing native contacts
    public void refreshContacts(){		
		try {
			if(pdialog != null)
				pdialog = null;		
			
			if(pdialog == null){
				pdialog = ProgressDialog.show(ContactMain.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
				Thread t = new Thread() 
				{
			        public void run() 
					{
						try{
							deletingContacts();
						} catch (Exception e) {
							ContactsConstants.showErrorLog("Error in deletingContacts Thread:"+e.toString());
						}
						contactsSearchData_Handler.post(refreshCall);
						stopProgressDialog();
					}
				};
			    t.start();	
			}
		} catch (Exception cre) {
			ContactsConstants.showErrorLog("Error in refreshContacts:"+cre.toString());
		}   
    	//initSoapConnection(); 	
    	/*final ContentResolver cr = getContentResolver();
    	cr.delete( Uri.parse(String.valueOf(ContactsContract.RawContacts.CONTENT_URI)), null, null);
    	ContactsConstants.showLog("Calling Full-Sets");
    	initSoapConnection();*/
    	
    }//refreshContacts      
		
	private void deletingContacts(){
		try{
			flag_pref2 = false;
			String id = "", orgWhere = "";
	    	String[] orgWhereParams = null;
			ContentResolver cr = getContentResolver();
	    	if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			ArrayList contactArrList = contactProCusDbObj.getAllContactId();
			if(contactArrList != null && contactArrList.size() > 0){
				for(int i = 0; i < contactArrList.size(); i++){
					id = contactArrList.get(i).toString().trim();
					if(id != null && id.length() > 0){
						/*orgWhere = ContactsContract.Data.CONTACT_ID + " = ?"; 
						orgWhereParams = new String[]{String.valueOf(id)};
						int val = cr.delete(ContactsContract.Data.CONTENT_URI, orgWhere, orgWhereParams);*/						
	                	if (Integer.parseInt(Build.VERSION.SDK) == 8 ) {
	                		int val = cr.delete( Uri.parse(String.valueOf(ContactsContract.RawContacts.CONTENT_URI)), "_ID=? ", new String[]{String.valueOf(id)});
							//ContactsConstants.showLog("ID  "+id+"  Deleted Val: "+val);
	                	}else{
	                		int val = cr.delete( Uri.parse(String.valueOf(ContactsContract.RawContacts.CONTENT_URI)), "_ID=? ", new String[]{String.valueOf(id)});
							//ContactsConstants.showLog("ID  "+id+"  Deleted Val: "+val);
	                	}
					}
				}
			}		
			contactProCusDbObj.clearTaskTable(); 
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
		} catch (Exception cre) {
			ContactsConstants.showErrorLog("Error in deletingContacts:"+cre.toString());
		}
	}//fn deletingContacts	

	final Runnable refreshCall = new Runnable(){
	    public void run()
	    {
	    	try{
		    	initSoapConnection(); 	
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in refreshCall:"+sfe.toString());
	    	}
	    }	    
    };	
    
	private void groupRelatedFunc(){
		try {
			// Check the Group is available or not
			Cursor groupCursor = null;
			String[] GROUP_PROJECTION = new String[] { ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
			groupCursor = this.managedQuery(ContactsContract.Groups.CONTENT_URI,GROUP_PROJECTION, ContactsContract.Groups.TITLE+ "=?", new String[]{SapGenConstants.SAP_GROUP_NAME}, ContactsContract.Groups.TITLE + " ASC");
			Log.d("*** Here Counts: ","** "+groupCursor.getCount());

			if(groupCursor.getCount() > 0){
				ContactsConstants.showLog("Group is already available");
			}
			else {
				ContactsConstants.showLog("Group is not available");
				//Here we create a new Group
	            try {
	                ContentValues groupValues = null;
	                ContentResolver cr = this.getContentResolver();
	                groupValues = new ContentValues();
	                groupValues.put(ContactsContract.Groups.TITLE, SapGenConstants.SAP_GROUP_NAME);
	                cr.insert(ContactsContract.Groups.CONTENT_URI, groupValues);
	                ContactsConstants.showLog("########### Group Creation Finished : ###### Success");    
	    			ContactsConstants.showLog("Created Successfully");
	            }
	            catch(Exception e){
	            	ContactsConstants.showErrorLog("########### Exception :"+e.getMessage()); 
	            }
			}
			groupCursor.close();
	        groupCursor = null;
            
            Cursor getGroupID_Cursor = null;
            getGroupID_Cursor = this.managedQuery(ContactsContract.Groups.CONTENT_URI,  GROUP_PROJECTION, ContactsContract.Groups.TITLE+ "=?", new String[]{SapGenConstants.SAP_GROUP_NAME}, null);
            ContactsConstants.showLog("**** Now Empty Cursor size: ** "+getGroupID_Cursor.getCount());
            getGroupID_Cursor.moveToFirst();
            groupID = (getGroupID_Cursor.getString(getGroupID_Cursor.getColumnIndex("_id")));
            ContactsConstants.showLog(" **** Group ID is:  ** "+groupID);
            getGroupID_Cursor.close();
            getGroupID_Cursor = null;
            
		} catch (Exception cre) {
			ContactsConstants.showErrorLog("Error in groupRelatedFunc:"+cre.toString());
		}
	}//fn groupRelatedFunc
	
	private void listviewcall(){
		try {
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				adpterForTablet = new MyContactListAdapterForTablet(this);
				setListAdapter(adpterForTablet);
			}else{
				adpterForPhone = new MyContactListAdapterForPhone(this);
				setListAdapter(adpterForPhone);
			}
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
	
	private void stopProgressDialog(){
    	try {
    		//ContactsConstants.showLog("pdialog in stopProgressDialog: "+pdialog);
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in stopProgressDialog:"+ce.toString());
		}
    }//fn stopProgressDialog
	
	private void initSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            ContactProInputConstraints C0[];
            C0 = new ContactProInputConstraints[3];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new ContactProInputConstraints(); 
            }
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            
            if(!flag_pref2){           	
            	C0[2].Cdata = "EVENT[.]CONTACT-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			}else{				
				C0[2].Cdata = "EVENT[.]CONTACT-FOR-EMPLOYEE-GET[.]VERSION[.]0";
			}
            
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            ContactsConstants.showLog(request.toString());
          
            respType = ContactsConstants.RESP_TYPE_GET_EMP_CONT;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, null, request);
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnection getting contact from sap! : "+asd.toString());
        }
    }//fn initSoapConnection
       
    public void updateReportsConfirmResponse(final SoapObject soap){	
    	boolean resMsgErr = false;
    	ContactProOutputConstraints category = null;
    	ArrayList<ContentProviderOperation> ops =null;
    	String contactId ="" ,name = "";
        int cindexno = 1;
    	
        try{ 
        	if(soap != null){                
            	String soapMsg = soap.toString();
            	resMsgErr = ContactsConstants.getSoapResponseSucc_Err(soapMsg);   
            	ContactsConstants.showLog("resMsgErr : "+resMsgErr);
            	if(!resMsgErr){
	        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());	    			
		    		
		            if(contactList != null)
		            	contactList.clear();
		            	            
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
		                    	//NEWLY ADDED FOR FULL-SETS
		                    	 if(j > 1 && j <= 2){
		                            	result = pii.getProperty(j).toString();
		                                firstIndex = result.indexOf(delimeter);
		                                eqIndex = result.indexOf("=");
		                                eqIndex = eqIndex+1;
		                                firstIndex = firstIndex + 3;
		                                docTypeStr = result.substring(eqIndex, (firstIndex-3));
		                                result = result.substring(firstIndex);
		                                finalresult = result.substring(0, result.length() - 3);
		                                SapGenConstants.showLog("final Result : "+finalresult);
		                                
		                                //ServiceProConstants.showLog("Result : "+result);                                
		                                resC = 0;
		                                indexA = 0;
		                                indexB = result.indexOf(delimeter);
		                                int index1 = 0;
		                                while (indexB != -1) {
		                                    res = result.substring(indexA, indexB);
		                                    indexA = indexB + delimeter.length();
		                                    indexB = result.indexOf(delimeter, indexA);
		                                    //ServiceProConstants.showLog("Result resp : "+resC+" : "+res);
		                                    if(resC == 0){
		                                    	docTypeStr = res;
		                                    }
		                                    if(resC == 1){
		 	                                    String[] respStr = res.split(";");
		 	                                    if(respStr.length >= 1){
		 	                                    	String respTypeData = respStr[0];
		 	                                    	//ServiceProConstants.showLog("respTypeData : "+respTypeData);
		 	                                    	index1 = respTypeData.indexOf("=");
		 	                                    	index1 = index1+1;
		 	                                        String respType = respTypeData.substring(index1, respTypeData.length());
		 	                                    	//ServiceProConstants.showLog("respType : "+respType);
		 	                                    	
		 	                                    	String rowCountStrData = respStr[1];
		 	                                    	//ServiceProConstants.showLog("rowCountStrData : "+rowCountStrData);
		 	                                    	index1 = rowCountStrData.indexOf("=");
		 	                                    	index1 = index1+1;
		 	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
		 	                                    	//ServiceProConstants.showLog("rowCount : "+rowCount);
		 	                                    	if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCT10")){
		 		                                    	//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
		 		                                    	contactsListCount = Integer.parseInt(rowCount);
		 		                                    	contactsListType = respType;
		 	                                        }	                                        
		 	                                    }
		                                    }
		                                    resC++;
		                                    if(resC == 2)
		                                    	break;
		                                }                                
		                            }
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
		                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCT10")){
		                            	if(contAlertFlag){
		                            		alertAddingContacts(cindexno, contactsListCount);
		                            	}
		                            	
		                                if(category != null)
		                                    category = null;
		                                    
		                                category = new ContactProOutputConstraints(resArray);	                                
		                              
		                                boolean val = false;
		                                name = "";
		                                if(category.getNameFirst().toString().trim() != null ){
		                                	name += category.getNameFirst().toString().trim();
		                                	if(category.getNameLast().toString().trim() != null){
		                                		name += " "+category.getNameLast().toString().trim();
		                                	}
		                                }else if(category.getNameFirst().toString().trim() == null ){
		                                	if(category.getNameLast().toString().trim() != null){
		                                		name += " "+category.getNameLast().toString().trim();
		                                	}
		                                }
		                                	
		                                val = contactsCheck(name);
		                                ContactsConstants.showLog("name : "+name);
		                                ContactsConstants.showLog("val : "+val);
		                                
		                                if(!val){
		                                	//ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		                                	ops = new ArrayList<ContentProviderOperation>(); 
			                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
			                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
			                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
			                                .build()); 
		
			                                //------------------------------------------------------ First Names 
			                                if(category.getNameFirst().length() != 0) 
			                                { 
			                                	/*String d_name = category.getNameFirst()+" "+category.getNameLast();
			                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build()); 
				                                */
				                                String d_name = category.getNameFirst()+" "+category.getNameLast();
				                                //ContactsConstants.showLog("d_name:"+d_name);
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, category.getNameFirst())
				                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, category.getNameLast())
				                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
			                                }
			                                else{
			                                	if(category.getEmailPrsnl().length() != 0){
			                                		String d_name = category.getEmailPrsnl();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else if(category.getEmailWork().length() != 0){
			                                		String d_name = category.getEmailWork();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else if(category.getEmailOthers().length() != 0){
			                                		String d_name = category.getEmailOthers();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else{
			                                		String d_name = "Unknown";
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                }
			                              	                                
			                                //------------------------------------------------------ Mobile Number 
			                                if(category.getTelfMobile().length() != 0) 
			                                { 
			                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfMobile()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) 
				                                .build()); 
			                                } 
		
			                                //------------------------------------------------------ Home Numbers 
			                                if(category.getTelfPrsnl().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfPrsnl()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME) 
				                                .build()); 
			                                } 
		
			                                //------------------------------------------------------ Work Numbers 
			                                if(category.getTelfWork().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfWork()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK) 
				                                .build()); 
			                                }
			                                
			                                //------------------------------------------------------ Others Numbers 
			                                if(category.getTelfOthers().length() != 0) 
			                                { 
			                                	
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfOthers()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER) 
				                                .build()); 
			                                }
			                                		                                
			                                //------------------------------------------------------ Work Email 
			                                if(category.getEmailWork().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailWork()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Home Email 
			                                if(category.getEmailPrsnl().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailPrsnl()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Other Email 
			                                if(category.getEmailOthers().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailOthers()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Imtype 
			                                if(category.getImType().length() != 0 && category.getIMId().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Im.DATA, category.getIMId()) 
				                                .withValue(ContactsContract.CommonDataKinds.Im.TYPE, category.getImType()) 
				                                .build()); 
			                                } 	                                
			                                
			                                //------------------------------------------------------ Address
			                                if(category.getLand1p().length() != 0) 
			                                { 
			                                	String streetData = "";
			                                	if(category.getStrasp().length() != 0){
			                                		streetData += category.getStrasp();
			                                	}
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, streetData)
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, category.getOrt01p()) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, category.getRegiop())
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, category.getPstlzp()) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, category.getLand1p()) 			                                
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
		
			                                //------------------------------------------------------ CustomerName and fun
			                                if(category.getFnctn().length() != 0 || category.getKunnrName1().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, category.getKunnrName1()) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, category.getFnctn()) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
			                                // Asking the Contact provider to create a new contact 
			                                try 
			                                { 
			                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 
			                                	if (ress!=null && ress[0]!=null) { 
			                                		Uri newContactUri = ress[0].uri;
			                                		Log.d("", "URI added contact:"+ newContactUri); 
			                        				String uri = newContactUri.toString();
			                        				int firstindex = uri.lastIndexOf("/");
			                        				String device_cont_id = uri.substring(firstindex+1, uri.length());
			                        				//ContactsConstants.showLog("Contact id is:"+device_cont_id);
			                        				//ContactsConstants.showLog("Contact id length is:"+device_cont_id.length());
			                        				displayUpdateData(cindexno, contactsListCount);
			                        				/*if(contactsListCount > 1){
			                        					pdialog.setMessage(getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS1) + cindexno +getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS2)+ contactsListCount );
			                        				}else{
			                        					pdialog.setMessage(getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS1) + cindexno +getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS2)+ contactsListCount );
			                        				}*/
			                        				//inserting data to local device DB
			                        				contactProCusDbObj.insertContactDetails(device_cont_id, category.getParnr().toString().trim(), 
			                        						category.getKunnr().toString().trim(), category.getNameFirst().toString().trim(), 
			                        						category.getNameLast().toString().trim());
			                        				if(contactProCusDbObj != null)
			                        					contactProCusDbObj.closeDBHelper();
			                                	}
			                                	else 
			                                		Log.e("", "Contact not added."); 
			                                } 
			                                catch (Exception e) 
			                                { 
			                                	e.printStackTrace(); 
			                                	if(contactProCusDbObj != null)
			                        				contactProCusDbObj.closeDBHelper();
			                                	Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show(); 
			                                }		                                	
		                                }else{
		                                	//ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		                                	ops = new ArrayList<ContentProviderOperation>(); 
			                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
			                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
			                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
			                                .build()); 
		
			                                //------------------------------------------------------ First Names 
			                                if(category.getNameFirst().length() != 0) 
			                                { 
			                                	String d_name = category.getNameFirst()+" "+category.getNameLast();
			                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, category.getNameFirst())
			                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, category.getNameLast())
			                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name)
			                                	.withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, category.getParnr()).build());
				                                //.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build()); 
			                                }
			                                else{
			                                	if(category.getEmailPrsnl().length() != 0){
			                                		String d_name = category.getEmailPrsnl()+"                    "+category.getParnr();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else if(category.getEmailWork().length() != 0){
			                                		String d_name = category.getEmailWork()+"                    "+category.getParnr();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else if(category.getEmailOthers().length() != 0){
			                                		String d_name = category.getEmailOthers()+"                    "+category.getParnr();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                	else{
			                                		String d_name = "Unknown"+"                    "+category.getParnr();
				                                	//ContactsConstants.showLog("d_name:"+d_name);
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
				                                }
			                                }
			                              	                                
			                                //------------------------------------------------------ Mobile Number 
			                                if(category.getTelfMobile().length() != 0) 
			                                { 
			                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfMobile()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) 
				                                .build()); 
			                                } 
		
			                                //------------------------------------------------------ Home Numbers 
			                                if(category.getTelfPrsnl().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfPrsnl()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME) 
				                                .build()); 
			                                } 
		
			                                //------------------------------------------------------ Work Numbers 
			                                if(category.getTelfWork().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfWork()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK) 
				                                .build()); 
			                                }
			                                
			                                //------------------------------------------------------ Others Numbers 
			                                if(category.getTelfOthers().length() != 0) 
			                                { 
			                                	
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfOthers()) 
				                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
				                                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER) 
				                                .build()); 
			                                }
			                                		                                
			                                //------------------------------------------------------ Work Email 
			                                if(category.getEmailWork().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailWork()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Home Email 
			                                if(category.getEmailPrsnl().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailPrsnl()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Other Email 
			                                if(category.getEmailOthers().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailOthers()) 
				                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER) 
				                                .build()); 
			                                } 
			                                
			                                //------------------------------------------------------ Imtype 
			                                if(category.getImType().length() != 0 && category.getIMId().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Im.DATA, category.getIMId()) 
				                                .withValue(ContactsContract.CommonDataKinds.Im.TYPE, category.getImType()) 
				                                .build()); 
			                                } 	                                
			                                
			                                //------------------------------------------------------ Address
			                                if(category.getLand1p().length() != 0) 
			                                { 
			                                	String streetData = "";
			                                	if(category.getStrasp().length() != 0){
			                                		streetData += category.getStrasp();
			                                	}
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, streetData)
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, category.getOrt01p()) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, category.getRegiop())
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, category.getPstlzp()) 
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, category.getLand1p()) 			                                
				                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
		
			                                //------------------------------------------------------ CustomerName and fun
			                                if(category.getFnctn().length() != 0 || category.getKunnrName1().length() != 0) 
			                                { 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
				                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
				                                .withValue(ContactsContract.Data.MIMETYPE, 
				                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, category.getKunnrName1()) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, category.getFnctn()) 
				                                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK) 
				                                .build()); 
			                                } 
			                                
			                                // Asking the Contact provider to create a new contact 
			                                try 
			                                { 
			                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 
			                                	if (ress!=null && ress[0]!=null) { 
			                                		Uri newContactUri = ress[0].uri;
			                                		Log.d("", "URI added contact:"+ newContactUri); 
			                        				String uri = newContactUri.toString();
			                        				int firstindex = uri.lastIndexOf("/");
			                        				String device_cont_id = uri.substring(firstindex+1, uri.length());
			                        				//ContactsConstants.showLog("Contact id is:"+device_cont_id);
			                        				//ContactsConstants.showLog("Contact id length is:"+device_cont_id.length());
			                        				displayUpdateData(cindexno, contactsListCount);
			                        				/*if(contactsListCount > 1){
			                        					pdialog.setMessage(getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS1) + cindexno +getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS2)+ contactsListCount );
			                        				}else{
			                        					pdialog.setMessage(getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS1) + cindexno +getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS2)+ contactsListCount );
			                        				}*/
			                        				//inserting data to local device DB
			                        				contactProCusDbObj.insertContactDetails(device_cont_id, category.getParnr().toString().trim(), 
			                        						category.getKunnr().toString().trim(), category.getNameFirst().toString().trim(), 
			                        						category.getNameLast().toString().trim());
			                        				if(contactProCusDbObj != null)
			                        					contactProCusDbObj.closeDBHelper();
			                                	}
			                                	else 
			                                		Log.e("", "Contact not added."); 
			                                } 
			                                catch (Exception e) 
			                                { 
			                                	e.printStackTrace(); 
			                                	if(contactProCusDbObj != null)
			                        				contactProCusDbObj.closeDBHelper();
			                                	Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show(); 
			                                }
		                                }		                                		                                
		                                /*else{
		                                	contactId = getContactId(name);
			                                if(contactId != null && contactId.length() > 0){
				                    			boolean userSAP = contactProCusDbObj.checkContactExists(contactId);
				                    			if(!userSAP){
				                                	//inserting data to local device DB
			                        				try {
														contactProCusDbObj.insertContactDetails(contactId, category.getParnr().toString().trim(), 
																category.getKunnr().toString().trim(), category.getNameFirst().toString().trim(), 
																category.getNameLast().toString().trim());
													} catch (Exception eff) {
														if(contactProCusDbObj != null)
															contactProCusDbObj.closeDBHelper();
											        	ContactsConstants.showErrorLog("Error in inserting data in to LDB:"+eff.toString());
													}
			                                	}
				                    			if(contactProCusDbObj != null)
				                    				contactProCusDbObj.closeDBHelper();
		                    				}
		                                }*/
		                                
		                                if(contactList != null)
		                                	contactList.add(category);	          

                        				cindexno = cindexno + 1;
		                            }	                           
		                        }
		                        else if(j == 0){
		                            String errorMsg = pii.getProperty(j).toString();
		                            ContactsConstants.showLog("Inside J == 0 ");
		                            int errorFstIndex = errorMsg.indexOf("Message=");
		                            if(errorFstIndex > 0){
		                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
		                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
		                                ContactsConstants.showLog(taskErrorMsgStr);
		                                ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
		                            }
		                        }
		                    }
		                }
		            }//for
	        	}else{
	        		this.runOnUiThread(new Runnable() {
	                    public void run() {
	                    	showErrorMsg(soap);
	                    }
	                });	        		
	        	}
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponse for getting contacts from sap:"+sff.toString());
        } 
        finally{
        	ContactsConstants.showLog("Contact List Size : "+contactList.size());
        	if(contactList.size() > 0){
        		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);    
    			SharedPreferences.Editor editor = sharedPreferences.edit();    
    			editor.putBoolean(PREFS_KEY, true);    
    			editor.commit();
        	}
			if(contactList.size() > 0){
    			SharedPreferences sharedPreferences2 = getSharedPreferences(ContactsConstants.PREFS_NAME_FOR_CONTACTS, 0);    
    			SharedPreferences.Editor editor = sharedPreferences2.edit();    
    			editor.putBoolean(ContactsConstants.PREFS_KEY_CONTACTS_FOR_MYSELF_GET, true);    
    			editor.commit();
    		}
			flag_pref2 = true;
        	if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
        	stopProgressDialog();
        }
    }//fn updateReportsConfirmResponse  
    
    private void displayUpdateData(final int no, final int count){
    	try{
			ContactMain.this.runOnUiThread(new Runnable() {
                public void run() {
					pdialog.setMessage(getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS1) +"  "+ no +" "+getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS2)+"  "+ count );
                }
            });	 
	    } catch (NotFoundException egg) {
			ContactsConstants.showErrorLog("Error in displayUpdateData:"+egg.toString());
		}
    }//fn
    
    private void alertAddingContacts(final int no, final int count){
		try {
			ContactMain.this.runOnUiThread(new Runnable() {
			    public void run() {
			    	AlertDialog.Builder builder = new AlertDialog.Builder(ContactMain.this);
			    	builder.setTitle(getResources().getString(R.string.SYNC_CONTACTS));
                	if(count > 1){
    			        builder.setMessage( count +"  "+ getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS4)+" "+ getResources().getString(R.string.SYNC_CONTACTS_MSG_TEXT));
                	}else{
    			        builder.setMessage( count +"  "+ getResources().getString(R.string.WAIT_TEXTS_FOR_INSERTING_CONTACTS3)+" "+ getResources().getString(R.string.SYNC_CONTACTS_MSG_TEXT));
                	}
			        builder.setPositiveButton(getResources().getString(R.string.OK_TEXT), new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            }
			        });                
			        builder.show();
			    }
			});	 
			contAlertFlag = false;
		} catch (Exception egge) {
			ContactsConstants.showErrorLog("Error in alertAddingContacts:"+egge.toString());
		}
    }//fn clickDeleteAction
    
    private void showErrorMsg(SoapObject soap){
    	try {
    		ContactsConstants.soapResponse(this, soap, false);
        	taskErrorMsgStr = ContactsConstants.SOAP_RESP_MSG;
        	taskErrorType = ContactsConstants.SOAP_ERR_TYPE;
        	ContactsConstants.showLog("On soap response : "+soap.toString());
			ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
		} catch (NotFoundException egg) {
			ContactsConstants.showErrorLog("Error in showErrorMsg:"+egg.toString());
		}
    }//fn showErrorMsg
    
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
				pdialog = ProgressDialog.show(ContactMain.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
				//ContactsConstants.showLog("pdialog in getContacts: "+pdialog);
				Thread t = new Thread() 
    			{
    	            public void run() 
    				{
            			try{
            				if(SapGenConstants.enterValue){
            				    ContactsConstants.showLog("Reading SAP Contacts!");
            					gettingSAPContacts();
            				}else if(SapGenConstants.enterNpersValue){
            				    ContactsConstants.showLog("Reading ALL Contacts!");
            					gettingContacts();            					
            				}else{
            				    ContactsConstants.showLog("Reading SAP Contacts!");
                				gettingSAPContacts();
            				}
            				//gettingContacts();
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
	
	private boolean contactsCheck(String nameSet){
		boolean val = false;
		try {			 
			String sa1 = "%"+nameSet+"%"; // contains an "input String"
			//ContactsConstants.showLog("String value : "+sa1);
			Cursor c = getContentResolver().query(Data.CONTENT_URI, null,Data.DISPLAY_NAME + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		
			long rawContactId = -1; 
			if(c.moveToFirst()){ 
			    rawContactId = c.getLong(c.getColumnIndex(Data.RAW_CONTACT_ID)); 
			} 
			
			if(rawContactId != -1)
				val = true;
			else
				val = false;
			
		    //ContactsConstants.showLog("rawContactId:"+rawContactId);
			c.close();
			return val;
		} catch (Exception ssqd) {
			ContactsConstants.showErrorLog("Error in contactsCheck:"+ssqd.getMessage());
			return val;
		} 
	}//fn contactsCheck
	
	private String getContactId(String nameSet){
		String id = null;
		try {			 
			String sa1 = "%"+nameSet+"%"; // contains an "input String"
			//ContactsConstants.showLog("String value : "+sa1);
			Cursor c = getContentResolver().query(Data.CONTENT_URI, null,Data.DISPLAY_NAME + " LIKE ? ", new String[] { sa1 }, null);//returns the selected rows which matches the input String
    		
			long rawContactId = -1; 
			if(c.moveToFirst()){ 
			    rawContactId = c.getLong(c.getColumnIndex(Data.RAW_CONTACT_ID)); 
			} 
			
			if(rawContactId != -1)
				id = String.valueOf(rawContactId);
			else
				id = null;
			
		    ContactsConstants.showLog("rawContactId:"+rawContactId);
			c.close();
			return id;
		} catch (Exception ssqd) {
			ContactsConstants.showErrorLog("Error in getContactId:"+ssqd.getMessage());
			return id;
		} 
	}//fn getContactId
	
	private void gettingContacts(){
		try {				
			if(errorDbObj == null)
				errorDbObj = new ContactProErrorMessagePersistent(this);
			//ContactsConstants.showLog("checkTableRow:" +errorDbObj.checkTableRow());
			ArrayList idListStr = errorDbObj.getIdList();
	    	String[] OrgDetails = null, Emails = null, PhNos = null, AddressDetails = null;
			Cursor C = null;
			C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
			while (C.moveToNext()) {                   
				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
			    boolean errornotify = false;	
			    //ContactsConstants.showLog("name:"+name);
			    //contactsCheck(name);
				if ( !idListStr.contains(contactId) ){
					errorDbObj.deleteRow(contactId);
				} else if (idListStr.contains(contactId)){
					errornotify = true;
				}
				
				// Get value for Organization
				OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId); 
				
			    // Get value for Emails
			 	//String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
			 	//String[] Emails = queryAllEmailAddressesForContact(contactId);
			 	
			 	// Get value for Phone no
			 	//String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
			 	//String[] PhNos = queryAllPhoneNumbersForContact(contactId);
			 	
			 	// Get value for Address Details
			 	//String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
				/*String[] Emails  = new String[0];
				String[] PhNos   = new String[0];
				String[] OrgDetails = new String[0];
				String[] AddressDetails = new String[0];*/
			 	
				allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
				contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
			}
			C.close();
			C = null;
			ContactsConstants.showLog("contactVect.size gettingContacts:"+contactVect.size());
			if(errorDbObj != null)
				errorDbObj.closeDBHelper();
		} catch (IllegalArgumentException sffee) {
			ContactsConstants.showErrorLog("Error in getContacts:"+sffee.toString());
		}
	}//fn gettingContacts	
	
	private void gettingSAPContacts(){
		try {				
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}						
			if(errorDbObj == null)
				errorDbObj = new ContactProErrorMessagePersistent(this);
			//ContactsConstants.showLog("checkTableRow:" +errorDbObj.checkTableRow());
			ArrayList idListStr = errorDbObj.getIdList();
			String selection = null;
	    	String[] selectionParams = null;
	    	String[] OrgDetails = null, Emails = null, PhNos = null, AddressDetails = null;
	    	/*selection = Data.DISPLAY_NAME + " LIKE ? ";
	    	String sa1 = "%"+"Vij"+"%";
			selectionParams = new String[]{sa1};*/
	    	Cursor C = null;
			//Cursor C  = getContentResolver().query(Data.CONTENT_URI, null,Data.DISPLAY_NAME + " LIKE ? ", new String[] { "Vij" }, null);			
			//Cursor C = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selection, selectionParams, null);
			C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			/*ContactsConstants.showLog("c.size :"+selection); 
			ContactsConstants.showLog("c.size :"+selectionParams[0]+"  "+ selectionParams[1]); 
			ContactsConstants.showLog("c.size :"+C.getCount()); */
			while (C.moveToNext()) {                   
				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
				boolean userSAP = contactProCusDbObj.checkContactExists(contactId);
				//ContactsConstants.showLog("userSAP:" +userSAP);
				if(userSAP){
				    boolean errornotify = false;	
				    //ContactsConstants.showLog("name:"+name);
				    //contactsCheck(name);
					if ( !idListStr.contains(contactId) ){
						errorDbObj.deleteRow(contactId);
					} else if (idListStr.contains(contactId)){
						errornotify = true;
					}
					
					/*if(groupID != null){
						ContactsConstants.showLog(" groupID is not null ");
						long contact = Long.parseLong(contactId);
                        long group = Long.parseLong(groupID);
                        addToGroup(contact, group);
					}else{
						ContactsConstants.showLog(" groupID is null ");
					}*/
					
					// Get value for Organization
					OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId); 
					
					/*// Get value for Emails
				 	String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
				 	
				 	// Get value for Phone no
				 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
				 	
				 	// Get value for Address Details
				 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);*/
				 	
					allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
					contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
				}
			}
			C.close();
			C = null;
			ContactsConstants.showLog("contactVect.size gettingSAPContacts:"+contactVect.size());
			if(errorDbObj != null)
				errorDbObj.closeDBHelper();
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
		} catch (IllegalArgumentException sffee) {
			ContactsConstants.showErrorLog("Error in gettingSAPContacts:"+sffee.toString());
		}
	}//fn gettingSAPContacts
	
	public void addToGroup(long personId, long groupId) {
	    try {
			ContentValues values = new ContentValues();
			values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
			        personId);
			values.put(
			        ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
			        groupId);
			values.put(
			        ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
			        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);

			this.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
		} catch (Exception sfee) {
			ContactsConstants.showErrorLog("Error in addToGroup:"+sfee.toString());
		}
	}//fn addToGroup
	
	final Runnable contactsCall = new Runnable(){
	    public void run()
	    {
	    	try{
			    ContactsConstants.showLog("Viewing Contacts!");
	    		ContactsConstants.showLog("contactVect.size in contactsCall:"+contactVect.size());
				listviewcall();
		        getListView().invalidateViews();
		        stopProgressDialog();				
		        if(searchET.getText().toString().length() > 0){
			        searchET.setText("");		        	
		        }
				sortItems();			
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
    };
		
	private void showAlert(){
		  try {
			  appCall = "";
			  LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			  View layout;
			  boolean showExtra = true;
			  ContactsConstants.showLog("APPLN_NAME_STR:"+ContactsConstants.APPLN_NAME_STR);
			  			  
			  if(appOptions.equals("SERVICEPRO")){
				  layout = inflater.inflate(R.layout.dialog_servicepro,
						  (ViewGroup) findViewById(R.id.layout_root));		
				  showExtra = false;
				  ContactsConstants.showLog("servicepro");
			  }else if(appOptions.equals("SALESPRO")){
				  layout = inflater.inflate(R.layout.dialog_salespro,
						  (ViewGroup) findViewById(R.id.layout_root));
				  showExtra = true;
				  ContactsConstants.showLog("SALESPRO");
			  }else{
				  layout = inflater.inflate(R.layout.dialog_servicepro,
						  (ViewGroup) findViewById(R.id.layout_root));	
				  showExtra = false;
			  }
			  ContactsConstants.showLog("showExtra: "+showExtra);
			  orgNameSel = getOrgName(selectedContactId);			

			  ImageView contactviewimg = (ImageView) layout.findViewById(R.id.contactviewimg);
			  contactviewimg.setOnClickListener(contactviewimglistener); 		  
			  ImageView contacteditimg = (ImageView) layout.findViewById(R.id.contacteditimg);
			  contacteditimg.setOnClickListener(contacteditimglistener); 		  
			  ImageView contactsapdetailimg = (ImageView) layout.findViewById(R.id.contactsapdetailimg);
			  contactsapdetailimg.setOnClickListener(contactsapdetailimglistener); 
			  
			  ImageView ddviewimg = (ImageView) layout.findViewById(R.id.ddviewimg);
			  ddviewimg.setOnClickListener(ddviewimglistener);
			  
			  ImageView interactionsviewimg = (ImageView) layout.findViewById(R.id.interactionsviewimg);
			  interactionsviewimg.setOnClickListener(interactionsviewimglistener);
			  
			  ImageView activityviewimg = (ImageView) layout.findViewById(R.id.activityviewimg);
			  activityviewimg.setOnClickListener(activityviewimglistener);
			  ImageView activitycrimg = (ImageView) layout.findViewById(R.id.activitycrimg);
			  activitycrimg.setOnClickListener(activitycrimglistener);
			  
			  if(showExtra){
				  ImageView SOcrimg = (ImageView) layout.findViewById(R.id.SOcrimg);
				  SOcrimg.setOnClickListener(SOcrimglistener);		  
				  ImageView SOviewimg = (ImageView) layout.findViewById(R.id.SOviewimg);
				  SOviewimg.setOnClickListener(SOviewimglistener);
			  }
			  
			  if(showExtra){
				  ImageView cusviewimg = (ImageView) layout.findViewById(R.id.cusviewimg);
				  cusviewimg.setOnClickListener(cusviewimglistener);
			  }
			  
			  builder = new AlertDialog.Builder(this);
			  builder.setTitle("Options");
			  builder.setView(layout);
			  alertDialog = builder.create();
			  alertDialog.show();
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in showAlert:"+ce.toString());
		}
	}//fn showAlert
	
	private OnClickListener contactviewimglistener = new OnClickListener(){
	      public void onClick(View v) {
	    	  	try {
					ContactsConstants.showLog("contacteditimglistener!");
					alertDialog.dismiss();	    	  	
					ContactsConstants.showLog("contact_pos:"+selectedContactId);
					ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);
					ContactsConstants.showLog("selectedContactId:"+selectedContactId);
					if(internetAccess){
					  	if(userCheck){
					  		ContactsConstants.showLog("Data sync from sap!");
					  		viewAction = "view";
					  		if(contactProCusDbObj == null){
								contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
							}
					  		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
					  		if(contactProCusDbObj != null)
								contactProCusDbObj.closeDBHelper();			        
					        ContactsConstants.showLog("Contact Flag Value : "+ContactsConstants.CONTACTFLAGVALUE);
					       			        	
					        if(ContactsConstants.CONTACTFLAGVALUE.equals("true")){
						        ContactsConstants.showLog("Data sync to sap if part !");
					        	initSoapConnectionForContactEditSynToSAP();	
						        reloadContact();
						        ContactsConstants.VIEWUPDATIONCALLFLAG = true;
					        	Intent i = new Intent(Intent.ACTION_VIEW);
					        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
					        	i.setData(contactUri); 
					        	startActivityForResult(i, INTENT_ACTION_VIEW);
					        }
					        else{
					        	ContactsConstants.showLog("Data sync from sap else part !");
					    		viewAction = "view";
					    		ContactsConstants.VIEWUPDATIONCALLFLAG = true;
					    		initSoapConnectionForContactUpdateCheckFromSAP();
					        }		                		
					  	}
					  	else{
					  		viewAction = "view";
					  		ContactsConstants.VIEWUPDATIONCALLFLAG = true;
					    	ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);			                	 
					    	Intent i = new Intent(Intent.ACTION_VIEW);
					    	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
					    	i.setData(contactUri); 
					    	startActivityForResult(i, INTENT_ACTION_VIEW);
					  	}
					}
					else{
						viewAction = "view";
						ContactsConstants.VIEWUPDATIONCALLFLAG = true;
					  	ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);  
					  	Intent i = new Intent(Intent.ACTION_VIEW);
					  	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
					  	i.setData(contactUri); 
					  	startActivityForResult(i, INTENT_ACTION_VIEW);
					}
				} catch (Exception ce) {
					ContactsConstants.showErrorLog("Error in contactviewimglistener:"+ce.toString());
				}
	      }
	};
	
	private OnClickListener contacteditimglistener = new OnClickListener(){
	      public void onClick(View v) {
	    	  try {
				alertDialog.dismiss();
				  ContactsConstants.showLog("selectedContactId:"+selectedContactId);
				  if(internetAccess){
					  if(userCheck){
				  			ContactsConstants.showLog("Data sync from sap!");
				  			viewAction = "edit";
				  			if(contactProCusDbObj == null){
				  				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
				  			}
				  			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
				  			if(contactProCusDbObj != null)
								contactProCusDbObj.closeDBHelper();
				  			if(ContactsConstants.CONTACTFLAGVALUE.equals("true")){
				  				ContactsConstants.showLog("Data sync to sap!");
				  				initSoapConnectionForContactEditSynToSAP();	
				  				reloadContact();
				  				Intent i = new Intent(Intent.ACTION_EDIT);
				  				Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
				  				i.setData(contactUri); 
				  				startActivityForResult(i, INTENT_ACTION_EDIT);
				  			}
				  			else{
				  				ContactsConstants.showLog("Data sync from sap!");
				        		viewAction = "edit";
				        		initSoapConnectionForContactUpdateCheckFromSAP();
				  			}		                		
					  	}
					  	else{
				  			viewAction = "edit";
				        	ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);
				        	Intent i = new Intent(Intent.ACTION_EDIT);
				        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
				        	i.setData(contactUri); 
				        	startActivityForResult(i, INTENT_ACTION_EDIT);	      
				  		}
				  	}
				  	else{
				  		viewAction = "edit";
				  		ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);
				  		Intent i = new Intent(Intent.ACTION_EDIT);
				  		Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
				  		i.setData(contactUri); 
				  		startActivityForResult(i, INTENT_ACTION_EDIT);	      
				  	}
			} catch (Exception ce) {
				ContactsConstants.showErrorLog("Error in contacteditimglistener:"+ce.toString());
			}
	      }
	};
	
	private OnClickListener contactsapdetailimglistener = new OnClickListener(){
	      public void onClick(View v) {
	    	  	try {
					alertDialog.dismiss();
					ContactsConstants.showLog("selectedContactId:"+selectedContactId);
					if(userCheck){
						try {	             
							try{                    	 
					        	if(errorDbObj == null)
									errorDbObj = new ContactProErrorMessagePersistent(ContactMain.this);
					        	errMsg = errorDbObj.getErrorMsg(String.valueOf(selectedContactId), ContactsConstants.CONTACT_MAINTAIN_API);			
								if(errorDbObj != null)
							    	errorDbObj.closeDBHelper();
					    	}
					    	catch(Exception e){
					    		ContactsConstants.showErrorLog("Error in getting error msg : "+e.toString());
					    	}  	
							
							Intent intent = new Intent(ContactMain.this, ContactProSAPDetails.class);
							intent.putExtra("selId", selectedContactId);                   	
					    	intent.putExtra("errMsg", errMsg);
							startActivityForResult(intent,SapGenConstants.SAPDETAIL_SCREEN);
						} 
						catch (Exception e) {
							ContactsConstants.showErrorLog(e.getMessage());
						}	
					}
					else{
						Toast.makeText(ContactMain.this, "Not in SAP yet!", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception ce) {
					ContactsConstants.showErrorLog("Error in contactsapdetailimglistener:"+ce.toString());
				}	       
	      }
	};	

	private void gotoSettingsScreen(){
		try{
			Intent intent = new Intent(ContactMain.this, ContactsSettings.class);
			startActivityForResult(intent,SapGenConstants.SETTINGS_SCREEN);
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in gotoSettingsScreen:"+ce.toString());
		}
	}//fn gotoSettingsScreen
	
	private OnClickListener interactionsviewimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("Interactionsviewimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(userCheck){
				interactionsviewcalling();
			}
			else{
				contLocal = true;
				appCall = ContactsConstants.APPL_NAME_CALL_INTER_VIEW;
				addLoacalContactToSAP();
			}	    
		}
	};
	
	private void interactionsviewcalling(){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();

			Intent intent = new Intent(ContactMain.this, SalesProCustActivity.class);
			intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
			intent.putExtra("contactId", ContactsConstants.CONTACTSAPID);
			intent.putExtra("customerId", ContactsConstants.ONTACTSAPCUSID);
			intent.putExtra("contactFName", ContactsConstants.CONTACTSAPCUSFNAME);
			intent.putExtra("contactLName", ContactsConstants.CONTACTSAPCUSLNAME);
			intent.putExtra("customerName", orgNameSel);
			startActivityForResult(intent,SapGenConstants.SOCUSTACTIVITY_SCREEN);
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in interactionsviewcalling :"+e.getMessage());
		}	
	}//fn interactionsviewcalling
	
	private OnClickListener activityviewimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("activitycrimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(userCheck){
				activityviewcalling();
			}
			else{
				contLocal = true;
				appCall = ContactsConstants.APPL_NAME_CALL_ACTIVITY_VIEW;
				addLoacalContactToSAP();
			}	    
		}
	}; 
	
	private void activityviewcalling(){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();

			String sapDevId = ContactsConstants.CONTACTSAPID;
			String sapCustomerId = ContactsConstants.ONTACTSAPCUSID;

			//Intent intent = new Intent(ContactMain.this, SalesOrderListActivity.class);
			Intent intent;
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(this, ActivityListforTablet.class);
			}else{
				intent = new Intent(this, ActivityListForPhone.class);
			}			
			//intent.putExtra("app_name", ContactsConstants.APPLN_NAME_STR);
			intent.putExtra("contactId", sapDevId);
			intent.putExtra("customerId", sapCustomerId);
			startActivityForResult(intent,SapGenConstants.SOCREATION_SCREEN);
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in activityviewcalling: "+e.getMessage());
		}	
	}//fn activityviewcalling
	
	private OnClickListener activitycrimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("activitycrimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(userCheck){
				activitycrcalling();
			}
			else{
				contLocal = true;
				appCall = ContactsConstants.APPL_NAME_CALL_ACTIVITY_CRE;
				addLoacalContactToSAP();
			}	    
		}
	}; 
	
	private void activitycrcalling(){
		try {
			if(selContactVect.size() > 0){
        		selContactVect.removeAllElements();
        		selContactVect.clear();
  			}
			
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
			
    		SapGenConstants.showLog("orgNameSel : "+orgNameSel);
			/*Intent intent = new Intent(ContactMain.this, CrtGenActivity.class);
			intent.putExtra("contactId", ContactsConstants.CONTACTSAPID);
			intent.putExtra("customerId", ContactsConstants.ONTACTSAPCUSID);
			intent.putExtra("contactFName", ContactsConstants.CONTACTSAPCUSFNAME);
			intent.putExtra("contactLName", ContactsConstants.CONTACTSAPCUSLNAME);
			intent.putExtra("customerName", orgNameSel);
			intent.putExtra("isContactFlag", true);
			startActivityForResult(intent,SapGenConstants.SOCUSTACTCREATE_SCREEN);*/		
			
			ContactSAPDetails obj = new ContactSAPDetails(selectedContactName, String.valueOf(selectedContactId), ContactsConstants.CONTACTSAPID, ContactsConstants.ONTACTSAPCUSID, ContactsConstants.CONTACTSAPCUSFNAME, ContactsConstants.CONTACTSAPCUSLNAME, orgNameSel);
			selContactVect.addElement(obj);
			ContactProVectSerializable vectObj1 = new ContactProVectSerializable(selContactVect);
    		Intent intent = new Intent(ContactMain.this, CrtGenActivity.class);
    		intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
    		intent.putExtra("selContacts", vectObj1);
			startActivityForResult(intent,SapGenConstants.SOCUSTACTCREATE_SCREEN);
			
			/*Intent intent = new Intent(ContactMain.this, ContactsList.class);
			startActivityForResult(intent,SapGenConstants.SOCUSTACTCREATE_SCREEN);*/
			
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in activitycrcalling: "+e.getMessage());
		}	
	}//fn activitycrcalling
	
	private OnClickListener SOcrimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("SOcrimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(userCheck){
				SOcrcalling();
			}
			else{
				appCall = ContactsConstants.APPL_NAME_CALL_SO_CRE;
				contLocal = true;
				addLoacalContactToSAP();
			}	                
		}
	};
	
	private void SOcrcalling(){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
		
			String sapDevId = ContactsConstants.CONTACTSAPID;
			String sapCustomerId = ContactsConstants.ONTACTSAPCUSID;
		
			//Intent intent = new Intent(ContactMain.this, SalesOrderCreation.class);
			Intent intent;
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(this, SalesOrderCreationTablet.class);
			}else{
				intent = new Intent(this, SalesOrderCreation.class);
			}
			//intent.putExtra("app_name", ContactsConstants.APPLN_NAME_STR);
			intent.putExtra("contactId", sapDevId);
			intent.putExtra("customerId", sapCustomerId);
			startActivityForResult(intent,SapGenConstants.SOCREATION_SCREEN);
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in SOcrcalling: "+e.getMessage());
		}	
	}//fn SOcrcalling
	
	private OnClickListener SOviewimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("SOviewimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(userCheck){
				SOviewcalling();
			}
			else{
				appCall = ContactsConstants.APPL_NAME_CALL_SO_VIEW;
				contLocal = true;
				addLoacalContactToSAP();
			}	                
		}
	};
	
	private void SOviewcalling(){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();

			String sapDevId = ContactsConstants.CONTACTSAPID;
			String sapCustomerId = ContactsConstants.ONTACTSAPCUSID;

			//Intent intent = new Intent(ContactMain.this, SalesOrderListActivity.class);
			Intent intent;
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(this, SalesOrderMainScreenTablet.class);
			}else{
				intent = new Intent(this, SalesOrderListActivity.class);
			}
			
			//intent.putExtra("app_name", ContactsConstants.APPLN_NAME_STR);
			intent.putExtra("contactId", sapDevId);
			intent.putExtra("customerId", sapCustomerId);
			startActivityForResult(intent,SapGenConstants.SOCREATION_SCREEN);
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in SOviewcalling: "+e.getMessage());
		}	
	}//fn SOviewcalling
		
	private OnClickListener cusviewimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			ContactsConstants.showLog("cusviewimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			ContactsConstants.showLog("customerId:"+ContactsConstants.ONTACTSAPCUSID);
			ContactsConstants.showLog("contactId:"+ContactsConstants.CONTACTSAPID);
			try {
				if(userCheck){
					cusviewcalling();
				}
				else{
					appCall = ContactsConstants.APPL_NAME_CALL_CUST_VIEW;
					contLocal = true;
					addLoacalContactToSAP();
				}	
			} 
			catch (Exception e) {
				ContactsConstants.showErrorLog(e.getMessage());
			}				
		}
	};
	
	private void cusviewcalling(){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
							
    		SapGenConstants.showLog("orgNameSel : "+orgNameSel);
    		Intent intent = new Intent(ContactMain.this, CustomerListActivity.class);
			//intent.putExtra("app_name", ContactsConstants.APPLN_NAME_STR);
			intent.putExtra("contactId", ContactsConstants.CONTACTSAPID);
			intent.putExtra("customerId", ContactsConstants.ONTACTSAPCUSID);
			intent.putExtra("contactFName", ContactsConstants.CONTACTSAPCUSFNAME);
			intent.putExtra("contactLName", ContactsConstants.CONTACTSAPCUSLNAME);
			intent.putExtra("customerName", orgNameSel);
			startActivityForResult(intent,SapGenConstants.CUSTCRDINFO_LAUNCH_SCREEN);
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in cusviewcalling: "+e.getMessage());
		}	
	}//fn cusviewcalling
	
	private OnClickListener ddviewimglistener = new OnClickListener(){
		public void onClick(View v) {
			alertDialog.dismiss();
			String strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", address = "";
			ContactsConstants.showLog("ddviewimglistener!");
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			try {
				// Get value for Address Details
			 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(ContactMain.this, String.valueOf(selectedContactId));         	
	         	strStreet = AddressDetails[0];
	         	strCity = AddressDetails[1];
	         	strRegion = AddressDetails[2];
	         	strPostalCode = AddressDetails[3];
	         	strCountry = AddressDetails[4];
	         	
	         	if(strStreet.toString().length() > 0){
	         		address += strStreet.toString().trim();
            	}
            	if(strCity.toString().length() > 0){
            		address += ","+strCity.toString().trim();
            	}
            	if(strRegion.toString().length() > 0){
            		address += ","+strRegion.toString().trim();
            	}                	
            	if(strCountry.toString().length() > 0){
            		address += ","+strCountry.toString().trim();
            	}      	                	
            	if(strPostalCode.toString().length() > 0){
            		address += ","+strPostalCode.toString().trim();
            	}
            	ContactsConstants.showLog("address:"+address);
	         	gotoDrivingDirection(address);
			} 
			catch (Exception e) {
				ContactsConstants.showErrorLog(e.getMessage());
			}				
		}
	};

	private void gotoDrivingDirection(final String address){
		try {			
			LocationResult locationResult = new LocationResult(){ 
	            @Override 
	            public void gotLocation(Location location){ 
	            	lat = ""+location.getLatitude();
	            	lon = ""+location.getLongitude();
	            	geoloc = "My current location is: " + 
	            	        "Latitud = " + location.getLatitude() + 
	            	        "Longitud = " + location.getLongitude(); 
	            	System.out.println("Text from inside:"+ geoloc);
	            		            	
	            	System.out.println("address:"+ address);
	            	
	                //Uri uri =Uri.parse("http://maps.google.com/maps?&saddr="+lat+","+lon+"&daddr=ksrtc bus stand,mysore, karnataka, india");
	                Uri uri =Uri.parse("http://maps.google.com/maps?&saddr="+lat+","+lon+"&daddr="+address);
	        		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	                startActivity(Intent.createChooser(intent, "Directions"));
	            } 
	        }; 
	        MyLocation myLocation = new MyLocation(); 
	        myLocation.getLocation(this, locationResult); 
		}catch (Exception e) {
			ContactsConstants.showErrorLog("gotoDrivingDirection: "+e.getMessage());
		}
	}//fn gotoServiceConfirmation
	
	//click listener for listview
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			final int contact_pos;
			System.out.println("Selected Item "+arg2);
			contact_pos = Integer.parseInt(((ContactDetails)contactVect.elementAt(arg2)).getContactId());		
			selectedContactId = contact_pos;
			selectedContactName = ((ContactDetails)contactVect.elementAt(arg2)).getContactName();
			ContactsConstants.showLog("selectedContactId: "+selectedContactId);
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				gettingContactDetails(selectedContactId);
        			} catch (Exception e) {
        				ContactsConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
        			}
        			contactsDataGetting_Handler.post(showDialog);
				}
			};
	        t.start();		
		}
	};
	
	final Runnable showDialog = new Runnable(){
	    public void run()
	    {
	    	try{
				showAlert();
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in showDialog:"+sfe.toString());
	    	}
	    }	    
    };
	
	private void gettingContactDetails(int contactId){
		try {
			if(contactProCusDbObj == null){
				contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
			}
			userCheck = contactProCusDbObj.checkContactExists(String.valueOf(selectedContactId));
			if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in gettingContactDetails func:"+e.getMessage());
		}
	}//fn gettingContactDetails
	
	private void addLoacalContactToSAP(){
		try{
			ContactsConstants.showLog("contLocal: "+contLocal);
			ContactsConstants.showLog("selectedContactId: "+selectedContactId);
			lastId = selectedContactId;
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
				ContactsConstants.showLog("addLoacalContactToSAP");
				initSoapConnectionForAddContactSynToSAP();
		        sortContactNameFlag = true;
			}     	
		} 
		catch (Exception e) {
			ContactsConstants.showErrorLog("Error in addLoacalContactToSAP func:"+e.getMessage());
		}
	}//fn addLoacalContactToSAP
	
	private void initSoapConnectionForContactUpdateCheckFromSAP(){   
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
         	    	 	
    		if(contactProCusDbObj == null){
    			contactProCusDbObj = new ContactProSAPCPersistent(this);
    		}
    		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
    		if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
    		strContactSAPID = ContactsConstants.CONTACTSAPID;
    		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;
    		strContactSAPCusFName = ContactsConstants.CONTACTSAPCUSFNAME;
    		strContactSAPCusLName = ContactsConstants.CONTACTSAPCUSLNAME;
	     	
    		//Assigning contact value to local value for comparing.
    		strContactSAPCusFName_Value = strContactSAPCusFName;
    		strContactSAPCusLName_Value = strContactSAPCusLName;
    		strOrgTitle_Value = strOrgTitle;
        	strPhoneWork_Value = strPhoneWork;
        	strPhoneMob_Value = strPhoneMob;
        	strPhoneHome_Value = strPhoneHome;
        	strPhoneOther_Value = strPhoneOther;
        	strEmailWork_Value = strEmailWork;
        	strEmailHome_Value = strEmailHome;
        	strEmailOther_Value = strEmailOther;
        	strImType_Value = strImType;
        	strImId_Value = strImId;
        	strStreet_Value = strStreet;
        	strCity_Value = strCity;
        	strRegion_Value = strRegion;
        	strPostalCode_Value = strPostalCode;
        	strCountry_Value = strCountry;
        	strOrgName_Value = strOrgName;
    		    		
        	ContactsConstants.showLog("initSoapConnection strOrgName_Value : "+strOrgName_Value);
        	SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME);    
            envelopeC = null;
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            ContactProInputConstraints C0[];
            C0 = new ContactProInputConstraints[4];
            for(int k=0; k<4; k++){
                C0[k] = new ContactProInputConstraints(); 
            }                        
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, ContactsConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]CONTACT-FOR-EMPLOYEE-GET[.]VERSION[.]0";             
            C0[3].Cdata = "ZGSXCAST_CNTCT10[.]"+strContactSAPID+"[.]"+strContactSAPCusFName+"[.]"+strContactSAPCusLName+"[.]"+strOrgTitle+"[.]"+strPhoneWork+"[.]"+strPhoneMob+"[.]"+strPhoneHome+"[.]"+strPhoneOther+"[.]"+strEmailWork+"[.]"+strEmailHome+"[.]"+strEmailOther+"[.]"+strImType+"[.]"+strImId+"[.]"+strStreet+"[.]"+strCity+"[.]"+strRegion+"[.]"+strPostalCode+"[.]"+strCountry+"[.]"+strContactSAPCusID+"[.]"+strOrgName;
            
            Vector listVect = new Vector();
            for(int k1=0; k1<C0.length; k1++){
                listVect.addElement(C0[k1]);
            }
                                 
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);
            envelopeC.setOutputSoapObject(request);        
            ContactsConstants.showLog(request.toString());
            
            respType = ContactsConstants.RESP_TYPE_GET_UPDATE_CHECK_FROM_SAP; 
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, String.valueOf(selectedContactId), request);
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForContactUpdateCheckFromSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForContactUpdateCheckFromSAP
	
    public void updateReportsConfirmResponseForRefresh(SoapObject soap){	
    	boolean errorflag = false, resMsgErr = false;
        try{ 
        	if(soap != null){
        		ContactsConstants.soapResponse(this, soap, false);
            	taskErrorMsgStr = ContactsConstants.SOAP_RESP_MSG;
            	taskErrorType = ContactsConstants.SOAP_ERR_TYPE;
            	ContactsConstants.showLog("On soap response : "+soap.toString());
            	String soapMsg = soap.toString();
            	resMsgErr = ContactsConstants.getSoapResponseSucc_Err(soapMsg);   
            	ContactsConstants.showLog("resMsgErr : "+resMsgErr);
            	if(!resMsgErr){
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
		                            
		                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCT10")){
		                                if(category != null)
		                                    category = null;
		                                    
		                                category = new ContactProOutputConstraints(resArray);	                              
		                                
		                            	if(j == 3){
		                                	ContactsConstants.showLog("********************************");
		                                	ContactsConstants.showLog("* Updation is there! *" + selectedContactId);
		                                	ContactsConstants.showLog("********************************");
		                                	ContactsConstants.VIEWREFRESHCALLFLAG = true;
		                                	
	                	                	String fname = category.getNameFirst();
	                	                	String lname = category.getNameLast();
	                	                	if(fname == null || fname.length() == 0){
	                	                		fname = "";
	                	            		}
	                	                	if(lname == null || lname.length() == 0){
	                	                		lname = "";
	                	            		}
	                	                	if(fname.length() > 0 && lname.length() > 0){
	                	                		final ContentResolver cr = getContentResolver();
	                    	                	if (Integer.parseInt(Build.VERSION.SDK) == 8 ) {
	                    	                		cr.delete( Uri.parse(String.valueOf(ContactsContract.RawContacts.CONTENT_URI)), "_ID=? ", new String[]{String.valueOf(selectedContactId)});
	                    	                	}else{
	                    	                		cr.delete( Uri.parse(String.valueOf(ContactsContract.RawContacts.CONTENT_URI)), "_ID=? ", new String[]{String.valueOf(selectedContactId)});
	                    	                	}
	                    	                	System.out.println("selectedPosition ContactId:"+selectedContactId);
	    	                                	         
			                                	ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 
				                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
				                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
				                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
				                                .build()); 
		
				                                //------------------------------------------------------ First Names 
				                                if(category.getNameFirst().length() != 0) 
				                                { 
				                                	String d_name = category.getNameFirst()+" "+category.getNameLast()+"                    "+category.getParnr();
				                                	ContactsConstants.showLog("d_name:"+d_name);
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build()); 
				                                }
				                                else{
				                                	if(category.getEmailPrsnl().length() != 0){
				                                		String d_name = category.getEmailPrsnl()+"                    "+category.getParnr();
					                                	ContactsConstants.showLog("d_name:"+d_name);
					                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
					                                }
				                                	else if(category.getEmailWork().length() != 0){
				                                		String d_name = category.getEmailWork()+"                    "+category.getParnr();
					                                	ContactsConstants.showLog("d_name:"+d_name);
					                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
					                                }
				                                	else if(category.getEmailOthers().length() != 0){
				                                		String d_name = category.getEmailOthers()+"                    "+category.getParnr();
					                                	ContactsConstants.showLog("d_name:"+d_name);
					                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
					                                }
				                                	else{
				                                		String d_name = "Unknown"+"                    "+category.getParnr();
					                                	ContactsConstants.showLog("d_name:"+d_name);
					                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, d_name).build());
					                                }
				                                }
				                              	                                
				                                //------------------------------------------------------ Mobile Number 
				                                if(category.getTelfMobile().length() != 0) 
				                                { 
				                                	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfMobile()) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
					                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) 
					                                .build()); 
				                                } 
		
				                                //------------------------------------------------------ Home Numbers 
				                                if(category.getTelfPrsnl().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfPrsnl()) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
					                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME) 
					                                .build()); 
				                                } 
		
				                                //------------------------------------------------------ Work Numbers 
				                                if(category.getTelfWork().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfWork()) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
					                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK) 
					                                .build()); 
				                                }
				                                
				                                //------------------------------------------------------ Others Numbers 
				                                if(category.getTelfOthers().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, category.getTelfOthers()) 
					                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 
					                                ContactsContract.CommonDataKinds.Phone.TYPE_OTHER) 
					                                .build()); 
				                                }
				                                		                                
				                                //------------------------------------------------------ Work Email 
				                                if(category.getEmailWork().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailWork()) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK) 
					                                .build()); 
				                                } 
				                                
				                                //------------------------------------------------------ Home Email 
				                                if(category.getEmailPrsnl().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailPrsnl()) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME) 
					                                .build()); 
				                                } 
				                                
				                                //------------------------------------------------------ Other Email 
				                                if(category.getEmailOthers().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.DATA, category.getEmailOthers()) 
					                                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER) 
					                                .build()); 
				                                } 
				                                
				                                //------------------------------------------------------ Imtype 
				                                if(category.getImType().length() != 0 && category.getIMId().length() != 0) 
				                                { 
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.Im.DATA, category.getIMId()) 
					                                .withValue(ContactsContract.CommonDataKinds.Im.TYPE, category.getImType()) 
					                                .build()); 
				                                } 	                                
				                                
				                                //------------------------------------------------------ Address
				                                if(category.getLand1p().length() != 0) 
				                                { 
				                                	String streetData = "";
				                                	if(category.getStrasp().length() != 0){
				                                		streetData += category.getStrasp();
				                                	}
					                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
					                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
					                                .withValue(ContactsContract.Data.MIMETYPE, 
					                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, streetData)
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, category.getOrt01p()) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, category.getRegiop())
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, category.getPstlzp()) 
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, category.getLand1p()) 			                                
					                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) 
					                                .build()); 
				                                } 
				                                
		
				                                //------------------------------------------------------ CustomerName and fun
				                                if(category.getKunnrName1().length() != 0) 
				                                { 
				                                	//
				                                	ContactsConstants.showLog("Type Work Value : "+ContactsContract.CommonDataKinds.Organization.TYPE_WORK);
				                                	String funcNameStr = strOrgTitle_Value;
				                                	try {
														if(category.getFnctn().length() != 0)
															funcNameStr = category.getFnctn().trim();
													} catch (Exception e) {
														e.printStackTrace();
													}
				                                	
				                                	if(ContactsContract.CommonDataKinds.Organization.TYPE_WORK != 1){
						                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, category.getKunnrName1()) 
						                                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, funcNameStr)  
						                                .build()); 
				                                	}
				                                	else{
				                                		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI) 
						                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0) 
						                                .withValue(ContactsContract.Data.MIMETYPE, 
						                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE) 
						                                .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, category.getKunnrName1()) 
						                                .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, funcNameStr) 
						                                .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK) 
						                                .build()); 
				                                	}
				                                }
				                                
				                                // Asking the Contact provider to create a new contact 
				                                try 
				                                { 
				                                	ContentProviderResult[] ress = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 
				                                	if (ress!=null && ress[0]!=null) { 
				                                		Uri newContactUri = ress[0].uri;
				                                		Log.d("", "URI added contact:"+ newContactUri); 
				                        				String uri = newContactUri.toString();
				                        				int firstindex = uri.lastIndexOf("/");
				                        				String device_cont_id = uri.substring(firstindex+1, uri.length());
				                        				ContactsConstants.showLog("Contact id is:"+device_cont_id);
				                        				//ContactsConstants.showLog("Contact id length is:"+device_cont_id.length());
				                        				
				                        				//updatind data to local device DB
				                        				contactProCusDbObj.update_row_data(String.valueOf(selectedContactId), device_cont_id, category.getParnr().toString().trim(), 
				                        						category.getKunnr().toString().trim(), category.getNameFirst().toString().trim(), 
				                        						category.getNameLast().toString().trim());		                        				
				                	                	selectedContactId = Integer.parseInt(device_cont_id);		
				                	                	if(contactProCusDbObj != null)
				                	        				contactProCusDbObj.closeDBHelper();
				                	                	reloadContact();
				                                	}
				                                	else 
				                                		Log.e("", "Contact not added."); 
				                                } 
				                                catch (Exception e) 
				                                { 
				                                	e.printStackTrace(); 
				                                	if(contactProCusDbObj != null)
				                        				contactProCusDbObj.closeDBHelper();
				                                	Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show(); 
				                                }  
	                	                	}
	                	                	else{
	                	                		
	                	                	}
		                            	}
		                                ContactsConstants.showLog("********************************");
		                                ContactsConstants.showLog("SAPCustomerContactID:"+category.getParnr());
		                                ContactsConstants.showLog("SAPCustomerFirstName:"+category.getNameFirst());
		                                ContactsConstants.showLog("SAPCustomerLastName:"+category.getNameLast());
		                                ContactsConstants.showLog("SAPCustomerFunction:"+category.getFnctn());
		                                ContactsConstants.showLog("SAPCustomerTELFWork:"+category.getTelfWork());
		                                ContactsConstants.showLog("SAPCustomerTELFMobile:"+category.getTelfMobile());
		                                ContactsConstants.showLog("SAPCustomerTELFPrsl:"+category.getTelfPrsnl());
		                                ContactsConstants.showLog("SAPCustomerTELFOthers:"+category.getTelfOthers());
		                                ContactsConstants.showLog("SAPCustomerEmailWork:"+category.getEmailWork());
		                                ContactsConstants.showLog("SAPCustomerEmailPrsl:"+category.getEmailPrsnl());
		                                ContactsConstants.showLog("SAPCustomerEmailOther:"+category.getEmailOthers());
		                                ContactsConstants.showLog("********************************");
		                                
		                                //Assigning contact value to local value for comparing.
		                                strContactSAPCusFName_Value = category.getNameFirst();
		                        		strContactSAPCusLName_Value = category.getNameLast();
		                        		strOrgTitle_Value = category.getFnctn();
		                            	strPhoneWork_Value = category.getTelfWork();
		                            	strPhoneMob_Value = category.getTelfMobile();
		                            	strPhoneHome_Value = category.getTelfPrsnl();
		                            	strPhoneOther_Value = category.getTelfOthers();
		                            	strEmailWork_Value = category.getEmailWork();
		                            	strEmailHome_Value = category.getEmailPrsnl();
		                            	strEmailOther_Value = category.getEmailOthers();
		                            	strImType_Value = category.getImType();
		                            	strImId_Value = category.getIMId();
		                            	strStreet_Value = category.getStrasp();
		                            	strCity_Value = category.getOrt01p();
		                            	strRegion_Value = category.getRegiop();
		                            	strPostalCode_Value = category.getPstlzp();
		                            	strCountry_Value = category.getLand1p();
		                            	strOrgName_Value = category.getKunnrName1();
		                            }	                           
		                        }
		                        else if(j == 0){
		                            String errorMsg = pii.getProperty(j).toString();
		                            ContactsConstants.showLog("Inside J == 0 ");
		                            ContactsConstants.showLog("********************************");
	                            	/*ContactsConstants.showLog("* Updation is not there! *"+ selectedContactId);
	                            	ContactsConstants.showLog("********************************");*/
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
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForRefresh:"+sff.toString());
        	errorflag = true;
        } 
        finally{   	   
            //stopProgressDialog();
            //reloadContact();
            ContactsConstants.showLog("VIEWUPDATIONCALLFLAG  "+ContactsConstants.VIEWUPDATIONCALLFLAG);            
            if(viewAction.equals("view")){
            	viewAction = "view";
            	ContactsConstants.VIEWUPDATIONCALLFLAG = true;
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
            	i.setData(contactUri); 
            	startActivityForResult(i, INTENT_ACTION_VIEW);
            }
            if(viewAction.equals("edit")){	
            	viewAction = "edit";
	        	Intent i = new Intent(Intent.ACTION_EDIT);
	        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
	        	i.setData(contactUri); 
	        	startActivityForResult(i, INTENT_ACTION_EDIT);
            }
        }
    }//fn updateReportsConfirmResponseForRefresh
    	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ADD_CONTACT, 0, "Add Contact");
		menu.add(0, MENU_SORT_BY_NAME, 0, "Sort by Name");
		menu.add(0, MENU_SETTINGS, 0, "Settings");
		menu.add(0, REFRESH, 0, "Refresh");
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ADD_CONTACT: {
				gotoAddContact();
		        break;
			}
			case MENU_SORT_BY_NAME: {
				sortItemsAction(ContactsConstants.CONTACT_SORT_NAME);
		        break;
			}
			case MENU_SETTINGS: {
				gotoSettingsScreen();
		        break;
			}
			case REFRESH: {
				refreshContacts();
		        break;
			}
			
	    }
		return super.onOptionsItemSelected(item);
	}
	
	private void sortItemsAction(int sortInd){
		try{
			sortIndex = sortInd;
			if(sortInd == ContactsConstants.CONTACT_SORT_NAME)
				sortContactNameFlag = !sortContactNameFlag;									
			Collections.sort(contactVect, contactSortComparator);	
			getListView().invalidateViews();
		}
		catch(Exception sfg){
			ContactsConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	private void sortItems(){
		try{
			sortIndex = ContactsConstants.CONTACT_SORT_NAME;
			sortContactNameFlag = true;									
			Collections.sort(contactVect, contactSortComparator);
			ContactsConstants.showLog("sortItems");  	
			listviewcall();
	        getListView().invalidateViews();
		}
		catch(Exception sfg){
			ContactsConstants.showErrorLog("Error in sortItems : "+sfg.toString());
		}
	}//fn sortItemsAction
			
	public void gotoAddContact(){
		viewAction = "add";
		Intent i = new Intent(Intent.ACTION_INSERT);
    	Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
    	i.setData(contactUri); 
    	startActivityForResult(i, INTENT_ACTION_ADD_CONTACT);
	}//fn gotoAddContact
	
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 		
		String text = s.toString().trim().toLowerCase();
		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			adpterForTablet.filter(text);
		}else{
			adpterForPhone.filter(text);
		}

		/*
		 * 
		String str = s.toString().trim();
		//searchItemCall(str);
		if(str.length() >= 2){
			searchItemCall(s.toString());
		}else{
			if(str.length() == 0){
				getAllContacts();
			}else{
				ContactsConstants.showErrorDialog(this, getString(R.string.CONTACTPRO_SEARCH_SHORT_TEXTS));
			}
			//ContactsConstants.showErrorDialog(this, getString(R.string.CONTACTPRO_SEARCH_SHORT_TEXTS));
		}*/
	} 
	
	private void searchItemCall(final String searchStr){
		if(pdialog != null)
			pdialog = null;		
		
		if(pdialog == null){
			pdialog = ProgressDialog.show(ContactMain.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				searchItemsAction(searchStr);
        			} catch (Exception e) {
        				ContactsConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
        			}
        			contactsSearchData_Handler.post(searchCall);
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
				listviewcall();
		        getListView().invalidateViews();
	    	} catch(Exception sfe){
	    		ContactsConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
    };	
	
	private void searchItemsAction(String match){  
        try{       
            searchStr = match;
            String mattStr = "", mattDescStr = "", mattId;
            String strValue = null;
            boolean errornotify = false;
                        
            String selection = null;
	    	String[] selectionParams = null;
	    	selection = Data.DISPLAY_NAME + " LIKE ? ";
	    	String sa1 = "%"+match+"%";
			selectionParams = new String[]{sa1};
			Cursor C = null;
			
			if(match != null && match.length() > 0){
				contactVect.clear();
				C = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, selection, selectionParams, null);
				while (C.moveToNext()) {                   
					String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
					String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 					
					if(SapGenConstants.enterValue){
						boolean userSAP = contactProCusDbObj.checkContactExists(contactId);
						if(userSAP){	
							// Get value for Organization
							String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId); 
							
						    // Get value for Emails
						 	String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
						 	//String[] Emails = queryAllEmailAddressesForContact(contactId);
						 	
						 	// Get value for Phone no
						 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
						 	//String[] PhNos = queryAllPhoneNumbersForContact(contactId);
						 	
						 	// Get value for Address Details
						 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
							/*String[] Emails  = new String[0];
							String[] PhNos   = new String[0];
							String[] OrgDetails = new String[0];
							String[] AddressDetails = new String[0];*/
						 	
							//allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
							contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
						}
					}else{						
						// Get value for Organization
						String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId); 
						
					    // Get value for Emails
					 	String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
					 	//String[] Emails = queryAllEmailAddressesForContact(contactId);
					 	
					 	// Get value for Phone no
					 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
					 	//String[] PhNos = queryAllPhoneNumbersForContact(contactId);
					 	
					 	// Get value for Address Details
					 	String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
						/*String[] Emails  = new String[0];
						String[] PhNos   = new String[0];
						String[] OrgDetails = new String[0];
						String[] AddressDetails = new String[0];*/
					 	
						//allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
						contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
					}					
				}
				ContactsConstants.showLog("contactVect.size searchItemsAction gettingSAPContacts:"+contactVect.size());
			}else{
				contactVect.clear();
				if((allContactVect != null) && (allContactVect.size() > 0)){
	                for(int i = 0; i < allContactVect.size(); i++){  
	                	strValue = (String)((ContactDetails)allContactVect.elementAt(i)).getContactName();
	                    mattId = (String)((ContactDetails)allContactVect.elementAt(i)).getContactId();
	                    errornotify = (boolean)((ContactDetails)allContactVect.elementAt(i)).getErrorNotify();
	                    String[] Emails = ContactsConstants.getContactEmailsDetails(this, mattId);
	                 	String[] PhNos = ContactsConstants.getContactPhDetails(this, mattId);        
	            		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, mattId);
	            		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, mattId);
	                    if(strValue != null){
	                    	contactVect.addElement(new ContactDetails(""+strValue, ""+mattId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
	                    }
	                }
				}
			}
			C.close();
			C = null;
			            
            /*if((allContactVect != null) && (allContactVect.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){             
                    contactVect.clear();
                    for(int i = 0; i < allContactVect.size(); i++){ 
                    	strValue = null;
                        mattStr = "";
                        mattDescStr = "";
                        strValue = (String)((ContactDetails)allContactVect.elementAt(i)).getContactName();
                        mattId = (String)((ContactDetails)allContactVect.elementAt(i)).getContactId();
                        errornotify = (boolean)((ContactDetails)allContactVect.elementAt(i)).getErrorNotify();
                        String[] Emails = ContactsConstants.getContactEmailsDetails(this, mattId);
                     	String[] PhNos = ContactsConstants.getContactPhDetails(this, mattId);
                		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, mattId);
                		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, mattId);
                        if(strValue != null){
                            mattStr = strValue.trim().toLowerCase();
                            match = match.toLowerCase();
                            if((mattStr.indexOf(match) >= 0)){
                            	contactVect.addElement(new ContactDetails(""+strValue, ""+mattId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
                            }
                        }
                    }//for 
                }
                else{
                    contactVect.clear();
                    for(int i = 0; i < allContactVect.size(); i++){  
                    	strValue = (String)((ContactDetails)allContactVect.elementAt(i)).getContactName();
                        mattId = (String)((ContactDetails)allContactVect.elementAt(i)).getContactId();
                        errornotify = (boolean)((ContactDetails)allContactVect.elementAt(i)).getErrorNotify();
                        String[] Emails = ContactsConstants.getContactEmailsDetails(this, mattId);
                     	String[] PhNos = ContactsConstants.getContactPhDetails(this, mattId);        
                		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, mattId);
                		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, mattId);
                        if(strValue != null){
                        	contactVect.addElement(new ContactDetails(""+strValue, ""+mattId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
                        }
                    }
                }
            }//if
            else
                return;*/
        }//try
        catch(Exception we){
            we.printStackTrace();
        }
    }//fn searchItemsAction  
	
	private void getAllContacts(){
		if(pdialog != null)
			pdialog = null;		
		
		if(pdialog == null){
			pdialog = ProgressDialog.show(ContactMain.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				getAllContactList();
        			} catch (Exception e) {
        				ContactsConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
        			}
        			contactsSearchData_Handler.post(searchCall);
        			stopProgressDialog();
				}
			};
	        t.start();	
		}		
	}//fn getAllContacts
	
	private void getAllContactList(){
		try{
			String mattId;
	        String strValue = null;
	        boolean errornotify = false;
			contactVect.clear();
			if((allContactVect != null) && (allContactVect.size() > 0)){
			    for(int i = 0; i < allContactVect.size(); i++){  
			    	strValue = (String)((ContactDetails)allContactVect.elementAt(i)).getContactName();
			        mattId = (String)((ContactDetails)allContactVect.elementAt(i)).getContactId();
			        errornotify = (boolean)((ContactDetails)allContactVect.elementAt(i)).getErrorNotify();
			        String[] Emails = ContactsConstants.getContactEmailsDetails(this, mattId);
			     	String[] PhNos = ContactsConstants.getContactPhDetails(this, mattId);        
					String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, mattId);
					String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, mattId);
			        if(strValue != null){
			        	contactVect.addElement(new ContactDetails(""+strValue, ""+mattId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
			        }
			    }
			}
		} catch (Exception sfre) {
			ContactsConstants.showErrorLog("Error in getAllContactList:"+sfre.toString());
		}
	}//fn getAllContactList
	
	public void reloadContact(){
		getContacts();
	}//fn reloadContact
	
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
	
	@Override 
    public void onActivityResult(int reqCode, int resultCode, Intent data) { 
    	super.onActivityResult(reqCode, resultCode, data); 
    	ContactsConstants.showLog("resultCode:   "+resultCode);
    	ContactsConstants.showLog("reqCode:   "+reqCode);
    	if(reqCode == INTENT_ACTION_EDIT){
    		if (resultCode == Activity.RESULT_OK) {     	
    			AlertDialog alertDialog;
    			boolean chekOrgValue = checkOrg(selectedContactId);
        		if(!chekOrgValue){
        			alertDialog = new AlertDialog.Builder(this)
        			.setMessage("Please enter your Organization name")
        			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
        				public void onClick(DialogInterface dialog, int which) {
        					Intent i = new Intent(Intent.ACTION_EDIT);
            	        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
            	        	i.setData(contactUri); 
            	        	startActivityForResult(i, INTENT_ACTION_EDIT);
        				}
        				})        			
        				.create();
        				alertDialog.show();
        		}
        		else{  
        			updationCall();        			                	       			 
        		}
    		}
    	}         
    	else if(reqCode == INTENT_ACTION_VIEW){
    		ContactsConstants.showLog("reqCode:   "+reqCode);
    		ContactsConstants.showLog("resultCode:   "+resultCode);
    		if (resultCode == Activity.RESULT_OK) {    			
    			ContactsConstants.showLog("Activity.RESULT_OK:   "+Activity.RESULT_OK);   	     	
    		}
    		else if (resultCode == Activity.RESULT_CANCELED) { 
    			ContactsConstants.showLog("Activity.RESULT_CANCELED:   "+Activity.RESULT_CANCELED);
    			//reloadContact();gg
    		}
    	}
    	else if(reqCode == INTENT_ACTION_EDIT_4_ADD_CONTACT){
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
    	else if (reqCode == INTENT_ACTION_ADD_CONTACT) {
    		ContactsConstants.showLog("Activity.RESULT_OK:"+Activity.RESULT_OK);
    		ContactsConstants.showLog("Activity.RESULT_CANCELED:"+Activity.RESULT_CANCELED);
    		if (resultCode == Activity.RESULT_OK) {  
    			Cursor C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
    			while (C.moveToNext()) {                   
    				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
    				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
    				/*boolean errornotify = false;
    				String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
                 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
            		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId);
            		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
    				allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
    				contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));*/
    				lastId = Integer.parseInt(contactId);
    			}
    			C.close();    			
        		
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
    	        sortContactNameFlag = true;
        	}        
    	}         	
    	else if(reqCode ==SapGenConstants.SETTINGS_SCREEN){
    		if (resultCode == Activity.RESULT_OK) {
    			reloadContact();
    		}
    	}
    } 	
	
	private void updationCall(){
		ContactsConstants.showLog("Else part for response!");
		boolean contactCmp = false;
		//Comparing contact value for syn to SAP                	
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
     	
	 	/*Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, 
				null, 
				ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID +" = ?", 
				new String[]{String.valueOf(selectedContactId)}, null);
        //if (nameCur.moveToFirst()) {
        while(nameCur.moveToNext()) {
        	String givenNameValue = nameCur.getString(
        			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
        	String middleNameValue = nameCur.getString(
        			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));   
        	String middleNameValue = nameCur.getString(
        			nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
	 	    strContactSAPCusFName = givenNameValue;
 	    	strContactSAPCusLName = middleNameValue; 	
 	    	
 	    	if(givenNameValue != null && !givenNameValue.equals("1")){
 	    		break;
 	    	}
 	    	
 	    	if(middleNameValue != null){
 	    		break;
 	    	}
        } 
        nameCur.close();*/
        
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
        Uri dataUri = Uri.withAppendedPath(contactUri, Contacts.Data.CONTENT_DIRECTORY);
        Cursor nameCursor = getContentResolver().query(
        		dataUri,
        		null,
        		Data.MIMETYPE+"=?",
        		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
        		null);
        while (nameCursor.moveToNext())
        {
        	String  firstName = nameCursor.getString(nameCursor.getColumnIndex(Data.DATA2));
        	String  lastName = nameCursor.getString(nameCursor.getColumnIndex(Data.DATA3));
        	strContactSAPCusFName = firstName;
  	    	strContactSAPCusLName = lastName; 
        }
        nameCursor.close();
        
        ContactsConstants.showLog("givenNameValue: "+strContactSAPCusFName+"  "+strContactSAPCusLName);
        
        if(strContactSAPCusFName == null || strContactSAPCusFName.length() == 0){
        	strContactSAPCusFName = "";
		}
        if(strContactSAPCusLName == null || strContactSAPCusLName.length() == 0){
        	strContactSAPCusLName = "";
		}
        		            
        if(!strContactSAPCusFName_Value.equalsIgnoreCase(strContactSAPCusFName)){
        	contactCmp = true;
        }
        if(!strContactSAPCusLName_Value.equalsIgnoreCase(strContactSAPCusLName)){
        	contactCmp = true;
        }
        if(!strOrgTitle_Value.equalsIgnoreCase(strOrgTitle)){
        	contactCmp = true;
        }
        if(!strPhoneWork_Value.equalsIgnoreCase(strPhoneWork)){
        	contactCmp = true;
        }
        if(!strPhoneMob_Value.equalsIgnoreCase(strPhoneMob)){
        	contactCmp = true;
        }
        if(!strPhoneHome_Value.equalsIgnoreCase(strPhoneHome)){
        	contactCmp = true;
        }
        if(!strPhoneOther_Value.equalsIgnoreCase(strPhoneOther)){
        	contactCmp = true;
        }
        if(!strEmailWork_Value.equalsIgnoreCase(strEmailWork)){
        	contactCmp = true;
        }
        if(!strEmailHome_Value.equalsIgnoreCase(strEmailHome)){
        	contactCmp = true;
        }
        if(!strEmailOther_Value.equalsIgnoreCase(strEmailOther)){
        	contactCmp = true;
        }
        if(!strImType_Value.equalsIgnoreCase(strImType)){
        	contactCmp = true;
        }
        if(!strImId_Value.equalsIgnoreCase(strImId)){
        	contactCmp = true;
        }
        if(!strStreet_Value.equalsIgnoreCase(strStreet)){
        	contactCmp = true;
        }
        if(!strCity_Value.equalsIgnoreCase(strCity)){
        	contactCmp = true;
        }
        if(!strRegion_Value.equalsIgnoreCase(strRegion)){
        	contactCmp = true;
        }
        if(!strPostalCode_Value.equalsIgnoreCase(strPostalCode)){
        	contactCmp = true;
        }
        if(!strPostalCode_Value.equalsIgnoreCase(strPostalCode)){
        	contactCmp = true;
        }
        if(!strCountry_Value.equalsIgnoreCase(strCountry)){
        	contactCmp = true;
        }
        if(!strOrgName_Value.equalsIgnoreCase(strOrgName)){
        	contactCmp = true;
        }
        ContactsConstants.showLog("contactCmp:"+contactCmp);
        if(contactCmp){
    		initSoapConnectionForContactEditSynToSAP(); 
    	} 
    	else{
    		ContactsConstants.VIEWUPDATIONCALLFLAG = true;
    	}
	}//fn updationCall
	
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

    		if(contactProCusDbObj == null){
    			contactProCusDbObj = new ContactProSAPCPersistent(this);
    		}
    		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
    		if(contactProCusDbObj != null)
				contactProCusDbObj.closeDBHelper();
    		strContactSAPID = ContactsConstants.CONTACTSAPID;
    		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;
	     	
    		if(strContactSAPID.length() != 0){    			
    			// Get value for Givenname and middlename
        		/*Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, 
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
                nameCur.close(); */               

                Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
                Uri dataUri = Uri.withAppendedPath(contactUri, Contacts.Data.CONTENT_DIRECTORY);
                Cursor nameCursor = getContentResolver().query(
                		dataUri,
                		null,
                		Data.MIMETYPE+"=?",
                		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
                		null);
                while (nameCursor.moveToNext())
                {
                	String  firstName = nameCursor.getString(nameCursor.getColumnIndex(Data.DATA2));
                	String  lastName = nameCursor.getString(nameCursor.getColumnIndex(Data.DATA3));
                	ContactsConstants.showLog("givenNameValue initsoap"+firstName+"  "+lastName);
        	 	    strContactSAPCusFName = firstName;
       	 	    	strContactSAPCusLName = lastName;
                }
                nameCursor.close();
                
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

	            internetAccess = SapGenConstants.checkConnectivityAvailable(ContactMain.this);
	            if(internetAccess){
		            respType = ContactsConstants.RESP_TYPE_GET_EDIT_2_SAP;
		            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, String.valueOf(selectedContactId), request);
	            }
	            else{
	            	ContactsConstants.showLog("No internet connection!");
	            	if(contactProCusDbObj == null){
	        			contactProCusDbObj = new ContactProSAPCPersistent(this);
	        		}
	        		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
	        		if(contactProCusDbObj != null)
	    				contactProCusDbObj.closeDBHelper();
	        		strContactSAPID = ContactsConstants.CONTACTSAPID;
	        		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;            	     	
	        		if(strContactSAPID.length() != 0){    			
	        			// Get value for Givenname and middlename
	            		/*Cursor nameCur1 = cr.query(ContactsContract.Data.CONTENT_URI, 
	            						null, 
	            						ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID +" = ?", 
	            						new String[]{String.valueOf(selectedContactId)}, null);
	                    //if (nameCur.moveToFirst()) {
	                    while(nameCur1.moveToNext()) {
	                    	String givenNameValue = nameCur1.getString(
	                    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	                    	String middleNameValue = nameCur1.getString(
	                    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));            	
	            	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
	            	 	    strContactSAPCusFName = givenNameValue;
	           	 	    	strContactSAPCusLName = middleNameValue;
	           	 	    	
	           	 	    	if(middleNameValue != null){
	           	 	    		break;
	           	 	    	}
	                    } 
	                    nameCur1.close();*/
	                    
	                    Uri contactUri1 = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
	                    Uri dataUri1 = Uri.withAppendedPath(contactUri1, Contacts.Data.CONTENT_DIRECTORY);
	                    Cursor nameCursor1 = getContentResolver().query(
	                    		dataUri1,
	                    		null,
	                    		Data.MIMETYPE+"=?",
	                    		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
	                    		null);
	                    while (nameCursor1.moveToNext())
	                    {
	                    	String  firstName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA2));
	                    	String  lastName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA3));
	                    	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
	            	 	    strContactSAPCusFName = firstName;
	           	 	    	strContactSAPCusLName = lastName;
	                    }
	                    nameCursor1.close();
	                    
	                    if(strContactSAPCusFName == null || strContactSAPCusFName.length() == 0){
	                    	strContactSAPCusFName = "";
	            		}
	                    if(strContactSAPCusLName == null || strContactSAPCusLName.length() == 0){
	                    	strContactSAPCusLName = "";
	            		}	
	                    strContactSAPCusFNameValue = strContactSAPCusFName;
	                    strContactSAPCusLNameValue = strContactSAPCusLName;
	        		}else{
	        			ContactsConstants.showLog("initSoapConnection from else");
	        			/*String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	                 	String[] nameWhereParams = new String[]{String.valueOf(selectedContactId), 
	                 		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
	                 	Cursor nameCur1 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
	                                null, nameWhere, nameWhereParams, null);
	                 	while(nameCur1.moveToNext()) { 
	                 		String givenNameValue = nameCur1.getString(
	            	    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	            	    	String middleNameValue = nameCur1.getString(
	            	    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
	            	 	    ContactsConstants.showLog("givenNameValue from else"+givenNameValue+"  "+middleNameValue);
	            	 	    strContactSAPCusFName = givenNameValue;
	          	 	    	strContactSAPCusLName = middleNameValue;
	                 	} 
	                 	nameCur1.close();*/
	                 	
	                 	Uri contactUri1 = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
	                    Uri dataUri1 = Uri.withAppendedPath(contactUri1, Contacts.Data.CONTENT_DIRECTORY);
	                    Cursor nameCursor1 = getContentResolver().query(
	                    		dataUri1,
	                    		null,
	                    		Data.MIMETYPE+"=?",
	                    		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
	                    		null);
	                    while (nameCursor1.moveToNext())
	                    {
	                    	String  firstName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA2));
	                    	String  lastName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA3));
	                    	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
	                    	strContactSAPCusFName = firstName;
	          	 	    	strContactSAPCusLName = lastName;
	                    }
	                    nameCursor1.close();
	                    
	                 	strNewContactSAPCusFName = strContactSAPCusFName;
	                 	strNewContactSAPCusLName = strContactSAPCusLName;
	                 	if(strNewContactSAPCusFName == null || strNewContactSAPCusFName.length() == 0){
	                 		strNewContactSAPCusFName = "";
	            		}
	                 	if(strNewContactSAPCusLName == null || strNewContactSAPCusLName.length() == 0){
	                 		strNewContactSAPCusLName = "";
	            		}                		
	                 	strContactSAPCusFNameValue = strNewContactSAPCusLName;
	                    strContactSAPCusLNameValue = strNewContactSAPCusLName;
	        		}
	            	if(strContactSAPCusFNameValue == null || strContactSAPCusFNameValue.length() == 0){
	            		strContactSAPCusFNameValue = "";
	            	}
	            	if(strContactSAPCusLNameValue == null || strContactSAPCusLNameValue.length() == 0){
	            		strContactSAPCusLNameValue = "";
	            	}
	            	contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
	            	if(contactProCusDbObj != null)
	    				contactProCusDbObj.closeDBHelper();
	        		strContactSAPID = ContactsConstants.CONTACTSAPID;
	        		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;
	        		
	            	ContactsConstants.showLog("strContactSAPIDValue : "+strContactSAPID);
	            	ContactsConstants.showLog("strContactSAPCusIDValue : "+strContactSAPCusID);
	            	ContactsConstants.showLog("strContactSAPCusFNameValue : "+strContactSAPCusFName);
	            	ContactsConstants.showLog("strContactSAPCusLNameValue : "+strContactSAPCusLName);        	
	            	     	
	            	String trancId = String.valueOf(selectedContactId); 
	            	Long now = Long.valueOf(System.currentTimeMillis());
	            	String newLocalId = trancId+now.toString();
	            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, trancId,newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
		        	
	            	ContactsConstants.showLog("Updating data to local databases : "+selectedContactId);
	    			//updating data to local device DB            			
	    			/*contactProCusDbObj.update_data(String.valueOf(selectedContactId), strContactSAPID, 
	    					strContactSAPCusID, strContactSAPCusFName.trim(), 
	    					strContactSAPCusLName.trim(), "true");
	    			contactProCusDbObj.closeDBHelper(); */
	    			//stopProgressDialog();
	    			reloadContact();
	            }  
	            
	            //startNetworkConnectionForContactEditSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
    		}else{
    			ContactsConstants.showLog("initSoapConnection from else");
    			/*String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
             	String[] nameWhereParams = new String[]{String.valueOf(selectedContactId), 
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
             	nameCur.close();*/
             	
             	Uri contactUri1 = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
                Uri dataUri1 = Uri.withAppendedPath(contactUri1, Contacts.Data.CONTENT_DIRECTORY);
                Cursor nameCursor1 = getContentResolver().query(
                		dataUri1,
                		null,
                		Data.MIMETYPE+"=?",
                		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
                		null);
                while (nameCursor1.moveToNext())
                {
                	String  firstName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA2));
                	String  lastName = nameCursor1.getString(nameCursor1.getColumnIndex(Data.DATA3));
                	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
                	strContactSAPCusFName = firstName;
      	 	    	strContactSAPCusLName = firstName;
                }
                nameCursor1.close();
                
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
                lastId = selectedContactId;
                
                internetAccess = SapGenConstants.checkConnectivityAvailable(ContactMain.this);
	            if(internetAccess){
	                respType = ContactsConstants.RESP_TYPE_ADD_SYNC_2_SAP;
		            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, String.valueOf(lastId), request);
	            }
	            else{
	            	ContactsConstants.showLog("No internet connection!");
	            	if(contactProCusDbObj == null){
	        			contactProCusDbObj = new ContactProSAPCPersistent(this);
	        		}
	        		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
	        		if(contactProCusDbObj != null)
	    				contactProCusDbObj.closeDBHelper();
	        		strContactSAPID = ContactsConstants.CONTACTSAPID;
	        		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;            	     	
	        		if(strContactSAPID.length() != 0){    			
	        			// Get value for Givenname and middlename
	            		/*Cursor nameCur1 = cr.query(ContactsContract.Data.CONTENT_URI, 
	            						null, 
	            						ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID +" = ?", 
	            						new String[]{String.valueOf(selectedContactId)}, null);
	                    //if (nameCur.moveToFirst()) {
	                    while(nameCur1.moveToNext()) {
	                    	String givenNameValue = nameCur1.getString(
	                    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	                    	String middleNameValue = nameCur1.getString(
	                    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));            	
	            	 	    ContactsConstants.showLog("givenNameValue"+givenNameValue+"  "+middleNameValue);
	            	 	    strContactSAPCusFName = givenNameValue;
	           	 	    	strContactSAPCusLName = middleNameValue;
	           	 	    	
	           	 	    	if(middleNameValue != null){
	           	 	    		break;
	           	 	    	}
	                    } 
	                    nameCur1.close();*/
	                    
	                    Uri contactUri11 = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
	                    Uri dataUri11 = Uri.withAppendedPath(contactUri11, Contacts.Data.CONTENT_DIRECTORY);
	                    Cursor nameCursor11 = getContentResolver().query(
	                    		dataUri11,
	                    		null,
	                    		Data.MIMETYPE+"=?",
	                    		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
	                    		null);
	                    while (nameCursor11.moveToNext())
	                    {
	                    	String  firstName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA2));
	                    	String  lastName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA3));
	                    	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
	                    	strContactSAPCusFName = firstName;
	           	 	    	strContactSAPCusLName = lastName;
	                    }
	                    nameCursor11.close();
	                    
	                    if(strContactSAPCusFName == null || strContactSAPCusFName.length() == 0){
	                    	strContactSAPCusFName = "";
	            		}
	                    if(strContactSAPCusLName == null || strContactSAPCusLName.length() == 0){
	                    	strContactSAPCusLName = "";
	            		}	
	                    strContactSAPCusFNameValue = strContactSAPCusFName;
	                    strContactSAPCusLNameValue = strContactSAPCusLName;
	        		}else{
	        			ContactsConstants.showLog("initSoapConnection from else");
	        			/*String nameWhere_else = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	                 	String[] nameWhereParams_else = new String[]{String.valueOf(selectedContactId), 
	                 		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}; 
	                 	Cursor nameCur1 = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
	                                null, nameWhere_else, nameWhereParams_else, null);
	                 	while(nameCur1.moveToNext()) { 
	                 		String givenNameValue = nameCur1.getString(
	            	    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	            	    	String middleNameValue = nameCur1.getString(
	            	    			nameCur1.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));            	
	            	 	    ContactsConstants.showLog("givenNameValue from else"+givenNameValue+"  "+middleNameValue);
	            	 	    strContactSAPCusFName = givenNameValue;
	          	 	    	strContactSAPCusLName = middleNameValue;
	                 	} 
	                 	nameCur1.close();*/
	                 	
	                 	Uri contactUri11 = ContentUris.withAppendedId(Contacts.CONTENT_URI, selectedContactId);
	                    Uri dataUri11 = Uri.withAppendedPath(contactUri11, Contacts.Data.CONTENT_DIRECTORY);
	                    Cursor nameCursor11 = getContentResolver().query(
	                    		dataUri11,
	                    		null,
	                    		Data.MIMETYPE+"=?",
	                    		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
	                    		null);
	                    while (nameCursor11.moveToNext())
	                    {
	                    	String  firstName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA2));
	                    	String  lastName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA3));
	                    	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
	                    	strContactSAPCusFName = firstName;
	          	 	    	strContactSAPCusLName = lastName;
	                    }
	                    nameCursor11.close();
	                 	
	                 	strNewContactSAPCusFName = strContactSAPCusFName;
	                 	strNewContactSAPCusLName = strContactSAPCusLName;
	                 	if(strNewContactSAPCusFName == null || strNewContactSAPCusFName.length() == 0){
	                 		strNewContactSAPCusFName = "";
	            		}
	                 	if(strNewContactSAPCusLName == null || strNewContactSAPCusLName.length() == 0){
	                 		strNewContactSAPCusLName = "";
	            		}                		
	                 	strContactSAPCusFNameValue = strNewContactSAPCusLName;
	                    strContactSAPCusLNameValue = strNewContactSAPCusLName;
	        		}
	            	if(strContactSAPCusFNameValue == null || strContactSAPCusFNameValue.length() == 0){
	            		strContactSAPCusFNameValue = "";
	            	}
	            	if(strContactSAPCusLNameValue == null || strContactSAPCusLNameValue.length() == 0){
	            		strContactSAPCusLNameValue = "";
	            	}
	            	contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
	            	if(contactProCusDbObj != null)
	    				contactProCusDbObj.closeDBHelper();
	        		strContactSAPID = ContactsConstants.CONTACTSAPID;
	        		strContactSAPCusID = ContactsConstants.ONTACTSAPCUSID;
	        		
	            	ContactsConstants.showLog("strContactSAPIDValue : "+strContactSAPID);
	            	ContactsConstants.showLog("strContactSAPCusIDValue : "+strContactSAPCusID);
	            	ContactsConstants.showLog("strContactSAPCusFNameValue : "+strContactSAPCusFName);
	            	ContactsConstants.showLog("strContactSAPCusLNameValue : "+strContactSAPCusLName);        	
	            	     	
	            	String trancId = String.valueOf(selectedContactId); 
	            	Long now = Long.valueOf(System.currentTimeMillis());
	            	String newLocalId = trancId+now.toString();
	            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, trancId,newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
		        	
	            	ContactsConstants.showLog("Updating data to local databases : "+selectedContactId);
	    			//updating data to local device DB            			
	    			/*contactProCusDbObj.update_data(String.valueOf(selectedContactId), strContactSAPID, 
	    					strContactSAPCusID, strContactSAPCusFName.trim(), 
	    					strContactSAPCusLName.trim(), "true");
	    			contactProCusDbObj.closeDBHelper(); */
	    			//stopProgressDialog();
	    			reloadContact();
	            }	            
                //startNetworkConnectionForAddContactSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
    		}
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForContactEditSynToSAP for getting contacts from sap:"+asd.toString());
        }
    }//fn initSoapConnectionForContactEditSynToSAP
	
    public void updateReportsConfirmResponseForEditSynToSAP(SoapObject soap){
    	boolean errorflag = false, resMsgErr = false;
        try{ 
        	if(soap != null){
        		ContactsConstants.soapResponse(this, soap, false);
            	taskErrorMsgStr = ContactsConstants.SOAP_RESP_MSG;
            	taskErrorType = ContactsConstants.SOAP_ERR_TYPE;
            	ContactsConstants.showLog("On soap response : "+soap.toString());
            	String soapMsg = soap.toString();
            	resMsgErr = ContactsConstants.getSoapResponseSucc_Err(soapMsg);   
            	ContactsConstants.showLog("resMsgErr : "+resMsgErr);
            	if(!resMsgErr){
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
		                            ContactsConstants.showLog("Inside J == 0 ");
		                            ContactsConstants.showLog("********************************");
	                            	/*ContactsConstants.showLog("* Updation is not there! *"+ selectedContactId);
	                            	ContactsConstants.showLog("********************************");*/
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
        	}
        }
        catch(Exception sff){
        	ContactsConstants.showErrorLog("Error in updateReportsConfirmResponseForEditSynToSAP:"+sff.toString());
        	errorflag = true;
        } 
        finally{
        	if(!errorflag){
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
				if(contactProCusDbObj != null)
					contactProCusDbObj.closeDBHelper();
				
				ContactsConstants.showLog("strContactSAPIDValue : "+strContactSAPIDValue.trim());
				ContactsConstants.showLog("strContactSAPCusIDValue : "+strContactSAPCusIDValue.trim());
				ContactsConstants.showLog("strContactSAPCusFNameValue : "+strContactSAPCusFNameValue.trim());
				ContactsConstants.showLog("strContactSAPCusLNameValue : "+strContactSAPCusLNameValue.trim());
        	}else{
        		boolean apiExists = false;
        		if(errorDbObj == null)
        			errorDbObj = new ContactProErrorMessagePersistent(this.getApplicationContext());
        		if(errorDbObj != null){
        			apiExists = errorDbObj.checkTrancIdApiExists(String.valueOf(selectedContactId), ContactsConstants.CONTACT_MAINTAIN_API);
			    	String tracId = String.valueOf(selectedContactId);
			    	String apiName = ContactsConstants.CONTACT_MAINTAIN_API;
			    	String errType = taskErrorType.trim();
			    	String errorDesc = taskErrorMsgStr.trim();
			    	if(!apiExists){
			    		errorDbObj.insertErrorMsgDetails(tracId, errType, errorDesc, apiName);
			    	} 
			    	else{
			    		errorDbObj.updateValue(tracId, errType, errorDesc, apiName);
			    	}	            		
        		}
        		if(errorDbObj != null)
        			errorDbObj.closeDBHelper();        		
        	}
            reloadContact();
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

    		strAddContactSAPCusEmailHome = strEmailHome;
    		strAddContactSAPCusEmailWork = strEmailWork;
    		strAddContactSAPCusEmailOther = strEmailOther;
    	 	strNewContactStreetName = strStreet.toString().trim();
    	    strNewContactCityName = strCity.toString().trim();
    	    strNewContactStateName = strRegion.toString().trim();
            
            /*String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
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
         	nameCur.close();*/
         	
         	Uri contactUri11 = ContentUris.withAppendedId(Contacts.CONTENT_URI, lastId);
            Uri dataUri11 = Uri.withAppendedPath(contactUri11, Contacts.Data.CONTENT_DIRECTORY);
            Cursor nameCursor11 = getContentResolver().query(
            		dataUri11,
            		null,
            		Data.MIMETYPE+"=?",
            		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
            		null);
            while (nameCursor11.moveToNext())
            {
            	String  firstName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA2));
            	String  lastName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA3));
            	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
            	strContactSAPCusFName = firstName;
            	strContactSAPCusLName = lastName;
            }
            nameCursor11.close();
         	
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
            
            internetAccess = SapGenConstants.checkConnectivityAvailable(ContactMain.this);
            if(internetAccess){
	            respType = ContactsConstants.RESP_TYPE_ADD_SYNC_2_SAP;
	            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, String.valueOf(lastId), request);
            }else{
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = String.valueOf(lastId)+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, String.valueOf(lastId),newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, request, now);
	        	addDataToLocalDB(lastId);
	        	reloadContact();
            }
            //startNetworkConnectionForAddContactSynToSAP(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	ContactsConstants.showErrorLog("Error in initSoapConnectionForAddContactSynToSAP:"+asd.toString());
        }
    }//fn initSoapConnectionForAddContactSynToSAP
        
    public void updateReportsConfirmResponseForAddContactSynToSAP(SoapObject soap){	
    	boolean listDisplay = false, errorflag = false, resMsgErr = false;
        try{ 
        	if(soap != null){
        		ContactsConstants.soapResponse(this, soap, false);
            	taskErrorMsgStr = ContactsConstants.SOAP_RESP_MSG;
            	taskErrorType = ContactsConstants.SOAP_ERR_TYPE;
            	ContactsConstants.showLog("On soap response : "+soap.toString());
            	String soapMsg = soap.toString();
            	resMsgErr = ContactsConstants.getSoapResponseSucc_Err(soapMsg);   
            	ContactsConstants.showLog("resMsgErr : "+resMsgErr);
            	//if(!resMsgErr){
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
		                    			//inserting data to local device DB
	                    				contactProCusDbObj.insertContactDetails(String.valueOf(lastId), categoryKey.getParnr().toString().trim(), 
	                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
	                    						strNewContactSAPCusLName.toString().trim());
	                    				if(contactProCusDbObj != null)
	                    					contactProCusDbObj.closeDBHelper();	                    				                   
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
	        	/*}else{
	        		errorflag = true;
	        		ContactsConstants.showErrorDialog(this, taskErrorMsgStr);
	        	}*/
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
	        		if(contLocal){
	        			try {	        				
	        				if(appCall.equals(ContactsConstants.APPL_NAME_CALL_INTER_VIEW))
	        					interactionsviewcalling();
	        				else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_ACTIVITY_VIEW))
	        					activityviewcalling();
	        				else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_ACTIVITY_CRE))
	        					activitycrcalling();
	        				else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_SO_CRE))
	        					SOcrcalling();
	        				else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_SO_VIEW))
	        					SOviewcalling();
	        				else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_CUST_VIEW))
	        					cusviewcalling();
	            		} 
	            		catch (Exception e) {
	            			ContactsConstants.showErrorLog(e.getMessage());
	            		}	
	        		}
	        		ContactsConstants.showLog("cusList size in updateReportsConfirmResponseForAddContactSynToSAP: "+cusList.size());
	                reloadContact();
	        	}
        	}			
        }
    }//End of updateReportsConfirmResponseForAddContactSynToSAP fn
    
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
         	
            /*String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
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
         	nameCur.close();*/
         	
         	Uri contactUri11 = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
            Uri dataUri11 = Uri.withAppendedPath(contactUri11, Contacts.Data.CONTENT_DIRECTORY);
            Cursor nameCursor11 = getContentResolver().query(
            		dataUri11,
            		null,
            		Data.MIMETYPE+"=?",
            		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
            		null);
            while (nameCursor11.moveToNext())
            {
            	String  firstName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA2));
            	String  lastName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA3));
            	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
            	strContactSAPCusFName = firstName;
            	strContactSAPCusLName = lastName;
            }
            nameCursor11.close();
            
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
                       
            internetAccess = SapGenConstants.checkConnectivityAvailable(ContactMain.this);
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
                    				//sleep(2000);
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
	                    			//inserting data to local device DB
                    				contactProCusDbObj.insertContactDetails(String.valueOf(contactId), categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strNewContactSAPCusFName.toString().trim(), 
                    						strNewContactSAPCusLName.toString().trim());
                    				if(contactProCusDbObj != null)
                    					contactProCusDbObj.closeDBHelper();                    				
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
            
            if(appCall.equals(ContactsConstants.APPL_NAME_CALL_INTER_VIEW))
				interactionsviewcalling();
			else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_ACTIVITY_CRE))
				activitycrcalling();
			else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_SO_CRE))
				SOcrcalling();
			else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_SO_VIEW))
				SOviewcalling();
			else if(appCall.equals(ContactsConstants.APPL_NAME_CALL_CUST_VIEW))
				cusviewcalling();
            
            reloadContact();
        }
    }//End of updateReportsConfirmResponseForAddContactAlrExitCusSynToSAP fn

    public class MyContactListAdapterForTablet extends BaseAdapter {  
    	private LayoutInflater mInflater;
    	String addressStr = "";  
    	String strOrgName = "", strOrgTitle = "", strOrgType = "", 
	    		strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "", 
	    		strEmailHome = "", strEmailWork = "", strEmailOther = "",
	    		strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "";
    	StartAddTask addTask = null; 
    	StartEmailTask emailTask = null;
    	StartPhTask phTask = null;
    	StartOrgTask orgTask = null;   
    	String[] Emails = null, PhNos = null, OrgDetails = null, AddressDetails = null;
    	String name = "", id = "", clickingId = "";
		
    	
        public MyContactListAdapterForTablet(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        public int getCount() {
        	try {
				if(contactVect != null)
					return contactVect.size();
			}
        	catch (Exception e) {
        		ContactsConstants.showErrorLog(e.getMessage());
			}
        	return 0;
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            
            /*if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contactmain_list, null);
                holder = new ViewHolder();
                holder.name_orgname = (TextView) convertView.findViewById(R.id.name);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.email = (TextView) convertView.findViewById(R.id.email);
                holder.ph = (TextView) convertView.findViewById(R.id.ph);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }        */     
            
            convertView = mInflater.inflate(R.layout.contactmain_list, null);
            holder = new ViewHolder();
            holder.name_orgname = (TextView) convertView.findViewById(R.id.name);
            holder.profimg = (ImageView) convertView.findViewById(R.id.profimg);            
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.email1 = (TextView) convertView.findViewById(R.id.email1);
            holder.email2 = (TextView) convertView.findViewById(R.id.email2);
            holder.email3 = (TextView) convertView.findViewById(R.id.email3);
            holder.ph1 = (TextView) convertView.findViewById(R.id.ph1);
            holder.ph2 = (TextView) convertView.findViewById(R.id.ph2);
            holder.ph3 = (TextView) convertView.findViewById(R.id.ph3);
            holder.ph4 = (TextView) convertView.findViewById(R.id.ph4);
            holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);
            holder.ddviewimg = (ImageView) convertView.findViewById(R.id.ddviewimg);
            
            try {
				name = (String)((ContactDetails)contactVect.elementAt(position)).getContactName();
				id = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
				
				emailTask = new StartEmailTask(ContactMain.this);	
				Emails = emailTask.execute(id).get();
				//String[] Emails = (String[])((ContactDetails)contactVect.elementAt(position)).getEmails();
				
				phTask = new StartPhTask(ContactMain.this);	
				PhNos = phTask.execute(id).get();
				//String[] PhNos = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();  
				
				orgTask = new StartOrgTask(ContactMain.this);	
				OrgDetails = orgTask.execute(id).get();
				//String[] OrgDetails = (String[])((ContactDetails)contactVect.elementAt(position)).getOrgDetails();

				addTask = new StartAddTask(ContactMain.this);	
				AddressDetails = addTask.execute(id).get();
				//String[] AddressDetails = (String[])((ContactDetails)contactVect.elementAt(position)).getAddressDetails();    		
				
				if(position%2 == 0)
					holder.llitembg.setBackgroundResource(R.color.item_even_color);
				else
					holder.llitembg.setBackgroundResource(R.color.item_odd_color);
				
				if(OrgDetails != null && OrgDetails.length > 0){
					strOrgName = OrgDetails[0];
					if(strOrgName == null || strOrgName.length() == 0){
						strOrgName = "";
					}
				}
				else{
					strOrgName = "";
				}
				
				Bitmap bitmap = loadContactPhoto(getContentResolver(), id);
				if(bitmap != null){
					holder.profimg.setImageBitmap(bitmap);
				}else{
					holder.profimg.setImageResource(R.drawable.profimg);
				} 
				
		    	String name_orgname = "";
				//int namefind = name.indexOf("                    ");
				int namefind = name.indexOf(",");
				if(namefind != -1){
				    String name_append = name.substring(0,namefind);
				    name_orgname = name_append;
				}
				else{
					String[] tokens = name.split(" ");
					if(tokens.length >= 3){
						String name_append = tokens[0]+" "+tokens[1];
						name_orgname = name_append;
					}
					else{
						 name_orgname = name;
					}            	
				}
				if(strOrgName.length() > 0){
					name_orgname += "\n"+strOrgName+"";
				}
				holder.name_orgname.setText(name_orgname);            
				
				addressStr = "";               
				if(AddressDetails != null && AddressDetails.length > 0){
					strStreet = AddressDetails[0];
					strCity = AddressDetails[1];
					strRegion = AddressDetails[2];
					if(strStreet == null || strStreet.length() == 0){
						strStreet = "";
					}
					if(strCity == null || strCity.length() == 0){
						strCity = "";
					}
					if(strRegion == null || strRegion.length() == 0){
						strRegion = "";
					}
					if(strStreet.length() > 0){
						addressStr += strStreet;
					}
					
					if(addressStr.length() > 0){
						if(strCity.length() > 0){
							addressStr += "\n"+strCity;
							if(strRegion.length() > 0){
					    		addressStr += ", "+strRegion;
					        }
					    }else{
					    	if(strRegion.length() > 0){
					    		addressStr += "\n"+strRegion;
					        }
					    }
					}
					else{
						if(strCity.length() > 0){
							addressStr += strCity;if(strRegion.length() > 0){
					    		addressStr += ", "+strRegion;
					        }
					    }else{
					    	if(strRegion.length() > 0){
					    		addressStr += strRegion;
					        }
					    }
					}     
				}
				else{
					addressStr = "";
				}
         
				holder.address.setText(addressStr);				
				
				if(addressStr != null && addressStr.length() > 0)
					holder.ddviewimg.setVisibility(View.VISIBLE);
				else
					holder.ddviewimg.setVisibility(View.GONE);

				holder.ddviewimg.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	String address = "";
						// Get value for Address Details
		            	//String[] AddressDetailsMap = (String[])((ContactDetails)contactVect.elementAt(position)).getAddressDetails();         	
						try {
							StartAddTask addTask = new StartAddTask(ContactMain.this);
							String[] AddressDetailsMap = addTask.execute(clickingId).get();
							strStreet = AddressDetailsMap[0];
							strCity = AddressDetailsMap[1];
							strRegion = AddressDetailsMap[2];
							strPostalCode = AddressDetailsMap[3];
							strCountry = AddressDetailsMap[4];
							
							if(strStreet.toString().length() > 0){
								address += strStreet.toString().trim();
							}
							if(strCity.toString().length() > 0){
								address += ","+strCity.toString().trim();
							}
							if(strRegion.toString().length() > 0){
								address += ","+strRegion.toString().trim();
							}                	
							if(strCountry.toString().length() > 0){
								address += ","+strCountry.toString().trim();
							}      	                	
							if(strPostalCode.toString().length() > 0){
								address += ","+strPostalCode.toString().trim();
							}
							ContactsConstants.showLog("address  :"+address);
							gotoDrivingDirection(address);
						} catch (InterruptedException eAdd) {
			        		ContactsConstants.showErrorLog("Clicking in Address: "+eAdd.getMessage());
						} catch (ExecutionException eAddex) {
			        		ContactsConstants.showErrorLog("Clicking in Address: "+eAddex.getMessage());
						}
		            } 
		        }); 
				
				if(Emails != null && Emails.length > 0){
					strEmailHome = Emails[0];
					strEmailWork = Emails[1];
					strEmailOther = Emails[2];
					if(strEmailHome == null || strEmailHome.length() == 0){
						strEmailHome = "";
					}
					if(strEmailWork == null || strEmailWork.length() == 0){
						strEmailWork = "";
					}
					if(strEmailOther == null || strEmailOther.length() == 0){
						strEmailOther = "";
					}
				}
				holder.email1.setText(strEmailHome);
				holder.email2.setText(strEmailWork);
				holder.email3.setText(strEmailOther);

				holder.email1.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	//String[] Emails = (String[])((ContactDetails)contactVect.elementAt(position)).getEmails();
						try {
							StartEmailTask emailTask = new StartEmailTask(ContactMain.this);
							String[] Emails = emailTask.execute(clickingId).get();
							ContactsConstants.showLog("Emails1 : "+Emails[0]);
							String em = Emails[0];
							if(em == null || em.length() == 0){
								em = "";
							}else{
								sendEmail(em);
							}
						} catch (InterruptedException eEmail) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmail.getMessage());
						} catch (ExecutionException eEmailExp) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmailExp.getMessage());
						}
		            } 
		        }); 
				holder.email2.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	/*String[] Emails = (String[])((ContactDetails)contactVect.elementAt(position)).getEmails();
		            	ContactsConstants.showLog("Emails2 : "+Emails[1]);
		            	String em = Emails[1];
		            	if(em == null || em.length() == 0){
		            		em = "";
						}else{
							sendEmail(em);
						}*/
		            	try {
							StartEmailTask emailTask = new StartEmailTask(ContactMain.this);
							String[] Emails = emailTask.execute(clickingId).get();
							ContactsConstants.showLog("Emails1 : "+Emails[1]);
							String em = Emails[1];
							if(em == null || em.length() == 0){
								em = "";
							}else{
								sendEmail(em);
							}
						} catch (InterruptedException eEmail) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmail.getMessage());
						} catch (ExecutionException eEmailExp) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmailExp.getMessage());
						}
		            } 
		        }); 
				holder.email3.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	/*String[] Emails = (String[])((ContactDetails)contactVect.elementAt(position)).getEmails();
		            	ContactsConstants.showLog("Emails3 : "+Emails[2]);
		            	String em = Emails[2];
		            	if(em == null || em.length() == 0){
		            		em = "";
						}else{
							sendEmail(em);
						}*/
		            	try {
							StartEmailTask emailTask = new StartEmailTask(ContactMain.this);
							String[] Emails = emailTask.execute(clickingId).get();
							ContactsConstants.showLog("Emails1 : "+Emails[2]);
							String em = Emails[2];
							if(em == null || em.length() == 0){
								em = "";
							}else{
								sendEmail(em);
							}
						} catch (InterruptedException eEmail) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmail.getMessage());
						} catch (ExecutionException eEmailExp) {
			        		ContactsConstants.showErrorLog("Clicking in Email: "+eEmailExp.getMessage());
						}
		            } 
		        });  
				
				if(strEmailHome.length() > 0)
					holder.email1.setVisibility(View.VISIBLE);
				else
					holder.email1.setVisibility(View.GONE);
				
				if(strEmailWork.length() > 0)
					holder.email2.setVisibility(View.VISIBLE);
				else
					holder.email2.setVisibility(View.GONE);
				
				if(strEmailOther.length() > 0)
					holder.email3.setVisibility(View.VISIBLE);
				else
					holder.email3.setVisibility(View.GONE);
				
				if(PhNos != null && PhNos.length > 0){
					strPhoneHome = PhNos[0];
					strPhoneMob = PhNos[1];
					strPhoneWork = PhNos[2];
					strPhoneOther = PhNos[3];
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
				}
				
				holder.ph1.setText(strPhoneHome);				
				holder.ph2.setText(strPhoneMob);
				holder.ph3.setText(strPhoneWork);
				holder.ph4.setText(strPhoneOther);

				holder.ph1.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	//String[] phs = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();
		            	try{
			            	StartPhTask phTask = new StartPhTask(ContactMain.this);
							String[] PhNos = phTask.execute(clickingId).get();						
			            	ContactsConstants.showLog("phs1 : "+PhNos[0]);
			            	String ph = PhNos[0];
			            	if(ph == null || ph.length() == 0){
			            		ph = "";
							}else{
								callPh(ph);
							}	
						} catch (InterruptedException ePh) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePh.getMessage());
						} catch (ExecutionException ePhExp) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePhExp.getMessage());
						}
		            } 
		        }); 
				holder.ph2.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	//String[] phs = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();
		            	try{
			            	StartPhTask phTask = new StartPhTask(ContactMain.this);
							String[] PhNos = phTask.execute(clickingId).get();					
			            	ContactsConstants.showLog("phs2 : "+PhNos[1]);
			            	String ph = PhNos[1];
			            	if(ph == null || ph.length() == 0){
			            		ph = "";
							}else{
								callPh(ph);
							}
						} catch (InterruptedException ePh) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePh.getMessage());
						} catch (ExecutionException ePhExp) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePhExp.getMessage());
						}
		            } 
		        }); 
				holder.ph3.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	//String[] phs = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();
			            try{
			            	StartPhTask phTask = new StartPhTask(ContactMain.this);
							String[] PhNos = phTask.execute(clickingId).get();	
			            	ContactsConstants.showLog("phs3 : "+PhNos[2]);
			            	String ph = PhNos[2];
			            	if(ph == null || ph.length() == 0){
			            		ph = "";
							}else{
								callPh(ph);
							}
						} catch (InterruptedException ePh) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePh.getMessage());
						} catch (ExecutionException ePhExp) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePhExp.getMessage());
						}
		            } 
		        });  
				holder.ph4.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	clickingId = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
		            	//String[] phs = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();
		            	try{
			            	StartPhTask phTask = new StartPhTask(ContactMain.this);
							String[] PhNos = phTask.execute(clickingId).get();
			            	ContactsConstants.showLog("phs4 : "+PhNos[3]);
			            	String ph = PhNos[3];
			            	if(ph == null || ph.length() == 0){
			            		ph = "";
							}else{
								callPh(ph);
							}
						} catch (InterruptedException ePh) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePh.getMessage());
						} catch (ExecutionException ePhExp) {
			        		ContactsConstants.showErrorLog("Clicking in ph: "+ePhExp.getMessage());
						}
		            } 
		        });  
								
				if(strPhoneHome.length() > 0)
					holder.ph1.setVisibility(View.VISIBLE);
				else
					holder.ph1.setVisibility(View.GONE);
				
				if(strPhoneMob.length() > 0)
					holder.ph2.setVisibility(View.VISIBLE);
				else
					holder.ph2.setVisibility(View.GONE);
				
				if(strPhoneWork.length() > 0)
					holder.ph3.setVisibility(View.VISIBLE);
				else
					holder.ph3.setVisibility(View.GONE);
				
				if(strPhoneOther.length() > 0)
					holder.ph4.setVisibility(View.VISIBLE);
				else
					holder.ph4.setVisibility(View.GONE);
				
			} catch (Exception qw) {
				ContactsConstants.showErrorLog("Error in MyContactListAdapterForTablet : "+qw.toString());
			}
            
            return convertView;
        }
        class ViewHolder {
            TextView name_orgname;
            ImageView profimg;
            TextView address;
            TextView email1;
            TextView email2;
            TextView email3;
            TextView ph1;
            TextView ph2;
            TextView ph3;
            TextView ph4;
            LinearLayout llitembg;
            ImageView ddviewimg;
        }    
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }        
        
        public void filter(String charText) {
        	String orgStrVal = "";
            charText = charText.toLowerCase();
            contactVect.clear();
            if (charText.length() == 0) {
            	if(allContactVect != null)
            		contactVect = (Vector)allContactVect.clone();
            } else {
                for (Object obj : allContactVect) {
                	String strValue = (String)((ContactDetails)obj).getContactName();
                	String[] strOrgValue = ((ContactDetails)obj).getOrgDetails();
                	if(strOrgValue != null && strOrgValue.length > 0){
                		orgStrVal = strOrgValue[0];
    					if(orgStrVal == null || orgStrVal.length() == 0){
    						orgStrVal = "";
    					}
                	}
                    if (strValue.toLowerCase().contains(charText) || orgStrVal.toLowerCase().contains(charText)) {
                    	contactVect.add(obj);
                    }
                }
            }
            notifyDataSetChanged();
        }
        
    }//End of MyContactListAdapterForTablet
    
    private void sendEmail(String email){
    	try {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
			emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { email}); 
			emailIntent.setType("text/plain"); 
			startActivity(emailIntent);
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in sendEmail:"+e.getMessage());
		}    	
    }//fn sendEmail	
    
    private void callPh(String telno){
    	try {
    		startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+telno)));
		} catch (Exception e) {
			ContactsConstants.showErrorLog("Error in callPh:"+e.getMessage());
		}    	
    }//fn callPh

    private class MyContactListAdapterForPhone extends BaseAdapter {
        private LayoutInflater mInflater;    
        String strOrgName = "", strStreet = "", strCity = "", strRegion = "";
        
        public MyContactListAdapterForPhone(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);   
        }
       
        public int getCount() {
        	try {
				if(contactVect != null)
					return contactVect.size();
			}
        	catch (Exception e) {
        		ContactsConstants.showErrorLog(e.getMessage());
			}
        	return 0;
        }
        
        public Object getItem(int position) {
            return position;
        }
        
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.

            class ViewHolder {
            	TextView name;
            	TextView orgname;
                TextView address;    
                ImageView profimg;    
                LinearLayout llitembg;
                ImageView arrowimg;
                //ImageView errstatus;
                /*TextView email;
                TextView ph;*/
            }
            ViewHolder holder;
                       
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contactmain_list1, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.orgname = (TextView) convertView.findViewById(R.id.orgname); 
                holder.address = (TextView) convertView.findViewById(R.id.address);    
                holder.profimg = (ImageView) convertView.findViewById(R.id.profimg);         
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);    
                holder.arrowimg = (ImageView) convertView.findViewById(R.id.arrowimg);  
                
                //holder.errstatus = (ImageView) convertView.findViewById(R.id.errstatus);             
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            try {
				String name = (String)((ContactDetails)contactVect.elementAt(position)).getContactName();
				String id = (String)((ContactDetails)contactVect.elementAt(position)).getContactId();
				//String[] Emails = (String[])((ContactDetails)contactVect.elementAt(position)).getEmails();
				//String[] PhNos = (String[])((ContactDetails)contactVect.elementAt(position)).getPhNos();
				String[] OrgDetails = (String[])((ContactDetails)contactVect.elementAt(position)).getOrgDetails();
				String[] AddressDetails = (String[])((ContactDetails)contactVect.elementAt(position)).getAddressDetails();
				
				if(position%2 == 0)
					holder.llitembg.setBackgroundResource(R.color.item_even_color);
				else
					holder.llitembg.setBackgroundResource(R.color.item_odd_color);
				
				Bitmap bitmap = loadContactPhoto(getContentResolver(), id);
				if(bitmap != null){
					holder.profimg.setImageBitmap(bitmap);
				}else{
					holder.profimg.setImageResource(R.drawable.profimg);
				}            
				
				if(OrgDetails != null && OrgDetails.length > 0){
					strOrgName = OrgDetails[0];
					if(strOrgName == null || strOrgName.length() == 0){
				 		strOrgName = "";
					}
				}else{
					strOrgName = "";
				}            
				
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
				holder.name.setText(cname);
         
				if(strOrgName.length() > 0){
					holder.orgname.setVisibility(View.VISIBLE);
					holder.orgname.setText(strOrgName);
				}else{
					holder.orgname.setVisibility(View.GONE);
				}            	   
				
				String addressStr = "";
				if(AddressDetails != null && AddressDetails.length > 0){
				    strStreet = AddressDetails[0];
				    strCity = AddressDetails[1];
				    strRegion = AddressDetails[2];
				    if(strStreet == null || strStreet.length() == 0){
				 		strStreet = "";
					}
				 	if(strCity == null || strCity.length() == 0){
				 		strCity = "";
					}
				 	if(strRegion == null || strRegion.length() == 0){
				 		strRegion = "";
					}
				    if(strStreet.length() > 0){
				    	addressStr += strStreet;
				    }
				    
				    if(addressStr.length() > 0){
				    	if(strCity.length() > 0){
				    		addressStr += ", "+strCity;
				    		if(strRegion.length() > 0){
				        		addressStr += ", "+strRegion;
				            }
				        }else{
				        	if(strRegion.length() > 0){
				        		addressStr += ", "+strRegion;
				            }
				        }
				    }
				    else{
				    	if(strCity.length() > 0){
				    		addressStr += strCity;
				    		if(strRegion.length() > 0){
				        		addressStr += ", "+strRegion;
				            }
				        }else{
				        	if(strRegion.length() > 0){
				        		addressStr += strRegion;
				            }
				        }
				    }      
				}else{
					addressStr = "";
				}
         
				if(addressStr.length() > 0){
					holder.address.setVisibility(View.VISIBLE);
					holder.address.setText(addressStr); 
				}else{
					holder.address.setVisibility(View.GONE);
				}       		  		          
				
				holder.arrowimg.setOnClickListener(new OnClickListener() { 					 
		            @Override 
		            public void onClick(View v) { 
		                // TODO Auto-generated method stub 		 
		            	ContactsConstants.showLog("position : "+position);
		            	ContactsConstants.showLog("getContactId : "+(String)((ContactDetails)contactVect.elementAt(position)).getContactId());
		            	String address = "";
						// Get value for Address Details
		            	final int contact_pos = Integer.parseInt(((ContactDetails)contactVect.elementAt(position)).getContactId());		
		    			selectedContactId = contact_pos;
		    			contactView();
		            } 
		        }); 
				
				/*boolean errornotify = (boolean)((ContactDetails)allContactVect.elementAt(position)).getErrorNotify();
				if(errornotify){
					holder.errstatus.setVisibility(View.VISIBLE);
				}
				else{
					holder.errstatus.setVisibility(View.GONE);
				}*/
			} catch (Exception qw) {
				ContactsConstants.showErrorLog("Error in MyContactListAdapterForPhone : "+qw.toString());
			}
            
			return convertView;
        }          
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        } 
        
        public void filter(String charText) {
            charText = charText.toLowerCase();
            contactVect.clear();
            if (charText.length() == 0) {
            	if(allContactVect != null)
            		contactVect = (Vector)allContactVect.clone();
            } else {
                for (Object obj : allContactVect) {
                	String strValue = (String)((ContactDetails)obj).getContactName();
                    if (strValue.toLowerCase().contains(charText)) {
                    	contactVect.add(obj);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }//End of MyContactListAdapterForPhone
    
    private void contactView(){
    	try {
  			Thread t = new Thread() 
  			{
  	            public void run() 
  				{
          			try{
          				gettingContactDetails(selectedContactId);
          			} catch (Exception e) {
          				ContactsConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
          			}
          			contactsDataGetting_Handler.post(showContactViewMode);
  				}
  			};
  	        t.start();		
  		}catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in contactviewimglistener:"+ce.toString());
		}
    }//fn contactView
  	
  	final Runnable showContactViewMode = new Runnable(){
  	    public void run()
  	    {
  	    	try{
  	    		contactViewModeScreen();
  	    	} catch(Exception sfe){
  	    		ContactsConstants.showErrorLog("Error in showDialog:"+sfe.toString());
  	    	}
  	    }	    
      };
    
    private void contactViewModeScreen(){
    	try {
			ContactsConstants.showLog("contacteditimglistener!");
			ContactsConstants.showLog("contact_pos:"+selectedContactId);
			ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);
			ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			if(internetAccess){
			  	if(userCheck){
			  		ContactsConstants.showLog("Data sync from sap!");
			  		viewAction = "view";
			  		if(contactProCusDbObj == null){
						contactProCusDbObj = new ContactProSAPCPersistent(ContactMain.this);
					}
			  		contactProCusDbObj.getContactDetails(String.valueOf(selectedContactId));
			  		if(contactProCusDbObj != null)
						contactProCusDbObj.closeDBHelper();				        
			        ContactsConstants.showLog("Contact Flag Value : "+ContactsConstants.CONTACTFLAGVALUE);
			       			        	
			        if(ContactsConstants.CONTACTFLAGVALUE.equals("true")){
				        ContactsConstants.showLog("Data sync to sap if part !");
			        	initSoapConnectionForContactEditSynToSAP();	
				        reloadContact();
				        ContactsConstants.VIEWUPDATIONCALLFLAG = true;
			        	Intent i = new Intent(Intent.ACTION_VIEW);
			        	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
			        	i.setData(contactUri); 
			        	startActivityForResult(i, INTENT_ACTION_VIEW);
			        }
			        else{
			        	ContactsConstants.showLog("Data sync from sap else part !");
			    		viewAction = "view";
			    		ContactsConstants.VIEWUPDATIONCALLFLAG = true;
			    		initSoapConnectionForContactUpdateCheckFromSAP();
			        }		                		
			  	}
			  	else{
			  		viewAction = "view";
			  		ContactsConstants.VIEWUPDATIONCALLFLAG = true;
			    	ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);			                	 
			    	Intent i = new Intent(Intent.ACTION_VIEW);
			    	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
			    	i.setData(contactUri); 
			    	startActivityForResult(i, INTENT_ACTION_VIEW);
			  	}
			}
			else{
				viewAction = "view";
				ContactsConstants.VIEWUPDATIONCALLFLAG = true;
			  	ContactsConstants.showLog("PContacts means return false / CContacts means return true:"+userCheck);  
			  	Intent i = new Intent(Intent.ACTION_VIEW);
			  	Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, selectedContactId); 
			  	i.setData(contactUri); 
			  	startActivityForResult(i, INTENT_ACTION_VIEW);
			}
		} catch (Exception ce) {
			ContactsConstants.showErrorLog("Error in contactviewimglistener:"+ce.toString());
		}
    }//fn contactView
	
	private final Comparator contactSortComparator =  new Comparator() {
        public int compare(Object o1, Object o2){ 
        	int comp = 0;
            String strObj1 = "0", strObj2="0";
            int intObj1 = 0, intObj2 = 0;
            ContactDetails cpOCObj1, cpOCObj2;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                    cpOCObj1 = (ContactDetails)o1;
                    cpOCObj2 = (ContactDetails)o2;                                                     
                    if(sortIndex == ContactsConstants.CONTACT_SORT_NAME){
                        if(cpOCObj1 != null)
                            strObj1 = cpOCObj1.getContactName().trim();                        
                        if(cpOCObj2 != null)
                            strObj2 = cpOCObj2.getContactName().trim();                        
                        if(sortContactNameFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }             
                }
             }
             catch(Exception qw){
            	 ContactsConstants.showErrorLog("Error in Report Sort Comparator : "+qw.toString());
             }                 
             if (comp != 0) {            
                return comp;
             } 
             else {            
                return 0;
             }
        }
    };
    
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
			            	initSoapConnectionForAddContactAlrExitCusSynToSAP(selectedContactId, catObj.getKunnr(), catObj.getName1());
		            	}
		            } 
		        }); 
		        builder.show(); 
	    	}
	    	else{
	    		catObj = (ContactProContactCreationOpConstraints)sapCusData.get(0);                
            	ContactsConstants.showLog("getKunnr: "+catObj.getKunnr());
            	ContactsConstants.showLog("getName1: "+catObj.getName1());
            	initSoapConnectionForAddContactAlrExitCusSynToSAP(selectedContactId, catObj.getKunnr(), catObj.getName1());
	    	}	        
    	}
        catch(Exception qw){
       	 	ContactsConstants.showErrorLog("Error in createListFile:"+qw.toString());
        }  
    }//End of createListFile fn
    
    //If Internet is not available, so data adding to local databases with empty SAP contatId and customerId
    private void addDataToLocalDB(int localContactId){
    	String strContactSAPCusFName = "", strContactSAPCusLName = "";
		/*String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
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
     	nameCur.close();*/
     	
     	Uri contactUri11 = ContentUris.withAppendedId(Contacts.CONTENT_URI, localContactId);
        Uri dataUri11 = Uri.withAppendedPath(contactUri11, Contacts.Data.CONTENT_DIRECTORY);
        Cursor nameCursor11 = getContentResolver().query(
        		dataUri11,
        		null,
        		Data.MIMETYPE+"=?",
        		new String[]{ StructuredName.CONTENT_ITEM_TYPE },
        		null);
        while (nameCursor11.moveToNext())
        {
        	String  firstName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA2));
        	String  lastName = nameCursor11.getString(nameCursor11.getColumnIndex(Data.DATA3));
        	ContactsConstants.showLog("givenNameValue: "+firstName+"  "+lastName);
        	strContactSAPCusFName = firstName;
        	strContactSAPCusLName = lastName;
        }
        nameCursor11.close();
     	
     	strNewContactSAPCusFName = strContactSAPCusFName;
     	strNewContactSAPCusLName = strContactSAPCusLName;
     	
     	ContactsConstants.showLog("strNewContactSAPCusFName:"+strNewContactSAPCusFName);
     	ContactsConstants.showLog("strNewContactSAPCusLName:"+strNewContactSAPCusLName);
     	ContactsConstants.showLog("Internet Connection is not there!");
     	
     	if(contactProCusDbObj == null){
			contactProCusDbObj = new ContactProSAPCPersistent(this);
		}     	
		//inserting data to local device DB
		contactProCusDbObj.insertContactDetails(String.valueOf(localContactId), "", 
				"", strNewContactSAPCusFName.toString().trim(), 
				strNewContactSAPCusLName.toString().trim());
		if(contactProCusDbObj != null)
			contactProCusDbObj.closeDBHelper();
    }//End of addDataToLocalDB fn
    
    private class MyContentObserver extends ContentObserver { 	
		public MyContentObserver() { 
			super(null); 
		} 
		public MyContentObserver(Handler h) {   super(h);  } 
		
		@Override 
		public boolean deliverSelfNotifications() { 
			return true; 
		}  
		
		@Override 
		public void onChange(boolean selfChange) {	
			super.onChange(selfChange);	
			try{
				//ContactsConstants.showLog("flag_pref2:"+flag_pref2);
				if(flag_pref2){
					ContactsConstants.showLog("Contacts is changed!"); 
					ContactsConstants.showLog("viewAction:"+viewAction); 
					
					/*Cursor contact_cursor = ContactMain.this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			        if (contact_cursor != null) {
				        if (contact_cursor.moveToFirst()) {
				        	String id = contact_cursor.getString(contact_cursor.getColumnIndex(ContactsContract.Contacts._ID));
				        	ContactsConstants.showLog("protocol id: " + id);
				        }
			        }*/
					
					String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
			     	String[] orgWhereParams = new String[]{String.valueOf(selectedContactId), 
			     		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
			     	Cursor contactIdCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
			                    null, orgWhere, orgWhereParams, null);
			     	
			     	ContactsConstants.showLog("selectedContactId:"+selectedContactId);
			     	ContactsConstants.showLog("orgCur size:"+contactIdCur.getCount());
			     	ContactsConstants.showLog("VIEWUPDATIONCALLFLAG:"+ ContactsConstants.VIEWUPDATIONCALLFLAG);
			     	ContactsConstants.showLog("VIEWREFRESHCALLFLAG:"+ContactsConstants.VIEWREFRESHCALLFLAG);
			     	
			     	if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			     		ContactsConstants.showLog("Tablet Updation!");
			     		if(viewAction.equalsIgnoreCase("edit") || viewAction.equalsIgnoreCase("view")){
			     			ContactsConstants.showLog("1");
				     		if(contactIdCur.getCount() > 0){
				     			ContactsConstants.showLog("2");
				     			if(ContactsConstants.VIEWUPDATIONCALLFLAG){
					     			ContactsConstants.showLog("3");
				     				if(!ContactsConstants.VIEWREFRESHCALLFLAG){
						     			ContactsConstants.showLog("4");
				     					ContactsConstants.VIEWUPDATIONCALLFLAG = false;
					     				updationCall(); 
				     				}
				     				else{
						     			ContactsConstants.showLog("5");
				     					ContactsConstants.VIEWREFRESHCALLFLAG = false;
				     				}
				     			}
				     		}else{
				     			if(viewAction.equalsIgnoreCase("add")){
					     			Cursor C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
					    			while (C.moveToNext()) {                   
					    				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
					    				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
					    				/*boolean errornotify = false;
					    				String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
					                 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
					            		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId);
					            		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
					    				allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
					    				contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));*/
					    				lastId = Integer.parseInt(contactId);
					    			}
					    			C.close();
					    			selectedContactId = lastId;
					    			
					    			if(ContactsConstants.VIEWUPDATIONCALLFLAG){
						     			ContactsConstants.showLog("3");
					     				if(!ContactsConstants.VIEWREFRESHCALLFLAG){
							     			ContactsConstants.showLog("4");
					     					ContactsConstants.VIEWUPDATIONCALLFLAG = false;
						     				updationCall(); 
					     				}
					     				else{
							     			ContactsConstants.showLog("5");
					     					ContactsConstants.VIEWREFRESHCALLFLAG = false;
					     				}
					     			}
				     			}
				     		}
						}else if(viewAction.equalsIgnoreCase("add")){
			     			Cursor C = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
			    			while (C.moveToNext()) {                   
			    				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
			    				String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
			    				/*boolean errornotify = false;
			    				String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
			                 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
			            		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId);
			            		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
			    				allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
			    				contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));*/
			    				lastId = Integer.parseInt(contactId);
			    			}
			    			C.close();
			    			selectedContactId = lastId;
			    			
			    			if(ContactsConstants.VIEWUPDATIONCALLFLAG){
				     			ContactsConstants.showLog("3");
			     				if(!ContactsConstants.VIEWREFRESHCALLFLAG){
					     			ContactsConstants.showLog("4");
			     					ContactsConstants.VIEWUPDATIONCALLFLAG = false;
				     				updationCall(); 
			     				}
			     				else{
					     			ContactsConstants.showLog("5");
			     					ContactsConstants.VIEWREFRESHCALLFLAG = false;
			     				}
			     			}
			     		}
					}else{
						if(viewAction.equalsIgnoreCase("view")){
				     		if(contactIdCur.getCount() > 0){
				     			if(ContactsConstants.VIEWUPDATIONCALLFLAG){
				     				if(!ContactsConstants.VIEWREFRESHCALLFLAG){
					     				ContactsConstants.VIEWUPDATIONCALLFLAG = false;
					     				updationCall(); 
				     				}
				     				else{
				     					ContactsConstants.VIEWREFRESHCALLFLAG = false;
				     				}
				     			}
				     		}
						}
					}
			     	contactIdCur.close();
				}
			}
	        catch(Exception qw){
	       	 	ContactsConstants.showErrorLog("Error in onChange:"+qw.toString());
	        } 
		} 
	} 	
    
    private String getOrgName(int contactId){
    	String strOrgName = "";
		// Get value for Organization
    	try{
	        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	     	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
	     		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
	     	Cursor orgCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
	                    null, orgWhere, orgWhereParams, null);
	     	if (orgCur.moveToNext()) { 
	     		strOrgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
	     		ContactsConstants.showLog("(getOrgName) Company Name : "+strOrgName);
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
	
	private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, String contactId, SoapObject request){
    	try{
    		requestContactId = contactId;
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
	    			if(respType == ContactsConstants.RESP_TYPE_GET_EMP_CONT){
	    				if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(ContactMain.this, "", getString(R.string.CONTACTPRO_COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				updateReportsConfirmResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	            			updateData_Handler.post(reloadListView);
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    				//updateReportsConfirmResponse(resultSoap);
	    			}
	    			else if(respType == ContactsConstants.RESP_TYPE_GET_UPDATE_CHECK_FROM_SAP){
	    				updateReportsConfirmResponseForRefresh(resultSoap);
	    			}
	    			else if(respType == ContactsConstants.RESP_TYPE_GET_EDIT_2_SAP){
	    				updateReportsConfirmResponseForEditSynToSAP(resultSoap);
	    			}
	    			else if(respType == ContactsConstants.RESP_TYPE_ADD_SYNC_2_SAP){
	    				updateReportsConfirmResponseForAddContactSynToSAP(resultSoap);
	    			}	
	    		}else{
	    			if(respType != ContactsConstants.RESP_TYPE_GET_EMP_CONT && respType != ContactsConstants.RESP_TYPE_GET_UPDATE_CHECK_FROM_SAP){
	    				addDataToLocalDB(Integer.parseInt(requestContactId));
		    			sendToQueueProcessor();
		    			reloadContact();
	    			}else{
	    				if(respType == ContactsConstants.RESP_TYPE_GET_EMP_CONT){
	    					reloadContact();
	    				}
	    				else if(respType == ContactsConstants.RESP_TYPE_GET_UPDATE_CHECK_FROM_SAP){
	    					updateReportsConfirmResponseForRefresh(null);
	    				}
	    			}
	    		}
	    	} catch(Exception edd){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+edd.toString());
	    	}
	    }	    
    };
    
    private void sendToQueueProcessor(){
    	try {
        	Long now = Long.valueOf(System.currentTimeMillis());
        	String newLocalId = requestContactId+now.toString();
    		//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, requestContactId,newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.CONTACT_MAINTAIN_API, requestSoapObj, now);
		} catch (Exception errg) {
			SapGenConstants.showErrorLog("Error in sendToQueueProcessor : "+errg.toString());
		}
    }//fn sendToQueueProcessor
    
    final Runnable reloadListView = new Runnable(){
	    public void run()
	    {
	    	try{
	    		reloadContact();
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
	
    public static Bitmap loadContactPhoto(ContentResolver cr, String id) { 
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)); 
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri); 
        if (input == null) { 
            return null; 
        } 
        return BitmapFactory.decodeStream(input); 
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.getApplicationContext().getContentResolver().unregisterContentObserver(observer);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}       
}