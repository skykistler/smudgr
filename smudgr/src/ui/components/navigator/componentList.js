function ComponentListCtrl($scope, project) {

  $scope.title = $scope.$ctrl.title;
  $scope.type = $scope.$ctrl.type;

  $scope.currentElement = function() {
    return project.getCurrentElement();
  };

  $scope.filterByType = function(input) {
    return input.type == $scope.type;
  };

}

angular.module('smudgr').component('componentList', {
  templateUrl: "components/navigator/componentList.html",
  controller: ComponentListCtrl,
  bindings: {
    type: '@',
    title: '@'
  }
});
