package com.globalsoft.SalesPro;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
import android.content.res.AssetManager;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Database.InventoryDBOperations;
import com.globalsoft.SalesPro.Database.SalesProInvntryCP;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibActivity.ActivityListforTablet.SOCustomerListAdapter;
import com.globalsoft.SapLibActivity.Utils.CrtGenActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StockListActivity extends ListActivity { 	
	private EditText customerET;		
	private ImageButton searchbtn, showsearchbtn;
	private LinearLayout searchLinear;
	private RelativeLayout selectrelativeLT;
	private Button showStockBtn;
	private ListView listView;
	private TextView customerTV, myTitle;
	private SoapObject requestSoapObj = null;
	private StartNetworkTask soapTask = null;
	ArrayList diogList =new ArrayList();
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	private SOCustomerListAdapter SOCustomerListAdapter;
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	
	private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	private boolean isConnAvail = false ,soapConstantFunc;
	final Handler invntrylistData_Handler = new Handler();
	private ArrayList<Boolean>  status =new ArrayList<Boolean>() ;
	
	private String taskErrorMsgStr = "", custSearchStr ,finalresult="";
    
    /*private Vector mattVector = new Vector();
    private Vector selMatVector = new Vector();*/
    
    private ArrayList stockList=new ArrayList();
    private ArrayList selectdStockList=new ArrayList();
    private ArrayList idStrSap=new ArrayList();
    private ArrayList invntryreadDBList=new ArrayList();
    private ArrayList getDBListarr=new ArrayList();
    private ArrayList mattlistcopy = new ArrayList();
    private ArrayList StockDBList = new ArrayList();
    private int stocksListCount = 0, offlineFlag=0;
    private String stocksListType = "", title="",linkText= " ";
    private boolean flag_pref = false;
    
    private String[] custDetArr;
    private About abt = new About();
    //public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	SalesOrderProConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_STKLIST_TITLE1);
        setContentView(R.layout.stocklistmain);*/
    	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.stocklistmain); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		 myTitle = (TextView) findViewById(R.id.myTitle);
		//myTitle.setText(getResources().getString(R.string.SALESORDPRO_STKLIST_TITLE2));

		title= getResources().getString(R.string.SALESORDPRO_STKLIST_TITLE2);
		//OfflineFunc();
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		
		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		myTitle.setText(title);
		SharedPreferences settings = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);      
		flag_pref = settings.getBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, false);
		
        initLayout();
    }
    
    private void initLayout(){
		try{
			customerET = (EditText) findViewById(R.id.customerET);
			
			searchbtn = (ImageButton) findViewById(R.id.custsearchbtn);
			searchbtn.setOnClickListener(custsrch_btnListener); 
			
			showsearchbtn = (ImageButton) findViewById(R.id.showsearchbtn);
			showsearchbtn.setOnClickListener(showsrch_btnListener); 
			
			showStockBtn = (Button) findViewById(R.id.showStockBtn);
			showStockBtn.setOnClickListener(showCustBtnListener); 
				
			searchLinear = (LinearLayout) findViewById(R.id.searchlinear);
			selectrelativeLT = (RelativeLayout) findViewById(R.id.showrelativeLT);
			selectrelativeLT.setVisibility(View.GONE);
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);
			
			listviewcall();
						
			try{
				int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
				if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){					
					showStockBtn.setTextSize(SalesOrderProConstants.TEXT_SIZE_BUTTON);
					
					LinearLayout.LayoutParams linparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					linparams1.width = SalesOrderProConstants.EDIT_TEXT_WIDTH;		
					linparams1.height = SalesOrderProConstants.EDIT_TEXT_HEIGHT;
					linparams1.rightMargin = SalesOrderProConstants.EDIT_TEXT_RIGHTMARGIN;
					customerET.setLayoutParams(linparams1);
					
					searchbtn.setBackgroundResource(R.drawable.search1);
					showsearchbtn.setBackgroundResource(R.drawable.back1);
					
					customerTV = (TextView) findViewById(R.id.customerTV);
					customerTV.setTextSize(SalesOrderProConstants.TEXT_SIZE_LABEL);
				}
			}catch(Exception sf){}
		}
		catch(Exception sfg){
			SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
       
    private void listviewcall(){
		try {						
			if(status != null){
				status.clear();
			}
			for (int i = 0; i < stockList.size(); i++) {				
					status.add(i, false);									            
	        }		
			setListAdapter(new StockListAdapter(this));			
		} catch (Exception ce) {
			SalesOrderProConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
    
    public class StockListAdapter extends BaseAdapter {
    	LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	public StockListAdapter(Context context) {
    		mInflater = LayoutInflater.from(context);   
        }
       
        public int getCount() {
        	try {
				if(stockList != null)
					return stockList.size();
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
            convertView = mInflater.inflate(R.layout.stocklist_checkbox, null);
            holder = new ViewHolder();
            holder.matnumb = (TextView) convertView.findViewById(R.id.matnumb);   
            holder.matdesc = (TextView) convertView.findViewById(R.id.matdesc); 
            holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg1);
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
            	if(stockList != null){
            		SalesOrdProMattConstraints stkCategory = null;                	
            		stkCategory = ((SalesOrdProMattConstraints)stockList.get(position));                	
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
						}
					});     
                	if(status.get(position)==true){
      	            	cbox.setChecked(true);
      	            }else
      	            	cbox.setChecked(false);
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
                if(state == false){
	                SalesOrderProConstants.showLog("1");
                	box.setChecked(true);
	                SalesOrderProConstants.showLog("2");
					status.set(position, true);		
	                SalesOrderProConstants.showLog("3");
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
    
    private void showPriceListDetailScreen(){
    	try{
    		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
    		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
    			Intent intent = new Intent(this, StockListDetailScreenTablet.class);
        		//intent.putExtra("selMatIds", selMatVector);
        		intent.putExtra("selMatIds", selectdStockList);
    			startActivityForResult(intent,SalesOrderProConstants.STOCKLIST_DETAIL_SCREEN_TABLET);   
    		}
    		else{
    			Intent intent = new Intent(this, StockListDetailScreen.class);
        		intent.putExtra("selMatIds", selectdStockList);
    			startActivityForResult(intent,SalesOrderProConstants.STOCKLIST_DETAIL_SCREEN); 
    		}    		
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("Error in showSalesOrdCreationScreen : "+adf.getMessage());
    	}
    }//fn showPriceListDetailScreen
    
    private void setWindowTitle(final String title){
    	try{
    		this.setTitle(title);
    	}
    	catch(Exception sdf){
    		SalesOrderProConstants.showErrorLog("Error in setWindowTitle : "+sdf.getMessage());
    	}
    }//fn setWindowTitle
    
    private OnClickListener showCustBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderProConstants.showLog("Show Cust btn clicked");
        	if(selectdStockList != null)
        		selectdStockList.clear();
        	/*if(selMatVector != null)
        		selMatVector.removeAllElements();
        	else
        		selMatVector = new Vector();*/
        	if(listView != null){
        		int len = listView.getCount();
        		SalesOrderProConstants.showLog("List Count : "+len);
        		
        		String mattIdStr = "";
        		if(stockList != null){ 
        			SalesOrdProMattConstraints matObj = null;       
        		for (int i = 0; i < len; i++){
        			boolean getstatus = status.get(i).booleanValue();
        			SalesOrderProConstants.showLog("getstatus : "+getstatus);   
					
        			if (getstatus==true) {     
        				if(i < stockList.size()){
        					matObj = ((SalesOrdProMattConstraints)stockList.get(i));
        					String matno=matObj.getMaterialNo().toString();
        					selectdStockList.add(matObj);
        					SalesOrderProConstants.showLog("selectdStockList Count : "+selectdStockList.size());
        					}        		       				
        				}
        			}
        		}
        	}		
        	
        	if(selectdStockList.size() > 0){
        		showPriceListDetailScreen();
        	}
        	else
        		SalesOrderProConstants.showErrorDialog(StockListActivity.this, "Material Id is empty!");
        }
    };
    
    private OnClickListener showsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(searchLinear != null)
        		searchLinear.setVisibility(View.VISIBLE);
        	
        	if(selectrelativeLT != null)
        		selectrelativeLT.setVisibility(View.GONE);
        	
        	setWindowTitle(getString(R.string.SALESORDPRO_STKLIST_TITLE1));
        }
    };
    
    private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(customerET.getText().toString().trim().length() != 0) {
				try{
					custSearchStr = customerET.getText().toString().trim();
					SalesOrderProConstants.showLog("customer : "+custSearchStr);
					isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(StockListActivity.this);					
					if(isConnAvail!=false){						
						initStockSoapConnection();
					}
					else
						initDBConnection(custSearchStr);
					//initStockSoapConnection();
				}
				catch(Exception wsfsg){
					SalesOrderProConstants.showErrorLog("Error in Material Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderProConstants.showErrorDialog(StockListActivity.this, "Enter Material name to search.");
        }
    };
    
	private void initDBConnection(String str) {	    	
		stockList = InventoryDBOperations.readAllSerchIdDataFromDB(StockListActivity.this,str);	    	
		if(stockList.size()!=0)
			prefillCustomerData();
	    else {
			Toast toast = Toast.makeText(StockListActivity.this, "Data Not available in Offline mode",
	                Toast.LENGTH_LONG);
	        toast.show();
		}	    	
	}//initDBConnection
    
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
	 
    private void initStockSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
        	String saptimeStr = getDateTime();
        	String sapcntxttimeStr = "+"+"START PROCESSING DEVICE"+saptimeStr+"\n"+"EVENT:PRODUCT-SEARCH"+"\n"+"API-BEGIN-TIME DEVICE"+saptimeStr;
 		   	diogList.add(sapcntxttimeStr);	 	
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            if(SapGenConstants.DIOG_FLAG==1){
              	 C0[0].Cdata = SapGenConstants.getApplicationIdentityParameterDiagnosis(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
              }else{
            	  C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SapGenConstants.APPLN_NAME_STR_SALESPRO);
              }                      
            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB";
            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
            
            if(!flag_pref){
				C0[2].Cdata ="EVENT[.]PRODUCT-SEARCH[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
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

            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initStockSoapConnection
    
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
        				pdialog = ProgressDialog.show(StockListActivity.this, "", getString(R.string.COMPILE_DATA),true);
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
    
  /*  public void OfflineFunc(){
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
    
    public void updateServerResponse(SoapObject soap){		
    	SalesOrdProMattConstraints mattCategory = null;
    	String finalString2="";
        try{	    	 
        	if(soap != null){          		
    			if(stockList != null)
    				stockList.clear();	            
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
                                String finalString= result.replace(";", " "); 
	                            finalString2= finalString.replace("}", " "); 
	                            SapGenConstants.showLog("finalString2"+finalString2);
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
	                                if(stockList != null)
	                                	stockList.add(mattCategory);
	                                
	                                if(mattlistcopy != null)
	                                	mattlistcopy.add(stockList);
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
                                            SalesOrderProConstants.showErrorDialog(StockListActivity.this, taskErrorMsgStr);
                                        }
                                    });
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
        	SalesOrderProConstants.showLog("Stocks List Size : "+stockList.size());
        	//prefillCustomerData();
        	try {       					
	    		if(stockList != null){
	    			if(stocksListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((stocksListCount == 0) && (stockList.size() == 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SER_CONTENT_URI);
	    					
	        			}
	    				else if((stocksListCount > 0) && (stockList.size() > 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SER_CONTENT_URI);
	    					insertSerchdDataIntoDB();
	        			}
	    			}
	    			if(stocksListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
	    				if((stocksListCount == 0) && (stockList.size() == 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SER_CONTENT_URI);
	        			}
	    				else if((stocksListCount > 0) && (stockList.size() == 0)){
	    					
	        			}
	    				else if((stocksListCount > 0) && (stockList.size() > 0)){
	    					InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SER_CONTENT_URI);
	    					insertSerchdDataIntoDB();
	        			}
	    			}
	    		}
	    		
	    			if(stockList.size() > 0){
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
        	 String saptimeStr = getDateTime();
  	          String strtparsStr="Stop Parsing- "+saptimeStr;	
  		      diogList.add(strtparsStr);	
        	StockListActivity.this.runOnUiThread(new Runnable() {
	            public void run() {
	            	String saptimeStr = getDateTime();
	    	          String strtparsStr="Start Rendering UI- "+saptimeStr;	
	    		      diogList.add(strtparsStr);
	            	initDBConnection();
	            	 String saptimeStr2 = getDateTime();
	    	          String strtparsStr2="Stop Rendering UI- "+saptimeStr2;	
	    		      diogList.add(strtparsStr2);	
	    		      
	    		  	String saptimeStr3 = getDateTime();
                	String sapcntxttimeStr = "+"+"STOP PROCESSING DEVICE"+saptimeStr3+"\n"+"EVENT:PRODUCT-SEARCH"+"\n"+"API-BEGIN-TIME DEVICE"+saptimeStr3;
         		   	diogList.add(sapcntxttimeStr);	
         		   if(SapGenConstants.DIOG_FLAG==1)
	               		DisplayDiogPopUp();
	            }
				
	        });   
        	
        }
      
    }//fn updateReportsConfirmResponse  
    
    private void cleanUpDatabase() {    	
    	InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SEL_CONTENT_URI);
    	InventoryDBOperations.deleteAllTableDataFromDB(StockListActivity.this, SalesProInvntryCP.IL_SER_CONTENT_URI);    	
    }//cleanUpDatabase

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
 
    
    private void initDBConnection() {
		try {
			stockList=InventoryDBOperations.readAllSerchDataFromDB(StockListActivity.this);
  			SalesOrderProConstants.showLog("info array size : "+stockList.size());
		}catch (Exception esf1) {
  			SalesOrderProConstants.showErrorLog("On initDBConnection : "+esf1.toString());
  		}	 
  		finally{
          	try {
          		prefillCustomerData();
  			} catch (Exception e) {}
  		}    	
    	//prefillCustomerData();
	}
    
    private void insertSerchdDataIntoDB() {
    	SalesOrdProMattConstraints stkCategory;
    	try {
			if(stockList != null){
				for(int k=0; k<stockList.size(); k++){
					stkCategory = ((SalesOrdProMattConstraints)stockList.get(k));
					if(stkCategory != null){
						InventoryDBOperations.insertSerchdDataInToDB(StockListActivity.this, stkCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SalesOrderProConstants.showErrorLog("Error On insertSerchdDataIntoDB: "+ewe.toString());
		}		
	}// insertSerchdDataIntoDB

	private void prefillCustomerData(){
    	try{
    		if(stockList != null){
    			if(stockList.size() > 0){
    	    		custDetArr = getCustomerList();
    	    		if(custDetArr != null){    	    				            	   	            	
    	            	 if(custDetArr.length>1) {
    	            		 if(searchLinear != null)
    	    	            		searchLinear.setVisibility(View.GONE);    	            	
    	    	            	if(selectrelativeLT != null)
    	    	            		selectrelativeLT.setVisibility(View.VISIBLE);    	            	
    	    	            	if(showStockBtn != null)
    	    	            		showStockBtn.setVisibility(View.VISIBLE);    
       	    	            	listviewcall();   
       	 	        	}else{
       	 	        		getSingleMaterial();
       	 	        	}
    	    		}
    	    		setWindowTitle(getString(R.string.SALESORDPRO_STKLIST_TITLE2));
    			}
    		}
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
    
	public void getSingleMaterial(){
    	if(selectdStockList != null)
    		selectdStockList.clear();
    	else
    		selectdStockList = new ArrayList();      
    	if(stockList != null){
    		SalesOrdProMattConstraints stckmatobj=null;   
    		stckmatobj = ((SalesOrdProMattConstraints)stockList.get(0));
    		selectdStockList.add(stckmatobj);
			if(selectdStockList.size() > 0){
	    		showPriceListDetailScreen();
	    	}
	    	else
	    		SalesOrderProConstants.showErrorDialog(StockListActivity.this, "Material Id is empty!");
    	}
    }//getSingleMaterial
	
    private String[] getCustomerList(){
    	String choices[] = null;
        try{
            if(stockList != null){
            	SalesOrdProMattConstraints matObj = null;
                String nameStr = "", idstr="", combStr="";
                
                int arrSize = stockList.size();
                choices = new String[arrSize];
                
                for(int h=0; h<arrSize; h++){
                	matObj = (SalesOrdProMattConstraints)stockList.get(h);
                    if(matObj != null){
                    	idstr = matObj.getMaterialNo().trim();
                    	//nameStr = matObj.getMaterialUnit().trim();
                    	nameStr = matObj.getMaterialDesc().trim();
                        combStr = idstr+":"+nameStr;
                    	//combStr = idstr;
                       
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
    
}//End of class StockListActivity