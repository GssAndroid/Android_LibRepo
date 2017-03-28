package com.globalsoft.SalesPro.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesProPriceListConstraints implements KvmSerializable, Serializable{
	
	
	public String MATDESC = "";
    public String PRICE = "";
    public String PER_QTY = "";
    public String MATERIAL = "";
    public String PROD_TYPE_DESC = "";
    public String PROD_LIST_DESC = "";
    public String PRICE_TYPE = "";
    public String PRICE_LIST = "";
    
    
    public SalesProPriceListConstraints (){}
    
    
    public SalesProPriceListConstraints (String[] values) {
    	MATDESC = values[0];
    	PRICE = values[1];
    	PER_QTY = values[2];
    	MATERIAL = values[3];
    	PROD_TYPE_DESC = values[4];
    	PROD_LIST_DESC = values[5];
    	PRICE_TYPE = values[6];
    	PRICE_LIST = values[7];
    }
    
    
    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return MATDESC;
            case 1:
                return PRICE;
            case 2:
                return PER_QTY;
            case 3:
                return MATERIAL;
            case 4:
                return PROD_TYPE_DESC;
            case 5:
                return PROD_LIST_DESC;
            case 6:
                return PRICE_TYPE;
            case 7:
                return PRICE_LIST;
        }
        
        return null;
    }
    
    public int getPropertyCount() {
        return 8;
    }

    
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATDESC";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PRICE";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PER_QTY";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "MATERIAL";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROD_TYPE_DESC";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PROD_LIST_DESC";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PRICE_TYPE";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "PRICE_LIST";
                break;
            default:break;
        }
    }
    
    
    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                MATDESC = value.toString();
                break;
            case 1:
                PRICE = value.toString();
                break;
            case 2:
                PER_QTY = value.toString();
                break;
            case 3:
                MATERIAL = value.toString();
                break;
            case 4:
                PROD_TYPE_DESC = value.toString();
                break;
            case 5:
                PROD_LIST_DESC = value.toString();
                break;
            case 6:
                PRICE_TYPE = value.toString();
                break;
            case 7:
            	PRICE_LIST = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getMatDesc(){
        return this.MATDESC;
    }//fn getMatDesc
    
    public String getPrice(){
        return this.PRICE;
    }//fn getPrice
    
    public String getPerQty(){
        return this.PER_QTY;
    }//fn getPartnerName2
    
    public String getMaterial(){
        return this.MATERIAL;
    }//fn getPerQty
    
    public String getProdDesc(){
        return this.PROD_TYPE_DESC;
    }//fn getProdDesc
    
    public String getProdListDesc(){
        return this.PROD_LIST_DESC;
    }//fn getProdListDesc
    
    public String getPriceType(){
        return this.PRICE_TYPE;
    }//fn getPriceType
        
    public String getPriceList(){
            return this.PRICE_LIST;
     }//fn getPriceList
}//end of SalesProPriceListConstraints
