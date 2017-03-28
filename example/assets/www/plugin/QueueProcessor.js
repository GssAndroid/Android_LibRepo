"use strict";
function QueueProcessorPlugin() {
}


QueueProcessorPlugin.prototype.sendToQP = function (successCallback, errorCallback, data) {
  cordova.exec(successCallback, errorCallback, "QueueProcessorPlugin", "sendToQP", data);
};

QueueProcessorPlugin.prototype.recvFromQP = function (successCallback, errorCallback) {
	  cordova.exec(successCallback, errorCallback, "QueueProcessorPlugin", "recvFromQP");
	};

QueueProcessorPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.queueprocessorPlugin = new QueueProcessorPlugin();
  return window.plugins.queueprocessorPlugin;
};

cordova.addConstructor(QueueProcessorPlugin.install);
