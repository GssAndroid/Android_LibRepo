package com.globalsoft.SalesPro;

import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class SalesProEmailActivity extends Activity{
	private TextView to,subject,message;
	private EditText email,subjct,body;	
	private static final int MENU_SEND = Menu.FIRST;
	private static final int MENU_CLEAR = Menu.FIRST+2;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SalesOrderProConstants.setWindowTitleTheme(this);
        /*setTitle(R.string.SALESORDPRO_PLIST_TITLE1);
        setContentView(R.layout.pricelistmain);*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
        setContentView(R.layout.email); 
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
		TextView myTitle = (TextView) findViewById(R.id.myTitle);
		myTitle.setText(getResources().getString(R.string.SALESPRO_EMAIL));

		int dispwidth = SalesOrderProConstants.getDisplayWidth(this);
		SalesOrderProConstants.showLog("dispwidth : "+dispwidth);
		if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
			myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
		}
        initLayout();
    }//oncreate	
	
	private void initLayout(){		 
		/* to = (TextView) findViewById(R.id.customerTV);
		 subject = (TextView) findViewById(R.id.customerTV);
		 message = (TextView) findViewById(R.id.customerTV);*/
		 
		 email = (EditText) findViewById(R.id.toEmail);
		 subjct = (EditText) findViewById(R.id.subject);
		 body = (EditText) findViewById(R.id.emailBody);
		 
	}//initLayout
    
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SEND, 0, "Send");		
		menu.add(0, MENU_CLEAR, 0, "Clear");
		return true;
	}	 
	 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CLEAR:
			email.setText("");
			subjct.setText("");
			body.setText("");
			break;
		case MENU_SEND:
			String to = email.getText().toString();
			String subject = subjct.getText().toString();
			String message = body.getText().toString();
 
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			email.putExtra(Intent.EXTRA_SUBJECT, subject);
			email.putExtra(Intent.EXTRA_TEXT, message);
 
			// need this to prompts email client only
			email.setType("message/rfc822");
 
			startActivity(Intent.createChooser(email, "Choose an Email client"));
 
			break;
		}
		return true;
	}	    
}//SalesProEmailActivity