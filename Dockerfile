FROM openjdk:21
EXPOSE 8090
ENV PORT 8090
ADD /build/libs/docker-k8s-demo-0.0.1-SNAPSHOT.jar docker-k8s-demo.jar
ENTRYPOINT ["java","-jar","/docker-k8s-demo.jar"]

