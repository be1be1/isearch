package isearch.searchengine;



/**
 * Created by Beibei on 25/10/2016.
 */
import java.util.HashMap;

import isearch.preprocessingmodule.InvertedFile;
import isearch.structure.Query;

abstract public class Model {

    static final int SEARCH_SUCCESS = 0;
    static final int SEARCH_ERROR = 1;
    protected final InvertedFile invertedFile;

    public Model(InvertedFile invertedFile){
        this.invertedFile = invertedFile;
    }

    // perform searching and return matched documents id and ranking score in ascending ranking
    abstract public HashMap<Integer, Double> search(Query query);
    
    abstract public HashMap<Integer, Double> search_NormalizeByMax(Query query);
    
    abstract public HashMap<Integer, Double> search_NormalizeBySum(Query query);
}

