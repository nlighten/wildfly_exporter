package nl.nlighten.prometheus.wildfly;

import org.infinispan.Cache;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Simple servlet used to generate metrics for integration tests
 */
@WebServlet("/")
public class TestServlet extends HttpServlet {

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource dataSource;

    @Resource(lookup = "java:jboss/infinispan/myLocalCache")
    private Cache<String,String> myLocalCache;

    @Resource(lookup = "java:jboss/infinispan/myReplicatedCache")
    private Cache<String,String> myCache;




    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // deliberately create a session to test session metrics
        req.getSession();

        // add something to caches
        myCache.put("key_1", "foo");
        myLocalCache.put("key_1", "foo");

        // generate cache hits
        myCache.get("key_1");
        myLocalCache.get("key_1");

        // generate cache misses
        myCache.get("key_2");
        myLocalCache.get("key_2");

        // dummy database select
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from NON_EXISTING_TABLE");
            rs.close();

        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        PrintWriter out = resp.getWriter();
        out.println("Hello world!");
        out.close();
    }
}
