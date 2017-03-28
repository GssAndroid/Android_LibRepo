package com.globalsoft.CalendarLib.Utils;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;

import com.globalsoft.SapLibSoap.Item.ContactSAPDetails;
import com.globalsoft.SapLibSoap.Utils.SapGenConstants;

public final class CalendarConstants {
	
	//Application Related Constants
    public static String CRTAPP_CALLING_APP_NAME = SapGenConstants.APPLN_NAME_STR_MOBILEPRO;
    public static String CRTAPP_PACKAGE_NAME = "com.globalsoft.CalendarLib";    
    public static String CRTAPP_CLASS_NAME = "com.globalsoft.CalendarLib.Service.CalendarBGService"; 
   
    //Screen Related Constants
    public static final int APP_EDIT_SCREEN = 1;
    public static final int APP_CRE_SCREEN = 2;
        
    public static final String APP_EDIT_ADD_API = "";
    
    //Gallery Related Constants
    public static int imageCountId = 1;
    
    public static String addToCalendar(Context ctx, final String title, final String desc, final long dtstart, final long dtend, Vector selContactVect) { 
    	final ContentResolver cr = ctx.getContentResolver();
    	String eventId = "";
    	try{	    		    		    	
	    	// get calendar 
            Calendar cal = Calendar.getInstance();      
            // event insert 
            ContentValues values = new ContentValues(); 
            Uri event;
            values.put("calendar_id", 1); 
            values.put("title", title); 
            values.put("description", desc);             
            values.put("dtstart", dtstart); 
            values.put("dtend", dtend); 
            values.put("hasAlarm", 1);    
            values.put(Events.GUESTS_CAN_MODIFY, 0);
            values.put(Events.EVENT_TIMEZONE,TimeZone.getDefault().getID());      
            
	    	if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
	    		cr.delete( Uri.parse("content://com.android.calendar/events"), "title=? ", new String[]{String.valueOf(title)});
	    	}else{
	    		cr.delete( Uri.parse("content://calendar/events"), "title=? ", new String[]{String.valueOf(title)});
	    	}
	    	
	    	if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) 
	    		event = cr.insert(Uri.parse("content://com.android.calendar/events"), values); 
	    	else
	    		event = cr.insert(Uri.parse("content://com.android.calendar/events"), values); 
	    	
	    	eventId = String.valueOf(Long.parseLong(event.getLastPathSegment()));
	    	
	    	// reminder insert 
            values = new ContentValues(); 
            values.put( "event_id", Long.parseLong(event.getLastPathSegment())); 
            values.put( "method", 1 ); 
            values.put( "minutes", 120 ); 
            
            if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
	    		cr.delete( Uri.parse("content://com.android.calendar/reminders"), "event_id=? ", new String[]{String.valueOf(Long.parseLong(event.getLastPathSegment()))});
	    	}else{
	    		cr.delete( Uri.parse("content://calendar/reminders"), "event_id=? ", new String[]{String.valueOf(Long.parseLong(event.getLastPathSegment()))});
	    	}
	    	
	    	if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) 
	    		cr.insert(Uri.parse("content://com.android.calendar/reminders"), values); 
	    	else
	    		cr.insert(Uri.parse("content://com.android.calendar/reminders"), values); 
	    	
	    	String attendeuesesUriString = "content://com.android.calendar/attendees";

	        /********
	         * To add multiple attendees need to insert ContentValues multiple
	         * times
	         ***********/
	    	ContentValues attendeesValues;
	    	if(selContactVect != null && selContactVect.size() > 0){
        		for (int pos = 0; pos < selContactVect.size(); pos++){
	        		String name = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactName();
					String id = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactDeviceId();
					String sapid = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getSapId();
					String cusid = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getCustomerId();
					String fname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getFName();
					String lname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getLName();
					String orgname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getOrgName();
									
					String contactName = fname+" "+lname;
					String[] Emails = new String[0];
					String strEmailHome = "", strEmailWork = "", strEmailOther = "", strEmail = "";
					
					if(id != null && id.length() > 0){
						// Get value for Emails
						Emails = getContactEmailsDetails(ctx, id);
					}
					if(Emails != null && Emails.length > 0){
						strEmailHome = Emails[0];
			         	strEmailWork = Emails[1];
			         	strEmailOther = Emails[2];
					}				 	
			        
			        if(strEmailWork != null && strEmailWork.length() > 0){
			        	strEmail = strEmailWork;
			        }else if(strEmailOther != null && strEmailOther.length() > 0){
			        	strEmail = strEmailOther;
			        }else if(strEmailHome != null && strEmailHome.length() > 0){
			        	strEmail = strEmailHome;
			        }
			        
			        if(strEmail != null && strEmail.length() > 0){
						attendeesValues = new ContentValues();
				        attendeesValues.put("event_id", Long.parseLong(event.getLastPathSegment()));
				        attendeesValues.put("attendeeName", contactName); // Attendees name
			        	attendeesValues.put("attendeeEmail", strEmail);// Attendee // Email id
			        	attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
						                        // Relationship_None(0),
						                        // Organizer(2),
						                        // Performer(3),
						                        // Speaker(4)
						attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
						                // Required(2), Resource(3)
						attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
						                    // Decline(2),
						                    // Invited(3),
						                    // Tentative(4)					
						Uri attendeuesesUri = cr.insert(Uri.parse(attendeuesesUriString), attendeesValues);
			        }
        		}
            }	    	
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in addToCalendar : "+sfg.toString());
			return eventId;
		}
		return eventId;
    }//fn addToCalendar
    
    public static void editToCalendar(Context ctx, final String title, final String desc, final long dtstart, final long dtend, Vector selContactVect, String eventId) { 
    	final ContentResolver cr = ctx.getContentResolver();
    	try{	    		    		    	
	    	// get calendar 
            Calendar cal = Calendar.getInstance();      
            // event insert 
            ContentValues values = new ContentValues(); 
            Uri event;    

            Uri updateUri = null;
            // The new title for the event
            values.put("calendar_id", 1); 
            values.put("title", title); 
            values.put("description", desc);             
            values.put("dtstart", dtstart); 
            values.put("dtend", dtend); 
            values.put("hasAlarm", 1);    
            values.put(Events.GUESTS_CAN_MODIFY, 0);
            values.put(Events.EVENT_TIMEZONE,TimeZone.getDefault().getID());  
            
            if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
            	updateUri = ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), Long.parseLong(eventId));
	    	}else{
	    		updateUri = ContentUris.withAppendedId(Uri.parse("content://calendar/events"), Long.parseLong(eventId));
	    	}            
            
            int rows = cr.update(updateUri, values, null, null);            
            SapGenConstants.showLog("Rows updated: " + rows); 

            /*if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
	    		cr.delete( Uri.parse("content://com.android.calendar/events"), "title=? ", new String[]{String.valueOf(title)});
	    	}else{
	    		cr.delete( Uri.parse("content://calendar/events"), "title=? ", new String[]{String.valueOf(title)});
	    	}
	    	
	    	if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) 
	    		event = cr.insert(Uri.parse("content://com.android.calendar/events"), values); 
	    	else
	    		event = cr.insert(Uri.parse("content://com.android.calendar/events"), values); */
	    	
	    	// reminder insert 
            values = new ContentValues(); 
            values.put( "event_id", Long.parseLong(eventId)); 
            values.put( "method", 1 ); 
            values.put( "minutes", 120 ); 
            
            if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) {
	    		cr.delete( Uri.parse("content://com.android.calendar/reminders"), "event_id=? ", new String[]{String.valueOf(Long.parseLong(eventId))});
	    	}else{
	    		cr.delete( Uri.parse("content://calendar/reminders"), "event_id=? ", new String[]{String.valueOf(Long.parseLong(eventId))});
	    	}
	    	
	    	if (Integer.parseInt(Build.VERSION.SDK) >= 8 ) 
	    		cr.insert(Uri.parse("content://com.android.calendar/reminders"), values); 
	    	else
	    		cr.insert(Uri.parse("content://com.android.calendar/reminders"), values); 
	    	
	    	String attendeuesesUriString = "content://com.android.calendar/attendees";

	        /********
	         * To add multiple attendees need to insert ContentValues multiple
	         * times
	         ***********/
	    	ContentValues attendeesValues;
	    	if(selContactVect != null && selContactVect.size() > 0){
        		for (int pos = 0; pos < selContactVect.size(); pos++){
	        		String name = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactName();
					String id = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getContactDeviceId();
					String sapid = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getSapId();
					String cusid = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getCustomerId();
					String fname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getFName();
					String lname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getLName();
					String orgname = (String)((ContactSAPDetails)selContactVect.elementAt(pos)).getOrgName();
									
					String contactName = fname+" "+lname;
					String[] Emails = new String[0];
					String strEmailHome = "", strEmailWork = "", strEmailOther = "", strEmail = "";
					
					if(id != null && id.length() > 0){
						// Get value for Emails
						Emails = getContactEmailsDetails(ctx, id);
					}
					if(Emails != null && Emails.length > 0){
						strEmailHome = Emails[0];
			         	strEmailWork = Emails[1];
			         	strEmailOther = Emails[2];
					}				 	
			        
			        if(strEmailWork != null && strEmailWork.length() > 0){
			        	strEmail = strEmailWork;
			        }else if(strEmailOther != null && strEmailOther.length() > 0){
			        	strEmail = strEmailOther;
			        }else if(strEmailHome != null && strEmailHome.length() > 0){
			        	strEmail = strEmailHome;
			        }
			        
			        if(strEmail != null && strEmail.length() > 0){
						attendeesValues = new ContentValues();
				        attendeesValues.put("event_id", Long.parseLong(eventId));
				        attendeesValues.put("attendeeName", contactName); // Attendees name
			        	attendeesValues.put("attendeeEmail", strEmail);// Attendee // Email id
			        	attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
						                        // Relationship_None(0),
						                        // Organizer(2),
						                        // Performer(3),
						                        // Speaker(4)
						attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
						                // Required(2), Resource(3)
						attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
						                    // Decline(2),
						                    // Invited(3),
						                    // Tentative(4)					
						cr.update(Uri.parse(attendeuesesUriString), attendeesValues, null, null);								
			        }
        		}
            }
		}
		catch(Exception sfg){
			SapGenConstants.showErrorLog("Error in addToCalendar : "+sfg.toString());
		}
    }//fn addToCalendar
    
    public static String[] getContactEmailsDetails(Context ctx, String contactId){
		String strEmailHome = "", strEmailWork = "", strEmailOther = "";
		String[] result = new String[3];
		ContentResolver cr = ctx.getContentResolver();
    	try {
    		// Get value for Emails
    		Cursor emailCur = cr.query( 
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{String.valueOf(contactId)}, null); 
    		while (emailCur.moveToNext()) { 
    		    // This would allow you get several email addresses
    	            // if the email addresses were stored in an array
    		    String email = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    	 	    String emailType = emailCur.getString(
    	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
    	 	    if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_HOME))){
    	 	    	strEmailHome = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK))){
    	 		   strEmailWork = email;
    	 	    }
    	 	    else if(emailType.equals(String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_OTHER))){
    	 		   strEmailOther = email;
    	 	    }
    	 	}
    		emailCur.close();
    		if(strEmailHome == null || strEmailHome.length() == 0){
    			strEmailHome = "";
    		}
    		if(strEmailWork == null || strEmailWork.length() == 0){
    			strEmailWork = "";
    		}
    		if(strEmailOther == null || strEmailOther.length() == 0){
    			strEmailOther = "";
    		}
         	result[0] = strEmailHome;
         	result[1] = strEmailWork;
         	result[2] = strEmailOther;
		} 
    	catch (Exception ssqw) {
    		SapGenConstants.showErrorLog("Error in getContactEmailsDetails:"+ssqw.getMessage());
    		result[0] = "";
         	result[1] = "";
         	result[2] = "";
		}
    	return result;
    }//fn getContactEmailsDetails
    
    public static String getGalleryImagePath(String time){
    	String path = "";
    	try{
    		File folder = new File(Environment.getExternalStorageDirectory() + "/Imgs/Gall"); 
			if (!folder.exists()) { 
			    folder.mkdir(); 
			} 
			if (folder.exists()) { 
				path = Environment.getExternalStorageDirectory() + "/Imgs/Gall/"+time+"_"+imageCountId+".jpg"; 
				File f = new File(path);
				if (f.exists()) {
					imageCountId += 1; 
					path = Environment.getExternalStorageDirectory() + "/Imgs/Gall/"+time+"_"+imageCountId+".jpg";
				}else{
					path = Environment.getExternalStorageDirectory() + "/Imgs/Gall/"+time+"_"+imageCountId+".jpg";
				}
			} else { 
				path = Environment.getExternalStorageDirectory() + "/Imgs/Gall/"+time+"_"+imageCountId+".jpg"; 
				File f = new File(path);
				if (f.exists()) {
					imageCountId += 1; 
					path = Environment.getExternalStorageDirectory() + "/"+time+"_"+imageCountId+".jpg";	
				}else{
					path = Environment.getExternalStorageDirectory() + "/"+time+"_"+imageCountId+".jpg";	
				}
			} 
    	} catch (Exception esfgf) {
    		SapGenConstants.showErrorLog("Error in getGalleryImagePath : "+esfgf.toString());
		}
    	return path;
    }//fn getGalleryImagePath
    
}//End of class CrtGenActivityConstants