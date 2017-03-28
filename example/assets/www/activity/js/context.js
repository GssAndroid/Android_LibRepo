var FIELD_ATTR_LIST = [];
var CUSTOMER_LABEL = "CUSTOM";



var fieldAttr;
var fieldLabel;

function getFieldAttr(results)
{
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{
		var rows = results.rows.item(i);
		console.log("ATTR RESULT:"  + rows.NAME);
	}
	fieldAttr = results;
	
}

function getFieldLabel(results)
{
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{
		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);
		   
	}
	fieldLabel = results;
	
}

function contextDetailsActivity()
{
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(checkConnection() != "none" && window.localStorage.getItem("lastscreenid") != "ACTIVITY_DETAILS")
	{
		var items = new Array();
		//items.push("DEVICE-ID:4BB99698EE13BC9F79448C030D67744C:DEVICE-TYPE:ANDROID:APPLICATION-ID:MOBILEPRO");
		items.push("DEVICE-ID:100000000000000:DEVICE-TYPE:BB:APPLICATION-ID:MOBILEPRO");
		items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
		//items.push("EVENT[.]ACTIVITY-CONTEXT-DATA-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]FULL-SETS");
		items.push("EVENT[.]ACTIVITY-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('8'));
		//items.push("EVENT[.]CONTACT-FOR-EMPLOYEE-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('8'));
		
		soapRequest(getContextDeailsActivity,items);
	}
	/*else if(window.localStorage.getItem("lastscreenid") == "ACTIVITY_DETAILS")
	{
		getActivityList();
	}*/
	else
	{
		checkContextActivitydatabase();
	}
	window.localStorage.setItem("lastscreenid", "ACTIVITY_INDEX");
}

function getScreenNameActivity(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$("#screenTitle").text(fieldLabelRow.VALUE);
		//alert(fieldLabelRow.VALUE);
	}
}

function getFieldHintActivitySearchbar(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$(".search").attr("placeholder", fieldLabelRow.VALUE);
		//alert(fieldLabelRow.VALUE);
	}
}

function getContextDeailsActivity(xmlHttpRequest, status)
{
	console.log("Context Response ::" + xmlHttpRequest.responseText);
	//console.log("Context Response ::" + xmlHttpRequest.responseXML);
	
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
			whereColumns["value"] = 8;
			
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
			whereColumns["value"] = 10;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
			
		}
		if(i == 4)
		{
			var result = name.split("[.]");
			
			createDynamicTable(result);
			
			var updateColumns = [];
			updateColumns.push({column:'tname',value:result[1]});
			
			var whereColumns = {};
			whereColumns["column"] = "id";
			whereColumns["value"] = 11;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
			
		}
   		
   		
   		if(i>=5)
   		{
   			//alert(name);
   			var result = name.split("[.]");
   			insertTableValues(result);
   			
   		}
   		i++;
   				//alert(name);
	});
	
	
	disgnostics_response_parser = disgnostics_response_parser + "<p>STOP DATA PARSING DEVICE:" + diagnosicsDate() + "</br></p>";
	
	checkContextActivitydatabase();
}



function checkContextActivitydatabase()
{  	
    db.transaction(getContextActivityOfflineValues, errorCB);
}

function getContextActivityOfflineValues(tx) { 
	//alert('2');   	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 8', [], showqueryContextActivitySuccess, errorCB);
}

function showqueryContextActivitySuccess(tx, results) 
{
  
	var len = results.rows.length;
	//alert(len);
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);
		
		//query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "PRICE-LIST-SELECTIONS"';
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "OVERVIEW-W"';
		//alert(query);
		selectTableValues(query,getScreenNameActivity);
		
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "OVERVIEW-W-HEADER-BLOCK"';
		selectTableValues(query,getFieldHintActivitySearchbar);
		
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "OVRVW-W-MAIN-BLOCK" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "OVRVW-W-MAIN-BLOCK"';
			console.log("showcontextquerySuccess ::" + query);
			selectTableValues(query,getFieldAttr);
			selectTableValues(query1,getFieldLabel);
			
			
			var query = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "OVRVW-W-MAIN-BLOCK" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "OVRVW-W-MAIN-BLOCK"';
			console.log("showcontextquerySuccess ::" + query);
			selectTableValues(query,getFieldAttr);
			selectTableValues(query1,getFieldLabel);
			
			
			
			
		}
		hideWaitMe();
		
		disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
		getActivityList();
		
	}
}

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
}



