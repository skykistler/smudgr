function CanvasCtrl($scope, smudgr, $document) {

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
    smudgr.exec("canvas.size", newValue);
  }, true);
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
