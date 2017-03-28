/*
 * VanStkColStrOpConstraints.java
 *
 * © <your company here>, <year>
 * Confidential and proprietary.
 */

package com.globalsoft.SalesProCustActivityLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class CustomerListOpConstraints implements KvmSerializable, Serializable {
    
	public String NAME1 = "";
    public String CRBLB = "";
    public String CTLPC = "";
    public String KLIMK = "";
    public String OBLIG = "";
    public String WAERS = "";
    public String KLPRZ = "";
    public String DBRTG = "";
    public String KUNNR = "";
    public String CTLPC_TEXT = "";
    public String ORT01 = "";
    public String REGIO = "";
    public String LAND1 = "";
    
    public CustomerListOpConstraints (){}
    

    public CustomerListOpConstraints (String[] values) {
        NAME1 = values[0];
        CRBLB = values[1];
        CTLPC = values[2];
        KLIMK = values[3];
        OBLIG = values[4];
        WAERS = values[5];
        KLPRZ = values[6];
        DBRTG = values[7];
        KUNNR = values[8];
        CTLPC_TEXT = values[9];
        ORT01 = values[10];
        REGIO = values[11];
        LAND1 = values[12];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return NAME1;
            case 1:
                return CRBLB;
            case 2:
                return CTLPC;
            case 3:
                return KLIMK; 
            case 4:
                return OBLIG;
            case 5:
                return WAERS;
            case 6:
                return KLPRZ;
            case 7:
                return DBRTG;
            case 8:
                return KUNNR;
            case 9:
                return CTLPC_TEXT;
            case 10:
                return ORT01;
            case 11:
                return REGIO;
            case 12:
                return LAND1;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 13;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME1";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CRBLB";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CTLPC";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KLIMK";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "OBLIG";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WAERS";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KLPRZ";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DBRTG";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CTLPC_TEXT";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIO";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                NAME1 = value.toString();
                break;
            case 1:
                CRBLB = value.toString();
                break;
            case 2:
                CTLPC = value.toString();
                break;
            case 3:
                KLIMK = value.toString();
                break;
            case 4:
                OBLIG = value.toString();
                break;
            case 5:
                WAERS = value.toString();
                break;
            case 6:
                KLPRZ = value.toString();
                break;
            case 7:
                DBRTG = value.toString();
                break;
            case 8:
                KUNNR = value.toString();
                break;
            case 9:
                CTLPC_TEXT = value.toString();
                break;
            case 10:
                ORT01 = value.toString();
                break;
            case 11:
                REGIO = value.toString();
                break;
            case 12:
                LAND1 = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getName(){
        return this.NAME1;
    }//fn getName
    
    public String getBlockedByCreditMgmt(){
        return this.CRBLB;
    }//fn getBlockedByCreditMgmt
    
    public String getRiskCategory(){
        return this.CTLPC;
    }//fn getRiskCategory
    
    public String getCustCreditLimit(){
        return this.KLIMK;
    }//fn getCustCreditLimit
    
    public String getCreditExposure(){
        return this.OBLIG;
    }//fn getCreditExposure
    
    public String getCurrency(){
        return this.WAERS;
    }//fn getCurrency
    
    public String getCreditLimitUsed(){
        return this.KLPRZ;
    }//fn getCreditLimitUsed
    
    public String getRating(){
        return this.DBRTG;
    }//fn getRating
    
    public String getCustomerNo1(){
        return this.KUNNR;
    }//fn getCustomerNo1
    
    public String getRiskClassName(){
        return this.CTLPC_TEXT;
    }//fn getRiskClassName
    
    public String getCity(){
        return this.ORT01;
    }//fn getCity
    
    public String getRegion(){
        return this.REGIO;
    }//fn getRegion
    
    public String getCountry(){
        return this.LAND1;
    }//fn getCountry
    
    public String getFieldValue(String field){
    	String fvalue = "";
        try {
        	fvalue = this.getClass().getDeclaredField(field).toString().trim();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return fvalue;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return fvalue;
		}
        return fvalue;
    }//fn getCountry
                    
}//End of class CustomerListOpConstraints 

