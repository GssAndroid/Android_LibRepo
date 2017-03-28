var fieldAttr;
var fieldLabel;



function add(fieldAttr,fieldLabel,values)
{

	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	var valueLen = values.rows.length;
	
	var label;
	var type;
	var value;
	var describedValue;
	
	$( "#divDunamicUI" ).text( '' );
	
	for(var i = 0; i < fieldAttrLen; i++)
	{
		label = "";
		value = "";
		describedValue = "";
		
		var cloumnLabel;
		var cloumnValue;
		var cloumnDescribedValue;
		
		var fieldAttrRow = fieldAttr.rows.item(i);
		
		if(valueLen > 0)
		{
			var fieldValueRow = values.rows.item(0);
			cloumnValue = fieldAttrRow.NAME.split("-")[1];
		
			//alert(cloumnValue);
		
			value = fieldValueRow[cloumnValue];
			//alert(value);
		
			if(fieldAttrRow.VALUE == "DESCRIBED")
			{
				cloumnDescribedValue = fieldAttrRow.TRGTNAME.split("-")[1];
			
				//alert(cloumnDescribedValue);
			
				describedValue = fieldValueRow[cloumnDescribedValue];
				//alert(describedValue);
			}
		}
		
		
		for(var j = 0; j < fieldLabelLen; j++)
		{
			var fieldLabelRow = fieldLabel.rows.item(j);
			
			if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				label = fieldLabelRow.VALUE;
		}
		
		if(value || describedValue)
		{
			var ui = '<div class="ui-grid-a" style="max-width:500px;"><div class="ui-block-a" ><p>'+label +'</p></div><div class="ui-block-b"><input readonly class="disabletext" type="text" id="' + i + '" value="' + value + " " + describedValue +' "/></div></div>';
			console.log(ui);
			$( "#divDunamicUI" ).append( ui );
		}
	}
	
	 $("#page").trigger("create");
	 window.localStorage.setItem("lastscreenid", FUNCTION_ID_TABLE[2].id);
	
}

function getCustomerDetails(xmlHttpRequest, status)
{
	console.log(xmlHttpRequest.responseText);
	console.log(xmlHttpRequest.responseXML);
	showWaitMe("Compiling Data");
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START DATABSE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
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
			whereColumns["value"] = 2;
			
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
	checkdatabase();
}

var id;
function customerDetails() {
	
	getContextDetails();
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	var url = document.URL;
	var value = url.split("=");
	id = value[1];	
	//alert(id);
	if(checkConnection() != "none")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]CUSTOMER-CREDIT-INFORMATION-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('2'));
	items.push("SDCAS_T_CUSTLIST[.]" + id) ;
	soapRequest(getCustomerDetails,items);
	}
	else
	{
		checkdatabase();
	}
}
function checkdatabase()
{  	
    db.transaction(getOfflineValues, errorCB);
}

function getOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 2', [], showquerySuccess, errorCB);
}

function showquerySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE ' + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
		console.log(query);
		selectTableValues(query,dynamicUI);
		hideWaitMe();
	}
  
 
}


function dynamicUI(results)
{
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{
		
		var rows = results.rows.item(i);
		console.log("CREDIT INFO TABLE RESULT:"  + rows[CUS_SER_COL_NAME1]);
		   
	}
	
	add(fieldAttr,fieldLabel,results);
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
	runtimePopup(disgnostics_response_parser, null);
	
}

function getContextDetails()
{
	 db.transaction(getContextValues, errorCB);
}

function getContextValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 0', [], showcontextquerySuccess, errorCB);
}
    
function showcontextquerySuccess(tx, results) 
{
	checkContextdatabase();
	var len = results.rows.length;
	var tablename = results.rows.item(0).tname;
	//alert(tablename);
	if(tablename != "")
	{
		var query = 'SELECT * FROM ' + tablename + ' WHERE CNTXT4 = "FIELD-ATTR" ORDER BY SEQNR ASC';
		var query1 = 'SELECT * FROM ' + tablename + ' WHERE CNTXT4 = "FIELD-LABE"';
		console.log("showcontextquerySuccess ::" + query);
		selectTableValues(query,getFieldAttr);
		selectTableValues(query1,getFieldLabel);
	}
  
 
}


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
