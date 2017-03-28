package com.globalsoft.ContactLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class ContactProContactCreationOpConstraints  implements KvmSerializable, Serializable {
    
    public String KUNNR = "";
    public String NAME1 = "";
    public String TEFN1 = "";
    public String TELFX = "";
    public String STRASK = "";
    public String ORT01K = "";
    public String REGIOK = "";
    public String PSTLZK = "";
    public String LAND1K = "";
    
    public ContactProContactCreationOpConstraints (){}
    

    public ContactProContactCreationOpConstraints (String[] values) {
        KUNNR = values[0];
        NAME1 = values[1];
        TEFN1 = values[2];
        TELFX = values[3];
        STRASK = values[4];
        ORT01K = values[5];
        REGIOK = values[6];
        PSTLZK = values[7];
        LAND1K = values[8];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return KUNNR;
            case 1:
                return NAME1;
            case 2:
                return TEFN1;
            case 3:
                return TELFX; 
            case 4:
                return STRASK;
            case 5:
                return ORT01K;
            case 6:
                return REGIOK;
            case 7:
                return PSTLZK;
            case 8:
                return LAND1K;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 9;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME1";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TEFN1";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELFX";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRASK";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01K";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIOK";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PSTLZK";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1K";
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
                NAME1 = value.toString();
                break;
            case 2:
                TEFN1 = value.toString();
                break;
            case 3:
                TELFX = value.toString();
                break;
            case 4:
                STRASK = value.toString();
                break;
            case 5:
            	ORT01K = value.toString();
                break;
            case 6:
            	REGIOK = value.toString();
                break;
            case 7:
            	PSTLZK = value.toString();
                break;
            case 8:
            	LAND1K = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getKunnr(){
        return this.KUNNR;
    }//fn getKunnr
    
    public String getName1(){
        return this.NAME1;
    }//fn getName1
    
    public String getTefn1(){
        return this.TEFN1;
    }//fn getTefn1
    
    public String getTelfx(){
        return this.TELFX;
    }//fn getTelfx
    
    public String getStrask(){
        return this.STRASK;
    }//fn getStrask
    
    public String getOrto1k(){
        return this.ORT01K;
    }//fn getOrt01k
    
    public String getRegiok(){
        return this.REGIOK;
    }//fn getRegiok
    
    public String getPstlzk(){
        return this.PSTLZK;
    }//fn getPstlzk
    
    public String getLand1k(){
        return this.LAND1K;
    }//fn getLand1k
    
}//End of class ContactProContactCreationOpConstraints 



