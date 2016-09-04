function ParameterCtrl($scope, project) {
  $scope.parameter = $scope.$ctrl.parameter;
  $scope.parameterValue = 50;
  $scope.knobOptions = {
    size: 100,
    barCap: 20,
    barWidth: 15,
    barColor: '#aaddaa',
    prevBarColor: 'rgba(0,0,0,.2)',
    displayPrevious: true,
    readOnly: true
  };

  $scope.initialMouseY = 0;
  $scope.initialVal = $scope.parameterValue;

  $scope.mouseDown = function() {
    $scope.initialVal = $scope.parameterValue;
  };

  $scope.mouseDrag = function(x1, y1, x2, y2) {
    $scope.setValue($scope.initialVal + (y1 - y2));
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
