/*
	    Copyright 2013 Bruno Carreira - Lucas Farias - Rafael Luna - Vinícius Fonseca.

		Licensed under the Apache License, Version 2.0 (the "License");
		you may not use this file except in compliance with the License.
		You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

		Unless required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		See the License for the specific language governing permissions and
   		limitations under the License.   			
 */

package org.apache.cordova.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

/**
 * This class launches the camera view, allows the user to take a picture,
 * closes the camera view, and returns the captured image. When the camera view
 * is closed, the screen displayed before the camera view was shown is
 * redisplayed.
 */
public class ContactsLauncher extends CordovaPlugin {
	public String callbackId;
	public static Boolean isStart1 = true;
	
	public static final int INTENT_ACTION_VIEW = 1;
    public static final int INTENT_ACTION_ADD_CONTACT = 2;
    public static final int INTENT_ACTION_EDIT = 3;	
    public static final int INTENT_ACTION_EDIT_4_ADD_CONTACT = 4;
	
    public int lastId;

    private CallbackContext callback;
    
	public ContactsLauncher() {
	}
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    	callback = callbackContext;
    	

    	
        if (action.equals("addContactsPage")) {
        	
        	
            this.addContacts();
  
           
            return true;
        }
        if (action.equals("viewContactsPage")) {
        	//newly added by sowmya        	
        	JSONObject jsonFilter = args.getJSONObject(0);
            this.viewContacts(jsonFilter.optString("id"));
  
            
            return true;
        }
        
        if (action.equals("getContacts")) {
        	
       	
           return true;
       }
        if (action.equals("searchContacts")) {
        	
          	
              return true;
          }
        
        if (action.equals("openAppointments")) {
        	
        	showGeneralAppointmentScreen();
            return true;
        }
        
        if (action.equals("openSalesOrders")) {
        	
        	showSalesOrdersScreen();
            return true;
        }
        
        if (action.equals("openProductCatalog")) {
        	
        	showGeneralProductCatalogScreen();
            return true;
        }
        if (action.equals("openAbout")) {
        	
        	showAboutScreen();
            return true;
        }
        
        return false;
    }

    
   /* private boolean recvFromQP() {
		try{
			Context context=this.cordova.getActivity().getApplicationContext();
    		ArrayList respnseList = new ArrayList();  		
    		respnseList= SapQueueProcessorHelperConstants.getResponseForPhonegap(context);
    		String[] responseArray = new String[respnseList.size()];
    		Log.d("response from QP", "respnseList: " + respnseList);
    		try {
    			byte[] response = (byte[]) respnseList.get(0);   
    			  String decoded = new String(response);
    			  responseArray[0] = decoded;
    			   mJSONArray = new JSONArray(Arrays.asList(responseArray));
    			//JSONArray obj = new JSONArray(decoded);
    			   
    		} catch (Throwable t) {
    			t.printStackTrace();
    		}
    	}catch (Exception td) {
			Log.e("Error inrecvFromQP" ,td.toString());
		}
		
		return true;
	}//recvFromQP
*/    
    private void showGeneralProductCatalogScreen(){
		try {
			//Intent intent = new Intent(this, CrtGenActivity.class);
			/*Context context=this.cordova.getActivity().getApplicationContext();
			Intent intent = new Intent(context, ProductMainScreenForTablet.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);*/
		} 
		catch (Exception sse) {
			//SalesOrderProConstants.showErrorLog("Error on showGeneralAppointmentScreen : "+sse.getMessage());
		}
	}//fn s
    
    
    
    
    private void showGeneralAppointmentScreen(){
		try {
			//Intent intent = new Intent(this, CrtGenActivity.class);
			/*Intent intent = new Intent(cordova.getActivity(), CalendarLists.class);
			this.cordova.startActivityForResult((CordovaPlugin) this,intent,SapGenConstants.ACTION_GEN_ACTIVITY);*/
		} 
		catch (Exception sse) {
			//SalesOrderProConstants.showErrorLog("Error on showGeneralAppointmentScreen : "+sse.getMessage());
		}
	}//fn s
    
    
    
    
    private void showAboutScreen(){
    	try {
			/*Intent intent = new Intent(cordova.getActivity(), About.class);
			this.cordova.startActivityForResult((CordovaPlugin) this,intent,SalesOrderProConstants.SAPABOUT_SCREEN);*/
		} 
		catch (Exception e) {
			///SalesOrderProConstants.showErrorLog(e.getMessage());
		}
    };
    
    
    private void showSalesOrdersScreen(){
		try {
			/*int dispwidth = SalesOrderProConstants.getDisplayWidth(cordova.getActivity());	
			if(SalesOrderConstants.CustomerMatrArrList!=null)
				SalesOrderConstants.CustomerMatrArrList.clear();
			SalesOrderProConstants.showLog("dispwidth: "+dispwidth); 
			SalesOrderProConstants.showLog("SCREEN_CHK_DISPLAY_WIDTH: "+SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH); 
			Intent intent;
			Context context=this.cordova.getActivity().getApplicationContext();
			intent = new Intent(context, SalesOrderMainScreenTablet.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);*/
			
			
			/*if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
				intent = new Intent(cordova.getActivity(), SalesOrderMainScreenTablet.class);
			}else{
				intent = new Intent(cordova.getActivity(), SalesOrderListActivity.class);
			}  	*/
			//this.cordova.startActivityForResult((CordovaPlugin) this,intent,SalesOrderProConstants.SALESORDLIST_LAUNCH_SCREEN);
			/*Intent intent = new Intent(SalesProActivity.this, SalesOrderListActivity.class);
			intent.putExtra("app_name", SalesOrderProConstants.APPLN_NAME_STR);
			startActivityForResult(intent,SalesOrderProConstants.SALESORDLIST_LAUNCH_SCREEN);*/
		} 
		catch (Exception e) {
			//SalesOrderProConstants.showErrorLog(e.getMessage());
		}
	}//fn showSalesOrdersScreen
    
    
    public void viewContacts(String selectedContactId) {
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(selectedContactId)); 
    	i.setData(contactUri); 
    	this.cordova.startActivityForResult((CordovaPlugin) this,i, INTENT_ACTION_VIEW);
	}
    
	public void addContacts() {
		
		Intent i = new Intent(Intent.ACTION_INSERT);
    	Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
    	i.setData(contactUri); 
    	this.cordova.startActivityForResult((CordovaPlugin) this,i, INTENT_ACTION_ADD_CONTACT);
		
		//Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		/*Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), ContactMain.class);
    	intent.putExtra("app_name_options", "SALESPRO");
    	intent.putExtra("app_name", SapGenConstants.APPLN_NAME_STR_MOBILEPRO);
		//Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), Contacts.class);
		//this.cordova.startActivityForResult((CordovaPlugin) this, intent, 1);
		this.cordova.getActivity().startActivity(intent);*/
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {		
		// If image available		
			//if (requestCode == INTENT_ACTION_ADD_CONTACT) {	    			    		 
	    			Cursor C = cordova.getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
	    			while (C.moveToNext()) {                   
	    				String contactId = C.getString(C.getColumnIndex(ContactsContract.Contacts._ID));       
	    				//String name = C.getString(C.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
	    				/*boolean errornotify = false;
	    				String[] Emails = ContactsConstants.getContactEmailsDetails(this, contactId);
	                 	String[] PhNos = ContactsConstants.getContactPhDetails(this, contactId);
	            		String[] OrgDetails = ContactsConstants.getContactOrgDetails(this, contactId);
	            		String[] AddressDetails = ContactsConstants.getContactAddressDetails(this, contactId);
	    				allContactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));
	    				contactVect.addElement(new ContactDetails(""+name, ""+contactId, Emails, PhNos, OrgDetails, AddressDetails, errornotify));*/
	    				lastId = Integer.parseInt(contactId);
	    			}
	    			C.close();    			
	    			
	    			JSONArray result = new JSONArray();
	    			result.put(lastId);
	    			SapGenConstants.ContactID = lastId;
	    			
	    			SapQueueProcessorHelperConstants.deleteContactTableDataFromDB(cordova.getActivity(),String.valueOf(SapGenConstants.ContactID));
	    			Toast.makeText(cordova.getActivity(), lastId+"", Toast.LENGTH_LONG).show();
	    			 PluginResult res = new PluginResult(PluginResult.Status.OK, result);
	    		      res.setKeepCallback(true);
	    		      callback.sendPluginResult(res);	    
	    		      
		/*}else if ((requestCode == INTENT_ACTION_ADD_CONTACT)){
			
		}*/
		}

}
