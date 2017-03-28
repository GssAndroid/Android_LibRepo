package com.globalsoft.SalesOrderLib.Constraints;

import java.io.Serializable;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SalesOrderCntxtOpConstraints implements KvmSerializable, Serializable  {

	public String CNTXT2 = "";
    public String CNTXT3 = "";
    public String CNTXT4 = "";
    public String NAME = "";
    public String DPNDNCY = "";
    public String SEQNR = "";
    public String TYPE = "";
    public String SIGN = "";
    public String OPTN = "";
    public String VALUE = "";
    public String VALUE_HIGH = "";
    public String TRGTNAME = "";
    public String TRGTVALUE = "";
    
    public SalesOrderCntxtOpConstraints (){}

    public SalesOrderCntxtOpConstraints (String[] values) {
        CNTXT2 = values[0];
        CNTXT3 = values[1];
        CNTXT4 = values[2];
        NAME = values[3];
        DPNDNCY = values[4];
        SEQNR = values[5];
        TYPE = values[6];
        SIGN = values[7];
        OPTN = values[8];
        VALUE = values[9];
        VALUE_HIGH = values[10];
        TRGTNAME = values[11];
        TRGTVALUE = values[12];
    }


    public Object getProperty(int arg0) {        
        switch(arg0) {
            case 0:
                return CNTXT2;
            case 1:
                return CNTXT3;
            case 2:
                return CNTXT4;
            case 3:
                return NAME;
            case 4:
                return DPNDNCY; 
            case 5:
                return SEQNR;
            case 6:
                return TYPE;
            case 7:
                return SIGN;
            case 8:
                return OPTN;
            case 9:
                return VALUE;
            case 10:
                return VALUE_HIGH;
            case 11:
                return TRGTNAME;
            case 12:
                return TRGTVALUE;
        }        
        return null;
    }

    public int getPropertyCount() {
        return 13;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CNTXT2";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CNTXT3";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "CNTXT4";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "NAME";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DPNDNCY";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SEQNR";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TYPE";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "SIGN";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "OPTN";
                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VALUE";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "VALUE_HIGH";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TRGTNAME";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "TRGTVALUE";
                break;
            default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index){
            case 0:
                CNTXT2 = value.toString();
                break;
            case 1:
                CNTXT3 = value.toString();
                break;
            case 2:
                CNTXT4 = value.toString();
                break;
            case 3:
                NAME = value.toString();
                break;
            case 4:
                DPNDNCY = value.toString();
                break;
            case 5:
                SEQNR = value.toString();
                break;
            case 6:
                TYPE = value.toString();
                break;
            case 7:
                SIGN = value.toString();
                break;
            case 8:
                OPTN = value.toString();
                break;
            case 9:
                VALUE = value.toString();
                break;
            case 10:
                VALUE_HIGH = value.toString();
                break;
            case 11:
                TRGTNAME = value.toString();
                break;
            case 12:
                TRGTVALUE = value.toString();
                break;
            default:
                break;
        }
    }
    
    public String getCntxt2(){
        return this.CNTXT2;
    }//fn getCntxt2
    
    public String getCntxt3(){
        return this.CNTXT3;
    }//fn getCntxt3
    
    public String getCntxt4(){
        return this.CNTXT4;
    }//fn getCntxt4
    
    public String getName(){
        return this.NAME;
    }//fn getName
    
    public String getDpndncy(){
        return this.DPNDNCY;
    }//fn getDpndncy
    
    public String getSeqNR(){
        return this.SEQNR;
    }//fn getSeqNR
    
    public String getType(){
        return this.TYPE;
    }//fn getType
    
    public String getSign(){
        return this.SIGN;
    }//fn getSign
    
    public String getOptn(){
        return this.OPTN;
    }//fn getOptn
    
    public String getValue(){
        return this.VALUE;
    }//fn getValue
    
    public String getValueHigh(){
        return this.VALUE_HIGH;
    }//fn getValueHigh
    
    public String getTrgtName(){
        return this.TRGTNAME;
    }//fn getTrgtName
    
    public String getTrgtValue(){
        return this.TRGTVALUE;
    }//fn getTrgtValue
    
}//SalesOrderCntxtOpConstraints
