package isearch.searchengine;

import java.util.HashMap;
import java.util.Map.Entry;

import isearch.preprocessingmodule.Document;
import isearch.preprocessingmodule.InvertedFile;
import isearch.structure.Query;

public class VSM_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Query q = new Query(601);
		q.addKeyword("comp");
		q.addKeyword("comp");
		q.addKeyword("comp");
		q.addKeyword("test");
		q.addKeyword("test");
		q.addKeyword("doc");
		
		InvertedFile invertedFile = new InvertedFile();
        invertedFile.insert("test", 1, 1);
        invertedFile.insert("test", 2, 1);
        invertedFile.insert("test", 4, 1);
        invertedFile.insert("doc", 2, 1);
        invertedFile.insert("doc", 3, 1);	invertedFile.insert("doc", 3, 1);
        invertedFile.insert("comp", 1, 1);
        invertedFile.insert("comp", 2, 1);	invertedFile.insert("comp", 2, 1);	invertedFile.insert("comp", 2, 1);
        invertedFile.insert("comp", 4, 1);	invertedFile.insert("comp", 4, 1);	invertedFile.insert("comp", 4, 1);
        invertedFile.insert("comp", 5, 1);
        invertedFile.insert("comp", 6, 1);
        
        int pos = 1;
        Document d1 = Document.getInstance(1);
        d1.addTerm("test", pos);	d1.addTerm("comp", pos);
        Document d2 = Document.getInstance(2);
        d2.addTerm("test", pos);	d2.addTerm("doc", pos);	
        d2.addTerm("comp", pos);	d2.addTerm("comp", pos);	d2.addTerm("comp", pos);
        Document d3 = Document.getInstance(3);
        d3.addTerm("doc", pos);		d3.addTerm("doc", pos);
        Document d4 = Document.getInstance(4);
        d4.addTerm("test", pos);
        d4.addTerm("comp", pos);	d4.addTerm("comp", pos);	d4.addTerm("comp", pos);
        Document d5 = Document.getInstance(5);
        d5.addTerm("comp", pos);
        Document d6 = Document.getInstance(6);
        d6.addTerm("comp", pos);
        
        
        VectorSpaceModel vsm = new VectorSpaceModel(invertedFile);
        
        HashMap<Integer, Double> results = vsm.search(q);        
        System.out.println("-----No normalization-----");
        for (Entry<Integer, Double> entry : results.entrySet()) {
        	Integer docId = entry.getKey();
		    double score = entry.getValue();
		    System.out.println("docID "+docId+" : "+score);
		}
        
        results = vsm.search_NormalizeByMax(q);        
        System.out.println("-----Normalize By Max-----");        
        for (Entry<Integer, Double> entry : results.entrySet()) {
        	Integer docId = entry.getKey();
		    double score = entry.getValue();
		    System.out.println("docID "+docId+" : "+score);
		}
        
        results = vsm.search_NormalizeBySum(q);
        System.out.println("-----Normalize By Sum-----");        
        for (Entry<Integer, Double> entry : results.entrySet()) {
        	Integer docId = entry.getKey();
		    double score = entry.getValue();
		    System.out.println("docID "+docId+" : "+score);
		}

            
              
	}

}
