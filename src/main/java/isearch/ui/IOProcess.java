package isearch.ui;

/**
 * Created by Beibei on 25/10/2016.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import isearch.preprocessingmodule.Document;
import isearch.preprocessingmodule.InvertedFile;
import isearch.structure.Query;
import isearch.util.InvertedFileThread;
import isearch.util.MergeInvertedFileThread;

public class IOProcess {
    private static Map<String, Integer> stopWordList = new HashMap<String, Integer>();
    public static int currentRecordNum = 0;
    static{
        int i = 0;
        for (String key: new String[]{"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount", "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as", "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"}) {
            stopWordList.put(key, i);
            i++;
        }
    };
    private static HashMap<Integer, String> documentNameMapping = new HashMap<Integer, String>();

    public static HashMap<Integer, String> readFileIdentifierMapping(String path) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();


        // read mapping information one by one
        // example entirety: "0 156 @ FBIS3-1001 f:\assign\data\FBIS3-1001"
        while (line != null){

            //read and tokenize line by line and get detail space by space
            StringTokenizer token = new StringTokenizer(line, " ");

            int documentId = Integer.parseInt(token.nextToken());
            token.nextToken(); // skip doc len
            token.nextToken(); // skip @

            String documentName = token.nextToken();

            // add the mapping entirety to HashMap
            documentNameMapping.put(documentId, documentName);

            line = br.readLine();
        }


        return documentNameMapping;
    }

    private static InvertedFile readDocuments(String documentPath, String mappingFilePath) throws IOException, InterruptedException {
    	InvertedFile invertedFile = new InvertedFile();
    	
        BufferedReader br = new BufferedReader(new FileReader(documentPath));
        String line = br.readLine();

        currentRecordNum = 0;

        //insert data into InvertedFile one by one
        while (line != null) {
            //read and tokenize line by line and get detail space by space

        	try {
				StringTokenizer token = new StringTokenizer(line, " ", false);
				String stemmedKeyword = token.nextToken();
				int documentId = Integer.parseInt(token.nextToken());
				int logicalPosition = Integer.parseInt(token.nextToken());

				Document d = Document.getInstance(documentId);
				d.addTerm(stemmedKeyword, logicalPosition);
				invertedFile.insert(stemmedKeyword, documentId, logicalPosition);
				currentRecordNum++;
        	} catch (Exception e) {

        	}

            line = br.readLine();
        }

        br.close();
    	
        documentNameMapping = readFileIdentifierMapping(mappingFilePath);
        
        return invertedFile;
    }

    public static InvertedFile readDocuments(String documentPath, String mappingFilePath, boolean multi_threading) throws IOException, InterruptedException{
    	Document.clearInstance();
    	if (multi_threading == false) {
    		return IOProcess.readDocuments(documentPath, mappingFilePath);
    	}
    	InvertedFile invertedFile = new InvertedFile();
    	
        BufferedReader br = new BufferedReader(new FileReader(documentPath));
        String line = br.readLine();
        
        InvertedFileThread[] at = new InvertedFileThread[Runtime.getRuntime().availableProcessors()];
        MergeInvertedFileThread mergeThread = new MergeInvertedFileThread(invertedFile);
        mergeThread.start();
        
        for (int i = 0; i < at.length; i++) {
        	at[i] = new InvertedFileThread();
        }

        currentRecordNum = 0;
        Calendar c = Calendar.getInstance();

        //insert data into InvertedFile one by one
        while (line != null) {
            //read and tokenize line by line and get detail space by space
        	try {
				StringTokenizer token = new StringTokenizer(line, " ", false);
				String stemmedKeyword = token.nextToken();
				int documentId = Integer.parseInt(token.nextToken());
				int logicalPosition = Integer.parseInt(token.nextToken());

				Document d = Document.getInstance(documentId);
				d.addTerm(stemmedKeyword, logicalPosition);
				int n = stemmedKeyword.charAt(0) % at.length;
				at[n].addRecord(stemmedKeyword, documentId, logicalPosition);
				currentRecordNum++;
        	} catch (Exception e) {
        		e.printStackTrace();
        	}

            line = br.readLine();

            if (Calendar.getInstance().getTimeInMillis() - c.getTimeInMillis() >= 3000) {
                for (int i = 0; i < at.length; i++) {
        			if (at[i] != null) {
        				at[i].start();
        			}
                }

                for (int i = 0; i < at.length; i++) {
                	if (at[i] != null) {
	                	at[i].join();
	                	mergeThread.addMergeInstance(at[i].getInvertedFile());
	                	at[i].shutdown();
	                	at[i] = new InvertedFileThread();
                	}
                }
//                System.out.println("Thead join : x = " + x + " ; time = " + ((Calendar.getInstance().getTimeInMillis() - c.getTimeInMillis()) / 1000.0) + " ; core = " + Runtime.getRuntime().availableProcessors());
                c = Calendar.getInstance();
            }

        }

        br.close();

        for (int i = 0; i < at.length; i++) {
			if (at[i] != null) {
				at[i].start();
			}
        }

        InvertedFile temp = new InvertedFile();
    	for (int i = 0; i < at.length; i++) {
			if (at[i] != null) {
	    		at[i].join();
	    		temp.merge(at[i].getInvertedFile());
	    		at[i].shutdown();
	    		at[i] = null;
			}
    	}
    	mergeThread.addMergeInstance(temp);
    	mergeThread.setRun(false);
        documentNameMapping = readFileIdentifierMapping(mappingFilePath);
    	mergeThread.join();
    	mergeThread.shutdown();
    	mergeThread = null;
    	
        return invertedFile;
    }

    public static ArrayList<Query> readQuery(String path) throws IOException{
        ArrayList<Query> queryList = new ArrayList<Query>();

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();

        while (line != null){
            // read and tokenize line by line and get detail space by space
            StringTokenizer token = new StringTokenizer(line, " ");
            int queryNumber = Integer.parseInt(token.nextToken());

            Query query = new Query(queryNumber);
            queryList.add(query);

            while (token.hasMoreTokens()){
                //From Stemmer: the word stemmed is expected to be in lower case
            	//stemming
                String keyword = token.nextToken().toLowerCase();

                // remove non alphanumeric char
                keyword.replaceAll("[^A-Za-z0-9]", "");

                 //the keyword is a stopword
                if (stopWordList.containsKey(keyword)){
                    continue;
                }
                Stemmer stemmer = new Stemmer();
                keyword = keyword.replaceAll("[^A-Za-z0-9]", "");
                char[] keywordArray = keyword.toCharArray();
                stemmer.add(keywordArray, keywordArray.length);
                stemmer.stem();

                String result = stemmer.toString();
                result = result.substring(0, result.length() - 1) + result.substring(result.length() - 1, result.length()).toUpperCase();
                //System.out.println(result);
                query.addKeyword(result);
            }
            line = br.readLine();
        }
        return queryList;
    }

    // output search result file which would be used as input for trec_eval_cmd.exe
    public static int searchResultToFile(int queryNumber, String outputFile,
                                         HashMap<Integer, Double> searchResult, String runIdentifier) throws IOException{
        //PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true));

        // sort the search result
        ValueComparator compareFunction = new ValueComparator(searchResult);
        TreeMap<Integer, Double> sortedMap = new TreeMap<Integer, Double>(compareFunction);
        sortedMap.putAll(searchResult);

        int i = 0;
        DecimalFormat df = new DecimalFormat("0.00000");
        Iterator<Entry<Integer, Double>> it = sortedMap.entrySet().iterator();
        while (it.hasNext()) {
        	if (i >= 1000) {
        		break;
        	}
            Entry<Integer, Double> pair = it.next();
            bw.append(queryNumber + " 0 " + documentNameMapping.get(pair.getKey()) + " " + i + " " + df.format(pair.getValue()) + " " + runIdentifier + "\n");
            i++;
        }

        bw.close();
        return 1;
    }
    
}

class ValueComparator implements Comparator<Integer> {
    Map<Integer, Double> base;

    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}