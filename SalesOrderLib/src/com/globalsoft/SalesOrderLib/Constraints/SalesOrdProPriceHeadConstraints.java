package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class SalesOrdProPriceHeadConstraints implements KvmSerializable, Serializable {
    
	public String KUNAG = "";
    public String KETDAT = "";
    public String NETWR = "";
    public String WAERK = "";
    
    public SalesOrdProPriceHeadConstraints (){}
    

    public SalesOrdProPriceHeadConstraints (String[] values) {
        KUNAG = values[0];
        KETDAT = values[1];
        NETWR = values[2];
        WAERK = values[3];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return KUNAG;
            case 1:
                return KETDAT;
            case 2:
                return NETWR;
            case 3:
                return WAERK; 
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 38;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNAG";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KETDAT";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NETWR";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WAERK";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                KUNAG = value.toString();
                break;
            case 1:
                KETDAT = value.toString();
                break;
            case 2:
                NETWR = value.toString();
                break;
            case 3:
                WAERK = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getSoldToParty(){
        return this.KUNAG;
    }//fn getSoldToParty
    
    public String getReqstdDate(){
        return this.KETDAT;
    }//fn getReqstdDate
    
    public String getNetValue(){
        return this.NETWR;
    }//fn getNetValue
    
    public String getDocCurrency(){
        return this.WAERK;
    }//fn getDocCurrency
                
}//End of class SalesOrdProPriceHeadConstraints 
