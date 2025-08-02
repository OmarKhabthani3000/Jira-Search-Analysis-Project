package org.hibernate.search;

import java.io.Serializable;

public class EntityInstanceAnalyzer implements Serializable {
    private String analyzerClassName;
    private Class[] analyzerParameterTypes;
    private Object[] analyzerParameterValues;

    public EntityInstanceAnalyzer(String analyzerClassName) {
        this.analyzerClassName = analyzerClassName;
    }

    public EntityInstanceAnalyzer(String analyzerClassName, Class[] analyzerParameterTypes, Object[] analyzerParameterValues) {
        this.analyzerClassName = analyzerClassName;
        this.analyzerParameterTypes = analyzerParameterTypes;
        this.analyzerParameterValues = analyzerParameterValues;
    }

    public String getAnalyzerClassName() {
        return analyzerClassName;
    }

    public Class[] getAnalyzerParameterTypes() {
        return analyzerParameterTypes;
    }

    public Object[] getAnalyzerParameterValues() {
        return analyzerParameterValues;
    }
}
