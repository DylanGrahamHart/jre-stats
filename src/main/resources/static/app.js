jreStats();

function jreStats() {
  formatNumbers();

  function formatNumbers() {
    var nodes = $('.js-format-number');

    for (var i = 0; i < nodes.length; i++) {
      var text = nodes[i].innerText;

      if (text.length > 9) {
        nodes[i].innerText = (Number(text) / Math.pow(10, 9)).toFixed(1) + 'B';
      } else if (text.length > 6) {
        nodes[i].innerText = (Number(text) / Math.pow(10, 6)).toFixed(1) + 'M';
      } else if (text.length > 3) {
        nodes[i].innerText = (Number(text) / Math.pow(10, 3)).toFixed(0) + 'K';
      }
    }
  }

  function $(selector) {
    return window.document.querySelectorAll(selector)
  }
}
