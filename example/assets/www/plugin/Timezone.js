"use strict";
function TimeZonePlugin() {
}


TimeZonePlugin.prototype.listTimezone = function (successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "TimeZonePlugin", "listTimezone", []);
};





TimeZonePlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.timeZonePlugin = new TimeZonePlugin();
  return window.plugins.timeZonePlugin;
};

cordova.addConstructor(TimeZonePlugin.install);
