// For IN CASE SENSITIVE CONTAINS CUSTOM

var disgnostics_response_parser;
function getURLParameter(name) {
      return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]);
  }

$.extend($.expr[":"], {
	"containsIN": function(elem, i, match, array) {
	return (elem.textContent || elem.innerText || "").toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
	}
	});

function checkConnection() {
    var networkState = navigator.connection.type;
    //alert(networkState);
    var states = {};
    states[Connection.UNKNOWN]  = 'Unknown connection';
    states[Connection.ETHERNET] = 'Ethernet connection';
    states[Connection.WIFI]     = 'WiFi connection';
    states[Connection.CELL]  = 'Cell 2G connection';
    states[Connection.CELL_2G]  = 'Cell 2G connection';
    states[Connection.CELL_3G]  = 'Cell 3G connection';
    states[Connection.CELL_4G]  = 'Cell 4G connection';
    states[Connection.NONE]     = 'No network connection';
    //alert('Connection type: ' + states[networkState]);    
    return networkState;    
}

function replaceAll(find, replace, str) {
	 
	 /*alert(find);
	 alert(replace);
	 alert(str);*/
	 
	 //alert(str.replace(new RegExp(find, 'g'), replace));
	 
	    return str.replace(new RegExp(find, 'g'), replace);
	}

function responseTypeDecider(key, id)
{
	if(id != null && id.indexOf("true") > -1)
	{
		return "";
	}else{
		window.localStorage.setItem(key, "true");
		return "[.]RESPONSE-TYPE[.]FULL-SETS";
	}
}

var dateFormat;
function getDateFormat() {
    navigator.globalization.getDatePattern(
    	function (date) {dateFormat = date.pattern;},
    	function () {alert('Error getting pattern\n');},
    	{formatLength:'short', selector:'date'}
  	);
}

function checkOffline()
{
	if(checkConnection() == "none")
	{
		$( "#screenTitle" ).after( "<span>(Offline)</span>" );
		offlineBorder();
	}
}

function offlineBorder()
{
	$( "[data-role=header]" ).addClass('offline-border-header');
	$( "[data-role=footer]" ).addClass('offline-border-footer');
	$( "[data-role=page]" ).addClass('offline-border-page');

}

var loadfunction = null;
function goBack()
{
    window.history.back()
}

var colors = ['#ffefe4'];
$(document).delegate(".ui-page", "pagebeforeshow", function () {
    $(this).css('background', colors[0]);
});

var app = {
	// Application Constructor		
	initialize: function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// 'load', 'deviceready', 'offline', and 'online'.
	bindEvents: function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);	      
	},
	// deviceready Event Handler
	//
	// The scope of 'this' is the event. In order to call the 'receivedEvent'
	// function, we must explicity call 'app.receivedEvent(...);'
	onDeviceReady: function() {
		app.receivedEvent('deviceready');
	},
	// Update DOM on a Received Event
	receivedEvent: function(id) {	    
		checkOffline();
		getDateFormat();
		createDatabase();
		console.log("init calling");	    	
		if(loadfunction != null)
			loadfunction();
	}
};
