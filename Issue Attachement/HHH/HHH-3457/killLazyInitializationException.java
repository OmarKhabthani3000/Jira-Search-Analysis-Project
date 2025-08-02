private void killLazyInitializationException(Object entity) {
    Class<?> entityClass = entity.getClass();
    
    try {
      for (Field field : entityClass.getDeclaredFields()) {
        field.setAccessible(true);
        System.out.println(field);
        Class<?> fieldClass = field.get(entity).getClass();
        if (AbstractPersistentCollection.class.isAssignableFrom(fieldClass)) {
          AbstractPersistentCollection persistentCollection = (AbstractPersistentCollection) field.get(entity);
          if (!persistentCollection.wasInitialized()) {
            System.out.println("\t" + field + " was not initialized!");
            setInitialized(persistentCollection);
            System.out.println("\tNow: " + persistentCollection.wasInitialized());
          }
        } else if (HibernateProxy.class.isAssignableFrom(fieldClass)) {
          HibernateProxy proxy = (HibernateProxy) field.get(entity);
          if (proxy.getHibernateLazyInitializer().isUninitialized()) {
            System.out.println("\t" + field + " was not initialized!");
            setInitialized(proxy);
            System.out.println("\tNow: " + !proxy.getHibernateLazyInitializer().isUninitialized());
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void setInitialized(HibernateProxy proxy) {
    Class<?> lazyInitializer = getSuperClass(proxy.getHibernateLazyInitializer(), AbstractLazyInitializer.class);
    try {
      Field initializedField = lazyInitializer.getDeclaredField("initialized");
      initializedField.setAccessible(true);
      initializedField.set(proxy.getHibernateLazyInitializer(), true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void setInitialized(AbstractPersistentCollection persistentCollection) {
    Class<?> entityClass = getSuperClass(persistentCollection, AbstractPersistentCollection.class);
    
    try {
      Method setInitializedMethod = entityClass.getDeclaredMethod("setInitialized", (Class<?>[]) null);
      setInitializedMethod.setAccessible(true);
      setInitializedMethod.invoke(persistentCollection, (Object[]) null);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private Class<?> getSuperClass(Object object, Class<?> superClass) {
    Class<?> objectClass = object.getClass();
    
    if (objectClass == superClass) {
      return objectClass;
    }
    
    do {
      objectClass = objectClass.getSuperclass();
      if (objectClass == Object.class) {
        throw new RuntimeException(object + " has no superclass " + superClass);
      }
    } while (objectClass != superClass);
    
    return objectClass;
  }
