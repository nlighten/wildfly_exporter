package nl.nlighten.prometheus.wildfly;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Exports metrics about Jboss Wildfly JDBC datasources.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new JbossJdbcPoolExports().register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   jboss_jdbc_connections_total{pool="ExampleDS",} 10.0
 *   jboss_jdbc_connections_threadswaiting_total{pool="ExampleDS",} 0.0
 *   jboss_jdbc_connections_idle_total{pool="ExampleDS",} 8.0
 *   jboss_jdbc_connections_active_total{pool="ExampleDS",} 2.0
 *   jboss_jdbc_xacommit_total{pool="ExampleDS",} 9837.0
 *   jboss_jdbc_xarollback_total{pool="ExampleDS",} 7.0
 *   jboss_jdbc_xarecover_total{pool="ExampleDS",} 0.0
 *   jboss_jdbc_preparedstatementcache_size_total{pool="ExampleDS",} 100.0
 *   jboss_jdbc_preparedstatementcache_miss_total{pool="ExampleDS",} 0.0
 *   jboss_jdbc_preparedstatementcache_hit_total{pool="ExampleDS",} 94837.0
 * </pre>
 * <p>
 * Note that you need to enable datasource statistics to see anything
 */
public class JbossJdbcPoolExports extends Collector {

    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            // data source stats
            ObjectName filterName = new ObjectName("jboss.as:subsystem=datasources,data-source=*,statistics=pool");
            Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);
            if (mBeans.size() > 0) {
                List<String> labelList = Collections.singletonList("pool");

                GaugeMetricFamily activeCountGauge = new GaugeMetricFamily(
                        "jboss_jdbc_connections_total",
                        "Total number of connections in this pool",
                        labelList);

                GaugeMetricFamily waitCountGauge = new GaugeMetricFamily(
                        "jboss_jdbc_connections_threadswaiting_total",
                        "Number of threads waiting for connections from this pool",
                        labelList);

                GaugeMetricFamily idleCountGauge = new GaugeMetricFamily(
                        "jboss_jdbc_connections_idle_total",
                        "Number of idle connections in this pool",
                        labelList);

                GaugeMetricFamily inUseCountGauge = new GaugeMetricFamily(
                        "jboss_jdbc_connections_active_total",
                        "Number of connections in use",
                        labelList);

                CounterMetricFamily xaCommitCounter = new CounterMetricFamily(
                        "jboss_jdbc_xacommit_total",
                        "Number of xa commits",
                        labelList);

                CounterMetricFamily xaRollbackCounter = new CounterMetricFamily(
                        "jboss_jdbc_xarollback_total",
                        "Number of xa recoveries",
                        labelList);

                CounterMetricFamily xaRecoverCounter = new CounterMetricFamily(
                        "jboss_jdbc_xarecover_total",
                        "Number of xa recoveries",
                        labelList);

                for (final ObjectInstance mBean : mBeans) {

                    activeCountGauge.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Integer) server.getAttribute(mBean.getObjectName(), "ActiveCount")).doubleValue());

                    waitCountGauge.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Integer) server.getAttribute(mBean.getObjectName(), "WaitCount")).doubleValue());

                    idleCountGauge.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Integer) server.getAttribute(mBean.getObjectName(), "IdleCount")).doubleValue());

                    inUseCountGauge.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Integer) server.getAttribute(mBean.getObjectName(), "InUseCount")).doubleValue());

                    xaCommitCounter.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "XACommitCount")).doubleValue());

                    xaRollbackCounter.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "XARollbackCount")).doubleValue());

                    xaRecoverCounter.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "XARecoverCount")).doubleValue());
                }
                mfs.add(activeCountGauge);
                mfs.add(waitCountGauge);
                mfs.add(inUseCountGauge);
                mfs.add(xaCommitCounter);
                mfs.add(xaRollbackCounter);
                mfs.add(xaRecoverCounter);
            }

            // jdbc stats
            filterName = new ObjectName("jboss.as:subsystem=datasources,data-source=*,statistics=jdbc");
            mBeans = server.queryMBeans(filterName, null);
            if (mBeans.size() > 0) {
                List<String> labelList = Collections.singletonList("pool");

                GaugeMetricFamily preparedStatementCacheCurrentSizeGauge = new GaugeMetricFamily(
                        "jboss_jdbc_preparedstatementcache_size_total",
                        "Prepared statement cache size",
                        labelList);

                CounterMetricFamily preparedStatementCacheMissCounter = new CounterMetricFamily(
                        "jboss_jdbc_preparedstatementcache_miss_total",
                        "Prepared statement cache miss count",
                        labelList);

                CounterMetricFamily preparedStatementCacheHitCounter = new CounterMetricFamily(
                        "jboss_jdbc_preparedstatementcache_hit_total",
                        "Prepared statement cache hit count",
                        labelList);

                for (final ObjectInstance mBean : mBeans) {
                    preparedStatementCacheCurrentSizeGauge.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Integer) server.getAttribute(mBean.getObjectName(), "PreparedStatementCacheCurrentSize")).doubleValue());

                    preparedStatementCacheMissCounter.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "PreparedStatementCacheMissCount")).doubleValue());

                    preparedStatementCacheHitCounter.addMetric(
                            Collections.singletonList(mBean.getObjectName().getKeyProperty("data-source")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "PreparedStatementCacheHitCount")).doubleValue());

                }
                mfs.add(preparedStatementCacheCurrentSizeGauge);
                mfs.add(preparedStatementCacheMissCounter);
                mfs.add(preparedStatementCacheHitCounter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mfs;
    }
}

