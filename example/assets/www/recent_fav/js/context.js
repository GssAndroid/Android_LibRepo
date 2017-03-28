var FIELD_ATTR_LIST = [];
var CUSTOMER_LABEL = "CUSTOM";

function contextDetailsPriceList()
{
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(checkConnection() != "none" && window.localStorage.getItem("lastscreenid") != "PRICELIST_DETAILS")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]PRICE-LIST-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('7'));
	soapRequest(getContextDeailsPriceList,items);
	}
	else
	{//checkContextInventorydatabase
		checkContextPriceListdatabase();
	}
	
	window.localStorage.setItem("lastscreenid", "PRICELIST_INDEX");
}


function contextCustomerLabel(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		CUSTOMER_LABEL = fieldLabelRow.VALUE;
		//alert(fieldLabelRow.VALUE);
	}
}


function getScreenNamePriceList(results)
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



function getFieldHintPriceListCustomers(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$("#txtCustomerSearch").attr("placeholder", fieldLabelRow.VALUE);
		//alert(fieldLabelRow.VALUE);
	}
}


function getFieldHintPriceList(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$("#txtSearch").attr("placeholder", fieldLabelRow.VALUE);
		//alert(fieldLabelRow.VALUE);
	}
}

function getActionBarPriceList(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$("#actionBarButton span span").text(fieldLabelRow.VALUE);
        $("#actionBarButton").button("refresh");

		//alert(fieldLabelRow.VALUE);
	}
}

function getFieldAttrDisplayValue(results)
{
	var len = results.rows.length;
	
	//alert(len);
	FIELD_ATTR_LIST = [];
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.NAME);
		var attrvalue = fieldLabelRow.NAME.split("-");
		FIELD_ATTR_LIST[i] = attrvalue[1];
		//alert(attrvalue[1]);
	}
}







function getContextDeailsPriceList(xmlHttpRequest, status)
{
	//console.log("Context Response ::" + xmlHttpRequest.responseText);
	//console.log("Context Response ::" + xmlHttpRequest.responseXML);
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	showWaitMe("Compiling Data");
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
			whereColumns["value"] = 7;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
			
		}
   		
   		
   		if(i>=3)
   		{
   			//alert(name);
   			var result = name.split("[.]");
   			insertTableValues(result);
   			
   		}
   		i++;
   				//alert(name);
	});
	
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	checkContextPriceListdatabase();
}



function checkContextPriceListdatabase()
{  	
	//alert('1');
    db.transaction(getContextPriceListOfflineValues, errorCB);
}

function getContextPriceListOfflineValues(tx) { 
	//alert('2');   	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 7', [], showqueryContextPriceListSuccess, errorCB);
}

function showqueryContextPriceListSuccess(tx, results) 
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
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH-BLOCK"';
		selectTableValues(query,getFieldHintPriceListCustomers);
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "MATERIAL-SEARCH-BLOCK"';
		selectTableValues(query,getFieldHintPriceList);
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "PRICE-LIST-SELECTIONS"';
		//alert(query);
		selectTableValues(query,getScreenNamePriceList);
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "MATERIAL-LIST-ACTION-BAR"';
		//alert(query);
		selectTableValues(query,getActionBarPriceList);
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "MATERIAL-LIST-MAIN" ORDER BY SEQNR ASC';
		//alert(query);
		selectTableValues(query,getFieldAttrDisplayValue);
		
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "CUSTOMER-SEARCH-RESULT-BLOCK"';
		//alert(query);
		selectTableValues(query,contextCustomerLabel);
		hideWaitMe();
		
		disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
		if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
			runtimePopup(disgnostics_response_parser, null);
		
	}
}

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
	
}

