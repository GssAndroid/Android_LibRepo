package com.globalsoft.SalesOrderLib;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProItemOpConstraints;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesOrderItemDetailScreen extends Activity {
	
	private TextView[] salesOrdLblTV, salesOrdItemLblTV;
	private EditText[] salesOrdValET, salesOrdItemValET;
	private TextView myTitle;
	
	private ImageButton showPrevBtn, showNextBtn;
	private DataBasePersistent dbitemObj = null;
	private DataBasePersistent dbObjUIConf = null;
	private ArrayList<String> CustHeaderList = new ArrayList<String>();
	private ArrayList stocksItemArrList1 = new ArrayList();
	private ArrayList itemHeadList = new ArrayList();
	HashMap<String, String> itemHeadValuesList = null; 
	private ArrayList headeritemdetailList = new ArrayList();
	private ArrayList itemBlckLabelList = new ArrayList();
	HashMap<String, String> itemBlckValList = null; 
	HashMap<String, String> itemblockMap = null; 	
	HashMap<String, String> itemheaderMap = null;
	private ArrayList stocksItemCopyArrList1 = new ArrayList();
	private ArrayList mattItemDetailArrList = new ArrayList();
	private ArrayList SOItemList = new ArrayList();
	
	/*private SalesOrdProHeadOpConstraints stkCategory = null;
	private SalesOrdProItemOpConstraints itemCategory = null;*/
	
	private int selIndex = 0, flag=-1, itemcols=0, pos=0;
	private int dispwidth = 300;
	 private boolean tableExits = false;
	private String selectedSoNumb ="", selectdLabel="";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	/*requestWindowFeature(Window.FEATURE_LEFT_ICON);
        	SapGenConstants.setWindowTitleTheme(this);
			setContentView(R.layout.salesorditemdetail);*/
			
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.salesorditemdetail); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			 myTitle = (TextView) findViewById(R.id.myTitle);			

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			myTitle.setText("Sales Order Item Detail Screen");
			
			selectedSoNumb = this.getIntent().getStringExtra("selectdSoNumb");
			selectdLabel = this.getIntent().getStringExtra("selectdLabelStr");
			if(CustHeaderList!=null)
				CustHeaderList.clear();
			CustHeaderList =  (ArrayList) SalesOrderConstants.metaheadlistArray.clone();			
	        stocksItemArrList1 = (ArrayList) SalesOrderConstants.stocksItemMainArrList.clone();	       
			/*stkCategory = (SalesOrdProHeadOpConstraints) this.getIntent().getSerializableExtra("stkCategoryObj");
			selIndex = this.getIntent().getIntExtra("SelIndex", 0);
			SalesOrderConstants.showLog("SelIndex : "+selIndex);*/

	     /*   if(stkCategory != null){
				String sumIconStr = stkCategory.getStatusSummary().trim();   
				if(sumIconStr.equalsIgnoreCase("R"))
					setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_red);
                else if(sumIconStr.equalsIgnoreCase("G"))
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_green);
                else if(sumIconStr.equalsIgnoreCase("Y"))
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_yellow);
                else
                	setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_black);
				
				//this.setTitle(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+stkCategory.getDocumentNo().trim()+" "+getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE2));
				myTitle.setText(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+stkCategory.getDocumentNo().trim()+" "+getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE2));
			}
			else{
	        	//setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.tl_black);
				//this.setTitle(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+" "+getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE2));
				myTitle.setText(getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE1)+" "+getString(R.string.SALESORDPRO_SOLIST_ITEMDET_TITLE2));
			}*/
			
	        dispwidth = SapGenConstants.getDisplayWidth(this);
	        //setTitleValue();
	        initheadLayout();
			initItemLayout();
        } catch (Exception de) {
        	SalesOrderConstants.showErrorLog(de.toString());
        }
    }//
	
	private void setTitleValue() {
    	try {
    		if(dbObjUIConf == null)
				dbObjUIConf = new DataBasePersistent(this, DBConstants.TABLE_SALESORDER_CONTEXT_LIST);
    		
    		String title = dbObjUIConf.getTitle(DBConstants.DEVICE_TYPE_WIDE_DETAIL_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
    		if(title != null && title.length() > 0){
    			myTitle.setText(title);
        		//SapGenConstants.showLog("title: "+title);
    		}   		    		
    		//searchbarhint = dbObjUIConf.getSearchBarHint(DBConstants.DEVICE_OVERVIEW_SEARCH, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, DBConstants.CNTXT4_ATTR_TAG, DBConstants.VALUE_SEARCHBAR_TAG);
    		/*if(searchbarhint != null && searchbarhint.length() > 0){
    			searchET.setHint(searchbarhint);
        		SapGenConstants.showLog("SearchHint: "+searchbarhint);
    		}   */ 		
    		dbObjUIConf.closeDBHelper();
    		
    	} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On setTitleValue: "+e.toString());
		}
	}//fn setTitleValue
	
	
	private void initheadLayout(){
		try {
			flag=1;
			getAllItemDetails(CustHeaderList,DBConstants.TABLE_SALESORDER_HEAD_LIST);   
			String labelStr = "",valueStr = "",descriStr = "";
			//SalesOrderConstants.showLog("1");
			showPrevBtn = (ImageButton) findViewById(R.id.showprevbtn);
			showPrevBtn.setOnClickListener(showPrevBtnListener); 
			showPrevBtn.setVisibility(View.GONE);
			
			showNextBtn = (ImageButton) findViewById(R.id.shownextbtn);
			showNextBtn.setOnClickListener(showNextBtnListener); 
			showNextBtn.setVisibility(View.GONE);
			
			if(itemHeadList!=null)
				itemHeadList.clear();
			if(itemHeadValuesList!=null)
				itemHeadValuesList.clear();
			
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
			         			if(metaLabel != null && metaLabel.length() > 0){
			         				lab = itemHeadValuesList.containsKey(metaLabel);
			         			}													
						if(metaValue != null && metaValue.length() > 0){
	                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG)){
	                 			LinearLayout llmclm = new LinearLayout(this);
	                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
	                        	String valTotStr = "";
	                 			if(lab){
	                 				String labStr = (String)itemHeadValuesList.get(metaLabel);
	                 				if(labStr != null && labStr.length() > 0){
	                 					salesOrdLblTV[i] = new TextView(this);
	                 					salesOrdLblTV[i].setPadding(5,5,5,5); 
	            						salesOrdLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
	            						salesOrdLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
	            						salesOrdLblTV[i].setMinWidth(100);
	            						salesOrdLblTV[i].setWidth(labelWidth);                       					
	            						salesOrdLblTV[i].setText(labStr);
	                					llmclm.addView(salesOrdLblTV[i]); 
	                 				}
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
	
	 private void initItemLayout(){
			try {			
				flag =0;		
				getAllItemDetails(stocksItemArrList1,DBConstants.TABLE_SALESORDER_ITEM_LIST);   
				String 	itemlabelStr = "",itemvalueStr="";		
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
				         			if(metaLabel != null && metaLabel.length() > 0){
				         				lab = itemBlckValList.containsKey(metaLabel);
				         			}													
							if(metaValue != null && metaValue.length() > 0){
		                 		if(metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DISPLAY_TAP_TAG) || metaValue.equalsIgnoreCase(DBConstants.VALUE_DESCRIBED_TAP_TAG)){
		                 			LinearLayout llmclm = new LinearLayout(this);
		                 			llmclm.setOrientation(LinearLayout.HORIZONTAL);
		                        	String valTotStr = "";
		                 			if(lab){
		                 				String labStr = (String)itemBlckValList.get(metaLabel);
		                 				if(labStr != null && labStr.length() > 0){
		                 					salesOrdItemLblTV[i] = new TextView(this);
		                 					salesOrdItemLblTV[i].setPadding(5,5,5,5); 
		                 					salesOrdItemLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
		                 					salesOrdItemLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
		                 					salesOrdItemLblTV[i].setMinWidth(100);
		                 					salesOrdItemLblTV[i].setWidth(labelWidth);                       					
		                 					salesOrdItemLblTV[i].setText(labStr);
		                					llmclm.addView(salesOrdItemLblTV[i]); 
		                 				}
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
					/*for(int i=0; i<itemcols; i++){
						tr1 = new TableRow(this);					
						itemlabelStr = itemBlckLabelList.get(i).toString().trim();
	            		 //SapGenConstants.showLog("labelStr: "+labelStr);	  									 
						salesOrdItemLblTV[i] = new TextView(this);
						salesOrdItemValET[i] = new EditText(this);
											
						salesOrdItemLblTV[i].setPadding(5,5,5,5); 
						salesOrdItemLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
						salesOrdItemLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
						salesOrdItemLblTV[i].setMinWidth(100);
						salesOrdItemLblTV[i].setWidth(labelWidth);
						salesOrdItemLblTV[i].setText(itemBlckValList.get(i).toString());
						if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
							salesOrdItemLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
						
						//salesOrdItemValET[i].setText(itemvalueStr);
						salesOrdItemValET[i].setText("");
						salesOrdItemValET[i].setPadding(5,5,5,5);
						//salesOrdValET[i].setWidth(160);
						salesOrdItemValET[i].setWidth(editWidth);
						salesOrdItemValET[i].setEnabled(false);
						
						tr1.addView(salesOrdItemLblTV[i]);
						tr1.addView(salesOrdItemValET[i]);
						tr1.setLayoutParams(linparams);									
						tl.setBackgroundDrawable(getResources().getDrawable(R.drawable.salesborder));
						tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
					}
					getSalesOrderItemDetails(pos);
					SalesOrderConstants.showLog("Textview width : "+salesOrdItemLblTV[0].getWidth());
					SalesOrderConstants.showLog("EditText width : "+salesOrdItemValET[0].getWidth());	*/
					//getSalesOrderItemDetails(pos);
					// showPagination(pos);	
				}catch(Exception sf){}
			}catch (Exception ssdf) {
				SalesOrderConstants.showErrorLog("Error in initItemLayout : "+ssdf.toString());
			}
		}//fn initItemLayout
	 
	
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
					dbitemObj.getColumns();	 				
					dbitemObj.setTableName_ColumnName(this,tablename,stockitemlist);
										
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
	        	//setPageData();
			}catch(Exception sggh){				
				dbitemObj.closeDBHelper();				
				SapGenConstants.showErrorLog("On initDBConnection : "+sggh.toString());
			}			
	}// 
	
	/*private void initItemLayout(){
		try {			
			int cols = 9;
			salesOrdItemLblTV = new TextView[cols];
			salesOrdItemValET = new EditText[cols];
			
			try{
				TableLayout tl = (TableLayout)findViewById(R.id.salesorditemdettbllayout2);
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
				SalesOrderConstants.showLog("Edit and label Width : "+editWidth+" : "+labelWidth);
				
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				
				for(int i=0; i<cols; i++){
					tr1 = new TableRow(this);
		            	
					salesOrdItemLblTV[i] = new TextView(this);
					salesOrdItemValET[i] = new EditText(this);
					
					salesOrdItemLblTV[i].setText("");
					salesOrdItemLblTV[i].setTextColor(getResources().getColor(R.color.bluelabel));
					salesOrdItemLblTV[i].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
					salesOrdItemLblTV[i].setPadding(5,5,5,5); 
					salesOrdItemLblTV[i].setMinWidth(100);
					salesOrdItemLblTV[i].setWidth(labelWidth);
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
						salesOrdItemLblTV[i].setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
					
					salesOrdItemValET[i].setText("");
					salesOrdItemValET[i].setPadding(5,5,5,5);
					//salesOrdItemValET[i].setWidth(160);
					salesOrdItemValET[i].setWidth(editWidth);
					
					tr1.addView(salesOrdItemLblTV[i]);
					tr1.addView(salesOrdItemValET[i]);
					tr1.setLayoutParams(linparams);
					
					tl.setBackgroundDrawable(getResources().getDrawable(R.drawable.salesborder2));
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				}
				
				SalesOrderConstants.showLog("Textview width : "+salesOrdLblTV[0].getWidth());
				SalesOrderConstants.showLog("EditText width : "+salesOrdValET[0].getWidth());
								
				//salesOrdItemLblTV[0].setText(R.string.SALESORDPRO_OVIEW_DOCNO);
				salesOrdItemLblTV[0].setText(R.string.SALESORDPRO_OVIEW_IDNO);
				salesOrdItemLblTV[1].setText(R.string.SALESORDPRO_OVIEW_IDMATNO);
				salesOrdItemLblTV[2].setText(R.string.SALESORDPRO_OVIEW_NETVALINDOC);
				salesOrdItemLblTV[3].setText(R.string.SALESORDPRO_OVIEW_CUMORDQTY);
				//salesOrdItemLblTV[4].setText(R.string.SALESORDPRO_OVIEW_SALESUNIT);
				//salesOrdItemLblTV[6].setText(R.string.SALESORDPRO_OVIEW_IDSDDOCCURR);
				salesOrdItemLblTV[4].setText(R.string.SALESORDPRO_OVIEW_REASONRJCN);
				salesOrdItemLblTV[5].setText(R.string.SALESORDPRO_OVIEW_REASONBILBLK);
				salesOrdItemLblTV[6].setText(R.string.SALESORDPRO_OVIEW_OVERALLSTS);
				salesOrdItemLblTV[7].setText(R.string.SALESORDPRO_OVIEW_DELVSTS);
				salesOrdItemLblTV[8].setText(R.string.SALESORDPRO_OVIEW_BILLSTS);
				
				getSalesOrderItemDetails(selIndex);
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesOrderConstants.showErrorLog("Error in initItemLayout : "+ssdf.toString());
		}
	}//fn initItemLayout
*/			
	  private void showPagination(long index){
			try{
				int totalsize = mattItemDetailArrList.size();
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
				}
			}
			catch(Exception sfg){
				SalesOrderConstants.showErrorLog("Error in showPagination : "+sfg.toString());
			}
		}//fn showPagination
	
	  private OnClickListener showPrevBtnListener = new OnClickListener(){
	        public void onClick(View v) {
	        	//SalesOrderConstants.showLog("Show Prev btn clicked");
	        	pos = pos-1;
	        	SalesOrderConstants.showLog("pos"+pos);
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
	        	SalesOrderConstants.showLog("pos"+pos);
	        	int totalsize = mattItemDetailArrList.size()-1;
	        	if(pos > totalsize)
	        		pos = totalsize;
	        	initItemLayout();
	        	//getSalesOrderItemDetails(pos);
	        }
	    };
	
}//End of class SalesOrderItemDetailScreen

