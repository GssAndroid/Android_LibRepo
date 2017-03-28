package com.globalsoft.SapLibActivity.Service;

import org.ksoap2.serialization.SoapObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.globalsoft.SapLibActivity.Database.ActDBOperations;
import com.globalsoft.SapLibActivity.Utils.CrtGenActivityConstants;
import com.globalsoft.SapLibSoap.Constraints.SapGenDocKeyOpConstraints;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class ActivityBGService extends Service {	
	
	private String uniqId="", errorDesc="", apiName="";
	private int colIdVal = -1;
	int count = 0;
	int i = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		SapGenConstants.showLog("onCreate:ActivityBGService");
	}

	@Override
	public void onDestroy() {
		SapGenConstants.showLog("onDestroy:ActivityBGService");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		SapGenConstants.showLog("onStart:ActivityBGService");
		try{
			getApplicationContext();
    		int colId = intent.getIntExtra(SapGenConstants.QUEUE_COLID, -1);
			String applRefId = intent.getStringExtra(SapGenConstants.QUEUE_APPREFID);
			String applName = intent.getStringExtra(SapGenConstants.QUEUE_APPLNAME);
			String soapAPIName = intent.getStringExtra(SapGenConstants.QUEUE_SOAPAPINAME);
			byte[] soapBytes = intent.getByteArrayExtra(SapGenConstants.QUEUE_RESULTSOAPOBJ);
			SoapObject soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
			colIdVal = colId;
			uniqId =  applRefId;
			apiName = soapAPIName;
			SapGenConstants.showLog("colId:"+colIdVal);
			SapGenConstants.showLog("applRefId:"+applRefId);
			SapGenConstants.showLog("applName:"+applName);
			SapGenConstants.showLog("soapAPIName:"+soapAPIName);
			SapGenConstants.showLog("soapObj:"+soapObj.toString());
			getSoapResponse(soapObj);
			//Toast.makeText(this, "Application Name:"+applName+"\n SOAPAPI Name:"+soapAPIName+"\n ResultSoapObj:"+soapObj.toString()+"\n", Toast.LENGTH_LONG).show();
		}
		catch(Exception sff){
        	SapGenConstants.showErrorLog("Error in BgProcess : "+sff.toString());
        }		
	}
	
	public void getSoapResponse(SoapObject soap){
		String taskErrorMsgStr="";
		boolean resMsgErr = false;
		SapGenDocKeyOpConstraints categoryObj = null;
        if(soap != null){
            try{
            	SapGenConstants.soapResponse(this, soap, true);
            	taskErrorMsgStr = SapGenConstants.SOAP_RESP_MSG;
            	errorDesc = taskErrorMsgStr;
            	String soapMsg = soap.toString();
            	resMsgErr = SapGenConstants.getSoapResponseMessageType(soapMsg); 
            	
            	String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[20];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                SapGenConstants.showLog("propsCount : "+propsCount);
	                if(propsCount > 0){
	                    for (int j = 0; j < propsCount; j++) {
	                        if(j > 2){
	                            result = pii.getProperty(j).toString();
	                            firstIndex = result.indexOf(delimeter);
	                            eqIndex = result.indexOf("=");
	                            eqIndex = eqIndex+1;
	                            firstIndex = firstIndex + 3;
	                            docTypeStr = result.substring(eqIndex, (firstIndex-3));
	                            result = result.substring(firstIndex);
	                            
	                            resC = 0;
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
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_DCMNTKEY")){
	                                if(categoryObj != null)
	                                	categoryObj = null;	                                
	                                categoryObj = new SapGenDocKeyOpConstraints(resArray);
	                                
	                                String objectIdVal = categoryObj.getObjectId().toString().trim();
	                                if(objectIdVal != null && objectIdVal.length() > 0){
	                                	SapGenConstants.showLog("objectIdVal:"+objectIdVal);
	                                	SapGenConstants.showLog("uniqId:"+uniqId);
		            	    			ActDBOperations.updateObjectIdValueToGal(this, objectIdVal, uniqId);
		            	    			ActDBOperations.updateObjectIdValueToAct(this, objectIdVal, uniqId);
	                                }
	                            }
	                             
	                        }
	                    }
	                }
	            }//for
            }
            catch(Exception sff){
            	SapGenConstants.showErrorLog("On gettingSoapResponse : "+sff.toString());
            }           
            try{
    			SapGenConstants.showLog("resMsgErr:"+resMsgErr);
            	SapGenConstants.showLog("apiName:"+apiName);
            	SapGenConstants.showLog("ACT_EDIT_ADD_API:"+CrtGenActivityConstants.ACT_EDIT_ADD_API);
            	if(!resMsgErr){
            		try{
            			if(apiName.equals(CrtGenActivityConstants.ACT_EDIT_ADD_API)){
	            			if(colIdVal > 0){
	                        	SapGenConstants.showLog("colIdVal > 0:"+colIdVal);
	            				SapQueueProcessorHelperConstants.updateSelectedRowStatusForServicePro(this, colIdVal, SapGenConstants.APPLN_NAME_STR_MOBILEPRO, SapGenConstants.STATUS_COMPLETED, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
	            			}else{
	                        	SapGenConstants.showLog("else part!");
	            				SapQueueProcessorHelperConstants.updateSelectedRowStatusForServicePro(this, 0, SapGenConstants.APPLN_NAME_STR_MOBILEPRO, SapGenConstants.STATUS_COMPLETED, CrtGenActivityConstants.CRTACT_CALLING_APP_NAME);
	            			}
            			}
            		}
                    catch(Exception sff1){
                    	SapGenConstants.showErrorLog("Error in updating queue process database: "+sff1.toString());
                    }       
            	}
            	else{            		
            		CharSequence contentText = errorDesc; 
            		SapGenConstants.showLog("contentText:"+contentText);	 
            	}
            }
            catch(Exception asf){
            	SapGenConstants.showErrorLog("Bg process in inserting databases : "+asf.toString());
            }
        }
    }//fn soapResponse
	
	/*@Override
	public void onStart(Intent intent, int startid) {
		SapGenConstants.showLog("onStart");
		try{
			colIdVal = intent.getIntExtra(SapGenConstants.QUEUE_COLID, -1);
			appRefId = intent.getIntExtra(SapGenConstants.QUEUE_APPREFID, -1);
			appNameStr = intent.getStringExtra(SapGenConstants.QUEUE_APPLNAME);
			soapApiName = intent.getStringExtra(SapGenConstants.QUEUE_SOAPAPINAME);
			byte[] soapBytes = intent.getByteArrayExtra(SapGenConstants.QUEUE_RESULTSOAPOBJ);
			
			//SoapObject soapObj = SapGenConstants.getDeSerializableSoapObject(soapBytes);
			
			SapGenConstants.showLog("colId: "+colIdVal);
			SapGenConstants.showLog("applRefId: "+appRefId);
			SapGenConstants.showLog("applName: "+appNameStr);
			SapGenConstants.showLog("soapAPIName: "+soapApiName);
			
			
			SapGenConstants.showLog("soapObj:"+soapObj.toString());
			
			getSoapResponse(soapObj);
			//Toast.makeText(this, "Application Name:"+applName+"\n SOAPAPI Name:"+soapAPIName+"\n ResultSoapObj:"+soapObj.toString()+"\n", Toast.LENGTH_LONG).show();
		}
		catch(Exception sff){
        	SapGenConstants.showErrorLog("Error in BgProcess : "+sff.toString());
        }		
	}	
	
	public void getSoapResponse(SoapObject soap){
		String taskErrorMsgStr="";
        if(soap != null){
            try{
            	SapGenConstants.soapResponse(this, soap, true);
            	taskErrorMsgStr = SapGenConstants.SOAP_RESP_MSG;
            	errorDesc = taskErrorMsgStr;
            	errType = SapGenConstants.SOAP_ERR_TYPE;
            }
            catch(Exception sff){
            	SapGenConstants.showErrorLog("On gettingSoapResponse : "+sff.toString());
            }           
            try{
            	if(taskErrorMsgStr.toLowerCase().indexOf("maintained") > 0){
            		SapGenConstants.showLog("maintained");            		
            		SapGenConstants.showLog("tracId:"+tracId);
            		SapGenConstants.showLog("errType:"+errType);
            		SapGenConstants.showLog("errorDesc:"+errorDesc);
            		SapGenConstants.showLog("apiName:"+apiName);
            		SapGenConstants.showLog("colIdVal:"+colIdVal);
            		try{
            			if(colIdVal > 0){
                			SapGenConstants.updateSelectedRowStatus(this, colIdVal, SapGenConstants.APPLN_NAME_STR, SapGenConstants.STATUS_COMPLETED);
            			}else{
            				SapGenConstants.updateSelectedRowStatus(this, 0, SapGenConstants.APPLN_NAME_STR, SapGenConstants.STATUS_COMPLETED);
            			}
            		}
                    catch(Exception sff1){
                    	SapGenConstants.showErrorLog("Error in updating queue process database: "+sff1.toString());
                    }       
            	}
            	else{            		
            		SapGenConstants.showLog("Storing Error to Error table");            		
            		SapGenConstants.showLog("tracId:"+tracId);
            		SapGenConstants.showLog("errType:"+errType);
            		SapGenConstants.showLog("errorDesc:"+errorDesc);
            		SapGenConstants.showLog("apiName:"+apiName);
            		SapGenConstants.showLog("colIdVal:"+colIdVal);            		            		
            		
            		CharSequence contentTitle = "ServicePro notifications";
            		CharSequence contentText = errorDesc;    		
            		
            		if(apiName.equals(SapGenConstants.STATUS_UPDATE_API)){
            			contentText = "Error in Status updates during queue processing!";
            		}else{
            			contentText = errorDesc;
            		}
            	}
            }
            catch(Exception asf){
            	SapGenConstants.showErrorLog("Bg process in inserting databases : "+asf.toString());
            }
        }
    }//fn soapResponse
*/	
	
}//End of class ActivityBGService
