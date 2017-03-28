package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesOrdProPriceItemConstraints implements KvmSerializable, Serializable{
    
	public String POSNR = "";
    public String MATNR = "";
    public String KWMENG = "";
    public String VRKME = "";
    public String NETWR = "";
    public String WAERK = "";
    
    public SalesOrdProPriceItemConstraints (){}
    

    public SalesOrdProPriceItemConstraints (String[] values) {
        POSNR = values[0];
        MATNR = values[1];
        KWMENG = values[2];
        VRKME = values[3];
        NETWR = values[4];
        WAERK = values[5];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return POSNR;
            case 1:
                return MATNR;
            case 2:
                return KWMENG;
            case 3:
                return VRKME; 
            case 4:
                return NETWR;
            case 5:
                return WAERK;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 6;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "POSNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATNR";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KWMENG";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VRKME";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NETWR";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WAERK";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                POSNR = value.toString();
                break;
            case 1:
                MATNR = value.toString();
                break;
            case 2:
                KWMENG = value.toString();
                break;
            case 3:
                VRKME = value.toString();
                break;
            case 4:
                NETWR = value.toString();
                break;
            case 5:
                WAERK = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getItemNo(){
        return this.POSNR;
    }//fn getItemNo
    
    public String getMaterialNo(){
        return this.MATNR;
    }//fn getMaterialNo
    
    public String getCummOrder(){
        return this.KWMENG;
    }//fn getCummOrder
    
    public String getSalesUnit(){
        return this.VRKME;
    }//fn getSalesUnit
    
    public String getNetValue(){
        return this.NETWR;
    }//fn getNetValue
    
    public String getDocCurrency(){
        return this.WAERK;
    }//fn getDocCurrency
    
    
}//End of class SalesOrdProPriceItemConstraints 


