var spawn = require('child_process').spawn;
var fs = require('fs');
var gulp = require('gulp');

var builtWebapp = './src/main/webapp/dist/spa/**';
var dest = './src/main/resources/static/'

gulp.task('default', function(done) {
  spawn('npm', ['install'], { cwd: './src/main/webapp', stdio: 'inherit', shell: true })
    .on('close', () => {
    	spawn('npm', ['run-script', 'build'], {cwd: './src/main/webapp', stdio: 'inherit', shell: true}).on('close', () => {
    		gulp.src(builtWebapp).pipe(gulp.dest(dest));
    		done();
    	});
    });
});