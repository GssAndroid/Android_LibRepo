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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProStockConstraints;
import com.globalsoft.SalesPro.Database.InventoryDBOperations;
import com.globalsoft.SalesPro.Database.SalesProInvntryCP;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StockListDetailScreen extends Activity implements TextWatcher  {	
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5;
	private TextView tableHeaderTV6, tableHeaderTV7, tableHeaderTV8, tableHeaderTV9, tableHeaderTV10, myTitle;
	private EditText searchET;
	private ProgressDialog pdialog = null;
	private TableRow.LayoutParams linparams11 = null;
	
	private SoapObject resultSoap = null;
	private ListView listView;
	private inventoryStockListAdapter stockViewListAdapter;
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();	

    private TableLayout tl = null;
	private ArrayList stockArrList = new ArrayList();
	private ArrayList SelectdstockArrList = new ArrayList();
	private ArrayList mattCopyArrList = new ArrayList();
	private boolean isConnAvail = false;
	final Handler inventoryData_Handler = new Handler();
	private static ArrayList getselctdDBListarr = new ArrayList();
	private static ArrayList idStrSap = new ArrayList();
	private static ArrayList invntryreadDBList = new ArrayList();
	
	private String searchStr = "";
	private boolean sortFlag = false, sortMattFlag = false, sortMattDescFlag = false, searchflag = true;
	private boolean sortPlantDesFlag = false, sortUnitMeasureFlag = false, sortTransitFlag = false, sortInspFlag = false;
	
    private int sortIndex = -1;
    private String labelStkStr = "", labelTransitStr = "" ,finalresult="";
    private int priceListCount = 0, offlineFlag=0;
    private String priceListType = "", title="";    
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7, headerWidth8, headerWidth9, headerWidth10;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3, sortHeader4=4, sortHeader5=5, sortHeader6=6, sortHeader7=7, sortHeader8=8, sortHeader9=9, sortHeader10=10;
	private int dispwidth = 300;
	
	//private ArrayList selMattVector;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	SalesOrderProConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_STKLISTDET_TITLE);
        setContentView(R.layout.stocklistdetail);*/
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.stocklistdetail); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
	    myTitle = (TextView) findViewById(R.id.myTitle);
		//myTitle.setText(getResources().getString(R.string.SALESORDPRO_STKLISTDET_TITLE));

		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		title= getResources().getString(R.string.SALESORDPRO_STKLISTDET_TITLE);
		//OfflineFunc();
        /*selMattVector = (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
        if(selMattVector != null && selMattVector.size() > 0){
        	SalesOrderProConstants.showLog("Size : "+selMattVector.size());
        	initPriceListSoapConnection();
        }*/
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		SelectdstockArrList = (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
        isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(StockListDetailScreen.this);
        
        if(isConnAvail!=false){
	        if(SelectdstockArrList != null && SelectdstockArrList.size() > 0){
	        	SalesOrderProConstants.showLog("Size : "+SelectdstockArrList.size());
	        	initPriceListSoapConnection();	        	
	        }
        }
        else{
        	initDBConnection();      	
        }
        initLayout();
    }
    
    private void initDBConnection() {
    	try{
    		stockArrList=InventoryDBOperations.readAllSelctdIDDataFromDB(this,SelectdstockArrList);
    		
    		if(stockArrList != null)
        	mattCopyArrList = (ArrayList)stockArrList.clone();
    		else
    			SalesOrderProConstants.showErrorDialog(StockListDetailScreen.this, "Data Not available in Offline mode");
        	
    		Collections.sort(stockArrList, stockSortComparator); 
    	}catch (Exception sse) {
    		SalesOrderProConstants.showErrorLog("Error on initDBConnection: "+sse.toString());
		}
    	finally{
        	try {
        		initLayout();
			} catch (Exception e) {}
		}
	}

	private void initLayout(){
    	try{
    		setContentView(R.layout.stocklistdetail);    		
    		dispwidth = SalesOrderProConstants.getDisplayWidth(this);    		
    		searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			listView = (ListView)findViewById(R.id.list1);
			listView.setTextFilterEnabled(true);
			//listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			   
			stockViewListAdapter = new inventoryStockListAdapter(this);
			listView.setAdapter(stockViewListAdapter);
			listView.setOnItemClickListener(listItemClickListener); 
	        
			searchET.setFocusable(true);
			searchET.setFocusableInTouchMode(true);
			searchET.requestFocus();
			
			//labelStkStr = getResources().getString(R.string.SALESORDPRO_STKLIST_IN_INSP); 
			//labelTransitStr = getResources().getString(R.string.SALESORDPRO_STKLIST_INTRANSIT); 
    	}
    	catch(Exception sfg){
    		SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
	
	public class inventoryStockListAdapter extends BaseAdapter { 
    	private LayoutInflater mInflater=null;
    	public SalesOrdProStockConstraints stockCategory;
    	String matterialnoStr = "", transitnoStr = "";
        String mattDescstckStr = "", unitsstckStr = "", quantityvalStr = "";
    	
        public inventoryStockListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
        	try {
				if(stockArrList != null)
					return stockArrList.size();
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

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;      
            try{
            	if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.stock_list,parent,false);
                    holder = new ViewHolder();
                    holder.text = (TextView)convertView.findViewById(R.id.custloc);
                    holder.taskid = (TextView)convertView.findViewById(R.id.taskid);
                    holder.date = (TextView)convertView.findViewById(R.id.date);
                    holder.llitembg = (LinearLayout)convertView.findViewById(R.id.llitembg1);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                           
                if(position%2 == 0)
                	holder.llitembg.setBackgroundResource(R.color.item_even_color);
                else
                	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
                
                String data = "";
                
                if(stockArrList != null){
                	stockCategory = ((SalesOrdProStockConstraints)stockArrList.get(position));
                	if(stockCategory != null){
                		try {
                			matterialnoStr = stockCategory.getMaterialNo().trim();                            
                			mattDescstckStr = stockCategory.getMattDesc().trim();
    						unitsstckStr = stockCategory.getMeasureUnit().trim();
    						quantityvalStr = stockCategory.getStock().trim();        
    						transitnoStr = stockCategory.getStockInTransfer().trim();              
    					} catch (Exception e) {}     
                        
    	        		 
                		
                        holder.date.setText(matterialnoStr);
                        holder.text.setText(mattDescstckStr);
                        holder.taskid.setText("Stock-Value"+": "+ quantityvalStr + " "+ unitsstckStr + "\n" + "   "+"In-Transit" + ": " +" "+transitnoStr);
                        
                		//boolean isExits = ServiceProConstants.errorTaskIdForStatus.contains(category.getObjectId().toString().trim());
                
                	}
                }          
            }catch(Exception e){
            	SalesOrderProConstants.showErrorLog(e.getMessage());
            }
            
            return convertView;
        }        
        class ViewHolder {
            TextView text;
            TextView taskid;
            TextView date;
            //ImageView indicator;
            //ImageView status;
            //ImageView errstatus;
            LinearLayout llitembg;
        }
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }        
    }//End of vanStockListAdapter

	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			try{
				//ServiceProConstants.showLog("Selected Item "+arg2);
				showStockDetailScreen(arg2);
			}
			catch (Exception dee) {
				SalesOrderProConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}
	};

	private void showStockDetailScreen(int selIndex) {
		try{
			SalesOrdProStockConstraints stkCategory = null;
	        boolean errFlag = false;
	        
	        if(stockArrList != null){
	            if(stockArrList.size() > selIndex){
	                stkCategory = (SalesOrdProStockConstraints)stockArrList.get(selIndex);
	                if(stkCategory != null){
	                	try {
							Intent intent = new Intent(this, StockListDetailView.class);
							intent.putExtra("stkCategory", stkCategory);
							startActivityForResult(intent, SalesOrderProConstants.STOCKVIEW_DETAIL_SCREEN);
						} 
						catch (Exception e) {
							SalesOrderProConstants.showErrorLog(e.getMessage());
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
	        	SalesOrderProConstants.showErrorDialog(this, ""+R.string.SALESORDPRO_STKLIST_ERR_DETCANT);
	    }
	    catch(Exception asf){
	    	SalesOrderProConstants.showErrorLog("On showStockDetailScreen : "+asf.toString());
	    }		
	}//showStockDetailScreen	
				
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
            //C0[0].Cdata = "DEVICE-ID:100000000000000:DEVICE-TYPE:BB:APPLICATION-ID:SALESPRO";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]MATERIAL-STOCK-GET[.]VERSION[.]0";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }            
           
            SalesOrdProIpConstraints C1;
            SalesOrdProMattConstraints stckObj = null;
            for(int i=0; i<SelectdstockArrList.size(); i++){
            	C1 = new SalesOrdProIpConstraints();
            	stckObj=(SalesOrdProMattConstraints)SelectdstockArrList.get(i);
				String matno = stckObj.getMaterialNo().toString();
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
        				pdialog = ProgressDialog.show(StockListDetailScreen.this, "", getString(R.string.COMPILE_DATA),true);
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
   /* private void startNetworkConnection(final Context ctxAct, final SoapSerializationEnvelope envelopeCE, final String url){
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
*/    
   /* private void getSOAPViaHTTP(SoapSerializationEnvelope envelopeCE, String url){		
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
                                finalresult = result.substring(0, result.length() - 3);
                                SapGenConstants.showLog("final Result : "+finalresult);
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
                                            SalesOrderProConstants.showErrorDialog(StockListDetailScreen.this, taskErrorMsgStr);
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
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
	        			}
	    				else if((priceListCount > 0) && (stockArrList.size() > 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
	    					insertSelcdDataIntoDB();
	        			}
	    			}
	    			if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
	    				
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
	    		        			pricelistData_Handler.post(insertSelcdDataIntoDB);
	    		        		}
	    			            
	    					};
	    			        t.start();
	    		       	}   catch(Exception adsf1){
	    		         	SalesOrderProConstants.showErrorLog("On updateServerResponse : "+adsf1.toString());
	    		         }
	    		    	stopProgressDialog(); */  
	    				if((priceListCount == 0) && (stockArrList.size() == 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
	        			}
	    				else if((priceListCount > 0) && (stockArrList.size() == 0)){
	    					initDBConnection();
	        			}
	    				else if((priceListCount > 0) && (stockArrList.size() > 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
	    					insertSelcdDataIntoDB();
	        			}
	    			}
	    		}
	    		
				if(stockArrList.size() > 0){
					SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, true);    
	    			editor.commit();
				}
				
				if(finalresult.equalsIgnoreCase("WIPE-OUT")){                                	
                	SapGenConstants.showLog("delete databases");
                	cleanUpDatabase();
                } 
				
    			
			} catch (Exception esf) {
				SalesOrderProConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			}
        	stopProgressDialog();
        	StockListDetailScreen.this.runOnUiThread(new Runnable() {
	            public void run() {
	            	initDBConnection();
	            }
	        }); 
        }
    }//fn updateReportsConfirmResponse 
    
    private void cleanUpDatabase() {    	
    	InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
    	InventoryDBOperations.deleteAllTableDataFromDB(StockListDetailScreen.this, SalesProInvntryCP.IL_SER_CONTENT_URI);    	
    }//cleanUpDatabase
    
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
 						InventoryDBOperations.insertselctdListDataInToDB(StockListDetailScreen.this, stkCategory);
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
            SalesOrderProConstants.showLog("mattCopyArrList size : "+mattCopyArrList.size());
            SalesOrderProConstants.showLog("match size : "+match);
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
                    //drawSubLayout();
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
                    //drawSubLayout();
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
				sortPlantDesFlag = !sortPlantDesFlag;
			else if(sortInd == sortHeader3)
				sortUnitMeasureFlag = !sortUnitMeasureFlag;
			else if(sortInd == sortHeader4)
				sortTransitFlag = !sortTransitFlag;
			else if(sortInd == sortHeader5)
				sortInspFlag = !sortInspFlag;
			else if(sortInd == sortHeader6)
				sortMattFlag = !sortMattFlag;
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
                    		
                        if(sortPlantDesFlag == true)
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
                    		
                        if(sortPlantDesFlag == true)
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
                    		
                        if(sortPlantDesFlag == true)
                            comp =  (int) (rateAmt1-rateAmt2);
                        else
                            comp =  (int) (rateAmt2-rateAmt1);
                    }
                    else{
                        // Code to sort by Material Desc (default)
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getMattDesc().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getMattDesc().trim();
                        
                        if(sortMattDescFlag == true)
                        	comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
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