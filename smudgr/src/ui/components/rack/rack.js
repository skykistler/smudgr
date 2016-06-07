function RackCtrl($scope, project, $timeout) {

  $scope.getSmudge = function() {
    return project.smudge;
  };

  $scope.$watch($scope.getSmudge, function(newVal) {
    $timeout(function() {
      $scope.$apply(function() {
        $scope.elements = project.smudge.algorithms;
      });
    }, 500);
  }, true);

}

angular.module('smudgr').component('smudgrRack', {
  templateUrl: "components/rack/rack.html",
  controller: RackCtrl
});
