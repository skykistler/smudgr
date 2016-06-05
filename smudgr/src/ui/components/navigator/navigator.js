function NavigatorCtrl($scope, project) {

  $scope.currentElement = function() {
    return project.getCurrentElement();
  };
  
}

angular.module('smudgr').component('smudgrNavigator', {
  templateUrl: "components/navigator/navigator.html",
  controller: NavigatorCtrl
});
