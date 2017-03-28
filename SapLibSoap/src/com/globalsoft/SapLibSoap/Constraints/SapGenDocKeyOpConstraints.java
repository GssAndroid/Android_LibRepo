package com.globalsoft.SapLibSoap.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SapGenDocKeyOpConstraints implements KvmSerializable, Serializable {
    
	public String OBJECT_ID = "";
	public String PROCESS_TYPE = "";
    
    public SapGenDocKeyOpConstraints (){}
    

    public SapGenDocKeyOpConstraints (String[] values) {
    	OBJECT_ID = values[0];
        PROCESS_TYPE = values[1];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
        	case 0:
        		return OBJECT_ID;
            case 1:
                return PROCESS_TYPE;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 2;
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
              
}//End of class SapGenDocKeyOpConstraints 
