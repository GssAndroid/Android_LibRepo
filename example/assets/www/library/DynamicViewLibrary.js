var LocalSonumblist = [];
var LocalSonumbIDList =[];
var objectIdList =[];
var statusStr;
var statusCodeStr;
var statusCodeList = [];
var statusSpinnerList= [];
var statusui;
var map = {};
var textareaflag =false;

function  dynamicDetailView(fieldAttr,fieldLabel,values,viewId)
{
	
	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	var valueLen = values.rows.length;
	
	var label;
	var type;
	var value;
	var describedValue;
	var date;
	
	$( "#"+viewId ).text( '' );
	
	for(var i = 0; i < fieldAttrLen; i++)
	{
		label = "";
		value = "";
		describedValue = "";
		//date="";
		
		var cloumnLabel;
		var cloumnValue;
		var cloumnDescribedValue;
		
		var fieldAttrRow = fieldAttr.rows.item(i);
		
		if(valueLen > 0)
		{
			var fieldValueRow = values.rows.item(0);
			cloumnValue = fieldAttrRow.NAME.split("-")[1];
			 value="";
			 value = fieldValueRow[cloumnValue];	
			//alert(cloumnValue);
			if(cloumnValue == "STATUS_TXT30"){
				statusStr = fieldValueRow[cloumnValue];
				//alert(statusStr);
				 checkstatusdatabase();	
				 value="";					 
					
				 //alert(statusSpinnerList.length);
				 value = elementSectionDialogWithValues("STATUSELECT",statusSpinnerList);;
			}		
			/*if(cloumnValue == "STATUS_TXT30"){
				 value="";					 
				
				 alert(statusSpinnerList.length);
				 value = elementSectionDialogWithValues("STATUSELECT",statusSpinnerList);;
				 //value = statusui;		 
			 }*/
			//alert(value);
			
			/*if(fieldAttrRow.VALUE == "DESCRIBED")
			{
				cloumnDescribedValue = fieldAttrRow.TRGTNAME.split("-")[1];
			
				//alert(cloumnDescribedValue);
			
				describedValue = fieldValueRow[cloumnDescribedValue];
				//alert(describedValue);
			}*/
			if(cloumnValue == "ZZKEYDATE"){
				value ="";
				value = fieldValueRow[cloumnValue];
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
				today = converToSystemDateFormat(value);
			
				//var dateStr = converToSystemDateFormat(value);
				//value ="<span style = 'margin-left:25px;margin-top:15px;display:block;padding:.2em;'>"+elementDatePicker("STARTDATE",dateStr);+"</span>";				
				value = elementDatePicker("STARTDATE",today);	
				//value ="";
			}
			//else{															 
				 if(actlistLen>1 && cloumnValue == "OBJECT_ID"){
					 value="";
					 value = fieldValueRow[cloumnValue];
					 value = value+"/"+numbextStr;
				 }
				 /*if(cloumnValue == "PROCESS_TYPE_DESCR"){
					 processtype = fieldValueRow[cloumnValue];
				 }*/
				  if(cloumnValue == "TIMEZONE_FROM"){
					 value="";
					 value = elementSectionDialogWithValues("TIMEZONESELECT",timeZone);
				 }	
				  if(cloumnValue == "ZZFIELDNOTE"){					
					  value = "<textarea id='myTextarea' onkeyup='addtext()' rows='4' cols='20' maxlength='20'></textarea>";
					  if(textareaflag){
						  value = "";
						  value="<textarea id='myTextarea' rows='4' cols='20' maxlength='20'>"+textlngth+"</textarea><a href='newtextarea.html'>More</a>";
					  }					  
				  }
				  
				  if(cloumnValue == "CP1_TEL_NO"){
					  if(value!="" && value!="undefined"){
						  //value= value+ "<a href='whatsapp://'>whatsapp</a>";
						  value= value+ "<img border='0'  src='../img/whtsapp.png' style='position: right;' onclick='sendwhatsapp()'/>";
					  }
					  //href="intent://send/0123456789#Intent;scheme=smsto;package=com.whatsapp;action=android.intent.action.SENDTO;end">						 
						 // value= value+ "<a href='intent://send/919738245060#Intent;scheme=smsto;package=com.whatsapp;action=android.intent.action.SENDTO;end' class='fa fa-whatsapp'>whatsapp</a>";					
				  }
				// else{
					
				// }										 					
			//}
		}				
		for(var j = 0; j < fieldLabelLen; j++)
		{
			var fieldLabelRow = fieldLabel.rows.item(j);
			
			if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				label = fieldLabelRow.VALUE;
		}		
		//if(value || describedValue)
		//{
		//var ui ="";
	
		if(value!=""){
			var ui = '<div class="ui-grid-a" style="max-width:500px;marginTop:20px;"><div class="ui-block-a" ><p>'+label +'</p></div><div class="ui-block-b" style="font-size:20px;marginTop:19px;"><span>' + value + " " + describedValue +'</span></div><br><br><br></div>';
			console.log(ui);
			replaceAll("undefined","",ui);
			$( "#"+viewId ).append( ui );	
		}		
		//}
	}
	
	 $("#page").trigger("create");	 
		$(function () {
		    var curr = new Date().getFullYear();
		    alert(dateFormat);
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
		
		
}//


//GET THE STATUS DETAILS//
function checkstatusdatabase()
{  	
    db.transaction(getstatusOfflineValues, errorCB);
}

function getstatusOfflineValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+serviceorders_cdocmt10_dtls, [], showstatusquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM '+TABLE_SERVICE_TASK_EMPLOYEE_DETAILS, [], showstatusquerySuccess, errorCB);	
}

function showstatusquerySuccess(tx, results) 
{ 
	var tname = TABLE_SERVICE_TASK_EMPLOYEE_DETAILS;
	var query = 'SELECT * FROM ' + tname + ' WHERE ' + OBJECT_ID +' = "' + serviceordernumb +'"' +' AND '+ STATUS_TXT +' = "' + statusStr +'"';
	selectTableValues(query,statusresult);
	/*var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE ' + OBJECT_ID +' = "' + serviceordernumb +'"' +' AND '+ STATUS_TXT +' = "' + statusStr +'"';
		console.log(query);
		selectTableValues(query,statusresult);
		hideWaitMe();
	}*/
}//

function statusresult(results)
{			
	var rows = results.rows.item(0);
	statusCodeStr = rows["STATUS"];
	console.log("Status RESULT:"  + statusCodeStr);			
	getstatusCodeListFromDB();
}//statusresult

function getstatusCodeListFromDB(){
	 db.transaction(getstatuscodeValues, errorCB);
}//

function getstatuscodeValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+serviceorders_sttsflow_dtls, [], showcodelistquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM '+TABLE_SERVICE_STATUSFLOW_DETAILS, [], showcodelistquerySuccess, errorCB);	
}

function showcodelistquerySuccess(tx, results) 
{ 
	var tname = TABLE_SERVICE_STATUSFLOW_DETAILS;
	var query = 'SELECT * FROM ' + tname + ' WHERE ' + STATUS +' = "' + statusCodeStr +'"';
	selectTableValues(query,statuscoderesult);
	/*var len = results.rows.length;
	var tname = "ZGSXCAST_STTSFLOW10";
	//alert(tname);
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname + ' WHERE ' + STATUS +' = "' + statusCodeStr +'"';
		console.log(query);
		selectTableValues(query,statuscoderesult);
		hideWaitMe();
	}*/
}//

function statuscoderesult(results)
{			
	var len = results.rows.length;
	//alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		statusCodeList[i]= rows["STATUS_NEXT"];	
		
	}	
	getstatusSpinner();
}//statusresult

function getstatusSpinner(){
	 db.transaction(getstatusSpinnerValues, errorCB);
}//

function getstatusSpinnerValues(tx) {    	
	//tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = '+serviceorders_stts_dtls, [], showstatusSpinnerquerySuccess, errorCB);
	tx.executeSql('SELECT * FROM '+TABLE_SERVICE_STATUS_DETAILS, [], showstatusSpinnerquerySuccess, errorCB);	
}//

function showstatusSpinnerquerySuccess(tx, results) 
{ 
	//var tname = results.rows.item(0).tname;
	var tname = TABLE_SERVICE_STATUS_DETAILS;
	var query = 'SELECT * FROM ' + tname + ' WHERE ';// + CUS_CRE_INFO_COL_KUNNR +' = "' + id +'"';
	for(var i = 0; i < statusCodeList.length; i++)
	{
		if(i != (statusCodeList.length-1))
			query = query + STATUS + "='" + statusCodeList[i] +"' OR " ;
		else if(i == (statusCodeList.length-1))
			query = query + STATUS + "='" + statusCodeList[i] +"'" ;
	}				
	console.log(query);
	selectTableValues(query,statuspinnerresult);
	hideWaitMe();
}//

function statuspinnerresult(results)
{			
	var len = results.rows.length;
	//alert(len);
	for (var i=0; i<len; i++)
	{		
		var rows = results.rows.item(i);
		statusSpinnerList[i]= rows["TXT30"];
		//add a item
		map[statusSpinnerList[i]] = statusCodeList[i];	
	}		
	 
	 for(var h=0;h<1830;h++){
		
	 }	 
	// statusui = elementSectionDialogWithValues("STATUSELECT",statusSpinnerList);
}//statusresult
//END OF GET STATUS DETAILS//*****//

function dynamicViewTable(values,fieldAttr,fieldLabel,viewId,objid)
{	
	//var sendqpflag1 = localStorage["SENDTOQP"];	
	//alert(sendqpflag1);
 $('#'+viewId +' thead').html("");
 $('#'+viewId +' tbody').html("");
 var len = values.rows.length;
 //alert(len);
 var fieldAttrLen = fieldAttr.rows.length;
 var fieldLabelLen = fieldLabel.rows.length;

 var label;
 var value;
 var headquery = "<tr>";
 
 for(var i = 0; i < fieldAttrLen; i++)
 {
  var fieldAttrRow = fieldAttr.rows.item(i);
  label = "";
  for(var j = 0; j < fieldLabelLen; j++)
  {
   var fieldLabelRow = fieldLabel.rows.item(j);
   if(fieldAttrRow.NAME == fieldLabelRow.NAME)
    label = fieldLabelRow.VALUE;
  }
  if(label!="")
	  headquery = headquery + "<th>" + label +"</th>";
  
 }
 
 headquery = headquery + "</tr>";
 
 $('#'+viewId +' thead').append(headquery); 
 
 
 for(var i = 0; i < len; i++)
 {
  var query = "<tr>";
  var productId = "";
  
  var rows = values.rows.item(i);
  var cls = "";
  
  if(objid != "null")
  {
	  productId = rows[objid];
	  objectIdList.push(rows["OBJECT_ID"]);
   query = "<tr id='" + rows[objid] + "'>";
  }
  
  var fieldValue = "";
  
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
    	   	
    	if(fieldAttrRow.NAME=="PRIORITY-DISPLAY" || fieldAttrRow.NAME=="STATUS-DISPLAY")
    		label = fieldAttrRow.NAME.split("-")[0];
    	else
    		label = fieldAttrRow.NAME.split("-")[1];
    	
    	if(label=="ZZKEYDATE" || label== "ZZETADATE"){
    		fieldValue ="";
    		fieldValue = rows[label];
		/*	var today = new Date();
			var dd = today.getDate();
			var mm = today.getMonth()+1; //January is 0!
			var yyyy = today.getFullYear();

			if(dd<10) {
			    dd='0'+dd
			} 

			if(mm<10) {
			    mm='0'+mm
			} 

			today = yyyy+'-'+mm+'-'+dd;*/
			fieldValue = converToSystemDateFormat(fieldValue);
		
			//var dateStr = converToSystemDateFormat(value);
			//value ="<span style = 'margin-left:25px;margin-top:15px;display:block;padding:.2em;'>"+elementDatePicker("STARTDATE",dateStr);+"</span>";				
			//fieldValue = elementDatePicker("ETADATE",today);	
    	}
     //alert(fieldAttrRow.DATATYPE);
    // alert(label);     
    	 fieldValue = "<span id='" + rows[objid] + "'>"+rows[label]+"</span>";         
     //fieldValue = "<span id='" + rows[objid] + "'>"+rows[label]+"</span>";
    // alert(fieldValue);
     if(fieldAttrRow.DATATYPE == "QUAN" || fieldAttrRow.DATATYPE == "CURR")
      cls = "right";
      
     
    }

	if(fieldAttrRow.VALUE == "DESCRIBED")
	{
		fieldValue = rows["NAME_ORG1"] + rows["NAME_ORG2"] +rows["ORT01"]+","+rows["PSTLZ"]+","+rows["LAND1"];
		
	}		
    if(fieldAttrRow.VALUE == "ICON")
    {
      
     if(rows[label] == "RELE" || rows[label] == "RLSD")
     {    	
      fieldValue = "<img src='../img/light_grey.png'/>";
     }
     else if(rows[label] == "ACPT"){
    	 fieldValue = "<img src='../img/light_green.png'/>";
     }
     else if(rows[label] == "LOW")
     {
      fieldValue = "<img src='../img/low_icon_l.png'/>";
     }
     else if(rows[label] == "HIGH")
     {
      fieldValue = "<img src='../img/vhigh_icon_l.png'/>";
     }
     else if(rows[label] == "NORMAL")
     {
      fieldValue = "<img src='../img/high_icon_l.png'/>";
     }
     else if(rows[label] == "O")
     {
      fieldValue = "<img src='../img/light_orange.png'/>";
     }
        
   }//
   /* if(fieldAttrRow.VALUE == "ICON")
    {
    	if(fieldAttrRow.NAME=="PRIORITY-DISPLAY"){
    		//alert(fieldValue);
    	}
    	if(fieldAttrRow.NAME=="STATUS-DISPLAY"){
    		//alert(fieldValue);
    		fieldValue = "<img src='../img/light_grey.png'/>";
    	}
    	
    }*/
   /*if(fieldAttrRow.VALUE == "ICON")
   {
     
    if(rows["STATUS"] == " ")
    {
     fieldValue = "<img src='../img/light_grey.png'/>";
    }
    else if(rows["STATUS"] == "B")
    {
     fieldValue = "<img src='../img/light_blue.png'/>";
    }
    else if(rows["STATUS"] == "G")
    {
     fieldValue = "<img src='../img/light_green.png'/>";
    }
    else if(rows["STATUS"] == "R")
    {
     fieldValue = "<img src='../img/light_red.png'/>";
    }
    else if(rows["STATUS"] == "O")
    {
     fieldValue = "<img src='../img/light_orange.png'/>";
    }*/
   }
   
   if(fieldValue!="" && fieldValue!="undefined")
	   query = query + "<td class='" + cls  +"'>" + fieldValue + "</td>";//store the entire row into a query variable
   //alert(query);
  }
   
  query = query +  "</tr>";//insert each query variable into a table row 
  replaceAll("undefined","",query);
  $('#'+viewId +' tbody').append(query); //insert table row to table body
 }
 localStorage["OBJECT_ID_LIST"] = JSON.stringify(objectIdList);
 /*var $table =  $("#"+viewId).tablesorter({
  widgets        : ['zebra', "filter"],
   widgetOptions : {filter_anyMatch : true,filter_columnFilters: false}
 });
 
 // $("#"+viewId) .tablesorterPager({container: $("#pager"), size: 1});
  
  $.tablesorter.filter.bindSearch( $table, $('.search') );*/
 
 
 
  $("#page").trigger("create");
     $("#contentTable").niceScroll();
     
     
     $('#'+viewId).dataTable({"info":false,"iDisplayLength": 10} );//used for pagination
  $(".dataTables_filter").hide();//de activating search bar from library
  $(".dataTables_length").hide();
  $(".search").keyup(function(){$(".dataTables_filter input").val($(".search").val());$( ".dataTables_filter input" ).keyup();});//search functionality for table
     
  $("#txtProductSearch").keyup(function(){$(".dataTables_filter input").val($("#txtProductSearch").val());$( ".dataTables_filter input" ).keyup();});
  $("#txtCustomerSearch").keyup(function(){$(".dataTables_filter input").val($("#txtCustomerSearch").val());$( ".dataTables_filter input" ).keyup();});
  
  $("#SotxtProductSearch").keyup(function(){$(".dataTables_filter input").val($("#SotxtProductSearch").val());$( ".dataTables_filter input" ).keyup();});
  $("#SotxtCustomerSearch").keyup(function(){$(".dataTables_filter input").val($("#SotxtCustomerSearch").val());$( ".dataTables_filter input" ).keyup();});
  $(".dataTables_filter input").attr("data-role", "none");//deactivating the automatic search functionality from the library
     
     
    /* $('#table1 tbody tr').click(function() {
         //alert($(this).attr('id'));
         
         var id = $(this).attr('id');
         
         window.localStorage.setItem("gallerypath", "SalesPro/Activities/"+id);
         window.location.href = 'UpdateActivity.html?id='+id;
       
    });*/
 
}



/*function dynamicViewTable(values, fieldAttr, fieldLabel, viewId, matId)
{
	if($("#"+viewId+" > tbody > tr").length != 0)
		$("#"+viewId).dataTable().fnDestroy();
		
	$('#'+viewId+" thead").html(""); 
	$('#'+viewId+" tbody").html(""); 
	var len = values.rows.length;
	var fieldAttrLen = fieldAttr.rows.length;
	console.log(fieldAttrLen);
	//alert(fieldAttrLen);
	var fieldLabelLen = fieldLabel.rows.length;	
	console.log(fieldLabelLen);
	//alert(fieldLabelLen);
	var label;
	var headquery = "<tr>";	
	for(var i = 0; i < fieldAttrLen; i++)
	{
		var fieldAttrRow = fieldAttr.rows.item(i);
		label = "";
		for(var j = 0; j < fieldLabelLen; j++)
		{
			var fieldLabelRow = fieldLabel.rows.item(j);
			if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				label = fieldLabelRow.VALUE;
		}		
		headquery = headquery + "<th>" + label +"</th>";		
	}
	
	headquery = headquery + "</tr>";	
	$('#'+viewId +' thead').append(headquery); 

	for(var i = 0; i < len; i++)
	{
		var query = "<tr>";
		var rows = values.rows.item(i);
		var cls = "";		
		var fieldValue = "";	
		var pId = "";
		var productId = "";

		if(matId != "null")
		{
			query = "<tr id='" + rows[matId] + "'>";
			pId = "class='" + rows[matId] + "'";
			productId = rows[matId];
		}else{
			pId = "class=''";
			productId = "";
		}
		
		for(var k = 0; k < fieldAttrLen; k++)
		{
			var fieldAttrRow = fieldAttr.rows.item(k);
			cls = "";
			fieldValue = "";				
			if(fieldAttrRow.VALUE == IMAGE_TAPPABLE)
			{
				var image = getImagesbyObjId(rows[MATNR]);					
				if(image.length == 0)
				{
					fieldValue = "<img height='40' width='40' class='proimg' src='../img/default_img.png' />";
				}
				else
				{						
					fieldValue = "<img height='40' width='40' class='proimg' src='"+ image[0] +"' />";						
				}					
			}		
			if(fieldAttrRow.VALUE == CHECKBOX)
			{
				fieldValue = '<input data-role="none"  id="'+ i +'" type="checkbox" value="'+productId+'" name="checkbox-product" class="css-checkbox"/><label for="'+ i +'" class="css-label"> </label>';
			}
			for(var j = 0; j < fieldLabelLen; j++)
			{
				var fieldLabelRow = fieldLabel.rows.item(j);
				if(fieldAttrRow.NAME == fieldLabelRow.NAME)
				{
					label = fieldAttrRow.NAME.split("-")[1];
					fieldValue = rows[label];
					if(fieldValue.length > 25){
						var strSep = fieldValue.substring(0, 25);
				        fieldValue = strSep + "...";
					}					
					if(fieldAttrRow.DATATYPE == "QUAN" || fieldAttrRow.DATATYPE == "CURR"){
						cls = "right";						
					}
					if(fieldAttrRow.DATATYPE == "DATS"){
						fieldValue = converToSystemDateFormat(fieldValue);
						cls = "left";						
					}
				}
			}		
			
			query = query + "<td style= 'color:#0000FF;'  class='" + cls  +"'>" + fieldValue + "</td>";
			if(fieldAttrRow.VALUE == CHECKBOX)
			{
				query = query + "<td style= 'color:#0000FF;'  class='" + cls  +"'>" + fieldValue + "</td>";
			}else{
				//query = query + "<td style= 'color:#0000FF;'  class='"  + cls  +"'><span id='details' "+pId+">" + fieldValue + "</span></td>";
			}			
		}			
		query = query +  "</tr>";
		$('#'+viewId +' tbody').append(query); 
	}	
	
	
	$("#"+viewId + ' tbody tr').click(function(event) {  						
        var id = $(this).attr('id');         
		alert("Id : "+id);		
		if(id != null && id != ""){
		alert("if");			
		}else{
		alert("else");
		}
    });
	
		
	$("#"+viewId).dataTable({"info":false,"iDisplayLength": 10} );
	$(".dataTables_filter").hide();
	$(".dataTables_length").hide();
	$("#txtProductSearch").keyup(function(){$(".dataTables_filter input").val($("#txtProductSearch").val());$( ".dataTables_filter input" ).keyup();});
                                              
	$(".dataTables_filter input").attr("data-role", "none");	
	$("#page").trigger("create");	
	
}*/

function dynamicViewList(values, fieldAttr, fieldLabel, viewId, objid)
{
	$('#'+viewId).html("");
	
	var len = values.rows.length;
	var ui = "";
	alert(len);
	for(var k = 0; k < len; k++)
	{
		var rows = values.rows.item(k);
		ui = ui + '<li><a href="#" id="' + rows[objid] + '">';
	var fieldAttrLen = fieldAttr.rows.length;
	var fieldLabelLen = fieldLabel.rows.length;
	
	
	
	var label;
	var value;
	var describedValue;
	
	var oColumnValue = 0;
	var oRowValue = 0;
	
	for(var i = 0; i < fieldAttrLen; i++)
	{
		label = "";
		value = "";
		describedValue = "";
		
		var cloumnLabel;
		var cloumnValue;
		var cloumnDescribedValue;
		
		var fieldAttrRow = fieldAttr.rows.item(i);
		
		var cRowValue = parseInt(fieldAttrRow.SEQNR.substring(2,4));
		var cColumnValue = parseInt(fieldAttrRow.SEQNR.substring(4,6));
		
		
		if( oRowValue != cRowValue && oRowValue != 0)
		{
			ui = ui + "</p>";
		}
		
		
		if( oRowValue != cRowValue)
		{
			ui = ui + '<p>';
			
		}
		
		
		
		if(len > 0)
		{
			var fieldValueRow = values.rows.item(k);
			cloumnValue = fieldAttrRow.NAME.split("-")[1];
		
			//alert(cloumnValue);
		
			if(fieldAttrRow.VALUE == "DISPLAY")
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
		
		
		ui = ui + "<span> " + label + " </span>" + "<span> " + value +" </span>";
		
		oColumnValue = cColumnValue;
		oRowValue = cRowValue;
		console.log(ui);
		
		if( i == (fieldAttrLen -1) )
		{
			ui = ui + "</p>";
		}
		
	}
	ui = ui + "</a></li>";
	}
	
	alert(ui);
	console.log(ui);
	$('#'+viewId).append(ui);
	$('#'+viewId).listview('refresh');
	
	 $("#page").trigger("create");
	 
	 
	 $(".search").keyup(function(){
		 $('#'+viewId + " li").show();
		 if($(".search").val().length == 0)
		 {
			 $('#'+viewId + " li").show();
		 }
		 else
		 {
			 $('#'+viewId + ' li:not(:containsIN('+ $(".search").val() +'))').hide(); 
			 
		 }
	});
	    
	 
	 $('#'+viewId +' li a').click(function() {
         alert($(this).attr('id'));
         
    });
}


var gridIds = [];
var defaultItemCount = 45;

function dynamicViewGrid(values,fieldAttr,fieldLabel,viewId,matId)
{


	var len = values.rows.length;
	var ui = "";
	
	for(var k = 0; k < len; k++)
	{
		var rows = values.rows.item(k);
		ui = ui;
		var fieldAttrLen = fieldAttr.rows.length;
		var fieldLabelLen = fieldLabel.rows.length;
		
		var label;
		var value;
		var imageValue = "";
		var checkboxValue = "";
		var priceValue = "";
		var describedValue;
	
		var oColumnValue = 0;
		var oRowValue = 0;
	
		for(var i = 0; i < fieldAttrLen; i++)
		{
			label = "";
			value = "";
		
			describedValue = "";
		
			var cloumnLabel;
			var cloumnValue;
			var cloumnDescribedValue;
		
			var fieldAttrRow = fieldAttr.rows.item(i);
		
			var cRowValue = parseInt(fieldAttrRow.SEQNR.substring(2,4));
			var cColumnValue = parseInt(fieldAttrRow.SEQNR.substring(4,6));
		
		
			if( oRowValue != cRowValue && oRowValue != 0)
			{
				ui = ui + "</p>";
			}
		
		
			if( oRowValue != cRowValue)
			{
				ui = ui + '<p>';
			
			}
		
			if(len > 0)
			{
				var fieldValueRow = values.rows.item(k);
				cloumnValue = fieldAttrRow.NAME.split("-")[1];
		
				//alert(cloumnValue);
			
				if(fieldAttrRow.VALUE == "DISPLAY")
					value = fieldValueRow[cloumnValue];
				//alert(value);
				//alert(fieldAttrRow.NAME);
				//alert(fieldLabelRow.NAME);
		
				if(fieldAttrRow.VALUE == "DESCRIBED")
				{
					cloumnDescribedValue = fieldAttrRow.TRGTNAME.split("-")[1];
			
					//alert(cloumnDescribedValue);
			
					describedValue = fieldValueRow[cloumnDescribedValue];
					//alert(describedValue);
				}
				//alert(fieldAttrRow.DATATYPE);
				if(fieldAttrRow.DATATYPE == "DATS")
				{
					//alert(fieldAttrRow.DATATYPE);
					value = converToSystemDateFormat(value);
				}
				if(fieldAttrRow.DATATYPE == "CURR" || fieldAttrRow.NAME.indexOf("KBETR") > -1)
				{
					priceValue = '<span class="price" > $'+ value +'</span>';
					value = "";
				}
			
				if(fieldAttrRow.VALUE == "ICON:STATUS-ITEM")
				{
					
					if(fieldValueRow["STATUS"] == "OPEN")
					{
						imageValue = "<div class='img'><img src='../img/light_grey.png'/></div>";
					}
					else if(fieldValueRow["STATUS"] == "INPR")
					{
						imageValue = "<div class='img'><img src='../img/light_blue.png'/></div>";
					}
					else if(fieldValueRow["STATUS"] == "COMP")
					{
						imageValue = "<div class='img'><img src='../img/light_green.png'/></div>";
					}
					else if(fieldValueRow["STATUS"] == "CANC")
					{
						imageValue = "<div class='img'><img src='../img/light_red.png'/></div>";
					}
					else if(fieldValueRow["STATUS"] == "LEAD")
					{
						imageValue = "<div class='img'><img src='../img/light_orange.png'/></div>";
					}
				}
				
				if(fieldAttrRow.VALUE == IMAGE_TAPPABLE)
				{
					var image = getImagesbyObjId(rows[MATNR]);					
					if(image.length == 0)
					{
						imageValue = "<img class='img' src='../img/default_img.png' />";
					}
					else
					{						
						imageValue = "<img class='img' src='"+ image[0] +"' />";						
					}					
				}		
				if(fieldAttrRow.VALUE == CHECKBOX)
				{
					checkboxValue = '<input class="chk" data-role="none"  id="'+ k +'" type="checkbox" value="'+rows[matId]+'" name="checkbox-product" /><label for="'+ k +'" class="css-label"> </label>';
				}
				
				
				
			}
		
			for(var j = 0; j < fieldLabelLen; j++)
			{
				var fieldLabelRow = fieldLabel.rows.item(j);
			
				if(fieldAttrRow.NAME == fieldLabelRow.NAME)
					label = fieldLabelRow.VALUE;
			}
		
		
			ui = ui + "<span> " + label + " </span>" + "<span> " + value +" </span>";
		
			oColumnValue = cColumnValue;
			oRowValue = cRowValue;
			//console.log(ui);
		
			if( i == (fieldAttrLen -1) )
			{
				ui = ui + "</p>" ;
			}
		
		}
		ui =  '<div class="thumb-parent" id="grid-child-'+k+'"><div class="thumb" id="'+rows[matId]+'">' + priceValue + checkboxValue + imageValue + "<div class='text'>" + ui +"</div> </a></div>";
		console.log(ui);
		$('#'+viewId).append(ui);
		ui = "";
		gridIds.push("grid-child-"+k);
		// imageValue = "";
	}
	$('#'+viewId).append("<div class='clear'></div><div id='gridPages'></div>");
	//alert(ui);
	console.log(ui);
	$("#page").trigger("create");
	
	
	$('#'+viewId+' .thumb').click(function() {  
        var id = $(this).attr('id');         
		alert("Id : "+id);
		/*var selectedContacts = []; 
		$("input:checkbox[name=checkbox-product]:checked").each(function()
		{   			
			alert($(this).val());
			selectedContacts.push($(this).val());
		});*/
		if(id != null && id != ""){
			window.location.href = 'ProductDetails.html?pId='+id;
		}else{
		}
    });
	
	
	$(".search").keyup(function(){
		gridIds = [];
		
		 $("#"+viewId + ".thumb-parent").show();
		 if($(".search").val().length == 0)
		 {
			$('#'+viewId + " .thumb-parent").show();
			 
			$('#'+viewId + " .thumb-parent").each(function() {
				    //alert( this.id );
					console.log( this.id );
				    gridIds.push(this.id);
			});
			alert(gridIds.length+ "len min");
			if(gridIds.length != 0)
			{
				$('#gridPages' ).twbsPagination("destroy");
				gridPagination(viewId,defaultItemCount,1);
			}
			
			 
		 }
		 else
		 {
			 $('#'+viewId + ' .thumb-parent:not(:containsIN('+ $(".search").val() +'))').hide();
			 $('#'+viewId + ' .thumb-parent:containsIN('+ $(".search").val() +')').each(function() {
				    //alert( this.id );
				 console.log( this.id );
				    gridIds.push(this.id);
			});
			 alert(gridIds.length + "len max");
			if(gridIds.length != 0)
			{
					$('#gridPages' ).twbsPagination("destroy");
					gridPagination(viewId,defaultItemCount,1);
			}
			 
		 }
		 
		 
		 
	});
	
	gridPagination(viewId,defaultItemCount,1);
	
}

function converToStandardDateFormat(fieldValue)
{
//alert(fieldValue);
var date = fieldValue.split("/");//2013-11-06

var format = dateFormat.split("/");

var formattedDate = "";

var year;
var month;
var day;

for(var i = 0; i < format.length; i++)
{
 if(format[i].indexOf("y") != -1)
 {
  year = date[i] ;
 }
 if(format[i].indexOf("m") != -1)
 {
  month = date[i];
 }
 if(format[i].indexOf("d") != -1)
 {
  day = date[i] ;
 }

}

formattedDate = year + "-" + month + "-" + day;

return formattedDate;
}

function gridPagination(viewId,itemCount,pageNumber)
{
	alert(viewId);
	//alert(gridIds.length);
	var totalPage = gridIds.length / itemCount;
	
	if(gridIds.length % itemCount != 0)
		totalPage = totalPage + 1;
	
	
	 $('#'+viewId + " .thumb-parent").hide();
	 
	 var startP = ((pageNumber -1) * itemCount);
	 var endP = ((pageNumber -1) * itemCount) + itemCount;
	 
	 
	for(var i = startP; i < endP; i++)
	{
		console.log(gridIds[i] + "selected ids");
		$('#'+gridIds[i] ).show();
	}
	
	
	$('#gridPages' ).twbsPagination({
        totalPages: totalPage,
        visiblePages: totalPage,
        onPageClick: function (event, page) {
            //alert('Page ' + page);
            gridPagination(viewId,itemCount,page);
        }
    });
	
	//alert($(".pagination").html());
	console.log($(".pagination").html());
}//

function elementSectionDialogWithValues(id,values)
{
	//alert(id);
	var len = values.length;
	//alert(len);
	
	//var timeZoneSelectedList = JSON.parse(localStorage["TIMEZONE_SELECTED_LIST"]);
	
	if(id == "TIMEZONESELECT"){
		var ui = "<select id='" + id +"' style='height: 30px;'>";
		
		ui = ui + "<optgroup label='Current Timezone'>";
		ui = ui + '<option value="' + currentTimeZone + '">' + currentTimeZone + '</option>';
		ui = ui + "</optgroup>";
		
		/*ui = ui + "<optgroup label='Selected Timezone'>";
		
		for(var i = 0; i < timeZoneSelectedList.length; i++)
		{
			ui = ui + '<option value="' + timeZoneSelectedList[i] + '">' + timeZoneSelectedList[i] + '</option>';
		}
		
		ui = ui + "</optgroup>";*/
		
		ui = ui + "<optgroup label='Device Timezone'>";
		
		for(var i = 0; i < len; i++)
		{
			//alert(values[i]);
			ui = ui + '<option value="' + values[i] + '">' + values[i] + '</option>';
		}
		
		ui = ui + "</optgroup>";
		ui = ui + '</select>';
		
	}else if(id == "STATUSELECT"){
		var ui = "<select id='" + id +"'>";										
		for(var i = 0; i < len; i++)
		{
			//alert(values[i]);
			ui = ui + '<option value="' + values[i] + '">' + values[i] + '</option>';
		}				
		ui = ui + '</select>';
	}
	
	
	return ui;
	
}//



function dynamicViewForPopupCategory(viewId)
{
	if(POPUPSHOW){
		$("#popupPanel").popup( "open");
		$("#piccat").niceScroll();
	}else{
		var values = getCategoryList(); 
		$('#'+viewId+" tbody").html(""); 
		var len = values.length;
		var val = "";
		if(len > 0){
			for(var i = 0; i < len; i++){		
				var query = "<tr>";
				var catId = values[i].categoryid;
				if(catId != "null" && catId != "")
				{
					query = "<tr style= 'width:100%;' class='category' id='" + catId + "'>";
				}
				query = query + '<td style= "color:#0000FF;" class="left"><input data-role="none" value="'+catId+'" id="checkbox-'+ catId +'" type="checkbox" name="checkbox-category" class="css-checkbox"/><label for="checkbox-'+ catId +'" class="css-label"> </label>';
				query = query + " " + values[i].category + "</td>";
				query = query +  "</tr>";
				$('#'+viewId +' tbody').append(query); 	
				val	 = val + query;
			} 
		}else{
			alert('Category Not Available!');
		}
		console.log(val);
		$("#"+viewId).dataTable({"info":false,"iDisplayLength": 5} );
		$(".dataTables_filter").hide();
		$(".dataTables_length").hide();
		$("#txtCategorySearch").keyup(function(){$("#popupPanel .dataTables_filter input").val($("#txtCategorySearch").val());$( "#popupPanel .dataTables_filter input" ).keyup();});
		$(".dataTables_filter input").attr("data-role", "none");
		$("#piccat").trigger("create");
		$("#popupPanel").popup( "open");
		$("#piccat").niceScroll();
		
		$('#popupPanel').on({
		  popupbeforeposition: function() {
			var maxHeight = $(window).height() - 50
			$('#popupPanel').css('max-height', maxHeight + 'px')
		  }
		})
		$("#txtCategorySearch").keyup();
		$("#popupPanel").css('overflow-y', 'scroll'); 	
		/*$('#cattable tr').click(function() {  
			var id = $(this).attr('id');      
			alert("ID "+id);
			 //window.location.href = 'ProductDetails.html?id='+id;
		});*/
		POPUPSHOW = true;
	}
}

function showSalesOrderSelectedNames(){
	/*var count = $("#cbFieldSet input:checked").length;
	var str = '';
	for(var i = 0; i < count; i++){
		if(i == (count-1) ){
			str += "'"+$("#cbFieldSet input:checked")[i].categoryid +"'";
		}else{			
			str += "'"+$("#cbFieldSet input:checked")[i].categoryid +"', ";
		}
	}
	CAT_STR = str;
	//alert("You selected----"+str);	*/
	
	/*var count = $("#category input:checked").length;
	alert("You selected count----"+count);
	var status = $('input[type="checkbox"]').filter('.custom').map(function(){
    var id = $(this).attr('id'); 
	var str = '';
    if($(this).is(':checked'))
		//str += id;
		alert("You selected----"+id);
	});
	
	$( "#popupPanel" ).popup( "close");*/
	//checkProductListWithCategorydatabase(str);
	
	var selectedContacts = []; 
    $("input:checkbox[name=checkbox-product]:checked").each(function()
    {       
		selectedContacts.push($(this).val());
    });	
	var str = '';
	var length = selectedContacts.length;
	alert(length);
	for(var i = 0; i < length; i++){
		if(i == (length-1) ){
			str += "'"+selectedContacts[i]+"'";
		}else{			
			str += "'"+selectedContacts[i]+"', ";
			alert(str);
		}
	}
	//alert("You selected----"+str);
	CAT_STR = str;
	
	/*if(CAT_STR != null && CAT_STR != ""){
		$("#catseltext").text("Selected Categories "+CAT_STR);
	}else{
		$("#catseltext").hide();
	}*/		
}

function showSelectedNames(){
	/*var count = $("#cbFieldSet input:checked").length;
	var str = '';
	for(var i = 0; i < count; i++){
		if(i == (count-1) ){
			str += "'"+$("#cbFieldSet input:checked")[i].categoryid +"'";
		}else{			
			str += "'"+$("#cbFieldSet input:checked")[i].categoryid +"', ";
		}
	}
	CAT_STR = str;
	//alert("You selected----"+str);	*/
	
	/*var count = $("#category input:checked").length;
	alert("You selected count----"+count);
	var status = $('input[type="checkbox"]').filter('.custom').map(function(){
    var id = $(this).attr('id'); 
	var str = '';
    if($(this).is(':checked'))
		//str += id;
		alert("You selected----"+id);
	});
	
	$( "#popupPanel" ).popup( "close");*/
	//checkProductListWithCategorydatabase(str);
	
	var selectedContacts = []; 
    $("input:checkbox[name=checkbox-category]:checked").each(function()
    {       
		selectedContacts.push($(this).val());
    });
	$( "#popupPanel" ).popup( "close");
	var str = '';
	var length = selectedContacts.length;
	for(var i = 0; i < length; i++){
		if(i == (length-1) ){
			str += "'"+selectedContacts[i]+"'";
		}else{			
			str += "'"+selectedContacts[i]+"', ";
		}
	}
	//alert("You selected----"+str);
	CAT_STR = str;
	
	/*if(CAT_STR != null && CAT_STR != ""){
		$("#catseltext").text("Selected Categories "+CAT_STR);
	}else{
		$("#catseltext").hide();
	}*/
	checkProductListWithCategorydatabase();
	
}

function createCheckboxes(){
	dynamicViewForPopupCategory("cattable");
}

function createCheckboxescat(){
	var array = getCategoryList();
	$("#piccat").empty();
	var val = "";	
	$("#piccat").append('<fieldset id="cbFieldSet" data-role="controlgroup">');
	val += '<fieldset id="cbFieldSet" data-role="controlgroup">';
	var length = array.length;
	if(length > 0){
		for(var i=0;i<length;i++){
			$("#cbFieldSet").append('<label for="cb-'+i+'">'+array[i].category+'</label><input type="checkbox" name="cb-'+i+'" id="cb-'+i+'" value="'+array[i].categoryid+'"/>');
			val += '<label for="cb-'+i+'">'+array[i].category+'</label><input type="checkbox" name="cb-'+i+'" id="cb-'+i+'" value="'+array[i].categoryid+'"/>';
		} 
		$("#piccat").append('</fieldset>');
		val += '</fieldset>';
		console.log(val);
		$("#piccat").trigger("create");
		$("#popupPanel").popup( "open");
		$("#piccat").niceScroll();
		
		$('#popupPanel').on({
		  popupbeforeposition: function() {
			var maxHeight = $(window).height() - 50
			$('#popupPanel').css('max-height', maxHeight + 'px')
			var maxHeight1 = $(window).height() - 300
			$('.ui-controlgroup-controls').css('max-height', maxHeight1 + 'px')
		  }
		})
		
		$("#popupPanel").css('overflow-y', 'scroll'); 
	}else{
		alert('Category Not Available!');
	}
}

function elementDatePicker(id,value)
{
	//alert(value);
	var ui = '<input type="text" id="' + id + '" class="text-input datepicker" value="' + value +'"  data-role="none"/>';
	
	return ui;
}

function converToSystemDateFormat(fieldValue)
{
	if(fieldValue=="")
		return fieldValue;
	var date = fieldValue.split("-");//2013-11-06
	//alert(dateFormat);
	var format = dateFormat.split("/");
	var formattedDate = "";

	for(var i = 0; i < format.length; i++)
	{
		if(format[i].indexOf("y") != -1)
		{
			formattedDate = formattedDate + date[0] + "/";
		}
		if(format[i].indexOf("M") != -1 || format[i].indexOf("m") != -1)
		{
			formattedDate = formattedDate + date[1] + "/";
		}
		if(format[i].indexOf("d") != -1)
		{
			formattedDate = formattedDate + date[2] + "/";
		}
	}
	formattedDate = formattedDate.substring(0, formattedDate.length - 1);
	return formattedDate;
}
/*function converToSystemDateFormat(fieldValue)
{
	if(fieldValue=="")
		return fieldValue;
	var date = fieldValue.split("-");//2013-11-06
	var format = dateFormat.split("/");
	var formattedDate = "";

	for(var i = 0; i < format.length; i++)
	{
		if(format[i].indexOf("y") != -1)
		{
			formattedDate = formattedDate + date[0] + "/";
		}
		if(format[i].indexOf("M") != -1 || format[i].indexOf("m") != -1)
		{
			formattedDate = formattedDate + date[1] + "/";
		}
		if(format[i].indexOf("d") != -1)
		{
			formattedDate = formattedDate + date[2] + "/";
		}
	}
	formattedDate = formattedDate.substring(0, formattedDate.length - 1);
	return formattedDate;
}*/