package no.nr.ant;

/**
 * JcsCollectionCache is java bean used by HibernateConfigFileTask.
 *
 * @author Per Thomas Jahr, perja at nr.no
 * @version $Id$
 */
public class JcsCollectionCache {
    private String collection;
    private String region;
    private String usage;

    public JcsCollectionCache() {
    }

    public String getCollection() {
        return collection;
    }

    public String getRegion() {
        return region;
    }

    public String getUsage() {
        return usage;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setUsage(HibernateConfigFileTask.Usage usage) {
        this.usage = usage.getValue();
    }
}
