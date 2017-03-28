package com.globalsoft.ProductLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.ProductLib.Database.ProductColumnDB;
import com.globalsoft.ProductLib.Database.ProductDBConstants;
import com.globalsoft.ProductLib.Database.ProductShoppingCartListPersistent;
import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductShoppingCartForTablet extends Activity implements TextWatcher {
	private TextView[] valTV;
	private ImageView[] prodIV;
	private ImageLoader imageLoader;
	private EditText qtyEditText;
	private LinearLayout procusheaderlinear, prodsearchlinear, custnameLinear, totprilinear;
	private CheckBox[] chckBox;
	private ProgressDialog pdialog = null;
	private SoapObject resultSoap = null;
	private String taskErrorMsgStr = "", serverErrorMsgStr = "";
	private String custDetStr, custAddStr = "", customerIdStr = "", totalPriceStr = "", clickedMatId = "", custSearchStr = "", idstr = "", nameStr = "", combStr = "",custName1Str="";
	private int custListCount = 0;	
	private String custListType = "";
    final Handler productsData_Handler = new Handler();
    final Handler custData_Handler = new Handler();
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
    private CustomerListAdapter CustomerListAdapter;	
    private int selctdPos = 0, copyflag = 0, socrtFlag = 0, valFlag = 0;	
	private TextView myTitle, custNameTV, custAddressTV; 
	private TextView[] headLblTV;
	public TextView pageTitle, firstPgTV, lastPgTV, nextPgTV, prevPgTV;
	int offset = 0;
    int data;
	private boolean tableExits = false;
	private EditText soValueET; 
	private String searchStr = "";
	private int dispwidth = 300;
    static final int SOAP_CONN_CUSTOMER = 1;
    
    //private DataBasePersistent dbObj = null;
	private DataBasePersistent dbObjAtta01 = null;
	private DataBasePersistent dbObjCatSel = null;
	private DataBasePersistent dbObj = null;
	private ProductColumnDB dbObjColumns = null;
	private ProductShoppingCartListPersistent dbObjSCart = null;
	
	private HashMap<String, String> labelMap = new HashMap<String, String>();	
	private HashMap<String, String> editTextMap = new HashMap<String, String>();	
	private ArrayList valArrayList = new ArrayList();	
	private ArrayList cartArrList = new ArrayList();	
	private ArrayList cartNQtyArrList = new ArrayList();

    private ArrayList custArrList = new ArrayList();
    private ArrayList custDetArr = new ArrayList();
    
	private ArrayList productList = new ArrayList();
	private ArrayList allProductList = new ArrayList();
	private ArrayList metaProdListArray = new ArrayList();
	private ArrayList metaCustListArray = new ArrayList();
	
    private ArrayList priceSoapList = new ArrayList<String>();    

	private int productsListCount = 0;	
	private String productsListType = "";
	private int mainPriceListCount = 0;	
	private String mainPriceListType = "";
	
	private ArrayList mainPriceList = new ArrayList();		
	private ArrayList metaMainPriceArray = new ArrayList();	
	
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	
	private boolean internetAccess = false;
	private static Context ctx;	
	private TableLayout colValTableLayout = null, colHeadTableLayout = null,
			tottbllayout = null, tottablelayout = null;
	
	private LinearLayout dynllheadblk, headblkcusdtl;
	private ArrayList valHeaderArrayList = new ArrayList();
	private HashMap<String, String> labelHeaderMap = new HashMap<String, String>();
	private int uiValue = 0; //0 = customer search, 1 = customer value, etc  
	private HashMap<String, String> custMap = null;
	private HashMap<String, String> proPriceMap = null;
	private HashMap<String, String> proTotPriceMap = null;
	private ArrayList priceList = new ArrayList();
	private RelativeLayout bodylayout;
	private Intent intent;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.productscarthead); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.MAINSCR_PRODLIST_TITLE1));
		Intent intent = this.getIntent();
		ctx = this.getApplicationContext();
		dispwidth = SapGenConstants.getDisplayWidth(this);	
		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		bodylayout = (RelativeLayout) findViewById(R.id.bodylayout);
		setTitleValue();
		initDBConnection();				
    }//onCreate
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
    		finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void initLayout(){
    	try{
			soValueET = (EditText) findViewById(R.id.soValueET);
			soValueET.setEnabled(false);
			
			prodsearchlinear = (LinearLayout) findViewById(R.id.prodsearchlinear);		
    		prodsearchlinear.setVisibility(View.VISIBLE);	
			procusheaderlinear = (LinearLayout) findViewById(R.id.procusheaderlinear);
			custnameLinear = (LinearLayout) findViewById(R.id.socustnamelinear);
			custnameLinear.setVisibility(View.GONE);	
			
			totprilinear = (LinearLayout) findViewById(R.id.totprilinear);
			totprilinear.setVisibility(View.GONE);	
			
			dynllheadblk = (LinearLayout) findViewById(R.id.headblk);
			dynllheadblk.removeAllViews();
			uiValue = 0;
			displayUI(DBConstants.DEVICE_TYPE_CUSTOMER_SEARCH_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, dynllheadblk);
			
			headblkcusdtl = (LinearLayout) findViewById(R.id.headblkcusdtl);
			headblkcusdtl.removeAllViews();
			
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
			
			SapGenConstants.showLog("customerIdStr  : "+customerIdStr);
			if(customerIdStr != null && customerIdStr.length() > 0){
		    	custnameLinear.setVisibility(View.VISIBLE);
		    	uiValue = 1;
				displayUI(DBConstants.DEVICE_TYPE_SHOPPING_CART_HEADER_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headblkcusdtl); 
				displayTableLayoutUI(DBConstants.DEVICE_TYPE_SHOPPING_CART_W_TOTAL, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
				/*LinearLayout totalLL = new LinearLayout(this);
				displayUI(DBConstants.DEVICE_TYPE_SHOPPING_CART_W_TOTAL, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, totalLL);*/
				if(totalPriceStr != null && totalPriceStr.length() > 0){
					totprilinear.setVisibility(View.VISIBLE);
				}else{
					totprilinear.setVisibility(View.VISIBLE);
				}
	    		prodsearchlinear.setVisibility(View.GONE);
			}else{
		    	if(prodsearchlinear != null)
		    		prodsearchlinear.setVisibility(View.VISIBLE);
		    	custnameLinear.setVisibility(View.GONE);
				totprilinear.setVisibility(View.GONE);
			}
			
    		colHeadTableLayout = (TableLayout)findViewById(R.id.prodheadtbllayout);
			if(colHeadTableLayout != null)
				colHeadTableLayout.removeAllViews();
			if(DBConstants.PRODUCT_UICONF_TABLE_NAME != null && DBConstants.PRODUCT_UICONF_TABLE_NAME.length() > 0){
    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
    		}	 
			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
			valArrayList = DataBasePersistent.readAllPossibleValuesFromDB(this, DBConstants.DEVICE_TYPE_WIDE_SCART_TAG_ITEMS, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			SapGenConstants.showLog("valArrayList : "+valArrayList.size());
			ArrayList labels = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.DEVICE_TYPE_WIDE_SCART_TAG_ITEMS, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			String labVal = "";
			if(valArrayList != null && valArrayList.size() > 0){
				int cols =  labels.size();					
				dispwidth = SapGenConstants.getDisplayWidth(this);
				if(dispwidth < SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					colHeadTableLayout.setColumnStretchable(1, true);
				headLblTV = new TextView[labels.size()];            
				TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.partial_table_row, null);
				//TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
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
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				tr1.setLayoutParams(linparams);	
				
				for(int i=0; i<cols; i++){
					labVal = labels.get(i).toString().trim();
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
					headLblTV[i].setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					if(valueValStr != null && valueValStr.length() > 0){
						if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) || valueValStr.equalsIgnoreCase(DBConstants.IMAGE_TAPPABLE_TAG) 
								|| valueValStr.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG)){
							headLblTV[i].setWidth(60);
    					}else{
    						if(typeValStr != null && typeValStr.length() > 0){
    	    					if(typeValStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
    	    							|| typeValStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
    	    						headLblTV[i].setWidth(70);
    	    					}else{
            						headLblTV[i].setMinWidth(100);
            						headLblTV[i].setWidth(labelWidth);     
    	    					}
    	    				}else{
        						headLblTV[i].setMinWidth(100);
        						headLblTV[i].setWidth(labelWidth);                   
    	    				}    						
    					}
					}else{
						if(typeValStr != null && typeValStr.length() > 0){
							if(typeValStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
	    							|| typeValStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
	    						headLblTV[i].setWidth(70);
	    					}else{
        						headLblTV[i].setMinWidth(100);
        						headLblTV[i].setWidth(labelWidth);     
	    					}
	    				}else{
    						headLblTV[i].setMinWidth(100);
    						headLblTV[i].setWidth(labelWidth);                   
	    				} 
					}
					headLblTV[i].setId(i);
					headLblTV[i].setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							if(!valueValStrCk.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.IMAGE_TAPPABLE_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG)){
								//sortItemsAction(metaValue);
							}
						}	
                    });		
					SapGenConstants.showLog("labValStr:  "+labValStr);
					SapGenConstants.showLog("metaValStr:  "+metaValStr);
					SapGenConstants.showLog("typeValStr:  "+typeValStr);
    				if(typeValStr != null && typeValStr.length() > 0){
    					if(typeValStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeValStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
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
						headLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_LABEL);								
					tr1.addView(headLblTV[i]);		
				}	
				colHeadTableLayout.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				drawSubLayout();
			}
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    private void custSrchBackBtnAction(){
	    try{
        	searchStr = "";
        	customerIdStr = "";
        	initDBConnection();
	    }
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
    }//fn custSrchBackBtnAction
    
    private OnClickListener firstPgTVListener = new OnClickListener(){
		public void onClick(View v){
			if(ProductLibConstant.SC_previous_page > 0 && ProductLibConstant.SC_current_page > 1){
				changeTotalPage();
				ProductLibConstant.SC_previous_page = 0;
	    		ProductLibConstant.SC_current_page = 1;
	    		lastPgTV.setText(""+ProductLibConstant.SC_total_page);
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
			if(ProductLibConstant.SC_total_page > 1 && ProductLibConstant.SC_current_page < ProductLibConstant.SC_total_page){
				changeTotalPage();
				ProductLibConstant.SC_previous_page = ProductLibConstant.SC_total_page - 1;
	    		ProductLibConstant.SC_current_page = ProductLibConstant.SC_total_page;
	    		lastPgTV.setText(""+ProductLibConstant.SC_total_page);
                
                int remains = ProductLibConstant.SC_total_record%ProductLibConstant.page_size;
                if(remains == 0){
                	offset = ProductLibConstant.SC_total_record - ProductLibConstant.page_size;
                	data = ProductLibConstant.SC_total_record;
                }else{
                	offset = (ProductLibConstant.SC_total_record/ProductLibConstant.page_size)*ProductLibConstant.page_size;
                    data = ProductLibConstant.SC_total_record;
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
			if(ProductLibConstant.SC_total_page > 1 && ProductLibConstant.SC_current_page < ProductLibConstant.SC_total_page){
				changeTotalPage();
	    		ProductLibConstant.SC_previous_page = ProductLibConstant.SC_current_page;
	    		ProductLibConstant.SC_current_page = ProductLibConstant.SC_current_page + 1;
	    		lastPgTV.setText(""+ProductLibConstant.SC_total_page);
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
			if(ProductLibConstant.SC_previous_page > 0 && ProductLibConstant.SC_current_page > 1){
				changeTotalPage();
	    		ProductLibConstant.SC_current_page = ProductLibConstant.SC_current_page - 1;
	    		ProductLibConstant.SC_previous_page = ProductLibConstant.SC_previous_page - 1;
	    		lastPgTV.setText(""+ProductLibConstant.SC_total_page);	  
	    		if(data != ProductLibConstant.SC_total_record){
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
			ProductLibConstant.SC_total_record = productList.size();	
			if(ProductLibConstant.SC_total_record > ProductLibConstant.page_size ){
				ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_record/ProductLibConstant.page_size;	
				int remain_page = ProductLibConstant.SC_total_record%ProductLibConstant.page_size;
				if(remain_page > 0){
					ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_page + 1;
				}else{
					ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_page + 1;
				}
			}		
		}			
		SapGenConstants.showLog("SC_total_page->"+ProductLibConstant.SC_total_page);	
	}//fn changeTotalPage
    
    public void setPageData(){
		try {
			ProductLibConstant.SC_previous_page = 0;
    		ProductLibConstant.SC_current_page = 1;
			if(productList != null && productList.size() > 0){
				ProductLibConstant.SC_total_record = productList.size();	
				if(ProductLibConstant.SC_total_record > ProductLibConstant.page_size ){
					ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_record/ProductLibConstant.page_size;	
					int remain_page = ProductLibConstant.SC_total_record%ProductLibConstant.page_size;
					if(remain_page > 0){
						ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_page + 1;
					}else{
						ProductLibConstant.SC_total_page = ProductLibConstant.SC_total_page + 1;
					}
				}
				else{
					ProductLibConstant.SC_total_page = 1;
				}				
			}			
			System.out.println("SC_total_page->"+ProductLibConstant.SC_total_page);
		} catch (Exception sgghr) {
			SapGenConstants.showErrorLog("On setPageData : "+sgghr.toString());
		}
    }//fn setPageData
    
    private void displayTableLayoutUI(final String detSumTag, String cntxt3){
    	try {
    		tottablelayout = (TableLayout)findViewById(R.id.tottbllayout);
    		if(tottablelayout != null)
    			tottablelayout.removeAllViews();
			valHeaderArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelHeaderMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("***************** displayTableLayoutUI ***********");			 
			SapGenConstants.showLog("valHeaderArrayList : "+valHeaderArrayList.size());			 
			if(valHeaderArrayList != null && valHeaderArrayList.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valHeaderArrayList.size());
			    valTV = new TextView[valHeaderArrayList.size()];
			    TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.partial_table_row, null);
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				linparams.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
				tr1.setLayoutParams(linparams);
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
								String typeStr = "";
								if(nameVal.indexOf("::") >= 0){
									String[] separated = nameVal.split("::");
									if(separated != null && separated.length > 0){
										if(separated.length > 2){
											metaLabel  = separated[0];
											metaValue  = separated[1];
											metaTrg = separated[2];
											typeStr = separated[3];
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
		                     	String valTotStr = "";
			         			boolean lab = false;  
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = labelHeaderMap.containsKey(metaLabel);
			         			}			         			
			                 	if(metaValue != null && metaValue.length() > 0){
			                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                 			String valStr = "";
			                 			valStr = (String) custMap.get(metaLabel);    
			                 			if(lab){
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
			                 					valTotStr += labStr;                               					
			                 				}
			                 			} 
			                 			TextView tv = new TextView(this);
			                 			tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 			tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                 			tv.setPadding(5, 0, 0, 0);
			                 			tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                 				tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                 			}
			                 			tv.setLayoutParams(new
			                 					 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                 			if(valStr != null && valStr.length() > 0){   
			                 				valTotStr += " "+valStr;
			                 			}                                      					
			                 			String trgStr = (String) custMap.get(metaTrgActStr.toString().trim());
			                 			SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			                 			SapGenConstants.showLog("trgStr : "+trgStr); 
			                 			if(trgStr != null && trgStr.length() > 0){
			                 				SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			                 				SapGenConstants.showLog("trgStr : "+trgStr);
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                 					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
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
			                 					valTotStr += " "+trgStr;
			                 				}
			                 			}

			                 			TextView tv1 = new TextView(this);
			                 			tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 			tv1.setTextColor(getResources().getColor(R.color.white));		
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) 
			                 						|| typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG) 
			                 						|| typeStr.equalsIgnoreCase(DBConstants.NUMC_DATATYPE_TAG)){
			                 					tv1.setGravity(Gravity.RIGHT);
			                 				}
			                 			}else{
			                 				tv1.setGravity(Gravity.LEFT);	                     
			                 			}
			                 			tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                 			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                 				tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                 			}
			                 			tv1.setLayoutParams(new
			                 					 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                 			if(valTotStr != null && valTotStr.length() > 0){
			                 				tv1.setText(valTotStr);
			                 			}else{
			                 				tv1.setText("");
			                 			}
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			                 						|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			                 					tv1.setHeight(50);
			                 					tv1.setWidth(60);
			                 					SapGenConstants.showLog("width if : "+70);
			                 				}else{
			                 					tv1.setHeight(50);  
			                 					tv1.setWidth(210);
			                 					SapGenConstants.showLog("width if : "+100);
			                 				}
			                 			}else{
			                 				tv1.setHeight(50);   
			                 				tv1.setWidth(210);          
			                 				SapGenConstants.showLog("width else : "+100);
			                 			}  			                     			  
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);
			                 			linearLayout.addView(tv1);
			                 			tr1.addView(linearLayout); 
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.COLUMN_ALIGNEDR_TAG)){
			                 			String valStr = "";
			                 			valStr = (String) custMap.get(metaLabel);    
			                 			if(lab){
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
			                 					valTotStr += labStr;                              					
			                 				}
			                 			} 
			                 			TextView tv = new TextView(this);
			                 			tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 			tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                 			tv.setPadding(5, 0, 0, 0);
			                 			tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                 				tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                 			}
			                 			tv.setLayoutParams(new
			                 					 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                 			if(valStr != null && valStr.length() > 0){   
			                 				valTotStr += " "+valStr;
			                 			}                                      					
			                 			String trgStr = "";
			                 			if(metaTrgActStr.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){												
			                 				if(mainPriceList != null && mainPriceList.size() > 0){
			                 					for (int i1 = 0; i1 < mainPriceList.size(); i1++) {
			                 						proTotPriceMap = (HashMap<String, String>) mainPriceList.get(i1);   
			                 						String tstr = (String) proTotPriceMap.get(DBConstants.NETWR_COLUMN_NAME);
			                 						if(tstr != null && tstr.length() > 0){  
			                 							trgStr = tstr;
			                 						}
			                 					}									    			
			                 				}												
			                 			}
			                 			SapGenConstants.showLog("COLUMN_ALIGNEDR_TAG metaTrgActStr : "+metaTrgActStr); 
			                 			SapGenConstants.showLog("COLUMN_ALIGNEDR_TAG trgStr : "+trgStr); 
			                 			if(trgStr != null && trgStr.length() > 0){
			                 				SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			                 				SapGenConstants.showLog("trgStr : "+trgStr);
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                 					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
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
			                 					valTotStr += " "+trgStr;
			                 				}
			                 			}

			                 			TextView tv1 = new TextView(this);
			                 			tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                 			tv1.setTextColor(getResources().getColor(R.color.white));		
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) 
			                 						|| typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG) 
			                 						|| typeStr.equalsIgnoreCase(DBConstants.NUMC_DATATYPE_TAG)){
			                 					tv1.setGravity(Gravity.RIGHT);
			                 				}
			                 			}else{
			                 				tv1.setGravity(Gravity.LEFT);	                     
			                 			}
			                 			tv1.setGravity(Gravity.RIGHT);
			                 			tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                 			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                 				tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                 			}
			                 			tv1.setLayoutParams(new
			                 					 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                 			if(valTotStr != null && valTotStr.length() > 0){
			                 				tv1.setText(valTotStr);
			                 			}else{
			                 				tv1.setText("");
			                 			}
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			                 						|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			                 					tv1.setWidth(60);
			                 					tv1.setHeight(50);
			                 					SapGenConstants.showLog("width if : "+70);
			                 				}else{
			                 					tv1.setWidth(210);
			                 					tv1.setHeight(50);
			                 					SapGenConstants.showLog("width if : "+100);
			                 				}
			                 			}else{
			                 				tv1.setWidth(210);  
			                 				tv1.setHeight(50);        
			                 				SapGenConstants.showLog("width else : "+100);
			                 			}  			                     			  
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);
			                 			linearLayout.addView(tv1);
			                 			tr1.addView(linearLayout);							
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                 			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			                 						|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			                 					layoutParamsiv.width = 70;
			                 				}else{
			                 					layoutParamsiv.width = 100;   
			                 				}
			                 			}else{
			                 				layoutParamsiv.width = 100;                 
			                 			} 
			                 			EditText et = new EditText(this);
			                 			et.setLayoutParams(layoutParamsiv);
			                 			et.setText("");
			                 			et.setLayoutParams(new LinearLayout.LayoutParams(
			                 					 LinearLayout.LayoutParams.WRAP_CONTENT,
			                 					 LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
			                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                 			if(lab){
			                 				String labStr = (String)labelHeaderMap.get(metaLabel);
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
			                 					//filter(searchStr);
			                 				}
			                 				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			                 				public void onTextChanged(CharSequence s, int start, int before, int count){}
			                 			}); 			                     			  
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);
			                 			linearLayout.addView(et);
			                 			tr1.addView(linearLayout); 
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SEARCH_TAG)){
		                    			/*ImageView iv = new ImageView(this);
			                 			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                 			if(typeStr != null && typeStr.length() > 0){
			    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			    								layoutParamsiv.width = 50;
			    								layoutParamsiv.height = 50;
			    	    					}else{
			    	    						layoutParamsiv.width = 100;  
			    								layoutParamsiv.height = 50;
			    	    					}
			    	    				}else{
			    	    					layoutParamsiv.width = 50;  
		    								layoutParamsiv.height = 50;     
			    	    				}
			                 			iv.setLayoutParams(layoutParamsiv);
			                 			iv.setPadding(5, 0, 0, 0);
			                 			iv.setImageResource(R.drawable.search1);
			                 			iv.setOnClickListener(new View.OnClickListener() {
			                 				public void onClick(View view) {
			                 					showCustomerSearch();
			                 				}	
			                 			});      			  
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);
			                 			linearLayout.addView(iv);
			                 			tr1.addView(linearLayout); */
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_PRICE)){
			                 			ImageView iv = new ImageView(this);
			                 			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                 			if(typeStr != null && typeStr.length() > 0){
			                 				if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			                 						|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			                 					layoutParamsiv.width = 50;
			                 					layoutParamsiv.height = 50;
			                 				}else{
			                 					layoutParamsiv.width = 100;  
			                 					layoutParamsiv.height = 50;
			                 				}
			                 			}else{
			                 				layoutParamsiv.width = 50;  
			                 				layoutParamsiv.height = 50;     
			                 			}
			                 			iv.setLayoutParams(layoutParamsiv);
			                 			iv.setImageResource(R.drawable.dollar);
			                 			iv.setOnClickListener(new View.OnClickListener() {
			                 				public void onClick(View view) {
			                 					getPriceAction();
			                 				}	
			                 			});			                    			
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);						         			
			                 			linearLayout.addView(iv);
			                 			tr1.addView(linearLayout);
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_SEARCH_AGAIN)){
			                 			ImageView iv = new ImageView(this);
			                 			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                 			if(typeStr != null && typeStr.length() > 0){
			    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
			    								layoutParamsiv.width = 50;
			    								layoutParamsiv.height = 50;
			    	    					}else{
			    	    						layoutParamsiv.width = 100;  
			    								layoutParamsiv.height = 50;
			    	    					}
			    	    				}else{
			    	    					layoutParamsiv.width = 50;  
		    								layoutParamsiv.height = 50;     
			    	    				}
			                 			iv.setLayoutParams(layoutParamsiv);
			                 			iv.setPadding(5, 0, 0, 0);
			                 			iv.setImageResource(R.drawable.back);
			                 			iv.setOnClickListener(new View.OnClickListener() {
			                 				public void onClick(View view) {
			                 					custSrchBackBtnAction();
			                 				}	
			                 			});      			  
			                 			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                 			linearLayout.setBackgroundResource(R.drawable.border);
			                 			linearLayout.addView(iv);
			                 			tr1.addView(linearLayout); 			          
			                     	}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
			                     		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			                     		layoutParamsiv.width = 210; 
			                     		layoutParamsiv.height = 50;
			                     		layoutParamsiv.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
			                     		Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     		bt.setLayoutParams(layoutParamsiv);
			                     		bt.setText("");
			                     		bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     		bt.setTextColor(getResources().getColor(R.color.white));
			                     		bt.setBackgroundResource(R.drawable.btn_blue1);
			                     		if(lab){
			                     			String labStr = (String)labelHeaderMap.get(metaLabel);
			                     			if(labStr != null && labStr.length() > 0){
			                     				bt.setText(labStr);
			                     			}
			                     		}
			                     		bt.setOnClickListener(new View.OnClickListener() {
			                     			public void onClick(View view) {
			                     				placeOrderAction();
			                     			}	
			                     		});       			  
			                     		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                     		linearLayout.setBackgroundResource(R.drawable.border);
			                     		linearLayout.addView(bt);
			                     		tr1.addView(linearLayout); 
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG)){
		                     			TextView tv1 = new TextView(this);
                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
                    					tv1.setTextColor(getResources().getColor(R.color.white));	
	                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
                    					}
                    					tv1.setLayoutParams(new
                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		                 				tv1.setText("");
		                 				tv1.setWidth(50);
	    								tv1.setHeight(50);			                 				  			                     			  
		                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
					        			linearLayout.setBackgroundResource(R.drawable.border);
					        			linearLayout.addView(tv1);
		                    			tr1.addView(linearLayout);	
		                     		}                             		
			                 	}            				    
							}else if (list.size() > 1){
			         			LinearLayout llmclm = new LinearLayout(this);
			         			llmclm.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			         			llmclm.setOrientation(LinearLayout.HORIZONTAL);			         			
								for(int l = 0; l < list.size(); l++){
									String name = list.get(l).toString().trim();
									SapGenConstants.showLog("name append else: "+name);                                 	
			                     	String nameVal = list.get(l).toString().trim();
									String metaLabel = "";
									String metaValue = "";
									String metaTrg = "", typeStr = "";
									SapGenConstants.showLog("nameVal displaytable Layout : "+nameVal);										  
									if(nameVal.indexOf("::") >= 0){										
										String[] separated = nameVal.split("::");									
										if(separated != null && separated.length > 0){										
											if(separated.length > 3){
			    								metaLabel  = separated[0];
			    								metaValue  = separated[1];
			    								metaTrg = separated[2];
												typeStr = separated[3];
												SapGenConstants.showLog("metaLabel : "+metaLabel);		
												SapGenConstants.showLog("metaValue : "+metaValue);		
												SapGenConstants.showLog("metaTrg : "+metaTrg);		
											}else if(separated.length > 2){
												//added by sowmya//
												metaLabel  = separated[0];
			    								metaValue  = separated[1];
			    								metaTrg = separated[2];
												//typeStr = separated[3];
												SapGenConstants.showLog("metaLabel : "+metaLabel);		
												SapGenConstants.showLog("metaValue : "+metaValue);		
												SapGenConstants.showLog("metaTrg : "+metaTrg);		
												//added by sowmya//
											}//added by sowmya//
											else{
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
			                     	String valTotStr = "";
			                     	boolean lab = false;  
			             			if(metaLabel != null && metaLabel.length() > 0){
			             				lab = labelHeaderMap.containsKey(metaLabel);
			             			}                         	
			                     	if(metaValue != null && metaValue.length() > 0){
			                     		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                     			String valStr = "";
											valStr = (String) custMap.get(metaLabel);    
			                     			if(lab){
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
				            						valTotStr += labStr;
			                        			    //llmclm.addView(tv);                                 					
			                     				}
			                     			} 
			                             	TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					tv.setPadding(5, 0, 0, 0);
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			            					if(valStr != null && valStr.length() > 0){   
			            						valTotStr += " "+valStr;
			                				}                                      					
			            					String trgStr = (String) custMap.get(metaTrgActStr.toString().trim());
			             					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			             					SapGenConstants.showLog("trgStr : "+trgStr); 
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			            						SapGenConstants.showLog("trgStr : "+trgStr);
			                     				if(trgStr != null && trgStr.length() > 0){
			                     					TextView tv1 = new TextView(this);
			                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                    					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
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
				            						valTotStr += " "+trgStr;
			                     				}
			                 				}

		                 					TextView tv1 = new TextView(this);
	                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
	                    					tv1.setTextColor(getResources().getColor(R.color.white));		
	                    					if(typeStr != null && typeStr.length() > 0){
	                        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) 
	                        							|| typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG) 
	                        							|| typeStr.equalsIgnoreCase(DBConstants.NUMC_DATATYPE_TAG)){
	                        						tv1.setGravity(Gravity.RIGHT);
	                        					}
	                        				}else{
	                        					tv1.setGravity(Gravity.LEFT);	                     
	                        				}
		                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
	                    					}
	                    					tv1.setLayoutParams(new
	                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                 				if(valTotStr != null && valTotStr.length() > 0){
		                    					tv1.setText(valTotStr);
			                 				}else{
		                    					tv1.setText("");
			                 				}
			                 				if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								tv1.setHeight(50);
				    								tv1.setWidth(60);
				            						SapGenConstants.showLog("width if : "+70);
				    	    					}else{
				    								tv1.setHeight(50);  
				    								tv1.setWidth(210);
				            						SapGenConstants.showLog("width if : "+100);
				    	    					}
				    	    				}else{
			    								tv1.setHeight(50);   
			    								tv1.setWidth(210);          
			            						SapGenConstants.showLog("width else : "+100);
				    	    				}  			                     			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(tv1);
			                    			tr1.addView(linearLayout); 
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                     			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								layoutParamsiv.width = 70;
				    	    					}else{
				    	    						layoutParamsiv.width = 100;   
				    	    					}
				    	    				}else{
				    	    					layoutParamsiv.width = 100;                 
				    	    				} 
			                     			EditText et = new EditText(this);
			                     			et.setLayoutParams(layoutParamsiv);
			                     			et.setText("");
				                 			et.setLayoutParams(new LinearLayout.LayoutParams(
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT,
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                     			et.setTextColor(getResources().getColor(R.color.black));
				                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
			                     			et.setBackgroundResource(R.drawable.editext_border);
				                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
				                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                     			if(lab){
			                     				String labStr = (String)labelHeaderMap.get(metaLabel);
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
			                						//filter(searchStr);
				                    	        }
				                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
				                    	    }); 			                     			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(et);
			                    			tr1.addView(linearLayout);   
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_IMAGE_TAG)){
			                     			/*String imageUrlStr = "";
			                    			if(dbObjAtta01 != null)
			                    				dbObjAtta01 = null;
			                    			if(dbObjAtta01 == null)
			                    				dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
			                    			imageUrlStr = dbObjAtta01.getImageUrl(matIdStr);
			                        		dbObjAtta01.closeDBHelper();	    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(160, 160);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                    			if(imageUrlStr != null && imageUrlStr.length() > 0){   			
			                    				iv.setTag(imageUrlStr);
			                    	            imageLoader.DisplayImage(imageUrlStr, iv);    					
			                    			}else{
			                    				imageUrlStr = "";
			                    				iv.setImageResource(R.drawable.default_img);
			                    			}
			            				    llmclm.addView(iv);*/
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_PRICE)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		if(typeStr != null && typeStr.length() > 0 || typeStr.equalsIgnoreCase("")){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								layoutParamsiv.width = 60;
				    								layoutParamsiv.height = 50;
				    	    					}else{
				    	    						layoutParamsiv.width = 140;  
				    								layoutParamsiv.height = 50;
				    	    					}
				    	    				}else{
				    	    					layoutParamsiv.width = 60;  
			    								layoutParamsiv.height = 50;     
				    	    				}
						         			iv.setLayoutParams(layoutParamsiv);
			                        		iv.setImageResource(R.drawable.dollar);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													getPriceAction();
												}	
					                        });			                    			
			                    			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);						         			
						        			linearLayout.addView(iv);
			                    			tr1.addView(linearLayout);
				                 		}else if(metaValue.equalsIgnoreCase(DBConstants.COLUMN_ALIGNEDR_TAG)){
				                 			String valStr = "";
											valStr = (String) custMap.get(metaLabel);    
			                     			if(lab){
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
				            						valTotStr += labStr;                              					
			                     				}
			                     			} 
			                             	TextView tv = new TextView(this);
			            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			            					tv.setPadding(5, 0, 0, 0);
			            					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			            					}
			            					tv.setLayoutParams(new
			            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			            					if(valStr != null && valStr.length() > 0){   
			            						valTotStr += " "+valStr;
			                				}                                      					
			            					String trgStr = "";
			            					if(metaTrgActStr.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){												
												if(mainPriceList != null && mainPriceList.size() > 0){
													for (int i1 = 0; i1 < mainPriceList.size(); i1++) {
														proTotPriceMap = (HashMap<String, String>) mainPriceList.get(i1);   
														String tstr = (String) proTotPriceMap.get(DBConstants.NETWR_COLUMN_NAME);
														if(tstr != null && tstr.length() > 0){  
															trgStr = tstr;
														}
										    		}									    			
												}												
											}
				         					SapGenConstants.showLog("COLUMN_ALIGNEDR_TAG metaTrgActStr : "+metaTrgActStr); 
				         					SapGenConstants.showLog("COLUMN_ALIGNEDR_TAG trgStr : "+trgStr); 
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
			            						SapGenConstants.showLog("trgStr : "+trgStr);
			                     				if(trgStr != null && trgStr.length() > 0 || trgStr.equalsIgnoreCase(null)){
			                     					TextView tv1 = new TextView(this);
			                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                    					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                    					//tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                    					tv1.setGravity(Gravity.RIGHT);
			                    					tv1.setPadding(5, 0, 0, 0);
			                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                    					}
			                    					tv1.setLayoutParams(new
			                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                    					if(metaTrg.indexOf("(") >= 0){
			                    						trgStr = "("+trgStr+")";
			                    					}
				            						valTotStr += " "+trgStr;
			                     				}
			                 				}

		                 					TextView tv1 = new TextView(this);
	                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
	                    					tv1.setTextColor(getResources().getColor(R.color.white));		
	                    					if(typeStr != null && typeStr.length() > 0 || typeStr.equalsIgnoreCase("")){
	                        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) 
	                        							|| typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG) 
	                        							|| typeStr.equalsIgnoreCase(DBConstants.NUMC_DATATYPE_TAG)){
	                        						tv1.setGravity(Gravity.RIGHT);
	                        					}
	                        				}else{
	                        					tv1.setGravity(Gravity.LEFT);	 //EDITED BY SOWMYA	                        					
	                        				}
		                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
	                    					}
	                    					tv1.setLayoutParams(new
	                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                 				if(valTotStr != null && valTotStr.length() > 0){
		                    					tv1.setText(valTotStr);
			                 				}else{
		                    					tv1.setText("");
			                 				}
			                 				if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								tv1.setWidth(60);
				    								tv1.setHeight(50);
				            						SapGenConstants.showLog("width if : "+70);
				    	    					}else{
				    								tv1.setWidth(210);
				    								tv1.setHeight(50);
				            						SapGenConstants.showLog("width if : "+100);
				    	    					}
				    	    				}else{
			    								tv1.setWidth(210);  
			    								tv1.setHeight(50);        
			            						SapGenConstants.showLog("width else : "+100);
				    	    				}  			                     			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(tv1);
			                    			tr1.addView(linearLayout);							
				                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_SEARCH_AGAIN)){
			                     			ImageView iv = new ImageView(this);
			                     			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								layoutParamsiv.width = 70;
				    	    					}else{
				    	    						layoutParamsiv.width = 100;   
				    	    					}
				    	    				}else{
				    	    					layoutParamsiv.width = 100;                 
				    	    				} 
						         			iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.back);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													custSrchBackBtnAction();
												}	
					                        });      			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(iv);
			                    			tr1.addView(linearLayout); 			                    			
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
			                     			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			                        		layoutParamsiv.width = 210; 
		    								layoutParamsiv.height = 50;
			                        		layoutParamsiv.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
			                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     			bt.setLayoutParams(layoutParamsiv);
			                     			bt.setText("");
			            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			bt.setTextColor(getResources().getColor(R.color.white));
			                     			bt.setBackgroundResource(R.drawable.btn_blue1);
			                     			if(lab){
			                     				String labStr = (String)labelHeaderMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					bt.setText(labStr);
			                     				}
			                     			}
			                     			bt.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													placeOrderAction();
												}	
					                        });       			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(bt);
			                    			tr1.addView(linearLayout); 
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG)){
			                     			TextView tv1 = new TextView(this);
	                    					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
	                    					tv1.setTextColor(getResources().getColor(R.color.white));	
		                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	                    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	                        					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
	                    					}
	                    					tv1.setLayoutParams(new
	                    							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			                 				tv1.setText("");
			                 				tv1.setWidth(50);
		    								tv1.setHeight(50);			                 				  			                     			  
			                     			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
						        			linearLayout.setBackgroundResource(R.drawable.border);
						        			linearLayout.addView(tv1);
			                    			tr1.addView(linearLayout);	
			                     		}
			                     	}  
								}				                 	
							}
						}    					
					 }
			     }
				SapGenConstants.showLog("***************** displayTableLayoutUI ***********");			
			    tottablelayout.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			}
		} catch (Exception eff) {
			SapGenConstants.showErrorLog("Error in displayTableLayoutUI : "+eff.toString());
		}
    }//fn displayTableLayoutUI
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
    		DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
			valHeaderArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelHeaderMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("valHeaderArrayList : "+valHeaderArrayList.size());			 
			if(valHeaderArrayList != null && valHeaderArrayList.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valHeaderArrayList.size());
			    valTV = new TextView[valHeaderArrayList.size()];
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
			         			boolean lab = false;  
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = labelHeaderMap.containsKey(metaLabel);
			         			}
			         			
			                 	if(metaValue != null && metaValue.length() > 0){
			                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
			                 			LinearLayout llmclm = new LinearLayout(this);
			                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
			                        	String valTotStr = "";
			                 			if(lab){
			                 				String labStr = (String)labelHeaderMap.get(metaLabel);
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
			                 			String valStr = "";
										if(uiValue == 1){
											valStr = (String) custMap.get(metaLabel);    
											if(valStr != null && valStr.length() > 0){
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
	
				        					String trgStr = (String) custMap.get(metaTrgActStr.toString().trim());
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
				             				}                    					
				        					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				         				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
				          				    dynll.addView(llmclm, layoutParams);
										}
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.COLUMN_ALIGNEDR_TAG)){
			                 			LinearLayout llmclm = new LinearLayout(this);
			                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
			                        	String valTotStr = "";
			                 			if(lab){
			                 				String labStr = (String)labelHeaderMap.get(metaLabel);	
											SapGenConstants.showLog("metaTrg : "+metaTrg); 
			                 				if(labStr != null && labStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                 					tv1 = (TextView) getLayoutInflater().inflate(R.layout.partial_textview, null);
			                					//tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                					tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.RIGHT);
			                					tv1.setPadding(5, 0, 5, 0);
			                					tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                					//tv1.setPadding(5, 0, 0, 0);
			                					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                    					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                					}
			                					tv1.setLayoutParams(new
			                							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
			                					tv1.setText(labStr);    				
			                					llmclm.addView(tv1); 
			                 				}
			                 			}                    					
			                 			String valStr = "";
										if(uiValue == 1){
											valStr = (String) custMap.get(metaLabel);   
											/*if(metaTrg.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){
												if(proPriceMap != null && proPriceMap.size() > 0){
													valStr = (String) proPriceMap.get(matIdStr);   
												}												
											}*/
											if(valStr != null && valStr.length() > 0){
				        						TextView tv = new TextView(this);
				            					tv = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
				            					tv.setTextColor(getResources().getColor(R.color.bluelabel));	
			                					tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.RIGHT);
			                					tv.setPadding(5, 0, 5, 0);
				            					//tv.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
				            					//tv.setPadding(5, 0, 0, 0);
				            					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				                					tv.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
				            					}
				            					tv.setLayoutParams(new
				            							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
				        						valTotStr += valStr;
				                         		tv.setText(valTotStr);    			
				                         		llmclm.addView(tv);
				                     		}    
				        					String trgStr = "";
											if(metaTrg.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){												
												if(mainPriceList != null && mainPriceList.size() > 0){
													for (int i1 = 0; i1 < mainPriceList.size(); i1++) {
														proTotPriceMap = (HashMap<String, String>) mainPriceList.get(i1);   
														String tstr = (String) proTotPriceMap.get(DBConstants.NETWR_COLUMN_NAME);
														if(tstr != null && tstr.length() > 0){  
															trgStr = tstr;
														}
										    		}									    			
												}												
											}
				         					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
				         					SapGenConstants.showLog("trgStr : "+trgStr); 
			                 				if(trgStr != null && trgStr.length() > 0){
			                 					TextView tv1 = new TextView(this);
			                					tv1 = (TextView) getLayoutInflater().inflate(R.layout.template_tv_normal, null);
			                					tv1.setTextColor(getResources().getColor(R.color.bluelabel));	
			                					//tv1.setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
			                					tv1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.RIGHT);
			                					tv1.setPadding(5, 0, 5, 0);
			                					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                    					tv1.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			                					}
			                					tv1.setLayoutParams(new
			                							 LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                					if(metaTrg.indexOf("(") >= 0){
			                						trgStr = "("+trgStr+")";
			                					}
			                					tv1.setText(trgStr);		
			                					llmclm.addView(tv1); 
				             				}                    					
				        					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				         				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
				          				    dynll.addView(llmclm, layoutParams);
											//dynll.setVisibility(View.GONE);
											//totalValUI(metaTrgActStr, trgStr);
											//break;		
										}								
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                 			EditText et = new EditText(this);
			                 			et.setText("");
			                 			//et.setPadding(5,5,5,5);
			                 			//et.setWidth(58);
			                 			et.setLayoutParams(new LinearLayout.LayoutParams(
			                 				     LinearLayout.LayoutParams.WRAP_CONTENT,
			                 				     LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
			                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
			                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                 			if(lab){
			                 				String labStr = (String)labelHeaderMap.get(metaLabel);
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
			            				 		//filter(searchStr);
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
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_IMAGE_TAG)){
			                 			/*String imageUrlStr = "";
			                			if(dbObjAtta01 != null)
			                				dbObjAtta01 = null;
			                			if(dbObjAtta01 == null)
			                				dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
			                			imageUrlStr = dbObjAtta01.getImageUrl(matIdStr);
			                    		dbObjAtta01.closeDBHelper();	    		
			                    		ImageView iv = new ImageView(this);
			                    		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(160, 160);
			                    		iv.setLayoutParams(layoutParamsiv);
			                    		iv.setPadding(5, 0, 0, 0);
			                			if(imageUrlStr != null && imageUrlStr.length() > 0){   			
			                				iv.setTag(imageUrlStr);
			                	            imageLoader.DisplayImage(imageUrlStr, iv);    					
			                			}else{
			                				imageUrlStr = "";
			                				iv.setImageResource(R.drawable.default_img);
			                			}
			                 			LinearLayout ll = new LinearLayout(this);
			        				    ll.setOrientation(LinearLayout.VERTICAL);
			        				    ll.addView(iv);
			        				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			       				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
			        				    dynll.addView(ll, layoutParams);*/
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SEARCH_TAG)){
		                     			ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.search1);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
									        	showCustomerSearch();
											}	
				                        });
		                     			dynll.addView(iv);
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_PRICE)){
		                     			ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.dollar);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												getPriceAction();
											}	
				                        });
		                     			dynll.addView(iv);
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_SEARCH_AGAIN)){
		                     			ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.back);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												custSrchBackBtnAction();
											}	
				                        });
		                     			dynll.addView(iv);
			                     	}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
		                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
		                     			bt.setText("");
		                     			bt.setPadding(5,5,5,5);
		            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		                     			bt.setTextColor(getResources().getColor(R.color.white));
		                     			bt.setBackgroundResource(R.drawable.btn_blue1);
		                     			if(lab){
		                     				String labStr = (String)labelHeaderMap.get(metaLabel);
		                     				if(labStr != null && labStr.length() > 0){
		                     					bt.setText(labStr);
		                     				}
		                     			}
		                     			bt.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												placeOrderAction();
											}	
				                        });
		                     			dynll.addView(bt);
		                     		/*}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG)){			                    			    		
		                        		ImageView iv = new ImageView(this);
		                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		                        		iv.setLayoutParams(layoutParamsiv);
		                        		iv.setPadding(5, 0, 0, 0);
		                        		iv.setImageResource(R.drawable.shoping_cart);
		                    			iv.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												showSCartScreen();
											}	
				                        });
		                    			dynll.addView(iv);*/
		                     		}                             		
			                 	}            				    
							}else if (list.size() > 1){
			         			LinearLayout llmclm = new LinearLayout(this);
			         			llmclm.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
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
			             				lab = labelHeaderMap.containsKey(metaLabel);
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
			                     			String valStr = "";
											if(uiValue == 1){
												valStr = (String) custMap.get(metaLabel);    
				                     			if(lab){
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
				            					String trgStr = (String) custMap.get(metaTrgActStr.toString().trim());
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
				                 				}
											}
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.SEARCHBAR_TAG)){
			                     			EditText et = new EditText(this);
			                     			et.setText("");
			                     			//et.setPadding(5,3,5,3);
			                     			//et.setWidth(58);
				                 			et.setLayoutParams(new LinearLayout.LayoutParams(
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT,
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                     			et.setTextColor(getResources().getColor(R.color.black));
				                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
			                     			et.setBackgroundResource(R.drawable.editext_border);
				                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
				                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                     			if(lab){
			                     				String labStr = (String)labelHeaderMap.get(metaLabel);
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
			                						//filter(searchStr);
				                    	        }
				                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
				                    	    }); 
			            				    llmclm.addView(et);                        				    
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_IMAGE_TAG)){
			                     			/*String imageUrlStr = "";
			                    			if(dbObjAtta01 != null)
			                    				dbObjAtta01 = null;
			                    			if(dbObjAtta01 == null)
			                    				dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
			                    			imageUrlStr = dbObjAtta01.getImageUrl(matIdStr);
			                        		dbObjAtta01.closeDBHelper();	    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(160, 160);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                    			if(imageUrlStr != null && imageUrlStr.length() > 0){   			
			                    				iv.setTag(imageUrlStr);
			                    	            imageLoader.DisplayImage(imageUrlStr, iv);    					
			                    			}else{
			                    				imageUrlStr = "";
			                    				iv.setImageResource(R.drawable.default_img);
			                    			}
			            				    llmclm.addView(iv);*/
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_PRICE)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.dollar);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													getPriceAction();
												}	
					                        });
			                    			llmclm.addView(iv);
				                 		}else if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_ACTION_FIELD_ICON_ICON_SEARCH_AGAIN)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.back);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													custSrchBackBtnAction();
												}	
					                        });
			                    			llmclm.addView(iv);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SEARCH_TAG)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.search1);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
										        	showCustomerSearch();
												}	
					                        });
			            				    llmclm.addView(iv);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_LABEL_TAG)){
			                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     			bt.setText("");
			                     			bt.setPadding(5,5,5,5);
			            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			bt.setTextColor(getResources().getColor(R.color.white));
			                     			bt.setBackgroundResource(R.drawable.btn_blue1);
			                     			if(lab){
			                     				String labStr = (String)labelHeaderMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					bt.setText(labStr);
			                     				}
			                     			}
			                     			bt.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													placeOrderAction();
												}	
					                        });
			            				    llmclm.addView(bt);
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG)){			                    			    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.shoping_cart);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													//showSCartScreen();
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
    
    private void getPriceAction(){
    	try{
	    	if(customerIdStr != null && customerIdStr.length() > 0){
	    		initPriceValueSoapConnection();
			}else{
				SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this,  getResources().getString(R.string.CUST_NOT_AVAILABLE));
			}
	    } catch (Exception eff) {
			SapGenConstants.showErrorLog("Error in getPriceAction : "+eff.toString());
		}    	
    }//fn getPriceAction
    
    private void placeOrderAction(){
    	try{
	    	if(customerIdStr != null && customerIdStr.length() > 0){
	    		initPOSoapConnection();
			}else{
				SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this,  getResources().getString(R.string.CUST_NOT_AVAILABLE));
			}
	    } catch (Exception eff) {
			SapGenConstants.showErrorLog("Error in placeOrderAction : "+eff.toString());
		}    	
    }//fn placeOrderAction
        
    public void showCustomerSearch(){
    	headerBlkEnable_Disable();
		customerSearchAction();
    }//showCustomerSearch
    
    private void headerBlkEnable_Disable(){
    	prodsearchlinear.setVisibility(View.VISIBLE);
    	custnameLinear.setVisibility(View.GONE);
		totprilinear.setVisibility(View.GONE);
    }//fn 
    
    public void customerSearchAction(){
    	if(searchStr != null && searchStr.length() > 0){
    		try{
				getCustomerSearch(searchStr.toString().trim());
			}
			catch(Exception wsfsg){
				SapGenConstants.showErrorLog("Error in Customer Search : "+wsfsg.toString());
			}
    	}else{
    		SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this, getResources().getString(R.string.CUSTOMER_SER_EMP_LBL));
    	}
    }//showCustomerSearch
        
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
            
            if(priceSoapList != null)
            	priceSoapList.clear();
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]SALES-ORDER-PRICE[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME";
        
            Vector listVect = new Vector();
            for(int k = 0; k < C0.length; k++){
                listVect.addElement(C0[k]);
            }

	        HashMap<String, String> qtyMap = null; 
            if(productList != null){
	            int itemSize = productList.size();
	            if(itemSize > 0){
	            	int itemNo = 0, itemQty = 0;
	            	String qtyStr = "", MaterialNo = "";
	            	for(int k = 0; k < itemSize; k++){
						qtyMap = (HashMap<String, String>) productList.get(k);     
	            		String matIdStr = (String) qtyMap.get(DBConstants.MATNR_COLUMN_NAME);
	            		if(matIdStr != null && matIdStr.length() > 0){
	            			priceSoapList.add(matIdStr);	
	            		}
	            	}	            	
	            	int psize = priceSoapList.size();
	            	SapGenConstants.showLog("psize : "+psize);
	            	C1 = new SapGenIpConstraints[psize];	            	
	            	for(int j = 0; j < psize; j++){
        				MaterialNo = priceSoapList.get(j).toString().trim();	
						if(MaterialNo != null && MaterialNo.length() > 0){
	        				SapGenConstants.showLog("MaterialNo : "+MaterialNo);
							if(editTextMap != null && editTextMap.size() > 0){
		            			qtyStr = (String)editTextMap.get(MaterialNo);            				
	            				itemQty = Integer.parseInt(qtyStr);
								SapGenConstants.showLog("Qty : "+itemQty);
							}		
		            		if(itemQty < 0)
		            			itemQty = 0;	            		
		            		SapGenConstants.showLog("itemQty : "+itemQty);	            		
		            		if(priceSoapList != null){
		            			C1[j] = new SapGenIpConstraints(); 
			            		C1[j].Cdata = "ZGSEVAST_SDCRTN20[.]"+customerIdStr+"[.][.][.]"+MaterialNo+"[.]"+itemQty+"[.][.]";
			            		listVect.addElement(C1[j]);
		            		}
		            	}
	            	}
	            }
            }
            	
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog(request.toString());          
            respType = SapGenConstants.PRODUCT_CAT_GET_PRICEVALUE;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initPriceValueSoapConnection : "+asd.toString());
        }
    }//fn initPriceValueSoapConnection
    
    private void initPOSoapConnection(){        
    	SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);            

            SapGenIpConstraints C0[], C1[];
            C0 = new SapGenIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SapGenIpConstraints(); 
            }
            
            if(priceSoapList != null)
            	priceSoapList.clear();
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]SALES-ORDER-CREATE[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME[.]BSTKD[.]BSTDK";
        
            Vector listVect = new Vector();
            for(int k = 0; k < C0.length; k++){
                listVect.addElement(C0[k]);
            }

	        HashMap<String, String> qtyMap = null; 
            if(productList != null){
	            int itemSize = productList.size();
	            if(itemSize > 0){
	            	int itemNo = 0, itemQty = 0;
	            	String qtyStr = "", MaterialNo = "";
	            	for(int k = 0; k < itemSize; k++){
						qtyMap = (HashMap<String, String>) productList.get(k);     
	            		String matIdStr = (String) qtyMap.get(DBConstants.MATNR_COLUMN_NAME);
	            		if(matIdStr != null && matIdStr.length() > 0){
	            			priceSoapList.add(matIdStr);	
	            		}
	            	}	            	
	            	int psize = priceSoapList.size();
	            	SapGenConstants.showLog("psize : "+psize);
	            	C1 = new SapGenIpConstraints[psize];	            	
	            	for(int j = 0; j < psize; j++){
        				MaterialNo = priceSoapList.get(j).toString().trim();	
						if(MaterialNo != null && MaterialNo.length() > 0){
	        				SapGenConstants.showLog("MaterialNo : "+MaterialNo);
							if(editTextMap != null && editTextMap.size() > 0){
		            			qtyStr = (String)editTextMap.get(MaterialNo);            				
	            				itemQty = Integer.parseInt(qtyStr);
								SapGenConstants.showLog("Qty : "+itemQty);
							}		
		            		if(itemQty < 0)
		            			itemQty = 0;	            		
		            		SapGenConstants.showLog("itemQty : "+itemQty);	            		
		            		if(priceSoapList != null){
		            			C1[j] = new SapGenIpConstraints(); 
			            		C1[j].Cdata = "ZGSEVAST_SDCRTN20[.]"+customerIdStr+"[.][.][.]"+MaterialNo+"[.]"+itemQty+"[.][.][.]";
			            		listVect.addElement(C1[j]);
		            		}
		            	}
	            	}
	            }
            }
            	
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog(request.toString());          
            respType = SapGenConstants.PRODUCT_CAT_PLACE_ORDER;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initPOSoapConnection : "+asd.toString());
        }
    }//fn initPOSoapConnection
    
    private void getCustomerSearch(String custIdStr){
    	try{
    		custSearchStr = custIdStr;
    		SapGenConstants.showLog("customer : "+custSearchStr);
			if(!custSearchStr.equalsIgnoreCase(""))
				initCustomerSoapConnection();
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in getCustomerSearch : "+sfg.toString());
    	}
    }//fn getCustomerSearch
    
    private void drawSubLayout(){
		try{
			colValTableLayout = (TableLayout)findViewById(R.id.prodvaluetbllayout);
			if(colValTableLayout != null)
				colValTableLayout.removeAllViews();

			String pageTitleValue = ""+ProductLibConstant.SC_current_page;
	        pageTitle.setText(pageTitleValue);

	        prevPgTV.setText("<<");
	        nextPgTV.setText(">>");
	        
	        firstPgTV.setText("1");
	        lastPgTV.setText(""+ProductLibConstant.SC_total_page);
	        
	        if(ProductLibConstant.SC_previous_page > 0 && ProductLibConstant.SC_current_page > 1 ){
	        	prevPgTV.setTextColor(Color.BLUE);
	        	firstPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	prevPgTV.setTextColor(Color.BLACK);
	        	firstPgTV.setTextColor(Color.BLACK);
	        }
	        
	        if(ProductLibConstant.SC_total_page > 1 && ProductLibConstant.SC_current_page < ProductLibConstant.SC_total_page ){
	        	nextPgTV.setTextColor(Color.BLUE);
	        	lastPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	nextPgTV.setTextColor(Color.BLACK);
	        	lastPgTV.setTextColor(Color.BLACK);
	        }
	        
	        HashMap<String, String> stockMap = null; 
	        HashMap<String, String> matMap = null; 			 
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));			
			
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
			
			if(valArrayList != null && valArrayList.size() > 0){
				if(productList != null){
					String valStr = "", imageUrlStr = "", matIdStr = "", qty = "",productStr=" ";                
	                int rowSize = productList.size();                
	                SapGenConstants.showLog("Product List Size  : "+rowSize);  
	                SapGenConstants.showLog("cart n qty List Size  : "+cartNQtyArrList.size());	                
					if(offset <= productList.size() && data <= productList.size() ){
						for (int i = offset; i < data; i++) {
							tr = new TableRow(this);        
							stockMap = (HashMap<String, String>) productList.get(i);     
	                		matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
	                		productStr = (String) stockMap.get(DBConstants.MAKTX_COLUMN_NAME);
	                		 SapGenConstants.showLog("Product description  : "+productStr);  
	                		if(cartNQtyArrList != null && cartNQtyArrList.size() > 0){
	                			for(int ie = 0; ie < cartNQtyArrList.size(); ie++) {
	                        		matMap = (HashMap<String, String>) cartNQtyArrList.get(ie);     
	                        		qty = (String) matMap.get(matIdStr);
	                        		if(qty != null){
	                        			break;
	                        		}  				
	                			}
	                    		SapGenConstants.showLog("matIdStr  : "+matIdStr); 
	                    		SapGenConstants.showLog("qty  : "+qty); 
	                		}	
		                    if(stockMap != null){
		                    	String tag1 = "", tag2 = "", nameVal = "", typeStr = "";
		                    	for (int i1 =0; i1 < valArrayList.size(); i1++) {
			                    	try {		                    		
			                    		nameVal = valArrayList.get(i1).toString().trim();
			            				String strVal = "";
			            				if(nameVal.indexOf("::") >= 0){
			            					String[] separated = nameVal.split("::");
			            					if(separated != null && separated.length > 0){
			            						strVal = separated[0];
			            						tag1 = separated[0];
			            						tag2 = separated[1];
			            						typeStr = separated[2];
			            					}
			            				}else{
			            					strVal = nameVal;
			            				}		

										SapGenConstants.showLog("nameVal: "+nameVal);
										SapGenConstants.showLog("strVal: "+strVal);
										SapGenConstants.showLog("tag1: "+tag1);
										SapGenConstants.showLog("tag2: "+tag2);
			                    		valStr = (String) stockMap.get(strVal);
			                    		SapGenConstants.showLog("valStr: "+valStr);
									} catch (Exception e) {
										e.printStackTrace();
									} 	
			        				final String pId = matIdStr;
			                    	if(tag2 != null && (tag2.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAPPABLE_TAG) || tag2.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG))){
				                    	TextView valTV = new TextView(this);
				                        valTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
				                        String valStrDis = valStr;
				                        SapGenConstants.showLog("valStrDis: "+valStrDis);
										if(valStrDis != null && valStrDis.length() > 0){
					        				if(valStrDis.length() > 25){
					                    		String strSep = valStrDis.substring(0, 25);
					                    		valTV.setText(strSep+"...");
					                    	}else{
					                    		valTV.setText(valStrDis);
					                    	}				        				
				    						if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								valTV.setWidth(70);
				    	    					}else{
				    	    						valTV.setMinWidth(100);
				    	    						valTV.setWidth(labelWidth);     
				    	    					}
				    	    				}else{
				    	    					valTV.setMinWidth(100);
				    	    					valTV.setWidth(labelWidth);                   
				    	    				} 
					                    	valTV.setId(i1);
					                    	if(typeStr != null && typeStr.length() > 0){
					        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					        						valTV.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					        					}else{
					        						valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	
						        				}
					        				}else{
					        					valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	 
					        				}
					                            					    					
					    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					    						valTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					    					}
					    					tr.addView(valTV);	
										}else{
											if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					    						valTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					    					}
											String val = "";
											if(tag1 != null && tag1.length() > 0){
												if(tag1.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){
													if(proPriceMap != null && proPriceMap.size() > 0){
														val = (String) proPriceMap.get(matIdStr);   
													}												
												}
											}										
											valTV.setText(val);
											if(typeStr != null && typeStr.length() > 0){
												if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
													valTV.setWidth(70);
				    	    					}else{
				    	    						valTV.setMinWidth(100);
				    	    						valTV.setWidth(labelWidth);     
				    	    					}
				    	    				}else{
				    	    					valTV.setMinWidth(100);
				    	    					valTV.setWidth(labelWidth);                   
				    	    				}
					                    	valTV.setId(i1);
					                    	if(typeStr != null && typeStr.length() > 0){
					        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					        						valTV.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					        					}else{
					        						valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);			                      
						        				}
					        				}else{
					        					valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	                     
					        				}
					    					tr.addView(valTV);	
										}
			                    	}else if(tag2 != null && (tag1.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG) || tag2.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG))){
			                    		final EditText etext = new EditText(this);
			                    		if(qty != null && qty.length() > 0){
			                    			etext.setText(qty);
			                    		}else{
			                    			etext.setText("");
			                    		}
			                    		SapGenConstants.showLog("qty: "+qty);
			                    		etext.setMinWidth(60);
			                    		etext.setWidth(60);
			                    		etext.setId(i1);
			                    		etext.setBackgroundResource(R.drawable.editext_border);
		        						etext.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);  
			                    		etext.setPadding(5,0,0,0);	
			                    		etext.addTextChangedListener(new TextWatcher(){
			                    	        public void afterTextChanged(Editable s) {
	                    						clickedMatId = pId;
	                    						String updatedQty = s.toString().trim();
	                    				 		SapGenConstants.showLog("Text : "+s.toString());			
	                    				 		SapGenConstants.showLog("Material Id : "+clickedMatId.toString().trim());
	                    				 		editTextMap.put(clickedMatId, updatedQty);
	                    				 		updateQtyValue(clickedMatId, updatedQty);
			                    	        }
			                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
			                    	    }); 
			                    		if(editTextMap != null && editTextMap.size() > 0){
			                    			String checkId = (String)editTextMap.get(matIdStr);
			                    			if(checkId == null){
				                    			editTextMap.put(matIdStr, qty);
			                    			}
			                    		}else{
			                    			editTextMap.put(matIdStr, qty);
			                    		}
			                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
				                    	if(typeStr != null && typeStr.length() > 0){
				        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
				        						linearLayout.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				        					}else{
				        						linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);			                      
					        				}
				        				}else{
				        					linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	                     
				        				}		                    		
				                    	if(typeStr != null && typeStr.length() > 0){
				                    		if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				                    			linearLayout.setMinimumWidth(70);
			    	    					}else{
					                    		linearLayout.setMinimumWidth(labelWidth);  
			    	    					}
			    	    				}else{
				                    		linearLayout.setMinimumWidth(labelWidth);    
			    	    				}
				                    	
			                    		linearLayout.addView(etext);
										tr.addView(linearLayout);
			                    	}else if(tag2 != null && (tag1.equalsIgnoreCase(DBConstants.ITEM_DELETION_ICONTAG) || tag2.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG))){
			                    		ImageView delIV = new ImageView(this); 
			                    		delIV.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												clickAction(pId);
											}	
				                        });
			                    		delIV.setImageResource(R.drawable.delete);
			                    		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
			                            	linlayparams1.height = 40;
			                            	linlayparams1.width = 40;
			                            	delIV.setLayoutParams(linlayparams1);
			                            }
			                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                    		linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	
			                            linearLayout.addView(delIV);
				    					tr.addView(linearLayout);	
			                    	}else{
				                    	TextView valTV = new TextView(this);
				                        valTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    		valTV.setText("");
				                        valTV.setGravity(Gravity.CENTER);                    		
				                    	if(typeStr != null && typeStr.length() > 0){
				                    		if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				                    			valTV.setMinimumWidth(70);
			    	    					}else{
						                        valTV.setMinWidth(100);
						                    	valTV.setWidth(labelWidth);
			    	    					}
			    	    				}else{
					                        valTV.setMinWidth(100);
					                    	valTV.setWidth(labelWidth);
			    	    				}
				                    	valTV.setId(i1);
				                    	valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				    					tr.addView(valTV);	
			                    	}
		                    	}
		    					if(i%2 == 0)
		    						tr.setBackgroundResource(R.color.item_even_color);
					            else
					            	tr.setBackgroundResource(R.color.item_odd_color);
		    					
		    					colValTableLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                    }
						}
	                }else{
						for (int i = offset; i < productList.size(); i++) {
							tr = new TableRow(this);        
							stockMap = (HashMap<String, String>) productList.get(i);     
	                		matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
	                		productStr = (String) stockMap.get(DBConstants.MAKTX_COLUMN_NAME);
	                		 SapGenConstants.showLog("Product description  : "+productStr);  
	                		if(cartNQtyArrList != null && cartNQtyArrList.size() > 0){
	                			for(int ie = 0; ie < cartNQtyArrList.size(); ie++) {
	                        		matMap = (HashMap<String, String>) cartNQtyArrList.get(ie);     
	                        		qty = (String) matMap.get(matIdStr);
	                        		if(qty != null){
	                        			break;
	                        		}  				
	                			}
	                    		SapGenConstants.showLog("matIdStr  : "+matIdStr); 
	                    		SapGenConstants.showLog("qty  : "+qty); 
	                		}	
		                    if(stockMap != null){
		                    	String tag1 = "", tag2 = "", nameVal = "", typeStr = "";
		                    	for (int i1 =0; i1 < valArrayList.size(); i1++) {
			                    	try {		                    		
			                    		nameVal = valArrayList.get(i1).toString().trim();
			            				String strVal = "";
			            				if(nameVal.indexOf("::") >= 0){
			            					String[] separated = nameVal.split("::");
			            					if(separated != null && separated.length > 0){
			            						strVal = separated[0];
			            						tag1 = separated[0];
			            						tag2 = separated[1];
			            						typeStr = separated[2];
			            					}
			            				}else{
			            					strVal = nameVal;
			            				}		

										SapGenConstants.showLog("nameVal: "+nameVal);
										SapGenConstants.showLog("strVal: "+strVal);
										SapGenConstants.showLog("tag1: "+tag1);
										SapGenConstants.showLog("tag2: "+tag2);
			                    		valStr = (String) stockMap.get(strVal);
			                    		SapGenConstants.showLog("valStr: "+valStr);
									} catch (Exception e) {
										e.printStackTrace();
									} 	
			        				final String pId = matIdStr;
			                    	if(tag2 != null && (tag2.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAPPABLE_TAG) || tag2.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG))){
				                    	TextView valTV = new TextView(this);
				                        valTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
				                        String valStrDis = valStr;
				                        SapGenConstants.showLog("valStrDis: "+valStrDis);
										if(valStrDis != null && valStrDis.length() > 0){
					        				if(valStrDis.length() > 25){
					                    		String strSep = valStrDis.substring(0, 25);
					                    		valTV.setText(strSep+"...");
					                    	}else{
					                    		valTV.setText(valStrDis);
					                    	}				        				
				    						if(typeStr != null && typeStr.length() > 0){
				    							if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				    								valTV.setWidth(70);
				    	    					}else{
				    	    						valTV.setMinWidth(100);
				    	    						valTV.setWidth(labelWidth);     
				    	    					}
				    	    				}else{
				    	    					valTV.setMinWidth(100);
				    	    					valTV.setWidth(labelWidth);                   
				    	    				} 
					                    	valTV.setId(i1);
					                    	if(typeStr != null && typeStr.length() > 0){
					        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					        						valTV.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					        					}else{
					        						valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	
						        				}
					        				}else{
					        					valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	 
					        				}
					                            					    					
					    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					    						valTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					    					}
					    					tr.addView(valTV);	
										}else{
											if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					    						valTV.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
					    					}
											String val = "";
											if(tag1 != null && tag1.length() > 0){
												if(tag1.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){
													if(proPriceMap != null && proPriceMap.size() > 0){
														val = (String) proPriceMap.get(matIdStr);   
													}												
												}
											}										
											valTV.setText(val);
											if(typeStr != null && typeStr.length() > 0){
												if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
				    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
													valTV.setWidth(70);
				    	    					}else{
				    	    						valTV.setMinWidth(100);
				    	    						valTV.setWidth(labelWidth);     
				    	    					}
				    	    				}else{
				    	    					valTV.setMinWidth(100);
				    	    					valTV.setWidth(labelWidth);                   
				    	    				}
					                    	valTV.setId(i1);
					                    	if(typeStr != null && typeStr.length() > 0){
					        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					        						valTV.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
					        					}else{
					        						valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);			                      
						        				}
					        				}else{
					        					valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	                     
					        				}
					    					tr.addView(valTV);	
										}
			                    	}else if(tag2 != null && (tag1.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG) || tag2.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG))){
			                    		final EditText etext = new EditText(this);
			                    		if(qty != null && qty.length() > 0){
			                    			etext.setText(qty);
			                    		}else{
			                    			etext.setText("");
			                    		}
			                    		SapGenConstants.showLog("qty: "+qty);
			                    		etext.setMinWidth(60);
			                    		etext.setWidth(60);
			                    		etext.setId(i1);
			                    		etext.setBackgroundResource(R.drawable.editext_border);
		        						etext.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);  
			                    		etext.setPadding(5,0,0,0);	
			                    		etext.addTextChangedListener(new TextWatcher(){
			                    	        public void afterTextChanged(Editable s) {
	                    						clickedMatId = pId;
	                    						String updatedQty = s.toString().trim();
	                    				 		SapGenConstants.showLog("Text : "+s.toString());			
	                    				 		SapGenConstants.showLog("Material Id : "+clickedMatId.toString().trim());
	                    				 		editTextMap.put(clickedMatId, updatedQty);
	                    				 		updateQtyValue(clickedMatId, updatedQty);
			                    	        }
			                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
			                    	    }); 
			                    		if(editTextMap != null && editTextMap.size() > 0){
			                    			String checkId = (String)editTextMap.get(matIdStr);
			                    			if(checkId == null){
				                    			editTextMap.put(matIdStr, qty);
			                    			}
			                    		}else{
			                    			editTextMap.put(matIdStr, qty);
			                    		}
			                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
				                    	if(typeStr != null && typeStr.length() > 0){
				        					if(typeStr.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
				        						linearLayout.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				        					}else{
				        						linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);			                      
					        				}
				        				}else{
				        					linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	                     
				        				}		                    		
				                    	if(typeStr != null && typeStr.length() > 0){
				                    		if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				                    			linearLayout.setMinimumWidth(70);
			    	    					}else{
					                    		linearLayout.setMinimumWidth(labelWidth);  
			    	    					}
			    	    				}else{
				                    		linearLayout.setMinimumWidth(labelWidth);    
			    	    				}
				                    	
			                    		linearLayout.addView(etext);
										tr.addView(linearLayout);
			                    	}else if(tag2 != null && (tag1.equalsIgnoreCase(DBConstants.ITEM_DELETION_ICONTAG) || tag2.equalsIgnoreCase(DBConstants.ICON_DELETE_ITEM_TAG))){
			                    		ImageView delIV = new ImageView(this); 
			                    		delIV.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												clickAction(pId);
											}	
				                        });
			                    		delIV.setImageResource(R.drawable.delete);
			                    		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
			                            	linlayparams1.height = 40;
			                            	linlayparams1.width = 40;
			                            	delIV.setLayoutParams(linlayparams1);
			                            }
			                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
			                    		linearLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);	
			                            linearLayout.addView(delIV);
				    					tr.addView(linearLayout);	
			                    	}else{
				                    	TextView valTV = new TextView(this);
				                        valTV = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                    		valTV.setText("");
				                        valTV.setGravity(Gravity.CENTER);                    		
				                    	if(typeStr != null && typeStr.length() > 0){
				                    		if(typeStr.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)
			    	    							|| typeStr.equalsIgnoreCase(DBConstants.UNIT_DATATYPE_TAG)){
				                    			valTV.setMinimumWidth(70);
			    	    					}else{
						                        valTV.setMinWidth(100);
						                    	valTV.setWidth(labelWidth);
			    	    					}
			    	    				}else{
					                        valTV.setMinWidth(100);
					                    	valTV.setWidth(labelWidth);
			    	    				}
				                    	valTV.setId(i1);
				                    	valTV.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				    					tr.addView(valTV);	
			                    	}
		                    	}
		    					if(i%2 == 0)
		    						tr.setBackgroundResource(R.color.item_even_color);
					            else
					            	tr.setBackgroundResource(R.color.item_odd_color);
		    					
		    					colValTableLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                    }
						}
	                }
					/*for (int i = 0; i < productList.size(); i++) {					
	                   					
					}*/
				}
			}
		}
		catch(Exception asgf){
			SapGenConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout    

   	public void updateQtyValue(String idVal, String qtyVal){
   		try{
			if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductShoppingCartForTablet.this);
        	dbObjSCart.updateQtyValue(idVal, qtyVal);
			dbObjSCart.closeDBHelper();
	    }
	    catch(Exception asf){
	    	SapGenConstants.showErrorLog("Error in updateQtyValue : "+asf.toString());
			dbObjSCart.closeDBHelper();
	    }
   	}//fn updateQtyValue
   	
   	public void clickAction(final String id){
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
   	
    private void deleteAction(String mId){
        try{ 
        	if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductShoppingCartForTablet.this);
        	dbObjSCart.deleteCart(mId);
			dbObjSCart.closeDBHelper();
        	initDBConnection();
        }
        catch(Exception dsgf){
        	SapGenConstants.showErrorLog("Error in deleteAction : "+dsgf.toString());
        }
    }//fn deleteAction
   
    private void setTitleValue() {
    	try {
    		if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
    		String title = dbObj.getTitle(DBConstants.DEVICE_TYPE_WIDE_SCART_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
        		SapGenConstants.showLog("title: "+title);
       		 	if(title.indexOf(SapGenConstants.title_offline) > 0){
       		 		bodylayout.setBackgroundResource(R.drawable.llborder);
       		 		bodylayout.setPadding(5, 5, 5, 5);
       		 	}
    		}   		    		
    		dbObj.closeDBHelper();
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
        
    protected void onDestroy() {
    	super.onDestroy();    	    	
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}
    	if(dbObjAtta01 != null){
    		dbObjAtta01.closeDBHelper();
    	}
    	if(dbObj != null){
    		dbObj.closeDBHelper();
    	}
    	if(dbObjColumns != null){
    		dbObjColumns.closeDBHelper();
    	}
    }//fn onDestroy
    
    private void initDBConnection(){
		try{			
			if(cartNQtyArrList != null && cartNQtyArrList.size() > 0){
				cartNQtyArrList.clear();
			}
			if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductShoppingCartForTablet.this);
        	cartArrList = dbObjSCart.getCartIdList();
        	cartNQtyArrList = dbObjSCart.getCartIdNQtyList();
			dbObjSCart.closeDBHelper();

    		SapGenConstants.showLog("cartArrList: "+cartArrList.size());
    		SapGenConstants.showLog("cartNQtyArrList: "+cartNQtyArrList.size());
			offset = 0;
			data = offset + ProductLibConstant.page_size;
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
    			tableExits = dbObj.checkTable();
    			if(tableExits)
    				productList = dbObj.readDataSelectedIdListDataFromDB(this, cartArrList, DBConstants.MATNR_COLUMN_NAME); 
        	
    		if(productList != null)
    			allProductList = (ArrayList)productList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper();     		
    		setPageData();
    		initLayout();			
		}catch(Exception sggh){
			dbObjSCart.closeDBHelper();
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}
    }//fn initDBConnection
    
    private void initCustomerSoapConnection(){        
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
            C0[2].Cdata = "EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0";
            C0[3].Cdata = "ZGSEVDST_CSTMRSRCH01[.]"+custSearchStr;        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);       
            respType = SapGenConstants.RESP_TYPE_GET_CUST_LIST;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initCustomerSoapConnection : "+asd.toString());
        }
    }//fn initCustomerSoapConnection  
    
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
	    		if(respType == SapGenConstants.RESP_TYPE_GET_CUST_LIST){
	    			if(pdialog != null)
	        			pdialog = null;	        		
	    			if(pdialog == null){
	    				ProductShoppingCartForTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductShoppingCartForTablet.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
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
	    			}
	    		}else if(respType == SapGenConstants.PRODUCT_CAT_GET_PRICEVALUE){
		    		if(pdialog != null)
	        			pdialog = null;	        		
	    			if(pdialog == null){
	    				ProductShoppingCartForTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductShoppingCartForTablet.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                        				updateGettingPriceListServerResponse(resultSoap);                       				
	                        				sleep(2000);
	                        			} catch (Exception e) {  }
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
		    	}else if(respType == SapGenConstants.PRODUCT_CAT_PLACE_ORDER){
		    		if(pdialog != null)
	        			pdialog = null;	        		
	    			if(pdialog == null){
	    				ProductShoppingCartForTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductShoppingCartForTablet.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                        				updatePOServerResponse(resultSoap);                       				
	                        				sleep(2000);
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
    
    public void updateServerResponse(SoapObject soap){	
        String[] resArray = null;
        String[] metaProductLength = null;
        String[] docTypeArray = new String[3];
		String[] CustArray = null;
        try{ 
        	if(soap != null){    			
    			if(custArrList != null)
    				custArrList.clear();    
    			if(metaCustListArray != null)
    				metaCustListArray.clear();  	
    			           	            
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
                                    	metaCustListArray.add(res);
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
	                                    		DBConstants.CUSTOMER_LIST_DB_TABLE_NAME = docTypeStr;
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
                                metaCustListArray.add(last);
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
	 	                           CustArray = new String[resArray.length];
	 	                          CustArray = Arrays.copyOf(resArray, resArray.length, String[].class);
	 	                            custArrList.add(CustArray);
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
			                                SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this, serverErrorMsgStr);
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
                			custData_Handler.post(custview);
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
        		initCustDBConnection(custSearchStr);
    			SapGenConstants.showErrorDialog(this, "Data Not available");
        	}
        }
    }//fn updateServerResponse
    
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
			                                SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this, serverErrorMsgStr);
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
    
    private void updatePOServerResponse(SoapObject soap){	
        try{ 
        	if(soap != null){  
        		String delimeter = "[.]", result="", res="";
                SoapObject pii = null;
                String[] resArray = new String[37];
                int propsCount = 0, errCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0;                   
                for (int i = 0; i < soap.getPropertyCount(); i++) {  
                    pii = (SoapObject)soap.getProperty(i);
                    propsCount = pii.getPropertyCount();
                    SapGenConstants.showLog("propsCount : "+propsCount);
                    if(propsCount > 0){
                        for (int j = 0; j < propsCount; j++) {
                        	SapGenConstants.showLog(j+" : "+pii.getProperty(j).toString());
                            if(j >= 4){
                                result = pii.getProperty(j).toString();
                                firstIndex = result.indexOf(delimeter);
                                firstIndex = firstIndex + 3;
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
                            }
                            else if(j < 4){
                                String errorMsg = pii.getProperty(j).toString();
                                SapGenConstants.showLog("Inside J == "+j+" : "+propsCount+" : "+errorMsg);
                                int errorFstIndex = errorMsg.indexOf("Message=");
                                if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SapGenConstants.showLog(taskErrorMsgStr);
                                	this.runOnUiThread(new Runnable() {
                                        public void run() {
                                        	SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this, taskErrorMsgStr);
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
        	SapGenConstants.showErrorLog("On updatePOServerResponse : "+sff.toString());
        	stopProgressDialog();
        } 
        finally{
        	stopProgressDialog();
        	try{
        		this.runOnUiThread(new Runnable() {
                    public void run() {
                        if(taskErrorMsgStr.equalsIgnoreCase("")){
                        	onClose();
                        }
                        else if(taskErrorMsgStr.indexOf("created") > 0){
                        	SapGenConstants.showErrorDialog(ProductShoppingCartForTablet.this, taskErrorMsgStr);   
                        	
                        	if(dbObjSCart == null)
                				dbObjSCart = new ProductShoppingCartListPersistent(ProductShoppingCartForTablet.this);
                        	dbObjSCart.clearListTable();
                			dbObjSCart.closeDBHelper();
                        	
                        	onClose();
                        }
                    }
                });
            }
            catch(Exception asf){}
        }
    }//fn updatePOServerResponse
    
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
    
    final Runnable custview = new Runnable(){
        public void run()
        {
        	try{
        		initCustDBConnection(custSearchStr);
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    private void initPriceDBConnection(){
		try{
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);
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
				valFlag = 1;
	    		for (int i = 0; i < priceList.size(); i++) {
	    			proListMap = (HashMap<String, String>) priceList.get(i);   
	    			id = (String) proListMap.get(DBConstants.MATNR_COLUMN_NAME);   
	    			price = (String) proListMap.get(DBConstants.NETWR_COLUMN_NAME); 
	    			proPriceMap.put(id, price);
	    		}
			}
    		
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME);
    			tableExits = dbObj.checkTable();
    			if(tableExits){
    				totalPriceStr = dbObj.getParticularField(DBConstants.NETWR_COLUMN_NAME, DBConstants.KUNAG_COLUMN_NAME, customerIdStr);
    				mainPriceList = dbObj.readListDataFromDB(this);      
    			}
        		if(priceList == null)
        			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
				if(totalPriceStr != null && totalPriceStr.length() > 0){
			    	custnameLinear.setVisibility(View.VISIBLE);
					totprilinear.setVisibility(View.VISIBLE);
					//totaPriceTV.setText(" "+totalPriceStr);
					SapGenConstants.showLog("totaPriceTV: "+totalPriceStr);
				}else{
					totprilinear.setVisibility(View.GONE);
				}
    		dbObj.closeDBHelper(); 
    		initDBConnection();
    		//initLayout();			
		}catch(Exception sggh){
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On initPriceDBConnection : "+sggh.toString());
		}
    }//fn initPriceDBConnection
    
    private void insertProductGetPriceDataIntoDB(){
    	try {
			if(metaProdListArray != null && metaProdListArray.size() > 0){
				if(DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME;
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
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);
					dbObj.dropTable(ProductShoppingCartForTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(ProductShoppingCartForTablet.this);
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
	    		if(DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME;
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
				if(dbObjColumns == null)
					dbObjColumns = new ProductColumnDB(ctx);
				dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
				dbObjColumns.closeDBHelper();					
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
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME);				
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(ProductShoppingCartForTablet.this);
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
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETPRICE_LIST_DB_TABLE_NAME);			
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
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME);
			boolean isExits = dbObj.checkTable();
			SapGenConstants.showLog(DBConstants.PRODUCT_GETNETPRICE_LIST_DB_TABLE_NAME+" isExits "+isExits);
			if(productTotalPriceListArray != null && productTotalPriceListArray.size() > 0){
				dbObj.insertDetails(productTotalPriceListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertProductMainPriceListDataIntoDB : "+e.toString());
		}        	
	}//fn insertProductMainPriceListDataIntoDB
    
    private void initCustDBConnection(String customername1Str) {
		try{
			SapGenConstants.showErrorDialog(this, customername1Str);
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.CUSTOMER_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.CUSTOMER_LIST_DB_TABLE_NAME);
    			tableExits = dbObj.checkTable();
    			if(tableExits)
    				custArrList = dbObj.readDataSelectedStrListDataFromDB(this, customername1Str, DBConstants.NAME1_COLUMN_NAME); 
        	if(custArrList == null && custArrList.size() <= 0)
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper(); 
			if(custArrList.size() != 0)
				prefillCustomerData();
			else
				SapGenConstants.showErrorDialog(this, getResources().getString(R.string.CUSTOMER_NOT_AVAILABLE));
		}catch(Exception sfg1){
			SapGenConstants.showErrorLog("Error in initCustDBConnection : "+sfg1.toString());
		}	
	}//end of initDBConnection
    
    private void insertCustDataIntoDB() {
    	try {
			if(metaCustListArray != null && metaCustListArray.size() > 0){
				if(DBConstants.CUSTOMER_LIST_DB_TABLE_NAME != null && DBConstants.CUSTOMER_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.CUSTOMER_LIST_DB_TABLE_NAME;
	    		}
				if(custArrList != null && custArrList.size() > 0){
					String columnLists = "";
					if(metaCustListArray != null && metaCustListArray.size() > 0){
						for(int i=0; i<metaCustListArray.size(); i++){
							if( i == (metaCustListArray.size() - 1)){
								columnLists += metaCustListArray.get(i).toString().trim();
							}else{
								columnLists += metaCustListArray.get(i).toString().trim()+":";
							}
						}
					}
					SapGenConstants.showLog("columnLists: "+columnLists);
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);					
					if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
					dbObjColumns.closeDBHelper();					
					if(metaCustListArray != null && metaCustListArray.size() > 0){
						metaCustListArray.clear();
					}					
					if(columnLists != null && columnLists.length() > 0){
						String[] separated = columnLists.split(":");
						if(separated != null && separated.length > 0){
							for(int i=0; i<separated.length; i++){
								SapGenConstants.showLog("  "+separated[i]);
								metaCustListArray.add(separated[i].toString().trim());
							}
						}
					}						
					DataBasePersistent.setTableContent(metaCustListArray);
					DataBasePersistent.setColumnName(metaCustListArray);

					if(DBConstants.CUSTOMER_LIST_DB_TABLE_NAME != null && DBConstants.CUSTOMER_LIST_DB_TABLE_NAME.length() > 0){
		    			DBConstants.DB_TABLE_NAME = DBConstants.CUSTOMER_LIST_DB_TABLE_NAME;
		    		}
					if(dbObj != null)
						dbObj = null;
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.CUSTOMER_LIST_DB_TABLE_NAME);
					dbObj.dropTable(ProductShoppingCartForTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.CUSTOMER_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(ProductShoppingCartForTablet.this);
					}	
					if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((custListCount == 0) && (custArrList.size() == 0)){
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
						}
						else if((custListCount > 0) && (custArrList.size() > 0)){
							dbObj.clearListTable();
							insertCustomerListDataIntoDB(custArrList);
						}
					}
					if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((custListCount == 0) && (custArrList.size() == 0)){
							dbObj.clearListTable();
						}
						else if((custListCount > 0) && (custArrList.size() == 0)){
						}
						else if((custListCount > 0) && (custArrList.size() > 0)){
							dbObj.clearListTable();
							insertCustomerListDataIntoDB(custArrList);
						}
					}					
				}
			}      
			dbObj.closeDBHelper();			
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
				dbObj = new DataBasePersistent(this, DBConstants.CUSTOMER_LIST_DB_TABLE_NAME);			
			if(custListArray != null && custListArray.size() > 0){
				dbObj.insertDetails(custListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertCustomerListDataIntoDB : "+e.toString());
		}        	
	}//fn insertCustomerListDataIntoDB
    
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
    
    private void prefillCustomerData(){
    	try{
    		//if(custArrList != null){
    			if(custArrList.size() > 0){
    	    		custDetArr = getCustomerList();
    	    		if(custDetArr != null){
	    	    		if(custDetArr.size() > 1){
	    	    			showCustomerAlert();
	    	    		}else{
	    	    			showCustDetails(0);
	    	    			/*if(copyflag == 1 || socrtFlag == 1){
	    	    				showCustomerHeaderInfo(idstr, 0);
	    	    			}else{
	    	    				showCustDetails(0);
	    	    			}	*/    	    				
	    	    		}
    	    		}
    	    		else
    	    			SapGenConstants.showErrorDialog(this, getResources().getString(R.string.CUSTOMER_NOT_AVAILABLE));
    			}
    			else
	    			SapGenConstants.showErrorDialog(this, getResources().getString(R.string.CUSTOMER_NOT_AVAILABLE));
    		/*}
    		else
    			SapGenConstants.showErrorDialog(this, getResources().getString(R.string.CUSTOMER_NOT_AVAILABLE));*/
    	}
    	catch(Exception adf){
    		SapGenConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData    
    
    private ArrayList getCustomerList(){
    	ArrayList choices = null;
        try{
            if(custArrList != null){              
                int arrSize = custArrList.size();
                choices = new ArrayList();             
            	if(custArrList != null && custArrList.size() > 0 ){
    				for (int i = 0; i < custArrList.size(); i++) {
    					custMap = (HashMap<String, String>) custArrList.get(i);  
    					nameStr = (String) custMap.get(DBConstants.NAME1_COLUMN_NAME);
    					idstr = (String) custMap.get(DBConstants.KUNNR_COLUMN_NAME);
                        combStr = nameStr+":"+idstr;                       
                        choices.add(combStr);
    				}
            	}
            }
            SapGenConstants.showLog("Size of choices : "+choices.size());
        }
        catch(Exception sfg){
        	SapGenConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
        }
        return choices;
    }//fn getCustomerList
    
    public void showCustomerAlert(){
		 try{   		     	       	
        	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout;    		  
    		layout = inflater.inflate(R.layout.customer_dialog, (ViewGroup) findViewById(R.id.listviewdialog));		        		       		  
    		ListView listview = (ListView)layout.findViewById(R.id.list2);
    		CustomerListAdapter = new CustomerListAdapter(this);
    		builder = new AlertDialog.Builder(this).setTitle("Select a Customer");	        		  	        		 
    		builder.setInverseBackgroundForced(true);
    		View view = inflater.inflate(R.layout.custom_title_layout,null);
    		builder.setCustomTitle(view);	        		 
    		builder.setView(layout); 	        		
    		builder.setSingleChoiceItems(CustomerListAdapter, -1,new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {			
    				SapGenConstants.showLog("which : "+which);
    				selctdPos = which;
    				SapGenConstants.showLog("Selected Position : "+selctdPos);
					showCustDetails(selctdPos);
    				/*if(copyflag == 1 || socrtFlag == 1){
    					showCustomerHeaderInfo(idstr, selctdPos);
    				}else{
    					showCustDetails(selctdPos);
    				}*/
    				alertDialog.dismiss();
    			}
    		});
    		alertDialog = builder.create();    		  
    		alertDialog.show();    		 
    	}catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in showAlert : "+sfg.toString());
    	}
	}//fn showCustomerAlert
    
    public void showCustDetails(int selIndex){
    	//HashMap<String, String> custMap = null;
    	String custNameStr="";
    	custnameLinear.setVisibility(View.VISIBLE);
    	if(prodsearchlinear != null)
    		prodsearchlinear.setVisibility(View.GONE);

		custMap = (HashMap<String, String>) custArrList.get(selIndex);
		SapGenConstants.showErrorLog("custMap : "+custMap);
    	if(custMap != null){
	       	String cname = (String) custMap.get(DBConstants.NAME1_COLUMN_NAME);
	       	custName1Str = cname;
	    	customerIdStr = (String) custMap.get(DBConstants.KUNNR_COLUMN_NAME);
	    	custNameStr= cname.trim()+"("+ customerIdStr.trim()+")";
	    	DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;	    		
			displayTableLayoutUI(DBConstants.DEVICE_TYPE_SHOPPING_CART_W_TOTAL, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
    	}
    	//prodcustNameTV.setText(" "+custNameStr);    	
    	uiValue = 1;
		displayUI(DBConstants.DEVICE_TYPE_SHOPPING_CART_HEADER_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, headblkcusdtl); 	
    }//showCustDetails
    
    private void showCustomerHeaderInfo(String custIdStr, int selIndex){
    	try{
        	HashMap<String, String> custMap = null;
    		if(!custIdStr.equalsIgnoreCase("") && (selIndex >= 0)){
    			 if((custArrList != null) && (selIndex < custArrList.size())){
    				 custMap = (HashMap<String, String>) custArrList.get(selIndex);  
                     if(custMap != null){
                    	 customerIdStr = (String) custMap.get(DBConstants.KUNNR_COLUMN_NAME);
                    	 String cname = (String) custMap.get(DBConstants.NAME1_COLUMN_NAME);
                    	 if(cname != null && cname.length() > 0){
                        	 custDetStr = cname+" ("+customerIdStr+")";
                    	 }else{
                        	 custDetStr = "("+customerIdStr+")";
                    	 }
                    	 
                    	 String city = (String) custMap.get(DBConstants.ORT01_COLUMN_NAME);
                    	 String region = (String) custMap.get(DBConstants.REGIO_COLUMN_NAME);
                    	 String country = (String) custMap.get(DBConstants.LAND1_COLUMN_NAME);
                    	 String data = "";
                    	 if(city != null && city.length() > 0){
                    		 data += city.toString();
                    	 }  
                    	 if(region.length() > 0){
                    		 if(data.length() > 0){
                        		 data += ","+region.toString();
                    		 }else{
                        		 data += region.toString();
                    		 }
                    	 }
                    	 if(country.length() > 0){
                    		 if(data.length() > 0){
                        		 data += ","+country.toString();
                    		 }else{
                        		 data += country.toString();
                    		 }
                    	 }
                    	 custAddStr = data;
                    	 
                    	 if(prodsearchlinear != null)
                    		 prodsearchlinear.setVisibility(View.GONE);
                    	 if(procusheaderlinear != null)
                    		 procusheaderlinear.setVisibility(View.VISIBLE);
                    	 procusheaderlinear.setBackgroundDrawable(getResources().getDrawable(R.drawable.sales_item_border));
                    	 if(custNameTV != null)
                    		 custNameTV.setText(custDetStr);
                    	 if(custAddressTV != null)
                    		 custAddressTV.setText(custAddStr);
                    	 if(soValueET != null)
                    		 soValueET.setEnabled(false);
                     }
    			 }
    		}
    	}
    	catch(Exception sfgh){
    		SapGenConstants.showErrorLog("Error in showCustomerHeaderInfo : "+sfgh.toString());
    	}
    }//fn showCustomerHeaderInfo
    
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
            	SapGenConstants.showErrorLog("Error in ContactListAdapter : "+qw.toString());
			}            
           return convertView;	           
		}		
		public void removeAllTasks() {
            notifyDataSetChanged();
        } 		
    }//End of ContactListAdapter
     
    public void beforeTextChanged(CharSequence s, int start, int count,
 			int after) {
 		// TODO Auto-generated method stub		
 	}

 	public void onTextChanged(CharSequence s, int start, int before, int count) {
 		// TODO Auto-generated method stub	
 	}
 	
 	public void afterTextChanged(Editable s) { 
 		  //SapGenConstants.showLog("Text : "+s.toString());			
 		  //SapGenConstants.showLog("Material Id : "+clickedMatId.toString().trim());
 	}
 	
 	private void onClose(){
    	try{
    		setResult(RESULT_OK); 
    		finish();
    	}
    	catch(Exception sfg){}
    }//fn onClose    
 	
 	public void onBackPressed() {
		setResult(RESULT_OK, intent); 
		finish();
	}//fn onBackPressed
}