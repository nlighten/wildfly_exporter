package nl.nlighten.prometheus.wildfly;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Servlet metrics integration test
 */
public class ServletMetricsTest extends AbstractIntegrationTest {

    @Test
    public void testLocalCacheInfinispanMetrics() {
        assertThat(metrics.get("servlet_request_seconds_bucket{context=\"/test\",method=\"GET\",le=\"+Inf\",}"), is(1.0));
        assertThat(metrics.get("servlet_request_seconds_count{context=\"/test\",method=\"GET\",}"), is(1.0));
        assertThat(metrics.get("servlet_request_seconds_sum{context=\"/test\",method=\"GET\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("servlet_request_concurrent_total{context=\"/test\",}"), is(0.0));
        assertThat(metrics.get("servlet_request_concurrent_total{context=\"/test\",}"), is(0.0));
        assertThat(metrics.get("servlet_response_status_total{context=\"/test\",status=\"200\",}"), is(1.0));
    }

}