package com.globalsoft.SapLibSoap.SoapConnection;

import org.ksoap2.serialization.SoapObject;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StartOrgTask extends AsyncTask<String, Integer, String[]> {
	
	private ProgressDialog pdialog = null;		
	private Context mContext;
	private String exceptionStr = "";
	
	public StartOrgTask(Context context){
		mContext = context;
		exceptionStr = "";
	}
	
	public StartOrgTask(Context context, boolean flag){
		mContext = context;
		exceptionStr = "";
	}
	
	protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
		//startProgressDialog();
    }
	
	protected String[] doInBackground(String... id) {
		//It accepts parameters as array so use first param
		return getContactOrgDetails(mContext, id[0]);
	}

	protected void onProgressUpdate(Integer... progress) {
		//do any progress update
		SapGenConstants.showLog("Progress : "+progress);
    }

	protected void onPostExecute(SoapObject resultSoap) {
		if(!exceptionStr.equalsIgnoreCase("")){
		}
    }
	
	public static String[] getContactOrgDetails(Context ctx, String contactId){
		String strOrgName = "", strOrgTitle = "", strOrgType = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Organization
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
         	String[] orgWhereParams = new String[]{String.valueOf(contactId), 
         		ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
         	Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI, 
                        null, orgWhere, orgWhereParams, null);
         	if (orgCur.moveToNext()) { 
         		String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
         		String orgTitle = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
         		String orgType = "";
         		try {
					strOrgName = orgName;
					strOrgTitle = orgTitle;
					orgType = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
					strOrgType = orgType;
				} catch (Exception sse) {
					SapGenConstants.showErrorLog("Error in getContactOrgDetails1:"+sse.getMessage());
					strOrgType = orgType;//2 is a others
					strOrgName = orgName;
					strOrgTitle = orgTitle;
				}     		
         	} 
         	orgCur.close();
         	
         	if(strOrgName == null || strOrgName.length() == 0){
    	 		strOrgName = "";
    		}
         	if(strOrgType == null || strOrgType.length() == 0){
         		strOrgType = "";
    		}
         	if(strOrgTitle == null || strOrgTitle.length() == 0){
         		strOrgTitle = "";
    		}
         	result[0] = strOrgName;
         	result[1] = strOrgType;
         	result[2] = strOrgTitle;
		} 
    	catch (Exception ssee) {
    		SapGenConstants.showErrorLog("Error in getContactOrgDetails2:"+ssee.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactOrgDetails
	
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
		
}//End of class StartOrgTask