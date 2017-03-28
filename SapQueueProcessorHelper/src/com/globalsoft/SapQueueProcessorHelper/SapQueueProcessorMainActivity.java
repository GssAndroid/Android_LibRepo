package com.globalsoft.SapQueueProcessorHelper;



import com.globalsoft.SapQueueProcessorHelper.Database.SapQueueProcessorContentProvider;
import com.globalsoft.SapQueueProcessorHelper.Utils.SapQueueProcessorHelperConstants;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;

public class SapQueueProcessorMainActivity extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.getApplicationContext().getContentResolver().registerContentObserver (SapQueueProcessorContentProvider.CONTENT_URI, true, new SapQueueProcessorDBContentObserver());
    }
    
    private class SapQueueProcessorDBContentObserver extends ContentObserver {

        public SapQueueProcessorDBContentObserver() {
            super(null);
            SapQueueProcessorHelperConstants.showLog("DB Content Observer Constructor Called");
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            SapQueueProcessorHelperConstants.showLog("DB Content Observer OnChange Called");
        }

    }//End of class SapQueueProcessorDBContentObserver

    
}//End of class SapQueueProcessorMainActivity