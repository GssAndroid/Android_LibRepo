function contextDetails()
{
	
	if(checkConnection() != "none")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]CUSTOMER-CREDIT-INFORMATION-CNTX[.]VERSION[.]0"+responseTypeDecider(customer_cnxt_dtls));
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
	
	
	
	
}







function getContextDeails(xmlHttpRequest, status)
{
	console.log("Context Response ::" + xmlHttpRequest.responseText);
	console.log("Context Response ::" + xmlHttpRequest.responseXML);
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
			whereColumns["value"] = customer_cnxt_dtls;
			
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
	
	checkContextdatabase();
	
	hideWaitMe();
	
	
}



function checkContextdatabase()
{  	
	
    db.transaction(getContextOfflineValues, errorCB);
}

function getContextOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+customer_cnxt_dtls, [], showqueryContextSuccess, errorCB);
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

