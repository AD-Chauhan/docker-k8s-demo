pipeline{
	agent any
	environment{
	   DOCKER_IMAGE_NAME = 'ad1989/docker-k8s'  // Set a name for the Docker image
	   //DOCKER_TAG = "${env.BUILD_NUMBER}"         // Tag for the Docker image (e.g., "1")
	   DOCKER_TAG = 'latest'     
	   
		   
	   
	}
	stages{
	stage("Checkout from GitHub"){
		steps{
			git 'https://github.com/AD-Chauhan/docker-k8s-demo.git'
		}
	}
	
	stage("Build With Gradle"){
	 steps {
                bat 'gradlew build' 
        }
	}
	
	 stage("Build Image"){
		 steps{
		script {
            withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
				bat 'docker rmi ad1989/docker-k8s:latest'
                docker.build("${DOCKER_IMAGE_NAME}:${DOCKER_TAG}")
            }
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
                    withKubeConfig([credentialsId: 'KUBECONFIG_CREDENTIAL']) {
                        bat """
                            kubectl delete svc docker-k8s-service --ignore-not-found
                            kubectl apply -f docker-k8s-service.yaml
                        """
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


	