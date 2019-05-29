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

const CSS_SRC = 'src/main/resources/static/**/*.css';

function html() {
  return gulp.src(CSS_SRC)
    .pipe(gulp.dest('out/production/resources/static'));
}

function htmlWatch() {
  gulp.watch(CSS_SRC, html);
}

exports.default = gulp.series(htmlWatch, html);

