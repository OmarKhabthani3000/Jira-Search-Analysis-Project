//$Id: AddLuceneWork.java 12844 2007-07-29 14:56:47Z epbernard $
package org.hibernate.search.backend;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;

/**
 * @author Emmanuel Bernard
 */
public class AddLuceneWork extends LuceneWork {
    private Analyzer analyzer;

    public AddLuceneWork(Serializable id, String idInString, Class entity, Document document) {
		super( id, idInString, entity, document );
	}

    public AddLuceneWork(Serializable id, String idInString, Class entity, Document document, Analyzer analyzer) {
		super( id, idInString, entity, document );
        this.analyzer = analyzer;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
