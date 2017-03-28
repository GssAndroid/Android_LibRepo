package com.globalsoft.SalesProCustActivityLib;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.SalesProCustActivityLib.Constraints.CustomerListOpConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesProCustActivityLib.Database.CustCreditDBConstants;
import com.globalsoft.SalesProCustActivityLib.Database.CustCreditDBOperations;
import com.globalsoft.SalesProCustActivityLib.Database.SalesProCustCreditCP;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class CustomerListDetailScreen extends Activity {
	
	private TextView[] salesOrdLblTV;
	private EditText[] salesOrdValET;
	
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	
	private CustomerListOpConstraints custCategory = null;
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();	
	private static int respType = 0;
	/*//NEW
	//private Vector custVector = new Vector();
	private ArrayList mattArrList = new ArrayList();
	private ArrayList mattCopyArrList = new ArrayList();*/

	private static boolean isConnAvail = false;
	final Handler custlistData_Handler = new Handler();
	private static ArrayList getselctdDBListarr = new ArrayList();
	private static ArrayList idStrcustSap = new ArrayList();
	private static ArrayList custreadDBList = new ArrayList();
	private static ArrayList selMattVector = new ArrayList();
	
	private static ArrayList mattArrList=new ArrayList();
	private static ArrayList mattCopyArrList=new ArrayList();
	private static ArrayList custdbarrlist=new ArrayList();
	private String selctdData="",custIdStr="";
	int flag=0;
	private String data=null;
	private ArrayList<String[]> Stringlist = new ArrayList<String[]>();
		
	private int dispwidth = 300;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SalesProCustActivityConstants.setWindowTitleTheme(this);
			/*this.setTitle(R.string.SALESORDPRO_CUSTDET_TITLE);
			setContentView(R.layout.custdetail);*/
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.custdetail); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			
			String title = CustCreditDBOperations.readScreenTitleFromDB(this, CustCreditDBConstants.CNTXT3_DETAIL_SCREEN_TAG);
			if(title != null && title.length() > 0){
				myTitle.setText(title);
			}else{
				myTitle.setText(getResources().getString(R.string.SALESORDPRO_CUSTDET_TITLE));
			}

			int dispwidth = SalesProCustActivityConstants.getDisplayWidth(this);	
			if(dispwidth > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			custIdStr = (String) this.getIntent().getStringExtra("CustomerId");  
        	SalesProCustActivityConstants.showLog("custIdStr : "+custIdStr);
			isConnAvail = SapGenConstants.checkConnectivityAvailable(CustomerListDetailScreen.this);
	        if(isConnAvail!=false){	        	
	        	SalesProCustActivityConstants.showLog("custIdStr : "+custIdStr);
				initSoapConnection();
	        } 	
	        else{
       		 	initLayout();
	        	getCustCreditDetails();
	        	/*custdbarrlist = CustCreditDBOperations.readAllSelctdDataFromDB(CustomerListDetailScreen.this, selMattVector);	        	
	        	if(custdbarrlist.size() == 0){
	        		SalesProCustActivityConstants.showErrorDialog(CustomerListDetailScreen.this, "Not available in Offline mode");
	        	}
	        	else{
	        		 initLayout();
		        	 //getCustCreditDBDetails();
		        	 getCustCreditDetails();
	        	}	 */       
	        }
	        
        } catch (Exception de) {
        	SalesProCustActivityConstants.showErrorLog(de.toString());
        }
    }
	
	private void initLayout(){
		try {			
			int cols =  CustCreditDBOperations.readRecordCountFromDB(this);
			salesOrdLblTV = new TextView[cols];
			salesOrdValET = new EditText[cols];
			
			ArrayList labels = CustCreditDBOperations.readAllLablesFromDB(this);
			for(int i=0; i<labels.size(); i++){
				SalesProCustActivityConstants.showLog("labels : "+labels.get(i).toString().trim());
			}
			
			try{
				dispwidth = SalesProCustActivityConstants.getDisplayWidth(this);
				TableLayout tl = (TableLayout)findViewById(R.id.customerdettbllayout1);
				if(dispwidth < SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH)
					tl.setColumnStretchable(1, true);
				
				TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				int labelWidth = 100, editWidth = 190;
				SalesProCustActivityConstants.showLog("dispwidth : "+dispwidth);
				if(dispwidth > 0){
					labelWidth = dispwidth-editWidth;
					if(labelWidth < 100)
						labelWidth = 140;
					else if(labelWidth > 160)
						labelWidth = 180;
					
					editWidth = dispwidth-labelWidth;
					if(editWidth < 160)
						editWidth = 160;
					else if(editWidth > 250)
						editWidth = 250;
					
					if(dispwidth > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH){
						labelWidth = 220;
						editWidth = 300;
					}
				}
				SalesProCustActivityConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				
				for(int i=0; i<cols; i++){
					tr1 = new TableRow(this);		            	
					salesOrdLblTV[i] = new TextView(this);
					salesOrdValET[i] = new EditText(this);					
					salesOrdLblTV[i].setText("");
					salesOrdLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
					salesOrdLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					salesOrdLblTV[i].setPadding(5,5,5,5); 
					salesOrdLblTV[i].setMinWidth(100);
					salesOrdLblTV[i].setWidth(labelWidth);
					if(dispwidth > SalesProCustActivityConstants.SCREEN_CHK_DISPLAY_WIDTH)
						salesOrdLblTV[i].setTextSize(SalesProCustActivityConstants.TEXT_SIZE_LABEL);					
					salesOrdValET[i].setText("");
					salesOrdValET[i].setPadding(5,5,5,5);
					//salesOrdValET[i].setWidth(160);
					salesOrdValET[i].setWidth(editWidth);					
					salesOrdLblTV[i].setText(labels.get(i).toString().trim());
					/*String val = labels.get(i).toString().trim();
					Paint p = new Paint();
					SalesProCustActivityConstants.showLog(" text width "+ p.measureText(val));*/
					tr1.addView(salesOrdLblTV[i]);
					tr1.addView(salesOrdValET[i]);
					tr1.setLayoutParams(linparams);					
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				}				
				SalesProCustActivityConstants.showLog("Textview width : "+salesOrdLblTV[0].getWidth());
				SalesProCustActivityConstants.showLog("EditText width : "+salesOrdValET[0].getWidth());
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesProCustActivityConstants.showErrorLog("Error in initLayout : "+ssdf.toString());
		}
	}//fn initLayout
	
	
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
            C0[2].Cdata = "EVENT[.]CUSTOMER-CREDIT-INFORMATION-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            C0[3].Cdata = "SDCAS_T_CUSTLIST[.]"+custIdStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }            
            request.addProperty (SalesProCustActivityConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesProCustActivityConstants.showLog(request.toString());          
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
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
	    			if(pdialog != null)
            			pdialog = null;
            		
        			if(pdialog == null){
        				pdialog = ProgressDialog.show(CustomerListDetailScreen.this, "", getString(R.string.COMPILE_DATA),true);
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
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
	
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
        		if(mattArrList != null)
            		mattArrList.clear();        		
    			SalesProCustActivityConstants.showLog("Count : "+soap.getPropertyCount());    			
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSEVXST_CSTMRCRDTINFRMTN10")){
	                                if(custCategory != null)
	                                	custCategory = null;	                                    
	                                custCategory = new CustomerListOpConstraints(resArray);
	                                if(mattArrList != null)
	                                	mattArrList.add(custCategory);	  
	                                
	                                if(mattCopyArrList != null)
	                                	mattCopyArrList.add(custCategory);	                                
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
	    	                                SalesProCustActivityConstants.showErrorDialog(CustomerListDetailScreen.this, taskErrorMsgStr);
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
        	SalesProCustActivityConstants.showLog("mattArrList : "+mattArrList.size());
        	try{        		
        		Thread t = new Thread() 
    			{
    	            public void run() 
    				{
            			try{
            				deleteSelctdData();
            				sleep(1000);
            			}catch(Exception e1){
            				SalesProCustActivityConstants.showErrorLog("Error in deleteSelctdData Thread:"+e1.toString());
            			}
            			custlistData_Handler.post(insertSelectdDataInToDB);
            		}    	            
    			};
    	        t.start();	
        	}catch(Exception sff1){
        		 SalesProCustActivityConstants.showErrorLog("On updateServerResponse : "+sff1.toString());
        	}
        }
    }//fn updateServerResponse     
    
    public void deleteSelctdData(){
    	try{
    		CustomerListOpConstraints matObj = null;
		    boolean matchStr=false;
		    
		    if(idStrcustSap.size()>0){
		    	idStrcustSap.clear();
		    }
		    if(custreadDBList.size()>0){
		    	custreadDBList.clear();
		    }
		    custreadDBList = CustCreditDBOperations.readAllSelctdidDataFromDB(this);
		    SalesProCustActivityConstants.showLog("get selected custreadDBList:"+custreadDBList.size());
		    for(int i=0;i<mattArrList.size();i++){
		    	matObj=(CustomerListOpConstraints)mattArrList.get(i);
		    	if(matObj!=null)
		    		idStrcustSap.add(matObj.getCustomerNo1().toString().trim());
    		}
		    SalesProCustActivityConstants.showLog("get id from SAP idStrcustSap:"+idStrcustSap.size());
    		if((mattArrList.size())>0 && (custreadDBList.size())>0){
    			getselctdDBListarr=CustCreditDBOperations.getDBselctdIdlist(this);
            	SalesProCustActivityConstants.showLog("get selected DBListarr:"+getselctdDBListarr.size());
            	if(getselctdDBListarr.size()>0){
					for(int i=0;i<idStrcustSap.size();i++){
						matchStr = getselctdDBListarr.contains(idStrcustSap.get(i).toString().trim());
						if(matchStr==true){
							CustCreditDBOperations.deleteIdselctdTableDataFromDB(this,SalesProCustCreditCP.CUS_SEL_CONTENT_URI,idStrcustSap.get(i).toString().trim());		        			
		        			SalesProCustActivityConstants.showLog("selectd  ID from SAP : "+idStrcustSap.get(i).toString().trim());
		        		}
	         			SalesProCustActivityConstants.showLog(" Selected matchStr : "+matchStr);
	    			}
            	}
    		}
    	} catch (Exception e) {
			SalesProCustActivityConstants.showErrorLog("Error in deleteSelctdData Thread:"+e.toString());
		}
    }

	final Runnable insertSelectdDataInToDB = new Runnable(){
	    public void run()
	    {
	    	try{
	    		insertSelectdDataInToDB();
	    	} catch(Exception sfe){
	    		SalesProCustActivityConstants.showErrorLog("Error in insertSerchdDataIntoDB:"+sfe.toString());
	    	}
	    }	    
	};
	
	private void insertSelectdDataInToDB() {
		CustomerListOpConstraints stkcategory;
		try{
			if((mattArrList!=null)){
				for(int k=0;k<mattArrList.size();k++){
					stkcategory= (CustomerListOpConstraints)mattArrList.get(k);
					if(stkcategory!=null){
						CustCreditDBOperations.insertselctdListDataInToDB(this, stkcategory);
					}
				}
			}
			stopProgressDialog();
        	initLayout();
        	getCustCreditDetails();
		}catch(Exception sff1){
			SalesProCustActivityConstants.showErrorLog("On insertSerchDataInToDB : "+sff1.toString());
		}
	}

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
    
	private void getCustCreditDetails(){
        try{
        	String delimeter = ":";
			ArrayList field = CustCreditDBOperations.readAllFieldValueFromDB(this);
			custdbarrlist = CustCreditDBOperations.readAllSelctdDataFromDB(CustomerListDetailScreen.this, custIdStr);
			if(custdbarrlist != null && custdbarrlist.size() > 0){
				custCategory = (CustomerListOpConstraints)custdbarrlist.get(0);
	        	HashMap<String, String> fieldArrList = new HashMap<String, String>();
				if(custdbarrlist != null && custdbarrlist.size() > 0){
					if(custCategory != null){   
						Class<?> objClass = custCategory.getClass();
					    Field[] fields = objClass.getFields();
					    for(Field field1 : fields) {
					        String name = field1.getName();
					        Object value = field1.get(custCategory);
					        fieldArrList.put(name, value.toString());
					    }				        
						for(int k1=0; k1<field.size(); k1++){
							 if(salesOrdValET[k1] != null){
								String data = "";
		                    	String dataValue = field.get(k1).toString().trim();	
		                    	if(dataValue.contains(delimeter)){
		                    		String[] value = dataValue.split(delimeter);	
		                    		String fval1 = (String) fieldArrList.get(value[0].toString().trim());
		                    		String fval2 = (String) fieldArrList.get(value[1].toString().trim());
		                    		data =  fval1 +" "+ fval2; 
		                    	}else{
		                    		String fval1 = (String) fieldArrList.get(dataValue);
		                    		data = fval1;
		                    	}
		                    	if(data != null && data.length() > 0){
			                    	salesOrdValET[k1].setText(data);
		                    		salesOrdLblTV[k1].setVisibility(View.VISIBLE);
		                    		salesOrdValET[k1].setVisibility(View.VISIBLE);
		                    	}else{
		                    		salesOrdLblTV[k1].setVisibility(View.GONE);
		                    		salesOrdValET[k1].setVisibility(View.GONE);
		                    	}
		                        salesOrdValET[k1].setEnabled(false);
		                        salesOrdValET[k1].setTextColor(getResources().getColor(R.color.bluelabel));
		                    }
		                }
		            }
				}            
        	}else{
        		SalesProCustActivityConstants.showErrorDialog(this, "No data available for that customer!");
        	}
        }
        catch(Exception adf){
        	SalesProCustActivityConstants.showErrorLog("Error in getSalesOrderDetails : "+adf.toString());
        }
    }//fn getCustCreditDetails
	
	/*private void getCustCreditDBDetails(){
		if(custdbarrlist != null){
			
			CustomerListOpConstraints custCategory = null;
			String name = "", creditmngmnt = "", riskcat = "";
            String custsrdtlimt = "", creditexp = "", curncy = "";
            String creditlimt = "", rate = "", custno = "";
            String classname = "", city = "", region = "",country="";
            
            riskcat = custCategory.getRiskCategory().toString().trim();      
            
            for (int i =0; i < custdbarrlist.size(); i++) {
            	custCategory = (CustomerListOpConstraints)custdbarrlist.get(i);
                if(custCategory != null){
                	name = custCategory.getName().toString().trim();
                    creditmngmnt = custCategory.getBlockedByCreditMgmt().toString().trim();
                    classname = custCategory.getRiskClassName().toString().trim() + "("+riskcat+ ")";
                    custsrdtlimt = custCategory.getCustCreditLimit().toString().trim();
                    creditexp = custCategory.getCreditExposure().toString().trim();
                    curncy = custCategory.getCurrency().toString().trim();        
                    creditlimt = custCategory.getCreditLimitUsed().toString().trim();
                    rate = custCategory.getRating().toString().trim();                        
                    custno = custCategory.getCustomerNo1().toString().trim();  
                    city = custCategory.getCity().toString().trim();                        
                    region = custCategory.getRegion().toString().trim();   
                    country = custCategory.getCountry().toString().trim();   
                    
                   
                }//ifstkCategory
            }//for
            
            for(int k1=0; k1<salesOrdValET.length; k1++){
            	
                if(salesOrdValET[k1] != null){
                    switch(k1){
                    case 0:
                      	salesOrdValET[k1].setText(name);
                        break;
                    case 1:
                    	salesOrdValET[k1].setText(creditmngmnt);
                        break;
                    case 2:
                    	//salesOrdValET[k1].setText(riskcat);
                    	salesOrdValET[k1].setText(classname);
                        break;
                    case 3:
                    	salesOrdValET[k1].setText(custsrdtlimt);
                        break;
                    case 4:
                    	salesOrdValET[k1].setText(creditexp);
                        break;
                    case 5:
                    	salesOrdValET[k1].setText(curncy);
                        break;
                    case 6:
                    	salesOrdValET[k1].setText(creditlimt);
                        break;
                    case 7:
                    	salesOrdValET[k1].setText(rate);
                        break;
                    case 8:
                    	salesOrdValET[k1].setText(custno);
                        break;
                    case 9:
                    	salesOrdValET[k1].setText(city);
                        break;
                    case 10:
                    	salesOrdValET[k1].setText(region);
                        break;
                    case 11:
                    	salesOrdValET[k1].setText(country);
                        break;
                    
                    }//switch
                    if(salesOrdValET[k1]==null){
                    	flag=1;
                    }
                    salesOrdValET[k1].setEnabled(false);
                    salesOrdValET[k1].setTextColor(getResources().getColor(R.color.bluelabel));
                }
            }  
		}//if mattArrList   
                
    }//fn getCustCreditDetails
*/	
}//End of class CustomerListDetailScreen
