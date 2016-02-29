$(window).resize(function() {
  fitSections();
});

$(document).ready(function() {
  fitSections();
});

function fitSections() {
  var top_layer_height = window.innerHeight - $('#sequence-bar-container').height();
  $('#detail-bar').height(top_layer_height);
  $('#render-view').height(top_layer_height);

  var detail_bar_width = $('#detail-bar').width();
  $('#render-view').width(window.innerWidth - detail_bar_width);
}

function queryTest() {
  window.cefQuery({
    request: 'This is a test :)',
    onSuccess: function(response) {alert(response);},
    onFailure: function(error_code, error_message) {}
  });
}
