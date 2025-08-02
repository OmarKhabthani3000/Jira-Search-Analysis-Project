// Copyright (c) 2005 Health Market Science, Inc.

import example.Cat;
import example.PolydactylCat;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;


/**
 *  Create a schema to herd cats.
 */
public class Test {
    
    public static void main(String[] argv) {
        Configuration cfg = new Configuration();
        cfg.addClass(Cat.class);
        cfg.addClass(PolydactylCat.class);

        SessionFactory sessFact = cfg.buildSessionFactory();

        SchemaExport export = new SchemaExport(cfg);
        export.create(true, true);
        
        sessFact.close();
    }
}
