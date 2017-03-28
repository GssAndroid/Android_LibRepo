package com.globalsoft.ProductLib;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.globalsoft.DataBaseLib.DBConstants;
import com.globalsoft.DataBaseLib.DataBasePersistent;
import com.globalsoft.ProductLib.Database.ProductDBConstants;
import com.globalsoft.ProductLib.Database.ProductShoppingCartListPersistent;
import com.globalsoft.ProductLib.ImgLoader.ImageLoader;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ProductDetailsForPhone extends Activity {

	private String matId = "", selMatId = "", ordQty = "";
	private ArrayList matIdList = new ArrayList();
	private ImageButton showPrevBtn, showNextBtn;
	private int selIndex = 0;
	private TextView[] valTV;
	private TextView[] tabTV;
	private int dispwidth = 300;
	
	private ArrayList productFieldList = new ArrayList();
	private ArrayList imageArrList = new ArrayList();
	private ArrayList cartArrList = new ArrayList();
	
	private String text1Str = "", matIdStr = "", descStr = "", priceStr = "", rateUnitStr = "", conUnitStr = "",
			imageUrlStr = "", text2Str = "", text3Str = "", text4Str = "";
	private TextView descTV, pnoTV, priceTV, rUnitTV, cUnitTV, text1TV, text2TV, text3TV, text4TV;
	//private TextView summaryTV, imagesTV, detailsTV, scartTV;
	private LinearLayout descLL, descLLimg, descLLd, pnoLL, priceLL, text1LL, text2LL, text3LL, text4LL, detailsLL, summaryLL, imagesLL;
	private ImageView imageIV;
	private EditText qtyEF;
	private TextView myTitle;
	private Gallery gallery;
	private ImageView gimageView;
	
	private DataBasePersistent dbObjAtta01 = null;
	private DataBasePersistent dbObj = null;
	private ProductShoppingCartListPersistent dbObjSCart = null;
	private String[] imageIDs = null;
	private Button cartbtn;
    public ImageLoader imageLoader; 

	private ArrayList tabStripValArrayList = new ArrayList();
	private HashMap<String, String> tabStripLabelMap = new HashMap<String, String>();	

	private ArrayList valArrayList = new ArrayList();
	private HashMap<String, String> labelMap = new HashMap<String, String>();	

	HashMap<String, String> stockMap = null;
	/*private ArrayList valArrayList = new ArrayList();
	private HashMap<String, String> labelMap = new HashMap<String, String>();	

	private ArrayList valArrayList = new ArrayList();
	private HashMap<String, String> labelMap = new HashMap<String, String>();*/
	
	///private DataBasePersistent dbObj = null;
	private LinearLayout dynll, menuTVLL, dynDetailsll; // detAcll;
	private ArrayList productListViewArray = new ArrayList();
	private RelativeLayout bodylayout;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.product_details); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			myTitle = (TextView) findViewById(R.id.myTitle);
			
			myTitle.setText(getResources().getString(R.string.MAINSCR_PROD_DETAILS_TITLE));
			
			dispwidth = SapGenConstants.getDisplayWidth(this);	
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			
			if(cartArrList != null){
				cartArrList.clear();
			}
			bodylayout = (RelativeLayout) findViewById(R.id.bodylayout);
			setTitleValue();
			imageLoader = new ImageLoader(ProductDetailsForPhone.this);
			matIdList = (ArrayList)this.getIntent().getSerializableExtra("selMatIds");
			selMatId = (String)this.getIntent().getStringExtra("matIdStr");
			SapGenConstants.showLog("SelIndex : "+selIndex);
			if(selIndex >= 0){
				if(matIdList != null && matIdList.size() > 0){
					if(selMatId != null && selMatId.length() > 0){
			    		int pos = matIdList.indexOf(selMatId);
			    		SapGenConstants.showLog("sel pos "+pos);
			    		selIndex = pos;						
						String idStr = (String)matIdList.get(selIndex).toString().trim();
			    		matId = idStr;
					}
				}else{
					if(selMatId != null && selMatId.length() > 0){
			    		matId = selMatId;
					}
				}
			}		
			initLayout();			
        } catch (Exception de) {
        	SapGenConstants.showErrorLog(de.toString());
        }
    }	
	
	private void initLayout(){
		try{				
			/*imagesTV = (TextView) findViewById(R.id.imagesTV);
			imagesTV.setOnClickListener(imagesTVListener);  
			summaryTV = (TextView) findViewById(R.id.summaryTV);
			summaryTV.setOnClickListener(summaryTVListener);
			detailsTV = (TextView) findViewById(R.id.detailsTV);
			detailsTV.setOnClickListener(detailsTVListener);
			scartTV = (TextView) findViewById(R.id.scartTV);
			scartTV.setOnClickListener(scartTVListener);*/


			dynll = (LinearLayout) findViewById(R.id.dynll);
			dynll.removeAllViews();
			dynll.setOrientation(LinearLayout.VERTICAL);
			
			dynDetailsll = (LinearLayout) findViewById(R.id.dynDetailsll);
			dynDetailsll.removeAllViews();
			dynDetailsll.setOrientation(LinearLayout.VERTICAL);
			
			/*detAcll = (LinearLayout) findViewById(R.id.detAcll);
			detAcll.removeAllViews();
			detAcll.setOrientation(LinearLayout.VERTICAL);*/
			
			showPrevBtn = (ImageButton) findViewById(R.id.showprevbtn);
			showPrevBtn.setOnClickListener(showPrevBtnListener); 
			showPrevBtn.setVisibility(View.GONE);
			
			showNextBtn = (ImageButton) findViewById(R.id.shownextbtn);
			showNextBtn.setOnClickListener(showNextBtnListener); 
			showNextBtn.setVisibility(View.GONE);
						
			detailsLL = (LinearLayout) findViewById(R.id.detailsll);
			summaryLL = (LinearLayout) findViewById(R.id.summaryll);
			imagesLL = (LinearLayout) findViewById(R.id.imagesll);
					
        	summaryLL.setVisibility(View.VISIBLE);
        	detailsLL.setVisibility(View.GONE);
        	imagesLL.setVisibility(View.GONE);

			//descLL = (LinearLayout) findViewById(R.id.descll);
        	gallery = (Gallery) findViewById(R.id.gallery1);     
        	gimageView = (ImageView) findViewById(R.id.imageview1);	
			
			initValueLayout();
		} catch (Exception de) {
			SapGenConstants.showErrorLog("Error in initLayout : "+de.toString());
        }
    }//fn initLayout	

	Button bt;
	private void initValueLayout(){
		try{						
        	showPagination(selIndex);        	
        	if(matId != null && matId.length() > 0){
				SapGenConstants.showLog("matId "+matId);
				if(DBConstants.PRODUCT_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_LIST_DB_TABLE_NAME;
	    		}
				if(dbObj != null)
					dbObj = null;
				if(dbObj == null)
					dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
				ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_LIST_DB_TABLE_NAME);
				productFieldList = dbObj.readAllFieldDataBySelctdIdFromDB(this, matId, DBConstants.MATNR_COLUMN_NAME);
				dbObj.closeDBHelper();
				if(productFieldList != null && productFieldList.size() > 0){
					stockMap = (HashMap<String, String>) productFieldList.get(0);  
            		descStr = (String) stockMap.get(DBConstants.MAKTX_COLUMN_NAME);   
                	matIdStr = (String) stockMap.get(DBConstants.MATNR_COLUMN_NAME);      
                    priceStr = (String) stockMap.get(DBConstants.KBETR_COLUMN_NAME);  
                    text1Str = (String) stockMap.get(DBConstants.ZZTEXT1_COLUMN_NAME);
                    text2Str = (String) stockMap.get(DBConstants.ZZTEXT2_COLUMN_NAME);
                    text3Str = (String) stockMap.get(DBConstants.ZZTEXT3_COLUMN_NAME);
                    text4Str = (String) stockMap.get(DBConstants.ZZTEXT4_COLUMN_NAME);
                    rateUnitStr = (String) stockMap.get(DBConstants.KONWA_COLUMN_NAME);
                    conUnitStr = (String) stockMap.get(DBConstants.KMEIN_COLUMN_NAME);
				}
				if(DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME != null && DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME.length() > 0){
	    			DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME;
	    		}
				if(dbObjAtta01 != null)
					dbObjAtta01 = null;
				if(dbObjAtta01 == null)
					dbObjAtta01 = new DataBasePersistent(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
				ProductDBConstants.setTableName_ColumnName(this, DBConstants.PRODUCT_ATTA01R_LIST_DB_TABLE_NAME);
				imageArrList = dbObjAtta01.getAllImageUrl(matIdStr);
	    		dbObjAtta01.closeDBHelper();			
				
	    		if(imageArrList != null && imageArrList.size() > 0){
	    			imageIDs = new String[imageArrList.size()]; 
	    			for(int i = 0; i < imageIDs.length; i++) { 
	    				imageIDs[i] = imageArrList.get(i).toString().trim(); 
	    			}
	    		}
			}
        	
            gallery.setAdapter(new ImageAdapter(this));          
            gimageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if(imageIDs != null && imageIDs.length > 0){
            	String imgUrl = imageIDs[0];
            	if(imgUrl != null && imgUrl.length() > 0){
                	imageLoader.DisplayImage(imgUrl, gimageView);  
            	}  
            }
            
            gallery.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position,
                        long id) {
                    // ---display the images selected---
                    if(imageIDs != null && imageIDs.length > 0){
                        URL url = null;
                        try {
                        	String urlStr = imageIDs[position];
                        	try{
        			            imageLoader.DisplayImage(urlStr, gimageView);    	
        					} catch (Exception e) {
								SapGenConstants.showErrorLog("On DownloadImageTask : "+e.toString());SapGenConstants.showErrorDialog(ProductDetailsForPhone.this, getResources().getString(R.string.IMAGE_NOT_AVAILABLE_GIVEN_URL));
								SapGenConstants.showErrorDialog(ProductDetailsForPhone.this, getResources().getString(R.string.IMAGE_NOT_AVAILABLE_GIVEN_URL));
							} 
                        } catch (Exception rfr) {
                			SapGenConstants.showErrorLog("Error in gallery : "+rfr.toString());
                        }
                    }
                }
            });
            
            DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
            //Detail-TabStrip value
            tabStripValArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, DBConstants.DEVICE_TYPE_WIDE_DETAIL_TAB_STRIP_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
            tabStripLabelMap = DataBasePersistent.readAllLablesFromDB(this, DBConstants.DEVICE_TYPE_WIDE_DETAIL_TAB_STRIP_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG);
            menuTVLL  = (LinearLayout) findViewById(R.id.menuTVLL);
            menuTVLL.removeAllViews();
            menuTVLL.setOrientation(LinearLayout.HORIZONTAL);
            int tvsize = 0;
			SapGenConstants.showLog("tabStripValArrayList : "+tabStripValArrayList.size());			 
     		if(tabStripValArrayList != null && tabStripValArrayList.size() > 0){
     			SapGenConstants.showLog("tabStripValArrayList : "+tabStripValArrayList.size());
                valTV = new TextView[tabStripValArrayList.size()];
                for(int i = 0; i < tabStripValArrayList.size(); i++){       
                	String secLine = "";         	 
                	ArrayList list = (ArrayList) tabStripValArrayList.get(i);
                	if(list != null){
    					SapGenConstants.showLog("list : "+list.size());
    					if(list.size() > 0){
        					if (list.size() > 1){
        						tabTV = new TextView[list.size()];
                     			LinearLayout llmclm = new LinearLayout(this);
                     			llmclm.setOrientation(LinearLayout.HORIZONTAL);
                     			
                     			//Getting no of ACTION-TAB-LABEL
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
                                 	
                                 	boolean lab = false;  
                         			if(metaLabel != null && metaLabel.length() > 0){
                         				lab = tabStripLabelMap.containsKey(metaLabel);
                						SapGenConstants.showLog("metaValue lab : "+lab);
                         			}                         	   
                                 	if(metaValue != null && metaValue.length() > 0){                   			
                                 		if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_WIDE_DETAIL_ACTION_TAB_LABEL_TAG)){  
                                 			tvsize += 1;
                                 		}
                                 	}
                     			}    
                     			
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
                                 	
                                 	boolean lab = false;  
                         			if(metaLabel != null && metaLabel.length() > 0){
                         				lab = tabStripLabelMap.containsKey(metaLabel);
                						SapGenConstants.showLog("metaValue lab : "+lab);
                         			}                         	   
                                 	if(metaValue != null && metaValue.length() > 0){                   			
                                 		if(metaValue.equalsIgnoreCase(DBConstants.DEVICE_TYPE_WIDE_DETAIL_ACTION_TAB_LABEL_TAG)){  
                                 			if(lab){  
                                 				String labStr = (String)tabStripLabelMap.get(metaLabel);
                        						SapGenConstants.showLog("labStr : "+labStr);
                    	        				final String pId = metaLabel;
                                 				if(labStr != null && labStr.length() > 0){      
                                 					final int id = l; 
                                 					final int listSize = tvsize; //list.size();
                                					tabTV[l] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
                                					tabTV[l].setTextColor(getResources().getColor(R.color.bluelabel));	
                                					tabTV[l].setTypeface(null, 1); //0 = Normal 1 = Bold 2 = Italic
                                					tabTV[l].setPadding(5, 5, 5, 5);
                                					tabTV[l].setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                                					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
                                						tabTV[l].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
                                					}
                                					if(l >= 0){
                                    					if(l == 0){
                                        					tabTV[l].setBackgroundResource(R.drawable.tvborder_sel);
                                    					}else{
                                        					tabTV[l].setBackgroundResource(R.drawable.tvborder_unsel);
                                    					} 
                                					}
                                					tabTV[l].setOnClickListener(new View.OnClickListener() {
                										public void onClick(View view) {
                											SapGenConstants.showLog("pId  "+pId);
                											SapGenConstants.showLog("id  "+id);
                											SapGenConstants.showLog("tvsize  "+listSize);
                                    						for(int tv = 0; tv < listSize; tv++){
                                    							if(tv != id){
                        											SapGenConstants.showLog("tv  "+tv);
                                                					tabTV[tv].setBackgroundResource(R.drawable.tvborder_unsel);
                                    							}else{
                        											SapGenConstants.showLog("tv  "+tv);
                                                					tabTV[tv].setBackgroundResource(R.drawable.tvborder_sel);
                                    							}
                                    						}                                        					
                											changeDetailScreen(pId);
                										}	
                			                        });                                					
                                					tabTV[l].setLayoutParams(new
                                							 LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));                        					
                                					tabTV[l].setText(labStr);
                                    			    llmclm.addView(tabTV[l]);
                                 				}
                                 			} 
                                 		}else if(metaValue.equalsIgnoreCase(DBConstants.ACTION_FIELD_ICON_ICON_SHOPPING_CART_TAG)){			                    			    		
			                        		ImageView iv = new ImageView(this);
			                        		LinearLayout.LayoutParams layoutParamsiv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			                        		iv.setLayoutParams(layoutParamsiv);
			                        		iv.setPadding(10, 0, 0, 0);
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
                             	menuTVLL.addView(llmclm, layoutParams);  
        					}
    					}    					
                	}
                }
     		}
			dynll.removeAllViews();
			displayUI(DBConstants.DEVICE_TYPE_WIDE_DETAIL_SUMMARY_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, dynll);
     		
     		/*detAcll.removeAllViews();
     		displayUI(DBConstants.DEVICE_TYPE_WIDE_DETAIL_ACTION_BAR_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_1LN_SCREEN_TAG, detAcll);*/            
			 
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in initValueLayout : "+sfg.toString());
		}
	}//fn initValueLayout

	/*private OnClickListener summaryTVListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("summary tv clicked");
        	summaryTV.setBackgroundResource(R.drawable.tvborder_sel);
        	imagesTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	detailsTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	scartTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	summaryLL.setVisibility(View.VISIBLE);
        	imagesLL.setVisibility(View.GONE);
        	detailsLL.setVisibility(View.GONE);
        }
    };
    
	private OnClickListener imagesTVListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("images tv clicked");
        	summaryTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	imagesTV.setBackgroundResource(R.drawable.tvborder_sel);
        	detailsTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	scartTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	summaryLL.setVisibility(View.GONE);
        	imagesLL.setVisibility(View.VISIBLE);
        	detailsLL.setVisibility(View.GONE);
            if(imageIDs == null || imageIDs.length == 0){
            	SapGenConstants.showErrorDialog(ProductDetailsForPhone.this, getResources().getString(R.string.IMAGE_NOT_AVAILABLE));
            }
			descLLimg = (LinearLayout) findViewById(R.id.descllimg);
			if(descStr != null && descStr.length() > 0){
				descLLimg.setVisibility(View.VISIBLE);
				descTV = (TextView) findViewById(R.id.descValimg);
				descTV.setText(descStr+"("+matId+")");
			}else{
				descLLimg.setVisibility(View.GONE);
			}
        }
    };
    
	private OnClickListener detailsTVListener = new OnClickListener(){
        public void onClick(View v) {
        	summaryTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	imagesTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	detailsTV.setBackgroundResource(R.drawable.tvborder_sel);
        	scartTV.setBackgroundResource(R.drawable.tvborder_unsel);
        	detailsLL.setVisibility(View.VISIBLE);
        	imagesLL.setVisibility(View.GONE);
        	summaryLL.setVisibility(View.GONE);
        	SapGenConstants.showLog("details tv clicked");
        	SapGenConstants.showLog("descStr: "+descStr);
			descLLd = (LinearLayout) findViewById(R.id.desclld);
			if(descStr != null && descStr.length() > 0){
				descLLd.setVisibility(View.VISIBLE);
				descTV = (TextView) findViewById(R.id.descVald);
				descTV.setText(descStr+"("+matId+")");
			}else{
				descLLd.setVisibility(View.GONE);
			}
        }
    };
    */
	private OnClickListener scartTVListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("shopping cart tv clicked");
        	showSCartScreen();
        }
    };
    
    private void showSCartScreen(){
    	try {
    		if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductDetailsForPhone.this);
        	ArrayList cartArrList = dbObjSCart.getCartIdList();
        	if(cartArrList.size() > 0){
    			Intent intent = new Intent(ProductDetailsForPhone.this, ProductShoppingCartForTablet.class);
    			startActivityForResult(intent,SapGenConstants.PRODUCT_CAT_SCART_SCREEN_TBL);
        	}else{
        		SapGenConstants.showErrorDialog(ProductDetailsForPhone.this, getResources().getString(R.string.MAINSCR_PROD_SHOPINGCART_NOLIST));
        	}
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("On showSCartScreen : "+ce.toString());
		}
    }//fn showSCartScreen
    
	private OnClickListener cartbtnlistener = new OnClickListener(){
        public void onClick(View v) {
        	cartBtnAction();
        }
    };
    
    private void cartBtnAction(){
    	try {
			SapGenConstants.showLog("shopping cart img clicked");
			SapGenConstants.showLog("material id: "+matId);        	
			if(dbObjSCart == null)
				dbObjSCart = new ProductShoppingCartListPersistent(ProductDetailsForPhone.this);
			cartArrList = dbObjSCart.getCartIdList();     
			dbObjSCart.closeDBHelper();
			String qty = ordQty.toString().trim();
			if(matId != null && matId.length() > 0){
				if(!cartArrList.contains(matId)){
					cartArrList.add(matId);        			
					if(dbObjSCart == null)
						dbObjSCart = new ProductShoppingCartListPersistent(ProductDetailsForPhone.this);
					if(qty != null && qty.length() > 0){
						dbObjSCart.insertMId(matId, qty);
					}else{
						dbObjSCart.insertMId(matId, "");
					}
					dbObjSCart.closeDBHelper();
					SapGenConstants.showErrorDialog(ProductDetailsForPhone.this,getResources().getString(R.string.ADD_CART));
					setResult(RESULT_OK); 
					finish();					
				}else{
					SapGenConstants.showErrorDialog(ProductDetailsForPhone.this,getResources().getString(R.string.ADD_CART_ALREADY));
					setResult(RESULT_OK); 
					finish();
				}
			}
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error On cartBtnAction: "+e.toString());
    		dbObj.closeDBHelper();
		}
    }//fn cartBtnAction
    
    private void setTitleValue() {
    	try {    		
    		if(dbObj == null)
				dbObj = new DataBasePersistent(this, DBConstants.PRODUCT_UICONF_TABLE_NAME);
    		String title = dbObj.getTitle(DBConstants.DEVICE_TYPE_SMALL_DETAIL_TAG, DBConstants.COLUNM_NAME_SCR_TITLE);
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
    		dbObj.closeDBHelper();
		}
	}//fn setTitleValue
    
	public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;

        public ImageAdapter(Context c) {
            context = c;
            // ---setting the style---
             TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(
             R.styleable.Gallery1_android_galleryItemBackground, 0);
             a.recycle();
        }

        // ---returns the number of images---
        public int getCount() {
        	if(imageIDs != null && imageIDs.length > 0){
        		return imageIDs.length;
        	}else{
        		return 0;
        	}
        }

        // ---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // ---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);            
            try {
                if(imageIDs != null && imageIDs.length > 0){
                	String urlStr = imageIDs[position];
                    imageUrlStr = imageIDs[position];
        			if(imageUrlStr != null && imageUrlStr.length() > 0){   			
        				imageView.setTag(imageUrlStr);
    		            imageLoader.DisplayImage(imageUrlStr, imageView);    					   					
        			}else{
        				imageUrlStr = "";
        			}
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
                    imageView.setBackgroundResource(itemBackground);
                }
            } catch (Exception efd) {
                // TODO Auto-generated catch block
    			SapGenConstants.showErrorLog("Error in getView : "+efd.toString());
            }
            return imageView;
        }
    }
	
	private void showPagination(long index){
		try{
			int totalsize = matIdList.size();
			SapGenConstants.showLog("Total Size : "+totalsize);
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
			SapGenConstants.showErrorLog("Error in showPagination : "+sfg.toString());
		}
	}//fn showPagination
	
	private OnClickListener showPrevBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("Show Prev btn clicked");
        	selIndex = selIndex-1;
        	if(selIndex < 0)
        		selIndex = 0;
        	SapGenConstants.showLog("selIndex: "+selIndex);
        	getProdDetails(selIndex);
			showPagination(selIndex);
        }
    };
    
    private OnClickListener showNextBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("Show Next btn clicked");
        	selIndex = selIndex+1;
        	int totalsize = matIdList.size()-1;
        	if(selIndex > totalsize)
        		selIndex = totalsize;
        	SapGenConstants.showLog("selIndex: "+selIndex);
        	getProdDetails(selIndex);
			showPagination(selIndex);
        }
    };
    
    private void changeDetailScreen(String id){
    	try {
    		if(id.equalsIgnoreCase(DBConstants.DEVICE_DETAIL_TAB_SUMMARY_LABEL_TAG)){
            	SapGenConstants.showLog("summary tv clicked");
            	summaryLL.setVisibility(View.VISIBLE);
            	imagesLL.setVisibility(View.GONE);
            	detailsLL.setVisibility(View.GONE);
    			dynll.removeAllViews();
    			displayUI(DBConstants.DEVICE_TYPE_WIDE_DETAIL_SUMMARY_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, dynll);
    		}else if(id.equalsIgnoreCase(DBConstants.DEVICE_DETAIL_TAB_IMAGES_LABEL_TAG)){
				SapGenConstants.showLog("images tv clicked");
            	summaryLL.setVisibility(View.GONE);
            	imagesLL.setVisibility(View.VISIBLE);
            	detailsLL.setVisibility(View.GONE);
                if(imageIDs == null || imageIDs.length == 0){
                	SapGenConstants.showErrorDialog(ProductDetailsForPhone.this, getResources().getString(R.string.IMAGE_NOT_AVAILABLE));
                }
    			descLLimg = (LinearLayout) findViewById(R.id.descllimg);
    			if(descStr != null && descStr.length() > 0){
    				descLLimg.setVisibility(View.VISIBLE);
    				descTV = (TextView) findViewById(R.id.descValimg);
    				descTV.setText(descStr+"("+matId+")");
    			}else{
    				descLLimg.setVisibility(View.GONE);
    			}
    		}else if(id.equalsIgnoreCase(DBConstants.DEVICE_DETAIL_TAB_DETAILS_LABEL_TAG)){
				detailsLL.setVisibility(View.VISIBLE);
            	imagesLL.setVisibility(View.GONE);
            	summaryLL.setVisibility(View.GONE);
            	SapGenConstants.showLog("details tv clicked");
            	SapGenConstants.showLog("descStr: "+descStr);
    			dynDetailsll.removeAllViews();
    			displayUI(DBConstants.DEVICE_TYPE_WIDE_DETAIL_DETAILS_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, dynDetailsll);
    		}else{
				SapGenConstants.showLog("summary tv clicked");
            	summaryLL.setVisibility(View.VISIBLE);
            	imagesLL.setVisibility(View.GONE);
            	detailsLL.setVisibility(View.GONE);
    			dynll.removeAllViews();
    			displayUI(DBConstants.DEVICE_TYPE_WIDE_DETAIL_SUMMARY_TAG, DBConstants.CNTXT3_DETAIL_LISTVW_MLN_SCREEN_TAG, dynll);
    		}
		} catch (Exception ff) {
			SapGenConstants.showErrorLog("Error in changeDetailScreen : "+ff.toString());
		}
    }//fn changeDetailScreen
    
    private void displayUI(final String detSumTag, String cntxt3, LinearLayout dynll){
    	try {
    		DBConstants.DB_TABLE_NAME = DBConstants.PRODUCT_UICONF_TABLE_NAME;
			//Detail-W/S-Main value
			valArrayList = DataBasePersistent.readAllValuesOrderFromDB(this, detSumTag, cntxt3);
			labelMap = DataBasePersistent.readAllLablesFromDB(this, detSumTag, cntxt3);
			SapGenConstants.showLog("valArrayList : "+valArrayList.size());			 
			if(valArrayList != null && valArrayList.size() > 0){
				SapGenConstants.showLog("valArrayList : "+valArrayList.size());
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
			                 	String valStr = (String) stockMap.get(metaLabel);      
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
			            				 		ordQty = updatedQty;
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
			        				    dynll.addView(ll, layoutParams);
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
												SapGenConstants.showLog("ordQty  "+ordQty);
												cartBtnAction();
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
			                     	String valStr = (String) stockMap.get(metaLabel);                                 	
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
			                 				}
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
			                				 		ordQty = updatedQty;
				                    	        }
				                    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				                    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
				                    	    }); 
			            				    llmclm.addView(et);                        				    
			                     		}else if(metaValue.equalsIgnoreCase(DBConstants.VALUE_IMAGE_TAG)){
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
			            				    llmclm.addView(iv);
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
													SapGenConstants.showLog("ordQty  "+ordQty);
													cartBtnAction();
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
    
    private void getProdDetails(int selIndex){
    	try{
			if(matIdList != null && matIdList.size() > 0){
	    		String idStr = (String)matIdList.get(selIndex).toString().trim();
	    		SapGenConstants.showLog("sel idStr "+idStr);
	    		matId = idStr;
	    		initValueLayout();
			}
    	}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in getProdDetails : "+sfg.toString());
		}
    }//fn getProdDetails
    
    public void onBackPressed() {
    	setResult(RESULT_OK); 
		finish();		
	}//fn onBackPressed
}