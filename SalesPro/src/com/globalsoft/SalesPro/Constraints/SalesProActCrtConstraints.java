package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProActCrtConstraints implements KvmSerializable, Serializable{
    
	public String PROCESS_TYPE = "";
    public String CATEGORY = "";
    public String P_DESCRIPTION_20 = "";
    public String DESCRIPTION = "";
    
    public SalesProActCrtConstraints (){}
    

    public SalesProActCrtConstraints (String[] values) {
        PROCESS_TYPE = values[0];
        CATEGORY = values[1];
        P_DESCRIPTION_20 = values[2];
        DESCRIPTION = values[3];
    }
    
    public SalesProActCrtConstraints (String processType, String category, String pDesc, String desc){
    	 PROCESS_TYPE = processType;
         CATEGORY = category;
         P_DESCRIPTION_20 = pDesc;
         DESCRIPTION = desc;
    }
    

    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return PROCESS_TYPE;
            case 1:
                return CATEGORY;
            case 2:
                return P_DESCRIPTION_20;
            case 3:
                return DESCRIPTION; 
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 4;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROCESS_TYPE";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CATEGORY";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "P_DESCRIPTION_20";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DESCRIPTION";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                PROCESS_TYPE = value.toString();
                break;
            case 1:
                CATEGORY = value.toString();
                break;
            case 2:
                P_DESCRIPTION_20 = value.toString();
                break;
            case 3:
                DESCRIPTION = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getProcessType(){
        return this.PROCESS_TYPE;
    }//fn getProcessType
    
    public String getCategory(){
        return this.CATEGORY;
    }//fn getCategory
    
    public String getPDescription(){
        return this.P_DESCRIPTION_20;
    }//fn getPDescription
    
    public String getDescription(){
        return this.DESCRIPTION;
    }//fn getDescription
    
                
}//End of class SalesProActCrtConstraints 

