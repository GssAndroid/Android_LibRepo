package com.globalsoft.SapLibActivity;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import com.globalsoft.SapLibActivity.Contraints.SalesOrdProIpConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActCrtConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActCustomersConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActStatusConstraints;
import com.globalsoft.SapLibActivity.Database.ActDBOperations;
import com.globalsoft.SapLibActivity.Utils.CrtGenActivityConstants;
import com.globalsoft.SapLibSoap.Constraints.SalesProActOutputConstraints;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class ActivityListforTablet extends Activity implements TextWatcher{

	private static String appNameStr = "";
	private boolean internetAccess = true;
	private static int respType = 0;
	private int sortIndex = -1;
	private boolean sortNameFlag = false,sortSOFlag = false, sortCNameFlag = false,sortDocFlag=false,sortCNAMEFlag=false,sortDateFlag=false, flag_pref = false;
	private ListView listView; 
	private SOCustomerListAdapter SOCustomerListAdapter;
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	private int dispwidth = 300 ,launchFlag=0, updateflag;
	private static ArrayList actList = new ArrayList();
	private static ArrayList allactList = new ArrayList();
	private static ArrayList cusList = new ArrayList();
	ArrayList categoryList = new ArrayList();
	ArrayList statusList = new ArrayList();
	ArrayList idList = new ArrayList();
	ArrayList galIdList = new ArrayList();
	ArrayList diogList =new ArrayList();
	ArrayList diagnosislist  =new ArrayList();
	ArrayList diagnosiscopylist  =new ArrayList();
	private int selected_index = -1;
	//private Vector selContactVect = new Vector();
	private EditText searchET;
	private String searchStr = "", mainContactId="", mainCustomerId="", linkText="";
	private String  statuslistType = "",activityListType = "",  doclistType = "" ,intrctnType ="";
	private int statuslistCount = 0,activityListCount = 0, doclistcount = 0, intrctcount =0;
	private ImageView addimg;
	
	private ImageView[] priorityIcon;
	private ImageView[] statusIcon;
	private ImageView[] errstatusIcon;
	private TextView[] custLocTxtView;
	private TableLayout colTableLayout = null;
	final Handler handlerForLayout = new Handler();
	
	private TextView tableHeaderTV1, tableHeaderTV2, tableHeaderTV3, tableHeaderTV4, tableHeaderTV5, tableHeaderTV6, tableHeaderTV7;
	private final int sortHeader1= 1, sortHeader2= 2,sortHeader4= 4 ,sortHeader5= 5;
    private int headerWidth1, headerWidth2, headerWidth3, headerWidth4, headerWidth5, headerWidth6, headerWidth7;
	private SoapObject resultSoap = null;
	private ProgressDialog pdialog = null;	
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	
	private static final int MENU_CRE_ACT = Menu.FIRST;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.activitymaintbl); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.ACTSCR_ACT_LIST_TITLE));			
			
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			SharedPreferences settings = getSharedPreferences(SapGenConstants.PREFS_NAME_FOR_ACTIVITY, 0);      
			flag_pref = settings.getBoolean(SapGenConstants.PREFS_KEY_ACTIVITY_FOR_MYSELF_GET, false);		
					
			//SapGenConstants.SapUrlConstants(this);//parsing url.xml file						
			appNameStr = this.getIntent().getStringExtra("app_name");
			if((appNameStr == null) || (appNameStr.equalsIgnoreCase("")))
            	appNameStr = SapGenConstants.APPLN_NAME_STR_MOBILEPRO;
			CrtGenActivityConstants.CRTACT_CALLING_APP_NAME = appNameStr;
			
			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			
			try{
				mainContactId = this.getIntent().getStringExtra("contactId");
				mainCustomerId = this.getIntent().getStringExtra("customerId");
				if(mainCustomerId != null)
					mainCustomerId = mainCustomerId.trim();				
				if(mainContactId != null)
					mainContactId = mainContactId.trim();				
				SapGenConstants.showLog("mainContactId: "+mainContactId);
				SapGenConstants.showLog("mainCustomerId: "+mainCustomerId);
			}catch (Exception de) {
				SapGenConstants.showLog("Error in mainCustomerId:"+de.toString());			
	        }
			searchET = (EditText)findViewById(R.id.searchBEF);
			searchET.setText(searchStr);
			searchET.addTextChangedListener(this);
			
			addimg = (ImageView) findViewById(R.id.addimg);
			addimg.setOnClickListener(addimglistener);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
			internetAccess = SapGenConstants.checkConnectivityAvailable(ActivityListforTablet.this);
			System.out.println("internetAccess:"+internetAccess);
			dispwidth = SapGenConstants.getDisplayWidth(this);
			
			Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{
        				layTaskListTableHeader();
        			} catch (Exception e) {
        				SapGenConstants.showErrorLog("Error in oncreate layTaskListTableHeader:"+e.toString());
        			}
        			handlerForLayout.post(displayData);
				}
			};
	        t.start();	
			
		}
		catch (Exception de) {
			SapGenConstants.showLog("Error in oncreate contactMain:"+de.toString());			
        }
	}
	
	private OnClickListener addimglistener = new OnClickListener(){
		public void onClick(View v) {
			launchCreateActivityScreen();
        }
    };
    
	final Runnable displayData = new Runnable(){
	    public void run()
	    {
	    	try{
	    		
	    		if(internetAccess){
								
	    			if(internetAccess){													
						/* DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");	                        				   
						   Calendar cal = Calendar.getInstance();	                        				 
						   SapGenConstants.showLog(" current date : "+dateFormat.format(cal.getTime()));
						   dateStr = dateFormat.format(cal.getTime());
						   strtStr = "+"+"START PROCESSING DEVICE"+dateStr+"\n"+"EVENT:SERVICE-DOX-CONTEXT-DATA-GET"+"\n"+"API-BEGIN-TIME DEVICE"+dateStr;
						   diogList.add(strtStr); */
	    				initStatusSoapConnection();	
	    				/*if(SapGenConstants.ACTIVITY_FLAG ==1){
	    					initSoapConnection();
	    				}else{
	    					initStatusSoapConnection();	
	    				}*/
				    											
					}
				}
				else{
					getLDBActivityList();
				}			    		
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
    
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub	
	}
	
	public void afterTextChanged(Editable s) { 
		searchItemCall(s.toString());
	} 
	
	private void searchItemCall(final String searchStr){
		/*if(pdialog != null)
			pdialog = null;		
		
		if(pdialog == null){
			pdialog = ProgressDialog.show(ActivityListforTablet.this, "", getString(R.string.ACTSCR_WAIT_TEXTS),true);*/
			/*Thread t = new Thread() 
			{
	            public void run() 
				{
        			try{*/
        				searchItemsAction(searchStr);
        			/*} catch (Exception e) {
        				SapGenConstants.showErrorLog("Error in searchItemCall Thread:"+e.toString());
        			}
        			ntwrkHandler.post(searchCall);
        			//stopProgressDialog();
				}
			};
	        t.start();	*/
		//}
	}//fn searchItemCall
	
	/*final Runnable searchCall = new Runnable(){
	    public void run()
	    {
	    	try{				
				drawSubLayout();
	    	} catch(Exception sfe){
	    		SapGenConstants.showErrorLog("Error in contactsCall:"+sfe.toString());
	    	}
	    }	    
    };	*/
	
    
    private void layTaskListTableHeader(){
		try{
			tableHeaderTV1 = (TextView)findViewById(R.id.TableHeaderTV1);
			tableHeaderTV1.setGravity(Gravity.LEFT);
			tableHeaderTV1.setPadding(10,5,5,5);
			
			tableHeaderTV2 = (TextView)findViewById(R.id.TableHeaderTV2);
			tableHeaderTV2.setGravity(Gravity.LEFT);
			tableHeaderTV2.setPadding(10,5,5,5);
			tableHeaderTV2.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader2);
				}
			});
			SpannableString underlinedStr = SapGenConstants.getUnderlinedString(tableHeaderTV2.getText().toString());
		    tableHeaderTV2.setText(underlinedStr);
		    
			tableHeaderTV3 = (TextView)findViewById(R.id.TableHeaderTV3);
			//tableHeaderTV3.setGravity(Gravity.CENTER);
			tableHeaderTV3.setGravity(Gravity.LEFT);
			tableHeaderTV3.setPadding(10,5,5,5);
			tableHeaderTV3.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					//sortItemsAction(SapGenConstants.TASK_SORT_ETA);
				}
			});
			
			tableHeaderTV4 = (TextView)findViewById(R.id.TableHeaderTV4);
			tableHeaderTV4.setGravity(Gravity.LEFT);
			tableHeaderTV4.setPadding(10,5,5,5);
			tableHeaderTV4.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader4);
				}
			});
			SpannableString underlinedStr1 = SapGenConstants.getUnderlinedString(tableHeaderTV4.getText().toString());
			tableHeaderTV4.setText(underlinedStr1);
			
			tableHeaderTV5 = (TextView)findViewById(R.id.TableHeaderTV5);
			tableHeaderTV5.setGravity(Gravity.LEFT);
			tableHeaderTV5.setPadding(10,5,5,5);
			tableHeaderTV5.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader5);
				}
			});
			
			SpannableString underlinedStr2 = SapGenConstants.getUnderlinedString(tableHeaderTV5.getText().toString());
			tableHeaderTV5.setText(underlinedStr2);
		    
			/*tableHeaderTV6 = (TextView)findViewById(R.id.TableHeaderTV6);
			tableHeaderTV6.setGravity(Gravity.LEFT);
			tableHeaderTV6.setPadding(10,5,5,5);
			
			tableHeaderTV6.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					//sortItemsAction(SapGenConstants.TASK_SORT_PRODUCT);
				}
			});*/
			
			tableHeaderTV7 = (TextView)findViewById(R.id.TableHeaderTV7);
			tableHeaderTV7.setGravity(Gravity.LEFT);
			tableHeaderTV7.setPadding(10,5,5,5);
			tableHeaderTV7.setOnClickListener(new View.OnClickListener() {	
				public void onClick(View v) {
					sortItemsAction(sortHeader1);
				}
			});
					
			SpannableString underlinedStr3 = SapGenConstants.getUnderlinedString(tableHeaderTV7.getText().toString());
			tableHeaderTV7.setText(underlinedStr3);
		    
			ViewTreeObserver vto1 = tableHeaderTV1.getViewTreeObserver();
	        vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV1.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth1 = tableHeaderTV1.getWidth();
	                SapGenConstants.showLog("tableHeaderTV1 Width1 : "+headerWidth1+" : "+tableHeaderTV1.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto2 = tableHeaderTV2.getViewTreeObserver();
	        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV2.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth2 = tableHeaderTV2.getWidth();
	                SapGenConstants.showLog("tableHeaderTV2 Width1 : "+headerWidth2+" : "+tableHeaderTV2.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto3 = tableHeaderTV3.getViewTreeObserver();
	        vto3.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV3.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth3 = tableHeaderTV3.getWidth();
	                SapGenConstants.showLog("tableHeaderTV3 Width1 : "+headerWidth3+" : "+tableHeaderTV3.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto4 = tableHeaderTV4.getViewTreeObserver();
	        vto4.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV4.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth4 = tableHeaderTV4.getWidth();
	                SapGenConstants.showLog("tableHeaderTV4 Width1 : "+headerWidth4+" : "+tableHeaderTV4.getMeasuredWidth());
	            }
	        });
	        
	        ViewTreeObserver vto5 = tableHeaderTV5.getViewTreeObserver();
	        vto5.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV5.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth5 = tableHeaderTV5.getWidth();
	                SapGenConstants.showLog("tableHeaderTV5 Width1 : "+headerWidth5+" : "+tableHeaderTV5.getMeasuredWidth());
	            }
	        });
	        
	        /*ViewTreeObserver vto6 = tableHeaderTV6.getViewTreeObserver();
	        vto6.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV6.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth6 = tableHeaderTV6.getWidth();
	                SapGenConstants.showLog("tableHeaderTV6 Width1 : "+headerWidth6+" : "+tableHeaderTV6.getMeasuredWidth());
	            }
	        });*/
	        
	        ViewTreeObserver vto7 = tableHeaderTV7.getViewTreeObserver();
	        vto7.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            public void onGlobalLayout() {
	                ViewTreeObserver obs = tableHeaderTV7.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                headerWidth7 = tableHeaderTV7.getWidth();
	                SapGenConstants.showLog("tableHeaderTV7 Width1 : "+headerWidth7+" : "+tableHeaderTV7.getMeasuredWidth());	                		
	            }
	        });
	        
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in layTaskListTableHeader : "+sfg.toString());
		}
	}//fn layTaskListTableHeader
    
    private void sortItemsAction(int sortInd){
		try{
			sortIndex = sortInd;
			
			 switch(sortInd){
			 	case sortHeader1:
			 		sortDateFlag = !sortDateFlag;
			 		break;
			 	case sortHeader2:
			 		sortCNameFlag = !sortCNameFlag;
			 		break;
			 	case sortHeader4:
			 		sortDocFlag = !sortDocFlag;
			 		break;		
			 	case sortHeader5:
			 		sortNameFlag = !sortNameFlag;
			 		break;	
			 }
			 
			/*if(sortInd == SapGenConstants.TASK_SORT_NAME)
				sortNameFlag = !sortNameFlag;
			else if(sortInd == SapGenConstants.TASK_SORT_DO)
				 sortDocFlag = !sortDocFlag;						
			else if(sortInd == SapGenConstants.TASK_SORT_CNAME)
				sortCNAMEFlag = !sortCNAMEFlag;*/
						
			 SapGenConstants.showLog("Selected Sort Index : "+sortInd);			
			Collections.sort(actList, reportSortComparator);
			SapGenConstants.showLog("taskList:"+actList.size()); 
			//getListView().invalidateViews();
			drawSubLayout();
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in sortItemsAction : "+sfg.toString());
		}
	}//fn sortItemsAction
    
private final Comparator reportSortComparator =  new Comparator() {
    	
        public int compare(Object o1, Object o2){ 
        	int comp = 0;
            String strObj1 = "0", strObj2="0";
            int intObj1 = 0, intObj2 = 0;
            SalesProActOutputConstraints spOCObj1, spOCObj2;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                    spOCObj1 = (SalesProActOutputConstraints)o1;
                    spOCObj2 = (SalesProActOutputConstraints)o2;                                        
                    //SapGenConstants.showLog("Comparator called for "+sortIndex);                    
                    
                     if(sortIndex == sortHeader1){
                    	 if(spOCObj1 != null){
                     		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                             Date dateObj = curFormater.parse(spOCObj1.getDateFrom().trim());
                         	Long dateLongValue = dateObj.getTime();
                         	strObj1 = String.valueOf(dateLongValue);
                         }                        
                         if(spOCObj2 != null){
                         	SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                             Date dateObj = curFormater.parse(spOCObj2.getDateFrom().trim());
                         	Long dateLongValue = dateObj.getTime();
                         	strObj2 = String.valueOf(dateLongValue);
                         }                        
                         if(sortDateFlag == true)
                             comp =  strObj1.compareTo(strObj2);
                         else
                             comp =  strObj2.compareTo(strObj1); 
                    	 //
                      
                    }
                     else if(sortIndex ==  sortHeader2){                    	
                    	  if(spOCObj1 != null){
                              strObj1 = spOCObj1.getParnrName();
                      	}                        
                          if(spOCObj2 != null){                            
                      		strObj2 = spOCObj2.getParnrName();
                          }                        
                          if(sortCNameFlag == true)
                              comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                          else
                              comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());                     
                    }             
                    else if(sortIndex == sortHeader4){
                        if(spOCObj1 != null)
                            strObj1 = spOCObj1.getObjectId().trim();                        
                        if(spOCObj2 != null)
                            strObj2 = spOCObj2.getObjectId().trim();                        
                        if(sortDocFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }     
                    else if(sortIndex == sortHeader5){
                        if(spOCObj1 != null)
                            strObj1 = spOCObj1.getKunnrName().trim();                        
                        if(spOCObj2 != null)
                            strObj2 = spOCObj2.getKunnrName().trim();                        
                        if(sortNameFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }                                          
        			//sortSOFlag = false, sortCNameFlag = false, sortProductFlag = false, sortETAFlag
                }
             }
             catch(Exception qw){
            	 SapGenConstants.showErrorLog("Error in Report Sort Comparator : "+qw.toString());
             }                 
             if (comp != 0) {            
                return comp;
             } 
             else {            
                return 0;
             }
        }
    };
    
   /* private final Comparator reportSortComparator =  new Comparator() {
    	
        public int compare(Object o1, Object o2){ 
        	int comp = 0;
            String strObj1 = "0", strObj2="0";
            int intObj1 = 0, intObj2 = 0;
            SalesProActOutputConstraints spOCObj1, spOCObj2;
            try{            	
                if (o1 == null || o2 == null){
                }
                else{            
                    spOCObj1 = (SalesProActOutputConstraints)o1;
                    spOCObj2 = (SalesProActOutputConstraints)o2;                                        
                    //SapGenConstants.showLog("Comparator called for "+sortIndex);                    
                    
                     if(sortIndex == SapGenConstants.TASK_SORT_NAME){                    	
                        if(spOCObj1 != null){
                            strObj1 = spOCObj1.getParnrName();
                    	}                        
                        if(spOCObj2 != null){                            
                    		strObj2 = spOCObj2.getParnrName();
                        }                        
                        if(sortNameFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }
                     else if(sortIndex == SapGenConstants.TASK_SORT_DATE){                    	
                    	if(spOCObj1 != null){
                    		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateObj = curFormater.parse(spOCObj1.getDateFrom().trim());
                        	Long dateLongValue = dateObj.getTime();
                        	strObj1 = String.valueOf(dateLongValue);
                        }                        
                        if(spOCObj2 != null){
                        	SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateObj = curFormater.parse(spOCObj2.getDateFrom().trim());
                        	Long dateLongValue = dateObj.getTime();
                        	strObj2 = String.valueOf(dateLongValue);
                        }                        
                        if(sortDateFlag == true)
                            comp =  strObj1.compareTo(strObj2);
                        else
                            comp =  strObj2.compareTo(strObj1);                        
                    }             
                    else if(sortIndex == SapGenConstants.TASK_SORT_DO){
                        if(spOCObj1 != null)
                            strObj1 = spOCObj1.getObjectId().trim();                        
                        if(spOCObj2 != null)
                            strObj2 = spOCObj2.getObjectId().trim();                        
                        if(sortSOFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }     
                    else if(sortIndex == SapGenConstants.TASK_SORT_CNAME){
                        if(spOCObj1 != null)
                            strObj1 = spOCObj1.getKunnrName().trim();                        
                        if(spOCObj2 != null)
                            strObj2 = spOCObj2.getKunnrName().trim();                        
                        if(sortCNameFlag == true)
                            comp =  strObj1.toLowerCase().compareTo(strObj2.toLowerCase());
                        else
                            comp =  strObj2.toLowerCase().compareTo(strObj1.toLowerCase());
                    }                                          
        			//sortSOFlag = false, sortCNameFlag = false, sortProductFlag = false, sortETAFlag
                }
             }
             catch(Exception qw){
            	 SapGenConstants.showErrorLog("Error in Report Sort Comparator : "+qw.toString());
             }                 
             if (comp != 0) {            
                return comp;
             } 
             else {            
                return 0;
             }
        }
    };*/
    
    private void drawSubLayout(){
		try{	
			colTableLayout = (TableLayout)findViewById(R.id.taskmaintbllayout3);
			if(colTableLayout != null)
				colTableLayout.removeAllViews();
			
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	
			
			LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
			linparams.leftMargin = 10; 
			linparams.rightMargin = 10; 
			linparams.gravity = Gravity.LEFT; //CENTER_VERTICAL;	
			
			if(actList != null){
				SalesProActOutputConstraints category = null;
				String statusStr = "", priorityStr = "", custLocStr = "";
                String serOrdStr = "", contNameStr = "", productDescStr = "", serviceDescStr = "";
                String estDateStr = "", batchStr = "", syncStr = "", startDatStr = "";
                
                int rowSize = actList.size();                
                SapGenConstants.showLog("Stocks List Size  : "+rowSize);
                
                //priorityIcon = new ImageView[rowSize];
                statusIcon = new ImageView[rowSize];
                errstatusIcon = new ImageView[rowSize];
                custLocTxtView = new TextView[rowSize];
                
				for (int i =0; i < actList.size(); i++) {
					category = (SalesProActOutputConstraints)actList.get(i);
                    if(category != null){
                    	custLocStr = "";                    	
                    	try {
							
							statusStr = category.getStatus().trim();
							serOrdStr = category.getObjectId().trim();  
							//productStr = category.getRefObjProductId().toString().trim();
							serviceDescStr = category.getDescription().toString().trim();
							//productDescStr = category..toString().trim();
							
							if(category.getKunnr().toString().trim().length() != 0){
								contNameStr = category.getParnrName();
							}
							
							if(category.getKunnrName().toString().length() > 0){
								custLocStr += category.getKunnrName().toString();
		                		//SapGenConstants.showLog("getNameOrg1 : "+category.getNameOrg1().toString());
		                	}
		                	
		                	/*if(category.getParnrName().toString().length() > 0){
		                		custLocStr += "\n"+category.getParnrName().toString();
		                		//SapGenConstants.showLog("getStreet : "+category.getStreet().toString());
		                	}*/
		                	
							
							startDatStr = category.getDateFrom().trim();
							if(!startDatStr.equalsIgnoreCase("")){
								String[] date_value = startDatStr.split("-");
								startDatStr = SapGenConstants.getMonthValue(Integer.parseInt(date_value[1]))+" "+date_value[2];
							}
							
							estDateStr = category.getTimeFrom()+" "+category.getTimeTo(); 
						}
                    	catch (Exception e1) {
                    		SapGenConstants.showErrorLog("On drawSubLayout Assignment : "+e1.toString());
						}           
                    	
                        tr = new TableRow(this);
                        
                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_linear, null);
                        linearLayout.setPadding(0, 10, 0, 10);
                        linearLayout.setGravity(Gravity.LEFT);
                        linearLayout.setOrientation(LinearLayout.VERTICAL); 
                        
                        LinearLayout linearLayout1 = new LinearLayout(this);
                        linearLayout1.setOrientation(LinearLayout.HORIZONTAL); 
                        linearLayout1.setLayoutParams(linparams);
                        linearLayout1.setGravity(Gravity.LEFT);
                        
                       
                        statusIcon[i] = new ImageView(this); 
                        statusIcon[i].setLayoutParams(linparams);   
                        String status = category.getStatus();            		
                        SalesProActStatusConstraints statusObj = getStatusDetails(statusStr);
                		if(statusObj != null){
                			String imageName = statusObj.getStatusIcon().toString().toLowerCase().trim();
                			if(imageName != null){
                				int resID = getResources().getIdentifier(imageName , "drawable", getPackageName());
                				statusIcon[i].setImageResource(resID);
                			}else{
                				//statusIcon[i].setImageResource(R.drawable.light_grey);
                				statusIcon[i].setVisibility(View.GONE);
                			}            			
                		}else{
                			//statusIcon[i].setImageResource(R.drawable.light_grey);
                			statusIcon[i].setVisibility(View.GONE);
                		}
                        
                        /*if(statusStr.equalsIgnoreCase(SapGenConstants.TASK_STATUS_READY_STR_FOR_SAP)){
                			statusIcon[i].setImageResource(R.drawable.t1_grey);
        				}
        				else if(statusStr.equalsIgnoreCase(SapGenConstants.TASK_STATUS_ACCEPTED_STR_FOR_SAP)){
        					statusIcon[i].setImageResource(R.drawable.tl_green);
        				}
        				else if(statusStr.equalsIgnoreCase(SapGenConstants.TASK_STATUS_DEFERRED_STR_FOR_SAP)){
        					statusIcon[i].setImageResource(R.drawable.tl_yellow);
        				}	
        				else if(statusStr.equalsIgnoreCase(SapGenConstants.TASK_STATUS_DECLINED_STR_FOR_SAP)){
        					statusIcon[i].setImageResource(R.drawable.tl_red);
        				}
        				else if(statusStr.equalsIgnoreCase(SapGenConstants.TASK_STATUS_COMPLETED_STR_FOR_SAP)){
        					statusIcon[i].setImageResource(R.drawable.tl_blue);
        				}*/
                        
                        linearLayout1.addView(statusIcon[i]);
                		linearLayout.addView(linearLayout1);
                		
                        LinearLayout linearLayout2 = new LinearLayout(this);
                        linearLayout2.setOrientation(LinearLayout.HORIZONTAL); 
                        linearLayout2.setLayoutParams(linparams); 
                        linearLayout2.setPadding(20, 5, 0, 0);
                        linearLayout2.setGravity(Gravity.CENTER);
                        
                        errstatusIcon[i] = new ImageView(this); 
                        errstatusIcon[i].setLayoutParams(linparams);
                        //boolean isExits = SapGenConstants.errorTaskIdForStatus.contains(category.getObjectId().toString().trim());
                        //errstatusIcon[i].setImageResource(R.drawable.notify);
                        //SapGenConstants.showLog("Error isExits : "+isExits);
                        boolean isExits = false;
                		linearLayout2.addView(errstatusIcon[i]);                		
                		linearLayout.addView(linearLayout2);
                		if(isExits){
                			errstatusIcon[i].setVisibility(View.VISIBLE);
                			linearLayout2.setVisibility(View.VISIBLE);
                		}
                		else{
                			errstatusIcon[i].setVisibility(View.GONE);
                			linearLayout2.setVisibility(View.GONE);
                		}
                		
                        custLocTxtView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					custLocTxtView[i].setText(custLocStr);
    					custLocTxtView[i].setId(i);
    					custLocTxtView[i].setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								int id = view.getId();	
								showEditActScreen(id);
							}	
                        });
    					custLocTxtView[i].setWidth(headerWidth2);
    					custLocTxtView[i].setGravity(Gravity.LEFT);
    					custLocTxtView[i].setPadding(30,0,0,10);
    					
    					
    					TextView strDateTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					strDateTxtView.setText(startDatStr);
    					strDateTxtView.setWidth(headerWidth7);
    					strDateTxtView.setGravity(Gravity.LEFT);
    					strDateTxtView.setPadding(30,10,30,0);
    					
    					TextView estArrivalTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					/*try {
							if((!estDateStr.equalsIgnoreCase("")) && (!estDateStr.startsWith("00"))){
								String[] timeArr = estDateStr.split(" ");
								//SapGenConstants.showLog("Time : "+timeArr.length+" : "+timeArr[0]+" : "+timeArr[1]);
								estDateStr = SapGenConstants.getSystemDateFormatString(this, timeArr[0])+" "+SapGenConstants.getSecondsRemovedTimeStr(timeArr[1]);
							}
							else
								estDateStr = "";
						} catch (Exception e) {
							e.printStackTrace();
						}*/
    					estDateStr = "";
    					estArrivalTxtView.setText(serviceDescStr);
    					estArrivalTxtView.setWidth(headerWidth3);
    					estArrivalTxtView.setGravity(Gravity.LEFT);
    					estArrivalTxtView.setPadding(30,10,0,10);
    					
    					TextView serOrdTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					serOrdTxtView.setText(serOrdStr);
    					serOrdTxtView.setWidth(headerWidth4);
    					serOrdTxtView.setGravity(Gravity.LEFT);
    					serOrdTxtView.setPadding(60,0,0,0);
    					serOrdTxtView.setId(i);
    					serOrdTxtView.setOnClickListener(new View.OnClickListener() {	
    						public void onClick(View v) {
    							showEditActScreen(v.getId());
    						}
    					});
    					
    					TextView contNameTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);
    					contNameTxtView.setText(contNameStr);
    					contNameTxtView.setWidth(headerWidth5);
    					contNameTxtView.setGravity(Gravity.LEFT);
    					contNameTxtView.setPadding(40,10,0,20);
    					
    					
    					TextView prdTxtView = (TextView) getLayoutInflater().inflate(R.layout.template_tv, null);					
    					prdTxtView.setText(productDescStr);
    					prdTxtView.setWidth(headerWidth6);
    					prdTxtView.setGravity(Gravity.LEFT);
    					prdTxtView.setPadding(30,0,0,0);
    					
    					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
    						strDateTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						custLocTxtView[i].setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						estArrivalTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						serOrdTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						contNameTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    						prdTxtView.setTextSize(SapGenConstants.TEXT_SIZE_TABLE_ROW);
    					}
    					
    					tr.addView(linearLayout);
    					tr.addView(strDateTxtView);
    					tr.addView(custLocTxtView[i]);
    					tr.addView(estArrivalTxtView);
    					tr.addView(serOrdTxtView);
    					tr.addView(contNameTxtView);    					
    					//tr.addView(prdTxtView);
    					
    					if(i%2 == 0)
    						tr.setBackgroundResource(R.color.item_even_color);
			            else
			            	tr.setBackgroundResource(R.color.item_odd_color);
    					colTableLayout.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                    }					
				}
			}
		}
		catch(Exception asgf){
			SapGenConstants.showErrorLog("On drawSubLayout : "+asgf.toString());
		}
	}//fn drawSubLayout
    
    public SalesProActStatusConstraints getStatusDetails(String status){
    	SalesProActStatusConstraints statusObj = null;
		try {	
			ArrayList statusListArray = ActDBOperations.readAllStatusDataFromDB(this);
			if(statusListArray.size() != 0){		
				for(int i = 0; i < statusListArray.size(); i++){
					statusObj = ((SalesProActStatusConstraints)statusListArray.get(i));
			        if(statusObj != null){
			        	String statusVal = statusObj.getStatus().toString().trim();
			        	if(statusVal.equalsIgnoreCase(status)){
			        		return statusObj;
			        	}else{
			        		statusObj = null;
			        	}
			        }
			    }
			}else{
				SapGenConstants.showLog("No List for status!");
        		return statusObj;
			}  
		} catch (Exception e) {
			SapGenConstants.showErrorLog("Error in getStatusDetails : "+e.toString());
    		return statusObj;
		}
		return statusObj;
	}//fn getStatusDetails
    
	private void searchItemsAction(String  match){  
        try{       
            searchStr = match;
            String mattStr = "";
            String strValue = null;
            SalesProActOutputConstraints category = null;
            if((allactList != null) && (allactList.size() > 0)){
                if((!match.equalsIgnoreCase("")) && (match.length() >= 1)){             
                    actList.clear();
                    for(int i = 0; i < allactList.size(); i++){ 
                    	strValue = null;
                        mattStr = "";
                        category = null;
    					category = (SalesProActOutputConstraints)allactList.get(i);
                        String data = "";
	            		if(category.getParnrName().toString().length() > 0){
	                		data += category.getParnrName().toString();
	                	}
	                	if(category.getKunnrName().toString().length() > 0){
	                		data += ", "+category.getKunnrName().toString();
	                	}	                	
	                	strValue = data;
                        if(strValue != null){
                        	if(category != null){
	                            mattStr = strValue.trim().toLowerCase();
	                            match = match.toLowerCase();
	                            if((mattStr.indexOf(match) >= 0)){
	                            	actList.add(category);
	                            }
	                            else{
                                	data = "";
                                	if(category.getObjectId().toString().length() > 0){
                                		data += category.getObjectId().toString();
                                	}  
                                	strValue = data;
                                	mattStr = strValue.trim().toLowerCase();
                                    match = match.toLowerCase();
                                    if((mattStr.indexOf(match) >= 0)){
                                    	actList.add(category);
                                    }
	                            }
                        	}
                        }
                    }//for 
                    drawSubLayout();
                }
                else{
                    actList.clear();
                    for(int i = 0; i < allactList.size(); i++){  
                    	category = null;
    					category = (SalesProActOutputConstraints)allactList.get(i);                        
                        if(category != null){
                        	actList.add(category);
                        }
                    }
                    drawSubLayout();
                }
            }//if
            else
                return;
        }//try
        catch(Exception we){
            we.printStackTrace();
        }
    }//fn searchItemsAction  
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CRE_ACT, 0, getResources().getString(R.string.ACTSCR_CRE_ACT));
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_CRE_ACT: {
				launchCreateActivityScreen();
		        break;
			}
	    }
		return super.onOptionsItemSelected(item);
	}

	
	private void showEditActScreen(int index){
		try {
			selected_index = index;
			SalesProActOutputConstraints category = null;
			SalesProActCustomersConstraints customers = null;
			String objectId = "";
			if(SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			if(actList != null){
				if(actList.size() > index){
					category = null;
					category = (SalesProActOutputConstraints)actList.get(index);
                    if(category != null){
                    	objectId = category.getObjectId().toString().trim();
                    	if(objectId.length() > 0){
                    		for(int i=0; i<cusList.size(); i++){
                    			customers = (SalesProActCustomersConstraints)cusList.get(i);  
                    			String cusObjId = customers.getObjectId().toString().trim();
                    			if(objectId.equalsIgnoreCase(cusObjId)){
                    				String cusid = customers.getKunnr();
                					String sapid = customers.getParnr();
                					String fname = "", lname ="";
                					String contactName = customers.getKunnrName();
                					int index1 = contactName.indexOf(" ");
                					if(index1 != -1){
                						fname = contactName.substring(0, index1);
                						lname = contactName.substring(index1+1, contactName.length());
                					}else{
                						fname = contactName;
                						lname = "";
                					}
                					String orgname = customers.getParnrName();
                    				ContactSAPDetails obj = new ContactSAPDetails("", "", sapid, cusid, fname, lname, orgname);
                    				if(!SapGenConstants.selContactIdArr.contains(sapid)){
                        				SapGenConstants.selContactVect.addElement(obj);
                        				SapGenConstants.selContactIdArr.add(sapid);
                					}   
                    			}                    			
                    		}
                    	}                    	                    	
                    	sentToEditActScreen(category);
                    }
				}
			}
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog(e.getMessage());
		}
	}//fn showEditActScreen
	
	private void launchCreateActivityScreen(){
		try {
			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
				SapGenConstants.selContactVect.removeAllElements();
				SapGenConstants.selContactVect.clear();
  			}
			if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
				SapGenConstants.selContactIdArr.clear();
			}
			Intent intent = new Intent(this, CrtGenActivity.class); 
        	intent.putExtra("editflag", false); 	
			intent.putExtra("contactId", mainContactId);
			intent.putExtra("customerId", mainCustomerId);
			startActivityForResult(intent, CrtGenActivityConstants.ACT_CRE_SCREEN);
		}
		catch (Exception e) {
			SapGenConstants.showErrorLog("Error in launchCreateActivityScreen:"+e.getMessage());
		}
	}//fn launchCreateActivityScreen	
		
	private void sentToEditActScreen(SalesProActOutputConstraints category){
		try {
        	Intent intent = new Intent(this, CrtGenActivity.class); 
        	intent.putExtra("editflag", true);   
        	intent.putExtra("actobj", category);    		
			intent.putExtra("contactId", mainContactId);
			intent.putExtra("customerId", mainCustomerId);
        	//ContactProVectSerializable vectObj1 = new ContactProVectSerializable(selContactVect);
    		//intent.putExtra("selContacts", vectObj1);
			startActivityForResult(intent, CrtGenActivityConstants.ACT_EDIT_SCREEN);
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog(e.getMessage());
		}
	}//fn showEditActScreen
	
	private void initStatusSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
        	String saptimeStr = getDateTime();
        	String sapcntxttimeStr = "+"+"START PROCESSING DEVICE"+saptimeStr+"\n"+"EVENT:ACTIVITY-CONTEXT-DATA-GET"+"\n"+"API-BEGIN-TIME DEVICE"+saptimeStr;
 		   	diogList.add(sapcntxttimeStr);	 		   	
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[3];
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }
             
            if(SapGenConstants.DIOG_FLAG==1){
            	 C0[0].Cdata = SapGenConstants.getApplicationIdentityParameterDiagnosis(this, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
            }else{
            	 C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
            }
            	
           
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            if(!flag_pref){
            	C0[2].Cdata = "EVENT[.]ACTIVITY-CONTEXT-DATA-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            }else{
            	C0[2].Cdata = "EVENT[.]ACTIVITY-CONTEXT-DATA-GET[.]VERSION[.]0";
            }
            //C0[2].Cdata = "EVENT[.]ACTIVITY-CONTEXT-DATA-GET[.]VERSION[.]0";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog("Request:"+request.toString());
            
            respType = SapGenConstants.RESP_TYPE_GET_STATUS;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initStatusSoapConnection : "+asd.toString());
        }
    }//fn initStatusSoapConnection
	
	
	private void initSoapConnection(){        
        SoapSerializationEnvelope envelopeC = null;
        try{
        	String saptimeStr = getDateTime();
        	String soapbeginStr = "+"+"API-FOR-EVENT:ACTIVITIES-FOR-EMPLOYEE-GET"+"\n"+"API-BEGIN-TIME DEVICE"+saptimeStr;
 		   	diogList.add(soapbeginStr);	
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[4];           
            
            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }
                        
            if(SapGenConstants.DIOG_FLAG==1){
           	 C0[0].Cdata = SapGenConstants.getApplicationIdentityParameterDiagnosis(this, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
           }else{
           	 C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
           }
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            if(mainCustomerId != null && mainCustomerId.length() > 0){
            	C0[2].Cdata = "EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
            	C0[3].Cdata = "ZGSXCAST_CNTCTKEY[.]"+mainCustomerId+"[.]";
            }else{
                if(!flag_pref){
                	C0[2].Cdata = "EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";  
                	
                }else{
                	C0[2].Cdata = "EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0";                	
                }
                C0[3].Cdata = "ZGSXCAST_CNTCTKEY[.][.][.]";
            }
            
            //C0[2].Cdata = "EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0";
        
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                    
            SapGenConstants.showLog("Request:"+request.toString());
          
            respType = SapGenConstants.RESP_TYPE_GET_TASK;
            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);           
        }
        catch(Exception asd){
            SapGenConstants.showErrorLog("Error in initSoapConnection : "+asd.toString());
        }
    }//fn initSoapConnection	

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
       
    final Runnable handlerFnName = new Runnable(){
	    public void run(){
	    	try{
	    		initSoapConnection();
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in handlerFnName : "+asegg.toString());
	    	}
	    }	    
    };
    
    /*final Runnable getNetworkResponseRunnable = new Runnable(){
	    public void run(){
	    	try{
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
		    	if(resultSoap != null){
	    			if(pdialog != null)
	        			pdialog = null;
	    			if(pdialog == null){
	    				pdialog = ProgressDialog.show(ActivityListforTablet.this, "", getString(R.string.ACTSCR_WAIT_TEXTS),true);
		            	Thread t = new Thread() 
		    			{
		    	            public void run() 
		    				{
		            			try{
		            				updateReportsConfirmResponse(resultSoap);
		            			} catch (Exception e) {
		            				SapGenConstants.showErrorLog("Error in updateReportsConfirmResponse Thread:"+e.toString());
		            			}
		            			ntwrkHandler.post(reloadListView);
		    				}
		    			};
		    	        t.start();	
	    			}
		    	}else{
		    		getLDBActivityList(); 
		    	}
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
	    	}
	    }	    
    };*/
    
    final Runnable getNetworkResponseRunnable = new Runnable(){
	    public void run(){
	    	try{
	    		SapGenConstants.showLog("Soap Env value : "+resultSoap);
	    		if(respType == SapGenConstants.RESP_TYPE_GET_STATUS){
	    			if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				ActivityListforTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ActivityListforTablet.this, "", getString(R.string.ACTSCR_WAIT_TEXTS_AFTER_RESULT),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                    	    			updateStatusConfirmResponseForRefresh(resultSoap);	                        				
	                        				sleep(2000);
	                        			} catch (Exception e) {  }
	                        			
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
	    		}
		    	else if(respType == SapGenConstants.RESP_TYPE_GET_TASK){
		    		if(pdialog != null)
	        			pdialog = null;
	        		
	    			if(pdialog == null){
	    				ActivityListforTablet.this.runOnUiThread(new Runnable() {
	                        public void run() {
	                        	pdialog = ProgressDialog.show(ActivityListforTablet.this, "", getString(R.string.ACTSCR_WAIT_TEXTS_AFTER_RESULT),true);
	                        	new Thread() {
	                        		public void run() {
	                        			try{                        				
	                        				updateReportsConfirmResponse(resultSoap);                           				
	                        				sleep(2000);
	                        			} catch (Exception e) {  }
	                        			ntwrkHandler.post(reloadListView);
	                 				}
	                        	}.start();
	                        }
	                    });
	    			}
				}
	    	} catch(Exception asegg){
	    		SapGenConstants.showErrorLog("Error in getNetworkResponseRunnable : "+asegg.toString());
	    	}
	    }	    
    };
    
   /* public void updateStatusConfirmResponseForRefresh(final SoapObject soap){	
    	boolean errorflag = false;
        try{ 
        	if(soap != null){                
            	String soapMsg = soap.toString();
        		SapGenConstants.showLog("Count : "+soap.getPropertyCount());
             
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[22];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            SalesProActStatusConstraints mattEmpObj = null; 
	            SalesProActCrtConstraints custActCategory = null;    	
	            
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_ACTVTYCTGRY10")){
	                                if(custActCategory != null)
	                                	custActCategory = null;
	                                    
	                                custActCategory = new SalesProActCrtConstraints(resArray);
	                                
	                                if(categoryList != null)
	                                	categoryList.add(custActCategory);	  
	                            }
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_STTS10")){
	                                if(mattEmpObj != null)
	                                	mattEmpObj = null;
	                                    
	                                mattEmpObj = new SalesProActStatusConstraints(resArray);
	                                if(mattEmpObj != null)
	                                	statusList.add(mattEmpObj);
	                            }
	                        }
	                    }
	                }
	            } 
        	}  
	          } catch(Exception sff){
	            	SapGenConstants.showErrorLog("Error in updateStatusConfirmResponseForRefresh for getting status from sap:"+sff.toString());
	            	errorflag = true;
	            } 
        finally{
        	try {
				
				if((statusList != null) && (statusList.size() > 0)){
					SapGenConstants.showLog("Status Vector Size : "+statusList.size());
					ActDBOperations.deleteAllStatusDataFromDB(this);
		        	insertActivityStatusDataIntoDB();
				}
				
				if((categoryList != null) && (categoryList.size() > 0)){
					SapGenConstants.showLog("categoryList Vector Size : "+categoryList.size());
					ActDBOperations.deleteAllCategoryDataFromDB(this);
					insertActivityDataIntoDB();
				}
				 stopProgressDialog();
		            this.runOnUiThread(new Runnable() {
		                public void run() {
		                	initSoapConnection();
		                }
		            });
				
			} catch (Exception esf) {
				stopProgressDialog();
				this.runOnUiThread(new Runnable() {
	                public void run() {
	                	initSoapConnection();
	                }
	            });
				SapGenConstants.showErrorLog("Error in Database Insert/update in updateStatusConfirmResponseForRefresh:"+esf.toString());
				//SapGenConstants.showErrorLog("On updateCategoryServerResponse finally block: "+esf.toString());
			} 
        }	
     } // updateresponseRefresh
*/    
    
    public void updateStatusConfirmResponseForRefresh(SoapObject soap){	   	
		boolean errorflag = false, resMsgErr = false;
		String finalString2="";
        try{             	
            String saptimeStr = getDateTime();
            String strtparsStr="Start Parsing- "+saptimeStr;	
 		   	diogList.add(strtparsStr);	           
        	if(soap != null){
        		SapGenConstants.soapResponse(this, soap, false);            	
            	SapGenConstants.showLog("On soap response : "+soap.toString());
            	String soapMsg = soap.toString();            
            	SapGenConstants.showLog("resMsgErr : "+resMsgErr);

            	 SalesProActStatusConstraints mattEmpObj = null; 
 	            SalesProActCrtConstraints custActCategory = null;         
    	        	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[50];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                    	 SapGenConstants.showLog(j+" : "+pii.getProperty(j).toString());
	                        if(j > 1 && j<=4){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
	                            resC = 0;
	                            indexA = 0;
	                            indexB = result.indexOf(delimeter);
	                            int index1 = 0;
	                            while (indexB != -1) {
	                                res = result.substring(indexA, indexB);
	                                resArray[resC] = res;
	                                indexA = indexB + delimeter.length();
	                                indexB = result.indexOf(delimeter, indexA);
	                                if(resC == 0){
                                    	docTypeStr = res;
                                    }
	                                if(resC == 1){
	                                	  String[] respStr = res.split(";");
	                                	  if(respStr.length >= 1){
	                                		  String respTypeData = respStr[0];
	 	                                    	SapGenConstants.showLog("respTypeData : "+respTypeData);
	 	                                    	index1 = respTypeData.indexOf("=");
	 	                                    	index1 = index1+1;
	 	                                        String respType = respTypeData.substring(index1, respTypeData.length());
	 	                                    	SapGenConstants.showLog("respType : "+respType);
	 	                                    	
	 	                                    	String rowCountStrData = respStr[1];
	 	                                    	SapGenConstants.showLog("rowCountStrData : "+rowCountStrData);
	 	                                    	index1 = rowCountStrData.indexOf("=");
	 	                                    	index1 = index1+1;
	 	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
	                                	  
	                                //resC++;
	                            //}
	                            int endIndex = result.lastIndexOf(';');
	                            resArray[resC] = result.substring(indexA,endIndex);
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_ACTVTYCTGRY10")){
	                            	activityListCount = Integer.parseInt(rowCount);
                             		activityListType = respType;	                            	                                
	                            }
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_STTS10")){
	                            	statuslistCount = Integer.parseInt(rowCount);
                             		statuslistType = respType;	                               
	                            }	                           
	                                	  }
	                                }
	                            resC++;
                                if(resC == 2)
                                	break;
	                            } 
	                        }
	                        if(j > 4){
	                        	 result = pii.getProperty(j).toString();
                                 //SapGenConstants.showLog("Result j>4 : "+result);       
 	                            firstIndex = result.indexOf(delimeter);
 	                            eqIndex = result.indexOf("=");
 	                            eqIndex = eqIndex+1;
 	                            firstIndex = firstIndex + 3;
 	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));	   
 	                           result = result.substring(firstIndex);
 	                          String finalString= result.replace(";", " "); 
	                            finalString2= finalString.replace("}", " "); 
	                           SapGenConstants.showLog("finalString2"+finalString2);
                               //SapGenConstants.showLog("Document Type : "+docTypeStr);
                               //SapGenConstants.showLog("Result : "+result);
                               
                               resC = 0;
                               indexA = 0;
                               indexB = result.indexOf(delimeter);
                               while (indexB != -1) {
                                   res = result.substring(indexA, indexB);
                                   //SapGenConstants.showLog(resC+" : "+res);
                                   resArray[resC] = res;
                                   indexA = indexB + delimeter.length();
                                   indexB = result.indexOf(delimeter, indexA);
                                   resC++;
                               }
                               
                               int endIndex = result.lastIndexOf(';');
                               resArray[resC] = result.substring(indexA, endIndex);
                                if(docTypeStr.equalsIgnoreCase("ZGSXCAST_ACTVTYCTGRY10")){
                                	if(custActCategory != null)
	                                	custActCategory = null;
	                                    
	                                custActCategory = new SalesProActCrtConstraints(resArray);
	                                
	                                if(categoryList != null)
	                                	categoryList.add(custActCategory);	 
	                            }
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_STTS10")){
	                            	if(mattEmpObj != null)
	                                	mattEmpObj = null;
	                                    
	                                mattEmpObj = new SalesProActStatusConstraints(resArray);
	                                if(mattEmpObj != null)
	                                	statusList.add(mattEmpObj);
	                            }else if(docTypeStr.equalsIgnoreCase("ZGSSMWST_DIAGNOSYSINFO01")){		                        	
		                               diogList.add(finalString2);                                              	                               
	                            	}
	                           
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SapGenConstants.showLog(taskErrorMsgStr);
	                                SapGenConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                    }
	                }
	            }//for
        	}else{
        		errorflag = true;
        	}
        }
        catch(Exception sff){
            SapGenConstants.showErrorLog("On updateTaskResponse : "+sff.toString());
            errorflag = true;
            stopProgressDialog();
        } 
        finally{ 
        	try{      
	        	if(!errorflag){	        		
	        		if(categoryList != null){
    	    			if(activityListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
    	    				if((activityListCount == 0) && categoryList == null){
    	    					ActDBOperations.deleteAllCategoryDataFromDB(this);
    	        			}
    	    				else if((activityListCount > 0) && categoryList != null){
    	    					ActDBOperations.deleteAllCategoryDataFromDB(this);
    	    					insertActivityDataIntoDB();
    	        			}
    	    			}
    	    			if(activityListType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
    	    				if((activityListCount == 0) && categoryList == null){
    	    					ActDBOperations.deleteAllCategoryDataFromDB(this);
    	        			}
    	    				else if((activityListCount > 0) && categoryList == null){
    	    					//initDBConnection();
    	        			}
    	    				else if((activityListCount > 0) && categoryList != null){
    	    					ActDBOperations.deleteAllCategoryDataFromDB(this);
    	    					insertActivityDataIntoDB();
    	        			}
    	    			}
    	    		}//status full-delta sets
	        				           
	        		if(statusList != null){
    	    			if(statuslistType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
    	    				if((statuslistCount == 0) && (statusList.size()==0)){
    	    					ActDBOperations.deleteAllStatusDataFromDB(this);
    	        			}
    	    				else if((statuslistCount > 0) && (statusList.size()!=0)){
    	    					ActDBOperations.deleteAllStatusDataFromDB(this);
    	    		        	insertActivityStatusDataIntoDB();
    	        			}
    	    			}
    	    			if(statuslistType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
    	    				if((statuslistCount == 0) && (statusList.size()==0)){
    	    					ActDBOperations.deleteAllStatusDataFromDB(this);
    	        			}
    	    				else if((statuslistCount > 0) && (statusList.size()==0)){
    	    					//initDBConnection();
    	        			}
    	    				else if((statuslistCount > 0) && (statusList.size()!=0)){
    	    					ActDBOperations.deleteAllStatusDataFromDB(this);
    	    		        	insertActivityStatusDataIntoDB();
    	        			}
    	    			}
    	    		}//confProductVect full-delta sets	        			        		
	        		/* ServiceOrdDocOpConstraints docsCategory = null;
		            if(documentsVect.size() > 0){
			            for(int i = 0; i < documentsVect.size(); i++){  
			            	docsCategory = ((ServiceOrdDocOpConstraints)documentsVect.get(i));
		                    if(docsCategory != null){
		    	        		ServiceProDBOperations.deleteAllDocsCategoryDataFromDB(this, ServiceProOfflineContraintsCP.SERPRO_CONFLIST_CONTENT_URI, docsCategory.getObjectId().toString().trim());
		    	        	}
		                }  	
		            }
		            insertConfListDataIntoDB();
		            	    	            
		            ServiceOrdDocOpConstraints spareDocsCategory = null;
		            if(confSparesVect.size() > 0){
			            for(int i = 0; i < confSparesVect.size(); i++){  
			            	spareDocsCategory = ((ServiceOrdDocOpConstraints)confSparesVect.get(i));
		                    if(docsCategory != null){
		    	        		ServiceProDBOperations.deleteAllConfSpareDocsCategoryDataFromDB(this, ServiceProOfflineContraintsCP.SERPRO_CONFSPARELIST_CONTENT_URI, spareDocsCategory.getObjectId().toString().trim());
		    	        	}
		                }  	
		            }
		            insertConfSpareListDataIntoDB();
		            
		            ServiceFollDocOpConstraints followCategory = null;
		            if(confCollecVect.size() > 0){
			            for(int i = 0; i < confCollecVect.size(); i++){
			            	followCategory = ((ServiceFollDocOpConstraints)confCollecVect.get(i));
		                    if(followCategory != null){
		    	        		ServiceProDBOperations.deleteAllConfCollecDocsCategoryDataFromDB(this, ServiceProOfflineContraintsCP.SERPRO_CONFCOLLECLIST_CONTENT_URI, followCategory.getSRCDocObjId().toString().trim());
		    	        	}
		                }  	
		            }
		            insertConfCollecListDataIntoDB();*/
	        	}  
	            stopProgressDialog();
	            String saptimeStr = getDateTime();
	            String strtparsStr="Stop Parsing- "+saptimeStr;	
	 		   	diogList.add(strtparsStr);	  		
	            this.runOnUiThread(new Runnable() {
	                public void run() {
	                	/* DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");	                        				   
						   Calendar cal = Calendar.getInstance();	                        				 
						   SapGenConstants.showLog(" current date : "+dateFormat.format(cal.getTime()));
						   String date = dateFormat.format(cal.getTime());
						   String soapbeginStr = "+"+"API-FOR-EVENT:SERVICE-DOX-FOR-EMPLY-BP-GET"+"\n"+"API-BEGIN-TIME DEVICE"+date;
						   diogList.add(soapbeginStr); */
	                	initSoapConnection();
	                }
	            });
        	}
			catch (Exception de) {
				stopProgressDialog();
				this.runOnUiThread(new Runnable() {
	                public void run() {
	                	initSoapConnection();
	                }
	            });
	        	SapGenConstants.showErrorLog("Error in Database Insert/update in updateStatusConfirmResponseForRefresh:"+de.toString());
	        }
        }
    }//fn updateStatusConfirmResponseForRefresh
    
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
    
    final Runnable reloadListView = new Runnable(){
	    public void run()
	    {
	    	try{
	    		drawSubLayout();   		
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };
    
    private void insertActivityStatusDataIntoDB(){
    	SalesProActStatusConstraints statusCategory;
    	try {
			if(statusList != null){
				for(int k=0; k<statusList.size(); k++){
					statusCategory = ((SalesProActStatusConstraints) statusList.get(k));
					if(statusCategory != null){
						ActDBOperations.insertStatusDataInToDB(this, statusCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActivityStatusDataIntoDB: "+ewe.toString());
		}
    }//fn insertActivityStatusDataIntoDB
     
    private void insertActivityDataIntoDB(){
    	SalesProActCrtConstraints custActCategory;
    	try {
			if(categoryList != null){
				for(int k=0; k<categoryList.size(); k++){
					custActCategory = (SalesProActCrtConstraints) categoryList.get(k);
					if(custActCategory != null){
						ActDBOperations.insertCategoryDataInToDB(this, custActCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActivityDataIntoDB: "+ewe.toString());
		}
    }//fn insertActivityDataIntoDB
    
    public void updateReportsConfirmResponse(SoapObject soap){	
		boolean errorflag = false, resMsgErr = false;
		String finalString2 = "";
		SalesProActOutputConstraints category = null;
		SalesProActCustomersConstraints customers = null;
        try{         	           	        	                                     
            String saptimeStr = getDateTime();
            String strtparsStr="Start Parsing- "+saptimeStr;	
 		   	diogList.add(strtparsStr);	
        	if(soap != null){  
        		String soapMsg = soap.toString();
        		SapGenConstants.showLog("Count : "+soap.getPropertyCount());
        		 if(actList != null)
 	            	actList.clear();
        		 if(allactList != null)
        			 allactList.clear();
 	            if(cusList != null)
 	            	cusList.clear();
 	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[50];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                    	SapGenConstants.showLog(j+" : "+pii.getProperty(j).toString());
	                        if(j > 1 && j<=2){
	                        	  result = pii.getProperty(j).toString();
		                            firstIndex = result.indexOf(delimeter);
		                            eqIndex = result.indexOf("=");
		                            eqIndex = eqIndex+1;
		                            firstIndex = firstIndex + 3;
		                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
		                            result = result.substring(firstIndex);
		                            
		                            resC = 0;
		                            indexA = 0;
		                            indexB = result.indexOf(delimeter);
		                            int index1 = 0;
		                            while (indexB != -1) {
		                                res = result.substring(indexA, indexB);
		                                resArray[resC] = res;
		                                indexA = indexB + delimeter.length();
		                                indexB = result.indexOf(delimeter, indexA);
		                                if(resC == 0){
	                                    	docTypeStr = res;
	                                    }
		                                if(resC == 1){
		                                	  String[] respStr = res.split(";");
		                                	  if(respStr.length >= 1){
		                                		  String respTypeData = respStr[0];
		                                		  SapGenConstants.showLog("respTypeData : "+respTypeData);
		 	                                    	index1 = respTypeData.indexOf("=");
		 	                                    	index1 = index1+1;
		 	                                        String respType = respTypeData.substring(index1, respTypeData.length());
		 	                                       SapGenConstants.showLog("respType : "+respType);
		 	                                    	
		 	                                    	String rowCountStrData = respStr[1];
		 	                                    	SapGenConstants.showLog("rowCountStrData : "+rowCountStrData);
		 	                                    	index1 = rowCountStrData.indexOf("=");
		 	                                    	index1 = index1+1;
		 	                                        String rowCount = rowCountStrData.substring(index1, rowCountStrData.length());
		                                	  
		                                //resC++;
		                            //}
		                            int endIndex = result.lastIndexOf(';');
		                            resArray[resC] = result.substring(indexA,endIndex);
		                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_INTRCTN11")){
	                                    	//ServiceProConstants.showLog("docTypeStr :"+ docTypeStr+"  rowCount : "+rowCount+"  respType : "+respType);
		                            	intrctcount = Integer.parseInt(rowCount);
		                            	intrctnType = respType;
                                     }	                            
                                 	
		                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_DCMNTCSTMRCNTCT10S")){
		                            	doclistcount = Integer.parseInt(rowCount);
                                 		doclistType = respType;
		                            	
                                     }	                                
		                                	  }
		                                }
		                            resC++;
	                                if(resC == 2)
	                                	break;
		                            } 
	                        } if(j > 3){
	                        	 result = pii.getProperty(j).toString();
                                 //SapGenConstants.showLog("Result j>4 : "+result);       
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
                               if(docTypeStr.equalsIgnoreCase("ZGSXCAST_INTRCTN11")){
                            	   if(category != null)
	                                    category = null;	                                    
	                                category = new SalesProActOutputConstraints(resArray);	
	                                if(actList != null)
	                                	actList.add(category);	    
	                                	if(allactList != null)
	                                			allactList.add(category);
  		                          }
  		                            else  if(docTypeStr.equalsIgnoreCase("ZGSXCAST_DCMNTCSTMRCNTCT10S")){
  		                            	if(customers != null)
  		                                	customers = null;
  		                                    
  		                                customers = new SalesProActCustomersConstraints(resArray);	
  		                                	                               
  		                                if(cusList != null)
  		                                	cusList.add(customers);	                        	                              
  		                            }  	else if(docTypeStr.equalsIgnoreCase("ZGSSMWST_DIAGNOSYSINFO01")){		                        	
			                               diogList.add(finalString2);                                              	                               
	                            }	                           
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                SapGenConstants.showLog(taskErrorMsgStr);
	                                SapGenConstants.showErrorDialog(this, taskErrorMsgStr);
	                            }
	                        }
	                    }
	                }
	            }//for
        	}else{
        		errorflag = true;
        	}
        }
        catch(Exception sff){
        	SapGenConstants.showErrorLog("On updateTaskResponse : "+sff.toString());
            errorflag = true;
        } 
        finally{        	
        	try{      
	        	//if(!errorflag){
	        		SharedPreferences sharedPreferences = getSharedPreferences(SapGenConstants.PREFS_NAME_FOR_ACTIVITY, 0);    
	    			SharedPreferences.Editor editor = sharedPreferences.edit();    
	    			editor.putBoolean(SapGenConstants.PREFS_KEY_ACTIVITY_FOR_MYSELF_GET, true);    
	    			editor.commit();   	    	        		
    	    			if(intrctnType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
    	    				if((intrctcount == 0) && (actList.size()==0)){
    	    					ActDBOperations.deleteAllActCategoryDataFromDB(this);
    	        			}
    	    				else if((intrctcount > 0) && (actList.size()!=0)){
    	    					ActDBOperations.deleteAllActCategoryDataFromDB(this);
    	    					insertActListDataIntoDB();
    	        			}
    	    			}
    	    			if(intrctnType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
    	    				if((intrctcount == 0) && (actList.size()==0)){
    	    					ActDBOperations.deleteAllActCategoryDataFromDB(this);
    	        			}
    	    				else if((doclistcount > 0) && (actList.size()==0)){
    	    					//initDBConnection();
    	        			}
    	    				else if((intrctcount > 0) && (actList.size()!=0)){
    	    					ActDBOperations.deleteAllActCategoryDataFromDB(this);
    	    					insertActListDataIntoDB();
    	        			}
    	    			}    	    			    	    		
        			
	        		if(cusList != null){
    	    			if(doclistType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_FULLSETS)){
    	    				if((doclistcount == 0) && (cusList.size()==0)){
    	    					ActDBOperations.deleteAllActCusCategoryDataFromDB(this);
    	        			}
    	    				else if((doclistcount > 0) && (cusList.size()!=0)){
    	    					ActDBOperations.deleteAllActCusCategoryDataFromDB(this);
    	    	        		insertActCusListDataIntoDB();
    	        			}
    	    			}
    	    			if(doclistType.equalsIgnoreCase(SapGenConstants.SAP_RESPONSE_DELTASETS)){    	    				    	    				
    	    				if((doclistcount == 0) && (cusList.size()==0)){
    	    					ActDBOperations.deleteAllActCusCategoryDataFromDB(this);
    	        			}
    	    				else if((doclistcount > 0) && (cusList.size()==0)){
    	    					//initDBConnection();
    	        			}
    	    				else if((doclistcount > 0) && (cusList.size()!=0)){
    	    					ActDBOperations.deleteAllActCusCategoryDataFromDB(this);
    	    	        		insertActCusListDataIntoDB();
    	        			}
    	    			}
    	    		}//confSympCodeVect full-delta sets
	        	    	        
	        		if(!errorflag){
	            		idList = ActDBOperations.readAllActIdFromDB(this);
	            		galIdList = ActDBOperations.readAllGallActIdFromDB(this);
	        			if(galIdList != null){
	        				//deletion for unwanted row form gallery table and if activity id not available from SAP list.
	    	        		if(galIdList != null && galIdList.size() > 0){
	    	        			ActDBOperations.deleteGallRowForEmptyID(this);
	    	        			for(int id=0;id<galIdList.size();id++){
	    	        				if(idList != null && idList.size() > 0){
	    	        					String gallId = galIdList.get(id).toString().trim();
	    	        					if(gallId != null && gallId.length() > 0){
	    			        				if (!idList.contains(gallId)){
	    			        					SapGenConstants.showLog("gallId : "+gallId);
	    			        					ActDBOperations.deleteGallRowByGivnenID(this, gallId);
	    			        				}
	    	        					}
	    	        				}
	    	        			}
	    	        		}
	        			}
	            	}
	        		stopProgressDialog();
    	        	 String saptimeStr = getDateTime();
    	             String strtparsStr="Stop Parsing- "+saptimeStr;	
    	  		   	diogList.add(strtparsStr);	
    	  		  ActivityListforTablet.this.runOnUiThread(new Runnable() {
        	            public void run() {	 	        	            	
        	            	getLDBActivityList();
	    	        		 String saptimeStr = getDateTime();
	    			         String strtparsStr="Stop Rendering- "+saptimeStr;	
	    					 diogList.add(strtparsStr);	
	    					 
	    						String sapendtimeStr = getDateTime();
	    			    		String endsaptimeStr="- API-END-TIME DEVICE"+sapendtimeStr+"\n";
	    					   diogList.add(endsaptimeStr);
	    					   if(SapGenConstants.DiagnosisDetailsArr.size()!=0){
	    						   ArrayList diaglist2 = new ArrayList();
	    						   diaglist2.addAll(diogList);
	    						   diogList.clear();
	    						   diogList.addAll(SapGenConstants.DiagnosisDetailsArr);
	    						   diogList.addAll(diaglist2);	    						   
	    					   }	    					  	    					   
	    						   //SapGenConstants.DiagnosisDetailsArr.addAll(diogList);	
	    					   String stopprocdevice = "-"+"Stop PROCESSING DEVICE"+sapendtimeStr;	
	    					   diogList.add(stopprocdevice);
        	            	//refreshList();	
        	            	if(SapGenConstants.DIOG_FLAG==1)
        	            		DisplayDiogPopUp();
        	            }
        	        });       	        	
        	}
			catch (Exception de) {
				stopProgressDialog();
				SapGenConstants.showErrorLog("Error in Database Insert/update in Taskmainscreen:"+de.toString());
	        }               
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
    
    
    private OnClickListener skype_btnListener = new OnClickListener() {
        public void onClick(View v) {          	        	
        	showSkypeActivity();   			
        }
    };
    
    public void showSkypeActivity(){
    	String skypeName = "sowmyaraob";
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.setData(Uri.parse("skype:" + skypeName));
    	startActivity(intent);
    }//
    
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
 
 
    private void insertActListDataIntoDB(){
    	SalesProActOutputConstraints actCategory;
    	try {
			if(actList != null){
				for(int k=0; k<actList.size(); k++){
					actCategory = (SalesProActOutputConstraints) actList.get(k);
					if(actCategory != null){
						ActDBOperations.insertActCategoryDataInToDB(this, actCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActListDataIntoDB: "+ewe.toString());
		}
    }//fn insertActListDataIntoDB
    
    private void insertActCusListDataIntoDB(){
    	SalesProActCustomersConstraints customers;
    	try {
			if(cusList != null){
				for(int k=0; k<cusList.size(); k++){
					customers = (SalesProActCustomersConstraints) cusList.get(k);
					if(customers != null){
						ActDBOperations.insertActCusCategoryDataInToDB(this, customers);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActCusListDataIntoDB: "+ewe.toString());
		}
    }//fn insertActCusListDataIntoDB
    
    private void getLDBActivityList(){
		try {
			 String saptimeStr = getDateTime();
	         String strtparsStr="Start Rendering- "+saptimeStr;	
	 		 diogList.add(strtparsStr);	
	 		 
			actList = ActDBOperations.readAllActDataFromDB(this);
			if(actList != null)
				allactList = (ArrayList)actList.clone();
			cusList = ActDBOperations.readAllActCusDataFromDB(this);
			
		} catch (Exception sse) {
			SapGenConstants.showErrorLog("Error on getLDBActivityList: "+sse.toString());
		}
		finally{
        	try {
        		layTaskListTableHeader();
        		drawSubLayout();
			} catch (Exception e) {}
		}
	}//fn getLDBActivityList
		
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			SapGenConstants.showErrorLog(ce.toString());
		}
    }//fn stopProgressDialog
    
   /* private void listviewcall(){
		try {
			setListAdapter(new MyActivityListAdapterForPhone(this));
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in listviewcall:"+ce.toString());
		}
	}//fn listviewcall
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  super.onActivityResult(requestCode, resultCode, data);
		  if(resultCode == RESULT_OK && requestCode == CrtGenActivityConstants.ACT_EDIT_SCREEN){			
			  //updateflag =1;
			  internetAccess = SapGenConstants.checkConnectivityAvailable(ActivityListforTablet.this);
			  if(diogList!=null)
				  diogList.clear();
			  if(internetAccess){
				  initSoapConnection();
			  }
			  else{
				  getLDBActivityList();	
				  SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
			  }	
		  }else if(resultCode == RESULT_OK && requestCode == CrtGenActivityConstants.ACT_CRE_SCREEN){
			  //updateflag = this.getIntent().getIntExtra("updateflag",updateflag);
			  internetAccess = SapGenConstants.checkConnectivityAvailable(ActivityListforTablet.this);
			  if(diogList!=null)
				  diogList.clear();
			  if(internetAccess){
				  initSoapConnection();
			  }
			  else{
				  getLDBActivityList();	
				  SapGenConstants.showLog("We can't communicate with SAP. Please try again later!");
			  }	
		  }		  
	}
	
    public void onBackPressed() {
    	try{
    		SapGenConstants.showLog("On Back Key Pressed");
    		if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
    			SapGenConstants.selContactVect.removeAllElements();
    			SapGenConstants.selContactVect.clear();
    		}
    		if(SapGenConstants.selContactIdArr != null && SapGenConstants.selContactIdArr.size() > 0){
    			SapGenConstants.selContactIdArr.clear();
    		}
    		finish();
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in onBackPressed : "+sfg.toString());
    	}
    }//fn onBackPressed
    
}//ActivityListforTablet