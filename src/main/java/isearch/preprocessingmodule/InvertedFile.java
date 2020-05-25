package isearch.preprocessingmodule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;

import isearch.util.Share;

/**
 * Note by Beibei:
 * Please read lecture 3 page 34 for hashtable + linklist implementation
 */

public class InvertedFile {
	
    private ArrayList<Integer> docExists;
    private Hashtable<Integer, LinkedList<TermNode>> hashTable;
    private long numData = 0;
    
	public static final String TAG = InvertedFile.class.getSimpleName();
	
    public InvertedFile() {
        this.hashTable = new Hashtable<Integer, LinkedList<TermNode>>();
        this.docExists = new ArrayList<Integer>();
    }
    
//    public void removeInvertedFile(String term){
//    	int hashKey = hashFunction(term);
//    	int numOfDocInInvertedFile = getTotalDocFreqOfLinkedList(hashKey);
//    	hashTable.remove(hashKey);
//    	//totalNumOfDoc -= numOfDocInInvertedFile;
//    }
    
//    public void resetPostingList(String term){
//    	TermNode targetNode = searchHashTable(term);
//    	int prevNumOfDoc = targetNode.getDf();
//    	targetNode.clearPostingList();
//    	//totalNumOfDoc -= prevNumOfDoc;
//    }
    
	//use for df for a term; 0 = no doc
	public int getDf(String term) {
		TermNode tn = searchHashTable(term);
		
		if(tn != null)
			return tn.getDf();
		
		return 0;
	}
	
	//use for finding max fij for a term; 0 = no doc
	public int getMaxFij(String term) {
		TermNode tn = this.searchHashTable(term);
		if (tn != null) {
			return tn.getMaxFij();
		}
		return 0;
	}
	
	//use for finding total total number of doc; 0 = no doc
	public int getN() {
		//return totalNumOfDoc;
		return this.docExists.size();
	}
	
	//use for finding specific fij; 0 = no doc
	public int getSpecificFij(String term, int docId) {
		TermNode tn = this.searchHashTable(term);
		
		if(tn != null){
			PostingNode pn = tn.seachPostingNode(docId);
			if(pn != null)
				return pn.getFreq();
		}
		
		return 0;
	}
	
	//use for finding total fij for a term; 0 = no doc
	public int getSumFij(String term) {
		TermNode tn = this.searchHashTable(term);
		if (tn != null) {
			return tn.getSumFij();
		}
		return 0;
	}
	
	@Deprecated
    private int getTotalDocFreqOfLinkedList(int hashKey){
    	LinkedList<TermNode> list = hashTable.get(hashKey);
    	
    	int counter = 0;
    	for(int i = 0; i < list.size(); i++){
    		TermNode tn = list.get(i);
    		counter += tn.getDf();
    	}

    	return counter;
    }
    
    public int hashFunction(String term){
    	//Reference to HashMap
    	//First hash: 32 bits hash: s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
    	int firstHash = term.hashCode();
    	
    	//Second hash:
    	/**
    	 * Applies a supplemental hash function to a given hashCode, which
    	 * defends against poor quality hash functions.  This is critical
    	 * because HashMap uses power-of-two length hash tables, that
    	 * otherwise encounter collisions for hashCodes that do not differ
    	 * in lower bits. Note: Null keys always map to hash 0, thus index 0.
    	 */
    	firstHash ^= (firstHash >>> 20) ^ (firstHash >>> 12);
    	int secondHash = firstHash ^ (firstHash >>> 7) ^ (firstHash >>> 4);
    	
    	return secondHash;
    }
	
    public void insert(String term, Integer docId, Integer term_pos){
    	TermNode targetNode = this.searchHashTable(term);
    	PostingNode curPList = null;
    	LinkedList<TermNode> tList = null;
    	
		//search node
	   	int hashKey = this.hashFunction(term);
		//check if it is a new hashKey
		if(hashTable.containsKey(hashKey)){
		   	tList = hashTable.get(hashKey);
		   	//targetNode = this.searchHashTable(term);
		} else {
			tList = new LinkedList<TermNode>();
			hashTable.put(hashKey, tList);
		}
			
		//term node is not existed
		if (targetNode == null) {
			targetNode = new TermNode(term);
	    	tList.addFirst(targetNode);
		} else {
			curPList = targetNode.seachPostingNode(docId);
		}
		
//		//Need to check the existence of the term in the LinkedList with the same docId
//		//if it exists, only need to add tf but not df
		if (curPList == null) {
			curPList = new PostingNode(docId);
			targetNode.addPostingNode(curPList);
		}
		
		// add the term position to the list
		curPList.addTermPos(term_pos);
    	
    	if (!this.docExists.contains(docId)) {
    		this.docExists.add(0, docId);
    	}
    	
    	this.numData++;
    }
    
    /*
	 * output the invertedFile in txt format
	 * Don't call this function unless you are debugging
	 * You can view it with json reader http://jsonviewer.stack.hu/
	 */
    public void outputDebugInvertedFileJSON() throws IOException{	
        
    	Map<String, Object> map = new HashMap<String, Object>();
        Set<Integer> keys = hashTable.keySet();
        for(Integer key: keys){     
			Map<String, Object> termValue = new HashMap<String, Object>();
            Iterator<TermNode> termList = hashTable.get(key).iterator();
			TermNode tn = termList.next();
            while(tn != null){
            	termValue.put("df", tn.getDf());
            	termValue.put("term", tn.getTerm());
            	termValue.put("nextTerm", tn.getTerm());
            	
        		Map<String, Object> postsValue = new HashMap<String, Object>();
            	Iterator<PostingNode> postList = tn.getPostingIterator();
        		PostingNode pn = postList.next();
            	while(pn != null){
            		Map<String, Integer> postValue = new HashMap<String, Integer>();
            		int docId = pn.getDocumentId();
            		postValue.put("docId", docId);
            		postValue.put("freq", pn.getFreq());
            		if(postList.hasNext()){
            			pn = postList.next();
                		postValue.put("nextPost", pn.getDocumentId());
            		}
            		else{
            			pn = null;
            		}
            		postsValue.put("Posting:"+docId, postValue);     		
            	}    	
            	termValue.put("Postings", postsValue);
            	
            	if(termList.hasNext()){
                   	tn = termList.next();
                   	termValue.put("nextTerm", tn.getTerm());	
        		}
        		else{
        			tn = null;
        		}
            }
            map.put("Hash:"+key, termValue);
        }
    	
    	JSONObject obj = new JSONObject(map);   
    	String timestamp = String.valueOf(new Date().getTime());
		FileWriter file = new FileWriter(String.format(Share.getProjectProperty("inverted.file.output.path"), timestamp));
		file.write(obj.toJSONString());
		file.close();
    }
    
    public void outputInvertedFile() throws IOException{	
        //TODO: implementation
    }
    
 public void removeDoc(String term, int docId){
    	TermNode targetNode = searchHashTable(term);
    	targetNode.removePostingNode(docId);
    }
    
    public void removePostingNode(String term, int docId, int pos) {  	
    	TermNode tn = searchHashTable(term);
    	Iterator<PostingNode> it_pn = tn.getPostingIterator();
		while (it_pn.hasNext()) {
			PostingNode pn = it_pn.next();
			if (pn.getDocumentId() == docId) {
				ArrayList<Integer> al_delpos = new ArrayList<Integer>();
				for (int i = 0; i < pn.getTermPos().size(); i++) {
					if (pn.getTermPos().get(i) == pos) {
						al_delpos.add(pos);
					}
				}
				
				for (int i = 0; i < al_delpos.size(); i++) {
					pn.getTermPos().remove(al_delpos.get(i));
				}
			}
			
			//Check the term appearance in the same doc
			//If the term is not found at the doc except the deleted one, TermNode DF - 1
			if (pn.getTermPos().isEmpty()) {
				tn.removePostingNode(pn);
				//If TermNode DF == 0, remove termNode
				if (tn.getDf() == 0) {
					this.removeTermNode(tn);
				}
			}
		}
    }
    
	public void removeTermNode(String term){
    	TermNode targetNode = searchHashTable(term);
    	this.removeTermNode(targetNode);
    }
	
	public void removeTermNode(TermNode tn) {
    	if (tn == null) {
    		return;
    	}
    	
    	if (tn.getTerm() == null) {
    		return;
    	}
    	
    	int hashKey = hashFunction(tn.getTerm());
    	LinkedList<TermNode> list = this.hashTable.get(hashKey);
    	list.remove(tn);
    	
    	Hashtable<Integer, Boolean> temp = new Hashtable<Integer, Boolean>();
    	Iterator<PostingNode> target_pn = tn.getPostingIterator();
		while (target_pn.hasNext()) {
			PostingNode pn = target_pn.next();
			temp.put(pn.getDocumentId(), false);
		}
		
		//Check the remaining TermNode having the same Doc ID or not
		//If the Doc ID is no longer existed, totalNumOfDoc - 1
    	Enumeration<Integer> e = this.hashTable.keys();
    	while (e.hasMoreElements()) {
    		Integer i = e.nextElement();
    		Iterator<TermNode> it = this.hashTable.get(i).iterator();
    		while (it.hasNext()) {
    			TermNode tn1 = it.next();
   				Iterator<PostingNode> it_pn = tn1.getPostingIterator();
	    		while (it_pn.hasNext()) {
	    			PostingNode pn = it_pn.next();
	    			if (temp.get(pn.getDocumentId()) != null) {
	    				if (temp.get(pn.getDocumentId()) == true) {
	    					continue;
	    				}
	    				temp.put(pn.getDocumentId(), true);
	    			}
	    		}
    		}
    	}
    	
    	Enumeration<Integer> e_temp = temp.keys();
    	while (e_temp.hasMoreElements()) {
    		Integer i = e_temp.nextElement();
    		if (!temp.get(i)) {
    			this.docExists.remove(i);
    		}
    	}
    }
	
	public void resetHashTable(){
    	this.hashTable.clear();
    	this.docExists.clear();
    }
	
	// return hashtable to calculate document length in VSM
    public Hashtable<Integer, LinkedList<TermNode>> returnHashTable(){ 	
    	return this.hashTable;
    }
	
	//search target term node from InvertedFile
    public TermNode searchHashTable(String targetedTerm) {
    	int hashKey = hashFunction(targetedTerm);    	
    	LinkedList<TermNode> tList = hashTable.get(hashKey);
    	
    	if (tList == null) {
    		return null;
    	}

    	if(tList != null){
			for(int i = 0; i < tList.size(); i++){
				if(targetedTerm.equals(tList.get(i).getTerm())){
					return tList.get(i);
				}
			}
    	}
    	return null;
    }
	
	public InvertedFile merge(InvertedFile instance) {
		Enumeration<Integer> keys = instance.hashTable.keys();
		while (keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			
			LinkedList<TermNode> tn_ll = instance.hashTable.get(key);
			if (tn_ll != null) {
				LinkedList<TermNode> target_tn_ll = this.hashTable.get(key);
				if (target_tn_ll == null) {
					this.hashTable.put(key, tn_ll);
				} else {
					Iterator<TermNode> i_i_tn = tn_ll.iterator();
					while (i_i_tn.hasNext()) {
						TermNode tn = i_i_tn.next();
						
						TermNode curTn = this.searchHashTable(tn.getTerm());
						if (curTn == null) {
							target_tn_ll.add(tn);
						} else {
							ListIterator<PostingNode> pns = tn.getPostingIterator();
							while (pns.hasNext()) {
								PostingNode pn = pns.next();
								
								PostingNode curPn = curTn.seachPostingNode(pn.getDocumentId());
								if (curPn == null) {
									curTn.addPostingNode(pn);
								} else {
									ArrayList<Integer> al_term_pos = pn.getTermPos();
									
									for (int i = 0; i < al_term_pos.size(); i++) {
										if (curPn.getTermPos().contains(al_term_pos.get(i))) {
										} else {
											curPn.addTermPos(al_term_pos.get(i));
										}
									}
									
								}
							}
						}
					}
				}
				
			}
		}

		this.docExists.addAll(instance.docExists);

		HashSet<Integer> s = new HashSet<Integer>();
		s.addAll(this.docExists);
		this.docExists.clear();
		this.docExists.addAll(s);
		
		this.numData += instance.numData;
		
		return this;
	}
	
	public long getNumOfData() {
		return this.numData;
	}
	
}
