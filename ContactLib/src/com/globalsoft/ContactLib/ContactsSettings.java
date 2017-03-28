package com.globalsoft.ContactLib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class ContactsSettings extends Activity {
	public static RadioButton enterrb, perrb;	//Filters setting value
	public Button saveBtn;
	/** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.settings); 
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.CONTACTPRO_SETTINGS_TITLE_LBL));

			int dispwidth = SapGenConstants.getDisplayWidth(this);	
			if(dispwidth > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}

			enterrb = (RadioButton) findViewById(R.id.enterrb);
			perrb = (RadioButton) findViewById(R.id.perrb);
	        saveBtn = (Button) findViewById(R.id.savebtn);
	        saveBtn.setOnClickListener(saveBtnListener);
			
			defaultSettings();
        } catch (Exception de) {
        	ContactsConstants.showErrorLog("Error in ContactsSettings Screen: "+de.toString());
        }
    }	
    
    public void defaultSettings()
    {               
    	try{ 
        	SapGenConstants.showLog("enterValue: "+SapGenConstants.enterValue);
        	SapGenConstants.showLog("enterNpersValue: "+SapGenConstants.enterNpersValue);
    		
    		if(SapGenConstants.enterValue){
        		enterrb.setChecked(true);
        		perrb.setChecked(false);  
			}else if(SapGenConstants.enterNpersValue){
	    		enterrb.setChecked(false);
	    		perrb.setChecked(true);           					
			}else{
        		enterrb.setChecked(true);
        		perrb.setChecked(false);  
        		SapGenConstants.enterValue = true;
			}
        }catch(Exception eff) {
        	ContactsConstants.showErrorLog("Error in defaultSettings Screen: "+eff.toString());
        }         
    }//End of fn defaultSettings
    
    private OnClickListener saveBtnListener = new OnClickListener(){
        public void onClick(View v) {
        	SapGenConstants.showLog("Save btn clicked");
        	saveAction();
        }
    };
    
    private void saveAction(){
    	try{ 
        	setEnterValue(enterrb.isChecked());
            setenterNpersValue(perrb.isChecked());
    		Intent intent = this.getIntent();
    		setResult(RESULT_OK,intent);
			finish();
        }catch(Exception eff) {
        	ContactsConstants.showErrorLog("Error in saveAction Screen: "+eff.toString());
        } 
    }//End of fn saveAction
    
    /* Enterprise checkbox value set and get Methods start */
    public static boolean getEnterValue()
    {
        return SapGenConstants.enterValue;
    }
    
    public void setEnterValue(boolean enterValue_set)
    {
    	SapGenConstants.enterValue = enterValue_set;
    }
    
    /* personal checkbox value set and get Methods start */
    public static boolean getenterNpersValue()
    {
        return SapGenConstants.enterNpersValue;
    }
    
    public void setenterNpersValue(boolean enterNpersValue_set)
    {
    	SapGenConstants.enterNpersValue = enterNpersValue_set;
    }
}
