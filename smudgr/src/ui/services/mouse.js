var app = angular.module('smudgr');

app.directive('mouseDrag', function ($document, $parse) {
  return function(scope, elem, attrs) {
    scope.hasMouseFocus = false;

    var initialMouseY = -1;
    var initialMouseX = -1;

    elem.on('mousedown', function(event) {
      scope.hasMouseFocus = true;
      initialMouseY = event.pageY;
      initialMouseX = event.pageX;

      if (scope.mouseDown)
        scope.mouseDown(initialMouseX, initialMouseY);
    });

    $document.on('mouseup', function(event) {
      angular.element('html').removeClass('.hide-cursor');

      scope.hasMouseFocus = false;
      initialMouseY = event.pageY;
      initialMouseX = event.pagex;

      if (scope.mouseUp)
        scope.mouseDown(initialMouseX, initialMouseY);
    });

    $document.on('mousemove', function(event) {
      if (!scope.hasMouseFocus) return;

      angular.element('html').css('cursor: none');
      event.preventDefault();

      scope.$apply(function() {
        if (scope.mouseDrag)
          scope.mouseDrag(initialMouseX, initialMouseY, event.pageX, event.pageY);
      })
    });
  };
});
