var activityIdList = [];
var timeZoneSelectedList = [];


var d = new Date();
var activityId = d.getTime();
window.localStorage.setItem("gallerypath", "SalesPro/Activities/"+activityId);

function getActivityList()
{
	if(checkConnection() != "none")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:MOBILEPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	//items.push("EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0");
	items.push("EVENT[.]ACTIVITIES-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('9'));
	soapRequest(getActivityListResponse,items);
	}
	else
	{
		checkActivityListdatabase();
	}
}


function getActivityListResponse(xmlHttpRequest, status)
{
	//alert(xmlHttpRequest.responseText);
	
	//alert($(xmlHttpRequest.responseXML).find('item').children().size());
	
	if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
	{
		if($(xmlHttpRequest.responseXML).find('item').children().size() != 8)
		{
			dropDynamicTable(window.localStorage.getItem('9'));
		  	window.localStorage.removeItem('9');
		  	
		  	dropDynamicTable(window.localStorage.getItem('13'));
		  	window.localStorage.removeItem('13');
		 }
	}
	else
	{
		if($(xmlHttpRequest.responseXML).find('item').children().size() != 4)
		{
			dropDynamicTable(window.localStorage.getItem('9'));
		  	window.localStorage.removeItem('9');
		  	
		  	dropDynamicTable(window.localStorage.getItem('13'));
		  	window.localStorage.removeItem('13');
		 }
	}
	
	
	
	
	
	
	
	console.log(xmlHttpRequest.responseXML);
	showWaitMe("Compiling Data");
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATABASE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	
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
					whereColumns["value"] = 9;
					
					updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
					
				}
				if(i == 3)
				{
					var result = name.split("[.]");
					
					createDynamicTable(result);
					
					var updateColumns = [];
					updateColumns.push({column:'tname',value:result[1]});
					
					var whereColumns = {};
					whereColumns["column"] = "id";
					whereColumns["value"] = 13;
					
					updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
					
				}
				if(i>=4)
				{
					var result = name.split("[.]");
					
					insertTableValues(result)
				}
				i++;
		});
		disgnostics_response_parser = disgnostics_response_parser + "<p>STOP DATA PARSING DEVICE:" + diagnosicsDate() + "</br></p>" ;
		checkActivityListdatabase();
		
}



function checkActivityListdatabase()
{  	
    db.transaction(getActivityListOfflineValues, errorCB);
}

function getActivityListOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 9', [], showActivityListquerySuccess, errorCB);
}

function showActivityListquerySuccess(tx, results) 
{
	var len = results.rows.length;
	//alert(len);
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname;
		console.log(query);
		selectTableValues(query,activityList);
	}
}



function activityList(results)
{
			
			disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATABASE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
			disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START UI RENDERING DEVICE " + diagnosicsDate() +" </br></p>";
	
	var len = results.rows.length;
	dynamicViewTable(results,fieldAttr,fieldLabel,"table1","OBJECT_ID");
	
	//dynamicViewList(results,fieldAttr,fieldLabel,"table1","OBJECT_ID");
	
	var len = results.rows.length;
	for(var i = 0; i < len; i++)
	{
		var rows = results.rows.item(i);
		activityIdList.push(rows["OBJECT_ID"]);
		timeZoneSelectedList.push(rows["TIMEZONE_FROM"]);
		CreateCalendarEvent(rows["DATE_FROM"],rows["TIME_FROM"],rows["DATE_TO"],rows["TIME_TO"],rows["KUNNR_NAME"],rows["DOCUMENTTYPE_DESCRIPTION"],rows["DESCRIPTION"]);
	}
	localStorage["ACTIVITY_ID_LIST"] = JSON.stringify(activityIdList);
	//localStorage["TIMEZONE_SELECTED_LIST"] = JSON.stringify(timeZoneSelectedList);
	hideWaitMe();
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP UI RENDERING DEVICE " + diagnosicsDate() +" </br></p>";
	
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	
	if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
	{
		if(window.localStorage.getItem("previous_disgnostics") != null)
		{
		 disgnostics_response_parser = window.localStorage.getItem("previous_disgnostics") + disgnostics_response_parser;
		 window.localStorage.removeItem("previous_disgnostics");
		 }
		runtimePopup(disgnostics_response_parser, null);
	}
}

