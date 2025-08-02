Index: CGLIBLazyInitializer.java
===================================================================
--- CGLIBLazyInitializer.java	(revision 9210)
+++ CGLIBLazyInitializer.java	(revision 10126)
@@ -4,16 +4,19 @@
 import java.io.Serializable;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
+import java.util.List;
+import java.util.ArrayList;
+import java.util.Iterator;
+
 
 import net.sf.cglib.proxy.Callback;
 import net.sf.cglib.proxy.CallbackFilter;
 import net.sf.cglib.proxy.Enhancer;
-import net.sf.cglib.proxy.Factory;
-import net.sf.cglib.proxy.MethodInterceptor;
-import net.sf.cglib.proxy.MethodProxy;
+import net.sf.cglib.proxy.InvocationHandler;
 import net.sf.cglib.proxy.NoOp;
 
 import org.hibernate.HibernateException;
+import org.hibernate.LazyInitializationException;
 import org.hibernate.proxy.pojo.BasicLazyInitializer;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.engine.SessionImplementor;
@@ -25,10 +28,7 @@
 /**
  * A <tt>LazyInitializer</tt> implemented using the CGLIB bytecode generation library
  */
-public final class CGLIBLazyInitializer extends BasicLazyInitializer implements MethodInterceptor {
-
-	
-	private static final Class[] CALLBACK_TYPES = new Class[]{ MethodInterceptor.class,NoOp.class };
+public final class CGLIBLazyInitializer extends BasicLazyInitializer implements InvocationHandler {
 	
 	private static final CallbackFilter FINALIZE_FILTER = new CallbackFilter() {
 		public int accept(Method method) {
@@ -62,13 +62,10 @@
 					session 
 				);
 			
-			final HibernateProxy proxy = (HibernateProxy) Enhancer.create(
-						interfaces.length == 1 ? persistentClass : null, 
-						interfaces,
-						FINALIZE_FILTER, 
-						new Callback[]{ instance, NoOp.INSTANCE }
-					);
-			
+			final HibernateProxy proxy;
+			Class factory = getProxyFactory(persistentClass,  interfaces);
+			Enhancer.registerCallbacks(factory, new Callback[]{ instance, null });
+			proxy = (HibernateProxy)factory.newInstance();
 			instance.constructed = true;
 			return proxy;
 		}
@@ -98,12 +95,12 @@
 		
 		final HibernateProxy proxy;
 		try {
+			Enhancer.registerCallbacks(factory, new Callback[]{ instance, null });
 			proxy = (HibernateProxy) factory.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "CGLIB Enhancement failed: " + persistentClass.getName(), e );
 		}
-		( (Factory) proxy ).setCallback( 0, instance );
 		instance.constructed = true;
 
 		return proxy;
@@ -111,28 +108,17 @@
 
 	public static Class getProxyFactory(Class persistentClass, Class[] interfaces)
 			throws HibernateException {
-		// note: interfaces is assumed to already contain HibernateProxy.class
-		
-		try {
-
-			Enhancer en = new Enhancer();
-			en.setUseCache( false );
-			en.setInterceptDuringConstruction( false );
-			
-			en.setCallbackTypes( CALLBACK_TYPES );
-			en.setCallbackFilter( FINALIZE_FILTER );
-			
-			en.setSuperclass( interfaces.length == 1 ? persistentClass : null );
-			en.setInterfaces( interfaces );
-
-			return en.createClass();
-
-		}
-		catch (Throwable t) {
-			LogFactory.getLog( BasicLazyInitializer.class )
-				.error( "CGLIB Enhancement failed: " + persistentClass.getName(), t );
-			throw new HibernateException( "CGLIB Enhancement failed: " + persistentClass.getName(), t );
-		}
+		Enhancer e = new Enhancer();
+		e.setSuperclass( interfaces.length == 1 ? persistentClass : null );
+		e.setInterfaces(interfaces);
+		e.setCallbackTypes(new Class[]{
+			InvocationHandler.class,
+			NoOp.class,
+	  		});
+  		e.setCallbackFilter(FINALIZE_FILTER);
+  		e.setUseFactory(false);
+		e.setInterceptDuringConstruction( false );
+		return e.createClass();
 	}
 
 	private CGLIBLazyInitializer(final String entityName, final Class persistentClass,
@@ -151,43 +137,75 @@
 		this.interfaces = interfaces;
 	}
 
-	public Object intercept(final Object proxy, final Method method, final Object[] args,
-			final MethodProxy methodProxy) throws Throwable {
-		if ( constructed ) {
+	private static boolean isCastable(Class caster, Class castee) {
+		if ( castee.equals( caster ) ) {
+			return true;
+		}
+		List list = addCheckingTypes( caster, new ArrayList() );
+		for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
+			Class cl = ( Class ) iter.next();
+			if ( castee.equals( cl ) ) {
+				return true;
+			}
+		}
+		return false;
+	}
 			
+	private static List addCheckingTypes(final Class type, final List list) {
+		Class superclass = type.getSuperclass();
+		if ( superclass != null ) {
+			list.add( superclass );
+			addCheckingTypes( superclass, list );
+		}
+		Class[] interfaces = type.getInterfaces();
+		for ( int i = 0; i < interfaces.length; ++i ) {
+			list.add( interfaces[i] );
+			addCheckingTypes( interfaces[i], list );
+		}
+		return list;
+	}
+
+	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
+		if ( constructed ) {
 			Object result = invoke( method, args, proxy );
 			if ( result == INVOKE_IMPLEMENTATION ) {
 				Object target = getImplementation();
 				final Object returnValue;
+				try {
 				if ( ReflectHelper.isPublic( persistentClass, method ) ) {
-					returnValue = methodProxy.invoke( target, args );
+						if ( !isCastable(
+								target.getClass(), method
+								.getDeclaringClass()
+						) ) {
+							throw new ClassCastException(
+									target.getClass()
+											.getName()
+							);
+						}
+						returnValue = method.invoke( target, args );
 				}
 				else {
 					if ( !method.isAccessible() ) method.setAccessible( true );
-					try {
 						returnValue = method.invoke( target, args );
 					}
+					return returnValue == target ? proxy : returnValue;
+				}
 					catch (InvocationTargetException ite) {
 						throw ite.getTargetException();
 					}
 				}
-				return returnValue == target ? proxy : returnValue;
-			}
 			else {
 				return result;
 			}
-			
 		}
 		else {
-			
 			// while constructor is running
 			if ( method.getName().equals( "getHibernateLazyInitializer" ) ) {
 				return this;
 			}
 			else {
-				return methodProxy.invokeSuper( proxy, args );
+				throw new LazyInitializationException("unexpected case hit, method=" + method.getName());
 			}
-			
 		}
 	}
 
