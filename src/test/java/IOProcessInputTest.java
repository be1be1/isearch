import org.junit.Test;
import isearch.ui.TrecEval;

public class IOProcessInputTest {

//	@Test
//	public void readDocumentsTest() throws IOException, InterruptedException {
//		try {
//			//IOProcess.readDocuments(String.format(Share.getProjectProperty("original.data.path")), "file.txt"); // change this
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		InvertedFile invertedFile = IOProcess.readDocuments(String.format(Share.getProjectProperty("original.data.path")));
//		//System.out.println("line 18 : " + i_f.getN());
//		
//		long now = System.currentTimeMillis();
//		System.out.println("object.getN() : " + invertedFile.getN() + " ; taken : " + (System.currentTimeMillis() - now) );
//		//assertTrue(InvertedFile.getTotalDocNum() == 11);
//		
//		now = System.currentTimeMillis();
//		String term1 = "doC";
//		TermNode tn1 = invertedFile.searchHashTable(term1);
//		System.out.println("tn1 : " + tn1.getDf() + " ; " + tn1.getMaxFij() + " ; " + tn1.getSumFij() + " ; " + (System.currentTimeMillis() - now) );
//		System.out.println("doc1 : " + Document.getDocLengthByDocId(1, Document.NORMALIZE_BY_FREQ, invertedFile) + "\t ; " + Document.getDocLengthByDocId(1, Document.NORMALIZE_BY_MAX_FREQ, invertedFile) + "\t ; " + Document.getDocLengthByDocId(1, Document.NORMALIZE_BY_SUM_FREQ, invertedFile) );
//		System.out.println("doc1 : " + Document.getInvertedIndexByDocId(1, Document.NORMALIZE_BY_FREQ, invertedFile) + "\t ; " + Document.getInvertedIndexByDocId(1, Document.NORMALIZE_BY_MAX_FREQ, invertedFile) + "\t ; " + Document.getInvertedIndexByDocId(1, Document.NORMALIZE_BY_SUM_FREQ, invertedFile) );
//
//		//assertTrue(i_f.getMaxFij(term1) == 2);
//	}	
	

//	@Test
//	public void readQueryTest() {
//		try {
//			IOProcess.readQuery("O:\\ecplise_workspace\\git\\COMP4133\\resources\\queryT"); // change this
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void trecEvalTest() {
		try {
			TrecEval te = new TrecEval("C:\\Users\\Alex\\Documents\\test\\trec_sample\\trec_eval_cmd.exe", "C:\\Users\\Alex\\Documents\\test\\out8", "C:\\Users\\Alex\\Documents\\test\\trec_sample\\judgerobust"); // change this
		} catch(Exception e){
			e.printStackTrace();
		}
	}
//
//	@Test
//	public void readFileIdentifierMapping() {
//		try {
//			HashMap<Integer, String> mapping = IOProcess.readFileIdentifierMapping("/Users/renee/Dropbox/Study/16-17_sem1/COMP4133/COMP4133/file.txt"); // change this
//			Iterator it = mapping.entrySet().iterator();
//			while (it.hasNext()) {
//				Map.Entry pair = (Map.Entry)it.next();
//				System.out.println(pair.getKey() + " " + pair.getValue());
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void searchResultToFile(){
//		try {
//			IOProcess.readFileIdentifierMapping("/Users/renee/Dropbox/Study/16-17_sem1/COMP4133/COMP4133/file.txt");
//			HashMap<Integer, Double> searchResult = new HashMap<Integer, Double>();
//			searchResult.put(4, 0.1);
//			searchResult.put(5, 0.3);
//			searchResult.put(2, 0.112);
//			searchResult.put(3, 0.33);
//			IOProcess.searchResultToFile(01, "/Users/renee/output.txt", searchResult, "ABC");
//
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}

}
