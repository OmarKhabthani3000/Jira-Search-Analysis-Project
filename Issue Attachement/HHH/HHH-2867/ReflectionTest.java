package com.mydomain.biz.bean;

import java.lang.reflect.Method;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;


public class ReflectionTest {

	private String myProp = "VALUE";
    Class[] argTypes = new Class[] { };
    Object[] arg = new Object[0];
    Method getterMethod;
    FastClass fastClass;
    FastMethod fastGetter;
    String getterName;
    int fastGetterIndex;
    org.hibernate.bytecode.javassist.FastClass javassistFastClass;
    int javassistFastGetterIndex;
    
    public Object getBeanProperty(Object bean) throws Exception {
		Class clazz = bean.getClass();
	    getterMethod = clazz.getDeclaredMethod(getterName, argTypes);
	    Object obj = getterMethod.invoke(bean, arg);
	    return obj;
	}

	public Object getBeanPropertyViaCachedMethod(Object bean) throws Exception {
	    Object obj = getterMethod.invoke(bean, arg);
	    return obj;
	}
	
	public Object getBeanPropertyViaCglibMethod(Object bean) throws Exception {
	    Object obj = fastGetter.invoke(bean, arg);
	    return obj;
	}
	
	public Object getBeanPropertyViaCglibMethodIndex(Object bean) throws Exception {
	    Object obj = fastClass.invoke(fastGetterIndex, bean, arg);
	    return obj;
	}

	public Object getBeanPropertyViaMethodIndexJavassist(Object bean) throws Exception {
	    Object obj = javassistFastClass.invoke(javassistFastGetterIndex, bean, arg);
	    return obj;
	}

	public String getMyProp() {
		return myProp;
	}

	public void init(String propName) {
		getterName = "get" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);

		fastClass = FastClass.create(ReflectionTest.class);
		fastGetter = fastClass.getMethod(getterName, argTypes);
		fastGetterIndex = fastClass.getIndex(getterName, argTypes);

	    javassistFastClass = org.hibernate.bytecode.javassist.FastClass.create(ReflectionTest.class);
	    javassistFastGetterIndex = javassistFastClass.getIndex(getterName, argTypes);
	}
	
	public static void main(String... args) throws Exception {
		
		ReflectionTest testBean = new ReflectionTest();
		testBean.init("myProp");
		
		final int ITERATIONS = 5000000;
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < ITERATIONS; i++) {
			String str = (String) testBean.getBeanProperty(testBean);
			//System.out.println(str);
		}
		long finish = System.currentTimeMillis();
		System.out.println("Run1 time=" + (finish-start) + "ms");
		
		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATIONS; i++) {
			String str = (String) testBean.getBeanPropertyViaCachedMethod(testBean);
			//System.out.println(str);
		}
		finish = System.currentTimeMillis();
		System.out.println("Run2 time=" + (finish-start) + "ms");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATIONS; i++) {
			String str = (String) testBean.getBeanPropertyViaCglibMethod(testBean);
			//System.out.println(str);
		}
		finish = System.currentTimeMillis();
		System.out.println("Run3 time=" + (finish-start) + "ms");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATIONS; i++) {
			String str = (String) testBean.getBeanPropertyViaCglibMethodIndex(testBean);
			//System.out.println(str);
		}
		finish = System.currentTimeMillis();
		System.out.println("Run4 time=" + (finish-start) + "ms");

		start = System.currentTimeMillis();
		for (int i = 0; i < ITERATIONS; i++) {
			String str = (String) testBean.getBeanPropertyViaMethodIndexJavassist(testBean);
			//System.out.println(str);
		}
		finish = System.currentTimeMillis();
		System.out.println("Run5 time=" + (finish-start) + "ms");
	}
}
