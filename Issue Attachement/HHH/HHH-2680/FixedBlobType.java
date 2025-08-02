package com.texunatech.hibernate.type;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.BlobType;
import java.sql.Blob;
import java.util.Map;

public class FixedBlobType extends BlobType {

    @Override
    public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) throws HibernateException {
        if(original == null) {
			return null;
		}
		try {
            return Hibernate.createBlob(((Blob)original).getBinaryStream());
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }
}
