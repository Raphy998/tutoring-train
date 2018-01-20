const path = require('path');

module.exports = {
  entry: [
    './src/client/app/index.jsx', './src/client/app/Login/styles/style.less'
  ],
  output: {
    path: __dirname + '/src/client/public',
    filename: 'bundle.js'
  },
  resolve: {
    extensions: [
      '.jsx', '.js'
    ],
    modules: [
      path.resolve(__dirname, 'node_modules'),
      path.resolve(__dirname, './src/client/')
    ]
  },
  module: {
    loaders: [
      {
        test: /\.jsx?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          cacheDirectory: true,
          presets: [
            'react', 'es2015'
          ],
          plugins: ['transform-class-properties']
        }
      }, {
        test: /\.less$/,
        use: ['style-loader', 'css-loader', 'less-loader']
      }, {
        test: /\.(jpe?g|png|gif|svg)$/i,
        loaders: ['file-loader?hash=sha512&digest=hex&name=[hash].[ext]', 'image-webpack-loader?bypassOnDebug&optimizationLevel=7&interlaced=false']
      }, {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      }
    ]
  }
}
