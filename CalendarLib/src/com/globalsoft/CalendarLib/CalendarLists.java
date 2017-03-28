package com.globalsoft.CalendarLib;

import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.globalsoft.CalendarLib.Contraints.SalesOrdProIpConstraints;
import com.globalsoft.CalendarLib.Contraints.SalesProAppCustomersConstraints;
import com.globalsoft.CalendarLib.Database.AppDBOperations;
import com.globalsoft.CalendarLib.Utils.CalendarConstants;
import com.globalsoft.SapLibSoap.Constraints.SalesProActOutputConstraints;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class CalendarLists extends ListActivity implements TextWatcher {

	private static String appNameStr = "";
	private boolean internetAccess = true;
	private ListView listView; 
	private int dispwidth = 300;
	private static ArrayList appList = new ArrayList();
	private static ArrayList allAppList = new ArrayList();
	private static ArrayList cusList = new ArrayList();
	ArrayList idList = new ArrayList();
	ArrayList galIdList = new ArrayList();
	private int selected_index = -1;
	//private Vector selContactVect = new Vector();
	private EditText searchET;
	private String searchStr = "";
	
	private SoapObject resultSoap = null;
	private ProgressDialog pdialog = null;	
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	
	private static final int MENU_CRE_APP = Menu.FIRST;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.applist); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.SCR_APP_LIST_TITLE));			
			
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			appNameStr = this.getIntent().getStringExtra("app_name");
			if((appNameStr == null) || (appNameStr.equalsIgnoreCase("")))
            	appNameStr = SapGenConstants.APPLN_NAME_STR_MOBILEPRO;
			CalendarConstants.CRTAPP_CALLING_APP_NAME = appNameStr;
			
			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);
			listviewcall();					
			
			searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
			internetAccess = SapGenConstants.checkConnectivityAvailable(CalendarLists.this);
			System.out.println("internetAccess:"+internetAccess);
			dispwidth = SapGenConstants.getDisplayWidth(this);
			if(internetAccess){
				SapGenConstants.showLog("Data from SAP!");	
				initSoapConnection();
			}
			else{
				getLDBActivityList();	
				SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
			}	
		}
		catch (Exception de) {
			SapGenConstants.showLog("Error in oncreate contactMain:"+de.toString());
        }
	}
	
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
			pdialog = ProgressDialog.show(CalendarLists.this, "", getString(R.string.SCR_WAIT_TEXTS),true);
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				searchItemsAction(searchStr);
        			} catch (Exception e) {
        				SapGenConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
        			}
        			ntwrkHandler.post(searchCall);
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
				System.out.println("appList.size():"+appList.size());	
				listviewcall();
		        getListView().invalidateViews();
	    	} catch(Exception sfe){
	    		SapGenConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
    };	
	
	private void searchItemsAction(String match){  
        try{       
            searchStr = match;
            String mattStr = "";
            String strValue = null;
            SalesProActOutputConstraints category = null;
            if((allAppList != null) && (allAppList.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){             
                    appList.clear();
                    for(int i = 0; i < allAppList.size(); i++){ 
                    	strValue = null;
                        mattStr = "";
                        category = null;
    					category = (SalesProActOutputConstraints)allAppList.get(i);
                        String data = "";
	            		if(category.getParnrName().toString().length() > 0){
	                		data += category.getParnrName().toString();
	                	}
	                	if(category.getKunnrName().toString().length() > 0){
	                		data += ", "+category.getKunnrName().toString();
	                	}	                	
	                	strValue = data;
                        if(strValue != null){
                        	if(category != null){
	                            mattStr = strValue.trim().toLowerCase();
	                            match = match.toLowerCase();
	                            if((mattStr.indexOf(match) >= 0)){
	                            	appList.add(category);
	                            }
	                            else{
                                	data = "";
                                	if(category.getObjectId().toString().length() > 0){
                                		data += category.getObjectId().toString();
                                	}  
                                	strValue = data;
                                	mattStr = strValue.trim().toLowerCase();
                                    match = match.toLowerCase();
                                    if((mattStr.indexOf(match) >= 0)){
                                    	appList.add(category);
                                    }
	                            }
                        	}
                        }
                    }//for 
                }
                else{
                    appList.clear();
                    for(int i = 0; i < allAppList.size(); i++){  
                    	category = null;
    					category = (SalesProActOutputConstraints)allAppList.get(i);                        
                        if(category != null){
                        	appList.add(category);
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
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CRE_APP, 0, getResources().getString(R.string.SCR_CRE_APP));
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
			case MENU_CRE_APP: {
				launchCreateAppointmentScreen();
		        break;
			}
	    }
		return super.onOptionsItemSelected(item);
	}
	
	//click listener for listview
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			SapGenConstants.showLog("Selected Item "+arg2);
			selected_index = arg2;
			showEditActScreen(arg2);
		}
	};	
	
	private void showEditActScreen(int index){
		try {
			selected_index = index;
			SalesProActOutputConstraints category = null;
			SalesProAppCustomersConstraints customers = null;
			String objectId = "";
			if(SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			if(appList != null){
				if(appList.size() > index){
					category = null;
					category = (SalesProActOutputConstraints)appList.get(index);
                    if(category != null){
                    	objectId = category.getObjectId().toString().trim();
                    	if(objectId.length() > 0){
                    		for(int i=0; i<cusList.size(); i++){
                    			customers = (SalesProAppCustomersConstraints)cusList.get(i);  
                    			String cusObjId = customers.getObjectId().toString().trim();
                    			if(objectId.equalsIgnoreCase(cusObjId)){
                    				String cusid = customers.getKunnr();
                					String sapid = customers.getParnr();
                					String fname = "", lname ="";
                					String contactName = customers.getKunnrName();
                					int index1 = contactName.indexOf(" ");
                					if(index1 != -1){
                						fname = contactName.substring(0, index1);
                						lname = contactName.substring(index1+1, contactName.length());
                					}else{
                						fname = contactName;
                						lname = "";
                					}
                					String orgname = customers.getParnrName();
                    				ContactSAPDetails obj = new ContactSAPDetails("", "", sapid, cusid, fname, lname, orgname);
                    				if(!SapGenConstants.selContactIdArr.contains(sapid)){
                        				SapGenConstants.selContactVect.addElement(obj);
                        				SapGenConstants.selContactIdArr.add(sapid);
                					}   
                    			}                    			
                    		}
                    	}                    	                    	
                    	sentToEditActScreen(category);
                    }
				}
			}
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog(e.getMessage());
		}
	}//fn showEditActScreen
	
	private void launchCreateActivityScreen(){
		try {
			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			Intent intent = new Intent(this, CreateAppointment.class); 
        	intent.putExtra("editflag", false); 
			startActivityForResult(intent, CalendarConstants.APP_CRE_SCREEN);
		}
		catch (Exception e) {
			SapGenConstants.showErrorLog("Error in launchCreateActivityScreen:"+e.getMessage());
		}
	}//fn launchCreateActivityScreen	
	
	private void launchCreateAppointmentScreen(){
		try {
			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			Intent intent = new Intent(this, CreateAppointment.class); 
        	intent.putExtra("editflag", false); 
			startActivityForResult(intent, CalendarConstants.APP_CRE_SCREEN);
		}
		catch (Exception e) {
			SapGenConstants.showErrorLog("Error in launchCreateAppointmentScreen:"+e.getMessage());
		}
	}//fn launchCreateAppointmentScreen
		
	private void sentToEditActScreen(SalesProActOutputConstraints category){
		try {
        	Intent intent = new Intent(this, CreateAppointment.class); 
        	intent.putExtra("editflag", true);   
        	intent.putExtra("actobj", category);    	
        	//ContactProVectSerializable vectObj1 = new ContactProVectSerializable(selContactVect);
    		//intent.putExtra("selContacts", vectObj1);
			startActivityForResult(intent, CalendarConstants.APP_EDIT_SCREEN);
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog(e.getMessage());
		}
	}//fn showEditActScreen
	
	private void initSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[3];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }
                        
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, CalendarConstants.CRTAPP_CALLING_APP_NAME);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]APPOINTMENTS-FOR-EMPLOYEE-GET[.]VERSION[.]0";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog("Request:"+request.toString());
          
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
            SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initSoapConnection	

    private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, SoapObject request){
    	try{
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
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
		    	if(resultSoap != null){
	    			if(pdialog != null)
	        			pdialog = null;
	    			if(pdialog == null){
	    				pdialog = ProgressDialog.show(CalendarLists.this, "", getString(R.string.SCR_WAIT_TEXTS),true);
		            	Thread t = new Thread() 
		    			{
		    	            public void run() 
		    				{
		            			try{
		            				updateReportsConfirmResponse(resultSoap);
		            			} catch (Exception e) {
		            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
		            			}
		            			ntwrkHandler.post(reloadListView);
		    				}
		    			};
		    	        t.start();	
	    			}
		    	}else{
		    		getLDBActivityList(); 
		    	}
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
	    	}
	    }	    
    };
    
    final Runnable reloadListView = new Runnable(){
	    public void run()
	    {
	    	try{
	        	listviewcall();
		        getListView().invalidateViews();	    		
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
    public void updateReportsConfirmResponse(final SoapObject soap){	
    	boolean errorflag = false;
        try{ 
        	if(soap != null){                
            	String soapMsg = soap.toString();
        		SapGenConstants.showLog("Count : "+soap.getPropertyCount());
    			SalesProActOutputConstraints category = null;
    			SalesProAppCustomersConstraints customers = null;
	            if(appList != null)
	            	appList.clear();
            	if(allAppList != null)
            		allAppList.clear();
	            if(cusList != null)
	            	cusList.clear();
	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[22];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_INTRCTN11")){
	                                if(category != null)
	                                    category = null;
	                                    
	                                category = new SalesProActOutputConstraints(resArray);	
	                                if(appList != null)
	                                	appList.add(category);	    
                                	if(allAppList != null)
                                		allAppList.add(category);
	                                
	                            }	    
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_DCMNTCSTMRCNTCT10S")){
	                                if(customers != null)
	                                	customers = null;
	                                    
	                                customers = new SalesProAppCustomersConstraints(resArray);	
	                                	                               
	                                if(cusList != null)
	                                	cusList.add(customers);	      
	                            }	    
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SapGenConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SapGenConstants.showLog(taskErrorMsgStr);
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {
	    	                                SapGenConstants.showErrorDialog(CalendarLists.this, taskErrorMsgStr);
	                                    }
	                                });
	                            }
	                        }
	                    }
	                }
	            }//for	        	
        	}else{
        		errorflag = true;
        	}
        }
        catch(Exception sff){
        	SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse for getting contacts from sap:"+sff.toString());
        	errorflag = true;
        } 
        finally{
        	SapGenConstants.showLog("appList Size : "+appList.size());
        	SapGenConstants.showLog("cusList Size : "+cusList.size());
        	if((appList != null) && (appList.size() > 0)){
        		AppDBOperations.deleteAllAppCategoryDataFromDB(this);
				insertappListDataIntoDB();
			}
        	
        	if((cusList != null) && (cusList.size() > 0)){
        		AppDBOperations.deleteAllAppCusCategoryDataFromDB(this);
        		insertActCusListDataIntoDB();
			}
        	if(idList != null){
    			idList.clear();
    		}
        	if(!errorflag){
        		idList = AppDBOperations.readAllAppIdFromDB(this);
        		galIdList = AppDBOperations.readAllGallAppIdFromDB(this);
    			if(galIdList != null){
    				//deletion for unwanted row form gallery table and if activity id not available from SAP list.
	        		if(galIdList != null && galIdList.size() > 0){
	        			AppDBOperations.deleteGallRowForEmptyID(this);
	        			for(int id=0;id<galIdList.size();id++){
	        				if(idList != null && idList.size() > 0){
	        					String gallId = galIdList.get(id).toString().trim();
	        					if(gallId != null && gallId.length() > 0){
			        				if (!idList.contains(gallId)){
			        					SapGenConstants.showLog("gallId : "+gallId);
			        					AppDBOperations.deleteGallRowByGivnenID(this, gallId);
			        				}
	        					}
	        				}
	        			}
	        		}
    			}
        	}
        	stopProgressDialog();
        }
    }//fn updateReportsConfirmResponse  
    
    private void insertappListDataIntoDB(){
    	SalesProActOutputConstraints actCategory;
    	try {
			if(appList != null){
				for(int k=0; k<appList.size(); k++){
					actCategory = (SalesProActOutputConstraints) appList.get(k);
					if(actCategory != null){
						AppDBOperations.insertAppCategoryDataInToDB(this, actCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertappListDataIntoDB: "+ewe.toString());
		}
    }//fn insertappListDataIntoDB
    
    private void insertActCusListDataIntoDB(){
    	SalesProAppCustomersConstraints customers;
    	try {
			if(cusList != null){
				for(int k=0; k<cusList.size(); k++){
					customers = (SalesProAppCustomersConstraints) cusList.get(k);
					if(customers != null){
						AppDBOperations.insertAppCusCategoryDataInToDB(this, customers);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActCusListDataIntoDB: "+ewe.toString());
		}
    }//fn insertActCusListDataIntoDB
    
    private void getLDBActivityList(){
		try {
			appList = AppDBOperations.readAllAppDataFromDB(this);
			if(appList != null)
				allAppList = (ArrayList)appList.clone();
			cusList = AppDBOperations.readAllAppCusDataFromDB(this);
			
		} catch (Exception sse) {
			SapGenConstants.showErrorLog("Error on getLDBActivityList: "+sse.toString());
		}
		finally{
        	try {
				listviewcall();
		        getListView().invalidateViews();
			} catch (Exception e) {}
		}
	}//fn getLDBActivityList
		
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			SapGenConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
    private void listviewcall(){
		try {
			setListAdapter(new MyActivityListAdapterForPhone(this));
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  super.onActivityResult(requestCode, resultCode, data);
		  if(resultCode == RESULT_OK && requestCode == CalendarConstants.APP_EDIT_SCREEN){
			  internetAccess = SapGenConstants.checkConnectivityAvailable(CalendarLists.this);
			  if(internetAccess){
				  initSoapConnection();
			  }
			  else{
				  getLDBActivityList();	
				  SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
			  }	
		  }else if(resultCode == RESULT_OK && requestCode == CalendarConstants.APP_CRE_SCREEN){
			  internetAccess = SapGenConstants.checkConnectivityAvailable(CalendarLists.this);
			  if(internetAccess){
				  initSoapConnection();
			  }
			  else{
				  getLDBActivityList();	
				  SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
			  }	
		  }		  
	}
	
	public class MyActivityListAdapterForPhone extends BaseAdapter {      

    	private LayoutInflater mInflater;
    	private SalesProActOutputConstraints category;
        
        public MyActivityListAdapterForPhone(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
        	try {
				if(appList != null){
					return appList.size();
				}
			}
        	catch (Exception e) {
        		SapGenConstants.showErrorLog(e.getMessage());
			}
        	return 0;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;            
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.app_list_item, null);
                holder = new ViewHolder();
                holder.status = (ImageView) convertView.findViewById(R.id.status); 
                holder.contactdetails = (TextView) convertView.findViewById(R.id.contactdetails);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                holder.datetime = (TextView) convertView.findViewById(R.id.datetime);
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
                       
            if(position%2 == 0)
            	holder.llitembg.setBackgroundResource(R.color.item_even_color);
            else
            	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            try{
	            if(appList != null){
	            	category = ((SalesProActOutputConstraints)appList.get(position));
	            	if(category != null){   
	            		String status = category.getStatus();
	            		if(status.equalsIgnoreCase("OPEN")){
	            			holder.status.setImageResource(R.drawable.t1_grey);
	    				}
	    				else if(status.equalsIgnoreCase("COMP")){
	    					holder.status.setImageResource(R.drawable.tl_green);
	    				}
	    				else if(status.equalsIgnoreCase("LEAD")){
	    					holder.status.setImageResource(R.drawable.tl_yellow);
	    				}	
	    				else if(status.equalsIgnoreCase("CANC")){
	    					holder.status.setImageResource(R.drawable.tl_red);
	    				}
	    				else if(status.equalsIgnoreCase("PROC")){
	    					holder.status.setImageResource(R.drawable.tl_blue);
	    				}            		            	

	                    String data = "";
	                    String parnrName = "", kunnrName = "";
	                    
	            		parnrName = category.getParnrName().toString().trim();
	            		if(parnrName != null && parnrName.length() > 0){          		
		            		if(CalendarLists.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		        				if(parnrName.length() >= SapGenConstants.MIN_STR_CHAR){
		        					parnrName = parnrName.substring(0, SapGenConstants.MIN_STR_CHAR)+"... ";
		        				}
		                		data += parnrName;		  
		        			} else {  
		                		data += parnrName;
		        			}
	                	}            		
	            		
	            		kunnrName = category.getKunnrName().toString().trim();
	            		if(kunnrName != null && kunnrName.length() > 0){          		
		            		if(CalendarLists.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		        				if(kunnrName.length() >= SapGenConstants.MIN_STR_CHAR){
		        					kunnrName = kunnrName.substring(0, SapGenConstants.MIN_STR_CHAR)+"... ";
		        				}
		                		data +=  ", "+kunnrName;		  
		        			} else {  
		                		data +=  ", "+kunnrName;
		        			}
	                	}	            		
	                	/*if(category.getKunnrName().toString().length() > 0){
	                		data += ", "+category.getKunnrName().toString();
	                	}*/	                	
	            		holder.contactdetails.setText(data);           		
	
	            		String desc = category.getDescription();
	            		String idValue = getResources().getString(R.string.SCR_LBLDOC)+" "+category.getObjectId().toString().trim();
	            		String line2 = idValue + " "+ desc;	            		
	            		holder.desc.setText(line2); 
	            		
	            		String datefrom = category.getDateFrom();
	            		String dateto = category.getDateTo();
	            		String timefrom = category.getTimeFrom();
	            		String timeto = category.getTimeTo();
	            		
	            		if(timefrom != null){
		            		int index1 = timefrom.lastIndexOf(":");
	    					if(index1 != -1){
	    						timefrom = timefrom.substring(0, index1);
	    					}
	            		}
	            		
	            		datefrom = datefrom.trim();
                    	if(!datefrom.equalsIgnoreCase("")){	                    		
                    		datefrom = SapGenConstants.getSystemDateFormatString(CalendarLists.this, datefrom);
                    	}
                    	
                    	dateto = dateto.trim();
                    	if(!dateto.equalsIgnoreCase("")){	                    		
                    		dateto = SapGenConstants.getSystemDateFormatString(CalendarLists.this, dateto);
                    	}	            		
	            		//holder.datetime.setText(datefrom+"  "+timefrom+" - "+dateto+" "+timeto);  	            		
	            		holder.datetime.setText(datefrom+"  "+timefrom);
	            	}
	            }
            } catch(Exception sfe){
            	SapGenConstants.showErrorLog("Error in listview:"+sfe.toString());
	    	}
            
            return convertView;
        }

        class ViewHolder {
            ImageView status;
            TextView contactdetails;
            TextView desc;
            TextView datetime;
            LinearLayout llitembg;
        }

        public void removeAllTasks() {
            notifyDataSetChanged();
        }        
    }//End of MyActivityListAdapterForPhone	
}