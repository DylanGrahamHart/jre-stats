const path = require('path');

module.exports = {
  mode: 'none',
  entry: './src/main.js',
  devtool: 'source-map',
  output: {
    filename: 'app.js',
    path: path.resolve(__dirname, 'public')
  },
  devServer: {
    contentBase: './public',
    liveReload: false
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