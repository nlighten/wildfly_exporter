package nl.nlighten.prometheus.wildfly;

public class Util {

    static String sanitizeLabel (String label) {
        return label.replace("\"","");
    }
}
