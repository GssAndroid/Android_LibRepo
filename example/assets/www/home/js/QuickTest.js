
 function showAlert() {
		        navigator.notification.alert(
		            'Do you want to !',  // message
		            alertDismissed,         // callback
		            'Please provide Additional data',            // title
		            'Done'                  // buttonName
		        );
		    }
 
 function alertDismissed() {
	 //window.location.href = '../contacts/index.html';
	var success = function(message) {}//alert("Success: " + JSON.stringify(message));getContactsById(message);
		var error = function(message) {};		
		window.plugins.contactsLauncher.addContactsPage(success,error);
 }//
 
function quickTest()
{

if(checkConnection() != "none")
{

	var items = new Array();
	
	items.push("DEVICE-ID:000000000000000:DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]DIAGNOSIS-AND-CHECKS[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS");
	
	soapRequest(quickTestSuccess,items);
}
}

function quickTestSuccess(xmlHttpRequest, status)
{

console.log(xmlHttpRequest.responseText);
	//alert(xmlHttpRequest.responseXML);
	var i = 0;
	var response = "";
	$(xmlHttpRequest.responseXML)
    .find('item')
    .each(function()
 	{
   		var name = $(this).find('Cdata').text();
   		
   		if(i>=2)
   		{
   			//alert(name);
   			var result = name.split("[.]");
   			response = response + "<p>" + result[1] + "</br></p>" ;
   			
   		}
   		i++;
	});
	hideWaitMe();
	runtimePopup(response, null);
	
}

