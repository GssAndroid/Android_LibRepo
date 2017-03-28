package com.globalsoft.SapLibSoap.Item;

import java.io.Serializable;

public class CalendarDetails implements Serializable{
	private String Title = "";
    private String EventId = "";
    private String Desc;
    private String DateSt;
    private String DateEnd;
    private String Location;
    private String CalendarId;
    
    public CalendarDetails(String title, String eventid, String desc, String datest, String dateend, String location, String calendarid) 
    {
    	Title = title;
    	EventId = eventid;
    	Desc = desc;
    	DateSt = datest;
    	DateEnd = dateend;
    	Location = location;
    	CalendarId = calendarid;
    }//End of constructor

    public String getTitle(){
        return Title;
    }//fn getTitle
    
    public void setTitle(String Title_set){
    	Title = Title_set;
    }//fn setTitle
    
    public String getEventId(){
        return EventId;
    }//fn getEventId
    
    public void setEventId(String EventId_set){
    	EventId = EventId_set;
    }//fn setEventId 
    
    public String getDesc(){
        return Desc;
    }//fn getDesc
    
    public void setDesc(String Desc_set){
    	Desc = Desc_set;
    }//fn setDesc
    
    public String getDateSt(){
        return DateSt;
    }//fn getDateSt
    
    public void setDateSt(String DateSt_set){
    	DateSt = DateSt_set;
    }//fn setDateSt 
    
    public String getDateEnd(){
        return DateEnd;
    }//fn getDateEnd
    
    public void setDateEnd(String DateEnd_set){
    	DateEnd = DateEnd_set;
    }//fn setDateEnd
    
    public String getLocation(){
        return Location;
    }//fn getLocation
    
    public void setLocation(String Location_set){
    	Location = Location_set;
    }//fn setLocation
    
    public String getCalendarId(){
        return CalendarId;
    }//fn getCalendarId
    
    public void setCalendarId(String CalendarId_set){
    	CalendarId = CalendarId_set;
    }//fn setCalendarId
}
