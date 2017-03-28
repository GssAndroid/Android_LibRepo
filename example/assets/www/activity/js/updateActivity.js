var PROCESSTYPE = "";

var activityIdList = JSON.parse(localStorage["ACTIVITY_ID_LIST"]);
var currentPosition;

var timeZoneSelectedList = [];

if(localStorage["TIMEZONE_SELECTED_LIST"] == null)
{
    localStorage["TIMEZONE_SELECTED_LIST"] = JSON.stringify(timeZoneSelectedList);
}
else
{
    timeZoneSelectedList = JSON.parse(localStorage["TIMEZONE_SELECTED_LIST"]);
}



function checkPosition()
{
	for(var i=0;i<activityIdList.length;i++)
	{
		if(activityIdList[i] == updateActivityId)
		{
			currentPosition = i;
		}
	}
}


function swipeLeft(event)
{
	//alert('swipeLeft');
	if(currentPosition != 0)
	{
		currentPosition = currentPosition - 1;
		updateActivityId = activityIdList[currentPosition];
		app.initialize();
	}
	
	event.stopImmediatePropagation();
}

function swipeRight(event)
{
	//alert('swipeRight');
	if(currentPosition != (activityIdList.length-1))
	{
		currentPosition = currentPosition + 1;
		updateActivityId = activityIdList[currentPosition];	
		app.initialize();
	}
	event.stopImmediatePropagation();
}



window.localStorage.setItem("lastscreenid","ACTIVITY_DETAILS");

function ClearAllValues()
{
        $("#page").find("input:text").each(function(){ window.localStorage.removeItem(this.id);});
        $("#page").find("select").each(function(){ window.localStorage.removeItem(this.id);});
         $("#page").find("textarea").each(function(){ window.localStorage.removeItem(this.id);});
        backFunction = null;
}

function getActivityDetails() {
	
	checkdatabase();
	//alert(window.localStorage.getItem(updateActivityId));
	window.localStorage.setItem("gallerypath", window.localStorage.getItem(updateActivityId));
	
}
function checkdatabase()
{  	
    db.transaction(getOfflineValues, errorCB);
}


function getOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 9', [], showquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 13', [], showqueryContactSuccess, errorCB);
}



function showqueryContactSuccess(tx, results) 
{
  
	var len = results.rows.length;
	//alert(len);
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE OBJECT_ID = "' + updateActivityId +'"';
		console.log(query);
		selectTableValues(query,updateContactDetails);
	}
  
 
}


function updateContactDetails(results)
{

	var selectedContacts = []; 
	selectedContacts = JSON.parse(localStorage["selectedContacts"])
	
	//alert(localStorage["selectedContacts"]);
	//alert(selectedContacts.length);
	if(selectedContacts.length == 0)
	{
		
		var len = results.rows.length;
		//alert(len);
		for (var i=0; i<len; i++)
		{
			var rows = results.rows.item(i);
			
			var customerId = rows["PARNR"];
			
			if(customerId)
			{
			var contacts = {};
    		contacts.id = localStorage[customerId];
    		contacts.details = rows["PARNR_NAME"] + " " + rows["KUNNR_NAME"];
    		//contacts.fullobject = 
    		selectedContacts.push(contacts);
			}
			
		}
		
		if(selectedContacts.length != 0)
    	{
    		
    		localStorage["selectedContacts"] = JSON.stringify(selectedContacts);
    		//alert(localStorage["selectedContacts"]);
    	}
	}
	
	setContactDetails();
	//alert(localStorage["selectedContacts"]);
}




function setContactDetails()
{
	var selectContacts = JSON.parse(localStorage["selectedContacts"]);
	$("#listContact-1").text("");
	$("#listContact-2").text("");
	if(selectContacts != null && selectContacts.length != 0)
	{
		$("#txtCustomerValue").text("Customers/Contacts  (" + selectContacts.length + ")");
		$("#deleteIconContacts").show();
		$("#btnPickContact .ui-btn-text").text("Pick More Contacts");
	}
	else
	{
		$("#txtCustomerValue").text("Customers/Contacts");
		$("#deleteIconContacts").hide();
		$("#listContact-1").text("No Contacts Selected");
		$("#btnPickContact .ui-btn-text").text("Pick Your Contacts");
	}
	
	if(selectContacts != null && selectContacts.length != 0)
	{
	for(var i = 0; i < selectContacts.length; i++)
	{
		var element = '<li><fieldset data-role="controlgroup" ><input type="checkbox" name="contacts"';
        element = element +  'value="' + selectContacts[i].id + '" id="cb-'+i+'"/>';
 		element = element + ' <label  class="bgcolor bgclorborder" for="cb-'+i+'" >';
 		element = element + selectContacts[i].details + "     ";
 		
        element = element + ' </label></fieldset></li>';
         
        console.log(element);
        
        $("#listContact-1").append(element);
        
        /*if(i%2 == 0)
        	$("#listContact-1").append(element);
        else
        	$("#listContact-2").append(element);*/
 		//$("#listContact").append(element);
		
	}
	
	$('#listContact-1').show();
	$('#listContact-2').show();
	$('#listContact-1').listview('refresh');
	$('#listContact-2').listview('refresh');
    $("#page").trigger("create");
    $("#ContactsParentDiv").niceScroll();
	}
}

function deleteActivityContacts()
{
	var deletedContacts = []; 
	var selectContacts = JSON.parse(localStorage["selectedContacts"]);
	
	for(i = 0; i < selectContacts.length; i++)
	{
		var check = false;
		$("input:checkbox[name=contacts]:checked").each(function()
		{
			 if($(this).val() == selectContacts[i].id)
				 check = true;
		});
		
		if(!check)
			deletedContacts.push(selectContacts[i]);
	}

	localStorage["selectedContacts"] = JSON.stringify(deletedContacts);
	setContactDetails();
}

function showquerySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE OBJECT_ID = "' + updateActivityId +'"';
		console.log(query);
		selectTableValues(query,dynamicUI);
	}
  
 
}

function dynamicUI(results)
{
	var len = results.rows.length;
	
	for (var i=0; i<len; i++)
	{
		
		var rows = results.rows.item(i);
		//alert(rows["CATEGORY"]);
		$("#ACTIVITYCATEGORY").val(rows["CATEGORY"]);
		//alert(rows["CATEGORY"]);
		$("#ACTIVITYSTATUS").val(rows["STATUS"]);
		$("#FROMDATEPICKER").val(converToSystemDateFormat(rows["DATE_FROM"]));
		$("#FROMTIMEPICKER").val(rows["TIME_FROM"]);
		$("#TODATEPICKER").val(converToSystemDateFormat(rows["DATE_TO"]));
		$("#TOTIMEPICKER").val(rows["TIME_TO"]);
		$("#TIMEZONE").val(rows["TIMEZONE_FROM"]);
		$("#ACTIVITYBRIEF").val(rows["DESCRIPTION"]);
		$("#ACTIVITYNOTES").val(rows["TEXT"]);
		PROCESSTYPE = rows["PROCESS_TYPE"];
		
		//alert($("#ACTIVITYCATEGORY").val());		   
	}
	
	
	 //$("#page").find("span[data-field]").each(function(){ this.innerHTML = (window.localStorage.getItem(this.id) != "null") ? window.localStorage.getItem(this.id) : this.innerHTML;});
     //$("#page").find("input:text").each(function(){ this.value = (window.localStorage.getItem(this.id) != "null") ? window.localStorage.getItem(this.id) : this.value;});
     //$("#page").find("select").each(function(){ this.value = (window.localStorage.getItem(this.id) != "null") ? window.localStorage.getItem(this.id) : this.value;});
     
     //alert($("#ACTIVITYNOTES").val());
     
     $("#page").trigger("create");
	 //$("#dtBox").DateTimePicker();
	 
	  
	 $(function () {
            var curr = new Date().getFullYear();
            var opt = {
                'date': {
                    preset: 'date',
                    dateOrder: replaceAll('yyyy','yy',replaceAll('/','',dateFormat)),
                    dateFormat: replaceAll('yyyy','yy',dateFormat)
                },
                'datetime': {
                    preset: 'datetime'
                },
                'time': {
                    preset: 'time',
                    timeFormat: 'HH:ii:ss',
                    timeWheels: 'HHiiss'
                },
                'credit': {
                    preset: 'date',
                    dateOrder: 'mmyy',
                    dateFormat: 'mm/yy',
                    startYear: curr,
                    endYear: curr + 10,
                    width: 100
                },
                'btn': {
                    preset: 'date',
                    showOnFocus: false
                },
                'inline': {
                    preset: 'date',
                    display: 'inline'
                }
            }

         $( ".datepicker" ).each(function( index ) {
  			$(this).val($(this).val()).scroller('destroy').scroller($.extend(opt['date'], { theme: 'ios', mode: 'scroller' }));
		});
		
		$( ".timepicker" ).each(function( index ) {
  			$(this).val($(this).val()).scroller('destroy').scroller($.extend(opt['time'], { theme: 'ios', mode: 'scroller' }));
		});
         
         
        //$('.datepicker').val($(this).val()).scroller('destroy').scroller($.extend(opt['date'], { theme: 'ios', mode: 'scroller' }));
        //$('.timepicker').val($(this).val()).scroller('destroy').scroller($.extend(opt['time'], { theme: 'ios', mode: 'scroller' }));
          
        });		
}


function listViewUIAddActivity(fieldAttrRow,fieldLabel)
{
	if(fieldAttrRow.VALUE == "DESCRIBED")
	{
		//alert(fieldAttrRow.TRGTNAME.split("#")[1].split("-")[1]);
		return describedElementSectionDialog("ACTIVITYCATEGORY",fieldAttrRow.NAME.split("-")[1],fieldAttrRow.TRGTNAME.split("#")[1].split("-")[1],ACTIVITYCATEGORYRESULTS)
	}
	else if(fieldAttrRow.VALUE == "SELECTION-DIALOG" && fieldAttrRow.NAME.indexOf('TIMEZONE_FROM') == -1)
	{
		return elementSectionDialog("ACTIVITYSTATUS","STATUS",fieldAttrRow.NAME.split("-")[1],ACTIVITYSTATUSRESULTS)
	}
	else if(fieldAttrRow.VALUE == "DATE-PICKER" && fieldAttrRow.NAME.indexOf('DATE_FROM') != -1)
	{
		return elementDatePicker("FROMDATEPICKER","");
	}
	else if(fieldAttrRow.VALUE == "TIME-PICKER" && fieldAttrRow.NAME.indexOf('TIME_FROM') != -1)
	{
		return elementTimePicker("FROMTIMEPICKER","");
	}
	else if(fieldAttrRow.VALUE == "DATE-PICKER" && fieldAttrRow.NAME.indexOf('DATE_TO') != -1)
	{
		return elementDatePicker("TODATEPICKER","");
	}
	else if(fieldAttrRow.VALUE == "TIME-PICKER" && fieldAttrRow.NAME.indexOf('TIME_TO') != -1)
	{
		return elementTimePicker("TOTIMEPICKER","");
	}
	else if(fieldAttrRow.VALUE == "SELECTION-DIALOG" && fieldAttrRow.NAME.indexOf('TIMEZONE_FROM') != -1)
	{
		return elementSectionDialogWithValues("TIMEZONE",timeZone);
	}
	else if(fieldAttrRow.VALUE == "EDITABLE" && fieldAttrRow.NAME.indexOf('DESCRIPTION') != -1)
	{
		return elementEditable("ACTIVITYBRIEF","","",getHint(fieldLabel,fieldAttrRow.NAME));
	}
	else if(fieldAttrRow.VALUE == "EDITABLE" && fieldAttrRow.NAME.indexOf('TEXT') != -1)
	{
		return elementTextarea("ACTIVITYNOTES","","",getHint(fieldLabel,fieldAttrRow.NAME));
	}
	else if(fieldAttrRow.VALUE == "ACTION-FIELD-ICON:ICON-IMAGE-CAPTURE" && fieldAttrRow.NAME.indexOf('ACTION-IMAGE-CAPTURE') != -1)
	{
		return elementActionFieldIcon("ACTIVITYIMAGECAPTURE","","custom");
	}
	else if(fieldAttrRow.VALUE == "ACTION-FIELD-ICON:ICON-GALLERY" && fieldAttrRow.NAME.indexOf('ACTION-GALLERY') != -1)
	{
		return elementActionFieldIcon("ACTIVITYGALLERY","","grid");
	}
	else if(fieldAttrRow.VALUE == "ACTION-FIELD-LABEL" && fieldAttrRow.NAME.indexOf('ACTION-FIELD-CANCEL') != -1)
	{
		return elementActionFieldLabel("ACTIVITYCANCEL",getLabel(fieldLabel,fieldAttrRow.NAME));
	}
	else if(fieldAttrRow.VALUE == "ACTION-FIELD-LABEL" && fieldAttrRow.NAME.indexOf('ACTION-FIELD-SAVE') != -1)
	{
		return elementActionFieldLabel("ACTIVITYUPDATE",getLabel(fieldLabel,fieldAttrRow.NAME));
	}
	
	return "";
}

function updateActivity()
{
dirReader(window.localStorage.getItem("gallerypath"),saveActivity);
//saveActivity();

}

function addActivityRequest()
{

var DESCRIPTION = $("#ACTIVITYBRIEF").val();
var TEXT = $("#ACTIVITYNOTES").val();
var DATE_FROM = $("#FROMDATEPICKER").val();
DATE_FROM = converToStandardDateFormat(DATE_FROM);
var DATE_TO = $("#TODATEPICKER").val();
DATE_TO = converToStandardDateFormat(DATE_TO);
var TIME_FROM = $("#FROMTIMEPICKER").val();
var TIME_TO = $("#TOTIMEPICKER").val();
var TIMEZONE_FROM = $("#TIMEZONE").val();
var DUR_SEC = "1200";
var CATEGORY = $("#ACTIVITYCATEGORY").val();
var CATEGORY = $("#ACTIVITYCATEGORY").val();
var STATUS = $("#ACTIVITYSTATUS").val();
var STATUS_TEXT = $("#ACTIVITYSTATUS").text();

var t1 = new Date(DATE_FROM.split("-")[0], DATE_FROM.split("-")[1], DATE_FROM.split("-")[2], TIME_FROM.split(":")[0], TIME_FROM.split(":")[1], 0)
var t2 = new Date(DATE_TO.split("-")[0], DATE_TO.split("-")[1], DATE_TO.split("-")[2], TIME_TO.split(":")[0], TIME_TO.split(":")[1], 0)
var dif = t1.getTime() - t2.getTime()

var Seconds_from_T1_to_T2 = dif / 1000;
var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);

//DUR_SEC = Seconds_Between_Dates;
DUR_SEC = "1200";

var addActReq = "ZGSXCAST_ACTVTY20[.]"+updateActivityId+"[.]"+PROCESSTYPE+"[.]"+DESCRIPTION+"[.]"+TEXT+"[.]"+DATE_FROM+"[.]"+DATE_TO+"[.]"+TIME_FROM+"[.]"+TIME_TO+"[.]"+TIMEZONE_FROM+"[.]"+DUR_SEC+"[.]"+CATEGORY;
return addActReq;
}



function saveActivity()
{

if(checkConnection() != "none")
{
disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	var items = new Array();
	
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:MOBILEPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]ACTIVITY-GENERIC-MAINTAIN[.]VERSION[.]0");
	items.push("DATA-TYPE[.]ZGSXCAST_ACTVTY20[.]OBJECT_ID[.]PROCESS_TYPE[.]DESCRIPTION[.]TEXT[.]DATE_FROM[.]DATE_TO[.]TIME_FROM[.]TIME_TO[.]TIMEZONE_FROM[.]DURATION_SEC[.]CATEGORY[.]STATUS[.]STATUS_TXT30[.]STATUS_REASON");
	items.push(addActivityRequest());
	
	var selectContacts = JSON.parse(localStorage["selectedContacts"]);
	if(selectContacts != null && selectContacts.length != 0)
	{
		for(var i = 0; i < selectContacts.length; i++)
		{
			items.push("ZGSXCAST_CSTMRCNTCT10S[.]"+window.localStorage.getItem("ORG"+selectContacts[i].id)+"[.][.]"+window.localStorage.getItem(selectContacts[i].id)+"[.][.]");
		}
	}
	
	
	for(var i = 0; i < filePth.length; i++)
	{
		items.push(attachmentReq[i]) ;
	}
	soapRequest(saveActivityDetails,items);
}
}

function saveActivityDetails(xmlHttpRequest, status)
{

console.log(xmlHttpRequest.responseText);
	//alert(xmlHttpRequest.responseXML);
		disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATA PARSING DEVICE " + diagnosicsDate() +" </br></p>";
	
	
	 $(xmlHttpRequest.responseXML)
    .find('DpostMssg').find('item')
    .each(function()
    {
          //alert($(this).find('Type').text() );
          
        if($(this).find('Type').text() == "S" || $(this).find('Type').text() == "I")
        {
          alert($(this).find('Message').text());
          dropDynamicTable(window.localStorage.getItem('9'));
		  window.localStorage.removeItem('9');
		  
		  disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATA PARSING DEVICE " + diagnosicsDate() +" </br></p>";
          disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
          
          window.localStorage.setItem("previous_disgnostics",disgnostics_response_parser);
		  
           goBack();
          
        }
    });
	
}

