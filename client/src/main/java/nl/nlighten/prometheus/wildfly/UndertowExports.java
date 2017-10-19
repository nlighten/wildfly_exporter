package nl.nlighten.prometheus.wildfly;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Exports undertow session and http-listener related metrics.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new UndertowExports().register();
 * }
 * </pre>
 * Example metrics being exported:
 * <pre>
 *   undertow_listener_request_total{server="default-server",listener="default",} 11000.0
 *   undertow_listener_error_total{server="default-server",listener="default",} 3.0
 *   undertow_listener_processingtime_nanos{server="default-server",listener="default",} 0.0
 *   undertow_listener_sent_bytes{server="default-server",listener="default",} 5056098.0
 *   undertow_listener_received_bytes{server="default-server",listener="default",} 406234.0
 *   undertow_session_active_total{deployment="foo",context="/bar",} 53.0
 *   undertow_session_rejected_total{deployment="foo",context="/bar",} 0.0
 *   undertow_session_created_total{deployment="foo",context="/bar",} 343.0
 *   undertow_session_expired_total{deployment="foo",context="/bar",} 290.0
 *   undertow_session_alivetime_avg_seconds{deployment="foo",context="/bar",} 2304482.0
 *   undertow_session_alivetime_max_seconds{deployment="foo",context="/bar",} 8983372.0
 *
 *
 * </pre>
 * Note that you need to enable undertow statistics to see anything
 */
public class UndertowExports extends Collector {

    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        try {
            final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName filterName = new ObjectName("jboss.as:subsystem=undertow,server=*,http-listener=*");
            Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

            if (mBeans.size() > 0) {

                GaugeMetricFamily errorCountGauge = new GaugeMetricFamily(
                        "undertow_listener_error_total",
                        "Number of errors for this http-listener",
                        Arrays.asList("server", "listener"));

                GaugeMetricFamily processingTimeGauge = new GaugeMetricFamily(
                        "undertow_listener_processingtime_nanos",
                        "Total processing time for this http-listener",
                        Arrays.asList("server", "listener"));

                GaugeMetricFamily requestCountGauge = new GaugeMetricFamily(
                        "undertow_listener_request_total",
                        "Total request count for this http-listener",
                        Arrays.asList("server", "listener"));

                GaugeMetricFamily bytesSentGauge = new GaugeMetricFamily(
                        "undertow_listener_sent_bytes",
                        "Number of bytes sent by this http-listener",
                        Arrays.asList("server", "listener"));

                GaugeMetricFamily bytesReceivedGauge = new GaugeMetricFamily(
                        "undertow_listener_received_bytes",
                        "Number of bytes received by this http-listener",
                        Arrays.asList("server", "listener"));

                for (final ObjectInstance mBean : mBeans) {

                    errorCountGauge.addMetric(
                            Arrays.asList(mBean.getObjectName().getKeyProperty("server"), mBean.getObjectName().getKeyProperty("http-listener")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "errorCount")).doubleValue());

                    processingTimeGauge.addMetric(
                            Arrays.asList(mBean.getObjectName().getKeyProperty("server"), mBean.getObjectName().getKeyProperty("http-listener")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "processingTime")).doubleValue());

                    requestCountGauge.addMetric(
                            Arrays.asList(mBean.getObjectName().getKeyProperty("server"), mBean.getObjectName().getKeyProperty("http-listener")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "requestCount")).doubleValue());

                    bytesSentGauge.addMetric(
                            Arrays.asList(mBean.getObjectName().getKeyProperty("server"), mBean.getObjectName().getKeyProperty("http-listener")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "bytesSent")).doubleValue());

                    bytesReceivedGauge.addMetric(
                            Arrays.asList(mBean.getObjectName().getKeyProperty("server"), mBean.getObjectName().getKeyProperty("http-listener")),
                            ((Long) server.getAttribute(mBean.getObjectName(), "bytesReceived")).doubleValue());
                }
                mfs.add(errorCountGauge);
                mfs.add(processingTimeGauge);
                mfs.add(requestCountGauge);

                filterName = new ObjectName("jboss.as.expr:deployment=*,subsystem=undertow");
                mBeans = server.queryMBeans(filterName, null);

                if (mBeans.size() > 0) {
                    GaugeMetricFamily activeSessionCountGauge = new GaugeMetricFamily(
                            "undertow_session_active_total",
                            "Number of active sessions",
                            Arrays.asList("deployment", "context"));

                    GaugeMetricFamily rejectedSessionCountGauge = new GaugeMetricFamily(
                            "undertow_session_rejected_total",
                            "Number of rejected sessions",
                            Arrays.asList("deployment", "context"));

                    GaugeMetricFamily createdSessionCountGauge = new GaugeMetricFamily(
                            "undertow_session_created_total",
                            "Number of sessions created",
                            Arrays.asList("deployment", "context"));

                    GaugeMetricFamily expiredSessionCountGauge = new GaugeMetricFamily(
                            "undertow_session_expired_total",
                            "Number of sessions expired",
                            Arrays.asList("deployment", "context"));

                    GaugeMetricFamily sessionAvgAliveTimeGauge = new GaugeMetricFamily(
                            "undertow_session_alivetime_avg_seconds",
                            "Average session alive time",
                            Arrays.asList("deployment", "context"));

                    GaugeMetricFamily sessionMaxAliveTimeGauge = new GaugeMetricFamily(
                            "undertow_session_alivetime_max_seconds",
                            "Average session alive time",
                            Arrays.asList("deployment", "context"));

                    for (final ObjectInstance mBean : mBeans) {
                        String deployment = mBean.getObjectName().getKeyProperty("deployment");
                        String context = (String) server.getAttribute(mBean.getObjectName(), "contextRoot");

                        activeSessionCountGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "activeSessions")));

                        rejectedSessionCountGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "rejectedSessions")));

                        createdSessionCountGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "sessionsCreated")));

                        expiredSessionCountGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "expiredSessions")));

                        sessionAvgAliveTimeGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "sessionAvgAliveTime")));

                        sessionMaxAliveTimeGauge.addMetric(
                                Arrays.asList(deployment, context),
                                Double.parseDouble((String) server.getAttribute(mBean.getObjectName(), "sessionMaxAliveTime")));
                    }
                    mfs.add(activeSessionCountGauge);
                    mfs.add(rejectedSessionCountGauge);
                    mfs.add(createdSessionCountGauge);
                    mfs.add(expiredSessionCountGauge);
                    mfs.add(sessionAvgAliveTimeGauge);
                    mfs.add(sessionMaxAliveTimeGauge);

                }

            }
        } catch (AttributeNotFoundException e) {
            // ignore, can happen during server startup.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mfs;
    }
}

