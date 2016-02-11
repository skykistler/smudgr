;(function( window ) {

  'use strict';

  function SequenceComponent( name ) {
    this.name = name;
    this._init();

    this.template = name;
  }

  SequenceComponent.prototype._init = function() {
    var id = this.name.replace(" ", "") + "-component";
    var html = "<div class='sequence-component' id='" + id + "'></div>";
    $('#sequence-bar').append(html);

    $('#' + id).append(this.template);
  }

  window.SequenceComponent = SequenceComponent;

})( window );
