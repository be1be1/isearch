package isearch.preprocessingmodule;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Document {
	
	private Integer docId = null;
	//private Hashtable<String, TermNode> termCount = null;
	private Hashtable<String, Integer> termCount = null;
	
	private static Hashtable<Integer, Document> documents = new Hashtable<Integer, Document>();
	public static int NORMALIZE_BY_FREQ = 0;
	public static int NORMALIZE_BY_MAX_FREQ = 1;
	public static int NORMALIZE_BY_SUM_FREQ = 2;

	private Document(int docId) {
		this.docId = docId;
		this.termCount = new Hashtable<String, Integer>();
	}
	
	public synchronized static Document getInstance(int docId) {
		if (Document.documents.containsKey(docId)) {
			return Document.documents.get(docId);
		}
		Document d = new Document(docId);
		Document.documents.put(docId, d);
		return d;
	}
	
	public synchronized static void clearInstance() {
		Document.documents.clear();
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public Hashtable<String, Integer> getTermCount() {
		return termCount;
	}
	
	public void addTerm(String term, int pos) {
		//TermNode tn = null;
		int count = 0;
		if (this.termCount.containsKey(term)) {
			count = this.termCount.get(term);
		}
		
		count++;
		this.termCount.put(term, count);
	}
	
	
	public Double getDocLength(int option, InvertedFile invertedFile) {
		return Math.sqrt(this.getInvertedIndex(option, invertedFile));
	}
	
	public static Double getDocLengthByDocId(int docId, int option, InvertedFile invertedFile) {
		return Document.getInstance(docId).getDocLength(option, invertedFile);
	}
	
	public Double getInvertedIndex(int option, InvertedFile invertedFile) {
		double r = 0.0;
		
		int totalDoc = invertedFile.getN();
		double base = 1.0;
		
		if (option == Document.NORMALIZE_BY_MAX_FREQ) {
			base = this.getMaxFreqAmongTerms() * 1.0;
		} else if (option == Document.NORMALIZE_BY_SUM_FREQ) {
			base = this.getTotalNumOfTerms() * 1.0;
		}
		
		Enumeration<String> e = this.termCount.keys();
		while (e.hasMoreElements()) {
			String term = e.nextElement();
			int freq = this.termCount.get(term);
			if (freq <= 0) {
				continue;
			}
						
			double idf = Math.log10((totalDoc * 1.0) / (invertedFile.getDf(term) * 1.0));
			double tf = freq * 1.0;
			
//			if (option == Document.NORMALIZE_BY_FREQ) {
//				tf = freq * 1.0;
//			} else if (option == Document.NORMALIZE_BY_MAX_FREQ) {
//				Enumeration<Integer> x = this.termCount.elements();
//				while (x.hasMoreElements()) {
//					base = Math.max(base, x.nextElement());
//				}
//				//tf = freq * 1.0 / invertedFile.getMaxFij(term) * 1.0;
//			} else if (option == Document.NORMALIZE_BY_SUM_FREQ) {
//				base = 0;
//				Enumeration<Integer> x = this.termCount.elements();
//				while (x.hasMoreElements()) {
//					base += x.nextElement();
//				}
//				//tf = freq * 1.0 / invertedFile.getSumFij(term) * 1.0;
//			}
			
			tf = freq * 1.0 / base * 1.0;
			
			r = r + Math.pow(idf * tf, 2);

		}
		
		return r;
	}
	
	public static Double getInvertedIndexByDocId(int docId, int option, InvertedFile invertedFile) {
		return Document.getInstance(docId).getInvertedIndex(option, invertedFile);
	}
	
	public static ArrayList<Document> getAllDocuments() {
		ArrayList<Document> al = new ArrayList<Document>();
		Enumeration<Document> e = Document.documents.elements();
		while (e.hasMoreElements()) {
			al.add(e.nextElement());
		}
		return al;
	}
	
	public static int getTotalNumOfTerms(int docId) {
		return Document.getInstance(docId).getTotalNumOfTerms();
	}
	
	public int getTotalNumOfTerms() {
		int termNum = 0;
		Enumeration<Integer> x = this.termCount.elements();
		while (x.hasMoreElements()) {
			termNum += x.nextElement();
		}
		return termNum;
	}
	
	public static int getMaxFreqAmongTerms(int docId) {
		return Document.getInstance(docId).getMaxFreqAmongTerms();
	}
	
	public int getMaxFreqAmongTerms() {
		int MaxTermNum = 0;
		Enumeration<Integer> x = this.termCount.elements();
		while (x.hasMoreElements()) {
			MaxTermNum = Math.max(MaxTermNum, x.nextElement());
		}
		return MaxTermNum;
	}
	
//	public String getText() {
//		return this.getText();
//	}

}