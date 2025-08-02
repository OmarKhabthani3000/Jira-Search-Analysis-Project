package com.sinapsi.hibernate3;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Lob;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.util.ReflectionUtils;


/**
 * Extension class used to fix https://hibernate.onjira.com/browse/HHH-4635 <br />
 * This will take the parsed hibernate configuration and re-arrange the column order in the entities that have Lobs,
 * forcing the Lob columns to be at the end of the list, because oracle accepts insert/updates ONLY if those columns are
 * at the end, otherwise it will throw error ORA-24816.
 * 
 * @author Danilo Ghirardelli
 */
public class CustomOracleLocalSessionFactoryBean extends LocalSessionFactoryBean {

    @Override
    @SuppressWarnings("unchecked")
    protected void postProcessConfiguration(Configuration config) throws HibernateException {
        super.postProcessConfiguration(config);
        // After the regular configuration, just move the @Lob columns at the end of the property list for each entity.
        Iterator<PersistentClass> entityIter = config.getClassMappings();
        while (entityIter.hasNext()) {
            PersistentClass entity = entityIter.next();
            Field propertiesField = ReflectionUtils.findField(entity.getClass(), "properties");
            if (propertiesField != null) {
                ReflectionUtils.makeAccessible(propertiesField);
                Object propertiesList = ReflectionUtils.getField(propertiesField, entity);
                if (propertiesList instanceof List< ? >) {
                    Collections.sort((List<Property>) propertiesList, new LobComparator());
                }
            }
            Field declaredPropertiesField = ReflectionUtils.findField(entity.getClass(), "declaredProperties");
            if (declaredPropertiesField != null) {
                ReflectionUtils.makeAccessible(declaredPropertiesField);
                Object propertiesList = ReflectionUtils.getField(declaredPropertiesField, entity);
                if (propertiesList instanceof List< ? >) {
                    Collections.sort((List<Property>) propertiesList, new LobComparator());
                }
            }
        }
    }

    /**
     * Comparator that will push the blob columns at the end of the column list.
     */
    private class LobComparator implements Comparator<Property> {

        @Override
        public int compare(Property o1, Property o2) {
            if ((hasLobAnnotation(o1)) && (hasLobAnnotation(o2))) {
                // Both Lobs, indifferent.
                return 0;
            }
            if ((hasLobAnnotation(o1)) && !(hasLobAnnotation(o2))) {
                // first is Lob, should be pushed to the end.
                return 1;
            }
            if (!(hasLobAnnotation(o1)) && (hasLobAnnotation(o2))) {
                // second is Lob, should be pushed to the end.
                return -1;
            }
            return 0;
        }

        /**
         * Internal check to see if a property is mapped as @Lob. If your Blobs are registered in other ways (i.e. with
         * xml) please adapt this.
         * 
         * @param p A property
         * @return true if it's a Lob
         */
        private boolean hasLobAnnotation(Property p) {
            Member ann = p.getGetter(p.getPersistentClass().getMappedClass()).getMember();
            if (ann instanceof Method) {
                Method m = (Method) ann;
                return (m.getAnnotation(Lob.class) != null);
            }
            if (ann instanceof Field) {
                Field f = (Field) ann;
                return (f.getAnnotation(Lob.class) != null);
            }
            return false;
        }
    }
}
