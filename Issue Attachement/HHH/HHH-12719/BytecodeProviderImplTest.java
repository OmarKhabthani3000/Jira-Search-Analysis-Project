package org.hibernate.bytecode.internal.bytebuddy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public class BytecodeProviderImplTest extends BaseCoreFunctionalTestCase {
	
	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				TestSuperclass.class,
				TestSubclass.class
		};
	}

	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
		//configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
	}
	
	/**
	 * This reproduces what
	 * {@link org.hibernate.tuple.entity.PojoEntityTuplizer#PojoEntityTuplizer(org.hibernate.tuple.entity.EntityMetamodel, org.hibernate.mapping.PersistentClass)}
	 * passes to
	 * {@link BytecodeProviderImpl#getReflectionOptimizer(Class, String[], String[], Class[])}
	 * based on results from
	 * {@link org.hibernate.internal.util.ReflectHelper#findGetterMethod(Class, String)
	 * at runtime for a subclass that inherits a property.
	 * 
	 * Reported as https://hibernate.atlassian.net/browse/HHH-12719
	 */
	@Test
	public void getReflectionOptimizerTest() throws Exception {
		BytecodeProviderImpl bp = new BytecodeProviderImpl();
		
		String[] getterNames = new String[] { "getDeclaredProperty", "getInheritedProperty" };
		String[] setterNames = new String[] { "setDeclaredProperty", "setInheritedProperty" };
		@SuppressWarnings("rawtypes")
		Class[] types = new Class[] { String.class, String.class };
		bp.getReflectionOptimizer( TestSubclass.class, getterNames, setterNames, types );
	}
}
