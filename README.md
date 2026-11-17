# Richard - DevOps Engineer

Personal portfolio for Richard, with **2 years of experience**, using the reference application's Spring Boot design and Kubernetes instance-information panel.

## Container image

- Docker Hub repository: [rd9263/richard-portfolio](https://hub.docker.com/r/rd9263/richard-portfolio)
- Image used by the Kubernetes manifests: `rd9263/richard-portfolio:1.0.0`
- GitHub source: [RichardL05/richard-portfolio](https://github.com/RichardL05/richard-portfolio)

## Quick Kubernetes deployment

On your configured control-plane node or administrator workstation:

```bash
git clone https://github.com/RichardL05/richard-portfolio.git
cd richard-portfolio
kubectl apply -f deploy.yaml
kubectl rollout status deployment/richard-portfolio -n richard
kubectl get pods,svc -n richard
```

`deploy.yaml` creates the namespace, Deployment and NodePort Service. Access the application at `http://WORKER_PUBLIC_IP:30080` once the Pods are ready and your network allows TCP 30080 from your client. If the image is private, configure an imagePullSecret before deploying. This command does not install Kubernetes or configure your cluster.

## Featured projects

- **Jenkins Controller-Agent Deployment on AWS:** two Ubuntu EC2 instances, SSH agent connectivity, Jenkins Freestyle job and Nginx Docker deployment.
- **Docker Services Lab on Ubuntu EC2:** Jenkins, Prometheus, SonarQube and Nginx containers, port mapping and container lifecycle practice.
- **Containerized DevOps Portfolio:** this application; Docker/Jenkins/Kubernetes configuration is included. Cluster deployment is a next step, not a claimed completed deployment.

No employer names, education, certification claims, contact addresses or employment dates have been invented.

## Run locally

Install JDK 17 or later and Maven 3.9.x, then run:

```bash
mvn test
mvn spring-boot:run
```

Open http://localhost:8080. The instance panel works locally; Kubernetes supplies Pod and node values when deployed.

After packaging, run `java -jar target/richard-portfolio.jar`. On Windows, `./run-local.ps1` also starts the built application; it uses JAVA_HOME or the portable Java runtime prepared in this workspace when available.

## Build and run with Docker

With Docker Engine running:

```bash
docker build -t richard-portfolio:local .
docker run --rm -p 8080:8080 richard-portfolio:local
```

## Personalize content

- Name, title, biography, experience: `src/main/resources/application.properties`.
- Project descriptions and skills: `src/main/resources/templates/index.html`.
- Styling: `src/main/resources/static/css/style.css`.
- Optional contact links: set `PROFILE_EMAIL`, `PROFILE_PHONE`, `PROFILE_LINKEDIN`, `PROFILE_GITHUB` environment variables, or configure their property defaults. Blank contacts and the empty contact section are hidden.
- Runtime version: optional `APP_VERSION` environment variable; default `1.0.0`.

## Deploy to Kubernetes

Prerequisite: a working cluster and a configured administrator kubeconfig. These files do not install Kubernetes, containerd or Calico.

1. The manifest is configured for `rd9263/richard-portfolio:1.0.0`. Make sure this tag has been pushed and is accessible to the cluster.
2. Build and push the exact same image reference:

```bash
docker login
docker build -t rd9263/richard-portfolio:1.0.0 .
docker push rd9263/richard-portfolio:1.0.0
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment-service.yaml -n richard
kubectl rollout status deployment/richard-portfolio -n richard
kubectl get pods,svc -n richard
```

Open `http://WORKER_PUBLIC_IP:30080` if the worker is publicly reachable and its security group permits TCP 30080 from your address. Three replicas do not require three worker nodes, but workers need sufficient resources. Private registries require image pull credentials.

Application resources: Deployment `richard-portfolio`, Service `richard-portfolio-svc`, namespace `richard`. Service port 80 forwards to application port 8080. Readiness and liveness endpoints are `/actuator/health/readiness` and `/actuator/health/liveness`.

## Jenkins pipeline

Create a Pipeline from SCM job pointing at **your own repository** and this Jenkinsfile. `checkout scm` uses that job's repository instead of the trainer's repository.

Configure:

- JDK tool `jdk17`, Maven tool `maven3`, Sonar scanner tool `sonar-scanner` and Docker tool `docker`.
- SonarQube server `sonar`, with its Jenkins webhook configured.
- Credentials `sonar-token`, `docker-cred`, and `k8-cred`.
- A Linux agent with Docker Engine access, Trivy, kubectl and connectivity to your cluster.
- Build parameters `DOCKERHUB_USER` (defaults to `rd9263`) and `K8S_SERVER` (for example, `https://PRIVATE_CONTROL_PLANE_IP:6443`).

As an administrator, create `richard` first and apply `k8s/jenkins-rbac.yaml` if using the included ServiceAccount approach. Configure `k8-cred` with the corresponding credentials and trusted cluster CA. The included token Secret is a long-lived lab mechanism; restrict and rotate its credentials. Do not commit live tokens or kubeconfigs.

The pipeline compiles, tests, scans, packages, builds and pushes a build-number image, deploys that exact image, and waits for the rollout. Sonar quality gate failures stop the pipeline. Trivy stages produce informational text reports; no vulnerability-based failure threshold is configured. Jenkins, Docker Hub and Kubernetes integration must be configured and tested in your environment.

## Source attribution

Adapted from https://github.com/Senthil6005/trainerstacks-portfolio at source commit `a1052981618f1efa4f24a37493fbea76abfa23d9`. The original visual layout and application structure are retained, with Richard's content and deployment configuration. Upstream repository content was not modified.
