function ComponentCtrl($scope, project) {

  $scope.component = $scope.$ctrl.component;
  $scope.showParamaters = false;

  $scope.collapseIcon = 'keyboard_arrow_right'

  $scope.toggleParameters = function() {
    $scope.showParamaters = !$scope.showParameters;

    if ($scope.collapseIcon == 'keyboard_arrow_down')
      $scope.collapseIcon = 'keyboard_arrow_right';
    else
      $scope.collapseIcon = 'keyboard_arrow_down';
  };

}

angular.module('smudgr').component('component', {
  templateUrl: "components/navigator/component.html",
  controller: ComponentCtrl,
  bindings: {
    component: '='
  }
});
