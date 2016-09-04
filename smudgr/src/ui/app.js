var app = angular.module('smudgr', ['ngMaterial', 'ngMdIcons']);

app.config(function($mdThemingProvider) {
  // $mdThemingProvider.theme('default')
  //   .primaryPalette('blue-grey')
  //   .accentPalette('cyan');

  $mdThemingProvider.theme('default')
    .primaryPalette('grey', {
      'default': '100', // by default use shade 400 from the pink palette for primary intentions
      'hue-1': '100', // use shade 100 for the <code>md-hue-1</code> class
      'hue-2': '600', // use shade 600 for the <code>md-hue-2</code> class
      'hue-3': 'A100' // use shade A100 for the <code>md-hue-3</code> class
    })
    // If you specify less than all of the keys, it will inherit from the
    // default shades
    .accentPalette('cyan', {
      'default': '800' // use shade 200 for default, and keep all other shades the same
    });
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
