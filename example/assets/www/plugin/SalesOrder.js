"use strict";
function SalesOrderPlugin() {
}


SalesOrderPlugin.prototype.sendSOToQP = function (successCallback, errorCallback, data) {
  cordova.exec(successCallback, errorCallback, "SalesOrderPlugin", "sendSOToQP", data);
};

SalesOrderPlugin.prototype.SetNotifyFlagToQP = function (successCallback, errorCallback, data) {
	  cordova.exec(successCallback, errorCallback, "SalesOrderPlugin", "SetNotifyFlagToQP",data);
	};

SalesOrderPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.salesorderPlugin = new SalesOrderPlugin();
  return window.plugins.salesorderPlugin;
};

cordova.addConstructor(SalesOrderPlugin.install);
