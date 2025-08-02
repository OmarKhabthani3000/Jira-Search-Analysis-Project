import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.SortedVIntList;
import org.hibernate.search.filter.AndDocIdSet;
import org.junit.Test;


public class AndDocIdSetTest {
	private SortedVIntList idSet1 = new SortedVIntList(new int[] {0, 5, 6, 10}); 
	
	@Test
	public void test_last() throws IOException {
		SortedVIntList idSet = new SortedVIntList(new int[] {1, 10});
		int docId = createAndDocIdSet(idSet).iterator().nextDoc();
		assertTrue(docId == 10);
	}
	
	@Test
	public void test_first() throws IOException {
		SortedVIntList idSet = new SortedVIntList(new int[] {0});
		int docId = createAndDocIdSet(idSet).iterator().nextDoc();
		assertTrue(docId == 0);
	}
	
	@Test
	public void test_middle() throws IOException {
		SortedVIntList idSet = new SortedVIntList(new int[] {6});
		int docId = createAndDocIdSet(idSet).iterator().nextDoc();
		assertTrue(docId == 6);
	}

	private AndDocIdSet createAndDocIdSet(DocIdSet secondDocIdSet) {
		List<DocIdSet> idSetList = new ArrayList<DocIdSet>();
		idSetList.add(idSet1);
		idSetList.add(secondDocIdSet);
		return new AndDocIdSet(idSetList, 11);
	}
}
