package com.powersystem.util;

import com.powersystem.exception.BeanException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ServicioBase implements IServicioBase {

    private static final long serialVersionUID = 1L;

    /**
     * EntityManager principal que apunta a la base de datos de Personas
     * Jurídicas (SPJ)
     */
    @PersistenceContext(unitName = "SEAR")
    protected EntityManager em;

    protected JPAQueryFactory qf;

    @PostConstruct
    private void init() {
        qf = new JPAQueryFactory(em);
    }

    @Override
    public void actualizar(Object g) {
        try {
            em.merge(g);
            em.flush();
        } catch (Exception e) {
            log.error("ServicioBase.actualizar({})", g.toString(), e);
            throw new BeanException(e);
        }
    }   //guardar


}
