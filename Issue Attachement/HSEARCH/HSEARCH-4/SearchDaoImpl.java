package ro.tremend.urban.dao.hibernate;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.hibernate.search.backend.Workspace;
import org.hibernate.search.event.FullTextIndexEventListener;
import ro.tremend.urban.dao.DocMapper;
import ro.tremend.urban.dao.SearchDao;
import ro.tremend.urban.lucene.LuceneIndexManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Search support
 *
 * @author icocan
 */
public class SearchDaoImpl extends BaseDAOHibernate implements SearchDao {
    FullTextIndexEventListener luceneEventListener;

    public List search(org.apache.lucene.search.Query query, Sort sort, Class searchedClass, DocMapper docMapper, int searchSize)
    {
        Workspace workspace = new Workspace(luceneEventListener.getDocumentBuilders(),
                luceneEventListener.getLockableDirectoryProviders());
        List results = new ArrayList();

        IndexReader reader = null;
        try
        {
            reader = workspace.getIndexReader(searchedClass);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, null, searchSize, sort);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs)
            {
                Document document = searcher.doc(scoreDoc.doc);
                Object obj = docMapper.mapDocument(document);

                results.add(obj);
            }
        } catch (IOException e)
        {
            logger.error("Could not execute search", e);
        } finally
        {
            LuceneIndexManager.closeQuietly(reader);
        }
        return results;
    }


    public void setLuceneEventListener(FullTextIndexEventListener luceneEventListener)
    {
        this.luceneEventListener = luceneEventListener;
    }
}
