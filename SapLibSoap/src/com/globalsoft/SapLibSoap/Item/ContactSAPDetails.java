package com.globalsoft.SapLibSoap.Item;

import java.io.Serializable;

public class ContactSAPDetails implements Serializable {
	private String ContactName = "";
    private String ContactDeviceId = "";
	private String SapId = "";
    private String CustomerId = "";
    private String FName = "";
    private String LName = "";
    private String OrgName = "";    
    
    public ContactSAPDetails(String name, String id, String sapid, String customerid, String fname, String lname, String orgname)
    {
    	ContactName = name;
    	ContactDeviceId = id;
    	SapId = sapid;
    	CustomerId = customerid;
    	FName = fname;
    	LName = lname;
    	OrgName = orgname;
    }//End of constructor

    public String getContactName(){
        return ContactName;
    }//fn getContactName
    
    public void setContactName(String ContactName_set){
    	ContactName = ContactName_set;
    }//fn setContactName
    
    public String getContactDeviceId(){
        return ContactDeviceId;
    }//fn getContactDeviceId
    
    public void setContactDeviceId(String ContactDeviceId_set){
    	ContactDeviceId = ContactDeviceId_set;
    }//fn setContactDeviceId
	
	public String getSapId(){
        return SapId;
    }//fn getSapId
    
    public void setSapId(String SapId_set){
    	SapId = SapId_set;
    }//fn setSapId
    
    public String getCustomerId(){
        return CustomerId;
    }//fn getCustomerId
    
    public void setCustomerId(String CustomerId_set){
    	CustomerId = CustomerId_set;
    }//fn setCustomerId 
    
    public String getFName(){
        return FName;
    }//fn getFName
    
    public void setFName(String FName_set){
    	FName = FName_set;
    }//fn setFName
    
    public String getLName(){
        return LName;
    }//fn getLName
    
    public void setLName(String LName_set){
    	LName = LName_set;
    }//fn setLName
    
    public String getOrgName(){
        return OrgName;
    }//fn getOrgName
    
    public void setOrgName(String OrgName_set){
    	OrgName = OrgName_set;
    }//fn setOrgName
    
}