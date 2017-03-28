package org.apache.cordova.phonegap;

import org.apache.cordova.DroidGap;
import org.apache.cordova.constants.Constants;
import org.json.JSONArray;
import org.ksoap2.serialization.SoapObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.globalsoft.SapLibSoap.Utils.SapGenConstants;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;
public class example extends DroidGap
{
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //String imei = Constants.getMobileIMEI(this);
        String imei = "000000000000000";
        Log.e("exampleActivity","inside example");
        super.loadUrl("file:///android_asset/www/home/index.html?imei="+imei,100);
    }//
}//

