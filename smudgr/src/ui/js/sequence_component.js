function SequenceComponent(name) {
  this.name = name;
  getHTML();
}

function getHTML() {
  window.cefQuery({
    request: 'smudgr://ui/html/sequence_component.html',
    onSuccess: function(response) {
      $('#sequence-bar').append(response);
    },
    onFailure: function(error_code, error_message) {
    }
  });
}
