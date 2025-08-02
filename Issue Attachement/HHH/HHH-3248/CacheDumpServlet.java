/*
 * MonitoringServlet.java
 * 
 * Copyright (c) 2005 Cegedim, all rights reserved.
 * 
 * This document and the information it contains are confidential and the exclusive property of CEGEDIM. They shall not be reproduced
 * nor disclosed to any person, except to those having a need to know them, without prior written consent of CEGEDIM.
 * 
 * Created: 18 oct. 07, by PGirolami
 */
package com.cegedim.onekey.common.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.cache.ReadWriteCache;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.cache.entry.CollectionCacheEntry;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CacheDumpServlet extends HttpServlet {
    private static final String SEPARATOR=";";
    private static final String NOTHING="-";
    private static final String OBJECT="Object";
    private static final String COLLECTION="Collection";
    private static final String UNKNOWN="Unknown";
    private static final String EMPTY="";

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
    {
        try {
            String region = arg0.getParameter("region");
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            SessionFactory sessionFactory = (SessionFactory) ctx.getBean("fwk-hibernateSessionFactory");
            Map<Object, ReadWriteCache.Item> entries = sessionFactory.getStatistics().getSecondLevelCacheStatistics(region).getEntries();
            Collection<Object> keys=entries.keySet();
            OutputStream os = new BufferedOutputStream(arg1.getOutputStream());
            PrintStream ps=new PrintStream(os);
            StringBuffer line=new StringBuffer("TYPE;KEY;areLazyPropertiesUnfetched;Id;timestamp;isLocked;The rest");
            ps.println(line.toString());            
            line.setLength(0);
            CacheEntry cacheEntry;
            CollectionCacheEntry collectionCacheEntry;
            ReadWriteCache.Item element;
            String _type=null;
            String _key=null;
            String _areLazyPropertiesUnfetched=null;
            String _id=null;
            long _timestamp=-1;
            String _theRest=null;
            for (Object key: keys) {
                element=entries.get(key);
                if (element.getValue() instanceof CacheEntry) {
                    cacheEntry=(CacheEntry) element.getValue();
                    final Serializable[] disassembledState = cacheEntry.getDisassembledState();

                    _type=OBJECT;
                    _key=key.toString();
                    _areLazyPropertiesUnfetched=String.valueOf(cacheEntry.areLazyPropertiesUnfetched());
                    _id=String.valueOf(disassembledState[0]);
                    _theRest=cacheEntry.toString();
                } else if (element.getValue() instanceof CollectionCacheEntry ){                    
                    collectionCacheEntry=(CollectionCacheEntry) element.getValue();

                    
                    _type=COLLECTION;
                    _key=key.toString();
                    _areLazyPropertiesUnfetched=NOTHING;
                    _id=NOTHING;
                    _theRest=collectionCacheEntry.toString();
                }else
                {   
                    _type=UNKNOWN;
                    _key=key.toString();
                    _areLazyPropertiesUnfetched=NOTHING;
                    _id=NOTHING;
                    _theRest=element.getValue().toString();

                }
                line.append(_type);
                line.append(SEPARATOR);
                line.append(_key);
                line.append(SEPARATOR);
                line.append(_areLazyPropertiesUnfetched);
                line.append(SEPARATOR);   
                line.append(_id);
                line.append(SEPARATOR);
                line.append(new Date(element.getFreshTimestamp()).toString());
                line.append(SEPARATOR);
                line.append(element.isLock());
                line.append(SEPARATOR);
                line.append(_theRest);
                
                ps.println(line.toString());
                line.setLength(0);
            }
            ps.flush();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}

// MonitoringServlet.java
