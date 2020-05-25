package isearch.searchengine;

/**
 * Created by Beibei on 25/10/2016.
 */
import java.util.*;

import isearch.preprocessingmodule.Document;
import isearch.preprocessingmodule.InvertedFile;
import isearch.preprocessingmodule.PostingNode;
import isearch.preprocessingmodule.TermNode;
import isearch.structure.Query;

public class VectorSpaceModel extends Model{
	private double totalWeight;
	private int totalDoc;	
	private HashMap<Integer, Double> docLengthMap;
	private HashMap<Integer, Double> docLengthMap_Max;
	private HashMap<Integer, Double> docLengthMap_Sum;
	
	private HashMap<Integer, Double> runningScore;	//documentId, ranking value
	//private Hashtable<Integer, Double> docArray;  	//documentId, document length 
	private LinkedHashMap<Integer, Double> rankedList;	//documentId, ranking value (sorted)
	private final int maxResultNum = 1000;
	
	public VectorSpaceModel(InvertedFile invertedFile) {
		super(invertedFile);
		totalDoc = invertedFile.getN();
		runningScore = new HashMap<Integer, Double>();
		docLengthMap = new HashMap<Integer, Double>();
		docLengthMap_Max = new HashMap<Integer, Double>();
		docLengthMap_Sum = new HashMap<Integer, Double>();
		//docArray = new Hashtable<Integer, Double>();
		//initialDocLength();
	}
	
	@Override
	public HashMap<Integer, Double> search(Query query) {
		runningScore = new HashMap<Integer, Double>();
		rankedList = new LinkedHashMap<Integer, Double>();		
		totalWeight = 0;
		Hashtable<String, Integer> keywordList;
		keywordList = query.returnKeywordList();
		Enumeration<String> enumKey = keywordList.keys();
		while(enumKey.hasMoreElements()) {
			String key = enumKey.nextElement();
			Integer freq = keywordList.get(key);
			totalWeight += freq*freq;
			calculateScore(this.invertedFile.searchHashTable(key),freq, Document.NORMALIZE_BY_FREQ);
		}

		sortScore();
		return rankedList;

	}

	public HashMap<Integer, Double> search_NormalizeByMax(Query query) {
		runningScore = new HashMap<Integer, Double>();
		rankedList = new LinkedHashMap<Integer, Double>();
		totalWeight = 0;
		Hashtable<String, Integer> keywordList;
		keywordList = query.returnKeywordList();
		Enumeration<String> enumKey = keywordList.keys();
		while(enumKey.hasMoreElements()) {
			String key = enumKey.nextElement();
			Integer freq = keywordList.get(key);
			totalWeight += freq*freq;
			calculateScore(this.invertedFile.searchHashTable(key),freq,Document.NORMALIZE_BY_MAX_FREQ);
		}

		sortScore();
		return rankedList;

	}

	public HashMap<Integer, Double> search_NormalizeBySum(Query query) {
		runningScore = new HashMap<Integer, Double>();
		rankedList = new LinkedHashMap<Integer, Double>();		
		totalWeight = 0;
		Hashtable<String, Integer> keywordList;
		keywordList = query.returnKeywordList();
		Enumeration<String> enumKey = keywordList.keys();
		while(enumKey.hasMoreElements()) {
			String key = enumKey.nextElement();
			Integer freq = keywordList.get(key);
			totalWeight += freq*freq;
			calculateScore(this.invertedFile.searchHashTable(key),freq,Document.NORMALIZE_BY_SUM_FREQ);
		}

		sortScore();
		return rankedList;

	}

	private void calculateScore(TermNode termNode, int queryWeight, int option){
		if (termNode == null) {
			return;
		}
		double idf = Math.log10((double)totalDoc/(double)termNode.getDf());
		ListIterator<PostingNode> pl = termNode.getPostingIterator();
		while (pl.hasNext()) {
			PostingNode pn = pl.next();
			int docId = pn.getDocumentId();
			double docFreq = pn.getFreq();
			double docLength = 0;
			if (option == Document.NORMALIZE_BY_FREQ) {
				docFreq = docFreq * 1.0;
				if (docLengthMap.containsKey(docId))
					docLength = docLengthMap.get(docId);
				else {
					docLength = Document.getDocLengthByDocId(docId, option , invertedFile);	
					docLengthMap.put(docId, docLength);
				}	
			} else if (option == Document.NORMALIZE_BY_MAX_FREQ) {
				docFreq = docFreq * 1.0 / Document.getMaxFreqAmongTerms(docId) * 1.0;
				if (docLengthMap_Max.containsKey(docId))
					docLength = docLengthMap_Max.get(docId);
				else {
					docLength = Document.getDocLengthByDocId(docId, option , invertedFile);	
					docLengthMap_Max.put(docId, docLength);
				}	
			} else if (option == Document.NORMALIZE_BY_SUM_FREQ) {
				docFreq = docFreq * 1.0 / Document.getTotalNumOfTerms(docId) * 1.0;
				if (docLengthMap_Sum.containsKey(docId))
					docLength = docLengthMap_Sum.get(docId);
				else {
					docLength = Document.getDocLengthByDocId(docId, option , invertedFile);	
					docLengthMap_Sum.put(docId, docLength);
				}	
			}
			
								
			double score = idf*(double)docFreq*queryWeight/docLength;			
			if (runningScore.containsKey(docId)){
				runningScore.put(docId, runningScore.get(docId) + score);
			} else {
				runningScore.put(docId, score);
			}
		}

	}

	private void sortScore() {
		List<Map.Entry<Integer, Double>> list =
				new LinkedList<Map.Entry<Integer, Double>>(runningScore.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1,
							   Map.Entry<Integer, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue()) == 1 ? -1 : 1;
			}
		});

		rankedList = new LinkedHashMap<Integer, Double>();
		int resultCount = 0;
		for (Map.Entry<Integer, Double> entry : list) {
			resultCount++;
			if (resultCount > maxResultNum)
				break;
			rankedList.put(entry.getKey(), entry.getValue()/Math.sqrt(totalWeight));
		}

	}


}