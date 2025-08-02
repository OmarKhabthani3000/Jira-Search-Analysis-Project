package org.hibernate;

import org.apache.commons.io.IOUtils;
import org.hibernate.service.classloading.internal.ClassLoaderServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Artem V. Navrotskiy
 */
public class ClassLoaderServiceImplTest {
    /**
     * Test for bug: HHH-7084
     */
    @Test
    public void classForName() throws IOException, ClassNotFoundException {
        // Create ClassLoader with overrided class.
        TestClassLoader anotherLoader = new TestClassLoader();
        anotherLoader.overrideClass(DummyClass.class);
        Class<?> anotherClass = anotherLoader.loadClass(DummyClass.class.getName());
        Assert.assertNotSame(DummyClass.class, anotherClass);

        // Check ClassLoaderServiceImpl().classForName() returns correct class (not from current ClassLoader).
        ClassLoaderServiceImpl loaderService = new ClassLoaderServiceImpl(anotherLoader);
        Class<Object> objectClass = loaderService.classForName(DummyClass.class.getName());
        Assert.assertSame(objectClass, anotherClass);
    }

    private static class TestClassLoader extends ClassLoader {
        /**
         * Reloading class from binary file.
         *
         * @param originalClass Original class.
         * @throws IOException .
         */
        public void overrideClass(final Class<?> originalClass) throws IOException {
            String originalPath = "/" + originalClass.getName().replaceAll("\\.", "/") + ".class";
            InputStream inputStream = originalClass.getResourceAsStream(originalPath);
            Assert.assertNotNull(inputStream);
            try {
                byte[] data = IOUtils.toByteArray(inputStream);
                defineClass(originalClass.getName(), data, 0, data.length);
            } finally {
                inputStream.close();
            }
            try {
                Class<?> checkClass = loadClass(originalClass.getName());
                originalClass.cast(checkClass.newInstance());
                Assert.fail("ClassCastException expected.");
            } catch (ClassCastException ignored) {
            } catch (ClassNotFoundException e) {
                Assert.fail(e.getMessage());
            } catch (java.lang.InstantiationException e) {
                Assert.fail(e.getMessage());
            } catch (IllegalAccessException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    /**
     * Simple class for ClassLoader checks.
     */
    public static class DummyClass {
    }
}
