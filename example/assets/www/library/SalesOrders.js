var timeZone = [];
var soapRespnse = [];

function sendSOToQueueProcessor(data)
{

var success = function(message) { timeZone = message; window.location.href = 'index.html';};
var error = function(message) { /*alert("Error: " + message);*/ };

window.plugins.salesorderPlugin.sendSOToQP(success,error,data);
}

function setNotifyFlagToQueueProcessor(data)
{

var success = function(message) { soapRespnse = message; /*alert("Success: " + JSON.stringify(message));alert(timeZone.length);*/   };
var error = function(message) { alert("Error: " + message); };

window.plugins.salesorderPlugin.SetNotifyFlagToQP(success,error,data);


}