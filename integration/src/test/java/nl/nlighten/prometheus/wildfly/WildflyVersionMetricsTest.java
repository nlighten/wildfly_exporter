package nl.nlighten.prometheus.wildfly;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Wildfly version metric integration test
 */
public class WildflyVersionMetricsTest extends AbstractIntegrationTest {

    @Test
    public void testWildflyVersionMetric() {
        assertThat(metrics.get("wildfly_info{name=\"WildFly Full\",version=\"10.1.0.Final\",}"), is(1.0));
    }
}