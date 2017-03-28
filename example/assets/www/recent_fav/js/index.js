function getRecentActivities()
{
	var query = "SELECT *,count(SCREEN_URL) as SCREEN_COUNT FROM TABLE_RECENT_ACTIVITIES GROUP BY SCREEN_URL ORDER BY rowid  DESC";
	selectTableValues(query,checkRecentActivitiesdatabase);
}


function checkRecentActivitiesdatabase(results)
{  	
	$("#listrecentActivity").text("");
    var len = results.rows.length;
    //alert(len);
    for(var i = 0; i < len; i++)
    {
    	var recentActivityrow = results.rows.item(i);
    	
    	var element = '<li><a rel="external" href="'+recentActivityrow["SCREEN_URL"]+'">';
        element = element +  '<p>'+recentActivityrow["SCREEN_TITLE"]+'</p>';
		element = element + '<h3>'+recentActivityrow["SCREEN_TIME"]+'</h3>';
		element = element + '<span class="ui-li-count">'+recentActivityrow["SCREEN_COUNT"]+'</span>';
		element = element + '</a></li>';
		$("#listrecentActivity").append(element)
    }
    $('#listrecentActivity').listview('refresh');
    $("#page").trigger("create");
}


