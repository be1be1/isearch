package isearch.searchengine;

import java.util.HashMap;
import java.util.Map.Entry;

import isearch.preprocessingmodule.InvertedFile;
import isearch.structure.Query;

public class BooleanTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Query q = new Query(601);
//		q.addKeyword("comp");
//		q.addKeyword("test");
		q.addKeyword("doc");
		q.addKeyword("info");
		
		InvertedFile invertedFile = new InvertedFile();
        invertedFile.insert("test", 1, 1);
        invertedFile.insert("test", 2, 1);
        invertedFile.insert("test", 4, 1);
        invertedFile.insert("doc", 2, 1);
        invertedFile.insert("doc", 3, 1);	//invertedFile.insert("doc", 3, 1);
        invertedFile.insert("comp", 1, 1);
        invertedFile.insert("comp", 2, 1);	//invertedFile.insert("comp", 2, 1);	invertedFile.insert("comp", 2, 1);
        invertedFile.insert("comp", 4, 1);	//invertedFile.insert("comp", 4, 1);	invertedFile.insert("comp", 4, 1);
        invertedFile.insert("comp", 5, 1);
        invertedFile.insert("comp", 6, 1);
        invertedFile.insert("info", 3, 1);
        invertedFile.insert("info", 5, 1);
        invertedFile.insert("info", 6, 1);
        
        
        BooleanModel bm = new BooleanModel(invertedFile);
        
        HashMap<Integer, Double> results = bm.search(q);     
        for (Entry<Integer, Double> entry : results.entrySet()) {
        	Integer docId = entry.getKey();
		    System.out.println("docID "+docId);
		}
            
              
	}

}
