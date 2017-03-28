package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesOrdProCustConstraints implements KvmSerializable, Serializable{
    
	public String KUNNR = "";
    public String NAME1 = "";
    public String LAND1 = "";
    public String REGIO = "";
    public String ORT01 = "";
    public String STRAS = "";
    public String TELF1 = "";
    public String TELF2 = "";
    public String SMTP_ADDR = "";
    
    public SalesOrdProCustConstraints (){}
    

    public SalesOrdProCustConstraints (String[] values) {
        KUNNR = values[0];
        NAME1 = values[1];
        LAND1 = values[2];
        REGIO = values[3];
        ORT01 = values[4];
        STRAS = values[5];
        TELF1 = values[6];
        TELF2 = values[7];
        SMTP_ADDR = values[8];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return KUNNR;
            case 1:
                return NAME1;
            case 2:
                return LAND1;
            case 3:
                return REGIO; 
            case 4:
                return ORT01;
            case 5:
                return STRAS;
            case 6:
                return TELF1;
            case 7:
                return TELF2;
            case 8:
                return SMTP_ADDR;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 9;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME1";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIO";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRAS";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF1";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF2";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SMTP_ADDR";
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
                NAME1 = value.toString();
                break;
            case 2:
                LAND1 = value.toString();
                break;
            case 3:
                REGIO = value.toString();
                break;
            case 4:
                ORT01 = value.toString();
                break;
            case 5:
                STRAS = value.toString();
                break;
            case 6:
                TELF1 = value.toString();
                break;
            case 7:
                TELF2 = value.toString();
                break;
            case 8:
                SMTP_ADDR = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getCustomerNo(){
        return this.KUNNR;
    }//fn getCustomerNo
    
    public String getName(){
        return this.NAME1;
    }//fn getName
    
    public String getCountry(){
        return this.LAND1;
    }//fn getCountry
    
    public String getRegion(){
        return this.REGIO;
    }//fn getRegion
    
    public String getCity(){
        return this.ORT01;
    }//fn getCity
    
    public String getStreet(){
        return this.STRAS;
    }//fn getStreet
    
    public String getTelNo1(){
        return this.TELF1;
    }//fn getTelNo1
    
    public String getTelNo2(){
        return this.TELF2;
    }//fn getTelNo2
    
    public String getEmail(){
        return this.SMTP_ADDR;
    }//fn getEmail
    
                
}//End of class SalesOrdProCustConstraints 

