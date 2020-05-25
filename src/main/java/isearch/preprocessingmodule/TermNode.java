package isearch.preprocessingmodule;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Beibei on 25/10/2016.
 */
public class TermNode{
	
    private String term;
    private LinkedList<PostingNode> postList;
    
    public TermNode(String term){
        this.term = term;
        postList = new LinkedList<PostingNode>();
    }
	
	public int getDf() {
		return this.postList.size();
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public PostingNode getFirstPostingNode(){
		return postList.getFirst();
	}
	
	public PostingNode getLastPostingNode(){
		return postList.getLast();
	}
	
	public void addPostingNode(PostingNode pn){
		postList.addFirst(pn);
	}
	
	public void clearPostingList(){
		postList.clear();
	}
	
	public void removePostingNode(int docId) {
		this.removePostingNode(this.seachPostingNode(docId));
	}
	
	public boolean removePostingNode(PostingNode pn) {
		if (pn == null) {
			return false;
		}
		return postList.remove(pn);
	}
	
	public PostingNode seachPostingNode(int docId){
		ListIterator<PostingNode> pList = this.getPostingIterator();
		if(pList != null){
	    	while(pList.hasNext()){
	    		PostingNode pn = pList.next();
	    		if (pn.getDocumentId() == docId) {
	    			return pn;
	    		}
			}
		}
    	return null;
	}
	
	public ListIterator<PostingNode> getPostingIterator(){
		return postList.listIterator();
	}
	
	//use for finding total fij for a term; 0 = no doc
	public int getSumFij() {	
		int freqCounter = 0;
		
		ListIterator<PostingNode> pList = this.getPostingIterator();
		if(pList != null){
	    	while(pList.hasNext()){
	    		PostingNode pn = pList.next();
	    		freqCounter += pn.getFreq();
			}
		}
		
		return freqCounter;
	}
	
	//use for finding max fij for a term; 0 = no doc
	public int getMaxFij() {
		int freqCounter = 0;
		
		ListIterator<PostingNode> pList = this.getPostingIterator();
		if(pList != null){
	    	while(pList.hasNext()){
	    		PostingNode pn = pList.next();
	    		freqCounter = Math.max(freqCounter, pn.getFreq());
			}
		}
		
		return freqCounter;
	}

}
