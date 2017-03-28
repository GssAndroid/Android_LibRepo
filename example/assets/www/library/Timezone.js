var timeZone = [];

function getAllTimeZone()
{
	//timeZone = [];
var success = function(message) { timeZone = message; /*alert("Success: " + JSON.stringify(message));alert(timeZone.length);*/  };
var error = function(message) { /*alert("Error: " + message);*/ };

window.plugins.timeZonePlugin.listTimezone(success,error);


}