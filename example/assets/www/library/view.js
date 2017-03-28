var CustomPage = {
getPage: function(success, failure,path){
    cordova.exec(success, failure, "CustomPage", "openView", path);
}
};