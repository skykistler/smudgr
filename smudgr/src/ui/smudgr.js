var smudgr = angular.module('smudgr', ['ngMaterial']);

smudgr.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
    .primaryPalette('blue-grey')
    .accentPalette('cyan');
});

smudgr.controller("AppCtrl", ['$scope', function($scope) {
  // var element1 = { name: "Test Element", test: "Test String" };
  // var element2 = { name: "Test Element 2", test: "Test String 2" };
  // this.elementList = { element1, element2 };
}]);
