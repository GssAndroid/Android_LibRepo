package com.globalsoft.SapLibActivity.Contraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProActCustomersConstraints implements KvmSerializable, Serializable{
    
    public String OBJECT_ID = "";
	public String PROCESS_TYPE = "";
    public String KUNNR = "";
    public String KUNNR_NAME = "";
    public String PARNR = "";
    public String PARNR_NAME = "";
    
    public SalesProActCustomersConstraints (){}
    

    public SalesProActCustomersConstraints (String[] values) {
    	OBJECT_ID = values[0];
        PROCESS_TYPE = values[1];
        KUNNR = values[2];
        KUNNR_NAME = values[3];
        PARNR = values[4];
        PARNR_NAME = values[5];
    }        

    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return OBJECT_ID;
            case 1:
                return PROCESS_TYPE;
            case 2:
                return KUNNR;
            case 3:
                return KUNNR_NAME;
            case 4:
                return PARNR;
            case 5:
                return PARNR_NAME;
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
                info.name = "OBJECT_ID";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROCESS_TYPE";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR_NAME";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR_NAME";
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
            	PROCESS_TYPE = value.toString();
                break;
            case 2:
                KUNNR = value.toString();
                break;
            case 3:
                KUNNR_NAME = value.toString();
                break;
            case 4:
            	PARNR = value.toString();
                break;
            case 5:
            	PARNR_NAME = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getObjectId(){
        return this.OBJECT_ID;
    }//fn getObjectId
    
    public String getProcessType(){
        return this.PROCESS_TYPE;
    }//fn getProcessType
    
    public String getKunnr(){
        return this.KUNNR;
    }//fn getKunnr
    
    public String getKunnrName(){
        return this.KUNNR_NAME;
    }//fn getKunnrName
    
    public String getParnr(){
        return this.PARNR;
    }//fn getParnr
    
    public String getParnrName(){
        return this.PARNR_NAME;
    }//fn getParnrName
                
}//End of class SalesProActCustomersConstraints 