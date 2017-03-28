package com.globalsoft.SalesOrderLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.Database.SalesOrderDBOperations;
import com.globalsoft.SalesOrderLib.SalesOrderItemScreen.SalesOrderItemAdapter;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesOrderItemScreenTablet extends Activity implements TextWatcher  {

	private TextView[] mattTxtView, materialTxtView, materialDescTxtView, salesOrderHeaderTV, soitemvaluHeaderTV, salesOrderTblHeadLblTV, itemvalTV, valTV;
	private LinearLayout headerlinear, headervaluelinear, sosummaryll, sosearchlinear,navigationrl,offlinetitlelayout;
	private TableLayout tl, tlValue;
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3;
	private TextView tableHeaderTV4, tableHeaderTV5, tableHeaderTV6, tableHeaderTV7;
	private TextView custNameTV, soNoTV, sovalueTV, reqDatTV,customerTV, myTitle;
	private EditText searchET ;
	protected CharSequence[] _options = null;
	private TextView[] headLblTV;
	private TableRow.LayoutParams linparams11 = null;
	private DataBasePersistent dbObjUIConf = null;
	private DataBasePersistent dbitemObj = null;
	private DataBasePersistent dbObj = null;
	
	
	private String searchStr = "", title="", selectedSoNumb ="", selectdLabel= "", labVal = "", valStr="",custmoerStr="", valueStr="",searchbarhint= "";
	private boolean sortFlag = false, sortItemFlag = false, sortMattFlag = false, searchflag = true, sortQtyFlag = false;
	
    private int sortIndex = -1,pos=0, itemcols=0, flag=-1, offlinemode,nextflag=0,prevflag=0,firstflag=0;
    private boolean tableExits = false;
    private boolean sortProdNameFlag = false;
    private TextView[] salesOrdLblTV, salesOrdItemLblTV;
	private EditText[] salesOrdValET, salesOrdItemValET;
	
	private ImageButton showPrevBtn, showNextBtn;
	private ImageView btprev,btnext;
	
	//private SalesOrdProHeadOpConstraints stkheadCategory = null;
	private SalesOrdProItemOpConstraints itemCategory = null;
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3;
	private int dispwidth = 300;
	private ListView listView;
	private SalesOrderItemAdapter salesOrderItemlistAdapter;	
	HashMap<String, String> itemblockMap = null; 	
	HashMap<String, String> itemheaderMap = null; 
	
	private ArrayList headeritemdetailList = new ArrayList();
	private ArrayList stocksItemArrList1 = new ArrayList();
	private ArrayList soItemLabel = new ArrayList();
	private ArrayList mattItemDetailArrList = new ArrayList();
	private ArrayList stocksItemCopyArrList1 = new ArrayList();
	private ArrayList sovalArrayList = new ArrayList();
	private ArrayList soLabelArrayList = new ArrayList();
	private ArrayList itemHeadList = new ArrayList();
	//private ArrayList itemHeadValuesList = new ArrayList();
	private ArrayList SOHeadList = new ArrayList();
	private ArrayList SOItemList = new ArrayList();
	private ArrayList itemBlckLabelList = new ArrayList();
	//private ArrayList itemBlckValList = new ArrayList();
	private ArrayList<String> CustHeaderList = new ArrayList<String>();
	private ArrayList allitemDetailList = new ArrayList();
	//private ArrayList subviewLftPartDetailList = new ArrayList();
	//private ArrayList subviewRightPartList = new ArrayList();
	//private ArrayList itemheadLabelList = new ArrayList();
	private ArrayList headlblDsplydesc = new ArrayList();
	private ArrayList itemlblDsplydesc = new ArrayList();
	private ArrayList valArrayList = new ArrayList();
	HashMap<String, String> labelMap = null; 
	HashMap<String, String> itemHeadValuesList = null; 
	HashMap<String, String> itemBlckValList = null; 
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            /*requestWindowFeature(Window.FEATURE_LEFT_ICON);
            SapGenConstants.setWindowTitleTheme(this);
            setContentView(R.layout.salesorderitem);*/
            
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
            overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
            //overridePendingTransition(R.anim.fadin, R.anim.fadeout);
	        setContentView(R.layout.salesorderitemfragment); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
	        myTitle = (TextView) findViewById(R.id.myTitle);
			//myTitle.setText(title);
	        offlinetitlelayout = (LinearLayout) findViewById(R.id.main_frags);
	       
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			myTitle.setText("Sales Order Item Detail");
			selectedSoNumb = this.getIntent().getStringExtra("selectdSoNumb");
			selectdLabel = this.getIntent().getStringExtra("selectdLabelStr");
			SapGenConstants.showLog("selectdLabel: "+selectdLabel);
			SapGenConstants.showLog("selectedSoNumb: "+selectedSoNumb);
			offlinemode =  this.getIntent().getIntExtra("offlinestr", 0);
			if(CustHeaderList!=null)
				CustHeaderList.clear();
			CustHeaderList =  (ArrayList) SalesOrderConstants.metaheadlistArray.clone();			
	        stocksItemArrList1 = (ArrayList) SalesOrderConstants.stocksItemMainArrList.clone();	       	      
	       /* if(stkCategory != null){
				String sumIconStr = stkCategory.getStatusSummary().trim();   
				if(sumIconStr.equalsIgnoreCase("R"))
					setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_red);
                else if(sumIconStr.equalsIgnoreCase("G"))
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_green);
                else if(sumIconStr.equalsIgnoreCase("Y"))
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_yellow);
                else
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_black);
				
				//this.setTitle(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+stkCategory.getDocumentNo().trim()+" "+getString(R.string.SALESORDPRO_SOLIST_ITEM_TITLE1));
				myTitle.setText(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+stkCategory.getDocumentNo().trim()+" "+getString(R.string.SALESORDPRO_SOLIST_ITEM_TITLE1));
			}
			else{
	        	//setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_black);
				//this.setTitle(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+" "+getString(R.string.SALESORDPRO_SOLIST_ITEM_TITLE1));
				myTitle.setText(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+" "+getString(R.string.SALESORDPRO_SOLIST_ITEM_TITLE1));
			}*/	       
	        //setTitleValue();	      
	        initLayout2();
        } catch (Exception de) {
        	SalesOrderConstants.showErrorLog("Error in onCreate:"+ de.toString());
        }
    }
        
    
    private void initLayout2(){
    	try{    		
	        dispwidth = SapGenConstants.getDisplayWidth(this);
	        navigationrl = (LinearLayout)findViewById(R.id.showlinear);   
	        initLayout();
			//ListviewAdapter();
			//showStockListDetailScreen(0);
	      firstCallstack(0);
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
       
    private void initHeaderLayout(){
    	try{   			
    		flag=1;    		
    		/*itemheadLabelList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.SO_SUMMARY_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("itemheadLabelList size: "+itemheadLabelList.size());
    		soItemLabel = DataBasePersistent.readAllTabletLablesFromDB(this,DBConstants.SO_SUMMARY_BLOCK,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);    		
 	        SalesOrderConstants.showLog("soItemLabel: "+soItemLabel.size());*/
 	       
    		getAllItemDetails(CustHeaderList,DBConstants.TABLE_SALESORDER_HEAD_LIST);   
    		//itemheaderMap = (HashMap<String, String>) headeritemdetailList.get(0); 
    		
    		sosummaryll = (LinearLayout) findViewById(R.id.sosummaryLL);
    		sosummaryll.removeAllViews();
    		sosummaryll.setOrientation(LinearLayout.VERTICAL);
    		sosummaryll.removeAllViews();
    		displayUI(DBConstants.SO_SUMMARY_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, sosummaryll);
    		sosearchlinear = (LinearLayout) findViewById(R.id.sosearchlinear);
    		sosearchlinear.removeAllViews();
    		sosearchlinear.setOrientation(LinearLayout.VERTICAL);
    		sosearchlinear.removeAllViews();
    		displayUI(DBConstants.DEVICE_OVERVIEW_SEARCH, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, sosearchlinear);
						
    		/*headerlinear = (LinearLayout)findViewById(R.id.headerl); 
    		headerlinear.setGravity(Gravity.CENTER_VERTICAL);
    		salesOrderHeaderTV = new TextView[soItemLabel.size()];
    		soitemvaluHeaderTV = new TextView[soItemLabel.size()];
    		
    		for(int k=0;k<itemheadLabelList.size();k++){ 
         		headervaluelinear = new LinearLayout(this);
         		salesOrderHeaderTV[k] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	          	         					
         		labVal = soItemLabel.get(k).toString().trim();        		
         		//SalesOrderConstants.showLog("labVal: "+labVal);
         		salesOrderHeaderTV[k].setText(labVal); 
         		
         		//getting all so item detail values
         		soitemvaluHeaderTV[k] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	
         		salesOrderHeaderTV[k].setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);         		
         		//SapGenConstants.showLog("labelStr: "+labelStr);	
         		String labelStr = itemheadLabelList.get(k).toString();
         		String str = (String) itemheaderMap.get(labelStr);         		
         		soitemvaluHeaderTV[k].setText(str);   
         		headervaluelinear.addView(salesOrderHeaderTV[k]);
         		headervaluelinear.addView(soitemvaluHeaderTV[k]);     		
         		headerlinear.addView(headervaluelinear);       		         		         	
         	}        	  */       	    		
			//updateUIElements();
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initHeaderLayout : "+sfg.toString());
    	}
    }//fn initHeaderLayout
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
    		DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			//Detail-W/S-Main value
			valArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("valArrayList : "+valArrayList.size());			 
			if(valArrayList != null && valArrayList.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valArrayList.size());
				itemheaderMap = (HashMap<String, String>) headeritemdetailList.get(0); 
			    valTV = new TextView[valArrayList.size()];
			    for(int i = 0; i < valArrayList.size(); i++){       
			    	String secLine = "";        
			    	ArrayList list = (ArrayList) valArrayList.get(i);
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
			                 	String valStr = (String) itemheaderMap.get(metaLabel);      
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

			        					String trgStr = (String) itemheaderMap.get(metaTrgActStr.toString().trim());
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
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG)){
			                 			EditText et = new EditText(this);
			                 			et.setText("");
			                 			//et.setPadding(5,5,5,5);
			                 			et.setWidth(58);
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				if(labStr != null && labStr.length() > 0){
			                 					et.setHint(labStr);
			                 				}
			                 			}	
			                 			et.addTextChangedListener(new TextWatcher(){
			                    	        public void afterTextChanged(Editable s) {
			            						String updatedQty = s.toString().trim();
			            				 		SapGenConstants.showLog("Text : "+s.toString());	
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
			                 		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_SEARCHBAR_TAG)){
			                 			EditText et = new EditText(this);
			                 			et.setText("");
			                 			//et.setPadding(5,5,5,5);
			                 			et.setWidth(40);
			                 			et.setLayoutParams(new LayoutParams(500,30));
			                 			et.setTextColor(getResources().getColor(R.color.black));
			                 			et.setBackgroundResource(R.drawable.editext_border);
			                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
			                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
			                 			if(lab){
			                 				String labStr = (String)labelMap.get(metaLabel);
			                 				if(labStr != null && labStr.length() > 0){
			                 					et.setHint(labStr);
			                 					et.setTextSize(12);
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
			                 			/*LinearLayout ll = new LinearLayout(this);
			        				    ll.setOrientation(LinearLayout.VERTICAL);
			        				    ll.addView(et);
			        				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			       				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
			        				    dynll.addView(ll, layoutParams);*/
			                 			dynll.addView(et);
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
												//SapGenConstants.showLog("ordQty  "+ordQty);
												//cartBtnAction();
											}	
				                        });
		                     			dynll.addView(bt);
		                     		}                            		
			                 	}            				    
							}else if (list.size() > 1){
			         			final LinearLayout llmclm = new LinearLayout(this);
			         			llmclm.setOrientation(LinearLayout.HORIZONTAL);
			         			//llmclm.removeAllViews();
			         			
			         			/*LinearLayout llmclm2 = new LinearLayout(this);
			         			//llmclm2.removeAllViews();
			         			llmclm2.setOrientation(LinearLayout.HORIZONTAL);*/
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
			                     	String valStr = (String) itemheaderMap.get(metaLabel);                                 	
			                     	secLine += " "+valStr;
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
			            					String trgStr = (String) itemheaderMap.get(metaTrgActStr.toString().trim());
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
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_SEARCHBAR_TAG)){
				                 			EditText et = new EditText(this);
				                 			et.setText("");
				                 			//et.setPadding(5,5,5,5);
				                 			et.setWidth(40);
				                 			et.setLayoutParams(new LayoutParams(500,30));
				                 			et.setTextColor(getResources().getColor(R.color.black));
				                 			et.setBackgroundResource(R.drawable.editext_border);
				                 			et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				                 			Drawable img = this.getResources().getDrawable( R.drawable.search_icon );
				                 			et.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
				                 			if(lab){
				                 				String labStr = (String)labelMap.get(metaLabel);
				                 				if(labStr != null && labStr.length() > 0){
				                 					et.setHint(labStr);
				                 					et.setTextSize(12);
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
				                 			dynll.addView(et);
				                 			/*LinearLayout ll = new LinearLayout(this);
				        				    ll.setOrientation(LinearLayout.VERTICAL);
				        				    ll.addView(et);
				        				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				       				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
				        				    dynll.addView(ll, layoutParams);*/
				                 		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_EDITABLE_TAG)){
			                     			EditText et = new EditText(this);
			                     			et.setText("");
			                     			//et.setPadding(5,3,5,3);
			                     			et.setWidth(58);
			                     			et.setTextColor(getResources().getColor(R.color.black));
			            					et.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
			                     			et.setBackgroundResource(R.drawable.editext_border);
			                     			if(lab){
			                     				String labStr = (String)labelMap.get(metaLabel);
			                     				if(labStr != null && labStr.length() > 0){
			                     					et.setHint(labStr);
			                     				}
			                     			}	
			                     			et.addTextChangedListener(new TextWatcher(){
				                    	        public void afterTextChanged(Editable s) {
			                						String updatedQty = s.toString().trim();
			                				 		SapGenConstants.showLog("Text : "+s.toString());	
			                				 		//ordQty = updatedQty;
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
													/*SapGenConstants.showLog("ordQty  "+ordQty);
													cartBtnAction();*/
												}	
					                        });
			            				    llmclm.addView(bt);
			                     		}/*else if(metaLabel.equalsIgnoreCase(DBConstants.SO_ITEM_NAVIGATION_ACTION_LEFT_TAG)){
			                     						                     				
			                     				 btprev = new ImageView(this);					                     				
					                     			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					                        		layoutParamsiv.gravity=Gravity.LEFT;			                        		
					                        		btprev.setLayoutParams(layoutParamsiv);		                    			
					                        		btprev.setBackgroundResource(R.drawable.prev);	
					                     			//bt.setB
					                        		btprev.setOnClickListener(new View.OnClickListener() {
														public void onClick(View view) {															
															Prevfunc();
														}	
							                        });
					                     			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					                     					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					                     			//layoutParams.gravity=Gravity.RIGHT;	
					                     			layoutParams.alignWithParent=Align.RIGHT;		
					                        		
					            				    llmclm.addView(btprev);			                     					                     			
			                     		}else if(metaLabel.equalsIgnoreCase(DBConstants.SO_ITEM_NAVIGATION_ACTION_RIGHT_TAG)){			                     				                     				
			                     				btnext = new ImageView(this);						                     			
				                     			LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				                        		layoutParamsiv.gravity=Gravity.RIGHT;
				                        		layoutParamsiv.leftMargin=560;
				                        		btnext.setLayoutParams(layoutParamsiv);		                 			
				                        		btnext.setBackgroundResource(R.drawable.next);			                     			
				                        		btnext.setOnClickListener(new View.OnClickListener() {
													public void onClick(View view) {	
														Nextfunc();														
													}													
						                        });
				                     			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				                     					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				                     			//layoutParams.gravity=Gravity.RIGHT;	
				                     			layoutParams.alignWithParent=Align.RIGHT != null;
				            				    llmclm.addView(bt,layoutParams);	
				                        		
				            				    llmclm.addView(btnext);
			                     			
			                     			
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
 
    private void Prevfunc() {
    	SapGenConstants.showLog("position onclick next button"+pos);
    	if(pos>=0){
			//prevflag=1;
			pos--;
			btnext.setVisibility(View.GONE);
			initItemLayout();
		}else{
			SapGenConstants.showLog("Left else");			
			btprev.setVisibility(View.GONE);
			btnext.setVisibility(View.VISIBLE);
			//llmclm.removeAllViews();
			displayUI(DBConstants.SO_ITEM_NAVIGATION_ACTION_TAG,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG,navigationrl);
		}
		
	}	//Nextfunc
    
    private void Nextfunc() {
    	SapGenConstants.showLog("position onclick next button"+pos);
    	if(pos<=mattItemDetailArrList.size() && pos>=0 ){
			//nextflag=1;
			pos++;
			btprev.setVisibility(View.GONE);
			initItemLayout();
		}else{						
			displayUI(DBConstants.SO_ITEM_NAVIGATION_ACTION_TAG,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG,navigationrl);
		}
		
	}	//Nextfunc
    private void setTitleValue() {
    	try {
    		dbObjUIConf=null;
    		if(dbObjUIConf == null)
				dbObjUIConf = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		
    		String title = dbObjUIConf.getTitle(DBConstants.DEVICE_TYPE_WIDE_DETAIL_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
    			 if(title.indexOf(SapGenConstants.title_offline) > 0){
    				 offlinetitlelayout.setBackgroundResource(R.drawable.llborder);
    				 offlinetitlelayout.setPadding(5, 5, 5, 5);
        		 }
        		//SapGenConstants.showLog("title: "+title);
    		}   		    		
    		//searchbarhint = dbObjUIConf.getSearchBarHint(DBConstants.DEVICE_OVERVIEW_SEARCH, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_SEARCHBAR_TAG);
    		if(searchbarhint != null && searchbarhint.length() > 0){
    			searchET.setHint(searchbarhint);
        		SapGenConstants.showLog("SearchHint: "+searchbarhint);
    		}    		
    		dbObjUIConf.closeDBHelper();
    		
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
    
    private void getAllItemDetails(ArrayList stockitemlist, String tablename) {					
		try{	
			if(DBConstants.TABLE_SALESORDER_CONTEXT_LIST != null && DBConstants.TABLE_SALESORDER_CONTEXT_LIST.length() > 0){
				DBConstants.DB_TABLE_NAME = DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
    		}		
			if(dbitemObj!=null)
				dbitemObj = null;
			
			if(dbitemObj == null){				
				dbitemObj = new DataBasePersistent(this,tablename);				
			}
			if(offlinemode ==1 || stockitemlist.size()==0){
				SapGenConstants.showLog("offlinemode"+offlinemode);					
				dbitemObj.getColumns();	 
			}else{
				dbitemObj.setTableName_ColumnName(this,tablename,stockitemlist);
			}						
			if(flag ==0){
				tableExits = dbitemObj.checkTable();
				if(tableExits)
					mattItemDetailArrList = dbitemObj.readDataSelectedStrListDataFromDB(this,selectedSoNumb,selectdLabel);					
					 stocksItemCopyArrList1 = (ArrayList) mattItemDetailArrList.clone();
					SOItemList = dbitemObj.readListDataFromDB(this);					
			}else{
				tableExits = dbitemObj.checkTable();
				if(tableExits)
					headeritemdetailList = dbitemObj.readDataSelectedStrListDataFromDB(this,selectedSoNumb,selectdLabel);					
			}			
			/*if(mattItemDetailArrList != null)
				mattCopyArrList = (ArrayList)mattItemDetailArrList.clone();*/
			//SapGenConstants.showLog(" item library database table name :"+DBConstants.DB_TABLE_NAME);
			
			if(mattItemDetailArrList != null)
				mattItemDetailArrList = (ArrayList)mattItemDetailArrList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
			dbitemObj.closeDBHelper();  
			
			//SapGenConstants.showLog("mattItemDetailArrList size:"+ mattItemDetailArrList.size());
			HashMap<String, String> stockMap = null;
            String matIdStrVal = "", id = "", desc = "";
        	if(mattItemDetailArrList != null && mattItemDetailArrList.size() > 0 ){
        		_options =  new CharSequence[mattItemDetailArrList.size()];
				for (int i = 0; i < mattItemDetailArrList.size(); i++) {
					stockMap = (HashMap<String, String>) mattItemDetailArrList.get(i);  
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
        	dbitemObj.closeDBHelper();	  		    				
		}catch(Exception sggh){				
			dbitemObj.closeDBHelper();				
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}			
}//         
    
    private void initLayout(){
    	try{    		
	        initHeaderLayout();
	        flag=0;
	        getAllItemDetails(stocksItemArrList1,DBConstants.TABLE_SALESORDER_ITEM_LIST);  
	        tl = (TableLayout)findViewById(R.id.sorditemtbllayout1);	
    		if(tl != null)
    			tl.removeAllViews();	
	        dispwidth = SapGenConstants.getDisplayWidth(this);
	        emptyvectors();
    		/*searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);*/
	        DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			sovalArrayList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.ITEM_SUBVIEW_TBLVW, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("sovalArrayList size: "+sovalArrayList.size());
			
			soLabelArrayList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.ITEM_SUBVIEW_TBLVW, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("soLabelArrayList size: "+soLabelArrayList.size());
			
			if(sovalArrayList != null && sovalArrayList.size() > 0){
				int cols =  soLabelArrayList.size();					
				dispwidth = SapGenConstants.getDisplayWidth(this);
				if(dispwidth < SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
					tl.setColumnStretchable(1, true);
				//headLblTV = new TextView[valArrayList.size()];  
				headLblTV = new TextView[soLabelArrayList.size()]; 
				TableRow tr1 = (TableRow) getLayoutInflater().inflate(R.layout.so_table_row, null);
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
					labVal = soLabelArrayList.get(i).toString().trim();
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
					headLblTV[i] = (TextView) getLayoutInflater().inflate(R.layout.so_template_textview, null);
					//headLblTV[i].setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					if(valueValStr != null && valueValStr.length() > 0){					
							headLblTV[i].setWidth(100);   
							headLblTV[i].setHeight(30);
					}					
					headLblTV[i].setId(i);
					headLblTV[i].setOnClickListener(new View.OnClickListener() {
						public void onClick(View view) {
							if(!valueValStrCk.equalsIgnoreCase(DBConstants.CHECKBOX_TAG) && !valueValStrCk.equalsIgnoreCase(DBConstants.VALUE_ICON_TAG)){
								//sortItemsAction(metaValue);
							}
						}	
                    });		
					SapGenConstants.showLog("labValStr:  "+labValStr);
					SapGenConstants.showLog("metaValStr:  "+metaValStr);
					SapGenConstants.showLog("typeValStr:  "+typeValStr);
					headLblTV[i].setGravity(Gravity.LEFT);	
					if(!metaValStr.equalsIgnoreCase(DBConstants.SO_MAIN_COL_VBELN)){
						SapGenConstants.getUnderlinedString(labValStr);
 					}				 			       
					
					if(labValStr != null && labValStr.length() > 0){
						headLblTV[i].setText(labValStr);
					}else{
						headLblTV[i].setText(" ");
					}					
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
						headLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_HEADER);								
					tr1.addView(headLblTV[i]);						
				}											
				tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				drawSubLayout();	
			}
						
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    private void drawSubLayout(){
		try{
			tlValue = (TableLayout)findViewById(R.id.sorditemtbllayout3);		
		  	if(tlValue != null)
		  		tlValue.removeAllViews();
			/*tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			tl.removeAllViews();*/
		  	 HashMap<String, String> itemMap = null; 		        	       
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
						labelWidth = 120;
						editWidth = 100;
					}
				}
				SapGenConstants.showLog("labelWidth : "+labelWidth+" : editWidth : "+editWidth);				
					if(mattItemDetailArrList != null){
						 SapGenConstants.showLog("mattItemDetailArrList "+mattItemDetailArrList.size());  
						String valStr = "", statusStr = "", matIdStr = "",labelStr = "";                
		                int rowSize = sovalArrayList.size();                
		                SapGenConstants.showLog("1");  
		               		              		              
							for (int i = 0; i < sovalArrayList.size(); i++) {							
			                    tr = new TableRow(this);        
			                    itemMap = (HashMap<String, String>) mattItemDetailArrList.get(i); 										
	                    		matIdStr = (String) itemMap.get(DBConstants.SO_HEAD_COL_VBELN);    
	                    	
	                    		//ordrnumbStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_NETWR);	                    			                    		
		    					
				               // itemvalTV = new TextView[sovalArrayList.size()];
			                    if(itemMap != null){		                    	
			                    	for (int i1 = 0; i1 < soLabelArrayList.size(); i1++) {
				                    	try {			
				                    		valStr = sovalArrayList.get(i1).toString().trim();			                    		
				                    		 //valStr = (String) itemMap.get(labelStr);	
				                    		 SapGenConstants.showLog("valStr: "+valStr);  
										} catch (Exception e) {
											SapGenConstants.showErrorLog("Error on drawSubLayout : "+e.toString());
										} 			                   				                    	
				                    	//itemvalTV[i1] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
				                    	TextView valtv = new TextView(SalesOrderItemScreenTablet.this);     			                    			
    	                				valtv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	
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
											String valStrDis = (String) itemMap.get(metaStr);
											SapGenConstants.showLog("valStrDis: "+valStrDis);  
											valtv.setText(valStrDis);											
					                        	  SapGenConstants.showLog("2");  
					                        	final int pId = i;
					                        	//itemvalTV[i1].setGravity(Gravity.CENTER);					                        	
					                        	valtv.setWidth(100);					                        	
					                        	valtv.setId(i1);			
					                        	valtv.setGravity(Gravity.LEFT);	
					                        	valtv.setOnClickListener(new View.OnClickListener() {
														public void onClick(View view) {
															pos= pId;
															initItemLayout();
														}	
							                        });			                    	 					
						    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
						    						//itemvalTV[i1].setTextSize(SapGenConstants.TEXT_SIZE_ITEM_TABLE_ROW);						    						
						    					}			    							    					 
						    					tr.addView(valtv);					    					    						
			                    	     }         			                    	
			    					if(i%2 == 0)
			    						tr.setBackgroundResource(R.color.item_even_color);
						            else
						            	tr.setBackgroundResource(R.color.item_odd_color);				    					  
			    					tlValue.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			                    }	
							}
					}				
		}
		catch(Exception asgf){
			SalesOrderConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
    
    private void sortItemsAction(String metaName){
		try{
			/*sortIndex = sortInd;
			if(sortInd == SORT_BY_PNAME)
				sortProdNameFlag = !sortProdNameFlag;	*/		
			sortProdNameFlag = !sortProdNameFlag;
			//valArrayList = DataBasePersistent.readAllValuesOrderByFromDB(this, DBConstants.DEVICE_TYPE_WIDE_OV_TAG, DBConstants.CNTXT3_DETAIL_TBLVW_1LN_SCREEN_TAG, DBConstants.MAKTX_COLUMN_NAME, sortProdNameFlag);			
			
			dbObj =null;
			if(dbObj == null)
        		dbObj = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_ITEM_LIST);
    			tableExits = dbObj.checkTable();
    			dbitemObj.getColumns();	 
    			if(tableExits)
    				mattItemDetailArrList = dbObj.readListDataOrderByFromDB(this, metaName, sortProdNameFlag); 
        	
    		if(mattItemDetailArrList != null)
    			allitemDetailList = (ArrayList)mattItemDetailArrList.clone();
    		else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");
    		dbObj.closeDBHelper();
    		
	    	//setPageData();
	    	drawSubLayout();
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
    
    private void emptyvectors() {
		if(sovalArrayList!=null)
			sovalArrayList.clear();
		if(soLabelArrayList!=null)
			soLabelArrayList.clear();
	}//emptyvectors
   
    public void firstCallstack(int position2){
    	pos=position2;
    	//firstflag=1;
    	initheadLayout();
    	initItemLayout();    	 	
    }//firstCallstack   
    
    private void initItemLayout(){
		try {				
			flag =0;		
			getAllItemDetails(stocksItemArrList1,DBConstants.TABLE_SALESORDER_ITEM_LIST);   
			String 	itemlabelStr = "",itemvalueStr="";		
			DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			itemBlckLabelList = DataBasePersistent.readAllValuesOrderFromDB(this, DBConstants.ITEM_SUBVIEW_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("itemBlckLabelList size: "+itemBlckLabelList.size());
			itemBlckValList = DataBasePersistent.readAllLablesFromDB(this, DBConstants.ITEM_SUBVIEW_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("itemBlckValList size: "+itemBlckValList.size());
			
			itemcols = itemBlckLabelList.size();			
			salesOrdItemLblTV = new TextView[itemcols];
			salesOrdItemValET = new EditText[itemcols];
			
			try{
				TableLayout tl = (TableLayout)findViewById(R.id.salesorditemdettbllayout2);
				if(tl != null)
					tl.removeAllViews();
				TableRow tr1 = new TableRow(this);
				 //HashMap<String, String> itemMap = null; 	
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				int labelWidth = 70, editWidth = 180;				
				if(dispwidth > 0){
					labelWidth = dispwidth-editWidth;
					if(labelWidth < 100)
						labelWidth = 100;
					else if(labelWidth > 160)
						labelWidth = 160;
					
					editWidth = dispwidth-labelWidth;
					if(editWidth < 160)
						editWidth = 160;
					else if(editWidth > 250)
						editWidth = 250;
					
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
						//labelWidth = 175;
						labelWidth = 240;
						editWidth = 300;
					}
				}
				SalesOrderConstants.showLog("Edit and label Width : "+editWidth+" : "+labelWidth);
				if(mattItemDetailArrList.size()>0){
					displayUI(DBConstants.SO_ITEM_NAVIGATION_ACTION_TAG,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG,navigationrl);
					SalesOrderConstants.showLog("action Bars");		
				}
								
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 8; 
				linparams.bottomMargin = 5; 
				itemblockMap = (HashMap<String, String>) mattItemDetailArrList.get(pos); 
				for(int i=0; i<itemcols; i++){
					tr1 = new TableRow(this);												   
					ArrayList list = (ArrayList) itemBlckLabelList.get(i);
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
								String valStr = (String) itemblockMap.get(metaLabel);	    
			                 	//String valStr = (String) itemHeadMap.get(metaLabel);      
			         			boolean lab = false;  
			         			if(valStr != null && metaLabel.length() > 0){
			         				lab = itemBlckValList.containsKey(metaLabel);
			         			}													
						if(metaValue != null && metaValue.length() > 0){
	                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAP_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAP_TAG)){
	                 			LinearLayout llmclm = new LinearLayout(this);
	                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
	                        	String valTotStr = "";
	                 			if(lab){
	                 				String labStr = (String)itemBlckValList.get(metaLabel);
	                 				if(valStr != null && valStr.length() > 0){
	                 					salesOrdItemLblTV[i] = new TextView(this);
	                 					//salesOrdItemLblTV[i] =(TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
	                 					salesOrdItemLblTV[i].setPadding(5,5,5,5); 
	                 					salesOrdItemLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
	                 					salesOrdItemLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	                 					salesOrdItemLblTV[i].setMinWidth(100);
	                 					salesOrdItemLblTV[i].setWidth(labelWidth);                       					
	                 					salesOrdItemLblTV[i].setText(labStr);
	                 					salesOrdItemLblTV[i].setTextSize(18);
	                					llmclm.addView(salesOrdItemLblTV[i]); 
	                 				}/*else{
	                 					salesOrdItemLblTV[i].setVisibility(View.GONE);
	                 				}*/
	                 			}                   					
	        					
	        					if(valStr != null && valStr.length() > 0){	                 				                 	    		                 	    		
	                         		salesOrdItemValET[i] = new EditText(this);	
	                         		salesOrdItemValET[i].setPadding(5,5,5,5);
	                         		//salesOrdItemValET[i].setText(valueStr);
	        						//salesOrdValET[i].setWidth(160);
	                         		salesOrdItemValET[i].setWidth(editWidth);
	                         		salesOrdItemValET[i].setEnabled(false);	        												            					
	        						valTotStr += valStr;
	        						salesOrdItemValET[i].setText(valTotStr);    			
	                         		llmclm.addView(salesOrdItemValET[i]);
	                     		}    	
	        					//tl.setBackgroundDrawable(getResources().getDrawable(R.drawable.salesborder));
	        					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	         				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
	        					tl.addView(llmclm, layoutParams);   					
	                 				}
								}	
							}	
						}		
			    	}				    	
				}				
				SalesOrderConstants.showLog("Textview width : "+salesOrdLblTV[0].getWidth());
				SalesOrderConstants.showLog("EditText width : "+salesOrdValET[0].getWidth());			
				//if(mattItemDetailArrList.size()>1){
					/*try{
						displayUI(DBConstants.SO_ITEM_NAVIGATION_ACTION_TAG,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG,navigationrl);
						SalesOrderConstants.showLog("action Bars");		
					}catch (Exception ssdf2) {
						SalesOrderConstants.showErrorLog("Error in getting action bar : "+ssdf2.toString());
					}	*/	    		
		    	//}					
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesOrderConstants.showErrorLog("Error in initItemLayout : "+ssdf.toString());
		}
	}//fn initItemLayout
    
    private void showPagination(long index){
		try{
			showPrevBtn.setVisibility(View.VISIBLE);
			showNextBtn.setVisibility(View.VISIBLE);
			/*int totalsize = mattItemDetailArrList.size();
			//SalesOrderConstants.showLog("Total Size : "+totalsize);
			totalsize = totalsize-1;
			if(showPrevBtn != null){
				if((index > 0) && (index <= totalsize))
					showPrevBtn.setVisibility(View.VISIBLE);
				else
					showPrevBtn.setVisibility(View.GONE);
			}
			if(showNextBtn != null){
				if((index >= 0) && (index < totalsize))
					showNextBtn.setVisibility(View.VISIBLE);
				else
					showNextBtn.setVisibility(View.GONE);
			}*/
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in showPagination : "+sfg.toString());
		}
	}//fn showPagination
	
    /*private OnClickListener showPrevBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	//SalesOrderConstants.showLog("Show Prev btn clicked");
        	pos = pos-1;
        	if(pos < 0)
        		pos = 0;
        	//getSalesOrderItemDetails(pos);
        	initItemLayout();
        }
    };
    
    private OnClickListener showNextBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	//SalesOrderConstants.showLog("Show Next btn clicked");
        	pos = pos+1;
        	int totalsize = mattItemDetailArrList.size()-1;
        	if(pos > totalsize)
        		pos = totalsize;
        	initItemLayout();
        	//getSalesOrderItemDetails(pos);
        }
    };*/
	
    private void initheadLayout(){
		try {
			flag=1;
			getAllItemDetails(CustHeaderList,DBConstants.TABLE_SALESORDER_HEAD_LIST);   
			String labelStr = "",valueStr = "",descriStr = "";
			//SalesOrderConstants.showLog("1");
			/*showPrevBtn = (ImageButton) findViewById(R.id.showprevbtn);
			showPrevBtn.setOnClickListener(showPrevBtnListener); 
			showPrevBtn.setVisibility(View.GONE);
			
			showNextBtn = (ImageButton) findViewById(R.id.shownextbtn);
			showNextBtn.setOnClickListener(showNextBtnListener); 
			showNextBtn.setVisibility(View.GONE);*/
			
			if(itemHeadList!=null)
				itemHeadList.clear();
			if(itemHeadValuesList!=null)
				itemHeadValuesList.clear();
			DBConstants.DB_TABLE_NAME= DBConstants.TABLE_SALESORDER_CONTEXT_LIST;
			itemHeadList = DataBasePersistent.readAllValuesOrderFromDB(this, DBConstants.ITEM_HEADER_SUBVIEW_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);			
			itemHeadValuesList = DataBasePersistent.readAllLablesFromDB(this, DBConstants.ITEM_HEADER_SUBVIEW_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);			
				
			int cols = itemHeadList.size();
			salesOrdLblTV = new TextView[cols];
			salesOrdValET = new EditText[cols];
			//SalesOrderConstants.showLog("2");
			try{
				LinearLayout tl = (LinearLayout)findViewById(R.id.salesorditemdettbllayout1);
				if(tl != null)
					tl.removeAllViews();
				 HashMap<String, String> itemHeadMap = null; 		  
				TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				int labelWidth = 100, editWidth = 200;
				if(dispwidth > 0){
					labelWidth = dispwidth-editWidth;
					if(labelWidth < 100)
						labelWidth = 100;
					else if(labelWidth > 160)
						labelWidth = 160;
					
					editWidth = dispwidth-labelWidth;
					if(editWidth < 160)
						editWidth = 160;
					else if(editWidth > 250)
						editWidth = 250;
					
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
						//labelWidth = 175;
						labelWidth = 240;
						editWidth = 300;
					}
				}
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				//SalesOrderConstants.showLog("3");				
				 itemHeadMap = (HashMap<String, String>) headeritemdetailList.get(0); 
				for(int i=0; i<itemHeadList.size(); i++){
					tr1 = new TableRow(this);							
					    labelStr = itemHeadList.get(i).toString().trim();						   
            		    SapGenConstants.showLog("labelStr: "+labelStr);	
					    valueStr = (String) itemHeadMap.get(labelStr);	
					ArrayList list = (ArrayList) itemHeadList.get(i);
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
			                 	String valStr = (String) itemHeadMap.get(metaLabel);      
			         			boolean lab = false;  
			         			if(valStr != null && metaLabel.length() > 0){
			         				lab = itemHeadValuesList.containsKey(metaLabel);
			         			}													
						if(metaValue != null && metaValue.length() > 0){
	                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
	                 			LinearLayout llmclm = new LinearLayout(this);
	                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
	                        	String valTotStr = "";
	                 			if(lab){
	                 				String labStr = (String)itemHeadValuesList.get(metaLabel);
	                 				if(valStr != null && valStr.length() > 0){
	                 					salesOrdLblTV[i] = new TextView(this);
	                 					//salesOrdLblTV[i] =(TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
	                 					salesOrdLblTV[i].setPadding(5,5,5,5); 
	            						salesOrdLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
	            						salesOrdLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	            						salesOrdLblTV[i].setMinWidth(100);
	            						salesOrdLblTV[i].setWidth(labelWidth);  
	            						salesOrdLblTV[i].setTextSize(18);
	            						salesOrdLblTV[i].setText(labStr);
	                					llmclm.addView(salesOrdLblTV[i]); 
	                 				}/*else{
	                 					salesOrdLblTV[i].setVisibility(View.GONE);
	                 				}*/
	                 			}                    					
	        					
	        					if(valStr != null && valStr.length() > 0){
	        						salesOrdValET[i] = new EditText(this);	
	        						salesOrdValET[i].setPadding(5,5,5,5);
	        						salesOrdValET[i].setText(valueStr);
	        						//salesOrdValET[i].setWidth(160);
	        						salesOrdValET[i].setWidth(editWidth);
	        						salesOrdValET[i].setEnabled(false);	        												            					
	        						valTotStr += valStr;
	        						salesOrdValET[i].setText(valTotStr);    			
	                         		llmclm.addView(salesOrdValET[i]);
	                     		}    

	        				/*	String trgStr = (String) itemheaderMap.get(metaTrgActStr.toString().trim());
	         					SapGenConstants.showLog("metaTrgActStr : "+metaTrgActStr); 
	         					SapGenConstants.showLog("trgStr : "+trgStr); 
	             				if(trgStr != null && trgStr.length() > 0){
	                 				if(trgStr != null && trgStr.length() > 0){
	                 					salesOrdLblTV[i] = new TextView(this);
	                 					salesOrdLblTV[i].setPadding(5,5,5,5); 
	            						salesOrdLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
	            						salesOrdLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	            						salesOrdLblTV[i].setMinWidth(100);
	            						salesOrdLblTV[i].setWidth(labelWidth);
	                					salesOrdLblTV[i].setLayoutParams(new
	                							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	                					if(metaTrg.indexOf("(") >= 0){
	                						trgStr = "("+trgStr+")";
	                					}
	                					salesOrdLblTV[i].setText(trgStr);
	                					llmclm.addView(salesOrdLblTV[i]); 
	                 				}
	             				} */                   					
	        					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	         				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
	        					tl.addView(llmclm, layoutParams);   					
	                 		}
			    	}	
			    	}	
			    	}		
			    	}
				    	/*salesOrdLblTV[i] = new TextView(this);
						salesOrdValET[i] = new EditText(this);		
						
						salesOrdLblTV[i].setPadding(5,5,5,5); 
						salesOrdLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
						salesOrdLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
						salesOrdLblTV[i].setMinWidth(100);
						salesOrdLblTV[i].setWidth(labelWidth);
						salesOrdLblTV[i].setText(itemHeadValuesList.get(i).toString());
						if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
							salesOrdLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
																						
						salesOrdValET[i].setPadding(5,5,5,5);
						salesOrdValET[i].setText(valueStr);
						//salesOrdValET[i].setWidth(160);
						salesOrdValET[i].setWidth(editWidth);
						salesOrdValET[i].setEnabled(false);
												
						tr1.addView(salesOrdLblTV[i]);
						tr1.addView(salesOrdValET[i]);
						tr1.setLayoutParams(linparams);	
																		
					//SalesOrderConstants.showLog("4");					
					tl.setBackgroundDrawable(getResources().getDrawable(R.drawable.salesborder));
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));*/
				}				
				SalesOrderConstants.showLog("Textview width : "+salesOrdLblTV[0].getWidth());
				SalesOrderConstants.showLog("EditText width : "+salesOrdValET[0].getWidth());										
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesOrderConstants.showErrorLog("Error in initheadLayout : "+ssdf.toString());
		}
	}//fn initLayout
          
    public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		  SalesOrderConstants.showLog("Text : "+s.toString());
		  filter(s.toString());
	} 
	
	/*private void searchItemsAction(String match){  
        try{
            searchflag = true;           
            searchStr = match;
            SalesOrdProItemOpConstraints stkObj = null;
            String matDescStr = "", mattStr = "";
            SalesOrderConstants.showLog("stocksItemCopyArrList1 Size :"+stocksItemCopyArrList1.size()+" : "+match.length());
            if((stocksItemCopyArrList1 != null) && (stocksItemCopyArrList1.size() > 0)){
            	if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){ 
            		if(stocksItemArrList1.size() > 0)
            			stocksItemArrList1.clear();
            		
                    SalesOrderConstants.showLog("stocksItemCopyArrList1 Size1 : "+stocksItemCopyArrList1.size()+" : "+SalesOrderConstants.stocksItemMainArrList.size()+" : "+stocksItemArrList1.size());
                    
                    for(int i = 0; i < stocksItemCopyArrList1.size(); i++){  
                    	SalesOrderConstants.showLog("Inside If ");
                        stkObj = null;
                        matDescStr = "";
                        mattStr = "";
                        SalesOrderConstants.showLog("Match : "+match+" ival : "+i);
                        stkObj = (SalesOrdProItemOpConstraints)stocksItemCopyArrList1.get(i);
                        SalesOrderConstants.showLog("Match : "+match+" ival : "+i+" : "+stkObj);  
                        if(stkObj != null){
                        	matDescStr = stkObj.getItemDescription().trim().toLowerCase();
                        	mattStr = stkObj.getMaterialNo().trim().toLowerCase();
                            match = match.toLowerCase();
                            SalesOrderConstants.showLog("match :"+match+" : "+matDescStr+" : "+mattStr);
                            if((matDescStr.indexOf(match) >= 0) || (mattStr.indexOf(match) >= 0)){
                            	stocksItemArrList1.add(stkObj);
                            }
                        }
                    }//for 
                    //initLayout();
                    drawSubLayout();
        			//searchET.setText(searchStr);
                }
                else{
                    System.out.println("Match is empty");
                    stocksItemArrList1.clear();
                    for(int i = 0; i < stocksItemCopyArrList1.size(); i++){  
                        stkObj = (SalesOrdProItemOpConstraints)stocksItemCopyArrList1.get(i);
                        if(stkObj != null){
                        	stocksItemArrList1.add(stkObj);
                        }
                    }
                    drawSubLayout();
                    //initLayout();
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
	public void filter(String charText) {
        try {       	        	
        	charText = charText.toLowerCase();			
        	mattItemDetailArrList.clear();
			if (charText.length() == 0) {				
					if(stocksItemCopyArrList1 != null)
						mattItemDetailArrList = (ArrayList)stocksItemCopyArrList1.clone();											
				}else{
					for (Object obj : stocksItemCopyArrList1) {
				    	String strValue = (String)((HashMap<String, String>)obj).get(DBConstants.SO_MAIN_COL_MATNR);					    		
				    	String soStr = (String)((HashMap<String, String>)obj).get(DBConstants.SO_HEAD_COL_VBELN);
				    	String descStr = (String)((HashMap<String, String>)obj).get(DBConstants.SO_MAIN_COL_ARKTX);
				        if (strValue.toLowerCase().contains(charText)){				        
				        	mattItemDetailArrList.add(obj);	
				        }else if(descStr.toLowerCase().contains(charText)){
				        	mattItemDetailArrList.add(obj);	
				        }
				        else{
				        	if(soStr.toLowerCase().contains(charText)){
				        		mattItemDetailArrList.add(obj);	
				        	}
			        	}					        
					}
				}						
			drawSubLayout();
		} catch (Exception sgghrr) {
			SapGenConstants.showErrorLog("On filter : "+sgghrr.toString());
		}
    }
	
	private void sortItemsAction(int sortInd){
		try{
			 sortFlag = true;
			 sortIndex = sortInd;
			 
			 if(sortInd == sortHeader1)
				 sortItemFlag = !sortItemFlag;
			 else if(sortInd == sortHeader2)
				 sortMattFlag = !sortMattFlag;
			 else if(sortInd == sortHeader3)
				 sortQtyFlag = !sortQtyFlag;
			 
			 SalesOrderConstants.showLog("Selected Sort Index : "+sortInd);
			 Collections.sort(stocksItemArrList1, soItemSortComparator); 
				
             initLayout();
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
	
	private final Comparator soItemSortComparator =  new Comparator() {

        public int compare(Object o1, Object o2){ 
            int comp = 0;
            double rateAmt1=0, rateAmt2=0;
            String strObj1 = "0", strObj2="0";
            SalesOrdProItemOpConstraints repOPObj1, repOPObj2;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                	repOPObj1 = (SalesOrdProItemOpConstraints)o1;
                    repOPObj2 = (SalesOrdProItemOpConstraints)o2;
                    
                    if(sortIndex == sortHeader1){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getSoldToParty().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getSoldToParty().trim();
                        
                        if(sortItemFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else if(sortIndex == sortHeader2){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getMaterialNo().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getMaterialNo().trim();
                        
                        if(sortMattFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                    else if(sortIndex == sortHeader3){
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getCummOrdQtySales().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getCummOrdQtySales().trim();
                       
                        if(!strObj1.equalsIgnoreCase(""))
                        	rateAmt1 = Double.parseDouble(strObj1);
                        	
                    	if(!strObj2.equalsIgnoreCase(""))
                    		rateAmt2 = Double.parseDouble(strObj2);
                    		
                        if(sortQtyFlag == true)
                            comp = (int) (rateAmt1-rateAmt2);
                        else
                            comp = (int) (rateAmt2-rateAmt1);
                    }
                    else{
                        // Code to sort by Material Desc (default)
                        if(repOPObj1 != null)
                            strObj1 = repOPObj1.getNetValDocCurr1().trim();
                        
                        if(repOPObj2 != null)
                            strObj2 = repOPObj2.getNetValDocCurr1().trim();
                        
                        if(sortMattFlag == true)
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
