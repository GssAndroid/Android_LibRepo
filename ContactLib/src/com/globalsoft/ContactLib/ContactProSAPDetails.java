package com.globalsoft.ContactLib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ContactProSAPDetails extends Activity {
	private int selId = 0;
	private ContactProSAPCPersistent contactProCusDbObj = null;
	private String sapDevId = "", sapCustomerId = "", sapFname = "", sapLName = "";
	public TextView devidvalue, devcusidvalue, errorLbl;
	private TextView devidTV, devcusidTV;
	private LinearLayout error_header_linear;
	
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);
			//setContentView(R.layout.sapdetails);		
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.sapdetails); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.app_name));

			int dispwidth = SapGenConstants.getDisplayWidth(this);	
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			devidvalue = (TextView) findViewById(R.id.devidvalue);
			devcusidvalue = (TextView) findViewById(R.id.devcusidvalue);
		    
			selId = this.getIntent().getIntExtra("selId", -1);
			if(selId != -1){
				if(contactProCusDbObj == null){
					contactProCusDbObj = new ContactProSAPCPersistent(ContactProSAPDetails.this);
				}
				contactProCusDbObj.getContactDetails(String.valueOf(selId));
	    		contactProCusDbObj.closeDBHelper();
	    		sapFname = ContactsConstants.CONTACTSAPCUSFNAME;
	    		sapLName = ContactsConstants.CONTACTSAPCUSLNAME;
	    		ContactsConstants.showLog("first name and last name for title \n"+sapFname+" "+sapLName);
	    		setTitle(getString(R.string.detail_name)+" "+sapFname+" "+sapLName);
	    		myTitle.setText(getString(R.string.detail_name)+" "+sapFname+" "+sapLName);
	    		
	    		sapDevId = ContactsConstants.CONTACTSAPID;
	    		sapCustomerId = ContactsConstants.ONTACTSAPCUSID;
	    		devidvalue.setText(sapDevId);
	    		devcusidvalue.setText(sapCustomerId);
			}
			
			try{
				String errMsg = this.getIntent().getStringExtra("errMsg");
				if(errMsg != null && errMsg.length() > 0){
					error_header_linear = (LinearLayout) findViewById(R.id.error_header_linear);
					error_header_linear.setVisibility(View.VISIBLE);
					errorLbl = (TextView) findViewById(R.id.errorLbl);		
					errorLbl.setText("Message from last sync: \n"+errMsg);		
					ContactsConstants.showLog("Message from last sync: \n"+errMsg);
				}
			}
			catch (Exception de) {
				ContactsConstants.showErrorLog("Error in errMsg:"+de.toString());
	        }
			
			//int dispwidth = SapGenConstants.getDisplayWidth(this);
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				devidvalue.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
				devcusidvalue.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
				
				devidTV = (TextView) findViewById(R.id.devidtile);
				devidTV.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
				
				devcusidTV = (TextView) findViewById(R.id.devcusidtile);
				devcusidTV.setTextSize(SapGenConstants.TEXT_SIZE_LABEL);
			}
        } catch (Exception de) {
        	ContactsConstants.showErrorLog(de.toString());
        }
    }	
}