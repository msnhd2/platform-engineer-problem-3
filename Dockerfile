FROM quay.io/wildfly/wildfly:latest-jdk17

ADD target/problem3-0.0.1-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/app.war
#RUN /opt/jboss/wildfly/bin/add-user.sh admin admin
EXPOSE 8080
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-c","standalone-microprofile.xml"]