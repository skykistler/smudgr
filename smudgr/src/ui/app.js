var app = angular.module('smudgr', ['ngMaterial']);

app.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
    .primaryPalette('blue-grey')
    .accentPalette('cyan');
});

app.controller("smudgrCtrl", function($scope, smudgr, project) {

  var handleResponse = function(response) {
    if (response.message) {
      if (response.status == 'success') {
        console.log(response.message);
      } else if (response.status == 'failure') {
        console.error(response.message);
      }
    }
  };

  smudgr.addHandler("response", handleResponse, $scope);

  smudgr.onOpen = function() {
      console.log('Opened connection to smudgr');
      smudgr.exec('project.get');
  };

});
