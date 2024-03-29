module.exports = function (config) {
	config.set({
		// base path, that will be used to resolve files and exclude
		basePath: '../../target/web/dev/',

		// list of files / patterns to load in the browser
		files: [
			'js-test/test.js',
			{pattern: 'bower_components/**/*.js', included: false},
			{pattern: 'js/**/*.js', included: false},
			{pattern: 'js-test/unit/**/*Spec.js', included: false}
		],

		frameworks: [
			'requirejs',
			'jasmine'
		],

		// list of files to exclude
		exclude: [
		  'js/app.js'
		],

		// test results reporter to use
		// possible values: 'dots', 'progress', 'junit'
		reporters: [
			'progress'
		],

		// web server port
		port: 9876,

		// cli runner port
		runnerPort: 9100,

		// enable / disable colors in the output (reporters and logs)
		colors: true,

		// level of logging
		// possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
		logLevel: config.LOG_INFO,

		// enable / disable watching file and executing tests whenever any file changes
		autoWatch: false,

		// Start these browsers, currently available:
		// - Chrome
		// - ChromeCanary
		// - Firefox
		// - Opera
		// - Safari (only Mac)
		// - PhantomJS
		// - IE (only Windows)
		browsers: [
			'PhantomJS'
		],

		// If browser does not capture in given timeout [ms], kill it
		captureTimeout: 60000,

		// Continuous Integration mode
		// if true, it capture browsers, run tests and exit
		singleRun: true
	});
};
