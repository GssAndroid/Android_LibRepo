/*
 * SapTasksInputConstraints.java
 *
 * © <GlobalSoft-Solutions>, <2010>
 * Confidential and proprietary.
 * Developer: G.M.Ibrahim 
 */

package com.globalsoft.SapQueueProcessor.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SapQueueProcessorIpConstraints implements KvmSerializable, Serializable{
    
	public String Cdata;
    
    public SapQueueProcessorIpConstraints (){}
    

    public SapQueueProcessorIpConstraints (String value) {
        Cdata = value;
    }

    public Object getProperty(int arg0) {        
        switch(arg0){
        case 0:
            return Cdata;            
        }
        return null;
    }//fn getProperty
    

    public int getPropertyCount() {
        return 1;
    }//fn getPropertyCount
    

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Cdata";
                break;
            default:break;
        }
    }//fn getPropertyInfo
    

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                Cdata = value.toString();
                break;
            default:
                break;
        }
    }//fn setProperty
    
}//End of class SapQueueProcessorIpConstraints 

