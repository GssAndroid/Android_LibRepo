package com.globalsoft.SapQueueProcessor.Constraints;

import java.io.Serializable;

public class QueueDetails implements Serializable{
	
	private String Date = "";
    private String AppId = "";
    private String RefId = "";
    private String ApiName = "";
    private String Status = "";
    private String ProcessCount = "";
    
    public QueueDetails(String date_arg, String appid_arg, String refId_arg, String apiName_arg, String status_arg, String processcount_arg) 
    {
    	Date = date_arg;
    	AppId = appid_arg;
    	RefId = refId_arg;
    	ApiName = apiName_arg;
    	Status = status_arg;
    	ProcessCount = processcount_arg;
    }//End of constructor

    public String getDate(){
        return Date;
    }//fn getDate
    
    public void setDate(String Date_set){
    	Date = Date_set;
    }//fn setDate
    
    public String getAppId(){
        return AppId;
    }//fn getAppId
    
    public void setAppId(String AppId_set){
    	AppId = AppId_set;
    }//fn setAppId 
    
    public String getRefId(){
        return RefId;
    }//fn getRefId
    
    public void setRefId(String RefId_set){
    	RefId = RefId_set;
    }//fn setRefId 
    
    public String getApiName(){
        return ApiName;
    }//fn getApiName
    
    public void setApiName(String ApiName_set){
    	ApiName = ApiName_set;
    }//fn setApiName 
    
    public String getStatus(){
        return Status;
    }//fn getStatus
    
    public void setStatus(String Status_set){
    	Status = Status_set;
    }//fn setStatus 
    
    public String getProcessCount(){
        return ProcessCount;
    }//fn getProcessCount
    
    public void setProcessCount(String ProcessCount_set){
    	ProcessCount = ProcessCount_set;
    }//fn setProcessCount 
}