function CanvasCtrl($scope, $http) {
  $scope.image = [];
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
