package com.globalsoft.ProductLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.ProductLib.Database.ProductColumnDB;
import com.globalsoft.ProductLib.Database.ProductDBConstants;
import com.globalsoft.ProductLib.Database.ProductSearchCategoryListPersistent;
import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductMainScreenForPhone extends ListActivity implements TextWatcher {

	private boolean internetAccess = false;
	private boolean tableExits = false;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private ListView listView;
	private SoapObject resultSoap = null;
	private ProgressDialog pdialog = null;
	private TextView myTitle, selCatTV;
	private String serverErrorMsgStr = "";
	private Button catBtn;

	private int uiConfListCount = 0;	
	private String uiConfListType = "";
	private int searchCatListCount = 0;	
	private String searchCatListType = "";
	private int productsListCount = 0;	
	private String productsListType = "";
	private int attach01ListCount = 0;	
	private String attach01ListType = "";
	private int attach01RListCount = 0;	
	private String attach01RListType = "";
	
	private EditText searchET;
	private String searchStr = "";
	private int dispwidth = 300;
	private static int respType = 0;
	public boolean flag_pref_contxt = false, flag_pref_employee_get = false;
	
	private ArrayList uiConfList = new ArrayList();
	private ArrayList alluiConfList = new ArrayList();
	private ArrayList metauiConfListArray = new ArrayList();	
	
	private ArrayList productList = new ArrayList();
	private ArrayList allProductList = new ArrayList();
	private ArrayList metaProdListArray = new ArrayList();	
	
	private ArrayList productListViewArray = new ArrayList();
	private ArrayList allProductListViewArray = new ArrayList();
		
	private ArrayList searchCatList = new ArrayList();
	private ArrayList allSearchCatList = new ArrayList();
	private ArrayList metaSearchCatListArray = new ArrayList();	
	
	private ArrayList attach01List = new ArrayList();
	private ArrayList metaAtta01Array = new ArrayList();
	
	private ArrayList attach01RList = new ArrayList();		
	private ArrayList metaAtta01RArray = new ArrayList();	

    final Handler productsData_Handler = new Handler();
    
	//private DataBasePersistent dbObj = null;
	private DataBasePersistent dbObjAtta01 = null;
	private DataBasePersistent dbObjCatSel = null;
	//private ProductAttachment01RListPersistent dbObjAtta01 = null;
    private MyProductListAdapterForPhone adpterForPhone = null;	
	private DataBasePersistent dbObj = null;
	//private ProductSearchCategoryListPersistent dbObjSerCat = null;
	private ProductColumnDB dbObjColumns = null;
	
	private HashMap<String, String> labelMap = new HashMap<String, String>();	
	private ArrayList valArrayList = new ArrayList();
	private static Context ctx;	
	
	public TextView pageTitle, firstPgTV, lastPgTV, nextPgTV, prevPgTV;
	int offset = 0;
    int data;

	protected CharSequence[] _options = null;
	boolean[] _selections =  null;
	private ArrayList srcCatFilterIdArrList = new ArrayList();
	private ArrayList srcCatFilterArrList = new ArrayList();
	private ArrayList allSrcCatFilterArrList = new ArrayList();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        SapGenConstants.setWindowTitleTheme(this);
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.productmainfph); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.MAINSCR_PRODLIST_TITLE1));
		 
		ctx = this.getApplicationContext();
		dispwidth = SapGenConstants.getDisplayWidth(this);	
		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		
		SharedPreferences settings = getSharedPreferences(ProductLibConstant.PREFS_NAME_FOR_PRODUCTCAT, 0);      
		flag_pref_contxt = settings.getBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_CONTEXT, false);
		flag_pref_employee_get = settings.getBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_FOR_EMPLOYEE_GET, false);
		
		//initLayout();

		catBtn = (Button) findViewById(R.id.catBtn);
		catBtn.setOnClickListener(catBtnListener);
		
		selCatTV = (TextView)findViewById(R.id.selCatTV);
		
		pageTitle = (TextView)findViewById(R.id.pagetitle);
        
        firstPgTV = (TextView)findViewById(R.id.firstpgtv);
        firstPgTV.setOnClickListener(firstPgTVListener);
                
        lastPgTV = (TextView)findViewById(R.id.lastpgtv);
        lastPgTV.setOnClickListener(lastPgTVListener);
        
        nextPgTV = (TextView)findViewById(R.id.nextpgtv);
        nextPgTV.setOnClickListener(nextPgTVListener);

        prevPgTV = (TextView)findViewById(R.id.prepgtv);
        prevPgTV.setOnClickListener(prevPgTVListener);        

        if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
        	pageTitle.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	firstPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	lastPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	nextPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        	prevPgTV.setTextSize(SapGenConstants.TEXT_SIZE_BIG_LABEL);
        }
		
		internetAccess = SapGenConstants.checkConnectivityAvailable(ProductMainScreenForPhone.this);
		if(internetAccess){
			SapGenConstants.showLog("Data from SAP!");	
			if(SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT == 0){
				SapGenConstants.showLog("first time launch");
				SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT = 1;
	    		initStatusSoapConnection();
	    		//setTitleValue();
			}else{
				initSoapConnection();
				//setTitleValue();
			}
		}
		else{
			initDBConnection();	
			SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
		}										
    }
    
    private OnClickListener catBtnListener = new OnClickListener(){
		public void onClick(View v){
			String editStr = searchET.getText().toString().trim();
			if(editStr != null && editStr.length() > 0){
				searchET.setText("");
			}
			showDialog(0);
		}
	};
	
    private OnClickListener firstPgTVListener = new OnClickListener(){
		public void onClick(View v){
			if(ProductLibConstant.PL_previous_page > 0 && ProductLibConstant.PL_current_page > 1){
				changeTotalPage();
				ProductLibConstant.PL_previous_page = 0;
	    		ProductLibConstant.PL_current_page = 1;
	    		lastPgTV.setText(""+ProductLibConstant.PL_total_page);
	    		offset = 0;
                data = offset + ProductLibConstant.page_size;
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
			if(ProductLibConstant.PL_total_page > 1 && ProductLibConstant.PL_current_page < ProductLibConstant.PL_total_page){
				changeTotalPage();
				ProductLibConstant.PL_previous_page = ProductLibConstant.PL_total_page - 1;
	    		ProductLibConstant.PL_current_page = ProductLibConstant.PL_total_page;
	    		lastPgTV.setText(""+ProductLibConstant.PL_total_page);
                
                int remains = ProductLibConstant.PL_total_record%ProductLibConstant.page_size;
                if(remains == 0){
                	offset = ProductLibConstant.PL_total_record - ProductLibConstant.page_size;
                	data = ProductLibConstant.PL_total_record;
                }else{
                	offset = (ProductLibConstant.PL_total_record/ProductLibConstant.page_size)*ProductLibConstant.page_size;
                    data = ProductLibConstant.PL_total_record;
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
			if(ProductLibConstant.PL_total_page > 1 && ProductLibConstant.PL_current_page < ProductLibConstant.PL_total_page){
				changeTotalPage();
	    		ProductLibConstant.PL_previous_page = ProductLibConstant.PL_current_page;
	    		ProductLibConstant.PL_current_page = ProductLibConstant.PL_current_page + 1;
	    		lastPgTV.setText(""+ProductLibConstant.PL_total_page);
	    		offset = offset + ProductLibConstant.page_size;
                data = offset + ProductLibConstant.page_size;
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
			if(ProductLibConstant.PL_previous_page > 0 && ProductLibConstant.PL_current_page > 1){
				changeTotalPage();
	    		ProductLibConstant.PL_current_page = ProductLibConstant.PL_current_page - 1;
	    		ProductLibConstant.PL_previous_page = ProductLibConstant.PL_previous_page - 1;
	    		lastPgTV.setText(""+ProductLibConstant.PL_total_page);	  
	    		if(data != ProductLibConstant.PL_total_record){
		    		offset = offset - ProductLibConstant.page_size;
	                data = data - ProductLibConstant.page_size;
	    		}else{
		    		offset = offset - ProductLibConstant.page_size;
		    		int remain = data%ProductLibConstant.page_size;
		    		if(remain > 0){
		    			data = data - remain;
		    		}else{
		                data = data - ProductLibConstant.page_size;
		    		}
	    		}
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
	
	public void changeTotalPage(){
		if(productList.size() > 0){
			ProductLibConstant.PL_total_record = productList.size();	
			if(ProductLibConstant.PL_total_record > ProductLibConstant.page_size ){
				ProductLibConstant.PL_total_page = ProductLibConstant.PL_total_record/ProductLibConstant.page_size;	
				int remain_page = ProductLibConstant.PL_total_record%ProductLibConstant.page_size;
				if(remain_page > 0){
					ProductLibConstant.PL_total_page = ProductLibConstant.PL_total_page + 1;
				}					
			}		
		}			
		SapGenConstants.showLog("PL_total_page->"+ProductLibConstant.PL_total_page);	
	}
        
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
    		SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT = 0;
    		ProductLibConstant.PL_current_page = 1;
			ProductLibConstant.PL_previous_page = 0;
			ProductLibConstant.PL_total_page = 0;
			ProductLibConstant.PL_total_record = 0;
    		finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
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
			//adpterForTablet.filter(text);
		}else{
			adpterForPhone.filter(text);
		}
	} 
	
    private void initLayout(){
		try{			
            SapGenConstants.showLog("initLayout Calling!");
			searchET = (EditText) findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);

    		setTitleValue();    		
			labelMap = DataBasePersistent.readAllPHLablesFromDB(this, DBConstants.DEVICE_TYPE_OVERVIEW_S_TAG);
			valArrayList = DataBasePersistent.readAllPHValuesFromDB(this, DBConstants.DEVICE_TYPE_OVERVIEW_S_TAG);
			
			drawSubLayout();			
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
    
    private void drawSubLayout(){
		try {
			String pageTitleValue = ""+ProductLibConstant.PL_current_page;
	        pageTitle.setText(pageTitleValue);

	        prevPgTV.setText("<<");
	        nextPgTV.setText(">>");
	        
	        firstPgTV.setText("1");
	        lastPgTV.setText(""+ProductLibConstant.PL_total_page);
	        
	        if(ProductLibConstant.PL_previous_page > 0 && ProductLibConstant.PL_current_page > 1 ){
	        	prevPgTV.setTextColor(Color.BLUE);
	        	firstPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	prevPgTV.setTextColor(Color.BLACK);
	        	firstPgTV.setTextColor(Color.BLACK);
	        }
	        
	        if(ProductLibConstant.PL_total_page > 1 && ProductLibConstant.PL_current_page < ProductLibConstant.PL_total_page ){
	        	nextPgTV.setTextColor(Color.BLUE);
	        	lastPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	nextPgTV.setTextColor(Color.BLACK);
	        	lastPgTV.setTextColor(Color.BLACK);
	        }
    		//setPageData();
			if(productList != null){
				productListViewArray.clear();
				HashMap<String, String> stockMap = null;
                SapGenConstants.showLog("Product List Size  : "+productList.size());  
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
                if(offset <= productList.size() && data <= productList.size() ){
					for (int i = offset; i < data; i++) {
						stockMap = (HashMap<String, String>) productList.get(i); 
	                    if(stockMap != null){
	                    	productListViewArray.add(stockMap);
	                    }					
					}
                }else{
					for (int i = offset; i < productList.size(); i++) {
						stockMap = (HashMap<String, String>) productList.get(i);  
	                    if(stockMap != null){
	                    	productListViewArray.add(stockMap);
	                    }					
					}
                }
			}
            SapGenConstants.showLog("productListViewArray List Size  : "+productListViewArray.size());  
			listviewcall();
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in drawSubLayout:"+ce.toString());
		}
	}//fn drawSubLayout
    
    private void listviewcall(){
		try {			
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				/*adpterForTablet = new MyContactListAdapterForTablet(this);
				setListAdapter(adpterForTablet);*/
			}else{
				adpterForPhone = new MyProductListAdapterForPhone(this);
				adpterForPhone.removeAllTasks();
				setListAdapter(adpterForPhone);
			}
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
    
    //click listener for listview
  	OnItemClickListener listItemClickListener = new OnItemClickListener() {
  		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
  			SapGenConstants.showLog("Selected Item "+arg2);		
  			if(productListViewArray != null){
  				String text1Str = "", matIdStr = "", descStr = "";
  				HashMap<String, String> stockMap = (HashMap<String, String>) productListViewArray.get(arg2);  
        		descStr = (String) stockMap.get(DBConstants.MAKTX_COLUMN_NAME);   
            	matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);   
            	SapGenConstants.showLog("matIdStr "+matIdStr);	
            	Intent intent = new Intent(ProductMainScreenForPhone.this, ProductDetailsForPhone.class);
        		intent.putExtra("matIdStr", matIdStr);
    			startActivityForResult(intent,SapGenConstants.PRODUCT_CAT_MATT_DETAIL_SCREEN_PH);
  			}
  		}
  	};
    
  	private void initStatusSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SapGenIpConstraints C0[];
            C0 = new SapGenIpConstraints[3];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SapGenIpConstraints(); 
            }
                        
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            if(!flag_pref_contxt){
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            }else{
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-CONTEXT[.]VERSION[.]0";
            }
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog("Request:"+request.toString());
            
            respType = SapGenConstants.RESP_TYPE_GET_PROD_CNTX;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initStatusSoapConnection : "+asd.toString());
        }
    }//fn initStatusSoapConnection  	
  	
    private void initSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SapGenIpConstraints C0[];
            C0 = new SapGenIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SapGenIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            if(!flag_pref_employee_get){
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            }else{
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
            }C0[3].Cdata = "ZGSEVDST_MTRLSRCH01[.]*";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog(request.toString());
            respType = SapGenConstants.RESP_TYPE_GET_PROD_LIST;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initSoapConnection
    
    private void doThreadNetworkAction(Context ctx, final Handler handler, final Runnable handlerFnName, final SoapSerializationEnvelope envelopeC, SoapObject request){
    	try{
            soapTask = new StartNetworkTask(ctx);
            this.runOnUiThread(new Runnable() {
                public void run() {
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
            });    		
    	}
    	catch(Exception asgg){
    		SapGenConstants.showErrorLog("Error in doThreadNetworkAction : "+asgg.toString());
    	}
    }//fn doThreadNetworkAction
    
    final Runnable getNetworkResponseRunnable = new Runnable(){
	    public void run(){
	    	try{
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
	    		
	    		if(respType == SapGenConstants.RESP_TYPE_GET_PROD_CNTX){
	    			if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				ProductMainScreenForPhone.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductMainScreenForPhone.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                    	    			updateProductContext(resultSoap);	                        				
	                        				sleep(2000);
	                        			} catch (Exception e) {  }
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
	    		}
		    	else if(respType == SapGenConstants.RESP_TYPE_GET_PROD_LIST){
		    		if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				ProductMainScreenForPhone.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductMainScreenForPhone.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
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
		    	}    			
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
	    	}
	    }	    
    };
    
    public void updateProductContext(SoapObject soap){	
        String[] resArray = null;
        String[] metaUIConfLength = null;
        String[] docTypeArray = new String[5];
		String[] uiConfgArray = null;
        String[] metaSearchCatLength = null;
		String[] searchCatArray = null;		
        try{ 
        	if(soap != null){    			
    			if(uiConfList != null)
    				uiConfList.clear();    	
    			    			
    			if(alluiConfList != null)
    				alluiConfList.clear();    			

    			if(metauiConfListArray != null)
    				metauiConfListArray.clear();  	
    			
    			if(metaSearchCatListArray != null)
    				metaSearchCatListArray.clear();  	
    			
    			if(searchCatList != null)
    				searchCatList.clear();    	
    			    			
    			if(allSearchCatList != null)
    				allSearchCatList.clear(); 
    			           	            
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
                                    	metauiConfListArray.add(res);
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
	                                    	if(j == 2){
	                                    		SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		DBConstants.PRODUCT_UICONF_TABLE_NAME = docTypeStr;
	                                    		docTypeArray[0] = docTypeStr;
	                                    		uiConfListCount = Integer.parseInt(rowCount);
	                                    		uiConfListType = respType;
	                                        }                             
	                                    }
                                    }
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaUIConfLength = new String[metaSize];
                                metauiConfListArray.add(last);
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
                                    	metaSearchCatListArray.add(res);
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
	                                    	if(j == 3){
	                                    		docTypeArray[1] = docTypeStr;
	                                    		DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME = docTypeStr;
	                                    		SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		searchCatListCount = Integer.parseInt(rowCount);
	                                    		searchCatListType = respType;
	                                        }                                 
	                                    }
                                    }
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaSearchCatLength = new String[metaSize];
                                metaSearchCatListArray.add(last);
                            }
	                    		                    	
	                        if(j > 3){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	   
	                            //SapGenConstants.showLog("table name for congig :"+ docTypeArray[0]);
	                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
		                        	resArray = new String[metaUIConfLength.length]; 
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
	 	                            uiConfList.add(uiConfgArray);
	 	                            alluiConfList.add(uiConfgArray);
	                            } 
	                            else if(docTypeStr.equalsIgnoreCase(docTypeArray[1])){
		                        	resArray = new String[metaSearchCatLength.length]; 
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
	 	                           	searchCatArray = new String[resArray.length];
	 	                           	searchCatArray = Arrays.copyOf(resArray, resArray.length, String[].class);
	 	                           	searchCatList.add(searchCatArray);
	 	                            allSearchCatList.add(searchCatArray);
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
			                                SapGenConstants.showErrorDialog(ProductMainScreenForPhone.this, serverErrorMsgStr);
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
            SapGenConstants.showErrorLog("On updateProductContext : "+sff.toString());
        } 
        finally{        	   
        	SapGenConstants.showLog("uiConfList Size : "+uiConfList.size());   
        	SapGenConstants.showLog("searchCatList Size : "+searchCatList.size());   
        	SapGenConstants.showLog("metaSearchCatListArray Size : "+metaSearchCatListArray.size()); 
        	
    		if(DBConstants.PRODUCT_UICONF_TABLE_NAME != null && DBConstants.PRODUCT_UICONF_TABLE_NAME.length() > 0){
    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
    		}
        	
        	if(uiConfList.size() > 0){    			    						
            	try {            		
					SharedPreferences sharedPreferences = getSharedPreferences(ProductLibConstant.PREFS_NAME_FOR_PRODUCTCAT, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_CONTEXT, true);    
	    			editor.commit();    				
            		
            		Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertUiConfDataIntoDB();
                			}catch(Exception e1){
                				SapGenConstants.showErrorLog("Error in insertUiConfDataIntoDB Thread:"+e1.toString());
                			}
                			productsData_Handler.post(uiContext);
                            stopProgressDialog();
                		}
        	            
        			};
        	        t.start();	
            	}
    			catch(Exception adsf1){
    	            SapGenConstants.showErrorLog("On updateProductContext : "+adsf1.toString());
    	            stopProgressDialog();
    				this.runOnUiThread(new Runnable() {
    	                public void run() {
    	                	initSoapConnection();
    	                }
    	            });
    			}  
        	}else{
        		this.runOnUiThread(new Runnable() {
                    public void run() {
		        		stopProgressDialog();
		        		initSoapConnection();
                    }
                });	
        	}    	
        }
    }//fn updateProductContext    
    
    public void updateServerResponse(SoapObject soap){	
        String[] resArray = null;
        String[] metaProductLength = null;
        String[] metaAtta01Length = null;
        String[] metaAtta01RLength = null;
        String[] docTypeArray = new String[3];
		String[] productArray = null;
		String[] attach01Array = null;
		String[] attach01RArray = null;		
        try{ 
        	if(soap != null){    			
    			if(productList != null)
    				productList.clear();    	
    			
    			if(allProductList != null)
    				allProductList.clear();    

    			if(metaProdListArray != null)
    				metaProdListArray.clear();    	
    			
    			if(metaAtta01Array != null)
    				metaAtta01Array.clear(); 	
    			
    			if(metaAtta01RArray != null)
    				metaAtta01RArray.clear(); 		
    			           	            
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
	                                    		DBConstants.PRODUCT_LIST_DB_TABLE_NAME = docTypeStr;	                                    		
		                                    	//SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		productsListCount = Integer.parseInt(rowCount);
	                                    		productsListType = respType;
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
	                    	
	                    	if(j == 3){
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
                                    	metaAtta01Array.add(res);
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
                                    		docTypeArray[1] = docTypeStr;
                                    		//DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME = docTypeStr;
                                    		//SapGenConstants.showLog("j : "+j);
                                    		//SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
                                        	attach01ListCount = Integer.parseInt(rowCount);
                                        	attach01ListType = respType;    
	                                    }
                                    }
                                    //SapGenConstants.showLog("resC :"+ resC);
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaAtta01Length = new String[metaSize];
                                metaAtta01Array.add(last);
                            }
	                    	
	                    	if(j == 4){
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
                                    	metaAtta01RArray.add(res);
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
                                    		docTypeArray[2] = docTypeStr;
                                    		DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME = docTypeStr;
                                    		//SapGenConstants.showLog("j : "+j);
                                    		SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
                                        	attach01RListCount = Integer.parseInt(rowCount);
                                        	attach01RListType = respType;	                                                                          
	                                    }
                                    }
                                    //SapGenConstants.showLog("resC :"+ resC);
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaAtta01RLength = new String[metaSize];
                                metaAtta01RArray.add(last);
                            }	   
	                    	
	                        if(j > 4){
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
	 	                              
 		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);                             
	 	                            productArray = new String[resArray.length];
	 	                            productArray = Arrays.copyOf(resArray, resArray.length, String[].class);
 	                                productList.add(productArray);
 	                                allProductList.add(productArray);
	                            } else if(docTypeStr.equalsIgnoreCase(docTypeArray[1])){
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
	 	                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);
	 	                              
 		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);                             
	 	                            attach01Array = new String[resArray.length];
	 	                            attach01Array = Arrays.copyOf(resArray, resArray.length, String[].class);
 	                                //stockArray.add(arr);
 	                                attach01List.add(attach01Array);
	                            } else if(docTypeStr.equalsIgnoreCase(docTypeArray[2])){
		                        	resArray = new String[metaAtta01RLength.length]; 
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
	 	                              
 		                            SapGenConstants.showLog("docTypeStr :"+ docTypeStr);                             
	 	                            attach01RArray = new String[resArray.length];
	 	                            attach01RArray = Arrays.copyOf(resArray, resArray.length, String[].class);
 	                                //stockArray.add(arr);
 	                                attach01RList.add(attach01RArray);
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
			                                SapGenConstants.showErrorDialog(ProductMainScreenForPhone.this, serverErrorMsgStr);
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
        	SapGenConstants.showLog("productList Size : "+productList.size()); 
        	SapGenConstants.showLog("attach01List Size : "+attach01List.size()); 
        	SapGenConstants.showLog("attach01RList Size : "+attach01RList.size()); 
    		
        	if(productList.size() > 0){    			    						
            	try {
            		SharedPreferences sharedPreferences = getSharedPreferences(ProductLibConstant.PREFS_NAME_FOR_PRODUCTCAT, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_FOR_EMPLOYEE_GET, true);    
	    			editor.commit();
            		Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertProductDataIntoDB();
                			}catch(Exception e1){
                				SapGenConstants.showErrorLog("Error in deleteInvSelctdData Thread:"+e1.toString());
                			}
                			productsData_Handler.post(productview);
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
		        		//refreshList();
                    }
                });	
        	}        	
        }
    }//fn updateServerResponse
    
    final Runnable uiContext = new Runnable(){
        public void run()
        {
        	try{
        		initSoapConnection();
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    private void setTitleValue() {
    	try {    		
    		if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
    		String title = dbObj.getTitle(DBConstants.DEVICE_TYPE_OVERVIEW_S_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
        		SapGenConstants.showLog("title: "+title);
    		}   		
    		
    		/*String SearchHint = dbObj.getSearchBarHint(UIDBConstants.DEVICE_TYPE_OVERVIEW_S_TAG, UIDBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, UIDBConstants.CNTXT4_ATTR_TAG, UIDBConstants.VALUE_SEARCHBAR_TAG);
    		if(SearchHint != null && SearchHint.length() > 0){
    			searchET.setHint(SearchHint);
        		SapGenConstants.showLog("SearchHint: "+SearchHint);
    		}*/
    		
    		dbObj.closeDBHelper();
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
    
    final Runnable productview = new Runnable(){
        public void run()
        {
        	try{
        		initDBConnection();
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    private void insertUiConfDataIntoDB() {
    	try {
    		if(metauiConfListArray != null && metauiConfListArray.size() > 0){ 
	        	if(DBConstants.PRODUCT_UICONF_TABLE_NAME != null && DBConstants.PRODUCT_UICONF_TABLE_NAME.length() > 0){
	        		DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
	    		}
				if(uiConfList != null && uiConfList.size() > 0){
					String columnLists = "";
					if(metauiConfListArray != null && metauiConfListArray.size() > 0){
						for(int i=0; i<metauiConfListArray.size(); i++){
							if( i == (metauiConfListArray.size() - 1)){
								columnLists += metauiConfListArray.get(i).toString().trim();
							}else{
								columnLists += metauiConfListArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);					
					if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
					dbObjColumns.closeDBHelper();					
					if(metauiConfListArray != null && metauiConfListArray.size() > 0){
						metauiConfListArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								SapGenConstants.showLog("  "+separated[i]);
								metauiConfListArray.add(separated[i].toString().trim());
							}
						}
					}					
					DataBasePersistent.setProductUIConfTableContent(metauiConfListArray);
					DataBasePersistent.setColumnName(metauiConfListArray);					
					this.deleteDatabase(DBConstants.DB_DATABASE_NAME);
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
					if(uiConfListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((uiConfListCount == 0) && (uiConfList.size() == 0)){
							dbObj.clearProductUIConfListTable();
						}
						else if((uiConfListCount > 0) && (uiConfList.size() > 0)){
							dbObj.clearProductUIConfListTable();
							insertProductUIConfDataIntoDB(uiConfList);
						}
					}
					if(uiConfListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((uiConfListCount == 0) && (uiConfList.size() == 0)){
							dbObj.clearProductUIConfListTable();
						}
						else if((uiConfListCount > 0) && (uiConfList.size() == 0)){
						}
						else if((uiConfListCount > 0) && (uiConfList.size() > 0)){
							dbObj.clearProductUIConfListTable();
							insertProductUIConfDataIntoDB(uiConfList);
						}
					}
					if(uiConfListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((uiConfListCount == 0) && (uiConfList.size() == 0)){
							dbObj.clearProductUIConfListTable();
						}
						else if((uiConfListCount > 0) && (uiConfList.size() == 0)){
						}
						else if((uiConfListCount > 0) && (uiConfList.size() > 0)){
							dbObj.clearProductUIConfListTable();
							insertProductUIConfDataIntoDB(uiConfList);
						}
					}					
				}
			}        			
			
    		if(DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME.length() > 0){
    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME;
    		}
			if(metaSearchCatListArray != null && metaSearchCatListArray.size() > 0){
				if(searchCatList != null && searchCatList.size() > 0){
					String columnLists = "";
					if(metaSearchCatListArray != null && metaSearchCatListArray.size() > 0){
						for(int i=0; i<metaSearchCatListArray.size(); i++){
							if( i == (metaSearchCatListArray.size() - 1)){
								columnLists += metaSearchCatListArray.get(i).toString().trim();
							}else{
								columnLists += metaSearchCatListArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME+"  "+columnLists);
					if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME, columnLists);
					dbObjColumns.closeDBHelper();
					
					if(metaSearchCatListArray != null && metaSearchCatListArray.size() > 0){
						metaSearchCatListArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								SapGenConstants.showLog("  "+separated[i]);
								metaSearchCatListArray.add(separated[i].toString().trim());
							}
						}
					}			
					DataBasePersistent.setTableContent(metaSearchCatListArray);
					DataBasePersistent.setColumnName(metaSearchCatListArray);
					if(dbObjCatSel == null)
						dbObjCatSel = new DataBasePersistent(this, DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME);
					dbObjCatSel.dropTable(ProductMainScreenForPhone.this);
					boolean isExits = dbObjCatSel.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObjCatSel.creatTable(ProductMainScreenForPhone.this);
					}	
					if(searchCatListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((searchCatListCount == 0) && (searchCatList.size() == 0)){
							dbObjCatSel.clearListTable();
						}
						else if((searchCatListCount > 0) && (searchCatList.size() > 0)){
							dbObjCatSel.clearListTable();
							insertProductSrcCatDataIntoDB(searchCatList);
						}
					}
					if(searchCatListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((searchCatListCount == 0) && (searchCatList.size() == 0)){
							dbObjCatSel.clearListTable();
						}
						else if((searchCatListCount > 0) && (searchCatList.size() == 0)){
						}
						else if((searchCatListCount > 0) && (searchCatList.size() > 0)){
							dbObjCatSel.clearListTable();
							insertProductSrcCatDataIntoDB(searchCatList);
						}
					}
					if(searchCatListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((searchCatListCount == 0) && (searchCatList.size() == 0)){
							dbObjCatSel.clearListTable();
						}
						else if((searchCatListCount > 0) && (searchCatList.size() == 0)){
						}
						else if((searchCatListCount > 0) && (searchCatList.size() > 0)){
							dbObjCatSel.clearListTable();
							insertProductSrcCatDataIntoDB(searchCatList);
						}
					}					
				}
			}
			dbObjCatSel.closeDBHelper();
			dbObj.closeDBHelper();		
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On insertUiConfDataIntoDB: "+e.toString());
		}
	}//fn insertUiConfDataIntoDB
    
    private void insertProductDataIntoDB() {
    	try {
    		if(metaProdListArray != null && metaProdListArray.size() > 0){
				if(DBConstants.PRODUCT_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_LIST_DB_TABLE_NAME;
	    		}
				if(productList != null && productList.size() > 0){
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
					if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
					dbObjColumns.closeDBHelper();					
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
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
					dbObj.dropTable(ProductMainScreenForPhone.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(ProductMainScreenForPhone.this);
					}	
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							dbObj.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() == 0)){
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							dbObj.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() == 0)){
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							dbObj.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}					
				}
				dbObj.closeDBHelper();
			}        	
			
    		if(metaAtta01RArray != null && metaAtta01RArray.size() > 0){    		
	    		if(DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME;
	    		}
				DataBasePersistent.setTableContent(metaAtta01RArray);
				DataBasePersistent.setColumnName(metaAtta01RArray);
				if(attach01RList != null && metaAtta01RArray.size() > 0){
					if(dbObjAtta01 == null)
						dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
					boolean isExits = dbObjAtta01.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObjAtta01.creatTable(ProductMainScreenForPhone.this);
					}		
					if(attach01RListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((attach01RListCount == 0) && (attach01RList.size() == 0)){
							dbObjAtta01.clearListTable();
						}
						else if((attach01RListCount > 0) && (attach01RList.size() > 0)){
							dbObjAtta01.clearListTable();
							insertProductAtta01RListDataIntoDB(attach01RList);
						}
					}
					if(attach01RListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((attach01RListCount == 0) && (attach01RList.size() == 0)){
							dbObjAtta01.clearListTable();
						}
						else if((attach01RListCount > 0) && (attach01RList.size() == 0)){
						}
						else if((attach01RListCount > 0) && (attach01RList.size() > 0)){
							dbObjAtta01.clearListTable();
							insertProductAtta01RListDataIntoDB(attach01RList);
						}
					}
					if(attach01RListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((attach01RListCount == 0) && (attach01RList.size() == 0)){
							dbObjAtta01.clearListTable();
						}
						else if((attach01RListCount > 0) && (attach01RList.size() == 0)){
						}
						else if((attach01RListCount > 0) && (attach01RList.size() > 0)){
							dbObjAtta01.clearListTable();
							insertProductAtta01RListDataIntoDB(attach01RList);
						}
					}					
				}
			}
			
			dbObj.closeDBHelper();
			dbObjAtta01.closeDBHelper();
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On insertProductDataIntoDB: "+e.toString());
		}
	}// insertProductDataIntoDB
    
    public void insertProductListDataIntoDB(ArrayList productListArray){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
			if(productListArray != null && productListArray.size() > 0){
				dbObj.insertDetails(productListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductListDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductListDataIntoDB
    
    public void insertProductUIConfDataIntoDB(ArrayList uiConfList){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
			if(uiConfList != null && uiConfList.size() > 0){
				dbObj.insertProductUIConfDetails(uiConfList);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductUIConfDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductUIConfDataIntoDB    
    
    public void insertProductSrcCatDataIntoDB(ArrayList searchCatList){		
    	try {
			if(DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME.length() > 0){
    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME;
    		}
			if(dbObjCatSel != null)
				dbObjCatSel = null;
			if(dbObjCatSel == null)
				dbObjCatSel = new DataBasePersistent(this, DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME);			
			if(searchCatList != null && searchCatList.size() > 0){
				dbObjCatSel.insertDetails(searchCatList);
			}
			dbObjCatSel.closeDBHelper();
		} catch (Exception e) {
			dbObjCatSel.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductSrcCatDataIntoDB : "+e.toString());
		}
	}//fn insertProductSrcCatDataIntoDB
    
    public void insertProductAtta01RListDataIntoDB(ArrayList productAtta01RListArray){		
		try {
			if(dbObjAtta01 != null)
				dbObjAtta01 = null;
			if(dbObjAtta01 == null)
				dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
			boolean isExits = dbObjAtta01.checkTable();
			SapGenConstants.showLog(DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME+" isExits "+isExits);
			if(productAtta01RListArray != null && productAtta01RListArray.size() > 0){
				dbObjAtta01.insertDetails(productAtta01RListArray);
			}
			dbObjAtta01.closeDBHelper();
		} catch (Exception e) {
			dbObjAtta01.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductAtta01RListDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductAtta01RListDataIntoDB
    
    private void initDBConnection(){
		try{			
			if(DBConstants.PRODUCT_UICONF_TABLE_NAME != null && DBConstants.PRODUCT_UICONF_TABLE_NAME.length() > 0){
				DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
    		}		
			offset = 0;
			data = offset + ProductLibConstant.page_size;
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
            
			if(dbObjColumns == null)
				dbObjColumns = new ProductColumnDB(ctx);
			String columnLists = "";			
			//product UI column name
			columnLists = dbObjColumns.readColumnNames(DBConstants.DB_TABLE_NAME);
			SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);				
			if(columnLists != null && columnLists.length() > 0){
				SapGenConstants.showLog("columnLists: "+columnLists);				
				if(metauiConfListArray != null && metauiConfListArray.size() > 0){
					metauiConfListArray.clear();
				}				
				String[] separated = columnLists.split(":");
				if(separated != null && separated.length > 0){
					for(int i=0; i<separated.length; i++){
						SapGenConstants.showLog("  "+separated[i]);
						metauiConfListArray.add(separated[i].toString().trim());
					}
				}
				DataBasePersistent.setColumnName(metauiConfListArray);				
			}else{
				SapGenConstants.showLog("No Data Available for product UI!");
			}
			dbObjColumns.closeDBHelper();
			
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
    			tableExits = dbObj.checkTable();
    			if(tableExits)
    				productList = dbObj.readListDataFromDB(this); 
        	
    		if(productList != null)
    			allProductList = (ArrayList)productList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper();  
			
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME);
			if(dbObjCatSel != null)
				dbObjCatSel = null;
			if(dbObjCatSel == null)
				dbObjCatSel = new DataBasePersistent(this, DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME);			
    		tableExits = dbObjCatSel.checkTable();
			if(tableExits)
	        	searchCatList = dbObjCatSel.readListDataFromDB(this);
        	if(searchCatList != null)
    			allSearchCatList = (ArrayList)searchCatList.clone();
        	SapGenConstants.showLog("searchCatList size:"+ searchCatList.size());
            HashMap<String, String> stockMap = null;
            String matIdStrVal = "", id = "", desc = "";
        	if(searchCatList != null && searchCatList.size() > 0 ){
        		_options =  new CharSequence[searchCatList.size()];
				for (int i = 0; i < searchCatList.size(); i++) {
					stockMap = (HashMap<String, String>) searchCatList.get(i);  
					id = (String) stockMap.get(DBConstants.MATKL_COLUMN_NAME);
					desc = (String) stockMap.get(DBConstants.WGBEZ_COLUMN_NAME);
					matIdStrVal = "";
					if(desc != null && desc.length() > 0){
						matIdStrVal += id+" ("+desc+")";
					}else{
						matIdStrVal += id;
					}
					_options[i]= matIdStrVal;
				}
        	}     
        	dbObjCatSel.closeDBHelper();
    		setPageData();
    		initLayout();
    		/*listviewcall();
	        getListView().invalidateViews();*/
		}catch(Exception sggh){
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}
    }//fn initDBConnection
    
    private void refreshList(){
		try{
    		listviewcall();
	        getListView().invalidateViews();
		}catch(Exception sggh){
			SapGenConstants.showErrorLog("On refreshList : "+sggh.toString());
		}
    }//fn refreshList
    
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
    
    private class MyProductListAdapterForPhone extends BaseAdapter {
        private LayoutInflater mInflater;       
        HashMap<String, String> stockMap = null;       
        public ImageLoader imageLoader; 
        		
        public MyProductListAdapterForPhone(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);   
            imageLoader = new ImageLoader(ProductMainScreenForPhone.this.getApplicationContext());
        }
       
        public int getCount() {
        	try {
				if(productListViewArray != null)
					return productListViewArray.size();
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

        public View getView(final int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.

            class ViewHolder {
            	//CheckBox checkbx;
                ImageView imgId;    
                LinearLayout llitembg;
                ImageView arrowimg;
                TableLayout tl;
            }
            final ViewHolder holder;
                       
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.productmainfph_list, null);
                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();  
                holder.tl = (TableLayout) convertView.findViewById(R.id.tbllayout1); 
                //holder.checkbx = (CheckBox) convertView.findViewById(R.id.checkbx);
                holder.imgId = (ImageView) convertView.findViewById(R.id.imgId);   
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);    
                holder.arrowimg = (ImageView) convertView.findViewById(R.id.arrowimg);  
                
                //holder.errstatus = (ImageView) convertView.findViewById(R.id.errstatus);             
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			linparams.topMargin = 5; 
			linparams.bottomMargin = 5;

	        TableRow tr1 = new TableRow(ProductMainScreenForPhone.this.getApplicationContext());
			tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); 
			
	        TableRow tr2 = new TableRow(ProductMainScreenForPhone.this.getApplicationContext());
			tr2.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));   
			
			TextView descTV = new TextView(ProductMainScreenForPhone.this.getApplicationContext());			
			descTV.setTextColor(getResources().getColor(R.color.bluelabel));
			descTV.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			//descTV.setPadding(5,5,5,5); 
			descTV.setPadding(5, 0, 0, 0);
			descTV.setMinWidth(100);
			descTV.setWidth(130);
			
			TextView priceTV = new TextView(ProductMainScreenForPhone.this.getApplicationContext());			
			priceTV.setTextColor(getResources().getColor(R.color.bluelabel));
			priceTV.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			//priceTV.setPadding(5,5,5,5); 
			priceTV.setPadding(5, 0, 0, 0);
			priceTV.setMinWidth(60);
			priceTV.setWidth(90);
			
			holder.tl.removeAllViews();
            String text1Str = "", matIdStr = "", descStr = "", priceStr = "", idnpriceStr = "";
			String imageUrlStr = "";
            try {
            	if(productListViewArray != null){
            		stockMap = (HashMap<String, String>) productListViewArray.get(position);  
            		descStr = (String) stockMap.get(DBConstants.MAKTX_COLUMN_NAME);   
                	matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);      
                    priceStr = (String) stockMap.get(DBConstants.KBETR_COLUMN_NAME);  
                    text1Str = (String) stockMap.get(DBConstants.ZZTEXT1_COLUMN_NAME);
                    
                    String secLine = "";
            		if(valArrayList != null && valArrayList.size() > 0){
                        TextView valTV[] = new TextView[valArrayList.size()];
                        for(int i=0; i<valArrayList.size(); i++){
                        	if(i == 1){
                        		String labs = (String) labelMap.get(valArrayList.get(i).toString().trim()); 
                            	String valStr = (String) stockMap.get(valArrayList.get(i).toString().trim());
                            	if(labs != null){
                            		if(valStr != null && valStr.length() > 0){
                            			secLine = labs + valStr +" ";
                            		}
                            	}else{
                            		if(valStr != null && valStr.length() > 0){
                            			secLine = valStr +" ";
                            		}
                            	}                            	
                        	}else{
                            	tr1 = new TableRow(ProductMainScreenForPhone.this.getApplicationContext());    		            	
                            	valTV[i] = new TextView(ProductMainScreenForPhone.this.getApplicationContext());
                                String labs = (String) labelMap.get(valArrayList.get(i).toString().trim()); 
                            	String valStr = (String) stockMap.get(valArrayList.get(i).toString().trim());
                            	valTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
                            	valTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
                            	valTV[i].setPadding(5, 0, 0, 0);
                            	valTV[i].setMinWidth(100);
                            	//valTV[i].setWidth(130);
                            	if(labs != null){
                            		if(valStr != null && valStr.length() > 0){
                            			if(i == 2){
                            				String valStrDis = secLine + labs + valStr;
                            				if(valStrDis.length() > 25){
                                        		String strSep = valStrDis.substring(0, 25);
                                        		valTV[i].setText(strSep+"...");
                                        	}else{
                                        		valTV[i].setText(valStrDis);
                                        	}
                            			}else{
                            				String valStrDis = labs + valStr;
                            				if(valStrDis.length() > 25){
                                        		String strSep = valStrDis.substring(0, 25);
                                        		valTV[i].setText(strSep+"...");
                                        	}else{
                                        		valTV[i].setText(valStrDis);
                                        	}
                                    		//valTV[i].setText(labs+valStr);
                            			}
                            		}else{
                            			valTV[i].setVisibility(View.GONE);
                            		}
                            	}else{
                            		if(valStr != null && valStr.length() > 0){
                            			if(i == 2){
                            				String valStrDis = secLine + valStr;
                            				if(valStrDis.length() > 25){
                                        		String strSep = valStrDis.substring(0, 25);
                                        		valTV[i].setText(strSep+"...");
                                        	}else{
                                        		valTV[i].setText(valStrDis);
                                        	}
                                    		//valTV[i].setText(secLine + valStr);
                            			}else{
                            				String valStrDis = valStr;
                            				if(valStrDis.length() > 25){
                                        		String strSep = valStrDis.substring(0, 25);
                                        		valTV[i].setText(strSep+"...");
                                        	}else{
                                        		valTV[i].setText(valStrDis);
                                        	}
                            				//valTV[i].setText(valStr);
                            			}
                            		}else{
                            			valTV[i].setVisibility(View.GONE);
                            		}
                            	}                            	
                            	tr1.addView(valTV[i]);
            					tr1.setLayoutParams(linparams);	    					
            					holder.tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                        	}
                        }
                        holder.tl.setVisibility(View.VISIBLE);
            		}
                	
                	imageUrlStr = getImageUrl(matIdStr);
    				if(imageUrlStr != null && imageUrlStr.length() > 0){    					
    					holder.imgId.setTag(imageUrlStr+matIdStr);
    		            imageLoader.DisplayImage(imageUrlStr, ProductMainScreenForPhone.this, holder.imgId);    					
    				}else{
    					imageUrlStr = "";
    					holder.imgId.setImageResource(R.drawable.default_img);
    				} 
	                //SapGenConstants.showLog("imageUrlStr lv:"+imageUrlStr);
	                //SapGenConstants.showLog("matIdStr lv:"+matIdStr);
                }    
            	
				if(position%2 == 0)
					holder.llitembg.setBackgroundResource(R.color.item_even_color);
				else
					holder.llitembg.setBackgroundResource(R.color.item_odd_color);				
						
			} catch (Exception qw) {
				SapGenConstants.showErrorLog("Error in MyProductListAdapterForPhone : "+qw.toString());
			}            
			return convertView;
        }          
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        } 
        
        /*public void filter(String charText) {
            charText = charText.toLowerCase();
            productList.clear();
            if (charText.length() == 0) {
            	if(allProductList != null)
            		productList = (ArrayList)allProductList.clone();
            } else {
                for (Object obj : allProductList) {
                	String strValue = (String)((HashMap<String, String>)obj).get(DataBasePersistent.MAKTX_COLUMN_NAME);
                	String idValue = (String)((HashMap<String, String>)obj).get(DataBasePersistent.MATNR_COLUMN_NAME);
                	//SapGenConstants.showLog("strValue : "+strValue);
                    if (strValue.toLowerCase().contains(charText)) {
                    	productList.add(obj);
                    }else if(idValue.toLowerCase().contains(charText)){
                    	productList.add(obj);
                    }
                }
            }
            offset = 0;
			data = offset + ProductLibConstant.page_size;
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
			setPageData();
			drawSubLayout();
        }*/
        
        public void filter(String charText) {
            try {
    			charText = charText.toLowerCase();
    			productList.clear();
    			if (charText.length() == 0) {
    				if(srcCatFilterArrList.size() <= 0){
    					if(allProductList != null)
    						productList = (ArrayList)allProductList.clone();
    				}else{
    					if(allSrcCatFilterArrList != null)
    						productList = (ArrayList)allSrcCatFilterArrList.clone();
    				}
    			} else {
    				if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
    					for (Object obj : allSrcCatFilterArrList) {
    				    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.MATNR_COLUMN_NAME);
    				        if (strValue.toLowerCase().contains(charText)) {
    				        	//SapGenConstants.showLog("strValue: "+strValue);
    				        	productList.add(obj);
    				        }
    				    }
    				}else{
    					for (Object obj : allProductList) {
    				    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.MATNR_COLUMN_NAME);
    				        if (strValue.toLowerCase().contains(charText)) {
    				        	//SapGenConstants.showLog("strValue: "+strValue);
    				        	productList.add(obj);
    				        }
    				    }
    				}
    			}
    			offset = 0;
    			data = offset + ProductLibConstant.page_size;
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
    			setPageData();
    			drawSubLayout();
    		} catch (Exception sgghrr) {
    			SapGenConstants.showErrorLog("On filter : "+sgghrr.toString());
    		}
        }
    }//End of filter
    
    public String getImageUrl(String id){
    	String url = "";
    	try {
			if(dbObjAtta01 != null)
				dbObjAtta01 = null;
    		if(dbObjAtta01 == null)
    			dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
    		url = dbObjAtta01.getImageUrl(id);
    		dbObjAtta01.closeDBHelper();
    		return url;
        }
        catch (Exception sgghrrf) {
        	SapGenConstants.showErrorLog("On getImageUrl : "+sgghrrf.toString());
        	dbObjAtta01.closeDBHelper();
        	return url;
        }
    }    
    
    protected Dialog onCreateDialog( int id ){		
		try {
			if(_options != null && _options.length > 0){
				_selections =  new boolean[ _options.length ];			
				return 
				new AlertDialog.Builder( this )
			    	.setTitle( getResources().getString(R.string.CATSEL_LBL) )
			    	.setMultiChoiceItems( _options, _selections, new DialogSelectionClickHandler() )
			    	.setPositiveButton( "OK", new DialogButtonClickHandler() )
			    	.create();
			}else{
				SapGenConstants.showLog("No Data Available!");
				return null;
			}
		} catch (Exception eyy) {
			SapGenConstants.showErrorLog("On onCreateDialog : "+eyy.toString());
			return null;
		}		
	}//fn onCreateDialog
	
	public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener
	{
		public void onClick( DialogInterface dialog, int clicked, boolean selected )
		{
			SapGenConstants.showLog("ME "+ _options[ clicked ] + " selected: " + selected);
		}
	}
	

	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked )
		{
			switch( clicked )
			{
				case DialogInterface.BUTTON_POSITIVE:
					printSelectedCategories();
					break;
			}
		}
	}
	
	protected void printSelectedCategories(){
		HashMap<String, String> stockMap = null;
		String matIdStrVal = "";
		if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
			srcCatFilterArrList.clear();
		}
		if(srcCatFilterIdArrList != null && srcCatFilterIdArrList.size() > 0){
			srcCatFilterIdArrList.clear();
		}
		for( int i = 0; i < _options.length; i++ ){
			if(_selections[i] == true){
				SapGenConstants.showLog("ME"+ _options[ i ] + " selected: " + _selections[i]);
				if(searchCatList != null && searchCatList.size() > 0 ){
					stockMap = (HashMap<String, String>) searchCatList.get(i);     
					matIdStrVal = (String) stockMap.get(DBConstants.MATKL_COLUMN_NAME);
					SapGenConstants.showLog("matIdStrVal:"+ matIdStrVal);
					srcCatFilterArrList.add(stockMap);
					srcCatFilterIdArrList.add(matIdStrVal.toString().trim());
	        	}
			}
		}				
		
		if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
			productList.clear();
			selCatTV.setVisibility(View.VISIBLE);
			String selCatVal = "", id = "", desc = "";
        	if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0 ){
				for (int i = 0; i < srcCatFilterArrList.size(); i++) {
					stockMap = (HashMap<String, String>) srcCatFilterArrList.get(i);     
					id = (String) stockMap.get(DBConstants.MATKL_COLUMN_NAME);
					desc = (String) stockMap.get(DBConstants.WGBEZ_COLUMN_NAME);
					if(desc != null && desc.length() > 0){
						if(i != srcCatFilterArrList.size() -1)
							selCatVal += id+" ("+desc+"), ";
						else
							selCatVal += id+" ("+desc+")";
					}else{
						if(i != srcCatFilterArrList.size() -1)
							selCatVal += id+", ";
						else
							selCatVal += id;
					}
				}
        	}        			
        	if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
        		if(srcCatFilterArrList.size() > 1)
            		selCatTV.setText("Selected categories : "+selCatVal);
        		else
            		selCatTV.setText("Selected category : "+selCatVal);
        	}
        	
        	ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
        	if(dbObj == null)
	    		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
	    	productList = dbObj.readSrcCatFiltersListDataFromDB(this, srcCatFilterIdArrList, DBConstants.MATKL_COLUMN_NAME);
			if(productList != null && productList.size() > 0){
				allSrcCatFilterArrList = (ArrayList)productList.clone();
			}
	    	dbObj.closeDBHelper();
		}else{
			selCatTV.setVisibility(View.GONE);
			productList.clear();
			if(allProductList != null)
				productList = (ArrayList)allProductList.clone();
		}
    	setPageData();
    	drawSubLayout();			
	}//fn printSelectedCategories
	
    public void setPageData(){
		try {
			ProductLibConstant.PL_previous_page = 0;
    		ProductLibConstant.PL_current_page = 1;
			if(productList != null && productList.size() > 0){
				ProductLibConstant.PL_total_record = productList.size();	
				if(ProductLibConstant.PL_total_record > ProductLibConstant.page_size ){
					ProductLibConstant.PL_total_page = ProductLibConstant.PL_total_record/ProductLibConstant.page_size;	
					int remain_page = ProductLibConstant.PL_total_record%ProductLibConstant.page_size;
					if(remain_page > 0){
						ProductLibConstant.PL_total_page = ProductLibConstant.PL_total_page + 1;
					}			
				}
				else{
					ProductLibConstant.PL_total_page = 1;
				}				
			}			
			System.out.println("PL_total_page->"+ProductLibConstant.PL_total_page);
		} catch (Exception sgghr) {
			SapGenConstants.showErrorLog("On setPageData : "+sgghr.toString());
		}
    }//fn setPageData
    
    protected void onDestroy() {
    	super.onDestroy();    	    	
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}
    	if(dbObjColumns != null){
    		dbObjColumns.closeDBHelper();
    	}
    }
}