def call(Map params) {

    if (!params.keySet().contains("artifactName")) {
        throw new IllegalArgumentException("artifactName is a mandatory parameter.");
    }
        
    withAWS(credentials: "JENKINS_IAM_USER", region: env.AWS_REGION) {
        sh "aws ecr get-login-password --region ${env.AWS_REGION} | docker login --username AWS --password-stdin ${env.ECR_REPO}"
        script {
            unstash 'applicationfiles'
            def gitHash = sh(script: "git rev-parse HEAD", returnStdout: true).trim() as String
            def image = docker.build("${env.ECR_REPO}/${params.artifactName}:latest")
            image.push()
            image.push(gitHash)
        }
    }
}
