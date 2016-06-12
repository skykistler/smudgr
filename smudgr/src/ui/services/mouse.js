var app = angular.module('smudgr');

app.directive('mouseDrag', function ($document, $parse) {
  return function($scope, $element, $attributes) {
    var invoker = $parse($attributes.mouseDrag);

    $document.on("mouseup mousedown mousemove", function(event) {
      if (!$scope.hasMouseFocus) return;

      event.preventDefault();

      $scope.$apply(function() {
        invoker($scope, { $event: event });
      })
    });
  };
});
