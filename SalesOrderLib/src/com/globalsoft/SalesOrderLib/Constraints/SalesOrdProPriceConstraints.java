package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesOrdProPriceConstraints implements KvmSerializable, Serializable{
    
	public String MAKTX = "";
    public String KBETR = "";
    public String KONWA = "";
    public String KPEIN = "";
    public String KMEIN = "";
    public String MATNR = "";
    public String KSCHL_TEXT = "";
    public String PLTYP_TEXT = "";
    public String KSCHL = "";
    public String PLTYP = "";
    
    public SalesOrdProPriceConstraints (){}
    

    public SalesOrdProPriceConstraints (String[] values) {
        MAKTX = values[0];
        KBETR = values[1];
        KONWA = values[2];
        KPEIN = values[3];
        KMEIN = values[4];
        MATNR = values[5];
        KSCHL_TEXT = values[6];
        PLTYP_TEXT = values[7];
        KSCHL = values[8];
        PLTYP = values[9];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return MAKTX;
            case 1:
                return KBETR;
            case 2:
                return KONWA;
            case 3:
                return KPEIN; 
            case 4:
                return KMEIN;
            case 5:
                return MATNR;
            case 6:
                return KSCHL_TEXT;
            case 7:
                return PLTYP_TEXT;
            case 8:
                return KSCHL;
            case 9:
                return PLTYP;
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
                info.name = "KBETR";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KONWA";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KPEIN";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KMEIN";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATNR";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KSCHL_TEXT";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PLTYP_TEXT";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KSCHL";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PLTYP";
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
                KBETR = value.toString();
                break;
            case 2:
                KONWA = value.toString();
                break;
            case 3:
                KPEIN = value.toString();
                break;
            case 4:
                KMEIN = value.toString();
                break;
            case 5:
                MATNR = value.toString();
                break;
            case 6:
                KSCHL_TEXT = value.toString();
                break;
            case 7:
                PLTYP_TEXT = value.toString();
                break;
            case 8:
                KSCHL = value.toString();
                break;
            case 9:
            	PLTYP = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getMattDesc(){
        return this.MAKTX;
    }//fn getMattDesc
    
    public String getRateAmount(){
        return this.KBETR;
    }//fn getRateAmount
    
    public String getRateunit(){
        return this.KONWA;
    }//fn getRateunit
    
    public String getCondPricingUnit(){
        return this.KPEIN;
    }//fn getCondPricingUnit
    
    public String getConditionUnit(){
        return this.KMEIN;
    }//fn getConditionUnit
    
    public String getMaterialNo(){
        return this.MATNR;
    }//fn getMaterialNo
    
    public String getKSCHLText(){
        return this.KSCHL_TEXT;
    }//fn getKSCHLText
    
    public String getPLTYPText(){
        return this.PLTYP_TEXT;
    }//fn getPLTYPText
    
    public String getConditionType(){
        return this.KSCHL;
    }//fn getConditionType
    
    public String getPriceListType(){
        return this.PLTYP;
    }//fn getPriceListType
    
}//End of class SalesOrdProPriceConstraints 

