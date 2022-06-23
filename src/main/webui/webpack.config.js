const CopyWebpackPlugin = require('copy-webpack-plugin');
const glob = require('glob');
const path = require('path');

module.exports = {
    entry: glob.sync('./src/**.js').reduce(function(obj, el){
        obj[path.parse(el).name] = el;
        return obj
    },{}),
    output: {
        path: path.resolve(__dirname, 'build'),
        filename: "[name].js"
    },
    plugins: [
        new CopyWebpackPlugin({
            patterns: [
                { from: 'public' }
            ]
        })
    ]
};