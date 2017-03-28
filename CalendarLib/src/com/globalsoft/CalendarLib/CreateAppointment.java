package com.globalsoft.CalendarLib;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.globalsoft.CalendarLib.Contraints.GalleryImageDetails;
import com.globalsoft.CalendarLib.Contraints.SalesOrdProIpConstraints;
import com.globalsoft.CalendarLib.Contraints.SalesProAppCustomersConstraints;
import com.globalsoft.CalendarLib.Database.AppDBOperations;
import com.globalsoft.CalendarLib.Utils.CalendarConstants;
import com.globalsoft.ContactsListCloneLib.ContactsList;
import com.globalsoft.SapLibSoap.Constraints.ContactProVectSerializable;
import com.globalsoft.SapLibSoap.Constraints.SalesProActOutputConstraints;
import com.globalsoft.SapLibSoap.Constraints.SapGenDocKeyOpConstraints;
import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class CreateAppointment extends Activity {
	
	private static String appNameStr = "", objectId = "", processType = "";
	private String contactId = "", customerId = "", contactFName="", contactLName="", customerName = ""; 
	private String selected_category = "", selected_staus, selCatCode = "";
	private String formattedDate = "", formattedTime = "", timez_offset = "", callDescStr = "", callNotesStr = "", callDurationStr = "";
	private boolean isConnAvail = true;
	private int selectedStatusPos = -1;
	private ImageView[] contDelImg;
	
	private int dispwidth = 300;
    private String[] statusTxtArr;
    private ArrayList categoryList = new ArrayList();
    
    private ProgressDialog pdialog = null;		
	private SoapObject resultSoap = null;
	private SoapObject requestSoapObj = null;
	
	private Button doneBtn, cancelBtn;
	private TextView contactTitle1, contactTitle2, contactTitle3;
	private EditText descEditText, notesEditText;
	private ImageButton custDelBtn, custSearchBtn1, custSearchBtn2;
	private ImageButton captureBtn, galleryBtn;
	private ContactProVectSerializable vectObj1;
	
	private static final int MAIN_ID = Menu.FIRST;
	private static final int MAIN_IMG_ID = Menu.FIRST+1;
	
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private static int respType = 0;
	private String _imagecapturepath;
	private LinearLayout contactdetails;

	public TextView start_date, end_date, start_time, end_time;
    private String start_date_value_cal = "", start_time_value_cal = "";
    private String end_date_value_cal = "", end_time_value_cal = "";
    
	// date and time
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    
    private int startYear_dp;
    private int startMonth_dp;
    private int startDay_dp;
    private int startHour_dp;
    private int startMinute_dp;
    
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;
    
    private int endYear_dp;
    private int endMonth_dp;
    private int endDay_dp;
    private int endHour_dp;
    private int endMinute_dp;

    private static final int START_DATE_DIALOG_ID = 1;
    private static final int START_TIME_DIALOG_ID = 2;    
    private static final int END_DATE_DIALOG_ID = 3;
    private static final int END_TIME_DIALOG_ID = 4;
    
    private SalesProActOutputConstraints category = null;
	private boolean editModeFlag = false, objIdExits = false;
	private String imageUniqueId = "";
	private String eventId = "";
    
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.genappcreate); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			SapGenConstants.MOBILE_IMEI = SapGenConstants.getMobileIMEI(this);
			contactdetails = (LinearLayout)findViewById(R.id.contactdetails);
			
			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			appNameStr = this.getIntent().getStringExtra("app_name");
			
			category = (SalesProActOutputConstraints)this.getIntent().getSerializableExtra("actobj");				
			editModeFlag = this.getIntent().getBooleanExtra("editflag", false);
			
			if(category != null){
				objectId = category.getObjectId().toString().trim();
				processType = category.getProcessType().toString().trim();
				String imageuid = AppDBOperations.getUniqueId(this, objectId);
				eventId = AppDBOperations.getEventId(this, objectId);
				if(eventId == null){
					eventId = "";
				}else{
					SapGenConstants.showLog("eventId11 " + eventId);
				}
				String imagecount = AppDBOperations.getGalImgCount(this, objectId);
				SapGenConstants.showLog("objectId " + objectId);
				if(imageuid != null && imageuid.length() > 0){
					objIdExits = true;
					imageUniqueId = imageuid;
					if(imagecount != null && imagecount.length() > 0){
						CalendarConstants.imageCountId = Integer.parseInt(imagecount);
					}else{
						CalendarConstants.imageCountId = 1;
					}
				}else{
					objIdExits = false;
					CalendarConstants.imageCountId = 1;
					imageUniqueId = ""+System.currentTimeMillis();
					if(imagecount != null && imagecount.length() > 0){
						AppDBOperations.insertAppGalListDetDataInToDB(this, objectId, imageUniqueId, imagecount, eventId);
					}else{
						AppDBOperations.insertAppGalListDetDataInToDB(this, objectId, imageUniqueId, "", eventId);
					}
				}
			}else{
				SapGenConstants.showLog("objectId not available!");
				CalendarConstants.imageCountId = 1;
				imageUniqueId = ""+System.currentTimeMillis();
			}			

    		SapGenConstants.showLog("imageUniqueId " + imageUniqueId);
    		SapGenConstants.showLog("imageCountIdd " + CalendarConstants.imageCountId);
			
			if(editModeFlag){
				myTitle.setText(getResources().getString(R.string.SCR_APP_DET_TITLE));				
			}else{
				myTitle.setText(getResources().getString(R.string.SCR_CRE_APP));
			}
			SapGenConstants.showLog("editModeFlag : "+editModeFlag);
    		SapGenConstants.showLog("customerName : "+customerName);					
			CalendarConstants.CRTAPP_PACKAGE_NAME = this.getApplicationContext().getPackageName();
			if((appNameStr == null) || (appNameStr.equalsIgnoreCase("")))
            	appNameStr = SapGenConstants.APPLN_NAME_STR_MOBILEPRO;
			CalendarConstants.CRTAPP_CALLING_APP_NAME = appNameStr;
			dispwidth = SapGenConstants.getDisplayWidth(this);		
			SapGenConstants.showLog("Called From Application : "+appNameStr+" : "+CalendarConstants.CRTAPP_PACKAGE_NAME);
			initLayout();
			
			isConnAvail = SapGenConstants.checkConnectivityAvailable(this);
    		SapGenConstants.showLog("Internet Connection Available : "+isConnAvail);
    		    		
			if(editModeFlag == true)
	            prefillEditActivityValues();
        } catch (Exception de) {
        	SapGenConstants.showErrorLog("Error in Application Init: "+de.toString());
        }
    }
    
    private void initLayout(){
    	try{			
			contactTitle1 = (TextView) findViewById(R.id.contactTitle1);
			contactTitle2 = (TextView) findViewById(R.id.contactTitle2);
			contactTitle3 = (TextView) findViewById(R.id.contactTitle3);
			
			doneBtn = (Button) findViewById(R.id.saveBtn);
			doneBtn.setOnClickListener(sendSAPBtnListener); 
			
			cancelBtn = (Button) findViewById(R.id.cancelBtn);
			cancelBtn.setOnClickListener(cancelBtnListener); 
						
			descEditText = (EditText)findViewById(R.id.calldescET);
			notesEditText = (EditText)findViewById(R.id.notesET);			
						
			captureBtn = (ImageButton) findViewById(R.id.captureimgbtn);
			captureBtn.setOnClickListener(captureBtnListener); 
			
			galleryBtn = (ImageButton) findViewById(R.id.galleryimgbtn);
			galleryBtn.setOnClickListener(galleryBtnListener); 
			
			custSearchBtn1 = (ImageButton) findViewById(R.id.custsearchbtn1);
			custSearchBtn1.setOnClickListener(custsrch_btnListener); 
			custSearchBtn2 = (ImageButton) findViewById(R.id.custsearchbtn2);
			custSearchBtn2.setOnClickListener(custsrch_btnListener); 
			
			custDelBtn = (ImageButton) findViewById(R.id.custdelbtn);
			custDelBtn.setOnClickListener(custDelBtnListener); 
			
			start_date = (TextView) findViewById(R.id.st_date);
	        start_date.setOnClickListener(start_dateListener);
	        
	        start_time = (TextView) findViewById(R.id.st_time);
	        start_time.setOnClickListener(start_timeListener);
	        
	        end_date = (TextView) findViewById(R.id.ed_date);
	        end_date.setOnClickListener(end_dateListener);
	        
	        end_time = (TextView) findViewById(R.id.ed_time);
	        end_time.setOnClickListener(end_timeListener);
				        
	        descEditText.setOnFocusChangeListener(new OnFocusChangeListener() {         
	            public void onFocusChange(View v, boolean hasFocus) {
	            	SapGenConstants.showLog("hasFocus : "+hasFocus);
	                if(!hasFocus){
	                	descEditText.setSelection(0);
		            	SapGenConstants.showLog("false");
	                }else{
	                	descEditText.setSelection(descEditText.getText().length());
		            	SapGenConstants.showLog("true");
	                }
	            }
	        });
	        
			final Calendar c = Calendar.getInstance();
	        //initialize for start date
	        startYear = c.get(Calendar.YEAR);
	        startMonth = c.get(Calendar.MONTH);
	        startDay = c.get(Calendar.DAY_OF_MONTH);
	        startHour = c.get(Calendar.HOUR_OF_DAY);
	        startMinute = c.get(Calendar.MINUTE);
	        
	        startYear_dp = startYear;
	        startMonth_dp = startMonth;
	        startDay_dp = startDay;
	        startHour_dp = startHour;
	        startMinute_dp = startMinute;
	        
	        updateDisplay_start_date();
	        updateDisplay_start_time();
	        
	        //initialize for end date
	        endYear = c.get(Calendar.YEAR);
	        endMonth = c.get(Calendar.MONTH);
	        endDay = c.get(Calendar.DAY_OF_MONTH);
	        endHour = c.get(Calendar.HOUR_OF_DAY);
	        endMinute = c.get(Calendar.MINUTE);
	        
	        endYear_dp = endYear;
	        endMonth_dp = endMonth;
	        endDay_dp = endDay;
	        endHour_dp = endHour;
	        endMinute_dp = endMinute;
	        
	        updateDisplay_end_date();
	        updateDisplay_end_time();
			
			if(SapGenConstants.galleryVect != null)
				SapGenConstants.galleryVect.removeAllElements();
			
			updateUIElements();			
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in initLayout : "+sfg.toString());
    	}
    }//fn initLayout
        
    private void updateUIElements(){
        try{        	
        	contactdetails.removeAllViews();
        	SapGenConstants.showLog("selContactVect: "+SapGenConstants.selContactVect);
        	if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
            	int rowSize = SapGenConstants.selContactVect.size();
        		contDelImg = new ImageView[rowSize];        		
            	custDelBtn.setVisibility(View.VISIBLE); 
            	custSearchBtn2.setVisibility(View.VISIBLE);
            	custSearchBtn1.setVisibility(View.GONE);
            	contactTitle1.setText(getResources().getString(R.string.SALESORDPRO_SORDHEAD_LBL_CUSTNAMETITLE)+" ("+SapGenConstants.selContactVect.size()+")");
        		contactTitle2.setVisibility(View.GONE);
            	contactTitle3.setVisibility(View.GONE);
    					        	
    			if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
            		for (int pos = 0; pos < SapGenConstants.selContactVect.size(); pos++){
    	        		String name = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getContactName();
    					String id = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getContactDeviceId();
    					String sapid = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getSapId();
    					String cusid = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getCustomerId();
    					String fname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getFName();
    					String lname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getLName();
    					String orgname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getOrgName();
    										
    					contactId = sapid;
    					customerId = cusid;
    					contactFName = fname;
    					contactLName = lname;
    					customerName = orgname;

    		        	SapGenConstants.showLog("SAP Contact Id : "+contactId);
    		            SapGenConstants.showLog("SAP Customer Id : "+customerId);
    		            SapGenConstants.showLog("SAP Customer FName : "+contactFName);
    		            SapGenConstants.showLog("SAP Customer LName : "+contactLName);
    		            SapGenConstants.showLog("SAP Customer Name : "+customerName);		            					

    				    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
    				    	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
    				    layoutParams.setMargins(0, 2, 0, 2);
    				    
    				    LinearLayout ll = new LinearLayout(this);
    				    ll.setOrientation(LinearLayout.VERTICAL);	
    				    
    				    LinearLayout.LayoutParams linparamstext = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f); 
    				    linparamstext.gravity = Gravity.CENTER_VERTICAL;
    				    
    				    if(contactFName.trim().equalsIgnoreCase("null") || contactFName.trim() == null){
    				    	contactFName = "";
    				    }
    				    
    				    if(contactLName.trim().equalsIgnoreCase("null") || contactLName.trim() == null){
    				    	contactLName = "";
    				    }
    				    
    					String contact = contactFName.trim()+" "+contactLName.trim()+", "+customerName.trim();
    					SapGenConstants.showLog("contact : "+contact.length());
    				    LinearLayout llcontc = new LinearLayout(this);
    				    llcontc.setOrientation(LinearLayout.HORIZONTAL);
    				    
    				    TextView conValuetv = new TextView(this);
    				    conValuetv.setTextAppearance(getApplicationContext(), R.style.appText);
    				    if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH)
    				    	conValuetv.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
    				    conValuetv.setLayoutParams(linparamstext);
    				    //conValuetv.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
    				    conValuetv.setText(" "+contact);  
    				    llcontc.addView(conValuetv);
    										
    					LinearLayout.LayoutParams marginparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
    					marginparams.leftMargin = 5; 
    					marginparams.rightMargin = 5;
    					marginparams.gravity = Gravity.RIGHT;
    					contDelImg[pos] = new ImageView(this); 
    					contDelImg[pos].setLayoutParams(marginparams); 
    					contDelImg[pos].setImageResource(R.drawable.delete);
    					contDelImg[pos].setId(pos);
    					contDelImg[pos].setOnClickListener(new View.OnClickListener() {
    						public void onClick(View view) {
    							int id = view.getId();	
    							SapGenConstants.showLog("Selected id : "+id);
    							SapGenConstants.selContactVect.remove(id);
    		    				SapGenConstants.selContactIdArr.remove(id);
    							updateUIElements();
    						}	
                        });
    					llcontc.addView(contDelImg[pos]);
    					
    				    ll.addView(llcontc);
    				    contactdetails.addView(ll, layoutParams);	
            		}
            	}
        	}else{
        		contactTitle1.setText(getResources().getString(R.string.SALESORDPRO_SORDHEAD_LBL_CUSTNAMETITLE1));
        		contactTitle2.setVisibility(View.VISIBLE);
        		contactTitle2.setText(getResources().getString(R.string.SALESORDPRO_SORDHEAD_LBL_CUSTNAMETITLE2));
        		contactTitle3.setVisibility(View.VISIBLE);
        		contactTitle3.setText(getResources().getString(R.string.SALESORDPRO_SORDHEAD_LBL_CUSTNAMETITLE3));
            	custSearchBtn1.setVisibility(View.VISIBLE);
            	custSearchBtn2.setVisibility(View.GONE);
        		custDelBtn.setVisibility(View.GONE);
        	}        		
        }
        catch(Exception asf){
        	SapGenConstants.showErrorLog("Error in updateUIElements : "+asf.toString());
        }
    }//fn updateUIElements
    
    private void prefillEditActivityValues(){
        try{
        	if(category != null){
				SapGenConstants.showLog("Selected cat : "+category.getCategory());
				SapGenConstants.showLog("Selected status : "+category.getStatusTxt30());
				SapGenConstants.showLog("Selected desc : "+category.getDescription());
				SapGenConstants.showLog("Selected text : "+category.getText());
				SapGenConstants.showLog("Selected datef : "+category.getDateFrom()+" "+category.getTimeFrom());
				SapGenConstants.showLog("Selected datet : "+category.getDateTo()+" "+category.getTimeTo());
				
                /* Description update & display part*/
				descEditText.setText(category.getDescription().trim());    
				
				/* Notes update & display part*/
                String notesStr = category.getText().trim();
                if(!notesStr.equalsIgnoreCase("")){
                	notesEditText.setText(notesStr);
                }    
                
                /* Start date and time update & display part*/
                String start_date_value = category.getDateFrom().toString().trim();
                String[] st_date_value = start_date_value.split("-");
        		int month_value = startMonth + 1;
    	    	String month_dec = SapGenConstants.getMonthValue(month_value); 
                startYear_dp = Integer.parseInt(st_date_value[0]);
                startMonth_dp = Integer.parseInt(st_date_value[1]);
                startDay_dp = Integer.parseInt(st_date_value[2]);                

                String start_time_value = category.getTimeFrom().toString().trim();
                String[] st_time_value = start_time_value.split(":");
                startHour_dp = Integer.parseInt(st_time_value[0]);
                startMinute_dp = Integer.parseInt(st_time_value[1]);
                String start_time_val = st_time_value[0]+":"+st_time_value[1];
                
                String taskDateStrValue = ""; 
    	    	taskDateStrValue = SapGenConstants.getDateFormatForSAP(startYear_dp, startMonth_dp, startDay_dp, startHour_dp, startMinute_dp);
    	    	if(taskDateStrValue.length() > 0 && taskDateStrValue != null){
    	    		String dateStr = taskDateStrValue.toString();
    	        	dateStr = dateStr.trim();
    				//ServiceProConstants.showLog("DateStr1 : "+dateStr);
    	        	if(!dateStr.equalsIgnoreCase("")){	                    		
    	            	String newdatestr = SapGenConstants.getSystemDateFormatString(this, dateStr);
    	    			start_date.setText(" "+newdatestr);
    	        	}
    	        	else{
    	        		start_date.setText(
    	        	            new StringBuilder()
    	        	                    // Month is 0 based so add 1
    	        	            		.append(startDay).append(" ")
    	        	                    .append(month_dec).append(" ")                    
    	        	                    .append(startYear).append(" "));
    	        	}
    	    	}
    	    	else{
    	    		taskDateStrValue = "";
    	    		start_date.setText(
    	    	            new StringBuilder()
    	    	                    // Month is 0 based so add 1
    	    	            		.append(startDay).append(" ")
    	    	                    .append(month_dec).append(" ")                    
    	    	                    .append(startYear).append(" "));
    	    	}	     	    	
                start_time.setText(start_time_val);
                
                /* End date and time update & display part*/
                String end_date_value = category.getDateTo().toString().trim();
                String[] end_date_value_spl = end_date_value.split("-");
                int end_month_value = endMonth + 1;
    	    	String end_month_dec = SapGenConstants.getMonthValue(end_month_value); 
    	    	endYear_dp = Integer.parseInt(end_date_value_spl[0]);
                endMonth_dp = Integer.parseInt(end_date_value_spl[1]);
                endDay_dp = Integer.parseInt(end_date_value_spl[2]);
                
                String end_time_value = category.getTimeTo().toString().trim();
                String[] end__time_value_spl = end_time_value.split(":");
                endHour_dp = Integer.parseInt(end__time_value_spl[0]);
                endMinute_dp = Integer.parseInt(end__time_value_spl[1]);
                String end_time_val = end__time_value_spl[0]+":"+end__time_value_spl[1];
    	    	
    	    	String endDateStrValue = ""; 
    	    	endDateStrValue = SapGenConstants.getDateFormatForSAP(endYear_dp, endMonth_dp, endDay_dp, endHour_dp, endMinute_dp);
    	    	if(endDateStrValue.length() > 0 && endDateStrValue != null){
    	    		String dateStr = endDateStrValue.toString();
    	        	dateStr = dateStr.trim();
    	        	if(!dateStr.equalsIgnoreCase("")){	                    		
    	            	String newdatestr = SapGenConstants.getSystemDateFormatString(this, dateStr);
    	    			end_date.setText(" "+newdatestr);
    	        	}
    	        	else{
    	        		end_date.setText(
    	        	            new StringBuilder()
    	        	                    // Month is 0 based so add 1
    	        	            		.append(endDay).append(" ")
    	        	                    .append(end_month_dec).append(" ")                    
    	        	                    .append(endYear).append(" "));
    	        	}
    	    	}
    	    	else{
    	    		endDateStrValue = "";
    	    		end_date.setText(
    	    	            new StringBuilder()
    	    	                    // Month is 0 based so add 1
    	    	            		.append(endDay).append(" ")
    	    	                    .append(end_month_dec).append(" ")                    
    	    	                    .append(endYear).append(" "));
    	    	}
                end_time.setText(end_time_val);                   
        	}
        }
        catch(Exception adfw){
        	SapGenConstants.showErrorLog("Error in prefillEditActivityValues : "+adfw.toString());
        }
    }//fn prefillEditActivityValues
    
    private OnClickListener captureBtnListener = new OnClickListener() {
        public void onClick(View v) {
        	launchDefaultCamera();
        }
    };    

    private OnClickListener galleryBtnListener = new OnClickListener() {
        public void onClick(View v) {
        	getCapturedImageDetails();
        }
    };
    
    private OnClickListener custsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {
        	try{
	        	//Customer Search Action needs to be done
	        	Intent intent = new Intent(CreateAppointment.this, ContactsList.class);
				intent.putExtra("app_name", CalendarConstants.CRTAPP_CALLING_APP_NAME);
	        	intent.putExtra("editflag", editModeFlag); 
	        	if(category != null){
		        	intent.putExtra("actobj", category);	        		
	        	}  
				startActivityForResult(intent, SapGenConstants.CONTACTLISTCLONE_LAUNCH_SCREEN);
	        }
	    	catch(Exception sfg){
	    		SapGenConstants.showErrorLog("Error in custsrch_btnListener : "+sfg.toString());
	    	}
        }
    };
    
    private OnClickListener custDelBtnListener = new OnClickListener() {
        public void onClick(View v) {
        	try{
	        	//Customer Delete Btn Action needs to be done
        		if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
        			SapGenConstants.selContactVect.removeAllElements();
        			SapGenConstants.selContactVect.clear();
    				SapGenConstants.selContactIdArr.clear();
    				updateUIElements();
        		}
	        }
	    	catch(Exception sfg){
	    		SapGenConstants.showErrorLog("Error in custDelBtnListener : "+sfg.toString());
	    	}
        }
    };
    
    private OnClickListener sendSAPBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("Send Sap btn clicked");
        	doSendToSapAction();
        }
    };
    
    private OnClickListener cancelBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("Cancel btn clicked");
        	onClose();
        }
    };
    
    private void onClose(){
    	try{
    		finish();
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in onClose : "+sfg.toString());
    	}
    }//fn onClose
        
    public void onBackPressed() {
    	try{
    		SapGenConstants.showLog("On Back Key Pressed");
    		onClose();
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in onBackPressed : "+sfg.toString());
    	}
    }//fn onBackPressed
    
    private void deleteAllImages(){
    	try{
    		if(SapGenConstants.galleryVect != null){
    			GalleryImageDetails imageObj = null;
    			File newImgFile = null;
    			String filePath = "";
    			for(int k=0; k<SapGenConstants.galleryVect.size(); k++){
    				imageObj = null;
    				imageObj = (GalleryImageDetails) SapGenConstants.galleryVect.elementAt(k);
    				if(imageObj != null){
    					filePath = imageObj.getImageFullPath().trim();
    					newImgFile = new File(filePath);
    					SapGenConstants.showLog("Filepath : "+newImgFile);
    					if(newImgFile != null){
    	    				SapGenConstants.showLog("Inside Not null");
    	    				if(newImgFile.exists()){
    	    					boolean delVal = newImgFile.delete();
    	    					SapGenConstants.showLog("File Deleted : "+delVal);
    	    				}
    	    			}
    				}
    				newImgFile = null;
    			}
    			SapGenConstants.galleryVect.removeAllElements();
    		}
    		SapGenConstants.showLog("After Images Removal");
    	}
    	catch(Exception asf){
    		SapGenConstants.showErrorLog("Error on deleteAllImages : "+asf.toString());
    	}
    }//fn deleteAllImages
    
    private void doSendToSapAction(){
    	try{
    		if(contactId == null)
    			contactId = "";
    		
    		if(customerId == null)
    			customerId = "";
		
			if(descEditText != null)
				callDescStr = descEditText.getText().toString().trim();
			
			if(notesEditText != null)
				callNotesStr = notesEditText.getText().toString().trim();
			
			int index1 = 0;
			//if(!selected_category.equalsIgnoreCase("")){
			long smsDatTime = System.currentTimeMillis();
			Date l_date = new Date(smsDatTime);
    		Calendar c = Calendar.getInstance(); 
    		TimeZone timeZone = c.getTimeZone();
    		timez_offset = String.valueOf(timeZone.getOffset(l_date.getTime()));
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        formattedDate = sdf.format(l_date.getTime());
	        formattedTime = l_date.getHours()+":"+l_date.getMinutes()+":"+l_date.getSeconds();    	        	
	        callDurationStr = "0";
	        
			SapGenConstants.showLog("Date : "+timez_offset+" : "+formattedDate+" : "+formattedTime);
			
			index1 = selected_category.indexOf('(');
			if(index1 >= 0)
				selCatCode = selected_category.substring(index1+1, selected_category.length()-1);
			SapGenConstants.showLog("selected_category code : "+selCatCode);
				
				initActivitySoapConnection();
			/*}
			else
				SapGenConstants.showErrorDialog(this, "Select an Activity type to proceed!");*/
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in doSendToSapAction : "+sfg.toString());
    	}
    }//fn doSendToSapAction
                
    private void initActivitySoapConnection(){        
    	SoapSerializationEnvelope envelopeC = null;
    	SalesProActOutputConstraints category  = new SalesProActOutputConstraints();
    	SalesProAppCustomersConstraints customers = null;
    	String statusStr = "";
    	GalleryImageDetails imageObj = null;
    	Bitmap imgBmp = null;
    	String fullpathStr = "", encodedImageDataStr = "";
    	int arrIndex = 0;
    	try{
    		if((selectedStatusPos >= 0) && (statusTxtArr != null)){
    			if(statusTxtArr.length > selectedStatusPos)
    				statusStr = statusTxtArr[selectedStatusPos];
    			
    			SapGenConstants.showLog("Selected Status Values : "+statusStr+" : "+selected_staus);
    		}
    	}
    	catch(Exception sgg){}
    	
        try{
            SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            
            int ipDataSize, initSize = 7;
            if(SapGenConstants.galleryVect != null){
            	ipDataSize = initSize+SapGenConstants.galleryVect.size();
            	if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
            		ipDataSize = ipDataSize + SapGenConstants.selContactVect.size();
            	}
            }
            else
                ipDataSize = initSize;
            
			try {
				//Adding Calendar Event
            	if(!SapGenConstants.MOBILE_IMEI.equalsIgnoreCase(SapGenConstants.EMULATOR_IMEI)){
        			String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);    
        			SapGenConstants.showLog("Emulator/device"+android_id);
        			if (android_id == null) {     
        				// Emulator!    
        				SapGenConstants.showLog("Emulator!"+android_id);
        			}
        			else {        
        				// Device	                        				
        				SapGenConstants.showLog("Device!"+android_id);
        				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");         	
        	            String startTimeStr = start_date_value_cal +" "+ start_time_value_cal+":00";
        	            String endTimeStr = end_date_value_cal +" "+ end_time_value_cal+":00";
        	            SapGenConstants.showLog("startTimeStr: "+startTimeStr);
        	            Date startDate_Time = dateFormat.parse(startTimeStr);
        	            
        	            Date date1 = dateFormat.parse(startTimeStr);
        	            final long startTime = date1.getTime();
        	            
        	            Date date2 = dateFormat.parse(endTimeStr);
        	            final long endTime = date2.getTime();
        	                    	            

        				if(objectId != null && objectId.length() > 0){
            				eventId = AppDBOperations.getEventId(this, objectId);
        					SapGenConstants.showLog("eventId1: "+objectId);
        					SapGenConstants.showLog("eventId: "+eventId);
        	            }else{
            				eventId = AppDBOperations.getEventId(this, imageUniqueId);
        					SapGenConstants.showLog("eventId2: "+imageUniqueId);
        					SapGenConstants.showLog("eventId: "+eventId);
        	            }
        				
        				Date currentDate = new Date();
        				if (startDate_Time.before(currentDate)) {
        					SapGenConstants.showLog("Past date & time");
        				}else{
        					SapGenConstants.showLog("Future date & time");
        					if(eventId != null && eventId.length() > 0){
            					SapGenConstants.showLog("Available");
            					SapGenConstants.showLog("eventId: "+eventId);
            					CalendarConstants.editToCalendar(this, selected_category+" "+callDescStr, callNotesStr, startTime, endTime, SapGenConstants.selContactVect, eventId);        						
        					}else{
            					String eventIdVal = CalendarConstants.addToCalendar(this, selected_category+" "+callDescStr, callNotesStr, startTime, endTime, SapGenConstants.selContactVect);
            					eventId = eventIdVal;
            					SapGenConstants.showLog("Not Available");
            					SapGenConstants.showLog("eventId: "+eventId);
        					}
        				}
        			}	
            	}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			String startDateStrValue = ""; 
			startDateStrValue = SapGenConstants.getDateFormatForSAP(startYear_dp, startMonth_dp, startDay_dp, startHour_dp, startMinute_dp);
        	if(startDateStrValue == null || startDateStrValue.length() < 0 ){
        		startDateStrValue = "0000-00-00";
        	}
        	SapGenConstants.showLog("startDateStrValue:"+startDateStrValue); 
        	
        	String startHourValue = getCorrectHour_N_Time(String.valueOf(startHour_dp));
        	String startMinuteValue = getCorrectHour_N_Time(String.valueOf(startMinute_dp));        
        	String startTimeStrValue = "";
        	startTimeStrValue = startHourValue+":"+startMinuteValue+":00";
        	if(startTimeStrValue == null || startTimeStrValue.length() < 0 ){
        		startTimeStrValue = "00:00:00";
        	}
        	SapGenConstants.showLog("startTimeStrValue:"+startTimeStrValue); 
        	
        	String endDateStrValue = ""; 
        	endDateStrValue = SapGenConstants.getDateFormatForSAP(endYear_dp, endMonth_dp, endDay_dp, endHour_dp, endMinute_dp);
        	if(endDateStrValue == null || endDateStrValue.length() < 0 ){
        		endDateStrValue = "0000-00-00";
        	}
        	SapGenConstants.showLog("endDateStrValue:"+endDateStrValue); 
        	
        	String endHourValue = getCorrectHour_N_Time(String.valueOf(endHour_dp));
        	String endMinuteValue = getCorrectHour_N_Time(String.valueOf(endMinute_dp));        
        	String endTimeStrValue = "";
        	endTimeStrValue = endHourValue+":"+endMinuteValue+":00";
        	if(endTimeStrValue == null || endTimeStrValue.length() < 0 ){
        		endTimeStrValue = "00:00:00";
        	}
        	SapGenConstants.showLog("endTimeStrValue:"+endTimeStrValue);
            
            SalesOrdProIpConstraints C0[];
            C0 = new SalesOrdProIpConstraints[ipDataSize];            
            for(int i=0; i<C0.length; i++){
                C0[i] = new SalesOrdProIpConstraints(); 
            }	            
            
            //String apiName = "ACTIVITY-GENERIC-MAINTAIN";
            
            C0[0].Cdata = SapGenConstants.getApplicationIdentityParameter(this, CalendarConstants.CRTAPP_CALLING_APP_NAME);
            C0[1].Cdata = SapGenConstants.SOAP_NOTATION_DELIMITER;
            C0[2].Cdata = "EVENT[.]"+CalendarConstants.APP_EDIT_ADD_API+"[.]VERSION[.]0";
            C0[3].Cdata = "DATA-TYPE[.]ZGSXCAST_ACTVTY20[.]OBJECT_ID[.]PROCESS_TYPE[.]DESCRIPTION[.]TEXT[.]DATE_FROM[.]DATE_TO[.]TIME_FROM[.]TIME_TO[.]TIMEZONE_FROM[.]DURATION_SEC[.]CATEGORY[.]STATUS[.]STATUS_TXT30[.]STATUS_REASON";
           
            /*if(processType.length() > 0)
            	C0[4].Cdata = " ZGSXCAST_ACTVTY20[.]"+objectId+"[.]"+processType+"[.]"+callDescStr+"[.]"+callNotesStr+"[.]"+startDateStrValue+"[.]"+endDateStrValue+"[.]"+startTimeStrValue+"[.]"+endTimeStrValue+"[.]"+timez_offset+"[.]"+callDurationStr+"[.]"+selCatCode+"[.]"+statusStr+"[.]"+selected_staus+"[.]";
            else	
            	C0[4].Cdata = " ZGSXCAST_ACTVTY20[.][.][.]"+callDescStr+"[.]"+callNotesStr+"[.]"+startDateStrValue+"[.]"+endDateStrValue+"[.]"+startTimeStrValue+"[.]"+endTimeStrValue+"[.]"+timez_offset+"[.]"+callDurationStr+"[.]"+selCatCode+"[.]"+statusStr+"[.]"+selected_staus+"[.]";*/
            
            if(processType.length() > 0)
            	C0[4].Cdata = " ZGSXCAST_ACTVTY20[.]"+objectId+"[.]"+processType+"[.]"+callDescStr+"[.]"+callNotesStr+"[.]"+startDateStrValue+"[.]"+endDateStrValue+"[.]"+startTimeStrValue+"[.]"+endTimeStrValue+"[.]"+timez_offset+"[.]"+callDurationStr+"[.][.][.][.]";
            else	
            	C0[4].Cdata = " ZGSXCAST_ACTVTY20[.][.][.]"+callDescStr+"[.]"+callNotesStr+"[.]"+startDateStrValue+"[.]"+endDateStrValue+"[.]"+startTimeStrValue+"[.]"+endTimeStrValue+"[.]"+timez_offset+"[.]"+callDurationStr+"[.][.][.][.]";
            
            
            C0[5].Cdata = "DATA-TYPE[.]ZGSXCAST_CSTMRCNTCT10S[.]KUNNR[.]KUNNR_NAME1[.]PARNR[.]PARNR_NAME1[.]";
            
            category.KUNNR = "";
            category.KUNNR_NAME = "";
            category.PARNR = "";
            category.PARNR_NAME = "";            
            
            arrIndex = 6;
            if(SapGenConstants.selContactVect != null && SapGenConstants.selContactVect.size() > 0){
        		for (int pos = 0; pos < SapGenConstants.selContactVect.size(); pos++){
        			customers = new SalesProAppCustomersConstraints();
	        		String name = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getContactName();
					String id = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getContactDeviceId();
					String sapid = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getSapId();
					String cusid = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getCustomerId();
					String fname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getFName();
					String lname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getLName();
					String orgname = (String)((ContactSAPDetails)SapGenConstants.selContactVect.elementAt(pos)).getOrgName();
															
					contactId = sapid;
					customerId = cusid;
					contactFName = fname;
					contactLName = lname;
					customerName = orgname;
					String contactName = contactFName+" "+contactLName;
					
					if(pos == 0){
						category.KUNNR = customerId;
			            category.KUNNR_NAME = customerName;
			            category.PARNR = contactId;
			            category.PARNR_NAME = contactName;
					}
					
					if(objectId != null && objectId.length() > 0){
						customers.OBJECT_ID = objectId;
		            }else{
		            	customers.OBJECT_ID = imageUniqueId;
		            }
					
					customers.KUNNR = customerId;
					customers.KUNNR_NAME = customerName;
					customers.PARNR = contactId;
					customers.PARNR_NAME = contactName;
					
					boolean idExits = AppDBOperations.checkCustomerIdExists(this, contactId);		            
		            if(idExits){
		            	AppDBOperations.updateAppCusCategoryDataInToDB(this, contactId, customers);
		            }else{
		            	AppDBOperations.insertAppCusCategoryDataInToDB(this, customers);
		            }
					
	            	C0[arrIndex].Cdata = "ZGSXCAST_CSTMRCNTCT10S[.]"+customerId+"[.]"+customerName+"[.]"+contactId+"[.]"+contactName+"[.]";
	            	arrIndex = arrIndex + 1;
        		}
            }
            
            if(SapGenConstants.galleryVect != null){
	            for(int i=0; i <SapGenConstants.galleryVect.size(); i++){
	            	imageObj = null;
	            	imageObj = (GalleryImageDetails)SapGenConstants.galleryVect.elementAt(i);
	            	if(imageObj != null){
	            		imgBmp = null;
	            		imgBmp = imageObj.getImageBitmap();
	                	fullpathStr = imageObj.getImageFullPath();
	                	encodedImageDataStr = "";
	                	if(imgBmp != null)
	                		encodedImageDataStr = SapGenConstants.getImageAsEncodedDataStr(imgBmp);
	            	}
	            	C0[arrIndex].Cdata = "ZGSSMWST_ATTCHMNT01[.][.][.]"+fullpathStr+"[.]"+encodedImageDataStr;
	            	arrIndex = arrIndex + 1;
	            }
            }
                        
            if(objectId != null && objectId.length() > 0){
    			AppDBOperations.updateGalleryUniqueIdValue(this, objectId, imageUniqueId);
    			AppDBOperations.updateGallerySizeValue(this, objectId, String.valueOf(CalendarConstants.imageCountId));
                category.OBJECT_ID = objectId;
            }else{
				AppDBOperations.insertAppGalListDetDataInToDB(this, "", imageUniqueId, String.valueOf(CalendarConstants.imageCountId), eventId);
            	category.OBJECT_ID = imageUniqueId;
            }
           
            AppDBOperations.updateEventId(this, category.OBJECT_ID.toString().trim(), eventId);
            
            category.PROCESS_TYPE = processType;
            category.DESCRIPTION = callDescStr;
            category.TEXT = callNotesStr;
            category.DATE_FROM = startDateStrValue;
            category.DATE_TO = endDateStrValue;
            category.TIME_FROM = startTimeStrValue;
            category.TIME_TO = endTimeStrValue;
            category.TIMEZONE_FROM = timez_offset;
            category.DURATION_SEC = callDurationStr;
            category.CATEGORY = selCatCode;
            category.STATUS = statusStr;
            category.STATUS_TXT30 = selected_staus;
            String id = category.OBJECT_ID.toString().trim();
            boolean idExits = AppDBOperations.checkObjectIdExists(this, id);
            SapGenConstants.showLog("id: "+id);
            SapGenConstants.showLog("idExits: "+idExits);
            if(idExits){
            	AppDBOperations.updateAppCategoryDataInToDB(this, id, category);
            }else{
            	AppDBOperations.insertAppCategoryDataInToDB(this, category);
            }
            
    		SapGenConstants.showLog("imageUniqueId : "+imageUniqueId);
    		SapGenConstants.showLog("imageCountId : "+String.valueOf(CalendarConstants.imageCountId));
            
            Vector listVect = new Vector();
            for(int k=0; k<C0.length; k++){
                listVect.addElement(C0[k]);
            }
        
            request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
            envelopeC.setOutputSoapObject(request);                  
            SapGenConstants.showLog(request.toString());
            
            respType = SapGenConstants.RESP_TYPE_UPDATE;  
    		requestSoapObj = request;          
            if(isConnAvail ==  true){
            	doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);
            }
            else{
            	String refId = imageUniqueId;
            	//No network so pass the contents to the offline QueueProcessor
            	SapGenConstants.showLog("CRTAPP_PACKAGE_NAME: "+CalendarConstants.CRTAPP_PACKAGE_NAME);
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = refId+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, CalendarConstants.CRTAPP_CALLING_APP_NAME, CalendarConstants.CRTAPP_PACKAGE_NAME, refId,newLocalId, CalendarConstants.CRTAPP_CLASS_NAME, CalendarConstants.APP_EDIT_ADD_API, request, now);
	    		Intent intent = new Intent(this, CalendarLists.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);      
				intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
				startActivity(intent);
            	/*setResult(RESULT_OK);
	    		finish();*/
				//sendToQueueProcessor();
            }
        }
        catch(Exception asd){
        	SapGenConstants.showErrorLog("Error in initActivitySoapConnection : "+asd.toString());
        }
    }//fn initActivitySoapConnection
        
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
	    			if(respType == SapGenConstants.RESP_TYPE_UPDATE){
	            		updateServerResponse(resultSoap);
	    			}
	    		}
	    		else{
	    			if(respType == SapGenConstants.RESP_TYPE_UPDATE){
	    				sendToQueueProcessor();
	    			}
	    		}
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }	    
    };    
    
    private void sendToQueueProcessor(){
    	try {
    		saveToLDB();
		} catch (Exception errg) {
			SapGenConstants.showErrorLog("Error in sendToQueueProcessor : "+errg.toString());
		}
    }//fn sendToQueueProcessor
    
    private void saveToLDB(){
		try{
			if(respType == SapGenConstants.RESP_TYPE_UPDATE){
            	String refId = imageUniqueId;
            	//No network so pass the contents to the offline QueueProcessor
            	SapGenConstants.showLog("CRTAPP_PACKAGE_NAME: "+CalendarConstants.CRTAPP_PACKAGE_NAME);
            	Long now = Long.valueOf(System.currentTimeMillis());
            	String newLocalId = refId+now.toString();
            	//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(this, CalendarConstants.CRTAPP_CALLING_APP_NAME, CalendarConstants.CRTAPP_PACKAGE_NAME, refId,newLocalId, CalendarConstants.CRTAPP_CLASS_NAME, CalendarConstants.APP_EDIT_ADD_API, requestSoapObj, now);
	    		setResult(RESULT_OK);
	    		finish();
			}
		}
	    catch (Throwable e) {
	    	SapGenConstants.showErrorLog("Error in saveToLDB : "+e.toString());
	    }
	}//fn saveToLDB
            
    private void updateServerResponse(SoapObject soap){
    	try {
			boolean errorflag = false;
			String errorDesc = "";
			SapGenDocKeyOpConstraints categoryObj = null;
	        try{ 
	        	if(soap != null){                
	    			errorflag = SapGenConstants.getUpdateServerResponse(this, soap);
	        		SapGenConstants.showLog("Count : "+soap.getPropertyCount());
					SapGenConstants.soapResponse(this, soap, true);
					errorDesc = SapGenConstants.SOAP_RESP_MSG;
		            String delimeter = "[.]", result="", res="", docTypeStr = "";
		            SoapObject pii = null;
		            String[] resArray = new String[20];
		            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
		            
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
		                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_DCMNTKEY")){
		                                if(categoryObj != null)
		                                	categoryObj = null;
		                                
		                                categoryObj = new SapGenDocKeyOpConstraints(resArray);
		                                
		                                String objectIdVal = categoryObj.getObjectId().toString().trim();
		                                if(objectIdVal != null && objectIdVal.length() > 0){
			            	    			AppDBOperations.updateObjectIdValueToGal(this, objectIdVal, imageUniqueId);
			            	    			AppDBOperations.updateGallerySizeValue(this, objectIdVal, String.valueOf(CalendarConstants.imageCountId));
		                                }
		                            }
		                             
		                        }
		                        else if(j == 0){
		                            String errorMsg = pii.getProperty(j).toString();
		                            SapGenConstants.showLog("Inside J == 0 ");
		                            int errorFstIndex = errorMsg.indexOf("Message=");
		                            if(errorFstIndex > 0){
		                                int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
		                                final String taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
		                                SapGenConstants.showLog(taskErrorMsgStr);
		                                this.runOnUiThread(new Runnable() {
		                                    public void run() {
		    	                                SapGenConstants.showErrorDialog(CreateAppointment.this, taskErrorMsgStr);
		                                    }
		                                });
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
	        	SapGenConstants.showErrorLog("Error in updateServerResponse:"+sff.toString());
	        	errorflag = true;
	        } 
	        finally{
	        	if(!errorflag){
		    		//deleteAllImages();
		    		/*setResult(RESULT_OK);
		    		finish();*/
		    		Intent intent = new Intent(CreateAppointment.this, CalendarLists.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);		
					startActivity(intent);
					finish();
				}
				else{
					final String text = errorDesc;
	            	this.runOnUiThread(new Runnable() {
                        public void run() {
                            SapGenConstants.showErrorDialog(CreateAppointment.this, text);
                        }
                    });
				}
	        }
			
		} catch (Exception eaa) {
			SapGenConstants.showErrorLog("Error On updateServerResponse: "+eaa.toString());
		}
    }//fn updateServerResponse
    
    /*private void insertActivityDataIntoDB(){
    	SalesProAppCrtConstraints custAppCategory;
    	try {
			if(categoryList != null){
				for(int k=0; k<categoryList.size(); k++){
					custAppCategory = (SalesProAppCrtConstraints) categoryList.get(k);
					if(custAppCategory != null){
						AppDBOperations.insertCategoryDataInToDB(this, custAppCategory);
					}
				}
			}
		} catch (Exception ewe) {
			SapGenConstants.showErrorLog("Error On insertActivityDataIntoDB: "+ewe.toString());
		}
    }//fn insertActivityDataIntoDB
*/        
    private void launchDefaultCamera(){
    	try{
    		//_imagecapturepath = Environment.getExternalStorageDirectory()+"/afs_"+imageUniqueId+".jpg";
    		_imagecapturepath = CalendarConstants.getGalleryImagePath(imageUniqueId);
    		SapGenConstants.showLog("Image Capture path : "+_imagecapturepath);
    		File file = new File( _imagecapturepath );
		    Uri outputFileUri = Uri.fromFile( file );
    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    		//Intent cameraIntent = new Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA); 
    		startActivityForResult(cameraIntent, SapGenConstants.CAMERA_PIC_REQUEST);
    	}
    	catch(Exception sfgds){
    		SapGenConstants.showErrorLog("Error in launchDefaultCamera : "+sfgds.toString());
    	}
    }//fn launchDefaultCamera
    
    private void launchGalleryScreen(){
    	try{
    		Intent intent = new Intent(CreateAppointment.this, GenGallery.class);
    		startActivityForResult(intent, SapGenConstants.LIB_ACTION_GALLERY_SCREEN);
    	}
    	catch(Exception sfgds){
    		SapGenConstants.showErrorLog("Error in launchGalleryScreen : "+sfgds.toString());
    	}
    }//fn launchGalleryScreen
        
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
			if (requestCode == SapGenConstants.CAMERA_PIC_REQUEST) {
				switch( resultCode ){
	  		    	case 0:
	  		    		SapGenConstants.showLog( "User cancelled" );
	  		    		break;
	  		    	case -1:
	  		    		BitmapFactory.Options options = new BitmapFactory.Options();
	  			        //options.inSampleSize = 4;
	  			        options.inSampleSize = 8;
	  			        Bitmap thumbnail = BitmapFactory.decodeFile( _imagecapturepath, options ); 
	  			        if(thumbnail != null){
				    		SapGenConstants.showLog("Image : "+thumbnail.getHeight()+" : "+thumbnail.getWidth()+" : "+SapGenConstants.galleryVect.size());
				    		updateCapturedImageDetails(thumbnail);
				    	}
	  		    		break;
	  		    }	
			}
			else if(resultCode==RESULT_OK && requestCode==SapGenConstants.CONTACTLISTCLONE_LAUNCH_SCREEN){
				String appName = data.getStringExtra("app_name");
				editModeFlag = data.getBooleanExtra("editflag", false);
				vectObj1 = (ContactProVectSerializable)data.getSerializableExtra("selContacts");	
				category = (SalesProActOutputConstraints)this.getIntent().getSerializableExtra("actobj");			
				Intent intent = new Intent(this, CreateAppointment.class);
	        	intent.putExtra("editflag", editModeFlag);   
	        	if(category != null){
		        	intent.putExtra("actobj", category);	        		
	        	}  
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);      
				intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
	    		intent.putExtra("selContacts", vectObj1);
				startActivity(intent);
			}
		} catch (Exception aae) {
			SapGenConstants.showErrorLog("Error in onActivityResult : "+aae.toString());
		}
    }//fn onActivityResult
    
    private void updateCapturedImageDetails(Bitmap thumbnail){
    	try{    		
    		Long id = System.currentTimeMillis();
    		
    		SapGenConstants.showLog("Image Id " + id);
 	        SapGenConstants.showLog("Image Path " + _imagecapturepath);
 	         	        
 	        GalleryImageDetails imageObj = new GalleryImageDetails(thumbnail, id, _imagecapturepath);
 	        
 	        if(SapGenConstants.galleryVect != null)
 	        	SapGenConstants.galleryVect.addElement(imageObj);
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in updateCapturedImageDetails : "+sfg.toString());
    	}
    }//fn updateCapturedImageDetails
    
    private void getCapturedImageDetails(){
    	try{    		
    		if(SapGenConstants.galleryVect != null)
    			SapGenConstants.galleryVect.clear();
    		String path = "";
    		int imgsize = CalendarConstants.imageCountId;
    		if(imgsize > 0){
    			for(int id=1; id<=imgsize; id++){
    	    		SapGenConstants.showLog("Image Id " + id);
    	    		path = Environment.getExternalStorageDirectory() + "/Imgs/Gall/"+imageUniqueId+"_"+id+".jpg";
    	 	        File f = new File(path);
    	 	        if(f.exists()){
    	 	        	SapGenConstants.showLog("Image Path " + path);        	 	        
        				BitmapFactory.Options options = new BitmapFactory.Options();
        			    options.inSampleSize = 8;
        			        Bitmap thumbnail = BitmapFactory.decodeFile( path, options ); 
        	 	        GalleryImageDetails imageObj = new GalleryImageDetails(thumbnail, id, path);
        	 	        
        	 	        if(SapGenConstants.galleryVect != null)
        	 	        	SapGenConstants.galleryVect.addElement(imageObj);
    	 	        }else{
    	 	        	SapGenConstants.showLog("Image Path " + path);
    	 	        	SapGenConstants.showLog("File not exists! ");    	 	        	
    	 	        }
    			}
    		}

    		if(objectId != null && objectId.length() > 0){
    			AppDBOperations.updateGalleryUniqueIdValue(this, objectId, imageUniqueId);
    			AppDBOperations.updateGallerySizeValue(this, objectId, String.valueOf(CalendarConstants.imageCountId));
            }
    		
        	launchGalleryScreen();
    	}
    	catch(Exception sfg){
    		SapGenConstants.showErrorLog("Error in updateCapturedImageDetails : "+sfg.toString());
    	}
    }//fn updateCapturedImageDetails
    
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, MAIN_ID, 0, "Capture Image");
        try{
        	if(SapGenConstants.galleryVect != null){
        		menu.add(0, MAIN_IMG_ID, 1, "Show Images");
        	}
        }
        catch(Exception sfg){}
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
			case MAIN_ID: {
				launchDefaultCamera();
		        break;
			}
			case MAIN_IMG_ID: {
				getCapturedImageDetails();
		        break;
			}
	    }
		return super.onOptionsItemSelected(item);
	}
	
	//start date click listener
    private OnClickListener start_dateListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	showDialog(START_DATE_DIALOG_ID);
        }
    };
    
    //start time click listener
    private OnClickListener start_timeListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	showDialog(START_TIME_DIALOG_ID);
        }
    };

    //end date click listener
    private OnClickListener end_dateListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	showDialog(END_DATE_DIALOG_ID);
        }
    };
        
    //end time click listener
    private OnClickListener end_timeListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	showDialog(END_TIME_DIALOG_ID);
        }
    };
    	
	/* For date related methods */
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                			startDateSetListener,
                			startYear_dp, startMonth_dp-1, startDay_dp);   
            case START_TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                			startTimeSetListener, startHour_dp, startMinute_dp, false);   
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            endDateSetListener,
                            endYear_dp, endMonth_dp-1, endDay_dp);
            case END_TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                			endTimeSetListener, endHour_dp, endMinute_dp, false);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(startYear_dp, startMonth_dp-1, startDay_dp);
                break;
            case END_DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(endYear_dp, endMonth_dp-1, endDay_dp);
                break;
        }
    }
    
    private void updateDisplay_start_date() {    
    	try{
    		int month_value = startMonth + 1;
	    	String month_dec = SapGenConstants.getMonthValue(month_value); 
	    	startDay_dp = startDay;
	    	startMonth_dp = month_value;
	    	startYear_dp = startYear;
	    	start_date_value_cal = month_value+"/"+startDay+"/"+startYear;    	
	    	
	    	String taskDateStrValue = ""; 
	    	taskDateStrValue = SapGenConstants.getDateFormatForSAP(startYear_dp, startMonth_dp, startDay_dp, startHour_dp, startMinute_dp);
	    	if(taskDateStrValue.length() > 0 && taskDateStrValue != null){
	    		String dateStr = taskDateStrValue.toString();
	        	dateStr = dateStr.trim();
	        	if(!dateStr.equalsIgnoreCase("")){	                    		
	            	String newdatestr = SapGenConstants.getSystemDateFormatString(this, dateStr);
	    			start_date.setText(" "+newdatestr);
	        	}
	        	else{
	        		start_date.setText(
	        	            new StringBuilder()
	        	                    // Month is 0 based so add 1
	        	            		.append(startDay).append(" ")
	        	                    .append(month_dec).append(" ")                    
	        	                    .append(startYear).append(" "));
	        	}
	    	}
	    	else{
	    		taskDateStrValue = "";
	    		start_date.setText(
	    	            new StringBuilder()
	    	                    // Month is 0 based so add 1
	    	            		.append(startDay).append(" ")
	    	                    .append(month_dec).append(" ")                    
	    	                    .append(startYear).append(" "));
	    	}	    	
	    }
	    catch(Exception asf){
	    	SapGenConstants.showErrorLog("Error in updateDisplay_start_date : "+asf.toString());
	    }
    }//fn updateDisplay_start_date
    
    private void updateDisplay_start_time() {
    	try{
	    	startHour_dp = startHour;
	    	startMinute_dp = startMinute;
	    	start_time_value_cal = startHour+":"+startMinute;
	    	start_time.setText(
	            new StringBuilder()
			            .append(pad(startHour)).append(":")
			            .append(pad(startMinute)));
	    }
	    catch(Exception asf){
	    	SapGenConstants.showErrorLog("Error in updateDisplay_start_time : "+asf.toString());
	    }
    }//fn updateDisplay_start_time
    
    private void updateDisplay_end_date() {    	
    	try{
	    	int end_month_value = endMonth + 1;
	    	String month_dec = SapGenConstants.getMonthValue(end_month_value); 
	    	endDay_dp = endDay;
	    	endMonth_dp = end_month_value;
	    	endYear_dp = endYear;
	    	end_date_value_cal = end_month_value+"/"+endDay+"/"+endYear;    	
	    	
	    	String taskDateStrValue = ""; 
	    	taskDateStrValue = SapGenConstants.getDateFormatForSAP(endYear_dp, endMonth_dp, endDay_dp, endHour_dp, endMinute_dp);
	    	if(taskDateStrValue.length() > 0 && taskDateStrValue != null){
	    		String dateStr = taskDateStrValue.toString();
	        	dateStr = dateStr.trim();
	        	if(!dateStr.equalsIgnoreCase("")){	                    		
	            	String newdatestr = SapGenConstants.getSystemDateFormatString(this, dateStr);
	    			end_date.setText(" "+newdatestr);
	        	}
	        	else{
	        		end_date.setText(
	        	            new StringBuilder()
	        	                    // Month is 0 based so add 1
	        	            		.append(endDay).append(" ")
	        	                    .append(month_dec).append(" ")                    
	        	                    .append(endYear).append(" "));
	        	}
	    	}
	    	else{
	    		taskDateStrValue = "";
	    		end_date.setText(
	    	            new StringBuilder()
	    	                    // Month is 0 based so add 1
	    	            		.append(endDay).append(" ")
	    	                    .append(month_dec).append(" ")                    
	    	                    .append(endYear).append(" "));
	    	}
	    }
	    catch(Exception asf){
	    	SapGenConstants.showErrorLog("Error in updateDisplay_end_date : "+asf.toString());
	    }
    }//fn updateDisplay_end_date
    
    private void updateDisplay_end_time() {
    	try{
	    	endHour_dp = endHour;
	    	endMinute_dp = endMinute;
	    	end_time_value_cal = endHour+":"+endMinute;
	    	end_time.setText(
	            new StringBuilder()
			            .append(pad(endHour)).append(":")
			            .append(pad(endMinute)));
	    }
	    catch(Exception asf){
	    	SapGenConstants.showErrorLog("Error in updateDisplay_end_time : "+asf.toString());
	    }
    }//fn updateDisplay_end_time
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	
    private DatePickerDialog.OnDateSetListener startDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                	startYear = year;
                	startMonth = monthOfYear;
                	startDay = dayOfMonth;
                    updateDisplay_start_date();
                }
            };

    private TimePickerDialog.OnTimeSetListener startTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                	startHour = hourOfDay;
                	startMinute = minute;
                    updateDisplay_start_time();
                }
            };
            
    private DatePickerDialog.OnDateSetListener endDateSetListener =
		    new DatePickerDialog.OnDateSetListener() {		
		        public void onDateSet(DatePicker view, int year, int monthOfYear,
		                int dayOfMonth) {
		            endYear = year;
		            endMonth = monthOfYear;
		            endDay = dayOfMonth;
		            updateDisplay_end_date();
		        }
		    };         
		    
    private TimePickerDialog.OnTimeSetListener endTimeSetListener =
		    new TimePickerDialog.OnTimeSetListener() {		
		        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		        	endHour = hourOfDay;
		        	endMinute = minute;
		            updateDisplay_end_time();
		        }
		    };	

    private String getCorrectHour_N_Time(String valueStr){
    	String val = "";
    	try {
			if(valueStr.length() > 0){
				if(valueStr.length() == 1){
					val = "0"+valueStr;
				}else{
					val = valueStr;
				}				
			}
		} catch (Exception easf) {
			SapGenConstants.showErrorLog("Error in getCorrectHour_N_Time : "+easf.toString());
		}finally{
			if((val == null)|| (val.equalsIgnoreCase(""))){
				val = "";
    		}			
		}
    	return val;
    }//fn getCorrectHour_N_Time
    
}//End of class CreateAppointment