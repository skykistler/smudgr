function CanvasCtrl($scope, $http, smudgr, $document) {
  $scope.image = [];

  var w = angular.element($document[0]);

  $scope.getWindowDimensions = function () {
      return {
          'h': w.height(),
          'w': w.width()
      };
  };

  $scope.$watch($scope.getWindowDimensions, function(newValue, oldValue) {
    var canvas = $document.find("smudgr-canvas");

    var x = canvas.offset().left;
    var y = canvas.offset().top;
    var w = canvas.width();
    var h = canvas.height();

    smudgr.exec("canvas.size", [x, y, w, h ]);
  });
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
