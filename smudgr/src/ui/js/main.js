$(window).resize(function() {
  fitSections();
});

$(document).ready(function() {
  fitSections();
});

function fitSections() {
  var top_layer_height = window.innerHeight - $('#sequence-bar-container').height();
  top_layer_height -= $('.mdl-layout__header').height();

  $('#detail-bar').height(top_layer_height);
  $('#render-view').height(top_layer_height);

  var detail_bar_width = $('#detail-bar').width();
  $('#render-view').width(window.innerWidth - detail_bar_width);
}

function test() {
  new SequenceComponent("test", "#sequence-bar");
}
