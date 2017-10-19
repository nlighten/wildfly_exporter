package nl.nlighten.prometheus.wildfly;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;

/**
 * Metrics servlet for Wildfly
 */
@WebServlet("/")
public class WildflyMetricsServlet extends MetricsServlet {
    @Override
    public void init(ServletConfig config) {
        DefaultExports.initialize();
        new JbossJdbcPoolExports().register();
        new UndertowExports().register();
        new InfinispanExports().register();
        new WildflyVersionExports().register();
    }
}
