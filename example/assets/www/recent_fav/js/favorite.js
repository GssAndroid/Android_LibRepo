function getSelectedFavorites()
{
	var selectedFavorites = []; 
	$("input:checkbox[name=favorites]:checked").each(function()
	{
			    // add $(this).val() to your array
		selectedFavorites.push($(this).val());
	});
	
	
	
	if(selectedFavorites.length != 0)
	{
		//alert(selectedFavorites);
		var query = "DELETE FROM TABLE_FAVORITE_ACTIVITIES WHERE SCREEN_URL = '" + selectedFavorites[0] + "'";
		delete_data(query);
		app.initialize();
	}
	else
		alert('Favorite Id is Empty');


}



function getFavoriteActivities()
{
	var query = "SELECT * FROM TABLE_FAVORITE_ACTIVITIES GROUP BY SCREEN_URL ORDER BY rowid  DESC";
	selectTableValues(query,checkFavoriteActivitiesdatabase);
}


function checkFavoriteActivitiesdatabase(results)
{  	
	$("#listfavoriteActivity").text("");
    var len = results.rows.length;
    //alert(len);
    for(var i = 0; i < len; i++)
    {
    	var recentActivityrow = results.rows.item(i);
    	
    	var element = '<li><a rel="external" href="'+recentActivityrow["SCREEN_URL"]+'">';
        element = element +  '<p>'+recentActivityrow["SCREEN_TITLE"]+'</p>';
		element = element + '<h3>'+recentActivityrow["SCREEN_TIME"]+'</h3>';
		
		element = element + '</a></li>';
		$("#listfavoriteActivity").append(element)
    }
    $('#listfavoriteActivity').listview('refresh');
    $("#page").trigger("create");
}


