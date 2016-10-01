function ComponentCtrl($scope, project) {

  $scope.component = $scope.$ctrl.component;
  $scope.showParameters = 'initial';

  $scope.collapseIcon = 'keyboard_arrow_right'

  $scope.toggleParameters = function() {
    $scope.showParameters = $scope.showParameters == 'initial' ? true : !$scope.showParameters;

    if ($scope.collapseIcon == 'keyboard_arrow_down')
      $scope.collapseIcon = 'keyboard_arrow_right';
    else
      $scope.collapseIcon = 'keyboard_arrow_down';
  };

  $scope.rowCount = function() {
    return Math.max(1, Math.floor($scope.component.parameters.length / 2));
  };

}

angular.module('smudgr').component('component', {
  templateUrl: "components/navigator/component.html",
  controller: ComponentCtrl,
  bindings: {
    component: '='
  }
});
