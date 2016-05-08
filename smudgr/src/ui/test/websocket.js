onmessage = function(e) {
  init(e);
  postMessage("Started");
}

var init = function(canvas) {

  var ws = new WebSocket('ws://localhost:8887')
  ws.binaryType = "arraybuffer";

  var context = canvas.getContext('2d');
  var bytes = null;

  ws.onopen = function() {
    console.log('open')
    ws.send('Hello')
  }

  ws.onmessage = function(e) {
    if (e.type == 'message') {
      var message = e.data.toString();

      if (message.startsWith("w")) {
        canvas.width = message.split(":")[1];
        console.log("Canvas width: " + canvas.width);
      }

      if (message.startsWith("h")) {
        canvas.height = message.split(":")[1];
        console.log("Canvas height: " + canvas.height);
      }
    }

    if (e.data instanceof ArrayBuffer) {
      var perf = window.performance.now();

      // if (bytes == null || bytes.length < e.data.length) {
      bytes = new Uint8Array(e.data);
      // } else {
      //   bytes.fill(e.data, 0, e.data.length);
      // }

      var imageData = context.getImageData(0, 0, canvas.width, canvas.height);

      for (var i = 0; i < imageData.data.length; i++)
        imageData.data[i] = bytes[i];

      context.putImageData(imageData, 0, 0);

      console.log("Took: " + (window.performance.now() - perf));

      ws.send("");
    }
  }

  ws.onclose = function() {
    console.log('close')
  }

}
