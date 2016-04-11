function CanvasCtrl($scope, $http, smudgr, $document) {
  $scope.image = [];

  $scope.canvasX = 0;
  $scope.canvasY = 0;
  $scope.canvasWidth = 0;
  $scope.canvasHeight = 0;

  $scope.canvasSizeChanged = function() {
    var canvas = $document.find("smudgr-canvas");

    var x = parseInt(canvas.offset().left);
    var y = parseInt(canvas.offset().top);
    var w = parseInt(canvas.width());
    var h = parseInt(canvas.height());

    if (x == $scope.canvasX && y == $scope.canvasY && w == $scope.canvasWidth && h == $scope.canvasHeight)
      return false;

    $scope.canvasX = x;
    $scope.canvasY = y;
    $scope.canvasWidth = w;
    $scope.canvasHeight = h;

    return true;
  }

  $scope.$watch('canvasSizeChanged()', function(newValue, oldValue) {
    smudgr.exec("canvas.size", [$scope.canvasX, $scope.canvasY, $scope.canvasWidth, $scope.canvasHeight] );
  });
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
