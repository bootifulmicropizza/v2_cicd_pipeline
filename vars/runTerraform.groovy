def call(Map params) {

    if (!params.keySet().contains("artifactName")) {
        throw new IllegalArgumentException("artifactName is a mandatory parameter.");
    }
        
    //withAWS(credentials: "JENKINS_IAM_USER", region: env.AWS_REGION) {
    withAWS(role: "BootifulMicroPizzaTerraformRole",
            roleAccount: params.awsAccount,
            principalArn: "arn:aws:iam::" + params.awsAccount + ":role/BootifulMicroPizzaTerraformRole",
            roleSessionName: "jenkins-terraform",
            region: "eu-west-1") {
        //  && (terraform workspace new ${params.artifactName} || terraform workspace select ${params.artifactName})
        sh '''
            #aws sts assume-role --role-arn arn:aws:iam::''' + params.awsAccount + ''':role/BootifulMicroPizzaTerraformRole --role-session-name jenkins-terraform --duration-seconds 900
            ELB=`aws elb describe-load-balancers --query "LoadBalancerDescriptions[0].DNSName" --output text`
            cd src/main/iac
            rm -rf .terraform .terraform.lock.hcl
            export TF_LOG=debug
            terraform init -backend-config="bucket=bootifulmicropizza-''' + params.environment + '''-tf-state"
            terraform apply -var="environment=''' + params.environment + '''" -var="awsAccount=''' + params.awsAccount + '''" -var="loadbalancer_arn=$ELB" --auto-approve
        '''
    }
}
