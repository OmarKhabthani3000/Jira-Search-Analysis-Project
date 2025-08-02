package org.hibernate.property;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hibernate.property.BasicPropertyAccessor;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("nls")
public class BasicPropertyAccessorTest {

	@BeforeClass
	public static void setUp() {
		assertThat(ContainsProperty.class.getDeclaredMethods().length, is(4));
	}

	/**
	 * This test will call
	 * {@link BasicPropertyAccessor#getGetter(Class, String)}. That method scans
	 * {@link Class#getDeclaredMethods()} for getters for a given property, e.g.
	 * "getName" and "isName" for the property "name".
	 *
	 * Since {@link Class#getDeclaredMethods()} is non-deterministic
	 * {@link BasicPropertyAccessor#getGetter(Class, String)} is not reliable.
	 *
	 * <strong>Because of the randomness, this test fails in ~ 9 of 10 runs,
	 * only.</strong>
	 */
	@Test
	public void testGetGetter() {
		BasicPropertyAccessor accessor = new BasicPropertyAccessor();

		Getter getter = accessor.getGetter(ContainsProperty.class, "name");

		assertThat(
				"Failed to find the getter, but found the 'isser'. Won't fail every time, so must be repeated",
				getter.getMethodName(), is("getName"));
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
