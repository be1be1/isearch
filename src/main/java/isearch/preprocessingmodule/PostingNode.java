package isearch.preprocessingmodule;

import java.util.ArrayList;

/**
 * Created by Beibei on 25/10/2016.
 */
public class PostingNode{
    private int documentId;
    private ArrayList<Integer> pos;

    public PostingNode(int documentId){
        this.documentId = documentId;
        this.pos = new ArrayList<Integer>();
    }
    
    public PostingNode(int documentId, Integer term_pos) {
    	this(documentId);
    	this.addTermPos(term_pos);
    }
    
    public int getFreq(){
        return this.pos.size();
    }

    public int getDocumentId(){
        return documentId;
    }
    
    public int addTermPos(Integer term_pos) {
    	this.pos.add(0, term_pos);
    	return 1;
    }
    
    public ArrayList<Integer> getTermPos() {
    	return this.pos;
    }
    
}
