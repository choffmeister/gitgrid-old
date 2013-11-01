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
          baseUrl: "#{targetDev}/src"
          mainConfigFile: "#{targetDev}/src/app.js"
          name: "app"
          out: "#{targetProd}/src/app.js"
          optimize: "uglify"

    coffee:
      dev:
        expand: true
        cwd: "src"
        src: ["**/*.coffee"]
        dest: "#{targetDev}/src"
        ext: ".js"
        options:
          bare: true
      test:
        expand: true
        cwd: "test"
        src: ["**/*.coffee"]
        dest: "#{targetDev}/test"
        ext: ".js"
        options:
          bare: true

    jade:
      dev:
        files: [
          expand: true
          cwd: "resources"
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
          cwd: "resources"
          src: "**/*.jade"
          dest: "#{targetProd}"
          ext: ".html"
        ]
        options:
          pretty: false

    less:
      dev:
        files: [
          src: "resources/styles/main.less"
          dest: "#{targetDev}/styles/main.css"
        ]
        options:
          paths: ["resources/styles"]
          yuicompress: false

      prod:
        files: [
          src: "resources/styles/main.less"
          dest: "#{targetProd}/styles/main.css"
        ]
        options:
          paths: ["resources/styles"]
          yuicompress: true

    copy:
      dev:
        files: [
          src: "resources/favicon.ico"
          dest: "#{targetDev}/favicon.ico"
        ,
          expand: true
          cwd: "resources/images"
          src: "**/*.*"
          dest: "#{targetDev}/images"
        ]

      prod:
        files: [
          expand: true
          cwd: "bower_components"
          src: "**/*"
          dest: "#{targetDev}/bower_components"
        ,
          expand: true
          cwd: "bower_components"
          src: "**/*"
          dest: "#{targetProd}/bower_components"
        ,
          src: "resources/favicon.ico"
          dest: "#{targetProd}/favicon.ico"
        ,
          src: "resources/robots.txt"
          dest: "#{targetProd}/robots.txt"
        ,
          src: "resources/.htaccess"
          dest: "#{targetProd}/.htaccess"
        ,
          expand: true
          cwd: "resources/images"
          src: "**/*.*"
          dest: "#{targetProd}/images"
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
        files: ["src/**/*.coffee"]
        tasks: ["coffee:dev"]

      jade:
        files: ["resources/**/*.jade"]
        tasks: ["jade:dev"]

      less:
        files: ["resources/styles/**/*.less"]
        tasks: ["less:dev"]

      images:
        files: ["resources/images/**/*.*"]
        tasks: ["copy:dev"]

    karma:
      options:
        singleRun: true
        autoWatch: false
        browsers: ["Chrome", "Firefox"]
      unit:
        configFile: "karma-unit.conf.js"

    clean:
      options: { force: true }
      dev: ["#{targetDev}/"]
      prod: ["#{targetProd}/"]

  require("matchdep").filterDev("grunt-*").forEach grunt.loadNpmTasks
  grunt.registerTask "dev-build", ["clean:dev", "coffee:dev", "jade:dev", "less:dev", "copy:dev"]
  grunt.registerTask "prod-build", ["dev-build", "clean:prod", "copy:prod", "requirejs:prod", "jade:prod", "less:prod"]
  grunt.registerTask "test-build", ["dev-build", "coffee:test", "copy:test"]
  grunt.registerTask "dev-server", ["dev-build", "configureProxies", "connect:dev"]
  grunt.registerTask "test", ["test-build", "karma:unit"]
  grunt.registerTask "default", ["dev-server", "watch"]
