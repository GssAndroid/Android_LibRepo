/*
 * VanStkColStrOpConstraints.java
 *
 * © <your company here>, <year>
 * Confidential and proprietary.
 */

package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class SalesOrdProItemOpConstraints implements KvmSerializable, Serializable {
    
	public String VBELN = "";
	public String POSNR = "";  
    public String MATNR = "";
    public String KWMENG = "";
    public String VRKME = "";
    public String NETWR = "";  
    public String WAERK = "";  
    public String ABGRU_TEXT = "";    
    public String FAKSP_TEXT = "";
    public String GBSTA_TEXT = "";
    public String LFSTA_TEXT = "";
    public String FKSTA_TEXT = "";
   
    
    public SalesOrdProItemOpConstraints (){}
    

    public SalesOrdProItemOpConstraints (String[] values) {
        VBELN = values[0];
        POSNR = values[1];
        MATNR = values[2];
        KWMENG = values[3];
        VRKME = values[4];
        NETWR = values[5];
        WAERK = values[6];
        ABGRU_TEXT = values[7];
        FAKSP_TEXT = values[8];
        GBSTA_TEXT = values[9];
        LFSTA_TEXT = values[10];
        FKSTA_TEXT = values[11];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return VBELN;
            case 1:
                return POSNR;
            case 2:
                return MATNR;
            case 3:
                return KWMENG; 
            case 4:
                return VRKME;
            case 5:
                return NETWR;
            case 6:
                return WAERK;
            case 7:
                return ABGRU_TEXT;
            case 8:
                return FAKSP_TEXT;
            case 9:
                return GBSTA_TEXT;
            case 10:
                return LFSTA_TEXT;
            case 11:
                return FKSTA_TEXT;
        }
        
        return null;
    }

    public int getPropertyCount() {
        return 12;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VBELN";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "POSNR";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATNR";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "KWMENG";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VRKME";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NETWR";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "WAERK";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ABGRU_TEXT";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FAKSP_TEXT";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "GBSTA_TEXT";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LFSTA_TEXT";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FKSTA_TEXT";
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
                POSNR = value.toString();
                break;
            case 2:
                MATNR = value.toString();
                break;
            case 3:
                KWMENG = value.toString();
                break;
            case 4:
                VRKME = value.toString();
                break;
            case 5:
                NETWR = value.toString();
                break;
            case 6:
                WAERK = value.toString();
                break;
            case 7:
                ABGRU_TEXT = value.toString();
                break;
            case 8:
                FAKSP_TEXT = value.toString();
                break;
            case 9:
                GBSTA_TEXT = value.toString();
                break;
            case 10:
                LFSTA_TEXT = value.toString();
                break;
            case 11:
                FKSTA_TEXT = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getDocumentNo(){
        return this.VBELN;
    }//fn getDocumentNo
    
    public String getSoldToParty(){
        return this.POSNR;
    }//fn getSoldToParty
    
    public String getMaterialNo(){
        return this.MATNR;
    }//fn getMaterialNo
    
    public String getCummOrdQtySales(){
        return this.KWMENG;
    }//fn getCummOrdQtySales
    
    public String getSalesUnit(){
        return this.VRKME;
    }//fn getSalesUnit
    
    public String getNetValDocCurr1(){
        return this.NETWR;
    }//fn getNetValDocCurr1
    
    public String getSDDocCurr(){
        return this.WAERK;
    }//fn getSDDocCurr
    
    public String getReasonForRjct(){
        return this.ABGRU_TEXT;
    }//fn getReasonForRjct
    
    public String getBillingBlkReason(){
        return this.FAKSP_TEXT;
    }//fn getBillingBlkReason
    
    public String getOverallStatus(){
        return this.GBSTA_TEXT;
    }//fn getOverallStatus
    
    public String getDeliveryStatus(){
        return this.LFSTA_TEXT;
    }//fn getDeliveryStatus
    
    public String getBillingStatus(){
        return this.FKSTA_TEXT;
    }//fn getBillingStatus
                
}//End of class SalesOrdProItemOpConstraints 

