package com.globalsoft.SalesProCustActivityLib;

import com.globalsoft.SalesProCustActivityLib.Constraints.SalesProCustActConstraints;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class SalesProCustDetailScreen extends Activity {
	
	private TextView[] custActLblTV;
	private EditText[] custActValET;
	private TextView custNameTV, customerTV;
	
	private SalesProCustActConstraints stkCategory = null;
	private String mainContactId="", mainCustomerId="", contactFName="", contactLName="", customerName = "";
	private int dispwidth = 300;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);
			this.setTitle("Interaction Details");
			setContentView(R.layout.socustactdetail);
			
			mainContactId = this.getIntent().getStringExtra("contactId");
			mainCustomerId = this.getIntent().getStringExtra("customerId");
			if(mainCustomerId != null)
				mainCustomerId = mainCustomerId.trim();
			
			if(mainContactId != null)
				mainContactId = mainContactId.trim();
			
			contactFName = this.getIntent().getStringExtra("contactFName");
			contactLName = this.getIntent().getStringExtra("contactLName");
			customerName = this.getIntent().getStringExtra("customerName");
			
			SalesProCustActivityConstants.showLog("mainContactId: "+mainContactId);
			SalesProCustActivityConstants.showLog("mainCustomerId: "+mainCustomerId);
			
			stkCategory = (SalesProCustActConstraints) this.getIntent().getSerializableExtra("stkCategoryObj");
			
			dispwidth = SapGenConstants.getDisplayWidth(this);
			
			initLayout();
        } catch (Exception de) {
        	SalesProCustActivityConstants.showErrorLog(de.toString());
        }
    }
	
	private void initHeaderLayout(){
    	try{
    		custNameTV = (TextView)findViewById(R.id.custNameTV);	
			customerTV = (TextView)findViewById(R.id.customerTV);
			
			updateUIElements();
    	}
    	catch(Exception sfg){
    		SalesProCustActivityConstants.showErrorLog("Error in initHeaderLayout : "+sfg.toString());
    	}
    }//fn initHeaderLayout
    
    private void updateUIElements(){
        try{
        	String contact = contactFName.trim()+" "+contactLName.trim()+" ("+mainContactId.trim()+")";
        	String customer = customerName.trim()+" ("+mainCustomerId.trim()+")";        	       	
        	
        	if(custNameTV != null)
        		custNameTV.setText(" :   "+customer); 
        	
        	if(customerTV != null)
        		customerTV.setText(" :   "+contact);  
        }
        catch(Exception asf){
        	SalesProCustActivityConstants.showErrorLog("Error in updateUIElements : "+asf.toString());
        }
    }//fn updateUIElements
	
	private void initLayout(){
		try {
			initHeaderLayout();
			
			int cols = 9;
			custActLblTV = new TextView[cols];
			custActValET = new EditText[cols];
			
			try{
				TableLayout tl = (TableLayout)findViewById(R.id.custactdettbllayout1);
				TableRow tr1 = new TableRow(this);
				tr1.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				int labelWidth = 100, editWidth = 200;
				if(dispwidth > 0){
					labelWidth = dispwidth-editWidth;
					if(labelWidth < 100)
						labelWidth = 100;
					else if(labelWidth > 150)
						labelWidth = 150;
					
					editWidth = dispwidth-labelWidth;
					if(editWidth < 160)
						editWidth = 160;
					else if(editWidth > 250)
						editWidth = 250;
					
					if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
						labelWidth = 175;
						editWidth = 300;
					}
				}
				
				LinearLayout.LayoutParams linparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				linparams.topMargin = 5; 
				linparams.bottomMargin = 5; 
				
				for(int i=0; i<cols; i++){
					tr1 = new TableRow(this);
		            	
					custActLblTV[i] = new TextView(this);
					custActValET[i] = new EditText(this);
					
					custActLblTV[i].setText("");
					custActLblTV[i].setPadding(5,5,5,5); 
					custActLblTV[i].setMinWidth(100);
					custActLblTV[i].setWidth(labelWidth);
					    					  					
					custActValET[i].setText("");
					custActValET[i].setPadding(5,5,5,5);
					custActValET[i].setWidth(editWidth);
					
					
					tr1.addView(custActLblTV[i]);
					tr1.addView(custActValET[i]);
					tr1.setLayoutParams(linparams);
					
					
					tl.addView(tr1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				}
				
				SalesProCustActivityConstants.showLog("Textview width : "+custActLblTV[0].getWidth());
				SalesProCustActivityConstants.showLog("EditText width : "+custActValET[0].getWidth());
				
				custActLblTV[0].setText("Document : ");
				custActLblTV[1].setText("Posted On : ");
				custActLblTV[2].setText("Status : ");
				custActLblTV[3].setText("Key Date : ");
				custActLblTV[4].setText("");
				custActLblTV[5].setText("Time Zone : ");
				custActLblTV[6].setText("Brief : ");
				custActLblTV[7].setText("Detail : ");
				custActLblTV[8].setText("Category : ");
				
				getCustActivityDetails();
			}catch(Exception sf){}
		}catch (Exception ssdf) {
			SalesProCustActivityConstants.showErrorLog("Error in initLayout : "+ssdf.toString());
		}
	}//fn initLayout
	
	
	private void getCustActivityDetails(){
        try{
            if(stkCategory != null){
                for(int k1=0; k1<custActValET.length; k1++){
                    if(custActValET[k1] != null){
                        switch(k1){
                            case 0:
                            	custActValET[k1].setText(stkCategory.getObjectID().trim()+" "+stkCategory.getDocumentTypeDesc().trim()+" ("+stkCategory.getProcessType().trim()+")");
                                break;
                            case 1:
                            	custActValET[k1].setText(SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", stkCategory.getPostingDate().trim()));
                                break;
                            case 2:
                            	custActValET[k1].setText(stkCategory.getStatus().trim());
                                break;
                            case 3:
                            	custActValET[k1].setText(SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", stkCategory.getDateFrom().trim())+" "+stkCategory.getTimeFrom().trim()+" - ");
                                break;
                            case 4:
                            	custActValET[k1].setText(SapGenConstants.getSystemDateFormat(this, "yyyy-MM-dd", stkCategory.getDateTo().trim())+" "+stkCategory.getTimeTo().trim());
                                break;
                            case 5:
                            	custActValET[k1].setText(stkCategory.getTimeZoneFrom().trim());
                                break;
                            case 6:
                            	custActValET[k1].setText(stkCategory.getDescription().trim());
                                break;
                            case 7:
                            	custActValET[k1].setText(stkCategory.getText().trim());
                                break;
                            case 8:
                            	custActValET[k1].setText(stkCategory.getCategory().trim()+" "+stkCategory.getDocumentTypeDesc().trim());
                                break;
                        }
                        custActValET[k1].setEnabled(false);
                    }
                }
            }
        }
        catch(Exception adf){
        	SalesProCustActivityConstants.showErrorLog("Error in getCustActivityDetails : "+adf.toString());
        }
    }//fn getCustActivityDetails
	
}//End of class SalesProCustDetailScreen

