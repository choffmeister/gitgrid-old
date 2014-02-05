path = require("path")
send = require("send")
targetDev = "../../target/web/dev"
targetProd = "../../target/web/prod"

mountFolder = (connect, dir) ->
  connect.static path.resolve(dir)

module.exports = (grunt) ->
  grunt.initConfig
    requirejs:
      prod:
        options:
          baseUrl: "#{targetDev}/js"
          mainConfigFile: "#{targetDev}/js/app.js"
          name: "app"
          out: "#{targetProd}/js/app.js"
          optimize: "uglify"

    coffee:
      dev:
        expand: true
        cwd: "src/coffee"
        src: ["**/*.coffee"]
        dest: "#{targetDev}/js"
        ext: ".js"
      test:
        expand: true
        cwd: "test"
        src: ["**/*.coffee"]
        dest: "#{targetDev}/js-test"
        ext: ".js"

    jade:
      dev:
        files: [
          expand: true
          cwd: "src/jade"
          src: "**/*.jade"
          dest: "#{targetDev}"
          ext: ".html"
          rename: (dest, src) ->
            path.join dest, (if src is "index.html" then "_index.html" else src)
        ]
        options:
          pretty: true

      prod:
        files: [
          expand: true
          cwd: "src/jade"
          src: "**/*.jade"
          dest: "#{targetProd}"
          ext: ".html"
        ]
        options:
          pretty: false

    less:
      dev:
        files: [
          src: "src/less/main.less"
          dest: "#{targetDev}/css/main.css"
        ]
        options:
          paths: ["src/less"]
          compress: false

      prod:
        files: [
          src: "src/less/main.less"
          dest: "#{targetProd}/css/main.css"
        ]
        options:
          paths: ["src/less"]
          compress: true

    copy:
      dev:
        files: [
          expand: true
          cwd: "src/resources"
          src: "**/*.*"
          dest: "#{targetDev}"
        ]

      prod:
        files: [
          expand: true
          cwd: "src/resources"
          src: "**/*.*"
          dest: "#{targetProd}"
        ,
          expand: true
          cwd: "bower_components"
          src: "**/*"
          dest: "#{targetDev}/bower_components"
        ,
          expand: true
          cwd: "bower_components"
          src: "**/*"
          dest: "#{targetProd}/bower_components"
        ]

      test:
        files: [
          expand: true
          cwd: "bower_components"
          src: "**/*.js"
          dest: "#{targetDev}/bower_components"
        ]

    connect:
      proxies: [
        context: "/api"
        host: "localhost"
        port: 8080
        https: false
        changeOrigin: false
      ]
      dev:
        options:
          port: 9000
          hostname: "0.0.0.0"
          middleware: (connect) ->
            [require("grunt-connect-proxy/lib/utils").proxyRequest
            , mountFolder(connect, "#{targetDev}/")
            , mountFolder(connect, "")
            , (req, res, next) ->
              req.url = "/"
              next()
            , (req, res, next) ->
              error = (err) ->
                res.statusCode = err.status or 500
                res.end err.message

              notFound = ->
                res.statusCode = 404
                res.end "Not found"

              if req.originalUrl.match(/\.(html|css|js|png|jpg|gif|ttf|woff|svg|eot)$/)
                notFound()
              else
                send(req, "_index.html").root("#{targetDev}/").on("error", error).pipe res
            ]

    watch:
      options:
        livereload: true

      coffeedev:
        files: ["src/coffee/**/*.coffee"]
        tasks: ["coffee:dev"]

      jade:
        files: ["src/jade/**/*.jade"]
        tasks: ["jade:dev"]

      less:
        files: ["src/less/**/*.less"]
        tasks: ["less:dev"]

      images:
        files: ["src/resources/**/*.*"]
        tasks: ["copy:dev"]

    karma:
      options:
        singleRun: true
        autoWatch: false
        browsers: ["PhantomJS"]
      unit:
        configFile: "karma-unit.conf.js"

    clean:
      options: { force: true }
      dev: ["#{targetDev}/"]
      prod: ["#{targetProd}/"]

  require("matchdep").filterDev("grunt-*").forEach grunt.loadNpmTasks
  grunt.registerTask "dev", ["clean:dev", "coffee:dev", "jade:dev", "less:dev", "copy:dev"]
  grunt.registerTask "test", ["clean:dev", "coffee:dev", "coffee:test", "copy:test", "karma:unit"]
  grunt.registerTask "dist", ["dev", "clean:prod", "copy:prod", "requirejs:prod", "jade:prod", "less:prod"]
  grunt.registerTask "default", ["dev", "configureProxies", "connect:dev", "watch"]
