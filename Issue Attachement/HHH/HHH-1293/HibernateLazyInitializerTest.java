import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.identity.Group;
import org.jbpm.identity.hibernate.IdentitySession;
import org.jbpm.identity.hibernate.IdentitySessionFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HibernateLazyInitializerTest extends TestCase {
	static Logger	log				= Logger.getLogger(HibernateLazyInitializerTest.class);
	static long		MAX_PROCESSES	= 62;
	static String	PROCESS_NAME	= "TestProcess";

	public HibernateLazyInitializerTest() {
	}

	public HibernateLazyInitializerTest(String testName) {
		super(testName);
	}

	public void testTaskCompletion() {
		createProcessInstances();
		int process = 0;
		try {
			for (process = 0; process < MAX_PROCESSES; process++)
				completeOneTaskInstance();
		}
		catch (NoSuchMethodError e) {
			log.error(e);
			fail("could not complete all generated tasks, last process completed was " + process);
		}
		catch (Exception e) {
			log.error(e);
			fail("could not complete all generated tasks, last process completed was " + process);
		}
	}

	void createProcessInstances() {
		JbpmContext context = JbpmConfiguration.getInstance().createJbpmContext();
		try {
			for (int i = 0; i < MAX_PROCESSES; i++) {
				ProcessInstance processInstance = context.newProcessInstance(PROCESS_NAME);
				processInstance.signal();
			}
		}
		finally {
			context.close();
		}
	}

	static List	INDEXERS	= Arrays.asList(new String[] { "indexers" });

	void completeOneTaskInstance() throws Exception {
		JbpmContext context = JbpmConfiguration.getInstance().createJbpmContext();
		try {
			List tasks = context.getGroupTaskList(INDEXERS);
			if (tasks == null || tasks.isEmpty())
				return;
			TaskInstance taskInstance = (TaskInstance)tasks.iterator().next();
			taskInstance.setActorId("me");
			taskInstance.end();
		}
		finally {
			context.close();
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("HibernateLazyInitializerTest");
		suite.addTestSuite(HibernateLazyInitializerTest.class);
		return new TestSetup(suite) {
			public void setUp() throws Exception {
				JbpmConfiguration configuration = JbpmConfiguration.getInstance();
				configuration.dropSchema();
				configuration.createSchema();

				JbpmContext context = configuration.createJbpmContext();
				try {
					InputStream inputStream = getClass().getResourceAsStream("TestProcess.xml");
					ProcessDefinition processDefinition = ProcessDefinition.parseXmlInputStream(inputStream);
					context.deployProcessDefinition(processDefinition);
				}
				finally {
					context.close();
				}

				IdentitySessionFactory identitySessionFactory = new IdentitySessionFactory();
				IdentitySession identitySession = identitySessionFactory.openIdentitySession();
				try {
					identitySession.beginTransaction();
					identitySession.saveGroup(new Group("indexers"));
					identitySession.commitTransactionAndClose();
				}
				catch (Exception e) {
					identitySession.rollbackTransactionAndClose();
					log.fatal(e);
					throw e;
				}
			}
		};
	}
}
