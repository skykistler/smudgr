function RackCtrl($scope, project) {
  $scope.smudge = function() {
    return project.getSmudge();
  }

  $scope.getElementName = function(element) {
    if (element.name)
      return element.name;

    if (element.components)
      for (var i in element.components) {
        var comp = element.components[i];
        if (comp.type == "Operation") {
          return comp.name;
        }
      }

    return "Element";
  };

  $scope.selectElement = function(element) {
    project.currentElement = element;
  };

}

angular.module('smudgr').component('smudgrRack', {
  templateUrl: "components/rack/rack.html",
  controller: RackCtrl
});
