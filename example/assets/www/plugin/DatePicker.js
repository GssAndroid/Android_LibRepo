"use strict";
function DatePickerPlugin() {
}

DatePickerPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.datePickerPlugin = new DatePickerPlugin();
  return window.plugins.datePickerPlugin;
};

cordova.addConstructor(DatePickerPlugin.install);
