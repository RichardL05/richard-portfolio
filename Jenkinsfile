pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
    parameters {
        string(name: 'DOCKERHUB_USER', defaultValue: 'rd9263', description: 'Your Docker Hub username')
        string(name: 'K8S_SERVER', defaultValue: '', description: 'Your Kubernetes API URL, e.g. https://PRIVATE_IP:6443')
    }
    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        NAMESPACE = 'richard'
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Validate Configuration') {
            steps {
                script {
                    def user = params.DOCKERHUB_USER.trim()
                    def server = params.K8S_SERVER.trim()
                    if (!(user ==~ /[a-z0-9][a-z0-9_-]*/)) {
                        error('Enter your Docker Hub username in DOCKERHUB_USER.')
                    }
                    if (!(server ==~ /https:\/\/[a-zA-Z0-9.-]+:[0-9]+/)) {
                        error('Enter K8S_SERVER as https://HOST:PORT.')
                    }
                    env.IMAGE = "${user}/richard-portfolio"
                    env.TAG = "${env.BUILD_NUMBER}"
                    env.K8S_SERVER = server
                }
            }
        }
        stage('Compile') {
            steps { sh 'mvn -B compile' }
        }
        stage('Test') {
            steps { sh 'mvn -B test' }
        }
        stage('Trivy File System Scan') {
            steps { sh 'trivy fs --format table -o trivy-fs-report.txt .' }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''"$SCANNER_HOME/bin/sonar-scanner" \\
                        -Dsonar.projectName=RichardPortfolio \\
                        -Dsonar.projectKey=RichardPortfolio \\
                        -Dsonar.java.binaries=target/classes \\
                        -Dsonar.sources=src/main \\
                        -Dsonar.tests=src/test \\
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'''
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true, credentialsId: 'sonar-token'
                }
            }
        }
        stage('Build Package') {
            steps { sh 'mvn -B package -DskipTests' }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t "$IMAGE:$TAG" -t "$IMAGE:latest" .'
            }
        }
        stage('Trivy Image Scan') {
            steps { sh 'trivy image --format table -o trivy-image-report.txt "$IMAGE:$TAG"' }
        }
        stage('Push Docker Image') {
            steps {
                withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                    sh 'docker push "$IMAGE:$TAG" && docker push "$IMAGE:latest"'
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                withKubeConfig(clusterName: 'kubernetes', credentialsId: 'k8-cred',
                        namespace: 'richard', restrictKubeConfigAccess: true,
                        serverUrl: "${env.K8S_SERVER}") {
                    sh '''sed "s|image: .*richard-portfolio:.*|image: $IMAGE:$TAG|" k8s/deployment-service.yaml > deployment-rendered.yaml
                        kubectl apply -n "$NAMESPACE" -f deployment-rendered.yaml
                        kubectl rollout status deployment/richard-portfolio -n "$NAMESPACE" --timeout=180s'''
                }
            }
        }
        stage('Verify Deployment') {
            steps {
                withKubeConfig(clusterName: 'kubernetes', credentialsId: 'k8-cred',
                        namespace: 'richard', restrictKubeConfigAccess: true,
                        serverUrl: "${env.K8S_SERVER}") {
                    sh 'kubectl get pods -o wide -n "$NAMESPACE" && kubectl get svc -n "$NAMESPACE"'
                }
            }
        }
    }
    post {
        always { archiveArtifacts artifacts: 'trivy-*.txt', allowEmptyArchive: true }
    }
}
