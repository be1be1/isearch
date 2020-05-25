package isearch.structure;

import java.util.*;

/**
 * Created by Beibei on 25/10/2016.
 */
import java.util.ArrayList;

public class Query {
    private int queryNumber;
    private Hashtable<String, Integer> keywordList;

    public Query(int queryNumber){
        this.queryNumber = queryNumber;
        keywordList = new Hashtable<String, Integer>();
    }

    public static final int QUERY_SUCESS = 0;
    public static final int QUERY_ERROR = 1;

    public int addKeyword(String keyword){
        if (keywordList.containsKey(keyword)) {
            keywordList.put(keyword, keywordList.get(keyword)+1);
        }else{
            keywordList.put(keyword, 1);
        }

        return QUERY_SUCESS;
    }
    
    public int getId() {
    	return this.queryNumber;
    }

    /*** Not done ****/
    
    // return the keyword list
    public Hashtable<String, Integer> returnKeywordList() {
    	return keywordList;
    }
}
