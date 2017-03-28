package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class SalesOrdProHeadOpConstraints implements KvmSerializable, Serializable {
    
	public String VBELN = "";
    public String KUNAG = "";
    public String NAME1A = "";
    public String LAND1A = "";
    public String REGIOA = "";
    public String ORT01A = "";
    public String STRASA = "";
    public String TELF1A = "";
    public String TELF2A = "";
    public String SMTP_ADDRA = "";
    public String PARNR = "";
    public String NAME1PK = "";
    public String LAND1P = "";
    public String REGIOP = "";
    public String ORT01P = "";
    public String STRASP = "";
    public String TELF1P = "";
    public String TELF2P = "";
    public String SMTP_ADDRP = "";
    public String AUDAT = "";
    public String WAERK = "";
    public String NETWR = "";
    public String AUGRU_TEXT = "";
    public String GBSTK_TEXT = "";
    public String ABSTK_TEXT = "";
    public String LFSTK_TEXT = "";
    public String CMGST_TEXT = "";
    public String SPSTG_TEXT = "";
    public String LIFSK_TEXT = "";   
    public String FKSTK_TEXT = "";  
    public String FAKSK_TEXT = "";   
    public String ZZSTATUS_SUMMARY = "";  
    public String KETDAT = "";  
    public String BSTKD = "";  
    public String BSTDK = "";  
    
    public SalesOrdProHeadOpConstraints (){}
    

    public SalesOrdProHeadOpConstraints (String[] values) {
        VBELN = values[0];
        KUNAG = values[1];
        NAME1A = values[2];
        LAND1A = values[3];
        REGIOA = values[4];
        ORT01A = values[5];
        STRASA = values[6];
        TELF1A = values[7];
        TELF2A = values[8];
        SMTP_ADDRA = values[9];
        PARNR = values[10];
        NAME1PK = values[11];
        LAND1P = values[12];
        REGIOP = values[13];
        ORT01P = values[14];
        STRASP = values[15];
        TELF1P = values[16];
        TELF2P = values[17];
        SMTP_ADDRP = values[18];
        AUDAT = values[19];
        WAERK = values[20];
        NETWR = values[21];
        AUGRU_TEXT = values[22];
        GBSTK_TEXT = values[23];
        ABSTK_TEXT = values[24];
        LFSTK_TEXT = values[25];
        CMGST_TEXT = values[26];
        SPSTG_TEXT = values[27];
        LIFSK_TEXT = values[28];
        FKSTK_TEXT = values[29];
        FAKSK_TEXT = values[30];
        ZZSTATUS_SUMMARY = values[31];
        KETDAT = values[32];
        BSTKD = values[33];
        BSTDK = values[34];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return VBELN;
            case 1:
                return KUNAG;
            case 2:
                return NAME1A;
            case 3:
                return LAND1A; 
            case 4:
                return REGIOA;
            case 5:
                return ORT01A;
            case 6:
                return STRASA;
            case 7:
                return TELF1A;
            case 8:
                return TELF2A;
            case 9:
                return SMTP_ADDRA;
            case 10:
                return PARNR;
            case 11:
                return NAME1PK;
            case 12:
                return LAND1P;
            case 13:
                return REGIOP;
            case 14:
                return ORT01P;
            case 15:
                return STRASP;
            case 16:
                return TELF1P;
            case 17:
                return TELF2P;
            case 18:
                return SMTP_ADDRP;
            case 19:
                return AUDAT;
            case 20:
                return WAERK;
            case 21:
                return NETWR;
            case 22:
                return AUGRU_TEXT;
            case 23:
                return GBSTK_TEXT;
            case 24:
                return ABSTK_TEXT;
            case 25:
                return LFSTK_TEXT;
            case 26:
                return CMGST_TEXT;
            case 27:
                return SPSTG_TEXT;
            case 28:
                return LIFSK_TEXT;
            case 29:
                return FKSTK_TEXT;
            case 30:
                return FAKSK_TEXT;
            case 31:
                return ZZSTATUS_SUMMARY;
            case 32:
                return KETDAT;
            case 33:
                return BSTKD;
            case 34:
                return BSTDK;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 35;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VBELN";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KUNAG";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME1A";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1A";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIOA";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01A";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRASA";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF1A";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF2A";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SMTP_ADDRA";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PARNR";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME1PK";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LAND1P";
                break;
            case 13:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "REGIOP";
                break;
            case 14:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ORT01P";
                break;
            case 15:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "STRASP";
                break;
            case 16:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF1P";
                break;
            case 17:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TELF2P";
                break;
            case 18:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SMTP_ADDRP";
                break;
            case 19:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "AUDAT";
                break;
            case 20:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WAERK";
                break;
            case 21:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NETWR";
                break;
            case 22:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "AUGRU_TEXT";
                break;
            case 23:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "GBSTK_TEXT";
                break;   
            case 24:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ABSTK_TEXT";
                break;   
            case 25:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LFSTK_TEXT";
                break;   
            case 26:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CMGST_TEXT";
                break;   
            case 27:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SPSTG_TEXT";
                break;   
            case 28:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LIFSK_TEXT";
                break;                 
            case 29:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FKSTK_TEXT";
                break;   
            case 30:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FAKSK_TEXT";
                break;   
            case 31:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ZZSTATUS_SUMMARY";
                break;  
            case 32:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KETDAT";
                break; 
            case 33:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "BSTKD";
                break;  
            case 34:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "BSTDK";
                break; 
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                VBELN = value.toString();
                break;
            case 1:
                KUNAG = value.toString();
                break;
            case 2:
                NAME1A = value.toString();
                break;
            case 3:
                LAND1A = value.toString();
                break;
            case 4:
                REGIOA = value.toString();
                break;
            case 5:
                ORT01A = value.toString();
                break;
            case 6:
                STRASA = value.toString();
                break;
            case 7:
                TELF1A = value.toString();
                break;
            case 8:
                TELF2A = value.toString();
                break;
            case 9:
                SMTP_ADDRA = value.toString();
                break;
            case 10:
                PARNR = value.toString();
                break;
            case 11:
                NAME1PK = value.toString();
                break;
            case 12:
                LAND1P = value.toString();
                break;
            case 13:
                REGIOP = value.toString();
                break;
            case 14:
                ORT01P = value.toString();
                break;
            case 15:
                STRASP = value.toString();
                break;
            case 16:
                TELF1P = value.toString();
                break;
            case 17:
                TELF2P = value.toString();
                break;
            case 18:
                SMTP_ADDRP = value.toString();
                break;
            case 19:
                AUDAT = value.toString();
                break;
            case 20:
                WAERK = value.toString();
                break;
            case 21:
                NETWR = value.toString();
                break;
            case 22:
                AUGRU_TEXT = value.toString();
                break;
            case 23:
                GBSTK_TEXT = value.toString();
                break;
            case 24:
                ABSTK_TEXT = value.toString();
                break;
            case 25:
                LFSTK_TEXT = value.toString();
                break;
            case 26:
                CMGST_TEXT = value.toString();
                break;
            case 27:
            	SPSTG_TEXT = value.toString();
                break;                
            case 28:
                LIFSK_TEXT = value.toString();
                break;
            case 29:
                FKSTK_TEXT = value.toString();
                break;
            case 30:
                FAKSK_TEXT = value.toString();
                break;
            case 31:
                ZZSTATUS_SUMMARY = value.toString();
                break;
            case 32:
            	KETDAT = value.toString();
                break;  
            case 33:
            	BSTKD = value.toString();
                break;
            case 34:
            	BSTDK = value.toString();
                break;  
            default:
                break;
        }
    }
    
    public String getDocumentNo(){
        return this.VBELN;
    }//fn getDocumentNo
    
    public String getSoldToParty(){
        return this.KUNAG;
    }//fn getSoldToParty
    
    public String getSPName(){
        return this.NAME1A;
    }//fn getSPName
    
    public String getSPCountry(){
        return this.LAND1A;
    }//fn getSPCountry
    
    public String getSPRegion(){
        return this.REGIOA;
    }//fn getSPRegion
    
    public String getSPCity(){
        return this.ORT01A;
    }//fn getSPCity
    
    public String getSPStreet(){
        return this.STRASA;
    }//fn getSPStreet
    
    public String getSPTel1(){
        return this.TELF1A;
    }//fn getSPTel1
    
    public String getSPTel2(){
        return this.TELF2A;
    }//fn getSPTel2
    
    public String getSPEmail(){
        return this.SMTP_ADDRA;
    }//fn getSPEmail
    
    public String getCPNo(){
        return this.PARNR;
    }//fn getCPNo
    
    public String getCPName(){
        return this.NAME1PK;
    }//fn getCPName
    
    public String getCPCountry(){
        return this.LAND1P;
    }//fn getCPCountry
       
    public String getCPRegion(){
        return this.REGIOP;
    }//fn getCPRegion
    
    public String getCPCity(){
        return this.ORT01P;
    }//fn getCPCity    
         
    public String getCPStreet(){
        return this.STRASP;
    }//fn getCPStreet    
     
    public String getCPTel1(){
        return this.TELF1P;
    }//fn getCPTel1    
     
    public String getCPTel2(){
        return this.TELF2P;
    }//fn getCPTel2
         
    public String getCPEmail(){
        return this.SMTP_ADDRP;
    }//fn getCPEmail
         
    public String getDocDate(){
        return this.AUDAT;
    }//fn getDocDate    
     
    public String getSDDocCurrency(){
        return this.WAERK;
    }//fn getSDDocCurrency    
     
    public String getNetValDocCurr(){
        return this.NETWR;
    }//fn getNetValDocCurr
    
    public String getOrdRcjtReason(){
        return this.AUGRU_TEXT;
    }//fn getOrdRcjtReason
    
    public String getDocStatus(){
        return this.GBSTK_TEXT;
    }//fn getDocStatus
    
    public String getRejectStatus(){
        return this.ABSTK_TEXT;
    }//fn getRejectStatus
    
    public String getDeliveryStatus(){
        return this.LFSTK_TEXT;
    }//fn getDeliveryStatus
    
    public String getCreditCheckStatus(){
        return this.CMGST_TEXT;
    }//fn getCreditCheckStatus
    
    public String getBlockedStatus(){
        return this.SPSTG_TEXT;
    }//fn getBlockedStatus    
    
    public String getDeliveryBlockReason(){
        return this.LIFSK_TEXT;
    }//fn getDelBlockReason
    
    public String getBillingStatus(){
        return this.FKSTK_TEXT;
    }//fn getBillingStatus
    
    public String getBillingBlockReason(){
        return this.FAKSK_TEXT;
    }//fn getBillingBlockReason
    
    public String getStatusSummary(){
        return this.ZZSTATUS_SUMMARY;
    }//fn getStatusSummary
    
    public String getRequiredDate(){
        return this.KETDAT;
    }//fn getRequiredDate
     
    public String getPurchaseOrderNo(){
        return this.BSTKD;
    }//fn getPurchaseOrderNo
    
    public String getPurchaseOrderDate(){
        return this.BSTDK;
    }//fn getPurchaseOrderDate
    
}//End of class SalesOrdProHeadOpConstraints 

