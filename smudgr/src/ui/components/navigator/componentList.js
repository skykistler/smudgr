function ComponentListCtrl($scope, project) {

  $scope.currentElement = function() {
    return project.getCurrentElement();
  };

}

angular.module('smudgr').component('componentList', {
  templateUrl: "components/navigator/componentList.html",
  controller: ComponentListCtrl,
  bindings: {
    type: '<',
    title: '<'
  }
});
