package org.hibernate.property;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.MappingException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("nls")
public class PropertyAccessStrategyBasicImplTest {

	@BeforeClass
	public static void setUp() {
		assertThat(ContainsProperty.class.getDeclaredMethods().length, is(4));
	}

	/**
	 * This test will use
	 * {@link PropertyAccessStrategyBasicImpl#buildPropertyAccess(Class, String)}
	 * to check situations where a class contains both a "{@code isMethod()}
	 * " and a "{@code getMethod()}".
	 *
	 * Prior to Hibernate 5.0 this fails to the the undeterministic behavior of
	 * {@link Class#getDeclaredMethods()}, so Hibernate would pick one or the
	 * other method on a random base.
	 *
	 * With 5.0.5 this is no longer the case, since there's a check called by
	 * {@link ReflectHelper#findGetterMethod(Class, String)}.
	 */
	@Test(expected=MappingException.class)
	public void testGetGetter() {
		PropertyAccessStrategyBasicImpl strategy = new PropertyAccessStrategyBasicImpl();

		PropertyAccess getter = strategy.buildPropertyAccess(
				ContainsProperty.class, "name");

		assertThat(
				"Failed to find the getter, but found the 'isser'. Won't fail every time, so must be repeated",
				getter.getGetter().getMethod().getName(), is("getName"));
	}

	public static class ContainsProperty {

		public boolean isName() {
			return false;
		}

		public ContainsProperty getName() {
			return new ContainsProperty();
		}

		public ContainsProperty getAnotherName() {
			return new ContainsProperty();
		}

		public boolean isAnotherName() {
			return false;
		}

	}
}
