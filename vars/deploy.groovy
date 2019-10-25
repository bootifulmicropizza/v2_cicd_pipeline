def call(Map params) {

    if (!params.keySet().contains("artifactName")) {
        throw new IllegalArgumentException("artifactName is a mandatory parameter.");
    }

    withCredentials([string(credentialsId: 'github', variable: 'TOKEN')]) {
        script {
            def GIT_HASH = sh(script: "git rev-parse HEAD", returnStdout: true).trim() as String
            def YAML_PATH = "bootifulmicropizza-app/overlays/${params.environment}/${params.artifactName}.yaml"
        
            sh "rm -rf gitops"
            sh "git clone https://github.com/bootifulmicropizza/gitops"
            dir("gitops") {
                sh "sed -i 's/tag:.*/tag: $GIT_HASH/g' ${YAML_PATH}"
                sh "git config user.email 'jenkins@bootifulmicropizza.com'"
                sh "git config user.name 'Jenkins'"
                sh "git add ${YAML_PATH}"
                sh "git commit --allow-empty -m 'Update image tag to $GIT_HASH'"

                sh '''
                    set +x
                    git push https://iancollington:${TOKEN}@github.com/bootifulmicropizza/gitops.git
                '''
            }
        }
    }
}
