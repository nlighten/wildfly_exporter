# Prometheus Wildlfy Exporter
A set of collectors that can be used to monitor Jboss Wildfly instances. This library has been tested with Wildfly 10.1.0.


### Available metrics
The following Wildfly related metrics are provided:

* Undertow processor metrics (request count, bytes send/received, etc.)
* Undertow session metrics
* Jboss jdbc connection pool metrics
* Infinispan cache metrics
* Wildfly version info
* Servlet metrics (response times, status codes) 


### Using this library
To use this library you need to perform the following steps:

* add the Wildfly Prometheus Exporter module jars to Wildfly's modules directory
* configure the Wildfly Prometheus Exporter module as global module 
* deploy the Wildfly Prometheus Exporter Servlet 
* enable metrics for the Wildlfy components you are interested in

Details are available in the paragraphs below.   

### Add exporter module jars to Wildfly
Download the latest [wildfly_exporter_module](https://search.maven.org/search?q=a:wildfly_exporter_module) from the maven repository and extract it in Wildfly's modules directory.
E.g. for version 0.0.2:

```
cp wildfly_exporter_module-0.0.2.jar /opt/jboss/wildfly/modules/.
cd /opt/jboss/wildfly/modules 
jar -xvf wildfly_exporter_module-0.0.2.jar 
rm -rf META-INF 
rm -f wildfly_exporter_module-0.0.2.jar

```

### Configure exporter module as global module
In Wildfly's config file (standalone.xml, standalone-ha.xml) add the following directly under ``<subsystem xmlns="urn:jboss:domain:ee:4.0">``:

```xml
<global-modules>
  <module name="nl.nlighten.prometheus.wildfly" services="true" meta-inf="true"/>
</global-modules>

``` 
or using the Jboss cli:

```
/subsystem=ee/:write-attribute(name=global-modules,value=[\
   {"name" => "nl.nlighten.prometheus.wildfly", "meta-inf" => "true", "services" => "true"}\
])
```

### Deploy exporter servlet
Download the latest [wildfly exporter servlet](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22wildfly_exporter_servlet%22) from the maven repository and copy it to Wildfly's deployments directory and rename it to ``metrics.war``.
E.g. for version 0.0.1-SNAPSHOT:

```
cp wildfly_exporter_servlet-0.0.1-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/metrics.war
```

### Enable metrics for Wildfly components
Most Wildfly components require explicit configuration before they expose metrics. Depending on the components you are interested in you need to configure one or more of the following: 


#### Undertow metrics
To enable undertow related metrics add ``statistics-enabled="true"`` to Wildfly's configuration section for the undertow subsystem: 

```xml
<subsystem xmlns="urn:jboss:domain:undertow:3.1" statistics-enabled="true">
``` 
or using the Jboss cli:

```
/subsystem=undertow:write-attribute(name=statistics-enabled, value=true)

```

#### Jdbc metrics
To enable jdbc related metrics add ``statistics-enabled="true"`` to Wildfly's datasource configuration section: 

```xml
<datasource jndi-name="java:jboss/datasources/ExampleDS" pool-name="ExampleDS" enabled="true"
            use-java-context="true" statistics-enabled="true">
      
```

or using the Jboss cli:

```
/subsystem=datasources/data-source=ExampleDS:write-attribute(name=statistics-enabled,value=true)  
```

#### Infinispan metrics
To enable infinispan related metrics add ``statistics-enabled="true"`` to the Infinispan cache configuration section, e.g.: 

````xml
<cache-container name="myCacheContainer" default-cache="myLocalCache">
  <transport lock-timeout="60000"/>
   <local-cache name="myLocalCache" statistics-enabled="true" jndi-name="java:jboss/infinispan/myLocalCache">
      <eviction strategy="LRU" max-entries="10000"/>
      <expiration lifespan="604800"/>
      <file-store path="myLocalCacheStore" passivation="false" preload="true" shared="false"/>
   </local-cache>
   <replicated-cache name="myReplicatedCache" statistics-enabled="true" jndi-name="java:jboss/infinispan/myReplicatedCache" mode="SYNC">
      <transaction mode="NON_XA"/>
      <locking isolation="REPEATABLE_READ"/>
      <eviction strategy="LRU" max-entries="10000"/>
      <expiration lifespan="604800"/>
      <file-store path="myReplicatedCacheStore" passivation="false" preload="true" shared="false"/>
   </replicated-cache>
 </cache-container>
````

or using the Jboss cli:

```
/subsystem=infinispan/cache-container=myCacheContainer/local-cache=myLocalCache:write-attribute(name=statistics-enabled,value=true)  
/subsystem=infinispan/cache-container=myCacheContainer/replicated-cache=myLocalCache:write-attribute(name=statistics-enabled,value=true)  
```

### Servlet metrics configuration
By default you should not need to change the configuration for the collection of system metrics, but you can configure the following system properties if needed:


|Property | Default | Description |
| ------- | ------- | ----------- |
| prometheus.wildfly.filter.blacklist | /metrics | Servlet context urls that should not be instrumented for metrics collection |
| prometheus.wildfly.filter.buckets | .01, .05, .1, .25, .5, 1, 2.5, 5, 10, 30 | The buckets used for the [histogram](https://prometheus.io/docs/concepts/metric_types/#histogram) used for collecting response times |



### Javadocs
There are canonical examples defined in the class definition Javadoc of the client packages.


