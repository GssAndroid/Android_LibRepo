function CreateCalendarEvent(fromdate,fromtime,todate,totime,title,location,notes)
{
var fDate = fromdate.split("-");
var tDate = todate.split("-");
var fTime = fromtime.split(":");
var tTime = totime.split(":");

var startDate = new Date(fDate[0],fDate[1]-1,fDate[2],fTime[0],fTime[1],0,0,0); // beware: month 0 = january, 11 = december
var endDate = new Date(tDate[0],tDate[1]-1,tDate[2],tTime[0],tTime[1],0,0,0);

var title = title;
var location = location;
var notes = notes ;
var success = function(message) { /*alert("Success: " + JSON.stringify(message));*/ };
var error = function(message) { /*alert("Error: " + message);*/ };
var newTitle = title;

window.plugins.calendar.deleteEvent(newTitle,location,notes,startDate,endDate,success,error);

var calOptions = window.plugins.calendar.getCalendarOptions(); // grab the defaults
calOptions.firstReminderMinutes = 120; // default is 60, pass in null for no reminder (alarm)
window.plugins.calendar.createEventWithOptions(title,location,notes,startDate,endDate,calOptions,success,error);


}