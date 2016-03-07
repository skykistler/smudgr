$(window).resize(function() {
  fitSections();
});

$(document).ready(function() {
  initializeMaterial();
  fitSections();
});

function initializeMaterial() {
  $('.js-body').addClass("mdl-layout mdl-js-layout mdl-layout--fixed-header");
  $('.js-header').addClass("mdl-layout__header");
  $('.js-header-row').addClass("mdl-layout__header-row");

  $('main').addClass("mdl-layout__content");

  $('.js-detail-bar').addClass("mdl-layout__content mdl-cell--4-col");
  $('.js-render-view').addClass("mdl-layout__content");
  $('.js-sequence-bar-container').addClass("mdl-layout__content");

  $('.js-button').addClass("mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect");
  $('.js-fab').addClass("mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--colored mdl-js-ripple-effect");
}

function fitSections() {
  var top_layer_height = window.innerHeight - $('.js-sequence-bar-container').height();
  top_layer_height -= $('.js-header').height();

  $('.js-detail-bar').height(top_layer_height);
  $('.js-render-view').height(top_layer_height);

  var detail_bar_width = $('.js-detail-bar').width();
  $('.js-render-view').width(window.innerWidth - detail_bar_width);

  sendRenderDimensions();
}

function test() {
  new SequenceComponent("test", ".js-sequence-bar");
}

function loadTest() {
  sendAction("open");
}

function sendRenderDimensions() {
  var offset = $('.js-render-view').offset();
  var renderOffsetY = offset.top;
  var renderOffsetX = offset.left;
	var renderViewWidth = $('.js-render-view').width();
	var renderViewHeight = $('.js-render-view').height();

  sendProperty('renderOffsetX', renderOffsetX);
  sendProperty('renderOffsetY', renderOffsetY);
  sendProperty('renderViewWidth', renderViewWidth);
  sendProperty('renderViewHeight', renderViewHeight);
  sendAction('updateRenderView');
}

function sendProperty(name, value) {
  sendRequest("prop:" + name + ":" + value);
}

function sendAction(action) {
  sendRequest("action:" + action);
}

function sendRequest(requestValue) {
  window.cefQuery({
    request: requestValue,
    onSuccess: function(response) {
      console.log(response);
    },
    onFailure: function(error_code, error_message) {
      console.log(error_message);
    }
  });
}
