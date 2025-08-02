package no.nr.ant;

/**
 * JcsClassCache is a java bean used by HibernateConfigFileTask.
 *
 * @author Per Thomas Jahr, perja at nr.no
 * @version $Id$
 */
public class JcsClassCache {
    private String classname;
    private String region;
    private String usage;

    public JcsClassCache() {
    }

    public String getClassname() {
        return classname;
    }

    public String getRegion() {
        return region;
    }

    public String getUsage() {
        return usage;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setUsage(HibernateConfigFileTask.Usage usage) {
        this.usage = usage.getValue();
    }
}
