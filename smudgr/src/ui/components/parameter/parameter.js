function ParameterCtrl($scope, project) {
  $scope.parameter = $scope.$ctrl.parameter;
  $scope.parameterValue = 50;
  $scope.knobOptions = {
    // size: 300
  };

  $scope.hasMouseFocus = false;
  $scope.initialMouseY = 0;
  $scope.initialVal = $scope.parameterValue;

  $scope.mouseDown = function($event) {
      $scope.initialMouseY = $event.pageY;
      $scope.initialVal = $scope.parameterValue;
      $scope.hasMouseFocus = true;
  };

  $scope.mouseDrag = function($event) {
    if (!$scope.hasMouseFocus)
      return;

    if ($event.type == 'mousemove')
      $scope.setValue($scope.initialVal + ($scope.initialMouseY - $event.pageY));

    if($event.type == 'mouseup')
      $scope.hasMouseFocus = false;
  };

  $scope.setValue = function(toVal) {
    toVal = toVal > 100 ? 100 : toVal;
    toVal = toVal < 0 ? 0 : toVal;

    $scope.parameterValue = toVal;
  };
}

angular.module('smudgr').component('parameter', {
  templateUrl: "components/parameter/parameter.html",
  controller: ParameterCtrl,
  bindings: {
    parameter: '='
  }
});
