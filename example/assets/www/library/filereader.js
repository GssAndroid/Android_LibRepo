var filePth = [];
var byteImage = [];
var attachmentReq = [];

var newFilename = "";

var callBackFunction = null;

function dirReader(path,callBack)
{

callBackFunction = callBack;


window.FS.root.getDirectory(path, {create: false, exclusive: false}, getDirSuccess, dfail);
}


function moveDirectory(path,newname)
{
newFilename = newname;
window.FS.root.getDirectory(path, {create: false, exclusive: false}, moveDirSuccess, mfail);
}


function moveDirSuccess(dirEntry) {
   dirEntry.moveTo(dirEntry, newFilename, msuccess, mfail);
}

function msuccess(entry) {
    console.log("New Path: " + entry.fullPath);
}

function mfail(error) {
    alert(error.code);
}



function getDirSuccess(dirEntry) {
    var directoryReader = dirEntry.createReader();

    // Get a list of all the entries in the directory
    directoryReader.readEntries(readerSuccess,dfail);
}

function readerSuccess(entries) {
    var i;
    filePth = [];
    byteImage = [];
    attachmentReq = [];
    for (i=0; i<entries.length; i++) {
        
        //alert(JSON.stringify(entries[i]));   
        
        filePth.push(entries[i].fullPath);
        
        //window.resolveLocalFileSystemURI(entries[i].fullPath, gotFileReaderReq, dfail); 
        
        
    }
    
    createFileReaders();
    
    //if(callBackFunction != null)
    	//callBackFunction();
}

function createFileReaders()
{
	 for (var i=0; i<filePth.length; i++) {
	 
	 	window.resolveLocalFileSystemURI(filePth[i], gotFileReaderReq, dfail); 
	 
	 }
}



function gotFileReaderReq(fileEntry){
        fileEntry.file(function(fileObj) {
            
            readDataUrl(fileObj);
        });
        
    }
    
    function readDataUrl(file) {
        var reader = new FileReader();
        reader.onloadend = function(evt) {
            console.log("Read as data URL");
           // alert("there was an error: " + JSON.stringify(evt));
           	//alert(evt.target.result);
            byteImage.push(evt.target.result);
            
           
            
            if(filePth.length ==  byteImage.length)
            {
            	attachmentReq.push("DATA-TYPE[.]ZGSXCAST_ATTCHMNT01[.]OBJECT_ID[.]OBJECT_TYPE[.]OBJECT_ZZSSRID[.]NUMBER_EXT[.]ATTCHMNT_ID[.]ATTCHMNT_CNTNT");
            	for(var i = 0; i < filePth.length; i++)
            	{
            		// attachmentReq.push(attachmentCreateRequest(filePth[i],byteImage[i]));
            		attachmentReq.push(attachmentCreateRequest(filePth[i],byteImage[i]));
            	}
            
            
            	 if(callBackFunction != null)
    			 	callBackFunction();
            }
            
        };
        reader.readAsDataURL(file);
    }




function dfail(error) {
    console.log("Error removing file: " + error.code);
    if(callBackFunction != null)
    			 	callBackFunction();
}


function attachmentCreateRequest(filepath,byte)
{
//byte = byte.replace("data:image/jpeg;base64,", "");
	byte = byte.replace("data:image/jsignature;base30,", "");

var attachReq = "ZGSXCAST_ATTCHMNT01[.][.][.][.][.][.]"  + byte + "[.]";

return attachReq;

}