var timeZone = [];
var soapRespnse = [];

function sendToQueueProcessor(data)
{
//alert(data);
var success = function(message) { timeZone = message; /*alert("Success: " + JSON.stringify(message));alert(timeZone.length);*/   };
var error = function(message) { /*alert("Error: " + message);*/ };

window.plugins.queueprocessorPlugin.sendToQP(success,error,data);


}

function reciveFromQueueProcessor()
{

var success = function(message) { soapRespnse = message; /*alert("Success: " + JSON.stringify(message));alert(timeZone.length);*/   };
var error = function(message) { alert("Error: " + message); };

window.plugins.queueprocessorPlugin.recvFromQP(success,error);


}