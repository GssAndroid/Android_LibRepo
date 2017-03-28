package com.globalsoft.SalesOrderLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.SalesOrderListActivity.SalesOrderAdapter.ViewHolder;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesOrderItemScreen extends Activity implements TextWatcher  {
	
	private TextView[] mattTxtView, materialTxtView, valTV;
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3,valtv;
	private TextView tableHeaderTV4, tableHeaderTV5, tableHeaderTV6;
	private TextView custNameTV, soNoTV, sovalueTV, reqDatTV,customerTV;
	private EditText searchET;
	int offset = 0;
	int data;
	private TableRow.LayoutParams linparams11 = null;
	
	private String searchStr = "", selectedSoNumb ="", selectdLabel= "", curLabel;
	private boolean sortFlag = false, sortItemFlag = false, sortMattFlag = false, searchflag = true, sortQtyFlag = false;
	
    private int sortIndex = -1,  flag=-1;
	
	private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6;	
	private final int sortHeader1=1, sortHeader2=2, sortHeader3=3;
	private int dispwidth = 300, offlinemode;
	public TextView pageTitle, firstPgTV, lastPgTV, nextPgTV, prevPgTV;
	
	private ListView listView;
	private SalesOrderItemAdapter salesOrderItemlistAdapter;
	private LinearLayout sosummaryll, sosearchlinear;
	private DataBasePersistent dbitemObj = null;
	private DataBasePersistent dbObj = null;
	private ArrayList valArrayList = new ArrayList();
	private ArrayList SOItemList = new ArrayList();
	private ArrayList sovalArrayList = new ArrayList();
	private ArrayList productListViewArray = new ArrayList();
	private ArrayList soLabelArrayList = new ArrayList();
	HashMap<String, String> labelMap = null; 
	private ArrayList headeritemdetailList = new ArrayList();
	private ArrayList navigationList = new ArrayList();
	
	HashMap<String, String> itemblockMap = null; 	
	HashMap<String, String> itemheaderMap = null; 
	private ArrayList mattItemDetailArrList = new ArrayList();
	
	private SalesOrdProHeadOpConstraints stkCategory = null;
	 private boolean tableExits = false;
	private ArrayList stocksItemArrList1 = new ArrayList();
	private ArrayList<String> CustHeaderList = new ArrayList<String>();
	private ArrayList stocksItemCopyArrList1 = new ArrayList();
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            /*requestWindowFeature(Window.FEATURE_LEFT_ICON);
            SapGenConstants.setWindowTitleTheme(this);
            setContentView(R.layout.salesorderitem);*/
            
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.salesorderitemnew); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText("Sales Order Creation");

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
            
			selectedSoNumb = this.getIntent().getStringExtra("selectdSoNumb");
			selectdLabel = this.getIntent().getStringExtra("selectdLabelStr");
			SapGenConstants.showLog("selectdLabel: "+selectdLabel);
			SapGenConstants.showLog("selectedSoNumb: "+selectedSoNumb);
			offlinemode =  this.getIntent().getIntExtra("offlinestr", 0);
	    	if(CustHeaderList!=null)
				CustHeaderList.clear();
			CustHeaderList =  (ArrayList) SalesOrderConstants.metaheadlistArray.clone();			
	        stocksItemArrList1 = (ArrayList) SalesOrderConstants.stocksItemMainArrList.clone();	
	       /* stocksItemArrList1 = (ArrayList) SalesOrderConstants.stocksItemMainArrList.clone();
	        stocksItemCopyArrList1 = (ArrayList) SalesOrderConstants.stocksItemMainArrList.clone();*/
	        
        	initLayout();
	        /*if(stocksItemArrList1.size()>5)
	        	initLayout();
	        else
	        	initLayout2();*/
        } catch (Exception de) {
        	SalesOrderConstants.showErrorLog("Error in onCreate:"+ de.toString());
        }
    }
    
    private void initHeaderLayout(){
    	try{
    		flag=1;    		
    		/*itemheadLabelList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.SO_SUMMARY_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("itemheadLabelList size: "+itemheadLabelList.size());
    		soItemLabel = DataBasePersistent.readAllTabletLablesFromDB(this,DBConstants.SO_SUMMARY_BLOCK,DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);    		
 	        SalesOrderConstants.showLog("soItemLabel: "+soItemLabel.size());*/
 	       
    		getAllItemDetails(CustHeaderList,DBConstants.TABLE_SALESORDER_HEAD_LIST);   
    		//itemheaderMap = (HashMap<String, String>) headeritemdetailList.get(0); 
    		
    		sosummaryll = (LinearLayout) findViewById(R.id.sosummaryLL2);
    		sosummaryll.removeAllViews();
    		sosummaryll.setOrientation(LinearLayout.VERTICAL);
    		sosummaryll.removeAllViews();
    		displayUI(DBConstants.SO_SUMMARY_BLOCK, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, sosummaryll);
    		sosearchlinear = (LinearLayout) findViewById(R.id.vanstocklinear);
    		sosearchlinear.removeAllViews();
    		sosearchlinear.setOrientation(LinearLayout.VERTICAL);
    		sosearchlinear.removeAllViews();
    		displayUI(DBConstants.DEVICE_OVERVIEW_SEARCH, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, sosearchlinear);
			
			//updateUIElements();
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initHeaderLayout : "+sfg.toString());
    	}
    }//fn initHeaderLayout
    
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
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
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
			                 			et.setLayoutParams(new LayoutParams(300,40));
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
			                    	        	if(!s.equals(null)){
			                 						searchStr = s.toString().trim();
			                						filter(searchStr);
			                 					}else{
			                 						mattItemDetailArrList = (ArrayList)stocksItemCopyArrList1.clone();	
			                 						setPageData();
			                 						drawSubLayout();
			                 					}			
			                    	        }
			                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
			                    	    }); 		    			                 			
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
				                 			et.setLayoutParams(new LayoutParams(300,40));
				                 			et.setTextColor(getResources().getColor(R.color.black));
				                 			et.setTextSize(11);
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
				                 			LinearLayout ll = new LinearLayout(this);
				        				    ll.setOrientation(LinearLayout.VERTICAL);
				        				    ll.addView(et);
				        				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				       				    	     LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
				        				    dynll.addView(ll, layoutParams);
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
    
    private void getAllItemDetails(ArrayList stockitemlist, String tablename) {					
		try{	
			
			offset = 0;
			data = offset + SalesOrderConstants.so_item_page_size;
            SapGenConstants.showLog("offset:"+offset);
            SapGenConstants.showLog("data:"+data);
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
				SapGenConstants.showLog("mattItemDetailArrList size :"+mattItemDetailArrList.size());
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
			
			/*if(mattItemDetailArrList != null)
				mattItemDetailArrList = (ArrayList)mattItemDetailArrList.clone();*/
    		/*else
    			SapGenConstants.showErrorDialog(this, "Data Not available in Offline mode");*/
			dbitemObj.closeDBHelper();  
			
			//SapGenConstants.showLog("mattItemDetailArrList size:"+ mattItemDetailArrList.size());
			HashMap<String, String> stockMap = null;
            String matIdStrVal = "", id = "", desc = "";
        	if(mattItemDetailArrList != null && mattItemDetailArrList.size() > 0 ){
        		//_options =  new CharSequence[mattItemDetailArrList.size()];
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
					//_options[i]= matIdStrVal;
				}
        	}     
        	dbitemObj.closeDBHelper();	
        	setPageData();
		}catch(Exception sggh){				
			dbitemObj.closeDBHelper();				
			SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
		}			
}//         
    
    public void setPageData(){
		try {
			SalesOrderConstants.SL_previous_page = 0;
			SalesOrderConstants.SL_current_page = 1;
			if(mattItemDetailArrList != null && mattItemDetailArrList.size() > 0){
				SalesOrderConstants.SL_total_record = mattItemDetailArrList.size();	
				if(SalesOrderConstants.SL_total_record > SalesOrderConstants.so_item_page_size ){
					SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_record/SalesOrderConstants.so_item_page_size;	
					int remain_page = SalesOrderConstants.SL_total_record%SalesOrderConstants.so_item_page_size;
					if(remain_page > 0){
						SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_page + 1;
					}			
				}
				else{
					SalesOrderConstants.SL_total_page = 1;
				}				
			}			
			System.out.println("PL_total_page->"+SalesOrderConstants.SL_total_page);
		} catch (Exception sgghr) {
			SapGenConstants.showErrorLog("On setPageData : "+sgghr.toString());
		}
    }//fn setPageData
    
    private void updateUIElements(){
        try{
            /*if(stkCategory != null){
            	String customer = stkCategory.getSPName().trim()+" ("+stkCategory.getSoldToParty().trim()+")";
            	String contact = stkCategory.getCPName().trim()+" ("+stkCategory.getCPNo().trim()+")";
            	
            	SalesOrderConstants.showErrorLog("customer : "+customer);
            	SalesOrderConstants.showErrorLog("contact : "+contact);
            	
            	custNameTV.setText(" :   "+customer);
            	soNoTV.setText(" :   "+stkCategory.getDocumentNo().trim());
            	sovalueTV.setText(" :   "+stkCategory.getNetValDocCurr().trim()+" "+stkCategory.getSDDocCurrency().trim());
            	String reqDatStr = stkCategory.getRequiredDate().trim();
            	if(!reqDatStr.equalsIgnoreCase("")){
            		reqDatStr = SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", reqDatStr);
            	}
            	reqDatTV.setText(" :   "+reqDatStr);
            	customerTV.setText(" :   "+contact);                            
            }
            else
            	SalesOrderConstants.showLog("stkCategory is null");*/
        }
        catch(Exception asf){
        	SalesOrderConstants.showErrorLog("Error in updateUIElements : "+asf.toString());
        }
    }//fn updateUIElements
    
    private void initLayout(){
    	try{
    		//setContentView(R.layout.salesorderitemlist);
	        initHeaderLayout();
	       
	        flag=0;
	        getAllItemDetails(stocksItemArrList1,DBConstants.TABLE_SALESORDER_ITEM_LIST);  
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
	        
			listView = (ListView)findViewById(R.id.list2);
			listView.setTextFilterEnabled(true);
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			listView.setOnItemClickListener(listItemClickListener);
			
			sovalArrayList = DataBasePersistent.readAllValuesFromDB(this, DBConstants.SO_DEVICE_TYPE_ITEM_SMALL_OV_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG);
			SalesOrderConstants.showLog("sovalArrayList size: "+sovalArrayList.size());
				
			soLabelArrayList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.SO_DEVICE_TYPE_ITEM_SMALL_OV_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG);
			SalesOrderConstants.showLog("soLabelArrayList size: "+soLabelArrayList.size());
			
			navigationList = DataBasePersistent.readAllTabletLablesFromDB(this, DBConstants.SO_DEVICE_TYPE_ITEM_SMALL_OV_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
			SalesOrderConstants.showLog("soLabelArrayList size: "+navigationList.size());
		        //dispwidth = SapGenConstants.getDisplayWidth(this);
    		/*searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			searchET.setFocusable(true);
			searchET.setFocusableInTouchMode(true);
			searchET.requestFocus();*/
			drawSubLayout();		
			//ListviewAdapter();
    	}
    	catch(Exception sfg){
    		SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
    
    private OnClickListener firstPgTVListener = new OnClickListener(){
		public void onClick(View v){
			if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1){
				changeTotalPage();
				SalesOrderConstants.SL_previous_page = 0;
	    		SalesOrderConstants.SL_current_page = 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
	    		offset = 0;
                data = offset + SalesOrderConstants.so_item_page_size;
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
			if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page){
				changeTotalPage();
				SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_total_page - 1;
	    		SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_total_page;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
                
                int remains = SalesOrderConstants.SL_total_record%SalesOrderConstants.so_item_page_size;
                if(remains == 0){
                	offset = SalesOrderConstants.SL_total_record - SalesOrderConstants.so_item_page_size;
                	data = SalesOrderConstants.SL_total_record;
                }else{
                	offset = (SalesOrderConstants.SL_total_record/SalesOrderConstants.so_item_page_size)*SalesOrderConstants.so_item_page_size;
                    data = SalesOrderConstants.SL_total_record;
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
			if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page){
				changeTotalPage();
	    		SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_current_page;
	    		SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_current_page + 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
	    		offset = offset + SalesOrderConstants.so_item_page_size;
                data = offset + SalesOrderConstants.so_item_page_size;
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
			if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1){
				changeTotalPage();
	    		SalesOrderConstants.SL_current_page = SalesOrderConstants.SL_current_page - 1;
	    		SalesOrderConstants.SL_previous_page = SalesOrderConstants.SL_previous_page - 1;
	    		lastPgTV.setText(""+SalesOrderConstants.SL_total_page);	  
	    		if(data != SalesOrderConstants.SL_total_record){
		    		offset = offset - SalesOrderConstants.so_item_page_size;
	                data = data - SalesOrderConstants.so_item_page_size;
	    		}else{
		    		offset = offset - SalesOrderConstants.so_item_page_size;
		    		int remain = data%SalesOrderConstants.so_item_page_size;
		    		if(remain > 0){
		    			data = data - remain;
		    		}else{
		                data = data - SalesOrderConstants.so_item_page_size;
		    		}
	    		}
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
	    		drawSubLayout();
			}
		}
	};
	
	public void changeTotalPage(){
		if(mattItemDetailArrList.size() > 0){
			SalesOrderConstants.SL_total_record = mattItemDetailArrList.size();	
			if(SalesOrderConstants.SL_total_record > SalesOrderConstants.so_item_page_size ){
				SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_record/SalesOrderConstants.so_item_page_size;	
				int remain_page = SalesOrderConstants.SL_total_record%SalesOrderConstants.so_item_page_size;
				if(remain_page > 0){
					SalesOrderConstants.SL_total_page = SalesOrderConstants.SL_total_page + 1;
				}					
			}		
		}			
		SapGenConstants.showLog("SL_total_page->"+SalesOrderConstants.SL_total_page);	
	}
        
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
    		SapGenConstants.FIRST_LANUCH_SCR_FOR_PROD_CAT = 0;
    		SalesOrderConstants.SL_current_page = 1;
			SalesOrderConstants.SL_previous_page = 0;
			SalesOrderConstants.SL_total_page = 0;
			SalesOrderConstants.SL_total_record = 0;
    		finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void drawSubLayout(){
		try {							
			String pageTitleValue = ""+SalesOrderConstants.SL_current_page;
	        pageTitle.setText(pageTitleValue);

	        prevPgTV.setText("<<");
	        nextPgTV.setText(">>");
	        
	        firstPgTV.setText("1");
	        lastPgTV.setText(""+SalesOrderConstants.SL_total_page);
	        
	        if(SalesOrderConstants.SL_previous_page > 0 && SalesOrderConstants.SL_current_page > 1 ){
	        	prevPgTV.setTextColor(Color.BLUE);
	        	firstPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	prevPgTV.setTextColor(Color.BLACK);
	        	firstPgTV.setTextColor(Color.BLACK);
	        }
	        
	        if(SalesOrderConstants.SL_total_page > 1 && SalesOrderConstants.SL_current_page < SalesOrderConstants.SL_total_page ){
	        	nextPgTV.setTextColor(Color.BLUE);
	        	lastPgTV.setTextColor(Color.BLUE);
	        }
	        else{
	        	nextPgTV.setTextColor(Color.BLACK);
	        	lastPgTV.setTextColor(Color.BLACK);
	        }
    		//setPageData();
			if(mattItemDetailArrList != null){
				productListViewArray.clear();
				HashMap<String, String> stockMap = null;
                SapGenConstants.showLog("Product List Size  : "+mattItemDetailArrList.size());  
                SapGenConstants.showLog("offset:"+offset);
                SapGenConstants.showLog("data:"+data);
                if(offset <= mattItemDetailArrList.size() && data <= mattItemDetailArrList.size() ){
					for (int i = offset; i < data; i++) {
						stockMap = (HashMap<String, String>) mattItemDetailArrList.get(i); 
	                    if(stockMap != null){
	                    	productListViewArray.add(stockMap);
	                    }					
					}
                }else{
					for (int i = offset; i < mattItemDetailArrList.size(); i++) {
						stockMap = (HashMap<String, String>) mattItemDetailArrList.get(i);  
	                    if(stockMap != null){
	                    	productListViewArray.add(stockMap);
	                    }					
					}
                }
			}
            SapGenConstants.showLog("productListViewArray List Size  : "+productListViewArray.size());  
            ListviewAdapter();
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in drawSubLayout:"+ce.toString());
		}
	}//fn drawSubLayout
    
    public void ListviewAdapter(){    	   	
		salesOrderItemlistAdapter = new SalesOrderItemAdapter(this);
		listView.setAdapter(salesOrderItemlistAdapter);
    		
	}//ListviewAdapter
    
    public class SalesOrderItemAdapter extends BaseAdapter {                  	    	
		HashMap<String, String> stockMap = null;
    	int count=0;
    	String valStr="", matIdStr = "", matId= "";
    	LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	       
        public SalesOrderItemAdapter(Context context){
            mInflater = LayoutInflater.from(context);
        }
        
        public int getCount() {  
        	try {
				if(productListViewArray != null)
					return productListViewArray.size();
			}
        	catch (Exception e) {
        		SalesOrderConstants.showErrorLog(e.getMessage());
			}
        	return 0;
        	/*int size = stocksSelectdItemArrList1.size();
        	return size;*/						        	
        }
        
        public Object getItem(int position) {       	
            return position;
        }
              
        public long getItemId(int position) {
        	
            return position;
        }

        
        public View getView(final int position, View convertView, ViewGroup parent) {
        	ViewHolder holder;               
            try{           	
	                    convertView = mInflater.inflate(R.layout.salesorder_list,parent,false);                   
	                    holder = new ViewHolder();                    	                                 
                        holder.llitembg = (LinearLayout)convertView.findViewById(R.id.litembg);
                        holder.llitembg2 = (LinearLayout)convertView.findViewById(R.id.textll);
                        holder.llitembg3 = (LinearLayout)convertView.findViewById(R.id.pricell);
                        convertView.setTag(holder);
                        
                        //if(position%2 == 0)
        					holder.llitembg.setBackgroundResource(R.color.item_even_color);
        				//else
        					//holder.llitembg.setBackgroundResource(R.color.item_odd_color);
                        
                     
                        if(productListViewArray != null){
    	            		int size = productListViewArray.size();	            		               
    		                valTV = new TextView[size];    		               
    		                //deleteBMF = new ImageButton[size];		 
    		              	  		                
    		                SalesOrderConstants.showLog("listview ");    	            		
    	            		stockMap = (HashMap<String, String>) productListViewArray.get(position); 
    	            		    	            		    	            		
    	            		 holder.llitembg.removeAllViews();	  
    	            		 holder.llitembg2.removeAllViews();	  
    	            		// holder.llitembg2.setBackgroundResource(R.drawable.listview_border);
    	            			LinearLayout llmclm = new LinearLayout(SalesOrderItemScreen.this);
    	            			llmclm.setLayoutParams(new LinearLayout.LayoutParams(
    	            					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		            		 	llmclm.removeAllViews();	
	              				llmclm.setOrientation(LinearLayout.HORIZONTAL); 	              				
	              				//llmclm.setBackgroundResource(R.drawable.listview_border);	              				
	              				
	              				LinearLayout llmclm4 = new LinearLayout(SalesOrderItemScreen.this);  
	              				llmclm4.removeAllViews();	
	              				llmclm4.setOrientation(LinearLayout.VERTICAL); 
	              					              				                    			
    	            		 matIdStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN); 
    	            		 matId = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);
    	            		 curLabel = DBConstants.SO_HEAD_COL_VBELN;	            		
    	            		 matIdStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN);  
    	            		 final String pId = matIdStr;	  
    	            		 //MatNoStringList.add(matId);    	            		          		
    	            			 
    	            		//if(valArrayList2 != null && valArrayList2.size() > 0){            			            			          			            			
    	                        for(int i=0; i<soLabelArrayList.size(); i++){	   
    	                        	SapGenConstants.showLog("i value:  "+i);
    	                        	try{
    	                        	
        		            		 	LinearLayout llmclm2 = new LinearLayout(SalesOrderItemScreen.this);  
        	              				llmclm2.removeAllViews();	
    	                    			llmclm2.setOrientation(LinearLayout.VERTICAL);     
    	                    			
        	              				
        	                        	String nameVal = soLabelArrayList.get(i).toString().trim();		                    					                    		
        	                    		String labValStr = "", valueValStr = "";
        	        					String metaValStr = "";
        	        					String typeValStr = "";
        	        					if(nameVal.indexOf("::") >= 0){
        	        						String[] separated = nameVal.split("::");
        	        						if(!separated.equals(null) && separated.length > 0){
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
        	        						        							        						        	        					    		
        	                    		if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAPPABLE_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG)){  
        	                    			/*RelativeLayout llmclm3 = new RelativeLayout(SalesOrderCreation.this);  
            	              				llmclm3.removeAllViews();	*/
        	                    			//llmclm2.setOrientation(LinearLayout.VERTICAL);     
        	                    			valStr = (String) stockMap.get(metaValStr);	         
        		                    		SapGenConstants.showLog("valStr:  "+valStr);        	                			                   							                                           				
        	                    				 if(valStr != null && valStr.length() > 0){  
        	                    						 valtv = new TextView(SalesOrderItemScreen.this);     			                    			
        	        	                				//valtv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	 
        	                    					 /*if(totalPriceStr!=null && metaValStr.equalsIgnoreCase(DBConstants.NETWR_COLUMN_NAME)){
        	                    						 valStr =" ";
        	                    						 if(proPriceMap != null && proPriceMap.size() > 0){
        														for (int m = 0; m < proPriceMap.size(); m++) {
        															//proTotPriceMap =  proPriceMap.get(m);   
        															String tstr = (String) proPriceMap.get(matId);
        															SapGenConstants.showLog("tstr:  "+tstr);
        															if(tstr != null && tstr.length() > 0){  
        																valStr = tstr;
        															}
        											    		}									    			
        													}	 
        	                    					 }*/
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
        	                    				 //valtv .setMinWidth(10);
        	                    				 valtv.setTextSize(16);
        	                    				 valtv.setTextColor(Color.DKGRAY);
        	                    				 valtv .setId(i);	       	                    				 
        	                    				 valtv .setGravity(Gravity.LEFT);   
        	                    				 if(!metaValStr.equals(DBConstants.SO_MAIN_COL_POSNR)){
        	                    					 //llmclm2.addView(valtv);
        	                    					 llmclm4.addView(valtv);
        	                    					 llmclm.addView(llmclm4);
        	                    					 //holder.llitembg2.setOrientation(LinearLayout.VERTICAL); 
                             					 	holder.llitembg.addView(llmclm);                                					 	
                             					 	/* holder.llitembg3.addView(holder.llitembg2);
                             					 	holder.llitembg.addView(holder.llitembg3);*/
        	                    				 }else{
        	                    					 llmclm2.addView(valtv);
        	                    					 //holder.llitembg.setOrientation(LinearLayout.HORIZONTAL);
                             					 	holder.llitembg.addView(llmclm2);  
        	                    				 }                       					 	  	                    				    	                    			
        	                    		}	                    			 		                  		        	                        
    	                        	}catch(Exception sfg2){
    	                    			SalesOrderConstants.showErrorLog("Error in for loop : "+sfg2.toString());
    	                    			sfg2.printStackTrace();
    	                    		}
    	            		
    	                        }
    	                        
    	                        for(int i=0; i<navigationList.size(); i++){	   
    	                        	SapGenConstants.showLog("i value:  "+i);
    	                        	try{
    	                        	
        		            		 	LinearLayout llmclm2 = new LinearLayout(SalesOrderItemScreen.this);  
        	              				llmclm2.removeAllViews();	
    	                    			llmclm2.setOrientation(LinearLayout.VERTICAL);     
    	                    			
        	              				
        	                        	String nameVal = navigationList.get(i).toString().trim();		                    					                    		
        	                    		String labValStr = "", valueValStr = "";
        	        					String metaValStr = "";
        	        					String typeValStr = "";
        	        					if(nameVal.indexOf("::") >= 0){
        	        						String[] separated = nameVal.split("::");
        	        						if(!separated.equals(null) && separated.length > 0){
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
        	        					
        	        					 if(valueValStr.equalsIgnoreCase(DBConstants.ACTION_DETAIL_TAG)){
        	                    			SapGenConstants.showLog("3"); 	        	                    		  
        	                    			ImageView syncBMF = new ImageView(SalesOrderItemScreen.this); 
        			                         syncBMF.setId(i);				                               			                       
        			                         syncBMF.setBackgroundResource(R.drawable.stock_right); 	
        			                         syncBMF.setOnClickListener(new View.OnClickListener() {
     	  										public void onClick(View view) {
     	  											int id = view.getId();	 											
     	  											showStockListDetailScreen(pId, curLabel);
     	  										}	
     	  			                        });
        			                         LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                                     LinearLayout.LayoutParams.WRAP_CONTENT);
        			                         LinearLayout ll = new LinearLayout(SalesOrderItemScreen.this);
        			                         ll.setId(2131230808);
        			                         ll.setGravity(Gravity.RIGHT);
        			                         ll_params.topMargin= 20;
        			                         ll.addView(syncBMF);
        			                         llmclm.addView(ll,ll_params);        				                      
        				                     SapGenConstants.showLog("5"); 	        				                      
        				                     holder.llitembg.addView(llmclm);
        	                    		}    
    	                        	}catch(Exception e){
        	                        	SalesOrderConstants.showLog("Error in Adapter:"+ e.toString());
        	                        }
    	                        }
    	                        
    	            		//}
    	            	}  
              
            }catch(Exception e){
            	SalesOrderConstants.showLog("Error in Adapter:"+ e.toString());
            }
           
            return convertView;	
            
          }
        		
		 class ViewHolder {
	            TextView text;
	            TextView taskid;
	            TextView date;
	            ImageButton delete;
	            //ImageButton[] delete = new ImageButton[mattsize];
	            TextView date1;
	            EditText value;
	            LinearLayout llitembg;
	            LinearLayout llitembg2;
	            LinearLayout llitembg3;	            
	            int ref;
	        }
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }    
       
    }//End of PriceListAdapter
    
    
  /*  public class SalesOrderItemAdapter extends BaseAdapter {      
        
    	private LayoutInflater mInflater=null;    
    	 HashMap<String, String> stockMap = null;      
    	String matterialStr = "", delstatus = "",street="";
        String docNoStr = "", spname = "", spcity = "",docDate="",city="",country="";
    	
        public SalesOrderItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {        
        	try {
				if(productListViewArray != null)
					return productListViewArray.size();
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

        public View getView(int position, View convertView, ViewGroup parent) {                      
            class ViewHolder {
                //TextView text;
                TextView taskid;
                TextView date;
                ImageView status;
                TextView date1;
                LinearLayout llitembg;
            }
            final ViewHolder holder;
            	if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.salesorderitem_list,null);  
                    holder = new ViewHolder();
                    //holder.text = (TextView) convertView.findViewById(R.id.matrlno);
                    //holder.taskid = (TextView) convertView.findViewById(R.id.matrdsc);
                    //holder.date = (TextView) convertView.findViewById(R.id.matrl);
                    //holder.date1 = (TextView) convertView.findViewById(R.id.podate);
                    //holder.date1.setId(position);
                   // holder.status = (ImageView) convertView.findViewById(R.id.status);
                    holder.llitembg = (LinearLayout)convertView.findViewById(R.id.llitembg);
                    convertView.setTag(holder);
                } else {
                    holder=(ViewHolder)convertView.getTag();
                }
                           
                if(position%2 == 0)
                	holder.llitembg.setBackgroundResource(R.color.item_even_color);
                else
                	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            
            
                LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5;				
		        TableRow tr1 = new TableRow(SalesOrderCrtMaterialScreen.this.getApplicationContext());
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); 				
	            String text1Str = "", matIdStr = "", descStr = "", priceStr = "", idnpriceStr = "", valueValStr2 = "",valStr ="";           
				String imageUrlStr = "";				
	            try {
	            	if(mattItemDetailArrList != null){
	            		int size = soLabelArrayList.size();	            		               
		                valTV = new TextView[size];		               
		                
		                SalesOrderConstants.showLog("listview ");
	            		//for(int k= 0;k<size;k++){
	            		stockMap = (HashMap<String, String>) productListViewArray.get(position); 
	            		//holder.llitembg.setOrientation(LinearLayout.HORIZONTAL);
	            		 holder.llitembg.removeAllViews();					
	            		 			            		
	            		 curLabel = DBConstants.SO_HEAD_COL_VBELN;	            		
	            		 matIdStr = (String) stockMap.get(DBConstants.SO_HEAD_COL_VBELN);  
	            		 
	            		 final String pId = matIdStr;	            		
	            		 LinearLayout llmclm = new LinearLayout(SalesOrderItemScreen.this);
	            		 llmclm.removeAllViews();
	            			llmclm.setLayoutParams(new LinearLayout.LayoutParams(
	            					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));	            		 		
           				llmclm.setOrientation(LinearLayout.HORIZONTAL); 	              				
           				llmclm.setBackgroundResource(R.drawable.listview_border);	              				
           				
           				LinearLayout llmclm4 = new LinearLayout(SalesOrderItemScreen.this);  
           				llmclm4.removeAllViews();	
           				llmclm4.setOrientation(LinearLayout.VERTICAL);       				
              				
              				
	            		if(soLabelArrayList != null && soLabelArrayList.size() > 0){  	            			
	                        for(int i=0; i<soLabelArrayList.size(); i++){	
	                        	LinearLayout llmclm2 = new LinearLayout(SalesOrderItemScreen.this.getApplicationContext());
		            		 	llmclm2.removeAllViews();	
	              				llmclm2.setOrientation(LinearLayout.HORIZONTAL);	
	              				
	              			//detail icon
	        					LinearLayout detaill = new LinearLayout(SalesOrderItemScreen.this.getApplicationContext());
	              				detaill.removeAllViews();	
	              				detaill.setOrientation(LinearLayout.HORIZONTAL);
	              				
	                        	String nameVal = soLabelArrayList.get(i).toString().trim();		                    					                    		
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
	        						        							        						
	        					//valStr = (String) stockMap.get(metaValStr);	             		
	                    		if(valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAPPABLE_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || valueValStr.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG)){	                    				                    					                    		
		                    		valStr = (String) stockMap.get(metaValStr);	         
		                    		SapGenConstants.showLog("valStr:  "+valStr);
	                				TextView valtv = new TextView(SalesOrderItemScreen.this);     			                    			
	                				valtv = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);	                    							                                           				
	                    				 if(valStr != null && valStr.length() > 0){  	                    					 	 				        			
	 				                    		if(labValStr!=null)
	 				                    			valStr =labValStr+" "+valStr;	 				                    		
	 				                    			valtv.setText(valStr);
	 				                    			 			                    					 				                    			 				                    			
	                    				 }	 									
	                    				 valtv.setTextSize(10);
	                    				 valtv.setId(i);	       	                    				 
	                    				 valtv.setGravity(Gravity.LEFT);   
	                    				 if(!metaValStr.equals(DBConstants.SO_MAIN_COL_POSNR)){
	                    					 //llmclm2.addView(valtv);
	                    					 llmclm4.addView(valtv);
	                    					 llmclm.addView(llmclm4);
	                    					 //holder.llitembg2.setOrientation(LinearLayout.VERTICAL); 
                     					 	holder.llitembg.addView(llmclm);                                					 	
                     					 	 holder.llitembg3.addView(holder.llitembg2);
                     					 	holder.llitembg.addView(holder.llitembg3);
	                    				 }else{
	                    					 llmclm2.addView(valtv);
	                    					 //holder.llitembg.setOrientation(LinearLayout.HORIZONTAL);
                     					 	holder.llitembg.addView(llmclm2);  
	                    				 }                       	
	                    			
	                    		}	                    		                  		
	                        }
	                        
	                        for(int i=0; i<navigationList.size(); i++){	   
	                        	SapGenConstants.showLog("i value:  "+i);
	                        	try{
	                        	
    		            		 	LinearLayout llmclm2 = new LinearLayout(SalesOrderItemScreen.this);  
    	              				llmclm2.removeAllViews();	
	                    			llmclm2.setOrientation(LinearLayout.VERTICAL);     
	                    			
    	              				
    	                        	String nameVal = navigationList.get(i).toString().trim();		                    					                    		
    	                    		String labValStr = "", valueValStr = "";
    	        					String metaValStr = "";
    	        					String typeValStr = "";
    	        					if(nameVal.indexOf("::") >= 0){
    	        						String[] separated = nameVal.split("::");
    	        						if(!separated.equals(null) && separated.length > 0){
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
    	        					
    	        					 if(valueValStr.equalsIgnoreCase(DBConstants.ACTION_DETAIL_TAG)){
    	                    			SapGenConstants.showLog("3"); 	        	                    		  
    	                    			ImageView syncBMF = new ImageView(SalesOrderItemScreen.this); 
    			                         syncBMF.setId(i);				                               			                       
    			                         syncBMF.setBackgroundResource(R.drawable.arrow); 	
    			                         syncBMF.setOnClickListener(new View.OnClickListener() {
 	  										public void onClick(View view) {
 	  											int id = view.getId();	 											
 	  											showStockListDetailScreen(pId, curLabel);
 	  										}	
 	  			                        });
    			                         LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                                 LinearLayout.LayoutParams.WRAP_CONTENT);
    			                         LinearLayout ll = new LinearLayout(SalesOrderItemScreen.this);
    			                         ll.setId(2131230808);
    			                         ll.setGravity(Gravity.RIGHT);
    			                         ll.addView(syncBMF);
    			                         llmclm.addView(ll,ll_params);        				                      
    				                     SapGenConstants.showLog("5"); 	        				                      
    				                     holder.llitembg.addView(llmclm);
    	                    		}    
	                        	}catch(Exception e){
    	                        	SalesOrderConstants.showLog("Error in Adapter:"+ e.toString());
    	                        }
	                        }
	                        
	            		}
	            	}	            	            	            
            }catch(Exception e){
            	SalesOrderConstants.showLog("Error in Adapter:"+ e.toString());
            }
            return convertView;	
            
          }
             
        public void removeAllTasks() {
            notifyDataSetChanged();
        }    
       
    }//End of PriceListAdapter
*/	
	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View view, final int position, long arg3) {
			try{
				
				//int id = view.getId();
				int id=position;
				//showStockListDetailScreen(id);
			}
			catch (Exception dee) {
				SalesOrderConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}

	};
	
    /*private void drawSubLayout(){
		try{
			TableLayout tl = (TableLayout)findViewById(R.id.sorditemtbllayout2);
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			
			if(stocksItemArrList1 != null){
				SalesOrdProItemOpConstraints stkCategory = null;
				String itemStr = "", mattStr = "", qtyStr = "";
                String soUnitStr = "", valueStr = "", currStr = "";
                
                int rowSize = stocksItemArrList1.size();                
                SalesOrderConstants.showLog("Stocks List Size  : "+rowSize);
                mattTxtView = new TextView[rowSize];
                materialTxtView = new TextView[rowSize];
                
				for (int i =0; i < rowSize; i++) {
					stkCategory = (SalesOrdProItemOpConstraints)stocksItemArrList1.get(i);
                    if(stkCategory != null){
                    	itemStr = stkCategory.getSoldToParty().trim();
                    	mattStr = stkCategory.getMaterialNo().trim();
                    	qtyStr = stkCategory.getCummOrdQtySales().trim(); 
                    	soUnitStr = stkCategory.getSalesUnit().trim();
                    	valueStr = stkCategory.getNetValDocCurr1().trim();        
                    	currStr = stkCategory.getSDDocCurr().trim();
                        
                        tr = new TableRow(this);
                                                
                        mattTxtView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					mattTxtView[i].setText(itemStr);
    					mattTxtView[i].setWidth(headerWidth1);
    					//mattTxtView[i].setTextColor(Color.BLUE);
    					mattTxtView[i].setId(i);
    					mattTxtView[i].setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								int id = view.getId();	
								showStockListDetailScreen(id);
							}	
                        });
    					mattTxtView[i].setGravity(Gravity.LEFT);
    					mattTxtView[i].setPadding(10,0,0,0);
    					
                        materialTxtView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
                        materialTxtView[i].setText(mattStr);
                        materialTxtView[i].setWidth(headerWidth2);
                        materialTxtView[i].setId(i);
                        materialTxtView[i].setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								int id = view.getId();	
								showStockListDetailScreen(id);
							}	
                        });
                        materialTxtView[i].setGravity(Gravity.LEFT);
                        materialTxtView[i].setPadding(10,0,0,0);
    					
    					TextView qtyTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					qtyTxtView.setText(qtyStr);
    					qtyTxtView.setWidth(headerWidth3);
    					qtyTxtView.setGravity(Gravity.RIGHT);
    					qtyTxtView.setPadding(0,0,10,0);
    					
    					TextView soUnitTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					soUnitTxtView.setText(soUnitStr);
    					soUnitTxtView.setWidth(headerWidth4);
    					soUnitTxtView.setGravity(Gravity.LEFT);
    					soUnitTxtView.setPadding(10,0,0,0);
    					
    					TextView valueTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);					
    					valueTxtView.setText(valueStr);
    					valueTxtView.setWidth(headerWidth5);
    					valueTxtView.setGravity(Gravity.RIGHT);
    					valueTxtView.setPadding(0,0,10,0);
    					
    					TextView currTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					currTxtView.setText(currStr);
    					currTxtView.setWidth(headerWidth6);
    					currTxtView.setGravity(Gravity.LEFT);
    					currTxtView.setPadding(10,0,0,0);
    					
    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
    						mattTxtView[i].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						materialTxtView[i].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						qtyTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						soUnitTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						valueTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						currTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    					}
    										
    					tr.addView(mattTxtView[i]);
    					tr.addView(materialTxtView[i]);
    					tr.addView(qtyTxtView);
    					tr.addView(soUnitTxtView);
    					tr.addView(valueTxtView);
    					tr.addView(currTxtView);
    					
    					if(i%2 == 0)
    						tr.setBackgroundResource(R.color.item_even_color);
			            else
			            	tr.setBackgroundResource(R.color.item_odd_color);
    					
    					tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                    }					
				}
			}
		}
		catch(Exception asgf){
			SalesOrderConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
*/    
    private void showStockListDetailScreen(String selIndex, String colmStr){
        try{
        	SalesOrderConstants.showLog("Selected index : "+selIndex);
        	Intent intent = new Intent(this, SalesOrderItemDetailScreen.class);
			intent.putExtra("selectdLabelStr", colmStr);
			intent.putExtra("selectdSoNumb", selIndex);
			startActivityForResult(intent, SapGenConstants.SALESORD_DETAIL_SCREEN);
        /*	boolean errFlag = false;
        	if(stkCategory != null){
            	try {
            		if(SalesOrderConstants.stocksItemMainArrList != null){
            			SalesOrderConstants.showLog("stocksItemMainArrList Size : "+SalesOrderConstants.stocksItemMainArrList.size());
            			
						Intent intent = new Intent(this, SalesOrderItemDetailScreen.class);
						intent.putExtra("stkCategoryObj", stkCategory);
						intent.putExtra("SelIndex", selIndex);
						startActivityForResult(intent, SapGenConstants.SALESORD_DETAIL_SCREEN);
            		}
            		else
            			errFlag = true;
				} 
				catch (Exception e) {
					SalesOrderConstants.showErrorLog(e.getMessage());
				}
            }
            else
                errFlag = true;
        	
        	if(errFlag == true)
            	SalesOrderConstants.showErrorDialog(this, ""+R.string.SALESORDPRO_STKLIST_ERR_DETCANT);*/
        }
        catch(Exception assf){
        	SalesOrderConstants.showLog("On showStockListItemScreen : "+assf.toString());
        }
    }//fn showStockListItemScreen
    
   
    public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		  SalesOrderConstants.showLog("Text : "+s.toString());
		  searchItemsAction(s.toString());
	} 
	
	private void searchItemsAction(String match){  
        try{
            searchflag = true;           
            searchStr = match;
            SalesOrdProItemOpConstraints stkObj = null;
            String itemNoStr = "", mattStr = "";
            SalesOrderConstants.showLog("stocksItemCopyArrList1 Size :"+stocksItemCopyArrList1.size()+" : "+match.length());
            if((stocksItemCopyArrList1 != null) && (stocksItemCopyArrList1.size() > 0)){
            	if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){ 
            		if(stocksItemArrList1.size() > 0)
            			stocksItemArrList1.clear();
            		
                    SalesOrderConstants.showLog("stocksItemCopyArrList1 Size1 : "+stocksItemCopyArrList1.size()+" : "+SalesOrderConstants.stocksItemMainArrList.size()+" : "+stocksItemArrList1.size());
                    
                    for(int i = 0; i < stocksItemCopyArrList1.size(); i++){  
                    	SalesOrderConstants.showLog("Inside If ");
                        stkObj = null;
                        itemNoStr = "";
                        mattStr = "";
                        SalesOrderConstants.showLog("Match : "+match+" ival : "+i);
                        stkObj = (SalesOrdProItemOpConstraints)stocksItemCopyArrList1.get(i);
                        SalesOrderConstants.showLog("Match : "+match+" ival : "+i+" : "+stkObj);  
                        if(stkObj != null){
                        	itemNoStr = stkObj.getSoldToParty().trim().toLowerCase();
                        	mattStr = stkObj.getMaterialNo().trim().toLowerCase();
                            match = match.toLowerCase();
                            SalesOrderConstants.showLog("match :"+match+" : "+itemNoStr+" : "+mattStr);
                            if((itemNoStr.indexOf(match) >= 0) || (mattStr.indexOf(match) >= 0)){
                            	stocksItemArrList1.add(stkObj);
                            }
                        }
                    }//for 
                    initLayout();
                    ListviewAdapter();
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
                    initLayout();
                    ListviewAdapter();
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
    
}//End of class SalesOrderItemScreen
