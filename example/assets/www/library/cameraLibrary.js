var pictureSource;
var destinationType;

    function onPhotoDataSuccess(imageData) {
      
      //var smallImage = document.getElementById('smallImage');
      //smallImage.style.display = 'block';
      //smallImage.src = "data:image/jpeg;base64," + imageData;
    }

    function onPhotoURISuccess(imageURI) {
   
      //var largeImage = document.getElementById('largeImage');
      //largeImage.style.display = 'block';
      //largeImage.src = imageURI;
      
      var gotFileEntry = function(fileEntry) {
    		//alert("Default Image Directory " + fileEntry.fullPath);
    		var gotFileSystem = function(fileSystem) {
    				
					fileSystem.root.getDirectory(window.localStorage.getItem("SVgallerypath"), {
            		create : true
        			}, function(dataDir) {
          				var d = new Date();
          				var n = d.getTime();
          				//new file name
          				var newFileName = n + ".png";
            			// copy the file
            			fileEntry.moveTo(dataDir, newFileName, null, fsFail);
            			
        			}, dirFail);
					};
    	window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, gotFileSystem,onFail);
    };
	window.resolveLocalFileSystemURI(imageURI, gotFileEntry, fsFail);

var onFail = function(error) {
    alert("failed " + error.code);
};

var dirFail = function(error) {
    alert("Directory" + error.code);
};

var fsFail = function(error) {
    alert("Directory fsFail" + error.code);
};
      
}
    

    function capturePhoto() {
      navigator.camera.getPicture(onPhotoURISuccess, onFail, { quality: 50,
        destinationType: destinationType.FILE_URI });
    }

    function capturePhotoEdit() {
      
      navigator.camera.getPicture(onPhotoDataSuccess, onFail, { quality: 20, allowEdit: true,
        destinationType: destinationType.DATA_URL });
    }

    function getPhoto(source) {
      navigator.camera.getPicture(onPhotoURISuccess, onFail, { quality: 50, 
        destinationType: destinationType.FILE_URI,
        sourceType: source });
    }

    function onFail(message) {
      alert('Failed because: ' + message);
    }
    
    
        