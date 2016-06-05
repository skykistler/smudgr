function ComponentCtrl($scope, project) {
  $scope.component = $scope.$ctrl.component;
}

angular.module('smudgr').component('component', {
  templateUrl: "components/navigator/component.html",
  controller: ComponentCtrl,
  bindings: {
    component: '='
  }
});
