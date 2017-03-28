package org.apache.cordova.plugin;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class SalesOrderPlugin extends CordovaPlugin {
	private final String pluginName = "SalesOrderPlugin";
	public static final String SEND_REQUEST = "sendSOToQP";
	public static final String SET_NFLAG_REQUEST = "SetNotifyFlagToQP";
	public static final String GET_RESPONSE = "response";
	public static final Integer RESULT_CODE_CREATE = 0;
	private CallbackContext callback;
	JSONArray mJSONArray =null;
	
	@SuppressLint("NewApi")
	@Override
	public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) {
		Log.d(pluginName, "DatePicker called with options: " + data);
		/*JSONArray arr =null;
		try {
			arr = new JSONArray(data);
		} catch (JSONException e) {			
			e.printStackTrace();
		}*/
		String[] list = new String[data.length()];
		for(int i = 0; i < data.length(); i++){
			try {
				list[i] = data.getString(i);
			} catch (JSONException e) {				
				e.printStackTrace();
			} 
		}
		boolean result = false;

		callback = callbackContext;
	    
	    if (SEND_REQUEST.equals(action)) {
	    	return sendSalesOrdersToQP(list);
	    }else if(SET_NFLAG_REQUEST.equals(action)){
	    	return SetNotifyFlagToQP(list);
	    }
	    return result;
	}//

	private boolean SetNotifyFlagToQP(String[] argumnts) {
		try{			
			Context context=this.cordova.getActivity().getApplicationContext();
    		String notificationFlag = argumnts[0];
    		SapGenConstants.AlternateID = argumnts[1];
    		String appname = "SALESPRO";
    		//COLUMN ID AND SEND TO QP TO UPDATE//
    		SapGenConstants.showLog(SapGenConstants.AlternateID);
    		SapGenConstants.showLog(notificationFlag);
    		int columnId = SapQueueProcessorHelperConstants.getColumnIdForPhonegap(context,appname,SapGenConstants.AlternateID);
    		SapGenConstants.showLog("Fetched Column id: "+columnId);
    		SapQueueProcessorHelperConstants.updateSelectedNotifyStatus(context,notificationFlag,columnId);
    	}catch (Exception td) {
			Log.e("Error inrecvFromQP" ,td.toString());
		}
		
		return true;
	}//recvFromQP

	private boolean sendSalesOrdersToQP(String[] argumnts){	
		Context context = this.cordova.getActivity().getApplicationContext();
		SoapSerializationEnvelope envelopeC = null;
		SoapObject request = new SoapObject(SapGenConstants.SOAP_SERVICE_NAMESPACE, SapGenConstants.SOAP_TYPE_FNAME); 
        envelopeC = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        
        SapGenIpConstraints C0[], C1[];
        C0 = new SapGenIpConstraints[argumnts.length-2];
        
        for(int i=0; i<C0.length; i++){
            C0[i] = new SapGenIpConstraints(); 
        }	
        
        for(int k=0;k<C0.length;k++){
        	 C0[k].Cdata = argumnts[k+2];
        }
        Vector listVect = new Vector();
        for(int k=0; k<C0.length; k++){
            listVect.addElement(C0[k]);
        }
        request.addProperty (SapGenConstants.SOAP_INPUTPARAM_NAME, listVect);            
        envelopeC.setOutputSoapObject(request);  
     	Long now = Long.valueOf(System.currentTimeMillis());     	
     	//String altidStr = now.toString()+argumnts[0];
     	SapGenConstants.AlternateID = now.toString()+argumnts[0];
     	SapGenConstants.showLog(SapGenConstants.AlternateID);
     	SapGenConstants.showLog(argumnts[1]);
     	SapGenConstants.platformphongap = "SOphonegap";
		//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(context, SapGenConstants.APPLN_NAME_STR_SALESPRO, "org.apache.cordova.phonegap", argumnts[0],"", "org.apache.cordova.phonegap.example", argumnts[1], request, now);
     	SapQueueProcessorHelperConstants.sendOfflineContentToQueueProcessor(context, SapGenConstants.APPLN_NAME_STR_SALESPRO, "org.apache.cordova.phonegap", argumnts[0],SapGenConstants.AlternateID, "org.apache.cordova.phonegap.example", argumnts[1], request, now,"SOphonegap");
		return true;
	}///sendToQP
	
	
	private boolean listTimezone() throws JSONException {
	    JSONArray jsonObject = null;
	    
	    String timeZone[] = TimeZone.getAvailableIDs();
	    jsonObject = new JSONArray(Arrays.asList(timeZone));

	    
	    PluginResult res = new PluginResult(PluginResult.Status.OK, jsonObject);
	    callback.sendPluginResult(res);
	    return true;
	  }
}//
