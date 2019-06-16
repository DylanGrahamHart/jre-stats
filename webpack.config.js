const path = require('path');

module.exports = {

  entry: './src/main/resources/static/js/main.js',
  mode: 'none',
  devtool: 'source-map',
  watch: true,

  output: {
    filename: 'react-app.js',
    path: path.resolve(__dirname, 'src/main/resources/static/js')
  },

  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader"
        }
      }
    ]
  }
};