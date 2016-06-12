function ElementCtrl($scope, project) {

  $scope.iconEnabled = 'radio_button_checked';
  $scope.iconDisabled = 'radio_button_unchecked';

  $scope.element = $scope.$ctrl.element;
  $scope.enableIcon = $scope.iconEnabled;

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

  $scope.boundFilter = function(input) {
    if (input.name != "Enable")
      return true;
  };

  $scope.selectElement = function() {
    project.currentElement = $scope.element;
  };

  $scope.toggleEnable = function() {
    if ($scope.enableIcon == $scope.iconDisabled)
      $scope.enableIcon = $scope.iconEnabled;
    else
      $scope.enableIcon = $scope.iconDisabled;
  };

}

angular.module('smudgr').component('element', {
  templateUrl: "components/rack/element.html",
  controller: ElementCtrl,
  bindings: {
    element: '='
  }
});
