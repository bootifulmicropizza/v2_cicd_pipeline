def call(Map params) {
    
    sh 'npm --userconfig /var/jenkins/npm_cache install'
    sh 'ng build --prod'
    stash name: 'applicationfiles', excludes: 'node_modules/**/*'
}
