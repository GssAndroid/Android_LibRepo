var fieldLabelRow;
var FIELD_ATTR_LIST = [];
var selectedMaterials = []; 
var addedMaterials = []; 
var addgtPriceReq= [];
var addgtSOReq= [];
var finaladdedMaterials = [];
var copiedMaterials = []; 
var MatIdList = [];
var MatIdDescList = [];
var UnitIdList = [];
var PriceIdList = [];
var QtyIdList = [];
var ItemIdList = [];
var DeleteProdIdList = [];
var GetpriceList = [];
var valuestrlist=[];
//var GetpriceList;
var GetpriceUnitList = [];
var GetPriceMattList = [];
var LocalSonumbList = [];
var getpriceTableContents;
var addeditemresults;
var copieditemresults;
var finalitemresults;


var productCategoryRows;
/*var fieldAttr;
var fieldLabel;*/
var materialtname;
var customernoStr;
var customernameStr;
var matnoStr;
var quantity;
var getpriceflag;
var finalprice;
var finalunit;
var indprice;
var indunit;
var customerIdStr;
var changeCustmrflag;
var addedmattflag;
var copysoId;
var id;
var copyflag;
var ponumbStr;
var popultetableFlag;
var qpsoid;
var qpflag;
var quantyStr;
var FULLFILLBY_DATE;
var unnitStr;
var itemIdStr;
var priceUnitIdStr;
var matdescStr;
var errordescStr;
var sonumbstr;
getpriceflag = "";
var nullstr ="";
var sendtolocaldbflag;
var flagtrue = true;
var flagfalse = false;


function add(type,value,id,className) {	
    var element =  '<li class="' + className + '" onclick="gotoCustDetails(' + id + ');" > ' + value+ " : " +id + '</li>';   
    $("#SO-dialog-form").append(element);
 
}//add

function getCustomerDetails()
{  		
	 qpflag = localStorage["QPFLAG"];
	alert(qpflag);
	if(qpflag){
		var altid = localStorage["ALTID"];
		 MatIdDescList = JSON.parse(localStorage["MatIdDescList"]);
		var requestflag = new Array();
		requestflag[0]= qpflag;
		requestflag[1]= altid;
		setNotifyFlagToQueueProcessor(requestflag);	
		localStorage["QPFLAG"] = nullstr;
		$('#ErrorDescription').show();	
		$('#createcustomersearch').hide();	
		$('#socustpickcatbtn').hide();	
		$('#creatematerialsearch').hide();
		$('#somatpickcatbtn').hide();		
	}else{
		$('#ErrorDescription').hide();	
		localStorage["MatIdDescList"] = JSON.stringify(nullstr);
		localStorage["REFID"] = nullstr ;		 
			qpsoid ="";	
			qpflag =nullstr;
			 localStorage["QPFLAG"] = nullstr;
			 localStorage["ERRMSG"]= nullstr;
			//localStorage["QPFLAG"] = qpflag;
	}	
	 qpsoid = localStorage["REFID"]; 
	 errordescStr = localStorage["ERRMSG"]; 
		//alert(errordescStr);
	 setErrorMessage("ErrorDescription");
    db.transaction(getProductListOfflineValues, errorCB);
}//getCustomerDetails

function setErrorMessage(viewId){
	var ui= "<img src='../img/error.png'/>" + errordescStr;
	$( "#"+viewId ).append( ui );
}//setErrorMessage


function getProductListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_customer_search, [], showCustomerListquerySuccess, errorCB);
}//getProductListOfflineValues

function showCustomerListquerySuccess(tx, results) 
{	
	//alert(customerIdStr);
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;	
	//alert(tname);
	if(tname != "")
	{
		if(qpflag){
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + SO_CUS_SER_COL_KUNNR +' = "' + qpsoid +'"';
			//selectTableValues(query, dynamicUI);
		}else{
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + SO_CUS_SER_COL_KUNNR +' = "'+ customerIdStr +'"';
		}
		
		console.log(query);
		selectTableValues(query,dynamicUI);
		hideWaitMe();
	}	
}//showCustomerListquerySuccess


function dynamicUI(results)
{	
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("CREDIT INFO TABLE RESULT:"  + rows[SO_CUS_SER_COL_KUNNR]);		
	}	
	dynamicCustomerDetailView(fieldAttr,fieldLabel,results,"ContactsParentDiv");	
	//window.localStorage.setItem("lastscreenid", FUNCTION_ID_TABLE[3].id);		
}//dynamicUI

function  dynamicCustomerDetailView(fieldAttr,fieldLabel,values,viewId)
{
	var creatflag;
	copyflag ="";
	 copyflag = localStorage["COPY_FLAG"];
	 creatflag = localStorage["CREATE_SO_FLAG"];
	 //alert(copyflag);
	 //addedmattflag =false;
	$("#SO-dialog-form").hide();
	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	var valueLen = values.rows.length;
	
	var label;
	var type;
	var value;
	var changeiconvar;
	var describedValue;
	var podatestr;
	var ponumber ="";
	var podate ="";
	
	$( "#"+viewId ).text( '' );
	//alert(fieldAttrLen);
	for(var i = 0; i < fieldAttrLen; i++)
	{
		//label = "";
		value = "";
		describedValue = "";
		changeiconvar ="";
		podatestr="";
		/*ponumber ="";
		podate="";*/
		
		var cloumnLabel;
		var cloumnValue;
		var cloumnDescribedValue;
		
		var fieldAttrRow = fieldAttr.rows.item(i);
		
		if(valueLen > 0)
		{
			var fieldValueRow = values.rows.item(0);
			cloumnValue = fieldAttrRow.NAME.split("-")[1];
			console.log(JSON.stringify(fieldValueRow));
			
			if(copyflag){
				$('#ErrorDescription').hide();	
				if(cloumnValue == "NAME1"){
					cloumnValue = "NAME1A";
				}
				if(cloumnValue == "ORT01"){
					cloumnValue = "ORT01A";
				}
				if(cloumnValue == "REGIO"){
					cloumnValue = "REGIOA";
				}
				if(cloumnValue == "LAND1"){
					cloumnValue = "LAND1A";
				}
				customernoStr = fieldValueRow["KUNAG"];
				customernameStr = fieldValueRow["NAME1A"];
			}
			if(creatflag){
				customernoStr = fieldValueRow["KUNNR"];
				customernameStr = fieldValueRow["NAME1"];
			}
			//alert(cloumnValue);
			/*else{
				customernoStr = fieldValueRow["KUNNR"];
			}*/
			value = fieldValueRow[cloumnValue];
			//alert(value);					
			//alert(customernoStr);
			
			if(fieldAttrRow.VALUE == "DESCRIBED")
			{
				cloumnDescribedValue = fieldAttrRow.TRGTNAME.split("-")[1];	
				 //replaceAll("undefined","",ui);
				//alert(cloumnDescribedValue);			
				describedValue = fieldValueRow[cloumnDescribedValue];
				//alert(describedValue);
			}								
			if(cloumnValue == "CHANGE"){
				//value = "";
				//label ="";
				//alert(cloumnValue);				
				changeiconvar = "<img src='../img/go_back.png' style='height= 2'onclick= 'showCustSearch();'/>";
				//$('#backicon').append(changeiconvar);	
				changeCustmrflag =true;
				//value= " ";
				//describedValue= " ";
			}
			if(cloumnValue == "BSTKD"){
				if(value!=""||value!="undefined"){					
					ponumber = "<input type= 'text' id= 'ponumb' value= '"+value+"' placeholder= ''/>";
					value ="";
				}else{
					ponumber = "<input type= 'text' id= 'ponumb' placeholder= 'PO Number' />";	
				}								
			}		
			if(cloumnValue == "NETWR" && getpriceflag==true){
				value = finalprice+" "+finalunit;				
			}
			
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
				today = converToSystemDateFormat(today);
				if(cloumnValue == "BSTDK"){
					value = "";
					podate ="<span style = 'margin-left:25px;margin-top:15px;display:block;padding:.2em;'>"+elementDatePicker("TODATEPICKER",today);+"</span>";				
				}else{
					value = elementDatePicker("TODATEPICKER",today);		
				}															
		     }					
		}
				
		for(var j = 0; j < fieldLabelLen; j++)
		{
			 fieldLabelRow = fieldLabel.rows.item(j);			
			if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				label = fieldLabelRow.VALUE;
			//alert(fieldLabelRow.NAME);			
		}			
		if(typeof label === 'undefined'){
			label="";
		}
		if(typeof value === 'undefined'){
			value="";
		}
		if(typeof describedValue === 'undefined'){
			describedValue="";
		}
		var ui ="";
		if(podate=="" && ponumber==""){
			 ui = '<div class="ui-grid-d"><div class="ui-block-a"><p>'+ label +'<br></p></div><div class="ui-block-b"><label> '+ value +""+ describedValue +""+ponumber+'</label></div><div class="ui-block-c">'+changeiconvar+""+podate+'</div></div>';
			 label = "";
		}
			
		else if(podate!="" && ponumber!=""){
			 ui = '<div class="ui-grid-d"><div class="ui-block-a"><p>'+ label +'<br></p></div><div class="ui-block-b"><label> '+ value +""+ describedValue +""+ponumber+'</label></div><div class="ui-block-c">'+changeiconvar+""+podate+'</div></div>';
			podate = "";
			ponumber="";
			label = "";
		}				
		//var ui = '<div class="ui-grid-d""><div class="ui-block-a" ><p> '+ label +'</p></div><div class="ui-block-b"><label> '+ value + " " + describedValue +' </label></div> '+changeiconvar+""+podatestr+' </div>';		
		console.log(ui);		
		 replaceAll("undefined","",ui);
		$( "#"+viewId ).append( ui );
		console.log(ui);
		if(copyflag==true || qpsoid!=""){			
			$('#creatematerialsearch').hide();
			$('#somatpickcatbtn').hide();
			$('#SOcreatefooter').show();	
			checkTableContextListdatabase();
		}
	}//
	// alert(dateFormat);
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
	
}//dynamicCustomerDetailView

function showCustSearch(){
	//alert(addedmattflag);
	localStorage["COPY_FLAG"] = "";
	//if(addedmattflag==true){
		$('#creatematerialsearch').hide();
		$('#somatpickcatbtn').hide();		
	//}
	$('#ContactsParentDiv').hide();	
	//$('#table4').hide();		
	$('#createcustomersearch').show();	
	$('#socustpickcatbtn').show();	
	$('#sopopupMenu').show();	
}//showCustSearch

/*$(document).ready(function(){
$('#backicon').click(function(){	    		    		    	
	$('#ContactsParentDiv').hide();
	//$('#table4').hide();		
	$('#createcustomersearch').show();	
	$('#socustpickcatbtn').show();	
});			
});	
*/


function gotoCustDetails(idstr)
{	
	customerIdStr = idstr;
	//alert(customerIdStr);
	$('#ContactsParentDiv').show();
	$('#sopopupMenu').hide();	
	$('#createcustomersearch').hide();
	$('#socustpickcatbtn').hide();
	/*if(copyflag){
		$('#ContactsParentDiv').show();
		$('#sopopupMenu').hide();	
		$('#createcustomersearch').hide();
		$('#socustpickcatbtn').hide();
		$('#SotxtProductSearch').hide();		
		$('#somatpickcatbtn').hide();	
	}else{
		$('#sopopupMenu').hide();	
		$('#createcustomersearch').hide();
		$('#socustpickcatbtn').hide();	
		$('#ContactsParentDiv').show();
		$('#SotxtProductSearch').show();		
		$('#somatpickcatbtn').show();	
	}	*/	
	checkCustomerContextSalesorderListdatabase();
}//gotoCustDetails

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
					whereColumns["value"] = so_customer_search;
					
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
		
}//getCustomersList


function customerSearch()
{		
	if(checkConnection() != "none")
	{
		dropDynamicTable(window.localStorage.getItem(so_customer_search));
		window.localStorage.removeItem(so_customer_search);
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]ACCOUNT-SEARCH[.]VERSION[.]0");
	items.push("ZGSEVDST_CSTMRSRCH01[.]" + $("#SotxtCustomerSearch").val());
	soapRequest(getCustomersList,items);
	}
	else
	{
		checkdatabase();
	}
}//customerSearch

function checkdatabase()
{  	
    db.transaction(getOfflineValues, errorCB);
}//checkdatabase

function getOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_customer_search, [], showquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 305', [], showquerySuccess, errorCB);
	
}//getOfflineValues

function showquerySuccess(tx, results) 
{
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{		
			var query = 'SELECT * FROM ' + tname + ' WHERE ' + SO_CUS_SER_COL_NAME1 +' LIKE "%' + $("#SotxtCustomerSearch").val() +'%"';
			selectTableValues(query,customerDialog);			
		console.log(query);		
		hideWaitMe();
	}
}//showquerySuccess

function customerDialog(results)
{
	//document.getElementById("#SO-dialog-form").innerHTML = "";
	$('#SO-dialog-form').show();	
	var len = results.rows.length;
	//alert(len);
	var header = '<li data-role="divider" class="listheader" data-theme="b">Select Customer</li>';
	//document.getElementById("").appendChild(header);
	//alert(len);
	$("#SO-dialog-form").append(header);
		
	if(len == 1)
	{
		var rows = results.rows.item(0);
		gotoCustDetails(rows[SO_CUS_SER_COL_KUNNR]);
		 return false;
	}
	else{
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
			add("button",rows[SO_CUS_SER_COL_NAME1],rows[SO_CUS_SER_COL_KUNNR],className);			   
		}
	}	
	$('#SO-dialog-form').listview('refresh');
	 var nestedLiPage = $(".ui-page-active");
     $('#sopopupMenu').clone().appendTo(nestedLiPage).popup().popup('open');
     //alert("displayed");
}//customerDialog

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
	
}//getScreenName


function getContextDeails(xmlHttpRequest, status)
{
	console.log("Context Response ::" + xmlHttpRequest.responseText);
	console.log("Context Response ::" + xmlHttpRequest.responseXML);	
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
}//getContextDeails

function checkContextdatabase()
{  	
	
    db.transaction(getContextOfflineValues, errorCB);
}//checkContextdatabase

function getContextOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+customer_cnxt_dtls, [], showqueryContextSuccess, errorCB);
}//getContextOfflineValues

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
}//showqueryContextSuccess

function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
}//checkValues

//MATERIAL SEARCH RELATED CODE
var matid;
var additemflag;
var socreateflag;
function MaterialSearch()
{
	
	/*additemflag="";
	socreateflag="";
	
	additemflag= JSON.parse(localStorage["ADDITEM_FLAG"]);
	socreateflag= JSON.parse(localStorage["CREATE_SO_FLAG"]);*/
	 matid = $("#SotxtProductSearch").val();	 
	/*if(additemflag){
		localStorage["MATERIAL_ID"] = JSON.stringify("");
		matid="";
		 matid = JSON.parse(localStorage["SO_MATERIAL_ID"]);
	}else if(socreateflag){
		 matid = JSON.parse(localStorage["MATERIAL_ID"]);		 
	}	*/
	/*var url = document.URL;
	var value = url.split("=");
	id = value[1];	*/				
	if(checkConnection() != "none")
	{
		dropDynamicTable(window.localStorage.getItem(so_material_search));
		window.localStorage.removeItem(so_material_search);
	var items = new Array();
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]PRODUCT-SEARCH-BROADER-RESULT-VIEW-A[.]VERSION[.]0");
	items.push("ZGSEVDST_MTRLSRCH01[.]"+matid);
	soapRequest(getMaterialList,items);
	}
	else
	{
		getMaterialDetails();
	}
}//MaterialSearch

function getMaterialList(xmlHttpRequest, status)
{
	//document.getElementById("materailSearchResults").innerHTML = "";
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
					//whereColumns["value"] = so_added_item_table_details;
					
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
}//getMaterialList

function getMaterialDetails()
{  	
    db.transaction(getMaterialListOfflineValues, errorCB);
}//getMaterialDetails

function getMaterialListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_material_search, [], showMaterialListquerySuccess, errorCB);
}//getMaterialListOfflineValues

function showMaterialListquerySuccess(tx, results) 
{
	var len = results.rows.length;	
		//alert(len);
		 materialtname = results.rows.item(0).tname;
		//alert(tname);
		if(materialtname != "")
		{
			var query = 'SELECT * FROM ' + materialtname + ' WHERE upper(' + SO_MAT_INFO_COL_MATNR +') LIKE "%' + matid.toUpperCase() +'%"'; 
			console.log(query);
			selectTableValues(query,SomaterialList);
			hideWaitMe();
		}	
}//showMaterialListquerySuccess

//GET ALL ADDED MATERIALS DETAILS FROM ADD ITEM MATERIAL SEARCH
	function getAddedMaterialDetails()
	{  	
	    db.transaction(getAddedMaterialListOfflineValues, errorCB);
	}//getAddedMaterialDetails

	function getAddedMaterialListOfflineValues(tx) {    	
		//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
		tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_material_search, [], showAddedMaterialListquerySuccess, errorCB);
	}//getAddedMaterialListOfflineValues

		function showAddedMaterialListquerySuccess(tx, results){	
			var tname;						
			alert(selectedMaterials.length);
			tname = results.rows.item(0).tname;
			if(tname != "")
			{
				var query = 'SELECT DISTINCT * FROM ' + tname + ' WHERE ';// + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
				for(var i = 0; i < selectedMaterials.length; i++)
				{
					if(i != (selectedMaterials.length-1))
						query = query + SO_MAT_INFO_COL_MATNR + "='" + selectedMaterials[i] +"' OR " ;
					else if(i == (selectedMaterials.length-1))
						query = query + SO_MAT_INFO_COL_MATNR + "='" + selectedMaterials[i] +"'" ;
				}				
				console.log(query);
				selectTableValues(query,MaterialTabledynamicUI);
				hideWaitMe();	
			}
	}//	showAddedMaterialListquerySuccess
		
		//END OF FETCHING DETAILS OF ADDED MATERIALS SEARCH//********//
	function dynamicMattViewTable(values,fieldAttr,fieldLabel,viewId,objid)
	{			
		if(!addedmattflag || getpriceflag==true){			
			$('#'+viewId +' thead').html("");
			$('#'+viewId +' tbody').html("");	
			//getpriceflag ="";
		}if(addedmattflag){
			 $('#'+viewId +' thead').html("");
		}
		
		//alert(popultetableFlag);		
	 //getpriceTableContents = values;
	 var len = values.rows.length;		
	 var fieldAttrLen = fieldAttr.rows.length;
	 var fieldLabelLen = fieldLabel.rows.length;
	// alert(rows);
	 var label;
	 var headquery = "<tr>";
	 
	 for(var i = 0; i < fieldAttrLen; i++)
	 {
	  var fieldAttrRow = fieldAttr.rows.item(i);
	  label = "";
	  for(var j = 0; j < fieldLabelLen; j++)
	  {
	   var fieldLabelRow = fieldLabel.rows.item(j);
	  
	   //alert(fieldAttrRow.NAME);
	   if(fieldAttrRow.NAME == fieldLabelRow.NAME)
	    label = fieldLabelRow.VALUE;
	  
	  // alert(label);
	  }	  
	  headquery = headquery + "<th>" + label +"</th>";	  
	 }	 
	 headquery = headquery + "</tr>";
	 
	 $('#'+viewId +' thead').append(headquery); 
	 
	 //alert(getpriceflag);
	 for(var i = 0; i < len; i++)
	 {	  
		 var productId = "";
		 var rows = values.rows.item(i);
		 productId = rows[objid];		
    	/* var  itemId = rows["POSNR"];	    						  	
		  ItemIdList[i] = itemId;
		  alert(ItemIdList[i]);			 */
		// alert(productId);
		 //var query="";
		 var found = $.inArray(productId, DeleteProdIdList) > -1;	
		// alert(found);
		 if(!found){			 		 
		 var query = "<tr >";	 	  
	 // var rows = values.rows.item(i);
	  var cls = "";
	  
	  if(objid != "null")
	  {		 
	   query = "<tr id='" + rows[objid] + "'>";
	  }
	  
	  var fieldValue = "";
	  //alert(fieldAttrLen);
	  for(var k = 0; k < fieldAttrLen; k++)
	  {
	   var fieldAttrRow = fieldAttr.rows.item(k);
	   cls = "";
	   fieldValue = "";
	 
	   for(var j = 0; j < fieldLabelLen; j++)
	   {
	    var fieldLabelRow = fieldLabel.rows.item(j);    
	   
	    if(fieldAttrRow.NAME == fieldLabelRow.NAME)
	    {   	    		    	 
	     label = fieldAttrRow.NAME.split("-")[1];
	     //alert(fieldAttrRow.DATATYPE);
	     //alert(label);	   
	     	    
	     if(copyflag==true && addedmattflag==true || socreateflag==true || addedmattflag==true){	    	
	    	  if(label == "ARKTX")
		 	    {	
		    	 label = "MAKTX";	
		    	 //matdescStr = rows[label];
		 	    }	
		     if(label == "VRKME")
		 	    {	
		    	 label = "MEINH";	   		
		 	    } 		    		    
	     }
	    
	     fieldValue = rows[label];	
	     if(qpflag){	    	
	    	 //alert(qpflag);
	    	 if(label == "ARKTX")	    		
	    	 fieldValue = MatIdDescList[i];
	    	// alert(MatIdDescList[i]);
	     }
	     
	     if(label == "NETWR" || label == "WAERK"){
	    	  fieldValue = "";	
	     }
	     if(getpriceflag){
	    	 var found1 = $.inArray(productId, GetPriceMattList) > -1;	
	    	if(found1)
	    		var indexID = GetPriceMattList.indexOf(productId);
	    	 if(label == "NETWR"){
	    		 fieldValue = GetpriceList[indexID];	    		 
	    		 alert(fieldValue);
	    	 }
	    		 //fieldValue = GetpriceList[i];		    		 
	    	  if(label == "WAERK")
	    		 fieldValue =GetpriceUnitList[indexID];
	    	 //fieldValue =GetpriceUintList
	     }
	     //alert(qpflag);
	     	   
	  	 if(label == "KWMENG"){	  	 			
	  	 	fieldValue = "<input type= 'text' name= 'QtyText[]' id= 'Qty"+i+"' placeholder= 'Quantity' value = ''/>";	  	 								
				}		  	 		  		  	 		    
	    // alert(fieldValue);
	     if(fieldAttrRow.DATATYPE == "QUAN" || fieldAttrRow.DATATYPE == "CURR")
	      cls = "right";
	      
	     if(fieldAttrRow.DATATYPE == "DATS"){
	    	 fieldValue = converToSystemDateFormat(fieldValue);
	     }
	      
	    }
	    if(fieldAttrRow.NAME == "ACTION-DELETE-ITEM")
	    {	    				
	         fieldValue = "<img src='../img/delete2.png' onclick='DeleteItems(this);'/>";	
	        
	    }	   	   
	   }	   
	   if(typeof fieldValue === 'undefined'){
		   fieldValue="";
	   }	  	   	  	 	    			   
		   query = query + "<td class='" + cls  +"'>" + fieldValue + "</td>";//store the entire row into a query variable		 	   	  
	   //alert(query);
	  }	   
	  query = query +  "</tr>";//insert each query variable into a table row 
	  replaceAll("undefined","",query);
	  $('#'+viewId +' tbody').append(query);
		 }
	  //console.log(query);//insert table row to table body
	 }
	  $("#page").trigger("create");
	     $("#contentTable").niceScroll();
	     	     
	     //$('#'+viewId).dataTable({"info":false,"iDisplayLength": 10} );//used for pagination
	  $(".dataTables_filter").hide();//de activating search bar from library
	  $(".dataTables_length").hide();
	  $(".search").keyup(function(){$(".dataTables_filter input").val($(".search").val());$( ".dataTables_filter input" ).keyup();});//search functionality for table
	     
	  $("#txtProductSearch").keyup(function(){$(".dataTables_filter input").val($("#txtProductSearch").val());$( ".dataTables_filter input" ).keyup();});
	  $("#txtCustomerSearch").keyup(function(){$(".dataTables_filter input").val($("#txtCustomerSearch").val());$( ".dataTables_filter input" ).keyup();});
	  
	  $("#SotxtProductSearch").keyup(function(){$(".dataTables_filter input").val($("#SotxtProductSearch").val());$( ".dataTables_filter input" ).keyup();});
	  $("#SotxtCustomerSearch").keyup(function(){$(".dataTables_filter input").val($("#SotxtCustomerSearch").val());$( ".dataTables_filter input" ).keyup();});
	  $(".dataTables_filter input").attr("data-role", "none");//deactivating the automatic search functionality from the library
	  
	  var rowCount = document.getElementById('table4').rows.length;	 
		  getpriceTableContents = values;
		  getAllTableData(rowCount);
	   	 
	}//dynamicMattViewTable
	
	function getAllTableData(rowcount){
		var mytable = document.getElementById("table4");
		for(var t=0;t<rowcount;t++){
			//alert(document.getElementById("table4").rows[t].cells[2].innerHTML);
			MatIdDescList[t] = document.getElementById("table4").rows[t].cells[1].innerHTML;
			MatIdList[t] = document.getElementById("table4").rows[t].cells[2].innerHTML;		
			//alert(MatIdList[t]);
			PriceIdList[t] = document.getElementById("table4").rows[t].cells[5].innerHTML;
			//var qtyStr = document.getElementById("Qty").value;
			UnitIdList[t] = document.getElementById("table4").rows[t].cells[4].innerHTML;
			//QtyIdList[t] = qtyStr;
			//var myinputs = mytable.getElementsByTagName('input');
			//alert(QtyIdList[t]);
		}		
		MatIdList.shift();
		MatIdDescList.shift();
		localStorage["MatIdDescList"] = JSON.stringify(MatIdDescList);
	}//getAllTableData
	
	function MaterialTabledynamicUI(results)
	{						
		$('#table4').show();		
		dynamicMattViewTable(results, fieldAttr, fieldLabel, "table4",SO_MAT_SER_COL_MATNR);			
	}//MaterialTabledynamicUI
		
	
	function DeleteItems(r){	
		 var id = r.parentNode.parentNode.id;
		 //alert(id);
		 var i = r.parentNode.parentNode.rowIndex;
		  document.getElementById("table4").deleteRow(i);
		  DeleteProdIdList.push(id);
		  alert(DeleteProdIdList);
		/*delete getpriceTableContents.rows.item(i);
		alert(getpriceTableContents.rows.length);*/
	}//DeleteItems
	
	
function SomaterialList(results)
{	
	$('#SolistMaterial').text('');
	var len = results.rows.length;
	//alert(len);
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
		localStorage["MATERIAL_ID"] = nullstr;
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
}//SomaterialList

function addMaterial(rows,className) {	
	$('#sopopupMenu').hide();	
	$('#createcustomersearch').hide();
	$('#socustpickcatbtn').hide();	
	$('#ContactsParentDiv').hide();
	$('#creatematerialsearch').hide();		
	$('#somatpickcatbtn').hide();	
	$('#materailSearchResults').show();	
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
 
}//addMaterial

function checkMaterialContextSalesorderListdatabase()
{  	
    db.transaction(getContextMaterialListOfflineValues, errorCB);
}//checkMaterialContextSalesorderListdatabase

function getContextMaterialListOfflineValues(tx) 
{ 	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 301', [], showqueryContextmaterialListSuccess, errorCB);
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+ salesorders_details, [], showProductCategoryListSuccess, errorCB);
}//getContextMaterialListOfflineValues

function showqueryContextmaterialListSuccess(tx, results) 
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
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "PRODUCT-SEARCH-RESULT"';
		selectTableValues(query, getScreenNameSalesorderList);	
		
		var query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "PRODUCT-SEARCH-RESULT-MAIN" ORDER BY SEQNR ASC';
		selectTableValues(query,getFieldAttrDisplayValue);
		
		MaterialSearch();		
	}		
}//showqueryContextmaterialListSuccess

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
}//getFieldAttrDisplayValue


function getSOSelectedMaterials()
{
	//addedmattflag="";
	addedmattflag= JSON.parse(localStorage["ADDED_ITEM"]);	
	//alert(addedmattflag);
	$("input:checkbox[name=materials]:checked").each(function()
			{
					    // add $(this).val() to your array
				selectedMaterials.push($(this).val());
			});
	
	if(qpsoid==""){
		localStorage["SelectedMattSOCreate"] = JSON.stringify(selectedMaterials);
	}
	if(selectedMaterials.length != 0 || finaladdedMaterials.length != 0){
	//if(finaladdedMaterials.length != 0){
		$('#sopopupMenu').hide();	
		$('#createcustomersearch').hide();
		$('#socustpickcatbtn').hide();			
		$('#creatematerialsearch').hide();		
		$('#somatpickcatbtn').hide();	
		$('#materailSearchResults').hide();	
		$('#footer').hide();	
		$('#SOcreatefooter').show();			
		$('#ContactsParentDiv').show();
		$('#table4').show();	
		checkTableContextListdatabase();		
	}		
	else
		alert('Material Id is Empty');		
}//getSOSelectedMaterials

//GET THE UI CONFIGURATION FOR TABLE HERE
function checkTableContextListdatabase()
{  	
    db.transaction(getTableContextListOfflineValues, errorCB);
}//checkTableContextListdatabase

function getTableContextListOfflineValues(tx) 
{ 
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = ' + salesorders_cnxt_dtls, [], showqueryTableContextListSuccess, errorCB);
}//getTableContextListOfflineValues

function showqueryTableContextListSuccess(tx, results) 
{  
	var len = results.rows.length;
	console.log(len);
	var tname = results.rows.item(0).tname;
	console.log(tname);
	if(tname != "")
	{
		var mattquery = 'SELECT * FROM ' + tname ;
		console.log(mattquery);
		//alert('tname ' + tname);
		selectTableValues(mattquery,checkValues);		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "SO-CREATE-COPY-W"';
		selectTableValues(query, getScreenNameSalesorderList);				
		
		var matquery = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "SO-ITEM-OVERVIEW-EDIT" ORDER BY SEQNR ASC';
		var matquery1 = 'SELECT * FROM ' + tname + ' WHERE CNTXT4 = "FIELD-LABE" AND CNTXT2 = "SO-ITEM-OVERVIEW-EDIT"';
		//console.log("showcontextquerySuccess ::" + query);
		selectTableValues(matquery, getmatFieldAttr);
		selectTableValues(matquery1, getmatFieldLabel);
	}		
	hideWaitMe();
	/*alert(copyflag);
	alert(getpriceflag);
	alert(addedmattflag);*/
	if(copyflag){		
		qpsoid ="";
		//copyflag =false;
		copiedMaterials = JSON.parse(localStorage["COPIED_ITEMS"]);	
		getCopyItemTableDetails();//METHOD TO DISPLAY THE COPIED MATERIAL DETAILS ONTO TABLE
	}
	if(addedmattflag==true || qpsoid!=""){					
		if(qpsoid!=""){
			checkSOOfflineListdatabase();
			//selectedMaterials =JSON.parse(localStorage["SelectedMattSOCreate"]);				
			/*var tablerows = JSON.parse(localStorage["TableRows"]);	
			MaterialTabledynamicUI(tablerows);*/
		}else{
			getAddedMaterialDetails();	
		}							
	}	
}//showqueryTableContextListSuccess

//to delete elements in array can be added to library//******//
/*function removeA(arr) {
    var what, a = arguments, L = a.length, ax;
    while (L > 1 && arr.length) {
        what = a[--L];
        while ((ax= arr.indexOf(what)) !== -1) {
            arr.splice(ax, 1);
        }
    }
    return arr;
}//removeA
*/function getmatFieldAttr(results)
{
	var len = results.rows.length;
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("ATTR RESULT:"  + rows.NAME);		   
	}
	fieldAttr = results;	
}//getmatFieldAttr

function getmatFieldLabel(results)
{
	var len = results.rows.length;
	//alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);		   
	}
	fieldLabel = results;	
}//getmatFieldLabel

//GETTING TABLE CONTENTS FROM DATABASE
function addmatterial(values)
{
	var len = values.rows.length;

	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	/*console.log(JSON.stringify(fieldAttr));
	console.log(JSON.stringify(fieldLabel));
	console.log(JSON.stringify(values));*/
	var label;
	var headquery = "<tr>";
	
	//$( "#divDunamicUI" ).text( '' );
	
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
	
	$('#table4 thead').append(headquery); 
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
		$('#table4 tbody').append(query); 				
		//console.log('table value :' + x) ;
	}
	console.log($('#table4 tbody').html());
}//addmatterial

//END OF TABLE CONTENTS

//GET PRICE FUNCTIONALITY//******//
function SendPriceToSAP()
{
	dropDynamicTable(window.localStorage.getItem(so_main_getprice_details));
	window.localStorage.removeItem(so_main_getprice_details);
	
	dropDynamicTable(window.localStorage.getItem(so_item_getprice_details));
	window.localStorage.removeItem(so_item_getprice_details);
	
	//
	var items = new Array();
	/*var unnitStr;
	var itemIdStr;
	var priceUnitIdStr;*/
	 FULLFILLBY_DATE = $("#TODATEPICKER").val();
	FULLFILLBY_DATE = converToStandardDateFormat(FULLFILLBY_DATE);
	ponumbStr = $("#ponumb").val();
	alert(ponumbStr);
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]SALES-ORDER-PRICE[.]VERSION[.]0");
	items.push("DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME");
	//items.push(addSaveOrderRequest());
	var length = getpriceTableContents.rows.length;
	for(var i=0;i<length;i++){
		 var rows = getpriceTableContents.rows.item(i);
		 var matchId =false;
		 for(var h=0;h<DeleteProdIdList.length;h++){
			// alert(MatIdList);
			 if(DeleteProdIdList[h] == MatIdList[i]){
				 matchId= true;
				 
			 }
		 }
		 //alert(matchId);
		 if(!matchId){
			// matnoStr = rows["MATNR"];	
			 matnoStr= MatIdList[i];
			 //unnitStr = rows["MEINH"];
			 unnitStr = UnitIdList[i];
			 itemIdStr = ItemIdList[i];
			 //priceUnitIdStr = priceUintList[i];
			 var quantyStr = document.getElementById("Qty"+i).value;	
			 QtyIdList[i] = quantyStr;
				 alert(quantyStr);
				addgtPriceReq[i] = "ZGSEVAST_SDCRTN20[.]"+customernoStr+"[.]"+FULLFILLBY_DATE+"[.]"+itemIdStr+"[.]"+matnoStr+"[.]"+quantyStr+"[.]"+unnitStr;
				items.push(addgtPriceReq[i]);			 
		 }
	}
	if(checkConnection() != "none")
	{			
		soapRequest(savePriceResultDetails,items);
	}
}//SendPriceToSAP

function savePriceResultDetails(xmlHttpRequest, status)
{
	//console.log(xmlHttpRequest.responseText);
	//console.log(xmlHttpRequest.responseXML);
	console.log("Soap Response ::" + xmlHttpRequest.responseText);
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
			whereColumns["value"] = so_main_getprice_details;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);					
		}
		if(i==3){
			var result = name.split("[.]");					
			createDynamicTable(result);
			
			var updateColumns = [];
			updateColumns.push({column:'tname',value:result[1]});
			
			var whereColumns = {};
			whereColumns["column"] = "id";
			whereColumns["value"] = so_item_getprice_details;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);			
		}
		if(i>3)
		{
			var result = name.split("[.]");					
			insertTableValues(result)
		}
		i++;
	});
	checkUpdatedPriceListdatabase();		
}//savePriceResultDetails

//GETTING UPDATED SO PRICE VALUES FROM DATABASE//**********//
function checkUpdatedPriceListdatabase()
{  	
    db.transaction(getUpdatedPriceListOfflineValues, errorCB);
}//checkUpdatedPriceListdatabase

function getUpdatedPriceListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_main_getprice_details, [], showUpdatedPriceListquerySuccess, errorCB);
}//getUpdatedPriceListOfflineValues

function showUpdatedPriceListquerySuccess(tx, results) 
{
	getpriceflag =true;
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;	
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		console.log(len);
		hideWaitMe();			
		selectTableValues(query, SoValueResult);
	}
}//showUpdatedPriceListquerySuccess


function SoValueResult(results)
{
	finalprice=" ";
	finalunit="";
	var len = results.rows.length;	
	console.log("result len: "+len);
	hideWaitMe();
	 var rows = results.rows.item(0);
	 fieldValue = rows["NETWR"];	
	 finalprice = fieldValue;
	 //alert(finalprice);
	 //GETTING FINAL UNIT
	 fieldValue = rows["WAERK"];	
	 finalunit = fieldValue;
	 //alert(finalunit);
	 checkCustomerContextSalesorderListdatabase();
	 checkIndivdlUpdatedPriceListdatabase();
}//SoValueResult
//END OF GETTING UPDATED SO PRICE VALUES FROM DATABASE//**********//


//GET INDIVIDUAL PRICE VALUES FOR TABLE
function checkIndivdlUpdatedPriceListdatabase()
{  	
    db.transaction(getUpdatedIndPriceListOfflineValues, errorCB);
}//checkIndivdlUpdatedPriceListdatabase

function getUpdatedIndPriceListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_item_getprice_details, [], showUpdatedIndivdlPriceListquerySuccess, errorCB);
}//getUpdatedIndPriceListOfflineValues

function showUpdatedIndivdlPriceListquerySuccess(tx, results) 
{
	//getpriceflag =true;
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;	
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		console.log(len);
		hideWaitMe();			
		selectTableValues(query, PriceValueResult);
	}
}//showUpdatedIndivdlPriceListquerySuccess


function PriceValueResult(results)
{
	getpriceflag =true;
	var valueStr;
	valueStr=""
	indprice="";
	indunit="";
	var len = results.rows.length;
	 for(var i = 0; i < len; i++)
	 {	
		
		 var rows = results.rows.item(i);
		 //var matnrStr = rows["MATNR"];	
		 //alert(matnrStr);
		 //var getpriceStr = rows["NETWR"];
		 //alert(getpriceStr);		 
		 //GetpriceList = {matnrStr:getpriceStr};
		 GetPriceMattList[i] = rows["MATNR"];	
		 GetpriceList[i]= rows["NETWR"];
		 GetpriceUnitList[i] =rows["WAERK"];	
	 }
	
	hideWaitMe();	
	 //alert(indprice);
	 //GETTING FINAL UNIT
	 //valueStr = rows["WAERK"];	
	 //indunit = valueStr;
	 //alert(indunit);
	 checkTableContextListdatabase();
	 
}//PriceValueResult
//END OF GETTING INDIVUDUAL PRICES//**************************//


////>SEND ORDER TO SAP-->>SAVE ORDER FUNCTIONALITY//******//
function SendOrderToSAP()
{
	var items = new Array();
	/*var unnitStr;
	var itemIdStr;
	var priceUnitIdStr;*/
	 FULLFILLBY_DATE = $("#TODATEPICKER").val();
	FULLFILLBY_DATE = converToStandardDateFormat(FULLFILLBY_DATE);
	ponumbStr = $("#ponumb").val();
	alert(ponumbStr);
	items.push("DEVICE-ID:"+window.localStorage.getItem("deviceid")+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
	items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
	items.push("EVENT[.]SALES-ORDER-CREATE[.]VERSION[.]0");
	items.push("DATA-TYPE[.]ZGSEVAST_SDCRTN20[.]KUNAG[.]KETDAT[.]POSNR[.]MATNR[.]KWMENG[.]VRKME[.]PARNR[.]BSTDK[.]BSTKD");
	//items.push(addSaveOrderRequest());	
	var length = getpriceTableContents.rows.length;
	for(var i=0;i<length;i++){
		 var rows = getpriceTableContents.rows.item(i);
		 var matchId =false;
		 for(var h=0;h<DeleteProdIdList.length;h++){
			// alert(MatIdList);
			 if(DeleteProdIdList[h] == MatIdList[i]){
				 matchId= true;
			 }
		 }
		 //alert(matchId);
		 if(!matchId){
			// matnoStr = rows["MATNR"];	
			 matnoStr= MatIdList[i];
			 //unnitStr = rows["MEINH"];
			 unnitStr = UnitIdList[i];
			 itemIdStr = ItemIdList[i];
			 //priceUnitIdStr = priceUintList[i];
			 var quantyStr = document.getElementById("Qty"+i).value;	
			// var quantyStr = document.getElementsByName("QtyText[]");
				///var qtyvalue= quantyStr[i].value;
			 //var qtyvalue= quantyStr.value;
				 alert(quantyStr);
			 addgtSOReq[i] = "ZGSEVAST_SDCRTN20[.]"+customernoStr+"[.]"+FULLFILLBY_DATE+"[.]"+itemIdStr+"[.]"+matnoStr+"[.]"+quantyStr+"[.]"+unnitStr+"[.][.]"+FULLFILLBY_DATE+"[.]"+ponumbStr;
				// resultlist[i] = "DATA-TYPE[.]PRODUCT_ZGSEVAST_SDCRTN20[.][.]VBELN[.]POSNR[.]MATNR[.]KWMENG[.]VRKME[.]NETWR[.]WAERK[.]ABGRU_TEXT[.]FAKSP_TEXT[.]GBSTA_TEXT[.]LFSTA_TEXT[.]FKSTA_TEXT[.]ARKTX";	
			// valuestrlist[i]= "PRODUCT_ZGSEVAST_SDCRTN20"+"[.]"+sonumbstr+"[.]"+ItemIdList[i]+"[.]"+matnoStr+"[.]"+quantyStr+"[.]"+unnitStr+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+MatIdDescList[i];			
			items.push(addgtSOReq[i]);
		 }
	}
	if(checkConnection() != "none")
	{		
		localStorage["SO"] = flagtrue;	
		//localStorage["LocalDataFlag"] = nullStr;
		soapRequest(saveSOResultDetails,items);					
	}
	else{
		sendtolocaldbflag =true;
		localStorage["SENDTOQP"] = true;
		//
		//alert(customernoStr);
		var request = new Array();
		request[0]= customernoStr;
		request[1]= "SALES-ORDER-CREATE";
		for(var i=0; i<items.length; i++){
			request.push(items[i]);
		}			
		alert("Sent to Queue Processor");
		sendSOToQueueProcessor(request);
		InsertIntoDB();
	}	

}//SendPriceToSAP

function InsertErrorDatatoDB(){
	
	for(var i=0;i<618;i++){
		
	}	
	//var respType = localStorage["RespnseType"];	
	sendtolocaldbflag =false;
	 var respType = localStorage["RespnseType"];
	 alert(respType);
	if(respType=="E"){
		hideWaitMe();
		//localStorage["SENDTOQP"] =flagfalse;
		InsertIntoDB();
	}		
	/*else{
		valuestrlist=[];	
	}*/		
}//InsertErrorDatatoDB

function InsertIntoDB(){
	var lcldataflag= true;
	localStorage["LocalDataFlag"]= lcldataflag;
	dropDynamicTable(window.localStorage.getItem('ZGSEVAST_SDCRTN20'));
	window.localStorage.removeItem('ZGSEVAST_SDCRTN20');
	var result = "DATA-TYPE[.]ZGSEVAST_SDCRTN20[.][.]NAME1A[.]VBELN[.]ZZSTATUS_SUMMARY[.]NETWR[.]KETDAT[.]BSTDK[.]BSTKD";	
	result = result.split("[.]");	
	createDynamicTable(result);
	var updateColumns = [];
	updateColumns.push({column:'tname',value:result[1]});
	
	var whereColumns = {};
	whereColumns["column"] = "id";
	whereColumns["value"] = so_qp_details;
	updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);	
	var length = getpriceTableContents.rows.length;
	// var sendtoQp = localStorage["SENDTOQP"];
	 var now = new Date(); 
	 var day = now.getDate();
	 alert(sendtolocaldbflag);
	 if(sendtolocaldbflag)
		 sonumbstr = customernoStr+ponumbStr+day+"[,]"+flagtrue;
	 else
		 sonumbstr = customernoStr+ponumbStr+day+"[,]"+flagfalse;
	
	//for(var k=0;k<length;k++){
		 //var quantyStr = document.getElementById("Qty"+k).value;			 
		 var valuestr= "ZGSEVAST_SDCRTN20"+"[.]"+customernameStr+"[.]"+sonumbstr+"[.]"+" "+"[.]"+""+"[.]"+FULLFILLBY_DATE+"[.]"+FULLFILLBY_DATE+"[.]"+ponumbStr;			
	//var valuestr= "ZGSEVAST_SDCRTN20"+"[.]"+customernoStr+"[.]"+FULLFILLBY_DATE+"[.]"+ItemIdList[k]+"[.]"+MatIdList[k]+"[.]"+quantyStr+"[.]"+unnitStr+"[.][.]"+FULLFILLBY_DATE+"[.]"+ponumbStr+"[.]"+MatIdDescList[k]+"[.]"+customernameStr+""+"[.]";			
		var resultstr =  valuestr.split("[.]");		
		insertTableValues(resultstr);
		InsertLocalProductDetails();
	//}	
}//InsertIntoDB

function InsertLocalProductDetails(){
	dropDynamicTable(window.localStorage.getItem('PRODUCT_ZGSEVAST_SDCRTN20'));
	window.localStorage.removeItem('PRODUCT_ZGSEVAST_SDCRTN20');
	var result = "DATA-TYPE[.]PRODUCT_ZGSEVAST_SDCRTN20[.][.]VBELN[.]POSNR[.]MATNR[.]KWMENG[.]VRKME[.]NETWR[.]WAERK[.]ABGRU_TEXT[.]FAKSP_TEXT[.]GBSTA_TEXT[.]LFSTA_TEXT[.]FKSTA_TEXT[.]ARKTX";	
	result = result.split("[.]");	
	createDynamicTable(result);
	var updateColumns = [];
	updateColumns.push({column:'tname',value:result[1]});
	
	var whereColumns = {};
	whereColumns["column"] = "id";
	whereColumns["value"] = so_localprice_details;
	updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);	
	//var length = getpriceTableContents.rows.length;
	 var now = new Date(); 
	 var day = now.getDate();
	
	// var sendtoQp = localStorage["SENDTOQP"];
	 alert(sendtolocaldbflag);
	for(var k=0;k<addgtSOReq.length;k++){
		
		 if(sendtolocaldbflag)
			 sonumbstr = customernoStr+ponumbStr+day+"[,]"+flagtrue;
		 else
			 sonumbstr = customernoStr+ponumbStr+day+"[,]"+flagfalse;
		 
		valuestrlist[k]= "PRODUCT_ZGSEVAST_SDCRTN20"+"[.]"+sonumbstr+"[.]"+ItemIdList[k]+"[.]"+matnoStr+"[.]"+quantyStr+"[.]"+unnitStr+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+""+"[.]"+MatIdDescList[k];			
		var resultstr2 =  valuestrlist[k].split("[.]");		
		insertTableValues(resultstr2);
	}
	/*for(var k=0;k<valuestrlist.length;k++){				
		var resultstr2 =  valuestrlist[k].split("[.]");		
		insertTableValues(resultstr2);
	}*/
}//InsertLocalProductDetails
//FETCHING DATA FOR QUEUE PROCESSOR//

function checkSOOfflineListdatabase()
{  	
    db.transaction(getDataOfflineValues, errorCB);
}//checkIndivdlUpdatedPriceListdatabase

function getDataOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_qp_details, [], showSOListquerySuccess, errorCB);
}//getProductListOfflineValues

function showSOListquerySuccess(tx, results) 
{	
	//alert(customerIdStr);
	//var len = results.rows.length;
//	var tname = results.rows.item(0).tname;	
	//alert(tname);
	//if(tname != "")
	//{		
			var query = 'SELECT * FROM ' + ' ZGSEVAST_SDCRTN20 ' + ' WHERE ' + SO_CUS_INFO_COL_KUNAG +' = "' + qpsoid +'"';
			//selectTableValues(query, dynamicUI);		
		console.log(query);
		selectTableValues(query,MaterialTabledynamicUI);
		hideWaitMe();
	//}	
}//showCustomerListquerySuccess


function showlist(results)
{	
	var len = results.rows.length;
	alert(len);
}//dynamicUI
//FETCHED THE DATA FOR QUEUE PROCESSOR///
function saveSOResultDetails(xmlHttpRequest, status)
{	
	
	/*if(qpsoid !=""){
		dropDynamicTable(window.localStorage.getItem('ZGSEVAST_SDCRTN20'));
		window.localStorage.removeItem('ZGSEVAST_SDCRTN20');
	}*/
}//saveSOResultDetails

function ClearAllDBData(){
	dropDynamicTable(window.localStorage.getItem('ZGSEVAST_SDCRTN20'));
	window.localStorage.removeItem('ZGSEVAST_SDCRTN20');
	localStorage["QPFLAG"] = nullstr;
}//ClearAllDBData
////>END OF SEND ORDER TO SAP-->>SAVE ORDER FUNCTIONALITY//******//

//COPY FUNCTIOANLITY CODE

function gotoCopyCustDetails()
{			
	var url = document.URL;
	var value = url.split("=");
	id = value[1];	
	/*if(flag){
		alert(SODetails[1]);
		id = SODetails[1];
	}	*/
	//alert(id);
	/*localStorage["CREATE_SO_FLAG"]= "";
	copysoId= JSON.parse(localStorage["COPY_SO_ID"]);*/
	//customerIdStr = id;
	/*
	if(qpflag){
		$('#sopopupMenu').hide();	
		$('#createcustomersearch').hide();
		$('#socustpickcatbtn').hide();	
		$('#ContactsParentDiv').show();
		$('#SotxtProductSearch').hide();		
		$('#somatpickcatbtn').hide();			
		getCustomerDetails();		
	}else{*/
		$('#sopopupMenu').hide();	
		$('#createcustomersearch').hide();
		$('#socustpickcatbtn').hide();	
		$('#ContactsParentDiv').show();
		$('#creatematerialsearch').show();		
		$('#somatpickcatbtn').show();	
		checkSOListdatabaseForCustomer();	
	//}
	
}//gotoCopyCustDetails

//GETTING CUSTOMER DETAILS FOR COPY FEATURE
function checkSOListdatabaseForCustomer()
{  	
    db.transaction(getSOListCustomerValues, errorCB);
}//checkSOListdatabaseForCustomer

function getSOListCustomerValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 303', [], showSOListCustomerSuccess, errorCB);
}//getSOListCustomerValues

function showSOListCustomerSuccess(tx, results) 
{
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;	
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname +  ' WHERE '+ VBELN +' = "' + id +'"'; ;
		console.log(query);
		console.log(len);
		selectTableValues(query, dynamicUI);
	}
}//showSOListCustomerSuccess

//GETTING ITEM DEATILS FROM DATABASE TO DISPLAY ONTO TABLE LAYOUT//*********//
function getCopyItemTableDetails()
{  	
    db.transaction(getCopyitemOfflineValues, errorCB);
}//getCopyItemTableDetails

function getCopyitemOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_item_details, [], showCopyItemListquerySuccess, errorCB);
}//getCopyitemOfflineValues

function showCopyItemListquerySuccess(tx, results) 
{
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;		
	//alert(copiedMaterials.length);
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT DISTINCT '+ SO_MAT_SER_COL_MATNR +","+SO_CUS_INFO_COL_ARKTX+","+SO_CUS_INFO_COL_VRKME+' FROM ' + tname + ' WHERE ';// + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
		for(var i = 0; i < copiedMaterials.length; i++)
		{
			if(i != (copiedMaterials.length-1))
				query = query + VBELN + "='" + copiedMaterials[i] +"' OR " ;
			else if(i == (copiedMaterials.length-1))
				query = query + VBELN + "='" + copiedMaterials[i] +"'" ;
		}
		selectTableValues(query,MaterialTabledynamicUI);
		
		/*var query = 'SELECT * FROM ' + tname + ' WHERE ' + VBELN +' = "' + id +'"';
		console.log(query);	
		selectTableValues(query,MaterialTabledynamicUI)		*/
		hideWaitMe();
	}		
}//showCopyItemListquerySuccess