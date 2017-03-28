package com.globalsoft.SalesPro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.globalsoft.SalesOrderLib.Utils.SalesOrderConstants;
import com.globalsoft.SalesPro.Constraints.SalesOrdProMattConstraints;
import com.globalsoft.SalesPro.Database.PriceListCP;
import com.globalsoft.SalesPro.Database.PriceListDBOperations;
import com.globalsoft.SalesPro.Database.SalesProPriceListDB;
import com.globalsoft.SalesPro.PriceListMainTablet.CustomerListAdapter;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SalesProCustActivityLib.Utils.SalesProCustActivityConstants;
import com.globalsoft.SapLibActivity.ActivityListForPhone;
import com.globalsoft.SapLibActivity.Contraints.SalesOrdProIpConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActCrtConstraints;
import com.globalsoft.SapLibActivity.Contraints.SalesProActStatusConstraints;
import com.globalsoft.SapLibActivity.Database.ActDBOperations;
import com.globalsoft.SapLibActivity.Utils.CrtGenActivityConstants;
import com.globalsoft.SapLibSoap.SoapConnection.StartNetworkTask;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/*! Starting of About class */
public class About extends Activity {  	
	private ImageButton diogbtn,settingsBtn,skype,email;
	private SoapObject requestSoapObj = null;
	String[] settingArray =null;
	private StartNetworkTask soapTask = null;
	private final Handler ntwrkHandler = new Handler();
	private SoapObject resultSoap = null;
	private ProgressDialog pdialog = null;
	private ArrayList priceList =new ArrayList();
	private String finalString2=" ",linkText="";
	private SOCustomerListAdapter SOCustomerListAdapter;
	//<private ServiceProSettingsAdapter ServiceProSettingsAdapter;	
	private AlertDialog alertDialog;
	private AlertDialog.Builder builder;
	String  last = "", urlName = "", androidOS = "", manufacturer = "", editionStr = "",imeno =""; 		
	Elements name, edition, device, version, deviceType, server, url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.aboutview);	       
	        WebView wv;	        
	        TextView tv;
	        wv = (WebView) findViewById(R.id.wv);
	        wv.getSettings().setJavaScriptEnabled(true);
	        tv = (TextView) findViewById(R.id.imenoids);	 
	         imeno = SapGenConstants.getMobileIMEI(this);
	        wv.loadUrl("file:///android_asset/about.html");//wrking	
	        AssetManager assetManager = getAssets();
    		assetManager.open("about.html");
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
	        String text1 = " GDID : "+imeno+"\n Server: "+urlName;
	        tv.setText(text1);   
	       // wv.loadUrl("file:///android_asset/about.html?id="+imeno); 
	        wv.loadUrl("file:///android_asset/about.html");
	       /* diaogButton=(Button)findViewById(R.id.diogBtn);
	        diaogButton.setOnClickListener(diaog_btnListener); */
	        email=(ImageButton)findViewById(R.id.email);
	        email.setOnClickListener(email1_btnListener); 	 
			skype=(ImageButton)findViewById(R.id.chat);
			skype.setOnClickListener(skype_btnListener); 	 
	        diogbtn=(ImageButton)findViewById(R.id.diogBtns);
			diogbtn.setOnClickListener(diogbtn_btnListener); 	  
			settingsBtn=(ImageButton)findViewById(R.id.settings2);
			settingsBtn.setOnClickListener(setting_btnListener); 	  
	    } catch (Exception de) {
	    	SapGenConstants.showErrorLog("Error in About screen: "+de.toString());
	    }
    }
   /* private OnClickListener diaog_btnListener = new OnClickListener() {
        public void onClick(View v) {          	        	
        	DiogAction();   			
        }
    };*/
    
    private OnClickListener email1_btnListener = new OnClickListener() {
        public void onClick(View v) {          	        	
        	Email();		
        }
    };
    
    private OnClickListener diogbtn_btnListener = new OnClickListener() {
        public void onClick(View v) {          	        	
        	DiogAction();		
        }
    };
    
    private OnClickListener setting_btnListener = new OnClickListener() {
        public void onClick(View v) {             	
        	 ShowSetting();  			
        }
    };
    
    private OnClickListener skype_btnListener = new OnClickListener() {
        public void onClick(View v) {             	
        	 ShowSkype();  			
        }
    };
    
    private OnClickListener btnDisplayListner = new OnClickListener() {
        public void onClick(View v) { 
        	alertDialog.dismiss(); 			
        }
    };
    
    public void ShowSkype(){
    	String skypeName = "sowmyaraob";
    	Intent intent = new Intent("android.intent.action.VIEW");
    	intent.setData(Uri.parse("skype:" + skypeName));
    	startActivity(intent);
    }
    
    public void Email(){
    	linkText = "File Name: "+name.text()+"\n"+edition.text()+":"+" "+device.text()+"\n"+deviceType.text()+" "+manufacturer+" "+editionStr+"\n"+version.text()+" "+androidOS+"\n"+"GDID: "+imeno+"\n"+"Server: "+urlName;
		String to = "gss.mobile@globalsoft-solutions.com";
		String subject = "GSS Mobile Diognosis & Checks";
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, linkText);
		email.setType("message/rfc822");   		 
		startActivity(Intent.createChooser(email, "Choose an Email client"));
    }//
    
    public void ShowSetting(){
    	 try {
    		 Intent intent = new Intent(this, SalesProDiagnosisSettings.class); 					 			
 			startActivityForResult(intent, 25);
    		/* SalesOrderConstants.showLog("1");    	
    	 LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
 		  View layout;
 		 SalesOrderConstants.showLog("2");
 		  layout = inflater.inflate(R.layout.settings_alert,
 				  (ViewGroup) findViewById(R.id.listviewlineardialog4));		    
 		
 		 ListView listview = (ListView)layout.findViewById(R.id.list2);		
 		 Button btnDisplay = (Button) findViewById(R.id.backbtn); 		 
 		btnDisplay.setOnClickListener(btnDisplayListner);
 		 SalesOrderConstants.showLog("3"); 
 		// ServiceProSettingsAdapter = new ServiceProSettingsAdapter(this);		   		  
 		builder= new AlertDialog.Builder(this).setTitle("Gss Mobile Diognosis & Checks");	        		  	        		 
 		builder.setInverseBackgroundForced(true);
 		  View view=inflater.inflate(R.layout.custom_titli_dialog, null);
 		  builder.setCustomTitle(view);	        		 
 		builder.setView(layout); 	 
 		final CharSequence[] items = {" Active "," Inactive "};
 		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int whichButton) {
    	         //input.setText("hi");
    	        // Do something with value!
    	      }
    	    });  
        // Creating and Building the Dialog        
      
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int item) {                     
            switch(item)
            {
                case 0:
                	SapGenConstants.DIOG_FLAG =1;
                         break;
                case 1:
                	SapGenConstants.DIOG_FLAG =0;
                        
                        break;             
                
            }
            alertDialog.dismiss();    
            }
        });      
 		builder.setSingleChoiceItems(ServiceProSettingsAdapter, -1,new DialogInterface.OnClickListener() { 
	        		public void onClick(DialogInterface dialog, int which) {	  	        			
	        			SalesOrderConstants.showLog("which : "+which);
	        			selctdPos=which;
	        			if(flagPos==1)
	        				flagPos=0;
	        			SalesOrderConstants.showErrorLog("Selected Position : "+selctdPos);	  	        			
	        			SOCreationScreen(selctdPos);
	        			SalesOrderConstants.showLog("selctdPos : "+selctdPos);
	        			
	        			alertDialog.dismiss();
	        		}
	        	});
 		 alertDialog = builder.create();    		  
 		alertDialog.show();
 		 SalesOrderConstants.showLog("4");*/
    	 }catch (Exception def) {
 	    	SapGenConstants.showErrorLog("Error in ShowSetting: "+def.toString());
 	    }
    }//ShowSetting
    public void DiogAction(){    	
    	try{   		     	       	   		       
    	        SoapSerializationEnvelope envelopeC = null;
    	       
    	            SoapObject request = new SoapObject(SalesOrderProConstants.SOAP_SERVICE_NAMESPACE, SalesOrderProConstants.SOAP_TYPE_FNAME); 
    	            envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	            
    	            SalesOrdProIpConstraints C0[];
    	            C0 = new SalesOrdProIpConstraints[3];
    	            
    	            for(int i=0; i<C0.length; i++){
    	                C0[i] = new SalesOrdProIpConstraints(); 
    	            }
    	                        
    	            C0[0].Cdata = SalesOrderProConstants.getApplicationIdentityParameter(this);
    	            C0[1].Cdata = SalesOrderProConstants.SOAP_NOTATION_DELIMITER;
    	            C0[2].Cdata = "EVENT[.]DIAGNOSIS-AND-CHECKS[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS";
    	        
    	            Vector listVect = new Vector();
    	            for(int k=0; k<C0.length; k++){
    	                listVect.addElement(C0[k]);
    	            }
    	        
    	            request.addProperty (SalesOrderProConstants.SOAP_INPUTPARAM_NAME, listVect);            
    	            envelopeC.setOutputSoapObject(request);                    
    	            SalesOrderProConstants.showLog("Request:"+request.toString());
    	            //respType = SapGenConstants.SalesOrderProConstants;
    	            //startNetworkConnection(this, envelopeC, SapGenConstants.SOAP_SERVICE_URL);
    	            doThreadNetworkAction(this, ntwrkHandler, getNetworkResponseRunnable, envelopeC, request);    	       
    	   		 
     	}catch(Exception sfg){
     		SalesProCustActivityConstants.showErrorLog("Error in DiogAction : "+sfg.toString());
     	}    	
    }//DiogAction  
    
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
	    SapGenConstants.showLog("Soap Env value : "+resultSoap);
	    updateServerResponse(resultSoap);
	    }		    
    };
    
    public void updateServerResponse(SoapObject soap){		
        try{ 
        	if(soap != null){    			    			    			
    			/*if(mattVector != null)
    				mattVector.removeAllElements();*/
    			
    			if(priceList != null)
    				priceList.clear();
    				            	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[40];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 1){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                           //String symb = ";"+"}";
	                           String finalString= result.replace(";", " "); 
	                            finalString2= finalString.replace("}", " "); 
	                           SapGenConstants.showLog("finalString2"+finalString2);
	                          /*  resC = 0;
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
	                          */
	                            if(docTypeStr.equalsIgnoreCase("ZGSSMWST_DIAGNOSYSINFO01")){	                                
	                                
	                                if(priceList != null)
	                                	priceList.add(finalString2);	                                	                              
	                            }
	                        }
	                        else if(j == 0){
	                            String errorMsg = pii.getProperty(j).toString();
	                            SapGenConstants.showLog("Inside J == 0 ");
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
        	}
        }
        catch(Exception sff){
            SapGenConstants.showErrorLog("On updateServerResponse : "+sff.toString());
        } 
        finally{
        	 SapGenConstants.showLog("priceList size"+priceList);        	
        	 ShowAlert();
        }
    }//fn updateServerResponse 
    
    public void ShowAlert(){
    	ImageButton emailbtn;
		TextView tv;
    	 LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
  		  View layout;
  		  
  		  layout = inflater.inflate(R.layout.activity_dialog,
  				  (ViewGroup) findViewById(R.id.listviewlineardialog3));		        		       		  
  		  
  		  ListView listview = (ListView)layout.findViewById(R.id.list4);
  		  SOCustomerListAdapter = new SOCustomerListAdapter(this);
  		  
  		  tv = (TextView)layout.findViewById(R.id.actdiogTV);
  		  tv.setVisibility(View.GONE);
  		  emailbtn = (ImageButton)layout.findViewById(R.id.showemailbtn2);
		  emailbtn.setOnClickListener(email_btnListener); 	   
  			        		  
  		  builder = new AlertDialog.Builder(this).setTitle("Gss Mobile Diognosis & Checks");	        		  	        		 
  		  builder.setInverseBackgroundForced(true);
  		 /* View view=inflater.inflate(R.layout.custom_titli_dialog, null);
  		  builder.setCustomTitle(view);	  */      		 
  		  builder.setView(layout); 	        		
  		  builder.setSingleChoiceItems(SOCustomerListAdapter, -1,new DialogInterface.OnClickListener() { 
	        		public void onClick(DialogInterface dialog, int which) {	  	        			
	        			SalesOrderConstants.showLog("which : "+which);
	        			/*selctdPos=which;
	        			if(flagPos==1)
	        				flagPos=0;
	        			SalesOrderConstants.showErrorLog("Selected Position : "+selctdPos);	  	        			
	        			SOCreationScreen(selctdPos);
	        			SalesOrderConstants.showLog("selctdPos : "+selctdPos);
	        			
	        			alertDialog.dismiss();*/
	        			//alertDialog.dismiss();
	        		}
	        	});
  		  alertDialog = builder.create();    		  
  		  alertDialog.show();
    }//ShowAlert
    
    private OnClickListener email_btnListener = new OnClickListener() {
        public void onClick(View v) {          	        	
        	showEmailActivity();   			
        }
    };
 public void showEmailActivity() {
    	try{    				
			for(int i=0;i<priceList.size();i++){
				linkText = linkText+priceList.get(i).toString()+"\n";
			}		
			linkText = "File Name: "+name.text()+"\n"+edition.text()+":"+" "+device.text()+"\n"+deviceType.text()+" "+manufacturer+" "+editionStr+"\n"+version.text()+" "+androidOS+"\n"+"GDID: "+imeno+"\n"+"Server: "+urlName;
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
 
    public class SOCustomerListAdapter extends BaseAdapter {	    			
	    LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    HashMap<String, String> checkdCustmap = null;
	    	
	    public SOCustomerListAdapter(Context context) {
	    	// Cache the LayoutInflate to avoid asking for a new one each time.
	    	mInflater = LayoutInflater.from(context);   
	    }
	    
	    public int getCount() {
	    	try {
	    		if(priceList != null)
	    			return priceList.size();
	    		//SalesOrderConstants.showLog("SOCheckedList size in adapter "+SOCheckedList.size());
	    	}
	    	catch (Exception e) {
	    		SalesOrderConstants.showErrorLog(e.getMessage());
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
            convertView = mInflater.inflate(R.layout.salesorderdialog, null);
            holder = new ViewHolder();
            holder.ctname = (TextView) convertView.findViewById(R.id.ctname);              
            holder.llitembg1 = (LinearLayout) convertView.findViewById(R.id.llitembg1);

            if(position%2 == 0)
				holder.llitembg1.setBackgroundResource(R.color.item_even_color);
			else
				holder.llitembg1.setBackgroundResource(R.color.item_odd_color);
            
            try {
            	if(priceList != null){  
            		
            		String spname = (String) priceList.get(position).toString();            		
            		 holder.ctname.setText(spname);			           	
            	} 
            }catch (Exception qw) {
            	SalesOrderConstants.showErrorLog("Error in ContactListAdapter : "+qw.toString());
			}
            
           return convertView;	           
		}
		
		public void removeAllTasks() {
            notifyDataSetChanged();
        } 		
	}//End of ContactListAdapter
}//