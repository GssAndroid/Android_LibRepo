package com.globalsoft.SalesPro;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProStockConstraints;
import com.globalsoft.SalesPro.Database.InventoryDBOperations;
import com.globalsoft.SalesPro.Database.SalesProInvntryCP;
import com.globalsoft.SalesPro.StockListActivity.SOCustomerListAdapter;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StockListDetailScreenTablet  extends Activity implements TextWatcher  {
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5;
	private TextView tableHeaderTV6, tableHeaderTV7, tableHeaderTV8, tableHeaderTV9, tableHeaderTV10;
	private TextView myTitle;
	private EditText searchET;
	private ProgressDialog pdialog = null;
	private TableRow.LayoutParams linparams11 = null;
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();	
	
	private SoapObject resultSoap = null;
	
	private ArrayList stockArrList = new ArrayList();
	private ArrayList SelectedstockArrList = new ArrayList();
	private ArrayList mattCopyArrList = new ArrayList();
	ArrayList diogList =new ArrayList();
	private boolean isConnAvail = false;
	final Handler inventoryData_Handler = new Handler();
	private static ArrayList getselctdDBListarr = new ArrayList();
	private static ArrayList idStrSap = new ArrayList();
	private static ArrayList invntryreadDBList = new ArrayList();
	private SOCustomerListAdapter SOCustomerListAdapter;
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	
	private String searchStr = "", title="", linkText=" ";
	private boolean sortFlag = false, sortMattFlag = false, sortMattDescFlag = false, searchflag = true, sortSLocDesFlag=false, sortPlantFlag=false;
	private boolean flag_pref = false ,soapConstantFunc;			    
	private boolean sortPlantDesFlag = false, sortUnitMeasureFlag = false, sortTransitFlag = false, sortInspFlag = false, sortstockFlag=false, sortSLocFlag=false;
	
	private int sortIndex = -1;
	private int priceListCount = 0, offlineFlag=0;
	private String priceListType = "";
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7, headerWidth8, headerWidth9, headerWidth10;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3, sortHeader4=4, sortHeader5=5, sortHeader6=6, sortHeader7=7, sortHeader8=8, sortHeader9=9, sortHeader10=10;
	private int dispwidth = 300;
	private About abt = new About();
	//public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;	
	//private ArrayList selMattVector;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SalesOrderProConstants.setWindowTitleTheme(this);
		//setTitle(R.string.SALESORDPRO_STKLISTDET_TITLE);		
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.stocklistdetail); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		myTitle = (TextView) findViewById(R.id.myTitle);	
			 
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		SharedPreferences settings = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_DETAIL_STOCK, 0);      
		flag_pref = settings.getBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_DETAIL_FOR_MYSELF_GET, false);		
		
		title= getResources().getString(R.string.SALESORDPRO_STKLISTDET_TITLE);
		myTitle.setText(title);
		//OfflineFunc();
		
		SelectedstockArrList = (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
		isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(StockListDetailScreenTablet.this);
		Log.e("info","isConnAvail: "+isConnAvail);
		
		if(isConnAvail!=false){
			//stockArrList = (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
			if(SelectedstockArrList != null && SelectedstockArrList.size() > 0){
				SalesOrderProConstants.showLog("Size : "+SelectedstockArrList.size());		        	
				initPriceListSoapConnection();
				
			}
		}
		else{
			//stockArrList=InventoryDBOperations.readAllSelctdDataFromDB(StockListDetailScreen.this);
			//Log.e("info","size of selMattArray : "+stockArrList.size());
			Log.e("info","size of selMattArray : "+stockArrList.size());
			int size=SelectedstockArrList.size();
		
			stockArrList=InventoryDBOperations.readAllSelctdIDDataFromDB(this,SelectedstockArrList);
			Log.e("info","size of selMattArray DB read : "+stockArrList.size());
			if(stockArrList.size()==0){
				SalesOrderProConstants.showErrorDialog(StockListDetailScreenTablet.this, "Data Not available in Offline mode");
			}
		}
		initLayout();
	}//onCreate
			   
	private void initLayout(){
		try{
			setContentView(R.layout.stocklistdetail);			
			dispwidth = SalesOrderProConstants.getDisplayWidth(this);			
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
			tableHeaderTV3.setGravity(Gravity.RIGHT);
			tableHeaderTV3.setPadding(5,5,10,5);
			tableHeaderTV3.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader3);
				}
			});
			
			tableHeaderTV4 = (TextView)findViewById(R.id.TableHeaderTV4);
			tableHeaderTV4.setGravity(Gravity.RIGHT);
			tableHeaderTV4.setPadding(5,5,10,5);
			tableHeaderTV4.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader4);
				}
			});
			
			tableHeaderTV5 =(TextView)findViewById(R.id.TableHeaderTV5);
			tableHeaderTV5.setGravity(Gravity.RIGHT);
			tableHeaderTV5.setPadding(5,5,10,5);
			tableHeaderTV5.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader5);
				}
			});
			
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
			tableHeaderTV9.setGravity(Gravity.RIGHT);
			tableHeaderTV9.setPadding(5,5,10,5);
			tableHeaderTV9.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader9);
				}
			});
			
			tableHeaderTV10 =(TextView)findViewById(R.id.TableHeaderTV10);
			tableHeaderTV10.setGravity(Gravity.RIGHT);
			tableHeaderTV10.setPadding(5,5,10,5);
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
					/*
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						headerWidth1 = headerWidth1+30;	    
						linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						linparams11.width = headerWidth1;
						tableHeaderTV1.setLayoutParams(linparams11);
					}
					*/
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
					SalesOrderProConstants.showLog("tableHeaderTV4 Width1 : "+headerWidth4+" : "+tableHeaderTV4.getMeasuredWidth());
				}
			});
			
			ViewTreeObserver vto5 = tableHeaderTV5.getViewTreeObserver();
			vto5.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					ViewTreeObserver obs = tableHeaderTV5.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					headerWidth5 = tableHeaderTV5.getWidth();
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
					/*
					if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
						headerWidth7 = headerWidth7+50;	    	
						linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						linparams11.width = headerWidth7;
						tableHeaderTV7.setLayoutParams(linparams11);
					}
					*/
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
						headerWidth8 = headerWidth8+60;	    	
						linparams11 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
			TableLayout tl = (TableLayout)findViewById(R.id.stocklisttbllayout2);
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			
			if(stockArrList != null){
				SalesOrdProStockConstraints stkCategory = null;
				String matterialStr = "", plantDescStr = "", unitMeasureStr = "";
				String mattDescStr = "", stockStr = "", inTransitStr = "";
				String inInspcStr = "", sLocDescStr = "", plantStr = "", sLocStr = "";
				
				int rowSize = stockArrList.size();                
				SalesOrderProConstants.showLog("Stocks List Size  : "+rowSize);
				
				for (int i =0; i < stockArrList.size(); i++) {
					stkCategory = (SalesOrdProStockConstraints)stockArrList.get(i);
					if(stkCategory != null){
						matterialStr = stkCategory.getMaterialNo().trim();
						plantDescStr = stkCategory.getPlantDesc().trim();
						unitMeasureStr = stkCategory.getMeasureUnit().trim();                                
						mattDescStr = stkCategory.getMattDesc().trim();
						stockStr = stkCategory.getStock().trim();
						inTransitStr = stkCategory.getStockInTransfer().trim();        
						inInspcStr = stkCategory.getStockInQualityInsp().trim();
						sLocDescStr = stkCategory.getStorageLocationDesc().trim();                        
						plantStr = stkCategory.getPlant().trim();    
						sLocStr = stkCategory.getStorageLocation().trim();
						
						tr = new TableRow(this);
						
						TextView matdescTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						matdescTxtView.setText(mattDescStr);
						matdescTxtView.setWidth(headerWidth1);
						matdescTxtView.setGravity(Gravity.LEFT);
						matdescTxtView.setPadding(10,0,0,0);
						
						TextView unitMeasureTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						unitMeasureTxtView.setText(unitMeasureStr);
						unitMeasureTxtView.setWidth(headerWidth2);
						unitMeasureTxtView.setGravity(Gravity.LEFT);
						unitMeasureTxtView.setPadding(10,0,0,0);
						
						TextView stockTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);					
						stockTxtView.setText(stockStr);
						stockTxtView.setWidth(headerWidth3);
						stockTxtView.setGravity(Gravity.RIGHT);
						stockTxtView.setPadding(0,0,10,0);
						
						TextView inTransitTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						inTransitTxtView.setText(inTransitStr);
						inTransitTxtView.setWidth(headerWidth4);
						inTransitTxtView.setGravity(Gravity.RIGHT);
						inTransitTxtView.setPadding(0,0,10,0);
						
						TextView inInspTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						inInspTxtView.setText(inInspcStr);
						inInspTxtView.setWidth(headerWidth5);
						inInspTxtView.setGravity(Gravity.RIGHT);
						inInspTxtView.setPadding(0,0,10,0);
						
						TextView mattTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						mattTxtView.setText(matterialStr);
						mattTxtView.setWidth(headerWidth6);
						mattTxtView.setGravity(Gravity.LEFT);
						mattTxtView.setPadding(10,0,0,0);
						
						TextView plantDescTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						plantDescTxtView.setText(plantDescStr);
						plantDescTxtView.setWidth(headerWidth7);
						plantDescTxtView.setGravity(Gravity.LEFT);
						plantDescTxtView.setPadding(10,0,0,0);
						
						TextView sLocDescTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						sLocDescTxtView.setText(sLocDescStr);		
						sLocDescTxtView.setWidth(headerWidth8);
						sLocDescTxtView.setGravity(Gravity.LEFT);
						sLocDescTxtView.setPadding(10,0,0,0);
						
						TextView plantTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						plantTxtView.setText(plantStr);		
						plantTxtView.setWidth(headerWidth9);
						plantTxtView.setGravity(Gravity.RIGHT);
						plantTxtView.setPadding(0,0,10,0);
						
						TextView sLocTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
						sLocTxtView.setText(sLocStr);		
						sLocTxtView.setWidth(headerWidth10);
						sLocTxtView.setGravity(Gravity.RIGHT);
						sLocTxtView.setPadding(0,0,10,0);
						
						if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
							matdescTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							unitMeasureTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							stockTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							inTransitTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							inInspTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							mattTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							plantDescTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							sLocDescTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							plantTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
							sLocTxtView.setTextSize(SalesOrderProConstants.TEXT_SIZE_TABLE_ROW);
						}
						
						tr.addView(matdescTxtView);
						tr.addView(unitMeasureTxtView);
						tr.addView(stockTxtView);
						tr.addView(inTransitTxtView);
						tr.addView(inInspTxtView);
						tr.addView(mattTxtView);
						tr.addView(plantDescTxtView);
						tr.addView(sLocDescTxtView);
						tr.addView(plantTxtView);
						tr.addView(sLocTxtView);
						
						tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					}					
				}
			}
		}
		catch(Exception asgf){
			SalesOrderProConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout  
	
	public String getDateTime(){
		 String soapendtimeStr = "";
		try{			
			DateFormat dateFormat3 = new SimpleDateFormat("yyyyMMdd HHmmss");	                        				   
			Calendar cal3 = Calendar.getInstance();	                        				 
			SapGenConstants.showLog(" current date : "+dateFormat3.format(cal3.getTime()));
		    soapendtimeStr = dateFormat3.format(cal3.getTime());
		   // soapendtimeStr="- API-END-TIME DEVICE"+soapendtimeStr+"\n"+"-"+"Stop PROCESSING DEVICE"+soapendtimeStr;	
		}catch (Exception def) {			
			SapGenConstants.showErrorLog("Error in getDateTime:"+def.toString());
      }  		
		   return soapendtimeStr;
	}//getDateTime
	
	private void initPriceListSoapConnection(){        
		SoapSerializationEnvelope envelopeC = null;
		try{
			String saptimeStr = getDateTime();
        	String sapcntxttimeStr = "+"+"START PROCESSING DEVICE"+saptimeStr+"\n"+"EVENT:MATERIAL-STOCK-GET"+"\n"+"API-BEGIN-TIME DEVICE"+saptimeStr;
 		   	diogList.add(sapcntxttimeStr);	
			SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
			envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			
			SalesOrdProIpConstraints C0[];
			C0 = new SalesOrdProIpConstraints[3];
			
			for(int i=0; i<C0.length; i++){
				C0[i] = new SalesOrdProIpConstraints(); 
			}	            
			
			if(SapGenConstants.DIOG_FLAG==1){
	            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameterDiagnosis(this,  SapGenConstants.APPLN_NAME_STR_SALESPRO);
	        }else{
	        	C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
	        }        			  			
			//C0[0].Cdata = "DEVICE-ID:100000000000000:DEVICE-TYPE:BB:APPLICATION-ID:SALESPRO";
			C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
			C0[2].Cdata = "EVENT[.]MATERIAL-STOCK-GET[.]VERSION[.]0";
			
		  /*  arraysize = CheckDBValue();
			SalesOrderProConstants.showLog("arraysize from database : "+arraysize);
			
			if(arraysize==0){
				 C0[2].Cdata = "EVENT[.]MATERIAL-STOCK-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			}else{
				 C0[2].Cdata = "EVENT[.]MATERIAL-STOCK-GET[.]VERSION[.]0";
			}           */		            
			Vector listVect = new Vector();
			for(int k=0; k<C0.length; k++){
				listVect.addElement(C0[k]);
			}            
		   
			SalesOrdProIpConstraints C1;
			SalesOrdProMattConstraints catgry=null;
			for(int i=0; i<SelectedstockArrList.size(); i++){
				C1 = new SalesOrdProIpConstraints();
				catgry=(SalesOrdProMattConstraints)SelectedstockArrList.get(i);
				String matno=catgry.getMaterialNo().toString();
				 SalesOrderProConstants.showLog("soap matno : "+matno);
				C1.Cdata = "MD_T_MATNR[.]"+matno;
				listVect.addElement(C1);
			}
			
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
						pdialog = ProgressDialog.show(StockListDetailScreenTablet.this, "", getString(R.string.COMPILE_DATA),true);
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
	/*private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
		try {
			if(pdialog != null)
				pdialog = null;
			
			if(pdialog == null)
				this.runOnUiThread(new Runnable() {
					public void run() {
						pdialog = ProgressDialog.show(ctxAct, "", getString(R.string.SALESORDPRO_WAIT),true);
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
*/	
	public void updateServerResponse(SoapObject soap){		
		SalesOrdProStockConstraints priceCategory = null;
		String finalString2="";
		try{ 
			if(soap != null){  
				emptyAllVectors();
				
				String delimeter = "[.]", result="", res="", docTypeStr = "";
				SoapObject pii = null;
				String[] resArray = new String[37];
				int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;                
				String saptimeStr = getDateTime();
	     	    String strtparsStr="Start Parsing- "+saptimeStr;	
	     	    diogList.add(strtparsStr);	
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
											if(docTypeStr.equalsIgnoreCase("ZGSEMBST_MTRLSTCK13")){
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
								String finalString= result.replace(";", " "); 
		                        finalString2= finalString.replace("}", " "); 
		                        //SapGenConstants.showLog("finalString2"+finalString2);
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
								
								
								if(docTypeStr.equalsIgnoreCase("ZGSEMBST_MTRLSTCK13")){
									if(priceCategory != null)
										priceCategory = null;
										
									priceCategory = new SalesOrdProStockConstraints(resArray);
									if(stockArrList != null)
										stockArrList.add(priceCategory);	  
									
									if(mattCopyArrList != null)
										mattCopyArrList.add(priceCategory);
								}else if(docTypeStr.equalsIgnoreCase("ZGSSMWST_DIAGNOSYSINFO01")){		                        	
		                               diogList.add(finalString2);                                              	                               
	                            } 
								
							}
							else if(j == 0){
								String errorMsg = pii.getProperty(j).toString();
								//ServiceProConstants.showLog("Inside J == 0 ");
								int errorFstIndex = errorMsg.indexOf("Message=");
								if(errorFstIndex > 0){
									int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
									final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
									SalesOrderProConstants.showLog(taskErrorMsgStr);
									this.runOnUiThread(new Runnable() {
										public void run() {
											SalesOrderProConstants.showErrorDialog(StockListDetailScreenTablet.this, taskErrorMsgStr);
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
			SalesOrderProConstants.showLog("Stocks List Size : "+stockArrList.size());
			//initLayout();
			try {		        						
				if(stockArrList != null){
					if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
						if((priceListCount == 0) && (stockArrList.size() == 0)){
							InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreenTablet.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
						}
						else if((priceListCount > 0) && (stockArrList.size() > 0)){
							deleteSelctdDatafromDB();
						}
					}
					if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){			    							    				
						if((priceListCount == 0) && (stockArrList.size() == 0)){
							InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreenTablet.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
						}
						else if((priceListCount > 0) && (stockArrList.size() == 0)){
							
						}
						else if((priceListCount > 0) && (stockArrList.size() > 0)){
							deleteSelctdDatafromDB();
						}
					}
				}
				
				/*if(stockArrList.size() > 0){
					SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_INVENTORY_DETAIL, 0);    
					SharedPreferences.Editor editor = sharedPreferences.edit();    
					editor.putBoolean(SalesOrderProConstants.PREFS_KEY_INVENTORY_DETAIL_FOR_MYSELF_GET, true);    
					editor.commit();
				}*/
				
			} catch (Exception esf) {
				SalesOrderProConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			}
			stopProgressDialog();
			 String saptimeStr = getDateTime();
 	          String strtparsStr="Stop Parsing- "+saptimeStr;	
 		      diogList.add(strtparsStr);	
			StockListDetailScreenTablet.this.runOnUiThread(new Runnable() {
				public void run() {
					String saptimeStr = getDateTime();
	    	        String strtparsStr="Start Rendering UI- "+saptimeStr;	
	    		    diogList.add(strtparsStr);
	    		    initDBConnection();
					String saptimeStr2 = getDateTime();
	    	        String strtparsStr2="Stop Rendering UI- "+saptimeStr2;	
	    		    diogList.add(strtparsStr2);	
	    		      
	    		  	String saptimeStr3 = getDateTime();
	    		  	String sapcntxttimeStr = "+"+"STOP PROCESSING DEVICE"+saptimeStr3+"\n"+"EVENT:MATERIAL-STOCK-GET"+"\n"+"API-END-TIME DEVICE"+saptimeStr3;
        		   	diogList.add(sapcntxttimeStr);	
        		   if(SapGenConstants.DIOG_FLAG==1)
	               		DisplayDiogPopUp();
				}
			}); 
		}
	}//fn updateReportsConfirmResponse 
   
	 private void DisplayDiogPopUp() {
			TextView tv;
			ImageButton emailbtn,skypebtn;
			SapGenConstants.showLog("diogList size"+diogList.size());
	    	 LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
			  View layout;
			  
			  layout = inflater.inflate(R.layout.activity_dialog,
					  (ViewGroup) findViewById(R.id.listviewlineardialog3));		        		       		  
			 
			  ListView listview = (ListView)layout.findViewById(R.id.list4);
			  tv = (TextView)layout.findViewById(R.id.actdiogTV);
			  tv.setOnClickListener(tv_btnListener); 	
			  emailbtn = (ImageButton)layout.findViewById(R.id.showemailbtn2);
			  emailbtn.setOnClickListener(email_btnListener); 	   
			  
			/* skypebtn = (ImageButton)layout.findViewById(R.id.skypebtn);
			  skypebtn.setOnClickListener(skype_btnListener); 	  */
			  SOCustomerListAdapter = new SOCustomerListAdapter(this);
			  
				        		  
			  builder = new AlertDialog.Builder(this).setTitle("Gss Mobile Diognosis & Checks");	        		  	        		 
			  builder.setInverseBackgroundForced(true);
			  View view=inflater.inflate(R.layout.activity_diag_popup, null);
			  builder.setCustomTitle(view);	        		 
			  builder.setView(layout); 	        		
			  builder.setSingleChoiceItems(SOCustomerListAdapter, -1,new DialogInterface.OnClickListener() { 
	        		public void onClick(DialogInterface dialog, int which) {	  	        			
	        			SapGenConstants.showLog("which : "+which);        			
	        			alertDialog.dismiss();
	        		}
	        	});
			  alertDialog = builder.create();    		  
			  alertDialog.show();
			
		}//DisplayDiogPopUp
	    
	    public class SOCustomerListAdapter extends BaseAdapter {	    			
		    LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    HashMap<String, String> checkdCustmap = null;
		    	
		    public SOCustomerListAdapter(Context context) {
		    	// Cache the LayoutInflate to avoid asking for a new one each time.
		    	mInflater = LayoutInflater.from(context);   
		    }
		    
		    public int getCount() {
		    	try {
		    		if(diogList!= null)
		    			return  diogList.size();
		    		//SalesOrderConstants.showLog("SOCheckedList size in adapter "+SOCheckedList.size());
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
	            convertView = mInflater.inflate(R.layout.activty_diag_adapter, null);
	            holder = new ViewHolder();
	            holder.ctname = (TextView) convertView.findViewById(R.id.ctname2);              
	            holder.llitembg1 = (LinearLayout) convertView.findViewById(R.id.llitembg2);

	            if(position%2 == 0)
					holder.llitembg1.setBackgroundResource(R.color.item_even_color);
				else
					holder.llitembg1.setBackgroundResource(R.color.item_odd_color);
	            
	            try {
	            	if(diogList!= null){              		
	            		String spname = (String) diogList.get(position).toString();            		
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
	    
	    private OnClickListener tv_btnListener = new OnClickListener() {
	        public void onClick(View v) {  
	        	if(SapGenConstants.DIOG_FLAG!=0)
	        		SapGenConstants.DIOG_FLAG=0;
	        	alertDialog.dismiss();	
	        	
	        }
	    };
	    
	    private OnClickListener email_btnListener = new OnClickListener() {
	        public void onClick(View v) {          	        	
	        	showEmailActivity();   			
	        }
	    };
	    
	    public void showEmailActivity() {
	    	try{  
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
				SapGenConstants.showLog(" urlStr: "+urlStr);
	            int firstIndex = urlStr.indexOf("//");
				SapGenConstants.showLog(" firstIndex: "+firstIndex);
	            String urlName1 = urlStr.substring(firstIndex+2, urlStr.length());
				SapGenConstants.showLog(" urlName1: "+urlName1);
	            int thirdIndex = urlName1.indexOf(":");
	            if(thirdIndex != -1){
	    			SapGenConstants.showLog(" thirdIndex: "+thirdIndex);
	    			urlName = urlName1.substring(0, thirdIndex);
	    			SapGenConstants.showLog(" urlName: "+urlName);
	            }else{
	                int thirdIndex1 = urlName1.indexOf("/");
	    			SapGenConstants.showLog(" thirdIndex1: "+thirdIndex1);
	    			urlName = urlName1.substring(0, thirdIndex1);
	    			SapGenConstants.showLog(" urlName: "+urlName);            	
	            }
	    		linkText = "File Name: "+name.text()+"\n"+edition.text()+":"+" "+device.text()+"\n"+deviceType.text()+" "+manufacturer+" "+editionStr+"\n"+version.text()+" "+androidOS+"\n"+"GDID: "+imeno+"\n"+"Server: "+urlName+"\n";
				for(int i=0;i<diogList.size();i++){
					linkText = linkText+diogList.get(i).toString()+"\n";
				}				
				String to = "gss.mobile@globalsoft-solutions.com";
				String subject = "GSS Mobile Diognosis & Checks";
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
				email.putExtra(Intent.EXTRA_SUBJECT, subject);
				email.putExtra(Intent.EXTRA_TEXT, linkText);
				email.setType("message/rfc822");   		 
				startActivity(Intent.createChooser(email, "Choose an Email client"));
	    	}
	    	catch(Exception adf){
	    		SapGenConstants.showErrorLog("Error in showEmailActivity : "+adf.getMessage());
	    	}		
		}       
	 
	private void deleteSelctdDatafromDB() {
		try {
			deleteSelctdData();
			insertSelcdDataIntoDB();
		}catch(Exception adsf1){
			SalesOrderProConstants.showErrorLog("On deleteSelctdDatafromDB : "+adsf1.toString());
		}
		stopProgressDialog();		
	}//deleteSelctdDatafromDB
						
	private void initDBConnection() {
		try{
			stockArrList=InventoryDBOperations.readAllSelctdIDDataFromDB(this,SelectedstockArrList);
			if(stockArrList != null)
				mattCopyArrList = (ArrayList)stockArrList.clone();
			else
				SalesOrderProConstants.showErrorDialog(StockListDetailScreenTablet.this, "Data Not available in Offline mode");
			
			Collections.sort(stockArrList, stockSortComparator); 
			
		}catch (Exception sse) {
			SalesOrderProConstants.showErrorLog("Error on initDBConnection: "+sse.toString());
		}
		finally{
			try {
				initLayout();
			} catch (Exception e) {}
		}
		
	}//

	public void deleteSelctdData(){
		try{
			SalesOrdProStockConstraints matObj = null;
			boolean matchStr=false;
			if(idStrSap.size()>0){
				idStrSap.clear();
			}
			if(invntryreadDBList.size()>0){
				invntryreadDBList.clear();
			}
			invntryreadDBList=InventoryDBOperations.readAllSelctdidDataFromDB(this);
			for(int i=0;i<stockArrList.size();i++){
				matObj=(SalesOrdProStockConstraints)stockArrList.get(i);
				if(matObj!=null)
					idStrSap.add(matObj.getMaterialNo().trim());
			}
			if((stockArrList.size())>0 && (invntryreadDBList.size())>0){
				getselctdDBListarr=InventoryDBOperations.getDBselctdIdlist(this);
				SalesOrderProConstants.showLog("get selected DBListarr:"+getselctdDBListarr.size());
				if(getselctdDBListarr.size()>0){
					for(int i=0;i<idStrSap.size();i++){
						matchStr = getselctdDBListarr.contains(idStrSap.get(i).toString().trim());
						if(matchStr==true){
							InventoryDBOperations.deleteIdselctdTableDataFromDB(this,SalesProInvntryCP.IL_SEL_CONTENT_URI,idStrSap.get(i).toString().trim());
							
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

	final Runnable insertSelcdDataIntoDB = new Runnable(){
		public void run()
		{
			try{
				//System.out.println("contactVect.size():"+contactVect.size());	
				insertSelcdDataIntoDB();
			} catch(Exception sfe){
				SalesOrderProConstants.showErrorLog("Error in insertSerchdDataIntoDB:"+sfe.toString());
			}
		}	    
	};
	
	private void insertSelcdDataIntoDB() {
		SalesOrdProStockConstraints stkCategory;
		try {
			if(stockArrList != null){
				for(int k=0; k<stockArrList.size(); k++){
					stkCategory = (SalesOrdProStockConstraints) stockArrList.get(k);
					if(stkCategory != null){
						InventoryDBOperations.insertselctdListDataInToDB(StockListDetailScreenTablet.this, stkCategory);
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
			if(stockArrList != null)
				stockArrList.clear();			
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
			SalesOrdProStockConstraints stkObj = null;
			String mattStr = "", mattDescStr = "";
			if((mattCopyArrList != null) && (mattCopyArrList.size() > 0)){
				if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){                                            
					System.out.println("Match : "+match);  
					stockArrList.clear();
					for(int i = 0; i < mattCopyArrList.size(); i++){  
						stkObj = null;
						mattStr = "";
						mattDescStr = "";
						stkObj = (SalesOrdProStockConstraints)mattCopyArrList.get(i);
						if(stkObj != null){
							mattStr = stkObj.getMaterialNo().trim().toLowerCase();
							mattDescStr = stkObj.getMattDesc().trim().toLowerCase();
							match = match.toLowerCase();
							if((mattStr.indexOf(match) >= 0) || (mattDescStr.indexOf(match) >= 0)){
								stockArrList.add(stkObj);
							}
						}
					}//for 
					initLayout();
					//searchET.setText(searchStr);
				}
				else{
					System.out.println("Match is empty");
					stockArrList.clear();
					for(int i = 0; i < mattCopyArrList.size(); i++){  
						stkObj = (SalesOrdProStockConstraints)mattCopyArrList.get(i);
						if(stkObj != null){
							stockArrList.add(stkObj);
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
				 sortUnitMeasureFlag = !sortUnitMeasureFlag;
			 else if(sortInd == sortHeader3)
				 sortstockFlag = !sortstockFlag;
			 else if(sortInd == sortHeader4)
				 sortTransitFlag = !sortTransitFlag;
			 else if(sortInd == sortHeader5)
				 sortInspFlag = !sortInspFlag;
			 else if(sortInd == sortHeader6)
				 sortMattFlag = !sortMattFlag;
			 else if(sortInd == sortHeader7)
				 sortPlantDesFlag = !sortPlantDesFlag;
			 else if(sortInd == sortHeader8)
				 sortSLocDesFlag = !sortSLocDesFlag;
			 else if(sortInd == sortHeader9)
				 sortPlantFlag = !sortPlantFlag;
			 else if(sortInd == sortHeader10)
				 sortSLocFlag = !sortSLocFlag;
			 
			 SalesOrderProConstants.showLog("Selected Sort Index : "+sortInd);
			 Collections.sort(stockArrList, stockSortComparator); 
				
			 initLayout();
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	private final Comparator stockSortComparator =  new Comparator() {
		public int compare(Object o1, Object o2){ 
			int comp = 0;
			double rateAmt1=0, rateAmt2=0;
			String strObj1 = "0", strObj2="0";
			SalesOrdProStockConstraints repOPObj1, repOPObj2;
			try{            	
				if (o1 == null || o2 == null){
				}
				else{            
					repOPObj1 = (SalesOrdProStockConstraints)o1;
					repOPObj2 = (SalesOrdProStockConstraints)o2;
					
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
					else if(sortIndex == sortHeader2){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getMeasureUnit().trim();
					
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getMeasureUnit().trim();
						
						if(sortUnitMeasureFlag == true)
							comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
						else
							comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader3){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getStock().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getStock().trim();
						
						if(!strObj1.equalsIgnoreCase(""))
							rateAmt1 = Double.parseDouble(strObj1);
							
						if(!strObj2.equalsIgnoreCase(""))
							rateAmt2 = Double.parseDouble(strObj2);
							
						if(sortstockFlag == true)
							comp =  (int) (rateAmt1-rateAmt2);
						else
							comp =  (int) (rateAmt2-rateAmt1);
						
					}
					else if(sortIndex == sortHeader4){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getStockInTransfer().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getStockInTransfer().trim();
						
						if(!strObj1.equalsIgnoreCase(""))
							rateAmt1 = Double.parseDouble(strObj1);
							
						if(!strObj2.equalsIgnoreCase(""))
							rateAmt2 = Double.parseDouble(strObj2);
							
						if(sortTransitFlag == true)
							comp =  (int) (rateAmt1-rateAmt2);
						else
							comp =  (int) (rateAmt2-rateAmt1);
					}
					else if(sortIndex == sortHeader5){
						if(repOPObj1 != null)
							strObj1 = repOPObj1.getStockInQualityInsp().trim();
						
						if(repOPObj2 != null)
							strObj2 = repOPObj2.getStockInQualityInsp().trim();
						
						if(!strObj1.equalsIgnoreCase(""))
							rateAmt1 = Double.parseDouble(strObj1);
							
						if(!strObj2.equalsIgnoreCase(""))
							rateAmt2 = Double.parseDouble(strObj2);
							
						if(sortInspFlag == true)
							comp =  (int) (rateAmt1-rateAmt2);
						else
							comp =  (int) (rateAmt2-rateAmt1);
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
					else if(sortIndex == sortHeader7){
						 if(repOPObj1 != null)
								strObj1 = repOPObj1.getPlantDesc().trim();
							
							if(repOPObj2 != null)
								strObj2 = repOPObj2.getPlantDesc().trim();
							
							if(sortPlantDesFlag == true)
								comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
							else
								comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader8){
						 if(repOPObj1 != null)
								strObj1 = repOPObj1.getStorageLocationDesc().trim();
							
							if(repOPObj2 != null)
								strObj2 = repOPObj2.getStorageLocationDesc().trim();
							
							if(sortSLocDesFlag == true)
								comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
							else
								comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader9){
						 if(repOPObj1 != null)
								strObj1 = repOPObj1.getPlant().trim();
							
							if(repOPObj2 != null)
								strObj2 = repOPObj2.getPlant().trim();
							
							if(sortPlantFlag == true)
								comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
							else
								comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
					}
					else if(sortIndex == sortHeader10){
						 if(repOPObj1 != null)
								strObj1 = repOPObj1.getStorageLocation().trim();
							
							if(repOPObj2 != null)
								strObj2 = repOPObj2.getStorageLocation().trim();
							
							if(sortSLocFlag == true)
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
						
						if(sortMattFlag == true)
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
	
}//End of class StockListDetailScreen