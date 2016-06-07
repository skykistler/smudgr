function CanvasCtrl($scope, smudgr, $document) {

  var canvasSizeChanged = function() {
    var canvas = $document.find("smudgr-canvas");

    return {
      x : parseInt(canvas.offset().left),
      y : parseInt(canvas.offset().top),
      w : parseInt(canvas.width()),
      h : parseInt(canvas.height())
    };
  };

  $scope.$watch(canvasSizeChanged, function(newValue, oldValue) {
    smudgr.exec("canvas.size", newValue);
  }, true);

  var canvas = $document.find('#canvas');

  if (canvas) {
    canvas = canvas.get(0);

    var ws = new WebSocket('ws://24.92.23.148:8887')
    ws.binaryType = "arraybuffer";

    var context = canvas ? canvas.getContext('2d') : null;
    var bytes = null;

    ws.onopen = function() {
      console.log('Connected to frame server')
      ws.send('Hello')
    };

    ws.onmessage = function(e) {
      if (e.type == 'message') {
        var message = e.data.toString();

        if (message.startsWith("w")) {
          if (canvas) {
            canvas.width = message.split(":")[1];
            console.log("Canvas width: " + canvas.width);
          }
        }

        if (message.startsWith("h")) {
          if (canvas) {
            canvas.height = message.split(":")[1];
            console.log("Canvas height: " + canvas.height);
          }
        }

        if(message.startsWith("bmp")) {
          $scope.image = message.split(":")[1];
          console.log("Image length: " + $scope.image.length);
          ws.send("");
        }
      }

      if (e.data instanceof ArrayBuffer) {
        var perf = window.performance.now();

        bytes = new Uint8Array(e.data);

        var imageData = context.getImageData(0, 0, canvas.width, canvas.height);

        for (var i = 0; i < imageData.data.length; i++)
          imageData.data[i] = bytes[i];

        context.putImageData(imageData, 0, 0);

        // console.log("Took: " + (window.performance.now() - perf));

        ws.send("");
      }
    };

    ws.onclose = function() {
      console.log('Closed connection to frame server')
    };
  }
}

angular.module('smudgr').component('smudgrCanvas', {
  templateUrl: "components/canvas/canvas.html",
  controller: CanvasCtrl
});
