'use strict';

module.exports = function(grunt) {

  grunt.loadNpmTasks('grunt-contrib-jshint');
  require('load-grunt-tasks')(grunt);

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    jshint: {
	  files: ['Gruntfile.js', 'src/main/javascript/*.js','src/test/javascript/*.js']
    },
        
    bower: {
      install: {
        options: {
          targetDir: 'bower_components',
          install: true,
          verbose: true,
          cleanTargetDir: true,
          cleanBowerDir: false,
          bowerOptions: {},
          copy: true
        }
      }
    },

    uglify: {
      build: {
        files: {
          'build/sample-component.min.js': ['src/main/javascript/sample-component.js']
        }
      }
    },

    copy: {
      main: {
        files: [{
          expand: true,
          cwd: 'src/main/javascript/',
          src: 'sample-component.js',
          dest: 'build/'
        }]
      }
    },

    clean: ['build/sample-component.js', '.bower-cache', '.bower-registry', '.bower-tmp'],

    karma: {
      unit: {
        configFile: 'src/test/javascript/karma.conf.js'
      }
    },

  });

  grunt.registerTask('default', ['jshint']);
  
  grunt.registerTask('build', function() {
    grunt.task.run([
      'clean',    
      'bower:install',
      'uglify', 
      'copy', 
//      'karma:unit',
    ]);
  });

};
