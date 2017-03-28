var FIELD_ATTR_LIST = [];
var fieldAttr;
var fieldLabel;
var headerflag=false;
var itemID;
var click =false;

function getSummaryFieldAttr(results)
{
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("ATTR RESULT:"  + rows.NAME);		   
	}
	fieldAttr = results;	
}

function getSummaryFieldLabel(results)
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

function getSummaryScreenNameSalesorderList(results)
{
	var len = results.rows.length;	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#screenTitle").text(fieldLabelRow.VALUE);
	}
}

function checkSummaryContextSalesorderListdatabase()
{  	
    db.transaction(getContextSummaryListOfflineValues, errorCB);
}

function getContextSummaryListOfflineValues(tx) 
{ 	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextsummaryListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}

var id;
function showqueryContextsummaryListSuccess(tx, results) 
{  
	//GET THE ID FROM OVERVIEW SCREEN
	var url = document.URL;
	var value = url.split("=");
	id = value[1];
	//******//
	var len = results.rows.length;
	console.log(len);
	var tname = results.rows.item(0).tname;
	console.log(tname);
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		//alert('tname ' + tname);
		selectTableValues(query,checkValues);
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-DETAIL-W"';
		selectTableValues(query, getSummaryScreenNameSalesorderList);	
		
		var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-SUMMARY-BLOCK" ORDER BY SEQNR ASC';
		var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-SUMMARY-BLOCK"';
		
		/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query, getFieldHintCustomerSearch);*/
				
		/*query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
		selectTableValues(query2, getFieldHintProductSearch);*/
		
		
		selectTableValues(query, getSummaryFieldAttr);
		selectTableValues(query1, getSummaryFieldLabel);
		getSummaryDetails();
		/*selectTableValues(query, getFieldAttr);
		selectTableValues(query1, getFieldLabel);*/
	}
 }
	
	function getSummaryDetails()
	{  	
	    db.transaction(getsummaryOfflineValues, errorCB);
	}

	function getsummaryOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_details, [], showsummaryListquerySuccess, errorCB);
	}

	function showsummaryListquerySuccess(tx, results) 
	{
		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			console.log(query);
			selectTableValues(query,dynamicUI);
			hideWaitMe();
		}
		/*if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname ;
			console.log(query);
			console.log(len);
			selectTableValues(query, dynamicUI);
		}*/
		
	}
	
	function dynamicUI(results)
	{
		var len = results.rows.length;
		/*for (var i=0; i<len; i++)
		{
			
			var rows = results.rows.item(i);
			console.log("CREDIT INFO TABLE RESULT:"  + rows[SO_CUS_SER_COL_KUNNR]);
			   
		}	*/			
		dynamicsummaryDetailView(fieldAttr,fieldLabel,results,"CustomerParentDiv");								
		//window.localStorage.setItem("lastscreenid", FUNCTION_ID_TABLE[3].id);		
	}

	function  dynamicsummaryDetailView(fieldAttr,fieldLabel,values,viewId)
	{

		var fieldAttrLen = fieldAttr.rows.length;
		var fieldLabelLen = fieldLabel.rows.length;
		var valueLen = values.rows.length;
		
		var label;
		var type;
		var value;
		var describedValue;
		
		$( "#"+viewId ).text( '' );
		
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
				
				/*if(cloumnValue == "CHANGE"){
					//alert(cloumnValue);				
					value = "<img src='../img/back1.png'/>";																				
				}
				if(fieldAttrRow.VALUE == "EDITABLE"){
					//alert(cloumnValue);								
					value = "<input type= 'search' name= 'text' id= 'ponumb' placeholder= 'PO number'/>";																				
				}*/
				if(fieldAttrRow.DATATYPE == "DATS"){
					var today = new Date();
					var dd = today.getDate();
					var mm = today.getMonth()+1; //January is 0!
					var yyyy = today.getFullYear();

					if(dd<10) {
					    dd='0'+dd
					} 

					if(mm<10) {
					    mm='0'+mm
					} 

					today = yyyy+'-'+mm+'-'+dd;
					//alert(today);				
					today = converToSystemDateFormat(today);
					//alert(today);
					value = elementDatePicker("TODATEPICKER",today);							
			     }		
				
			}
					
			for(var j = 0; j < fieldLabelLen; j++)
			{
				 fieldLabelRow = fieldLabel.rows.item(j);			
				if(fieldAttrRow.NAME == fieldLabelRow.NAME)
					label = fieldLabelRow.VALUE;
				//alert(fieldLabelRow.NAME);			
			}		
			/*if(fieldLabelRow.NAME == "ZGSEVAST_SDCRTN20-BSTDK" || fieldLabelRow.NAME == "ZGSEVAST_SDCRTN20-BSTKD"){
				var uiele = ""
			}*/
			//var ui = '<div class="ui-block-a" style="width:30%" >'+'<font color="blue" style="bold">'+label +'</font></div><div class="ui-block-a" style="width:10%">'+ value +'<span>  </span>'+ describedValue +'</div></div>';
			var ui = '<div class="ui-grid-a" style="max-width:300px;"><div class="ui-block-a" ><p>'+label +'</p></div><div class="ui-block-b"><p style="font-weight: normal !important; color: black !important;">'+ value + " " + describedValue +'</p></div></div>';
			console.log(ui);	
			$( "#"+viewId ).append( ui );											
		}
		//checkMaterialContextdatabase();
		 $("#page").trigger("create");	 
		$(function () {
		    var curr = new Date().getFullYear();
		    //alert(dateFormat);
		    //alert(replaceAll('/','',dateFormat));
		    var opt = {
		        'date': {
		            preset: 'date',
		            dateOrder: replaceAll('/','',dateFormat),
		            dateFormat: dateFormat
		        },
		        'datetime': {
		            preset: 'datetime'
		        },
		        'time': {
		            preset: 'time',
		            timeFormat: 'HH:ii:ss',
		            timeWheels: 'HHiiss'
		        },
		        'credit': {
		            preset: 'date',
		            dateOrder: 'mmyy',
		            dateFormat: 'mm/yy',
		            startYear: curr,
		            endYear: curr + 10,
		            width: 100
		        },
		        'btn': {
		            preset: 'date',
		            showOnFocus: false
		        },
		        'inline': {
		            preset: 'date',
		            display: 'inline'
		        }
		    }

		    $(".datepicker" ).each(function( index ) {
		        $(this).val($(this).val()).scroller('destroy').scroller($.extend(opt['date'], { theme: 'ios', mode: 'scroller' }));
		     });
		     
		     $(".timepicker" ).each(function( index ) {
		        $(this).val($(this).val()).scroller('destroy').scroller($.extend(opt['time'], { theme: 'ios', mode: 'scroller' }));
		     });
		/*$('.datepicker').val($(this).val()).scroller('destroy').scroller($.extend(opt['date'], { theme: 'ios', mode: 'scroller' }));
		$('.timepicker').val($(this).val()).scroller('destroy').scroller($.extend(opt['time'], { theme: 'ios', mode: 'scroller' }));	  	  */
		}); 
		getHeaderData();
	}
	function checkValues(results)
	{
		var len = results.rows.length;
		//alert('len ' + len);
		console.log("Context Length ::" + len);
	}
	
	//GETTING HEADER BLOCK CONTEXT AND VALUES
	
	function getHeaderData()
	{
		 db.transaction(getHeaderValues, errorCB);
	}

	function getHeaderValues(tx) 
	{ 	
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryHeaderSuccess, errorCB);
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
	}
	
	function showqueryHeaderSuccess(tx, results) 
	{  
		var len = results.rows.length;
		console.log(len);
		var tname = results.rows.item(0).tname;
		console.log(tname);
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname ;
			console.log(query);
			//alert('tname ' + tname);
			selectTableValues(query,checkValues);		
			
			var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-HEADER-DATA-BLOCK" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-HEADER-DATA-BLOCK"';
			
			/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query, getFieldHintCustomerSearch);*/
					
			/*query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query2, getFieldHintProductSearch);*/
			
			
			selectTableValues(query, getSummaryFieldAttr);
			selectTableValues(query1, getSummaryFieldLabel);			
			getHeadValueDetails();
			/*selectTableValues(query, getFieldAttr);
			selectTableValues(query1, getFieldLabel);*/
		}
	 }
	
	//HEADER VALUE DATA//*******//
	function getHeadValueDetails()
	{  	
	    db.transaction(getHeadOfflineValues, errorCB);
	}

	function getHeadOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_details, [], showHeadListquerySuccess, errorCB);
	}

	function showHeadListquerySuccess(tx, results) 
	{
		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			console.log(query);
			selectTableValues(query,dynamicHeadUI);	
			hideWaitMe();
		}
		/*if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname ;
			console.log(query);
			console.log(len);
			selectTableValues(query, dynamicUI);
		}*/
		
	}

	
	function dynamicHeadUI(results)
	{
		var len = results.rows.length;
		/*for (var i=0; i<len; i++)
		{
			
			var rows = results.rows.item(i);
			console.log("CREDIT INFO TABLE RESULT:"  + rows[VBELN]);
			   
		}	*/		
			dynamicDetailView(fieldAttr,fieldLabel,results,"HeadertDiv");	
			getItemBlockContext();
		//window.localStorage.setItem("lastscreenid", FUNCTION_ID_TABLE[3].id);		
	}
//***********************//	
	
//GET ITEM BLOCK CONTEXT AND DETAILS//*******************//
	function getItemBlockContext()
	{  	
	    db.transaction(getContextitemListOfflineValues, errorCB);
	}

	function getContextitemListOfflineValues(tx) 
	{ 	
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextItemListSuccess, errorCB);
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
	}

	var id;
	function showqueryContextItemListSuccess(tx, results) 
	{  
		//GET THE ID FROM OVERVIEW SCREEN
		var url = document.URL;
		var value = url.split("=");
		id = value[1];
		//******//
		var len = results.rows.length;
		console.log(len);
		var tname = results.rows.item(0).tname;
		console.log(tname);
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname ;
			console.log(query);
			//alert('tname ' + tname);
			selectTableValues(query,checkValues);
			/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-CREATE-W"';
			selectTableValues(query, getSummaryScreenNameSalesorderList);	*/
			
			var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-ITEM-DATA-BLOCK" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-ITEM-DATA-BLOCK"';
			
			/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query, getFieldHintCustomerSearch);*/
					
			/*query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query2, getFieldHintProductSearch);*/
			
			
			selectTableValues(query, getSummaryFieldAttr);
			selectTableValues(query1, getSummaryFieldLabel);
			getItemBlockDetails();
			/*selectTableValues(query, getFieldAttr);
			selectTableValues(query1, getFieldLabel);*/
		}
	 }
	//ENDING ITEM BLOCK CONTEXT//*******//
	
//GETTING VALUES FOR ITEM BLOCK//**//	
	function getItemBlockDetails()
	{  	
	    db.transaction(getitemOfflineValues, errorCB);
	}

	function getitemOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_item_details, [], showItemListquerySuccess, errorCB);
	}

	function showItemListquerySuccess(tx, results) 
	{
		alert(id);
		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		//alert(tname);
		if(!click){
			itemID = "10";
		}
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"'+ ' AND ' + POSNR +' = "' + itemID +'"';
			console.log(query);	
			selectTableValues(query,dynamicItemUI);	
			getContextTableContents();
			hideWaitMe();
		}		
	}
	
	function dynamicItemUI(results)
	{
		var len = results.rows.length;	
		dynamicDetailView(fieldAttr,fieldLabel,results,"ItemsDiv");			
	}
	
	//GET CONTEXT TABLE CONTEXT FOR ITEM DETAIL SCREEN
	
	function getContextTableContents()
	{  	
	    db.transaction(getContextTableValues, errorCB);
	}

	function getContextTableValues(tx) 
	{ 	
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryTableListSuccess, errorCB);
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
	}

	/*var id;*/
	function showqueryTableListSuccess(tx, results) 
	{  
		//GET THE ID FROM OVERVIEW SCREEN
	/*	var url = document.URL;
		var value = url.split("=");
		id = value[1];*/
		//******//
		var len = results.rows.length;
		console.log(len);
		var tname = results.rows.item(0).tname;
		console.log(tname);
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname ;
			console.log(query);
			//alert('tname ' + tname);
			selectTableValues(query,checkValues);
			/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-CREATE-W"';
			selectTableValues(query, getSummaryScreenNameSalesorderList);	*/
			
			var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-ITEM-OVERVIEW" ORDER BY SEQNR ASC';
			var query1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-ITEM-OVERVIEW"';
			
			/*query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "CUSTOMER-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query, getFieldHintCustomerSearch);*/
					
			/*query2 = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-HINT" AND CNTXT2 = "PRODUCT-SEARCH" AND NAME = "SEARCHBAR"';
			selectTableValues(query2, getFieldHintProductSearch);*/
			
			
			selectTableValues(query, getSummaryFieldAttr);
			selectTableValues(query1, getSummaryFieldLabel);
			var localQdata = localStorage["LocalDataFlag"];
			alert(localQdata);
			if(localQdata)
				getlocalItemDetails();
			else
				getItemtableDetails();
			
			/*selectTableValues(query, getFieldAttr);
			selectTableValues(query1, getFieldLabel);*/
		}
	 }
	
	//GET ITEM DETAIL SCREEN TABLE CONTENTS//****************//
	
	function getlocalItemDetails(){
		db.transaction(getQitemtableOfflineValues, errorCB);
	}//getlocalItemDetails
	
	function getQitemtableOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_localprice_details, [], showQItemTablequerySuccess, errorCB);
	}
	
	function showQItemTablequerySuccess(tx, results) 
	{
		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		alert(tname);
		
		if(tname != "")
		{
			//var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			console.log(query);	
			selectTableValues(query,ItemTabledynamicUI);	
				
			hideWaitMe();
		}		
	}
	
	function getItemtableDetails()
	{  	
	    db.transaction(getitemtableOfflineValues, errorCB);
	}

	function getitemtableOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_item_details, [], showItemTablequerySuccess, errorCB);
	}

	function showItemTablequerySuccess(tx, results) 
	{
		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		//alert(tname);
		
		if(tname != "")
		{
			//var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			console.log(query);	
			selectTableValues(query,ItemTabledynamicUI);	
				
			hideWaitMe();
		}		
	}
	
	/*function getTableContents(){	

		var len = results.rows.length;
		var tname = results.rows.item(0).tname;	
		//alert(tname);
		if(tname != "")
		{
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
			console.log(query);	
			selectTableValues(query,mattdynamicUI);	
				
			hideWaitMe();
		}						
}*/
	function ItemTabledynamicUI(results)
	{
		$('#table5').show();
		dynamicItemViewTable(results, fieldAttr, fieldLabel, "table5","VBELN");
		$('#table5 tbody tr td').click(function() {
			click=true;
	        //alert($(this).attr('id'));			
			itemID = $(this).text();
	        //var id = $(this).attr('id');
	      alert(itemID);
	      getItemBlockContext();
	        /*//window.localStorage.setItem("gallerypath", "SalesPro/Activities/"+id);
	        window.location.href = 'mainframe.html?id='+id;
	        checkbox-product*/
	   });
	}//ItemTabledynamicUI
	
	function dynamicItemViewTable(results,fieldAttr,fieldLabel,viewId,objid){
		//if(click){
			 $('#'+viewId +' thead').html("");
			 $('#'+viewId +' tbody').html("");
		//}
		var len = results.rows.length;
		alert(len);
			var fieldAttrLen = fieldAttr.rows.length;
			var fieldLabelLen = fieldLabel.rows.length;
			
			var label;
			var headquery = "<tr>";
			
			
			
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
			
			$('#table5 thead').append(headquery); 
			for(var i = 0; i < len; i++)
			{
				var query = "<tr>";
				var rows = results.rows.item(i);
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
							//alert(label);
							if(fieldAttrRow.DATATYPE == "QUAN" || fieldAttrRow.DATATYPE == "CURR")
								cls = "right";
						}
					}
					
					query = query + "<td class='" + cls  +"'>" + rows[label] + "</td>";
					//query =query + "<td class='" +"<span id='" + cls + "'>"+rows[label]+"</td>"+ "</span>";
					//alert(query);
				}
					
				query = query +  "</tr>";
				
				$('#table5 tbody').append(query); 			
			}
			console.log($('#table5 tbody').html());			
			//PERFORM ONCLIK FUNCTIONALITY FOR TABLE CONTENTS			
	}//
	