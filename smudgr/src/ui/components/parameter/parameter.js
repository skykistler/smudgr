function ParameterCtrl($scope, project) {
  $scope.parameter = $scope.$ctrl.parameter;
}

angular.module('smudgr').component('parameter', {
  templateUrl: "components/parameter/parameter.html",
  controller: ParameterCtrl,
  bindings: {
    parameter: '='
  }
});
