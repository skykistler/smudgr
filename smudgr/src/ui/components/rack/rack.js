function RackCtrl($scope, project) {

  $scope.smudge = function() {
    return project.getSmudge();
  };

}

angular.module('smudgr').component('smudgrRack', {
  templateUrl: "components/rack/rack.html",
  controller: RackCtrl
});
