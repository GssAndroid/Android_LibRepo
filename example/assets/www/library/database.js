var db; 

/*//WEBSERVICE DETAILS
var WEBSERVICE_CONTEXT = "SERVISOR-CONTEXT-DATA-GET";
var WEBSERVICE_SERVICE_ASSIGNED = "SERVICE-ASSIGNED-TO-EMPLOYEE";
var WEBSERVICE_EMPLOYEE_LOCATION = "SERVICE-EMPLOYEE-LOCATION";
var WEBSERVICE_CUSTOMER_SEARCH = "ACCOUNT-SEARCH";
var WEBSERVICE_CALENDAR_EVENTS = "SERVICE-EMPLOYEE-ASSIGNMENTS";
var WEBSERVICE_CUSTOMER_CREDIT = "CUSTOMER-CREDIT-INFORMATION-GET";


//TABLE DETAILS
var TABLE_EMPLOYEE_DETAILS = "SERVISOR_CONTEXT_DATA_GET_ZGSXCAST_EMPLY01A";
var TABLE_STATUS = "SERVISOR_CONTEXT_DATA_GET_ZGSXCAST_STTS10";
var TABLE_EMPLOYEE_DETAILS1 = "SERVICE_EMPLOYEE_LOCATION_ZGSXCAST_EMPLY01A";
var TABLE_EMPLOYEE_MOBILE_LOCATION = "SERVICE_EMPLOYEE_LOCATION_ZGSSMWST_MOBILELCTN01";
var TABLE_CUSTOMER = "ACCOUNT_SEARCH_ZGSEVDST_CSTMR10";
//var TABLE_CUSTOMER_CREDIT = "CUSTOMER_CREDIT_INFORMATION_GET_";
var TABLE_SERVICE_ASSIGNED_EMPLOYEE = "SERVICE_ASSIGNED_TO_EMPLOYEE_ZGSXSMST_EMPLYASSGNMNT10";
var TABLE_CALENDAR_EVENTS = "SERVICE_EMPLOYEE_ASSIGNMENTS_ZGSXSMST_EMPLYASSGNMNT10";*/

//SERVICE TASK DETAILS
var WEBSERVICE_CONTEXT = "SERVICE-DOX-CONTEXT-DATA-GET";
var WEBSERVICE_SERVICE_TASK = "SERVICE-DOX-FOR-EMPLY-BP-GET";
var TABLE_SERVICE_TASK_EMPLOYEE_DETAILS="SERVICE_DOX_FOR_EMPLY_BP_GET_ZGSXSMST_SRVCDCMNT10";
var TABLE_SERVICE_CONTEXT_EMPLOYEE_DETAILS= "SERVICE_DOX_CONTEXT_DATA_GET_ZGSSMWST_UICNFG01";
var TABLE_SERVICE_STATUSFLOW_DETAILS= "SERVICE_DOX_CONTEXT_DATA_GET_ZGSXCAST_STTSFLOW10";
var TABLE_SERVICE_STATUS_DETAILS= "SERVICE_DOX_CONTEXT_DATA_GET_ZGSXCAST_STTS10";

var TABLE_SERVICE_ATTCHMNT_EMPLOYEE_DETAILS= "SERVICE_DOX_FOR_EMPLY_BP_GET_ZGSXCAST_ATTCHMNT01";

function createDatabase() 
{
        db = window.openDatabase("servisor", "1.0", "SERVISOR", 1000000);
        //db.transaction(populateDB, errorCB, successCB);
}

//Function For Creating TableMaster

//Funtion For Updating Table Values
function updateDynamicTable(tableName,updateColumns,whereColumns)
{
	
	
	db.transaction(function(transaction){ updateTable(transaction, tableName,updateColumns,whereColumns);}, errorCB, successCB);
}


function updateTable(tx,tableName,updateColumns,whereColumns)
{
	
	var query = 'UPDATE ' + tableName + ' SET ';
	
	for(var i = 0; i < updateColumns.length; i++ )
	{
		query = query +  updateColumns[i].column + '="' + updateColumns[i].value + '",';
	}
	
	query = query.substring(0, query.length - 1);
	
	query = query + ' WHERE '+ whereColumns.column + '= ' + whereColumns.value + '';
	
	console.log(query);

	tx.executeSql(query);
	
	
}

//Funtion For Creating Dynamic Tables
function createDynamicTable(dynamicColumnValues)
{
	//$.mobile.showPageLoadingMsg("b", "Compiling Data....");
	db.transaction(function(transaction){ populateTable(transaction, dynamicColumnValues);}, errorCB, successCB);
}

//Funtion For Creating Dynamic Tables
function populateTable(tx,dynamicColumnValues) {
    //tx.executeSql('DROP TABLE IF EXISTS '+ dynamicColumnValues[1]);
    
    var query = 'CREATE TABLE IF NOT EXISTS ' + dynamicColumnValues[1] +' (';
    
    for(var i = 3; i < dynamicColumnValues.length; i++)
    {
    	query = query + dynamicColumnValues[i];
    	
    	if(i != (dynamicColumnValues.length - 1 ))
    		query = query + ',';
    }
    query = query + ')';
    
    console.log(query);
    
    tx.executeSql( query );
}

//Funtion For Drop Dynamic Tables
function dropDynamicTable(tableName)
{
	db.transaction(function(transaction){ dropTable(transaction, tableName);}, errorCB, successCB);
}

function dropTable(tx,tableName) {
    tx.executeSql('DROP TABLE IF EXISTS '+ tableName);
    //tx.executeSql( query );
}




//Funtion For truncate Dynamic Tables
function truncateDynamicTable(tableName)
{
	db.transaction(function(transaction){ truncateTable(transaction, tableName);}, errorCB, successCB);
}

function truncateTable(tx,tableName) {
    tx.executeSql('DELETE FROM '+ tableName);
    //tx.executeSql( query );
}

//Funtion For inserting values in Dynamic Tables
function insertTableValues(dynamicTableValues)
{
	db.transaction(function(transaction){ insertvalues(transaction, dynamicTableValues);}, errorCB, successCB);
}

//Funtion inserting values in Dynamic Tables
function insertvalues(tx,dynamicTableValues) {
   
	var query = 'INSERT INTO ' + dynamicTableValues[0] +' VALUES(';
    
    for(var i = 1; i < dynamicTableValues.length; i++)
    {
    	//query = query + "'" + dynamicTableValues[i] + "'" ;
    	query = query + '"' + dynamicTableValues[i] + '"' ;    
    	
    	if(i != (dynamicTableValues.length - 1 ))
    		query = query + ',';
    }
    query = query + ')';
    
    console.log(query);
    
    tx.executeSql( query );
}


function selectTableValues(query,callBackFunction)
{
	db.transaction(function(transaction){ queryDB(transaction, query, callBackFunction);}, errorCB);
}

function queryDB(tx,query,callBackFunction) {
    tx.executeSql(query, [], function(transaction,results){ querySuccess(transaction, results, callBackFunction);} , errorCB);
}

function querySuccess(tx, results,callBackFunction) {
    // this will be empty since no rows were inserted.
    //console.log("Insert ID = " + results.insertId);
    // this will be 0 since it is a select statement
   // console.log("Rows Affected = " + results.rowAffected);
    // the number of rows returned by the select statement
    console.log("Insert ID = " + results.rows.length);
    callBackFunction(results);
}


function delete_data(query) {
    db.transaction(function (tx){tx.executeSql(query);}, errorCB);
}




function errorCB(err) {
  console.log("Error processing SQL: "+JSON.stringify(err));
}

function successCB() {
	console.log("success!");
	
}