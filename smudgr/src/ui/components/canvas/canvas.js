function CanvasCtrl($scope, $http, smudgr, $document) {
  
  var canvasSizeChanged = function() {
    var canvas = $document.find("smudgr-canvas");

    return {
      x : parseInt(canvas.offset().left),
      y : parseInt(canvas.offset().top),
      w : parseInt(canvas.width()),
      h : parseInt(canvas.height())
    };
  }

  $scope.$watch(canvasSizeChanged, function(newValue, oldValue) {
    smudgr.exec("canvas.size", [newValue.x, newValue.y, newValue.w, newValue.h] );
  }, true);
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
