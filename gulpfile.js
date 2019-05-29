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
function css() {
  return gulp.src(CSS_SRC)
    .pipe(gulp.dest('out/production/resources/static'));
}
function cssWatch() {
  gulp.watch(CSS_SRC, css);
}

exports.default = gulp.parallel(cssWatch, htmlWatch, css, html);

