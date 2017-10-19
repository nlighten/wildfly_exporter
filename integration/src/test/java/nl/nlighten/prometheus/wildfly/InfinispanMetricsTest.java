package nl.nlighten.prometheus.wildfly;


import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Infinispan metrics integration test
 */
public class InfinispanMetricsTest extends AbstractIntegrationTest {


    @Test
    public void testLocalCacheInfinispanMetrics() {
        assertThat(metrics.get("infinispan_entries_total{name=\"myLocalCache(local)\",manager=\"myCacheContainer\",}"), is(1.0));
        assertThat(metrics.get("infinispan_hit_total{name=\"myLocalCache(local)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_miss_total{name=\"myLocalCache(local)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_hit_ratio{name=\"myLocalCache(local)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_evictions_total{name=\"myLocalCache(local)\",manager=\"myCacheContainer\",}"), is(0.0));
    }

    @Test
    public void testReplicatedCacheInfinispanMetrics() {
        assertThat(metrics.get("infinispan_entries_total{name=\"myReplicatedCache(repl_sync)\",manager=\"myCacheContainer\",}"), is(1.0));
        assertThat(metrics.get("infinispan_hit_total{name=\"myReplicatedCache(repl_sync)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_miss_total{name=\"myReplicatedCache(repl_sync)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_hit_ratio{name=\"myReplicatedCache(repl_sync)\",manager=\"myCacheContainer\",}"), is(greaterThan(0.0)));
        assertThat(metrics.get("infinispan_evictions_total{name=\"myReplicatedCache(repl_sync)\",manager=\"myCacheContainer\",}"), is(0.0));
    }

}