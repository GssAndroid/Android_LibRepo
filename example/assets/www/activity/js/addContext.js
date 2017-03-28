var ACTIVITYCATEGORYRESULTS;
var ACTIVITYSTATUSRESULTS;


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
	for (var i=0; i<len; i++)
	{
		
		var rows = results.rows.item(i);
		console.log("LABEL RESULT:"  + rows.NAME);
		   
	}
	fieldLabel = results;
	
	
	addListView(fieldAttr,fieldLabel,"");
}


function getActivityTypeCategory(results)
{
	ACTIVITYCATEGORYRESULTS = results;
}

function getActivityStatusCategory(results)
{
	ACTIVITYSTATUSRESULTS = results;
}


function getScreenNameAddActivity(results)
{
	var len = results.rows.length;
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		$("#screenTitle").text(fieldLabelRow.VALUE);
	}
}

function getFieldHintAddActivity(results)
{
	var len = results.rows.length;
	
	//alert(len);
	
	for(var i = 0; i < len; i++)
	{
		var fieldLabelRow = results.rows.item(i);
		//alert(fieldLabelRow.VALUE);
		$(".search").attr("placeholder", fieldLabelRow.VALUE);
		//alert(fieldLabelRow.VALUE);
	}
}



function checkContextActivitydatabase()
{  	
    
    db.transaction(getActivityCategoryOfflineValues, errorCB);
    db.transaction(getActivityStatusOfflineValues, errorCB);
    db.transaction(getContextActivityOfflineValues, errorCB);
}

function getContextActivityOfflineValues(tx) { 

	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 8', [], showqueryContextActivitySuccess, errorCB);
}

function getActivityCategoryOfflineValues(tx) { 

	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 10', [], showqueryCategoryActivitySuccess, errorCB);
}

function getActivityStatusOfflineValues(tx) { 

	tx.executeSql('SELECT * FROM FUNCTIONMASTER WHERE id = 11', [], showqueryStatusActivitySuccess, errorCB);
}

function showqueryContextActivitySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);

		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "SCRN-TITLE" AND CNTXT2 = "ADD-ACTIVITY-W"';
		selectTableValues(query,getScreenNameAddActivity);
		
		
		query = 'SELECT * FROM ' + tname +' WHERE CNTXT4 = "FIELD-ATTR" AND CNTXT2 = "ADD-ACTIVITY-W-MAIN-BLOCK" ORDER BY SEQNR ASC';
		selectTableValues(query,getFieldAttr);
		
		var query1 = 'SELECT * FROM ' + tname +' WHERE CNTXT2 = "ADD-ACTIVITY-W-MAIN-BLOCK" AND (CNTXT4 = "FIELD-LABE" OR CNTXT4 = "FIELD-HINT") ';
		selectTableValues(query1,getFieldLabel);
		
	}
}

function showqueryCategoryActivitySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);

		query = 'SELECT * FROM ' + tname ;
		selectTableValues(query,getActivityTypeCategory);
		
	}
}

function showqueryStatusActivitySuccess(tx, results) 
{
  
	var len = results.rows.length;
	var tname = results.rows.item(0).tname;
	if(tname != "")
	{
		var query = 'SELECT * FROM ' + tname ;
		console.log(query);
		selectTableValues(query,checkValues);

		query = 'SELECT * FROM ' + tname ;
		selectTableValues(query,getActivityStatusCategory);
		
	}
}


function checkValues(results)
{
	var len = results.rows.length;
	console.log("Context Length ::" + len);
}


