package nl.nlighten.prometheus.wildfly;

import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.FilterInfo;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * An undertow servlet extension that adds the {@link ServletMetricsFilter}  to a deployment unless the context is blacklisted through
 * the prometheus.wildfly.filter.blacklist system property.
 */

public class MetricFilterExtension implements ServletExtension {
    private static final Logger LOG = LoggerFactory.getLogger(MetricFilterExtension.class);
    private static List<String> contextBlacklist = Arrays.asList(System.getProperty("prometheus.wildfly.filter.blacklist", "/metrics").split(","));


    @Override
    public void handleDeployment(DeploymentInfo deploymentInfo, ServletContext servletContext) {

        if (!contextBlacklist.contains(deploymentInfo.getContextPath())) {
            LOG.info("Adding metrics filter to  deployment for context " + deploymentInfo.getContextPath());
            FilterInfo metricsFilterInfo = new FilterInfo("metricsfilter", ServletMetricsFilter.class);
            metricsFilterInfo.setAsyncSupported(true);
            metricsFilterInfo.addInitParam(ServletMetricsFilter.BUCKET_CONFIG_PARAM,System.getProperty("prometheus.wildfly.filter.buckets",""));
            deploymentInfo.addFilter(metricsFilterInfo);
            deploymentInfo.addFilterUrlMapping("metricsfilter", "/*", DispatcherType.REQUEST);
        } else {
            LOG.info("Metrics filter not added to black listed context " + deploymentInfo.getContextPath());
            LOG.info(contextBlacklist.toString());
        }
    }
}