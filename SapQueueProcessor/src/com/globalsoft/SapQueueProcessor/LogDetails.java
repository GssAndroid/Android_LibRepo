package com.globalsoft.SapQueueProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessor.Utils.SapQueueProcessorConstants;

public class LogDetails extends Activity {
	private WebView myWebView;	
	private String fileName = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.logdetails);	 
			
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.SCR_LOG_DETAILS_TITLE));

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
	        	 
	        fileName = this.getIntent().getStringExtra("filename");
	        myWebView = (WebView) findViewById(R.id.wv);
	        myWebView.getSettings().setJavaScriptEnabled(true);
	        StringBuilder sb = new StringBuilder("<html><head><title>Log Details</title></head><body>");
	        sb.append(readTextFile(fileName));
	        sb.append("</body></html>");
	        myWebView.loadData(sb.toString(), "text/html", "UTF-8");

	    } catch (Exception de) {
	    	SapGenConstants.showErrorLog("Error in About screen: "+de.toString());
	    }
    }//
    
    public String readTextFile(String filename) {
    	String aBuffer = "";
		try {
			File f = new File(SapQueueProcessorConstants.LOG_FILE_PATH+filename); 
			FileInputStream fIn;
			String aDataRow = "";
			aBuffer = "";
			fIn = new FileInputStream(f);		
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			while ((aDataRow = myReader.readLine()) != null) 
			{
			    aBuffer += aDataRow ;
			}
			myReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aBuffer;
    }
}
