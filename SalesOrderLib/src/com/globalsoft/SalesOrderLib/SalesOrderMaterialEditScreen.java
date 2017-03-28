package com.globalsoft.SalesOrderLib;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProCustConstraints;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

	public class SalesOrderMaterialEditScreen extends Activity {
		
		private TextView custNameTV, custAddressTV;
		public TextView reqBy_date, reqBy_time;
		private EditText customerET, soValueET;	
		private ImageButton custbackbtn, custSearchBtn;
		private String custSelID="";
		private int custpos;
		private LinearLayout searchLinear, headerLinear;
		 private ArrayList custArrayList = new ArrayList();
		 
		 //date and time
		 private int startYear;
		    private int startMonth;
		    private int startDay;
		    private int startHour;
		    private int startMinute;
		    
		    static final int START_DATE_DIALOG_ID = 1;
		    static final int START_TIME_DIALOG_ID = 2;    
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	try{
		SapGenConstants.setWindowTitleTheme(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.material_edit); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText("Sales Order Material Edit Screen");

		SalesOrderConstants.APPLN_NAME_STR = this.getIntent().getStringExtra("app_name");
		if(SalesOrderConstants.APPLN_NAME_STR == null || SalesOrderConstants.APPLN_NAME_STR.length() == 0){
			SalesOrderConstants.APPLN_NAME_STR = "SALESPRO";
		}
		
		custSelID = this.getIntent().getStringExtra("custIdStr");
		custpos=this.getIntent().getIntExtra(null, custpos);
		custArrayList= this.getIntent().getStringArrayListExtra("custDetArr");
		showCustomerHeaderInfo(custSelID,custpos);
		
		int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
		if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}			
		initLayout();
	}catch (Exception de) {
    	SalesOrderConstants.showErrorLog(de.toString());
    }
	
	}//onCreate

	

	private void initLayout() {
		try{
			setContentView(R.layout.material_edit);
			
			custNameTV = (TextView) findViewById(R.id.custNameTV);
			custAddressTV = (TextView) findViewById(R.id.custAddressTV);
			
			soValueET = (EditText) findViewById(R.id.soValueET);
			
			reqBy_date = (TextView) findViewById(R.id.req_date);
			reqBy_date.setOnClickListener(reqBy_dateListener);
	        
			reqBy_time = (TextView) findViewById(R.id.req_time);
			reqBy_time.setOnClickListener(reqBy_timeListener);
			
			custbackbtn = (ImageButton) findViewById(R.id.custbackbtn);
			custbackbtn.setOnClickListener(custback_btnListener);
			
				searchLinear = (LinearLayout) findViewById(R.id.sosearchlinear);
				headerLinear = (LinearLayout) findViewById(R.id.socrtheaderlinear);
				headerLinear.setVisibility(View.GONE);
				
				final Calendar c = Calendar.getInstance();
		        //initialize for start date
		        startYear = c.get(Calendar.YEAR);
		        startMonth = c.get(Calendar.MONTH);
		        startDay = c.get(Calendar.DAY_OF_MONTH);
		        startHour = c.get(Calendar.HOUR_OF_DAY);
		        startMinute = c.get(Calendar.MINUTE);
		        updateDisplay_start_date();
		        updateDisplay_start_time();
		        
		}catch(Exception sfgh1){
    		SalesOrderConstants.showErrorLog("Error in initLayout : "+sfgh1.toString());
		}
		
	}//initLayout
	
	private void showCustomerHeaderInfo(String custSelID, int custpos2) {
		try{
    		SalesOrdProCustConstraints empObj = null;
    		String customerIdStr="",custDetStr="",custAddStr="";
    		if(!custSelID.equalsIgnoreCase("") && (custpos2 >= 0)){
    			 if((custArrayList != null) && (custpos2 < custArrayList.size())){
    				 empObj = (SalesOrdProCustConstraints)custArrayList.get(custpos2);
                     if(empObj != null){
                    	 customerIdStr = empObj.getCustomerNo().trim();
                    	 custDetStr = empObj.getName().trim()+" ("+customerIdStr+")";
                    	 custAddStr = empObj.getCity().trim()+", "+empObj.getRegion().trim()+", "+empObj.getCountry().trim();
                    	 
                    	 if(searchLinear != null)
                    		 searchLinear.setVisibility(View.GONE);
                    	 
                    	 if(headerLinear != null)
                    		 headerLinear.setVisibility(View.VISIBLE);
                    	 
                    	 if(custNameTV != null)
                    		 custNameTV.setText(custDetStr);
                    	 
                    	 if(custAddressTV != null)
                    		 custAddressTV.setText(custAddStr);
                    	 
                    	 if(soValueET != null)
                    		 soValueET.setEnabled(false);
                     }
    			 }
    		}
    	}
    	catch(Exception sfgh){
    		SalesOrderConstants.showErrorLog("Error in showCustomerHeaderInfo : "+sfgh.toString());
    	}
		
	}
    
	 private OnClickListener reqBy_dateListener = new OnClickListener(){
	        public void onClick(View v){
	        	showDialog(START_DATE_DIALOG_ID);
	        }
	    };
	    
	    private OnClickListener reqBy_timeListener = new OnClickListener(){
	        public void onClick(View v){
	        	showDialog(START_TIME_DIALOG_ID);
	        }
	    };
	    
	    private OnClickListener custback_btnListener = new OnClickListener() {
	        public void onClick(View v) {
	        	hideCustomerHeaderInfo();
	        }
	    };
	    
	    
	    protected Dialog onCreateDialog(int id) {
	        switch (id) {
	            case START_DATE_DIALOG_ID:
	                return new DatePickerDialog(this,
	                			startDateSetListener,
	                			startYear, startMonth, startDay);   
	            case START_TIME_DIALOG_ID:
	                return new TimePickerDialog(this,
	                			startTimeSetListener, startHour, startMinute, false);   
	        }
	        return null;
	    }
	    
	    protected void onPrepareDialog(int id, Dialog dialog) {
	        switch (id) {
	            case START_DATE_DIALOG_ID:
	                ((DatePickerDialog) dialog).updateDate(startYear, startMonth, startDay);
	                break;
	        }
	    }    

	    private void updateDisplay_start_date() {    	
	    	int month_value = startMonth + 1;
	    	String month_dec = getMonthValue(month_value);    	
	    	reqBy_date.setText(
	            new StringBuilder()
	                    // Month is 0 based so add 1
	            		.append(startDay).append(" ")
	                    .append(month_dec).append(" ")                    
	                    .append(startYear).append(" "));
	    }
	    
	    private void updateDisplay_start_time() {
	    	reqBy_time.setText(
	            new StringBuilder()
			            .append(pad(startHour)).append(":")
			            .append(pad(startMinute)));
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
	            
	            private static String pad(int c) {
	                if (c >= 10)
	                    return String.valueOf(c);
	                else
	                    return "0" + String.valueOf(c);
	            }
	            
	            public String getMonthValue(int month_value){
	            	String month_dec = null;
	            	if(month_value == 1){
	            		month_dec = "Jan";
	            	}
	            	else if(month_value == 2){
	            		month_dec = "Feb";
	            	}
	            	else if(month_value == 3){
	            		month_dec = "Mar";
	            	}
	            	else if(month_value == 4){
	            		month_dec = "Apr";
	            	}
	            	else if(month_value == 5){
	            		month_dec = "May";
	            	}
	            	else if(month_value == 6){
	            		month_dec = "Jun";
	            	}
	            	else if(month_value == 7){
	            		month_dec = "Jul";
	            	}
	            	else if(month_value == 8){
	            		month_dec = "Aug";
	            	}
	            	else if(month_value == 9){
	            		month_dec = "Sep";
	            	}
	            	else if(month_value == 10){
	            		month_dec = "Oct";
	            	}
	            	else if(month_value == 11){
	            		month_dec = "Nov";
	            	}
	            	else if(month_value == 12){
	            		month_dec = "Dec";
	            	}
	            	return month_dec;
	            }
	            
	            private void hideCustomerHeaderInfo(){
	            	try{
	            		 if(searchLinear != null)
	                		 searchLinear.setVisibility(View.VISIBLE);
	                	 
	                	 if(headerLinear != null)
	                		 headerLinear.setVisibility(View.GONE);
	                	 
	                	 if(customerET != null)
	                		 customerET.setText("");
	                	 
	                	 /*custDetStr = "";
	             		 custAddStr = "";
	             		 customerIdStr = "";*/
	            	}
	            	catch(Exception sfgh){
	            		SalesOrderConstants.showErrorLog("Error in hideCustomerHeaderInfo : "+sfgh.toString());
	            	}
	            }//fn hideCustomerHeaderInfo
	            
}//SalesOrderMaterialEditScreen
