function add(type,value,id,className) {
	//Create an input type dynamically.
   /* var element = document.createElement("input");
 
    //Assign different attributes to the element.
    element.setAttribute("value", id + " " + value);
    element.setAttribute("id", id);
    element.className = className;
    element.onclick = function(){
    
    window.location.href = 'details.html?id='+this.id;
    return false;
    };*/
    
    
    
    
    var element =  '<li class="' + className + '" onclick="gotoDetails(' + id + ');" > ' + value+ " : " +id + '</li>';
 
    $("#dialog-form").append(element);
 
}



function gotoDetails(id)
{
	window.history.back();
	window.location.href = 'details.html?id='+id;

}


function getCustomersList(xmlHttpRequest, status)
{
		//alert(status);
		//alert(xmlHttpRequest.responseXML);
	console.log(xmlHttpRequest.responseText);
	console.log(xmlHttpRequest.responseXML);
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
					whereColumns["value"] = 1;
					
					updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
					
				}
				if(i>=3)
				{
					var result = name.split("[.]");
				
					insertTableValues(result)
					//	add("button",result[2],result[1],className);
				}
				i++;
				//alert(name);
		});
		checkdatabase();
		
}

function customerSearch()
{
	document.getElementById("dialog-form").innerHTML = "";
	dropDynamicTable(window.localStorage.getItem("1"));
		window.localStorage.removeItem("1");
	if(checkConnection() != "none")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0");
	items.push("ZGSEVDST_CSTMRSRCH01[.]" + $("#txtSearch").val());
	soapRequest(getCustomersList,items);
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
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 1', [], showquerySuccess, errorCB);
}

function showquerySuccess(tx, results) 
{
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE ' + CUS_SER_COL_NAME1 +' LIKE "%' + $("#txtSearch").val() +'%"';
		console.log(query);
		selectTableValues(query,customerDialog);
		hideWaitMe();
	}
}

function customerDialog(results)
{
	var len = results.rows.length;
	
	var header = '<li data-role="divider" class="listheader bluebg" data-theme="b">Select Customer</li>';
	//document.getElementById("").appendChild(header);
	
	$("#dialog-form").append(header);
	
	
	if(len == 1)
	{
		var rows = results.rows.item(0);
		gotoDetails(rows[CUS_SER_COL_KUNNR]);
		 return false;
	}
	
	for (var i=0; i<len; i++)
	{
		var className;
		
		if(i % 2 == 0)
		{
			className = "bgcolor";
		}
		else
		{
			className = "darkbgcolor";
		}
		var rows = results.rows.item(i);
		add("button",rows[CUS_SER_COL_NAME1],rows[CUS_SER_COL_KUNNR],className);
		   
	}
	$('#dialog-form').listview('refresh');
	 var nestedLiPage = $(".ui-page-active");
     $('#popupMenu').clone().appendTo(nestedLiPage).popup().popup('open');
}




function contextDetails()
{
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "START PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(checkConnection() != "none" && window.localStorage.getItem("lastscreenid") != FUNCTION_ID_TABLE[2].id)
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]CUSTOMER-CREDIT-INFORMATION-CNTX[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+responseTypeDecider('0'));
	soapRequest(getContextDeails,items);
	}
	else
	{
		checkContextdatabase();
	}
}




function getScreenName(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$("#screenTitle").text(fieldLabelRow.VALUE);
	}
	
	window.localStorage.setItem("lastscreenid", FUNCTION_ID_TABLE[1].id);
	
}







function getContextDeails(xmlHttpRequest, status)
{
	console.log("Context Response ::" + xmlHttpRequest.responseText);
	console.log("Context Response ::" + xmlHttpRequest.responseXML);
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
			whereColumns["value"] = 0;
			
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
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP DATABASE PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	checkContextdatabase();
	hideWaitMe();
	disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
		runtimePopup(disgnostics_response_parser, null);
}



function checkContextdatabase()
{  	
	
    db.transaction(getContextOfflineValues, errorCB);
}

function getContextOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 0', [], showqueryContextSuccess, errorCB);
}

function showqueryContextSuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "DETAIL"';
		//alert(query);
		selectTableValues(query,getScreenName);
		
	}
}

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
}


