package nl.nlighten.prometheus.wildfly;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Jdbc metrics integration test
 */
public class JbossJdbcMetricsTest extends AbstractIntegrationTest {


    @Test
    public void testJbossJdbcConnectionsMetrics() {
        assertThat(metrics.get("jboss_jdbc_connections_total{pool=\"ExampleDS\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("jboss_jdbc_connections_threadswaiting_total{pool=\"ExampleDS\",}"), is(0.0));
        assertThat(metrics.get("jboss_jdbc_connections_active_total{pool=\"ExampleDS\",}"), is(0.0));
    }

    @Test
    public void testJbossJdbcXAMetrics() {
        // we only test if metrics exists
        assertThat(metrics.get("jboss_jdbc_xacommit_total{pool=\"ExampleDS\",}"), is(notNullValue()));
        assertThat(metrics.get("jboss_jdbc_xarollback_total{pool=\"ExampleDS\",}"), is(notNullValue()));
        assertThat(metrics.get("jboss_jdbc_xarecover_total{pool=\"ExampleDS\",}"), is(notNullValue()));
    }

    @Test
    public void testJbossPreparedStatementCacheMetrics() {
        // we only test if metrics exists
        assertThat(metrics.get("jboss_jdbc_preparedstatementcache_size_total{pool=\"ExampleDS\",}"), is(notNullValue()));
        assertThat(metrics.get("jboss_jdbc_preparedstatementcache_hit_total{pool=\"ExampleDS\",}"), is(notNullValue()));
        assertThat(metrics.get("jboss_jdbc_preparedstatementcache_miss_total{pool=\"ExampleDS\",}"), is(notNullValue()));
    }
}