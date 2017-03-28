package com.globalsoft.SapQueueProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessor.Utils.SapQueueProcessorConstants;

public class LogsList extends ListActivity {   
	
	private Button bbtn;      
	private ListView listView;
	private MyLogsListAdapter logsListAdapter;
	private static ArrayList logsList = new ArrayList();
	private AlertDialog alertDialog;
	private ProgressDialog pdialog = null;
    final Handler logData_Handler = new Handler();
    private String htmlFilePath = "";
    private String htmlFileName = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.loglist);	 
			
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.SCR_LOG_TITLE));

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
	        
	        listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(listItemClickListener);    
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			logsListAdapter = new MyLogsListAdapter(this);
			setListAdapter(logsListAdapter);
			
	        bbtn = (Button) findViewById(R.id.bbtn);
	        bbtn.setOnClickListener(bbtnListener); 
	        
			getLogsList();
	    } catch (Exception de) {
	    	SapGenConstants.showErrorLog("Error in logsList screen: "+de.toString());
	    }
    }
    
    OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			try{
				showLogText(arg2);
			}
			catch (Exception dee) {
				SapGenConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}
	};
	
	private void showLogText(int index){
		try {
			String filename = logsList.get(index).toString().trim();
			Intent intent = new Intent(this, LogDetails.class);
			intent.putExtra("filename", filename);
			startActivity(intent);
			/*String filename = logsList.get(index).toString().trim();
		    File f = new File(Environment.getExternalStorageDirectory()+ File.separator+"Sapqueueprocessor"+File.separator+"Logs"+File.separator+filename); 
	        FileInputStream fIn;
	        String aDataRow = "";
	        String aBuffer = "";
			fIn = new FileInputStream(f);		
	        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
	        while ((aDataRow = myReader.readLine()) != null) 
	        {
	            aBuffer += aDataRow ;
	        }
	        myReader.close();

			alertDialog = new AlertDialog.Builder(this)
			.setTitle(filename)
            .setMessage(aBuffer)
			.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
				})	
				.create();
				alertDialog.show();*/
		} 
		catch (Exception e) {
			SapGenConstants.showErrorLog(e.getMessage());
		}
	}//fn showLogText
    
    //back btn click listener
    private OnClickListener bbtnListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	try{
        		backAction();
        	}
        	catch(Exception e){
        		SapGenConstants.showErrorLog("Error in bbtnListener"+e.toString());
        	}
        }
    };
    
    public void backAction(){
    	try{
    		File f = new File(Environment.getExternalStorageDirectory()+ File.separator+"Sapqueueprocessor"+File.separator+"Logs");
	        File[] files = f.listFiles();
	        String name = "";
	        
	        for( int i=0; i< files.length; i++)
	        {
	        	name = files[i].getName().toString().trim();	    	        	
	        	if(name.endsWith(".html")){
	        		File logFile = new File(SapQueueProcessorConstants.LOG_FILE_PATH+name);
    	    		if (logFile.exists()){
    	    			logFile.delete();
    	    		}
	        	}
	        }
    		finish();
    	}
    	catch(Exception e){
    		SapGenConstants.showErrorLog("Error in backAction"+e.toString());
    	}
    }//fn backAction
    
    public void onBackPressed() {
    	backAction();
	}//fn onBackPressed
    
    //getting logs lists
  	public void getLogsList(){
  		try {
  			if(logsList.size() > 0){
  				logsList.clear();
  			}		
  			if(pdialog != null)
      			pdialog = null;			
      		
  			if(pdialog == null){
  				pdialog = ProgressDialog.show(LogsList.this, "", getString(R.string.WAIT_TEXTS),true);
  				Thread t = new Thread() 
      			{
      	            public void run() 
      				{
              			try{
              				gettingLogs();
              			} catch (Exception e) {
              				SapGenConstants.showErrorLog("Error in getLogsList Thread:"+e.toString());
              			}
              			logData_Handler.post(logCall);
      				}
      			};
      	        t.start();	
  			}
  			
  		} catch (Exception sffe) {
  			SapGenConstants.showErrorLog("Error in getLogsList:"+sffe.toString());
  		}
  	}//fn getLogsList
  	
  	private void gettingLogs(){
		try {
			File f = new File(Environment.getExternalStorageDirectory()+ File.separator+"Sapqueueprocessor"+File.separator+"Logs");
	        File[] files = f.listFiles();
	        String name = "";
	        
	        for( int i=0; i< files.length; i++)
	        {
	        	name = files[i].getName().toString().trim();	  
	        	if(name != null && name.length() > 0){
		        	logsList.add(name);       		
	        	}
	        }
		} catch (Exception sffee) {
			SapGenConstants.showErrorLog("Error in gettingLogs:"+sffee.toString());
		}
	}//fn gettingLogs
  	
  	final Runnable logCall = new Runnable(){
	    public void run()
	    {
	    	try{
	    		SapGenConstants.showLog("logs size:"+logsList.size());
				stopProgressDialog();
				Collections.sort(logsList);
		        getListView().invalidateViews();		        
				if(logsList.size() == 0)
					SapGenConstants.showErrorDialog(LogsList.this, getResources().getString(R.string.LOGS_EMPTY));
	    	} catch(Exception sfe){
	    		SapGenConstants.showErrorLog("Error in logCall:"+sfe.toString());
	    	}
	    }	    
    };
    
    private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				pdialog.dismiss();
				pdialog = null;
			}
			else
				pdialog = null;	
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in stopProgressDialog:"+ce.toString());
		}
    }//fn stopProgressDialog    
        
    public class MyLogsListAdapter extends BaseAdapter {      
        
    	private LayoutInflater mInflater;
        
        public MyLogsListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
        	try {
				if(logsList != null)
					return logsList.size();
			}
        	catch (Exception e) {
        		SapGenConstants.showErrorLog(e.getMessage());
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
            ViewHolder holder;            
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.loglistitem, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.email = (ImageView) convertView.findViewById(R.id.email);
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
                       
			
            if(position%2 == 0)
            	holder.llitembg.setBackgroundResource(R.color.item_even_color);
            else
            	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            if(logsList != null){
            	try {
					String name = logsList.get(position).toString().trim();
					File file = new File(SapQueueProcessorConstants.LOG_FILE_PATH+name);
					Date lastModDate = new Date(file.lastModified());  
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String mdDateandTime = sdf.format(lastModDate);
					//getting Time
					SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
					String mdTime = sdfTime.format(lastModDate);	
					//getting Date
					SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd"); 
					Date dateObj = curFormater.parse(mdDateandTime);
					Long dateLongValue = dateObj.getTime(); 					
					String dateVal = SapGenConstants.getTaskDateStringFormat(Long.valueOf(dateLongValue), false);
					String sysDate = SapQueueProcessorConstants.getSystemDateFormatString(LogsList.this, dateVal);
					
					if(name != null && name.length() > 0){
						holder.name.setText(name+"  "+sysDate+" "+mdTime);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }    
            
            holder.email.setOnClickListener(new OnClickListener() { 					 
	            @Override 
	            public void onClick(View v) { 
	                // TODO Auto-generated method stub 		 
	            	//SapGenConstants.showLog("position : "+position);
	            	SapGenConstants.showLog("getfilename : "+logsList.get(position).toString().trim());
	            	String fileName = logsList.get(position).toString().trim();
	            	if(fileName != null && fileName.length() > 0){
		            	sendEmail(fileName);	            		
	            	}
	            } 
	        }); 
            
            return convertView;
        }

        class ViewHolder {
            TextView name;  
            ImageView email;  
            LinearLayout llitembg;
        }
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }        
    }//End of MyLogsListAdapter	
    
    private void sendEmail(String filename){
    	try {
    		String path = SapQueueProcessorConstants.LOG_FILE_PATH+filename;  
    		htmlFilePath = path.replace(".txt", ".html"); 
    		htmlFileName = filename.replace(".txt", ".html");
    		File logFile = new File(htmlFilePath);
    		if (!logFile.exists()){
				try {
					logFile.createNewFile();
				} 
				catch (IOException e){
					Log.e(SapQueueProcessorConstants.SAPQUEUEPROCESSOR_TAG, e.toString());
	    		}	
    		}else{
    			logFile.delete();
				logFile.createNewFile();
    		}
    		StringBuilder sb = new StringBuilder("<html><head><title>Log Details</title></head><body>");
	        sb.append(readTextFile(filename));
	        sb.append("</body></html>");
	        SapQueueProcessorConstants.writeLogToFile(htmlFileName, sb.toString());
	        Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	  	sendIntent.setType("message/rfc822");
    		sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Attachement for LOG");
    		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"vijay.baskar@globalsoftsolutions.net"});      	
    		Uri uri = Uri.fromFile(new File(SapQueueProcessorConstants.LOG_FILE_PATH+htmlFileName));
    		sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
    		sendIntent.putExtra(Intent.EXTRA_TEXT, "Attachement for LOG");
    		startActivityForResult(Intent.createChooser(sendIntent, "Choose an Email client :"), SapGenConstants.EMAIL_SEND_ACTION); 
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error in sendEmail:"+ce.toString());
		}
    }//fn sendEmail  
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  super.onActivityResult(requestCode, resultCode, data);
		  SapGenConstants.showLog("requestCode:"+requestCode);
		  SapGenConstants.showLog("resultCode:"+resultCode);
	}
        
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