package org.apache.cordova.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.globalsoft.SapLibSoap.Constraints.SapGenIpConstraints;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;



public class QueueProcessorPlugin extends CordovaPlugin {

	private final String pluginName = "QueueProcessorPlugin";
	public static final String SEND_REQUEST = "sendToQP";
	public static final String RECIEVE_REQUEST = "recvFromQP";
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
	    	return sendToQP(list);
	    }/*else if(RECIEVE_REQUEST.equals(action)){
	    	return recvFromQP();
	    }*/
	    return result;
	}//

	/*private boolean recvFromQP() {
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
    			JSONObject xmlJSONObj = XML.toJSONObject(TEST_XML_STRING);
                String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
                System.out.println(jsonPrettyPrintString);
    			   
    		} catch (Throwable t) {
    			t.printStackTrace();
    		}
    	}catch (Exception td) {
			Log.e("Error inrecvFromQP" ,td.toString());
		}
		
		return true;
	}//recvFromQP
*/	
	private boolean sendToQP(String[] argumnts){
		Context context=this.cordova.getActivity().getApplicationContext();
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
    	/*String altidStr = now.toString()+argumnts[0];
     	SapGenConstants.AlternateID = altidStr;
     	SapGenConstants.showLog(altidStr);*/
     	SapGenConstants.platformphongap = "Cphonegap";
     	String contactIDStr = String.valueOf(SapGenConstants.ContactID);
		//SapQueueProcessorHelperConstants.saveOfflineContentToQueueProcessor(context, SapGenConstants.APPLN_NAME_STR_SALESPRO, "org.apache.cordova.phonegap", argumnts[0],"", "org.apache.cordova.phonegap.example", argumnts[1], request, now);
     	SapQueueProcessorHelperConstants.sendOfflineContentToQueueProcessor(context, SapGenConstants.APPLN_NAME_STR_SALESPRO, "org.apache.cordova.phonegap", contactIDStr,"0", "org.apache.cordova.phonegap.example", argumnts[1], request, now,"Cphonegap");
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
}