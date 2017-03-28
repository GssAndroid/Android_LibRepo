package com.globalsoft.SapLibActivity.Contraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProActStatusConstraints implements KvmSerializable, Serializable{
    
	public String STATUS = "";
    public String TXT30 = "";
    public String ZZSTATUS_ICON = "";
    public String ZZSTATUS_POSTSETACTION = "";
    
    
    public SalesProActStatusConstraints (){}
    

    public SalesProActStatusConstraints (String[] values) {
        STATUS = values[0];
        TXT30 = values[1];
        ZZSTATUS_ICON = values[2];
        ZZSTATUS_POSTSETACTION = values[3];
    }
    
    public SalesProActStatusConstraints (String status, String txtDesc, String statusicon, String statussetaction ){
    	 STATUS = status;
         TXT30 = txtDesc;
         ZZSTATUS_ICON = statusicon;
         ZZSTATUS_POSTSETACTION = statussetaction;
    }
    

    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return STATUS;
            case 1:
                return TXT30;
            case 2:
                return ZZSTATUS_ICON;
            case 3:
                return ZZSTATUS_POSTSETACTION;
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
                info.name = "STATUS";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TXT30";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ZZSTATUS_ICON";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ZZSTATUS_POSTSETACTION";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                STATUS = value.toString();
                break;
            case 1:
                TXT30 = value.toString();
                break;
            case 2:
            	ZZSTATUS_ICON = value.toString();
                break;
            case 3:
            	ZZSTATUS_POSTSETACTION = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getStatus(){
        return this.STATUS;
    }//fn getStatus
    
    public String getStatusDesc(){
        return this.TXT30;
    }//fn getStatusDesc
    
    public String getStatusIcon(){
        return this.ZZSTATUS_ICON;
    }//fn getStatusDesc
    
    public String getStatusPstAction(){
        return this.ZZSTATUS_POSTSETACTION;
    }//fn getStatusDesc
    
}//End of class SalesProActStatusConstraints 



