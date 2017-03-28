package com.globalsoft.SapLibSoap.SoapConnection;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StartAddTask extends AsyncTask<String, Integer, String[]> {
	
	private ProgressDialog pdialog = null;		
	private Context mContext;
	private String exceptionStr = "";
	
	public StartAddTask(Context context){
		mContext = context;
		exceptionStr = "";
	}
	
	public StartAddTask(Context context, boolean flag){
		mContext = context;
		exceptionStr = "";
	}
	
	protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
		//startProgressDialog();
    }
	
	protected String[] doInBackground(String... id) {
		//It accepts parameters as array so use first param
		return getContactAddressDetails(mContext, id[0]);
	}

	protected void onProgressUpdate(Integer... progress) {
		//do any progress update
		SapGenConstants.showLog("Progress : "+progress);
    }

	protected void onPostExecute(SoapObject resultSoap) {
		if(!exceptionStr.equalsIgnoreCase("")){
		}
    }
	
	public static String[] getContactAddressDetails(Context ctx, String contactId){
		String strStreet = "", strCity = "", strRegion = "", strPostalCode = "", strCountry = "", strAddType = "";
		String[] result = new String[6];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Address Details
    		String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
    		String[] addrWhereParams = new String[]{String.valueOf(contactId), 
    			ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
    		Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
    	                null, addrWhere, addrWhereParams, null); 
    		while(addrCur.moveToNext()) {
    	 		String street = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
    	 		String city = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
    	 		String region = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
    	 		String postalCode = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
    	 		String country = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
    	 		String type = addrCur.getString(
    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME))){
             		strStreet = street;
             		strCity = city;
             		strRegion = region;
             		strPostalCode = postalCode;
             		strCountry = country;
             		strAddType = type;// 1 is home
    	 	    }     	
    	 	} 
    		addrCur.close();
    		if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
    	 	    	 	
    	 	addrCur = cr.query(ContactsContract.Data.CONTENT_URI, 
	                null, addrWhere, addrWhereParams, null);
    		if(strStreet.length() <= 0 && strCity.length() <= 0 && strRegion.length() <= 0 && strPostalCode.length() <= 0 && strCountry.length() <= 0){  
    			while(addrCur.moveToNext()) {
	    	 		String street = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
	    	 		String city = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
	    	 		String region = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
	    	 		String postalCode = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
	    	 		String country = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
	    	 		String type = addrCur.getString(
	    	                     addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
	    	 		if(type.equals(String.valueOf(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK))){
	             		strStreet = street;
	             		strCity = city;
	             		strRegion = region;
	             		strPostalCode = postalCode;
	             		strCountry = country;
	             		strAddType = type;// 1 is home
	    	 	    }     	
	    	 	}     			  			
    		}
    		addrCur.close();
    		
    	 	if(strStreet == null || strStreet.length() == 0){
    	 		strStreet = "";
    		}
    	 	if(strCity == null || strCity.length() == 0){
    	 		strCity = "";
    		}
    	 	if(strRegion == null || strRegion.length() == 0){
    	 		strRegion = "";
    		}
    	 	if(strPostalCode == null || strPostalCode.length() == 0){
    	 		strPostalCode = "";
    		}
    	 	if(strCountry == null || strCountry.length() == 0){
    	 		strCountry = "";
    		}
    	 	if(strAddType == null || strAddType.length() == 0){
    	 		strAddType = "";
    		}
         	result[0] = strStreet;
         	result[1] = strCity;
         	result[2] = strRegion;
         	result[3] = strPostalCode;
         	result[4] = strCountry;
         	result[5] = strAddType;
		} 
    	catch (Exception ssqw) {
    		SapGenConstants.showErrorLog("Error in getContactAddressDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
         	result[3] = "";
         	result[4] = "";
         	result[5] = "";
		}
    	return result;
    }//fn getContactAddressDetails
	
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
		
}//End of class StartAddTask