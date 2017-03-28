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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.ProductLib.Database.ProductColumnDB;
import com.globalsoft.ProductLib.Database.ProductDBConstants;
import com.globalsoft.ProductLib.Database.ProductShoppingCartListPersistent;
import com.globalsoft.ProductLib.ImgLoader.ImageLoader;
import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductMainScreenForTablet extends Activity implements TextWatcher {
	private TextView[] valTV;
	private ImageView[] prodIV;
	private ImageView[] prodIVCart;
	private CheckBox[] chckBox;
	//private EditText searchET;
	private ProgressDialog pdialog = null;
	private SoapObject resultSoap = null;
	private boolean sortProdNameFlag = false;
	private int sortIndex = -1;
	private String serverErrorMsgStr = "";
	private TextView myTitle, selCatTV;
	//private Button catBtn;
	private TextView[] headLblTV;
	public TextView pageTitle, firstPgTV, lastPgTV, nextPgTV, prevPgTV;
	int offset = 0;
    int data;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private boolean tableExits = false;
    public ImageLoader imageLoader; 
    private static final int MAIN_ID = Menu.FIRST;

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
	
	private String searchStr = "";
	private int dispwidth = 300;
	private static int respType = 0;
	public boolean flag_pref_contxt = false, flag_pref_employee_get = false;
	private ProductShoppingCartListPersistent dbObjSCart = null;	
	private ArrayList cartArrList = new ArrayList();
	
	private ArrayList selchkboxlist = new ArrayList();
	
	private ArrayList uiConfList = new ArrayList();
	private ArrayList alluiConfList = new ArrayList();
	private ArrayList metauiConfListArray = new ArrayList();
	
	private ArrayList searchCatList = new ArrayList();
	private ArrayList allSearchCatList = new ArrayList();
	private ArrayList metaSearchCatListArray = new ArrayList();	
	
	private ArrayList productList = new ArrayList();
	private ArrayList allProductList = new ArrayList();
	private ArrayList metaProdListArray = new ArrayList();		

	private ArrayList attach01List = new ArrayList();
	private ArrayList attach01RList = new ArrayList();
	
	private ArrayList metaAtta01Array = new ArrayList();	
	private ArrayList metaAtta01RArray = new ArrayList();	

    final Handler productsData_Handler = new Handler();
    
    private DataBasePersistent dbObj = null;
	private DataBasePersistent dbObjAtta01 = null;
	private DataBasePersistent dbObjCatSel = null;
	//private DataBasePersistent dbObj = null;
	//private ProductSearchCategoryListPersistent dbObjSerCat = null;
	private ProductColumnDB dbObjColumns = null;
	
	private HashMap<String, String> labelMap = new HashMap<String, String>();	
	private ArrayList valArrayList = new ArrayList();
	
	private boolean internetAccess = false;
	private static Context ctx;	
	private TableLayout colValTableLayout = null, colHeadTableLayout = null;
	protected CharSequence[] _options = null;
	boolean[] _selections =  null;
	private ArrayList srcCatFilterIdArrList = new ArrayList();
	private ArrayList srcCatFilterArrList = new ArrayList();
	private ArrayList allSrcCatFilterArrList = new ArrayList();
	private LinearLayout dynllheadblk, actscll;
	private RelativeLayout bodylayout;
	private ArrayList valHeaderArrayList = new ArrayList();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.producthead); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.MAINSCR_PRODLIST_TITLE1));

		ctx = this.getApplicationContext();
		dispwidth = SapGenConstants.getDisplayWidth(this);	
		if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		imageLoader = new ImageLoader(ProductMainScreenForTablet.this.getApplicationContext());
		SharedPreferences settings = getSharedPreferences(ProductLibConstant.PREFS_NAME_FOR_PRODUCTCAT, 0);      
		flag_pref_contxt = settings.getBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_CONTEXT, false);
		flag_pref_employee_get = settings.getBoolean(ProductLibConstant.PREFS_KEY_PRODUCT_LIST_FOR_EMPLOYEE_GET, false);
		
		bodylayout = (RelativeLayout) findViewById(R.id.bodylayout);
		
		dynllheadblk = (LinearLayout) findViewById(R.id.headblk);
		dynllheadblk.removeAllViews();
		dynllheadblk.setOrientation(LinearLayout.HORIZONTAL);
		
		actscll = (LinearLayout) findViewById(R.id.actscll);
		actscll.removeAllViews();
		actscll.setOrientation(LinearLayout.VERTICAL);
		
		/*searchET = (EditText)findViewById(R.id.searchBEF);
		searchET.setText(searchStr);
		searchET.addTextChangedListener(this);
		
		searchET.setFocusable(true);
		searchET.setFocusableInTouchMode(true);
		searchET.requestFocus();*/

		/*catBtn = (Button) findViewById(R.id.catBtn);
		catBtn.setOnClickListener(catBtnListener);*/
		
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
        
		internetAccess = SapGenConstants.checkConnectivityAvailable(ProductMainScreenForTablet.this);
		if(internetAccess){
			SapGenConstants.showLog("Data from SAP!");	
			if(SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT == 0){
				SapGenConstants.showLog("first time launch");
				SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT = 1;
	    		initStatusSoapConnection();
			}else{
				initSoapConnection();
			}
		}
		else{
    		setTitleValue();
			initDBConnection();	
			SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
		}		
    }//onCreate
    
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
	
	public void refreshList(){
		flag_pref_employee_get = false;
		flag_pref_contxt =false;
		initSoapConnection();
		initStatusSoapConnection();
	  }//refreshList
	
    /*private OnClickListener catBtnListener = new OnClickListener(){
		public void onClick(View v){
			catBtnAction();
		}
	};*/
	
	private void catBtnAction(){
		try {
			/*String editStr = searchET.getText().toString().trim();
			if (editStr != null && editStr.length() > 0) {
				searchET.setText("");
			}*/
			showDialog(0);
		} catch (Exception sfg) {
			SapGenConstants.showErrorLog("Error in catBtnAction : "+sfg.toString());
		}
	}//fn catBtnAction
    
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
    
    private void initLayout(){
    	try{
    		colHeadTableLayout = (TableLayout)findViewById(R.id.prodheadtbllayout);
			if(colHeadTableLayout != null)
				colHeadTableLayout.removeAllViews();
			
			dynllheadblk.removeAllViews();
			displayUI(DBConstants.DEVICE_TYPE_OVRVW_W_HEADER_BLOCK_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, dynllheadblk);
			
			actscll.removeAllViews();
     		displayUI(DBConstants.DEVICE_TYPE_OVRVW_ACTION_BAR_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, actscll);            
			 			
     		DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
			//labelMap = DataBasePersistent.readAllLablesFromDB(this, SapGenConstants.DEVICE_TYPE_WIDE_OV_TAG);
			valArrayList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.DEVICE_TYPE_OVRVW_W_MAIN_BLOCK_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);						
			SapGenConstants.showLog("labelMap : "+labelMap.size());
			SapGenConstants.showLog("valArrayList : "+valArrayList.size());
			ArrayList labels = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.DEVICE_TYPE_OVRVW_W_MAIN_BLOCK_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			String labVal = "";
			if(valArrayList != null && valArrayList.size() > 0){
				int cols =  labels.size();					
				dispwidth = SapGenConstants.getDisplayWidth(this);
				if(dispwidth < SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					colHeadTableLayout.setColumnStretchable(1, true);
				//headLblTV = new TextView[valArrayList.size()];  
				headLblTV = new TextView[labels.size()]; 
				TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.partial_table_row, null);
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
	
				/*TextView headLblTVForCB = (TextView) getLayoutInflater().inflate(R.layout.partial_textview, null);
				headLblTVForCB.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				headLblTVForCB.setWidth(55);			
				headLblTVForCB.setText(" ");
				if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					headLblTVForCB.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);								
				tr1.addView(headLblTVForCB);
				
				TextView headLblTVForImg = (TextView) getLayoutInflater().inflate(R.layout.partial_textview, null);
				headLblTVForImg.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				headLblTVForImg.setWidth(55);			
				headLblTVForImg.setText(" ");
				if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					headLblTVForImg.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);								
				tr1.addView(headLblTVForImg);*/
				
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
						if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) || valueValStr.equalsIgnoreCase(DBConstants.IMAGE_TAPPABLE_TAG)){
							headLblTV[i].setWidth(55);
    					}else{
    						headLblTV[i].setMinWidth(100);
    						headLblTV[i].setWidth(labelWidth);
    					}
					}					
					headLblTV[i].setId(i);
					headLblTV[i].setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							if(!valueValStrCk.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.IMAGE_TAPPABLE_TAG)){
								sortItemsAction(metaValue);
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
				//Manually added Field for Cart indicator
				TextView headLblTVForSC = (TextView) getLayoutInflater().inflate(R.layout.partial_textview, null);
				headLblTVForSC.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				headLblTVForSC.setWidth(60);			
				headLblTVForSC.setText("Cart");			
				tr1.addView(headLblTVForSC);				
				colHeadTableLayout.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				drawSubLayout();
			}
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
    		DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
			valHeaderArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
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
			                 			et.setLayoutParams(new LinearLayout.LayoutParams(
			                 				     LinearLayout.LayoutParams.WRAP_CONTENT,
			                 				     LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
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
												catBtnAction();
											}	
				                        });
		                     			dynll.addView(bt);
		                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG)){			                    			    		
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
		                    			dynll.addView(iv);
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
				                 			et.setLayoutParams(new LinearLayout.LayoutParams(
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT,
				                 				     LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f));
			                     			et.setTextColor(getResources().getColor(R.color.black));
				                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL|Gravity.LEFT);
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
													catBtnAction();
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
													showSCartScreen();
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
    
    private void showSCartScreen(){
    	try {
    		if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductMainScreenForTablet.this);
        	ArrayList cartArrList = dbObjSCart.getCartIdList();
        	if(cartArrList.size() > 0){
    			Intent intent = new Intent(ProductMainScreenForTablet.this, ProductShoppingCartForTablet.class);
    			startActivityForResult(intent,SapGenConstants.PRODUCT_CAT_SCART_SCREEN_TBL);
        	}else{
        		SapGenConstants.showErrorDialog(ProductMainScreenForTablet.this, getResources().getString(R.string.MAINSCR_PROD_SHOPINGCART_NOLIST));
        	}
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("On showSCartScreen : "+ce.toString());
		}
    }//fn showSCartScreen
    
    private void drawSubLayout(){
		try{
			int i;
			colValTableLayout = (TableLayout)findViewById(R.id.prodvaluetbllayout);
			if(colValTableLayout != null)
				colValTableLayout.removeAllViews();
			
			if(selchkboxlist != null && selchkboxlist.size() > 0){
				selchkboxlist.clear();
			}
			
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
	        
	        HashMap<String, String> stockMap = null; 			 
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));			
			LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			linparams.leftMargin = 10; 
			linparams.rightMargin = 10; 
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
			
			if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductMainScreenForTablet.this);
        	cartArrList = dbObjSCart.getCartIdList();
			dbObjSCart.closeDBHelper();
    		SapGenConstants.showLog("cartArrList: "+cartArrList.size());
			
			if(valArrayList != null && valArrayList.size() > 0){
				if(productList != null){
					String valStr = "", imageUrlStr = "", matIdStr = "";
	                int rowSize = productList.size();                
	                SapGenConstants.showLog("Product List Size  : "+rowSize);  
	                SapGenConstants.showLog("offset:"+offset);
	                SapGenConstants.showLog("data:"+data);
	                chckBox = new CheckBox[rowSize];
	                prodIV = new ImageView[rowSize]; 
	                prodIVCart = new ImageView[rowSize]; 
	                if(offset <= productList.size() && data <= productList.size() ){
						for (i = offset; i < data; i++) {
		                    tr = new TableRow(this);        
							stockMap = (HashMap<String, String>) productList.get(i);     

                    		matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
                    		imageUrlStr = getImageUrl(matIdStr);
                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);

	        				final String pId = matIdStr;
	        				final int chek = i;
                    		chckBox[i] = new CheckBox(this); 
                            chckBox[i].setId(i);                       
                            chckBox[i].setLayoutParams(linparams); 
                            chckBox[i].setButtonDrawable(R.drawable.checkboxcustom);
                            chckBox[i].setOnClickListener( new View.OnClickListener()
                            { 
                                public void onClick(View v) 
                                {
             						CheckBox cb = (CheckBox) v ; 
             				        if (((CheckBox) v).isChecked()) {
						        		if(!selchkboxlist.contains(pId)){
	             				            selchkboxlist.add(pId);
						        		}
             				        } else {
             				        	selchkboxlist.remove(pId);
             				        }
             					}
             				});
        	                linearLayout.addView(chckBox[i]);
                    		
        	                prodIV[i] = new ImageView(this); 
        	                prodIV[i].setPadding(5,5,0,5);
        	                prodIV[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            				if(imageUrlStr != null && imageUrlStr.length() > 0){ 
            					try{
            						imageLoader.DisplayImage(imageUrlStr, prodIV[i]);
            					} catch (Exception e) {
                					prodIV[i].setImageResource(R.drawable.default_img);
									SapGenConstants.showErrorLog("On DownloadImageTask : "+e.toString());
								} 
            					prodIV[i].invalidate();
            				}else{
            					imageUrlStr = "";
            					prodIV[i].setImageResource(R.drawable.default_img);
            				} 
        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
                            	linlayparams1.height = 40;
                            	linlayparams1.width = 40;
                            	prodIV[i].setLayoutParams(linlayparams1);
                            }
        	                linearLayout.addView(prodIV[i]);
	    					tr.addView(linearLayout);	
	    					
			                valTV = new TextView[valArrayList.size()];
		                    if(stockMap != null){
		                    	for (int i1 =0; i1 < valArrayList.size(); i1++) {
			                    	try {
			                    		valStr = valArrayList.get(i1).toString().trim();
									} catch (Exception e) {
										SapGenConstants.showErrorLog("Error on drawSubLayout : "+e.toString());
									} 		                		                              
			                        valTV[i1] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                        String metaStr = "";			                        
			                        String typeStrDis = "";
									if(valStr.indexOf("::") >= 0){
	        							String[] separated = valStr.split("::");
	        							if(separated != null && separated.length > 0){
	        								if(separated.length > 1){
	        									metaStr  = separated[0];
	        									typeStrDis  = separated[1];
	        								}else{
	        									metaStr  = separated[0];
	        								}
	        							}
	        						}else{
	        							metaStr = valStr;
	        						}
									String valStrDis = (String) stockMap.get(metaStr);
									if(valStrDis != null && valStrDis.length() > 0){
				        				if(valStrDis.length() > 25){
				                    		String strSep = valStrDis.substring(0, 25);
				                    		valTV[i1].setText(strSep+"...");
				                    	}else{
				                    		valTV[i1].setText(valStrDis);
				                    	}
									}else{
										valTV[i1].setText("");
									}
			        				//final String pId = matIdStr;
			        				if(typeStrDis != null && typeStrDis.length() > 0){
			        					if(typeStrDis.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStrDis.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					                    	valTV[i1].setGravity(Gravity.RIGHT);
			        					}else{
					                    	valTV[i1].setGravity(Gravity.LEFT);			                      
				        				}
			        				}else{
				                    	valTV[i1].setGravity(Gravity.LEFT);	                     
			        				}
			                        valTV[i1].setMinWidth(100);
			                    	valTV[i1].setWidth(labelWidth);
			                    	valTV[i1].setId(i1);
			                    	valTV[i1].setOnClickListener(new View.OnClickListener() {
										public void onClick(View view) {
											int id = view.getId();	
											//showStockListItemScreen(id);
											if(selchkboxlist != null && selchkboxlist.size() > 0){
								        		if(!selchkboxlist.contains(pId)){
								        			selchkboxlist.add(pId);
								        		}
											}else{
								        		selchkboxlist.add(pId);
											}
							        		chckBox[chek].setChecked(true);
											//SapGenConstants.showLog("value  "+pId);
											showProductDetailsScreen(pId);
										}	
			                        });
			                    	//valTV[i1].setPadding(5,0,0,0);			                            					    					
			    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			    						valTV[i1].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			    					}			    					
			    					tr.addView(valTV[i1]);   					
		                    	}
		                    	LinearLayout linearLayoutCV = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
	                    		prodIVCart[i] = new ImageView(this); 
		                    	prodIVCart[i].setPadding(5,5,0,5);
		                    	prodIVCart[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            				if(cartArrList != null && cartArrList.size() > 0){
	            					if(cartArrList.contains(matIdStr)){
	            						prodIVCart[i].setVisibility(View.VISIBLE);
		            					prodIVCart[i].setImageResource(R.drawable.shoping_cart);
	    	        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	    	                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
	    	                            	linlayparams1.height = 40;
	    	                            	linlayparams1.width = 40;
	    	                            	prodIVCart[i].setLayoutParams(linlayparams1);
	    	                            }
	    	        	                linearLayoutCV.addView(prodIVCart[i]);
	    		    					tr.addView(linearLayoutCV);
	            					}else{
	            						prodIVCart[i].setVisibility(View.GONE);
	            					}
	            				} else{
            						prodIVCart[i].setVisibility(View.GONE);	            					
	            				}
	        	                //SapGenConstants.showLog("sc matIdStr:"+matIdStr);
		                    	
		    					if(i%2 == 0)
		    						tr.setBackgroundResource(R.color.item_even_color);
					            else
					            	tr.setBackgroundResource(R.color.item_odd_color);			    					
		    					colValTableLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                    }					
						}
	                }else{
						for (i = offset; i < productList.size(); i++) {
		                    tr = new TableRow(this);        
							stockMap = (HashMap<String, String>) productList.get(i);      
                    		matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
                    		imageUrlStr = getImageUrl(matIdStr);        
                    		LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
                    		
                    		final String pId = matIdStr;
	        				final int chek = i;
                    		chckBox[i] = new CheckBox(this); 
                            chckBox[i].setId(i);                       
                            chckBox[i].setLayoutParams(linparams); 
                            chckBox[i].setButtonDrawable(R.drawable.checkboxcustom);
                            chckBox[i].setOnClickListener( new View.OnClickListener()
                            { 
                                public void onClick(View v) 
                                {
             						CheckBox cb = (CheckBox) v ; 
             				        if (((CheckBox) v).isChecked()) {
										if(selchkboxlist != null && selchkboxlist.size() > 0){
							        		if(!selchkboxlist.contains(pId)){
		             				            selchkboxlist.add(pId);
							        		}
										}else{
											selchkboxlist.add(pId);
										}
             				            //SapGenConstants.showLog("Selected CheckBox val" + pId+ " true");
             				        } else {
             				        	selchkboxlist.remove(pId);
             				        	//SapGenConstants.showLog("Not selected" + pId+ " false");
             				        }
             					}
             				});
        	                linearLayout.addView(chckBox[i]);
        	                
                    		prodIV[i] = new ImageView(this); 
        	                prodIV[i].setPadding(5,5,0,5);
        	                prodIV[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            				if(imageUrlStr != null && imageUrlStr.length() > 0){  
            					try{
            						//new DownloadImageTask(prodIV[i], ProductMainScreenForTablet.this).execute(imageUrlStr);
            						imageLoader.DisplayImage(imageUrlStr, prodIV[i]);
            					} catch (Exception e) {
                					prodIV[i].setImageResource(R.drawable.default_img);
									SapGenConstants.showErrorLog("On DownloadImageTask : "+e.toString());
								} 
            					//imageLoader.DisplayImage(imageUrlStr, ProductMainScreenForTablet.this, prodIV[i]);  
            				}else{
            					imageUrlStr = "";
            					prodIV[i].setImageResource(R.drawable.default_img);
            				} 
        	                //SapGenConstants.showLog("imageUrlStr:"+imageUrlStr);
        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
                            	linlayparams1.height = 40;
                            	linlayparams1.width = 40;
                            	prodIV[i].setLayoutParams(linlayparams1);
                            }
        	                linearLayout.addView(prodIV[i]);
	    					tr.addView(linearLayout);
            				
        	                //SapGenConstants.showLog("imageUrlStr:"+imageUrlStr);                       
			                valTV = new TextView[valArrayList.size()];
		                    if(stockMap != null){
		                    	for (int i1 =0; i1 < valArrayList.size(); i1++) {
		                    		try {
			                    		valStr = valArrayList.get(i1).toString().trim();
									} catch (Exception e) {
										SapGenConstants.showErrorLog("Error on drawSubLayout : "+e.toString());
									} 		                		                              
			                        valTV[i1] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
			                        String metaStr = "";			                        
			                        String typeStrDis = "";
									if(valStr.indexOf("::") >= 0){
	        							String[] separated = valStr.split("::");
	        							if(separated != null && separated.length > 0){
	        								if(separated.length > 1){
	        									metaStr  = separated[0];
	        									typeStrDis  = separated[1];
	        								}else{
	        									metaStr  = separated[0];
	        								}
	        							}
	        						}else{
	        							metaStr = valStr;
	        						}
									String valStrDis = (String) stockMap.get(metaStr);
									if(valStrDis != null && valStrDis.length() > 0){
				        				if(valStrDis.length() > 25){
				                    		String strSep = valStrDis.substring(0, 25);
				                    		valTV[i1].setText(strSep+"...");
				                    	}else{
				                    		valTV[i1].setText(valStrDis);
				                    	}
									}else{
										valTV[i1].setText("");
									}
		        	                //SapGenConstants.showLog("valStrDis:"+valStrDis);  
		        	                //SapGenConstants.showLog("typeStrDis:"+typeStrDis);  
			        				if(typeStrDis != null && typeStrDis.length() > 0){
			        					if(typeStrDis.equalsIgnoreCase(DBConstants.CURR_DATATYPE_TAG) || typeStrDis.equalsIgnoreCase(DBConstants.QUAN_DATATYPE_TAG)){
					                    	valTV[i1].setGravity(Gravity.RIGHT);	
			        					}else{
					                    	valTV[i1].setGravity(Gravity.LEFT);			                      
				        				}
			        				}else{
				                    	valTV[i1].setGravity(Gravity.LEFT);			                      
			        				}
			                        valTV[i1].setMinWidth(100);
			                    	valTV[i1].setWidth(labelWidth);
			                    	valTV[i1].setId(i1);
			                    	valTV[i1].setOnClickListener(new View.OnClickListener() {
										public void onClick(View view) {
											int id = view.getId();	
											//showStockListItemScreen(id);
											//SapGenConstants.showLog("value  "+pId);
							        		chckBox[chek].setChecked(true);
											showProductDetailsScreen(pId);
										}	
			                        });
			                    	//valTV[i1].setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);      					    					
			    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			    						valTV[i1].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
			    					}			    			
			    					tr.addView(valTV[i1]);		    					
		                    	}
		                    	
		                    	LinearLayout linearLayoutCV = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
	                    		prodIVCart[i] = new ImageView(this); 
		                    	prodIVCart[i].setPadding(5,5,0,5);
		                    	prodIVCart[i].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            				if(cartArrList != null && cartArrList.size() > 0){
	            					if(cartArrList.contains(matIdStr)){
	            						prodIVCart[i].setVisibility(View.VISIBLE);
		            					prodIVCart[i].setImageResource(R.drawable.shoping_cart);
	    	        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	    	                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	                            	linlayparams1.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
	    	                            	linlayparams1.height = 40;
	    	                            	linlayparams1.width = 40;
	    	                            	prodIVCart[i].setLayoutParams(linlayparams1);
	    	                            }
	    	        	                linearLayoutCV.addView(prodIVCart[i]);
	    		    					tr.addView(linearLayoutCV);
	            					}else{
	            						prodIVCart[i].setVisibility(View.GONE);
	            					}
	            				} else{
            						prodIVCart[i].setVisibility(View.GONE);	            					
	            				}
	        	                //SapGenConstants.showLog("sc matIdStr:"+matIdStr);
		    					if(i%2 == 0)
		    						tr.setBackgroundResource(R.color.item_even_color);
					            else
					            	tr.setBackgroundResource(R.color.item_odd_color);		
		    					
		    					colValTableLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		                    }					
						}
	                }
				}
			}
		}
		catch(Exception asgf){
			SapGenConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
   
    private void showProductDetailsScreen(String matIdStr){
    	try {    		
    		if(selchkboxlist != null && selchkboxlist.size() > 0){
            	for (int i1 =0; i1 < selchkboxlist.size(); i1++) {
                	try {
                		String valStr = (String)selchkboxlist.get(i1).toString().trim();
                		//SapGenConstants.showLog("valStr "+valStr);
					} catch (Exception e) {
						SapGenConstants.showErrorLog("Error on drawSubLayout : "+e.toString());
					} 		
            	}
    		}
			Intent intent = new Intent(ProductMainScreenForTablet.this, ProductDetailsForPhone.class);
			//intent.putExtra("SelIndex", selIndex);
			intent.putExtra("selMatIds", selchkboxlist);
    		intent.putExtra("matIdStr", matIdStr);
			startActivityForResult(intent,SapGenConstants.PRODUCT_CAT_MATT_DETAIL_SCREEN_PH);
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("On showProductDetailsScreen : "+ce.toString());
		}
    }//fn showProductDetailsScreen
		        
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
    
    public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		  SapGenConstants.showLog("Text : "+s.toString());
		  searchStr = s.toString().trim();
		  filter(searchStr);
	} 

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
            //C0[2].Cdata = "EVENT[.]PRODUCT-LIST-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            if(!flag_pref_contxt){
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]REFRESHED";
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
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]REFRESHED";
            }else{
            	C0[2].Cdata = "EVENT[.]PRODUCT-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
            }
            C0[3].Cdata = "ZGSEVDST_MTRLSRCH01[.]*";
        
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
	    				ProductMainScreenForTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductMainScreenForTablet.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
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
	    				ProductMainScreenForTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ProductMainScreenForTablet.this, "", getString(R.string.WAIT_TEXTS_AFTER_RESULT),true);
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
	                                    	if(j == 2){
	                                    		SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		docTypeArray[0] = docTypeStr;
	                                    		//DBConstants.PRODUCT_UICONF_TABLE_NAME = docTypeStr;	
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
                                metaUIConfLength = new String[metaSize];
                                metaSearchCatListArray.add(last);
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
	                                    	if(j == 3){
	                                    		docTypeArray[1] = docTypeStr;
	                                    		//DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME = docTypeStr;
	                                    		SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
	                                    		uiConfListCount = Integer.parseInt(rowCount);
	                                    		uiConfListType = respType;
	                                    		/*searchCatListCount = Integer.parseInt(rowCount);
	                                    		searchCatListType = respType;*/
	                                        }                                 
	                                    }
                                    }
                                    resC++;
                                    metaSize = resC-1;
                                }                    
                                int endIndex = result.lastIndexOf(';');
                                String last = result.substring(indexA,endIndex);
                                metaSearchCatLength = new String[metaSize];
                                metauiConfListArray.add(last);
                                
                            }
	                    		                    	
	                        if(j > 3){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	                            
	                            if(docTypeStr.equalsIgnoreCase(docTypeArray[0])){
	                            	// SapGenConstants.showLog("docTypeArray[0] :"+ docTypeStr);
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
	 	                           searchCatList.add(uiConfgArray);
	 	                          allSearchCatList.add(uiConfgArray);	 	                            	 	                           	 	                          
	                            } 
	                            else if(docTypeStr.equalsIgnoreCase(docTypeArray[1])){
		                        	resArray = new String[metaSearchCatLength.length]; 
	                            	result = result.substring(firstIndex);
	 	                            //SapGenConstants.showLog("docTypeArray[1] :"+ docTypeStr);
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
	 	                           uiConfList.add(searchCatArray);
	 	                          alluiConfList.add(searchCatArray);
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
			                                SapGenConstants.showErrorDialog(ProductMainScreenForTablet.this, serverErrorMsgStr);
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
    	            //stopProgressDialog();
    	            SapGenConstants.showErrorLog("On updateProductContext : "+adsf1.toString());
    	            stopProgressDialog();
    				this.runOnUiThread(new Runnable() {
    	                public void run() {
    	            		setTitleValue();
    	                	initSoapConnection();
    	                }
    	            });
    			}  
        	}else{
        		this.runOnUiThread(new Runnable() {
                    public void run() {
		        		stopProgressDialog();
		        		setTitleValue();
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
                                    		//SapGenConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
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
	 	                              
 		                            //SapGenConstants.showLog("docTypeStr :"+ docTypeStr);                             
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
			                                SapGenConstants.showErrorDialog(ProductMainScreenForTablet.this, serverErrorMsgStr);
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
                    }
                });	
        	}     	
        }
    }//fn updateServerResponse
    
    final Runnable uiContext = new Runnable(){
        public void run()
        {
        	try{
        		setTitleValue();
        		initSoapConnection();
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };
    
    private void setTitleValue() {
    	try {
    		dbObj =null;
    		if(dbObj == null)
    			dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
    		String title = dbObj.getTitle(DBConstants.DEVICE_TYPE_OVERVIEW_W_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
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
    		if(dbObj != null)
				dbObj =null;
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);	
			boolean uiisExits = dbObj.checkTable();
			SapGenConstants.showLog(DBConstants.PRODUCT_UICONF_TABLE_NAME+" isExits "+uiisExits);
			
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
					if(!uiisExits){
						dbObj.creatTable(this);
					}
					//this.deleteDatabase(DBConstants.UICONF_DATABASE_NAME);
					/*if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);*/
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
							//dbObj.clearProductUIConfListTable();
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
					SapGenConstants.showLog("ConfigList: "+DBConstants.DB_TABLE_NAME+"  "+columnLists);
					if(dbObjColumns == null)
						dbObjColumns = new ProductColumnDB(ctx);
					dbObjColumns.insertColumnsDetails(DBConstants.DB_TABLE_NAME, columnLists);
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
					dbObjCatSel.dropTable(ProductMainScreenForTablet.this);
					boolean isExits = dbObjCatSel.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_CATSEL_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObjCatSel.creatTable(ProductMainScreenForTablet.this);
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
					
		        	if(dbObjSCart == null)
						dbObjSCart = new ProductShoppingCartListPersistent(ProductMainScreenForTablet.this);
					
					DataBasePersistent.setTableContent(metaProdListArray);
					DataBasePersistent.setColumnName(metaProdListArray);
					if(dbObj == null)
						dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
					dbObj.dropTable(ProductMainScreenForTablet.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.PRODUCT_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(ProductMainScreenForTablet.this);
					}	
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
							dbObjSCart.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							dbObj.clearListTable();
							dbObjSCart.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
							dbObjSCart.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() == 0)){
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							//dbObj.clearListTable();
							//dbObjSCart.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}
					if(productsListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTA_ROWS_ONLY)){
						if((productsListCount == 0) && (productList.size() == 0)){
							dbObj.clearListTable();
							dbObjSCart.clearListTable();
						}
						else if((productsListCount > 0) && (productList.size() == 0)){
						}
						else if((productsListCount > 0) && (productList.size() > 0)){
							dbObj.clearListTable();
							dbObjSCart.clearListTable();
							insertProductListDataIntoDB(productList);
						}
					}					
				}
				dbObj.closeDBHelper();
				dbObjSCart.closeDBHelper();
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
						dbObjAtta01.creatTable(ProductMainScreenForTablet.this);
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
    }
    
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
    }//fn getImageUrl
    
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
    			if(tableExits){
    				if(srcCatFilterArrList != null && srcCatFilterArrList.size() > 0){
        				productList = dbObj.readSrcCatFiltersListDataFromDB(this, srcCatFilterIdArrList, DBConstants.MATKL_COLUMN_NAME);
        				if(productList != null && productList.size() > 0){
        					allSrcCatFilterArrList = (ArrayList)productList.clone();
        				}
        			}else{
        				productList = dbObj.readListDataFromDB(this);
        			}
    			}
        	
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
		}catch(Exception sggh){
			dbObjColumns.closeDBHelper();
			dbObj.closeDBHelper();
        	dbObjCatSel.closeDBHelper();
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}
    }//fn initDBConnection
    
	protected Dialog onCreateDialog( int id ){		
		try {
			if(_options != null && _options.length > 0){
				_selections =  new boolean[ _options.length ];			
				return 
				new AlertDialog.Builder( this )
			    	.setTitle( "Select Category" )
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
				    	String strValue1 = (String)((HashMap<String, String>)obj).get(DBConstants.MAKTX_COLUMN_NAME);
				        if (strValue.toLowerCase().contains(charText) || strValue1.toLowerCase().contains(charText)) {
				        	//SapGenConstants.showLog("strValue: "+strValue);
				        	productList.add(obj);
				        }
				    }
				}else{
					for (Object obj : allProductList) {
				    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.MATNR_COLUMN_NAME);
				    	String strValue1 = (String)((HashMap<String, String>)obj).get(DBConstants.MAKTX_COLUMN_NAME);
				        if (strValue.toLowerCase().contains(charText) || strValue1.toLowerCase().contains(charText)) {
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
    
    /*public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SORT_BY_PNAME, 0, "Sort by Product Name");
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SORT_BY_PNAME: {
				sortItemsAction(SORT_BY_PNAME);
		        break;
			}
	    }
		return super.onOptionsItemSelected(item);
	}*/
	
	private void sortItemsAction(String metaName){
		try{
			/*sortIndex = sortInd;
			if(sortInd == SORT_BY_PNAME)
				sortProdNameFlag = !sortProdNameFlag;	*/		
			sortProdNameFlag = !sortProdNameFlag;
			//valArrayList = DataBasePersistent.readAllValuesOrderByFromDB(this, DBConstants.DEVICE_TYPE_WIDE_OV_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, DBConstants.MAKTX_COLUMN_NAME, sortProdNameFlag);			
			
			ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
    			tableExits = dbObj.checkTable();
    			if(tableExits)
    				productList = dbObj.readListDataOrderByFromDB(this, metaName, sortProdNameFlag); 
        	
    		if(productList != null)
    			allProductList = (ArrayList)productList.clone();
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	try {   			
    		SapGenConstants.showLog("Request Code "+requestCode);
    		SapGenConstants.showLog("Result Code "+resultCode);
    		SapGenConstants.showLog("Result Code "+RESULT_OK);    		
    		if(requestCode == SapGenConstants.PRODUCT_CAT_MATT_DETAIL_SCREEN_PH){
    			if(resultCode == RESULT_OK){
    				initDBConnection();
    			}
    		}else if(requestCode == SapGenConstants.PRODUCT_CAT_SCART_SCREEN_TBL){
    			if(resultCode == RESULT_OK){
    				initDBConnection();
    			}
    		}
		} catch (Exception sfeg) {
			SapGenConstants.showErrorLog("Error in onActivityResult : "+sfeg.toString());
		}
	}
}