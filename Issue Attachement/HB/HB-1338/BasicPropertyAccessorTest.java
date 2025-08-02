import net.sf.hibernate.PropertyNotFoundException;
import net.sf.hibernate.property.BasicPropertyAccessor;
import net.sf.hibernate.property.PropertyAccessor;
import junit.framework.TestCase;

/**
 * @author Luís Miranda
 */
public class BasicPropertyAccessorTest extends TestCase {

	public void testProperty_xy() throws PropertyNotFoundException {

		final class Bean0 {
			Object getXy()         { return null; }
			void   setXy(Object o) { return;      }
		}

		propertyAccessor.getGetter(Bean0.class, "xy");
		propertyAccessor.getSetter(Bean0.class, "xy");
	}

	public void testProperty_xY() throws PropertyNotFoundException {

		final class Bean1 {
			Object getXY()         { return null; }
			void   setXY(Object o) { return;      }
		}

		propertyAccessor.getGetter(Bean1.class, "xY");
		propertyAccessor.getSetter(Bean1.class, "xY");
	}

	public void testProperty_Xy() throws PropertyNotFoundException {

		final class Bean2 {
			Object getXy()         { return null; }
			void   setXy(Object o) { return;      }
		}

		propertyAccessor.getGetter(Bean2.class, "Xy");
		propertyAccessor.getSetter(Bean2.class, "Xy");
	}

	public void testProperty_XY() throws PropertyNotFoundException {

		final class Bean3 {
			Object getXY()         { return null; }
			void   setXY(Object o) { return;      }
		}

		propertyAccessor.getGetter(Bean3.class, "XY");
		propertyAccessor.getSetter(Bean3.class, "XY");
	}

	// immutable class so no need for setUp()
	private PropertyAccessor propertyAccessor = new BasicPropertyAccessor();
}