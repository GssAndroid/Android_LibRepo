"use strict";
function QueueProcessorPlugin() {
}


QueueProcessorPlugin.prototype.sendToQP = function (successCallback, errorCallback, data) {
  cordova.exec(successCallback, errorCallback, "QueueProcessorPluginToPhonegap", "sendToPhoneGap", data);
};


QueueProcessorPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.queueprocessorPlugin = new QueueProcessorPlugin();
  return window.plugins.queueprocessorPlugin;
};

cordova.addConstructor(QueueProcessorPlugin.install);
