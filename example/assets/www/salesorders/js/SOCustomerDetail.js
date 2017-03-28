var FIELD_ATTR_LIST = [];
var selectedMaterials = []; 

function checkMaterialContextSalesorderListdatabase()
{  	
    db.transaction(getContextMaterialListOfflineValues, errorCB);
}

function getContextMaterialListOfflineValues(tx) 
{ 	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextmaterialListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}

function showqueryContextmaterialListSuccess(tx, results) 
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
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "PRODUCT-SEARCH-RESULT"';
		selectTableValues(query, getScreenNameSalesorderList);	
		
		var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "PRODUCT-SEARCH-RESULT-MAIN" ORDER BY SEQNR ASC';
		selectTableValues(query,getFieldAttrDisplayValue);
		//var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "PRODUCT-SEARCH-RESULT-MAIN"';
		
		/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query, getFieldHintCustomerSearch);
				
		query1 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';*/		
		//selectTableValues(query1, getFieldLabel);
		MaterialSearch();
		/*selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);*/
	}		
	//hideWaitMe();
	//getProductList();
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
	}
}


function getMaterialDetails()
{  	
    db.transaction(getMaterialListOfflineValues, errorCB);
}

function getMaterialListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_material_search, [], showMaterialListquerySuccess, errorCB);
}




function showMaterialListquerySuccess(tx, results) 
{
	var len = results.rows.length;
	//alert(len);
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE upper(' + SO_MAT_INFO_COL_MAKTX +') LIKE "%' + id.toUpperCase() +'%" OR upper(' + SO_MAT_INFO_COL_MATNR + ') LIKE "%' + id.toUpperCase()  +'%"';
		console.log(query);
		selectTableValues(query,SomaterialList);
		hideWaitMe();
	}
}

function getSOSelectedMaterials()
{
	
	$("input:checkbox[name=materials]:checked").each(function()
	{
			    // add $(this).val() to your array
		selectedMaterials.push($(this).val());
	});
	
	
	//selectedMaterials.push(customerId);//passing customer id to second screen by adding it to array
	
	if(selectedMaterials.length != 1)
		window.location.href = 'salesorder_create.html?id='+selectedMaterials;
	else
		alert('Material Id is Empty');
	
	
}

/*function getCreateMaterialContext()
{  	
    db.transaction(getContextCreateMaterialValues, errorCB);
}

function getContextCreateMaterialValues(tx) 
{ 	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextCreateMaterilListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}

function showqueryContextCreateMaterilListSuccess(tx, results) 
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
		
		var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-ITEM-OVERVIEW-EDIT" ORDER BY SEQNR ASC';
		var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-ITEM-OVERVIEW-EDIT"';
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query, getFieldHintCustomerSearch);
				
		query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query2, getFieldHintProductSearch);
		
		selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);
		soCreatematerialList();
	}		
}
*/
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
	alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);		   
	}
	fieldLabel = results;	
}

function soCreatematerialList()
{	
	$('#pickmatcatbtn').hide();
	$('#table4').show();
	//var len = selectedMaterials.rows.length;	
	//console.log("result len: "+len);
	//hideWaitMe();
	//add(selectedMaterials);
	dynamicViewTable(selectedMaterials, fieldAttr, fieldLabel, "table4","MATNR");		
}

/*function add(values)
{
	var len = values.rows.length;

	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	
	var label;
	var headquery = "<tr>";
	
	$( "#divDunamicUI" ).text( '' );
	
	for(var i = 0; i < fieldAttrLen; i++)
	{
		var fieldAttrRow = fieldAttr.rows.item(i);
		for(var j = 0; j < fieldLabelLen; j++)
		{
			var fieldLabelRow = fieldLabel.rows.item(j);
			if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				label = fieldLabelRow.VALUE;
		}
		
		headquery = headquery + "<th>" + label +"</th>";
		
	}
	
	headquery = headquery + "</tr>";
	
	$('#table2 thead').append(headquery); 
	
	
	
	

	for(var i = 0; i < len; i++)
	{
		var query = "<tr>";
		var rows = values.rows.item(i);
		var cls = "";
		
		for(var k = 0; k < fieldAttrLen; k++)
		{
			var fieldAttrRow = fieldAttr.rows.item(k);
			cls = "";
			for(var j = 0; j < fieldLabelLen; j++)
			{
				var fieldLabelRow = fieldLabel.rows.item(j);
				if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				{
					label = fieldAttrRow.NAME.split("-")[1];
					//alert(fieldAttrRow.DATATYPE);
					if(fieldAttrRow.DATATYPE == "QUAN" || fieldAttrRow.DATATYPE == "CURR")
						cls = "right";//alligning header data to left and is defined in css class
				}
			}
			
			query = query + "<td class='" + cls  +"'>" + rows[label] + "</td>";
			//alert(query);
		}
			
		query = query +  "</tr>";
		//var query = "<tr><td>" + rows[INVEN_DET_INFO_COL_MAKTX] +"</td><td>" + rows[INVEN_DET_INFO_COL_MEINS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LABST] +"</td><td>" + rows[INVEN_DET_INFO_COL_UMLME] +"</td><td>" + rows[INVEN_DET_INFO_COL_INSME] +"</td><td>" + rows[INVEN_DET_INFO_COL_MATNR] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS_TEXT] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGOBE] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGORT] +"</td></tr>";
		//" + rows[INVEN_DET_INFO_COL_MAKTX] +"</td><td>" + rows[INVEN_DET_INFO_COL_MEINS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LABST] +"</td><td>" + rows[INVEN_DET_INFO_COL_UMLME] +"</td><td>" + rows[INVEN_DET_INFO_COL_INSME] +"</td><td>" + rows[INVEN_DET_INFO_COL_MATNR] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS_TEXT] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGOBE] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS] +"</td></tr>";
		//console.log(query);
		$('#table2 tbody').append(query); 
		
		
		//console.log('table value :' + x) ;
	}
	var $table =  $("#table2").tablesorter({
		widgets        : ['zebra', "filter"],
		
		 widgetOptions : {
 // if true overrides default find rows behaviours and if any column matches query it returns that row
 filter_anyMatch : true,
 filter_columnFilters: false
}
	});
	 $.tablesorter.filter.bindSearch( $table, $('.search') );
	 $("#page").trigger("create");
     $("#contentTable").niceScroll();
     //$("#contentTable").niceScroll({cursorcolor:"#00F"});
	 $('td').each(function () {
		    if ($(this).html().match(/^\s*\d[\d,\.]*\s*$/)) {
		        $(this).css('text-align', 'right');
		        // TODO: something cool
		    }
		});
	
	
}*/
/*function showMaterialListquerySuccess(tx, results) 
{
	hideWaitMe();
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;	
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		console.log(len);
		selectTableValues(query, materialList);
	}
}*/

function SomaterialList(results)
{
	$('#SolistMaterial').text('');
	var len = results.rows.length;
	alert(len);
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
		//addMaterial(rows[PRICE_MAT_INFO_COL_MATNR],rows[PRICE_MAT_INFO_COL_MAKTX],className);
		 addMaterial(rows,className); 
		//add(rows);
	}
		
	/*if(!customerId)
		$('#customerSearchInputs').hide();*/
	
	//$('#materailSearchInputs').hide();
	$('#SolistMaterial').show();
	$('#footer').show();
	$('#SolistMaterial').listview('refresh');
	 $("#page").trigger("create");
	 
	 
	 /*disgnostics_response_parser = disgnostics_response_parser + "<p>" + "STOP PROCESSING DEVICE " + diagnosicsDate() +" </br></p>";
	if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
	runtimePopup(disgnostics_response_parser, null);*/	
}

	
function addMaterial(rows,className) {	
	var value = rows[SO_MAT_INFO_COL_MATNR];
	
	var text = "";
	
	for(var i = 0 ; i < FIELD_ATTR_LIST.length; i++)
	{
		var fildattr = FIELD_ATTR_LIST[i];
		text = text + "<p>" +rows[fildattr] +"</p>";
	}
	
	//FIELD_ATTR_LIST 
    
	var element = '<li><a href="#" style="padding-top: 0px;padding-bottom: 0px;padding-right: 0px;padding-left: 0px;"><label class="'+ className +'" style="border-top-width: 0px;margin-top: 0px;border-bottom-width: 0px;margin-bottom: 0px;border-left-width: 0px;border-right-width: 0px;" data-corners="false"><fieldset data-role="controlgroup" data-iconpos="right"><input type="checkbox" name="materials"';
    element = element +  'value="' + value + '" />';
    element = element + ' <label for="checkbox-2b" style="border-top-width: 0px;margin-top: 0px;border-bottom-width: 0px;margin-bottom: 0px;border-left-width: 0px;border-right-width: 0px;"><label  style="padding:10px 0px 0px 10px;">';
    element = element + text;
    element = element + ' </label></label></fieldset></label></a></li>';
    
    console.log(element);
 
    $("#SolistMaterial").append(element);
 
}

var id;
function MaterialSearch()
{
	var url = document.URL;
	var value = url.split("=");
	id = value[1];	
	//document.getElementById("listMaterial").innerHTML = "";
	
	/*dropDynamicTable(window.localStorage.getItem(so_material_search));
		window.localStorage.removeItem(so_material_search);*/
		
	if(checkConnection() != "none")
	{
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]PRODUCT-SEARCH-BROADER-RESULT-VIEW-A[.]VERSION[.]0");
	items.push("ZGSEVDST_MTRLSRCH01[.]"+id);
	soapRequest(getMaterialList,items);
	}
	else
	{
		checkdatabase();
	}
}

function getMaterialList(xmlHttpRequest, status)
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
					whereColumns["value"] = so_material_search;
					
					updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);
					
				}
				if(i>2)
				{
					var result = name.split("[.]");
				
					insertTableValues(result)
					//	add("button",result[2],result[1],className);
				}
				i++;
				//alert(name);
		});
		//checkMaterialdatabase();
		getMaterialDetails();
}



/*function getFieldHintProductSearch(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#txtProductSearch").attr("placeholder", fieldLabelRow.VALUE);
	}
}*/






