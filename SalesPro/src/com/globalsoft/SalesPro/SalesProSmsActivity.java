package com.globalsoft.SalesPro;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesProSmsActivity extends Activity {	
	private String contactId = "", customerId = "", contactFName="", contactLName="", customerName = "", callDurationStr = "", taskErrorMsgStr = "";
	private String formattedDate = "", formattedTime = "", timez_offset = "", callDescStr = "", callNotesStr = "";
	private boolean diagdispFlag = false;
	
	private String smsBodyStr = "", smsPhoneNoStr = "";
    private long smsDatTime = System.currentTimeMillis();
    
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	
	private Button doneBtn, cancelBtn;
	private TextView custNameTV, phoneNoTV, contactTV;
	private EditText descEditText, notesEditText;
	
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	private SoapObject requestSoapObj = null;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SalesOrderProConstants.setWindowTitleTheme(this);
        	/*setTitle("Save Sms Summary To SAP");
			setContentView(R.layout.phonesmslisten);*/	
        	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.phonesmslisten); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText("Save Sms Summary To SAP");

			int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
			if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			contactId = this.getIntent().getStringExtra("contactId");
			customerId = this.getIntent().getStringExtra("customerId");
			contactFName = this.getIntent().getStringExtra("contactFName");
			contactLName = this.getIntent().getStringExtra("contactLName");
			smsBodyStr = this.getIntent().getStringExtra("smsBody");
			smsPhoneNoStr = this.getIntent().getStringExtra("smsPhoneNo");
			smsDatTime = this.getIntent().getLongExtra("smsDateTime", System.currentTimeMillis());
			customerName = this.getIntent().getStringExtra("customerName");
			
			initLayout();
        } catch (Exception de) {
        	SalesOrderProConstants.showErrorLog("Error in Application Init: "+de.toString());
        }
    }
    
    private void initLayout(){
    	try{
    		custNameTV = (TextView)findViewById(R.id.custNameTV);			
    		phoneNoTV = (TextView)findViewById(R.id.phoneNoTV);		
			contactTV = (TextView)findViewById(R.id.contactTV);
			
			doneBtn = (Button) findViewById(R.id.sendSapBtn);
			doneBtn.setOnClickListener(sendSAPBtnListener); 
			
			cancelBtn = (Button) findViewById(R.id.cancelBtn);
			cancelBtn.setOnClickListener(cancelBtnListener); 
						
			descEditText = (EditText)findViewById(R.id.calldescET);
			notesEditText = (EditText)findViewById(R.id.notesET);			
			
			updateUIElements();
    	}
    	catch(Exception sfg){
    		SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    
    private void updateUIElements(){
        try{
        	SalesOrderProConstants.showLog("SAP Contact Id : "+contactId);
            SalesOrderProConstants.showLog("SAP Customer Id : "+customerId);
            SalesOrderProConstants.showLog("SAP Contact FName : "+contactFName);
            SalesOrderProConstants.showLog("SAP Contact LName : "+contactLName);
            SalesOrderProConstants.showLog("SAP Customer Name : "+customerName);
            
            String contact = contactFName.trim()+" "+contactLName.trim()+" ("+contactId.trim()+")";
        	String customer = customerName.trim()+" ("+customerId.trim()+")";
        	
        	custNameTV.setText(" :   "+customer);        	
        	contactTV.setText(" :   "+contact);       
        	
        	if(smsDatTime > 0){			
        		Date l_date = new Date(smsDatTime);
        		Calendar c = Calendar.getInstance(); 
        		TimeZone timeZone = c.getTimeZone();
        		timez_offset = String.valueOf(timeZone.getOffset(l_date.getTime()));
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	        formattedDate = sdf.format(l_date.getTime());
    	        formattedTime = l_date.getHours()+":"+l_date.getMinutes()+":"+l_date.getSeconds();    	        	
    	        callDurationStr = "0";
    	        
				SalesOrderProConstants.showLog("Date : "+timez_offset+" : "+formattedDate+" : "+formattedTime);
                SalesOrderProConstants.showLog("Call Duration : "+callDurationStr);
                SalesOrderProConstants.showLog("Call PhoneNo : "+smsPhoneNoStr);    
                SalesOrderProConstants.showLog("Call Date : "+l_date);
            	phoneNoTV.setText(" :   "+smsPhoneNoStr.trim());
            	
            	if(notesEditText != null)
            		notesEditText.setText(smsBodyStr);
        	}
            else
            	SalesOrderProConstants.showLog("mRecord is null");
        }
        catch(Exception asf){
        	SalesOrderProConstants.showErrorLog("Error in updateUIElements : "+asf.toString());
        }
    }//fn updateUIElements
    
    private OnClickListener sendSAPBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Send Sap btn clicked");
        	doSendToSapAction();
        }
    };
    
    private OnClickListener cancelBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Cancel btn clicked");
        	onClose();
        }
    };
    
    private void onClose(){
    	try{
    		finish();
    		/*this.finish();
    		System.exit(0);*/
    	}
    	catch(Exception sfg){
    		SalesOrderProConstants.showErrorLog("Error in onClose : "+sfg.toString());
    	}
    }//fn onClose
    
    private void doSendToSapAction(){
    	try{
    		if((!contactId.equalsIgnoreCase("")) && (!customerId.equalsIgnoreCase(""))){
    			if(descEditText != null)
    				callDescStr = descEditText.getText().toString().trim();
    			
    			if(notesEditText != null)
    				callNotesStr = notesEditText.getText().toString().trim();
    			
    			initPhoneActSoapConnection();
    		}
    		else
    			SalesOrderProConstants.showErrorDialog(this, "Contact Id and Customer Id Should not be Empty!");
    	}
    	catch(Exception sfg){
    		SalesOrderProConstants.showErrorLog("Error in doSendToSapAction : "+sfg.toString());
    	}
    }//fn doSendToSapAction
    
   
    private void initPhoneActSoapConnection(){        
    	SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[5];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            //C0[0].Cdata = "DEVICE-ID:200000000000000:DEVICE-TYPE:BB:APPLICATION-ID:SALESPRO";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]ACTIVITY-FOR-SMS-MESSAGE-CREATE[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_ACTVTY20[.]OBJECT_ID[.]PARNR[.]KUNNR[.]DESCRIPTION[.]TEXT[.]DATE_FROM[.]TIME_FROM[.]TIMEZONE_FROM";
            C0[4].Cdata = "ZGSXCAST_ACTVTY20[.][.]"+contactId+"[.]"+customerId+"[.]"+callDescStr+"[.]"+callNotesStr+"[.]"+formattedDate+"[.][.]"+formattedTime+"[.][.]"+timez_offset;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderProConstants.showLog(request.toString());
            
            respType = SapGenConstants.RESP_TYPE_UPDATE;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            
            /*
            StartNetworkTask soapTask = new StartNetworkTask(this);
            SoapObject resultSoap = soapTask.execute(envelopeC).get();
            if(resultSoap != null){
        		boolean errFlag = SapGenConstants.getUpdateServerResponse(this, resultSoap);
        		updateServerResponse(errFlag);
        	}
            */
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesOrderProConstants.showErrorLog("Error in initPhoneActSoapConnection : "+asd.toString());
        }
    }//fn initPhoneActSoapConnection
    
    private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, SoapObject request){
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
	    			if(respType == SapGenConstants.RESP_TYPE_UPDATE){
	            		updateServerResponse(resultSoap);
	    			}
	    		}else{
	    			//sendToQueueProcessor();
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };

    
    private void sendToQueueProcessor(){
    	try {
    		Long now = Long.valueOf(System.currentTimeMillis());
    		String newLocalId = formattedTime+now.toString();
    		//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, ContactsConstants.APPLN_NAME_STR, ContactsConstants.APPLN_PACKAGE_NAME, formattedTime,newLocalId, ContactsConstants.APPLN_BGSERVICE_NAME, ContactsConstants.PHONE_ACTIVITY_SMS_MESSAGE_API, requestSoapObj, now);
			onClose();
		} catch (Exception errg) {
			SapGenConstants.showErrorLog("Error in sendToQueueProcessor : "+errg.toString());
		}
    }//fn sendToQueueProcessor
    
    private void updateServerResponse(SoapObject soap){
    	try {
			boolean errFlag = SapGenConstants.getUpdateServerResponse(this, soap);
			if(!errFlag){
				onClose();
			}
			else{
				//onClose();
			}
		} catch (Exception eaa) {
			SapGenConstants.showErrorLog("Error On updateServerResponse: "+eaa.toString());
		}
    }//fn updateServerResponse
    
}//End of class SalesProSmsActivity