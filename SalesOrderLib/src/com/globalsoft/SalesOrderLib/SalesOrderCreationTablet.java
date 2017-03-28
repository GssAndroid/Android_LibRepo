package com.globalsoft.SalesOrderLib;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesOrderLib.Database.SalesOrderCP;
import com.globalsoft.SalesOrderLib.Database.SalesOrderDBOperations;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.AppLocationService;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesOrderCreationTablet extends Activity {	
	private TextView[] valueTxtView ,materialTxtView, salesOrderTblHeadLblTV;
	private EditText[] qtyEditText;
	private TextView[] valTV;
	private ImageButton[] deleteBMF;
	private TextView[] headLblTV;
	private TextView reqBy_date, poDateTV;
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5, tableHeaderTV6, tableHeaderTV7;	
	private TextView custNameTV, custAddressTV ,custLabelTV ,socustnameTV, myTitle;
	private EditText customerET, soValueET,ponumb,poDateET,materialET;		
	private ImageButton custbackbtn, custSearchBtn,matSearchBtn ,custnamebackbtn;
	private Button addItemBtn, getPriceBtn, saveSoBtn;
	private LinearLayout searchLinear, headerLinear ,searchLinear2,showLayout ,custnameLinear,sapsendLayout,sovaluelayout, error_header_linear;
	private TableLayout tableLayout;
	private RelativeLayout relativeLayout,bodylayout;
	private String custListType = "";
	private String serverErrorMsgStr = "",metaLabel = "";
	private int idvalue;
    private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	int cust=0,prod =0, backflag=0, priceupdate=0;
	private int custListCount = 0;	
	private int dispwidth = 300;
	
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	AppLocationService appLocationService;
 	private double latitide,longitude;
	// date and time
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    
    private int POstartYear;
    private int POstartMonth;
    private int POstartDay;
    private int POstartHour;
    private int POstartMinute;
    
    static final int START_DATE_DIALOG_ID = 1;
    static final int START_TIME_DIALOG_ID = 2; 
    static final int PO_DATE_DIALOG_ID = 3;    
    
    private String taskErrorMsgStr = "", custDetStr, custAddStr, customerIdStr="", custSearchStr,refID = "", qtyS = "", title="",editStr="",totalPriceStr = "";
    String itemStr = "", mattDescStr = "", qtyStr = "", valueStr = "", unitStr = "", curncyStr = "",quantyStr="" ,nameStr = "", idstr="", combStr="";
    
    private boolean diagdispFlag = false, isConnAvail = false, contactFlag = false,internetAccess=false;
    private String custID ="",custIDcopy= " ";
    private CustomerListAdapter CustomerListAdapter;	
    private boolean flag,flag2, soapConstantFunc;
    private HashMap<String, String>  hashmap = new HashMap<String,String>(); 
    HashMap<String, String> custMap = null;
    HashMap<String, String> custDetArrSO = null;    
    private boolean tableExits = false,notAlertStr;
    final Handler productsData_Handler = new Handler();
    HashMap<String, String> labelMap = null;
    private ArrayList priceList = new ArrayList();
    private HashMap<String, String> proPriceMap = null;
	private HashMap<String, String> proTotPriceMap = null;
    
    private ArrayList<String> MatNoStringList=  new ArrayList<String>();
    private int mainPriceListCount = 0;	
	private String mainPriceListType = "";
	private int productsListCount = 0,colId;	
	private String productsListType = "";
	//private DataBasePersistent dbObjUIConf = null;
	
	private ArrayList mainPriceList = new ArrayList();	
	private ArrayList custdatatypeList = new ArrayList();		
	private ArrayList createScreenList = new ArrayList();		
	private ArrayList metaMainPriceArray = new ArrayList();	
    private ArrayList metaProdListArray = new ArrayList();
    private ArrayList custArrList = new ArrayList();
    private DataBasePersistent dbObj = null, dbObjColumns = null,dbObjUIConf=null;
    private ArrayList<String> MatNoUnitSoapList=  new ArrayList<String>();
    private HashMap<String, String> editTextMap = new HashMap<String, String>();	
    private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7;
    
    //private Vector custArrayList = new Vector();
    //private ArrayList custArrayList = new ArrayList();
    private Vector priceHeadVector = new Vector();
    private Vector priceItemVector = new Vector();
    //private ArrayList custDetArrSO = new ArrayList();
    private ArrayList CommonMattList = new ArrayList();
	private ArrayList<String> QtyList = new ArrayList<String>();   
    private ArrayList<String> MatrlcopyList = new ArrayList<String>();
    private ArrayList stocksSelectdItemArrList1 = new ArrayList();
    private ArrayList stocksArrListOffline = new ArrayList();
    private ArrayList valArrayList1 = new ArrayList();
    private ArrayList custDetArr = new ArrayList();
    private ArrayList sovalArrayList= new ArrayList();
    private ArrayList soLabelArrayList = new ArrayList();
    private ArrayList valArrayList = new ArrayList();
    private ArrayList Labelslist = new ArrayList();
    private ArrayList selMattArrList = new ArrayList();
    private  ArrayList<String>  PriceSoapList = new ArrayList<String>();
    private ArrayList mattItemArrLst = new ArrayList();    
    static final int SOAP_CONN_CUSTOMER = 1;
    static final int SOAP_CONN_PRICEVALUE = 2;   
    static final int SOAP_CONN_SOCREATION = 3; 
    private int multListviewFlag = -1, selctdPos = 0, copyflag = 0, socrtFlag=0, offlineFlag=0, custsearchflag=0, matsrchflag=0,addflag =0;
    
    private String mainContactId = "", uniqId=" ",mainCustomerId = "", poNumbStr = "", poDateStr = "", custIdStr="", clickedMatId="", dateStr= "",MaterialNoSO = "",errMsg=" ";
    private Vector qtyArr = new Vector();
    //private SalesOrderMainScreenTablet soapObj = new SalesOrderMainScreenTablet();
      
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.salesordercreate); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			 myTitle = (TextView) findViewById(R.id.myTitle);
			//myTitle.setText(getResources().getString(R.string.SALESPRO_LAUNCHMENU_CREATESO));
			 bodylayout = (RelativeLayout) findViewById(R.id.sobodylayout);
			 setTitleValue(); 
			SalesOrderConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
			if(SalesOrderConstants.APPLN_NAME_STR == null || SalesOrderConstants.APPLN_NAME_STR.length() == 0){
				SalesOrderConstants.APPLN_NAME_STR = "SALESPRO";
			}
			
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			isConnAvail = SapGenConstants.checkConnectivityAvailable(this);
			//CHECK FOR QUEUEPROCESSOR NOTIFICATIONS																													
			
			notAlertStr = this.getIntent().getBooleanExtra("fromNotification",notAlertStr);
			
			SapGenConstants.showLog("notAlertStr: "+notAlertStr);
			if(notAlertStr==true){	
				uniqId = this.getIntent().getStringExtra("custID");
				SapGenConstants.showLog("uniqId after notification: "+uniqId);
				colId = this.getIntent().getIntExtra(SapGenConstants.QUEUE_COLID, -1);
				errMsg = this.getIntent().getStringExtra(SapGenConstants.QUEUE_ERR_MSG);
				refID = this.getIntent().getStringExtra(SapGenConstants.QUEUE_ERR_APPREFID);
				
				SapGenConstants.showLog("errMsg:"+errMsg);
				SapGenConstants.showLog("refID:"+refID);
				SapGenConstants.showLog("uniqId:"+uniqId);
				final Calendar c = Calendar.getInstance();
		        //initialize for start date
		        startYear = c.get(Calendar.YEAR);
		        startMonth = c.get(Calendar.MONTH);
		        startDay = c.get(Calendar.DAY_OF_MONTH);
		        startHour = c.get(Calendar.HOUR_OF_DAY);
		        startMinute = c.get(Calendar.MINUTE);
		        //updateDisplay_start_date();	        
		        POstartYear = c.get(Calendar.YEAR);
		        POstartMonth = c.get(Calendar.MONTH);
		        POstartDay = c.get(Calendar.DAY_OF_MONTH);
		        POstartHour = c.get(Calendar.HOUR_OF_DAY);
		        POstartMinute = c.get(Calendar.MINUTE);	       
				//initCustErorDBConnection(refID);
				createScreenList=SalesOrderDBOperations.readAllCreateItemDataFromDB(this,uniqId);
				SalesOrderConstants.showLog("createScreenList: "+createScreenList.size());
				if(stocksSelectdItemArrList1!=null)
					stocksSelectdItemArrList1.clear();
				stocksSelectdItemArrList1.addAll(createScreenList);
				initItemHeadLayout();	
				sapsendLayout = (LinearLayout) findViewById(R.id.showlinear);		
		        sapsendLayout.setVisibility(View.VISIBLE);	      
		        sapsendLayout.removeAllViews();	
		        displayUI(DBConstants.EDIT_ACTION_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, sapsendLayout);
				//SalesOrderDBOperations.deleteAllTableDataFromDB(this, SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI);		        	
	        	SapGenConstants.stopNotificationAlert(colId);	
	        	/*Intent sendIntent = new Intent();
        		ComponentName  cn = new ComponentName("com.globalsoft.SapQueueProcessor.Receiver", "com.globalsoft.SapQueueProcessor.Receiver.SapQueueProcessorNWReceiver" );
            	SapGenConstants.showLog("from sales order background service");
				Intent sent = sendIntent.setComponent(cn);
				SapGenConstants.showLog("sent to sendintent"+sent);
				String text = "20";
				 int integersec = Integer.parseInt(text.toString());				
				    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, sendIntent, 0);	
				    SapGenConstants.showLog("pendingIntent"+pendingIntent);
				    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);				  
				    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				        + (integersec * 1000), pendingIntent);    			  
				    Toast.makeText(this, "Alarm set in " + integersec + " seconds",
				        Toast.LENGTH_LONG).show();   */    
				//this.startService(sendIntent);
			}else{
				//SalesOrderDBOperations.deleteAllTableDataFromDB(this, SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI);	
				mainContactId = this.getIntent().getStringExtra("contactId");
				mainCustomerId = this.getIntent().getStringExtra("customerId");
				if(mainCustomerId != null){
					mainCustomerId = mainCustomerId.trim();
					contactFlag = true;
					//getCustomerSearch(mainCustomerId);
				}
				if(mainContactId != null)
					mainContactId = mainContactId.trim();				
				SalesOrderConstants.showLog("mainContactId: "+mainContactId);
				SalesOrderConstants.showLog("mainCustomerId: "+mainCustomerId);
				
				flag=(Boolean)this.getIntent().getBooleanExtra("flag", flag);
				SalesOrderConstants.showLog("flag: "+flag);			
				if(flag == true){					
					if(custDetArrSO!=null)
						custDetArrSO.clear();
					
					if(stocksSelectdItemArrList1!=null)
						stocksSelectdItemArrList1.clear();
					if(MatNoStringList!=null)
						MatNoStringList.clear();
					
					MatrlcopyList.clear();
					custDetArrSO = (HashMap)this.getIntent().getSerializableExtra("selMatIds");	
					if(custMap!=null)
		    			custMap.clear();
					SalesOrderConstants.showLog("custDetArrSO: "+custDetArrSO);
					stocksSelectdItemArrList1 = (ArrayList)this.getIntent().getSerializableExtra("soitemdetails");	
					//MatrlcopyList.addAll(stocksSelectdItemArrList1);
					copyflag = 1;				
					initLayout2();								
				}else{
					initLayout();
				}
			}										
        } catch (Exception de) {
        	SalesOrderConstants.showErrorLog(de.toString());
        }
    }	//
	
	 /*private void initCustErorDBConnection(String customerIdStr){
			try{				            
				if(dbObj != null)
					dbObj=null;
					dbObj = new DataBasePersistent(this,DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME);
					dbObj.getColumns();	  
				if(offlinemode ==1){					
	            	SapGenConstants.showLog("offlinemode"+offlinemode);
	            	dbObj.getColumns();	         	            		            	
	            }else{
	            	dbObj.setTableName_ColumnName(this,DBConstants.TABLE_SALESORDER_HEAD_LIST,metaHeadArray);
	            }								
				tableExits = dbObj.checkTable();
				if(tableExits){
					//custArrList = dbObj.readListDataFromDB(this);
					custArrList =dbObj.readDataSelectedStrListDataFromDB(this, customerIdStr, DBConstants.KUNNR_COLUMN_NAME); 
				}					
				if(custArrList != null)
					//mattCopyArrList = (ArrayList)mattArrList.clone();
				SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);
							
				dbObj.closeDBHelper();  
				
				SapGenConstants.showLog("custArrList size:"+ custArrList.size());
				HashMap<String, String> stockMap = null;
	            String matIdStrVal = "", id = "", desc = "";
	        	
	        	dbObj.closeDBHelper();        
	        	prefillCustomerData();
			}catch(Exception sfg1){
				dbObj.closeDBHelper();	
				SapGenConstants.showErrorLog("Error in initCustDBConnection : "+sfg1.toString());
			}	
		}//end of initDBConnection
*/	 
	private void setTitleValue() {
    	try {
    		String context2 = " ";
    		if(dbObjUIConf == null)
				dbObjUIConf = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		if(flag == true){
    			context2 = DBConstants.SO_CREATE_COPY_TITLE_TAG;
    		}else{
    			context2 = DBConstants.SO_CREATE_TITLE_TAG;
    		}
    			
    		String title = dbObjUIConf.getTitle(context2, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
    			SapGenConstants.showLog("title: "+title);
       		 if(title.indexOf(SapGenConstants.title_offline) > 0){
            		bodylayout.setBackgroundResource(R.drawable.llborder);
            		bodylayout.setPadding(5, 5, 5, 5);
       		 }
        		SapGenConstants.showLog("title: "+title);
    		}   		    		
    		/*String SearchHint = dbObjUIConf.getSearchBarHint(DBConstants.DEVICE_TYPE_OVERVIEW_W_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_SEARCHBAR_TAG);
    		if(SearchHint != null && SearchHint.length() > 0){
    			searchET.setHint(SearchHint);
        		SapGenConstants.showLog("SearchHint: "+SearchHint);
    		}*/  		
    		dbObjUIConf.closeDBHelper();
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
	
	private void initLayout2() {					
		/*custLabelTV = (TextView) findViewById(R.id.step1tv);
		if(copyflag==1)
			custLabelTV.setVisibility(View.GONE);		
		socustnameTV = (TextView) findViewById(R.id.socustNameTV);
		custnameLinear = (LinearLayout) findViewById(R.id.socustnamelinear);
		custnameLinear.setVisibility(View.GONE);		
		customerET = (EditText) findViewById(R.id.customerET);
		customerET.setVisibility(View.GONE);				
		custNameTV = (TextView) findViewById(R.id.custNameTV);
		custAddressTV = (TextView) findViewById(R.id.custAddressTV);		
		materialET = (EditText) findViewById(R.id.materialET);
		materialET.setVisibility(View.GONE);		
		ponumb = (EditText) findViewById(R.id.ponumb);						
		poDateTV = (TextView) findViewById(R.id.podate);
		poDateTV.setOnClickListener(po_dateListener);		
		soValueET = (EditText) findViewById(R.id.soValueET);
		soValueET.setEnabled(false);			
		custSearchBtn = (ImageButton) findViewById(R.id.custsearchbtn);
		custSearchBtn.setOnClickListener(custsrch_btnListener); 
		custSearchBtn.setVisibility(View.GONE);		
		reqBy_date = (TextView) findViewById(R.id.req_date);
		reqBy_date.setOnClickListener(reqBy_dateListener);	     		
		custbackbtn = (ImageButton) findViewById(R.id.custbackbtn);
		custbackbtn.setOnClickListener(custback_btnListener2);		
		addItemBtn = (Button) findViewById(R.id.addItemBtn);
		addItemBtn.setOnClickListener(addItemBtnListener); 		
		getPriceBtn = (Button) findViewById(R.id.getPriceBtn);
		getPriceBtn.setOnClickListener(getPriceBtnListener); 		
		saveSoBtn = (Button) findViewById(R.id.saveSOBtn);
		saveSoBtn.setOnClickListener(saveSOBtnListener); 	
		searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);
		searchLinear2 = (LinearLayout) findViewById(R.id.sosearchlinear2);
		searchLinear2.setVisibility(View.GONE);
		headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);
		headerLinear.setVisibility(View.VISIBLE);		
		tableLayout = (TableLayout) findViewById(R.id.socreationItemtbllayout1);		
		relativeLayout = (RelativeLayout) findViewById(R.id.showlinear);
		relativeLayout.setVisibility(View.VISIBLE);*/
		 //sovaluelayout = (LinearLayout) findViewById(R.id.sovaluelinear);	
		final Calendar c = Calendar.getInstance();
        //initialize for start date
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);
       // updateDisplay_start_date();        
        POstartYear = c.get(Calendar.YEAR);
        POstartMonth = c.get(Calendar.MONTH);
        POstartDay = c.get(Calendar.DAY_OF_MONTH);
        POstartHour = c.get(Calendar.HOUR_OF_DAY);
        POstartMinute = c.get(Calendar.MINUTE);
       // updatePO_start_date();
        //updateDisplay_start_time();
       /* customerET.setFocusable(true);
		customerET.setFocusableInTouchMode(true);
		customerET.requestFocus();*/
		showSelectdCustomerHeaderInfo();
		initItemHeadLayout();
		sapsendLayout = (LinearLayout) findViewById(R.id.showlinear);		
        sapsendLayout.setVisibility(View.VISIBLE);	      
        sapsendLayout.removeAllViews();	
        displayUI(DBConstants.EDIT_ACTION_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, sapsendLayout);
	}//
				
	private void showSelectdCustomerHeaderInfo(){
	    try{
	    	custMap =(HashMap<String, String>)custDetArrSO.clone();	   
	    	custID =  (String) custMap.get(DBConstants.KUNAG_COLUMN_NAME);
	    	headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);	
	    	 if(headerLinear != null)
        		 headerLinear.setVisibility(View.VISIBLE);
        	 	 headerLinear.removeAllViews();					
        		 headerLinear.setOrientation(LinearLayout.VERTICAL);       		
        		
        	// headblckflag =1;
        	 displayUI(DBConstants.HEADER_CREATE_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headerLinear);	    		
    	}
    	catch(Exception sfgh){
    		SalesOrderConstants.showErrorLog("Error in showSelectdCustomerHeaderInfo : "+sfgh.toString());
    	}
	}//fn showCustomerHeaderInfo
	 
	public void getCustomerHeader(String idstr){
		try{
			DBConstants.DB_TABLE_NAME =" ";
			//setTitleValue();
			/*if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
				DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    		}	*/				
            	            
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME);
			dbObj.getColumns();	  
									
			tableExits = dbObj.checkTable();
			if(tableExits)
				custArrList= dbObj.readAllFieldDataBySelctdIdFromDB(this,idstr,DBConstants.KUNAG_COLUMN_NAME);
				//custArrList = dbObj.readListDataFromDB(this);
							
			SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);					
			SapGenConstants.showLog("custArrList size:"+ custArrList.size());
			HashMap<String, String> stockMap = null;
            String matIdStrVal = "", id = "", desc = "";       	
        	dbObj.closeDBHelper();               		
		}catch(Exception sfg1){
			dbObj.closeDBHelper();	
			SapGenConstants.showErrorLog("Error in initCustDBConnection : "+sfg1.toString());
		}	
	}//
	
	private void initLayout(){
		try{
			if(mattItemArrLst != null)
				mattItemArrLst.clear();	
			
			if(MatNoStringList!=null)
				MatNoStringList.clear();
			//custLabelTV = (TextView) findViewById(R.id.step1tv);
			customersearch();
			productsearch();			   		    		
			/*custNameTV = (TextView) findViewById(R.id.custNameTV);
			custAddressTV = (TextView) findViewById(R.id.custAddressTV);			
			socustnameTV = (TextView) findViewById(R.id.socustNameTV);
			custnameLinear = (LinearLayout) findViewById(R.id.socustnamelinear);
			custnameLinear.setVisibility(View.GONE);			
			custnamebackbtn = (ImageButton) findViewById(R.id.socustsearchbtn);
			custnamebackbtn.setOnClickListener(socustnameback_btnListener);	*/		
			/*customerET = (EditText) findViewById(R.id.customerET);
			materialET = (EditText) findViewById(R.id.materialET);			*/
			/*soValueET = (EditText) findViewById(R.id.soValueET);
			soValueET.setEnabled(false);				
			reqBy_date = (TextView) findViewById(R.id.req_date);
			reqBy_date.setOnClickListener(reqBy_dateListener);	        
			ponumb = (EditText) findViewById(R.id.ponumb);						
			poDateTV = (TextView) findViewById(R.id.podate);
			poDateTV.setOnClickListener(po_dateListener);						
			custbackbtn = (ImageButton) findViewById(R.id.custbackbtn);
			custbackbtn.setOnClickListener(custback_btnListener);			*/
			/*custSearchBtn = (ImageButton) findViewById(R.id.custsearchbtn);
			custSearchBtn.setOnClickListener(custsrch_btnListener); 
			custSearchBtn.requestFocus();			
			matSearchBtn = (ImageButton) findViewById(R.id.matsearchbtn);
			matSearchBtn.setOnClickListener(matsrch_btnListener); 
			matSearchBtn.requestFocus();			*/
			/*addItemBtn = (Button) findViewById(R.id.addItemBtn);
			addItemBtn.setOnClickListener(addItemBtnListener); 			
			getPriceBtn = (Button) findViewById(R.id.getPriceBtn);
			getPriceBtn.setOnClickListener(getPriceBtnListener); 			
			saveSoBtn = (Button) findViewById(R.id.saveSOBtn);
			saveSoBtn.setOnClickListener(saveSOBtnListener); 	*/		
			
						
			/*headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);
			headerLinear.setVisibility(View.GONE);		*/	
			/*tableLayout = (TableLayout) findViewById(R.id.socreationItemtbllayout1);
			tableLayout.setVisibility(View.GONE);	*/		
				
			/*showLayout = (LinearLayout) findViewById(R.id.showbuttons);
			showLayout.setVisibility(View.GONE);
			*/			
			final Calendar c = Calendar.getInstance();
	        //initialize for start date
	        startYear = c.get(Calendar.YEAR);
	        startMonth = c.get(Calendar.MONTH);
	        startDay = c.get(Calendar.DAY_OF_MONTH);
	        startHour = c.get(Calendar.HOUR_OF_DAY);
	        startMinute = c.get(Calendar.MINUTE);
	        //updateDisplay_start_date();	        
	        POstartYear = c.get(Calendar.YEAR);
	        POstartMonth = c.get(Calendar.MONTH);
	        POstartDay = c.get(Calendar.DAY_OF_MONTH);
	        POstartHour = c.get(Calendar.HOUR_OF_DAY);
	        POstartMinute = c.get(Calendar.MINUTE);	        
	       // updatePO_start_date();
	        //updateDisplay_start_time();	        
	       // initItemHeadLayout();
	        
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
	
	 private void productsearch() {		 
		 searchLinear2 = (LinearLayout) findViewById(R.id.sosearchlinear2);    	 	
 		searchLinear2.removeAllViews();
 		searchLinear2.setOrientation(LinearLayout.HORIZONTAL);    		 		
 		displayUI(DBConstants.PRODUCT_SEARCH_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, searchLinear2);	
 		//matsrchflag =1;
	}

	private void customersearch() {				
		searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);		
		searchLinear.removeAllViews();
		searchLinear.setOrientation(LinearLayout.HORIZONTAL);	
		displayUI(DBConstants.CUSTOMER_SEARCH_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, searchLinear);
		//custsearchflag =1;
	}//customersearch

	private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {	
    		String labVal = "";    		
    		valArrayList1 = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			
			SapGenConstants.showLog("valHeaderArrayList : "+valArrayList1.size());			 
			if(valArrayList1 != null && valArrayList1.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valArrayList1.size());
				
				LinearLayout llmclmhorsntl = new LinearLayout(SalesOrderCreationTablet.this);
  				llmclmhorsntl.setLayoutParams(new LinearLayout.LayoutParams(
    					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
  				llmclmhorsntl.removeAllViews();	
  				llmclmhorsntl.setOrientation(LinearLayout.HORIZONTAL); 	    
  				
  				LinearLayout llmclm4 = new LinearLayout(SalesOrderCreationTablet.this);  
  				llmclm4.removeAllViews();	
  				llmclm4.setOrientation(LinearLayout.VERTICAL); 
  				
  				LinearLayout reqstdatell = new LinearLayout(SalesOrderCreationTablet.this);
  				reqstdatell.setLayoutParams(new LinearLayout.LayoutParams(
    					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
  				reqstdatell.removeAllViews();	
  				reqstdatell.setOrientation(LinearLayout.HORIZONTAL); 	 
  				
  				LinearLayout podatell = new LinearLayout(SalesOrderCreationTablet.this);
  				podatell.setLayoutParams(new LinearLayout.LayoutParams(
    					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
  				podatell.removeAllViews();	
  				podatell.setOrientation(LinearLayout.HORIZONTAL); 	
  				
  				LinearLayout sovaluell = new LinearLayout(SalesOrderCreationTablet.this);  
  				sovaluell.removeAllViews();	
  				sovaluell.setOrientation(LinearLayout.HORIZONTAL); 
  				
			    //valTV = new TextView[valArrayList1.size()];
			    for( int i = 0; i < valArrayList1.size(); i++){  			 
			    	String secLine = "";         	 
			    	ArrayList list = (ArrayList) valArrayList1.get(i);
			    	if(list != null){
						SapGenConstants.showLog("list : "+list.size());
						if(list.size() > 0){
							if(list.size() == 1){									
								String name = list.get(0).toString().trim();	
								SapGenConstants.showLog("name : "+name);        						
								String nameVal = list.get(0).toString().trim();							
								String metaValue = "";
								String metaTrg = "";
								if(nameVal.indexOf("::") >= 0){
									String[] separated = nameVal.split("::");
									if(separated != null && separated.length > 0){
										if(separated.length > 2){
											metaLabel  = separated[0];
											metaValue  = separated[1];
											metaTrg = separated[2];
										}else{
											metaLabel  = separated[0];
											metaValue  = separated[1];
										}
									}
								}else{
									metaLabel = nameVal;
								}								
								SapGenConstants.showLog("metaLabel : "+metaLabel);	
								SapGenConstants.showLog("metaValue : "+metaValue); 	
								SapGenConstants.showLog("metaTrg : "+metaTrg); 
								String metaTrgActStr = "";
								if(metaTrg != null && metaTrg.length() > 0){
									if(metaTrg.indexOf("(") >= 0){
										metaTrgActStr = metaTrg.replaceAll("[\\#\\@\\(\\)]","");
									}else{
										metaTrgActStr = metaTrg;
									}
								}  
								String valStr = (String) custMap.get(metaLabel);    
								SapGenConstants.showLog("valStr : "+valStr);
			         			boolean lab = false;  
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = labelMap.containsKey(metaLabel);				         				
			         			}
			         			
			                 	if(metaValue != null && metaValue.length() > 0){				                 		
			                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                 			/*LinearLayout llmclm = new LinearLayout(this);
			                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);*/
			                        	String valTotStr = "";				                        	
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				SapGenConstants.showLog("labStr : "+labStr); 
			                 				if(labStr != null && labStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                					tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                					tv1.setPadding(5, 0, 0, 0);
			                					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                    					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                					}*/
			                					tv1.setLayoutParams(new
			                							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                					tv1.setText(labStr);
			                					tv1.setTextSize(20);
			                					//tv1.setTextColor(Color.BLACK);
			                					sovaluell.addView(tv1); 
			                					//llmclm.addView(tv1); 
			                 				}
			                 			} 
			                 			TextView tv = new TextView(this);
		            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
		            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
		            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
		            					tv.setTextSize(18);
		            					tv.setPadding(5, 0, 0, 0);
		            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
		                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
		            					}
			                 			if(metaLabel.equals(DBConstants.SO_MAIN_COL_NETWR)){
                   						 if(priceupdate==1){													
                   							 tv.setText(totalPriceStr);																					    			
											}	
			                 			}else if(valStr != null && valStr.length() > 0){	
			                 				tv.setText(valStr);
			        						/*TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			            					tv.setPadding(5, 0, 0, 0);
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}*/
			            					/*if(metaLabel.equals(DBConstants.SO_MAIN_COL_NETWR)){
	                    						 if(priceupdate==1){													
	                    							 tv.setText(totalPriceStr);																					    			
												}	
	                    					 }*//*else{
	                    						 valStr = " "; 
	                    					    tv.setText(valStr);
	                    					 }		*/	}             							                         	
			                         		sovaluell.addView(tv); 			            					
			                     		   			        					
			        					dynll.addView(sovaluell);
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_SEARCHBAR_TAG)){
			                 			EditText et = new EditText(this);
			                 			et.setText("");
			                 			//et.setPadding(5,5,5,5);
			                 			et.setWidth(40);
			                 			et.setLayoutParams(new LayoutParams(500,30));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
			                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				if(labStr != null && labStr.length() > 0){
			                 					et.setHint(labStr);
			                 					et.setTextSize(12);
			                 				}else{
			                     				et.setHint(R.string.SEARCH_HINT_LBL);
			                     			}
			                 			}	
			                 			et.addTextChangedListener(new TextWatcher(){
			                    	        public void afterTextChanged(Editable s) {
			            						String updatedQty = s.toString().trim();
			            				 		SapGenConstants.showLog("Text : "+s.toString());
			            				 		editStr = s.toString().trim();
			            				 		//filter(editStr);
			            				 		//ordQty = updatedQty;
			                    	        }
			                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
			                    	    }); 		                 		
			                 			LinearLayout ll = new LinearLayout(this);
			        				    ll.setOrientation(LinearLayout.VERTICAL);
			        				    ll.addView(et);
			        				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			       				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
			        				    dynll.addView(ll, layoutParams);
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
		                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
		                     			bt.setText("");
		                     			bt.setPadding(5,5,5,5);
		            					bt.setGravity(Gravity.CENTER);
		                     			bt.setTextColor(getResources().getColor(R.color.white));
		                     			bt.setBackgroundResource(R.drawable.btn_blue1);
		                     			if(lab){
		                     				String labStr = (String)labelMap.get(metaLabel);
		                     				if(labStr != null && labStr.length() > 0){
		                     					SapGenConstants.showLog("labStr "+labStr);
		                     					bt.setText(labStr);
		                     				}
		                     			}
		                     			bt.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												/*if(metaLabel == "ACTION-ADD-ITEM"){
													showSalesOrdItemSelScreen();
												}else if(metaLabel == "ACTION-REPRICE"){
													
												}else if(metaLabel == "ACTION-SAVE"){
													
												}*/
											}	
				                        });
		                     			dynll.addView(bt);
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG)){	
		                     			LinearLayout llmclm2 = new LinearLayout(this);
		                     			llmclm2.setOrientation(LinearLayout.HORIZONTAL);
		                     			if(lab){
		                     				String labStr = (String)labelMap.get(metaLabel);
		                     				SapGenConstants.showLog("labStr : "+labStr); 
		                     				 //valStr = (String) custMap.get(metaLabel); 
		                     				if(labStr != null && labStr.length() > 0){                 					
		                     					TextView tv = new TextView(this);
		                    					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
		                    					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
		                    					tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
		                    					tv.setPadding(5, 0, 8, 0);
		                    					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		                    					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
		                        					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
		                    					}*/
		                    					tv.setLayoutParams(new
		                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
		                    					tv.setText(labStr);	
		                    					tv.setTextSize(20);
		                    					//tv.setTextColor(Color.BLACK);
		                    					reqstdatell.addView(tv);
		                     				}
		                     			} 
		                     			 if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_KETDAT) || metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTDK)){				                     				
				            					if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_KETDAT)){				            						
				            						SapGenConstants.showLog("metaLabel "+metaLabel);
				                     				reqBy_date = new TextView(this);
					                     			reqBy_date = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
					                     			reqBy_date.setTextColor(getResources().getColor(R.color.bluelabel));	
					                     			reqBy_date.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					                     			reqBy_date.setPadding(5, 0, 5, 0);
					            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					            						reqBy_date.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					            					}
					            					reqBy_date.setLayoutParams(new
					            							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					            					updateDisplay_start_date();		
					            					//Display_start_date();
				            						reqBy_date.setOnClickListener(reqBy_dateListener);	
				            						reqBy_date.setTextSize(18);				            								            						
				            						reqstdatell.addView(reqBy_date);
				            						//llmclm2.addView(reqBy_date);
				            						dynll.addView(reqstdatell); 	
				            					}					            						
				            					else if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTDK)){
				            						SapGenConstants.showLog("metaLabel "+metaLabel);
				            						poDateTV = new TextView(this);
				            						poDateTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
				            						poDateTV.setTextColor(getResources().getColor(R.color.bluelabel));	
				            						poDateTV.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
					            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
				            						poDateTV.setPadding(5, 0, 5, 0);
					            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					            						poDateTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					            					}
					            					poDateTV.setLayoutParams(new
					            							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					            					//poDateTV.setOnClickListener(reqBy_dateListener);	
				            						updatePO_start_date(); 
					            					//Display_PO_date();
				            						poDateTV.setOnClickListener(po_dateListener);
				            						reqBy_date.setTextSize(18);
				            						reqstdatell.addView(poDateTV); 
				            						dynll.addView(reqstdatell); 	
				            					}				            										            					
			                     			 }else{
			                     				EditText et = new EditText(this);
				                     			et.setText("");
				                     			et.setPadding(5,0,0,0);
				                     			et.setWidth(20);
				                     			et.setTextColor(getResources().getColor(R.color.black));
				                     			et.setLayoutParams(new LayoutParams(120,30));
				            					et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				                     			et.setBackgroundResource(R.drawable.editext_border);
				                     			
				                     			et.addTextChangedListener(new TextWatcher(){
					                    	        public void afterTextChanged(Editable s) {
				                						String updatedQty = s.toString().trim();
				                				 		SapGenConstants.showLog("Text : "+s.toString());
				                				 		poNumbStr = s.toString();
				                				 		//ordQty = updatedQty;
					                    	        }
					                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
					                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
					                    	    });
				                     			dynll.addView(et); 
			                     			 }			                     				                     				                     			
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_SEARCH_ICON_TAG)){
		                     			ImageButton iv = new ImageButton(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        					                        			
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.search1);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {														
												SapGenConstants.showLog("detSumTag"+detSumTag);	
												if(detSumTag ==DBConstants.CUSTOMER_SEARCH_TAG)
											        getCustomerSearch(editStr);
												else    												      
											        showSalesOrdMatSelScreen(editStr);										         												      			
											}	
				                        });
		                    			dynll.addView(iv);
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_CHANGE_ICON_TAG)){			                    			    		
		                        		ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.back1);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												if(headerLinear != null)
									    			headerLinear.setVisibility(View.GONE); 
												//searchLinear= null; 
												backflag =1;
												if(custnameLinear!=null)
													custnameLinear.setVisibility(View.GONE); 
												searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);	
												searchLinear.setVisibility(View.VISIBLE); 
												customersearch();
												
												//customersearch();
											}	
				                        });
		                    			dynll.addView(iv);
		                     		}                          		
			                 	}            				    
							}else if (list.size() > 1){
			         			LinearLayout llmclm = new LinearLayout(this);
			         			llmclm.setOrientation(LinearLayout.HORIZONTAL);
			         						         			
								for(int l = 0; l < list.size(); l++){
									String name = list.get(l).toString().trim();
									SapGenConstants.showLog("name append else: "+name);                                 	
			                     	String nameVal = list.get(l).toString().trim();									
									String metaValue = "";
									String metaTrg = "";
									if(nameVal.indexOf("::") >= 0){
										String[] separated = nameVal.split("::");
										if(separated != null && separated.length > 0){
											if(separated.length > 2){
			    								metaLabel  = separated[0];
			    								metaValue  = separated[1];
			    								metaTrg = separated[2];
											}else{
			    								metaLabel  = separated[0];
			    								metaValue  = separated[1];
											}
										}
									}else{
										metaLabel = nameVal;
									}								
									SapGenConstants.showLog("metaLabel : "+metaLabel);	
									SapGenConstants.showLog("metaValue : "+metaValue); 	
									SapGenConstants.showLog("metaTrg : "+metaTrg); 
									String metaTrgActStr = "";
									if(metaTrg != null && metaTrg.length() > 0){
			    						if(metaTrg.indexOf("(") >= 0){
			    							metaTrgActStr = metaTrg.replaceAll("[\\#\\@\\(\\)]","");
			    						}else{
			    							metaTrgActStr = metaTrg;
			    						}
									}      					
									//SapGenConstants.showLog("1"); 
									//String valStr = (String) custMap.get(metaLabel);
									//SapGenConstants.showLog("2 "); 
			                     	//secLine += " "+valStr;
			                     	String valTotStr = "";
			                     	boolean lab = false;  
			             			if(metaLabel != null && metaLabel.length() > 0){
			             				lab = labelMap.containsKey(metaLabel);
			             			}                         	
			                     	if(metaValue != null && metaValue.length() > 0){				                     		
			                 			if(l != 0){
			                 				TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			            					tv.setPadding(5, 0, 0, 0);
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}*/
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                         		tv.setText("");
			            				    llmclm.addView(tv);       
			                 			}
			                     		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                     			String valStr = (String) custMap.get(metaLabel);
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				SapGenConstants.showLog("labStr : "+labStr); 
			                     				 //valStr = (String) custMap.get(metaLabel); 
			                     				if(labStr != null && labStr.length() > 0){                 					
			                     					TextView tv = new TextView(this);
			                    					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                    					tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                    					tv.setPadding(5, 0, 0, 0);
			                    					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                    					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                        					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                    					}*/
			                    					tv.setLayoutParams(new
			                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                    					tv.setText(labStr);	
			                    					tv.setTextSize(20);
			                    					//tv.setTextColor(Color.BLACK);
			                    				    llmclm.addView(tv);  			                    					 			                        			                                  					
			                     				}
			                     			} 
			                             	TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					tv.setTextSize(18);
			            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			            					tv.setPadding(5, 0, 0, 0);
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}*/
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			            					if(valStr != null && valStr.length() > 0){   
			            						valTotStr += valStr;
			                             		tv.setText(valTotStr);			                             		
		                    				   llmclm.addView(tv);
		                    					
			                             		//sovaluell.addView(tv);			                				   
			                				}                                      					
			            					String trgStr = (String) custMap.get(metaTrgActStr.toString().trim());
			             					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			             					//SapGenConstants.showLog("trgStr : "+trgStr); 
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			            						SapGenConstants.showLog("trgStr : "+trgStr);
			                     				if(trgStr != null && trgStr.length() > 0){
			                     					TextView tv1 = new TextView(this);
			                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                    					//tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                    					tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                    					tv1.setPadding(5, 0, 0, 0);
			                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                    					}
			                    					tv1.setLayoutParams(new
			                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                    					if(metaTrg.indexOf("(") >= 0){
			                    						//trgStr = "("+trgStr+")";
			                    					}
			                    					//tv1.setText(trgStr);
			                    					llmclm.addView(tv1); 
			                     				}
			                 				}
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG)){	
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				SapGenConstants.showLog("labStr : "+labStr); 
			                     				 //valStr = (String) custMap.get(metaLabel); 
			                     				if(labStr != null && labStr.length() > 0){                 					
			                     					TextView tv = new TextView(this);
			                    					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                    					tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                    					tv.setPadding(5, 0, 8, 0);
			                    					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                    					/*if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                        					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                    					}*/
			                    					tv.setLayoutParams(new
			                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                    					tv.setText(labStr);
			                    					tv.setTextSize(20);
			                    					//tv.setTextColor(Color.BLACK);
			                        			    llmclm.addView(tv);                                 					
			                     				}
			                     			} 
			                     			if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_KETDAT) || metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTDK)){				                     				
				            					if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_KETDAT)){
				            						SapGenConstants.showLog("metaLabel "+metaLabel);
				                     				reqBy_date = new TextView(this);
					                     			reqBy_date = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
					                     			reqBy_date.setTextColor(getResources().getColor(R.color.bluelabel));	
					                     			reqBy_date.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					                     			reqBy_date.setPadding(5, 0, 5, 0);
					            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					            						reqBy_date.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					            					}
					            					reqBy_date.setLayoutParams(new
					            							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				            						reqBy_date.setOnClickListener(reqBy_dateListener);	
				            						reqBy_date.setTextSize(20);
				            						updateDisplay_start_date();
				            						llmclm.addView(reqBy_date); 	
				            					}					            						
				            					else if(metaLabel.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTDK)){
				            						SapGenConstants.showLog("metaLabel "+metaLabel);
				            						poDateTV = new TextView(this);
				            						poDateTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
				            						poDateTV.setTextColor(getResources().getColor(R.color.bluelabel));	
				            						poDateTV.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
					            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
				            						poDateTV.setPadding(5, 0, 5, 0);
					            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					            						poDateTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					            					}
					            					poDateTV.setLayoutParams(new
					            							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					            					//poDateTV.setOnClickListener(reqBy_dateListener);
					            					poDateTV.setTextSize(18);
				            						updatePO_start_date(); 
				            						poDateTV.setOnClickListener(po_dateListener);
				            						llmclm.addView(poDateTV); 	
				            					}				            										            					
			                     			 }else{
				                     				EditText et = new EditText(this);
					                     			et.setText("");
					                     			//et.setPadding(5,0,0,0);
					                     			et.setWidth(20);
					                     			et.setTextColor(getResources().getColor(R.color.black));
					                     			et.setLayoutParams(new LayoutParams(120,30));
					            					et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
					                     			et.setBackgroundResource(R.drawable.editext_border);
					                     			
					                     			et.addTextChangedListener(new TextWatcher(){
						                    	        public void afterTextChanged(Editable s) {
					                						String updatedQty = s.toString().trim();
					                				 		SapGenConstants.showLog("Text : "+s.toString());	
					                				 		poNumbStr = s.toString();
						                    	        }
						                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
						                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
						                    	    });
					                     			llmclm.addView(et); 
				                     			 }				                     							                     				                     			
				                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_SEARCHBAR_TAG)){
				                     			EditText et = new EditText(this);
					                 			et.setText("");
					                 			//et.setPadding(5,5,5,5);
					                 			et.setWidth(40);
					                 			et.setLayoutParams(new LayoutParams(500,30));
					                 			et.setTextColor(getResources().getColor(R.color.black));
					                 			et.setBackgroundResource(R.drawable.editext_border);
					                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
					                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
					                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
					                 			if(lab){
					                 				String labStr = (String)labelMap.get(metaLabel);
					                 				if(labStr != null && labStr.length() > 0){
					                 					et.setHint(labStr);
					                 					et.setTextSize(12);
					                 				}else{
					                     				et.setHint(R.string.SEARCH_HINT_LBL);
					                     			}
					                 			}	
					                 			et.addTextChangedListener(new TextWatcher(){
					                    	        public void afterTextChanged(Editable s) {
					            						String updatedQty = s.toString().trim();
					            				 		SapGenConstants.showLog("Text : "+s.toString());
					            				 		editStr = s.toString().trim();
					            				 		//filter(editStr);
					            				 		//ordQty = updatedQty;
					                    	        }
					                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
					                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
					                    	    }); 		        		
			            				    llmclm.addView(et);                        				    
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_SEARCH_ICON_TAG)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        									                        		
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.search1);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													SapGenConstants.showLog("detSumTag"+detSumTag);	
													if(detSumTag ==DBConstants.CUSTOMER_SEARCH_TAG)
												        getCustomerSearch(editStr);
													else    												      
												        showSalesOrdMatSelScreen(editStr);	
												}	
					                        });
			            				    llmclm.addView(iv);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
			                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     			bt.setId(l+1);
			                     			bt.setText("");
			                     			bt.setPadding(5,5,5,5);
			            					bt.setGravity(Gravity.CENTER);
			                     			bt.setTextColor(getResources().getColor(R.color.white));
			                     			bt.setBackgroundResource(R.drawable.btn_blue1);
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					SapGenConstants.showLog("labStr "+labStr);
			                     					SapGenConstants.showLog("l value "+l);
			                     					bt.setText(labStr);
			                     				}
			                     			}
			                     			bt.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													 switch(view.getId()){
										              case 1:
										            	  addflag =1;
										            	  showSalesOrdItemSelScreen();
										              break;
										               //Second button click
										              case 2:
										            	 // pingConnection();
										            	  initPriceValueSoapConnection();
										              break;
										              case 3:
										            	  //pingConnection();
										            	  initSOCreateSoapConnection();
										              break;
										             									           
										             default:
										              break;
										              }													 												
												}	
					                        });			                     			
			                     			llmclm.addView(bt);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_CHANGE_ICON_TAG)){			                    			    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.back1);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													if(headerLinear != null)
										    			headerLinear.setVisibility(View.GONE); 
													//searchLinear= null; 
													backflag =1;
													if(custnameLinear!=null)
														custnameLinear.setVisibility(View.GONE); 
													searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);	
													searchLinear.setVisibility(View.VISIBLE); 
													customersearch();													
												}	
					                        });
			            				    llmclm.addView(iv);
			                     		}
			                     	}  
								}	
			                 	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			  				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
			   				    dynll.addView(llmclm, layoutParams);  
							}
						}    					
					 }
			     }
			}
		} catch (Exception eff) {
			SapGenConstants.showErrorLog("Error in displayUI : "+eff.toString());
		}
    }//fn displayUI 	 	
	 
	private void pingConnection() {
		internetAccess = SapGenConstants.checkConnectivityAvailable(SalesOrderCreationTablet.this);	
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
		
	}//

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
	            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this,SalesOrderConstants.APPLN_NAME_STR)+":GLTTD:"+latitide+":GLNGTD:"+longitude+":";
	            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
	            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
	            C0[2].Cdata = "EVENT[.]PING-SERVER[.]VERSION[.]0";
	           // C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+CustomerStr;
	        
	            Vector listVect = new Vector();
	            for(int k=0; k<C0.length; k++){
	                listVect.addElement(C0[k]);
	            }        
	            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
	            envelopeC.setOutputSoapObject(request);                    
	            SapGenConstants.showLog(request.toString());

	           // respType = SOAP_CONN_CUSTOMER;
	            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
	        }
	        catch(Exception asd){
	        	SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
	        }
	    }//fn initPriceSoapConnection
	 
	private void initItemHeadLayout(){
		try{
			tableLayout = (TableLayout)findViewById(R.id.socreationItemtbllayout1);	
	    		if(tableLayout != null)
	    			tableLayout.removeAllViews();	
		        dispwidth = SapGenConstants.getDisplayWidth(this);
		       // emptyvectors();
	    		/*searchET = (EditText)findViewById(R.id.searchBEF);
				searchET.setText(searchStr);
				searchET.addTextChangedListener(this);*/
				
				sovalArrayList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.SO_DEVICE_OVERVIEW_EDIT_W, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
				SalesOrderConstants.showLog("sovalArrayList size: "+sovalArrayList.size());
				
				soLabelArrayList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.SO_DEVICE_OVERVIEW_EDIT_W, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
				SalesOrderConstants.showLog("soLabelArrayList size: "+soLabelArrayList.size());
				String labVal = "";
				if(sovalArrayList != null && sovalArrayList.size() > 0){
					int cols =  soLabelArrayList.size();					
					dispwidth = SapGenConstants.getDisplayWidth(this);
					if(dispwidth < SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
						tableLayout.setColumnStretchable(1, true);
					//headLblTV = new TextView[valArrayList.size()];  
					headLblTV = new TextView[soLabelArrayList.size()]; 
					TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.so_table_row, null);
					tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					int labelWidth = 100, editWidth = 90;
					SapGenConstants.showLog("dispwidth : "+dispwidth);
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
						
						if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
							labelWidth = 220;
							editWidth = 300;
						}
					}
					SapGenConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);
					
					LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
					linparams.topMargin = 5; 
					linparams.bottomMargin = 5; 
					tr1.setLayoutParams(linparams);		
		
					for(int i=0; i<cols; i++){
						labVal = soLabelArrayList.get(i).toString().trim();
						String labValStr = "", valueValStr = "";
						String metaValStr = "";
						String typeValStr = "";
						if(labVal.indexOf("::") >= 0){
							String[] separated = labVal.split("::");
							if(separated != null && separated.length > 0){
								if(separated.length <= 2){
									labValStr  = separated[0];
									metaValStr  = separated[1];
								}else if(separated.length == 3){
									labValStr  = separated[0];
									metaValStr  = separated[1];
									typeValStr  = separated[2];
								}else if(separated.length == 4){
									labValStr  = separated[0];
									metaValStr  = separated[1];
									typeValStr  = separated[2];
									valueValStr  = separated[3];
								}else{
									labValStr  = separated[0];
								}
							}
						}else{
							labValStr = labVal;
						}
						final String metaValue = metaValStr;
						final String valueValStrCk = valueValStr;
						headLblTV[i] = (TextView) getLayoutInflater().inflate(R.layout.so_template_textview, null);
						headLblTV[i].setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						if(valueValStr != null && valueValStr.length() > 0){
							if(metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_ARKTX)){
								headLblTV[i].setWidth(190);  
							}else if(metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_NETWR)){
								headLblTV[i].setWidth(140);  
							}else if(valueValStrCk.equalsIgnoreCase(DBConstants.ACTION_DELETE_ICON_TAG) ){
								headLblTV[i].setWidth(60);  
							}else{
								headLblTV[i].setWidth(100);  
							}
							headLblTV[i].setHeight(30);
						}					
						headLblTV[i].setId(i);
						/*headLblTV[i].setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								if(!valueValStrCk.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
									sortItemsAction(metaValue);
								}
							}	
	                    });*/
						SapGenConstants.showLog("labValStr:  "+labValStr);
						SapGenConstants.showLog("metaValStr:  "+metaValStr);
						SapGenConstants.showLog("typeValStr:  "+typeValStr);
						//headLblTV[i].setGravity(Gravity.LEFT);	
						if(metaValStr != null && metaValStr.length() > 0){
	    					if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_NETWR) ){
	    						headLblTV[i].setGravity(Gravity.RIGHT);
	    					}
	    				}else{
	    					headLblTV[i].setGravity(Gravity.LEFT);	                     
	    				}				 			       
						
						if(labValStr != null && labValStr.length() > 0){
							headLblTV[i].setText(labValStr);
						}else{
							headLblTV[i].setText(" ");
						}					
						if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
							headLblTV[i] .setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);								
						tr1.addView(headLblTV[i]);						
					}											
					tableLayout.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					drawItemSubLayout();	
				}
		}
		catch(Exception swfg){
			SalesOrderConstants.showErrorLog("Error in initItemHeadLayout : "+swfg.toString());
		}
	}//fn initItemHeadLayout
	
	 private void drawItemSubLayout(){
		try{        
			int flag=0;
			int len =0;
			 HashMap<String, String> stockMap = null; 
			TableLayout tl = (TableLayout)findViewById(R.id.socreationItemtbllayout2);
			tl.removeAllViews();
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			linparams.leftMargin = 10; 
			linparams.rightMargin = 2; 
			linparams.gravity = Gravity.LEFT; //CENTER_VERTICAL;
			
			int labelWidth = 100, editWidth = 190;
			SapGenConstants.showLog("dispwidth : "+dispwidth);
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
				
				if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					labelWidth = 220;
					editWidth = 300;
				}
			}
			SapGenConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);
			
			
			if(soLabelArrayList != null && soLabelArrayList.size() > 0){
				if(stocksSelectdItemArrList1 != null){
					String valStr = "", imageUrlStr = "", matIdStr = "", qty = "";  					
	                int rowSize = soLabelArrayList.size();                
	                SapGenConstants.showLog("stocksSelectdItemArrList1 List Size  : "+stocksSelectdItemArrList1.size());
	               // valTV = new TextView[rowSize];
	                //SapGenConstants.showLog("offset:"+offset);
	                //SapGenConstants.showLog("data:"+data);
					for (int i = 0; i < stocksSelectdItemArrList1.size(); i++) {
	                    tr = new TableRow(this);        
						stockMap = (HashMap<String, String>) stocksSelectdItemArrList1.get(i);     
                		matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
                		 final String pId = matIdStr;
                		 if(multListviewFlag==0 || addflag==1){
                			 qtyStr = "1";
                			 editTextMap.put(matIdStr, qtyStr);
                		 }else{
                			 qtyStr = (String) stockMap.get(DBConstants.SO_MAIN_COL_KWMENG); 
                     		editTextMap.put(matIdStr, qtyStr);
                		 }               		
                		MatNoStringList.add(matIdStr);
		                //valTV = new TextView[valArrayList.size()];
	                    if(stockMap != null){	                    	
	                    	for (int i1 =0; i1 < soLabelArrayList.size(); i1++) {		                    		                    		
		                    		String nameVal = soLabelArrayList.get(i1).toString().trim();		                    					                    		
		                    		String labValStr = "", valueValStr = "";
		        					String metaValStr = "";
		        					String typeValStr = "";
		        					if(nameVal.indexOf("::") >= 0){
		        						String[] separated = nameVal.split("::");
		        						if(separated != null && separated.length > 0){
		        							if(separated.length <= 2){
		        								labValStr  = separated[0];
		        								metaValStr  = separated[1];
		        							}else if(separated.length == 3){
		        								labValStr  = separated[0];
		        								metaValStr  = separated[1];
		        								typeValStr  = separated[2];
		        							}else if(separated.length == 4){
		        								labValStr  = separated[0];
		        								metaValStr  = separated[1];
		        								typeValStr  = separated[2];
		        								valueValStr  = separated[3];
		        							}else{
		        								labValStr  = separated[0];
		        							}
		        						}
		        					}else{
		        						labValStr = nameVal;
		        					}		
		        					SapGenConstants.showLog("labValStr:  "+labValStr);
		        					SapGenConstants.showLog("metaValStr:  "+metaValStr);
		        					SapGenConstants.showLog("typeValStr:  "+typeValStr);
		        					SapGenConstants.showLog("valueValStr:  "+valueValStr);
		                    		//valStr = (String) stockMap.get(metaValStr);								
		        			
        				 		//SapGenConstants.showLog("tag1 : "+tag1);	
        				 		//SapGenConstants.showLog("tag2 : "+tag2);	
		        				if(valueValStr != null && (valueValStr.equalsIgnoreCase("DISPLAY-TAPPABLE") || valueValStr.equalsIgnoreCase("DISPLAY"))){
		        					SapGenConstants.showLog("valStr:  "+valStr);
		        					TextView valtv = new TextView(SalesOrderCreationTablet.this);     			                    			
	                				valtv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);				                    	
										/* String valStrDis = (String) stockMap.get(metaValStr);
											SapGenConstants.showLog("valStrDis: "+valStrDis); 	*/																															
	 			        						if(metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_NETWR)){
	 			        							if(priceupdate==1){
	 			        								SapGenConstants.showLog("inside else part of priceupdate"); 
		 			        							SapGenConstants.showLog("priceupdate: "+priceupdate); 
		   	                    						 if(metaValStr.equals(DBConstants.SO_MAIN_COL_NETWR) && proPriceMap.size() > 0){
																for (int m = 0; m < proPriceMap.size(); m++) {
																	//proTotPriceMap =  proPriceMap.get(m);   
																	String tstr = (String) proPriceMap.get(matIdStr);
																	SapGenConstants.showLog("tstr:  "+tstr);
																	//if(tstr != null && tstr.length() > 0){  
																		valStr = tstr;
																		valtv.setText(valStr);
																		valtv.setWidth(110);
																		valtv.setGravity(Gravity.RIGHT);
																	//}
													    		}									    			
															}	
	 			        							}else{
	 			        								valStr = (String) stockMap.get(metaValStr);	 			        								
	 			        								valtv.setText(valStr);
	 			        								valtv.setWidth(110);
	 			        								valtv.setPadding(0,0,5,0);	
	 			        								valtv.setGravity(Gravity.RIGHT);
	 			        							}	 			        							
	   	                    					 }else{
	   	                    						 String valStrDis = (String) stockMap.get(metaValStr);
	   												SapGenConstants.showLog("valStrDis: "+valStrDis); 	
	   												if(valStrDis.length() > 35){
	   						                    		String strSep = valStrDis.substring(0, 35);
	   						                    		valtv.setText(strSep+"...");
	   						                    		valtv.setGravity(Gravity.LEFT);
	   						                    	}else{
	   						                    		valtv.setText(valStrDis);
	   						                    		valtv.setGravity(Gravity.LEFT);
	   						                    	}	
	   	                    						if(metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_ARKTX)){
														valtv.setWidth(190);
														valtv.setGravity(Gravity.LEFT);
													}else if(metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_WAERK)){
														valtv.setWidth(110);
														valtv.setPadding(25,0,0,0);	
														valtv.setGravity(Gravity.LEFT);
													}else{
														valtv.setWidth(120);
														valtv.setGravity(Gravity.LEFT);
													}
		 			        						//valtv.setGravity(Gravity.LEFT);
	   	                    					 }
	 			        						/*valtv.setGravity(Gravity.RIGHT);
	 			        						valtv.setPadding(30,0,0,0);*/
	 			        						
	 				        				
	                    				 /*if(valStrDis.length() > 35){
					                    		String strSep = valStrDis.substring(0, 35);
					                    		valtv.setText(strSep+"...");
					                    	}else{
					                    		valtv.setText(valStrDis);
					                    	}	*/  																							                        	  				                        				                        					                        						                        	
				                        	valtv.setId(i1);							                        						    					
					    					valtv .setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);				    											    								    							    					 
					    					tr.addView(valtv);				                    				                  									                       	                    				 						                    				                    					                    					                    										    						                    			 		                    	
		                    	}else if(valueValStr != null && (valueValStr.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG))){
		                    		EditText et = new EditText(SalesOrderCreationTablet.this.getApplicationContext());	
		                    		if(priceupdate==1){
		                    			String qtyupdate = editTextMap.get(matIdStr);
		                    			et.setText(qtyupdate);
		                    		}else{
		                    			String qtyupdate = editTextMap.get(matIdStr);
		                    			et.setText(qtyStr);
		                    		}	                     			
	                     			//et.setPadding(5,3,5,3);
	                     			et.setWidth(40);
	                     			et.setTextColor(getResources().getColor(R.color.black));
	                     			et.setLayoutParams(new LayoutParams(70,30));
	            					et.setGravity(Gravity.CENTER);
	                     			et.setBackgroundResource(R.drawable.editext_border);
	                     			
	                     			et.addTextChangedListener(new TextWatcher(){
		                    	        public void afterTextChanged(Editable s) {
	                						String updatedQty = s.toString().trim();	                				 		
	                				 		clickedMatId = pId;                  						
                    				 		SapGenConstants.showLog("Text : "+s.toString());			
                    				 		SapGenConstants.showLog("Material Id : "+clickedMatId.toString().trim());
                    				 		editTextMap.put(clickedMatId, updatedQty);
		                    	        }
		                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		                    	    }); 
	                     			//et.setPadding(30,0,0,0);
		                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
		                    		linearLayout.setGravity(Gravity.LEFT);	
		                    		linearLayout.setPadding(0,0,5,0);
		                    		linearLayout.addView(et);
									tr.addView(linearLayout);
		                    	}else if(valueValStr != null && (valueValStr.equalsIgnoreCase(DBConstants.ACTION_DELETE_ICON_TAG))){
		                    		final ImageView delIV = new ImageView(SalesOrderCreationTablet.this.getApplicationContext()); 
		                    		delIV.setPadding(5,5,0,5);
		                    		delIV.setId(i);
		                    		
		                    		delIV.setOnClickListener(new View.OnClickListener() {
										public void onClick(View view) {											
											SapGenConstants.showLog("value  "+delIV.getId());
											clickAction(delIV.getId());
										}	
			                        });
		                    		delIV.setImageResource(R.drawable.delete1);
		                    		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
		                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
		                            	linlayparams1.height = 40;
		                            	linlayparams1.width = 40;
		                            	delIV.setLayoutParams(linlayparams1);
		                            }
		                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
		                    		linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		                    		//linearLayout.setMinimumWidth(labelWidth);
		                            linearLayout.addView(delIV);
			    					tr.addView(linearLayout);	
		                    	}
	                    	}
	    					if(i%2 == 0)
	    						tr.setBackgroundResource(R.color.item_even_color);
				            else
				            	tr.setBackgroundResource(R.color.item_odd_color);
	    					
	    					tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	                    }					
					}
				}
			}			
		}
		catch(Exception asgf){
			SalesOrderConstants.showErrorLog("On drawItemSubLayout : "+asgf.toString());
		}
	}//fn drawItemSubLayout
		
	 public void clickAction(final int id){
	   		try{
	   			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage(R.string.COMMON_DG_DELTHIS);
		        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	deleteAction(id);
		            }
		        });                
		        builder.setNegativeButton(R.string.CANCEL, null);
		        builder.show();
		    }
		    catch(Exception asf){
		    	SapGenConstants.showErrorLog("Error in clickAction : "+asf.toString());
		    }
	   	}//fn clickAction
	 private void deleteAction(int strmId){
		 try{ 	        	
			 stocksSelectdItemArrList1.remove(stocksSelectdItemArrList1.get(strmId));				
			 drawItemSubLayout();
	        }
	        catch(Exception dsgf){
	        	SapGenConstants.showErrorLog("Error in deleteAction : "+dsgf.toString());
	        }
	    }//fn deleteAction
	   
	 
	public void getQty(){
		try{							 
			String itemString="" ,qtyString="";
			SalesOrdProMattConstraints  mattCategory = null;	
			SalesOrdProItemOpConstraints itemCategory = null;				
			if(hashmap!=null)
				hashmap.clear();	
			SalesOrderConstants.showLog("size of qty : "+QtyList.size());
			SalesOrderConstants.showLog("number of materials: "+CommonMattList.size());
			for(int i=0;i<CommonMattList.size();i++){
				try{
					 itemCategory = (SalesOrdProItemOpConstraints)CommonMattList.get(i);	                   	                    			                    	                    	
					 itemString = itemCategory.getMaterialNo().trim();
					 SalesOrderConstants.showLog("value of item number  : "+itemString);
					 qtyString =qtyEditText[i].getText().toString();
					 SalesOrderConstants.showLog("value of qty : "+qtyString);
					 hashmap.put(itemString, qtyString);			                    	
					 
				   }catch(Exception asf1){
						SalesOrderConstants.showErrorLog("Error in SalesOrdProItemOpConstraints part : "+asf1.toString());
					}
						
				try{						
					mattCategory = (SalesOrdProMattConstraints)CommonMattList.get(i);			                 		                    	 		                    	
					//mattDescStr = mattCategory.getMaterialDesc().trim();
					itemString = mattCategory.getMaterialNo().trim();
					SalesOrderConstants.showLog("value of item number  : "+itemString);
					//String qtyString2 = QtyList.get(i).toString();
					qtyString= qtyEditText[i].getText().toString();
					SalesOrderConstants.showLog("value of qty : "+qtyString);
					hashmap.put(itemString, qtyString);
									
				}catch(Exception asf2){
					SalesOrderConstants.showErrorLog("Error in SalesOrdProMattConstraints part : "+asf2.toString());
				}						
			} 
		 }catch(Exception asgf2){
				SalesOrderConstants.showErrorLog("On getQty : "+asgf2.toString());
		}			 
	 }//getQty
	 
/*	private OnClickListener custback_btnListener2 = new OnClickListener() {
		public void onClick(View v) {
			hideCustomerHeaderInfoforSelectedCust();
		}
	};*/
	    
    private void hideCustomerHeaderInfoforSelectedCust(){
    	try{
    		if(searchLinear != null)
    			searchLinear.setVisibility(View.VISIBLE);    		
    		//custLabelTV.setVisibility(View.GONE);    		 
    		if(headerLinear != null)
    			headerLinear.setVisibility(View.GONE);    
    		if(custnameLinear != null)
    			custnameLinear.setVisibility(View.GONE);    
        	//customerET.setVisibility(View.VISIBLE);
        	//custSearchBtn.setVisibility(View.VISIBLE);		        	 
    	}
    	catch(Exception sfgh){
    		SalesOrderConstants.showErrorLog("Error in hideCustomerHeaderInfo : "+sfgh.toString());
    	}
    }//fn hideCustomerHeaderInfo
	    
	private void postFillQtyValues(){
		try{
			int lenth = 0;
			if(qtyEditText != null)
				lenth = qtyEditText.length;
			if(qtyArr != null){
				SalesOrderConstants.showLog("Edit length : "+lenth+" : "+qtyArr.size());
				for(int h=0; h<qtyArr.size(); h++){
					if((qtyEditText[h] != null) && (h < lenth)){
						qtyEditText[h].setText(String.valueOf(qtyArr.elementAt(h)));
					}
				}
			}
		}
		catch(Exception assgf){
			SalesOrderConstants.showErrorLog("On postFillQtyValues : "+assgf.toString());
		}
	}//fn postFillQtyValues
	 
	private void preFillQtyArray(){
		try{
			int arrSize = 0;
			if(qtyEditText != null){
				arrSize = qtyEditText.length;
				SalesOrderConstants.showLog("Unit Array Size: "+arrSize);
				if(arrSize > 0){
					String qtyStr = "";
					if(qtyArr != null)
						qtyArr.removeAllElements();					
					for(int h=0; h<arrSize; h++){
						if(qtyEditText[h] != null){
							qtyStr = qtyEditText[h].getText().toString();
							if((qtyStr != null) && (!qtyStr.equalsIgnoreCase("")))
								qtyArr.addElement(Integer.parseInt(qtyStr)); 
							else
								qtyArr.addElement(" ");							
							SalesOrderConstants.showLog("Qty : "+qtyStr);
						}
					}
				}
			}
		}
		catch(Exception asgf){
			SalesOrderConstants.showErrorLog("On preFillQtyArray : "+asgf.toString());
		}
	}//fn preFillQtyArray
	 
	private void deleteItemAction(int selId){
		try{
			if(CommonMattList != null){
				if(CommonMattList.size() > selId){
					CommonMattList.remove(selId);						
					SalesOrderConstants.showLog("CommonMattList Size : "+CommonMattList.size());
		            preFillQtyArray();
		            if(qtyArr != null){
						SalesOrderConstants.showLog("Qty Size: "+qtyArr.size()+" : "+selId);
						if(qtyArr.size() > selId)
							qtyArr.removeElementAt(selId);
					}
					drawItemSubLayout();
				}
			}
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("On deleteItemAction : "+sfg.toString());
		}
	}//fn deleteItemAction
	 
	private void clickDeleteAction(final int position){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this Item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	deleteItemAction(position);
            }
        });                
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }//fn clickDeleteAction
	
	/*private void showSalesOrdItemSelScreen(){
			try {
				Intent intent = new Intent(this, SalesOrderCrtMaterialScreen.class);
				startActivityForResult(intent,SapGenConstants.SALESORD_CRT_MATTSEL_SCREEN);
			} 
			catch (Exception e) {
				SalesOrderConstants.showErrorLog(e.getMessage());
			}
		}//fn showSalesOrdItemSelScreen
	 */		
	
	private void showSalesOrdItemSelScreen(){
		try {						
			Intent intent = new Intent(this, SalesOrderCrtMaterialScreen.class);
			intent.putExtra("matFlag", addflag);
			startActivityForResult(intent,SapGenConstants.SALESORD_CRT_MATTSEL_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderConstants.showErrorLog(e.getMessage());
		}
	}//fn showSalesOrdItemSelScreen	
	
	private void showSalesOrdCustomerSelScreen(){
		try {
			Intent intent = new Intent(this, SalesOrderCrtCustScreen.class);
			intent.putExtra("custDetArr", custDetArr);
			if((mainCustomerId != null) && (!mainCustomerId.equalsIgnoreCase("")))
				intent.putExtra("isAvailable", true);
			else
				intent.putExtra("isAvailable", false);
			startActivityForResult(intent,SapGenConstants.SALESORD_CRT_CUSTSEL_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderConstants.showErrorLog(e.getMessage());
		}
	}//fn showSalesOrdCustomerSelScreen	
	
	private OnClickListener addItemBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderConstants.showLog("Add Item btn clicked");
			getQty();
        	showSalesOrdItemSelScreen();
        }
    };
    
    private OnClickListener getPriceBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderConstants.showLog("Get Price btn clicked");
        	getPriceValueforItems();
        }
    };
    
    private OnClickListener saveSOBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderConstants.showLog("Save SO btn clicked");      
        	getSalesOrderCreation();
        }
    };
    	  
  
    private void getCustomerSearch(String custIdStr){
    	try{
    		custSearchStr = custIdStr;
			SalesOrderConstants.showLog("customer : "+custSearchStr);
			if(isConnAvail!=false)						
				initCustomerSoapConnection();
			else
				initCustDBConnection(custIdStr);			
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in getCustomerSearch : "+sfg.toString());
    	}
    }//fn getCustomerSearch
    
    private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(customerET.getText().toString().trim().length() != 0) {
				try{
					 custIdStr = customerET.getText().toString().trim();
					getCustomerSearch(custIdStr);
				}
				catch(Exception wsfsg){
					SalesOrderConstants.showErrorLog("Error in Customer Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Enter Customer name to search.");
        }
    };
    
    private OnClickListener matsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(materialET.getText().toString().trim().length() != 0) {
				try{
					String custIdStr = materialET.getText().toString().trim();
					showSalesOrdMatSelScreen(custIdStr);
				}
				catch(Exception wsfsg){
					SalesOrderConstants.showErrorLog("Error in material Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Enter Customer name to search.");
        }
    };
    
    private void showSalesOrdMatSelScreen(String matStr){
		try {				
			flag2=true;
			Intent intent = new Intent(this, SalesOrderCrtMaterialScreen.class);
			intent.putExtra("matFlag2", flag2);
			intent.putExtra("matStr", matStr);
			startActivityForResult(intent,SapGenConstants.SALESORD_CRT_MATTSEL_SCREEN);
		} 
		catch (Exception e) {
			SalesOrderConstants.showErrorLog(e.getMessage());
		}
	}//fn showSalesOrdItemSelScreen
    
    private void getPriceValueforItems(){
    	try{
    		if((customerIdStr != null) && (!customerIdStr.equalsIgnoreCase(""))){
    			SalesOrderConstants.showLog("customerIdStr : "+customerIdStr);
    			if((CommonMattList != null) && (CommonMattList.size() > 0)){	    				
    				initPriceValueSoapConnection();
    			}
    			else
        			SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Add an item to Process.");
    		}
    		else
    			SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Select a Customer to Process.");
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in getPriceValueforItems : "+sfg.toString());
    	}
    }//fn getPriceValueforItems    
    
    private void getSalesOrderCreation(){
    	try{
    		if((customerIdStr != null) && (!customerIdStr.equalsIgnoreCase(""))){
    			SalesOrderConstants.showLog("customerIdStr : "+customerIdStr);
    			if((CommonMattList != null) && (CommonMattList.size() > 0)){    				
    				initSOCreateSoapConnection();
    			}
    			else
        			SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Add an item to Process.");
    		}
    		else
    			SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, "Select a Customer to Process.");
    	}
    	catch(Exception ssfg){
    		SalesOrderConstants.showErrorLog("Error in getSalesOrderCreation : "+ssfg.toString());
    	}
    }//fn getSalesOrderCreation    
        
    private void initSOCreateSoapConnection(){        
    	SoapSerializationEnvelope envelopeC = null;
    	String contIdStr = "";
    	try{
    		if((mainContactId != null) && (!mainContactId.equalsIgnoreCase("")))
    			contIdStr = mainContactId;
    	}
    	catch(Exception sgg){}
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SapGenIpConstraints C0[], C1[];
            C0 = new SapGenIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SapGenIpConstraints(); 
            }	            
            
            if(PriceSoapList!=null)
            	PriceSoapList.clear();
            
            if(MatNoUnitSoapList!=null)
            	MatNoUnitSoapList.clear();
            
            //C0[0].Cdata = "DEVICE-ID:"+SalesOrderProConstants.getMobileIMEI(this)+":DEVICE-TYPE:BB";
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]SALES-ORDER-CREATE[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME[.]PARNR[.]BSTDK[.]BSTKD";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            //poNumbStr = ponumb.getText().toString();
            //poDateStr =  poDateTV.getText().toString();
            SalesOrderConstants.showLog("poNumbStr : "+poNumbStr);
            SalesOrderConstants.showLog("stocksSelectdItemArrList1 size : "+stocksSelectdItemArrList1.size());
            HashMap<String, String> qtyMap = null; 
            if(stocksSelectdItemArrList1 != null){
	            int itemSize = stocksSelectdItemArrList1.size();
	            if(stocksArrListOffline!=null)
	            	stocksArrListOffline.clear();
	            stocksArrListOffline.addAll(stocksSelectdItemArrList1);
	            if(itemSize > 0){
	            	//C1 = new SalesOrdProIpConstraints[itemSize];
	            	int itemNo = 0, itemQty = 0;	            	
	            
	            	for(int k = 0; k < itemSize; k++){
						qtyMap = (HashMap<String, String>) stocksSelectdItemArrList1.get(k);     
	            		String matIdStr = (String) qtyMap.get(DBConstants.MATNR_COLUMN_NAME);
	            		if(matIdStr != null && matIdStr.length() > 0){
	            			PriceSoapList.add(matIdStr);	
	            		}
	            	}	           	
	            	String qtyStr = "",MaterialUnit = "";
	            	dateStr = startYear+"-"+startMonth+"-"+startDay;
	            	poDateStr = POstartYear+"-"+POstartMonth+"-"+POstartDay;
	            	SalesOrderConstants.showLog("dateStr : "+dateStr);
	            	SalesOrderConstants.showLog("poDateStr : "+poDateStr);
	            	
	            	int psize=PriceSoapList.size();
	            	SalesOrderConstants.showLog("psize : "+psize);
	            	C1 = new SapGenIpConstraints[psize];
	            	for(int j=0; j<psize; j++){
	            		MaterialNoSO = PriceSoapList.get(j).toString().trim();	
						if(MaterialNoSO != null && MaterialNoSO.length() > 0){
	        				SapGenConstants.showLog("MaterialNo : "+MaterialNoSO);
							if(editTextMap != null && editTextMap.size() > 0){
		            			qtyStr = (String)editTextMap.get(MaterialNoSO);  
		            			try{
		            				itemQty = Integer.parseInt(qtyStr.toString());
		            			}catch(NumberFormatException  asd){
		            	        	SalesOrderConstants.showErrorLog("Integer parse exception : "+asd.toString());
		            	        	//itemQty =0;
		            	        }	            				
								SapGenConstants.showLog("Qty : "+itemQty);
							}	
						}
		            		/*if(itemQty < 0)
		            			itemQty = 0;	*/            		
		            		SapGenConstants.showLog("itemQty : "+itemQty);	            
	            		
	            		if(PriceSoapList != null){
	            			C1[j] = new SapGenIpConstraints(); 
		            		C1[j].Cdata = "ZGSEVAST_SDCRTN20[.]"+custID+"[.]"+dateStr+"[.]"+itemNo+"[.]"+MaterialNoSO.trim()+"[.]"+itemQty+"[.]"+MaterialUnit.trim()+"[.]"+contIdStr+"[.]"+poDateStr+"[.]"+poNumbStr+"[.]";
		            		listVect.addElement(C1[j]);
	            		}
	            	}
	            }
            }
            	
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());
            respType = SOAP_CONN_SOCREATION;
            
            if(isConnAvail == true){
           	 doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
           }
           else{
        	   //if(SapGenConstants.setQpFlag==true){
        		 	String refId = custID;
                   	//No network so pass the contents to the offline QueueProcessor
                   	//SapGenConstants.showLog("SO_PACKAGE_NAME: "+SapGenConstants.SO_PACKAGE_NAME);
                   	Long now = Long.valueOf(System.currentTimeMillis());
                   	String newLocalId = refId+now.toString();
                   	SapGenConstants.showLog("newLocalId : "+newLocalId);
                   	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, SalesOrderConstants.SO_CALLING_APP_NAME, SalesOrderConstants.SO_PACKAGE_NAME, refId,newLocalId, SalesOrderConstants.APPLN_BGSERVICE_NAME, "SALES-ORDER-CREATE", request, now);
                   	//SapQueueProcessorHelperConstants.sendOfflineContentToQueueProcessor(this, SalesOrderConstants.SO_CALLING_APP_NAME, SalesOrderConstants.SO_PACKAGE_NAME, newLocalId,refId, SalesOrderConstants.APPLN_BGSERVICE_NAME, "SALES-ORDER-CREATE", request, now);
                   	SalesOrderDBOperations.insertCreateScreenDataInToDB(this,stocksSelectdItemArrList1,editTextMap,custID,newLocalId);
                   
                  /* 	Intent intent;
                   	int dispwidth = SapGenConstants.getDisplayWidth(this);
            		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
        				intent = new Intent(SalesOrderCreationTablet.this, SalesOrderMainScreenTablet.class);
        				//intent = new Intent(this, ActivityListForPhone.class);
        			}else{
        				intent = new Intent(SalesOrderCreationTablet.this, SalesOrderListActivity.class);
        			} 
            		
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);      
        			intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_SALESPRO);		
        			intent.putExtra("contactId", mainContactId);
        			intent.putExtra("customerId", mainCustomerId);
        			startActivity(intent);*/
                   //}
        	   }        
          	Intent intent;
          	intent = new Intent(SalesOrderCreationTablet.this, SalesOrderMainScreenTablet.class);
           	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);      
			intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_SALESPRO);		
			intent.putExtra("contactId", mainContactId);
			intent.putExtra("customerId", mainCustomerId);
			startActivity(intent);         
            //doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);          
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, SOAP_CONN_SOCREATION);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initSOCreateSoapConnection : "+asd.toString());
        }
    }//fn initSOCreateSoapConnection
    
    private void initPriceValueSoapConnection(){        
    	SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SapGenIpConstraints C0[], C1[];
            C0 = new SapGenIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SapGenIpConstraints(); 
            }	            
            if(PriceSoapList!=null)
            	PriceSoapList.clear();
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]SALES-ORDER-PRICE[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            HashMap<String, String> qtyMap = null; 
            if(stocksSelectdItemArrList1 != null){
	            int itemSize = stocksSelectdItemArrList1.size();
	            if(itemSize > 0){
	            	int itemNo = 0, itemQty = 0;
	            	String qtyStr = "", MaterialNo = "";
	            	for(int k = 0; k < itemSize; k++){
						qtyMap = (HashMap<String, String>) stocksSelectdItemArrList1.get(k);     
	            		String matIdStr = (String) qtyMap.get(DBConstants.MATNR_COLUMN_NAME);
	            		if(matIdStr != null && matIdStr.length() > 0){
	            			PriceSoapList.add(matIdStr);	
	            		}
	            	}	            	
	            	int psize = PriceSoapList.size();
	            	SapGenConstants.showLog("psize : "+psize);
	            	C1 = new SapGenIpConstraints[psize];	            	
	            	for(int j = 0; j < psize; j++){
        				MaterialNo = PriceSoapList.get(j).toString().trim();	
						if(MaterialNo != null && MaterialNo.length() > 0){
	        				SapGenConstants.showLog("MaterialNo : "+MaterialNo);
							if(editTextMap != null && editTextMap.size() > 0){
		            			qtyStr = (String)editTextMap.get(MaterialNo);  
		            			try{
		            				itemQty = Integer.parseInt(qtyStr.toString());
		            			}catch(NumberFormatException  asd){
		            	        	SalesOrderConstants.showErrorLog("Integer parse exception : "+asd.toString());
		            	        	//itemQty =0;
		            	        }	            				
								SapGenConstants.showLog("Qty : "+itemQty);
							}		
		            		/*if(itemQty < 0)
		            			itemQty = 0;	*/            		
		            		SapGenConstants.showLog("itemQty : "+itemQty);	            		
		            		if(PriceSoapList != null){
		            			C1[j] = new SapGenIpConstraints(); 
			            		C1[j].Cdata = "ZGSEVAST_SDCRTN20[.]"+custID+"[.][.][.]"+MaterialNo+"[.]"+itemQty+"[.][.]";
			            		listVect.addElement(C1[j]);
		            		}
		            	}
	            	}
	            }
            }
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());
            respType = SOAP_CONN_PRICEVALUE;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);          
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, SOAP_CONN_PRICEVALUE);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initPriceValueSoapConnection : "+asd.toString());
        }
    }//fn initPriceValueSoapConnection
    
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
            
            //C0[0].Cdata = "DEVICE-ID:"+SalesOrderProConstants.getMobileIMEI(this)+":DEVICE-TYPE:BB";
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0";
            C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+custSearchStr;
            
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());
            respType = SOAP_CONN_CUSTOMER;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL, SOAP_CONN_CUSTOMER);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initCustomerSoapConnection : "+asd.toString());
        }
    }//fn initCustomerSoapConnection     
    
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
	    			if(respType == SOAP_CONN_SOCREATION){
		    			if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(SalesOrderCreationTablet.this, "", getString(R.string.COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	                    		updateSOCreationServerResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    			}else if(respType == SOAP_CONN_PRICEVALUE){
	    				if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(SalesOrderCreationTablet.this, "", getString(R.string.COMPILE_DATA),true);
	    	            	Thread t = new Thread() 
	    	    			{
	    	    	            public void run() 
	    	    				{
	    	            			try{
	    	            				updateGettingPriceListServerResponse(resultSoap);
	    	            			} catch (Exception e) {
	    	            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
	    	            			}
	    	    				}
	    	    			};
	    	    	        t.start();	
	        			}
	    			}else if(respType == SOAP_CONN_CUSTOMER){
	    				if(pdialog != null)
	            			pdialog = null;
	            		
	        			if(pdialog == null){
	        				pdialog = ProgressDialog.show(SalesOrderCreationTablet.this, "", getString(R.string.COMPILE_DATA),true);
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
	    			if((respType == SOAP_CONN_CUSTOMER)){
	    				initCustDBConnection(custIdStr);
	    			}
	    			
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };   
     
    /*public void OfflineFunc(){
       	if(offlineFlag==1){
       		SapGenConstants.checkInternetConnection(this, myTitle,title,resultSoap);	
       	}else{
       		SapGenConstants.checkInternetConnection(this,myTitle,title);
       	}    	  		
       }//OfflineFunc
*/    
    
    
    private void onClose(){
    	try{
    		setResult(RESULT_OK);           	
       	 	if(contactFlag)
       	 		contactFlag = false;
       	// alertDialog.dismiss();
    		this.finish();
    	}
    	catch(Exception sfg){}
    }//fn onClose
    
    private void updateSOCreationServerResponse(SoapObject soap){	
        try{ 
        	if(soap != null){  
        		String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[37];
                int propsCount = 0, errCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;   
                
                for (int i = 0; i < soap.getPropertyCount(); i++) {  
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    SalesOrderConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
                        	SalesOrderConstants.showLog(j+" : "+pii.getProperty(j).toString());
                            if(j >= 4){
                                result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                firstIndex = firstIndex + 3;
                                result = result.substring(firstIndex);
                                //SapTasksConstants.Log(result);
                                
                                resC = 0;
                                indexA = 0;
                                indexB = result.indexOf(delimeter);
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    //SapTasksConstants.Log(resC+" : "+res);
                                    resArray[resC] = res;
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    resC++;
                                }
                                int endIndex = result.lastIndexOf(';');
                                resArray[resC] = result.substring(indexA,endIndex);
                                //SapTasksConstants.Log(resC+" : "+resArray[resC]);
                            }
                            else if(j < 4){
                                String errorMsg = pii.getProperty(j).toString();
                                SalesOrderConstants.showLog("Inside J == "+j+" : "+propsCount+" : "+errorMsg);
                                int errorFstIndex = errorMsg.indexOf("Message=");
                                if(errorFstIndex > 0){
                                    if((errorMsg.indexOf("Type=A") > 0) || (errorMsg.indexOf("Type=E") > 0) || (errorMsg.indexOf("Type=S") > 0)){
                                        int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
                                        if(taskErrorMsgStr.equalsIgnoreCase(""))
                                            taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex).trim();
                                        
                                    }
                                    SapGenConstants.SO_SOAP_RESP_MSG = taskErrorMsgStr;
	                               /* int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);*/
	                                SalesOrderConstants.showLog(taskErrorMsgStr);
                                	/*this.runOnUiThread(new Runnable() {
                                        public void run() {
                                        	//SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, taskErrorMsgStr);
                                        	DisplayDiogPopUp();
                                        }
                                    });*/
                                    /*if(errCount < 0)
                                        errCount = 0;
                                    System.out.println("errCount : "+errCount+" : "+j);    
                                    if(j == errCount){
                                    	SalesOrderConstants.showErrorDialog(this, taskErrorMsgStr);
                                        diagdispFlag = true;
                                    }*/
                                }
                            }
                        }
                    }
                }
        	}
        }
        catch(Exception sff){
            SalesOrderConstants.showErrorLog("On updateSOCreationServerResponse : "+sff.toString());
        	stopProgressDialog();
        } 
        finally{        	
        	SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, taskErrorMsgStr);  
        	stopProgressDialog();
        	onClose();
        	/*try{
        		this.runOnUiThread(new Runnable() {
                    public void run() {
                        if(taskErrorMsgStr.equalsIgnoreCase("")){
                        	onClose();
                        }
                        else if(taskErrorMsgStr.indexOf("created") > 0){
                        	SalesOrderConstants.showErrorDialog(SalesOrderCreationTablet.this, taskErrorMsgStr);                	
                        	onClose();
                        }
                    }
                });
            }
            catch(Exception asf){}*/
        }
    }//fn updateSOCreationServerResponse      
       
    private void DisplayDiogPopUp() {
		TextView tv;
		
		ImageButton emailbtn,skypebtn;		
    	 LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		  View layout;
		  
		  layout = inflater.inflate(R.layout.salesorder_email_error,
				  (ViewGroup) findViewById(R.id.listviewlineardialog3));		        		       		  
		  tv = (TextView)layout.findViewById(R.id.errortxt);
		  tv.setTextColor(Color.GRAY);
		  tv.setText(taskErrorMsgStr);
		  emailbtn = (ImageButton)layout.findViewById(R.id.showemailbtn2);		  
		  emailbtn.setOnClickListener(email_btnListener); 	   
		  		
		  builder = new AlertDialog.Builder(this); 		 
		  builder.setIcon(R.drawable.notify);
		  //builder.setInverseBackgroundForced(true);
		  //View view=inflater.inflate(R.layout.activity_diag_popup, null);
		  builder.setTitle("Error");	        		 
		  builder.setView(layout); 	        		
		 // builder.setMessage(taskErrorMsgStr);
		  alertDialog = builder.create();    		  
		  alertDialog.show();		
	}//DisplayDiogPopUp
    
    
    private OnClickListener email_btnListener = new OnClickListener() {
        public void onClick(View v) {         	
        	showEmailActivity(); 
        	
        }
    };
    
    public void showEmailActivity() {
    	try{   
    		String  linkText ="",matnrStr= "";
    		String  last = "", urlName = "", androidOS = "", manufacturer = "", editionStr = "",imeno =""; 		
    		Elements name, edition, device, version, deviceType, server, url;
    		imeno = SapGenConstants.getMobileIMEI(this);
    		AssetManager assetManager = getAssets();
    		InputStream inputstrm;    		
    		inputstrm = assetManager.open("aboutscreen.xml");  
    		int size = inputstrm.available();    		     
			byte[] buffer = new byte[size];   		
			inputstrm.read(buffer);   		
			inputstrm.close();
			String text = new String(buffer);   		    		    	
			Document doc = Jsoup.parse(text);
			name = doc.getElementsByTag("name");
			edition = doc.getElementsByTag("edition");
			device = doc.getElementsByTag("device");
			version = doc.getElementsByTag("version");
			deviceType = doc.getElementsByTag("devicetype");
			server = doc.getElementsByTag("server");
			url = doc.getElementsByTag("SOAP_SERVICE_URL");
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			androidOS = Build.VERSION.RELEASE;
			manufacturer = Build.MANUFACTURER;
			editionStr = android.os.Build.MODEL;
			String urlStr = url.text().toString().trim();
			//SapGenConstants.showLog(" urlStr: "+urlStr);
            int firstIndex = urlStr.indexOf("//");
			//SapGenConstants.showLog(" firstIndex: "+firstIndex);
            String urlName1 = urlStr.substring(firstIndex+2, urlStr.length());
			//SapGenConstants.showLog(" urlName1: "+urlName1);
            int thirdIndex = urlName1.indexOf(":");
            if(thirdIndex != -1){
    			//SapGenConstants.showLog(" thirdIndex: "+thirdIndex);
    			urlName = urlName1.substring(0, thirdIndex);
    			//SapGenConstants.showLog(" urlName: "+urlName);
            }else{
                int thirdIndex1 = urlName1.indexOf("/");
    			//SapGenConstants.showLog(" thirdIndex1: "+thirdIndex1);
    			urlName = urlName1.substring(0, thirdIndex1);
    			//SapGenConstants.showLog(" urlName: "+urlName);            	
            }
    		linkText = "File Name: "+name.text()+"\n"+edition.text()+":"+" "+device.text()+"\n"+deviceType.text()+" "+manufacturer+" "+editionStr+"\n"+version.text()+" "+androidOS+"\n"+"GDID: "+imeno+"\n"+"Server: "+urlName+"\n";
    		for(int i=0;i<PriceSoapList.size();i++){
    			matnrStr+= PriceSoapList.get(i).toString()+", ";
    		}
    		SapGenConstants.showLog(" matnrStr: "+matnrStr);          
    		matnrStr+="\n"+"Error: "+taskErrorMsgStr;
    	   linkText =linkText+"\n"+ "Event Name: "+"SALES-ORDER-CREATE"+ "\n" +"DATA-TYPE: "+"ZGSEVAST_SDCRTN20"+ "\n" +"Customer ID: "+custID+ "\n" +"Requested Date: "+dateStr+ "\n" +"Material Numbers: "+matnrStr;  		
			String to = "gss.mobile@globalsoft-solutions.com";
			String subject = "Email on Error";
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			email.putExtra(Intent.EXTRA_SUBJECT, subject);
			email.putExtra(Intent.EXTRA_TEXT, linkText);
			email.setType("message/rfc822");   		 
			startActivity(Intent.createChooser(email, "Choose an Email client"));
			alertDialog.dismiss();
    	}
    	catch(Exception adf){
    		SapGenConstants.showErrorLog("Error in showEmailActivity : "+adf.getMessage());
    	}		
	}//showEmailActivity       
    
    public void updateGettingPriceListServerResponse(SoapObject soap){	
        String[] resArray = null;
        String[] metaProductLength = null;
        String[] metaMainPriceLength = null;
        String[] docTypeArray = new String[3];
		String[] productArray = null;
		String[] mainPriceArray = null;
        try{ 
        	if(soap != null){    			
    			if(priceList != null)
    				priceList.clear();    		
    			if(mainPriceList != null)
    				mainPriceList.clear();
    			if(metaProdListArray != null)
    				metaProdListArray.clear();  
    			if(metaMainPriceArray != null)
    				metaMainPriceArray.clear();	
    			           	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resIndex = 0, resC = 0, eqIndex = 0, metaSize = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
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
                                resC = 0;
                                indexA = 0;
                                resIndex = 0;
                                indexB = result.indexOf(delimeter);
                                int index1 = 0;
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    if(resC > 1){
                                    	metaMainPriceArray.add(res);
                                    }
                                    if(resC == 0){
                                    	docTypeStr = res;
                                    }
                                    if(resC == 1){
	                                    String[] respStr = res.split(";");
	                                    if(respStr.length >= 1){
	                                    	String respTypeData = respStr[0];
	                                    	index1 = respTypeData.indexOf("=");
	                                    	index1 = index1+1;
	                                        String respType = respTypeData.substring(index1, respTypeData.length());	                                    	
	                                    	String rowCountStrData = respStr[1];
	                                    	index1 = rowCountStrData.indexOf("=");
	                                    	index1 = index1+1;
	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
                                    		docTypeArray[0] = docTypeStr;
                                        	mainPriceListCount = Integer.parseInt(rowCount);
                                        	mainPriceListType = respType;    
	                                    }
                                    }
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaMainPriceLength = new String[metaSize];
                                metaMainPriceArray.add(last);
                            }	                    	
	                    	if(j == 3){
                            	result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                eqIndex = result.indexOf("=");
                                eqIndex = eqIndex+1;
                                firstIndex = firstIndex + 3;
                                docTypeStr = result.substring(eqIndex, (firstIndex-3));
                                result = result.substring(firstIndex);                      
                                resC = 0;
                                indexA = 0;
                                resIndex = 0;
                                indexB = result.indexOf(delimeter);
                                int index1 = 0;
                                while (indexB != -1) {
                                    res = result.substring(indexA, indexB);
                                    indexA = indexB + delimeter.length();
                                    indexB = result.indexOf(delimeter, indexA);
                                    if(resC > 1){
                                    	metaProdListArray.add(res);
                                    }
                                    if(resC == 0){
                                    	docTypeStr = res;
                                    }
                                    if(resC == 1){
	                                    String[] respStr = res.split(";");
	                                    if(respStr.length >= 1){
	                                    	String respTypeData = respStr[0];
	                                    	index1 = respTypeData.indexOf("=");
	                                    	index1 = index1+1;
	                                        String respType = respTypeData.substring(index1, respTypeData.length());
	                                    	String rowCountStrData = respStr[1];
	                                    	index1 = rowCountStrData.indexOf("=");
	                                    	index1 = index1+1;
	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
	                                       	docTypeArray[1] = docTypeStr;
                                    		//DBConstants.PRODUCT_LIST_DB_TABLE_NAME = docTypeStr;
                                    		productsListCount = Integer.parseInt(rowCount);
                                    		productsListType = respType;
	                                    }
                                    }
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaProductLength = new String[metaSize];
                                metaProdListArray.add(last);
                            }	    	                    	
	                        if(j > 3){
	                        	result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	   
	                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
		                        	resArray = new String[metaMainPriceLength.length]; 
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
	 	                            mainPriceArray = new String[resArray.length];
	 	                            mainPriceArray = Arrays.copyOf(resArray, resArray.length, String[].class);
 	                                mainPriceList.add(mainPriceArray);
	                            }else if(docTypeStr.equalsIgnoreCase(docTypeArray[1])){
		                        	resArray = new String[metaProductLength.length]; 
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
	 	                            productArray = new String[resArray.length];
	 	                            productArray = Arrays.copyOf(resArray, resArray.length, String[].class);
 	                                priceList.add(productArray);
	                            } 
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SapGenConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                serverErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {  
			                                SapGenConstants.showLog(serverErrorMsgStr);
			                                SapGenConstants.showErrorDialog(SalesOrderCreationTablet.this, serverErrorMsgStr);			                                
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
            stopProgressDialog();
            SapGenConstants.showErrorLog("On updateGettingPriceListServerResponse : "+sff.toString());
        } 
        finally{        	     	
        	SapGenConstants.showLog("priceList Size : "+priceList.size());
        	SapGenConstants.showLog("mainPriceList Size : "+mainPriceList.size());
        	if(priceList.size() > 0){    			    						
            	try {
            		Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertProductGetPriceDataIntoDB();
                			}catch(Exception e1){
                				SapGenConstants.showErrorLog("Error in updateGettingPriceListServerResponse Thread:"+e1.toString());
                			}
                			productsData_Handler.post(priceview);
                            stopProgressDialog();
                		}        	            
        			};
        	        t.start();	
            	}
    			catch(Exception adsf1){
    	            stopProgressDialog();
    	            SapGenConstants.showErrorLog("On updateGettingPriceListServerResponse : "+adsf1.toString());
    			}  
        	}else{
        		stopProgressDialog();
        		initPriceDBConnection();
    			SapGenConstants.showErrorDialog(this, "Data Not available");
        	}
        }
    }//fn updateGettingPriceListServerResponse
    
    final Runnable priceview = new Runnable(){
        public void run()
        {
        	try{
        		initPriceDBConnection();
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    
    private void initPriceDBConnection(){
		try{
			//ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);
			/*DataBasePersistent.setTableContent(metaHeadArray);
			DataBasePersistent.setColumnName(metaHeadArray);*/
			if(dbObj!=null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);
			dbObj.getColumns();	  			
    			tableExits = dbObj.checkTable();
    			if(tableExits)
    				priceList = dbObj.readListDataFromDB(this);         	
    		if(priceList == null)
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper();  
    		proPriceMap = new HashMap<String, String>();
    		if(proPriceMap != null && proPriceMap.size() > 0){
    			proPriceMap.clear();
    		}
    		HashMap<String, String> proListMap = new HashMap<String, String>();
    		String id = "", price = "";   		
			if(priceList != null && priceList.size() > 0){
				//valFlag = 1;
	    		for (int i = 0; i < priceList.size(); i++) {
	    			proListMap = (HashMap<String, String>) priceList.get(i);   
	    			id = (String) proListMap.get(DBConstants.MATNR_COLUMN_NAME);   
	    			price = (String) proListMap.get(DBConstants.NETWR_COLUMN_NAME); 
	    			proPriceMap.put(id, price);
	    			priceupdate =1;
	    		}
			}
    		
			//ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME);
			if(dbObj!=null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME);
			dbObj.getColumns();	  
    			tableExits = dbObj.checkTable();
    			if(tableExits){
    				//String matid ="";   				
    				totalPriceStr = dbObj.getParticularField(DBConstants.NETWR_COLUMN_NAME, DBConstants.KUNAG_COLUMN_NAME, custID);
    				mainPriceList = dbObj.readListDataFromDB(this);      
    			}
        		if(priceList == null)
        			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
				if(totalPriceStr != null && totalPriceStr.length() > 0){
					priceupdate =1;
			    	//custnameLinear.setVisibility(View.VISIBLE);
					//totprilinear.setVisibility(View.VISIBLE);
					//totaPriceTV.setText(" "+totalPriceStr);
					SapGenConstants.showLog("totaPriceTV: "+totalPriceStr);
				}else{
					//totprilinear.setVisibility(View.GONE);
				}
    		dbObj.closeDBHelper(); 
    		dbObj = null;
				dbObj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		if(copyflag!=1){
    		 headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);	        	            	 
   	   		 headerLinear.setVisibility(View.VISIBLE);
   	   	 	 headerLinear.removeAllViews();					
   	   		 headerLinear.setOrientation(LinearLayout.VERTICAL);
   	   		custnameLinear.removeAllViews();	
    		}else{
    			 headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);	        	            	 
       	   		 headerLinear.setVisibility(View.VISIBLE);
       	   		 headerLinear.removeAllViews();	
    			//custnameLinear.removeAllViews();	
    		}
    		if(flag == true && backflag==0){
    			displayUI(DBConstants.HEADER_CREATE_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headerLinear);	
    		}else{
    			 displayUI(DBConstants.SO_CUSTOMER_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headerLinear);
    		}    		 
    		initItemHeadLayout();	
    		//drawItemSubLayout();
		}catch(Exception sggh){
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On initPriceDBConnection : "+sggh.toString());
		}
    }//fn initPriceDBConnection
    
    
    private void insertProductGetPriceDataIntoDB(){
    	try {
			if(metaProdListArray != null && metaProdListArray.size() > 0){
				if(DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME != null && DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME;
	    		}
				if(priceList != null && priceList.size() > 0){
					String columnLists = "";
					if(metaProdListArray != null && metaProdListArray.size() > 0){
						for(int i=0; i<metaProdListArray.size(); i++){
							if( i == (metaProdListArray.size() - 1)){
								columnLists += metaProdListArray.get(i).toString().trim();
							}else{
								columnLists += metaProdListArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);					
					/*if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
					dbObjColumns.closeDBHelper();	*/				
					if(metaProdListArray != null && metaProdListArray.size() > 0){
						metaProdListArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								SapGenConstants.showLog("  "+separated[i]);
								metaProdListArray.add(separated[i].toString().trim());
							}
						}
					}						
										
					DataBasePersistent.setTableContent(metaProdListArray);
					DataBasePersistent.setColumnName(metaProdListArray);
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME);
					dbObj.dropTable(SalesOrderCreationTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(SalesOrderCreationTablet.this);
					}	
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((productsListCount == 0) && (priceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (priceList.size() > 0)){
							dbObj.clearListTable();
							insertProductGetPriceListDataIntoDB(priceList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((productsListCount == 0) && (priceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (priceList.size() == 0)){
						}
						else if((productsListCount > 0) && (priceList.size() > 0)){
							dbObj.clearListTable();
							insertProductGetPriceListDataIntoDB(priceList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((productsListCount == 0) && (priceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (priceList.size() == 0)){
						}
						else if((productsListCount > 0) && (priceList.size() > 0)){
							dbObj.clearListTable();
							insertProductGetPriceListDataIntoDB(priceList);
						}
					}					
				}
				dbObj.closeDBHelper();
			}        	
			
			if(metaMainPriceArray != null && metaMainPriceArray.size() > 0){    	
	    		if(DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME != null && DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME;
	    		}
				String columnLists = "";
				if(metaMainPriceArray != null && metaMainPriceArray.size() > 0){
					for(int i=0; i<metaMainPriceArray.size(); i++){
						if( i == (metaMainPriceArray.size() - 1)){
							columnLists += metaMainPriceArray.get(i).toString().trim();
						}else{
							columnLists += metaMainPriceArray.get(i).toString().trim()+":";
						}
					}
				}
				SapGenConstants.showLog("columnLists: "+columnLists);
				SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);					
			/*	if(dbObjColumns == null)
					dbObjColumns = new ProductColumnDB(ctx);
				dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
				dbObjColumns.closeDBHelper();			*/		
				if(metaMainPriceArray != null && metaMainPriceArray.size() > 0){
					metaMainPriceArray.clear();
				}					
				if(columnLists != null && columnLists.length() > 0){
					String[] separated = columnLists.split(":");
					if(separated != null && separated.length > 0){
						for(int i=0; i<separated.length; i++){
							SapGenConstants.showLog("  "+separated[i]);
							metaMainPriceArray.add(separated[i].toString().trim());
						}
					}
				}
				DataBasePersistent.setTableContent(metaMainPriceArray);
				DataBasePersistent.setColumnName(metaMainPriceArray);
				if(mainPriceList != null && metaMainPriceArray.size() > 0){
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME);				
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(SalesOrderCreationTablet.this);
					}		
					if(mainPriceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((mainPriceListCount == 0) && (mainPriceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((mainPriceListCount > 0) && (mainPriceList.size() > 0)){
							dbObj.clearListTable();
							insertProductMainPriceListDataIntoDB(mainPriceList);
						}
					}
					if(mainPriceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((mainPriceListCount == 0) && (mainPriceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((mainPriceListCount > 0) && (mainPriceList.size() == 0)){
						}
						else if((mainPriceListCount > 0) && (mainPriceList.size() > 0)){
							dbObj.clearListTable();
							insertProductMainPriceListDataIntoDB(mainPriceList);
						}
					}
					if(mainPriceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((mainPriceListCount == 0) && (mainPriceList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((mainPriceListCount > 0) && (mainPriceList.size() == 0)){
						}
						else if((mainPriceListCount > 0) && (mainPriceList.size() > 0)){
							dbObj.clearListTable();
							insertProductMainPriceListDataIntoDB(mainPriceList);
						}
					}					
				}
			}
			dbObj.closeDBHelper();			
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On insertProductGetPriceDataIntoDB: "+e.toString());
		}
	}// insertProductGetPriceDataIntoDB

    public void insertProductGetPriceListDataIntoDB(ArrayList priceListArray){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.SO_GETPRICE_LIST_DB_TABLE_NAME);			
			if(priceListArray != null && priceListArray.size() > 0){
				dbObj.insertDetails(priceListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductGetPriceListDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductGetPriceListDataIntoDB

    public void insertProductMainPriceListDataIntoDB(ArrayList productTotalPriceListArray){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME);
			boolean isExits = dbObj.checkTable();
			SapGenConstants.showLog(DBConstants.So_GETNETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
			if(productTotalPriceListArray != null && productTotalPriceListArray.size() > 0){
				dbObj.insertDetails(productTotalPriceListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductMainPriceListDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductMainPriceListDataIntoDB
    
/*    private void prefillItemData(){
    	try{
    		if(priceHeadVector != null){
    			if(priceHeadVector.size() > 0){
    				SalesOrdProPriceHeadConstraints headCategory = null;
    				headCategory = (SalesOrdProPriceHeadConstraints) priceHeadVector.elementAt(0);
    				if(headCategory != null){
    					String headValue = headCategory.getNetValue().trim()+" "+headCategory.getDocCurrency().trim();
    					if(soValueET != null){
    						soValueET.setText(headValue);
    					}
    				}
    			}
    		}    		
    		if(priceItemVector != null){
    			SalesOrdProPriceItemConstraints itemCategory = null;
    			String itemValue = "",itemUnit="";
    			for(int l=0; l<priceItemVector.size(); l++){
    				itemCategory = (SalesOrdProPriceItemConstraints) priceItemVector.elementAt(l);
    				if(itemCategory != null){
    					//itemValue = itemCategory.getNetValue().trim()+" "+itemCategory.getDocCurrency().trim();
    					itemValue = itemCategory.getNetValue().trim(); 
    					itemUnit =  itemCategory.getDocCurrency().trim();
    					if(valueTxtView[l] != null)
    						valueTxtView[l].setText(itemValue);
    						materialTxtView[l].setText(itemUnit);
    				}
    			}
    		}
			getQty();
    	}
    	catch(Exception adf){
    		SalesOrderConstants.showErrorLog("On prefillItemData : "+adf.toString());
    	}
    }//fn prefillItemData    
*/    
    public void updateServerResponse(SoapObject soap){	
        String[] resArray = null;
        String[] metaProductLength = null;
        String[] docTypeArray = new String[3];
		String[] productArray = null;
        try{ 
        	if(soap != null){    			
    			if(custArrList != null)
    				custArrList.clear();    
    			if(metaProdListArray != null)
    				metaProdListArray.clear();  	
    			           	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resIndex = 0, resC = 0, eqIndex = 0, metaSize = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                //SapGenConstants.showLog("propsCount : "+propsCount);
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
                                    	metaProdListArray.add(res);
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
	                                    		docTypeArray[0] = docTypeStr;
	                                    		DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME = docTypeStr;
		                                    	SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		custListCount = Integer.parseInt(rowCount);
	                                    		custListType = respType;
	                                        }                                     
	                                    }
                                    }
                                    //SapGenConstants.showLog("resC :"+ resC);
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaProductLength = new String[metaSize];
                                metaProdListArray.add(last);
                            }
	                    	
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	                            
	                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
		                        	resArray = new String[metaProductLength.length]; 
	                            	result = result.substring(firstIndex);
	 	                            //SapGenConstants.showLog("result r :"+ result);
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
 		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);                             
	 	                            productArray = new String[resArray.length];
	 	                            productArray = Arrays.copyOf(resArray, resArray.length, String[].class);
	 	                            custArrList.add(productArray);
	                            } 	                           
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SapGenConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                serverErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {  
			                                SapGenConstants.showLog(serverErrorMsgStr);
			                                SapGenConstants.showErrorDialog(SalesOrderCreationTablet.this, serverErrorMsgStr);
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
            stopProgressDialog();
            SapGenConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{        	   
        	SapGenConstants.showLog("custArrList Size : "+custArrList.size());
        	SapGenConstants.CustDetailsArr.addAll(custArrList);
        	if(custArrList.size() > 0){    			    						
            	try {
            		Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertCustDataIntoDB();
                			}catch(Exception e1){
                				SapGenConstants.showErrorLog("Error in deleteInvSelctdData Thread:"+e1.toString());
                			}
                			productsData_Handler.post(custview);
                            stopProgressDialog();
                		}        	            
        			};
        	        t.start();	
            	}
    			catch(Exception adsf1){
    	            stopProgressDialog();
    	            SapGenConstants.showErrorLog("On updateServerResponse : "+adsf1.toString());
    			}  
        	}else{
        		stopProgressDialog();
        		initCustDBConnection(customerIdStr);
    			SapGenConstants.showErrorDialog(this, "Data Not available");
        	}
        }
    }//fn updateServerResponse
    
    private void initCustDBConnection(String customerIdStr){
		try{
			//setTitleValue();
			/*if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
				DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    		}				
            	*/            
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME);
			dbObj.getColumns();	  
			/*if(offlinemode ==1){					
            	SapGenConstants.showLog("offlinemode"+offlinemode);
            	dbObj.getColumns();	         	            		            	
            }else{
            	dbObj.setTableName_ColumnName(this,DBConstants.TABLE_SALESORDER_HEAD_LIST,metaHeadArray);
            }	*/							
			tableExits = dbObj.checkTable();
			if(tableExits)
				custArrList = dbObj.readListDataFromDB(this);
			if(custArrList != null)
				//mattCopyArrList = (ArrayList)mattArrList.clone();
			SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);
		/*
			if(custArrList != null)
				mattArrList = (ArrayList)mattArrList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");*/
			dbObj.closeDBHelper();  
			
			SapGenConstants.showLog("custArrList size:"+ custArrList.size());
			HashMap<String, String> stockMap = null;
            String matIdStrVal = "", id = "", desc = "";
        	/*if(mattArrList != null && mattArrList.size() > 0 ){
        		_options =  new CharSequence[mattArrList.size()];
				for (int i = 0; i < mattArrList.size(); i++) {
					stockMap = (HashMap<String, String>) mattArrList.get(i);  
					id = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN);
					desc = (String) stockMap.get(DBConstants.SO_HEAD_COL_KUNAG);
					matIdStrVal = "";
					if(desc != null && desc.length() > 0){
						matIdStrVal += id+" ("+desc+")";
					}else{
						matIdStrVal += id;
					}
					//_options[i]= matIdStrVal;
				}
        	}  */   
        	dbObj.closeDBHelper();        
        		prefillCustomerData();
		}catch(Exception sfg1){
			dbObj.closeDBHelper();	
			SapGenConstants.showErrorLog("Error in initCustDBConnection : "+sfg1.toString());
		}	
	}//end of initDBConnection
       
    private void insertCustDataIntoDB() {
    	try {
			if(metaProdListArray != null && metaProdListArray.size() > 0){
				if(DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME != null && DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME;
	    		}
				if(custArrList != null && custArrList.size() > 0){
					String columnLists = "";
					if(metaProdListArray != null && metaProdListArray.size() > 0){
						for(int i=0; i<metaProdListArray.size(); i++){
							if( i == (metaProdListArray.size() - 1)){
								columnLists += metaProdListArray.get(i).toString().trim();
							}else{
								columnLists += metaProdListArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);	
					
					if(metaProdListArray != null && metaProdListArray.size() > 0){
						metaProdListArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								//SapGenConstants.showLog("  "+separated[i]);
								metaProdListArray.add(separated[i].toString().trim());
							}
						}
					}						
						
					DataBasePersistent.setTableContent(metaProdListArray);
					DataBasePersistent.setColumnName(metaProdListArray);
										
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME);
					dbObj.dropTable(SalesOrderCreationTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(SalesOrderCreationTablet.this);
					}	
					if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((custListCount == 0) && (custArrList.size() == 0)){
	    					//SalesOrderDBOperations.deleteAllTableDataFromDB(SalesOrderMainScreenTablet.this, SalesOrderCP.SO_HEAD_OP_CONTENT_URI);
	    					dbObj.clearListTable();
	        			}
	    				else if((custListCount > 0) && (custArrList.size() > 0)){
	    					dbObj.clearListTable();
	    					insertCustomerListDataIntoDB(custArrList);
	        			}
	    			}
	    			if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
	    				if((custListCount == 0) && (custArrList.size() == 0)){
	    					dbObj.clearListTable();
	        			}
	    				else if((custListCount > 0) && (custArrList.size() == 0)){
	    					//initDBConnection();
	        			}
	    				else if((custListCount > 0) && (custArrList.size() > 0)){
	    					dbObj.clearListTable();
	    					insertCustomerListDataIntoDB(custArrList);
	    						        				    			
				}	
	    			
				}
				dbObj.closeDBHelper();												
			}  
		}	
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On insertCustDataIntoDB: "+e.toString());
			dbObj.closeDBHelper();
		}
	}// insertCustDataIntoDB
    
    public void insertCustomerListDataIntoDB(ArrayList custListArray){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.SO_CUSTOMER_LIST_DB_TABLE_NAME);			
			if(custListArray != null && custListArray.size() > 0){
				dbObj.insertDetails(custListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertCustomerListDataIntoDB : "+e.toString());
		}        	
	}//fn insertCustomerListDataIntoDB
    
    final Runnable custview = new Runnable(){
        public void run()
        {
        	try{
        		initCustDBConnection(customerIdStr);
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };   
    
    private void prefillCustomerData(){
    	try{
    		if(custArrList != null){
    			if(custArrList.size() > 0){
    	    		custDetArr = getCustomerList();
    	    		if(custDetArr.size()>1){
    	    			//showSalesOrdCustomerSelScreen();	
    	    			showCustomerAlert();
    	    		}else{
    	    			if(copyflag==1 || socrtFlag==1){
    	    				showCustomerHeaderInfo(idstr,0);
    	    			}else{
    	    				showCustomerHeaderInfo(refID,0);
    	    			}	  	    				
    	    		}
    	    		/*else
    	    			SalesOrderConstants.showErrorDialog(this, "No results available. Try a different name!");*/
    			}
    			else
	    			SalesOrderConstants.showErrorDialog(this, "No results available. Try a different name!");
    		}
    		else
    			SalesOrderConstants.showErrorDialog(this, "No results available. Try a different name!");
    	}
    	catch(Exception adf){
    		SalesOrderConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData    
    
    private ArrayList getCustomerList(){
    	String choices[] = null;
        try{
        	if(custDetArr!=null)
        		custDetArr.clear();
            if(custArrList != null){
            	 HashMap<String, String> customerMap = null; 
            	//SalesOrdProCustConstraints empObj = null;
                //String nameStr = "", idstr="", combStr="";                
                int arrSize = custArrList.size();
                choices = new String[arrSize];
                
                for(int h=0; h<arrSize; h++){
                	customerMap = (HashMap<String, String>) custArrList.get(h); 	
                    if(customerMap != null){
                    	nameStr = (String) customerMap.get(DBConstants.NAME1_COLUMN_NAME);  
                    	idstr = (String) customerMap.get(DBConstants.KUNNR_COLUMN_NAME);  
                        combStr = nameStr+":"+idstr;
                       
                        //choices[h] = combStr;
                        custDetArr.add(combStr);
                    }
                }
            }
            SalesOrderConstants.showLog("Size of choices : "+choices.length);
        }
        catch(Exception sfg){
        	SalesOrderConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        return custDetArr;
    }//fn getCustomerList
    
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
				public void onClick(DialogInterface dialog, int which) {						
					SalesOrderConstants.showLog("which : "+which);
					selctdPos=which;
					SalesOrderConstants.showErrorLog("Selected Position : "+selctdPos);
					showCustomerHeaderInfo(idstr,selctdPos);
				/*	if(copyflag==1 || socrtFlag==1){
						showCustomerHeaderInfo(idstr,selctdPos);
					}*//*else{
						showCustDetails(selctdPos);
					}*/
					//showCustomerHeaderInfo(idstr,selctdPos);
					alertDialog.dismiss();
				}
			});
    		alertDialog = builder.create();    		  
    		alertDialog.show();    		 
    	}catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in showAlert : "+sfg.toString());
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
        		SalesOrderConstants.showErrorLog(e.getMessage());
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
            	SalesOrderConstants.showErrorLog("Error in ContactListAdapter : "+qw.toString());
			}
            
           return convertView;	           
		}
		
		public void removeAllTasks() {
            notifyDataSetChanged();
        } 		
    }//End of ContactListAdapter
    
   /* public void showCustDetails(int selIndex){
    	SalesOrdProCustConstraints CustempObj = null;
    	String custNameStr="";
    	custnameLinear.setVisibility(View.VISIBLE);
    	 if(searchLinear != null)
    		 searchLinear.setVisibility(View.GONE);
    	 
    	CustempObj = (SalesOrdProCustConstraints)custArrayList.get(selIndex);
    	if(CustempObj != null){
    		custNameStr= " "+CustempObj.getName().trim()+"("+ CustempObj.getCustomerNo().trim()+")";
    	}
    	socustnameTV.setText(custNameStr);
    }//showCustDetails
*/    
    private void showCustomerHeaderInfo(String custIdStr, int selIndex){
    	DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    	try{
    		if(notAlertStr==true){							 	
				SapGenConstants.showLog("errMsg:"+errMsg);
				SapGenConstants.showLog("colId:"+colId);
				error_header_linear = (LinearLayout) findViewById(R.id.error_header_linear);
				error_header_linear.setOrientation(LinearLayout.HORIZONTAL);
					error_header_linear.setVisibility(View.VISIBLE);
					ImageView errorindicator = new ImageView(this);
					ImageView delete = new ImageView(this);
					errorindicator.setImageResource(R.drawable.notify);					
					delete.setImageResource(R.drawable.delete1);					
					delete.setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							error_header_linear.setVisibility(View.GONE);
							notAlertStr=false;
							deleteData();
							ShowAlertQ();							 	
						}											
                    });
					TextView errorLbl = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);		
					errorLbl.setText(errMsg);	
					error_header_linear.addView(errorindicator);
					error_header_linear.addView(errorLbl);
					error_header_linear.addView(delete);
					/*notAlertStr=false;
					deleteData();		*/
			}
    		SapGenConstants.showLog("custIdStr and selIndex "+custIdStr +selIndex );      
    		/*if(backflag ==1)
    			copyflag =0;*/
    		SapGenConstants.showLog("custArrList size "+custArrList.size());        	    			   		
    			custMap = (HashMap<String, String>) custArrList.get(selIndex);  
            	SapGenConstants.showLog("custMap "+custMap);    
            	customerIdStr = (String) custMap.get(DBConstants.KUNAG_COLUMN_NAME);
            	custID =  (String) custMap.get(DBConstants.KUNNR_COLUMN_NAME);
            	SapGenConstants.showLog("custID "+custID); 
    	  		   		  		       	
        	if(searchLinear != null)
       		 searchLinear.setVisibility(View.GONE);   
        	if(backflag ==1 ||multListviewFlag == 0 || notAlertStr==true){
        		headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);	        	            	 
        		 headerLinear.setVisibility(View.VISIBLE);
        	 	 headerLinear.removeAllViews();					
        		 headerLinear.setOrientation(LinearLayout.VERTICAL);
    			 displayUI(DBConstants.SO_CUSTOMER_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headerLinear);        		
        	}else if(backflag ==1 || multListviewFlag == 1){
        		 headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);	        	            	 
         		 headerLinear.setVisibility(View.VISIBLE);
         	 	 headerLinear.removeAllViews();					
         		 headerLinear.setOrientation(LinearLayout.VERTICAL);
     			 displayUI(DBConstants.HEADER_CREATE_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headerLinear);
        	}else{
        		custnameLinear = (LinearLayout) findViewById(R.id.socustnamelinear);
      			 if(custnameLinear != null)
      				 custnameLinear.setVisibility(View.VISIBLE);
      			 	custnameLinear.removeAllViews();					
      			 	custnameLinear.setOrientation(LinearLayout.VERTICAL);
              		 displayUI(DBConstants.SO_CUSTOMER_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, custnameLinear);
        	}       	     	    	
    	}
    	catch(Exception sfgh){
    		SalesOrderConstants.showErrorLog("Error in showCustomerHeaderInfo : "+sfgh.toString());
    	}
    }//fn showCustomerHeaderInfo
    
    private void ShowAlertQ() {
    	 builder = new AlertDialog.Builder(this); 		 
		  builder.setIcon(R.drawable.notify);		
		  builder.setTitle("Alert");			  
		 builder.setMessage("Do you want to delete this Customer details from QueueProcessor?..");
		 builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {	
	            	deleteDataInQueueProcessor();
	            }
	        });                
	        builder.setNegativeButton(R.string.CANCEL, null);
		  alertDialog = builder.create();    		  
		  alertDialog.show();	
		
	}//	
    
    private void deleteData() {   	
    	SalesOrderDBOperations.deleteCustTableDataFromDB(this, SalesOrderCP.SO_CREATE_SCREEN_CONTENT_URI,uniqId);			
	}//	
    
    private void deleteDataInQueueProcessor(){
    	//SapQueueProcessorDBOperations.deleteCusIdData(this,uniqId);
    }//
   /* private void hideCustomerHeaderInfo(){
    	try{
    		socrtFlag=1;
    		if(headerLinear != null)
				headerLinear.setVisibility(View.GONE);
    		
    		searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);	
			if(searchLinear != null)
				searchLinear.setVisibility(View.VISIBLE);		
    	}
    	catch(Exception sfgh){
    		SalesOrderConstants.showErrorLog("Error in hideCustomerHeaderInfo : "+sfgh.toString());
    	}
    }//fn hideCustomerHeaderInfo    
*/    
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			SalesOrderConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog    
    
    //start date click listener
    private OnClickListener reqBy_dateListener = new OnClickListener(){
        public void onClick(View v){
        	showDialog(START_DATE_DIALOG_ID);
        }
    };
    
    private OnClickListener po_dateListener = new OnClickListener(){
        public void onClick(View v){
        	showDialog(PO_DATE_DIALOG_ID);
        }
    };

    
    /*//start time click listener
    private OnClickListener reqBy_timeListener = new OnClickListener(){
        public void onClick(View v){
        	showDialog(START_TIME_DIALOG_ID);
        }
    };*/
    
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                			startDateSetListener,
                			startYear, startMonth, startDay);   
            case PO_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                			poDateSetListener,
                			POstartYear, POstartMonth, POstartDay);   
            /*case START_TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                			startTimeSetListener, startHour, startMinute, false);  */ 
        }
        return null;
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(startYear, startMonth, startDay);
                break;
            case PO_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(POstartYear, POstartMonth, POstartDay);
                break;
        }
    }    
    
    private void updateDisplay_start_date() {    	
    	int month_value = startMonth + 1;
    	//int month_value = startMonth ;
    	String month_dec = getMonthValue(month_value);    	
    	SalesOrderConstants.showLog("month_value : "+month_value);
    	startMonth = month_value;
    	reqBy_date.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(startDay).append(" ")
                    .append(month_dec).append(" ")                    
                    .append(startYear).append(" "));
    }//updateDisplay_start_date
    
    private void updatePO_start_date() {    	
    	int month_value = POstartMonth + 1;
    	//int month_value = POstartMonth;
    	String month_dec = getMonthValue(month_value);    	
    	SalesOrderConstants.showLog("month_value : "+month_value);
    	POstartMonth = month_value;
    	poDateTV.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(POstartDay).append(" ")
                    .append(month_dec).append(" ")                    
                    .append(POstartYear).append(" "));
    }//updatePO_start_date
    
    private void Display_start_date() {    	
    	/*int month_value = startMonth + 1;
    	//int month_value = startMonth ;
    	String month_dec = getMonthValue(month_value);    	
    	SalesOrderConstants.showLog("month_value : "+month_value);
    	startMonth = month_value;*/
    	reqBy_date.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(startDay).append(" ")
                    .append(startMonth).append(" ")                    
                    .append(startYear).append(" "));
    }//updateDisplay_start_date
    
    private void Display_PO_date() {    	
    	
    	poDateTV.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                		.append(POstartDay).append(" ")
                        .append(POstartMonth).append(" ")                    
                        .append(POstartYear).append(" "));
    }//updateDisplay_start_date
   /*private void updateDisplay_start_time() {
    	reqBy_time.setText(
            new StringBuilder()
		            .append(pad(startHour)).append(":")
		            .append(pad(startMinute)));
    }*/
    
    private DatePickerDialog.OnDateSetListener startDateSetListener =
    		new DatePickerDialog.OnDateSetListener() {
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		    	startYear = year;
		    	startMonth = monthOfYear;
		    	startDay = dayOfMonth;
		        updateDisplay_start_date();
		       
		    }
	};

    private DatePickerDialog.OnDateSetListener poDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		    	POstartYear = year;
		    	POstartMonth = monthOfYear;
		    	POstartDay = dayOfMonth;	    	                   
		        updatePO_start_date();
		    }
    };

    /*private TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                	startHour = hourOfDay;
                	startMinute = minute;
                    updateDisplay_start_time();
                }
            };*/
            

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    public String getMonthValue(int month_value){
    	if(month_value>12){
    		month_value=1;
    	}
    	String month_dec = null;
    	if(month_value == 1){
    		month_dec = "Jan";
    	}
    	else if(month_value == 2){
    		month_dec = "Feb";
    	}
    	else if(month_value == 3){
    		month_dec = "Mar";
    	}
    	else if(month_value == 4){
    		month_dec = "Apr";
    	}
    	else if(month_value == 5){
    		month_dec = "May";
    	}
    	else if(month_value == 6){
    		month_dec = "Jun";
    	}
    	else if(month_value == 7){
    		month_dec = "Jul";
    	}
    	else if(month_value == 8){
    		month_dec = "Aug";
    	}
    	else if(month_value == 9){
    		month_dec = "Sep";
    	}
    	else if(month_value == 10){
    		month_dec = "Oct";
    	}
    	else if(month_value == 11){
    		month_dec = "Nov";
    	}
    	else if(month_value == 12){
    		month_dec = "Dec";
    	}
    	return month_dec;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	try {   			
    		SalesOrderConstants.showLog("Request Code "+requestCode);
    		SalesOrderConstants.showLog("Result Code "+resultCode);
    		SalesOrderConstants.showLog("Result Code "+RESULT_OK);
    		
    		if(requestCode == SapGenConstants.SALESORD_CRT_CUSTSEL_SCREEN){
    			if(resultCode == RESULT_OK){
    				if(data != null){
    					String custIdStr = data.getStringExtra("custIdStr");
    					int selCustPos = data.getIntExtra("selCustPos", 0);
    					SalesOrderConstants.showLog("Result from customer : "+custIdStr+" : "+selCustPos);
    					showCustomerHeaderInfo(custIdStr, selCustPos);
    				}
    			}
    		}
    		else if(requestCode == SapGenConstants.SALESORD_CRT_MATTSEL_SCREEN){
    			if(resultCode == RESULT_OK){
    				if(data != null){
    					try{
    						selMattArrList = (ArrayList)data.getSerializableExtra("selMatIds");
        					SalesOrderConstants.showLog("Result From Material : "+selMattArrList.size());
        					//if(mattItemArrLst != null){    						
        						if(selMattArrList != null){
    		    					//mattItemArrLst.addAll(selMattArrList);
    		    					if(flag == true){   								
        								multListviewFlag = 1;        								
        								stocksSelectdItemArrList1.addAll(selMattArrList); 
        								//CommonMattList.addAll(selMattArrList); 
        								//SalesOrderConstants.showLog("stocksSelectdItemArrList1 Size after adding matterial list: "+stocksSelectdItemArrList1.size());
        								SalesOrderConstants.showLog("multListviewFlag value : "+multListviewFlag);
        								idstr= custID;
    		    					}else{
    		    						multListviewFlag = 0;   		    						
    		    						stocksSelectdItemArrList1.addAll(selMattArrList);
    		    					}
    		    					SalesOrderConstants.showLog("stocksSelectdItemArrList1 Size after adding : "+stocksSelectdItemArrList1.size());
    		    					//preFillQtyArray();
    		    					if(flag == false){
    		    						if(searchLinear2 !=null)
        		    						searchLinear2.setVisibility(View.GONE);
        		    						custnameLinear.setVisibility(View.GONE);		 
    		    					}    		    					
    		    					SapGenConstants.showLog("idstr, selctdPos "+idstr +selctdPos ); 
    		    					
    		    					getCustomerHeader(idstr);
    		    					showCustomerHeaderInfo(idstr, selctdPos);
    		    					initItemHeadLayout();
    		    					
    		    					sapsendLayout = (LinearLayout) findViewById(R.id.showlinear);		
    		    			        sapsendLayout.setVisibility(View.VISIBLE);	      
    		    			        sapsendLayout.removeAllViews();	
    		    			        displayUI(DBConstants.EDIT_ACTION_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, sapsendLayout);
    		    					//drawItemSubLayout();
        						}
    					}catch(Exception sfgh3){
    			    		SalesOrderConstants.showErrorLog("Error in RESULT_OK : "+sfgh3.toString());
    			    	}
    				
    					//}
    				}
    			}
    		}
			//finish();
		} catch (Exception e) {
			SalesOrderConstants.showErrorLog(e.toString());
		}
    }//fn onActivityResult
    
}//End of class SalesOrderCreation

