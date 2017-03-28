package com.globalsoft.SalesProCustActivityLib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.globalsoft.SalesProCustActivityLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesProCustActivityLib.Constraints.SalesProCustActConstraints;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesProCustActivity extends Activity implements TextWatcher  {
	
	private TextView[] mattTxtView;
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3;
	
	private TextView custNameTV, customerTV;
	private EditText searchET;
	
	private ProgressDialog pdialog = null;
	
	private SoapObject resultSoap = null;
	
	private ArrayList custActArrList = new ArrayList();
	private ArrayList custActCopyArrList = new ArrayList();
	
	private String searchStr = "", mainContactId="", mainCustomerId="", contactFName="", contactLName="", customerName = "";
	private boolean sortFlag = false, sortcustNoFlag = false, searchflag = true;
	private boolean sortDocFlag = false, sortDescFlag = false, sortPostDateFlag = false;
	
    private int sortIndex = -1;
	
	private int headerWidth1, headerWidth2, headerWidth3;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3;
	private int dispwidth = 300;

	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this);
        setTitle("Recent Interactions");
        setContentView(R.layout.socustactoverview);
        try{
        	SalesProCustActivityConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
        	SalesProCustActivityConstants.APPLN_NAME_STR = "SALESPRO";
			if(SalesProCustActivityConstants.APPLN_NAME_STR == null || SalesProCustActivityConstants.APPLN_NAME_STR.length() == 0){
				SalesProCustActivityConstants.APPLN_NAME_STR = "";
			}
			
			mainContactId = this.getIntent().getStringExtra("contactId");
			mainCustomerId = this.getIntent().getStringExtra("customerId");
			if(mainCustomerId != null)
				mainCustomerId = mainCustomerId.trim();
			
			if(mainContactId != null)
				mainContactId = mainContactId.trim();
			
			contactFName = this.getIntent().getStringExtra("contactFName");
			contactLName = this.getIntent().getStringExtra("contactLName");
			customerName = this.getIntent().getStringExtra("customerName");
			
			SalesProCustActivityConstants.showLog("mainContactId: "+mainContactId);
			SalesProCustActivityConstants.showLog("mainCustomerId: "+mainCustomerId);
		}
		catch(Exception afg){}
		initHeaderLayout();
        initCustActListSoapConnection();
    }
    
    private void initHeaderLayout(){
    	try{
    		custNameTV = (TextView)findViewById(R.id.custNameTV);	
			customerTV = (TextView)findViewById(R.id.customerTV);
			
			updateUIElements();
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in initHeaderLayout : "+sfg.toString());
    	}
    }//fn initHeaderLayout
    
    private void updateUIElements(){
        try{
        	String contact = contactFName.trim()+" "+contactLName.trim()+" ("+mainContactId.trim()+")";
        	String customer = customerName.trim()+" ("+mainCustomerId.trim()+")";
        	
        	if(custNameTV != null)
        		custNameTV.setText(" :   "+customer);   
        	
        	if(customerTV != null)
        		customerTV.setText(" :   "+contact);  
        }
        catch(Exception asf){
        	SalesProCustActivityConstants.showErrorLog("Error in updateUIElements : "+asf.toString());
        }
    }//fn updateUIElements
    
    private void initLayout(){
    	try{    		
    		setContentView(R.layout.socustactoverview);
    		
    		//dispwidth = SalesOrderProConstants.getDisplayWidth(this);
    		
    		initHeaderLayout();
    		
    		searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			tableHeaderTV1 = (TextView)findViewById(R.id.TableHeaderTV1);
			tableHeaderTV1.setGravity(Gravity.LEFT);
			tableHeaderTV1.setPadding(10,5,5,5);
			tableHeaderTV1.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader1);
				}
			});
			
			tableHeaderTV2 = (TextView)findViewById(R.id.TableHeaderTV2);
			tableHeaderTV2.setGravity(Gravity.LEFT);
			tableHeaderTV2.setPadding(10,5,5,5);
			tableHeaderTV2.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader2);
				}
			});
			
			tableHeaderTV3 = (TextView)findViewById(R.id.TableHeaderTV3);
			tableHeaderTV3.setGravity(Gravity.LEFT);
			tableHeaderTV3.setPadding(10,5,5,5);
			tableHeaderTV3.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader3);
				}
			});		
			
			ViewTreeObserver vto1 = tableHeaderTV1.getViewTreeObserver();
	        vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV1.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth1 = tableHeaderTV1.getWidth();
	               // SalesProCustActivityConstants.showLog("tableHeaderTV1 Width1 : "+headerWidth1+" : "+tableHeaderTV1.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto2 = tableHeaderTV2.getViewTreeObserver();
	        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV2.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth2 = tableHeaderTV2.getWidth();
	                //SalesProCustActivityConstants.showLog("tableHeaderTV2 Width1 : "+headerWidth2+" : "+tableHeaderTV2.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto3 = tableHeaderTV3.getViewTreeObserver();
	        vto3.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV3.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth3 = tableHeaderTV3.getWidth();
	                //SalesProCustActivityConstants.showLog("tableHeaderTV3 Width1 : "+headerWidth3+" : "+tableHeaderTV3.getMeasuredWidth());
	                drawSubLayout();
	            }
	        });
	        
			searchET.setFocusable(true);
			searchET.setFocusableInTouchMode(true);
			searchET.requestFocus();
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    private void drawSubLayout(){
		try{
			TableLayout tl = (TableLayout)findViewById(R.id.sorditemtbllayout2);
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			
			if(custActArrList != null){
				SalesProCustActConstraints stkCategory = null;
				String documentStr = "", postingDatStr = "", descStr = "";
                
                int rowSize = custActArrList.size();                
                SalesProCustActivityConstants.showLog("Stocks List Size  : "+rowSize);
                
                mattTxtView = new TextView[rowSize];
                
				for (int i =0; i < custActArrList.size(); i++) {
					stkCategory = (SalesProCustActConstraints)custActArrList.get(i);
                    if(stkCategory != null){
                    	documentStr = stkCategory.getObjectID().trim();
                    	descStr = stkCategory.getDescription().trim();
                    	postingDatStr = stkCategory.getPostingDate().trim();
                    	
                    	if(!postingDatStr.equalsIgnoreCase("")){
                    		postingDatStr = SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", postingDatStr);
                    	}
                    	
                        tr = new TableRow(this);
                        
                        mattTxtView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					mattTxtView[i].setText(documentStr);
    					mattTxtView[i].setWidth(headerWidth1);
    					mattTxtView[i].setId(i);
    					mattTxtView[i].setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								int id = view.getId();	
								showCustActDetailScreen(id);
							}	
                        }); 
    					mattTxtView[i].setGravity(Gravity.LEFT);
    					mattTxtView[i].setPadding(10,0,0,0);
    								
    					TextView postDatTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);					
    					postDatTxtView.setText(postingDatStr);
    					postDatTxtView.setWidth(headerWidth2);
    					postDatTxtView.setGravity(Gravity.LEFT);
    					postDatTxtView.setPadding(10,0,0,0);
    							
    					TextView descTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					descTxtView.setText(descStr);
    					descTxtView.setWidth(headerWidth3);
    					descTxtView.setGravity(Gravity.LEFT);
    					descTxtView.setPadding(10,0,0,0);
    					
    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
    						mattTxtView[i].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						postDatTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						descTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    					}
    					
    					tr.addView(mattTxtView[i]);
    					tr.addView(postDatTxtView);
    					tr.addView(descTxtView);
    					
    					if(i%2 == 0)
    						tr.setBackgroundResource(R.color.item_even_color);
			            else
			            	tr.setBackgroundResource(R.color.item_odd_color);
    					
    					tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                    }					
				}
			}
		}
		catch(Exception asgf){
			SalesProCustActivityConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
    
    
    private void showCustActDetailScreen(int selIndex){
        try{
        	SalesProCustActivityConstants.showLog("Selected index : "+selIndex);
        	boolean errFlag = false;
        	SalesProCustActConstraints stkCategory = null;
        	if(custActArrList != null){
	        	try {
	        		if(custActArrList.size() > selIndex){
	        			stkCategory = (SalesProCustActConstraints)custActArrList.get(selIndex);
						Intent intent = new Intent(this, SalesProCustDetailScreen.class);
						intent.putExtra("stkCategoryObj", stkCategory);
						intent.putExtra("contactId", mainContactId);
	        			intent.putExtra("customerId", mainCustomerId);
	        			intent.putExtra("contactFName", contactFName);
            			intent.putExtra("contactLName", contactLName);
            			intent.putExtra("customerName", customerName);
						startActivityForResult(intent, SapGenConstants.SALESORD_DETAIL_SCREEN);
	        		}
	        		else
	        			errFlag = true;
				} 
				catch (Exception e) {
					SalesProCustActivityConstants.showErrorLog(e.getMessage());
				}
        	}
        	else
    			errFlag = true;
        	
        	if(errFlag == true)
        		SalesProCustActivityConstants.showErrorDialog(this, "Customer Activity Detail Screen can't be shown");
        }
        catch(Exception assf){
        	SalesProCustActivityConstants.showLog("On showCustActDetailScreen : "+assf.toString());
        }
    }//fn showCustActDetailScreen
    
    
    private void initCustActListSoapConnection(){        
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
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]RECENT-INTERACTIONS-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTKEY[.]KUNNR[.]PARNR";
            C0[4].Cdata = "ZGSXCAST_CNTCTKEY[.]"+mainCustomerId+"[.]"+mainContactId;
//            C0[4].Cdata = "ZGSXCAST_CNTCTKEY[.]104[.]112";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesProCustActivityConstants.showLog(request.toString());
          
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SalesProCustActivityConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initCustActListSoapConnection
    
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
    		SalesProCustActivityConstants.showErrorLog("Error in doThreadNetworkAction : "+asgg.toString());
    	}
    }//fn doThreadNetworkAction
       
    final Runnable getNetworkResponseRunnable = new Runnable(){
	    public void run(){
	    	try{
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
    			if(pdialog != null)
        			pdialog = null;
        		
    			if(pdialog == null)
    				SalesProCustActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                        	pdialog = ProgressDialog.show(SalesProCustActivity.this, "", getString(R.string.WAIT_TEXTS),true);
                        	new Thread() {
                        		public void run() {
                        			try{
                        				updateServerResponse(resultSoap);
                        				sleep(2000);
                        			} catch (Exception e) {  }
                 				}
                        	}.start();
                        }
                    });
	    	} catch(Exception asegg){
	    		SalesProCustActivityConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
	    	}
	    }	    
    };
    
    /*private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	pdialog = ProgressDialog.show(ctxAct, "", "Please wait while processing...",true);
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
*/    
    public void updateServerResponse(SoapObject soap){		
        try{ 
        	if(soap != null){
    			SalesProCustActivityConstants.showLog("Count : "+soap.getPropertyCount());
    			
    			SalesProCustActConstraints custActCategory = null;
    			
    			emptyAllVectors();
	            	            	            
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_INTRCTN11")){
	                                if(custActCategory != null)
	                                	custActCategory = null;
	                                    
	                                custActCategory = new SalesProCustActConstraints(resArray);
	                                
	                                if(custActArrList != null)
	                                	custActArrList.add(custActCategory);	  
	                                
	                                if(custActCopyArrList != null)
	                                	custActCopyArrList.add(custActCategory);
	                            }
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SalesProCustActivityConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SalesProCustActivityConstants.showLog(taskErrorMsgStr);
	                                SalesProCustActivityConstants.showErrorDialog(this, taskErrorMsgStr);
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
        	stopProgressDialog();
        	SalesProCustActivityConstants.showLog("Customer Activity List Size : "+custActArrList.size());
        	Collections.sort(custActArrList, custActSortComparator);      
        	SalesProCustActivity.this.runOnUiThread(new Runnable() {
	            public void run() {	   
	            	initLayout();
	            }
	        });   
        }
    }//fn updateServerResponse     
    
    private void emptyAllVectors(){
        try{
            searchStr = "";
            
            if(custActArrList != null)
            	custActArrList.clear();
            
            if(custActCopyArrList != null)
            	custActCopyArrList.clear();
        }
        catch(Exception adsf){
        	SalesProCustActivityConstants.showErrorLog("On emptyAllVectors : "+adsf.toString());
        }
    }//fn emptyAllVectors
    
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
    
    public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		  SalesProCustActivityConstants.showLog("Text : "+s.toString());
		  searchItemsAction(s.toString());
	} 
	
	private void searchItemsAction(String match){  
        try{
            searchflag = true;           
            searchStr = match;
            SalesProCustActConstraints stkObj = null;
            String objectIdStr = "", descpStr = "", postDatStr = "";
            if((custActCopyArrList != null) && (custActCopyArrList.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){                                            
                    System.out.println("Match : "+match);  
                    custActArrList.clear();
                    for(int i = 0; i < custActCopyArrList.size(); i++){  
                        stkObj = null;
                        objectIdStr = "";
                        descpStr = "";
                        postDatStr = "";
                        stkObj = (SalesProCustActConstraints)custActCopyArrList.get(i);
                        if(stkObj != null){
                        	objectIdStr = stkObj.getObjectID().trim().toLowerCase();
                            descpStr = stkObj.getDescription().trim().toLowerCase();
                            postDatStr = stkObj.getPostingDate().trim().toLowerCase();
                            match = match.toLowerCase();
                            if((objectIdStr.indexOf(match) >= 0) || (descpStr.indexOf(match) >= 0) || (postDatStr.indexOf(match) >= 0)){
                            	custActArrList.add(stkObj);
                            }
                        }
                    }//for 
                    initLayout();
        			//searchET.setText(searchStr);
                }
                else{
                    System.out.println("Match is empty");
                    custActArrList.clear();
                    for(int i = 0; i < custActCopyArrList.size(); i++){  
                        stkObj = (SalesProCustActConstraints)custActCopyArrList.get(i);
                        if(stkObj != null){
                        	custActArrList.add(stkObj);
                        }
                    }
                    initLayout();
        			//searchET.setText(searchStr);
                }
            }//if
            else
                return;
        }//try
        catch(Exception we){
        	SalesProCustActivityConstants.showErrorLog("Error On searchItemsAction : "+we.toString());
        }
    }//fn searchItemsAction  
    
	
	private void sortItemsAction(int sortInd){
		try{
			 sortFlag = true;
			 sortIndex = sortInd;
			 
			 if(sortInd == sortHeader1)
				 sortDocFlag = !sortDocFlag;
			 else if(sortInd == sortHeader2)
				 sortPostDateFlag = !sortPostDateFlag;
			 else if(sortInd == sortHeader3)
				 sortDescFlag = !sortDescFlag;
			 
			 SalesProCustActivityConstants.showLog("Selected Sort Index : "+sortInd);
			 Collections.sort(custActArrList, custActSortComparator); 
				
             initLayout();
		}
		catch(Exception sfg){
			SalesProCustActivityConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	try {   			
    		SalesProCustActivityConstants.showLog("Request Code "+requestCode);
    		SalesProCustActivityConstants.showLog("Result Code "+resultCode);
    		SalesProCustActivityConstants.showLog("Result Code "+RESULT_OK);
    		
		} catch (Exception e) {
			SalesProCustActivityConstants.showErrorLog("onActivityResult : "+e.toString());
		}
    }//fn onActivityResult
	
	private final Comparator custActSortComparator =  new Comparator() {

        public int compare(Object o1, Object o2){ 
            int comp = 0;
            String strObj1 = "0", strObj2="0";
            SalesProCustActConstraints repOPObj1, repOPObj2;
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd"); 
            Date dateObj1 = null, dateObj2 = null;
            long timeMil1 = 0, timeMil2 = 0;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                	repOPObj1 = (SalesProCustActConstraints)o1;
                    repOPObj2 = (SalesProCustActConstraints)o2;
                    
                    if(sortIndex == sortHeader1){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getObjectID().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getObjectID().trim();
                        
                        if(sortDocFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }             
                    else if(sortIndex == sortHeader2){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getPostingDate().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getPostingDate().trim();
                        try{
	                        dateObj1 = curFormater.parse(strObj1); 
	                        dateObj2 = curFormater.parse(strObj2); 
	                        timeMil1 = dateObj1.getTime();
	                        timeMil2 = dateObj2.getTime();
	                        strObj1 = String.valueOf(timeMil1);
	                        strObj2 = String.valueOf(timeMil2);
                        }
                        catch(Exception sfg){}
                        /*
                        SalesProCustActivityConstants.showLog("Time : "+timeMil1+" : "+timeMil2);
                        if(sortPostDateFlag == true)
                            comp =  (int) (timeMil1-timeMil2);
                        else
                            comp =  (int) (timeMil2-timeMil1);
                        */
                        if(sortPostDateFlag == true)
                            comp =  strObj1.compareTo(strObj2);
                        else
                            comp =  strObj2.compareTo(strObj1);
                    }
                    else if(sortIndex == sortHeader3){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getDescription().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getDescription().trim();
                        
                        if(sortDescFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else{
                        // Code to sort by Material Desc (default)
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getObjectID().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getObjectID().trim();
                        
                        if(sortcustNoFlag == true)
                        	comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                }
             }
             catch(Exception qw){
            	 SalesProCustActivityConstants.showErrorLog("Error in Serv Order Comparator : "+qw.toString());
             }
                 
             if (comp != 0) {            
                return comp;
             } 
             else {            
                return 0;
            }
        }
    };

}//End of class SalesProCustActivity
