package net.sf.hibernate.xml;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.*;
import net.sf.hibernate.util.XMLHelper;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author    Ara Abrahamian (ara_e_w@yahoo.com)
 * @created   Sep 9, 2003
 * @version   $Revision: 1.9 $
 */
public class ReverseXMLDatabinder
{
    private List objects = new ArrayList();
    private Map objectsMap = new HashMap();
    private SessionFactoryImplementor factory;
    private SessionImplementor session;

    public ReverseXMLDatabinder(SessionFactoryImplementor factory, SessionImplementor session)
    {
        this.factory = factory;
        this.session = session;
    }

    public List getObjects()
    {
        return objects;
    }

    public void fromXML(InputStream xmlInputStream) throws HibernateException
    {
        try
        {
			List xmlValidationErrorsList = new ArrayList();
            SAXReader reader = XMLHelper.createSAXReader("XML InputStream", xmlValidationErrorsList);
            Document doc = reader.read(new InputSource(xmlInputStream));

            Element root = doc.getRootElement();
            Iterator iter = root.elementIterator();

            while (iter.hasNext())
            {
                Element element = (Element)iter.next();
                Class clazz = getClassForElement(element, "class", "package");
                ClassPersister persister = getPersister(clazz);
                Object obj = null;

                //ID (composite ids not supported atm)
                Type idType = persister.getIdentifierType();
                String idStrValue = element.elementText("id");

                //no id property, try composite id
                if (idStrValue == null)
                {
                    Element compositeIdElement = element.element("composite-id");
                    if (compositeIdElement != null)
                    {
                        List propertiesOfCompositeIdElement = compositeIdElement.elements("property");
                        String[] compositeIdValueStr = new String[propertiesOfCompositeIdElement.size()];

                        for (int i = 0; i < propertiesOfCompositeIdElement.size(); i++)
                        {
                            Element propertyOfCompositeIdElement = (Element)propertiesOfCompositeIdElement.get(i);
                            compositeIdValueStr[i] = propertyOfCompositeIdElement.getText();
                        }

                        obj = addObjectOrGetExisting(persister, compositeIdValueStr, idType, clazz);

                        Object[] propertyValuesOfCompositeId = new Object[propertiesOfCompositeIdElement.size()];
                        Type[] subtypes = ((ComponentType)idType).getSubtypes();
                        for (int i = 0; i < subtypes.length; i++)
                        {
                            Type subtype = subtypes[i];
                            propertyValuesOfCompositeId[i] = fromXml(subtype, compositeIdValueStr[i]);
                        }

                        ((ComponentType)idType).setPropertyValues(obj, propertyValuesOfCompositeId);
                    }
                }
                else
                {
                    obj = addObjectOrGetExisting(persister, idStrValue, idType, clazz);
                }

                //PROPERTIES
                List properties = element.elements("property");
                for (int i = 0; i < properties.size(); i++)
                {
                    Element propertyElement = (Element)properties.get(i);
                    setProperty(obj, persister, propertyElement);
                }

                //COLLECTIONS
                List collections = element.elements("collection");
                for (int i = 0; i < collections.size(); i++)
                {
                    Element collectionElement = (Element)collections.get(i);
                    setProperty(obj, persister, collectionElement);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Class getClassForElement(Element element, String classAttributeName, String packageAttributeName)
            throws ClassNotFoundException
    {
        String className = element.attributeValue(classAttributeName);
        String fullyQualifiedClassName = "";

        if (packageAttributeName != null)
        {
            String packageName = element.attributeValue(packageAttributeName);
            fullyQualifiedClassName += packageName + ".";
        }

        fullyQualifiedClassName += className;

        Class clazz = Class.forName(fullyQualifiedClassName);

        return clazz;
    }

    private Object addObjectOrGetExisting(ClassPersister persister, Object rawIdValue, Type idType, Class clazz)
            throws HibernateException, IllegalAccessException, InstantiationException
    {
        if (rawIdValue == null)
        {
            return clazz.newInstance();
        }

        Object obj = null;

        if (persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier())
        {
            Object idValue = null;
            ClassAndIdPair classAndIdPair = null;

            if (persister.hasIdentifierProperty())
            {
                idValue = fromXml(idType, (String)rawIdValue);
            }
            else if (persister.getIdentifierType() instanceof ComponentType)
            {
                idValue = rawIdValue;
            }

            classAndIdPair = new ClassAndIdPair(clazz, idValue);
            obj = objectsMap.get(classAndIdPair);

            if (obj == null)
            {
                if (persister.hasIdentifierProperty())
                {
                    obj = session.instantiate(clazz, (Serializable)idValue);
                }
                else
                {
                    obj = clazz.newInstance();
                }

                if (persister.hasIdentifierProperty())
                {
                    persister.setIdentifier(obj, (Serializable)idValue);
                }

                objects.add(obj);
                objectsMap.put(classAndIdPair, obj);
            }
        }
        else
        {
            //? just to be on the safe side
            obj = clazz.newInstance();
        }

        return obj;
    }

    private void setProperty(Object obj, ClassPersister persister, Element propertyElement)
            throws HibernateException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        String propertyName = propertyElement.attributeValue("name");
        String propertyValueStr = propertyElement.getText();
        Type type = persister.getPropertyType(propertyName);

        if (type.isPersistentCollectionType())
        {
            Collection collection = (Collection)((ClassMetadata)persister).getPropertyValue(obj,propertyName);

            if (collection == null)
            {
                CollectionPersister collectionPersister = session.getFactory().getCollectionPersister(((PersistentCollectionType)type).getRole());
                collection = (Collection)((PersistentCollectionType)type).instantiate(session, collectionPersister);
                //collection = (Collection)((PersistentCollectionType)type).getCollection(persister.getIdentifier(obj), obj, session);

                //latest Hibernate code seems to be broken, a hack:
                if (collection instanceof java.util.Set) {
                    collection = new java.util.HashSet();
                }
                else if (collection instanceof java.util.List) {
                    collection = new java.util.ArrayList();
                }

                ((ClassMetadata)persister).setPropertyValue(obj, propertyName, collection);
            }

            List elements = propertyElement.elements("element");
            for (int i = 0; i < elements.size(); i++)
            {
                Element elementElement = (Element)elements.get(i);
                String elementIdStr = elementElement.elementText("id");
                Class elementClazz = getClassForElement(elementElement, "class", "package");
                ClassPersister elementPersister = getPersister(elementClazz);
                Type elementIdType = elementPersister.getIdentifierType();

                Object element = addObjectOrGetExisting(elementPersister, elementIdStr, elementIdType, elementClazz);
                collection.add(element);
            }
        }
        else if (type.isEntityType())
        {
            String entityIdStr = propertyElement.elementText("id");
            //String entityIdStr = propertyElement.attributeValue("id");
            if (entityIdStr != null)
            {
                Class entityClazz = getClassForElement(propertyElement, "type", null);
                ClassPersister entityPersister = getPersister(entityClazz);
                Type entityIdType = entityPersister.getIdentifierType();

                Object propertyValue = addObjectOrGetExisting(persister, entityIdStr, entityIdType, entityClazz);
                ((ClassMetadata)persister).setPropertyValue(obj, propertyName, propertyValue);
            }
        }
        else
        {
            Object propertyValue = fromXml(type, propertyValueStr);
            ((ClassMetadata)persister).setPropertyValue(obj, propertyName, propertyValue);
        }
    }

    /**
     * @todo: correct TimestampType and DateType's fromString() to use the same format of the corresponding toString()
     */
    private Object fromXml(Type idType, String str) throws HibernateException
    {
        if(idType instanceof TimestampType)
        {
            try
            {
                if (str != null && str.trim().length() > 0)
                {
                    return new SimpleDateFormat("dd MMMM yyyy hh:mm:ss").parse(str);
                }
                else
                {
                    return null;
                }
            }
            catch (ParseException e)
            {
                return null;
            }
        }
        else if(idType instanceof DateType)
        {
            try
            {
                if (str != null && str.trim().length() > 0)
                {
                    return new SimpleDateFormat("dd MMMM yyyy").parse(str);
                }
                else
                {
                    return null;
                }
            }
            catch (ParseException e)
            {
                return null;
            }
        }
        else
        {
            return idType.fromString(str, factory);
        }
    }

    private ClassPersister getPersister(Class clazz) throws MappingException
    {
        return factory.getPersister(clazz);
    }

    private static final class ClassAndIdPair
    {
        public Class clazz;
        public Object id;

        public ClassAndIdPair(Class clazz, Object id)
        {
            this.clazz = clazz;
            this.id = id;
        }

        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        public int hashCode()
        {
            return clazz.getName().hashCode() + (id!=null ? id.hashCode() : 0);
        }
    }
}
