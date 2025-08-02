package org.hibernate.tool.ant;

import java.io.File;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.POJOExporter;

public class CustomPojoExporter extends POJOExporter {

    public CustomPojoExporter(Configuration cfg, File outputdir) {
        super(cfg, outputdir);
    }

    public CustomPojoExporter() {
        super();
    }

}
