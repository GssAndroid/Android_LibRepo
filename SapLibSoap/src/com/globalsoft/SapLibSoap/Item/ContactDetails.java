package com.globalsoft.SapLibSoap.Item;

import java.io.Serializable;

public class ContactDetails implements Serializable{
	private String ContactName = "";
    private String ContactId = "";
    private String[] Emails;
    private String[] PhNos;
    private String[] OrgDetails;
    private String[] AddressDetails;
    private boolean ErrorNotify;
    
    public ContactDetails(String name, String id, String[] emails, String[] phnos, String[] orgdetails, String[] addressdetails, boolean errorNotify) 
    {
    	ContactName = name;
    	ContactId = id;
    	Emails = emails;
    	PhNos = phnos;
    	OrgDetails = orgdetails;
    	AddressDetails = addressdetails;
    	ErrorNotify = errorNotify;
    }//End of constructor

    public String getContactName(){
        return ContactName;
    }//fn getContactName
    
    public void setContactName(String ContactName_set){
    	ContactName = ContactName_set;
    }//fn setContactName
    
    public String getContactId(){
        return ContactId;
    }//fn getContactId
    
    public void setContactId(String ContactId_set){
    	ContactId = ContactId_set;
    }//fn setContactId 
    
    public String[] getEmails(){
        return Emails;
    }//fn getEmails
    
    public void setEmails(String[] Emails_set){
    	Emails = Emails_set;
    }//fn setEmails
    
    public String[] getPhNos(){
        return PhNos;
    }//fn getPhNos
    
    public void setPhNos(String[] PhNos_set){
    	PhNos = PhNos_set;
    }//fn setPhNos 
    
    public String[] getOrgDetails(){
        return OrgDetails;
    }//fn getOrgDetails
    
    public void setOrgDetails(String[] OrgDetails_set){
    	OrgDetails = OrgDetails_set;
    }//fn setOrgDetails
    
    public String[] getAddressDetails(){
        return AddressDetails;
    }//fn getAddressDetails
    
    public void setAddressDetails(String[] AddressDetails_set){
    	AddressDetails = AddressDetails_set;
    }//fn setAddressDetails
    
    public boolean getErrorNotify(){
        return ErrorNotify;
    }//fn getErrorNotify
    
    public void setErrorNotify(boolean ErrorNotify_set){
    	ErrorNotify = ErrorNotify_set;
    }//fn setErrorNotify
}