package com.globalsoft.SapLibSoap.SoapConnection;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StartPhTask extends AsyncTask<String, Integer, String[]> {
	
	private ProgressDialog pdialog = null;		
	private Context mContext;
	private String exceptionStr = "";
	
	public StartPhTask(Context context){
		mContext = context;
		exceptionStr = "";
	}
	
	public StartPhTask(Context context, boolean flag){
		mContext = context;
		exceptionStr = "";
	}
	
	protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
		//startProgressDialog();
    }
	
	protected String[] doInBackground(String... id) {
		//It accepts parameters as array so use first param
		return getContactPhDetails(mContext, id[0]);
	}

	protected void onProgressUpdate(Integer... progress) {
		//do any progress update
		SapGenConstants.showLog("Progress : "+progress);
    }

	protected void onPostExecute(SoapObject resultSoap) {
		if(!exceptionStr.equalsIgnoreCase("")){
		}
    }
	
	public static String[] getContactPhDetails(Context ctx, String contactId){
		String strPhoneHome = "", strPhoneMob = "", strPhoneWork = "", strPhoneOther = "";
		String[] result = new String[4];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Phone no
    		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    						null, 
    						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    						new String[]{String.valueOf(contactId)}, null);
            while (pCur.moveToNext()) {
            	String phValue = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
            	String phType = pCur.getString(
            			pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            	if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME))){
            		strPhoneHome = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))){
    	 	    	strPhoneMob = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK))){
    	 	    	strPhoneWork = phValue;
    	 	    }
            	else if(phType.equals(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))){
    	 	    	strPhoneOther = phValue;
    	 	    }
            } 
            pCur.close();
            if(strPhoneHome == null || strPhoneHome.length() == 0){
            	strPhoneHome = "";
    		}
            if(strPhoneMob == null || strPhoneMob.length() == 0){
            	strPhoneMob = "";
    		}
            if(strPhoneWork == null || strPhoneWork.length() == 0){
            	strPhoneWork = "";
    		}
            if(strPhoneOther == null || strPhoneOther.length() == 0){
            	strPhoneOther = "";
    		}
         	result[0] = strPhoneHome;
         	result[1] = strPhoneMob;
         	result[2] = strPhoneWork;
         	result[3] = strPhoneOther;
		} 
    	catch (Exception ssq) {
    		SapGenConstants.showErrorLog("Error in getContactPhDetails:"+ssq.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
		}
    	return result;
    }//fn getContactPhDetails
	
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
		
}//End of class StartPhTask