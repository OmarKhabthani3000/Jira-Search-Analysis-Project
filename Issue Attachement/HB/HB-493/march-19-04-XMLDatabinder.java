import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.proxy.HibernateProxy;
import net.sf.hibernate.proxy.HibernateProxyHelper;
import net.sf.hibernate.proxy.LazyInitializer;
import net.sf.hibernate.type.*;
import net.sf.hibernate.util.StringHelper;

import java.lang.reflect.Array;
import java.util.*;
import java.io.Writer;
import java.io.IOException;

//import com.atlassian.core.util.StringUtils;
//import com.atlassian.confluence.util.GeneralUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLDatabinder
{
    //~ Instance variables ---------------------------------------------------------------------------------------------
    private SessionFactoryImplementor factory;
    private List objects = new ArrayList();
    private Map excludedObjects = new HashMap();
    private Set associatedObjects;
    private Set processedObjects;
    private boolean initializeLazy = false;
    private String encoding;
    private static Log log = LogFactory.getLog(XMLDatabinder.class);


    // some static variables to make things faster - I think - less String creation?
    private static final String LEFT_CHEVRON = "<";
    private static final String RIGHT_CHEVRON = ">";
    private static final String CARRIAGE_RETURN = "\n";
    private static final String START_CLOSE_TAG = "</";
    private static final String END_TAG_CARRIAGE_RETURN = RIGHT_CHEVRON + CARRIAGE_RETURN;
    private static final String CONST_NAME = "name";
    private static final String CONST_CLASS = "class";
    private static final String CONST_COMPOSITE_ELEMENT = "composite-element";
    private static final String CONST_ELEMENT = "element";
    private static final String CONST_SUBCOLLECTION = "subcollection";
    private static final String CONST_ID = "id";
    private static final String CONST_COMPOSITE_ID = "composite-id";
    private static final String CONST_OPEN_OBJECT_TAG = "<object";
    private static final String CONST_CLOSE_OBJECT_TAG = "</object>" + CARRIAGE_RETURN;
    private static final String CONST_COLLECTION = "collection";
    private static final String CONST_PROPERTY = "property";
    private static final String CONST_COMPONENT = "component";
    private static final String CONST_TYPE = "type";
    private static final String CONST_OPEN_CDATA = "<![CDATA[";
    private static final String CONST_CLOSE_CDATA = "]]>";

    //~ Constructors ---------------------------------------------------------------------------------------------------

    public XMLDatabinder(SessionFactoryImplementor factory, String encoding)
    {
        this.factory = factory;
        this.encoding = encoding;
    }

    //~ Methods --------------------------------------------------------------------------------------------------------

    private ClassPersister getPersister(Class clazz) throws MappingException
    {
        return factory.getPersister(clazz);
    }

    public void setInitializeLazy(boolean initializeLazy)
    {
        this.initializeLazy = initializeLazy;
    }

    public void toGenericXML(Writer writer) throws HibernateException, IOException
    {
        this.associatedObjects = new HashSet();
        this.processedObjects = new HashSet();

        writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
        String date = Hibernate.TIMESTAMP.toString(new Date(), factory);
        writer.write("<hibernate-generic datetime=\"" + date + "\">\n");

        Iterator iter;

        // keep going until we run out of bound objects and their associated objects
        while (objects.size() > 0)
        {
            log.debug("OUTSIDE ITERATOR::: System.currentTimeMillis() = " + System.currentTimeMillis() + " " + objects.size());
            iter = objects.iterator();
            int count = 0;

            while (iter.hasNext())
            {
                Object object = iter.next();

                if (count++ % 300 == 0)
                    log.debug("System.currentTimeMillis() = " + System.currentTimeMillis() + " " + count + " " + objects.size());

                if (excludedObjects.containsKey(object))
                {
                    continue;
                }

                writer.write(CONST_OPEN_OBJECT_TAG);

                object = maybeInitializeIfProxy(object);

                if (object != null)
                {
                    addClass(writer, object.getClass());
                    writer.write(END_TAG_CARRIAGE_RETURN);

                    ClassPersister persister = getPersister(object.getClass());

                    //ID
                    if (persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier())
                    {
                        Object id = persister.getIdentifier(object);
                        renderProperty(writer, persister.getIdentifierPropertyName(),
                                persister.getIdentifierType(), id, CONST_COMPOSITE_ID, CONST_ID, null, false);
                    }

                    //PROPERTIES
                    Type[] types = persister.getPropertyTypes();
                    Object[] values = persister.getPropertyValues(object);
                    String[] names = persister.getPropertyNames();

                    //This approach wont work for components + collections
                    for (int i = 0; i < types.length; i++)
                    {
                        if (excludedObjects.containsKey(values[i]))
                        {
                            continue;
                        }

                        renderProperty(writer, names[i], types[i], values[i], CONST_COMPONENT, CONST_PROPERTY, CONST_COLLECTION, false);
                    }

                    writer.write(CONST_CLOSE_OBJECT_TAG);
                }
            }

            processedObjects.addAll(objects);
            objects = new ArrayList(associatedObjects);
            associatedObjects = new HashSet();
        }

        writer.write("</hibernate-generic>");
    }

    private void addClass(Writer writer, Class clazz) throws IOException
    {
        String className = clazz.getName();
        String unqualifiedClassName = StringHelper.unqualify(className);
        String packageName = StringHelper.qualifier(className);
        writer.write(" class=\"" + unqualifiedClassName + "\" package=\"" + packageName + "\"");
    }

    private Object maybeInitializeIfProxy(Object object)
    {
        if (!(object instanceof HibernateProxy))
        {
            return object;
        }

        LazyInitializer li = HibernateProxyHelper.getLazyInitializer((HibernateProxy)object);

        if (li.isUninitialized() && !initializeLazy)
        {
            return null;
        }

        return li.getImplementation();
    }

    public XMLDatabinder bind(Object object)
    {
        objects.add(object);

        return this;
    }

    public XMLDatabinder unbind(Object object)
    {
        excludedObjects.put(object, object);

        return this;
    }

    public XMLDatabinder bindAll(Collection objects)
    {
        this.objects.addAll(objects);

        return this;
    }

    public XMLDatabinder unbindAll(Collection objects)
    {
        for (Iterator iterator = objects.iterator(); iterator.hasNext();)
        {
            Object object = iterator.next();
            this.excludedObjects.put(object, object);
        }

        return this;
    }

    private void renderProperty(Writer writer, String name, Type type, Object value, String componentName, String propertyName,
                                String collectionName, boolean doType) throws HibernateException, IOException
    {
        if (type.isComponentType())
        {
            renderComponentType(writer, name, type, value, componentName, doType);
        }
        else if (type.isPersistentCollectionType())
        {
            renderCollectionType(writer, name, type, value, collectionName, doType);
        }
        else if (type.isEntityType())
        {
            renderEntityType(writer, name, type, value, propertyName, doType);
        }
        else
        {
            renderOtherType(writer, name, type, value, propertyName, doType);
        }
    }

    private void renderOtherType(Writer writer, String name, Type type, Object value, String propertyName, boolean doType)
        throws HibernateException, IOException
    {
        writer.write(LEFT_CHEVRON + propertyName);
        if (name != null)
        {
            appendAttribute(writer, CONST_NAME, name);
        }
        if (doType)
        {
            appendAttribute(writer, CONST_TYPE, type.getName());
        }

        if (value != null)
        {
            writer.write(RIGHT_CHEVRON);

            String xmlValue = type.toString(value, factory);

            if (type instanceof StringType)
            {
//                writer.write(CONST_OPEN_CDATA + GeneralUtil.escapeCDATA(StringUtils.escapeCP1252(xmlValue, encoding)) + CONST_CLOSE_CDATA);
                writer.write(CONST_OPEN_CDATA + xmlValue, encoding + CONST_CLOSE_CDATA);
            }
            else
            {
                writer.write(xmlValue);
            }

            writer.write(START_CLOSE_TAG + propertyName + END_TAG_CARRIAGE_RETURN);
        }
        else
        {
            writer.write("/>");
        }
    }

    private void appendAttribute(Writer writer, String attributeName, String attributeValue) throws IOException
    {
        writer.write(" " + attributeName + "=\"" + attributeValue + "\"");
    }

    private void renderEntityType(Writer writer, String name, Type type, Object value, String propertyName, boolean doType)
        throws HibernateException, IOException
    {
        if ((value = maybeInitializeIfProxy(value)) != null)
        {
            writer.write(LEFT_CHEVRON + propertyName);
            if (name != null)
            {
                appendAttribute(writer, CONST_NAME, name);
            }
            if (doType)
            {
                appendAttribute(writer, CONST_TYPE, type.getName());
            }

            addClass(writer, value.getClass());
            writer.write(RIGHT_CHEVRON);

            ClassPersister persister = getPersister(value.getClass());

            if (persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier())
            {
                Type idType = persister.getIdentifierType();
                Object id = persister.getIdentifier(value);
                renderProperty(writer, persister.getIdentifierPropertyName(), idType, id, CONST_COMPOSITE_ID, CONST_ID, null, false);
            }

            // avhiboid duplications (including objects that have a field referencing to themselves)
            if (!processedObjects.contains(value) && !objects.contains(value))
            {
                associatedObjects.add(value);
            }

            writer.write(START_CLOSE_TAG + propertyName + RIGHT_CHEVRON + CARRIAGE_RETURN);
        }
    }

    private void renderCollectionType(Writer writer, String name, Type type, Object value, String collectionName, boolean doType)
        throws HibernateException, IOException
    {
        if (value == null)
        {
            return;
        }

        PersistentCollectionType collectiontype = (PersistentCollectionType)type;
        String role = collectiontype.getRole();
        CollectionPersister persister = factory.getCollectionPersister(role);

        if (persister.isArray())
        {
            int length = Array.getLength(value);
            if (length == 0)
            {
                return;
            }
        }
        else if (value instanceof Collection)
        {
            if (((Collection)value).isEmpty())
            {
                return;
            }
        }

        if (persister.isArray())
        {
            collectionName = "array";
        }

        writer.write(LEFT_CHEVRON + collectionName);
        if (name != null)
        {
            appendAttribute(writer, CONST_NAME, name);
        }
        if ((!persister.isArray()) && doType)
        {
            appendAttribute(writer, CONST_CLASS, type.getName());
        }

        Type elemType = persister.getElementType();
        writer.write(RIGHT_CHEVRON);

        if (persister.isArray())
        {
            int length = Array.getLength(value);

            for (int i = 0; i < length; i++)
            {
                renderProperty(writer, null, elemType, Array.get(value, i), CONST_COMPOSITE_ELEMENT,
                        CONST_ELEMENT, CONST_SUBCOLLECTION, false);
            }
        }
        else
        {
            boolean wasInitialized = false;
            // "real" collections
            if (value instanceof PersistentCollection)
            {
                PersistentCollection persistentCollection = (PersistentCollection)value;
                wasInitialized = persistentCollection.wasInitialized();
            }

            if (!(persister.isLazy() && !this.initializeLazy && !wasInitialized))
            {
                // Try to do this next bit polymorphically, instead of the following:
                if (type instanceof ListType)
                {
                    Iterator iter = ((List)value).iterator();
                    while (iter.hasNext())
                    {
                        Object collectionItem = iter.next();

                        if (excludedObjects.containsKey(collectionItem))
                        {
                            continue;
                        }

                        renderProperty(writer, null, elemType, collectionItem, CONST_COMPOSITE_ELEMENT,
                                CONST_ELEMENT, CONST_SUBCOLLECTION, false);
                    }
                }
                else if ((type instanceof SetType) || (type instanceof BagType))
                {
                    Iterator iter = ((Collection)value).iterator();

                    while (iter.hasNext())
                    {
                        Object collectionItem = iter.next();

                        if (excludedObjects.containsKey(collectionItem))
                        {
                            continue;
                        }

                        renderProperty(writer, null, elemType, collectionItem, CONST_COMPOSITE_ELEMENT,
                                CONST_ELEMENT, CONST_SUBCOLLECTION, false);
                    }
                }
                else if (type instanceof MapType)
                {
                    Iterator iter = ((Map)value).entrySet().iterator();

                    while (iter.hasNext())
                    {
                        Map.Entry e = (Map.Entry)iter.next();
                        Object collectionItem = e.getValue();

                        if (excludedObjects.containsKey(collectionItem))
                        {
                            continue;
                        }

                        renderProperty(writer, null, elemType, collectionItem, CONST_COMPOSITE_ELEMENT,
                                CONST_ELEMENT, CONST_SUBCOLLECTION, false);
                    }
                }
            }
        }

        writer.write(START_CLOSE_TAG + collectionName + END_TAG_CARRIAGE_RETURN);
    }

    private void renderComponentType(Writer writer, String name, Type type, Object value, String componentName, boolean doType)
        throws HibernateException, IOException
    {
        if (value != null)
        {
            AbstractComponentType componenttype = (AbstractComponentType)type;
            writer.write(LEFT_CHEVRON + componentName);

            if (name != null)
            {
                appendAttribute(writer, CONST_NAME, name);
            }
            if (doType)
            {
                appendAttribute(writer, CONST_CLASS, type.getName());
            }
            writer.write(RIGHT_CHEVRON);

            String[] properties = componenttype.getPropertyNames();
            Object[] subvalues = componenttype.getPropertyValues(value, null);//We know that null is okay here .. at least for ComponentType .... TODO: something safer??
            Type[] subtypes = componenttype.getSubtypes();

            for (int j = 0; j < properties.length; j++)
            {
                renderProperty(writer, properties[j], subtypes[j], subvalues[j], CONST_COMPONENT, CONST_PROPERTY,
                        CONST_COLLECTION, false);
            }

            writer.write(START_CLOSE_TAG + componentName + END_TAG_CARRIAGE_RETURN);
        }
    }
}
