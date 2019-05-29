const gulp = require('gulp');

////

const HTML_SRC = 'src/main/resources/templates/**/*.html';

function html() {
  return gulp.src(HTML_SRC)
    .pipe(gulp.dest('out/production/resources/templates'));
}

function htmlWatch() {
  gulp.watch(HTML_SRC, html);
}

exports.default = gulp.series(html, htmlWatch)

