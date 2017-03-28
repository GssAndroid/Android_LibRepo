package com.globalsoft.SapQueueProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessor.Constraints.QueueDetails;
import com.globalsoft.SapQueueProcessor.Utils.SapQueueProcessorConstants;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorContentProvider;
import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorDBConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class QueueList extends ListActivity {   
	
	private Button bbtn;      
	private ListView listView;
	private MyQueueListAdapter queueListAdapter;
	private static ArrayList queueList = new ArrayList();

	private ProgressDialog pdialog = null;
    final Handler queueData_Handler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	SapGenConstants.setWindowTitleTheme(this);   
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
	        setContentView(R.layout.queuelist);	 
			
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle); 	    	
			TextView myTitle = (TextView) findViewById(R.id.myTitle);
			myTitle.setText(getResources().getString(R.string.SCR_TITLE));

			int dispwidthTitle = SapGenConstants.getDisplayWidth(this);	
			if(dispwidthTitle > SapGenConstants.SCREEN_CHK_DISPLAY_WIDTH){
				myTitle.setTextAppearance(this, R.style.titleTextStyleBig); 
			}
	        
	        listView = (ListView)findViewById(android.R.id.list);
			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(listItemClickListener);    
			listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1));
			queueListAdapter = new MyQueueListAdapter(this);
			setListAdapter(queueListAdapter);
			
	        bbtn = (Button) findViewById(R.id.bbtn);
	        bbtn.setOnClickListener(bbtnListener); 
	        
			getQueueList();				
			//CallReciverClass();
	    } catch (Exception de) {
	    	SapGenConstants.showErrorLog("Error in QueueList screen: "+de.toString());
	    }
    }
    
   /* private void CallReciverClass() {
    	String text = "30";
			 int integersec = Integer.parseInt(text.toString());
			 Intent intent2 = new Intent(this,SapQueueProcessorNWReceiver.class);
			    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 234324243, intent2, 0);
			    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
			        + (integersec * 1000), pendingIntent);    			  
			    Toast.makeText(this, "Alarm set in " + integersec + " seconds",
			        Toast.LENGTH_LONG).show();       
		
	}*/

	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			try{
			    
			}
			catch (Exception dee) {
				SapGenConstants.showErrorLog("Error in listItemClickListener:"+dee.toString());
		    }
		}
	};
	
    //back btn click listener
    private OnClickListener bbtnListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	try{
        		finish();
        	}
        	catch(Exception e){
        		SapGenConstants.showErrorLog("Error in bbtnListener"+e.toString());
        	}
        }
    };
    
    //getting queue lists
  	public void getQueueList(){
  		try {
  			if(queueList.size() > 0){
  				queueList.clear();
  			}		
  			if(pdialog != null)
      			pdialog = null;			
      		
  			if(pdialog == null){
  				pdialog = ProgressDialog.show(QueueList.this, "", getString(R.string.WAIT_TEXTS),true);
  				Thread t = new Thread() 
      			{
      	            public void run() 
      				{
              			try{
              				gettingQueues();
              			} catch (Exception e) {
              				SapGenConstants.showErrorLog("Error in gettingQueueCall Thread:"+e.toString());
              			}
              			queueData_Handler.post(queueCall);
      				}
      			};
      	        t.start();	
  			}
  			
  		} catch (Exception sffe) {
  			SapGenConstants.showErrorLog("Error in getQueueList:"+sffe.toString());
  		}
  	}//fn getQueueList
  	
  	private void gettingQueues(){
		try {
			Uri uri = Uri.parse(SapQueueProcessorContentProvider.CONTENT_URI+"");
			Cursor C = getContentResolver().query(uri, null, null, null, null); 
    		SapGenConstants.showLog("Cursor size:"+C.getCount());
			while (C.moveToNext()) {                   
				String date = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_DATE));     
				String appId = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_APPNAME));       
				String refId = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_APPREFID));     
				String apiName = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_FUNCNAME));     
				String status = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_STATUS));     
				String processcount = C.getString(C.getColumnIndex(SapQueueProcessorHelperConstants.COL_PROCESS_COUNT)); 
				SapGenConstants.showLog("date:"+date);			
				queueList.add(new QueueDetails(""+date, ""+appId, ""+refId, ""+apiName, ""+status, ""+processcount));
			}
			C.close();
		} catch (Exception sffee) {
			SapGenConstants.showErrorLog("Error in gettingQueues:"+sffee.toString());
		}
	}//fn gettingQueues
  	
  	final Runnable queueCall = new Runnable(){
	    public void run()
	    {
	    	try{
	    		SapGenConstants.showLog("queue size:"+queueList.size());
				stopProgressDialog();
		        getListView().invalidateViews();		        
				if(queueList.size() == 0)
					SapGenConstants.showErrorDialog(QueueList.this, getResources().getString(R.string.QUEUE_EMPTY));
	    	} catch(Exception sfe){
	    		SapGenConstants.showErrorLog("Error in queueCall:"+sfe.toString());
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
        
    public class MyQueueListAdapter extends BaseAdapter {      
        
    	private LayoutInflater mInflater;
        
        public MyQueueListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
        	try {
				if(queueList != null)
					return queueList.size();
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

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;            
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.queuelistitem, null);
                holder = new ViewHolder();
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.appid = (TextView) convertView.findViewById(R.id.appid);
                holder.refid = (TextView) convertView.findViewById(R.id.refid);
                holder.status = (ImageView) convertView.findViewById(R.id.status);
                holder.llitembg = (LinearLayout) convertView.findViewById(R.id.llitembg);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
                       
			
            if(position%2 == 0)
            	holder.llitembg.setBackgroundResource(R.color.item_even_color);
            else
            	holder.llitembg.setBackgroundResource(R.color.item_odd_color);
            
            if(queueList != null){
            	String date = (String)((QueueDetails)queueList.get(position)).getDate();
    			String appId = (String)((QueueDetails)queueList.get(position)).getAppId();
    			String refId = (String)((QueueDetails)queueList.get(position)).getRefId();
    			String apiName = (String)((QueueDetails)queueList.get(position)).getApiName();
    			String status = (String)((QueueDetails)queueList.get(position)).getStatus();
    			String processcount = (String)((QueueDetails)queueList.get(position)).getProcessCount();
    			int pcount = Integer.parseInt(processcount);
    			
    			if(status.equalsIgnoreCase(String.valueOf(SapQueueProcessorHelperConstants.STATUS_IDLE))){
        			holder.status.setImageResource(R.drawable.grey);
    			}else if(status.equalsIgnoreCase(String.valueOf(SapQueueProcessorHelperConstants.STATUS_PROCEEDEDBYSERVER)) && pcount > 0){
        			holder.status.setImageResource(R.drawable.red);    				
    			}else if(status.equalsIgnoreCase(String.valueOf(SapQueueProcessorHelperConstants.STATUS_COMPLETED))){
        			holder.status.setImageResource(R.drawable.green);    				
    			}
    			
            	if(date != null && date.length() > 0){            		
            		try {
            			SapGenConstants.showLog("date:"+date);
						Date dateObj = new Date(Long.parseLong(date)); 						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String mdDateandTime = sdf.format(dateObj);
						//getting Time
						SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
						String mdTime = sdfTime.format(dateObj);	
						//getting Date
						SimpleDateFormat curFormater1 = new SimpleDateFormat("yyyy-MM-dd"); 
						Date dateObj1 = curFormater1.parse(mdDateandTime);
						Long dateLongValue1 = dateObj1.getTime(); 					
						String dateVal2 = SapGenConstants.getTaskDateStringFormat(Long.valueOf(dateLongValue1), false);
						String sysDate1 = SapQueueProcessorConstants.getSystemDateFormatString(QueueList.this, dateVal2);
						holder.date.setText("Date  "+sysDate1+" "+mdTime);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
				}
            	if(appId != null && appId.length() > 0){            		
            		if(apiName != null && apiName.length() > 0){
    					holder.appid.setText(""+appId+"/"+apiName);            			
            		}else{
            			holder.appid.setText(""+appId);
            		}
				}
            	if(refId != null && refId.length() > 0){
					holder.refid.setText("Ref ID  "+refId);
				}
            }            
            return convertView;
        }

        class ViewHolder {
            TextView date;
            TextView appid;
            TextView refid;
            ImageView status;
            LinearLayout llitembg;
        }
        
        public void removeAllTasks() {
            notifyDataSetChanged();
        }        
    }//End of MyQueueListAdapter	
}