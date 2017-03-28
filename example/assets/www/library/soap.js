var webServiceURL = 'http://75.99.152.10:8050/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/110/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00';

//var webServiceURL = 'http://gsswd.globalsoftsolutions.net:8051/sap/bc/srt/rfc/sap/z_gssmwfm_hndl_evntrqst00/111/z_gssmwfm_hndl_evntrqst00/z_gssmwfm_hndl_evntrqst00';


var disgnostics_response_parser = "";
function soapRequest(endSaveProduct,items) {
  		// build SOAP request

		
	disgnostics_response_parser = disgnostics_response_parser + "<p>EVENT:" + items[2].split("[.]")[1] + "</br></p>" ;
	disgnostics_response_parser = disgnostics_response_parser + "<p>API-BEGIN-TIME DEVICE:" + diagnosicsDate() + "</br></p>";

	
	onCompleteteFunction = endSaveProduct;
	
	//$.mobile.showPageLoadingMsg("b", "Contacting Server....");
	
	showWaitMe("Contacting Server");
  		
  		var soapMessage = soapMessageBuilder(items);
  		console.log("webServiceURL ::" +webServiceURL);
  		console.log("soapMessage::" +soapMessage);
  		//alert(soapMessage);
  		
  		$.ajax({
    	url: webServiceURL,
    	type: "POST",
    	dataType: "xml",
    	timeout: 10000000,
    	data: soapMessage,
    	contentType: "text/xml; charset=\"utf-8\"",
    	complete: onComplete,
    	success:function(Result, textStatus, XMLHttpRequest) { 
    	//alert(Result);
            //navigator.notification.activityStop();
    		 //$.mobile.loading("hide");
             //$("body").removeClass('ui-disabled');
        },
    	error: function(XMLHttpRequest, textStatus, errorThrown) { 
            //alert("Status: " + textStatus); alert("Error: " + errorThrown); 
            //navigator.notification.activityStop();
    		 $.mobile.loading("hide");
             //$("body").removeClass('ui-disabled');
        }    
  		});
}


function soapMessageBuilder(items)
{
	
	var soapMessage = '<v:Envelope xmlns:i="http://www.w3.org/2001/XMLSchema-instance" xmlns:d="http://www.w3.org/2001/XMLSchema" xmlns:c="http://schemas.xmlsoap.org/soap/encoding/" xmlns:v="http://schemas.xmlsoap.org/soap/envelope/"><v:Header /><v:Body><n0:ZGssmwfmHndlEvntrqst00 id="o0" c:root="1" xmlns:n0="urn:sap-com:document:sap:soap:functions:mc-style"><DpistInpt i:type="c:Array" c:arrayType="d:anyType[4]">'+
	message(items) + '</DpistInpt></n0:ZGssmwfmHndlEvntrqst00></v:Body></v:Envelope>';
	return soapMessage;
}

function message(items)
{
var message = "";

if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
	items[0] = items[0]+":MODE:D"
        	
for(var i = 0; i < items.length; i++)
{
	message = message + '<item i:type="d:anyType"><Cdata i:type="d:string">' + 	items[i] + '</Cdata></item>';
}
return message;	
}

function onComplete(xmlHttpRequest, status)
{
    var i = 0;
    
    //alert(xmlHttpRequest.responseText);
    
    $.mobile.hidePageLoadingMsg();
    hideWaitMe();
    showWaitMe("Compiling Data");
    //$.mobile.showPageLoadingMsg("b", "Compiling Data....");
    
    $(xmlHttpRequest.responseXML)
    .find('DpostMssg').find('item')
    .each(function()
    {
          //alert($(this).find('Type').text() );
         
        if(($(this).find('Type').text() == "E" || $(this).find('Type').text() == "A" || $(this).find('Type').text() == "X") && i == 0)
        {
          alert($(this).find('Message').text());
          i++;
          hideWaitMe();
          //return;
        }
    });
    
    
   
   
    
    $(xmlHttpRequest.responseXML)
    .find('DpostOtpt').find('item')
    .each(function()
    {
          //alert($(this).find('Type').text() );
          
          var name = $(this).find('Cdata').text();
          
          if(window.localStorage.getItem("diagnostics") != null && window.localStorage.getItem("diagnostics") != "" && window.localStorage.getItem("diagnostics") == "yes")
          {
          	var result = name.split("[.]");
          	if(result[0] == "ZGSSMWST_DIAGNOSYSINFO01")
          	disgnostics_response_parser = disgnostics_response_parser + "<p>" + result[1] + "</br></p>" 
          }
          
        
    });
    disgnostics_response_parser = disgnostics_response_parser + "<p>API-END-TIME DEVICE:" + diagnosicsDate() + "</br></p>";
    if(i == 0)
    {
    	
    	disgnostics_response_parser = disgnostics_response_parser + "<p>START DATA PARSING DEVICE:" + diagnosicsDate() + "</br></p>" ;
    	
    	var ii = 0;
    	var webservice_name;
    	var table_name = "";
    	$(xmlHttpRequest.responseXML)
        .find('item')
        .each(function()
     	{
       		var name = $(this).find('Cdata').text();
       		
       		if(ii == 1)
    		{
    			var result = name.split("[.]");
    			
    			webservice_name = result[1];
    			window.localStorage.setItem(webservice_name,webservice_name);
    		}
       		else if(ii>=2)
       		{
       			//alert(name);
       			var result = name.split("[.]");
       			
       			if(result[0] == "DATA-TYPE")
       			{
       				result[1] = replaceAll("-","_",webservice_name) + "_" + result[1];
       				createDynamicTable(result);
       			}
       			else
       			{
       				if(table_name != result[0])
       				{
       					//alert(table_name+result[0]);
       					//truncateDynamicTable("result[0]");
       					truncateDynamicTable(replaceAll("-","_",webservice_name) + "_" + result[0]);
       				}
       				table_name = result[0];
       				/*else
       				{
       					table_name = result[0];
       				}*/
       				result[0] = replaceAll("-","_",webservice_name) + "_" + result[0];
       				insertTableValues(result);
       			}
       			
       		}
       		ii++;
       				//alert(name);
    	});
    	disgnostics_response_parser = disgnostics_response_parser + "<p>STOP DATA PARSING DEVICE:" + diagnosicsDate() + "</br></p>" ;
    	
    	
    	
    	
    	
    	
    	
    	setTimeout(function(){onCompleteteFunction(xmlHttpRequest,status);}, 5000);
    }
	 
     
}




function showWaitMe(messsage){
    $('body').waitMe({
                     effect: "win8_linear",
                     text: messsage+'...',
                     bg: 'rgba(255,255,255,0.7)',
                     color:'#000000',
                     sizeW:'',
                     sizeH:''
                     });
}

function hideWaitMe()
{
	$('body').waitMe('hide');
}
