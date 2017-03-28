var productAttachmentRows;
var copiedMaterials = [];
var vblenStr = [];
var createflag;
var localdataresults;
var offlinedataflag;
offlinedataflag="";
var restype;
function getProductList()
{
	if(checkConnection() != "none" && window.localStorage.getItem("LastScreenId") != SALESORDERS_OVERVIEW)
	{
		 restype = responseTypeDecider(salesorders_details);
		var items = new Array();
		var imeino = localStorage.getItem("imeino");
		var productlistflag = window.localStorage.getItem(SALESORDERS_LIST_PREF);
		items.push("DEVICE-ID:"+imeino+":DEVICE-TYPE:ANDROID:APPLICATION-ID:SALESPRO");
		items.push("NOTATION:ZML:VERSION:0:DELIMITER:[.]");
		if(restype!="")
		 items.push("EVENT[.]SALES-ORDER-FOR-EMPLY-GET[.]VERSION[.]0[.]RESPONSE-TYPE[.]"+restype);
		else
			items.push("EVENT[.]SALES-ORDER-FOR-EMPLY-GET[.]VERSION[.]0");
		items.push("ZGSEVDST_MTRLSRCH01[.]*");		
		soapRequest(getSalesOrderListResponse,items);
	}
	else
	{
		showWaitMe("Compiling Data");	
		var localflag = localStorage["LocalDataFlag"];
		if(localflag)
			checklocalOfflineListdatabase();
		else
			checkProductListdatabase();	
		//checklocalOfflineListdatabase();
		window.localStorage.setItem("LastScreenId", SALESORDERS_OVERVIEW);
	}
}//getProductList

function getSalesOrderListResponse(xmlHttpRequest, status)
{
	//<!CHECK THE DATABASE FOR DUPLICATE DATA AND REMOVE IF ANY>/////
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
			var result = "";	
			result = name.split("[.]");	
			createDynamicTable(result);
			
			var updateColumns = [];
			updateColumns.push({column:'tname',value:result[1]});
			
			var whereColumns = {};
			whereColumns["column"] = "id";
			whereColumns["value"] = salesorders_details;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);					
		}
		if(i==3){
			var result = "";		
			result = name.split("[.]");		
			createDynamicTable(result);
			
			var updateColumns = [];
			updateColumns.push({column:'tname',value:result[1]});
			
			var whereColumns = {};
			whereColumns["column"] = "id";
			whereColumns["value"] = salesorders_item_details;
			//whereColumns["value"] = so_added_item_table_details;
			
			updateDynamicTable('FUNCTIONMASTER',updateColumns,whereColumns);			
		}
		if(i>3)
		{
			var result = "";		
			result = name.split("[.]");		
			insertTableValues(result);
		}
		i++;
	});
	//alert(restype);
	var localflag = localStorage["LocalDataFlag"];
	if(localflag)
		checklocalOfflineListdatabase();
	else
		checkProductListdatabase();	
}

//check offline local data//
function checklocalOfflineListdatabase()
{  	
    db.transaction(getDatalocalOfflineValues, errorCB);
}//checkIndivdlUpdatedPriceListdatabase

function getDatalocalOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+so_qp_details, [], showSOLocalquerySuccess, errorCB);
}//getProductListOfflineValues

function showSOLocalquerySuccess(tx, results) 
{	
	var len = results.rows.length;
	//alert(len);
	//var query;
	var tname = results.rows.item(0).tname;		
	var query = 'SELECT * FROM '+tname;	
		console.log(query);
		selectTableValues(query,getresult);
		hideWaitMe();
	//}	
}//showCustomerListquerySuccess
function getresult(results){
	var len= results.rows.length;
	//alert(len);
	//var sonumblist=[];
	for(var l=0;l<len;l++){
		 var rows = results.rows.item(l);
		// sonumblist = rows["VBELN"].split("[,]");	
		 vblenStr[l] = rows["VBELN"];			 	
	}
	localStorage["LOCALSONUMBS"] = JSON.stringify(vblenStr);
	//alert(vblenStr);
	if(len!=0 && len!="")
		offlinedataflag =true;
	/*else
		localStorage["LocalDataFlag"] = false;*/
	
	  checkProductListdatabase();
	//localdataresults =results;
}
//end of check offline local data
function checkProductListdatabase()
{  		
    db.transaction(getProductListOfflineValues, errorCB);
}

function getProductListOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+product_attachmentR_list, [], showProductListAttachmentquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_details, [], showProductListquerySuccess, errorCB);
}

function showProductListquerySuccess(tx, results) 
{
	var len = results.rows.length;
	//var query;
	var tname = results.rows.item(0).tname;	
	
	//alert(tname);
	if(tname != "")
	{		
		//alert(offlinedataflag);
		if(offlinedataflag){
			//query="";
			var query = 'SELECT '+SO_HEADER_COL_NAME1A+","+VBELN+","+SO_HEADER_ZZSTATUS_SUMMARY+","+SO_HEADER_INFO_COL_NETWR+","+SO_HEADER_INFO_COL_KETDAT+","+ SO_HEADER_INFO_COL_BSTDK+","+ SO_HEADER_INFO_COL_BSTKD+' FROM ' + tname +' UNION '+' SELECT '+SO_HEADER_COL_NAME1A+","+VBELN+","+SO_HEADER_ZZSTATUS_SUMMARY+","+SO_HEADER_INFO_COL_NETWR+","+SO_HEADER_INFO_COL_KETDAT+","+ SO_HEADER_INFO_COL_BSTDK+","+ SO_HEADER_INFO_COL_BSTKD+' FROM '+'ZGSEVAST_SDCRTN20';
		}else{
			//query="";
			var query = 'SELECT DISTINCT '+SO_HEADER_COL_NAME1A+","+VBELN+","+SO_HEADER_ZZSTATUS_SUMMARY+","+SO_HEADER_INFO_COL_NETWR+","+SO_HEADER_INFO_COL_KETDAT+","+ SO_HEADER_INFO_COL_BSTDK+","+ SO_HEADER_INFO_COL_BSTKD+' FROM ' + tname;
		}
		//var query = 'SELECT * FROM ' + tname ;	
		console.log(query);
		console.log(len);
		selectTableValues(query, salesorderList);
	}
}

function salesorderList(results)
{
	var len = results.rows.length;	
	console.log("result len: "+len);
	hideWaitMe();
	var sendqpflag = localStorage["SENDTOQP"];	
	dynamicViewTable(results, fieldAttr, fieldLabel, "table1","VBELN");
	
	$('#table1 tbody tr td span').click(function() {
        //alert($(this).attr('id'));
        
        var id = $(this).attr('id');
      alert(id);
      if(sendqpflag)
        window.location.href = 'mainframe.html?id='+id;
      else
    	  window.location.href = 'salesorder_create.html?id='+id;
        /*checkbox-product*/
   });
	//dynamicViewGrid(results, fieldAttr, fieldLabel, "contentTable","MATNR");			
}

//COPY FUNCTIONALITY//********************//
function getSOCopyMaterials()
{
	
	$("input:checkbox[name=checkbox-product]:checked").each(function()
	{
			    // add $(this).val() to your array		
		copiedMaterials.push($(this).val());
	});
	
	for(var i=0;i<copiedMaterials.length;i++){
		alert(copiedMaterials[i]);
	}
	checkSOdatabase();
	//selectedMaterials.push(customerId);//passing customer id to second screen by adding it to array
}//


//FETCH CUSTOMER DETAILS FROM DB TO SHOW IN POPUP
function checkSOdatabase()
{  	
    db.transaction(getSOOfflineValues, errorCB);
}

function getSOOfflineValues(tx) {    	
	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+salesorders_details, [], showsoquerySuccess, errorCB);
}

function showsoquerySuccess(tx, results) 
{
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	/*for(var k=0;k<copiedMaterials.length; k++){
		SOCopiedMaterials[k] = copiedMaterials[k];
	}*/
	
	//alert(tname);
	if(tname != "")
	{
		var query = ' SELECT DISTINCT '+ VBELN + "," + SO_HEADER_COL_NAME1A +' FROM ' + tname + ' WHERE ';// + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
		for(var i = 0; i < copiedMaterials.length; i++)
		{
			if(i != (copiedMaterials.length-1))
				query = query + VBELN + "='" + copiedMaterials[i] +"' OR " ;
			else if(i == (copiedMaterials.length-1))
				query = query + VBELN + "='" + copiedMaterials[i] +"'" ;			
		}
		console.log(query);
		selectTableValues(query,SOcustomerDialog);
	}
}

function SOcustomerDialog(results)
{
	var len = results.rows.length;
	
	var header = '<li data-role="divider" class="listheader" data-theme="b">Select Customer</li>';
	//document.getElementById("").appendChild(header);
	
	$("#copy-dialog-form").append(header);
		
	if(len == 1)
	{
		var rows = results.rows.item(0);
		gotoItemDetails(rows[VBELN]);
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
		add("button",rows[SO_HEADER_COL_NAME1A],rows[VBELN],className);
		SOCopiedMaterials[i] = rows[VBELN];   
	}
	$('#copy-dialog-form').listview('refresh');
	 var nestedLiPage = $(".ui-page-active");
     $('#copypopupMenu').clone().appendTo(nestedLiPage).popup().popup('open');
}

function add(type,value,id,className) {	
    var element =  '<li class="' + className + '" onclick="gotoItemDetails(' + id + ');" > ' + value+ " : " +id + '</li>';
 
    $("#copy-dialog-form").append(element);
 
}

function gotoItemDetails(id)
{ 
	var copyflag =true;
	//localStorage["COPY_SO_ID"] = id;	
	//window.history.back();
	localStorage["COPIED_ITEMS"] = JSON.stringify(SOCopiedMaterials);
 	//alert(SOCopiedMaterials.length);
	window.location.href = 'salesorder_create.html?id='+id;	
 	//alert(id);	 	
}

