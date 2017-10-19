package nl.nlighten.prometheus.wildfly;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Exports wildfly product name and version.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   new WildflyVersionExports().register();
 * }
 * </pre>
 *
 * Example metrics being exported:
 * <pre>
 *   wildfly_info{name="WildFly Full",version="10.1.0.Final",} 1.0
 * </pre>
 */
public class WildflyVersionExports extends Collector {

    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();
        try {
            final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            final ObjectName filterName = new ObjectName("jboss.as:management-root=server");
            Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);
            if (mBeans.size() > 0 ) {

                GaugeMetricFamily wildflyInfo = new GaugeMetricFamily(
                        "wildfly_info",
                        "Wildfly version info",
                        Arrays.asList("name", "version"));

                for (final ObjectInstance mBean : mBeans) {
                    wildflyInfo.addMetric(Arrays.asList((String) server.getAttribute(mBean.getObjectName(), "productName"), (String) server.getAttribute(mBean.getObjectName(), "productVersion")), 1);
                }
                mfs.add(wildflyInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mfs;
    }
}

