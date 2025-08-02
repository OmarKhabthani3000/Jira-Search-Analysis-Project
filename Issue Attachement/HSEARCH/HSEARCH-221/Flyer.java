package com.mediatorsystems.pf.domain;

import org.jboss.seam.annotations.Name;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Kenneth Christensen
 *         Copyright (c) 2006, 2007, 2008 Mediator Systems ApS. All rights reserved.
 */
@Entity
@Name("flyerDomain")
@Table(name = "flyer")
@Indexed
public class Flyer implements Serializable {
    private Long id;
    private String language;
    private String text;

    @Id
    @GeneratedValue
    @DocumentId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Field(index = Index.UN_TOKENIZED, store = Store.YES)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Field(index = Index.TOKENIZED, store = Store.NO)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Transient
    public Analyzer getLuceneAnalyzer() {
        return new SnowballAnalyzer(language);
    }
}
