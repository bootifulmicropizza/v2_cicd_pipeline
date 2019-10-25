def call(Map params) {

    if (!params.keySet().contains("artifactName")) {
        throw new IllegalArgumentException("artifactName is a mandatory parameter.");
    }

    pipeline {
        agent {
            label 'buildtools'
        }

        environment {
            ECR_REPO = "337036170088.dkr.ecr.eu-west-1.amazonaws.com"
            AWS_REGION = "eu-west-1"
        }

        stages {
            stage ('Build') {
                steps {
                    buildWithAngular(params)
                }
            }

            stage ('Package') {
                agent {
                    label 'docker'
                }
                steps {
                    buildAndPublishDockerImage(params)
                }
            }

            stage ('Deploy to Dev') {
                parallel {
                    stage ('Infrastructure') {
                        steps {
                            script {
                               params.environment = "dev"
                               params.awsAccount = "255580044180"
                            }
                            runTerraform(params)
                        }
                    }

                    stage ('Code') {
                        steps {
                            script {
                               params.environment = "dev"
                            }
                            deploy(params)
                        }
                    }
                }
            }

            stage ('Deploy to Prod') {
                parallel {
                    stage ('Infrastructure') {
                        steps {
                            script {
                               params.environment = "prod"
                               params.awsAccount = "201655463889"
                            }
                            runTerraform(params)
                        }
                    }

                    stage ('Code') {
                        steps {
                            script {
                               params.environment = "prod"
                            }
                            deploy(params)
                        }
                    }
                }
            }
        }
    }
}
