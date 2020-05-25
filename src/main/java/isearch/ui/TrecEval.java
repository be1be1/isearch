package isearch.ui;

import java.io.InputStream;

/**
 * Created by Beibei on 13/11/2019.
 */
public class TrecEval {
	private double averagePrecision = -1.0;
	private double RPrecision = -1.0;
	private double FPrecision = -1.0;
	private int totalRetrieved = -1;
	private int totalRelevant = -1;
	private int totalRetrivedAndRelevant = -1;
	private double recall = -1.0;
	private double precision = -1.0;
	private double top5Precision = -1.0;
	private double top10Precision = -1.0;
	private double top15Precision = -1.0;
	private double top20Precision = -1.0;
	private double top30Precision = -1.0;
	private double top100Precision = -1.0;
	private XYcoordinate[] RecallPrecisionXY = new XYcoordinate[11];

	public TrecEval(String trecExePath, String searchResultPath, String judgeFilePath)
			throws Exception {
		ProcessBuilder pb = new ProcessBuilder(trecExePath, "-o", judgeFilePath, searchResultPath);
		//Process process = Runtime.getRuntime().exec(new String[] { trecExePath, "-o", judgeFilePath, searchResultPath });
		Process process = pb.start();
		System.out.println(trecExePath + " -o " + judgeFilePath + " " + searchResultPath);

		int exitValue = process.waitFor();
		InputStream in = process.getInputStream();

		byte b[] = new byte[in.available()];
		in.read(b, 0, b.length);
		String[] evalResult = (new String(b)).split(System.getProperty("line.separator"));

		// line 3 is the total retrieved document
		totalRetrieved = Integer.parseInt((evalResult[3].substring(18)).trim());
		// line 4 is the total relevant document
		totalRelevant = Integer.parseInt(evalResult[4].substring(18).trim());

		// line 5 is the relevant intersect retrived
		totalRetrivedAndRelevant = Integer.parseInt(evalResult[5].substring(18).trim());

		// line 7 - 17 are the XY coordinate for the recall-precision-graph
		for (int i = 0; i < 11; i++) {
			RecallPrecisionXY[i] = new XYcoordinate(Double.parseDouble(evalResult[7 + i].substring(7, 12)),
					Double.parseDouble(evalResult[7 + i].substring(18, 24)));
		}

		// line 19 is the average precision
		averagePrecision = Double.parseDouble(evalResult[19].substring(18, 24));

		// line 21 - 26 is the top x docs precision
		top5Precision = Double.parseDouble(evalResult[21].substring(18, 24));
		top10Precision = Double.parseDouble(evalResult[22].substring(18, 24));
		top15Precision = Double.parseDouble(evalResult[23].substring(18, 24));
		top20Precision = Double.parseDouble(evalResult[24].substring(18, 24));
		top30Precision = Double.parseDouble(evalResult[25].substring(18, 24));
		top100Precision = Double.parseDouble(evalResult[26].substring(18, 24));
		
		// line 31 is the r precision
		RPrecision = Double.parseDouble(evalResult[31].substring(18, 24));

		recall = (double) totalRetrivedAndRelevant / (double) totalRelevant; // caclulate
																				// recall
		precision = (double) totalRetrivedAndRelevant / (double) totalRetrieved; // caclulate
																					// recall
		FPrecision = (2 * recall * precision) / (recall + precision); // calculate
																		// f
																		// precision

		return;
	}

	public double getRPrecision() {
		return RPrecision;
	}

	public double getFPrecision() {
		return FPrecision;
	}

	public double getRecall() {
		return recall;
	}

	public double getPrecision() {
		return precision;
	}

	public double getTop5Precision(){
		return top5Precision;
	}
	
	public double getTop10Precision(){
		return top10Precision;
	}
	
	public double getTop15Precision(){
		return top15Precision;
	}
	
	public double getTop20Precision(){
		return top20Precision;
	}
	
	public double getTop30Precision(){
		return top30Precision;
	}
	
	public double getTop100Precision(){
		return top100Precision;
	}
	
	public XYcoordinate[] getRecallPrecisionXY() {
		return RecallPrecisionXY;
	}

	class XYcoordinate {
		public double x;
		public double y;

		public XYcoordinate(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}
