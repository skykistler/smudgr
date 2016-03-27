function RackCtrl($scope, $element, $attrs) {
  $scope.rackElements = new Array(10);
}

angular.module('smudgr').component('smudgrRack', {
  templateUrl: "components/rack/rack.html",
  controller: RackCtrl
});
