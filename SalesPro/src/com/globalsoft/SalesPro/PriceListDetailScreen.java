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
import android.widget.TextView;


import com.globalsoft.SalesPro.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Constraints.SalesOrdProPriceConstraints;
import com.globalsoft.SalesPro.Database.PriceListCP;
import com.globalsoft.SalesPro.Database.PriceListDBOperations;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;


public class PriceListDetailScreen extends Activity implements TextWatcher  {	
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5;
	private TextView tableHeaderTV6, tableHeaderTV7, tableHeaderTV8, tableHeaderTV9, tableHeaderTV10, searchLbl, custNameTV;
	private EditText searchET;
	private ProgressDialog pdialog = null;
	private LinearLayout searchLinear,custmorsearchlinear,headerLinear;
	
	private SoapObject resultSoap = null;
	private boolean flag_pref = false;
	private ListView listView;
	private PriceListAdapter pListAdapter;
	final Handler pricelistData_Handler = new Handler();
    private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;	
	
	private ArrayList mattCopyArrList = new ArrayList();	
	private static ArrayList getselctdDBListarr = new ArrayList();
	private static ArrayList idStrSap = new ArrayList();
	private static ArrayList pricereadDBList = new ArrayList();
	private static ArrayList SelectdMattList = new ArrayList();
	
	private String searchStr = "";
	private boolean sortFlag = false, sortMattFlag = false, sortMattDescFlag = false, searchflag = true;
	private boolean sortAmtFlag = false, sortRateUnitFlag = false,soapConstantFunc;
	private static String labelStkStr = "", labelTransitStr = "" ,finalresult="";
	private int priceListCount = 0;
    private String priceListType = "",custName,custNumb;
	
    private int sortIndex = -1;
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7, headerWidth8, headerWidth9, headerWidth10;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3, sortHeader4=4, sortHeader5=5, sortHeader6=6, sortHeader7=7, sortHeader8=8, sortHeader9=9, sortHeader10=10;
	private int dispwidth = 300;
	
	private ArrayList selMattVector = new ArrayList();;
    private static boolean isConnAvail = false;
    private About abt = new About();
    //public static Elements serviceUrl,ActionUrl,TypeFname,ServiceNamespace,InputParamName,NotationDelimiter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SalesOrderProConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_PLIST_TITLE1);
        setContentView(R.layout.pricelistdetail);*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.pricelistviewdetail); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.SALESORDPRO_PLIST_TITLE1));

		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);	
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
		
		//SalesOrderProConstants.SapUrlConstants(this);//parsing url.xml file
		
		SharedPreferences settings = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);      
		flag_pref = settings.getBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, false);
       
		SelectdMattList =  (ArrayList) this.getIntent().getSerializableExtra("selMatIds");
		custName =  (String) this.getIntent().getSerializableExtra("custname");
		custNumb =  (String) this.getIntent().getSerializableExtra("custnumb");
		
		isConnAvail = SalesOrderProConstants.checkConnectivityAvailable(PriceListDetailScreen.this);        
		if(isConnAvail!=false){	        	
			initPriceListSoapConnection();  
		}		
		else
			initDBConnection();		
    }
    
    private void initDBConnection() {
    	try{
    		selMattVector=PriceListDBOperations.readAllSelctdIdDataFromDB(this,SelectdMattList);
    		if(selMattVector != null)
    			mattCopyArrList = (ArrayList)selMattVector.clone();
    		else
    			SalesOrderProConstants.showErrorDialog(PriceListDetailScreen.this, "Data Not available in Offline mode");    		
    		Collections.sort(selMattVector, priceSortComparator);         	
    	}catch (Exception sse) {
    		SalesOrderProConstants.showErrorLog("Error on initDBConnection: "+sse.toString());
		}
    	finally{
        	try {
        		initLayout();
			} catch (Exception e) {}
		}		
	}//initDBConnection
	
   /* public void showURLActivity() {
    	try{    	    		
    		AssetManager assetManager = getAssets();
    		InputStream inputstrm;
    		inputstrm = assetManager.open("url_file.xml"); 
    		     int size = inputstrm.available();    		     
    		          byte[] buffer = new byte[size];   		
    		          inputstrm.read(buffer);   		
    		          inputstrm.close();
 
    		          String text = new String(buffer);    		    		    	
    		          Document doc = Jsoup.parse(text);    		        
    		         serviceUrl = doc.getElementsByTag("SOAP_SERVICE_URL");
    		         ActionUrl = doc.getElementsByTag("SOAP_ACTION_URL");
    		         TypeFname = doc.getElementsByTag("SOAP_TYPE_FNAME");
    		         ServiceNamespace = doc.getElementsByTag("SOAP_SERVICE_NAMESPACE");
    		         InputParamName = doc.getElementsByTag("SOAP_INPUTPARAM_NAME");
    		         NotationDelimiter = doc.getElementsByTag("SOAP_NOTATION_DELIMITER");    
    		         //assigning values to variables
    		         
    		         SapGenConstants.SOAP_SERVICE_URL = serviceUrl.text();
    		         SapGenConstants.SOAP_ACTION_URL = ActionUrl.text();
    		         SapGenConstants.SOAP_TYPE_FNAME = TypeFname.text();
    		         SapGenConstants.SOAP_SERVICE_NAMESPACE = ServiceNamespace.text();
    		         SapGenConstants.SOAP_INPUTPARAM_NAME = InputParamName.text();
    		         SapGenConstants.SOAP_NOTATION_DELIMITER = NotationDelimiter.text();
    	}
    	catch(Exception adf){
    		SalesOrderProConstants.showErrorLog("Error in showEmailActivity : "+adf.getMessage());
    	}
		
    }//showURLActivity
*/	
    private void initLayout(){
    	try{
    		setContentView(R.layout.pricelistviewdetail);
    		dispwidth = SalesOrderProConstants.getDisplayWidth(this);
    		searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);			
			headerLinear = (LinearLayout) findViewById(R.id.sorditem_srlheader_linear);			
			custNameTV = (TextView)findViewById(R.id.custNameTV);			
			if(custName.length() == 0 && custNumb.length() == 0){				
				headerLinear.setVisibility(View.GONE);
			}else{				
				headerLinear.setVisibility(View.VISIBLE);
				if(custName!=null || custNumb!=null){
		        	custNameTV.setText(" "+custName+" "+"("+custNumb+")");
		        }
			}
			
			listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			//listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			    
			pListAdapter = new PriceListAdapter(this);
			listView.setAdapter(pListAdapter);
			listView.setOnItemClickListener(listItemClickListener);			
			
			searchET.setFocusable(true);
			searchET.setFocusableInTouchMode(true);
			searchET.requestFocus();
			//labelStkStr = getResources().getString(R.string.SALESORDPRO_PLIST_KSCHL); 
			//labelTransitStr = getResources().getString(R.string.SALESORDPRO_PLIST_PLTYP); 			
    	}
    	catch(Exception sfg){
    		SalesOrderProConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
			
	public class PriceListAdapter extends BaseAdapter {  
    	private LayoutInflater mInflater=null;
    	public SalesOrdProPriceConstraints stkCategory;
    	String matterialStr = "", pricetype = "";
        String mattDescStr = "", conditionunit = "", kschltext = "",rateamount="",rateunit="",conditinpriceunit="";
    	
        public PriceListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
        	try {
				if(selMattVector != null)
					return selMattVector.size();
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
                    convertView = mInflater.inflate(R.layout.price_list,parent,false);
                    holder = new ViewHolder();
                    holder.text = (TextView) convertView.findViewById(R.id.matrlno);
                    holder.taskid = (TextView) convertView.findViewById(R.id.matrdsc);
                    holder.date = (TextView) convertView.findViewById(R.id.matrl);
                    holder.llitembg = (LinearLayout)convertView.findViewById(R.id.llitembg);
                    convertView.setTag(holder);
                } else {
                    holder=(ViewHolder)convertView.getTag();
                }
                           
                if(position%2 == 0)
                	holder.llitembg.setBackgroundResource(R.color.item_even_color);
                else
                	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
				String data = "";
				if(selMattVector != null){            	
					stkCategory = ((SalesOrdProPriceConstraints)selMattVector.get(position));            	
					if(stkCategory != null){
						try {
							matterialStr = stkCategory.getMaterialNo().trim();                            
							mattDescStr = stkCategory.getMattDesc().trim();
							conditionunit = stkCategory.getConditionUnit().trim();
							kschltext = stkCategory.getKSCHLText().trim();     
							rateamount=stkCategory.getRateAmount();
							rateunit=stkCategory.getRateunit();
							conditinpriceunit=stkCategory.getCondPricingUnit();
							
							//pricetype = stkCategory.getPLTYPText().trim();              
							
						} catch (Exception e) {}    
						holder.date.setText(matterialStr);
						holder.text.setText(mattDescStr);
						//holder.taskid.setText(kschltext+" "+conditionunit+"");
						holder.taskid.setText(rateamount+" "+rateunit+" "+"for"+" "+conditinpriceunit+" "+conditionunit+"");
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
            LinearLayout llitembg;
        }
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }           
    }//End of PriceListAdapter
	
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			try{
				
				showPriceDetailScreen(arg2);
			}
			catch (Exception dee) {
				SalesOrderProConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
			}
		}
	};

	private void showPriceDetailScreen(int selIndex){
		try{
			SalesOrdProPriceConstraints stkCategory = null;			
			boolean errFlag = false;			
			if(selMattVector != null){
				if(selMattVector.size() > selIndex){
					stkCategory = (SalesOrdProPriceConstraints)selMattVector.get(selIndex);
					if(stkCategory != null){
						try {
							Intent intent = new Intent(this, PriceListDetailViewScreen.class);
							intent.putExtra("priceCategoryObj", stkCategory);
							startActivityForResult(intent, SalesOrderProConstants.PRICEVIEWLIST_DETAIL_SCREEN);
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
			}else{
				errFlag = true;
			}				
			if(errFlag == true)
				SalesOrderProConstants.showErrorDialog(this, ""+R.string.SALESORDPRO_PLIST_ERR_DETCANT);
		}
		catch(Exception asf){
			SalesOrderProConstants.showErrorLog("On showPriceDetailScreen : "+asf.toString());
		}
	}//fn showPriceDetailScreen
   
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
			
			if(!flag_pref){           	
				 C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
			}else{				
				 C0[2].Cdata = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
			}
		
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
		}catch(Exception asd){
			SalesOrderProConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
		}
	}//fn initPriceListSoapConnection	
    
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
	    			if(pdialog != null)
            			pdialog = null;
            		
        			if(pdialog == null){
        				pdialog = ProgressDialog.show(PriceListDetailScreen.this, "", getString(R.string.COMPILE_DATA),true);
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
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
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
                                    SalesOrderProConstants.showLog(taskErrorMsgStr);
                                    this.runOnUiThread(new Runnable() {
                                        public void run() {
                                        	SalesOrderProConstants.showErrorDialog(PriceListDetailScreen.this, taskErrorMsgStr);
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
        	//initLayout();
        	try {        						
	    		if(selMattVector != null){
	    			if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((priceListCount == 0) && (selMattVector.size() == 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
	        			}
	    				else if((priceListCount > 0) && (selMattVector.size() > 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
	    					insertSelcdDataIntoDB();
	        			}
	    			}
	    			if(priceListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){
	    				if((priceListCount == 0) && (selMattVector.size() == 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
	        			}
	    				else if((priceListCount > 0) && (selMattVector.size() == 0)){
	        			}
	    				else if((priceListCount > 0) && (selMattVector.size() > 0)){
	    					PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
	    					insertSelcdDataIntoDB();
	        			}
	    			}
	    		}
	    		
				if(selMattVector.size() > 0){
					SharedPreferences sharedPreferences = getSharedPreferences(SalesOrderProConstants.PREFS_NAME_FOR_PRICE_STOCK, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(SalesOrderProConstants.PREFS_KEY_PRICE_LIST_FOR_MYSELF_GET, true);    
	    			editor.commit();
				}
				
				//wipe out code
				if(finalresult.equalsIgnoreCase("WIPE-OUT")){                                	
                	SapGenConstants.showLog("delete databases");
                	cleanUpDatabase();
                } 
    			
			} catch (Exception esf) {
	        	stopProgressDialog();
				SalesOrderProConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			}
        	stopProgressDialog();
        	PriceListDetailScreen.this.runOnUiThread(new Runnable() {
	            public void run() {
	            	initDBConnection();
	            }
	        }); 
        }
    }//fn updateReportsConfirmResponse     

   	private void cleanUpDatabase() {   	
   		PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SEL_CONTENT_URI);
   		PriceListDBOperations.deleteAllTableDataFromDB(this, PriceListCP.PL_SER_CONTENT_URI);   	
   }//cleanUpDatabase
   
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
       SalesOrdProPriceConstraints stkCategory;
    	try {
			if(selMattVector != null){
				for(int k=0; k<selMattVector.size(); k++){
					stkCategory = (SalesOrdProPriceConstraints) selMattVector.get(k);
					if(stkCategory != null){
						PriceListDBOperations.insertselctdListDataInToDB(this, stkCategory);
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
			else if(sortInd == sortHeader3)
				sortRateUnitFlag = !sortRateUnitFlag;
			else if(sortInd == sortHeader6)
				sortMattFlag = !sortMattFlag;			 
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
                    else if(sortIndex == sortHeader3){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getRateunit().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getRateunit().trim();
                        
                        if(sortRateUnitFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
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
    
}//End of class PriceListDetailScreen