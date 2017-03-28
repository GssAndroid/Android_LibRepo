var fieldAttr;
var fieldLabel;

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
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


function getFieldHintInventory(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$(".search").attr("placeholder", fieldLabelRow.VALUE);
	}
}

function getScreenNameInventory(results)
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

function checkContextInventorydatabase()
{  	
	
    db.transaction(getContextInventoryOfflineValues, errorCB);
}

function getContextInventoryOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 7', [], showqueryContextInventorySuccess, errorCB);
}

function showqueryContextInventorySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRICE-LIST-SEARCH-BLOCK"';
		selectTableValues(query,getFieldHintInventory);
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "PRICE-LIST-W"';
		selectTableValues(query,getScreenNameInventory);
		
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "PRICE-LIST-MAIN-W" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "PRICE-LIST-MAIN-W"';
			console.log("showcontextquerySuccess ::" + query);
			selectTableValues(query,getFieldAttr);
			selectTableValues(query1,getFieldLabel);
		}
	  
	
	}
}











var materials;
function priceDetails() {
	checkContextInventorydatabase();
	var url = document.URL;
	var value = url.split("=");
	materials = value[1].split(",");	
	//alert(id);
	if(checkConnection() != "none")
	{
	var items = new Array();
	items[0] = "EVENT[.]PRICE-LIST-FOR-EMPLOYEE-GET[.]VERSION[.]0";
	for(var i = 0; i < materials.length; i++)
	{
		if( i != (materials.length - 1))
			items[i+1] = "MD_T_MATNR[.]" + materials[i] ;
		if(i == (materials.length - 1))
		{
			if(materials[i] != 0)
				items[i+1] = "CVIS_CUSTOMER_T[.]" + materials[i] ;
				
		}
	}
	soapRequest(getPriceDetails,items);
	}
	else
	{
		checkdatabase();
	}
	window.localStorage.setItem("lastscreenid", "PRICELIST_DETAILS");
}



function getPriceDetails(xmlHttpRequest, status)
{
	console.log(xmlHttpRequest.responseText);
	console.log(xmlHttpRequest.responseXML);
	
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
			whereColumns["value"] = 4;
			
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
	checkdatabase();
	
	
}



function checkdatabase()
{  	
    db.transaction(getOfflineValues, errorCB);
}


function getOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 4', [], showquerySuccess, errorCB);
}

function showquerySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE ';// + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
		for(var i = 0; i < materials.length; i++)
		{
			if(i != (materials.length-1))
				query = query + PRICE_DET_INFO_COL_MATNR + "='" + materials[i] +"' OR " ;
			else if(i == (materials.length-1))
				query = query + PRICE_DET_INFO_COL_MATNR + "='" + materials[i] +"'" ;
		}
		console.log(query);
		selectTableValues(query,dynamicUI);
	}
  
 
}

function dynamicUI(results)
{
	var len = results.rows.length;
	add(results);
}


function add(values)
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
	
	$('#table1 thead').append(headquery); 
	
	
	
	

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
						cls = "right";
				}
			}
			
			query = query + "<td class='" + cls  +"'>" + rows[label] + "</td>";
			//alert(query);
		}
			
		query = query +  "</tr>";
		//var query = "<tr><td>" + rows[INVEN_DET_INFO_COL_MAKTX] +"</td><td>" + rows[INVEN_DET_INFO_COL_MEINS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LABST] +"</td><td>" + rows[INVEN_DET_INFO_COL_UMLME] +"</td><td>" + rows[INVEN_DET_INFO_COL_INSME] +"</td><td>" + rows[INVEN_DET_INFO_COL_MATNR] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS_TEXT] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGOBE] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGORT] +"</td></tr>";
		//" + rows[INVEN_DET_INFO_COL_MAKTX] +"</td><td>" + rows[INVEN_DET_INFO_COL_MEINS] +"</td><td>" + rows[INVEN_DET_INFO_COL_LABST] +"</td><td>" + rows[INVEN_DET_INFO_COL_UMLME] +"</td><td>" + rows[INVEN_DET_INFO_COL_INSME] +"</td><td>" + rows[INVEN_DET_INFO_COL_MATNR] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS_TEXT] +"</td><td>" + rows[INVEN_DET_INFO_COL_LGOBE] +"</td><td>" + rows[INVEN_DET_INFO_COL_WERKS] +"</td></tr>";
		//console.log(query);
		$('#table1 tbody').append(query); 
		
		
		//console.log('table value :' + x) ;
	}
	var $table =  $("#table1").tablesorter({
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
	/* $('td').each(function () {
		    if ($(this).html().match(/^\s*\d[\d,\.]*\s*$/)) {
		        $(this).css('text-align', 'right');
		        // TODO: something cool
		    }
		});
	*/
	
}
