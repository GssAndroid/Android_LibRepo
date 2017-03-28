package com.globalsoft.SalesPro.Service;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;

import com.globalsoft.ContactLib.AlrtActivity;
import com.globalsoft.ContactLib.ContactMain;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationKeyOpConstraints;
import com.globalsoft.ContactLib.Constraints.ContactProContactCreationOpConstraints;
import com.globalsoft.ContactLib.Database.ContactProSAPCPersistent;
import com.globalsoft.ContactLib.Utils.ContactsConstants;
import com.globalsoft.SalesOrderLib.SalesOrderCreation;
import com.globalsoft.SalesPro.Database.SalesProErrorMessagePersistent;
import com.globalsoft.SalesPro.Utils.SalesOrderProConstants;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

public class BackgroundService extends Service {	
	private SalesProErrorMessagePersistent errorDbObj = null;
	private String tracId="", errType="", errorDesc="", apiName="";
	private int colIdVal = -1;
	int count = 0;
	int i = 0;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private PendingIntent contentIntent;
	private Context context;
	private int dispwidth = 300;
	private static ArrayList cusList = new ArrayList();
    private static ArrayList sapCusData = new ArrayList();
    private static ArrayList cusListKey = new ArrayList();
    private ContactProSAPCPersistent contactProCusDbObj = null;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		ContactsConstants.showLog("onCreate");
		//dispwidth = ServiceProConstants.getDisplayWidth(this);
	}

	@Override
	public void onDestroy() {
		ContactsConstants.showLog("onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		ContactsConstants.showLog("onStart");
		try{
			int colId = intent.getIntExtra(ContactsConstants.QUEUE_COLID, -1);
			String applRefId = intent.getStringExtra(ContactsConstants.QUEUE_APPREFID);
			String applName = intent.getStringExtra(ContactsConstants.QUEUE_APPLNAME);
			String soapAPIName = intent.getStringExtra(ContactsConstants.QUEUE_SOAPAPINAME);
			byte[] soapBytes = intent.getByteArrayExtra(ContactsConstants.QUEUE_RESULTSOAPOBJ);
			SoapObject soapObj = SapQueueProcessorHelperConstants.getDeSerializableSoapObject(soapBytes);
			colIdVal = colId;
			tracId =  applRefId;
			apiName = soapAPIName;
			ContactsConstants.showLog("colId:"+colIdVal);
			ContactsConstants.showLog("applRefId:"+applRefId);
			ContactsConstants.showLog("applName:"+applName);
			ContactsConstants.showLog("soapAPIName:"+soapAPIName);
			ContactsConstants.showLog("soapObj:"+soapObj.toString());
			getSoapResponse(soapObj);
		}
		catch(Exception sff){
			ContactsConstants.showErrorLog("Error in BgProcess : "+sff.toString());
        }	
	}
	
	public void getSoapResponse(SoapObject soap){
		String taskErrorMsgStr="";
		boolean resMsgErr = false;
        if(soap != null){
            try{
            	soapResponse(this, soap, true);
            	taskErrorMsgStr = SalesOrderProConstants.SOAP_RESP_MSG;
            	errorDesc = taskErrorMsgStr;
            	errType = SalesOrderProConstants.SOAP_ERR_TYPE;
            	String soapMsg = soap.toString();
            	resMsgErr = SalesOrderProConstants.getSoapResponseSucc_Err(soapMsg); 
            }
            catch(Exception sff){
            	SalesOrderProConstants.showErrorLog("On gettingSoapResponse : "+sff.toString());
            }           
            try{
            	//if(taskErrorMsgStr.toLowerCase().indexOf("maintained") > 0){
            	if(!resMsgErr){
            		try{
            			if(colIdVal > 0){
            				SapQueueProcessorHelperConstants.updateSelectedRowStatus(this, colIdVal, SalesOrderProConstants.APPLN_NAME_STR, ContactsConstants.STATUS_COMPLETED);
            			}else{
            				SapQueueProcessorHelperConstants.updateSelectedRowStatus(this, 0, SalesOrderProConstants.APPLN_NAME_STR, ContactsConstants.STATUS_COMPLETED);
            			}
            		}
                    catch(Exception sff1){
                    	SalesOrderProConstants.showErrorLog("Error in updating queue process database: "+sff1.toString());
                    }       
            	}
            	else{            		
            		CharSequence contentTitle = "ContactPro notifications";
            		CharSequence contentText = errorDesc;    		
            		
            		if(apiName.equals(ContactsConstants.CONTACT_MAINTAIN_API)){
            			contentText = "Error in Contact updates during queue processing!";
            		}else{
            			contentText = errorDesc;
            		}           		
            		
            		/*int uniqueIdVal = (int) (Integer.parseInt(tracId) + System.currentTimeMillis());
            		//ServiceProConstants.showLog("uniqueIdVal:"+uniqueIdVal); 
            		Intent notificationIntent = new Intent(Intent.ACTION_MAIN); 
            		
      				if(dispwidth > SalesOrderProConstants.SCREEN_CHK_DISPLAY_WIDTH){
      					notificationIntent.setClass(getApplicationContext(), ServiceProTaskMainScreenForTablet.class);
      				}else{
      					notificationIntent.setClass(getApplicationContext(), ServiceProTaskMainScreenForPhone.class);
      				} 
            		//notificationIntent.setClass(getApplicationContext(), ServiceProTaskMainScreen.class);    		
            		notificationIntent.putExtra(ServiceProConstants.QUEUE_COLID, colIdVal);
            		notificationIntent.putExtra(ServiceProConstants.QUEUE_ERR_APPREFID, tracId);
            		notificationIntent.putExtra(ServiceProConstants.QUEUE_ERR_MSG, errorDesc);
            		//notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		notificationIntent.setAction("action_id_" + uniqueIdVal);
            		contentIntent = PendingIntent.getActivity(context, uniqueIdVal, notificationIntent, 0);
            		//contentIntent = PendingIntent.getActivity(context, uniqueIdVal, notificationIntent, 0);
            		//contentIntent = PendingIntent.getActivity(context, ServiceProConstants.TASKLIST_SCREEN, notificationIntent, 0);
            		//contentIntent = PendingIntent.getActivity(context, ServiceProConstants.TASKLIST_SCREEN, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);            		
            		mNotificationManager.notify(colIdVal, notification);     
            		//mNotificationManager.notify(0, notification);*/
            		
            		boolean apiExists = false;
            		if(errorDbObj == null)
	        			errorDbObj = new SalesProErrorMessagePersistent(this.getApplicationContext());
            		if(errorDbObj != null){
            			apiExists = errorDbObj.checkTrancIdApiExists(tracId.trim(), apiName.trim());
				    	//ServiceProConstants.showLog("apiExists : "+apiExists);
				    	if(!apiExists){
				    		errorDbObj.insertErrorMsgDetails(tracId, errType, errorDesc, apiName);
				    	} 
				    	else{
				    		 errorDbObj.updateValue(tracId, errType, errorDesc, apiName);
				    	}	            		
            		}
            		if(errorDbObj != null)
	        			errorDbObj.closeDBHelper();	
            	}
            }
            catch(Exception asf){
            	SalesOrderProConstants.showErrorLog("Bg process in inserting databases : "+asf.toString());
            	if(errorDbObj != null)
        			errorDbObj.closeDBHelper();	 
            }
        }
    }//fn soapResponse
	
	public void soapResponse(Context ctx, SoapObject soap, boolean offline){
		String taskErrorMsgStr="", errorDesc="", errType="", strAddContactSAPCusFName="", strAddContactSAPCusLName ="";
		boolean listDisplay = false;
		if(soap != null){ 
        	try{
        		ContactsConstants.showLog("Count : "+soap.getPropertyCount());
        		ContactProContactCreationOpConstraints category = null;
        		ContactProContactCreationKeyOpConstraints categoryKey = null;
	            if(cusList != null){
	            	cusList.clear();
	            	sapCusData.clear();
	            }
	            if(cusListKey != null)
	            	cusListKey.clear();
	            	            
	            String delimeter = "[.]", result="", res="", docTypeStr = "";
	            SoapObject pii = null;
	            String[] resArray = new String[37];
	            int propsCount = 0, indexA = 0, indexB = 0, firstIndex = 0, resC = 0, eqIndex = 0;
	            
	            for (int i = 0; i < soap.getPropertyCount(); i++) {                
	                pii = (SoapObject)soap.getProperty(i);
	                propsCount = pii.getPropertyCount();
	                ContactsConstants.showLog("propsCount : "+propsCount);
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
	                            
	                            if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CMPNYCNTCTDATA01")){
	                                if(category != null)
	                                    category = null;
	                                    
	                                category = new ContactProContactCreationOpConstraints(resArray);	
	                                if(category != null){    
	                                	cusList.add(category.getKunnr().trim()+", "+category.getName1().trim()+", "+category.getOrto1k().trim()+", "+category.getRegiok().trim()+", "+category.getLand1k().trim());
	                                }
	                                if(category != null)
	                                	sapCusData.add(category);

	                    			ContactsConstants.showLog("cusList getKunnr in service : "+category.getKunnr());
	                    			ContactsConstants.showLog("cusList getName1 in service : "+category.getName1());
	                    			
	                    			if(contactProCusDbObj == null){
	                        			contactProCusDbObj = new ContactProSAPCPersistent(ctx);
	                        		}
	                    			//getting data from local device DB 
                    				contactProCusDbObj.getContactDetails(String.valueOf(tracId));
                    				strAddContactSAPCusFName = ContactsConstants.CONTACTSAPCUSFNAME;
                    				strAddContactSAPCusLName = ContactsConstants.CONTACTSAPCUSLNAME;
                    				contactProCusDbObj.closeDBHelper();
                    				
	                    			listDisplay = true;
	                            }	  
	                            else if(docTypeStr.equalsIgnoreCase("ZGSXCAST_CNTCTKEY")){
	                                if(categoryKey != null)
	                                	categoryKey = null;
	                                    
	                                categoryKey = new ContactProContactCreationKeyOpConstraints(resArray);	
	                                if(cusListKey != null)
	                                	cusListKey.add(categoryKey);	                       
	                                
	                    			
	                    			if(contactProCusDbObj == null){
	                        			contactProCusDbObj = new ContactProSAPCPersistent(ctx);
	                        		}
	                    			
	                    			//getting data from local device DB 
                    				contactProCusDbObj.getContactDetails(tracId);
                    				strAddContactSAPCusFName = ContactsConstants.CONTACTSAPCUSFNAME;
                    				strAddContactSAPCusLName = ContactsConstants.CONTACTSAPCUSLNAME;
                    				
                    				ContactsConstants.showLog("cusListKey getKunnr in service : "+categoryKey.getKunnr());
	                    			ContactsConstants.showLog("cusListKey getParnr in service : "+categoryKey.getParnr());
                    				ContactsConstants.showLog("strAddContactSAPCusFName in service : "+strAddContactSAPCusFName);
                    				ContactsConstants.showLog("strAddContactSAPCusLName in service : "+strAddContactSAPCusLName);
                    				ContactsConstants.showLog("tracId in service : "+tracId);
                    				
                    				//updating data to local device DB
                    				contactProCusDbObj.update_data(tracId, categoryKey.getParnr().toString().trim(), 
                    						categoryKey.getKunnr().toString().trim(), strAddContactSAPCusFName.trim(), 
                    						strAddContactSAPCusLName.trim());
                    				contactProCusDbObj.closeDBHelper();
                    				
                    				/*ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(); 		                               
	                                //------------------------------------------------------ First Names 
	                                if(strAddContactSAPCusFName.length() != 0) 
	                                { 
		                                String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
	                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
	                            		String[] phoneArgs = new String[]{String.valueOf(tracId)}; 
	                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
	                            		.withSelection(selectPhone, phoneArgs) 
	                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strAddContactSAPCusFName+" "+strAddContactSAPCusLName+"                    "+categoryKey.getParnr()).build()); 
	                            	}
	                                else{	                                	
		                                	String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='" + 
		                            		ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'"; 
		                            		String[] phoneArgs = new String[]{String.valueOf(tracId)}; 
		                            		ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI) 
		                            		.withSelection(selectPhone, phoneArgs) 
		                            		.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Unknown"+"                    "+categoryKey.getParnr()).build());
	                                	try 
		                                { 
		                                	ContentProviderResult[] ress = ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
		                                	if(ress != null){
		                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                        		
		                                	}		                                	
		                                } 
		                                catch (Exception e) 
		                                { 
		                                	ContactsConstants.showErrorLog("Error in ressDISPLAY_NAME:"+e.toString());
		                                }
	                                }
	                                try 
	                                { 
	                                	ContentProviderResult[] ress = ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops); 	
	                                	if(ress != null){
	                                		ContactsConstants.showLog("ressDISPLAY_NAME:"+ress[0]);                            		
	                                	}		                                	
	                                } 
	                                catch (Exception e) 
	                                { 
	                                	ContactsConstants.showErrorLog("Error in ressDISPLAY_NAME:"+e.toString());
	                                }*/
	                            }	  
	                        }
                            else if(j == 0){
                                String errorMsg = pii.getProperty(j).toString();
                                //ServiceProConstants.showLog("Inside J == 0 ");
	                            int errorFstIndex = errorMsg.indexOf("Message=");
	                            if(errorFstIndex > 0){
	                            	int errorLstIndex = errorMsg.indexOf(";", errorFstIndex);
	                                taskErrorMsgStr = errorMsg.substring((errorFstIndex+"Message=".length()), errorLstIndex);
	                                //ServiceProConstants.showLog("Msg:"+taskErrorMsgStr);
	                                if(!offline)
	                                	SalesOrderProConstants.showErrorDialog(ctx, taskErrorMsgStr);
	                            	errorDesc = taskErrorMsgStr;	
	                            	SalesOrderProConstants.SOAP_RESP_MSG = errorDesc; 
                                }
	                            int typeFstIndex = errorMsg.indexOf("Type=");
	                            if(typeFstIndex > 0){
	                            	int typeLstIndex = errorMsg.indexOf(";", typeFstIndex);
	                                String taskErrorTypeMsgStr = errorMsg.substring((typeFstIndex+"Type=".length()), typeLstIndex);
	                                //ServiceProConstants.showLog("Type:"+taskErrorTypeMsgStr);
	                                errType = taskErrorTypeMsgStr;
	                                SalesOrderProConstants.SOAP_ERR_TYPE = errType;
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception sff){
            	SalesOrderProConstants.showErrorLog("On soapResponse : "+sff.toString());
            }
        	finally{   	 
            	if(listDisplay){
        			Intent intent = new Intent(this, AlrtActivity.class);	
        			intent.putExtra("queueProcess", true);
        			intent.putExtra("FName", strAddContactSAPCusFName);
        			intent.putExtra("LName", strAddContactSAPCusLName);
        			intent.putExtra("ContactId", String.valueOf(tracId));
        	    	intent.putStringArrayListExtra("cusList", cusList);
        	    	intent.putStringArrayListExtra("sapCusData", sapCusData);
        			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			this.getApplicationContext().startActivity(intent); 
            	}
            }
        }
    }//fn soapResponse
}