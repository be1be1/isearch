import static org.junit.Assert.*;

import java.io.IOException;

import isearch.preprocessingmodule.InvertedFile;

import org.junit.Test;



public class InvertedFileBuildTest {

	@Test
	public void insertSameTerm() throws IOException {
		String term = "Test1";
		InvertedFile invertedFile = new InvertedFile();
        invertedFile.insert(term, 1, 5);
        invertedFile.insert(term, 2, 1);
        invertedFile.insert(term, 2, 5);
        int sumFij = invertedFile.getSumFij(term);
        assertTrue (sumFij == 3);
        
        int maxFij = invertedFile.getMaxFij(term);
        assertTrue (maxFij == 2);
        
        int df = invertedFile.getDf(term);
        assertTrue (df == 2);
	}
	
	@Test
	public void insertDiffTerm() throws IOException {
		String term1 = "Test1";
		String term2 = "Test2";
		String term3 = "Test2";
		InvertedFile invertedFile = new InvertedFile();
        invertedFile.insert(term1, 1, 3);
        invertedFile.insert(term2, 2, 20);
        invertedFile.insert(term3, 2, 20);
        
        int sumFij1 = invertedFile.getSumFij(term1);
        assertTrue (sumFij1 == 1);
        
        int sumFij2 = invertedFile.getSumFij(term2);
        assertTrue (sumFij2 == 2);
        
        int maxFij1 = invertedFile.getMaxFij(term1);
        assertTrue (maxFij1 == 1);
        
        int maxFij2 = invertedFile.getMaxFij(term2);
        assertTrue (maxFij2 == 2);
        
        int df1 = invertedFile.getDf(term1);
        assertTrue (df1 == 1);
        int df2 = invertedFile.getDf(term2);
        assertTrue (df2 == 1);
	}
	
	@Test
	public void insertSameTermDiffDocId() throws IOException {
		String term = "Test2";
		InvertedFile invertedFile = new InvertedFile();
        invertedFile.insert(term, 1, 20);
        invertedFile.insert(term, 2, 20);
        
        int sumFij = invertedFile.getSumFij(term);
        assertTrue (sumFij == 2);
     
        int maxFij = invertedFile.getMaxFij(term);
        assertTrue (maxFij == 1);
        
        int df2 = invertedFile.getDf(term);
        assertTrue (df2 == 2);
	}
	
	@Test
	public void hashValue() throws IOException {
		InvertedFile invertedFile = new InvertedFile();
        int i = invertedFile.hashFunction("Test1");
        int ii = invertedFile.hashFunction("Test1");
        assertTrue (i == ii);
	}
	
	@Test
	public void removePostingNodeTest() throws IOException {
		String term1 = "Test1";
		String term2 = "Test2";
		String term3 = "Test2";
		InvertedFile invertedFile = new InvertedFile();
		invertedFile.insert(term1, 1, 3);
		invertedFile.insert(term2, 2, 10);
		invertedFile.insert(term3, 2, 20);
		
		int n = invertedFile.getN();
		int df = invertedFile.getDf(term2);
		invertedFile.removePostingNode(term2, 2, 10);
		
		assertTrue (2 == invertedFile.getN());
		assertTrue (1 == invertedFile.getDf(term2));
		assertTrue (1 == invertedFile.getDf(term1));
		
	}
	
	@Test
	public void removeTermNodeTest() throws IOException {
		String term1 = "Test1";
		String term2 = "Test2";
		String term3 = "Test2";
		InvertedFile invertedFile = new InvertedFile();
		invertedFile.insert(term1, 1, 3);
		invertedFile.insert(term2, 2, 20);
		invertedFile.insert(term3, 2, 20);
		
		int n = invertedFile.getN();
		int df = invertedFile.getDf(term2);
		invertedFile.removeTermNode(term2);
		
		assertTrue (n-df == invertedFile.getN());
		assertTrue (0 == invertedFile.getDf(term2));
		assertTrue (1 == invertedFile.getDf(term1));
		assertTrue (0 == invertedFile.getSumFij(term2));
		assertTrue (0 == invertedFile.getSpecificFij(term2,2));
		
	}
	
//	@Test
//	public void removeInvertedFileTest() throws IOException {
//		String term1 = "Test1";
//		String term2 = "Test2";
//		String term3 = "Test2";
//		InvertedFile invertedFile = new InvertedFile();
//		invertedFile.insert(term1, 1, 3);
//		invertedFile.insert(term2, 2, 20);
//		invertedFile.insert(term3, 2, 20);
//		
//		int n = invertedFile.getN();
//		int df = invertedFile.getDf(term2);
//		invertedFile.removeInvertedFile(term2);
//
//		assertTrue (n-df == invertedFile.getN());
//		assertTrue (0 == invertedFile.getDf(term2));
//		assertTrue (1 == invertedFile.getDf(term1));
//		assertTrue (0 == invertedFile.getSumFij(term2));
//		assertTrue (0 == invertedFile.getSpecificFij(term2,2));
//		
//	}
	
//	@Test
//	public void insert() throws IOException {
//		File file = new File(String.format(Share.getProjectProperty("original.data.path")));
//		BufferedReader br = new BufferedReader(new FileReader(file));
//		InvertedFile invertedFile = new InvertedFile();
//int i = 0;
//		String data = br.readLine();
//		while (data != null) {
//			String[] datas = data.split("\\s+");
//			String term = datas[0];
//			Integer docId = Integer.parseInt(datas[1]);
//			Integer position = Integer.parseInt(datas[2]);
//			
//	        invertedFile.insert(term, docId, position);
//			
//			data = br.readLine();
//			i++;
//			System.out.println(i);
//		}
//		
//		br.close();
//		
////		InvertedFile invertedFile = IOProcess.readDocuments(String.format(Share.getProjectProperty("original.data.path")));
//
//        //int sumFij = invertedFile.getSumFij(term);
//       // assertTrue (sumFij == 11);
//        
//       // int maxFij = invertedFile.getMaxFij(term);
//       // assertTrue (maxFij == 6);
//        
//        //int df = invertedFile.getDf(term);
//       // assertTrue (df == 2);
//		assertTrue (invertedFile.getN() == 64813);
//		
//	}

}
