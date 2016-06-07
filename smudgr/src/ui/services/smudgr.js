class smudgr {

  constructor() {
    this.handlers = {};

    this.socket = new WebSocket('ws://' + location.hostname + ':45455');
    this.socket.parent = this;

    this.socket.onmessage = function (e) {
      this.parent.onMessage(e);
    }

    this.socket.onclose = function() {
      console.log('Closed connection to smudgr');
    };
  }

  exec(command, data) {
    if (this.socket.readyState !== 1)
      return;

    console.log("Executing " + command + "...");

    var payload = {
      command: command,
      data: data
    };

    var request = JSON.stringify(payload);
    console.log("Request: " + request);

    this.socket.send(request);
  }

  onMessage(e) {
    var message = JSON.parse(e.data);

    var command = message.command;
    if (!command) {
      console.log("Malformed packet from smudgr");
      console.log(e);
      return;
    }

    var handler = this.handlers[command];
    if (!handler) {
      console.log("No handler found for: " + command);
      return;
    }

    handler.callback.call(handler.context, message.data);
  };

  addHandler(command, callback, context) {
    this.handlers[command] = {
      callback: callback,
      context: context
    };
  }

  set onOpen(callback) {
    if (this.socket && this.socket.readyState == 1)
      callback();
    else
      this.socket.onopen = callback;
  }
}

angular.module('smudgr').service('smudgr', smudgr);
