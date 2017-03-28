package com.globalsoft.ContactLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class ContactProContactCreationKeyOpConstraints  implements KvmSerializable, Serializable {
    
    public String KUNNR = "";
    public String PARNR = "";
    
    public ContactProContactCreationKeyOpConstraints (){}
    

    public ContactProContactCreationKeyOpConstraints (String[] values) {
        KUNNR = values[0];
        PARNR = values[1];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return KUNNR;
            case 1:
                return PARNR;           
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
                info.name = "KUNNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
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
                PARNR = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getKunnr(){
        return this.KUNNR;
    }//fn getKunnr
    
    public String getParnr(){
        return this.PARNR;
    }//fn getParnr
        
}//End of class ContactProContactCreationKeyOpConstraints 