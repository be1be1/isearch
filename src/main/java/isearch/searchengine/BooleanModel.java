package isearch.searchengine;

import java.util.*;
import isearch.preprocessingmodule.InvertedFile;
import isearch.preprocessingmodule.PostingNode;
import isearch.preprocessingmodule.TermNode;
import isearch.structure.Query;

public class BooleanModel extends Model{
	private final int maxResultNum = 1000;
	private int resultCount;
	private HashMap<Integer, Double> retrievedList; //documentID, zero (no need rank);
	
	public BooleanModel(InvertedFile invertedFile) {
		super(invertedFile);
	}

	@Override
	public HashMap<Integer, Double> search(Query query) {
		resultCount = 0;
		retrievedList = new HashMap<Integer, Double>(); 
    	Hashtable<String, Integer> keywordList;
		keywordList = query.returnKeywordList();
		Enumeration<String> enumKey = keywordList.keys();
		while(enumKey.hasMoreElements()) {
		    String key = enumKey.nextElement();
		    searchDocuments(this.invertedFile.searchHashTable(key));
		}
		return retrievedList;
	}

	private void searchDocuments(TermNode termNode) {
		if (termNode == null) {
			return;
		}
		ListIterator<PostingNode> pl = termNode.getPostingIterator();
		while (pl.hasNext()) {
			PostingNode pn = pl.next();
			int docId = pn.getDocumentId();
			double score = 0;
			if (!retrievedList.containsKey(docId))
				resultCount++;
			if (resultCount > maxResultNum)
				break;
			retrievedList.put(docId, score);			
			
		}		
	}

	@Override
	public HashMap<Integer, Double> search_NormalizeByMax(Query query) {
		// no weighting
		return this.search(query);
	}

	@Override
	public HashMap<Integer, Double> search_NormalizeBySum(Query query) {
		// no weighting
		return this.search(query);
	}

}
