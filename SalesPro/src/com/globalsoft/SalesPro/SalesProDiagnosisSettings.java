package com.globalsoft.SalesPro;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.globalsoft.SapLibSoap.Utils.SapGenConstants;


public class SalesProDiagnosisSettings extends Activity{
	private CheckBox switchStatus;
	private ToggleButton toggleButton1, toggleButton2;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			SapGenConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.salespro_settings_view); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			String titleStr = "Setting";
			myTitle.setText(titleStr);

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
			toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);
			//toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);		
			toggleButton1.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View v) {
		 
					if (toggleButton1.isChecked()) 
				    {
						SapGenConstants.DIOG_FLAG=1;
						/*SharedPreferences sharedPreferences = getSharedPreferences("tgpref", 0);    
						SharedPreferences.Editor editor = sharedPreferences.edit();
		                editor.putBoolean("tgpref", true); // value to store
		                editor.commit();*/
				    }
				    else
				    {
				    	SapGenConstants.DIOG_FLAG=0;
				    	/*SharedPreferences sharedPreferences = getSharedPreferences("tgpref", 0);    
						SharedPreferences.Editor editor = sharedPreferences.edit();
		                editor.putBoolean("tgpref", false); // value to store
		                editor.commit();*/
				    }
					
				  /* StringBuffer result = new StringBuffer();
				   result.append("toggleButton1 : ").append(toggleButton1.getText());
				   //result.append("\ntoggleButton2 : ").append(toggleButton2.getText());
				   String switchStr = result.toString();
				   SalesOrderConstants.showLog("result "+switchStr);
					if(switchStr=="ON"){
						ServiceProConstants.DIOG_FLAG=1;
					}else if(switchStr=="OFF"){
						ServiceProConstants.DIOG_FLAG=0;
					}
				   Toast.makeText(ServiceProDiognosisSettings.this, result.toString(),
					Toast.LENGTH_SHORT).show();*/		 
				}
		 
			});
			/*SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			boolean tgpref = preferences.getBoolean("tgpref", true);  //default is true
*/			if (SapGenConstants.DIOG_FLAG==1) //if (tgpref) may be enough, not sure
			{
				toggleButton1.setChecked(true);
			}
			else
			{
				toggleButton1.setChecked(false);
			}
			/*switchStatus=(CheckBox)findViewById(R.id.ckbox);
			
			switchStatus.setOnCheckedChangeListener(new OnCheckedChangeListener(){					
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {					
						SalesOrderConstants.showLog("boolean "+arg1);
						//CheckBox cb = (CheckBox) arg0 ; 
						if(arg1==true){
							ServiceProConstants.DIOG_FLAG=1;
						}					             							
						else{
							ServiceProConstants.DIOG_FLAG=0;
						}					             							
						//SalesOrderConstants.showLog("CompoundButton "+arg0);
						if(!selchkboxlist.contains(pId)){
				            selchkboxlist.add(pId);
		        		}
					}
				}); */
		} catch (Exception de) {
			SapGenConstants.showErrorLog("Error in onCreate: "+de.toString());
	    }
		
	
	}//onCreate
}//
