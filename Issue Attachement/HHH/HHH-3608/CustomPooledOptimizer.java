import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AccessCallback;
import org.hibernate.id.enhanced.OptimizerFactory.OptimizerSupport;

/**
 * Customized pooled optimizer which fixes a few bugs in Hibernate's
 * implementation.
 *
 */
public class CustomPooledOptimizer extends OptimizerSupport {

	private final Log log = LogFactory.getLog(CustomPooledOptimizer.class);

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	private IntegralDataTypeHolder value;

	private IntegralDataTypeHolder hiValue;

	// ----------------------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------------------

	/**
	 * Constructs a new optimizer.
	 *
	 * @param returnClass
	 *            the return class.
	 * @param incrementSize
	 *            the increment size.
	 */
	public CustomPooledOptimizer(Class<?> returnClass, int incrementSize) {
		super(returnClass, incrementSize);
		if (incrementSize < 1) {
			throw new HibernateException("Increment size cannot be less than 1");
		}

		if (log.isTraceEnabled()) {
			log.trace("Creating pooled optimizer with [incrementSize="
					+ incrementSize + "; returnClass=" + returnClass.getName()
					+ "]");
		}
	}

	// ----------------------------------------------------------------------
	// Public methods
	// ----------------------------------------------------------------------

	/**
	 * Generates the next identifier.
	 *
	 * @param callback
	 *            the data access callback.
	 */
	public synchronized Serializable generate(AccessCallback callback) {

		if (hiValue == null || !value.lt(hiValue)) {
			value = callback.getNextValue();
			hiValue = value.copy().add(incrementSize);
		}

		return value.makeValueThenIncrement();
	}

	/**
	 * Gets the last source value.
	 *
	 * @return the last source value.
	 */
	public IntegralDataTypeHolder getLastSourceValue() {
		return hiValue;
	}

	/**
	 * Returns whether this implementation applies the increment size to source
	 * values. Always returns <code>true</code>.
	 *
	 * @return always <code>true</code>.
	 */
	public boolean applyIncrementSizeToSourceValues() {
		return true;
	}

	/**
	 * Gets the last value.
	 *
	 * @return the last value.
	 */
	public IntegralDataTypeHolder getLastValue() {
		return value.copy().decrement();
	}
}
