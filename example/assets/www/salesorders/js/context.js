var FIELD_ATTR_LIST = [];
var productCategoryRows;
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
	//alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);		   
	}
	fieldLabel = results;	
}

function ShowCreateScreenFromQP(){
	//var nullstr= "";
	 var qpflagStr = localStorage["QPFLAG"];
	 if(qpflagStr){
		 //localStorage["QPFLAG"] = nullstr;
		var socustIdStr= localStorage["REFID"];
		 window.location.href = 'salesorder_create.html';	
	 }else{
		 contextDetailsSalesOrderList();
	 }
}//ShowCreateScreenFromQP

function contextDetailsSalesOrderList()
{
	if(checkConnection() != "none" && window.localStorage.getItem("LastScreenId") != PROD_DETAILS)
	{
		var resptype = responseTypeDecider(salesorders_cnxt_dtls);
		var items = new Array();
		var imeino = localStorage.getItem("imeino");		
		var productcontextflag = window.localStorage.getItem(SALESORDERS_CNTXT_LIST_PREF);
		items.push("DEVICE-ID:"+imeino+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
		items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
		if(resptype=="REFRESHED")
		items.push("EVENT[.]SALES-ORDER-CONTEXT[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+resptype);
		else
			items.push("EVENT[.]SALES-ORDER-CONTEXT[.]VERSION[.]0");
		
		soapRequest(getContextSalesorderList,items);
	}
	else
	{
		checkContextProductListdatabase();
	}	
}

function getScreenNameSalesorderList(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#screenTitle").text(fieldLabelRow.VALUE);
	}
}

/*function getFieldLabelProductCategory(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#createBtn .catTitle").text(fieldLabelRow.VALUE);
	}
}*/

function getFieldHintProductSearch(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#txtProductSearch").attr("placeholder", fieldLabelRow.VALUE);
	}
}

function getActionBarProductList(results)
{
	var len = results.rows.length;
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#actionBarButton span span").text(fieldLabelRow.VALUE);
        $("#actionBarButton").button("refresh");
	}
}

function getFieldAttrDisplayValue(results)
{
	var len = results.rows.length;
	FIELD_ATTR_LIST = [];
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		var attrvalue = fieldLabelRow.NAME.split("-");
		FIELD_ATTR_LIST[i] = attrvalue[1];
	}
}

function getContextSalesorderList(xmlHttpRequest, status)
{
	console.log("Soap Response ::" + xmlHttpRequest.responseText);
	//$.mobile.showPageLoadingMsg("b", "Compiling Data....");
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
			whereColumns["value"] = salesorders_cnxt_dtls;			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);			
		} 		   		
				
   		if(i>=3)
   		{
   			var result = name.split("[.]");
   			insertTableValues(result);
   		}
   		i++;
	});
	//$.mobile.loading("hide");
	checkContextProductListdatabase();
}

function checkContextProductListdatabase()
{  	
    db.transaction(getContextProductListOfflineValues, errorCB);
}

function getContextProductListOfflineValues(tx) 
{ 
	//alert('SELECT * FROM FUNCTIONMASTER WHERE id = ' + product_cnxt_dtls);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = ' + salesorders_cnxt_dtls, [], showqueryContextProductListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}

function showqueryContextProductListSuccess(tx, results) 
{  
	var len = results.rows.length;
	console.log(len);
	var tname = results.rows.item(0).tname;
	console.log(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		//alert('tname ' + tname);
		selectTableValues(query,checkValues);		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-OVERVIEW-W"';
		selectTableValues(query, getScreenNameSalesorderList);		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "SO-OVERVIEW-TOP-PART" AND NAME = "SEARCHBAR"';
		selectTableValues(query, getFieldHintProductSearch);
		
		var query = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-OVERVIEW-W-MAIN-PART" ORDER BY SEQNR ASC';
		var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-OVERVIEW-W-MAIN-PART"';
		console.log("showcontextquerySuccess ::" + query);
		selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);
	}		
	hideWaitMe();
	getProductList();
}

/*function showProductCategoryListSuccess(tx, results) 
{
	var len = results.rows.length;	
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		//var query = 'SELECT * FROM ' + tname +' LIMIT 10';
		var query = 'SELECT * FROM ' + tname + ' order by '+VBELN;
		console.log("showProductCategoryListSuccess "+query);
		//alert("showProductCategoryListSuccess "+query);
		selectTableValues(query, productCategoryList);
	}
}

function productCategoryList(results)
{
	var len = results.rows.length;	
	console.log("showProductCategoryListSuccess results "+results);
	console.log("showProductCategoryListSuccess len "+len);
	productCategoryRows = results;
}

function getCategoryList()
{
	var len = productCategoryRows.rows.length;
	var result = [];	
	for(var i = 0; i < len; i++)
	{
		var catObj = productCategoryRows.rows.item(i);	
		var strSep = "";
		if(catObj.NAME1A != null && catObj.NAME1A != ""){
			strSep =  catObj.NAME1A+"("+catObj.VBELN+")";
		}else{
			strSep = catObj.VBELN;
		}		
		console.log("strSep ::" + strSep);
		//result.push(strSep);
		result.push({category:strSep,categoryid:catObj.VBELN});
	}	
	return result;
}*/

function checkValues(results)
{
	var len = results.rows.length;
	//alert('len ' + len);
	console.log("Context Length ::" + len);
}