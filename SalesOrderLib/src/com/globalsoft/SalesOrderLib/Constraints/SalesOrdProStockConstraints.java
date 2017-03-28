package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesOrdProStockConstraints implements KvmSerializable, Serializable{
    
	public String MAKTX = "";
    public String MEINS = "";
    public String LABST = "";
    public String UMLME = "";
    public String INSME = "";
    public String MATNR = "";
    public String WERKS_TEXT = "";
    public String LGOBE = "";
    public String WERKS = "";
    public String LGORT = "";
    
    public SalesOrdProStockConstraints (){}
    

    public SalesOrdProStockConstraints (String[] values) {
        MAKTX = values[0];
        MEINS = values[1];
        LABST = values[2];
        UMLME = values[3];
        INSME = values[4];
        MATNR = values[5];
        WERKS_TEXT = values[6];
        LGOBE = values[7];
        WERKS = values[8];
        LGORT = values[9];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return MAKTX;
            case 1:
                return MEINS;
            case 2:
                return LABST;
            case 3:
                return UMLME; 
            case 4:
                return INSME;
            case 5:
                return MATNR;
            case 6:
                return WERKS_TEXT;
            case 7:
                return LGOBE;
            case 8:
                return WERKS;
            case 9:
                return LGORT;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 10;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MAKTX";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MEINS";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LABST";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "UMLME";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "INSME";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATNR";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WERKS_TEXT";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LGOBE";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WERKS";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LGORT";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                MAKTX = value.toString();
                break;
            case 1:
                MEINS = value.toString();
                break;
            case 2:
                LABST = value.toString();
                break;
            case 3:
                UMLME = value.toString();
                break;
            case 4:
                INSME = value.toString();
                break;
            case 5:
                MATNR = value.toString();
                break;
            case 6:
                WERKS_TEXT = value.toString();
                break;
            case 7:
                LGOBE = value.toString();
                break;
            case 8:
                WERKS = value.toString();
                break;
            case 9:
            	LGORT = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getMattDesc(){
        return this.MAKTX;
    }//fn getMattDesc
    
    public String getMeasureUnit(){
        return this.MEINS;
    }//fn getMeasureUnit
    
    public String getStock(){
        return this.LABST;
    }//fn getStock
    
    public String getStockInTransfer(){
        return this.UMLME;
    }//fn getStockInTransfer
    
    public String getStockInQualityInsp(){
        return this.INSME;
    }//fn getStockInQualityInsp
    
    public String getMaterialNo(){
        return this.MATNR;
    }//fn getMaterialNo
    
    public String getPlantDesc(){
        return this.WERKS_TEXT;
    }//fn getPlantDesc
    
    public String getStorageLocationDesc(){
        return this.LGOBE;
    }//fn getStorageLocationDesc
    
    public String getPlant(){
        return this.WERKS;
    }//fn getPlant
    
    public String getStorageLocation(){
        return this.LGORT;
    }//fn getStorageLocation
    
}//End of class SalesOrdProStockConstraints 

