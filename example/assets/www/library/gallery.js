function openGallery(path)
{
//alert(path);
window.FS.root.getDirectory(path, {create: false, exclusive: false}, getDirSuccess, gfail);
}

function getDirSuccess(dirEntry) {
    // Get a directory reader
    var directoryReader = dirEntry.createReader();
	//alert(dirEntry);
    // Get a list of all the entries in the directory
    directoryReader.readEntries(readerSuccess,gfail);
}

function readerSuccess(entries) {
    var i;
    //alert(entries.length);
    $(".sp-wrap").text('');
    for (i=0; i<entries.length; i++) {        
		var imageGallery = "<a href='"+entries[i].fullPath+"' data-gallery=''><img width='100' height='100' src='"+entries[i].fullPath+"' id='"+ JSON.stringify(entries[i]) + "'></a>";
		console.log("imageGallery::"+imageGallery);
		/*var image = document.getElementById('viewimg');
	     image.src = "data:image/jpeg;base64," + entries;*/
		$("#links").append(imageGallery);
    }
}


function removefile(path){
	window.resolveLocalFileSystemURI(path, gotRemoveFileEntry, gfail); 
}

function gotRemoveFileEntry(fileEntry){
    fileEntry.remove(removeSuccess, gfail);
}

function removeSuccess(entry) {
    console.log("Removal succeeded");
    openGallery(pathGallery);
}

function gfail(error) {
    //alert("Error removing file: " + error.code);
}