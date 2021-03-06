package com.globalsoft.SalesOrderLib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrderCntxtOpConstraints;
import com.globalsoft.SalesOrderLib.Database.SalesOrderDBOperations;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.AppLocationService;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesOrderMainScreenTablet extends Activity{
	private TextView[] mattTxtView, custNameTxtView;
	private TextView[] valTV;
	private boolean sortProdNameFlag = false;
	private LinearLayout soheadll, actscll;
	protected CharSequence[] _options = null;
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, myTitle;
	private TextView tableHeaderTV5, tableHeaderTV6, tableHeaderTV7, tableHeaderTV8, tableHeaderTV9,tableHeaderTV10;
	private ImageView[] syncBMF;
	private RelativeLayout bodylayout;
	//private LinearLayout linearLayout2;
	private TextView[] headLblTV;
	private TextView[] salesOrderHeadLblTV;
	private CheckBox[] chckBox;
	private TextView[] productHeadLblTV;
	
	public TextView pageTitle, firstPgTV, lastPgTV, nextPgTV, prevPgTV;
	private EditText searchET;
	private Button mOverLayImage,cling_press_image_2;
	private ProgressDialog pdialog = null;
	private TableLayout tlValue = null, tl = null;
	private TableRow.LayoutParams linparams11 = null;
	private ImageView addimg,copyimg,clingImage,cling_image2;	
	private SoapObject resultSoap = null;
	private String serverErrorMsgStr = "";
	private static Context ctx;	
	int offset = 0, soFlag=0;
    int data,pos=-1;
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	
	private SOCustomerListAdapter SOCustomerListAdapter;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private final Handler soHandler = new Handler();
	//private DataBasePersistent dbObjUIConf = null;
	private DataBasePersistent dbObj = null,dbcopyobj = null;
	private DataBasePersistent dbObjColumns = null;	
	private DataBasePersistent dbObjitem = null;	
	private DataBasePersistent dbObjItemColumns = null;	
		
	private static int respType = 0;
	private static ArrayList cntxArrList = new ArrayList();
	private ArrayList selchkboxlist = new ArrayList();
	private ArrayList selchkboxNamelist = new ArrayList();
	private ArrayList Labelslist = new ArrayList();
	HashMap<String, String> custDetail = null;
	HashMap<String, String> somattDetail = null;
	
    ArrayList metaNamesArray = new ArrayList();
    private ArrayList metaHeadArray = new ArrayList();	
	private ArrayList metaItemArray = new ArrayList();	
		
	private int cntxsListCount = 0, cntctFlag=0,alertflag=0,salesOrdcrte= 0;	
	private float valueX,valueY;
	private String cntxsListType = "";
	private boolean tableExits = false,tableExits2 =false,tableExits3 =false;
	private String curLabel =" ",custIdStr ="",nameStr = "",curnameStr="",valStr = "",unitStr=" ";
	private String[] metheadList =null;
	final Handler salesOrderData_Handler = new Handler();
	private static final int MAIN_ID = Menu.FIRST;
	
	private ArrayList mattArrList = new ArrayList();
	private ArrayList mattCopyArrList = new ArrayList();
	private ArrayList mattArrCopyArrList = new ArrayList();
	private ArrayList mattItemArrList = new ArrayList();
	private ArrayList SOCheckedList = new ArrayList();
	private ArrayList CustIdList = new ArrayList();
	private ArrayList CustNameList = new ArrayList();
	private ArrayList SOSelcustList = new ArrayList();
	private ArrayList SelectdfinalList = new ArrayList();
	private ArrayList SelectdCustDetailsfinalList = new ArrayList();
	private ArrayList SelectdCustDetailsList = new ArrayList();
	private ArrayList SelectdCustMaterialList = new ArrayList();
	private ArrayList MaterialList = new ArrayList();
	private ArrayList OldMaterialList = new ArrayList();
	private ArrayList valHeaderArrayList = new ArrayList();	
	private HashMap<String, String>  map = new HashMap<String,String>(); 
	private HashMap<String, String>  labelMap = new HashMap<String,String>(); 
	HashMap<String, String> Valueslist = null;
	private ArrayList valArrayList = new ArrayList();
	
	private ArrayList<Boolean>  status =new ArrayList<Boolean>();	
	private String searchStr = "", mainContactId="", mainCustomerId="",CustId="",stockItemListType="",stockheadListType="", title="", appNameStr="",searchbarhint="", customrStr="", valueStr="", ordrnumbStr="";	
	private boolean sortCNameFlag = false, sortcustNoFlag = false;
	private boolean sortSoNoFlag = false, sortSoValFlag = false, sortRDateValFlag = false;
	private boolean sortPoNoFlag = false, sortPoDateFlag = false,isConnAvail=false,internetAccess=false;

	private int sortIndex = -1, chckId, count = 0, selctdPos = 0, Index, flagPos = 0, stockheadListCount = 0,	stockItemListCount = 0, offlineFlag=0, statusvalid;
	
	private int headerWidth1, headerWidth2, headerWidth4, headerWidth5, headerWidth6, headerWidth7, headerWidth8, headerWidth9,headerWidth10;	
	private final int sortHeader1 = 1, sortHeader2 = 2, sortHeader4 = 4, sortHeader6 = 6, sortHeader7 = 7, sortHeader8 = 8, sortHeader9 = 9,sortHeader10 = 10;
	private int dispwidth = 320, offlinemode =0,getApplQueueCount=0;
	private boolean flag_pref = false , soapConstant,returnflag =false;
	private TableLayout colValTableLayout = null, colHeadTableLayout = null;
	int[] location = new int[2];
	private Point p;
	 int popupWidth = 200;
     int popupHeight = 150;
     int OFFSET_X = 30;
     int OFFSET_Y = 30;   
    private TimerTask scanTask; 
 	private final Handler handler = new Handler(); 
 	private Timer t = new Timer();
 	AppLocationService appLocationService;
 	private double latitide,longitude;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this); 
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.salesorderhead);         
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		myTitle = (TextView) findViewById(R.id.myTitle);
		 bodylayout = (RelativeLayout) findViewById(R.id.sobgll);
		//myTitle.setText(getString(R.string.SALESORDPRO_SOLIST_TITLE));
		ctx = this.getApplicationContext();
		int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
		if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		 appLocationService = new AppLocationService(
				 SalesOrderMainScreenTablet.this);
		/*title = SalesOrderDBOperations.readSOScreenTitleFromDB(this, DBConstants.DEVICE_TYPE_WIDE_OV_TAG);
		searchbarhint = SalesOrderDBOperations.readSOSearchBarFromDB(this,DBConstants.CNTXT4_HINT_TAG);
		myTitle.setText(title);*/
		
		//SalesOrderConstants.showLog("searchbar : "+searchbarhint);
		
		appNameStr = this.getIntent().getStringExtra("app_name");
		if((appNameStr == null) || (appNameStr.equalsIgnoreCase("")))
        	appNameStr = SapGenConstants.APPLN_NAME_STR_SALESPRO;
		SalesOrderConstants.SO_CALLING_APP_NAME = appNameStr;
		
		/*firstPgTV = (TextView)findViewById(R.id.firstpgtv);
        firstPgTV.setOnClickListener(firstPgTVListener);
                
        lastPgTV = (TextView)findViewById(R.id.prepgtv);
        lastPgTV.setOnClickListener(lastPgTVListener);
        
        nextPgTV = (TextView)findViewById(R.id.nextpgtv);
        nextPgTV.setOnClickListener(nextPgTVListener);

        prevPgTV = (TextView)findViewById(R.id.lastpgtv);
        prevPgTV.setOnClickListener(prevPgTVListener);    
		
        pageTitle = (TextView)findViewById(R.id.pagetitle);
        
        if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
        	pageTitle.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	firstPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	lastPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	nextPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	prevPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        }*/
		/*title= getResources().getString(R.string.SALESORDPRO_SOLIST_TITLE);
		OfflineFunc();	*/
		//SapGenConstants.checkInternetConnection(this,myTitle,title);//checking for internet connectivity
		isConnAvail = SapGenConstants.checkConnectivityAvailable(SalesOrderMainScreenTablet.this);	
		SharedPreferences settings = getSharedPreferences(SalesOrderConstants.PREFS_NAME_FOR_SALES_ORDER, 0);      
		flag_pref = settings.getBoolean(SalesOrderConstants.PREFS_KEY_SALES_ORDER_FOR_MYSELF_GET, false);		
		
		SalesOrderConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
		if(SalesOrderConstants.APPLN_NAME_STR == null || SalesOrderConstants.APPLN_NAME_STR.length() == 0){
			SalesOrderConstants.APPLN_NAME_STR = "SALESPRO";
		}else{
			returnflag =true;
		}
		//int status = SapQueueProcessorDBOperations.getAppStatus(this,"SALES-ORDER-CREATE");
		 //getApplQueueCount = SapQueueProcessorHelperConstants.getSubApplicationProcessingQueueCount(this, "SALES-ORDER-CREATE");
		try{
			if(getApplQueueCount <= 0){
				if(isConnAvail == true){			
					SalesOrderConstants.showLog("getApplQueueCount: "+getApplQueueCount);
					initContextSoapConnection();
					/*Intent serIntent = new Intent(this,GSMService.class);  
	    			this.startService(serIntent);      	*/
					//pingConnection();								
					//initSalesListSoapConnection();
			    }
				else{
					//offlinemode = 1;
					initDBConnection();				
				}			
			}
			else{
				if(returnflag ==true || isConnAvail == false)
					initDBConnection();	
				try{
					SapQueueProcessorHelperConstants.updateSelectedRowStatus(this, 0, SalesOrderConstants.APPLN_NAME_STR, SalesOrderConstants.STATUS_HIGHPRIORITY);
					checkAppQueueCount();				
				}
	            catch(Exception sff2){
	            	SalesOrderConstants.showErrorLog("Error in ServicePro for getApplicationQueueCount else part: "+sff2.toString());
	            }   
			}			
		}
        catch(Exception sff1){
        	SalesOrderConstants.showErrorLog("Error in Sales Orders for getApplicationQueueCount: "+sff1.toString());
        }    
        try{
			mainContactId = this.getIntent().getStringExtra("contactId");
			mainCustomerId = this.getIntent().getStringExtra("customerId");
			if(mainCustomerId != null)
				mainCustomerId = mainCustomerId.trim();
			
			if(mainContactId != null)
				mainContactId = mainContactId.trim();
			
			SalesOrderConstants.showLog("mainContactId: "+mainContactId);
			SalesOrderConstants.showLog("mainCustomerId: "+mainCustomerId);
		}
		catch(Exception afg){}
              
	}//onCreate           
    
	private void pingConnection() {
		internetAccess = SapGenConstants.checkConnectivityAvailable(SalesOrderMainScreenTablet.this);	
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

	            respType = SapGenConstants.SO_PING;
	            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
	        }
	        catch(Exception asd){
	        	SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
	        }
	    }//fn initPriceSoapConnection
	 
	 private void checkAppQueueCount(){
	    	int count = 0;
	    	try {
				//count = SapQueueProcessorHelperConstants.getSubApplicationProcessingQueueCount(this, "SALES-ORDER-CREATE");
				if(count == 0){					
					stopProgressDialog();					
				}else{
					if(count >= 1){
						pdialog.setMessage("Please wait QueueProcessor Updation is going on" + " " +count + " items remaining..." );
					}
						//here call the GSM service
					/*	
					}else{
						pdialog.setMessage("Please wait.." + " " +count + " item remaining..." );
					}*/
				}
				
			} catch (Exception e) {
				SapGenConstants.showErrorLog("Error in checkAppQueueCount:"+e.toString());
			}
	    }//fn checkAppQueueCount
	 
	/* private void startProgressDialog(){
			try {
				if(pdialog != null)
	    			pdialog = null;
	    		
				if(pdialog == null)                    
	            	pdialog = ProgressDialog.show(this, " ", "Please wait the Queue Processor is running for Sales Orders",true);						

				//SapGenConstants.showLog("Inside Progress 2");
			} catch (Exception ce) {
				SapGenConstants.showErrorLog("Error on startProgressDialog : "+ce.toString());
			}
		}//fn startProgressDialog
*/	 
	/*public void dostarttimer(){
    	try{     	
    		scanTask = new TimerTask() { 
            public void run() { 
                handler.post(new Runnable() { 
                    public void run() { 
                    	SapGenConstants.showLog("TIMER: Timer started"); 
                    	checkAppQueueCount();
                    } 
               }); 
            }};
	        t.schedule(scanTask, 100, SapGenConstants.TIMER_CONST);
    	}
    	catch(Exception qw){
    		SapGenConstants.showErrorLog("Error in dostarttimer:"+qw.toString());
    	}  
    }//fn dostarttimer 
*/	
		 
	 private void stopBackgroundService(){
	    	try{
	    		/*Intent serIntent = new Intent(this, GSMService.class);
	    		this.stopService(serIntent);*/
	    	}
	    	catch(Exception ssgh){
	    		SapGenConstants.showErrorLog("Error in stopBackgroundService : "+ssgh.toString());
	    	}
	    }//fn stopBackgroundService
	
	 private void PreinitDBConnection() {					
		try{	
			setTitleValue();
			if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
				DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    		}		
			offset = 0;
			data = offset + SalesOrderConstants.page_size;
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
            	if(dbObj!=null)
            		dbObj=null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_HEAD_LIST);
			dbObj.getColumns();	  
			/*if(offlinemode ==1){					
            	SapGenConstants.showLog("offlinemode"+offlinemode);
            	dbObj.getColumns();	         	            		            	
            }else{
            	dbObj.setTableName_ColumnName(this,DBConstants.TABLE_SALESORDER_HEAD_LIST,metaHeadArray);
            }	*/	
			if(OldMaterialList!=null)
				OldMaterialList.clear();
			tableExits = dbObj.checkTable();
			if(tableExits)
				OldMaterialList = dbObj.readListDataFromDB(this);
			
			if(OldMaterialList != null)
				mattArrCopyArrList = (ArrayList)OldMaterialList.clone();
			SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);
		
			/*if(mattArrList != null)
				mattArrList = (ArrayList)mattArrList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");*/
			dbObj.closeDBHelper();  
			
			SapGenConstants.showLog("OldMaterialList size:"+ OldMaterialList.size());			             	
        	dbObj.closeDBHelper();	   				
		}catch(Exception sggh){				
			dbObj.closeDBHelper();				
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}			
}//	
	private void initDBConnection() {					
			try{	
				setTitleValue();
				if(DBConstants.TABLE_SALESORDER_HEAD_LIST != null && DBConstants.TABLE_SALESORDER_HEAD_LIST.length() > 0){
					DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_HEAD_LIST;
	    		}		
				offset = 0;
				data = offset + SalesOrderConstants.page_size;
	            SapGenConstants.showLog("offset:"+offset);
	            SapGenConstants.showLog("data:"+data);
	            	if(dbObj!=null)
	            		dbObj=null;
				if(dbObj == null)
					dbObj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_HEAD_LIST);
				dbObj.getColumns();	  
				/*if(offlinemode ==1){					
	            	SapGenConstants.showLog("offlinemode"+offlinemode);
	            	dbObj.getColumns();	         	            		            	
	            }else{
	            	dbObj.setTableName_ColumnName(this,DBConstants.TABLE_SALESORDER_HEAD_LIST,metaHeadArray);
	            }	*/	
				if(mattArrList!=null)
					mattArrList.clear();
				tableExits = dbObj.checkTable();
				if(tableExits)
					mattArrList = dbObj.readListDataFromDB(this);
				
				if(mattArrList != null)
					mattArrCopyArrList = (ArrayList)mattArrList.clone();
				SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);
			
				/*if(mattArrList != null)
					mattArrList = (ArrayList)mattArrList.clone();
	    		else
	    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");*/
				dbObj.closeDBHelper();  
				
				SapGenConstants.showLog("mattArrList size:"+ mattArrList.size());
				HashMap<String, String> stockMap = null;
	            String matIdStrVal = "", id = "", desc = "";
	        	if(mattArrList != null && mattArrList.size() > 0 ){
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
						_options[i]= matIdStrVal;
					}
	        	}     
	        	dbObj.closeDBHelper();	
	    		setPageData();
	    		initLayout();				
			}catch(Exception sggh){				
				dbObj.closeDBHelper();				
				SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
			}			
	}//

	@SuppressLint("NewApi")
	private void initLayout(){
    	try{    		   		 	    		 
    		 if(status != null){
 				status.clear();
 			}
 			for (int i = 0; i < mattArrList.size(); i++) {				
 					status.add(i, false);								
 	            //status.add(i, false);
 	        }
 			
 			if(selchkboxlist.size()!= 0)
				  selchkboxlist.clear();
    		firstPgTV = (TextView)findViewById(R.id.fstpgtv);
	        firstPgTV.setOnClickListener(firstPgTVListener);
	                
	        lastPgTV = (TextView)findViewById(R.id.solastpgtv);
	        lastPgTV.setOnClickListener(lastPgTVListener);
	        
	        nextPgTV = (TextView)findViewById(R.id.sonextpgtv);
	        nextPgTV.setOnClickListener(nextPgTVListener);

	        prevPgTV = (TextView)findViewById(R.id.soprepgtv);
	        prevPgTV.setOnClickListener(prevPgTVListener);    
			
	        pageTitle = (TextView)findViewById(R.id.sopagetitle);
	        
	        if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	        	pageTitle.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
	        	firstPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
	        	lastPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
	        	nextPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
	        	prevPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
	        }
	        
    		tl = (TableLayout)findViewById(R.id.sordheadtbllayout1);
			if(tl != null)
				tl.removeAllViews();
			
			soheadll = (LinearLayout) findViewById(R.id.vanstocklinear);
			soheadll.removeAllViews();
			soheadll.setOrientation(LinearLayout.HORIZONTAL);
			DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			displayUI(DBConstants.SO_DEVICE_TYPE_WIDE_OV_TOP_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, soheadll);
					     		
			valArrayList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.SO_DEVICE_TYPE_WIDE_OV_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);						
			SapGenConstants.showLog("labelMap : "+labelMap.size());
			SapGenConstants.showLog("valArrayList : "+valArrayList.size());
			Labelslist = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.SO_DEVICE_TYPE_WIDE_OV_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			String labVal = "";
			if(valArrayList != null && valArrayList.size() > 0){
				int cols =  Labelslist.size();					
				dispwidth = SapGenConstants.getDisplayWidth(this);
				if(dispwidth < SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					tl.setColumnStretchable(1, true);
				//headLblTV = new TextView[valArrayList.size()];  
				headLblTV = new TextView[Labelslist.size()]; 
				TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.partial_table_row, null);
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
					labVal = Labelslist.get(i).toString().trim();
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
					headLblTV[i] = (TextView) getLayoutInflater().inflate(R.layout.partial_textview, null);
					//headLblTV[i].setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					if(valueValStr != null && valueValStr.length() > 0){
						if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
							headLblTV[i].setWidth(70);
    					}else{
    						headLblTV[i].setMinWidth(100);
    						headLblTV[i].setWidth(labelWidth);
    					}
					}					
					headLblTV[i].setId(i);
					headLblTV[i].setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							if(!valueValStrCk.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
								sortItemsAction(metaValue);
							}
						}	
                    });		
					SapGenConstants.showLog("labValStr:  "+labValStr);
					SapGenConstants.showLog("metaValStr:  "+metaValStr);
					SapGenConstants.showLog("typeValStr:  "+typeValStr);
    				if(metaValStr != null && metaValStr.length() > 0){
    					if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_NETWR)){
    						headLblTV[i].setGravity(Gravity.RIGHT);
    						SapGenConstants.getUnderlinedString(labValStr);
    					}else{
        					headLblTV[i].setGravity(Gravity.LEFT);	                     
        				}
    				}/*else{
    					headLblTV[i].setGravity(Gravity.LEFT);	                     
    				}*/
					
					if(!labValStr.equalsIgnoreCase(null) && labValStr.length() > 0){
						headLblTV[i].setText(labValStr);						
					}else{
						headLblTV[i].setText(" ");
					}					
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
						headLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_LABEL);								
					tr1.addView(headLblTV[i]);						
				}											
				tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				drawSubLayout();
				SalesOrderConstants.showErrorDialog(this, "Standard Order 13810 Created");  
				try{
					if(flag_pref==false){
						 final Dialog overlayInfo = new Dialog(SalesOrderMainScreenTablet.this);
					        overlayInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
					        overlayInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					        overlayInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);			       
					        overlayInfo.setContentView(R.layout.showview);			       
					        overlayInfo.show();
					       clingImage = (ImageView) overlayInfo.findViewById(R.id.cling_image);
					       //clingImage.setBackgroundColor(Color.LTGRAY);			    	       
					        mOverLayImage = (Button) overlayInfo.findViewById(R.id.cling_press_image);			      
					        mOverLayImage.setOnClickListener(new OnClickListener() {			            			       
					            public void onClick(View v) {                
					                overlayInfo.cancel();
					                ShowInstrForCopy();
					            }						
					        });
					}		
				}catch(Exception sfg2){
		    		SalesOrderConstants.showErrorLog("Error in GUI : "+sfg2.toString());
		    	}
						     
			}
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    	
	
	private void ShowInstrForCopy() {
		final Dialog overlayInfo = new Dialog(SalesOrderMainScreenTablet.this);
        overlayInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overlayInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        overlayInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);			       
        overlayInfo.setContentView(R.layout.showview_copy);			       
        overlayInfo.show();
        cling_image2 = (ImageView) overlayInfo.findViewById(R.id.cling_image_copy);
       //clingImage.setBackgroundColor(Color.LTGRAY);			    	       
        cling_press_image_2 = (Button) overlayInfo.findViewById(R.id.cling_press_image_copy);			      
        cling_press_image_2.setOnClickListener(new OnClickListener() {			            			       
            public void onClick(View v) {                
                overlayInfo.cancel();              
            }						
        }); 
		
	}//ShowInstrForCopy
	
	private void sortItemsAction(String metaName){
		try{
			/*sortIndex = sortInd;
			if(sortInd == SORT_BY_PNAME)
				sortProdNameFlag = !sortProdNameFlag;	*/		
			sortProdNameFlag = !sortProdNameFlag;
			//valArrayList = DataBasePersistent.readAllValuesOrderByFromDB(this, DBConstants.DEVICE_TYPE_WIDE_OV_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, DBConstants.MAKTX_COLUMN_NAME, sortProdNameFlag);			
			if(dbObj!=null)
				dbObj=null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_HEAD_LIST);
			dbObj.getColumns();	  
			/*if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_HEAD_LIST);
    			tableExits = dbObj.checkTable();*/
    			if(tableExits)
    				mattArrList = dbObj.readListDataOrderByFromDB(this, metaName, sortProdNameFlag); 
        	
    		if(mattArrList != null)
    			mattArrCopyArrList = (ArrayList)mattArrList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper();
    		
	    	setPageData();
	    	drawSubLayout();
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	 private void drawSubLayout(){
		  try{	
			  int i;
			  String  matIdStr = "",labelStr = "", strVal = "",labelValStr = ""; 			
			  	tlValue = (TableLayout)findViewById(R.id.sordheadtbllayout2);		
			  	if(tlValue != null)
			  		tlValue.removeAllViews();		  	
	    		 if( selchkboxNamelist.size()!= 0)
	    			 selchkboxNamelist.clear();    	
	    		 
			  	String pageTitleValue = ""+SalesOrderConstants.SL_current_page;
		        pageTitle.setText(pageTitleValue);

		        prevPgTV.setText("<<");
		        nextPgTV.setText(">>");
		        
		        firstPgTV.setText("1");
		        lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
		        SapGenConstants.showLog("1 "); 	
		        if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1 ){
		        	prevPgTV.setTextColor(Color.BLUE);
		        	firstPgTV.setTextColor(Color.BLUE);
		        }
		        else{
		        	prevPgTV.setTextColor(Color.BLACK);
		        	firstPgTV.setTextColor(Color.BLACK);
		        }
		        
		        if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page ){
		        	nextPgTV.setTextColor(Color.BLUE);
		        	lastPgTV.setTextColor(Color.BLUE);
		        }
		        else{
		        	nextPgTV.setTextColor(Color.BLACK);
		        	lastPgTV.setTextColor(Color.BLACK);
		        }
		        SapGenConstants.showLog("2"); 	
		        HashMap<String, String> stockMap = null; 		        	       
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
				SapGenConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);
				
				if(valArrayList != null && valArrayList.size() > 0){
					if(mattArrList != null){						  					
		                int rowSize = mattArrList.size();                
		                SapGenConstants.showLog("mattArrList  Size  : "+rowSize);  
		                SapGenConstants.showLog("offset:"+offset);
		                SapGenConstants.showLog("data:"+data);
		               // chckBox = new CheckBox[rowSize];
		                syncBMF = new ImageView[rowSize];
		               // valTV = new TextView[rowSize];
		                if(offset <= mattArrList.size() && data <= mattArrList.size() ){
							for (i = offset; i < data; i++) {
			                    tr = new TableRow(this);        
								stockMap = (HashMap<String, String>) mattArrList.get(i);     								
	                    		matIdStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN);
	                    		nameStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_NAME1A);
	                    		unitStr=  (String) stockMap.get(DBConstants.SO_HEAD_COL_WAERK);
	                    		final String pId = matIdStr;
	                    		curLabel = DBConstants.SO_HEAD_COL_VBELN;
	                    		curnameStr = DBConstants.SO_HEAD_COL_NAME1A;
	                    		//imageUrlStr = getImageUrl(matIdStr);
	                    		 if(stockMap != null){
	                    			 valTV = new TextView[Labelslist.size()];
				                    	for (int i1 = 0; i1 < Labelslist.size(); i1++) {			                    		
					                    		String nameVal = Labelslist.get(i1).toString().trim();				                    		
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
				                    		//labelStr = strVal;
					        					 valStr = (String) stockMap.get(metaValStr);
					        					 if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAP_TAG)){		
					                    			 //valStr = (String) stockMap.get(metaValStr);
					                    				if(valStr.equalsIgnoreCase(" ")){
					                    					valTV[i1].setText(valStr);	
					                    				}else{
					                    					 valTV[i1] = new TextView(SalesOrderMainScreenTablet.this.getApplicationContext());     			                    			
							                    			 valTV[i1] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						                    				 if(typeValStr.equalsIgnoreCase(DBConstants.DATS_DATATYPE_TAG)){		
						                    					 SapGenConstants.showLog("valStr:  "+valStr);
						                    					
										    						valStr = SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", valStr);
										    						valTV[i1] .setGravity(Gravity.LEFT);
										    						 valTV[i1] .setPadding(25,0,0,0);	
										    						 valTV[i1].setText(valStr);	
										    						 //valTV[i1] .setPadding(70,0,0,0);	
										    					}	
						                    				 else if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_NETWR)){						 			        					
						 			        						valTV[i1] .setGravity(Gravity.RIGHT);
						 			        						valueX= valTV[i1].getX();
						 			        						valueY= valTV[i1].getY();
						 			        						valTV[i1].setOnClickListener(new View.OnClickListener() {
																			public void onClick(View view) {																
																				PopupMenu popupMenu = new PopupMenu(SalesOrderMainScreenTablet.this, view);															
																				popupMenu.inflate(R.layout.popup__menu);																	
																				//popupMenu.getMenu().add(Menu.NONE,R.id.action1,Menu.NONE, "Not ready");																
																				popupMenu.getMenu().getItem(0).setTitle("Unit: "+unitStr);
																				popupMenu.show();
																			}	
												                        });		
						 			        						valTV[i1].setText(valStr);	
						 			        						 //valTV[i1] .setPadding(10,0,0,0);	
						 			        				} else if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTKD)){	
						 			        					 SapGenConstants.showLog("valStr:  "+valStr);
						 			        					valTV[i1] .setGravity(Gravity.RIGHT);
					 			        						 valTV[i1] .setPadding(25,0,0,0);	
					 			        						valTV[i1].setText(valStr);	
					 			        				}else{
					 			        					 SapGenConstants.showLog("valStr:  "+valStr);
						 			        					valTV[i1] .setGravity(Gravity.LEFT);
						 			        					 valTV[i1] .setPadding(10,0,0,0);
						 			        					 valTV[i1]  .setOnClickListener(new View.OnClickListener() {
																		public void onClick(View view) {
																			showStockListItemScreen(pId,curLabel);
																		}	
											                        });	
						 			        					valTV[i1].setText(valStr);	
						 			        				}
						                    			
						                    				/* if(valStr.equalsIgnoreCase(" "))
						                    					 valTV[i1]  .setText(" ");
						                    				 else*/
						                    				 //valTV[i1]  .setText(valStr);
						                    				 //valTV[i1].setLayoutParams(linparams); 
										        				//valTV .setGravity(Gravity.CENTER);
						                    				 valTV[i1] .setMinWidth(100);
						                    				 valTV[i1]  .setWidth(labelWidth);
						                    				 valTV[i1]  .setId(i1);						                    													        				
						                    				 //valTV[i1] .setGravity(Gravity.CENTER_VERTICAL);
						                    								                    				   					    					
										    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
										    						valTV[i1]  .setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
										    					}
										    					tr.addView(valTV[i1]);
					                    				}
						                    			 //if(!valStr.equalsIgnoreCase(" ") && valStr.length() > 0){	
						                    														    					
						                    			 //}
					                    		}else if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG)){
				                    			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);                    		                   		                    		
	        	                    			final CheckBox chckBox = new CheckBox(SalesOrderMainScreenTablet.this.getApplicationContext());  
	        		                    		 chckBox.setId(i); 
	        		                    		 pos =i;
	        		                             //chckBox[i1].setLayoutParams(linparams); 
	        		                             chckBox.setButtonDrawable(R.drawable.checkboxcustom);
	        		        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	        		                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        		                            	linlayparams1.gravity = Gravity.LEFT;
	        		                            	linlayparams1.height = 40;
	        		                            	linlayparams1.width = 40;
	        		                            	chckBox.setLayoutParams(linlayparams1);
	        		                            }		        	               
	        		        	                chckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){					
	        		             					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {					
	        		             						SalesOrderConstants.showLog("boolean "+arg1);
	        		             						CheckBox cb = (CheckBox) arg0 ; 
	        		             						if(arg1==true){
	        		             							//chckId=arg0.getId();
	        		             							cb.setChecked(arg1);
	        		             							selchkboxlist.add(pId);					             							
	        		             							//status.set(chckId, arg1);		             						
	        		             							//selchkboxNamelist.add(nameStr);
	        		             						}					             							
	        		             						else{
	        		             							cb.setChecked(arg1);
	        		             							selchkboxlist.remove(pId);					             							
	        		             							//status.set(chckId, arg1);
	        		             							//selchkboxNamelist.remove(nameStr);
	        		             						}					             							        		             						
	        		             						if(selchkboxlist.contains(pId)){
	        		             							chckBox.setChecked(true);
	        		             						}else{
	        		             							chckBox.setChecked(false);
	        		             						}
	        		             					}
	        		             				}); 	        		        	              
	        		        	                linearLayout.addView(chckBox);		        	                
						    					tr.addView(linearLayout);
				                    		}else if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
				                    			String valStr2 = (String) stockMap.get(metaValStr);
				                    			SapGenConstants.showLog("valStr Value: "+valStr2); 	
				                    			//SapGenConstants.showLog("3"); 	
				                    			LinearLayout linearLayout2 = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
					                        	 syncBMF[i] = new ImageView(this); 
						                        // syncBMF[i1].setId(i);	
						                         //syncBMF[i1].setLayoutParams(linparams); 		
					                        	 
					                        		 if(valStr2.equalsIgnoreCase("R"))
								                         	syncBMF[i].setBackgroundResource(R.drawable.tl_red);
								                         else if(valStr2.equalsIgnoreCase("G"))
								                         	syncBMF[i].setBackgroundResource(R.drawable.tl_green);
								                         else if(valStr2.equalsIgnoreCase("Y"))
								                         	syncBMF[i].setBackgroundResource(R.drawable.tl_yellow); 					                        					                        		 	
								                         else 
								                        	 syncBMF[i].setBackgroundResource(R.drawable.tl_black);
					                        		 
					                        		// SapGenConstants.showLog("4"); 	                      	 	 
					                         		 syncBMF[i].setPadding(80, 0, 0, 0);
							                         if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
							                        	 LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							                         	linlayparams1.gravity = Gravity.CENTER_VERTICAL;
						                            	linlayparams1.height = 20;
						                            	linlayparams1.width = 22;
							                         	syncBMF[i].setLayoutParams(linlayparams1);
							                         }								                        
							                       syncBMF[i].setOnClickListener(new View.OnClickListener() {
															public void onClick(View view) {																
																PopupMenu popupMenu = new PopupMenu(SalesOrderMainScreenTablet.this, view);															
																popupMenu.inflate(R.layout.popup__menu);																	
																//popupMenu.getMenu().add(Menu.NONE,R.id.action1,Menu.NONE, "Not ready");																
																popupMenu.getMenu().getItem(0).setTitle("Not Ready");
																popupMenu.show();
															}	
								                        });		
							                         linearLayout2.addView(syncBMF[i]);					                         
							                         tr.addView(linearLayout2);
							                         //SapGenConstants.showLog("5"); 	
				                    		}
				    					if(i%2 == 0)
				    						tr.setBackgroundResource(R.color.item_even_color);
							            else
							            	tr.setBackgroundResource(R.color.item_odd_color);				    					  
				    					
				                    		}					
				                    	tlValue.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				                    }
								}
			                }else{
			                	for (i = offset; i < mattArrList.size(); i++) {							
				                    tr = new TableRow(this);        
									stockMap = (HashMap<String, String>) mattArrList.get(i); 										
		                    		matIdStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN);
		                    		final String pId = matIdStr;
		                    		nameStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_NAME1A);  
		                    		valueStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_NETWR); 		                    	
		                    		curLabel = DBConstants.SO_HEAD_COL_VBELN;
		                    		curnameStr = DBConstants.SO_HEAD_COL_NAME1A;
				                    if(stockMap != null){		
				                    	  valTV = new TextView[Labelslist.size()];
				                    	for (int i1 = 0; i1 < Labelslist.size(); i1++) {		                    			                    		
				                    		String nameVal = Labelslist.get(i1).toString().trim();				                    		
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
				        									        					
					                    		if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAP_TAG)){		
					                    			 valStr = (String) stockMap.get(metaValStr);
					                    				SapGenConstants.showLog("valStr:  "+valStr);
					                    				if(valStr.equalsIgnoreCase(" ")){
					                    					valTV[i1].setText(valStr);	
					                    				}else{
					                    					 valTV[i1] = new TextView(SalesOrderMainScreenTablet.this.getApplicationContext());     			                    			
							                    			 valTV[i1] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						                    				 if(typeValStr.equalsIgnoreCase(DBConstants.DATS_DATATYPE_TAG)){	
						                    					 SapGenConstants.showLog("valStr:  "+valStr);
										    						valStr = SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", valStr);
										    						valTV[i1] .setGravity(Gravity.LEFT);
										    						 valTV[i1] .setPadding(25,0,0,0);	
										    						 valTV[i1].setText(valStr);	
										    						 //valTV[i1] .setPadding(70,0,0,0);	
										    					}	
						                    				 else if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_NETWR)){						 			        					
						 			        						valTV[i1] .setGravity(Gravity.RIGHT);	
						 			        						valTV[i1].setOnClickListener(new View.OnClickListener() {
																		public void onClick(View view) {																
																			PopupMenu popupMenu = new PopupMenu(SalesOrderMainScreenTablet.this, view);															
																			popupMenu.inflate(R.layout.popup__menu);																	
																			//popupMenu.getMenu().add(Menu.NONE,R.id.action1,Menu.NONE, "Not ready");																
																			popupMenu.getMenu().getItem(0).setTitle("Unit: "+unitStr);
																			popupMenu.show();
																		}	
											                        });		
						 			        						valTV[i1].setText(valStr);	
						 			        						 //valTV[i1] .setPadding(10,0,0,0);	
						 			        				}else if(metaValStr.equalsIgnoreCase(DBConstants.SO_HEAD_COL_BSTKD)){					 			        					
						 			        					valTV[i1] .setGravity(Gravity.RIGHT);
					 			        						 valTV[i1] .setPadding(25,0,0,0);	
					 			        						valTV[i1].setText(valStr);	
					 			        				}else{
					 			        					 SapGenConstants.showLog("valStr:  "+valStr);
						 			        					valTV[i1] .setGravity(Gravity.LEFT);	  
						 			        					 valTV[i1] .setPadding(10,0,0,0);
						 			        					 valTV[i1].setOnClickListener(new View.OnClickListener() {
																		public void onClick(View view) {
																			showStockListItemScreen(pId,curLabel);
																		}	
											                        });
						 			        					valTV[i1].setText(valStr);	
						 			        				}						                    				
						                    				// valTV[i1].setText(valStr);					                    				
						                    				 valTV[i1].setMinWidth(100);
						                    				 valTV[i1].setWidth(labelWidth);
						                    				 valTV[i1].setId(i1);
						                    									                    			
						                    				 //valTV[i1] .setPadding(10,0,0,0);					                    				   					    					
										    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
										    						valTV[i1]  .setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
										    					}
										    					tr.addView(valTV[i1]);	
					                    				}
						                    			// if(!valStr.equalsIgnoreCase(" ") && valStr.length() > 0){	
						                    													    					
						                    			 //}		                    							                    					            					            							                     			                    	    		                    			                    				                    
					                    		}else if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG)){
					                    			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);                    		                   		                    		
		        	                    			final CheckBox chckBox = new CheckBox(SalesOrderMainScreenTablet.this.getApplicationContext());  
		        		                    		 chckBox.setId(i); 
		        		                    		 pos =i;
		        		                             //chckBox[i1].setLayoutParams(linparams); 
		        		                             chckBox.setButtonDrawable(R.drawable.checkboxcustom);
		        		        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
		        		                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		        		                            	linlayparams1.gravity = Gravity.LEFT;		        		                            	
		        		                            	linlayparams1.height = 40;
		        		                            	linlayparams1.width = 40;
		        		                            	chckBox.setLayoutParams(linlayparams1);
		        		                            }		        	               
		        		        	                chckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){					
		        		             					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {					
		        		             						SalesOrderConstants.showLog("boolean "+arg1);
		        		             						CheckBox cb = (CheckBox) arg0 ; 
		        		             						if(arg1==true){
		        		             							//chckId=arg0.getId();
		        		             							cb.setChecked(arg1);
		        		             							selchkboxlist.add(pId);					             							
		        		             							//status.set(chckId, arg1);		             						
		        		             							//selchkboxNamelist.add(nameStr);
		        		             						}					             							
		        		             						else{
		        		             							cb.setChecked(arg1);
		        		             							selchkboxlist.remove(pId);					             							
		        		             							//status.set(chckId, arg1);
		        		             							//selchkboxNamelist.remove(nameStr);
		        		             						}					             							        		             						
		        		             						if(selchkboxlist.contains(pId)){
		        		             							chckBox.setChecked(true);
		        		             						}else{
		        		             							chckBox.setChecked(false);
		        		             						}
		        		             					}
		        		             				}); 	        		        	              
		        		        	                linearLayout.addView(chckBox);		        	                
							    					tr.addView(linearLayout);
					                    		}else if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
					                    			String valStr2 = (String) stockMap.get(metaValStr);
					                    			SapGenConstants.showLog("valStr Value: "+valStr2); 	
					                    			SapGenConstants.showLog("3"); 	
					                    			LinearLayout linearLayout2 = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						                        	 syncBMF[i] = new ImageView(this); 
							                        // syncBMF[i1].setId(i);	
							                         //syncBMF[i1].setLayoutParams(linparams); 		
						                        	 
						                        		 if(valStr2.equalsIgnoreCase("R"))
									                         	syncBMF[i].setBackgroundResource(R.drawable.tl_red);
									                         else if(valStr2.equalsIgnoreCase("G"))
									                         	syncBMF[i].setBackgroundResource(R.drawable.tl_green);
									                         else if(valStr2.equalsIgnoreCase("Y"))
									                         	syncBMF[i].setBackgroundResource(R.drawable.tl_yellow); 					                        					                        		 	
									                         else 
									                        	 syncBMF[i].setBackgroundResource(R.drawable.tl_black);
						                        		 
						                        		 //SapGenConstants.showLog("4"); 	                      	 	 
						                         		 syncBMF[i].setPadding(80, 0, 0, 0);
								                         if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
								                        	 LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								                         	linlayparams1.gravity = Gravity.CENTER_VERTICAL;
							                            	linlayparams1.height = 20;
							                            	linlayparams1.width = 22;
								                         	syncBMF[i].setLayoutParams(linlayparams1);
								                         }									                        
								                         linearLayout2.addView(syncBMF[i]);					                         
								                         tr.addView(linearLayout2);
								                         //SapGenConstants.showLog("5"); 	
					                    		}
					    					if(i%2 == 0)
					    						tr.setBackgroundResource(R.color.item_even_color);
								            else
								            	tr.setBackgroundResource(R.color.item_odd_color);		    					  
				    				
				                    		}	
				                    	tlValue.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				                    }
			                	}
			                }
					}
				}
			}
			catch(Exception asgf){
				SalesOrderConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
			}
		}//fn drawSubLayout
	 
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
    		DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			valHeaderArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("valHeaderArrayList : "+valHeaderArrayList.size());			 
			if(valHeaderArrayList != null && valHeaderArrayList.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valHeaderArrayList.size());
			    //valTV = new TextView[valHeaderArrayList.size()];
			    for(int i = 0; i < valHeaderArrayList.size(); i++){       
			    	String secLine = "";         	 
			    	ArrayList list = (ArrayList) valHeaderArrayList.get(i);
			    	if(list != null){
						SapGenConstants.showLog("list : "+list.size());
						if(list.size() > 0){
							if(list.size() == 1){
								String name = list.get(0).toString().trim();	
								SapGenConstants.showLog("name : "+name);        						
								String nameVal = list.get(0).toString().trim();
								String metaLabel = "";
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
			                 	//String valStr = (String) stockMap.get(metaLabel);      
			         			boolean lab = false;  
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = labelMap.containsKey(metaLabel);
			         			}
			         			
			                 	if(metaValue != null && metaValue.length() > 0){
			                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                 			LinearLayout llmclm = new LinearLayout(this);
			                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
			                        	String valTotStr = "";
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				if(labStr != null && labStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                					tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                					tv1.setPadding(5, 0, 0, 0);
			                					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                    					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                					}
			                					tv1.setLayoutParams(new
			                							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                					tv1.setText(labStr);
			                					llmclm.addView(tv1); 
			                 				}
			                 			}                    					
			        					
			        					/*if(valStr != null && valStr.length() > 0){
			        						TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			            					tv.setPadding(5, 0, 0, 0);
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));                        					
			        						valTotStr += valStr;
			                         		tv.setText(valTotStr);    			
			                         		llmclm.addView(tv);
			                     		}    

			        					String trgStr = (String) stockMap.get(metaTrgActStr.toString().trim());
			         					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			         					SapGenConstants.showLog("trgStr : "+trgStr); 
			             				if(trgStr != null && trgStr.length() > 0){
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
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
			                						trgStr = "("+trgStr+")";
			                					}
			                					tv1.setText(trgStr);
			                					llmclm.addView(tv1); 
			                 				}
			             				}   */                 					
			        					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			         				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
			          				    dynll.addView(llmclm, layoutParams);   					
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                 			EditText et = new EditText(this);
			                 			et.setText("");
			                 			//et.setPadding(5,5,5,5);
			                 			//et.setWidth(58);
			                 			et.setLayoutParams(new LayoutParams(1100,40));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
			                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				if(labStr != null && labStr.length() > 0){
			                 					et.setHint(labStr);
			                 				}else{
			                     				et.setHint(R.string.SEARCH_HINT_LBL);
			                     			}
			                 			}	
			                 			et.addTextChangedListener(new TextWatcher(){
			                    	        public void afterTextChanged(Editable s) {
			            						String updatedQty = s.toString().trim();
			            				 		SapGenConstants.showLog("Text : "+s.toString());
			            				 		searchStr = s.toString().trim();
			            				 		filter(searchStr);
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
		            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		                     			bt.setTextColor(getResources().getColor(R.color.white));
		                     			bt.setBackgroundResource(R.drawable.btn_blue1);
		                     			if(lab){
		                     				String labStr = (String)labelMap.get(metaLabel);
		                     				if(labStr != null && labStr.length() > 0){
		                     					bt.setText(labStr);
		                     				}
		                     			}
		                     			bt.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												//catBtnAction();
											}	
				                        });
		                     			dynll.addView(bt);
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_COPY_ICON_TAG)){			                    			    		
		                        		ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.copyicon);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												 if(SOCheckedList.size()!= 0)
										 			  	SOCheckedList.clear();
												showAlert(); 
											}	
				                        });
		                    			dynll.addView(iv);
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_CREATE_ICON_TAG)){			                    			    		
		                        		ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.add);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												showSalesOrdCreationScreen();
											}	
				                        });
		                    			dynll.addView(iv);
		                     		}/*else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG)){			                    			    		
		                        		ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		//iv.setImageResource(R.drawable.shoping_cart);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												//showSCartScreen();
											}	
				                        });
		                    			dynll.addView(iv);
		                     		} */                            		
			                 	}            				    
							}else if (list.size() > 1){
			         			LinearLayout llmclm = new LinearLayout(this);
			         			llmclm.setOrientation(LinearLayout.HORIZONTAL);
								for(int l = 0; l < list.size(); l++){
									String name = list.get(l).toString().trim();
									SapGenConstants.showLog("name append else: "+name);                                 	
			                     	String nameVal = list.get(l).toString().trim();
									String metaLabel = "";
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
			                     	/*String valStr = (String) stockMap.get(metaLabel);                                 	
			                     	secLine += " "+valStr;*/
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
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                         		tv.setText("");
			            				    llmclm.addView(tv);       
			                 			}
			                     		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                     			/*if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){                 					
			                     					TextView tv = new TextView(this);
			                    					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                    					tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                    					tv.setPadding(5, 0, 0, 0);
			                    					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                        					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                    					}
			                    					tv.setLayoutParams(new
			                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                    					tv.setText(labStr);
			                        			    llmclm.addView(tv);                                 					
			                     				}
			                     			} 
			                             	TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			            					tv.setPadding(5, 0, 0, 0);
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			            					if(valStr != null && valStr.length() > 0){   
			            						valTotStr += valStr;
			                             		tv.setText(valTotStr);
			                				    llmclm.addView(tv);
			                				}                                      					
			            					String trgStr = (String) stockMap.get(metaTrgActStr.toString().trim());
			             					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			             					SapGenConstants.showLog("trgStr : "+trgStr); 
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			            						SapGenConstants.showLog("trgStr : "+trgStr);
			                     				if(trgStr != null && trgStr.length() > 0){
			                     					TextView tv1 = new TextView(this);
			                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
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
			                    						trgStr = "("+trgStr+")";
			                    					}
			                    					tv1.setText(trgStr);
			                    					llmclm.addView(tv1); 
			                     				}
			                 				}*/
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                     			EditText et = new EditText(this);
			                     			et.setText("");
			                     			//et.setPadding(5,3,5,3);
			                     			//et.setWidth(58);
				                 			et.setLayoutParams(new LayoutParams(1100,40));
			                     			et.setTextColor(getResources().getColor(R.color.black));
			            					et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			et.setBackgroundResource(R.drawable.editext_border);
				                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
				                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					et.setHint(labStr);
			                     				}
			                     			}else{
			                     				et.setHint(R.string.SEARCH_HINT_LBL);
			                     			}
			                     			et.addTextChangedListener(new TextWatcher(){
				                    	        public void afterTextChanged(Editable s) {
			                						String updatedQty = s.toString().trim();
			                				 		SapGenConstants.showLog("Text : "+s.toString());
			                						searchStr = s.toString().trim();
			                						filter(searchStr);
				                    	        }
				                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
				                    	    }); 
			            				    llmclm.addView(et);                        				    
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
			                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     			bt.setText("");
			                     			bt.setPadding(5,5,5,5);
			            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			bt.setTextColor(getResources().getColor(R.color.white));
			                     			bt.setBackgroundResource(R.drawable.btn_blue1);
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					bt.setText(labStr);
			                     				}
			                     			}
			                     			bt.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													//catBtnAction();
												}	
					                        });
			            				    llmclm.addView(bt);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_COPY_ICON_TAG)){			                    			    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.copyicon);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													showAlert();        
												}	
					                        });
			            				    llmclm.addView(iv);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_CREATE_ICON_TAG)){			                    			    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.add);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													showSalesOrdCreationScreen();
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
    
    private OnClickListener firstPgTVListener = new OnClickListener(){
		public void onClick(View v){
			if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1){
				changeTotalPage();
				SalesOrderConstants.SL_previous_page = 0;
				SalesOrderConstants.SL_current_page = 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
	    		offset = 0;
                data = offset + SalesOrderConstants.page_size;
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
	
	private OnClickListener lastPgTVListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page){
				changeTotalPage();
				SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_total_page - 1;
				SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_total_page;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
                
                int remains = SalesOrderConstants.SL_total_record%SalesOrderConstants.page_size;
                if(remains == 0){
                	offset = SalesOrderConstants.SL_total_record - SalesOrderConstants.page_size;
                	data = SalesOrderConstants.SL_total_record;
                }else{
                	offset = (SalesOrderConstants.SL_total_record/SalesOrderConstants.page_size)*SalesOrderConstants.page_size;
                    data = SalesOrderConstants.SL_total_record;
                }
                
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
    
	private OnClickListener nextPgTVListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page){
				changeTotalPage();
				SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_current_page;
				SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_current_page + 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
	    		offset = offset + SalesOrderConstants.page_size;
                data = offset + SalesOrderConstants.page_size;
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
	
	private OnClickListener prevPgTVListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1){
				changeTotalPage();
				SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_current_page - 1;
				SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_previous_page - 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);	  
	    		if(data != SalesOrderConstants.SL_total_record){
		    		offset = offset - SalesOrderConstants.page_size;
	                data = data - SalesOrderConstants.page_size;
	    		}else{
		    		offset = offset - SalesOrderConstants.page_size;
		    		int remain = data%SalesOrderConstants.page_size;
		    		if(remain > 0){
		    			data = data - remain;
		    		}else{
		                data = data - SalesOrderConstants.page_size;
		    		}
	    		}
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
	
	 public void setPageData(){
			try {
				SalesOrderConstants.SL_previous_page = 0;
				SalesOrderConstants.SL_current_page = 1;
				if(mattArrList != null && mattArrList.size() > 0){
					SalesOrderConstants.SL_total_record = mattArrList.size();	
					if(SalesOrderConstants.SL_total_record > SalesOrderConstants.page_size ){
						SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_record/SalesOrderConstants.page_size;	
						int remain_page = SalesOrderConstants.SL_total_record%SalesOrderConstants.page_size;
						if(remain_page > 0){
							SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_page + 1;
						}			
					}
					else{
						SalesOrderConstants.SL_total_page = 1;
					}				
				}			
				System.out.println("SL_total_page->"+SalesOrderConstants.SL_total_page);
			} catch (Exception sgghr) {
				SapGenConstants.showErrorLog("On setPageData : "+sgghr.toString());
			}
	    }//fn setPageData
	    
	 
    /*private OnClickListener addimglistener = new OnClickListener(){
		public void onClick(View v) {
			showSalesOrdCreationScreen();
        }
    };*/
	
    private OnClickListener refreshimglistener = new OnClickListener(){
		public void onClick(View v) {
			refreshList();
        }
    };
    
  /* private OnClickListener copylistener = new OnClickListener(){
        public void onClick(View v) {       	
        	showAlert();        	
        }
  };*/
    
  public void changeTotalPage(){
		if(mattArrList.size() > 0){
			SalesOrderConstants.SL_total_record = mattArrList.size();	
			if(SalesOrderConstants.SL_total_record > SalesOrderConstants.page_size ){
				SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_record/SalesOrderConstants.page_size;	
				int remain_page = SalesOrderConstants.SL_total_record%SalesOrderConstants.page_size;
				if(remain_page > 0){
					SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_page + 1;
				}					
			}		
		}			
		SapGenConstants.showLog("SL_total_page->"+SalesOrderConstants.SL_total_page);	
	}
  
  public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK ) {  		
  		SalesOrderConstants.SL_current_page = 1;
  		SalesOrderConstants.SL_previous_page = 0;
  		SalesOrderConstants.SL_total_page = 0;
  		SalesOrderConstants.SL_total_record = 0;
  		finish();
      }
      return super.onKeyDown(keyCode, event);
  } 
	  public void refreshList(){
	   	flag_pref = false;
	   	initSalesListSoapConnection();
	  }//refreshList
   	 	  
	private void getCheckedItems(){
	    try{ 
	    	HashMap<String, String> chckdorders = null;
	    	HashMap<String, String> chckdfinalorders = null;
	    	 ArrayList<String> ItemsCompArrList = new ArrayList<String>();	
	    	if(SelectdfinalList!=null)
	    		SelectdfinalList.clear();
	    		
	    	if(SOCheckedList!=null)
	    		SOCheckedList.clear();
	    	String spname ="",name="";	
	    	 if(dbcopyobj!=null)
				 dbcopyobj =null;
	    	if(dbcopyobj == null)
	    		dbcopyobj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_HEAD_LIST);
	    	dbcopyobj.getColumns();	
	    	 SapGenConstants.showLog("selchkboxlist size"+selchkboxlist.size());
	    	tableExits2 = dbObj.checkTable();
			if(tableExits2)
				SOCheckedList = dbcopyobj.readDataSelectedIdListDataFromDB(this,selchkboxlist,curLabel);
					
			 if(selchkboxlist != null && selchkboxlist.size() > 0){				
				 SelectdfinalList.add(SOCheckedList.get(0));	
				 chckdfinalorders = (HashMap<String, String>)SelectdfinalList.get(0);				
					String matidStr =(String) chckdfinalorders.get(DBConstants.SO_HEAD_COL_NAME1A);
					ItemsCompArrList.add(matidStr);
				 //CustNameList.add(SOCheckedList.get(0));
	           	for (int i1 =1; i1 <selchkboxlist.size(); i1++) {
	                	try {	 
	                		 chckdorders = (HashMap<String, String>)SOCheckedList.get(i1);
	                		 spname = (String)chckdorders.get(DBConstants.SO_HEAD_COL_NAME1A);	                			                		
	                		if(!ItemsCompArrList.contains(spname)){
	                			 SapGenConstants.showLog("spname"+spname);
	                			SelectdfinalList.add(SOCheckedList.get(i1));	
	                			ItemsCompArrList.add(spname);
	                		}
						} catch (Exception e) {
							SapGenConstants.showErrorLog("Error on getCheckedItems : "+e.toString());
						} 		
	            	}
	    		}	    											
	    	SapGenConstants.showLog("SelectdfinalList size "+SelectdfinalList.size());
	    	SapGenConstants.showLog("CustNameList size "+CustNameList.size());
	    	//getting all details of checked orders	    		    				
				if(SelectdfinalList.size()==1){				
					SOCreationScreen(pos);
				}else{										
					try{   		     	       	
		            	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		        		  View layout;
		        		  
		        		  layout = inflater.inflate(R.layout.salesorderdialog_list,
		        				  (ViewGroup) findViewById(R.id.listviewlineardialog));		        		       		  
		        		  
		        		  ListView listview = (ListView)layout.findViewById(R.id.list1);
		        		  SOCustomerListAdapter = new SOCustomerListAdapter(this);
		        		  
		        			        		  
		        		  builder = new AlertDialog.Builder(this).setTitle("Select a Customer");	        		  	        		 
		        		  builder.setInverseBackgroundForced(true);
		        		  View view=inflater.inflate(R.layout.custom_titli_dialog, null);
		        		  builder.setCustomTitle(view);	        		 
		        		  builder.setView(layout); 	        		
		        		  builder.setSingleChoiceItems(SOCustomerListAdapter, -1,new DialogInterface.OnClickListener() { 
		  	        		public void onClick(DialogInterface dialog, int which) {	  	        			
		  	        			SalesOrderConstants.showLog("which : "+which);
		  	        			selctdPos=which;
		  	        			if(flagPos==1)
		  	        				flagPos=0;
		  	        			SalesOrderConstants.showErrorLog("Selected Position : "+selctdPos);	  	        			
		  	        			SOCreationScreen(selctdPos);
		  	        			SalesOrderConstants.showLog("selctdPos : "+selctdPos);
		  	        			
		  	        			alertDialog.dismiss();
		  	        		}
		  	        	});
		        		  alertDialog = builder.create();    		  
		        		  alertDialog.show();
		        		 
		        	}catch(Exception sfg){
		        		SalesOrderConstants.showErrorLog("Error in showAlert : "+sfg.toString());
		        	}	
				}
				//fetching details and displaying in pop up window				    
	    }
	    catch(Exception assf){
	    	SalesOrderConstants.showLog("On getCheckedItems : "+assf.toString());
	    }
	    dbObj.closeDBHelper();	
	}//fn showStockListItemScreen
		
	public void showAlert(){
		getCheckedItems();	
	 }//showAlert	 	 
	
	 private void SOCreationScreen(int selPos){
		 try{				 
			 SalesOrderConstants.showLog("Show Sales Order Creation btn clicked");	
			 if(SelectdfinalList.size()==1){
				 custDetail = (HashMap<String, String>)SelectdfinalList.get(0);	
				custIdStr = custDetail.get(DBConstants.SO_MAIN_COL_VBELN);
			 }else{
				 custDetail = (HashMap<String, String>)SelectdfinalList.get(selPos);	
					custIdStr = custDetail.get(DBConstants.SO_MAIN_COL_VBELN);
			 }			 
			/* if(selchkboxlist.size()>1 && SelectdfinalList.size()==1){
					custDetail = (HashMap<String, String>)SOCheckedList.get(0);	
					custIdStr = custDetail.get(DBConstants.SO_MAIN_COL_VBELN);
				}else{
					custDetail = (HashMap<String, String>)mattArrList.get(selPos);	
					custIdStr = custDetail.get(DBConstants.SO_MAIN_COL_VBELN);
				}		*/	 			 							
		 	showSalesOrdCustCreationScreen();				 				
	 	}catch(Exception assf2){
	    	SalesOrderConstants.showLog("On SOCreationScreen : "+assf2.toString());
	 	}		    	
	 }//	 
	 	 
	 private ArrayList getDialogSelectedStockItems(){		
		 ArrayList strSelctdItemsArrList = new ArrayList();	 
		 ArrayList finalItemsArrList = new ArrayList();	
		 HashMap<String, String> finalMattMap = null; 
		 ArrayList<String> ItemsCompArrList = new ArrayList<String>();	
		 try{	
			 if(strSelctdItemsArrList!= null)
				 strSelctdItemsArrList.clear();
			 
			 if(dbcopyobj!=null)
				 dbcopyobj =null;
			 if(dbcopyobj == null)
		    		dbcopyobj = new DataBasePersistent(this,DBConstants.TABLE_SALESORDER_ITEM_LIST);
		    	dbcopyobj.getColumns();	
		    	
		    	tableExits3 = dbObj.checkTable();
				if(tableExits3)
					strSelctdItemsArrList = dbcopyobj.readDataSelectedIdListDataFromDB(this,selchkboxlist,curLabel);
				//finalMattMap = (HashMap<String, String>) strSelctdItemsArrList.get(0); 
				
				/*for(int i=0;i<strSelctdItemsArrList.size();i++){	
					finalMattComMap = (HashMap<String, String>) strSelctdItemsArrList.get(i);										
				}//		
*/				finalMattMap = (HashMap<String, String>) strSelctdItemsArrList.get(0); 
				finalItemsArrList.add(strSelctdItemsArrList.get(0));
				String matidStr =(String) finalMattMap.get(DBConstants.MATNR_COLUMN_NAME);
				ItemsCompArrList.add(matidStr);
				for(int k=1;k<strSelctdItemsArrList.size();k++){																					
						finalMattMap = (HashMap<String, String>) strSelctdItemsArrList.get(k); 
						String matid =(String) finalMattMap.get(DBConstants.MATNR_COLUMN_NAME);
						if(!ItemsCompArrList.contains(matid)){
							finalItemsArrList.add(strSelctdItemsArrList.get(k));
							ItemsCompArrList.add(matid);
						}
					
				}
				
				SalesOrderConstants.showLog("finalItemsArrList size : "+finalItemsArrList.size());
				dbcopyobj.closeDBHelper();
		 }
		 catch(Exception sggh){
			 SalesOrderConstants.showLog("On getDialogSelectedStockItems : "+sggh.toString());
			 dbcopyobj.closeDBHelper();
		 }
		 return finalItemsArrList;		 
	 }//fn getSelectedStockItems	 
	 
	 private void showSalesOrdCustCreationScreen(){
    	try {
    		if(MaterialList!=null)
    			MaterialList.clear();
    		MaterialList = getDialogSelectedStockItems();
    		boolean flag=true;	    		
			Intent intent = new Intent(this, SalesOrderCreationTablet.class);
			intent.putExtra("selMatIds", custDetail);
			intent.putExtra("flag", flag);					
			intent.putExtra("soitemdetails", MaterialList);
			//intent.putExtra("CustId", CustId);							
			
			startActivityForResult(intent, SapGenConstants.SO_CREATION_SCREEN_TABLET);
		} 
		catch (Exception adwf) {
			SalesOrderConstants.showErrorLog("Error in showSalesOrdCreationScreen : "+adwf.getMessage());
		}
    }//showSalesOrdCustCreationScreen
	 
	public class SOCustomerListAdapter extends BaseAdapter {	    			
	    LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    HashMap<String, String> checkdCustmap = null;
	    	
	    public SOCustomerListAdapter(Context context) {
	    	// Cache the LayoutInflate to avoid asking for a new one each time.
	    	mInflater = LayoutInflater.from(context);   
	    }
	    
	    public int getCount() {
	    	try {
	    		if(SelectdfinalList != null)
	    			return SelectdfinalList.size();
	    		SalesOrderConstants.showLog("SOCheckedList size in adapter "+SOCheckedList.size());
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
            convertView = mInflater.inflate(R.layout.salesorderdialog, null);
            holder = new ViewHolder();
            holder.ctname = (TextView) convertView.findViewById(R.id.ctname);              
            holder.llitembg1 = (LinearLayout) convertView.findViewById(R.id.llitembg1);

            if(position%2 == 0)
				holder.llitembg1.setBackgroundResource(R.color.item_even_color);
			else
				holder.llitembg1.setBackgroundResource(R.color.item_odd_color);
            
            try {
            	if(SelectdfinalList != null){  
            		checkdCustmap = (HashMap<String, String>) SelectdfinalList.get(position); 
            		String spname = (String) checkdCustmap.get(DBConstants.SO_HEAD_COL_NAME1A);
            		//String spname= SelectdfinalList.get(position).toString();
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

    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, MAIN_ID, 0, "Refresh");
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
			case MAIN_ID: {
				refreshList();
		        break;
			}
	    }
		return super.onOptionsItemSelected(item);
	}
	        
    private void showSalesOrdCreationScreen(){
		try {
			Intent intent = new Intent(this, SalesOrderCreationTablet.class);
			startActivityForResult(intent, SapGenConstants.SO_CREATION_SCREEN_TABLET);
		} 
		catch (Exception adwf) {
			SalesOrderConstants.showErrorLog("Error in showSalesOrdCreationScreen : "+adwf.getMessage());
		}
	}//fn showSalesOrdCreationScreen  
    
    private void showStockListItemScreen(String selIndex, String labelStr){
        try{
        	SalesOrderConstants.showLog("Selected Index : "+selIndex);
        	if(SalesOrderConstants.stocksItemMainArrList!=null)
        		SalesOrderConstants.stocksItemMainArrList.clear();
        	SalesOrderConstants.stocksItemMainArrList.addAll(metaItemArray);
        	        	
        	if(SalesOrderConstants.metaheadlistArray!=null)
        		SalesOrderConstants.metaheadlistArray.clear();
        	SalesOrderConstants.metaheadlistArray.addAll(metaHeadArray);
        	
        	if(SalesOrderConstants.soItemArrList!=null)
        		SalesOrderConstants.soItemArrList.clear();
        	SalesOrderConstants.soItemArrList.addAll(mattItemArrList);
        	        	
        	Intent intent = new Intent(this, SalesOrderItemScreenTablet.class);
			intent.putExtra("selectdSoNumb", selIndex);		
			intent.putExtra("selectdLabelStr", labelStr);			
			intent.putExtra("offlinestr", offlinemode);
			
			startActivityForResult(intent, SapGenConstants.SALESORD_ITEM_SCREEN);
        }
        catch(Exception assf){
        	SalesOrderConstants.showLog("On showStockListItemScreen : "+assf.toString());
        }
    }//fn showStockListItemScreen
    
   /* private void showStockListItemScreen(String selIndex){
        try{
        	SalesOrderConstants.showLog("Selected Index : "+selIndex);
        	SalesOrdProHeadOpConstraints stkCategory = null;
            boolean errFlag = false;
            if(mattArrList != null){
                if(mattArrList.size() > selIndex){
                    stkCategory = (SalesOrdProHeadOpConstraints)mattArrList.get(selIndex);
                    if(stkCategory != null){
                    	try {
                    		String soNoStr1 = stkCategory.getDocumentNo().trim();
                    		SalesOrderConstants.showLog("Selected Document No : "+soNoStr1);
                    		SalesOrderConstants.stocksItemMainArrList = getSelectedStockItems(soNoStr1);
                    		if(SalesOrderConstants.stocksItemMainArrList != null){
                    			SalesOrderConstants.showLog("stocksItemMainArrList Size : "+SalesOrderConstants.stocksItemMainArrList.size());
                    			
    							Intent intent = new Intent(this, SalesOrderItemScreenTablet.class);
    							intent.putExtra("stkCategoryObj", stkCategory);
    							startActivityForResult(intent, SapGenConstants.SALESORD_ITEM_SCREEN);
                    		}
                    		else
                    			errFlag = true;
						} 
						catch (Exception e) {
							SalesOrderConstants.showErrorLog(e.getMessage());
						}
                    }
                    else
                        errFlag = true;
                }
                else{
                    errFlag = true;
                }
            }
            else{
                errFlag = true;
            }
            
            if(errFlag == true)
            	SalesOrderConstants.showErrorDialog(this, ""+R.string.SALESORDPRO_STKLIST_ERR_DETCANT);
        }
        catch(Exception assf){
        	SalesOrderConstants.showLog("On showStockListItemScreen : "+assf.toString());
        }
    }//fn showStockListItemScreen
*/    
    private ArrayList getSelectedStockItems(String docNo){
    	ArrayList strItemsArrList = null;
    	String itemdocNo = "",matno="";
    	try{
    		if(!docNo.equalsIgnoreCase("")){
    			if(mattItemArrList != null){
    				SalesOrderConstants.showLog("mattItemArrList Size : "+mattItemArrList.size()+" : "+docNo);
    				SalesOrdProItemOpConstraints itemStkObj = null;
    				strItemsArrList = new ArrayList();
    				/*if(strItemsArrList!=null)
    					strItemsArrList.clear();*/
    				for(int k=0; k<mattItemArrList.size(); k++){
    					itemStkObj = (SalesOrdProItemOpConstraints)mattItemArrList.get(k);
    					if(itemStkObj != null){
    						itemdocNo = itemStkObj.getDocumentNo().trim();
    						if(docNo.equalsIgnoreCase(itemdocNo)){
    							strItemsArrList.add(itemStkObj);								    							    							
    						}
    					}
    				}
    			}
    		}
    	}
    	catch(Exception sggh){
    		SalesOrderConstants.showLog("On getSelectedStockItems : "+sggh.toString());
    	}
    	return strItemsArrList;
    }//fn getSelectedStockItems
    
    private void initContextSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;          	
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[5];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;           
            //C0[2].Cdata = "EVENT[.]SALES-ORDER-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";	
            if(!flag_pref){           	
            	C0[2].Cdata = "EVENT[.]SALES-ORDER-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]REFRESHED";	
			}else{				
				C0[2].Cdata = "EVENT[.]SALES-ORDER-CONTEXT[.]VERSION[.]0";	
			}
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());

            respType = SapGenConstants.SO_OV_CNTXT_SCREEN_TABLET;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initContextSoapConnection
        
    private void initSalesListSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        String custIdStr = "";
        try{
    		if((mainCustomerId != null) && (!mainCustomerId.equalsIgnoreCase("")))
    			custIdStr = mainCustomerId;
    	}
    	catch(Exception sgg){}
    	
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[5];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            //C0[2].Cdata = "EVENT[.]SALES-ORDER-FOR-EMPLY-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			if(!flag_pref){           	
            	C0[2].Cdata = "EVENT[.]SALES-ORDER-FOR-EMPLY-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]REFRESHED";
			}else{				
				C0[2].Cdata = "EVENT[.]SALES-ORDER-FOR-EMPLY-GET[.]VERSION[.]0";
			}
			C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_CNTCTKEY[.]KUNNR[.]PARNR[.]";
            C0[4].Cdata = "ZGSXCAST_CNTCTKEY[.]"+custIdStr+"[.][.]";           
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
            
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());

            respType = SapGenConstants.SO_OV_SCREEN_TABLET;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initSalesListSoapConnection
    
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
	    		if(respType == SapGenConstants.SO_OV_CNTXT_SCREEN_TABLET){
	    			if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				SalesOrderMainScreenTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(SalesOrderMainScreenTablet.this, "", getString(R.string.COMPILE_DATA),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                    	    			updateSOContext(resultSoap);	                        				
	                        				sleep(2000);
	                        			} catch (Exception e) {  }
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
	    		}
		    	else if(respType == SapGenConstants.SO_OV_SCREEN_TABLET){
		    		if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				SalesOrderMainScreenTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(SalesOrderMainScreenTablet.this, "", getString(R.string.COMPILE_DATA),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                        				updateServerResponse(resultSoap);
	                        			} catch (Exception e) {  }
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
		    	} else if(respType==SapGenConstants.SO_PING){
		    		initContextSoapConnection();
		    	}
	    		}else{
	    			offlineFlag = 1;
	    			initDBConnection();
	    		}	    		
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
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
    
    public void updateSOContext(SoapObject soap){		
        try{ 
            String[] metaLength = null;
            String[] docTypeArray = new String[1];
            String[] uiConfgArray = null;
        	if(soap != null){
        		SalesOrderCntxtOpConstraints custCntx = null;
        		if(cntxArrList != null)
        			cntxArrList.clear();
    			if(metaNamesArray != null)
    				metaNamesArray.clear(); 
        		
    			SapGenConstants.showLog("Count : "+soap.getPropertyCount());
    			
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[40];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, metaSize = 0, eqIndex = 0, resIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
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
	                                    		//DBConstants.TABLE_SALESORDER_CONTEXT_LIST = "SO"+docTypeStr;
		                                    	SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		cntxsListCount = Integer.parseInt(rowCount);
	                                    		cntxsListType = respType;
	                                        }
	                                        
	                                    }
                                    }
                                    //SapGenConstants.showLog("resC :"+ resC);
                                    resC++;
                                    metaSize = resC-1;
                                    SapGenConstants.showLog("metaSize :"+ metaSize);
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaLength = new String[metaSize];
                                metaNamesArray.add(last);
                            }
	                        if(j > 2){
	                           /* result = pii.getProperty(j).toString();
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
	                                custCntx = new SalesOrderCntxtOpConstraints(resArray);
	                                if(custCntx != null)
	                                	cntxArrList.add(custCntx);	*/
	                        	  result = pii.getProperty(j).toString();
	                        	  //SapGenConstants.showLog("result :"+ result);
		                            firstIndex = result.indexOf(delimeter);
		                           // SapGenConstants.showLog("firstIndex :"+ firstIndex);
		                            eqIndex = result.indexOf("=");
		                            eqIndex = eqIndex+1;
		                            //SapGenConstants.showLog("eqIndex :"+ eqIndex);
		                            firstIndex = firstIndex + 3;
		                            //SapGenConstants.showLog("firstIndex :"+ firstIndex);
		                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	    
		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);
		                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
			                        	resArray = new String[metaLength.length]; 
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
		 	                            uiConfgArray = new String[resArray.length];
		 	                            uiConfgArray = Arrays.copyOf(resArray, resArray.length, String[].class);
		 	                           cntxArrList.add(uiConfgArray);
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
	                                //if(taskErrorMsgStr!=null)
                                 	  
	                                this.runOnUiThread(new Runnable() {
	                                    public void run() {
	                                    	SapGenConstants.showErrorDialog(SalesOrderMainScreenTablet.this, taskErrorMsgStr);
	                                    	 initDBConnection();
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
        	SapGenConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{
        	SapGenConstants.showLog("cntxArrList : "+cntxArrList.size());
        	SapGenConstants.showLog("metaNamesArray : "+metaNamesArray.size());
        	stopProgressDialog();
        	try{
        		//if((cntxArrList!=null) && (cntxArrList.size()>0)){        			
        			//insertCntxData();
        			Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertCntxData();
                			}catch(Exception e1){
                				SapGenConstants.showErrorLog("Error in insertUiConfDataIntoDB Thread:"+e1.toString());
                			}
                			soHandler.post(uiContext);
                            stopProgressDialog();
                		}
        	            
        			};
        	        t.start();	
        		//}
        		/*else{        			
                		this.runOnUiThread(new Runnable() {
                            public void run() {
        		        		stopProgressDialog();
        		        		initDBConnection();
                            }
                        });	
        		}*/
        	
        	}catch(Exception sff1){
        		SapGenConstants.showErrorLog("On updateServerResponseForContext : "+sff1.toString());
        	}
        	
        }
    }//fn updateServerResponseForContext
    
    final Runnable uiContext = new Runnable(){
        public void run()
        {
        	try{
        		setTitleValue();
        		initSalesListSoapConnection();	   
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    private void insertCntxData() {
    	try {
    		
    		if(dbObj != null)
    			dbObj =null;
    		dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);				
    		if(metaNamesArray != null && metaNamesArray.size() > 0){ 
    			if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
    				DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
        		}
			//if(metaNamesArray != null && metaNamesArray.size() > 0){
				if(cntxArrList != null && cntxArrList.size() > 0){	
					String columnLists = "";
					if(metaNamesArray != null && metaNamesArray.size() > 0){
						for(int i=0; i<metaNamesArray.size(); i++){
							if( i == (metaNamesArray.size() - 1)){
								columnLists += metaNamesArray.get(i).toString().trim();
							}else{
								columnLists += metaNamesArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);	
					
					if(metaNamesArray != null && metaNamesArray.size() > 0){
						metaNamesArray.clear();
					}					
					SapGenConstants.showLog(" 1 ");
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								SapGenConstants.showLog("  "+separated[i]);
								metaNamesArray.add(separated[i].toString().trim());
							}
						}
					}			
					SapGenConstants.showLog(" 2 ");
					//DataBasePersistent.setProductUIConfTableContent(metaNamesArray);
					DataBasePersistent.setProductUIConfTableContent(metaNamesArray);		
					DataBasePersistent.setColumnName(metaNamesArray);	
					SapGenConstants.showLog(" 2.1 ");					
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.TABLE_SALESORDER_CONTEXT_LIST+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(this);
					}
					//this.deleteDatabase(DBConstants.DB_TABLE_NAME);
										
					/*if(dbObj != null)
						dbObj =null;
					dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);*/	
					
				
					if(cntxsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((cntxsListCount == 0) && (cntxArrList.size() == 0)){							
							dbObj.clearProductUIConfListTable();
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() > 0)){							
							dbObj.clearProductUIConfListTable();							
							insertCntxDataInToLDB(cntxArrList);
						}
					}
					if(cntxsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((cntxsListCount == 0) && (cntxArrList.size() == 0)){							
							dbObj.clearProductUIConfListTable();
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() == 0)){
						}
						else if((cntxsListCount > 0) && (cntxArrList.size() > 0)){							
							dbObj.clearProductUIConfListTable();
							insertCntxDataInToLDB(cntxArrList);
						}
					}
				}
			}
    		
    		//dbObjUIConf.closeDBHelper();	
    		
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On insertCntxData: "+e.toString());
		}
	}//fn insertCntxData
    
   protected void onDestroy() {
    	super.onDestroy();    	    	    	
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}   	
    }
    
    public void insertCntxDataInToLDB(ArrayList cntxArrList){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
			if(cntxArrList != null && cntxArrList.size() > 0){
				dbObj.insertProductUIConfDetails(cntxArrList);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertCntxDataInToLDB : "+e.toString());
		}        	
	}//fn insertProductUIConfDataIntoDB 
  
    
   public void updateServerResponse(SoapObject soap){		
    	
		String[] soHeadArray = null;
		String[] soItemArray = null;
		String[] resArray = null;
		String[] metaProductLength = null;
        String[] metaAtta01Length = null;
           try{ 
           	if(soap != null){         			
           		emptyAllVectors();  
           		if(metaHeadArray!=null)
           			metaHeadArray.clear();
           		
           		if(metaItemArray!=null)
           			metaItemArray.clear();
           		
       			 String delimeter = "[.]", result="", res="", docTypeStr = "";
    	            SoapObject pii = null;
    	           //String[] resArray = new String[37];
                   int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0, metaSize=0;                
                   
                   for (int i = 0; i < soap.getPropertyCount(); i++) {                
                       pii = (SoapObject)soap.getProperty(i);
                       propsCount = pii.getPropertyCount();
                       //SalesOrderConstants.showLog("propsCount : "+propsCount);
                       if(propsCount > 0){
                           for (int j = 0; j < propsCount; j++) {
                        	   //SalesOrderConstants.showLog(j+" : "+pii.getProperty(j).toString());
                               if(j ==2){
                               	result = pii.getProperty(j).toString();
                               	//SalesOrderConstants.showLog("result : "+result +"j value"+j);
                                   firstIndex = result.indexOf(delimeter);
                                   //SalesOrderConstants.showLog("firstIndex "+firstIndex);
                                   eqIndex = result.indexOf("=");
                                   eqIndex = eqIndex+1;
                                   firstIndex = firstIndex + 3;
                                   docTypeStr = result.substring(eqIndex, (firstIndex-3));
                                   result = result.substring(firstIndex);
                                   //SalesOrderConstants.showLog("result "+result);                               
                                   resC = 0;
                                   indexA = 0;
                                   indexB = result.indexOf(delimeter);
                                  // SalesOrderConstants.showLog("indexB "+indexB);      
                                   int index1 = 0;
                                   while (indexB != -1) {
                                       res = result.substring(indexA, indexB);
                                       indexA = indexB + delimeter.length();
                                       indexB = result.indexOf(delimeter, indexA);
                                       //SalesOrderConstants.showLog("Result resp : "+resC+" : "+res);
                                       if(resC == 0){
                                       	docTypeStr = res;
                                       }
                                       if(resC > 1){
                                    	   metaHeadArray.add(res);                                    	   
                                       }
                                       if(resC == 1){
    	                                    String[] respStr = res.split(";");
    	                                    if(respStr.length >= 1){
    	                                    	String respTypeData = respStr[0];
    	                                    	//SalesOrderConstants.showLog("respTypeData : "+respTypeData);
    	                                    	index1 = respTypeData.indexOf("=");
    	                                    	index1 = index1+1;
    	                                        String respType = respTypeData.substring(index1, respTypeData.length());
    	                                        //SalesOrderConstants.showLog("respType : "+respType);
    	                                    	
    	                                    	String rowCountStrData = respStr[1];
    	                                    	//SalesOrderConstants.showLog("rowCountStrData : "+rowCountStrData);
    	                                    	index1 = rowCountStrData.indexOf("=");
    	                                    	index1 = index1+1;
    	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
    	                                    	//ServiceProConstants.showLog("rowCount : "+rowCount);
    	                                    	if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDHEADER10")){
    		                                    	//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
    		                                    	stockheadListCount = Integer.parseInt(rowCount);
    		                                    	stockheadListType = respType;
    		                                    	DBConstants.TABLE_SALESORDER_HEAD_LIST= docTypeStr;
    	                                        }	                                 	                                    	
    	                                    	/*if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDITEM10")){
    		                                    	//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);   	                                    		
    		                                    	stockItemListCount = Integer.parseInt(rowCount);
    		                                    	stockItemListType = respType;
    		                                    	DBConstants.TABLE_SALESORDER_ITEM_LIST = docTypeStr;
    	                                        }	 */                                    
    	                                    }
                                       }
                                       resC++;
                                       metaSize = resC-1;
                                       /*if(resC == 2)
                                       	break;*/
                                   }          
                                   int endIndex = result.lastIndexOf(';');
                                   metaAtta01Length = new String[metaSize];   
                                   String last = result.substring(indexA,endIndex);
                                   SapGenConstants.showLog("last :"+ last);                                  
                                   metaHeadArray.add(last);
                               }
                            	if(j == 3){
                                	result = pii.getProperty(j).toString();
                                    firstIndex = result.indexOf(delimeter);
                                    eqIndex = result.indexOf("=");
                                    eqIndex = eqIndex+1;
                                    firstIndex = firstIndex + 3;
                                    docTypeStr = result.substring(eqIndex, (firstIndex-3));
                                    result = result.substring(firstIndex);
                                   // SapGenConstants.showLog("Result j==3 : "+result);                                
                                    resC = 0;
                                    indexA = 0;
                                    //resIndex = 0;
                                    indexB = result.indexOf(delimeter);
                                    int index1 = 0;
                                    while (indexB != -1) {
                                        res = result.substring(indexA, indexB);
                                        indexA = indexB + delimeter.length();
                                        indexB = result.indexOf(delimeter, indexA);
                                        //SapGenConstants.showLog("Result resp of item details : "+resC+" : "+res);
                                        if(resC == 0){
                                        	docTypeStr = res;
                                        }
                                        if(resC > 1){
                                        	metaItemArray.add(res);
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
    	                                        if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDITEM10")){
    		                                    	//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);   	                                    		
    		                                    	stockItemListCount = Integer.parseInt(rowCount);
    		                                    	stockItemListType = respType;
    		                                    	DBConstants.TABLE_SALESORDER_ITEM_LIST = docTypeStr;
    	                                        }	
    	                                    }
                                        }
                                        //SapGenConstants.showLog("resC :"+ resC);
                                        resC++;
                                        metaSize = resC-1;
                                    }   
                                    int endIndex2 = result.lastIndexOf(';');
                                    metaProductLength = new String[metaSize];
                                    String last = result.substring(indexA,endIndex2);
                                    //SapGenConstants.showLog("last :"+ last);                                  
                                    metaItemArray.add(last);
                                }
                               if(j > 3){                                   
                            	   result = pii.getProperty(j).toString();
                                   //SapGenConstants.showLog("Result j>4 : "+result);       
   	                            firstIndex = result.indexOf(delimeter);
   	                            eqIndex = result.indexOf("=");
   	                            eqIndex = eqIndex+1;
   	                            firstIndex = firstIndex + 3;
   	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	                            
   	                            if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDHEADER10")){
   		                        	resArray = new String[metaAtta01Length.length]; 
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
	   	 	                        soHeadArray = new String[resArray.length];
	   	 	                       // SapGenConstants.showLog("soHeadArray length :"+ soHeadArray.length);
	   	 	                        soHeadArray = Arrays.copyOf(resArray, resArray.length, String[].class);	
	   	 	                        //soHeadArray =  resArray;
	   	 	                        mattArrList.add(soHeadArray);
	   	 	                    	mattCopyArrList.add(soHeadArray);	   	 	                    
                                   
   	                           /* if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDHEADER10")){   		                        		 	                                                   
	   	 	                        
   	                            }*/
   	                            }
   	                            else if(docTypeStr.equalsIgnoreCase("ZGSEVAST_SDITEM10")){   	                            	
 		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);
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
	 	                           soItemArray = new String[resArray.length];
	 	                           soItemArray = Arrays.copyOf(resArray, resArray.length, String[].class); 	                               
	 	                           mattItemArrList.add(soItemArray);	   	 	                           
   	                            }
   	                            	/*if(mattItemCategory != null)
   	                            		mattItemCategory = null;
   	                                    
   	                            	mattItemCategory = new SalesOrdProItemOpConstraints(resArray);
   	                                
   	                            	if(mattItemArrList != null)
   	                            		mattItemArrList.add(mattItemCategory);	  
   	                            }*/
                               }
                               else if(j == 0){
                                   String errorMsg = pii.getProperty(j).toString();
                                   //ServiceProConstants.showLog("Inside J == 0 ");
                                   int errorFstIndex = errorMsg.indexOf("Message=");
                                   if(errorFstIndex > 0){
                                       int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
                                       final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
                                       SalesOrderConstants.showLog(taskErrorMsgStr);
                                       //if(taskErrorMsgStr!=null)
                                    	   
                                       this.runOnUiThread(new Runnable() {
		                                    public void run() {
		                                    	SalesOrderConstants.showErrorDialog(SalesOrderMainScreenTablet.this, taskErrorMsgStr);
		                                    	initDBConnection();
		                                    }
		                                });
                                   }
                               }
                           }
                       }
                   }
           	}           
           }
           catch(Exception sff){
        	   SalesOrderConstants.showErrorLog("On updateTaskResponse : "+sff.toString());
           } 
           finally{        	   
           	SapGenConstants.showLog("mattArrList Size : "+mattArrList.size());
           	SapGenConstants.showLog("mattItemArrList Size : "+mattItemArrList.size()); 
           	if(mattArrList.size() > 0){    			    						
               	try {               		
               		SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderConstants.PREFS_NAME_FOR_SALES_ORDER, 0);    
   	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
   	    			editor.putBoolean(SalesOrderConstants.PREFS_KEY_SALES_ORDER_FOR_MYSELF_GET, true);    
   	    			editor.commit();   	    			 	    			
               		Thread t = new Thread() 
           			{
           	            public void run() 
           				{
                   			try{
                   				insertSODataIntoDB();
                   			}catch(Exception e1){
                   				SapGenConstants.showErrorLog("Error in deleteInvSelctdData Thread:"+e1.toString());
                   			}
                   			salesOrderData_Handler.post(SOview);
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
           		this.runOnUiThread(new Runnable() {
                       public void run() {
   		        		stopProgressDialog();
   		        		initDBConnection();
                       }
                   });	
           	}     	
           }
       }//fn updateServerResponse
       
       final Runnable SOview = new Runnable(){
           public void run()
           {
           	try{
           		initDBConnection();
           	} catch(Exception sfe){
           		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
           	}
           }	    
       };    
                 
    private void insertSODataIntoDB() {
    	try {
			if(metaHeadArray != null && metaHeadArray.size() > 0){
				if(DBConstants.TABLE_SALESORDER_HEAD_LIST != null && DBConstants.TABLE_SALESORDER_HEAD_LIST.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_HEAD_LIST;
	    		}
				if(mattArrList != null && mattArrList.size() > 0){
					String columnLists = "";
					if(metaHeadArray != null && metaHeadArray.size() > 0){
						for(int i=0; i<metaHeadArray.size(); i++){
							if( i == (metaHeadArray.size() - 1)){
								columnLists += metaHeadArray.get(i).toString().trim();
							}else{
								columnLists += metaHeadArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);					
							
					if(metaHeadArray != null && metaHeadArray.size() > 0){
						metaHeadArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								//SapGenConstants.showLog("  "+separated[i]);
								metaHeadArray.add(separated[i].toString().trim());
							}
						}
					}						
						
					DataBasePersistent.setTableContent(metaHeadArray);
					DataBasePersistent.setColumnName(metaHeadArray);
										
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_HEAD_LIST);
					dbObj.dropTable(SalesOrderMainScreenTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.TABLE_SALESORDER_HEAD_LIST+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(SalesOrderMainScreenTablet.this);
					}	
					if(stockheadListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((stockheadListCount == 0) && (mattArrList.size() == 0)){
	    					//SalesOrderDBOperations.deleteAllTableDataFromDB(SalesOrderMainScreenTablet.this, SalesOrderCP.SO_HEAD_OP_CONTENT_URI);
	    					dbObj.clearListTable();
	        			}
	    				else if((stockheadListCount > 0) && (mattArrList.size() > 0)){
	    					dbObj.clearListTable();
	    					insertSalesOrderHeadOpDataIntoDB(mattArrList);
	        			}
	    			}
	    			if(stockheadListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
	    				if((stockheadListCount == 0) && (mattArrList.size() == 0)){
	    					dbObj.clearListTable();
	        			}
	    				else if((stockheadListCount > 0) && (mattArrList.size() == 0)){
	    					//initDBConnection();
	        			}
	    				else if((stockheadListCount > 0) && (mattArrList.size() > 0)){
	    					dbObj.clearListTable();
	    					/*PreinitDBConnection();
	    					for(int i=0;i<OldMaterialList.size();i++){
	    						if(mattArrList.contains(OldMaterialList.get(i))){
	    							//SapGenConstants.showLog("Data ");
	    						}else{
	    							//f
	    						}
	    					}//
*/	    					insertSalesOrderHeadOpDataIntoDB(mattArrList);
	        			}
	    			}
	    			dbObj.closeDBHelper();
				}	
	    			//item related code
	    			if(metaItemArray != null && metaItemArray.size() > 0){
	    				if(DBConstants.TABLE_SALESORDER_ITEM_LIST != null && DBConstants.TABLE_SALESORDER_ITEM_LIST.length() > 0){
	    	    			DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_ITEM_LIST;
	    	    		}
	    				if(mattItemArrList != null && mattItemArrList.size() > 0){
	    					String columnLists2 = "";
	    					if(metaItemArray != null && metaItemArray.size() > 0){
	    						for(int i=0; i<metaItemArray.size(); i++){
	    							if( i == (metaItemArray.size() - 1)){
	    								columnLists2 += metaItemArray.get(i).toString().trim();
	    							}else{
	    								columnLists2 += metaItemArray.get(i).toString().trim()+":";
	    							}
	    						}
	    					}
	    					SapGenConstants.showLog("columnLists: "+columnLists2);
	    					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists2);					
	    									
	    					if(metaItemArray != null && metaItemArray.size() > 0){
	    						metaItemArray.clear();
	    					}					
	    					if(columnLists2 != null && columnLists2.length() > 0){
	    						String[] separated = columnLists2.split(":");
	    						if(separated != null && separated.length > 0){
	    							for(int i=0; i<separated.length; i++){
	    								SapGenConstants.showLog("  "+separated[i]);
	    								metaItemArray.add(separated[i].toString().trim());
	    							}
	    						}
	    					}			
	    					/*if(dbObjItemColumns == null)
	    						dbObjItemColumns = new DataBasePersistent(ctx,DBConstants.TABLE_SALESORDER_ITEM_LIST);
	    					dbObjItemColumns.insertDetails(metaItemArray);
	    					dbObjItemColumns.closeDBHelper();			*/
	    					
	    					DataBasePersistent.setTableContent(metaItemArray);	
	    					DataBasePersistent.setColumnName(metaItemArray);
	    					    					
	    					if(dbObjitem == null)
	    						dbObjitem = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_ITEM_LIST);
	    					dbObjitem.dropTable(SalesOrderMainScreenTablet.this);
	    					boolean isExits = dbObjitem.checkTable();
	    					SapGenConstants.showLog(DBConstants.TABLE_SALESORDER_ITEM_LIST+" isExits "+isExits);
	    					if(!isExits){
	    						dbObjitem.creatTable(SalesOrderMainScreenTablet.this);
	    					}	
	    			if(stockItemListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((stockItemListCount == 0) && (mattItemArrList.size() == 0)){
	    					dbObjitem.clearListTable();
	        			}
	    				else if((stockItemListCount > 0) && (mattItemArrList.size() > 0)){
	    					dbObjitem.clearListTable();
	    					insertItemDataIntoDB(mattItemArrList);
	        			}
	    			}
	    			if(stockItemListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
	    				if((stockItemListCount == 0) && (mattItemArrList.size() == 0)){
	    					dbObjitem.clearListTable();
	        			}
	    				else if((stockItemListCount > 0) && (mattItemArrList.size() == 0)){
	    					//initDBConnection();
	        			}
	    				else if((stockItemListCount > 0) && (mattItemArrList.size() > 0)){
	    					dbObjitem.clearListTable();
	    					insertItemDataIntoDB(mattItemArrList);
	    					//insertDeltaItemDataIntoDB();
	        			}
	    			}		
				}
				dbObj.closeDBHelper();								
				dbObjitem.closeDBHelper();
			}  
		}
			
    	}catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
    	
    }//

    private void setTitleValue() {
    	try {
    		dbObj =null;
    		if(dbObj == null)
    			dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		SapGenConstants.showLog("DBConstants.TABLE_SALESORDER_CONTEXT_LIST: "+DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		 title = dbObj.getTitle(DBConstants.SO_DEVICE_TYPE_WIDE_OV, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
    			 if(title.indexOf(SapGenConstants.title_offline) > 0){
              		bodylayout.setBackgroundResource(R.drawable.llborder);
              		bodylayout.setPadding(5, 5, 5, 5);
         		 }
    			//OfflineFunc();
        		SapGenConstants.showLog("title: "+title);
    		}   		    		
    		/*searchbarhint = dbObjUIConf.getSearchBarHint(DBConstants.SO_DEVICE_TYPE_WIDE_OV_TOP_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_SEARCHBAR_TAG);
    		if(searchbarhint != null && searchbarhint.length() > 0){
    			searchET.setHint(searchbarhint);
        		SapGenConstants.showLog("SearchHint: "+searchbarhint);
    		}    		*/
    		dbObj.closeDBHelper();
    		
    		/*title = SalesOrderDBOperations.readSOScreenTitleFromDB(this, DBConstants.DEVICE_TYPE_WIDE_OV_TAG);
    		if(title != null && title.length() > 0){
    			//myTitle.setText(titleStr);
    			OfflineFunc();
        		SapGenConstants.showLog("title: "+title);
    		}   		    		
    		searchbarhint = SalesOrderDBOperations.readSOSearchBarFromDB(this,DBConstants.CNTXT4_HINT_TAG);*/
        	
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
    
    private void insertSalesOrderHeadOpDataIntoDB(ArrayList somattArrList) {
    	try {
    		if(dbObj != null)
    			dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_HEAD_LIST);
			boolean isExits = dbObj.checkTable();
			SapGenConstants.showLog(DBConstants.TABLE_SALESORDER_HEAD_LIST+" isExits "+isExits);
			if(somattArrList != null && somattArrList.size() > 0){
				dbObj.insertDetails(somattArrList);
			}
			dbObj.closeDBHelper();			
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertSalesOrderHeadOpDataIntoDB : "+e.toString());
		}        	
	}	
	
    private void insertDeltaSalesOrderHeadOpDataIntoDB() {
    	SalesOrdProHeadOpConstraints salesordrCategory = null;
    	String id = "";
    	boolean check = false;
		try{			
			if(mattArrList != null){
				for(int k=0; k<mattArrList.size(); k++){
					salesordrCategory = (SalesOrdProHeadOpConstraints) mattArrList.get(k);
					if(salesordrCategory != null){
						id = salesordrCategory.getDocumentNo().toString().trim();
						if(id != null && id.length() > 0){
							check = SalesOrderDBOperations.checkIdExists(SalesOrderMainScreenTablet.this, id);
							if(!check){
								SalesOrderDBOperations.insertSalesOrderHeadOutputDataInToDB(SalesOrderMainScreenTablet.this, salesordrCategory);
							}else{
								SalesOrderDBOperations.updateSODataValue(SalesOrderMainScreenTablet.this, id, salesordrCategory);
							}							
						}												
					}
				}
			}	
		}catch(Exception sff){
            SalesOrderConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
	}
	
	private void insertItemDataIntoDB(ArrayList somattItemArrList) {
		try {			
			if(dbObjitem != null)
				dbObjitem = null;
			if(dbObjitem == null)
				dbObjitem = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_ITEM_LIST);
			boolean isExits = dbObjitem.checkTable();
			SapGenConstants.showLog(DBConstants.TABLE_SALESORDER_ITEM_LIST+" isExits "+isExits);
			if(somattItemArrList != null && somattItemArrList.size() > 0){
				dbObjitem.insertDetails(somattItemArrList);
			}
			dbObjitem.closeDBHelper();
		} catch (Exception e) {
			dbObjitem.closeDBHelper();
			SapGenConstants.showErrorLog("On insertItemDataIntoDB : "+e.toString());
		}        	
	}
	
	private void insertDeltaItemDataIntoDB() {
    	SalesOrdProItemOpConstraints mattItemCategory = null;   
    	String id = "";
    	boolean check = false; 	
		try{
			if(mattItemArrList != null){
				for(int k=0; k<mattItemArrList.size(); k++){
					mattItemCategory = (SalesOrdProItemOpConstraints) mattItemArrList.get(k);
					if(mattItemCategory != null){
						id = mattItemCategory.getDocumentNo().toString().trim();
						if(id != null && id.length() > 0){
							check = SalesOrderDBOperations.checkItemIdExists(SalesOrderMainScreenTablet.this, id);
							if(!check){
								SalesOrderDBOperations.insertSalesOrderItemDataInToDB(SalesOrderMainScreenTablet.this, mattItemCategory);
							}else{
								SalesOrderDBOperations.updateSalesOrderItemDataInToDB(SalesOrderMainScreenTablet.this, id, mattItemCategory);
							}							
						}	
					}
				}
			}	
		}catch(Exception sff){
            SalesOrderConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        }     	
	}

	private void emptyAllVectors(){
        try{
            searchStr = "";
            
            if(mattArrList != null)
            	mattArrList.clear();
            
            if(mattCopyArrList != null)
            	mattCopyArrList.clear();
           
            if(mattItemArrList != null)
            	mattItemArrList.clear();
        }
        catch(Exception adsf){
        	SalesOrderConstants.showErrorLog("On emptyAllVectors : "+adsf.toString());
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
			SalesOrderConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
    /*public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		  SalesOrderConstants.showLog("Text : "+s.toString());
		  searchStr = s.toString().trim();
		  filter(searchStr);
	} */
	
	/*public void filter(String charText) {
        try {       	        	
        	charText = charText.toLowerCase();			
        	 mattArrList.clear();
			if (charText.length() == 0) {				
					if(mattCopyArrList != null)
						mattArrList = (ArrayList)mattCopyArrList.clone();											
				}else{
					for (Object obj : mattCopyArrList) {
				    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_KUNAG);	
				    	String custStr = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_NAME1A);	
				    	String soStr = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_VBELN);
				        if (strValue.toLowerCase().contains(charText)){				        
				        	mattArrList.add(obj);	
				        }else if(custStr.toLowerCase().contains(charText)){
			        		mattArrList.add(obj);	
			        	}
				        else if(soStr.toLowerCase().contains(charText)){
			        		mattArrList.add(obj);	
			        	}				        
					}
				}			
			offset = 0;
			data = offset + SalesOrderConstants.page_size;			
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
			setPageData();
			drawSubLayout();
		} catch (Exception sgghrr) {
			SapGenConstants.showErrorLog("On filter : "+sgghrr.toString());
		}
    }*/
	
	  public void filter(String charText) {
	        try {
				charText = charText.toLowerCase();
				mattArrList.clear();
				if (charText.length() == 0) {
					if(mattArrCopyArrList != null)
						mattArrList = (ArrayList)mattArrCopyArrList.clone();	
				} else {
					//if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
						for (Object obj : mattArrCopyArrList) {
					    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_VBELN);
					    	String custStr = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_NAME1A);	
					        if (strValue.toLowerCase().contains(charText)){
					        	//SapGenConstants.showLog("strValue: "+strValue);
					        	mattArrList.add(obj);
					        }else if(custStr.toLowerCase().contains(charText)){
					        	mattArrList.add(obj);
					        }
					    }					
				}
				offset = 0;
				data = offset + SalesOrderConstants.page_size;
	            SapGenConstants.showLog("offset:"+offset);
	            SapGenConstants.showLog("data:"+data);
				setPageData();
				drawSubLayout();
			} catch (Exception sgghrr) {
				SapGenConstants.showErrorLog("On filter : "+sgghrr.toString());
			}
	    }
	  
	/*private void searchItemsAction(String match){  
        try{
            searchStr = match;
            SalesOrdProHeadOpConstraints stkObj = null;
            String custNoStr = "", custNameStr = "", soStr = "", poStr = "";
            if((mattCopyArrList != null) && (mattCopyArrList.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){                                            
                    System.out.println("Match : "+match);  
                    mattArrList.clear();
                    for(int i = 0; i < mattCopyArrList.size(); i++){ 
                        stkObj = null;
                        custNoStr = "";
                        custNameStr = "";
                        soStr = "";
                        poStr = "";
                        stkObj = (SalesOrdProHeadOpConstraints)mattCopyArrList.get(i);
                        if(stkObj != null){
                        	try {
								custNoStr = stkObj.getSoldToParty().trim().toLowerCase();
								custNameStr = stkObj.getSPName().trim().toLowerCase();
								soStr = stkObj.getDocumentNo().trim().toLowerCase();
								poStr = stkObj.getPurchaseOrderNo().trim().toLowerCase();
							} catch (Exception e) {
								e.printStackTrace();
								if(custNoStr == null)
									custNoStr = "";
								
								if(custNameStr == null)
									custNameStr = "";
								
								if(soStr == null)
									soStr = "";
								
								if(poStr == null)
									poStr = "";
							}
                        	
                            match = match.toLowerCase();
                            if((custNoStr.indexOf(match) >= 0) || (custNameStr.indexOf(match) >= 0) || 
                            	   (soStr.indexOf(match) >= 0) || (poStr.indexOf(match) >= 0)){
                            	mattArrList.add(stkObj);
                            }
                        }
                    }//for 
                    initLayout();
        			//searchET.setText(searchStr);
                }
                else{
                    System.out.println("Match is empty");
                    mattArrList.clear();
                    for(int i = 0; i < mattCopyArrList.size(); i++){  
                        stkObj = (SalesOrdProHeadOpConstraints)mattCopyArrList.get(i);
                        if(stkObj != null){
                        	mattArrList.add(stkObj);
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
        	SalesOrderConstants.showErrorLog("Error On searchItemsAction : "+we.toString());
        }
    }//fn searchItemsAction  
*/    
	/*
	private void sortItemsAction(int sortInd){
		try{
			 sortIndex = sortInd;
			 switch(sortInd){
			 	case sortHeader1:
			 		sortCNameFlag = !sortCNameFlag;
			 		break;
			 	case sortHeader2:
			 		sortSoNoFlag = !sortSoNoFlag;
			 		break;
			 	case sortHeader4:
			 		sortSoValFlag = !sortSoValFlag;
			 		break;
			 	case sortHeader6:
			 		sortRDateValFlag = !sortRDateValFlag;
			 		break;
			 	case sortHeader7:
			 		sortcustNoFlag = !sortcustNoFlag;
			 		break;
			 	case sortHeader8:
			 		sortPoNoFlag = !sortPoNoFlag;
			 		break;
			 	case sortHeader9:
			 		sortPoDateFlag = !sortPoDateFlag;
			 		break;
			 }			 
			 SalesOrderConstants.showLog("Selected Sort Index : "+sortInd);
			 Collections.sort(mattArrList, priceSortComparator); 				
             initLayout();
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
*/	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	try {   			
    		SalesOrderConstants.showLog("Request Code "+requestCode);
    		SalesOrderConstants.showLog("Result Code "+resultCode);
    		SalesOrderConstants.showLog("Result Code "+RESULT_OK);    		
    		if(requestCode == SapGenConstants.SO_CREATION_SCREEN_TABLET){
    			if(resultCode == RESULT_OK){
    				SalesOrderConstants.showLog("SO Order Created");
    				if(SalesOrderConstants.CustomerMatrArrList.size()!=0)
                		SalesOrderConstants.CustomerMatrArrList.clear();
    				salesOrdcrte=1;
    				this.runOnUiThread(new Runnable() {
    	                public void run() {       	                	
    	                	initSalesListSoapConnection();    	                
    	                }
    	            });
    			}
    		}
		} catch (Exception e) {
			SalesOrderConstants.showErrorLog("onActivityResult : "+e.toString());
		}
    }//fn onActivityResult
	
	private final Comparator priceSortComparator =  new Comparator() {
        public int compare(Object o1, Object o2){ 
            int comp = 0;
            double rateAmt1=0, rateAmt2=0;
            String strObj1 = "0", strObj2="0";
            SalesOrdProHeadOpConstraints repOPObj1, repOPObj2;
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd"); 
            Date dateObj1 = null, dateObj2 = null;
            long timeMil1 = 0, timeMil2 = 0;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                	repOPObj1 = (SalesOrdProHeadOpConstraints)o1;
                    repOPObj2 = (SalesOrdProHeadOpConstraints)o2;
                    
                    if(sortIndex == sortHeader1){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getSPName().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getSPName().trim();
                        
                        if(sortCNameFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else if(sortIndex == sortHeader2){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getDocumentNo().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getDocumentNo().trim();
                        
                        if(sortSoNoFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else if(sortIndex == sortHeader4){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getNetValDocCurr().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getNetValDocCurr().trim();
                        
                        if(!strObj1.equalsIgnoreCase(""))
                        	rateAmt1 = Double.parseDouble(strObj1);
                        	
                    	if(!strObj2.equalsIgnoreCase(""))
                    		rateAmt2 = Double.parseDouble(strObj2);
                    		
                    	if(sortSoValFlag == true)
                            comp = (int) (rateAmt1-rateAmt2);
                        else
                            comp = (int) (rateAmt2-rateAmt1);
                    }
                    else if(sortIndex == sortHeader6){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getRequiredDate().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getRequiredDate().trim();
                        try{
	                        dateObj1 = curFormater.parse(strObj1); 
	                        dateObj2 = curFormater.parse(strObj2); 
	                        timeMil1 = dateObj1.getTime();
	                        timeMil2 = dateObj2.getTime();
	                        strObj1 = String.valueOf(timeMil1);
	                        strObj2 = String.valueOf(timeMil2);
                        }
                        catch(Exception sfg){}
                        if(sortRDateValFlag == true)
                            comp =  strObj1.compareTo(strObj2);
                        else
                            comp =  strObj2.compareTo(strObj1);
                    }
                    else if(sortIndex == sortHeader8){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getPurchaseOrderNo().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getPurchaseOrderNo().trim();
                        
                        if(sortPoNoFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else if(sortIndex == sortHeader9){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getPurchaseOrderDate().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getPurchaseOrderDate().trim();
                        try{
	                        dateObj1 = curFormater.parse(strObj1); 
	                        dateObj2 = curFormater.parse(strObj2); 
	                        timeMil1 = dateObj1.getTime();
	                        timeMil2 = dateObj2.getTime();
	                        strObj1 = String.valueOf(timeMil1);
	                        strObj2 = String.valueOf(timeMil2);
                        }
                        catch(Exception sfg){}
                        
                        if(sortPoDateFlag == true)
                            comp =  strObj1.compareTo(strObj2);
                        else
                            comp =  strObj2.compareTo(strObj1);
                    }
                    else{
                        // Code to sort by Material Desc (default)
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getSoldToParty().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getSoldToParty().trim();
                        
                        if(sortcustNoFlag == true)
                        	comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                }
             }
             catch(Exception qw){
            	 SalesOrderConstants.showErrorLog("Error in Serv Order Comparator : "+qw.toString());
             }
                 
             if (comp != 0) {            
                return comp;
             } 
             else {            
                return 0;
            }
        }
    };
}//
