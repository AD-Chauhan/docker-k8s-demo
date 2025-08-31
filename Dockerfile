FROM openjdk:21.0.8-jdk-slim
EXPOSE 8090
ENV PORT 8090
ADD /build/libs/docker-k8s-demp-0.0.1-SNAPSHOT.jar docker-k8s-demo.jar
ENTRYPOINT ["java","-jar","/docker-k8s-demo.jar"]