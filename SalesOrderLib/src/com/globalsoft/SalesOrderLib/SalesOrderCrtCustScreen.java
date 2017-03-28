package com.globalsoft.SalesOrderLib;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.globalsoft.SalesOrderLib.Constraints.SalesOrdProHeadOpConstraints;
import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class SalesOrderCrtCustScreen extends ListActivity { 
		
	private ImageButton  showBackbtn;
	private RelativeLayout selectrelativeLT;
	private Button showDoneBtn;
	private ListView listView;
	
    private String[] custDetArr; 
    private String custIdStr = "";
    private int selCustPos = 0,custFlag=0;
    private boolean isAvailFlag = false;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SapGenConstants.setWindowTitleTheme(this);
        /*setTitle("Select Customer");
        setContentView(R.layout.salesordercust);*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.salesordercust); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText("Select Customer");

		int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
		if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}			
        initLayout();
        try{
        	custDetArr = this.getIntent().getStringArrayExtra("custDetArr");
        	isAvailFlag = this.getIntent().getBooleanExtra("isAvailable", false);        		
        	SalesOrderConstants.showLog("isAvailFlag : "+isAvailFlag);       		
        	if(custDetArr != null){
	        	if(custDetArr.length > 0){
	        		prefillCustomerData();
	        	}
        	}
        }
        catch(Exception sdggh){
        	SalesOrderConstants.showErrorLog("Error in onCreate : "+sdggh.toString());
        }
    }
    
    private void initLayout(){
		try{			
			/*showBackbtn = (ImageButton) findViewById(R.id.showcustSearchbtn);
			showBackbtn.setOnClickListener(showsrch_btnListener); 
			
			showDoneBtn = (Button) findViewById(R.id.showDoneBtn);*/
			//showDoneBtn.setOnClickListener(showDoneBtnListener); 
			
			selectrelativeLT = (RelativeLayout) findViewById(R.id.showrelativeLT);
			selectrelativeLT.setVisibility(View.GONE);
			
			//listView = (ListView)findViewById(android.R.id.list);
			listView = getListView();
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			//listView.setOnItemSelectedListener(listItemSelectListener);
			listView.setItemsCanFocus(false);
		}
		catch(Exception sfg){
			SalesOrderConstants.showErrorLog("Error in initLayout : "+sfg.toString());
		}
	}//fn initLayout
    
    private void setWindowTitle(final String title){
    	try{
    		this.setTitle(title);
    	}
    	catch(Exception sdf){
    		SalesOrderConstants.showErrorLog("Error in setWindowTitle : "+sdf.getMessage());
    	}
    }//fn setWindowTitle
    
    /*OnItemSelectedListener listItemSelectListener = new OnItemSelectedListener() {
    	
    	@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
    		
    		selCustPos = arg2;
    		doCustomerSelectAction();
    		
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}		
	};*/
	
	
    private void onClose(boolean doneFlag){
    	try {
			//System.gc();
    		if(doneFlag == true){
				Intent addintent = new Intent(this, SalesOrderCrtCustScreen.class);
				addintent.putExtra("custIdStr", custIdStr);
				addintent.putExtra("selCustPos", selCustPos);
				setResult(RESULT_OK, addintent); 
    		}
			this.finish();
		} catch (Exception e) {
			SalesOrderConstants.showErrorLog("Error on onClose "+e.toString());
		}
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
		selCustPos = position;
		doCustomerSelectAction();
		SalesOrderConstants.showLog("Selected item pos : "+selCustPos+" : "+l.getItemAtPosition(selCustPos));
	}
    
    /*private OnClickListener showDoneBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SalesOrderConstants.showLog("Done btn clicked");
        	doCustomerSelectAction();
        }
    };*/
    
    private void doCustomerSelectAction(){
    	String item = "";    	
    	try{
        	if(listView != null){
        		item = (String) listView.getItemAtPosition(selCustPos);
				SalesOrderConstants.showLog("Selected item : "+item+" : "+selCustPos);
				if(item != null){
            		int index = item.lastIndexOf(":");
            		custIdStr = item.substring(index+1);
            		custIdStr = custIdStr.trim();
        			SalesOrderConstants.showLog("custIdStr : "+custIdStr);
            	}
        	}
        	
        	if(!item.equalsIgnoreCase("")){
        		onClose(true);
        	}
        	else
        		SalesOrderConstants.showErrorDialog(SalesOrderCrtCustScreen.this, "Select a Customer!");
    	}
    	catch(Exception ghh){
    		SalesOrderConstants.showErrorLog("Error in : "+ghh.toString());
    	}
    }//fn doCustomerSelectAction
    
    
    private OnClickListener showsrch_btnListener = new OnClickListener() {
        public void onClick(View v) {        	
        	//Back Btn is clicked do the required action
        	onClose(false);
        }
    };
    
    
    private void prefillCustomerData(){
    	try{
			if(custDetArr != null){
	        	if(selectrelativeLT != null)
	        		selectrelativeLT.setVisibility(View.VISIBLE);
	        	
	        	if(showDoneBtn != null)
	        		showDoneBtn.setVisibility(View.VISIBLE);
				
	        	//listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, custDetArr));
	        	listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, custDetArr){ 
            	    @Override 
            	    public View getView(int position, View convertView, ViewGroup parent) { 
            	        TextView textView = (TextView) super.getView(position, convertView, parent);     	            	 
            	        textView.setTextColor(SalesOrderCrtCustScreen.this.getResources().getColor(R.color.bluelabel));    	            	 
            	        return textView; 
            	    } 
            	}); 
			}
			
			setWindowTitle("Select Customer");
			
			if(isAvailFlag == true){
				if(selCustPos < 0)
					selCustPos = 0;
				
				doCustomerSelectAction();
			}
			
    	}
    	catch(Exception adf){
    		SalesOrderConstants.showErrorLog("On prefillCustomerData : "+adf.toString());
    	}
    }//fn prefillCustomerData
    
    
}//End of class SalesOrderCrtCustScreen
