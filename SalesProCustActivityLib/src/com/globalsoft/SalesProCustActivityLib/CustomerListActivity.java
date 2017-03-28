package com.globalsoft.SalesProCustActivityLib;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.globalsoft.SalesProCustActivityLib.Constraints.CustomerCntxOpConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.SalesOrdProCustConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesProCustActivityLib.Database.CustCreditDBOperations;
import com.globalsoft.SalesProCustActivityLib.Database.SalesProCustCreditCP;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class CustomerListActivity extends Activity {	
	private EditText customerET;		
	private ImageButton searchbtn, showsearchbtn;
	private LinearLayout searchLinear, selectLinear;
	private Button showCustBtn;
	public Spinner customer_sp;
	private TextView customerTV, customerTV1;	
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	private static ArrayList getcustDBListarr = new ArrayList();
	private static ArrayList idStrcustSap = new ArrayList();
	private static ArrayList custreadDBList = new ArrayList();
	private static ArrayList custdialogArraylist = new ArrayList();
	private int status=0,selctdPos=0; 	
	private static ArrayList cntxArrList = new ArrayList();
    ArrayList metaNamesArray = new ArrayList();
	private int cntxsListCount = 0, cntctFlag=0;	
	private String cntxsListType = "";
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();	
	private static int respType = 0;
    
	private String taskErrorMsgStr = "", custIdStr, custSearchStr;
    private boolean diagdispFlag = false;
    private boolean isConnAvail = false;
    final Handler custlistData_Handler = new Handler();
    private CustomerListAdapter CustomerListAdapter;
    private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	static final int SOAP_CONTXT = 1;
    static final int SOAP_CUS = 2;
    
    //private Vector custVector = new Vector();
    private ArrayList custList=new ArrayList();
    //private String[] custDetArr;
    private ArrayList custDetArr=new ArrayList();
	private String contactId = "", customerId = "", contactFName="", contactLName="", customerName = "", appNameStr = "",CustId=""; 
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SalesProCustActivityConstants.setWindowTitleTheme(this);
       /* setTitle(R.string.SALESORDPRO_CUST_TITLE1);
        setContentView(R.layout.custmain);*/     
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.custmain); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.SALESORDPRO_CUST_TITLE1));
		appNameStr = this.getIntent().getStringExtra("appName");
		contactId = this.getIntent().getStringExtra("contactId");
		customerId = this.getIntent().getStringExtra("customerId");
		contactFName = this.getIntent().getStringExtra("contactFName");
		contactLName = this.getIntent().getStringExtra("contactLName");
		customerName = this.getIntent().getStringExtra("customerName");
		SalesProCustActivityConstants.showLog("customerId:"+customerId);
		
		int dispwidth = SalesProCustActivityConstants.getDisplayWidth(this);	
		if(dispwidth > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		
		SalesProCustActivityConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
		SalesProCustActivityConstants.APPLN_NAME_STR = "SALESPRO";
		if(SalesProCustActivityConstants.APPLN_NAME_STR == null || SalesProCustActivityConstants.APPLN_NAME_STR.length() == 0){
			SalesProCustActivityConstants.APPLN_NAME_STR = "";
		}
		
		if(customerId != null && customerId.length() > 0){
            initLayout();
            if(customerName != null && customerName.length() > 0)
            	customerET.setText(customerName);
			custSearchAction();
			/*finish();
			Intent intent = new Intent(this, CustomerListDetailScreen.class);
    		intent.putExtra("CustomerId", customerId);
			startActivityForResult(intent,SalesProCustActivityConstants.CUST_DETAIL_SCREEN);*/
		}else{
			isConnAvail = SapGenConstants.checkConnectivityAvailable(CustomerListActivity.this);
	        if(isConnAvail!=false){	        	
	        	SalesProCustActivityConstants.showLog("custIdStr : "+custIdStr);
				initSoapConnection();
	        	initLayout();
	        } 	
	        else{
	            initLayout();
	        }
		}		
    }
    
    private void initLayout(){
		try{
			customerET = (EditText) findViewById(R.id.customerET);			
			searchbtn = (ImageButton) findViewById(R.id.custsearchbtn);
			searchbtn.setOnClickListener(custsrch_btnListener); 			
			int dispwidth = SalesProCustActivityConstants.getDisplayWidth(this);
			if(dispwidth > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH){				
				LinearLayout.LayoutParams linparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				linparams1.width = SalesProCustActivityConstants.EDIT_TEXT_WIDTH;		
				linparams1.height = SalesProCustActivityConstants.EDIT_TEXT_HEIGHT;
				linparams1.rightMargin = SalesProCustActivityConstants.EDIT_TEXT_RIGHTMARGIN;
				customerET.setLayoutParams(linparams1);				
				searchbtn.setBackgroundResource(R.drawable.search1);
				showCustBtn.setTextSize(SalesProCustActivityConstants.TEXT_SIZE_BUTTON);
			}
		}
		catch(Exception sfg){
			SalesProCustActivityConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
    
    private void showCustomerDetailScreen(){
    	try{
    		SalesProCustActivityConstants.showLog("entering detail screeen : ");
    		Intent intent = new Intent(this, CustomerListDetailScreen.class);
    		intent.putExtra("CustomerId", CustId);
			startActivityForResult(intent,SalesProCustActivityConstants.CUST_DETAIL_SCREEN);    
    	}
    	catch(Exception adf){
    		SalesProCustActivityConstants.showErrorLog("Error in showSalesOrdCreationScreen : "+adf.getMessage());
    	}
    }//fn showCustomerDetailScreen
    
    private void initSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SalesProCustActivityConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]CUSTOMER-CREDIT-INFORMATION-CNTX[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            request.addProperty (SalesProCustActivityConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesProCustActivityConstants.showLog(request.toString());         
            respType = SOAP_CONTXT; 
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);           
            //startNetworkConnectionForContext(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
            SalesProCustActivityConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initSoapConnection
    
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
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
	    		if(resultSoap != null){
	    			if(respType == SOAP_CONTXT){
		    			if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(CustomerListActivity.this, "", getString(R.string.COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				updateServerResponseForContext(resultSoap);	    	            				
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    			}else if(respType == SOAP_CUS){
	    				if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(CustomerListActivity.this, "", getString(R.string.COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				updateServerResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    			}
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
	
	private void startNetworkConnectionForContext(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.CUSTOMER_WAIT_TEXTS),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTPForContext(envelopeCE, url);
                    				sleep(2000);
                    			} catch (Exception e) {  }
             				}
                    	}.start();
                    }
                });
		} catch (Exception ae) {
			SalesProCustActivityConstants.showErrorLog(ae.toString());
		}
    }//fn startNetworkConnectionForContext
    
    private void getSOAPViaHTTPForContext(SoapSerializationEnvelope envelopeCE, String url){		
        try {                
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	SalesProCustActivityConstants.showErrorLog("Data handling error : "+ex2);
            	SalesProCustActivityConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                SalesProCustActivityConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	SalesProCustActivityConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                    	SalesProCustActivityConstants.showErrorDialog(ctx, extStr.toString());
                    }
                });
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                	String result = (envelopeCE.getResponse()).toString();
                	SalesProCustActivityConstants.showLog("Results : "+result);
                    
                    SoapObject result1 = (SoapObject)envelopeCE.bodyIn; 
                    SalesProCustActivityConstants.showLog("Results1 : "+result1.toString());
                    
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	SalesProCustActivityConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	SalesProCustActivityConstants.showErrorLog("Error in Soap Conn : "+e.toString());
        }
        finally {                     
        	SalesProCustActivityConstants.showLog("========END OF LOG========");    
            stopProgressDialog();
            this.runOnUiThread(new Runnable() {
                public void run() {
                	updateServerResponseForContext(resultSoap);
                }
            });
        }
    }//fn getSOAPViaHTTPForContext
    
    public void updateServerResponseForContext(SoapObject soap){		
        try{ 
            String[] metaLength = null;
            String[] docTypeArray = new String[1];
        	if(soap != null){
        		CustomerCntxOpConstraints custCntx = null;
        		if(cntxArrList != null)
        			cntxArrList.clear();
    			if(metaNamesArray != null)
    				metaNamesArray.clear(); 
        		
    			SalesProCustActivityConstants.showLog("Count : "+soap.getPropertyCount());
    			
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[40];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, metaSize = 0, eqIndex = 0, resIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SalesProCustActivityConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                    	if(j == 2){
                            	result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                eqIndex = result.indexOf("=");
                                eqIndex = eqIndex+1;
                                firstIndex = firstIndex + 3;
                                docTypeStr = result.substring(eqIndex, (firstIndex-3));
                                result = result.substring(firstIndex);
                                //SapGenConstants.showLog("Result : "+result);                                
                                resC = 0;
                                indexA = 0;
                                resIndex = 0;
                                indexB = result.indexOf(delimeter);
                                int index1 = 0;
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    //SapGenConstants.showLog("Result resp : "+resC+" : "+res);
                                    if(resC > 1){
                                    	metaNamesArray.add(res);
                                    }
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
	                                        //SapGenConstants.showLog("respType : "+respType);
	                                    	
	                                    	String rowCountStrData = respStr[1];
	                                    	//ServiceProConstants.showLog("rowCountStrData : "+rowCountStrData);
	                                    	index1 = rowCountStrData.indexOf("=");
	                                    	index1 = index1+1;
	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
	                                    	//ServiceProConstants.showLog("rowCount : "+rowCount);
	                                        if(j == 2){
	                                        	//if(docTypeStr.equalsIgnoreCase("ZGSEMMST_MTRL10")){
	                                    		docTypeArray[0] = docTypeStr;
		                                    	//SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		cntxsListCount = Integer.parseInt(rowCount);
	                                    		cntxsListType = respType;
	                                        }
	                                        
	                                    }
                                    }
                                    //SapGenConstants.showLog("resC :"+ resC);
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaLength = new String[metaSize];
                                metaNamesArray.add(last);
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
	                            
	                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
	                                if(custCntx != null)
	                                	custCntx = null;	                                    
	                                custCntx = new CustomerCntxOpConstraints(resArray);
	                                if(custCntx != null)
	                                	cntxArrList.add(custCntx);	
	                            }
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SalesProCustActivityConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SalesProCustActivityConstants.showLog(taskErrorMsgStr);
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {
	    	                                SalesProCustActivityConstants.showErrorDialog(CustomerListActivity.this, taskErrorMsgStr);
	                                    }
	                                });
	                            }
	                        }
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
            SalesProCustActivityConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{
        	SalesProCustActivityConstants.showLog("cntxArrList : "+cntxArrList.size());
        	stopProgressDialog();
        	try{
        		if((cntxArrList!=null) && (cntxArrList.size()>0)){
        			insertCntxData();
        		}
        	}catch(Exception sff1){
        		 SalesProCustActivityConstants.showErrorLog("On updateServerResponseForContext : "+sff1.toString());
        	}
        	if(cntctFlag ==1){
        		CustomerListActivity.this.runOnUiThread(new Runnable() {
    	            public void run() {
    	            	initCustomerSoapConnection();
    	            }
    	        });
        	}        	
        }
    }//fn updateServerResponseForContext
    
    private void insertCntxData() {
    	try {
			if(metaNamesArray != null && metaNamesArray.size() > 0){
				if(cntxArrList != null && cntxArrList.size() > 0){
					if(cntxsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((cntxsListCount == 0) && (cntxArrList.size() == 0)){
							CustCreditDBOperations.deleteCntxDataFromDB(this,SalesProCustCreditCP.CUS_CNTX_CONTENT_URI);
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() > 0)){
							CustCreditDBOperations.deleteCntxDataFromDB(this,SalesProCustCreditCP.CUS_CNTX_CONTENT_URI);
							insertCntxDataInToDB();
						}
					}
					if(cntxsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((cntxsListCount == 0) && (cntxArrList.size() == 0)){
							CustCreditDBOperations.deleteCntxDataFromDB(this,SalesProCustCreditCP.CUS_CNTX_CONTENT_URI);
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() == 0)){
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() > 0)){
							CustCreditDBOperations.deleteCntxDataFromDB(this,SalesProCustCreditCP.CUS_CNTX_CONTENT_URI);
							insertCntxDataInToDB();
						}
					}
				}
			}
		} catch (Exception e) {
			SalesProCustActivityConstants.showErrorLog("Error On insertCntxData: "+e.toString());
		}
	}//fn insertCntxData
    
    private void insertCntxDataInToDB() {
		CustomerCntxOpConstraints cntxcategory;
		try{
			if((cntxArrList!=null)){
				for(int k=0;k<cntxArrList.size();k++){
					cntxcategory= (CustomerCntxOpConstraints)cntxArrList.get(k);
					if(cntxcategory!=null){
						CustCreditDBOperations.insertCntxListDataInToDB(this, cntxcategory);
					}
				}
			}
		}catch(Exception sff1){
			SalesProCustActivityConstants.showErrorLog("On insertSerchDataInToDB : "+sff1.toString());
		}
	}
    
    private void setWindowTitle(final String title){
    	try{
    		this.setTitle(title);
    	}
    	catch(Exception sdf){
    		SalesProCustActivityConstants.showErrorLog("Error in setWindowTitle : "+sdf.getMessage());
    	}
    }//fn setWindowTitle
    
    private OnClickListener showsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(searchLinear != null)
        		searchLinear.setVisibility(View.VISIBLE);
        	
        	if(selectLinear != null)
        		selectLinear.setVisibility(View.GONE);
        	
        	if(showCustBtn != null)
        		showCustBtn.setVisibility(View.GONE);
        	
        	setWindowTitle(getString(R.string.SALESORDPRO_CUST_TITLE1));
        }
    };
    
    private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	custSearchAction();
        }
    };
    
    private void custSearchAction(){
    	try {
    		if(customerId != null && customerId.length() > 0){
    			cntctFlag = 1;
    			custSearchStr = customerId.toString().trim();
				SalesProCustActivityConstants.showLog("customer : "+custSearchStr);
				isConnAvail = SapGenConstants.checkConnectivityAvailable(CustomerListActivity.this);					
				if(isConnAvail!=false){						
					initSoapConnection();
				}						
				else
					initDBConnection(custSearchStr);
    		}else{
    			if(customerET.getText().toString().trim().length() != 0) {
    				try{
    					custSearchStr = customerET.getText().toString().trim();
    					SalesProCustActivityConstants.showLog("customer : "+custSearchStr);
    					isConnAvail = SapGenConstants.checkConnectivityAvailable(CustomerListActivity.this);					
    					if(isConnAvail!=false)
    						initCustomerSoapConnection();						
    					else
    						initDBConnection(custSearchStr);
    				}
    				catch(Exception wsfsg){
    					SalesProCustActivityConstants.showErrorLog("Error in Customer Search : "+wsfsg.toString());
    				}
    			}
    			else
    				SalesProCustActivityConstants.showErrorDialog(CustomerListActivity.this, "Enter Customer name to search.");
    		}
		} catch (Exception easd) {
        	SalesProCustActivityConstants.showErrorLog("Error in custSearchAction : "+easd.toString());
		}
    }//fn custSearchAction    
            
	private void initDBConnection(String idStr) {	    	
    	custList = CustCreditDBOperations.readAllSerchIdDataFromDB(CustomerListActivity.this,idStr);	    	
    	if(custList.size() != 0)
			prefillCustomerData();     		
    	else {
    		SalesProCustActivityConstants.showErrorDialog(CustomerListActivity.this, "Data Not available in Offline mode");
		}	    	
	}//initDBConnection
    
    private void initCustomerSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SalesProCustActivityConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0";
            C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+custSearchStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SalesProCustActivityConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesProCustActivityConstants.showLog(request.toString());                 
            respType = SOAP_CUS; 
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesProCustActivityConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initCustomerSoapConnection
    
    private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.CUSTOMER_WAIT_TEXTS),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTP(envelopeCE, url);
                    				sleep(2000);
                    			} catch (Exception e) {  }
             				}
                    	}.start();
                    }
                });
		} catch (Exception ae) {
			SalesProCustActivityConstants.showErrorLog(ae.toString());
		}
    }//fn startNetworkConnection
    
    private void getSOAPViaHTTP(SoapSerializationEnvelope envelopeCE, String url){		
        try {                
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	SalesProCustActivityConstants.showErrorLog("Data handling error : "+ex2);
            	SalesProCustActivityConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                SalesProCustActivityConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	SalesProCustActivityConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                    	SalesProCustActivityConstants.showErrorDialog(ctx, extStr.toString());
                    }
                });
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                	String result = (envelopeCE.getResponse()).toString();
                	SalesProCustActivityConstants.showLog("Results : "+result);
                    
                    SoapObject result1 = (SoapObject)envelopeCE.bodyIn; 
                    SalesProCustActivityConstants.showLog("Results1 : "+result1.toString());
                    
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	SalesProCustActivityConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	SalesProCustActivityConstants.showErrorLog("Error in Soap Conn : "+e.toString());
        }
        finally {                     
        	SalesProCustActivityConstants.showLog("========END OF LOG========");    
            stopProgressDialog();
            this.runOnUiThread(new Runnable() {
                public void run() {
                	updateServerResponse(resultSoap);
                }
            });
        }
    }//fn getSOAPViaHTTP
    
    public void updateServerResponse(SoapObject soap){		
        try{ 
        	if(soap != null){
    			SalesProCustActivityConstants.showLog("Count : "+soap.getPropertyCount());
    			SalesOrdProCustConstraints custCategory = null;
    			    			
    			if(custList != null && custList.size() > 0)
    				custList.clear();
	            	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[40];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SalesProCustActivityConstants.showLog("propsCount : "+propsCount);
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSEVDST_CSTMR10")){
	                                if(custCategory != null)
	                                	custCategory = null;
	                                    
	                                custCategory = new SalesOrdProCustConstraints(resArray);
	                                
	                                if(custList != null)
	                                	custList.add(custCategory);
	                            }
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SalesProCustActivityConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SalesProCustActivityConstants.showLog(taskErrorMsgStr);
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {
	    	                                SalesProCustActivityConstants.showErrorDialog(CustomerListActivity.this, taskErrorMsgStr);
	                                    }
	                                });
	                            }
	                        }
	                    }
	                }
	            }//for
        	}
        }
        catch(Exception sff){
            SalesProCustActivityConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{
        	SalesProCustActivityConstants.showLog("custList Size : "+custList.size());
        	//prefillCustomerData();        	
        	try{
        		Thread t = new Thread() 
    			{
    	            public void run() 
    				{
            			try{
            				deleteSelctdData();
            			}catch(Exception e11){
            				SalesProCustActivityConstants.showErrorLog("Error in deleteSelctdData Thread:"+e11.toString());
            			}
            			custlistData_Handler.post(insertSerchDataInToDB);
            		}    	            
    			};
    	        t.start();	
	    	}catch(Exception sff1){
	    		SalesProCustActivityConstants.showErrorLog("On updateServerResponse : "+sff1.toString());
	    	}
        }
    }//fn updateServerResponse     
    
    final Runnable insertSerchDataInToDB = new Runnable(){
        public void run()
        {
        	try{
        		insertSerchDataInToDB();
        		stopProgressDialog();
        	} catch(Exception sfe){
        		SalesProCustActivityConstants.showErrorLog("Error in insertSerchDataInToDB:"+sfe.toString());
        	}
        }	    
    };
        
    public void deleteSelctdData(){
    	
    	try{
    		SalesOrdProCustConstraints custObj = null;
    		 boolean matchStr=false;
    		 
		    if(idStrcustSap.size()>0){
		    	idStrcustSap.clear();
		    }
		    if(custreadDBList.size()>0){
		    	custreadDBList.clear();
		    }
		    custreadDBList = CustCreditDBOperations.readAllSerchDataFromDB(this);
		    
		    for(int i = 0;i < custList.size();i++){
		    	custObj=(SalesOrdProCustConstraints)custList.get(i);
		    	if(custObj!=null)
		    		idStrcustSap.add(custObj.getCustomerNo().trim());
		    	SalesProCustActivityConstants.showLog(" idStrSap : "+custObj.getCustomerNo().trim());
    		}
    		if((custList.size()) > 0 && (custreadDBList.size()) > 0){
    			getcustDBListarr=CustCreditDBOperations.getDBlist(this);
    			SalesProCustActivityConstants.showLog("getcustDBListarr: "+getcustDBListarr.size());
            	if(getcustDBListarr.size()>0){
					for(int i=0;i<idStrcustSap.size();i++){
						matchStr = getcustDBListarr.contains(idStrcustSap.get(i).toString().trim());
					   	if(matchStr == true){
							CustCreditDBOperations.deleteIdTableDataFromDB(this,SalesProCustCreditCP.CUS_SER_CONTENT_URI, idStrcustSap.get(i).toString().trim());
							SalesProCustActivityConstants.showLog(" ID from SAP : "+idStrcustSap.get(i).toString().trim());
		        		}
					   	SalesProCustActivityConstants.showLog(" matchStr : "+matchStr);
	    			}
            	}
    		}
    	} catch (Exception e) {
    		SalesProCustActivityConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
		}
    }//deleteSelctdData
    
    private void insertSerchDataInToDB() {
    	SalesOrdProCustConstraints custcategory;
		try{
			if((custList!=null)){
    			for(int k=0;k<custList.size();k++){
					custcategory= (SalesOrdProCustConstraints)custList.get(k);
					if(custcategory!=null){
						CustCreditDBOperations.insertSerchdDataInToDB(this, custcategory);
					}
				}
			}
			prefillCustomerData();
		}catch(Exception sff1){
			SalesProCustActivityConstants.showErrorLog("On insertSerchDataInToDB : "+sff1.toString());
		}
	}

	private void prefillCustomerData(){
    	try{
   		 	SalesOrdProCustConstraints matobj=null;
    		if(custList != null){
    			if(custList.size() > 0){
    	    		custDetArr = getCustomerList();
    	    		if(custDetArr != null){
    	    			if(searchLinear != null)
    	            		searchLinear.setVisibility(View.GONE);
    	            	
    	            	if(selectLinear != null)
    	            		selectLinear.setVisibility(View.VISIBLE);
    	            	
    	            	if(showCustBtn != null)
    	            		showCustBtn.setVisibility(View.VISIBLE);    	    			
    	            	
    	            	//showAlert();
    	            	if(custList.size()>1){
    	            		showAlert();
    	            	}else{   	            			   	            		
    	            		matobj = ((SalesOrdProCustConstraints)custList.get(0));
   	    				 	CustId=matobj.getCustomerNo().toString();    	            	
   	    				 	SalesProCustActivityConstants.showLog(" CustId : "+CustId);   	    				    	    				
	    	            }    	 
    	    		}
    	    		
    			}
    		}else{
    			CustId = custSearchStr;
    		}
    		if(CustId!=null){
		     		showCustomerDetailScreen();
		     	}
    		setWindowTitle(getString(R.string.SALESORDPRO_CUST_TITLE2));
    	}
    	catch(Exception adf){
    		SalesProCustActivityConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
    
	public void showAlert(){ 		
		 alert();	    	
	}//fn showAlert    
	 
	public void alert(){
		 try{   		     	       	
         	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
     		  View layout;
     		  
     		  layout = inflater.inflate(R.layout.customer_dialog,
     				  (ViewGroup) findViewById(R.id.listviewdialog));		        		       		  
     		  
     		  ListView listview = (ListView)layout.findViewById(R.id.list2);
     		  CustomerListAdapter = new CustomerListAdapter(this);
     			        		  
     		  builder = new AlertDialog.Builder(this).setTitle("Select a Customer");	        		  	        		 
     		  builder.setInverseBackgroundForced(true);
     		  View view=inflater.inflate(R.layout.custom_title_layout,null);
     		  builder.setCustomTitle(view);	        		 
     		  builder.setView(layout); 	        		
     		  builder.setSingleChoiceItems(CustomerListAdapter, -1,new DialogInterface.OnClickListener() {     		  	  
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SalesProCustActivityConstants.showLog("which : "+which);
					selctdPos=which;
					SalesProCustActivityConstants.showErrorLog("Selected Position : "+selctdPos);
					customerDetailScreen(selctdPos);
					alertDialog.dismiss();
					}
				});
     		  alertDialog = builder.create();    		  
     		  alertDialog.show();
     		 
     	}catch(Exception sfg){
     		SalesProCustActivityConstants.showErrorLog("Error in showAlert : "+sfg.toString());
     	}
	 }
	 
	 public class CustomerListAdapter extends BaseAdapter {
	    	
	    	LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	
	    	public CustomerListAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);   
	        }
	       
	        public int getCount() {
	        	try {
					if(custDetArr != null)
						return custDetArr.size();
				}
	        	catch (Exception e) {
	        		SalesProCustActivityConstants.showErrorLog(e.getMessage());
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
	            	TextView ctname;   
	            	LinearLayout llitembg1;
	            }
	            
	            ViewHolder holder;
	            convertView = mInflater.inflate(R.layout.customer_list, null);
	            holder = new ViewHolder();
	            holder.ctname = (TextView) convertView.findViewById(R.id.custname);              
	            holder.llitembg1 = (LinearLayout) convertView.findViewById(R.id.llitembg2);
	            

	            if(position%2 == 0)
					holder.llitembg1.setBackgroundResource(R.color.item_even_color);
				else
					holder.llitembg1.setBackgroundResource(R.color.item_odd_color);
	            
	            try {
	            	if(custDetArr != null){            		
	            		//stkCategory = ((SalesOrdProHeadOpConstraints)priceList.get(position));
	            		String spname=custDetArr.get(position).toString();
	            		 holder.ctname.setText(spname);			           	
	            } 
	            }catch (Exception qw) {
	            	SalesProCustActivityConstants.showErrorLog("Error in ContactListAdapter : "+qw.toString());
				}
	            
	           return convertView;	           
			}
			
			public void removeAllTasks() {
	            notifyDataSetChanged();
	        } 		
	    }//End of ContactListAdapter
	 

	 
	 private void customerDetailScreen(int statuspos){

		 SalesProCustActivityConstants.showLog("Show Cust btn clicked");
		 String data="";
		 SalesOrdProCustConstraints matobj=null;
		 if(custdialogArraylist != null)
			 custdialogArraylist.clear();
	    	else
	    		custdialogArraylist = new ArrayList();
     	if(custList != null){
     		int len = custList.size();
     		SalesProCustActivityConstants.showLog("len : "+len);	    
     		SalesProCustActivityConstants.showLog("statuspos : "+statuspos);	   		  			    			  
    				matobj = ((SalesOrdProCustConstraints)custList.get(statuspos));
    				 CustId=matobj.getCustomerNo().toString();
    				//custdialogArraylist.add(CustId);
    				//SalesProCustActivityConstants.showLog("priceArrSelctdList Count : "+custdialogArraylist.size());  		   		   	  		
     	}		 
     	if(CustId!=null){
     		showCustomerDetailScreen();
     	}
     	else
     		SalesProCustActivityConstants.showErrorDialog(CustomerListActivity.this, "Customer Id is empty!");
     }
	 
	 private void backButton(){
		 if(searchLinear != null)
     		searchLinear.setVisibility(View.VISIBLE);
     	     	
     	
     	setWindowTitle(getString(R.string.SALESORDPRO_CUST_TITLE1));
	 }//
	 
	 
    private ArrayList getCustomerList(){
    	String choices[] = null;
        try{
		    if(custDetArr.size() > 0){
		    	custDetArr.clear();
		    }		    
            if(custList != null){
            	SalesOrdProCustConstraints empObj = null;
                String nameStr = "", idstr="", combStr="";
                
                int arrSize = custList.size();
                choices = new String[arrSize];
                
                for(int h=0; h<arrSize; h++){
                    empObj = (SalesOrdProCustConstraints)custList.get(h);
                    if(empObj != null){
                    	nameStr = empObj.getName().trim();
                    	idstr = empObj.getCustomerNo().trim();
                        combStr = nameStr+":"+idstr;
                        custDetArr.add(combStr);
                    }
                }
            }
        }
        catch(Exception sfg){
        	SalesProCustActivityConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        SalesProCustActivityConstants.showLog("Size of choices : "+choices.length);
        return custDetArr;
    }//fn getCustomerList
    
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			SalesProCustActivityConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
}//End of class CustomerListActivity
