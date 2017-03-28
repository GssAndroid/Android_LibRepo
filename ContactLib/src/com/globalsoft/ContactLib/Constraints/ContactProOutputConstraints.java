package com.globalsoft.ContactLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class ContactProOutputConstraints  implements KvmSerializable, Serializable{
    
    public String PARNR = "";
    public String NAME_FIRST = "";
    public String NAME_LAST = "";
    public String FNCTN = "";
    public String TELF_WORK = "";
    public String TELF_MOBILE = "";
    public String TELF_PRSNL = "";
    public String TELF_OTHR = "";
    public String EMAIL_WORK = "";
    public String EMAIL_PRSNL = "";
    public String EMAIL_OTHR = "";
    public String IM_TYPE = "";
    public String IM_ID = "";
    public String STRASP = "";
    //public String STRS2P = "";
    public String ORT01P = "";
    public String REGIOP = "";
    public String PSTLZP = "";
    public String LAND1P = "";
    //public String PFACHP = "";
    //public String PSTL2P = "";
    public String KUNNR = "";
    public String KUNNR_NAME1 = "";
    
    public ContactProOutputConstraints (){}
    

    public ContactProOutputConstraints (String[] values) {
        PARNR = values[0];
        NAME_FIRST = values[1];
        NAME_LAST = values[2];
        FNCTN = values[3];
        TELF_WORK = values[4];
        TELF_MOBILE = values[5];
        TELF_PRSNL = values[6];
        TELF_OTHR = values[7];
        EMAIL_WORK = values[8];
        EMAIL_PRSNL = values[9];
        EMAIL_OTHR = values[10];
        IM_TYPE = values[11];
        IM_ID = values[12];
        STRASP = values[13];
        //STRS2P = values[14];
        ORT01P = values[14];
        REGIOP = values[15];
        PSTLZP = values[16];
        LAND1P = values[17];
        //PFACHP = values[19];
        //PSTL2P = values[20];
        KUNNR = values[18];
        KUNNR_NAME1 = values[19];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return PARNR;
            case 1:
                return NAME_FIRST;
            case 2:
                return NAME_LAST;
            case 3:
                return FNCTN; 
            case 4:
                return TELF_WORK;
            case 5:
                return TELF_MOBILE;
            case 6:
                return TELF_PRSNL;
            case 7:
                return TELF_OTHR;
            case 8:
                return EMAIL_WORK;
            case 9:
                return EMAIL_PRSNL;
            case 10:
                return EMAIL_OTHR;
            case 11:
                return IM_TYPE;
            case 12:
                return IM_ID;
            case 13:
                return STRASP;
            /*case 14:
                return STRS2P;*/
            case 14:
                return ORT01P;
            case 15:
                return REGIOP;
            case 16:
                return PSTLZP;
            case 17:
                return LAND1P;
            /*case 19:
                return PFACHP;
            case 20:
                return PSTL2P;*/
            case 18:
                return KUNNR;
            case 19:
                return KUNNR_NAME1;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 19;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME_FIRST";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME_LAST";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FNCTN";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF_WORK";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF_MOBILE";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF_PRSNL";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF_OTHR";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "EMAIL_WORK";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "EMAIL_PRSNL";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "EMAIL_OTHR";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "IM_TYPE";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "IM_ID";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRASP";
                break;
            /*case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRS2P";
                break;*/
            case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01P";
                break;
            case 15:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIOP";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PSTLZP";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1P";
                break;
            /*case 19:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PFACHP";
                break;
            case 20:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PSTL2P";
                break;*/
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR";
                break;
            case 19:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNNR_NAME1";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                PARNR = value.toString();
                break;
            case 1:
                NAME_FIRST = value.toString();
                break;
            case 2:
                NAME_LAST = value.toString();
                break;
            case 3:
                FNCTN = value.toString();
                break;
            case 4:
                TELF_WORK = value.toString();
                break;
            case 5:
                TELF_MOBILE = value.toString();
                break;
            case 6:
                TELF_PRSNL = value.toString();
                break;
            case 7:
                TELF_OTHR = value.toString();
                break;
            case 8:
                EMAIL_WORK = value.toString();
                break;
            case 9:
                EMAIL_PRSNL = value.toString();
                break;
            case 10:
                EMAIL_OTHR = value.toString();
                break;
            case 11:
                IM_TYPE = value.toString();
                break;
            case 12:
                IM_ID = value.toString();
                break;
            case 13:
                STRASP = value.toString();
                break;
            /*case 14:
                STRS2P = value.toString();
                break;*/
            case 14:
                ORT01P = value.toString();
                break;
            case 15:
                REGIOP = value.toString();
                break;
            case 16:
                PSTLZP = value.toString();
                break;
            case 17:
                LAND1P = value.toString();
                break;
            /*case 19:
                PFACHP = value.toString();
                break;
            case 20:
                PSTL2P = value.toString();
                break;*/
            case 18:
                KUNNR = value.toString();
                break;
            case 19:
                KUNNR_NAME1 = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getParnr(){
        return this.PARNR;
    }//fn getPARNR
    
    public String getNameFirst(){
        return this.NAME_FIRST;
    }//fn getNameFirst
    
    public String getNameLast(){
        return this.NAME_LAST;
    }//fn getNameLast
    
    public String getFnctn(){
        return this.FNCTN;
    }//fn getFnctn
    
    public String getTelfWork(){
        return this.TELF_WORK;
    }//fn getTelfWork
    
    public String getTelfMobile(){
        return this.TELF_MOBILE;
    }//fn getTelfMobile
    
    public String getTelfPrsnl(){
        return this.TELF_PRSNL;
    }//fn getTelfPrsnl
    
    public String getTelfOthers(){
        return this.TELF_OTHR;
    }//fn getTelfOthers
    
    public String getEmailWork(){
        return this.EMAIL_WORK;
    }//fn getEmailWork
    
    public String getEmailPrsnl(){
        return this.EMAIL_PRSNL;
    }//fn getEmailPrsnl
    
    public String getEmailOthers(){
        return this.EMAIL_OTHR;
    }//fn getEmailOthers
    
    public String getImType(){
        return this.IM_TYPE;
    }//fn getImType
    
    public String getIMId(){
        return this.IM_ID;
    }//fn getIMId
       
    public String getStrasp(){
        return this.STRASP;
    }//fn getStatus
        
    /*public String getStrs2p(){
        return this.STRS2P;
    }//fn getStrs2p    
*/         
    public String getOrt01p(){
        return this.ORT01P;
    }//fn getOrt01p    
     
    public String getRegiop(){
        return this.REGIOP;
    }//fn getRegiop    
     
    public String getPstlzp(){
        return this.PSTLZP;
    }//fn getPstlzp
         
    public String getLand1p(){
        return this.LAND1P;
    }//fn getLand1p
         
    /*public String getPfachp(){
        return this.PFACHP;
    }//fn getPfachp
         
    public String getPstl2p(){
        return this.PSTL2P;
    }//fn getPstl2p    
*/     
    public String getKunnr(){
        return this.KUNNR;
    }//fn getKunnr    
     
    public String getKunnrName1(){
        return this.KUNNR_NAME1;
    }//fn getKunnrName1
                
}//End of class SapTasksOutputConstraints 
