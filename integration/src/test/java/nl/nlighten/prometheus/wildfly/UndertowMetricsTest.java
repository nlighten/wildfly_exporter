package nl.nlighten.prometheus.wildfly;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


/**
 * Undertow metrics integration test
 */
public class UndertowMetricsTest extends AbstractIntegrationTest {

    @Test
    public void testUndertowListenerMetrics() {
        assertThat(metrics.get("undertow_listener_error_total{server=\"default-server\",listener=\"default\",}"), is(0.0));
        assertThat(metrics.get("undertow_listener_processingtime_nanos{server=\"default-server\",listener=\"default\",}"), is(0.0)); // This seems to be a bug in undertow as it is always returning 0. When fixed this assert will fail.
        assertThat(metrics.get("undertow_listener_request_total{server=\"default-server\",listener=\"default\",}"), is(greaterThan(0.0)));
    }

    @Test
    public void testUndertowSessionMetrics() {
        assertThat(metrics.get("undertow_session_active_total{deployment=\"test.war\",context=\"/test\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("undertow_session_rejected_total{deployment=\"test.war\",context=\"/test\",}"), is(notNullValue()));
        assertThat(metrics.get("undertow_session_expired_total{deployment=\"test.war\",context=\"/test\",}"), is(notNullValue()));
        assertThat(metrics.get("undertow_session_alivetime_avg_seconds{deployment=\"test.war\",context=\"/test\",}"), is(notNullValue()));
        assertThat(metrics.get("undertow_session_alivetime_max_seconds{deployment=\"test.war\",context=\"/test\",}"), is(notNullValue()));
    }

}