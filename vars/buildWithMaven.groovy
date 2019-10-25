def call(Map params) {
    
    sh "mvn -Dmaven.repo.local=/var/jenkins/m2_cache clean package"
    stash 'applicationfiles'
}
