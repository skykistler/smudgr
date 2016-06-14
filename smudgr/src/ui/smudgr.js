var app = angular.module('smudgr', ['ngMaterial']);

app.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
    .primaryPalette('blue-grey')
    .accentPalette('cyan');
});

app.controller("smudgrCtrl", function($scope, smudgr) {
});

app.factory('smudgr', function() {
  return {
    exec: function (command, payload) {
      console.log("Executing " + command + "...");

      var data = command;

      if (payload) {
        console.log("Payload: " + payload);

        if (!Array.isArray(payload))
          payload = [ payload ];

        payload.forEach(function (elem) {
          data += ":" + elem;
        });
      }

      if (window.cefQuery) {
        window.cefQuery({
          request: data,
          onSuccess: function(response) {
            console.log("Success: " + response);
          },
          onFailure: function(error_code, error_message) {
            console.log("Error: " + error_code + " - " + error_message);
          }
        });
      } else {
        console.log("CEF disconnected.");
      }
    }
  };
});