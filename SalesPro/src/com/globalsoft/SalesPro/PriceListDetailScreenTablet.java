package com.globalsoft.SalesPro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProPriceConstraints;
import com.globalsoft.SalesPro.Database.PriceListCP;
import com.globalsoft.SalesPro.Database.PriceListDBOperations;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class PriceListDetailScreenTablet extends Activity implements TextWatcher  {	
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5;
	private TextView tableHeaderTV6, tableHeaderTV7, tableHeaderTV8, tableHeaderTV9, tableHeaderTV10, searchLbl,custNameTV,soNoTV, myTitle;
	private EditText searchET;
	private LinearLayout searchLinear,custmorsearchlinear,headerLinear;
	private ProgressDialog pdialog = null;
	
	private SoapObject resultSoap = null;
	String SelcItem="",custName,custNumb;
	private int priceListCount = 0, offlineFlag=0;
	private boolean flag_pref = false;
	private String priceListType = "";
	final Handler pricelistData_Handler = new Handler();
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	
	private ArrayList mattArrList = new ArrayList();
	private ArrayList mattCopyArrList = new ArrayList();
	private static ArrayList idStrSap = new ArrayList();
	private static ArrayList pricereadDBList = new ArrayList();
	private static ArrayList SelectdMattList = new ArrayList();
	private static ArrayList getselctdDBListarr = new ArrayList();
	
	private String searchStr = "", title="";
	private boolean sortFlag = false, sortMattFlag = false, sortMattDescFlag = false, searchflag = true;
	private boolean sortAmtFlag = false, sortPrTypeDescFlag = false ,sortPrListDescFlag = false, sortPrTypeFlag = false, sortPerQtyFlag = false, sortPrList = false;
	private boolean selitem=false,soapConstantFunc;
	
	private int sortIndex = -1;
	private static boolean isConnAvail = false;
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7, headerWidth8, headerWidth9, headerWidth10;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3, sortHeader4=4, sortHeader5=5, sortHeader6=6, sortHeader7=7, sortHeader8=8, sortHeader9=9, sortHeader10=10;
	private int dispwidth = 300;
	
	private ArrayList selMattVector=new ArrayList();
	private ArrayList selMattArray=new ArrayList();
	//public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;
	 private About abt = new About();
	//PriceListViewfrmDatabase plDB;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SalesOrderProConstants.setWindowTitleTheme(this);
		//setTitle(R.string.SALESORDPRO_PLIST_TITLE3);
		/*setContentView(R.layout.pricelistdetail);
		title= getResources().getString(R.string.SALESORDPRO_PLIST_TITLE3);
		OfflineFunc();	*/
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.pricelistdetail); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
        myTitle = (TextView) findViewById(R.id.myTitle);
		//myTitle.setText(getResources().getString(R.string.SALESORDPRO_PLIST_TITLE1));
        int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
		SalesOrderProConstants.showLog("dispwidth : "+dispwidth);
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}	
		
        title= getResources().getString(R.string.SALESORDPRO_PLIST_TITLE3);
        myTitle.setText(title);
		//OfflineFunc();
		SelectdMattList =  (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
		custName =  (String) this.getIntent().getSerializableExtra("custname");
		custNumb =  (String) this.getIntent().getSerializableExtra("custnumb");
		
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		
		SharedPreferences settings = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);      
		flag_pref = settings.getBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, false);			
		isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(PriceListDetailScreenTablet.this);
		Log.e("info","isConnAvail: "+isConnAvail);
		
		if(isConnAvail!=false){
			// selMattVector = (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
			if(SelectdMattList != null && SelectdMattList.size() > 0){
				SalesOrderProConstants.showLog("Size : "+SelectdMattList.size());	        	
				initPriceListSoapConnection();	        	
			}
		}
		else{	        	
			Log.e("info","size of selMattArray : "+SelectdMattList.size());
			int size=SelectdMattList.size();
			for(int i=0;i<size;i++){	        		
				SelcItem=SelectdMattList.get(i).toString();	        		
				selMattVector=PriceListDBOperations.readAllSelctdIdDataFromDB(this,SelectdMattList);	        		
			}
			if(selMattVector.size()==0){
				SalesOrderProConstants.showErrorDialog(PriceListDetailScreenTablet.this, "Data Not available in Offline mode");
			}
		}
		initLayout();			
	}//onCreate
	
	public void onBackPressed() {
		this.finish();			
	}//fn onBackPressed
				
	private void initLayout(){
		try{
			setContentView(R.layout.pricelistdetail);
			dispwidth = SalesOrderProConstants.getDisplayWidth(this);
			
			searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
																			
			headerLinear = (LinearLayout) findViewById(R.id.sorditem_srlheader_linear);				
			custNameTV = (TextView)findViewById(R.id.custNameTV);			
			//soNoTV = (TextView)findViewById(R.id.soNoTV);		
			
			if(custName.length()==0 && custNumb.length()==0)					
				headerLinear.setVisibility(View.GONE);
			else{
				if(custName!=null || custNumb!=null){
					custNameTV.setText(" "+custName+" "+"("+custNumb+")");
					//soNoTV.setText(" "+custNumb);
				}
			}				
			tableHeaderTV1 = (TextView)findViewById(R.id.TableHeaderTV1);
			tableHeaderTV1.setGravity(Gravity.LEFT);
			tableHeaderTV1.setPadding(10,5,5,5);
			tableHeaderTV1.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader1);
				}
			});
			
			tableHeaderTV2 = (TextView)findViewById(R.id.TableHeaderTV2);
			tableHeaderTV2.setGravity(Gravity.RIGHT);
			tableHeaderTV2.setPadding(5,5,10,5);
			tableHeaderTV2.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader2);
				}
			});
			
			tableHeaderTV3 = (TextView)findViewById(R.id.TableHeaderTV3);
			tableHeaderTV3.setGravity(Gravity.LEFT);
			tableHeaderTV3.setPadding(10,5,5,5);
			
			
			tableHeaderTV4 = (TextView)findViewById(R.id.TableHeaderTV4);
			tableHeaderTV4.setGravity(Gravity.RIGHT);
			tableHeaderTV4.setPadding(5,5,10,5);
			tableHeaderTV4.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader4);
				}
			});
			
			tableHeaderTV5 =(TextView)findViewById(R.id.TableHeaderTV5);
			tableHeaderTV5.setGravity(Gravity.LEFT);
			tableHeaderTV5.setPadding(10,5,5,5);
			
			tableHeaderTV6 =(TextView)findViewById(R.id.TableHeaderTV6);
			tableHeaderTV6.setGravity(Gravity.LEFT);
			tableHeaderTV6.setPadding(10,5,5,5);
			tableHeaderTV6.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader6);
				}
			});
			
			tableHeaderTV7  =(TextView)findViewById(R.id.TableHeaderTV7);
			tableHeaderTV7.setGravity(Gravity.LEFT);
			tableHeaderTV7.setPadding(10,5,5,5);
			tableHeaderTV7.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader7);
				}
			});
			
			tableHeaderTV8 = (TextView)findViewById(R.id.TableHeaderTV8);
			tableHeaderTV8.setGravity(Gravity.LEFT);
			tableHeaderTV8.setPadding(10,5,5,5);
			tableHeaderTV8.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader8);
				}
			});
			
			tableHeaderTV9 =(TextView)findViewById(R.id.TableHeaderTV9);
			tableHeaderTV9.setGravity(Gravity.LEFT);
			tableHeaderTV9.setPadding(10,5,5,5);
			tableHeaderTV9.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader9);
				}
			});
			
			tableHeaderTV10 =(TextView)findViewById(R.id.TableHeaderTV10);
			tableHeaderTV10.setGravity(Gravity.LEFT);
			tableHeaderTV10.setPadding(10,5,5,5);
			tableHeaderTV10.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader10);
				}
			});
	
			
			ViewTreeObserver vto1 = tableHeaderTV1.getViewTreeObserver();
			vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV1.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth1 = tableHeaderTV1.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV1 Width1 : "+headerWidth1+" : "+tableHeaderTV1.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto2 = tableHeaderTV2.getViewTreeObserver();
			vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV2.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth2 = tableHeaderTV2.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV2 Width1 : "+headerWidth2+" : "+tableHeaderTV2.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto3 = tableHeaderTV3.getViewTreeObserver();
			vto3.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV3.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth3 = tableHeaderTV3.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV3 Width1 : "+headerWidth3+" : "+tableHeaderTV3.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto4 = tableHeaderTV4.getViewTreeObserver();
			vto4.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV4.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth4 = tableHeaderTV4.getWidth();
					/*
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						headerWidth4 = headerWidth4+30;
						TableRow.LayoutParams linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						linparams11.width = headerWidth4;
						tableHeaderTV4.setLayoutParams(linparams11);
					}
					*/
					SalesOrderProConstants.showLog("tableHeaderTV4 Width1 : "+headerWidth4+" : "+tableHeaderTV4.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto5 = tableHeaderTV5.getViewTreeObserver();
			vto5.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV5.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth5 = tableHeaderTV5.getWidth();
					/*
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						headerWidth5 = headerWidth5+20;
						TableRow.LayoutParams linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						linparams11.width = headerWidth5;
						tableHeaderTV5.setLayoutParams(linparams11);
					}
					*/
					SalesOrderProConstants.showLog("tableHeaderTV5 Width1 : "+headerWidth5+" : "+tableHeaderTV5.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto6 = tableHeaderTV6.getViewTreeObserver();
			vto6.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV6.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth6 = tableHeaderTV6.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV6 Width1 : "+headerWidth6+" : "+tableHeaderTV6.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto7 = tableHeaderTV7.getViewTreeObserver();
			vto7.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV7.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth7 = tableHeaderTV7.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV7 Width1 : "+headerWidth7+" : "+tableHeaderTV7.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto8 = tableHeaderTV8.getViewTreeObserver();
			vto8.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV8.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth8 = tableHeaderTV8.getWidth();
					/*
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						headerWidth8 = headerWidth8+100;
						TableRow.LayoutParams linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						linparams11.width = headerWidth8;
						tableHeaderTV8.setLayoutParams(linparams11);
					}
					*/
					SalesOrderProConstants.showLog("tableHeaderTV8 Width1 : "+headerWidth8+" : "+tableHeaderTV8.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto9 = tableHeaderTV9.getViewTreeObserver();
			vto9.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV9.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth9 = tableHeaderTV9.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV9 Width1 : "+headerWidth9+" : "+tableHeaderTV9.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto10 = tableHeaderTV10.getViewTreeObserver();
			vto10.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV10.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth10 = tableHeaderTV10.getWidth();
					SalesOrderProConstants.showLog("tableHeaderTV10 Width1 : "+headerWidth10+" : "+tableHeaderTV10.getMeasuredWidth());
					drawSubLayout();
				}
			});
			
			
			searchET.setFocusable(true);
			searchET.setFocusableInTouchMode(true);
			searchET.requestFocus();
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
	
	private void drawSubLayout(){
		try{
			TableLayout tl = (TableLayout)findViewById(R.id.pricelisttbllayout2);
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			//if(mattArrList != null)
			if(selMattVector != null){
				SalesOrdProPriceConstraints stkpriceCategory = null;
				String matterialStr = "", rateAmtStr = "", rateunitStr = "";
				String mattDescStr = "", condPrcUnitStr = "", condUnitStr = "";
				String condTypeStr = "", priceListTypeStr = "", kschlStr = "", pltypStr = "";
				
				int rowSize = selMattVector.size();                
				SalesOrderProConstants.showLog("Stocks List Size  : "+rowSize);
				
				for (int i =0; i < selMattVector.size(); i++) {
					stkpriceCategory = ((SalesOrdProPriceConstraints)selMattVector.get(i));
					if(stkpriceCategory != null){
						matterialStr = stkpriceCategory.getMaterialNo().trim();
						rateAmtStr = stkpriceCategory.getRateAmount().trim();
						rateunitStr = stkpriceCategory.getRateunit().trim();                                
						mattDescStr = stkpriceCategory.getMattDesc().trim();
						condPrcUnitStr = stkpriceCategory.getCondPricingUnit().trim();
						condUnitStr = stkpriceCategory.getConditionUnit().trim();        
						condTypeStr = stkpriceCategory.getConditionType();
						priceListTypeStr = stkpriceCategory.getPriceListType();                        
						kschlStr = stkpriceCategory.getKSCHLText();    
						pltypStr = stkpriceCategory.getPLTYPText();
						
						SalesOrderProConstants.showLog("matterialStr : "+matterialStr);
						SalesOrderProConstants.showLog("rateAmtStr : "+rateAmtStr);
						SalesOrderProConstants.showLog("rateunitStr : "+rateunitStr);
						SalesOrderProConstants.showLog("mattDescStr : "+mattDescStr);
						SalesOrderProConstants.showLog("condPrcUnitStr : "+condPrcUnitStr);
						SalesOrderProConstants.showLog("condUnitStr : "+condUnitStr);
						SalesOrderProConstants.showLog("condTypeStr : "+condTypeStr);
						SalesOrderProConstants.showLog("priceListTypeStr : "+priceListTypeStr);
						SalesOrderProConstants.showLog("kschlStr : "+kschlStr);
						SalesOrderProConstants.showLog("pltypStr : "+pltypStr);
													
						tr = new TableRow(this);
					   
						TextView matdescTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						matdescTxtView.setText(mattDescStr);
						matdescTxtView.setWidth(headerWidth1);
						matdescTxtView.setGravity(Gravity.LEFT);
						matdescTxtView.setPadding(10,0,0,0);
						
						TextView rateAmtTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						rateAmtTxtView.setText(rateAmtStr);
						rateAmtTxtView.setWidth(headerWidth2);
						rateAmtTxtView.setGravity(Gravity.RIGHT);	    					
						rateAmtTxtView.setPadding(0,0,10,0);
						
						TextView rateUnitTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						rateUnitTxtView.setText(rateunitStr);
						rateUnitTxtView.setWidth(headerWidth3);    
						rateUnitTxtView.setGravity(Gravity.LEFT);
						rateUnitTxtView.setPadding(10,0,0,0);
						
						TextView condPrcUnitTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);					
						condPrcUnitTxtView.setText(condPrcUnitStr);
						condPrcUnitTxtView.setWidth(headerWidth4);
						condPrcUnitTxtView.setGravity(Gravity.RIGHT);
						condPrcUnitTxtView.setPadding(0,0,10,0);
						
						TextView condUnitTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						condUnitTxtView.setText(condUnitStr);
						condUnitTxtView.setWidth(headerWidth5);
						condUnitTxtView.setGravity(Gravity.LEFT);
						condUnitTxtView.setPadding(10,0,0,0);

						TextView mattTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						mattTxtView.setText(matterialStr);
						mattTxtView.setWidth(headerWidth6);
						mattTxtView.setGravity(Gravity.LEFT);
						mattTxtView.setPadding(10,0,0,0);
						
						TextView kschlTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						kschlTxtView.setText(kschlStr);		
						kschlTxtView.setWidth(headerWidth7);
						kschlTxtView.setGravity(Gravity.LEFT);
						kschlTxtView.setPadding(10,0,0,0);
						
						TextView pltypTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						pltypTxtView.setText(pltypStr);		
						pltypTxtView.setWidth(headerWidth8);    
						pltypTxtView.setGravity(Gravity.LEFT);
						pltypTxtView.setPadding(10,0,0,0);

						TextView condTypeTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						condTypeTxtView.setText(condTypeStr);
						condTypeTxtView.setWidth(headerWidth9);
						condTypeTxtView.setGravity(Gravity.LEFT);
						condTypeTxtView.setPadding(10,0,0,0);
						
						TextView priceListTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						priceListTxtView.setText(priceListTypeStr);		
						priceListTxtView.setWidth(headerWidth10);	
						priceListTxtView.setGravity(Gravity.LEFT);
						priceListTxtView.setPadding(10,0,0,0);
						
						if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
							matdescTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							rateAmtTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							rateUnitTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							condPrcUnitTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							condUnitTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							mattTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							kschlTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							pltypTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							condTypeTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							priceListTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
						}						
						tr.addView(matdescTxtView);
						tr.addView(rateAmtTxtView);
						tr.addView(rateUnitTxtView);
						tr.addView(condPrcUnitTxtView);
						tr.addView(condUnitTxtView);
						tr.addView(mattTxtView);
						tr.addView(kschlTxtView);
						tr.addView(pltypTxtView);
						tr.addView(condTypeTxtView);
						tr.addView(priceListTxtView);
						
						tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					}					
				}
			}
		}
		catch(Exception asgf){
			SalesOrderProConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
	
	private void initPriceListSoapConnection(){        
		SoapSerializationEnvelope envelopeC = null;
		try{
			SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
			envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			
			SalesOrdProIpConstraints C0[];
			C0 = new SalesOrdProIpConstraints[3];
			
			for(int i=0; i<C0.length; i++){
				C0[i] = new SalesOrdProIpConstraints(); 
			}	            
			
			C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
		   // C0[0].Cdata = "DEVICE-ID:100000000000000:DEVICE-TYPE:BB:APPLICATION-ID:SALESPRO";
			C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
			C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
			//C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
		   /* arraysize = CheckDBValue();
			SalesOrderProConstants.showLog("arraysize from database : "+arraysize);
			if(arraysize==0){           	
				 C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			}else{				
				 C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
			}*/
			Vector listVect = new Vector();
			for(int k=0; k<C0.length; k++){
				listVect.addElement(C0[k]);
			}
		   
			SalesOrdProIpConstraints C1;
			SalesOrdProIpConstraints C2;
			SalesOrdProMattConstraints catgry=null;
			for(int i=0; i<SelectdMattList.size(); i++){
				C1 = new SalesOrdProIpConstraints();
				catgry=(SalesOrdProMattConstraints)SelectdMattList.get(i);
				String matno=catgry.getMaterialNo().toString();
				C1.Cdata = "MD_T_MATNR[.]"+matno;	            	
				listVect.addElement(C1);
			}
			C2 = new SalesOrdProIpConstraints();
			C2.Cdata = "CVIS_CUSTOMER_T[.]"+custNumb;
			listVect.addElement(C2);
			request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
			envelopeC.setOutputSoapObject(request);                    
			SalesOrderProConstants.showLog(request.toString());

			doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
			//startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
		}
		catch(Exception asd){
			SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
		}
	}//fn initPriceListSoapConnection
	
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
						pdialog = ProgressDialog.show(PriceListDetailScreenTablet.this, "", getString(R.string.COMPILE_DATA),true);
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
	
	/*public void OfflineFunc(){
    	if(offlineFlag==1){
    		SapGenConstants.checkInternetConnection(this, myTitle,title,resultSoap);	
    	}else{
    		SapGenConstants.checkInternetConnection(this,myTitle,title);
    	}    	  		
    }//OfflineFunc
*/	
	private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
		try {
			if(pdialog != null)
				pdialog = null;			
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
					public void run() {
						//pdialog = ProgressDialog.show(ctxAct, "", "Please wait while processing...",true);
						pdialog = ProgressDialog.show(ctxAct, "", "Contacting Server...",true);
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
			SalesOrderProConstants.showErrorLog(ae.toString());
		}
	}//fn startNetworkConnection
	
	private void getSOAPViaHTTP(SoapSerializationEnvelope envelopeCE, String url){		
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
					updateServerResponse(resultSoap);
				}
			});
		}
	}//fn getSOAPViaHTTP
	
	public void updateServerResponse(SoapObject soap){		
		SalesOrdProPriceConstraints mattCategory = null;
		try{ 
			if(soap != null){  
				emptyAllVectors();
				
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
											if(docTypeStr.equalsIgnoreCase("ZGSEVKST_MTRLPRICE10")){
												//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
												priceListCount = Integer.parseInt(rowCount);
												priceListType = respType;
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
								
								
								if(docTypeStr.equalsIgnoreCase("ZGSEVKST_MTRLPRICE10")){
									if(mattCategory != null)
										mattCategory = null;
										
									mattCategory = new SalesOrdProPriceConstraints(resArray);
									if(selMattVector != null)
										selMattVector.add(mattCategory);	  
									
									if(mattCopyArrList != null)
										mattCopyArrList.add(mattCategory);
								}
								
							}
							else if(j == 0){
								String errorMsg = pii.getProperty(j).toString();
								//ServiceProConstants.showLog("Inside J == 0 ");
								int errorFstIndex = errorMsg.indexOf("Message=");
								if(errorFstIndex > 0){
									int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
									final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
									SalesOrderProConstants.showLog(taskErrorMsgStr);this.runOnUiThread(new Runnable() {
										public void run() {
											SalesOrderConstants.showErrorDialog(PriceListDetailScreenTablet.this, taskErrorMsgStr);
										}
									});
								}
							}
						}
					}
				}
			}
			else{
				initDBConnection();
			}
		}
		catch(Exception sff){
			SalesOrderProConstants.showErrorLog("On updateTaskResponse : "+sff.toString());
		} 
		finally{
			SalesOrderProConstants.showLog("Stocks List Size : "+selMattVector.size());	        	
			try {								
				if(selMattVector != null){
					if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((priceListCount == 0) && (selMattVector.size() == 0)){
							PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
						}
						else if((priceListCount > 0) && (selMattVector.size() > 0)){
							deleteSelctdDatafromDB();
							//insertSelcdDataIntoDB();
						}
					}
					if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
												
						if((priceListCount == 0) && (selMattVector.size() == 0)){
							PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
						}
						else if((priceListCount > 0) && (selMattVector.size() == 0)){
							
						}
						else if((priceListCount > 0) && (selMattVector.size() > 0)){
							deleteSelctdDatafromDB();
						}
					}
				}				
				/*if(selMattVector.size() > 0){
					SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK_DETAIL, 0);    
					SharedPreferences.Editor editor = sharedPreferences.edit();    
					editor.putBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, true);    
					editor.commit();
				}*/
				
			} catch (Exception esf) {
				SalesOrderProConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			}
			stopProgressDialog();
			PriceListDetailScreenTablet.this.runOnUiThread(new Runnable() {
				public void run() {
					initDBConnection();
				}
			}); 
			//initLayout();
		}
	}//fn updateReportsConfirmResponse     

	private void deleteSelctdDatafromDB() {
		try {
			deleteSelctdData();
			insertSelcdDataIntoDB();
					
		}   catch(Exception adsf1){
			SalesOrderProConstants.showErrorLog("On deleteSelctdDatafromDB : "+adsf1.toString());
		 }
		stopProgressDialog();
		
	}//deleteSelctdDatafromDB

	public void deleteSelctdData(){
		try{
			SalesOrdProPriceConstraints matObj = null;
			boolean matchStr=false;
			if(idStrSap.size()>0){
				idStrSap.clear();
			}
			if(pricereadDBList.size()>0){
				pricereadDBList.clear();
			}
			pricereadDBList=PriceListDBOperations.readAllSelctdidDataFromDB(this);
			for(int i=0;i<selMattVector.size();i++){
				matObj=(SalesOrdProPriceConstraints)selMattVector.get(i);
				if(matObj!=null)
					idStrSap.add(matObj.getMaterialNo().trim());
			}
			if((selMattVector.size())>0 && (pricereadDBList.size())>0){
				getselctdDBListarr=PriceListDBOperations.getDBselctdIdlist(this);
				SalesOrderProConstants.showLog("get selected DBListarr:"+getselctdDBListarr.size());
				if(getselctdDBListarr.size()>0){
					for(int i=0;i<idStrSap.size();i++){
						matchStr = getselctdDBListarr.contains(idStrSap.get(i).toString().trim());
						if(matchStr==true){
							PriceListDBOperations.deleteIdselctdTableDataFromDB(this,PriceListCP.PL_SEL_CONTENT_URI,idStrSap.get(i).toString().trim());
							SalesOrderProConstants.showLog("selectd  ID from SAP : "+idStrSap.get(i).toString().trim());
						}
						SalesOrderProConstants.showLog(" Selected matchStr : "+matchStr);
					}
				}
			}
		} catch (Exception e) {
			SalesOrderProConstants.showErrorLog("Error in gettingContactsCall Thread:"+e.toString());
		}
	}

	/*final Runnable insertSelcdDataIntoDB = new Runnable(){
		public void run()
		{
			try{					
				insertSelcdDataIntoDB();
			} catch(Exception sfe){
				SalesOrderProConstants.showErrorLog("Error in insertSerchdDataIntoDB:"+sfe.toString());
			}
		}	    
	};
		   */
	
	private void initDBConnection() {
		try{
			selMattVector=PriceListDBOperations.readAllSelctdIdDataFromDB(this,SelectdMattList);
			if(selMattVector != null)
				mattCopyArrList = (ArrayList)selMattVector.clone();
			else
				SalesOrderProConstants.showErrorDialog(PriceListDetailScreenTablet.this, "Data Not available in Offline mode");
			
			Collections.sort(selMattVector, priceSortComparator); 
			
		}catch (Exception sse) {
			SalesOrderProConstants.showErrorLog("Error on initDBConnection: "+sse.toString());
		}
		finally{
			try {
				initLayout();
			} catch (Exception e) {}
		}
		
	}
	
	private void insertSelcdDataIntoDB() {
	   SalesOrdProPriceConstraints stkpriceCategory;
		try {
			if(selMattVector != null){
				for(int k=0; k<selMattVector.size(); k++){
					stkpriceCategory = (SalesOrdProPriceConstraints) selMattVector.get(k);
					if(stkpriceCategory != null){
						PriceListDBOperations.insertselctdListDataInToDB(this, stkpriceCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SalesOrderProConstants.showErrorLog("Error On insertSelcdDataIntoDB: "+ewe.toString());
		}
	}//fn insertSelcdDataIntoDB
	
	private void emptyAllVectors(){
		try{
			searchStr = "";	            
			if(selMattVector != null)
				selMattVector.clear();
			
			if(mattCopyArrList != null)
				mattCopyArrList.clear();
		}
		catch(Exception adsf){
			SalesOrderProConstants.showErrorLog("On emptyAllVectors : "+adsf.toString());
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
			SalesOrderProConstants.showErrorLog(ce.toString());
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
		  SalesOrderProConstants.showLog("Text : "+s.toString());
		  searchItemsAction(s.toString());
	} 
	
	private void searchItemsAction(String match){  
		try{
			searchflag = true;           
			searchStr = match;
			SalesOrdProPriceConstraints stkObj = null;
			String mattStr = "", mattDescStr = "";
			if((mattCopyArrList != null) && (mattCopyArrList.size() > 0)){
				if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){                                            
					System.out.println("Match : "+match);  
				 
					selMattVector.clear();
					for(int i = 0; i < mattCopyArrList.size(); i++){  
						stkObj = null;
						mattStr = "";
						mattDescStr = "";
						stkObj = (SalesOrdProPriceConstraints)mattCopyArrList.get(i);
						if(stkObj != null){
							mattStr = stkObj.getMaterialNo().trim().toLowerCase();
							mattDescStr = stkObj.getMattDesc().trim().toLowerCase();
							match = match.toLowerCase();
							if((mattStr.indexOf(match) >= 0) || (mattDescStr.indexOf(match) >= 0)){
								selMattVector.add(stkObj);
							}
						}
					}//for 
					initLayout();
					//searchET.setText(searchStr);
				}
				else{
					System.out.println("Match is empty");
					selMattVector.clear();
					for(int i = 0; i < mattCopyArrList.size(); i++){  
						stkObj = (SalesOrdProPriceConstraints)mattCopyArrList.get(i);
						if(stkObj != null){
							selMattVector.add(stkObj);
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
			SalesOrderProConstants.showErrorLog("Error On searchItemsAction : "+we.toString());
		}
	}//fn searchItemsAction  	
	
	private void sortItemsAction(int sortInd){
		try{
			 sortFlag = true;
			 sortIndex = sortInd;			 
			 if(sortInd == sortHeader1)
				 sortMattDescFlag = !sortMattDescFlag;
			 else if(sortInd == sortHeader2)
				 sortAmtFlag = !sortAmtFlag;
			 else if(sortInd == sortHeader4)
				 sortPerQtyFlag = !sortPerQtyFlag;
			 else if(sortInd == sortHeader6)
				 sortMattFlag = !sortMattFlag;
			 else if(sortInd == sortHeader7)
				 sortPrTypeDescFlag = !sortPrTypeDescFlag;
			 else if(sortInd == sortHeader8)
				 sortPrListDescFlag = !sortPrListDescFlag;
			 else if(sortInd == sortHeader9)
				 sortPrTypeFlag = !sortPrTypeFlag;
			 else if(sortInd == sortHeader10)
				 sortPrList = !sortPrList;
			 
			 SalesOrderProConstants.showLog("Selected Sort Index : "+sortInd);
			 Collections.sort(selMattVector, priceSortComparator); 				
			 initLayout();
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	private final Comparator priceSortComparator =  new Comparator() {
		public int compare(Object o1, Object o2){ 
			int comp = 0;
			double rateAmt1=0, rateAmt2=0;
			String strObj1 = "0", strObj2="0";
			SalesOrdProPriceConstraints repOPObj1, repOPObj2;
			try{            	
				if (o1 == null || o2 == null){
				}
				else{            
					repOPObj1 = (SalesOrdProPriceConstraints)o1;
					repOPObj2 = (SalesOrdProPriceConstraints)o2;
					
					if(sortIndex == sortHeader6){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getMaterialNo().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getMaterialNo().trim();
						
						if(sortMattFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader1){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getMattDesc().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getMattDesc().trim();
						
						if(sortMattDescFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader2){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getRateAmount().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getRateAmount().trim();
						
						if(!strObj1.equalsIgnoreCase(""))
							rateAmt1 = Double.parseDouble(strObj1);
							
						if(!strObj2.equalsIgnoreCase(""))
							rateAmt2 = Double.parseDouble(strObj2);
							
						if(sortAmtFlag == true)
							comp = (int) (rateAmt1-rateAmt2);
						else
							comp = (int) (rateAmt2-rateAmt1);
					}
					else if(sortIndex == sortHeader4){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getCondPricingUnit().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getCondPricingUnit().trim();
						
						if(sortPerQtyFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader7){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getKSCHLText().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getKSCHLText().trim();
						
						if(sortPrTypeDescFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader8){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getPLTYPText().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getPLTYPText().trim();
						
						if(sortPrListDescFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader9){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getConditionType().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getConditionType().trim();
						
						if(sortPrTypeFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader10){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getPriceListType().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getPriceListType().trim();
						
						if(sortPrList == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					/*else{
						// Code to sort by Material Desc (default)
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getMattDesc().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getMattDesc().trim();
						
						if(sortMattDescFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}*/
				}
			 }
			 catch(Exception qw){
				 SalesOrderProConstants.showErrorLog("Error in Serv Order Comparator : "+qw.toString());
			 }
				 
			 if (comp != 0) {            
				return comp;
			 } 
			 else {            
				return 0;
			}
		}
	};

}//End of class PriceListDetailScreen