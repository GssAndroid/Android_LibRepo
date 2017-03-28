var timeZone = [];

function sendToQueueProcessor(data)
{

var success = function(message) { timeZone = message; /*alert("Success: " + JSON.stringify(message));alert(timeZone.length);*/   };
var error = function(message) { /*alert("Error: " + message);*/ };

window.plugins.QueueProcessorPluginToPhonegap.sendToPhoneGap(success,error,data);


}