const gulp = require('gulp');
const sass = require('gulp-sass');
const sourcemaps = require('gulp-sourcemaps');

////

const HTML_SRC = 'src/main/resources/templates/**/*.html';
function html() {
  return gulp.src(HTML_SRC)
    .pipe(gulp.dest('out/production/resources/templates'));
}
function htmlWatch() {
  gulp.watch(HTML_SRC, html);
}

const CSS_SRC = 'src/main/resources/static/css/**/*.scss';
function css1() {
  return gulp.src(CSS_SRC)
    .pipe(sourcemaps.init())
    .pipe(sass().on('error', sass.logError))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('out/production/resources/static'));
}
function css2() {
  return gulp.src(CSS_SRC)
    .pipe(sourcemaps.init())
    .pipe(sass().on('error', sass.logError))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('src/main/resources/static'));
}
function cssWatch() {
  gulp.watch(CSS_SRC, gulp.parallel(css1, css2));
}

exports.default = gulp.parallel(
  cssWatch, htmlWatch, jsWatch,
  css1, css2, html, js
);

