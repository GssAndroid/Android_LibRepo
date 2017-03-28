package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProCustActConstraints implements KvmSerializable, Serializable {
    
	public String OBJECT_ID = "";
    public String PARNR = "";
    public String PARNR_NAME = "";
    public String KUNNR = "";
    public String KUNNR_NAME = "";
    public String DESCRIPTION = "";
    public String TEXT = "";
    public String DATE_FROM = "";
    public String DATE_TO = "";
    public String TIME_FROM = "";
    public String TIME_TO = "";
    public String TIMEZONE_FROM = "";
    public String DURATION_SEC = "";
    public String CATEGORY = "";
    public String PROCESS_TYPE = "";
    public String P_DESCRIPTION_20 = "";
    public String POSTING_DATE = "";
    public String STATUS = "";
    public String TXT30 = "";
    
    public SalesProCustActConstraints (){}
    

    public SalesProCustActConstraints (String[] values) {
        OBJECT_ID = values[0];
        PARNR = values[1];
        PARNR_NAME = values[2];
        KUNNR = values[3];
        KUNNR_NAME = values[4];
        DESCRIPTION = values[5];
        TEXT = values[6];
        DATE_FROM = values[7];
        DATE_TO = values[8];
        TIME_FROM = values[9];
        TIME_TO = values[10];
        TIMEZONE_FROM = values[11];
        DURATION_SEC = values[12];
        CATEGORY = values[13];
        PROCESS_TYPE = values[14];
        P_DESCRIPTION_20 = values[15];
        POSTING_DATE = values[16];
        STATUS = values[17];
        TXT30 = values[18];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return OBJECT_ID;
            case 1:
                return PARNR;
            case 2:
                return PARNR_NAME;
            case 3:
                return KUNNR; 
            case 4:
                return KUNNR_NAME;
            case 5:
                return DESCRIPTION;
            case 6:
                return TEXT;
            case 7:
                return DATE_FROM;
            case 8:
                return DATE_TO;
            case 9:
                return TIME_FROM;
            case 10:
                return TIME_TO;
            case 11:
                return TIMEZONE_FROM;
            case 12:
                return DURATION_SEC;
            case 13:
                return CATEGORY;    
            case 14:
                return PROCESS_TYPE;
            case 15:
                return P_DESCRIPTION_20;
            case 16:
                return POSTING_DATE;
            case 17:
                return STATUS;
            case 18:
                return TXT30;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 19;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "OBJECT_ID";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR_NAME";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR_NAME";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DESCRIPTION";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TEXT";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DATE_FROM";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DATE_TO";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIME_FROM";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIME_TO";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIMEZONE_FROM";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DURATION_SEC";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CATEGORY";
                break;    
            case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROCESS_TYPE";
                break;
            case 15:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "P_DESCRIPTION_20";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "POSTING_DATE";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STATUS";
                break;
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TXT30";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                OBJECT_ID = value.toString();
                break;
            case 1:
                PARNR = value.toString();
                break;
            case 2:
                PARNR_NAME = value.toString();
                break;
            case 3:
                KUNNR = value.toString();
                break;
            case 4:
                KUNNR_NAME = value.toString();
                break;
            case 5:
                DESCRIPTION = value.toString();
                break;
            case 6:
                TEXT = value.toString();
                break;
            case 7:
                DATE_FROM = value.toString();
                break;
            case 8:
                DATE_TO = value.toString();
                break;
            case 9:
                TIME_FROM = value.toString();
                break;
            case 10:
                TIME_TO = value.toString();
                break;
            case 11:
                TIMEZONE_FROM = value.toString();
                break;
            case 12:
                DURATION_SEC = value.toString();
                break;
            case 13:
            	CATEGORY = value.toString();
                break;   
            case 14:
                PROCESS_TYPE = value.toString();
                break;
            case 15:
                P_DESCRIPTION_20 = value.toString();
                break;
            case 16:
                POSTING_DATE = value.toString();
                break;
            case 17:
                STATUS = value.toString();
                break;
            case 18:
                TXT30 = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getObjectId(){
        return this.OBJECT_ID;
    }//fn getObjectId
    
    public String getContactId(){
        return this.PARNR;
    }//fn getContactId
    
    public String getContactName(){
        return this.PARNR_NAME;
    }//fn getContactName
    
    public String getCustomerId(){
        return this.KUNNR;
    }//fn getCustomerId
    
    public String getCustomerName(){
        return this.KUNNR_NAME;
    }//fn getCustomerName
    
    public String getDescription(){
        return this.DESCRIPTION;
    }//fn getDescription
    
    public String getText(){
        return this.TEXT;
    }//fn getText
    
    public String getDateFrom(){
        return this.DATE_FROM;
    }//fn getDateFrom
    
    public String getDateTo(){
        return this.DATE_TO;
    }//fn getDateTo
    
    public String getTimeFrom(){
        return this.TIME_FROM;
    }//fn getTimeFrom
    
    public String getTimeTo(){
        return this.TIME_TO;
    }//fn getTimeTo
    
    public String getTimeZoneFrom(){
        return this.TIMEZONE_FROM;
    }//fn getTimeZoneFrom
    
    public String getDurationSec(){
        return this.DURATION_SEC;
    }//fn getDurationSec
    
    public String getCategory(){
        return this.CATEGORY;
    }//fn getCategory
       
    public String getProcessType(){
        return this.PROCESS_TYPE;
    }//fn getProcessType
    
    public String getPDescription20(){
        return this.P_DESCRIPTION_20;
    }//fn getPDescription20    
         
    public String getPostingDate(){
        return this.POSTING_DATE;
    }//fn getPostingDate    
     
    public String getStatus(){
        return this.STATUS;
    }//fn getStatus    
     
    public String getTXT30(){
        return this.TXT30;
    }//fn getTXT30
      
}//End of class SalesProCustActConstraints 


