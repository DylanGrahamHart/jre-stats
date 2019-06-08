./gradlew build
gsutil cp build/libs/*.jar gs://jre-stats-jars/derp.jar
gcloud compute instances stop instance-1
gcloud compute instances start instance-1

