package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class SalesOrdProMattConstraints implements KvmSerializable, Serializable {
    
	public String MATNR = "";
    public String MAKTX = "";
    public String MEINH = "";
    public String MSEHT = "";
    
    public SalesOrdProMattConstraints (){}
    

    public SalesOrdProMattConstraints (String[] values) {
        MATNR = values[0];
        MAKTX = values[1];
        MEINH = values[2];
        MSEHT = values[3];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return MATNR;
            case 1:
                return MAKTX;
            case 2:
                return MEINH;
            case 3:
                return MSEHT; 
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
                info.name = "MATNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MAKTX";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MEINH";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MSEHT";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                MATNR = value.toString();
                break;
            case 1:
                MAKTX = value.toString();
                break;
            case 2:
                MEINH = value.toString();
                break;
            case 3:
                MSEHT = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getMaterialNo(){
        return this.MATNR;
    }//fn getMaterialNo
    
    public String getMaterialDesc(){
        return this.MAKTX;
    }//fn getMaterialDesc
    
    public String getMaterialUnit(){
        return this.MEINH;
    }//fn getMaterialUnit
    
    public String getMaterialUnitDesc(){
        return this.MSEHT;
    }//fn getMaterialUnitDesc
                
}//End of class SalesOrdProMattConstraints 
