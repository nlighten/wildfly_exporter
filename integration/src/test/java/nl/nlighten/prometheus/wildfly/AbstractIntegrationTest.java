package nl.nlighten.prometheus.wildfly;


import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Abstract integration test
 */
public abstract class AbstractIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    static Map<String, Double> metrics;
    private static int dockerPort;

    @BeforeClass
    public static void setUp() throws Exception {
        if (metrics == null) {
            dockerPort = Integer.parseInt(System.getProperty("dockerPort", "8888"));
            doTestRequest();
            metrics = getMetrics();
        }
    }



    private static Map<String, Double> getMetrics() {
        // send GET request
        Map<String, Double> metrics = new HashMap<>();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:"+ dockerPort + "/metrics/").openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (!inputLine.startsWith("#")) {
                        String metricName = inputLine.substring(0, inputLine.lastIndexOf(" "));
                        Double metricValue = Double.parseDouble(inputLine.substring(inputLine.lastIndexOf(" ") + 1, inputLine.length()));
                        metrics.put(metricName, metricValue);
                    }
                }
                in.close();
            } else {
                LOG.error("Metrics call failed with status code " + urlConnection.getResponseCode());
                return null;
            }
            urlConnection.getInputStream().close();
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metrics;
    }


    private static void doTestRequest() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://localhost:" + dockerPort + "/test/").openConnection();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOG.error("Call to test servlet failed with status code " + urlConnection.getResponseCode());
            }
            urlConnection.getInputStream().close();
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}