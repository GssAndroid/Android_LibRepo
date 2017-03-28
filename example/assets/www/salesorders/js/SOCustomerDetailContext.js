var FIELD_ATTR_LIST = [];
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

/*function gotoDetails(id)
{
	
	//window.history.back();
	window.location.href = 'materiallistscreen.html?id='+id;

}*/

function getFieldLabel(results)
{
	var len = results.rows.length;
	alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);		   
	}
	fieldLabel = results;	
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


/*function getFieldHintCustomerSearch(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#txtCustomerSearch").attr("placeholder", fieldLabelRow.VALUE);
	}
}*/

/*function getActionBarProductList(results)
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
*/

function checkCustomerContextSalesorderListdatabase()
{  	
    db.transaction(getContextProductListOfflineValues, errorCB);
}

function getContextProductListOfflineValues(tx) 
{ 	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextcustomerListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}

function showqueryContextcustomerListSuccess(tx, results) 
{  
	var len = results.rows.length;
	console.log(len);
	var tname = results.rows.item(0).tname;
	console.log(tname);
	alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		alert('tname ' + tname);
		selectTableValues(query,checkValues);
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-CREATE-W"';
		selectTableValues(query, getScreenNameSalesorderList);	
		
		var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "CUSTOMER-SEARCH-RESULT" ORDER BY SEQNR ASC';
		var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "CUSTOMER-SEARCH-RESULT"';
		
		/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query, getFieldHintCustomerSearch);*/
				
		query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query2, getFieldHintProductSearch);
		
		selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);
		getCustomerDetails();
		/*selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);*/
	}		
	//hideWaitMe();
	//getProductList();
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