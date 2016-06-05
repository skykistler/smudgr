function ElementCtrl($scope, project) {

  $scope.element = $scope.$ctrl.element;

  $scope.getElementName = function() {
    if ($scope.element.name)
      return $scope.element.name;

    if ($scope.element.components)
      for (var i in $scope.element.components) {
        var comp = $scope.element.components[i];
        if (comp.type == "Operation") {
          return comp.name;
        }
      }

    return "Element";
  };

  $scope.selectElement = function() {
    project.currentElement = $scope.element;
  };

}

angular.module('smudgr').component('element', {
  templateUrl: "components/rack/element.html",
  controller: ElementCtrl,
  bindings: {
    element: '='
  }
});
