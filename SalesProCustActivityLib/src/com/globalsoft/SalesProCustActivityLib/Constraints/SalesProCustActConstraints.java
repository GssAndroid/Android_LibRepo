package com.globalsoft.SalesProCustActivityLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProCustActConstraints implements KvmSerializable, Serializable {
    
	public String KUNNR = "";
    public String KUNNR_NAME = "";
    public String PARNR = "";
    public String PARNR_NAME = "";
    public String OBJECT_ID = "";
    public String PROCESS_TYPE = "";
    public String DESCRIPTION = "";
    public String TEXT = "";
    public String DATE_FROM = "";
    public String DATE_TO = "";
    public String TIME_FROM = "";
    public String TIME_TO = "";
    public String TIMEZONE_FROM = "";
    public String DURATION_SEC = "";
    public String CATEGORY = "";
    public String STATUS = "";
    public String STATUS_TXT30 = "";
    public String STATUS_REASON = "";
    public String ZZSSRID = "";
    public String DOCUMENTTYPE_DESCRIPTION = "";
    public String POSTING_DATE = "";
    
    public SalesProCustActConstraints (){}
    

    public SalesProCustActConstraints (String[] values) {
        KUNNR = values[0];
        KUNNR_NAME = values[1];
        PARNR = values[2];
        PARNR_NAME = values[3];
        OBJECT_ID = values[4];
        PROCESS_TYPE = values[5];
        DESCRIPTION = values[6];
        TEXT = values[7];
        DATE_FROM = values[8];
        DATE_TO = values[9];
        TIME_FROM = values[10];
        TIME_TO = values[11];
        TIMEZONE_FROM = values[12];
        DURATION_SEC = values[13];
        CATEGORY = values[14];
        STATUS = values[15];
        STATUS_TXT30 = values[16];
        STATUS_REASON = values[17];
        ZZSSRID = values[18];
        DOCUMENTTYPE_DESCRIPTION = values[19];
        POSTING_DATE = values[20];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return KUNNR;
            case 1:
                return KUNNR_NAME;
            case 2:
                return PARNR;
            case 3:
                return PARNR_NAME; 
            case 4:
                return OBJECT_ID;
            case 5:
                return PROCESS_TYPE;
            case 6:
                return DESCRIPTION;
            case 7:
                return TEXT;
            case 8:
                return DATE_FROM;
            case 9:
                return DATE_TO;
            case 10:
                return TIME_FROM;
            case 11:
                return TIME_TO;
            case 12:
                return TIMEZONE_FROM;
            case 13:
                return DURATION_SEC;    
            case 14:
                return CATEGORY;
            case 15:
                return STATUS;
            case 16:
                return STATUS_TXT30;
            case 17:
                return STATUS_REASON;
            case 18:
                return ZZSSRID;
            case 19:
                return DOCUMENTTYPE_DESCRIPTION;
            case 20:
                return POSTING_DATE;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 21;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR_NAME";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR_NAME";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "OBJECT_ID";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROCESS_TYPE";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DESCRIPTION";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TEXT";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DATE_FROM";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DATE_TO";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIME_FROM";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIME_TO";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TIMEZONE_FROM";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DURATION_SEC";
                break;    
            case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CATEGORY";
                break;
            case 15:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STATUS";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STATUS_TXT30";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STATUS_REASON";
                break;
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ZZSSRID";
                break;
            case 19:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DOCUMENTTYPE_DESCRIPTION";
                break;
            case 20:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "POSTING_DATE";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                KUNNR = value.toString();
                break;
            case 1:
                KUNNR_NAME = value.toString();
                break;
            case 2:
                PARNR = value.toString();
                break;
            case 3:
                PARNR_NAME = value.toString();
                break;
            case 4:
                OBJECT_ID = value.toString();
                break;
            case 5:
                PROCESS_TYPE = value.toString();
                break;
            case 6:
                DESCRIPTION = value.toString();
                break;
            case 7:
                TEXT = value.toString();
                break;
            case 8:
                DATE_FROM = value.toString();
                break;
            case 9:
                DATE_TO = value.toString();
                break;
            case 10:
                TIME_FROM = value.toString();
                break;
            case 11:
                TIME_TO = value.toString();
                break;
            case 12:
                TIMEZONE_FROM = value.toString();
                break;
            case 13:
            	DURATION_SEC = value.toString();
                break;   
            case 14:
                CATEGORY = value.toString();
                break;
            case 15:
                STATUS = value.toString();
                break;
            case 16:
                STATUS_TXT30 = value.toString();
                break;
            case 17:
                STATUS_REASON = value.toString();
                break;
            case 18:
                ZZSSRID = value.toString();
                break;
            case 19:
            	DOCUMENTTYPE_DESCRIPTION = value.toString();
                break;
            case 20:
            	POSTING_DATE = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getCustomerId(){
        return this.KUNNR;
    }//fn getCustomerId
    
    public String getCustomerName(){
        return this.KUNNR_NAME;
    }//fn getCustomerName
    
    public String getContactId(){
        return this.PARNR;
    }//fn getContactId
    
    public String getContactName(){
        return this.PARNR_NAME;
    }//fn getContactName
    
    public String getObjectID(){
        return this.OBJECT_ID;
    }//fn getObjectID
    
    public String getProcessType(){
        return this.PROCESS_TYPE;
    }//fn getProcessType
    
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
    
    public String getStatus(){
        return this.STATUS;
    }//fn getStatus    
         
    public String getStatusTXT30(){
        return this.STATUS_TXT30;
    }//fn getStatusTXT30    
     
    public String getStatusReson(){
        return this.STATUS_REASON;
    }//fn getStatusReson    
     
    public String getZzssrid(){
        return this.ZZSSRID;
    }//fn getZzssrid 
    
   public String getDocumentTypeDesc(){
       return this.DOCUMENTTYPE_DESCRIPTION;
   }//fn getDocumentTypeDesc
   
   public String getPostingDate(){
       return this.POSTING_DATE;
   }//fn getPostingDate
      
}//End of class SalesProCustActConstraints 