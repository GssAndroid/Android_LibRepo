"use strict";
function ContactsLauncher() {
}


ContactsLauncher.prototype.addContactsPage = function (successCallback, errorCallback) {
//alert('hi');
  cordova.exec(successCallback, errorCallback, "ContactsLauncher", "addContactsPage", []);
};

ContactsLauncher.prototype.viewContactsPage = function (id,successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "ContactsLauncher", "viewContactsPage", [{"id":id}]);
};

ContactsLauncher.prototype.openView = function (id,successCallback, errorCallback) {
	  cordova.exec(successCallback, errorCallback, "ContactsLauncher", id, []);
	};




ContactsLauncher.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.contactsLauncher = new ContactsLauncher();
  return window.plugins.contactsLauncher;
};

cordova.addConstructor(ContactsLauncher.install);
