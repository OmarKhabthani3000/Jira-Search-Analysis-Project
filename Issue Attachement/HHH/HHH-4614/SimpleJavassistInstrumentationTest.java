package org.hibernate.test.instrument2.runtime;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.bytecode.BytecodeProvider;
import org.hibernate.bytecode.javassist.BytecodeProviderImpl;


public class SimpleJavassistInstrumentationTest extends
		SimpleTransformingClassLoaderInstrumentTestCase {
	public SimpleJavassistInstrumentationTest(String string) {
		super( string );
	}

	protected BytecodeProvider buildBytecodeProvider() {
		return new BytecodeProviderImpl();
	}

	public static Test suite() {
		return new TestSuite( SimpleJavassistInstrumentationTest.class );
	}

	public void testDumb() {
		super.testDumb();
	}


}