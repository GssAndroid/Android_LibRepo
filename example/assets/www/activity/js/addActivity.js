var timeZoneSelectedList = [];

if(localStorage["TIMEZONE_SELECTED_LIST"] == null)
{
    localStorage["TIMEZONE_SELECTED_LIST"] = JSON.stringify(timeZoneSelectedList);
}
else
{
    timeZoneSelectedList = JSON.parse(localStorage["TIMEZONE_SELECTED_LIST"]);
}

window.localStorage.setItem("lastscreenid","ACTIVITY_DETAILS");

function ClearAllValues()
{
        $("#page").find("input:text").each(function(){ window.localStorage.removeItem(this.id);});
        $("#page").find("select").each(function(){ window.localStorage.removeItem(this.id);});
        $("#page").find("textarea").each(function(){ window.localStorage.removeItem(this.id);});
        backFunction = null;
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
		//alert(fieldAttrRow.NAME);
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
		return elementActionFieldLabel("ACTIVITYSAVE",getLabel(fieldLabel,fieldAttrRow.NAME));
	}
	return "";
}






function addActivity()
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
//alert(DATE_FROM);
var DATE_TO = $("#TODATEPICKER").val();
DATE_TO = converToStandardDateFormat(DATE_TO);
var TIME_FROM = $("#FROMTIMEPICKER").val();
var TIME_TO = $("#TOTIMEPICKER").val();
var TIMEZONE_FROM = $("#TIMEZONE").val();
var DUR_SEC = "1200";
var CATEGORY = $("#ACTIVITYCATEGORY").val();
var STATUS = $("#ACTIVITYSTATUS").val();
var STATUS_TEXT = $("#ACTIVITYSTATUS").text();
timeZoneSelectedList.push(TIMEZONE_FROM);
localStorage["TIMEZONE_SELECTED_LIST"] = JSON.stringify(timeZoneSelectedList);

var t1 = new Date(DATE_FROM.split("-")[0], DATE_FROM.split("-")[1], DATE_FROM.split("-")[2], TIME_FROM.split(":")[0], TIME_FROM.split(":")[1], 0)
var t2 = new Date(DATE_TO.split("-")[0], DATE_TO.split("-")[1], DATE_TO.split("-")[2], TIME_TO.split(":")[0], TIME_TO.split(":")[1], 0)
var dif = t1.getTime() - t2.getTime()

var Seconds_from_T1_to_T2 = dif / 1000;
var Seconds_Between_Dates = Math.abs(Seconds_from_T1_to_T2);

//DUR_SEC = Seconds_Between_Dates;

var addActReq = "ZGSXCAST_ACTVTY20[.][.][.]"+DESCRIPTION+"[.]"+TEXT+"[.]"+DATE_FROM+"[.]"+DATE_TO+"[.]"+TIME_FROM+"[.]"+TIME_TO+"[.]"+TIMEZONE_FROM+"[.]"+DUR_SEC+"[.]"+CATEGORY+"[.]"+STATUS+"[.]"+STATUS_TEXT+"[.]";
//alert(addActReq);
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
	//alert(items);
	console.log(JSON.stringify(items));
	soapRequest(saveActivityDetails,items);
}
}

function saveActivityDetails(xmlHttpRequest, status)
{
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATA PARSING DEVICE " + diagnosicsDate() +" </br></p>";

console.log(xmlHttpRequest.responseText);
	//alert(xmlHttpRequest.responseXML);
	
	 $(xmlHttpRequest.responseXML)
    .find('DpostMssg').find('item')
    .each(function()
    {
          //alert($(this).find('Type').text() );
          
        if($(this).find('Type').text() == "S" || $(this).find('Type').text() == "I")
        {
          alert($(this).find('Message').text());
          
          //dropDynamicTable(window.localStorage.getItem('9'));
		  //window.localStorage.removeItem('9');
          //alert($(this).find('MessageV2').text());
          
		window.localStorage.setItem($(this).find('MessageV2').text().replace(/^0+/, ''),window.localStorage.getItem("gallerypath"));
          //alert(window.localStorage.getItem($(this).find('MessageV2').text().replace(/^0+/, '')));
          	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATA PARSING DEVICE " + diagnosicsDate() +" </br></p>";
          	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
          
          window.localStorage.setItem("previous_disgnostics",disgnostics_response_parser);
          
          
          //moveDirectory(window.localStorage.getItem("gallerypath"),$(this).find('MessageV2').text());
          goBack();
          
        }
    });


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
	//$('#listContact-2').show();
	$('#listContact-1').listview('refresh');
	//$('#listContact-2').listview('refresh');
    $("#page").trigger("create");
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
