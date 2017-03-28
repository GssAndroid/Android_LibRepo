package com.globalsoft.SalesPro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProCustConstraints;
import com.globalsoft.SalesOrderLib.Database.SalesOrderCP;
import com.globalsoft.SalesOrderLib.Database.SalesOrderDBOperations;
import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Database.PriceListCP;
import com.globalsoft.SalesPro.Database.PriceListDBOperations;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class PriceListMainTablet extends ListActivity{
	private EditText customerET,custSearchET;		
	private ImageButton searchbtn, showcustsearchbtn,customrSearchbtn,backBtn;
	private LinearLayout searchLinear,custmorsearchlinear,headerLinear;
	private RelativeLayout selectrelativeLT;
	private Button showCustBtn,editTextdeleteBtn,priceTextDeleteBtn;
	private ListView listView;
	private TextView customerTV,custNameTV,soNoTV, myTitle;
	private CustomerListAdapter CustomerListAdapter;	   
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	private int stocksListCount = 0, offlineFlag=0;
    private String stocksListType = "", title="";
    private About abt = new About();
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	
	private String taskErrorMsgStr = "", custSearchStr,CustomerStr,mainCustomerId="",custname="",Custnumb="";
	//public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;
	
	
    //private Vector mattVector = new Vector();
    private Vector pricelistvector = new Vector();
    //private String[] custDetArr;
    private String[] priceDetArr;
    private ArrayList custDetArr = new ArrayList();
    private ArrayList custdialogArraylist = new ArrayList();
    
    private ArrayList<Boolean>  status =new ArrayList<Boolean>() ;
   
    private ArrayList priceArrSelctdList; 
    private PriceListAdapter PriceListAdapter;
    private boolean flag_pref = false ,soapConstantFunc;
    
	private static ArrayList priceList = new ArrayList();
	private static ArrayList getDBListarr = new ArrayList();
	private static ArrayList idStrSap = new ArrayList();
	private static ArrayList pricereadDBList = new ArrayList();
	private static ArrayList comparestr = new ArrayList();
	private ArrayList CustArrayList = new ArrayList();
	private boolean isConnAvail = false;
    private ArrayList mattlistcopy = new ArrayList();
	final Handler pricelistData_Handler = new Handler();
    
	static final int SOAP_CONN_CUSTOMER = 1;
    static final int SOAP_CONN_PRICE_LIST_VALUE = 2;
    private int selctdCust=0,selctdPos=0;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SalesOrderProConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_PLIST_TITLE1);
        setContentView(R.layout.pricelistmain);*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.pricelistmaintablet); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		 myTitle = (TextView) findViewById(R.id.myTitle);		
		//myTitle.setText(getResources().getString(R.string.SALESORDPRO_PLIST_TITLE1));

		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
		SalesOrderProConstants.showLog("dispwidth : "+dispwidth);
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}		
		 title= getResources().getString(R.string.SALESORDPRO_PLIST_TITLE1);
		 myTitle.setText(title);
		// OfflineFunc();	
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		mainCustomerId = this.getIntent().getStringExtra("customerId");
		if(mainCustomerId != null)
			mainCustomerId = mainCustomerId.trim();
		SharedPreferences settings = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);      
		flag_pref = settings.getBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, false);
		
        initLayout();
    }
    
    private void initLayout(){
		try{
			custSearchET = (EditText) findViewById(R.id.customerserchET);
			customerET = (EditText) findViewById(R.id.customerET);			
			
			//soNoTV = (TextView)findViewById(R.id.soNoTV);			
			custNameTV = (TextView)findViewById(R.id.custNameTV);		
			
			customrSearchbtn = (ImageButton) findViewById(R.id.customersearchbtn);
			customrSearchbtn.setOnClickListener(Customersrch_btnListener); 
			
			searchbtn = (ImageButton) findViewById(R.id.custsearchbtn);
			searchbtn.setOnClickListener(custsrch_btnListener); 
							
			showCustBtn = (Button) findViewById(R.id.showCustBtn);
			showCustBtn.setOnClickListener(showCustBtnListener); 			
			/*editTextdeleteBtn = (Button) findViewById(R.id.clr_edit_text);
			editTextdeleteBtn.setOnClickListener(showDeleteBtnListener); 
			
			priceTextDeleteBtn = (Button) findViewById(R.id.clr_price_edit_text);
			priceTextDeleteBtn.setOnClickListener(showpriceDeleteBtnListener); 
			*/			
			backBtn = (ImageButton) findViewById(R.id.showsearchbtn);
			backBtn.setOnClickListener(showBackbtnListener); 
			
			showcustsearchbtn = (ImageButton) findViewById(R.id.showcustsearchbtn);
			showcustsearchbtn.setOnClickListener(showCustBackbtnListener); 
					
			custmorsearchlinear = (LinearLayout) findViewById(R.id.custmorsearchlinear);
			//searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);
			headerLinear = (LinearLayout) findViewById(R.id.sorditem_srlheader_linear);
			headerLinear.setVisibility(View.GONE);
			
			searchLinear = (LinearLayout) findViewById(R.id.searchlinear);
			selectrelativeLT = (RelativeLayout) findViewById(R.id.showrelativeLT);
			selectrelativeLT.setVisibility(View.GONE);
			
			/*searchLinear = (LinearLayout) findViewById(R.id.lineardialog);			
			selectrelativeLT.setVisibility(View.GONE);*/
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);
			listviewcall();			
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
           
    public void onBackPressed() {
		this.finish();		
	}//fn onBackPressed
    
    private OnClickListener Customersrch_btnListener = new OnClickListener() {
        public void onClick(View v) {        	
        	if(custSearchET.getText().toString().trim().length() != 0) {
				try{
					CustomerStr = custSearchET.getText().toString().trim();
					getCustomerSearch(CustomerStr);
					SalesOrderProConstants.showLog("customer String : "+CustomerStr);
					/*//new	
					isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(PriceListMainTablet.this);//check for internet connection					
					if(isConnAvail!=false)
						initCustomerSoapConnection();
					else
						initDBConnection(custSearchStr);*/					
					}
				catch(Exception wsfsg){
					SalesOrderProConstants.showErrorLog("Error in Material Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "Enter Material name to search.");
        }
    };
    
    public void initCustomerDBConnection(String CustmerSrchStr){
    	CustArrayList = SalesOrderDBOperations.readAllCustomerIdDataFromDB(PriceListMainTablet.this,CustmerSrchStr);
		SalesOrderProConstants.showLog("info array size : "+CustArrayList.size());    	
    	prefillcustomerData();
    }//initCustomerDBConnection    
    
    private void getCustomerSearch(String custIdStr){
    	try{
    		custSearchStr = custIdStr;    		
    		SalesOrderProConstants.showLog("customer : "+custSearchStr);
			isConnAvail = SapGenConstants.checkConnectivityAvailable(PriceListMainTablet.this);//check for internet connection			
			if(isConnAvail!=false){				
				initCustomerSoapConnection();
			}		
			else
				initCustomerDBConnection(custIdStr);			
			/*if(!custSearchStr.equalsIgnoreCase(""))
				initCustomerSoapConnection();*/
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in getCustomerSearch : "+sfg.toString());
		}
	}//fn getCustomerSearch    
 
    private void setWindowTitle(final String title){
    	try{
    		this.setTitle(title);
    	}
    	catch(Exception sdf){
    		SalesOrderProConstants.showErrorLog("Error in setWindowTitle : "+sdf.getMessage());
    	}
    }//fn setWindowTitle
    
    private void showPriceListDetailScreen(){
    	try{
    		CustomerStr = custSearchET.getText().toString().trim();
    		Intent intent = new Intent(this, PriceListDetailScreenTablet.class);
        		//intent.putExtra("selMatIds", selMatVector);
        		intent.putExtra("selMatIds", priceArrSelctdList);
        		intent.putExtra("custname", custname);
        		intent.putExtra("custnumb", Custnumb);        		
    			startActivityForResult(intent,SalesOrderProConstants.PRICEVIEWLIST_DETAIL_SCREEN_TABLET);       		
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("Error in showSalesOrdCreationScreen : "+adf.getMessage());
    	}
    }//fn showPriceListDetailScreen
    
    private void listviewcall(){
		try {						
			if(status != null){
				status.clear();
			}
			for (int i = 0; i < priceList.size(); i++) {				
					status.add(i, false);									            
	        }
			setListAdapter(new PriceListAdapter(this));
		} catch (Exception ce) {
			SalesOrderProConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall    

    private OnClickListener showCustBtnListener = new OnClickListener(){
        public void onClick(View v) {        	
        	getMaterials();
        }
    };
    
    public void getSingleMaterial(){
    	if(priceArrSelctdList != null)
    		priceArrSelctdList.clear();
    	else
    		priceArrSelctdList = new ArrayList();      
    	if(priceList != null){
			SalesOrdProMattConstraints matobj=null;   
			matobj = ((SalesOrdProMattConstraints)priceList.get(0));
			priceArrSelctdList.add(matobj);
			if(priceArrSelctdList.size() > 0){
	    		showPriceListDetailScreen();
	    	}
	    	else
	    		SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "Material Id is empty!");
    	}
    }//getSingleMaterial
    
    public void getMaterials(){
    	SalesOrderProConstants.showLog("Show Cust btn clicked");
    	if(priceArrSelctdList != null)
    		priceArrSelctdList.clear();
    	else
    		priceArrSelctdList = new ArrayList();        	
    	if(listView != null){
    		int len = listView.getCount();
    		SalesOrderProConstants.showLog("List Count : "+len);        		
    		String mattIdStr = "";        		
    		if(priceList != null){
    			SalesOrdProMattConstraints matobj=null;        	
				for (int i = 0; i < len; i++){
					boolean getstatus = status.get(i).booleanValue();
					SalesOrderProConstants.showLog("getstatus : "+getstatus);				        	
					if (getstatus==true) {       				
						matobj = ((SalesOrdProMattConstraints)priceList.get(i));
						priceArrSelctdList.add(matobj);
						SalesOrderProConstants.showLog("priceArrSelctdList Count : "+priceArrSelctdList.size());       				
					}
				}
			}
		}	        	
    	if(priceArrSelctdList.size() > 0){
    		showPriceListDetailScreen();
    	}
    	else
    		SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "Material Id is empty!");
    
    }//getMaterials
    
    private OnClickListener showBackbtnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(searchLinear != null)
        		searchLinear.setVisibility(View.VISIBLE);        	
        	if(selectrelativeLT != null)
        		selectrelativeLT.setVisibility(View.GONE);        	
        	setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE1));
        }
    };
    
    private OnClickListener showpriceDeleteBtnListener = new OnClickListener() {
        public void onClick(View v) {
        	customerET.setText(" ");
        }
    };
    
    private OnClickListener showDeleteBtnListener = new OnClickListener() {
        public void onClick(View v) {
        	custSearchET.setText(" ");
        }
    };
    
    private OnClickListener showCustBackbtnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(custmorsearchlinear != null)
        		custmorsearchlinear.setVisibility(View.VISIBLE);        	
        	if(headerLinear != null)
        		headerLinear.setVisibility(View.GONE); 
        	custname = "";
        	Custnumb = "";        	
        	setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE1));
        }
    };    
    
    private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {        	
        	if(customerET.getText().toString().trim().length() != 0) {
				try{
					custSearchStr = customerET.getText().toString().trim();
					SalesOrderProConstants.showLog("customer : "+custSearchStr);
					//new	
					isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(PriceListMainTablet.this);//check for internet connection					
					if(isConnAvail!=false)
						initPriceSoapConnection();
					else
						initDBConnection(custSearchStr);					
				}
				catch(Exception wsfsg){
					SalesOrderProConstants.showErrorLog("Error in Material Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "Enter Material name to search.");
        }
    };
    
    private void initDBConnection(String idStr) {    	
		priceList = PriceListDBOperations.readAllSerchIdDataFromDB(PriceListMainTablet.this,idStr);
		SalesOrderProConstants.showLog("info array size : "+priceList.size());
	
		if(priceList.size()!=0)
			prefillPriceListData(); 		
		else {
			/*Toast toast = Toast.makeText(PriceListActivity.this, "Data Not available in Offline mode",
            Toast.LENGTH_LONG);
			toast.show();*/
			SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "Data Not available in Offline mode");
		}	
    }//initDBConnection
    
    private void initPriceSoapConnection(){        
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
            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            
            if(!flag_pref){           	
            	C0[2].Cdata = "EVENT[.]PRODUCT-SEARCH[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			}else{				
				C0[2].Cdata = "EVENT[.]PRODUCT-SEARCH[.]VERSION[.]0";
			}
                       
            C0[3].Cdata = "ZGSEVDST_MTRLSRCH01[.]"+custSearchStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderProConstants.showLog(request.toString());

            respType = SOAP_CONN_PRICE_LIST_VALUE;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL,SOAP_CONN_PRICE_LIST_VALUE);
        }
        catch(Exception asd){
        	SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initPriceSoapConnection
    
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
	    			if(respType == SOAP_CONN_CUSTOMER){
		    			if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(PriceListMainTablet.this, "", getString(R.string.COMPILE_DATA),true);
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
	    			}else if(respType == SOAP_CONN_PRICE_LIST_VALUE){
	    				if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(PriceListMainTablet.this, "", getString(R.string.COMPILE_DATA),true);
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
	    		}else{
	    			offlineFlag=1;
	    			//OfflineFunc();
	    			initDBConnection();
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
   /* public void OfflineFunc(){
    	if(offlineFlag==1){
    		SapGenConstants.checkInternetConnection(this, myTitle,title,resultSoap);	
    	}else{
    		SapGenConstants.checkInternetConnection(this,myTitle,title);
    	}    	  		
    }//OfflineFunc
*/    
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
            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0";
            C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+CustomerStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderProConstants.showLog(request.toString());

            respType = SOAP_CONN_CUSTOMER;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL,SOAP_CONN_CUSTOMER);
        }
        catch(Exception asd){
        	SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initPriceSoapConnection
    
    private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url,final int action){
    	try {
    		if(pdialog != null)
    			pdialog = null;
    		
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
                    public void run() {
                    	//pdialog = ProgressDialog.show(ctxAct, "", "Please wait while processing...",true);
                    	pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.SALESORDPRO_WAIT),true);
                    	new Thread() {
                    		public void run() {
                    			try{
                    				getSOAPViaHTTP(envelopeCE, url,action);
                    				sleep(2000);
                    			} catch (Exception e) {  }
             				}
                    	}.start();
                    }
                });
		} catch (Exception ae) {
			SalesOrderProConstants.showErrorLog(ae.toString());
		}
    }//fn startNetworkConnection
    
    private void getSOAPViaHTTP(SoapSerializationEnvelope envelopeCE, String url,final int action){		
        try {                
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (url);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	SalesOrderProConstants.showErrorLog("Data handling error : "+ex2);
            	SalesOrderProConstants.showErrorDialog(this, ex2.toString());
                envelopeCE = null;
                return;
            }
            catch(IOException oex){
                final String extStr = oex.toString();
                SalesOrderProConstants.showErrorLog("Network error : "+extStr);
                envelopeCE = null;
                return;
            }
            catch(Exception ex){
            	final String extStr = ex.toString();
            	SalesOrderProConstants.showErrorLog("Error in Sap Resp : "+ex.toString());
                final Context ctx = this;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                    	SalesOrderProConstants.showErrorDialog(ctx, extStr.toString());
                    }
                });
                envelopeCE = null;
                return;
            }
            
            if(envelopeCE != null){
                try{
                	String result = (envelopeCE.getResponse()).toString();
                	SalesOrderProConstants.showLog("Results : "+result);
                    
                    SoapObject result1 = (SoapObject)envelopeCE.bodyIn; 
                    SalesOrderProConstants.showLog("Results1 : "+result1.toString());
                    
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                }
                catch(Exception dgg){
                	SalesOrderProConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	SalesOrderProConstants.showErrorLog("Error in Soap Conn : "+e.toString());
        }
        finally {                     
        	SalesOrderProConstants.showLog("========END OF LOG========");    
            stopProgressDialog();
            this.runOnUiThread(new Runnable() {
                public void run() {
                	//updateServerResponse(resultSoap);
                	if(action == SOAP_CONN_CUSTOMER)
                		updateCustomerServerResponse(resultSoap);
                	else if(action == SOAP_CONN_PRICE_LIST_VALUE)                		
                		updateServerResponse(resultSoap);
                }
            });
        }
    }//fn getSOAPViaHTTP
    
    public void updateCustomerServerResponse(SoapObject soap){		
        try{ 
        	if(soap != null){
        		SalesOrderProConstants.showLog("Count : "+soap.getPropertyCount());
    			SalesOrdProCustConstraints custCategory = null;
    			    			
    			if(CustArrayList != null)
    				CustArrayList.clear();
	            	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[40];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SalesOrderProConstants.showLog("propsCount : "+propsCount);
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
	                                
	                                if(CustArrayList != null)
	                                	CustArrayList.add(custCategory);
	                            }
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SalesOrderProConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SalesOrderProConstants.showLog(taskErrorMsgStr);
                                    this.runOnUiThread(new Runnable() {
                                        public void run() {
                                        	SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, taskErrorMsgStr);
                                        }
                                    });
	                            }
	                        }
	                    }
	                }
	            }//for
        	}/*else{
        		initDBConnection(custSearchStr);	
        	}*/
        }
        catch(Exception sff){
        	SalesOrderProConstants.showErrorLog("On updateServerResponse : "+sff.toString()); 
            stopProgressDialog();
        } 
        finally{ 
            PriceListMainTablet.this.runOnUiThread(new Runnable() {
                public void run() {
                	Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				SalesOrderProConstants.showLog("custArrayList Size : "+CustArrayList.size());
	                        	if(CustArrayList.size() > 0){
	                        		PriceListMainTablet.this.runOnUiThread(new Runnable() {
	                                    public void run() {
			                            	prefillcustomerData();
			                            	SalesOrderDBOperations.deleteAllTableDataFromDB(PriceListMainTablet.this, SalesOrderCP.SO_CUST_CONTENT_URI);
			                            	insertCustDataintoDB();
			                            }
	                                });  
	                        	}else{
	                                stopProgressDialog();
	                        		SalesOrderProConstants.showErrorDialog(PriceListMainTablet.this, "No results available. Try a different name!");
	                        	}
                			} catch (Exception e) {
                				ContactsConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
                			}
                			stopProgressDialog();
        				}
        			};
        	        t.start();
                	
                }
            });   			
        }       
    }//fn updateServerResponse     
    
    private ArrayList getcustomerList(){
    	String choices[] = null;
        try{
        	if(custDetArr!=null)
        		custDetArr.clear();
            if(CustArrayList != null){
            	SalesOrdProCustConstraints empObj = null;
                String nameStr = "", idstr="", combStr="";
                
                int arrSize = CustArrayList.size();
                choices = new String[arrSize];
                
                for(int h=0; h<arrSize; h++){
                    empObj = (SalesOrdProCustConstraints)CustArrayList.get(h);
                    if(empObj != null){
                    	nameStr = empObj.getName().trim();
                    	idstr = empObj.getCustomerNo().trim();
                        combStr = nameStr+":"+idstr;
                       
                        //choices[h] = combStr;
                        custDetArr.add(combStr);
                    }
                }
            }
            SalesOrderProConstants.showLog("Size of choices : "+choices.length);
        }
        catch(Exception sfg){
        	SalesOrderProConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        return custDetArr;
    }//fn getCustomerList
    
    private void prefillcustomerData(){
    	try{
    		SalesOrdProCustConstraints custCategory=null;
    		if(CustArrayList != null){
    			if(CustArrayList.size() > 0){
    	    		custDetArr = getcustomerList();
    	    		if(custDetArr != null){
    	    			 if(custDetArr.size()>1) {
    	    				 showAlert1();
    	 	        	}else{    	 	        		
    		        		 custCategory=(SalesOrdProCustConstraints)CustArrayList.get(0);    	 
    		            	 custname=custCategory.getName().toString();
    		            	 Custnumb=custCategory.getCustomerNo().trim();
    		            	 
    		            	 if(custmorsearchlinear != null)
    		            		 custmorsearchlinear.setVisibility(View.GONE);
    		            	 
    		            	 if(headerLinear != null)
    		            		 headerLinear.setVisibility(View.VISIBLE);
    		            	 
    		            	 if(custNameTV != null)
    		            		 custNameTV.setText(" "+custname+" "+"("+Custnumb+")");
    	 	        	}
    	    			   	    			
    	    		}
    	    		else
    	    			SalesOrderProConstants.showErrorDialog(this, "No results available. Try a different name!");
    			}
    			else
    				SalesOrderProConstants.showErrorDialog(this, "No results available. Try a different name!");
    		}
    		else
    			SalesOrderProConstants.showErrorDialog(this, "No results available. Try a different name!");
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData    

	public void getCustomerString(int pos){
		SalesOrdProCustConstraints custCategory=null;
		custCategory=(SalesOrdProCustConstraints)CustArrayList.get(pos);    	 
		custname=custCategory.getName().toString();
		Custnumb=custCategory.getCustomerNo().trim();
		/*String street=custCategory.getStreet().toString();
		String region=custCategory.getRegion().toString();
		String country=custCategory.getCountry().toString();
		*/
		if(custmorsearchlinear != null)
		 custmorsearchlinear.setVisibility(View.GONE);

		if(headerLinear != null)
		 headerLinear.setVisibility(View.VISIBLE);

		if(custNameTV != null)
		 custNameTV.setText(" "+custname+" "+"("+Custnumb+")");    	
    }//
   
    private void insertCustDataintoDB() {
    	SalesOrdProCustConstraints custCategory;
    	try {
			if(CustArrayList != null){
				for(int k=0; k<CustArrayList.size(); k++){
					custCategory = (SalesOrdProCustConstraints)CustArrayList.get(k);
					if(custCategory != null)
						SalesOrderDBOperations.insertCustomerDataInToDB(PriceListMainTablet.this,custCategory);
				}
			}			 
            //stopProgressDialog();
		} catch (Exception ewe) {
			SalesOrderProConstants.showErrorLog("Error On inertCustDataintoDB: "+ewe.toString());
		}		
		
	}    
	
	public void updateServerResponse(SoapObject soap){		
		SalesOrdProMattConstraints mattCategory = null;
		try{ 
			if(soap != null){  
				/*if(mattVector != null)
					mattVector.removeAllElements();*/				
				if(priceList != null)
					priceList.clear();					
				String delimeter = "[.]", result="", res="", docTypeStr = "";
				SoapObject pii = null;
				String[] resArray = new String[37];
				int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;  
				for (int i = 0; i < soap.getPropertyCount(); i++) {	                
				   pii = (SoapObject)soap.getProperty(i);
				   propsCount = pii.getPropertyCount();
				   SalesOrderProConstants.showLog("propsCount : "+propsCount);
				   if(propsCount > 0){
					   for (int j = 0; j < propsCount; j++) {
						SalesOrderProConstants.showLog(j+" : "+pii.getProperty(j).toString());
							if(j > 1 && j <= 2){
								result = pii.getProperty(j).toString();
								firstIndex = result.indexOf(delimeter);
								eqIndex = result.indexOf("=");
								eqIndex = eqIndex+1;
								firstIndex = firstIndex + 3;
								docTypeStr = result.substring(eqIndex, (firstIndex-3));
								result = result.substring(firstIndex);
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
											if(docTypeStr.equalsIgnoreCase("ZGSEMMST_MTRL10")){
												//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
												stocksListCount = Integer.parseInt(rowCount);
												stocksListType = respType;
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
								//ServiceProConstants.showLog("Document Type : "+docTypeStr);
								//ServiceProConstants.showLog("Result : "+result);

								resC = 0;
								indexA = 0;
								indexB = result.indexOf(delimeter);
								while (indexB != -1) {
								   res = result.substring(indexA, indexB);
								   //ServiceProConstants.showLog(resC+" : "+res);
								   resArray[resC] = res;
								   indexA = indexB + delimeter.length();
								   indexB = result.indexOf(delimeter, indexA);
								   resC++;
								}
							   
								int endIndex = result.lastIndexOf(';');
								resArray[resC] = result.substring(indexA, endIndex);
								//ServiceProConstants.showLog(resC+" : "+resArray[resC]);
							   
								if(docTypeStr.equalsIgnoreCase("ZGSEMMST_MTRL10")){
									if(mattCategory != null)
										mattCategory = null;
										
									mattCategory = new SalesOrdProMattConstraints(resArray);
									
								   /* if(mattVector != null)
										mattVector.addElement(mattCategory);
									*/
									if(priceList != null)
										priceList.add(mattCategory);
									
									if(mattlistcopy != null)
										mattlistcopy.add(priceList);
								}                              
							}	
							else if(j == 0){
							   String errorMsg = pii.getProperty(j).toString();
							   //ServiceProConstants.showLog("Inside J == 0 ");
							   int errorFstIndex = errorMsg.indexOf("Message=");
							   if(errorFstIndex > 0){
								   int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
								   String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
								   SalesOrderProConstants.showLog(taskErrorMsgStr);
								   SalesOrderProConstants.showErrorDialog(this, taskErrorMsgStr);
								}
							}
						}
					}
				}
			}
			/*else{
				initDBConnection();
			}*/
		}
		catch(Exception sff){
			SalesOrderProConstants.showErrorLog("On updateTaskResponse : "+sff.toString());
		} 
		finally{
			SalesOrderProConstants.showLog("Stocks List Size : "+priceList.size());
			//prefillCustomerData();       		        
			try{      						
				if(priceList != null){
	    			if(stocksListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((stocksListCount == 0) && (priceList.size() == 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SER_CONTENT_URI);
	        			}
	    				else if((stocksListCount > 0) && (priceList.size() > 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SER_CONTENT_URI);
	    					insertSerchdDataIntoDB();
	        			}
	    			}
	    			if(stocksListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
	    				
	    				/*try {
	    		    		Thread t = new Thread() 
	    					{
	    			            public void run() 
	    						{
	    		        			try{
	    		        				deleteSelctdData();
	    		        			}catch(Exception e1){
	    		        				SalesOrderProConstants.showErrorLog("Error in deleteSelctdData Thread:"+e1.toString());
	    		        			}
	    		        			pricelistData_Handler.post(insertSerchdDataIntoDB);
	    		        		}
	    			            
	    					};
	    			        t.start();
	    		       	}   catch(Exception adsf1){
	    		         	SalesOrderProConstants.showErrorLog("On updateServerResponse : "+adsf1.toString());
	    		         }
	    		    	stopProgressDialog();   */
	    				
	    				if((stocksListCount == 0) && (priceList.size() == 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SER_CONTENT_URI);
	        			}
	    				else if((stocksListCount > 0) && (priceList.size() == 0)){
	    					
	        			}
	    				else if((stocksListCount > 0) && (priceList.size() > 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SER_CONTENT_URI);
	    					insertSerchdDataIntoDB();
	        			}
	    			}
	    		}	    		
	    		if(priceList.size() > 0){
					SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, true);    
	    			editor.commit();
				}   			
			} catch (Exception esf) {
				SalesOrderProConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			}
			stopProgressDialog();
			PriceListMainTablet.this.runOnUiThread(new Runnable() {
	            public void run() {
	            	initDBConnection();
	            }
	        });
		}
	}//fn updateReportsConfirmResponse     
	
	public void initDBConnection(){
		try {
			priceList = PriceListDBOperations.readAllSerchDataFromDB(PriceListMainTablet.this);
			SalesOrderProConstants.showLog("info array size : "+priceList.size());
		}catch (Exception esf1) {
			SalesOrderProConstants.showErrorLog("On initDBConnection : "+esf1.toString());
		}	 
		finally{
        	try {
        		prefillPriceListData();
			} catch (Exception e) {}
		}
	}//initDBConnection
   
    public void deleteSelctdData(){
       	try{
       		SalesOrdProMattConstraints matObj = null;
   		    boolean matchStr=false;
   		    if(idStrSap.size()>0){
   		    	idStrSap.clear();
   		    }
   		    if(pricereadDBList.size()>0){
   		    	pricereadDBList.clear();
   		    }
   		    pricereadDBList=PriceListDBOperations.readAllSerchDataFromDB(this);
   		    for(int i=0;i<priceList.size();i++){
   		    	matObj=(SalesOrdProMattConstraints)priceList.get(i);
   		    	if(matObj!=null)
   		    		idStrSap.add(matObj.getMaterialNo().trim());
       		}
       		if((priceList.size())>0 && (pricereadDBList.size())>0){
       			getDBListarr=PriceListDBOperations.getDBlist(this);
               	SalesOrderProConstants.showLog("getDBListarr:"+getDBListarr.size());
               	if(getDBListarr.size()>0){
   					for(int i=0;i<idStrSap.size();i++){
   						matchStr = getDBListarr.contains(idStrSap.get(i).toString().trim());
   						if(matchStr==true){
   							PriceListDBOperations.deleteSerchIdTableDataFromDB(this,PriceListCP.PL_SER_CONTENT_URI, idStrSap.get(i).toString().trim());
   		        			SalesOrderProConstants.showLog(" ID from SAP : "+idStrSap.get(i).toString().trim());
   		        		}
   	         			 SalesOrderProConstants.showLog(" matchStr : "+matchStr);
   	    			}
               	}
       		}
       	} catch (Exception e) {
   			SalesOrderProConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
   		}
    }

  /* 	final Runnable insertSerchdDataIntoDB = new Runnable(){
   	    public void run()
   	    {
   	    	try{
   				insertSerchdDataIntoDB();
   	    	} catch(Exception sfe){
   	    		SalesOrderProConstants.showErrorLog("Error in insertSerchdDataIntoDB:"+sfe.toString());
   	    	}
   	    }	    
   	};   	*/
   
    //new
    private void insertSerchdDataIntoDB() {
    	SalesOrdProMattConstraints stkCategory;
    	try {
			if(priceList != null){
				for(int k=0; k<priceList.size(); k++){
					stkCategory = (SalesOrdProMattConstraints) priceList.get(k);
					if(stkCategory != null){
						PriceListDBOperations.insertSerchdDataInToDB(this, stkCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SalesOrderProConstants.showErrorLog("Error On insertSerchdDataIntoDB: "+ewe.toString());
		}		
	}// insertSerchdDataIntoDB
    
    private void prefillPriceListData(){
    	try{    		
    		if(priceList != null){
    			if(priceList.size() > 0){
    				priceDetArr = getPriceList();
    	    		if(priceDetArr != null){
    	    			 if(priceDetArr.length>1) {
    	    				 if(searchLinear != null)
    	    	            		searchLinear.setVisibility(View.GONE);
    	    	    			
    	    	    			if(custmorsearchlinear != null)
    	    	    				custmorsearchlinear.setVisibility(View.GONE);
    	    	    			
    	    	            	if(selectrelativeLT != null)
    	    	            		selectrelativeLT.setVisibility(View.VISIBLE);
    	    	            	
    	    	            	if(showCustBtn != null)
    	    	            		showCustBtn.setVisibility(View.VISIBLE);  
    	    	            	//showAlert1();
    	    	            	listviewcall();   
    	 	        	}else{
    	 	        		getSingleMaterial();
    	 	        	}
    	    				            	
    	    		}
    	    		setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE2));
    			}
    		}
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
      
    public void showAlert1(){       	   	
    	/*if(status != null){
			status.clear();
		}
		for (int i = 0; i < custDetArr.size(); i++) {				
				status.add(i, false);									            
        }		*/
		showCustomerAlert();
    	
	}//fn showAlert    
    
    public void showCustomerAlert(){
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
					getCustomerString(selctdPos);
					
					alertDialog.dismiss();
				}
			});
			alertDialog = builder.create();    		  
			alertDialog.show();    		 
    	}catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in showAlert : "+sfg.toString());
    	}
	}//
    
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
   
    private void backButton(){
    	if(searchLinear != null)
    		searchLinear.setVisibility(View.VISIBLE);
    	setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE1));
    }
   
    public class PriceListAdapter extends BaseAdapter {    	
    	LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
    	public PriceListAdapter(Context context) {
    		mInflater = LayoutInflater.from(context);   
        }
		
        public int getCount() {
        	try {
				if(priceList != null)
					return priceList.size();
			}
        	catch (Exception e) {
        		SalesOrderProConstants.showErrorLog(e.getMessage());
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
            	TextView matnumb;
            	TextView matdesc;            	
                LinearLayout llitembg;
            }
            
            ViewHolder holder;
            convertView = mInflater.inflate(R.layout.pricelist_checkbox_list, null);
            holder = new ViewHolder();
            holder.matnumb = (TextView) convertView.findViewById(R.id.matnumb);   
            holder.matdesc = (TextView) convertView.findViewById(R.id.matdesc); 
            holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);
            /*if (convertView == null) {
                convertView = mInflater.inflate(R.layout.checkbox_list, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.cname = (TextView) convertView.findViewById(R.id.cname);    
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);             
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }*/

            if(position%2 == 0)
				holder.llitembg.setBackgroundResource(R.color.item_even_color);
			else
				holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            try {
            	if(priceList != null){
            		SalesOrdProMattConstraints stkCategory=null;            	
            		stkCategory = ((SalesOrdProMattConstraints)priceList.get(position));                	
                	if(stkCategory != null){
                		try {
                			String matnumb="",matdesc="",data="";
                			matnumb = stkCategory.getMaterialNo().toString().trim();  
    						//docNoStr = stkCategory.getMaterialDesc().toString().trim();
    						
                            holder.matnumb.setText(matnumb);
                            
                            if(stkCategory.getMaterialDesc().toString().length() > 0){
                        		data +=stkCategory.getMaterialDesc().toString();
                        	}
                            
                        	 holder.matdesc.setText(data);
    						
    					} catch (Exception defe) {
    						SalesOrderProConstants.showErrorLog("Error :"+defe.toString());
    					}     
					}
				
                	CheckBox cbox = (CheckBox) convertView.findViewById(R.id.ckbox);            	
                	cbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){					
						public void onCheckedChanged(CompoundButton arg0, boolean arg1) {					
							SalesOrderProConstants.showLog("boolean "+arg1);
							status.set(position, arg1);	
							/*if(arg1==true)
							cbox.setChecked(arg1);*/
						}
					});     
					if(status.get(position)==true){
						cbox.setChecked(true);
					}else
						cbox.setChecked(false);
					/*holder.llitembg.setOnClickListener(new OnClickListener() { 					 
						@Override 
						public void onClick(View v) {
							SalesOrderConstants.showLog("boolean cbox"+position);	
							SalesOrderConstants.showLog("cbox "+cbox.isChecked());
							SalesOrderConstants.showLog("status cbox"+status.get(position));
							boolean cbval = status.get(position).booleanValue();
							if(cbval==false){
								cbox.setChecked(true);
								status.set(position, true);	
								removeAllTasks();
							}else{
								cbox.setChecked(false);
								status.set(position, false);			
								removeAllTasks();
							}
							
						} 
					}); */					
				} 
            }catch (Exception qw) {
            	SalesOrderProConstants.showErrorLog("Error in ContactListAdapter : "+qw.toString());
			}            
           return convertView;	           
		}
		
		public void removeAllTasks() {
            notifyDataSetChanged();
        } 		
    }//End of ContactListAdapter
    
    OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			try{
				CheckBox box = (CheckBox)view.findViewById(R.id.ckbox);
                boolean state = status.get(position).booleanValue();
                SalesOrderProConstants.showLog("state on item click:"+state);
                SalesOrderProConstants.showLog("position on item click:"+position);
                                
                if(state==false){
                	box.setChecked(true);
					status.set(position, true);		
                }
                else{
                	box.setChecked(false);
					status.set(position, false);		
                }
			}catch (Exception dee) {
				SalesOrderProConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}
    };
    
    private String[] getPriceList(){
    	String choices[] = null;
        try{
            if(priceList != null){
            	SalesOrdProMattConstraints matObj = null;
                String nameStr = "", idstr="", combStr="";
                
                int arrSize = priceList.size();
                choices = new String[arrSize];
                
                for(int h=0; h<arrSize; h++){
                	matObj = ((SalesOrdProMattConstraints)priceList.get(h));
                    if(matObj != null){
                    	idstr = matObj.getMaterialDesc().trim();
                    	//nameStr = matObj.getMaterialUnit().trim();
                    	nameStr = matObj.getMaterialNo().trim();
                        combStr = idstr+":"+nameStr;
                        if(combStr.length() > 22)
                        	combStr = combStr.substring(0,22);
                       
                        choices[h] = combStr;
                    }
                }
            }
        }
        catch(Exception sfg){
        	SalesOrderProConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        System.out.println("Size of choices : "+choices.length);
        return choices;
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
			SalesOrderProConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
}//