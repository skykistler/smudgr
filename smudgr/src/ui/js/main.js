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

  $('.js-detail-bar').addClass("mdl-layout__content");
  $('.js-render-view').addClass("mdl-layout__content");
  $('.js-sequence-bar-container').addClass("mdl-layout__content");

  $('.js-button').addClass("mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect");
  $('.js-fab').addClass("mdl-button mdl-js-button mdl-button--fab mdl-button--mini-fab mdl-button--colored mdl-js-ripple-effect");
}

function fitSections() {
  var top_layer_height = window.innerHeight - $('.js-sequence-bar-container').height();
  top_layer_height -= $('.mdl-layout__header').height();

  $('.js-detail-bar').height(top_layer_height);
  $('.js-render-view').height(top_layer_height);

  var detail_bar_width = $('.js-detail-bar').width();
  $('.js-render-view').width(window.innerWidth - detail_bar_width);
}

function test() {
  new SequenceComponent("test", ".js-sequence-bar");
}

function loadTest() {
  window.cefQuery({
    request: "open",
    onSuccess: function(response) {
      console.log(response);
    },
    onFailure: function(error_code, error_message) {
    }
  });
}
