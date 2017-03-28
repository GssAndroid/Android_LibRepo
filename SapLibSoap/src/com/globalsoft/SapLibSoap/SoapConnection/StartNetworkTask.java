package com.globalsoft.SapLibSoap.SoapConnection;

import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public class StartNetworkTask extends AsyncTask<SoapSerializationEnvelope, Integer, SoapObject> {
	
	private ProgressDialog pdialog = null;	
	
	private Context mContext;
	private String exceptionStr = "";
	
	public StartNetworkTask(Context context){
		mContext = context;
		exceptionStr = "";
		pdialog = new ProgressDialog(context);
		startProgressDialog();
	}
	
	public StartNetworkTask(Context context, boolean flag){
		mContext = context;
		exceptionStr = "";
	}
	
	protected void onPreExecute() {
        // We could do some setup work here before doInBackground() runs
		//startProgressDialog();
    }
	
	protected SoapObject doInBackground(SoapSerializationEnvelope... envelopeC) {
		//It accepts parameters as array so use first param
		return getSOAPResponseViaHTTP(envelopeC[0]);
	}

	protected void onProgressUpdate(Integer... progress) {
		//do any progress update
		SapGenConstants.showLog("Progress : "+progress);
    }

	protected void onPostExecute(SoapObject resultSoap) {
		if(!exceptionStr.equalsIgnoreCase("")){
			//SapGenConstants.showErrorDialog(mContext, exceptionStr);
		}
    }
	
	private SoapObject getSOAPResponseViaHTTP(SoapSerializationEnvelope envelopeCE){
		SoapObject resultSoap = null;
		try {                
        	HttpTransportSE  androidHttpTransport = new HttpTransportSE (SapGenConstants.SOAP_SERVICE_URL);
            try{
            	androidHttpTransport.call(SapGenConstants.SOAP_ACTION_URL, envelopeCE);
            }
            catch(org.xmlpull.v1.XmlPullParserException ex2){
            	exceptionStr = ex2.toString();
            	SapGenConstants.showErrorLog("Data handling error : "+exceptionStr);
                envelopeCE = null;
                resultSoap = null;
            }
            catch(IOException oex){
            	exceptionStr = oex.toString();
                SapGenConstants.showErrorLog("Network error : "+exceptionStr);
                envelopeCE = null;
                resultSoap = null;
            }
            catch(Exception ex){
            	exceptionStr = ex.toString();
            	SapGenConstants.showErrorLog("Error in Sap Resp : "+exceptionStr);
                envelopeCE = null;
                resultSoap = null;
            }
            
            if(envelopeCE != null){
                try{
                    if(envelopeCE.bodyIn != null)
                    	resultSoap = (SoapObject)envelopeCE.bodyIn; 
                    else
                    	resultSoap = null;
                }
                catch(Exception dgg){
                	SapGenConstants.showErrorLog("Error in Envelope Null : "+dgg.toString());
                }
            }
        }
        catch (Throwable e) {
        	SapGenConstants.showErrorLog("Error in getSOAPResponseViaHTTP : "+e.toString());
        	resultSoap = null;
        }
		finally{
			SapGenConstants.showLog("stopProgressDialog calling before in finally");
			stopProgressDialog();
			SapGenConstants.showLog("stopProgressDialog calling after in finally");
		}
		return resultSoap;
	}//fn getSOAPResponseViaHTTP
	
	private void startProgressDialog(){
		try {
			//SapGenConstants.showLog("Inside Progress");
			/*if(pdialog != null){
				pdialog = null;
			}
			
			pdialog = ProgressDialog.show(mContext, "", "Please wait while processing...", true);*/
			//this.pdialog.setMessage("Please wait while processing...");
			this.pdialog.setMessage("Contacting Server...");
			pdialog.setIndeterminate(true);
			pdialog.setCancelable(false);
			pdialog.show();


			//SapGenConstants.showLog("Inside Progress 2");
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
		
}//End of class StartNetworkTask

