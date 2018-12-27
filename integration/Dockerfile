FROM jboss/wildfly

#RUN curl -o /opt/jboss/wildfly/standalone/deployments/hawtio.war https://oss.sonatype.org/content/repositories/public/io/hawt/hawtio-no-slf4j/1.4.66/hawtio-no-slf4j-1.4.66.war
COPY target/wildfly_exporter_servlet-${exporter.version}.war /opt/jboss/wildfly/standalone/deployments/metrics.war
COPY target/wildfly_exporter_testservlet-${exporter.version}.war /opt/jboss/wildfly/standalone/deployments/test.war
COPY target/wildfly_exporter_module-${exporter.version}.jar /opt/jboss/wildfly/modules
COPY src/test/resources/standalone-ha.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
RUN cd /opt/jboss/wildfly/modules && jar -xvf wildfly_exporter_module-${exporter.version}.jar && rm -rf META-INF && rm -f wildfly_exporter_module-0.0.1-SNAPSHOT.jar

