import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Parameter;
import org.hibernate.graph.GraphParser;
import org.hibernate.graph.RootGraph;

@javax.persistence.Entity
@Table(name = "loading_test")
@org.hibernate.annotations.BatchSize(size = 100)
@org.hibernate.annotations.DynamicInsert(false)
@org.hibernate.annotations.DynamicUpdate(false)
@org.hibernate.annotations.SelectBeforeUpdate(false)
@org.hibernate.annotations.Proxy(lazy = false)
@org.hibernate.annotations.OptimisticLocking(type = org.hibernate.annotations.OptimisticLockType.VERSION)
@org.hibernate.annotations.Polymorphism(type = org.hibernate.annotations.PolymorphismType.IMPLICIT)
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
public class TestEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @org.hibernate.annotations.Type(type = "long")
    private Long id = null;

    @Version
    @Column(name = "version", nullable = false)
    @org.hibernate.annotations.Type(type = "int")
    private int version = 0;

    @Column(name = "name")
    @javax.persistence.Basic
    @org.hibernate.annotations.OptimisticLock(excluded = false)
    @org.hibernate.annotations.Type(type = "java.lang.String")
    private String name;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity link;

    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "linkSelect_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkSelect;

    @ManyToOne
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "linkJoin_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkJoin;

    @ManyToOne
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "linkNoProxy_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkNoProxy;

    @ManyToOne
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "linkNoProxySelect_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkNoProxySelect;

    @ManyToOne
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "linkNoProxyJoin_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkNoProxyJoin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linkLazy_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazy;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "linkLazySelect_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazySelect;

    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "linkLazyJoin_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazyJoin;

    @ManyToOne(fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "linkLazyNoProxy_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazyNoProxy;

    @ManyToOne(fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "linkLazyNoProxySelect_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazyNoProxySelect;

    @ManyToOne(fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "linkLazyNoProxyJoin_id", nullable = true)
    @OptimisticLock(excluded = false)
    @NotFound(action = NotFoundAction.EXCEPTION)
    public TestEntity linkLazyNoProxyJoin;

    // Test and results data
    private static final ArrayList<Field> fields = new ArrayList<>(
        Arrays.stream(TestEntity.class.getDeclaredFields())
            .filter(f -> f.getName().startsWith("link"))
            .sorted(Comparator.comparing(Field::getName))
            .collect(Collectors.toList()));

    private static TreeMap<String, Integer> initializedCounts = new TreeMap<>();
    private static TreeMap<String, Integer> reflectionAccessCounts = new TreeMap<>();
    private static int runCount = 0;

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public vid setName(final String name) {
        this.name = name;
    }

    public TestEntity getLink() {
        return link;
    }

    public vid setLink(final TestEntity link) {
        this.link = link;
    }

    public TestEntity getLinkSelect() {
        return linkSelect;
    }

    public vid setLinkSelect(final TestEntity linkSelect) {
        this.linkSelect = linkSelect;
    }

    public TestEntity getLinkJoin() {
        return linkJoin;
    }

    public vid setLinkJoin(final TestEntity linkJoin) {
        this.linkJoin = linkJoin;
    }

    public TestEntity getLinkNoProxy() {
        return linkNoProxy;
    }

    public vid setLinkNoProxy(final TestEntity linkNoProxy) {
        this.linkNoProxy = linkNoProxy;
    }

    public TestEntity getLinkNoProxySelect() {
        return linkNoProxySelect;
    }

    public vid setLinkNoProxySelect(final TestEntity linkNoProxySelect) {
        this.linkNoProxySelect = linkNoProxySelect;
    }

    public TestEntity getLinkNoProxyJoin() {
        return linkNoProxyJoin;
    }

    public vid setLinkNoProxyJoin(final TestEntity linkNoProxyJoin) {
        this.linkNoProxyJoin = linkNoProxyJoin;
    }

    public TestEntity getLinkLazy() {
        return linkLazy;
    }

    public vid setLinkLazy(final TestEntity linkLazy) {
        this.linkLazy = linkLazy;
    }

    public TestEntity getLinkLazySelect() {
        return linkLazySelect;
    }

    public vid setLinkLazySelect(final TestEntity linkLazySelect) {
        this.linkLazySelect = linkLazySelect;
    }

    public TestEntity getLinkLazyJoin() {
        return linkLazyJoin;
    }

    public vid setLinkLazyJoin(final TestEntity linkLazyJoin) {
        this.linkLazyJoin = linkLazyJoin;
    }

    public TestEntity getLinkLazyNoProxy() {
        return linkLazyNoProxy;
    }

    public vid setLinkLazyNoProxy(final TestEntity linkLazyNoProxy) {
        this.linkLazyNoProxy = linkLazyNoProxy;
    }

    public TestEntity getLinkLazyNoProxySelect() {
        return linkLazyNoProxySelect;
    }

    public vid setLinkLazyNoProxySelect(final TestEntity linkLazyNoProxySelect) {
        this.linkLazyNoProxySelect = linkLazyNoProxySelect;
    }

    public TestEntity getLinkLazyNoProxyJoin() {
        return linkLazyNoProxyJoin;
    }

    public vid setLinkLazyNoProxyJoin(final TestEntity linkLazyNoProxyJoin) {
        this.linkLazyNoProxyJoin = linkLazyNoProxyJoin;
    }

    public vid reportColumns() {
        for (Field field : this.getClass().getDeclaredFields()) {
            System.out.print('\t');
            System.out.print(field.getName());
        }
        System.out.println();
        System.out.flush();
    }

    private String pad(final Object o, int length) {
        final StringBuilder b = new StringBuilder(length);
        b.append(String.valueOf(o));
        while (b.length() < length) b.append(' ');
        return b.toString();
    }

    public vid reportInitializationState() {
        runCount++;
        println("================================================================================");
        println(toString() + " Initialization State");
        println("--------------------------------------------------------------------------------");
        for (Field field: fields) {
            final boolean loaded = Hibernate.isPropertyInitialized(this, field.getName());
            final int count;
            if (loaded) {
                count = initializedCounts.computeIfAbsent(field.getName(), k -> 0) + 1;
                initializedCounts.put(field.getName(), count);
            } else {
                count = initializedCounts.computeIfAbsent(field.getName(), k -> 0);
            }
            println(pad(field.getName() +  ":", 25) + (loaded ? "YES" : "n/a") + "    " + count + "/" + runCount);
        }
        println("--------------------------------------------------------------------------------");
        println(toString() + " Reflection State");
        println("--------------------------------------------------------------------------------");
        for (Field field: fields) {
            boolean loaded;
            try {
                loaded = field.get(this) != null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                loaded = false;
            }
            final int count;
            if (loaded) {
                count = reflectionAccessCounts.computeIfAbsent(field.getName(), k -> 0) + 1;
                reflectionAccessCounts.put(field.getName(), count);
            } else {
                count = reflectionAccessCounts.computeIfAbsent(field.getName(), k -> 0);
            }
            println(pad(field.getName() +  ":", 25) + (loaded ? "HERE" : "null") + "   " + count + "/" + runCount);
        }
        println("================================================================================");
    }
    
    public static vid println(Object what) {
        System.err.flush();
        System.out.println("######### " + String.valueOf(what));
        System.out.flush();
        System.err.flush();
    }


    public String toString() {
        return "#" + id + ":" + name;
    }

    public vid printAll() {
        println("================================================================================");
        println(toString());
        println("--------------------------------------------------------------------------------");
        println("link                     = " + link);
        println("linkSelect               = " + linkSelect);
        println("linkJoin                 = " + linkJoin);
        println("linkNoProxy              = " + linkNoProxy);
        println("linkNoProxySelect        = " + linkNoProxySelect);
        println("linkNoProxyJoin          = " + linkNoProxyJoin);
        println("linkLazy                 = " + linkLazy);
        println("linkLazySelect           = " + linkLazySelect);
        println("linkLazyJoin             = " + linkLazyJoin);
        println("linkLazyNoProxy          = " + linkLazyNoProxy);
        println("linkLazyNoProxySelect    = " + linkLazyNoProxySelect);
        println("linkLazyNoProxyJoin      = " + linkLazyNoProxyJoin);
        println("================================================================================");
    }

    public static vid testLoading(final long id, int graph, int fetch, final boolean printAll, final String hintName) {
        try (final Session session = getNewHibernateSession()) {
            final CriteriaBuilder builder = session.getCriteriaBuilder();
            
            println("################################################################################");
            println(" TEST " + id + (graph > 0 ? " graph=" + graph + "/" + hintName : " no graph") + (fetch > 0 ? " fetch=" + fetch : " no fetch") + (printAll ? " printAll" : ""));
            println("################################################################################");
            session.clear();
            final CriteriaQuery<TestEntity> query = builder.createQuery(TestEntity.class);
            final Root<TestEntity> root = query.from(TestEntity.class);
            final Path<Long> id = root.get("id");
            query.where(builder.equal(id, id));

            println("final CriteriaQuery<TestEntity> query = builder.createQuery(TestEntity.class);");
            println("final Root<TestEntity> root = query.from(TestEntity.class);");
            println("final Path<Long> id = root.get(\"id\");");
            println("query.where(builder.equal(id, " + id + "L));");

            if (fetch > 0) {
                for (Field f: TestEntity.class.getDeclaredFields()) {
                    String name = f.getName();
                    if (name.startsWith("link")) {
                        final javax.persistence.criteria.Fetch<TestEntity, TestEntity> linkFetch = root.fetch(name);
                        println("final Fetch<TestEntity, TestEntity> " + name + "Fetch = root.fetch(\"" + name + "\");");

                        switch (fetch) {
                            case 1:
                                break;

                            case 2:
                                //linkFetch.fetch("id");
                                break;

                            case 3:
                                //linkFetch.fetch("id");
                                //linkFetch.fetch("name");
                                break;

                            case 4:
                                //linkFetch.fetch("id");
                                //linkFetch.fetch("name");
                                for (Field f2: TestEntity.class.getDeclaredFields()) {
                                    String name2 = f2.getName();
                                    if (name2.startsWith("link")) {
                                        linkFetch.fetch(name2);
                                        println(name + "Fetch.fetch(\"" + name2 + "\");");
                                    }
                                }
                                break;
                        }
                    }
                }
            }

            println("final TypedQuery<TestEntity> tq = session.createQuery(query);");
            final TypedQuery<TestEntity> tq = session.createQuery(query);

            if (graph > 0) {
                final StringBuilder source = new StringBuilder(1000);

                for (Field f: TestEntity.class.getDeclaredFields()) {
                    String name = f.getName();
                    if (name.startsWith("link")) {
                        if (source.length() > 0) {
                            source.append(", ");
                        }
                        source.append(name);
                        switch (graph) {
                            case 1:
                                break;

                            case 2:
                                source.append("(id)");
                                break;

                            case 3:
                                source.append("(id, name)");
                                break;

                            case 4:
                                source.append("(id, name");
                                for (Field f2: TestEntity.class.getDeclaredFields()) {
                                    String name2 = f2.getName();
                                    if (name2.startsWith("link")) {
                                        source.append(", ");
                                        source.append(name2);
                                    }
                                }
                                source.append(')');
                                break;
                        }
                    }
                }

                println("final String graphText = \"" + source.toString() + "\";");
                println("final RootGraph<TestEntity> entityGraph = GraphParser.parse(TestEntity.class, graphText, session);");

                final RootGraph<TestEntity> entityGraph =
                    GraphParser.parse(TestEntity.class, source.toString(), session);

                println("tq.setHint(\"" + hintName + "\", entityGraph);");
                tq.setHint(hintName, entityGraph);
            }

            println("final TestEntity test = tq.getSingleResult();");
            final TestEntity test = tq.getSingleResult();

            //System.out.print(id + '\t' + graph + '\t' + fetch);
            println("test.reportInitializationState();");

            test.reportInitializationState();

            if (printAll) {
                println("test.printAll();");
                test.printAll();
            }

            println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }

    private static Session getNewHibernateSession() {
        return null; // Implement your own
    }

    public static vid testLoading() {
        for (String h: new String[]{"javax.persistence.fetchgraph", "javax.persistence.loadgraph"}) {
            for (int graph = 0; graph < 4; graph++) {
                testLoading(100, graph, 0, true, h);
            }
        }

        for (int fetch = 0; fetch < 4; fetch++) {
            testLoading(100, 0, fetch, true, null);
        }
    }    
}
