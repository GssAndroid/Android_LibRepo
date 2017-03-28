package com.globalsoft.SalesPro;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.CalendarLib.CalendarLists;
import com.globalsoft.ContactLib.ContactMain;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.ProductLib.ProductMainScreenForTablet;
import com.globalsoft.SalesOrderLib.SalesOrderListActivity;
import com.globalsoft.SalesOrderLib.SalesOrderMainScreenTablet;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Observer.SalesProSmsObserver;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SalesProCustActivityLib.CustomerListActivity;
import com.globalsoft.SapLibActivity.ActivityListForPhone;
import com.globalsoft.SapLibActivity.ActivityListforTablet;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesProActivity extends Activity {	
	private Button contactsBtn, customerBtn, salesordBtn, pricelistBtn, quotationsBtn, inventoryBtn;
	private Button activitiesBtn, appointmentsBtn;
	private ImageView infoBtn;
	private boolean internetAccess = false;
	
	//For Timer 
	private TimerTask scanTask; 
	private final Handler handler = new Handler(); 
	private Timer t = new Timer();
	
	final Handler service_Handler = new Handler();
	private ProgressDialog pdialog = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private double latitide,longitude;
	
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
    AppLocationService appLocationService;
    
    private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private ContactProContactCreationOpConstraints catObj;
    private ArrayList updationForEdit = new ArrayList();
    private ArrayList updationForAdd = new ArrayList();
    private boolean postflag = false;
    
    //For Adding 
    private String strAddContactSAPCusFName = "";
	private String strAddContactSAPCusLName = "";
	private String strAddContactSAPCusEmailHome = "";
	private String strAddContactSAPCusEmailWork = "";
	private String strAddContactSAPCusEmailOther = "";
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //try {	        
        	SalesOrderProConstants.setWindowTitleTheme(this);        
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.main);
	        appLocationService = new AppLocationService(
	        		SalesProActivity.this);
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle_i); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.app_name));

			int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
			if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			if(SapGenConstants.DiagnosisDetailsArr.size()!=0)
				SapGenConstants.DiagnosisDetailsArr.clear();
			
			contactsBtn = (Button) findViewById(R.id.contactsBtn);
			contactsBtn.setOnClickListener(contactsBtnListener);
			
			salesordBtn = (Button) findViewById(R.id.salesordBtn);
			salesordBtn.setOnClickListener(salesordBtnListener);  
		     
			appointmentsBtn = (Button) findViewById(R.id.appointmentsBtn);
			appointmentsBtn.setOnClickListener(appointmentsBtnListener); 
		     
			customerBtn = (Button) findViewById(R.id.customerBtn);
			customerBtn.setOnClickListener(customerBtnListener);  
			
			activitiesBtn = (Button) findViewById(R.id.activitiesBtn);
			activitiesBtn.setOnClickListener(activitiesBtnListener);  
		     
			pricelistBtn = (Button) findViewById(R.id.pricelistBtn);
			pricelistBtn.setOnClickListener(pricelistBtnListener); 
			
			quotationsBtn = (Button) findViewById(R.id.quotationsBtn);
			quotationsBtn.setOnClickListener(quotationBtnListener);  
		     
			inventoryBtn = (Button) findViewById(R.id.inventoryBtn);
			inventoryBtn.setOnClickListener(inventoryBtnListener); 

			infoBtn = (ImageView) findViewById(R.id.info);
			infoBtn.setOnClickListener(infoBtnListener);
						
			if(SalesOrderProConstants.smsSentObserver == null){
		    	SalesOrderProConstants.smsSentObserver = new SalesProSmsObserver(new Handler(), this);
		    }
			this.getContentResolver().registerContentObserver(SalesOrderProConstants.SMS_STATUS_URI, true, SalesOrderProConstants.smsSentObserver);
			
			
			internetAccess = SapGenConstants.checkConnectivityAvailable(SalesProActivity.this);	
			if(internetAccess){
				Location nwLocation = appLocationService
						.getLocation(LocationManager.NETWORK_PROVIDER);

				if (nwLocation != null) {
					latitide = nwLocation.getLatitude();
					 longitude = nwLocation.getLongitude();
					Toast.makeText(
							getApplicationContext(),
							"Mobile Location (NW): \nLatitude: " + latitide
									+ "\nLongitude: " + longitude,
							Toast.LENGTH_LONG).show();
				} /*else {
					showSettingsAlert("NETWORK");
				}*/
				initPingConnection();
			}
			/*if(internetAccess){
				try{
					disableBtnAction();
					int getApplQueueCount = SapQueueProcessorHelperConstants.getApplicationQueueCount(this, SalesOrderProConstants.APPLN_NAME_STR);
					//ServiceProConstants.showLog("getApplQueueCount:"+getApplQueueCount); 
					try{
						if(getApplQueueCount <= 0){
							enableBtnAction();
						}
						else{
							try{
								SapQueueProcessorHelperConstants.updateSelectedRowStatus(this, 0, SalesOrderProConstants.APPLN_NAME_STR, ContactsConstants.STATUS_HIGHPRIORITY);
								//startProgressDialog();
								enableBtnAction();
							}
				            catch(Exception sff2){
				            	SalesOrderProConstants.showErrorLog("Error in ServicePro for getApplicationQueueCount else part: "+sff2.toString());
				            }   
						}			
					}
		            catch(Exception sff1){
		            	SalesOrderProConstants.showErrorLog("Error in ServicePro for getApplicationQueueCount: "+sff1.toString());
		            }    
				}
	            catch(Exception sff2){
	            	SalesOrderProConstants.showErrorLog("Error in getApplQueueCount if part: "+sff2.toString());
	            }   
			}*/			
        /*} catch (Exception de) {
        	SalesOrderProConstants.showErrorLog("Error in Application Init: "+de.toString());
        }*/
    }//oncreate
    
    private void initPingConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];           
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	                        
            C0[0].Cdata = SalesOrderProConstants.getApplicationIdentityParameter(this)+":GLTTD:"+latitide+":GLNGTD:"+longitude+":";
            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]PING-SERVER[.]VERSION[.]0";
           // C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+CustomerStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }        
            request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderProConstants.showLog(request.toString());

           // respType = SOAP_CONN_CUSTOMER;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initPriceSoapConnection
    
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
	    			/*if(respType == SOAP_CONN_CUSTOMER){
		    			if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(PriceListActivity.this, "", getString(R.string.COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				updateCustomerServerResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    			}*/
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
    private void enableBtnAction(){
    	try {
			contactsBtn.setClickable(true);
    		salesordBtn.setClickable(true);
    		appointmentsBtn.setClickable(true);
    		customerBtn.setClickable(true);
    		activitiesBtn.setClickable(true);
    		pricelistBtn.setClickable(true);
    		quotationsBtn.setClickable(true);
    		inventoryBtn.setClickable(true);
		} catch (Exception ee) {
			SalesOrderProConstants.showErrorLog("Error in enableBtnAction: "+ee.toString());
		}
    }//fn enableBtnAction
    
    private void disableBtnAction(){
    	try {
    		contactsBtn.setClickable(false);
    		salesordBtn.setClickable(false);
    		appointmentsBtn.setClickable(false);
    		customerBtn.setClickable(false);
    		activitiesBtn.setClickable(false);
    		pricelistBtn.setClickable(false);
    		quotationsBtn.setClickable(false);
    		inventoryBtn.setClickable(false);
		} catch (Exception ee) {
			SalesOrderProConstants.showErrorLog("Error in disableBtnAction: "+ee.toString());
		}
    }//fn disableBtnAction
    
    private void startProgressDialog(){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)                    
            	pdialog = ProgressDialog.show(this, "", getString(R.string.SERVICEPRO_WAIT_TEXTS_FOR_UPDATION),true);				
				dostarttimer();
				
		} catch (Exception ae) {
			SalesOrderProConstants.showErrorLog("Error in startProgressDialog: "+ae.toString());
		}
    }//fn startProgressDialog
    
    public void dostarttimer(){
    	try{     	
    		scanTask = new TimerTask() { 
            public void run() { 
                handler.post(new Runnable() { 
                    public void run() { 
                    	SalesOrderProConstants.showLog("TIMER: Timer started"); 
                    	checkAppQueueCount();
                    } 
               }); 
            }};
	        t.schedule(scanTask, 100, ContactsConstants.TIMER_CONST);
    	}
    	catch(Exception qw){
    		SalesOrderProConstants.showErrorLog("Error in dostarttimer:"+qw.toString());
    	}  
    }//fn dostarttimer 
    
    private void checkAppQueueCount(){
    	int count = 0;
    	try {
			count = SapQueueProcessorHelperConstants.getApplicationQueueCount(this, SalesOrderProConstants.APPLN_NAME_STR);
			if(count == 0){
				stopTimerTask();
				stopProgressDialog();
				enableBtnAction();
			}else{
				if(count > 1){
					pdialog.setMessage(getResources().getString(R.string.SERVICEPRO_WAIT_TEXTS_FOR_UPDATION) + " " +count + " items remaining..." );
				}else{
					pdialog.setMessage(getResources().getString(R.string.SERVICEPRO_WAIT_TEXTS_FOR_UPDATION) + " " +count + " item remaining..." );
				}
			}
			
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in checkAppQueueCount:"+e.toString());
		}
    }//fn checkAppQueueCount
    
    public void stopTimerTask(){  
    	try{
	       if(scanTask!=null){ 
	    	   SalesOrderProConstants.showLog("TIMER: Timer canceled"); 
	    	   scanTask.cancel(); 
	       } 
    	}
    	catch(Exception qw){
    		SalesOrderProConstants.showErrorLog("Error in stoptimer:"+qw.toString());
    	}  
    }//fn stoptimer
        
    private OnClickListener contactsBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("contactsBtn clicked");
        	showEnterpriseContactsScreen();
        }
    };
    
    private OnClickListener customerBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("customer Btn clicked");
        	showCustomerCreditInfoScreen();
        }
    };
    
    private OnClickListener salesordBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("salesord Btn clicked");
        	showSalesOrdersScreen();
        }
    };
    
    private OnClickListener pricelistBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("pricelist Btn clicked");
        	showPriceListScreen();
        }
    };
    
    private OnClickListener quotationBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("quotation Btn clicked");
        	showQuotationsScreen();
        }
    };
    
    private OnClickListener inventoryBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("inventory Btn clicked");
        	showInventoryScreen();
        }
    };
    
    private OnClickListener activitiesBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Activities Btn clicked");
        	showGeneralActivityScreen();
        }
    };
    
    private OnClickListener appointmentsBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Appointments Btn clicked");
        	showGeneralAppointmentScreen();
        }
    };
    
    private OnClickListener infoBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Info Btn clicked");
        	showAboutScreen();
        }
    };
    
    private void showAboutScreen(){
    	try {
			Intent intent = new Intent(this, About.class);
			startActivityForResult(intent,SalesOrderProConstants.SAPABOUT_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
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
			SalesOrderProConstants.showErrorLog("Error in stopProgressDialog:"+ce.toString());
		}
    }//fn stopProgressDialog
    
    private void showEnterpriseContactsScreen(){
		try {
			Intent intent = new Intent(SalesProActivity.this, ContactMain.class);
        	intent.putExtra("app_name_options", "SALESPRO");
        	intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
			startActivity(intent);
			/*internetAccess = SalesOrderProConstants.checkConnectivityAvailable(SalesProActivity.this);
            if(internetAccess){
            	if(contactProCusDbObj == null){
    				contactProCusDbObj = new ContactProSAPCPersistent(this);
    			}
    			final String updateCount[] = contactProCusDbObj.getUpdateCheckForDevToSAP();
    			if(updateCount != null){			
    				if(updateCount.length > 0){		
    					for(int i=0;i<updateCount.length;i++){
							SalesOrderProConstants.showLog("trueValue:"+updateCount[i]);
						}			
    					//startServiceFun();
		        		startUpdates();    
    				}				
    				else{
    					Intent intent = new Intent(SalesProActivity.this, ContactMain.class);
    					intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
    					startActivity(intent);
    				}
    			}
    			else{
    				SalesOrderProConstants.showLog("updateCount:"+updateCount);
    				Intent intent = new Intent(SalesProActivity.this, ContactMain.class);
    				intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
    				startActivity(intent);
    			}		
            }
            else{
            	Intent intent = new Intent(SalesProActivity.this, ContactMain.class);
            	intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
				startActivity(intent);
            }*/				
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showEnterpriseContactsScreen
    
    private void showCustomerCreditInfoScreen(){
		try {
			Intent intent = new Intent(this, CustomerListActivity.class);
			intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
			startActivityForResult(intent,SalesOrderProConstants.CUSTCRDINFO_LAUNCH_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showCustomerCreditInfoScreen
    
    private void showSalesOrdersScreen(){
		try {
			int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
			if(SalesOrderConstants.CustomerMatrArrList!=null)
				SalesOrderConstants.CustomerMatrArrList.clear();
			SalesOrderProConstants.showLog("dispwidth: "+dispwidth); 
			SalesOrderProConstants.showLog("SCREEN_CHK_DISPLAY_WIDTH: "+SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH); 
			Intent intent;
			if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(this, SalesOrderMainScreenTablet.class);
			}else{
				intent = new Intent(this, SalesOrderListActivity.class);
			}  	
			startActivityForResult(intent,SalesOrderProConstants.SALESORDLIST_LAUNCH_SCREEN);
			/*Intent intent = new Intent(SalesProActivity.this, SalesOrderListActivity.class);
			intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
			startActivityForResult(intent,SalesOrderProConstants.SALESORDLIST_LAUNCH_SCREEN);*/
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showSalesOrdersScreen
    
    private void showPriceListScreen(){
		try {
			int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
		
			if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				Intent intent = new Intent(this, PriceListMainTablet.class);
				startActivityForResult(intent,SalesOrderProConstants.PRICELIST_LAUNCH_SCREEN_TABLET);
			}else{
				Intent intent = new Intent(this, PriceListActivity.class);
				startActivityForResult(intent,SalesOrderProConstants.PRICELIST_LAUNCH_SCREEN);
			}
			
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showPriceListScreen
    
    private void showQuotationsScreen(){
		try {
			
			Intent intent = new Intent(this, ProductMainScreenForTablet.class);
			startActivityForResult(intent,SalesOrderProConstants.QUOTATION_LAUNCH_SCREEN);
		
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showQuotationsScreen
    
    private void showInventoryScreen(){
		try {
			Intent intent = new Intent(this, StockListActivity.class);
			startActivityForResult(intent,SalesOrderProConstants.STOCKLIST_LAUNCH_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showInventoryScreen
        
    private void showGeneralActivityScreen(){
		try {
			int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
			SalesOrderProConstants.showLog("dispwidth: "+dispwidth); 
			SalesOrderProConstants.showLog("SCREEN_CHK_DISPLAY_WIDTH: "+SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH); 
			Intent intent;
			if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(this, ActivityListforTablet.class);
				//intent = new Intent(this, ActivityListForPhone.class);
			}else{
				intent = new Intent(this, ActivityListForPhone.class);
			}  	
			startActivityForResult(intent,SapGenConstants.ACTION_GEN_ACTIVITY);
			/*//Intent intent = new Intent(this, CrtGenActivity.class);
			Intent intent = new Intent(this, ActivityListForPhone.class);*/			
		} 
		catch (Exception sse) {
			SalesOrderProConstants.showErrorLog("Error on showGeneralActivityScreen : "+sse.getMessage());
		}
	}//fn showGeneralActivityScreen
            
    private void showGeneralAppointmentScreen(){
		try {
			//Intent intent = new Intent(this, CrtGenActivity.class);
			Intent intent = new Intent(this, CalendarLists.class);
			startActivityForResult(intent,SapGenConstants.ACTION_GEN_ACTIVITY);
		} 
		catch (Exception sse) {
			SalesOrderProConstants.showErrorLog("Error on showGeneralAppointmentScreen : "+sse.getMessage());
		}
	}//fn showGeneralAppointmentScreen
        
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	try {   	
    		if(SapGenConstants.DiagnosisDetailsArr.size()!=0)
				SapGenConstants.DiagnosisDetailsArr.clear();
    		SalesOrderProConstants.showLog("Request Code "+requestCode);
    		SalesOrderProConstants.showLog("Result Code "+resultCode);
    		SalesOrderProConstants.showLog("Result Code "+RESULT_OK);
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog(e.toString());
		}
    }
    
}//End of class SalesProActivity