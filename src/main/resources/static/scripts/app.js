(function(){

  init();

  ////

  function init() {
    home();
  }

  function home() {
    lazyLoadImgs();
    doSort();

    ////

    function lazyLoadImgs() {
      $('[data-src]').each(function(){
        var imgSrc = $(this).attr('data-src');
        $(this).attr('src', imgSrc);
      });
    }

    function doSort() {
      window.sort = function(sortValue) {
        var params = getParams();
        var hostAndPath = location.href.substr(0, location.href.indexOf('?'));

        params['sort'] = sortValue;
        location.href = hostAndPath + getParamsQueryString(params)
      };

      function getParams() {
        var s = location.search.substr(1);
        var params = {};

        s.split('&').map(function(el){
          return el.split('=');
        }).forEach(function(el){
          if (el[0] && el[1]) {
            params[el[0]] = el[1];
          }
        });

        return params;
      }

      function getParamsQueryString(params) {
        var keys = Object.keys(params);
        var queryString = '';

        if (keys.length) {
          queryString = '?';

          for (var i = 0; i < keys.length; i++) {
            queryString += keys[i] + '=' + params[keys[i]];

            if (i+1 !== keys.length) {
              queryString += '&'
            }
          }
        }

        return queryString;
      }
    }

  }

})();


