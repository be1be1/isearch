package isearch.searchengine;

import isearch.preprocessingmodule.Document;
import isearch.preprocessingmodule.InvertedFile;
import isearch.preprocessingmodule.PostingNode;
import isearch.preprocessingmodule.TermNode;
import isearch.structure.Query;

import java.util.*;

/**
 * The BM25 weighting scheme
 * Created by beibei on 27/10/2019.
 */
public class BM25Model extends Model {
    /**
     * Free parameter, usually = 2.0
     */
    private double k;

    /**
     * Free parameter, usually = 0.75
     */
    private double b;

    /**
     * Search Mode, e.g. NORMALIZE_BY_FREQ, NORMALIZE_BY_MAX_FREQ, NORMALIZE_BY_SUM_FREQ
     */
    private int searchMode = Document.NORMALIZE_BY_FREQ;

    /**
     * Average Document Length of each search mode
     */
    public Map<Integer, Double> avgDocLength = new HashMap<Integer, Double>();

    /**
     * Constructor
     *
     * @param invertedFile Inverted file
     */
    public BM25Model(InvertedFile invertedFile) {
        this(invertedFile, 2.0, 0.75);
    }

    /**
     * Constructor
     *
     * @param invertedFile Inverted file
     * @param k            Free parameter, usually = 2.0
     * @param b            Free parameter, usually = 0.75
     */
    public BM25Model(InvertedFile invertedFile, double k, double b) {
        super(invertedFile);

        this.k = k;
        this.b = b;
    }

    /**
     * Get all document IDs in the inverted file
     * @return Doc IDs
     */
    private Set<Integer> getAllDocsID(){
        Set<Integer> docsID = new HashSet<Integer>();

//        Hashtable<Integer, LinkedList<TermNode>> InvHashTable = this.invertedFile.returnHashTable();
//        Enumeration<Integer> enumKey = InvHashTable.keys();
//        while(enumKey.hasMoreElements()) {
//            Integer key = enumKey.nextElement();
//            LinkedList<TermNode> termNodeList = InvHashTable.get(key);
//            for (int i=0; i<termNodeList.size(); i++){
//                TermNode termNode = termNodeList.get(i);
//
//                ListIterator<PostingNode> pl = termNode.getPostingIterator();
//                while (pl.hasNext()) {
//                    PostingNode pn = pl.next();
//                    int docId = pn.getDocumentId();
//
//                    docsID.add(docId);
//                }
//            }
//
//        }
        
        Iterator<Document> i_d = Document.getAllDocuments().iterator();
        while(i_d.hasNext()) {
        	docsID.add(i_d.next().getDocId());
        }

        return docsID;
    }

    /**
     * Get average document length
     * @return Average document length
     */
    private double getAvgDocLength(int searchMode) {
        Set<Integer> docsIDs =  getAllDocsID();
        double avgDocLength;
        double docLength = 0.0;
        for (Integer id : docsIDs) {
            docLength += Document.getDocLengthByDocId(id, searchMode, this.invertedFile);
        }

        avgDocLength = docLength / docsIDs.size();

        return avgDocLength;
    }

    @Override
    public HashMap<Integer, Double> search(Query query) {
        this.searchMode = Document.NORMALIZE_BY_FREQ;
        return startSearch(query);
    }

    /**
     * Search with NORMALIZE_BY_MAX_FREQ
     * @param query Query
     * @return Score of the documents
     */
    public HashMap<Integer, Double> search_NormalizeByMax(Query query) {
        this.searchMode = Document.NORMALIZE_BY_MAX_FREQ;
        return startSearch(query);
    }

    /**
     * Search with NORMALIZE_BY_SUM_FREQ
     * @param query Query
     * @return Score of the documents
     */
    public HashMap<Integer, Double> search_NormalizeBySum(Query query) {
        this.searchMode = Document.NORMALIZE_BY_SUM_FREQ;
        return startSearch(query);
    }

    /**
     * Start Search
     * @param query Query
     * @return Score of the documents
     */
    private HashMap<Integer, Double> startSearch(Query query) {
        // calculate average length, if not calculate the average length in these searchMode before
        if(!this.avgDocLength.containsKey(this.searchMode)){
            this.avgDocLength.put(this.searchMode, getAvgDocLength(this.searchMode));
        }

        return sortScore(rank(query.returnKeywordList()));
    }

    /**
     * Sort the result in ascending order by score
     * @see <a href="https://www.mkyong.com/java/how-to-sort-a-map-in-java/">Sorting Ref 1</a>
     * @return Sorted Result
     */
    public HashMap<Integer, Double> sortScore(HashMap<Integer, Double> result){
        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(result.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue()) == 1 ? -1 : 1;
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        HashMap<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Rank for the document with query
     * @param queryTerms Query Keywords
     * @return Overall Score
     */
    public HashMap<Integer, Double> rank(Hashtable<String, Integer> queryTerms) {

        // HaspMap <DocumentID, score>
        HashMap<Integer, Double> result = new HashMap<Integer, Double>();

        // For each query terms
        for (String term : queryTerms.keySet()) {

            TermNode tn = this.invertedFile.searchHashTable(term);

            if (tn == null) {
                continue;
            }

            // Find all docs that contain the term
            ListIterator<PostingNode> pns = tn.getPostingIterator();

            // Searching and calc score by docs
            while (pns.hasNext()) {
                PostingNode pn = pns.next();
                int docID = pn.getDocumentId();

                // example, df = 4, 4 docs have this keyboard
                double df = this.invertedFile.getDf(term);
                // example, tf = 6, total 6 keyboard in all docs
                double tf = this.invertedFile.getSpecificFij(term, docID);
                double totalNumOfDoc = this.invertedFile.getN();
                double docLength = Document.getDocLengthByDocId(docID, this.searchMode , this.invertedFile);

                double score = score(df, tf, totalNumOfDoc, docLength, this.avgDocLength.get(this.searchMode));

                // Put score to result
                if (result.containsKey(docID)) { // exists score + new score * number of this term in query
                    result.put(docID, result.get(docID) + score * queryTerms.get(term));
                } else { // new score * number of this term in query
                    result.put(docID, score * queryTerms.get(term));
                }

            }

        }

        return result;
    }

    /**
     * Get Number of words in a document
     * The original code(I have commented them) misunderstand the meaning of |D|
     * |D| is the length of the document D (either euclidean length or word count length)
     * @see <a href="ttp://nlp.stanford.edu/IR-book/html/htmledition/okapi-bm25-a-non-binary-model-1.html">BM25 Ref 1</a>
     */
//    private int getWordLengthofDoc(int docID) {
//        int length = 0;
//        Hashtable<Integer, LinkedList<TermNode>> InvHashTable = this.invertedFile.returnHashTable();
//
//        for (int key : InvHashTable.keySet()) {
//            LinkedList<TermNode> t = InvHashTable.get(key);
//
//            // For each term in the inverted file
//            for (int i = 0; i < t.size(); i++) {
//                TermNode tn = t.get(i);
//                // Get Documents
//                ListIterator<PostingNode> pns = tn.getPostingIterator();
//
//                // For each document in pns var
//                while (pns.hasNext()) {
//                    PostingNode pn = pns.next();
//                    // Add keywords count to length var
//                    length += pn.getFreq();
//                }
//            }
//        }
//
//        return length;
//    }

    /**
     * Calculate score between a term and a document based on the inverted file.
     *
     * @param df number of documents containing query's keyword
     * @param tf Term Frequency
     * @param totalNumOfDoc total number of documents in the collection
     * @param docLength Number of words in the document
     * @return score
     * @see <a href="http://opensourceconnections.com/blog/2015/10/16/bm25-the-next-generation-of-lucene-relevation/">BM25 Ref 2</a>
     * @see <a href="https://turi.com/products/create/docs/generated/graphlab.text_analytics.bm25.html">BM25 Ref 2</a>
     */
    private double score(double df, double tf, double totalNumOfDoc, double docLength, double avgDocLength) {
        // Inverse Document Frequency
        // idf = Math.log(dl - df + 0.5 / df + 0.5)

        // Average Length
        // (dl/wl)

        return Math.log((totalNumOfDoc - df + 0.5) / (df + 0.5)) * ((k + 1) * tf) / (k * (1.0 - b + b * (docLength / avgDocLength)) + tf);
    }

}