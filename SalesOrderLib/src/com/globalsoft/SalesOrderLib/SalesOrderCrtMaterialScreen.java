package com.globalsoft.SalesOrderLib;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProIpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesOrderLib.Database.SalesOrderCP;
import com.globalsoft.SalesOrderLib.Database.SalesOrderDBOperations;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.HttpTransportSE;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesOrderCrtMaterialScreen extends ListActivity { 
	
	private EditText customerET;		
	private ImageButton searchbtn, showsearchbtn;
	private LinearLayout searchLinear,showlinear,bodylayout;
	private TextView[] valTV;
	private CheckBox[] chckBox;
	private RelativeLayout selectrelativeLT;
	private Button showCustBtn;
	private ListView listView;
	private String serverErrorMsgStr = "";
	private TextView customerTV, myTitle;
	
	private ProgressDialog pdialog = null;		
	private HashMap<String, String> stockMap = null; 
	private SoapObject resultSoap = null;
	private boolean isConnAvail=false;
	final Handler salesOrderMattData_Handler = new Handler();
	private ArrayList<Boolean>  status =new ArrayList<Boolean>() ;
    private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	final Handler productsData_Handler = new Handler();
	private DataBasePersistent dbObj = null, dbObjColumns = null;
	
	private String taskErrorMsgStr = "", custSearchStr="",matStr="" ,finalresult="", title="", nameStr = "", idstr="", combStr="", editStr="", metaLabel= "";
	private static int count=0;
	private int custListCount = 0;	
	private String custListType = "";
	private int offlineFlag=0;
	private boolean tableExits= false;
	private DataBasePersistent dbObjUIConf = null;
    
    private ArrayList priceArrayList = new ArrayList();
    private ArrayList valArrayList1 = new ArrayList();    
    
    private ArrayList metaProdListArray = new ArrayList();
    private ArrayList selMatArrayList = new ArrayList();
    private ArrayList custDetArr = new ArrayList();
    private ArrayList<String> DBMatArrayList = new ArrayList<String>();
    private ArrayList labelList = new ArrayList();
   // private HashMap<String, String> valArrayList = new HashMap<String, String>();	
    private HashMap<String, String> labelMap = new HashMap<String, String>();	    
    private ArrayList valArrayList = new ArrayList();
    private ArrayList<String> allDBMatArrayList = new ArrayList<String>();
    //private String[] custDetArr;
    private boolean flag;
    private int flag2=0;
    private int dispwidth = 320;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_PLIST_TITLE1);
        setContentView(R.layout.pricelistmain);*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.sopricelistmain); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		 myTitle = (TextView) findViewById(R.id.myTitle);
		//myTitle.setText(getString(R.string.SALESORDPRO_PLIST_TITLE2));

		int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
		if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}	
		 bodylayout = (LinearLayout) findViewById(R.id.somatrlayout);
		//title= getResources().getString(R.string.SALESORDPRO_PLIST_TITLE2);
		//OfflineFunc();	
		setTitleValue(); 
		//SapGenConstants.SapUrlConstants(this);
		//flag=(Boolean)this.getIntent().getBooleanExtra("matFlag",flag);
		flag2= (Integer)this.getIntent().getIntExtra("matFlag", flag2);
		 SalesOrderConstants.showLog("flag2:"+flag2);
		matStr = (String)this.getIntent().getStringExtra("matStr");
		 SalesOrderConstants.showLog("matStr:"+matStr);
		 dispwidth = SapGenConstants.getDisplayWidth(this);
		if(flag2 ==1){
			searchLinear = (LinearLayout) findViewById(R.id.searchlinear);
			 if(searchLinear != null)
				 searchLinear.setVisibility(View.VISIBLE);
			 searchLinear.removeAllViews();					
			 searchLinear.setOrientation(LinearLayout.HORIZONTAL);
			 displayUI(DBConstants.PRODUCT_SEARCH_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, searchLinear);	
			 DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			 labelList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.PRODUCT_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG);
	    		SapGenConstants.showLog("labs size"+labelList.size());
				valArrayList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.PRODUCT_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG);
				SapGenConstants.showLog("valArrayList size "+valArrayList.size());						
			//listviewcall();
			//MaterialSearch();
		}else{
			initLayout();
		}				         
    }
    
    private void initLayout(){
		try{
			/*customerET = (EditText) findViewById(R.id.customerET);
			
			searchbtn = (ImageButton) findViewById(R.id.custsearchbtn);
			searchbtn.setOnClickListener(custsrch_btnListener); 
			
			showsearchbtn = (ImageButton) findViewById(R.id.showsearchbtn);
			showsearchbtn.setOnClickListener(showsrch_btnListener); 
			
			showCustBtn = (Button) findViewById(R.id.showCustBtn);
			showCustBtn.setOnClickListener(showCustBtnListener); 
			showCustBtn.setText("Done");*/
			
			/*searchLinear = (LinearLayout) findViewById(R.id.searchlinear);			
			selectrelativeLT = (RelativeLayout) findViewById(R.id.showrelativeLT);
			selectrelativeLT.setVisibility(View.GONE);
			*/
		/*listView = (ListView)findViewById(android.R.id.list);
			//listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			//listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.setItemsCanFocus(false);*/
			labelList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.PRODUCT_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG);
    		SapGenConstants.showLog("labs size"+labelList.size());
			valArrayList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.PRODUCT_SEARCH_RESULT_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_MLN_SCREEN_TAG);
			SapGenConstants.showLog("valArrayList size "+valArrayList.size());			
			
			listviewcall();
			//showlinear	
			showlinear = (LinearLayout) findViewById(R.id.actionbarll);	
	    	 if(showlinear != null)
	    		 showlinear.setVisibility(View.VISIBLE);
	    	 showlinear.removeAllViews();					
	    	 showlinear.setOrientation(LinearLayout.HORIZONTAL);
			 displayUI(DBConstants.PRODUCT_SEARCH_RESULT_ACTION_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, showlinear);	
			
			try{
				int dispwidth = SapGenConstants.getDisplayWidth(this);
				if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
					LinearLayout.LayoutParams linparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					linparams1.width = SapGenConstants.EDIT_TEXT_WIDTH;		
					linparams1.height = SapGenConstants.EDIT_TEXT_HEIGHT;
					linparams1.rightMargin = SapGenConstants.EDIT_TEXT_RIGHTMARGIN;
					/*customerET.setLayoutParams(linparams1);
					
					searchbtn.setBackgroundResource(R.drawable.search1);
					showsearchbtn.setBackgroundResource(R.drawable.back1);
					
					customerTV = (TextView) findViewById(R.id.customerTV);
					customerTV.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
					
					showCustBtn.setTextSize(SapGenConstants.TEXT_SIZE_BUTTON);*/
				}
			}catch(Exception sf){}
			MaterialSearch();			
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {		    		
    		DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    		valArrayList1 = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
    		labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("labelMap size : "+labelList.size());			 
			if(valArrayList1 != null && valArrayList1.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valArrayList1.size());
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
								//String valStr = (String) custMap.get(metaLabel);       
			         			/*boolean lab = false;  
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = labelMap.containsKey(metaLabel);				         				
			         			}*/			         						                 	
			                 		LinearLayout llmclm = new LinearLayout(this);
		                 			llmclm.setOrientation(LinearLayout.VERTICAL);
			                 		
			                 	 if(metaLabel.equalsIgnoreCase(DBConstants.SO_MATT_DONE_FIELD_LABEL_TAG)){
		                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
		                     			bt.setText("");
		                     			bt.setPadding(5,5,5,5);
		            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		                     			bt.setTextColor(getResources().getColor(R.color.white));
		                     			bt.setBackgroundResource(R.drawable.btn_blue1);		                     			
		                     				String labStr = (String)labelMap.get(metaLabel);
		                     				SapGenConstants.showLog("button labStr : "+labStr);
		                     				if(labStr != null && labStr.length() > 0){
		                     					bt.setText(labStr);
		                     				}
		                     			
		                     			bt.setOnClickListener(new View.OnClickListener() {
											public void onClick(View view) {
												showCustBtnListener();
											}	
				                        });
		                     			dynll.addView(bt);
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
									//SapGenConstants.showLog("1"); 
									//String valStr = (String) custMap.get(metaLabel);
									//SapGenConstants.showLog("2 "); 
			                     	//secLine += " "+valStr;
			                     	String valTotStr = "";
			                     	boolean lab = false;  
			             			if(metaLabel != null && metaLabel.length() > 0){
			             				lab = labelMap.containsKey(metaLabel);
			             			}                      	
			                      if(metaValue.equalsIgnoreCase(DBConstants.VALUE_SEARCHBAR_TAG)){
			                     			//if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			                     				EditText et = new EditText(this);
				                     			et.setText("");
				                     			//et.setPadding(5,3,5,3);
				                     			et.setWidth(18);
					                 			et.setLayoutParams(new LayoutParams(250,30));
				                     			et.setTextColor(getResources().getColor(R.color.black));
				            					et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				                     			et.setBackgroundResource(R.drawable.editext_border);
					                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
					                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
				                     			if(lab){
				                     				String labStr = (String)labelMap.get(metaLabel);
				                     				if(labStr != null && labStr.length() > 0){
				                     					et.setHint(labStr);
				                     					et.setTextSize(12);
				                     				}
				                     			}else{
				                     				et.setHint(R.string.SEARCH_HINT_LBL);
				                     			}
				                     			et.addTextChangedListener(new TextWatcher(){
					                    	        public void afterTextChanged(Editable s) {
				                						//String updatedQty = s.toString().trim();
				                				 		SapGenConstants.showLog("Text : "+s.toString());
				                				 		editStr = s.toString().trim();
				                						//filter(searchStr);
					                    	        }
					                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
					                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
					                    	    }); 
			                      
				                     			  llmclm.addView(et); 
			                     			//}
				                     			 /*else{
			                     				SearchView et = new SearchView(this);			                     					                     			
				                     			//et.setIconifiedByDefault(false);			                 		
					                 			et.setLayoutParams(new LayoutParams(230,40));
					                 			if(lab){
				                     				String labStr = (String)labelMap.get(metaLabel);
				                     				if(labStr != null && labStr.length() > 0){
				                     					et.setQueryHint(labStr);
				                     				}
				                     			}else{
				                     				et.setQueryHint("Search");
				                     			}		
					                 								                 			
					                 			et.setOnQueryTextListener(new OnQueryTextListener() {				                 				
					                 				public boolean onQueryTextSubmit(String query) {
					                 					editStr = query.toString().trim();
					                 					SapGenConstants.showLog("detSumTag"+detSumTag);	
					                 					custSearchStr =editStr;
					                 					 MaterialSearch();
					                 					return false;
					                 				}				                 				
					                 				public boolean onQueryTextChange(String newText) {			                 					
					                 					return false;				                 									                 					
					                 				}
					                 			});
					                 			  llmclm.addView(et); 
			                     			}*/			                     							       			                     					            				                         				    
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_SEARCH_ICON_TAG)){
			                     			ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        					                        			
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.search1);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {														
													custSearchStr =editStr;	
											        MaterialSearch();								         												      			
												}	
					                        });			                     			
			            				    llmclm.addView(iv);
			                     		}else if(metaLabel.equalsIgnoreCase(DBConstants.SO_MATT_DONE_FIELD_LABEL_TAG)){
			                     			Button bt = new Button(this, null, R.style.innerBtnStyle);
			                     			bt.setText("");
			                     			bt.setPadding(5,5,5,5);
			            					bt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			bt.setTextColor(getResources().getColor(R.color.white));
			                     			bt.setBackgroundResource(R.drawable.btn_blue1);			                     			
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				SapGenConstants.showLog("button labStr : "+labStr);
		                     				if(labStr != null && labStr.length() > 0){
			                     					bt.setText(labStr);
			                     				}	                     		
			                     			bt.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													showCustBtnListener();
												}	
					                        });
			            				    llmclm.addView(bt);
			                     		//}/*else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_CHANGE_ICON_TAG)){			                    			    		
			                        		/*ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(5, 0, 0, 0);
			                        		iv.setImageResource(R.drawable.back1);
			                    			iv.setOnClickListener(new View.OnClickListener() {
												public void onClick(View view) {
													showCustomerSearch();
												}	
					                        });
			            				    llmclm.addView(iv);
			                     		}*/
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
    
    private void setTitleValue() {
    	try {
    		if(dbObjUIConf == null)
				dbObjUIConf = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		String title = dbObjUIConf.getTitle(DBConstants.SO_PRODUCT_SEARCH_RESULT_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
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
    
    OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			try{
				//CheckBox box = (CheckBox)view.findViewById(R.id.ckbox);
                boolean state = status.get(position).booleanValue();
                SalesOrderConstants.showLog("state on item click:"+state);
                SalesOrderConstants.showLog("position on item click:"+position);
                                
                if(state==false){
                	//box.setChecked(true);
					status.set(position, true);		
                }
                else{
                	//box.setChecked(false);
					status.set(position, false);		
                }
			}catch (Exception dee) {
				SalesOrderConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}
    };
    private void listviewcall(){
		try {			
			if(status != null){
				status.clear();
			}
			for (int i = 0; i < priceArrayList.size(); i++) {				
					status.add(i, false);									            
	        }			
			setListAdapter(new ContactListAdapter(this));
		} catch (Exception ce) {
			SalesOrderConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
    
    public class ContactListAdapter extends BaseAdapter {
    	private boolean statuschkbx = true, statusfalse = false;
    	LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	String valStr="";
    	public ContactListAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);   
        }
       
        public int getCount() {
        	try {
				if(priceArrayList != null)
					return priceArrayList.size();
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
            	TextView matnumb;
            	TextView matdesc; 
            	 //LinearLayout llitembg2;
                LinearLayout llitembg;
            }        
           
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.salesorder_checkbox_list, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                //holder.cname = (TextView) convertView.findViewById(R.id.cname);    
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.litembg);    
               // holder.llitembg2 = (LinearLayout) convertView.findViewById(R.id.llitembg2);    
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(position%2 == 0)
				holder.llitembg.setBackgroundResource(R.color.item_even_color);
			else
				holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			linparams.topMargin = 5; 
			linparams.bottomMargin = 5;				
	       /* TableRow tr1 = new TableRow(SalesOrderCrtMaterialScreen.this.getApplicationContext());
			tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); 		*/		
            String text1Str = "", matIdStr = "", descStr = "", priceStr = "", idnpriceStr = "";           
			String imageUrlStr = "";				
            try {
            	if(priceArrayList != null){
            		int size = priceArrayList.size();
            		//chckBox = new CheckBox[size];	               
	                //valTV = new TextView[size];
	                SalesOrderConstants.showLog("listview ");
            		//for(int k= 0;k<size;k++){
            		stockMap = (HashMap<String, String>) priceArrayList.get(position); 
            		 holder.llitembg.removeAllViews();					           		  
            		  
            		/* LinearLayout llmclm = new LinearLayout(SalesOrderCrtMaterialScreen.this);
         			llmclm.setLayoutParams(new LinearLayout.LayoutParams(
         					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
         		 	llmclm.removeAllViews();	
       				llmclm.setOrientation(LinearLayout.HORIZONTAL); 	     */         				       				      
       				
            		 LinearLayout llmclmhorsntl = new LinearLayout(SalesOrderCrtMaterialScreen.this);
       				llmclmhorsntl.setLayoutParams(new LinearLayout.LayoutParams(
         					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
       				llmclmhorsntl.removeAllViews();	
       				llmclmhorsntl.setOrientation(LinearLayout.VERTICAL); 	    
       				
            		 matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);      	                  
            		if(valArrayList != null && valArrayList.size() > 0){            			            			          			            			
                        for(int i=0; i<valArrayList.size(); i++){	
                        	/*LinearLayout llmclm = new LinearLayout(SalesOrderCrtMaterialScreen.this.getApplicationContext());
                    		llmclm.removeAllViews();	
                    		llmclm.setOrientation(LinearLayout.HORIZONTAL);	*/
                    		
                    		/*LinearLayout llmclm2 = new LinearLayout(SalesOrderCrtMaterialScreen.this.getApplicationContext());
                    		llmclm2.removeAllViews();	
                    		llmclm2.setOrientation(LinearLayout.VERTICAL);	*/		 
                        	String nameVal = valArrayList.get(i).toString().trim();		                    					                    		
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
                    		//if(valueValStr != null && valueValStr.length() > 0){
                    		if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAPPABLE_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG)){	 
                    			valStr = (String) stockMap.get(metaValStr);	
                        		SapGenConstants.showLog("valStr:  "+valStr);
	                    		SapGenConstants.showLog("valStr:  "+valStr);		                    		
	                    		TextView valtv = new TextView(SalesOrderCrtMaterialScreen.this);     			                    			
                				valtv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	                 							                                           				
                    				 if(valStr != null && valStr.length() > 0){
 				        				if(valStr.length() > 25){
 				                    		String strSep = valStr.substring(0, 25);
 				                    		valtv.setText(strSep+"...");
 				                    	}else{
 				                    		if(labValStr!=null)
 			                    				valStr =labValStr+" "+valStr;
 				                    		valtv.setText(valStr);
 				                    	}
 									}else{
 										valtv.setText("");
 									}                    				
                    				 valtv .setMinWidth(100);                   			
                    				 valtv .setId(i);	                   				
                    				 valtv .setGravity(Gravity.CENTER_VERTICAL);
                    				 valtv .setPadding(20,0,0,0); 
                    				 llmclmhorsntl.addView(valtv);
                    				// llmclm.addView(llmclmhorsntl);
                    				 holder.llitembg.addView(llmclmhorsntl);
                    			
                    		}else if(valueValStr.equalsIgnoreCase(DBConstants.CHECKBOX_TAG)){
                    			/*SapGenConstants.showLog("2"); 	                   			
                    			//LinearLayout linearLayout = new LinearLayout(SalesOrderCrtMaterialScreen.this.getApplicationContext());             		                   		                    		
	                    		chckBox[i] = new CheckBox(SalesOrderCrtMaterialScreen.this.getApplicationContext());  
	                    		 chckBox[i].setId(i); 	                    		
	                    		 chckBox[i].setGravity(Gravity.RIGHT);
	                             //chckBox[i1].setLayoutParams(linparams); 
	                             chckBox[i].setButtonDrawable(R.drawable.checkboxcustom);
	        	                if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
	                            	LinearLayout.LayoutParams linlayparams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	                            	linlayparams1.gravity = Gravity.RIGHT;
	                            	linlayparams1.height = 40;
	                            	linlayparams1.width = 40;
	                            	chckBox[i].setLayoutParams(linlayparams1);
	                            }	        	               
	        	                chckBox[i].setOnCheckedChangeListener(new OnCheckedChangeListener(){					
	             					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {					
	             						SalesOrderConstants.showLog("boolean "+arg1);
	             						CheckBox cb = (CheckBox) arg0 ; 
	             						if(arg1==true){	             							
	             							cb.setChecked(arg1);
	             							status.set(position, true);			             							
	             						}					             							
	             						else{
	             							cb.setChecked(arg1);
	             							status.set(position, false);			             							
	             						}					             								             						
	             					}
	             				}); */
	        	                //LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);                    		                   		                    		
                    			final CheckBox chckBox = new CheckBox(SalesOrderCrtMaterialScreen.this.getApplicationContext());  
	                    		 chckBox.setId(i); 
	                    		// pos =i;
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
	             							cb.setChecked(arg1);
	             							status.set(position, true);			             							
	             						}					             							
	             						else{
	             							cb.setChecked(arg1);
	             							status.set(position, false);			             							
	             						}			            							        		             						
	             						/*if(selchkboxlist.contains(pId)){
	             							chckBox.setChecked(true);
	             						}else{
	             							chckBox.setChecked(false);
	             						}*/
	             					}
	             				}); 
	        	              
	        	                LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
		                         LinearLayout ll = new LinearLayout(SalesOrderCrtMaterialScreen.this);
		                         ll.setId(2131230808);
		                         ll.setGravity(Gravity.RIGHT);
		                         ll_params.topMargin= 20;
		                         ll.addView(chckBox);		                              				                      
			                     SapGenConstants.showLog("5"); 	        				                      
			                    // holder.llitembg.addView(llmclm);
	        	                holder.llitembg.addView(ll,ll_params);
			                   /*  SapGenConstants.showLog("5"); 	        				                      
			                     holder.llitembg.addView(chckBox[i]);*/
	        	             
                    		}                    	                  			 	              		
                        }
            		}
            	//}
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
    
    
    
    private void setWindowTitle(final String title){
    	try{
    		this.setTitle(title);
    	}
    	catch(Exception sdf){
    		SalesOrderConstants.showErrorLog("Error in setWindowTitle : "+sdf.getMessage());
    	}
    }//fn setWindowTitle
    
    private void onClose(boolean doneFlag){
    	try {
			//System.gc();
    		if(doneFlag == true){
				Intent addintent = new Intent(this, SalesOrderCrtMaterialScreen.class);
				//addintent.putExtra("selMatIds", selMatArrayList);
				addintent.putExtra("selMatIds", selMatArrayList);				
				setResult(RESULT_OK, addintent); 
    		}
			this.finish();
		} catch (Exception e) {
			SalesOrderConstants.showErrorLog("Error on onClose "+e.toString());
		}
    }//fn onClose
    
    private void ShowSOCreationScreen(boolean doneFlag){
    	try {
			//System.gc();
    		if(doneFlag == true){
				Intent addintent = new Intent(this, SalesOrderCrtMaterialScreen.class);
				addintent.putExtra("selMatIds", selMatArrayList);
				//addintent.putExtra("selMatIds", allDBMatArrayList);				
				setResult(RESULT_OK, addintent); 
    		}
			this.finish();
		} catch (Exception e) {
			SalesOrderConstants.showErrorLog("Error on onClose "+e.toString());
		}
    }//fn onClose
    
    public void showCustBtnListener(){
    	SalesOrderConstants.showLog("Show Cust btn clicked");
    	 HashMap<String, String> matMap = null;
    	if(selMatArrayList != null)
    		selMatArrayList.clear();
    	
    	/*if(listView != null){
    		int len = listView.getCount();*/
    		//SalesOrderConstants.showLog("List Count : "+len);
    		//SparseBooleanArray checked = listView.getCheckedItemPositions();
    		
    		//SalesOrderConstants.showLog("Checked List Count : "+checked.size());
    		String mattIdStr = "";
    		if(priceArrayList != null){        			       	    			
    				for (int i = 0; i < priceArrayList.size(); i++){
    					boolean getstatus = status.get(i).booleanValue();
    					SalesOrderConstants.showLog("getstatus : "+getstatus);
    				
        			if (getstatus==true) {
        				//if(i < priceArrayList.size()){
        					matMap = (HashMap<String, String>)priceArrayList.get(i);
        					if(matMap.containsKey(DBConstants.SO_MAKTX_COLUMN_NAME)){
    	                		String val = (String)matMap.get(DBConstants.SO_MAKTX_COLUMN_NAME);
    	                		matMap.remove(DBConstants.SO_MAKTX_COLUMN_NAME);
    	                		matMap.put(DBConstants.SO_MAIN_COL_ARKTX,val);
    	                	}
        					if((matMap.containsKey(DBConstants.SO_MEINH_COLUMN_NAME))){
        						String val = (String)matMap.get(DBConstants.SO_MEINH_COLUMN_NAME);
    	                		matMap.remove(DBConstants.SO_MEINH_COLUMN_NAME);
    	                		matMap.put(DBConstants.SO_MAIN_COL_VRKME,val);
        					}
        					matMap.put(DBConstants.SO_MAIN_COL_NETWR," ");
        					matMap.put(DBConstants.SO_MAIN_COL_WAERK," ");
        					selMatArrayList.add(matMap);
        					SalesOrderConstants.showLog("selMatArrayList Count : "+selMatArrayList.size());
        				}
        				/*
        				String item = (String)listView.getAdapter().getItem(i);
        				SalesOrderConstants.showLog("Selected item : "+item);
        				if(item != null){
    	            		int index = item.lastIndexOf(":");
    	            		//mattIdStr = item.substring(index+1);
    	            		mattIdStr = item.substring(0, index);
    	            		mattIdStr = mattIdStr.trim();
	            			SalesOrderConstants.showLog("mattIdStr : "+mattIdStr);
	        				selMatVector.addElement(mattIdStr);
    	            	}
    	            	*/
        			//}
        		}
    		}
    	//}
    	
    	if(selMatArrayList.size() > 0){
    		onClose(true);
    	}
    	else
    		SalesOrderConstants.showErrorDialog(SalesOrderCrtMaterialScreen.this, "Material Id is empty!");
    
    }//
   /* private OnClickListener showCustBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderConstants.showLog("Show Cust btn clicked");
        	if(selMatArrayList != null)
        		selMatArrayList.clear();
        	else
        		selMatArrayList = new selMatArrayList();
        	
        	if(listView != null){
        		int len = listView.getCount();
        		SalesOrderConstants.showLog("List Count : "+len);
        		//SparseBooleanArray checked = listView.getCheckedItemPositions();
        		
        		//SalesOrderConstants.showLog("Checked List Count : "+checked.size());
        		String mattIdStr = "";
        		if(priceArrayList != null){        			       	
        				SalesOrdProMattConstraints matObj = null;
        				for (int i = 0; i < len; i++){
        					boolean getstatus = status.get(i).booleanValue();
        					SalesOrderConstants.showLog("getstatus : "+getstatus);
        				
	        			if (getstatus==true) {
	        				if(i < priceArrayList.size()){
	        					matObj = ((SalesOrdProMattConstraints)priceArrayList.get(i));
	        					selMatArrayList.add(matObj);
	        					SalesOrderConstants.showLog("selMatArrayList Count : "+selMatArrayList.size());
	        				}
	        				
	        				String item = (String)listView.getAdapter().getItem(i);
	        				SalesOrderConstants.showLog("Selected item : "+item);
	        				if(item != null){
	    	            		int index = item.lastIndexOf(":");
	    	            		//mattIdStr = item.substring(index+1);
	    	            		mattIdStr = item.substring(0, index);
	    	            		mattIdStr = mattIdStr.trim();
		            			SalesOrderConstants.showLog("mattIdStr : "+mattIdStr);
		        				selMatVector.addElement(mattIdStr);
	    	            	}
	    	            	
	        			}
	        		}
        		}
        	}
        	
        	if(selMatArrayList.size() > 0){
        		onClose(true);
        	}
        	else
        		SalesOrderConstants.showErrorDialog(SalesOrderCrtMaterialScreen.this, "Material Id is empty!");
        }
    };*/
    /*private OnClickListener showsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(searchLinear != null)
        		searchLinear.setVisibility(View.VISIBLE);
        	
        	if(selectrelativeLT != null)
        		selectrelativeLT.setVisibility(View.GONE);
        	
        	setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE1));
        }
    };*/
    
    private OnClickListener showsrch_btnListener2 = new OnClickListener() {
        public void onClick(View v) {
        	onBackPressed();
        	
        	setWindowTitle(getString(R.string.SALESORDPRO_PLIST_TITLE1));
        }
    };
    
    
    public void onBackPressed() {
		this.finish();		
	}//fn onBackPressed
    
    
    /*private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	if(customerET.getText().toString().trim().length() != 0) {
				try{					
					custSearchStr = customerET.getText().toString().trim();
					SalesOrderConstants.showLog("customer : "+custSearchStr);								
			      isConnAvail = SapGenConstants.checkConnectivityAvailable(SalesOrderCrtMaterialScreen.this);//check for internet connection
					
					if(isConnAvail!=false)
						initPriceSoapConnection();
					else
						initDBConnection(custSearchStr);														
				}
				catch(Exception wsfsg){
					SalesOrderConstants.showErrorLog("Error in Material Search : "+wsfsg.toString());
				}
			}
        	else
        		SalesOrderConstants.showErrorDialog(SalesOrderCrtMaterialScreen.this, "Enter Material name to search.");
        }		
    };
    */
    public void MaterialSearch(){ 
    	if(flag2 !=1){
    		custSearchStr =matStr;	
    	}						
		isConnAvail = SapGenConstants.checkConnectivityAvailable(SalesOrderCrtMaterialScreen.this);//check for internet connection
		
		if(isConnAvail!=false)
			initPriceSoapConnection();
		else
			initMattDBConnection(custSearchStr);
		
    }//MaterialSearch
    
    private void initPriceSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, SalesOrderConstants.APPLN_NAME_STR);
            //C0[0].Cdata = "DEVICE-ID:353490044287783:DEVICE-TYPE:BB:APPLICATION-ID:SALESPRO";
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]PRODUCT-SEARCH-BROADER-RESULT-VIEW-A[.]VERSION[.]0";
            C0[3].Cdata = "ZGSEVDST_MTRLSRCH01[.]"+custSearchStr;
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SalesOrderConstants.showLog(request.toString());
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request); 
          
            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
        }
        catch(Exception asd){
        	SalesOrderConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initPriceSoapConnection   
    
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
        				pdialog = ProgressDialog.show(SalesOrderCrtMaterialScreen.this, "", getString(R.string.COMPILE_DATA),true);
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
	    			initMattDBConnection(custSearchStr);
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };   

    public void updateServerResponse(SoapObject soap){	
        String[] resArray = null;
        String[] metaProductLength = null;
        String[] docTypeArray = new String[3];
		String[] productArray = null;
        try{ 
        	if(soap != null){    			
    			if(priceArrayList != null)
    				priceArrayList.clear();    
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
	                                    		DBConstants.MATT_LIST_DB_TABLE_NAME = docTypeStr;
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
	 	                           priceArrayList.add(productArray);
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
			                                SapGenConstants.showErrorDialog(SalesOrderCrtMaterialScreen.this, serverErrorMsgStr);
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
        	SapGenConstants.showLog("priceArrayList Size : "+priceArrayList.size());
        	if(priceArrayList.size() > 0){    			    						
            	try {
            		Thread t = new Thread() 
        			{
        	            public void run() 
        				{
                			try{
                				insertMattDataIntoDB();
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
        	}
        }
    }//fn updateServerResponse
    
    final Runnable custview = new Runnable(){
        public void run()
        {
        	try{
        		initMattDBConnection(custSearchStr);
        	} catch(Exception sfe){
        		SapGenConstants.showErrorLog("Error in productview:"+sfe.toString());
        	}
        }	    
    };   
    
    private void insertMattDataIntoDB() {
    	try {
			if(metaProdListArray != null && metaProdListArray.size() > 0){
				if(DBConstants.MATT_LIST_DB_TABLE_NAME != null && DBConstants.MATT_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.MATT_LIST_DB_TABLE_NAME;
	    		}
				if(priceArrayList != null && priceArrayList.size() > 0){
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
						dbObj = new DataBasePersistent(this, DBConstants.MATT_LIST_DB_TABLE_NAME);
					dbObj.dropTable(SalesOrderCrtMaterialScreen.this);
					boolean isExits = dbObj.checkTable();
					SapGenConstants.showLog(DBConstants.MATT_LIST_DB_TABLE_NAME+" isExits "+isExits);
					if(!isExits){
						dbObj.creatTable(SalesOrderCrtMaterialScreen.this);
					}	
					if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
	    				if((custListCount == 0) && (priceArrayList.size() == 0)){
	    					//SalesOrderDBOperations.deleteAllTableDataFromDB(SalesOrderMainScreenTablet.this, SalesOrderCP.SO_HEAD_OP_CONTENT_URI);
	    					dbObj.clearListTable();
	        			}
	    				else if((custListCount > 0) && (priceArrayList.size() > 0)){
	    					dbObj.clearListTable();
	    					insertMaterialListDataIntoDB(priceArrayList);
	        			}
	    			}
	    			if(custListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
	    				if((custListCount == 0) && (priceArrayList.size() == 0)){
	    					dbObj.clearListTable();
	        			}
	    				else if((custListCount > 0) && (priceArrayList.size() == 0)){
	    					//initDBConnection();
	        			}
	    				else if((custListCount > 0) && (priceArrayList.size() > 0)){
	    					dbObj.clearListTable();
	    					insertMaterialListDataIntoDB(priceArrayList);
	    						        				    			
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
    
    public void insertMaterialListDataIntoDB(ArrayList custListArray){		
		try {
			if(dbObj != null)
				dbObj = null;
			if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.MATT_LIST_DB_TABLE_NAME);			
			if(custListArray != null && custListArray.size() > 0){
				dbObj.insertDetails(custListArray);
			}
			dbObj.closeDBHelper();
		} catch (Exception e) {
			dbObj.closeDBHelper();
			SapGenConstants.showErrorLog("On insertCustomerListDataIntoDB : "+e.toString());
		}        	
	}//fn insertCustomerListDataIntoDB
    
    private void initMattDBConnection(String customerIdStr){
  		try{
  			//setTitleValue();
  			if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
  				DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
      		}					
              if(dbObj != null)	    
            	  dbObj = null;
  			if(dbObj == null)
  				dbObj = new DataBasePersistent(this,DBConstants.MATT_LIST_DB_TABLE_NAME);
  			dbObj.getColumns();	  
  			/*if(offlinemode ==1){					
              	SapGenConstants.showLog("offlinemode"+offlinemode);
              	dbObj.getColumns();	         	            		            	
              }else{
              	dbObj.setTableName_ColumnName(this,DBConstants.TABLE_SALESORDER_HEAD_LIST,metaHeadArray);
              }	*/							
  			tableExits = dbObj.checkTable();
  			if(tableExits)
  				priceArrayList = dbObj.readListDataFromDB(this);
  		
  				//mattCopyArrList = (ArrayList)mattArrList.clone();
  			SapGenConstants.showLog("library database table name :"+DBConstants.DB_TABLE_NAME);
  			dbObj.closeDBHelper();  
  			
  			SapGenConstants.showLog("priceArrayList size:"+ priceArrayList.size());
  			//HashMap<String, String> stockMap = null;
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
    private void cleanUpDatabase() {    	
    	SalesOrderDBOperations.deleteAllTableDataFromDB(SalesOrderCrtMaterialScreen.this, SalesOrderCP.SO_MATT_CONTENT_URI);    	
    }//cleanUpDatabase
 

	private void prefillCustomerData(){
    	try{
    		if(priceArrayList != null){
    			if(priceArrayList.size() > 0){
    	    		custDetArr = getCustomerList();
    	    		if(custDetArr != null){  
    	    			if(flag2 ==1)
    	    				searchLinear.setVisibility(View.GONE);
    	    			showlinear = (LinearLayout) findViewById(R.id.actionbarll);	
    			    	 if(showlinear != null)
    			    		 showlinear.setVisibility(View.VISIBLE);
    			    	 showlinear.removeAllViews();					
    			    	 showlinear.setOrientation(LinearLayout.HORIZONTAL);
    					 displayUI(DBConstants.PRODUCT_SEARCH_RESULT_ACTION_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, showlinear);	
      	    	           listviewcall();   
      	 	        	}
    	    		}    	    		
    			}
    		//}
    	}
    	catch(Exception adf){
    		SalesOrderConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
    
	/*public void getSingleMaterial(){
    	if(selMatArrayList != null)
    		selMatArrayList.clear();
    	else
    		selMatArrayList = new ArrayList();      
    	if(priceArrayList != null){
			SalesOrdProMattConstraints matobj=null;   
			matobj = ((SalesOrdProMattConstraints)priceArrayList.get(0));
			selMatArrayList.add(matobj);
			if(selMatArrayList.size() > 0){
        		onClose(true);
        	}
	    	else
	    		SalesOrderConstants.showErrorDialog(SalesOrderCrtMaterialScreen.this, "Material Id is empty!");
    	}
    }//getSingleMaterial
	*/
	 private ArrayList getCustomerList(){
	    	String choices[] = null;
	        try{
	        	if(custDetArr!=null)
	        		custDetArr.clear();
	            if(priceArrayList != null){
	            	 HashMap<String, String> customerMap = null; 
	            	//SalesOrdProCustConstraints empObj = null;
	                //String nameStr = "", idstr="", combStr="";                
	                int arrSize = priceArrayList.size();
	                choices = new String[arrSize];
	                
	                for(int h=0; h<arrSize; h++){
	                	customerMap = (HashMap<String, String>) priceArrayList.get(h);                	
	                    if(customerMap != null){
	                    	nameStr = (String) customerMap.get(DBConstants.NAME1_COLUMN_NAME);  
	                    	idstr = (String) customerMap.get(DBConstants.KUNNR_COLUMN_NAME);  
	                        combStr = nameStr+":"+idstr;	                       
	                        //choices[h] = combStr;
	                        custDetArr.add(combStr);
	                    }
	                }
	            }
	            SalesOrderConstants.showLog("Size of custDetArr : "+custDetArr.size());
	        }
	        catch(Exception sfg){
	        	SalesOrderConstants.showErrorLog("Error in getCustomerList : "+sfg.toString());
	        }
	        return custDetArr;
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
			SalesOrderConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
}//End of class SalesOrderCrtMaterialScreen

