var queueContactObject = new Array();
var queueIndex = 0;
var totalContacts;

function getContactsListSAP()
{
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(checkConnection() != "none")
	{
		var items = new Array();
		items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:MOBILEPRO");
		items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
		items.push("EVENT[.]CONTACT-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('12'));
		soapRequest(getContactsListSAPResponse,items);
	}
	else
	{
		checkContextPriceListdatabase();
	}
	
	window.localStorage.setItem("lastscreenid", "CONTACT_INDEX");
}

function getContactsListSAPResponse(xmlHttpRequest, status)
{
	//addContacts("hi");
    //createContact();
	console.log("Context Response ::" + xmlHttpRequest.responseText);
	//console.log("Context Response ::" + xmlHttpRequest.responseXML);
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	showWaitMe("Compiling Data");
    
    totalContacts = $(xmlHttpRequest.responseXML).find('item').length;
    totalContacts = totalContacts - 3;
    
	var i = 0;
	$(xmlHttpRequest.responseXML)
    .find('item')
    .each(function()
 	{
   		var name = $(this).find('Cdata').text();
   		
   		if(i == 2)
		{
			var result = name.split("[.]");
			
			createDynamicTable(result);
			
			var updateColumns = [];
			updateColumns.push({column:'tname',value:result[1]});
			
			var whereColumns = {};
			whereColumns["column"] = "id";
			whereColumns["value"] = 12;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
			
		}
   		
   		
   		if(i>=3)
   		{
   			//alert(name);
            queueContactObject.push(name);
   			var result = name.split("[.]");
   			insertTableValues(result);
   			
   			
   		}
   		i++;
   				//alert(name);
	});
	
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
    addContacts();
	checkContactListdatabase();
	
}



function checkContactListdatabase()
{  	
    db.transaction(getContactOfflineValues, errorCB);
}

function getContactOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 12', [], showqueryContactListSuccess, errorCB);
}

function showqueryContactListSuccess(tx, results) 
{
  showWaitMe("Compiling Data....");
	var len = results.rows.length;
	//alert(len);
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);
		
		hideWaitMe();
		
		disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
		if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
			runtimePopup(disgnostics_response_parser, null);
		
	}
}

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Contact Length ::" + len);
	//add(results);
    chooseContact();
}




function chooseContact() {
    var options = new ContactFindOptions();
    options.filter = "Aaron";          // empty search string returns all contacts
    options.multiple = true;
    var fields = ["displayName", "name", "emails", "phoneNumbers","addresses","organizations"];
    navigator.contacts.find(fields,onChooseContactSuccess, onError,options);
}

function onChooseContactSuccess(contact) {
    //alert(contact.length);
    console.log(JSON.stringify(contact));
    
    
    for(var i = 0; i < contact.length; i++)
    {
        var query = "<tr>";
        
        var name = contact[i].name.formatted;
        
        var organization = "";
        
        if(contact[i].organizations != null)
            organization = contact[i].organizations[0].name;
        
        
        var address = [];
        
        if(contact[i].addresses != null)
            address = contact[i].addresses;
        
        var email = [];
        
        if(contact[i].emails != null)
            email = contact[i].emails;
        
        var phone = [];
        
        if(contact[i].phoneNumbers != null)
            phone = contact[i].phoneNumbers;
        
        var contactRowResult = contactRow(name,organization,address,email,phone);
        
        query = query +  contactRowResult + "</tr>";
        $('#table1 tbody').append(query);
    }
    
    var $table =  $("#table1").tablesorter({
                                           widgets        : ['zebra', "filter"],
                                           widgetOptions : {filter_anyMatch : true,filter_columnFilters: false}});
	
	
    
	
    $.tablesorter.filter.bindSearch( $table, $('.search') );
    $("#page").trigger("create");
    $("#contentTable").niceScroll();
    
    
}

function contactRow(name,organization,address,email,phone)
{
    var td = '<td>';
    var nameElement = '<p>' + name + '</p>';
    var organizationElement = '<p>' + organization + '</p>';
    td = td + nameElement + organizationElement + '</td>';
    
    var addressElement = "";
    for(var i = 0; i < address.length; i++)
    {
        addressElement = addressElement + '<p> <a href="#" onclick="openMap(this.innerHTML)">' + address[i].streetAddress + " " + address[i].locality + " " + address[i].region + " " + address[i].country + " " + address[i].postalCode + '</a></p>';
    }
    
    td = td + '<td>' + addressElement + '</td>';
    
    var emailElement = "";
    for(var i = 0; i < email.length; i++)
    {
        emailElement = emailElement + '<p> <a href="#" onclick="openMail(this.innerHTML)">' + email[i].value + '</a></p>';
    }
    
    td = td + '<td>' + emailElement + '</td>';
    
    var phoneElement = "";
    for(var i = 0; i < phone.length; i++)
    {
        phoneElement = phoneElement + '<p> <a href="#" onclick="openTel(this.innerHTML)">' + phone[i].value + '</a></p>';
    }
    
    td = td + '<td>' + phoneElement + '</td>';
    
    
    td = replaceAll('null','',td);
    
    return td;
    //var address =
    
}

function openMap(address)
{
    //window.open('http://maps.google.com/maps?daddr='+address);
    //location.target = "_system";
    //window.location ="http://maps.google.com/maps?daddr="+address;
    location.href= "geo:0,0?q="+address;
    //$( "#popupMap" ).popup( "open" );
    
    //location.href="geo://?daddr=chennai";

	//location.href="geo:894%20Granville%20Street%20Vancouver%20BC%20V6Z%201K3";
    
    //document.getElementById('mapIframe').src = "https://www.google.com/maps/embed/v1/directions?key=AIzaSyAlpEX27uvPadycu3VZ0_3wXfXMXBFSeLw&origin=My Location&destination="+address;
    
    
}

function openMail(address)
{
    location.href="mailto:"+address;
}

function openTel(address)
{
    //window.open('tel:'+address, '_system');
    location.href="tel:"+address;
}

function add(values)
{
    var len = values.rows.length;
    
	for(var i = 0; i < len; i++)
	{
		var query = "<tr>";
		var rows = values.rows.item(i);
		var cls = "";
		
		for(var k = 0; k < 4; k++)
		{
			if( k ==0)
            {
                
            }
			
			query = query + "<td class='" + cls  +"'>" + rows[label] + "</td>";
			//alert(query);
		}
        
		query = query +  "</tr>";
		
		$('#table1 tbody').append(query);
		
	}
}


function addContacts()
{
    
    var result = queueContactObject[queueIndex].split("[.]");
    
	var contactDetails = new Object();
	
	contactDetails.displayName = result[2];
	contactDetails.name = new Object();
	contactDetails.name.givenName = result[2];
	contactDetails.name.formatted = result[2] + " " + result[3];
	contactDetails.name.familyName = result[3];
	contactDetails.nickname = result[2];
	
	var phoneNumber = new Array();
	
	var phoneNumbersObject = new Object();
	phoneNumbersObject.type = "work";
	phoneNumbersObject.value = result[5];
	phoneNumber.push(phoneNumbersObject);
	
	phoneNumbersObject = new Object();
	phoneNumbersObject.type = "mobile";
	phoneNumbersObject.value = result[6];
	phoneNumber.push(phoneNumbersObject);
	
	phoneNumbersObject = new Object();
	phoneNumbersObject.type = "personal";
	phoneNumbersObject.value = result[7];
	phoneNumber.push(phoneNumbersObject);
	
	phoneNumbersObject = new Object();
	phoneNumbersObject.type = "other";
	phoneNumbersObject.value = result[8];
	phoneNumber.push(phoneNumbersObject);
	
	contactDetails.phoneNumbers = new Array();
	contactDetails.phoneNumbers = phoneNumber;
	
	var emailIds = new Array();
	
	var emailIdsObject = new Object();
	emailIdsObject.type = "work";
	emailIdsObject.value = result[9];
	emailIds.push(emailIdsObject);
	
	emailIdsObject = new Object();
	emailIdsObject.type = "personal";
	emailIdsObject.value = result[10];
	emailIds.push(emailIdsObject);
	
	emailIdsObject = new Object();
	emailIdsObject.type = "other";
	emailIdsObject.value = result[11];
	emailIds.push(emailIdsObject);
	
	contactDetails.emails = new Array();
	contactDetails.emails = emailIds;
	
	contactDetails.ims = result[12];
	
	var address = new Array();
	
	var addressObject = new Object();
	addressObject.type = "work";
	addressObject.postalCode = result[17];
	addressObject.locality = result[15];
	addressObject.streetAddress = result[14];
	addressObject.region = result[16];
	addressObject.country = result[18];
	address.push(addressObject);
	
	contactDetails.addresses = new Array();
	contactDetails.addresses = address;
	
	var organization = new Array();
	
	var organizationsObject = new Object();
	organizationsObject.type = "work";
	organizationsObject.name = result[19];
	organizationsObject.title = result[20];
	organization.push(organizationsObject);
	
	contactDetails.organizations = new Array();
	contactDetails.organizations = organization;
	
	//alert(JSON.stringify(contactDetails));
	
	console.log(JSON.stringify(contactDetails));
	
	
	var contactObj = navigator.contacts.create(contactDetails);
	
    queueIndex++;
    
    showWaitMe("Syncing Contacts " + queueIndex + " Out Of " + totalContacts);
    
	//contactObj.save(onSuccess,onError);
    
   
	
	
}


 function onSuccess(contacts) {
        //alert(JSON.stringify(contacts));
        //alert(contacts.id);
     hideWaitMe();
        addContacts();
  }

    // onError: Failed to get the contacts
    //
    function onError(contactError) {
        alert('onError!');
        hideWaitMe();
        addContacts();
    }




