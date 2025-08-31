pipeline{
	agent any
	environment{
	   DOCKER_HUB_CREDENTIALS = 'DockerHubId'
	   DOCKER_IMAGE_NAME = 'ad1989/docker-k8s'  // Set a name for the Docker image
	   DOCKER_TAG = '1'         // Tag for the Docker image (e.g., "1")
		   
	   
	}
	stages{
	stage("Checkout from GitHub"){
		steps{
			git 'https://github.com/AD-Chauhan/docker-k8s-app.git'
		}
	}
	
	stage("Build With Gradle"){
	 steps {
		        sh 'chmod +x ./gradlew'
                sh './gradlew build' 
        }
	}
	
	 stage("Build Image"){
		 steps{
		 script {
	     // Build the Docker image using the Dockerfile
                    docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}")
		}
	  }
	}
	
	 stage("Push Image in Hub"){
		 steps{
		 script {
                    // Push the Docker image to Docker Hub or any Docker registry
                    withDockerRegistry([credentialsId: 'DOCKER_HUB_CREDENTIALS', url: 'https://index.docker.io/v1/']) {
                        docker.image("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}").push()
                    }
		}
	  }
	}
	
	  stage('Deploying container to Kubernetes') {
	      steps {
		  script {
	       withKubeConfig([credentialsId: 'k8sId']) {
                 bat 'kubectl delete svc docker-k8s-service'
			     bat 'kubectl apply -f docker-k8s-service.yaml'
          }
		  }
	      }
	  
	}
	
  }
  post {
	  always {
		  echo 'Cleaning up...'
		  // Clean up or additional post-processing
	  }

	  success {
		  echo 'Build and Docker image creation successful!'
	  }

	  failure {
		  echo 'Something went wrong during the build!'
	  }
  }
}
	