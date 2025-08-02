package annotations.playground.delta;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import annotations.playground.TestCase;
import annotations.playground.delta.ukMapping.DetailUk;
import annotations.playground.delta.ukMapping.MasterUk1;

/** 
 * <PRE>
 * Copyright: Delta Energy Solution AG, 2009
 *
 * Tests the bidirectional OneToMany relation between a <code>MasterUk1 </code>class and
 * a <code>DetailUk</code> class.
 * 
 * Modification History:
 * Version:   Date:          Author: Description:
 * ---------- -------------- ------- ------------
 * 1.0        12.08.2009     rfr      created
 *
 *</PRE>
 * @author rfr
 * @version $$Id$$
 */
public class UkMappingTest extends TestCase {
    
    /**
     * Constructs a <code>OneToManyTest</code> instance. 
     * @param x
     */
    public UkMappingTest(String x) {
        super(x);
    }

    /**
     * Gets the instance of <code>MasterUk1</code> with given id
     * @param id the id of the master
     * @param check true if you want to assert a <code>MasterUk1</code> was loaded.
     * @return the master
     */
    private MasterUk1 getMasterById(int id, boolean check) {
        System.out.println();
        Session s = openSession();
        MasterUk1 result = (MasterUk1)s.get(MasterUk1.class, id);
        if (check) {
            assertNotNull("No MasterUk1 loaded", result);
        }
        s.close();
        return result;
    }
    
    /**
     * Gets the instance of <code>DetailUk</code> with given name
     * @param name the name of the detail
     * @param check true if you want to assert if exactly one <code>DetailUk</code> was loaded.
     * @return the detail
     */
    private DetailUk getDetailByName(String name, boolean check) {
        Session s = openSession();
        List<DetailUk> details = s.createCriteria(DetailUk.class).add(Restrictions.like("name", name)).list();
        if (check) {
            assertEquals("Number of loaded detailUks", 1, details.size());
        }
        s.close();
        return details.size() == 0 ? null : details.get(0);
    }

    
    /**
     * Creates a master with its detailUks and checks the master by loading the master by id and
     * doing some assertions on the structure.
     * @param name the name of the master
     * @param detailUks the detailUks (optional)
     * @return the instance of the <code>MasterUk1</code>.
     */
    private MasterUk1 createAndCheckMaster(String name, DetailUk...details) {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        MasterUk1 master = new MasterUk1(name);
        
        for (DetailUk detail : details) {
            master.addDetail(detail);
        }

        s.persist(master);
        tx.commit();
        s.close();
        
        checkMaster(master.getId(), details.length);

        return master;
    }
    
    /**
     * Checks the structure of the master.
     * @param id the id of the master
     * @param detailCount the expected number of detailUks
     * @return the master loaded from database
     */
    private MasterUk1 checkMaster(int id, int detailCount) {
        MasterUk1 master = getMasterById(id, true);
        assertNotNull(master);
        assertEquals("Number of detailUks", detailCount, master.getDetails().size());
        for (DetailUk detail : master.getDetails()) {
            assertNotNull("MasterUk1 not set in detail: " + detail, detail.getMaster());
            assertEquals("MasterUk1 in DetailUk", master, detail.getMaster());
        }
        return master;
    }
    
    /**
     * Test creation and reading of <code>MasterUk1</code> and <code>DetailUk</code> instances.
     */
    public void testCreateAndLoad() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");
        DetailUk detail = getDetailByName("D4.3", true);
        assertEquals(master.getId(), detail.getMaster().getId());
        System.out.println("MasterUk1 of DetailUk D4.3 is: " + detail.getMaster());
        System.out.println("***************************\n");

        System.out.println("\nLoaded from DB:\n===============");
        for (MasterUk1 m : masters) {
            System.out.println("=> " + getMasterById(m.getId(), true));
        }
    }
    
    /**
     * Test deleting a <code>MasterUk1</code> instance.
     */
    public void testDeleteMaster() {
        final MasterUk1 created = createAndCheckMaster("MasterUk1", new DetailUk("DetailUk 1"),
                new DetailUk("DetailUk 2"),
                new DetailUk("DetailUk 3"),
                new DetailUk("DetailUk 4"),
                new DetailUk("DetailUk 5"));
        
        List<String> detailNames = new ArrayList<String>();
        for (DetailUk detail : created.getDetails()) {
            detailNames.add(detail.getName());
        }
        assertEquals("Number of detailUks", 5, detailNames.size());

        Session s = openSession();
        MasterUk1 loaded = (MasterUk1)s.get(MasterUk1.class, created.getId());
        Transaction tr = s.beginTransaction();
        s.delete(loaded);
        tr.commit();
        s.close();
        
        // check the deletion
        assertNull("MasterUk1 instance not deleted", getMasterById(created.getId(), false));
        for (String detailName : detailNames) {
            assertNull("DetailUk instance not deleted: " + detailName, getDetailByName(detailName, false));
        }
    }

    /**
     * Tests the removal of an <code>DetailUk</code> instance.<br/>
     * <br/>
     * <u><b>Attention</b>:</u><br/>
     * To remove a <code>DetailUk</code> from a <code>MasterUk1</code>, both instances should be
     * <code>transient</code>, i.e. the should be loaded into the same session.<br/>
     * <br/>
     * <u>Hint</u>:<br/>
     * The OneToMany side (class <code>MasterUk1</code>) is annotated with
     * <code>CascadeType.DELETE_ORPHAN</code>.<br/>
     */
    public void testRemoveDetail() {
        final String nameOfDetailToRemove = "DetailUk 4";
        createAndCheckMaster("MasterUk1", new DetailUk("DetailUk 1"),
                new DetailUk("DetailUk 2"),
                new DetailUk("DetailUk 3"),
                new DetailUk(nameOfDetailToRemove),
                new DetailUk("DetailUk 5"));

        DetailUk detail = getDetailByName(nameOfDetailToRemove, true);
        MasterUk1 master = getMasterById(detail.getMaster().getId(), true);

        // ------ BEGIN SESSION -----------------------------------------------
        Session s = openSession();

        // connect he master and the detail to the same session 
        master = (MasterUk1)s.get(MasterUk1.class, master.getId());
        detail = (DetailUk)s.load(DetailUk.class, detail.getId());

        Transaction tr = s.beginTransaction();

        master.removeDetail(detail);
        s.update(master);
        tr.commit();
        s.close();
        // ------ END SESSION -------------------------------------------------

        // check the removal
        assertNull(getDetailByName(nameOfDetailToRemove, false));
        checkMaster(master.getId(), 4);
    }

    
    /** 
     * {@inheritDoc}
     * @see annotations.playground.TestCase#getMappings()
     */
    @Override
    protected Class<?>[] getMappings() {
        return new Class[]{
                MasterUk1.class,
                DetailUk.class
        };
    }

    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCreateAndLoadMasterByCriteriaProjections() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        DetachedCriteria criteria = DetachedCriteria.forClass(MasterUk1.class);
        criteria.add(Restrictions.eq("name", "M4"));
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("id"), "id");
        proList.add(Projections.property("name"), "name");
        criteria.setProjection(proList);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
//        criteria.setResultTransformer(new AliasToBOResultTransformer(MasterUk1.class));
        List list = criteria.getExecutableCriteria(s).list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        s.close();
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals("M4", m.getName());

        System.out.println("***************************\n");
    }

    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCreateAndLoadMasterByHqlProjections() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("id"), "id");
        proList.add(Projections.property("name"), "name");
//        criteria.setResultTransformer(new AliasToBOResultTransformer(MasterUk1.class));
        Query query = s.createQuery(
                "select m.id as id," +
                "       m.name as name" +
                " from  MasterUk1 as m" +
                " where m.name = 'M4'");
        query.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
        List list = query.list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        s.close();
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals("M4", m.getName());

        System.out.println("***************************\n");
    }

    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCreateAndLoadMasterByHqlFetchMode() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        Query query = s.createQuery("from  MasterUk1 where name = 'M4'");
//        query.setFetchMode() // not supported
//        For HQL queries, use the FETCH keyword instead.
        List list = query.list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        s.close();
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals("M4", m.getName());

        System.out.println("***************************\n");
    }
    
    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCreateAndLoadMasterBySqlProjections() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("id"), "id");
        proList.add(Projections.property("name"), "name");
//        criteria.setResultTransformer(new AliasToBOResultTransformer(MasterUk1.class));
        Query query = s.createSQLQuery(
                "select m.id," +
                "       m.name" +
                " from  Master_Uk as m" +
                " where m.name = 'M4'");
        query.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
        List list = query.list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        s.close();
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals("M4", m.getName());

        System.out.println("***************************\n");
    }
    
    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCheckCacheWithCriteriaProjectionsAndCriteria() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************1");

        Session s = openSession();
        s.setCacheMode(CacheMode.GET);
        DetachedCriteria criteria = DetachedCriteria.forClass(MasterUk1.class);
        criteria.add(Restrictions.eq("name", "M4"));
//        ProjectionList proList = Projections.projectionList();
//        proList.add(Projections.property("id"), "id");
//        proList.add(Projections.property("name"), "name");
//        criteria.setProjection(proList);
//        criteria.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
        List list = criteria.getExecutableCriteria(s).list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals("M4", m.getName());
        System.out.println("\n***************************2");
        
        DetachedCriteria criteria2 = DetachedCriteria.forClass(MasterUk1.class);
        criteria2.add(Restrictions.eq("name", "M4"));
        List list2 = criteria2.getExecutableCriteria(s).list();
        assertFalse("no Master2 found", (list2.size() == 0));
        MasterUk1 m2 = (MasterUk1)list2.get(0); 
        assertEquals("Number of Details", 4, m2.getDetails().size());
        
        s.close();

        System.out.println("***************************3\n");
    }

    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Projection
     */
    public void testCheckCacheWithCriteriaProjectionsTwoTimes() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        s.setCacheMode(CacheMode.GET);
        DetachedCriteria criteria = DetachedCriteria.forClass(MasterUk1.class);
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("id"), "id");
        proList.add(Projections.property("name"), "name");
        criteria.setProjection(proList);
        criteria.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
        List list = criteria.getExecutableCriteria(s).list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        MasterUk1 m = (MasterUk1)list.get(0); 
        assertEquals(0, m.getDetails().size());
        
        DetachedCriteria criteria2 = DetachedCriteria.forClass(MasterUk1.class);
        criteria2.add(Restrictions.eq("name", "M4"));
        criteria2.setProjection(proList);
        criteria2.setResultTransformer(new AliasToBeanResultTransformer(MasterUk1.class));
        List list2 = criteria2.getExecutableCriteria(s).list();
        assertFalse("no Master2 found", (list2.size() == 0));
        MasterUk1 m2 = (MasterUk1)list2.get(0); 
        assertEquals(1, list2.size());
        s.close();

        System.out.println("***************************\n");
    }

    /**
     * Test creation and reading of <code>MasterUk1</code> by a Hibernate Criteria
     */
    public void testCreateAndLoadMasterByCriteriaFetchMode() {
        List<MasterUk1> masters = new ArrayList<MasterUk1>();
        
        masters.add(createAndCheckMaster("M1"));

        masters.add(createAndCheckMaster("M2", new DetailUk("D2.1"),
                new DetailUk("D2.2"),
                new DetailUk("D2.3")));

        masters.add(createAndCheckMaster("M3"));
        
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        masters.add(master);
        System.out.println("\n***************************");

        Session s = openSession();
        DetachedCriteria criteria = DetachedCriteria.forClass(MasterUk1.class);
        criteria.add(Restrictions.eq("name", "M4"));
        criteria.setFetchMode("detailUks", FetchMode.LAZY); // dos not work
        List list = criteria.getExecutableCriteria(s).list();
        assertFalse("no MasterUk1 found", (list.size() == 0));
        s.close();
        MasterUk1 m = (MasterUk1)list.get(0);
        assertEquals("M4", m.getName());

        System.out.println("***************************\n");

    }
    
    /**
     * Test creation and reading of <code>MasterUk1</code> and <code>DetailUk</code> instances.
     */
    public void testCreateAndCheckMasterInDetail() {
        MasterUk1 master = createAndCheckMaster("M4", new DetailUk("D4.1"),
                new DetailUk("D4.2"),
                new DetailUk("D4.3"),
                new DetailUk("D4.4")); 
        assertEquals(MasterUk1.class, master.getDetails().get(0).getMaster().getClass());
    }
}
