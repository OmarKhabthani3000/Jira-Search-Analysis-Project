package test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.List;

@RunWith(Arquillian.class)
public class CountryServiceTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage("test")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource("hello-ds.xml");
    }

    @Inject
    CountryServiceBean countryServiceBean;

    @After
    public void deleteCountries() {
    	countryServiceBean.deleteAllCountries();
    }

    private Country createAndCheckCountry(String name) {
    	// create single country:
        Country country = new Country();
        country.setName(name);
        country.getNameList().add(new NameElement("A"));
        country.getNameList().add(new NameElement("B"));
        country.getNameList().add(new NameElement("C"));

        System.out.println("Initial save country to DB:");
        countryServiceBean.persistCountry(country);

        // reload and check:
        System.out.println("\nReload and check (1):");
        List<Country> check = countryServiceBean.findAllCountries();
        Assert.assertEquals(1, check.size());
        Country countryFromDB = check.get(0);
        Assert.assertEquals(name, countryFromDB.getName());
        Assert.assertNotNull(countryFromDB.getId());
        Assert.assertEquals("initial: 3 names in list",3,countryFromDB.getNameList().size());
        Assert.assertEquals("initial: name 1 is A","A",countryFromDB.getNameList().get(0).getName());
        Assert.assertEquals("initial: name 2 is B","B",countryFromDB.getNameList().get(1).getName());
        Assert.assertEquals("initial: name 3 is C","C",countryFromDB.getNameList().get(2).getName());

        return country;
    }

    private void checkModifiedCountry(String name) {
        // reload and check:
        System.out.println("\nCheck the final result:");
        List<Country> check = countryServiceBean.findAllCountries();
        Assert.assertEquals(1, check.size());
        Country countryFromDB = check.get(0);
        Assert.assertEquals(name, countryFromDB.getName());
        Assert.assertNotNull(countryFromDB.getId());
        Assert.assertEquals("final: 3 names in list",3,countryFromDB.getNameList().size());
        Assert.assertEquals("final: name 1 is A","A",countryFromDB.getNameList().get(0).getName());
        Assert.assertEquals("final: name 2 is C","C",countryFromDB.getNameList().get(1).getName());
        Assert.assertEquals("final: name 3 is B","B",countryFromDB.getNameList().get(2).getName());
    }

    /**
     * Create country "England" with name list "A, B, C" and resort the name list to "A, C, B".
     * All works as expected.
     * @throws Exception
     */
    @Test
    public void resortDetached() throws Exception {
    	System.out.println("\n\n--- START TEST resortDetached ---");

    	Country countryFromDB=createAndCheckCountry("England");

        // resort the name list:
        NameElement movedElement=countryFromDB.getNameList().remove(1);
        countryFromDB.getNameList().add(movedElement);
        Assert.assertEquals("after move: 3 names in list",3,countryFromDB.getNameList().size());
        Assert.assertEquals("after move: name 1 is A","A",countryFromDB.getNameList().get(0).getName());
        Assert.assertEquals("after move: name 2 is C","C",countryFromDB.getNameList().get(1).getName());
        Assert.assertEquals("after move: name 3 is B","B",countryFromDB.getNameList().get(2).getName());

        // Merge (of detached object):
        System.out.println("\n\nMerge after change order:");
        countryFromDB = countryServiceBean.mergeCountry(countryFromDB);

        checkModifiedCountry("England");
        System.out.println("--- END TEST resortDetached ---");
    }

    /**
     * Create country "England" with name list "A, B, C" and resort the name list to "A, C, B".
     * This time, the resorting is done within the EJB. And now no changes are stored back to DB!
     * Because of this, the final check fails, because DB still contains "A, B, C".
     * @throws Exception
     */
    @Test
    public void resortInTransaction() throws Exception {
    	System.out.println("\n\n--- START TEST resortInTransactions ---");

    	Country countryFromDB=createAndCheckCountry("England");

    	// resort in EJB (do the modification within an transaction):
        countryServiceBean.moveNameToEnd(countryFromDB,null); // don't change the name, now no change is written to DB!

        checkModifiedCountry("England");
        System.out.println("--- END TEST resortInTransactions ---");
    }

    /**
     * Create country "England" with name list "A, B, C" and resort the name list to "A, C, B".
     * Same as test above. But this time, also the country name is changed to "Scotland".
     * Because of this, Hibernate detects that there are changes and persists the whole object to DB.
     * Because of this, also the name order is persisted as "A, C, B".
     * With @DynamicUpdate(true) also this test fails!
     * @throws Exception
     */
    @Test
    public void resortInTransaction_ButChangeName() throws Exception {
    	System.out.println("\n\n--- START TEST resortInTransactions ---");

    	Country countryFromDB=createAndCheckCountry("England");

    	// resort in EJB (do the modification within an transaction):
        countryServiceBean.moveNameToEnd(countryFromDB,"Scotland"); // By changing the name, Hibernate is aware of a change and writes back everything to DB!

        checkModifiedCountry("Scotland");
        System.out.println("--- END TEST resortInTransactions ---");
    }

}