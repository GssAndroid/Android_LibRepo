package com.globalsoft.SapLibSoap.SoapConnection;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StartEmailTask extends AsyncTask<String, Integer, String[]> {
	
	private ProgressDialog pdialog = null;		
	private Context mContext;
	private String exceptionStr = "";
	
	public StartEmailTask(Context context){
		mContext = context;
		exceptionStr = "";
	}
	
	public StartEmailTask(Context context, boolean flag){
		mContext = context;
		exceptionStr = "";
	}
	
	protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
		//startProgressDialog();
    }
	
	protected String[] doInBackground(String... id) {
		//It accepts parameters as array so use first param
		return getContactEmailsDetails(mContext, id[0]);
	}

	protected void onProgressUpdate(Integer... progress) {
		//do any progress update
		SapGenConstants.showLog("Progress : "+progress);
    }

	protected void onPostExecute(SoapObject resultSoap) {
		if(!exceptionStr.equalsIgnoreCase("")){
		}
    }
	
	public static String[] getContactEmailsDetails(Context ctx, String contactId){
		String strEmailHome = "", strEmailWork = "", strEmailOther = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{String.valueOf(contactId)}, null); 
    		while (emailCur.moveToNext()) { 
    		    // This would allow you get several email addresses
    	            // if the email addresses were stored in an array
    		    String email = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    	 	    String emailType = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_HOME))){
    	 	    	strEmailHome = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	}
    		emailCur.close();
    		if(strEmailHome == null || strEmailHome.length() == 0){
    			strEmailHome = "";
    		}
    		if(strEmailWork == null || strEmailWork.length() == 0){
    			strEmailWork = "";
    		}
    		if(strEmailOther == null || strEmailOther.length() == 0){
    			strEmailOther = "";
    		}
         	result[0] = strEmailHome;
         	result[1] = strEmailWork;
         	result[2] = strEmailOther;
		} 
    	catch (Exception ssqw) {
    		SapGenConstants.showErrorLog("Error in getContactEmailsDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactEmailsDetails
	
	private void startProgressDialog(){
		try {
			this.pdialog.setMessage("Contacting Server...");
			pdialog.setIndeterminate(true);
			pdialog.setCancelable(false);
			pdialog.show();
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error on startProgressDialog : "+ce.toString());
		}
	}//fn startProgressDialog	
	
	private void stopProgressDialog(){
    	try {
			if(pdialog != null){
				if(pdialog.isShowing())
					pdialog.dismiss();
			}
			
			pdialog = null;	
		} catch (Exception ce) {
			SapGenConstants.showErrorLog("Error on stopProgressDialog : "+ce.toString());
		}
    }//fn stopProgressDialog
		
}//End of class StartEmailTask