package isearch.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import isearch.preprocessingmodule.InvertedFile;
import isearch.searchengine.BM25Model;
import isearch.searchengine.BooleanModel;
import isearch.searchengine.Model;
import isearch.searchengine.VectorSpaceModel;
import isearch.structure.Query;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class CustomMenu {

	private static CustomMenu cm = null;
	private InvertedFile invertedFile = null;
	private PreProcessTask preProcessThread = null;
	private QueryTask queryThread = null;
	private GetNumRecordTask getNumRecordThread = null;
	private PreProcessTimerTask preProcessTimerThread = null;

	private JFrame frame;
	// private String[] lblColumn;
	// private String[] tradeType;

	private JLabel lblSourceFile;
	private JButton btnBrowseSource;
	private JLabel lblSourcePath;

	private JLabel lblQueryFile;
	private JButton btnBrowseQuery;
	private JLabel lblQueryPath;
	
	private JLabel lblFileIdentifier;
	private JButton btnFileIdentifier;
	private JLabel lblFileIdentifierPath;
	
	private JLabel lblJudgerobust;
	private JButton btnJudgerobust;
	private JLabel lblJudgerobustPath;
	
	private JLabel lblTrecEvaluationProgram;
	private JButton btnTrecEvaluationProgram;
	private JLabel lblTrecEvaluationProgramPath;

	private JLabel lblModel;
	private JRadioButton radBoolean;
	private JRadioButton radVectorSpace;
	private JRadioButton radBM25;
	private ButtonGroup bgpModel;

	private JLabel lblTermWeight;
	private JRadioButton radSum;
	private JRadioButton radMax;
	private JRadioButton radNo;
	private ButtonGroup bgpTermWeight;
	
	private JLabel lblProcessWith;
	private JRadioButton radSingleThread;
	private JRadioButton radMultiThread;
	private ButtonGroup bgpProcessWith;

	private JButton btnPreload;
	private JButton btnCalculate;
	private JProgressBar pbPreProcess;
	private JProgressBar pbQuery;
	
	private double preprocessEndTime = 0.0;
	private double queryprocessEndTime = 0.0;

	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//		dataset.addValue(15, "", "1970");
//		dataset.addValue(30, "", "1980");
//		dataset.addValue(60, "", "1990");
//		dataset.addValue(120, "", "2000");
//		dataset.addValue(240, "", "2010");
//		dataset.addValue(300, "", "2014");
		return dataset;
	}

	private JFreeChart lineChart;
	private ChartPanel chartPanel = null;
	private JLabel lblTotalNumOfRecords;
	private JLabel lblTotalNumOfDoc;
	private JLabel lblValidRecord;
	private JLabel lblInvalidRecord;
	private JLabel lblPreProcessTime;
	private JLabel lblQueryTime;
	/****************************************/
	private JLabel lblRPrecision;
	private JLabel lblFPrecision;
	private JLabel lblRecall;
	private JLabel lblPrecision;
	private JLabel lblTop5Precision;
	private JLabel lblTop10Precision;
	private JLabel lblTop15Precision;
	private JLabel lblTop20Precision;
	private JLabel lblTop30Precision;
	private JLabel lblTop100Precision;
	
	/****************************************/
	private JLabel lblTotalNumOfRecordsVar;
	private JLabel lblTotalNumOfDocVar;
	private JLabel lblValidRecordVar;
	private JLabel lblInvalidRecordVar;
	private JLabel lblPreProcessTimeVar;
	private JLabel lblQueryTimeVar;
	/****************************************/
	private JLabel lblRPrecisionVar;
	private JLabel lblFPrecisionVar;
	private JLabel lblRecallVar;
	private JLabel lblPrecisionVar;
	private JLabel lblTop5PrecisionVar;
	private JLabel lblTop10PrecisionVar;
	private JLabel lblTop15PrecisionVar;
	private JLabel lblTop20PrecisionVar;
	private JLabel lblTop30PrecisionVar;
	private JLabel lblTop100PrecisionVar;
	
	/****************************************/
	private Font boldFont;
	private Font plainFont;

	public CustomMenu() {
		CustomMenu.cm = this;
	}

	public void run() {
		// Create and set up the window.
		frame = new JFrame("IR Project");
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frame.getContentPane();

		// Loads Components
		LoadComponents();

		// Size and display the window.
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		frame.setSize(1180, 768);
		frame.setLocation((screenWidth / 2) - (frame.getWidth() / 2), (screenHeight / 2) - (frame.getHeight() / 2));
		frame.setVisible(true);
		pane.setBackground(new Color(204, 229, 255));
	}

	private void LoadComponents() {
		Container pane = frame.getContentPane();
		boldFont = new Font("SansSerif", Font.BOLD, 16);
		plainFont = new Font("SansSerif", Font.PLAIN, 16);

		lblSourceFile = new JLabel("Source File");
		btnBrowseSource = new JButton("Browse");
		lblSourcePath = new JLabel("(Path)");
		btnBrowseSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						lblSourcePath.setText(chooser.getSelectedFile().getAbsolutePath());
						if (cm.getNumRecordThread == null) {
							cm.lblTotalNumOfRecordsVar.setText("0");
							cm.getNumRecordThread = new GetNumRecordTask(CustomMenu.cm);
							cm.getNumRecordThread.start();
						} else {
							if (cm.getNumRecordThread.isAlive() == true) {
								cm.getNumRecordThread.stops();
							}
							cm.lblTotalNumOfRecordsVar.setText("0");
							cm.getNumRecordThread = new GetNumRecordTask(CustomMenu.cm);
							cm.getNumRecordThread.start();
						}
						
						if (cm.preProcessTimerThread != null) {
							if (cm.preProcessTimerThread.isAlive() == true) {
								if (preProcessTimerThread instanceof PreProcessTimerTask) {
									((PreProcessTimerTask) preProcessTimerThread).stops();
								}
							}
						}
						cm.pbPreProcess.setValue(0); //Set value
						cm.pbPreProcess.repaint(); //Refresh graphics
					} else {
						lblSourcePath.setText("(No file selection)");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Browse Source File failed!", "Browse Source File", JOptionPane.WARNING_MESSAGE);
				} finally {
					chooser = null;
				}
			}
		});

		lblQueryFile = new JLabel("Query File:");
		btnBrowseQuery = new JButton("Browse");
		lblQueryPath = new JLabel("(Path)");
		btnBrowseQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						lblQueryPath.setText(chooser.getSelectedFile().getAbsolutePath());
						lblQueryTimeVar.setText("0");
					} else {
						lblQueryPath.setText("(No file selection)");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Browse Query File failed!", "Browse Query File", JOptionPane.WARNING_MESSAGE);
				} finally {
					chooser = null;
				}
			}
		});
		
		lblFileIdentifier = new JLabel("File Idenitfier:");
		btnFileIdentifier = new JButton("Browse");
		lblFileIdentifierPath = new JLabel("(Path)");
		btnFileIdentifier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						lblFileIdentifierPath.setText(chooser.getSelectedFile().getAbsolutePath());
					} else {
						lblFileIdentifierPath.setText("(No file selection)");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Browse File Idenitfier File failed!", "Browse File Idenitfier File", JOptionPane.WARNING_MESSAGE);
				} finally {
					chooser = null;
				}
			}
		});
		
		lblJudgerobust = new JLabel("Judge Robust File:");
		btnJudgerobust = new JButton("Browse");
		lblJudgerobustPath = new JLabel("(Path)");
		btnJudgerobust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						lblJudgerobustPath.setText(chooser.getSelectedFile().getAbsolutePath());
					} else {
						lblJudgerobustPath.setText("(No file selection)");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Browse Judge Robust File failed!", "Browse Judge Robust File", JOptionPane.WARNING_MESSAGE);
				} finally {
					chooser = null;
				}
			}
		});
		
		lblTrecEvaluationProgram = new JLabel("Evaluation Program:");
		btnTrecEvaluationProgram = new JButton("Browse");
		lblTrecEvaluationProgramPath = new JLabel("(Path)");
		btnTrecEvaluationProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						lblTrecEvaluationProgramPath.setText(chooser.getSelectedFile().getAbsolutePath());
					} else {
						lblTrecEvaluationProgramPath.setText("(No file selection)");
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Browse Trec Evaluation Program failed!", "Browse Trec Evaluation Program", JOptionPane.WARNING_MESSAGE);
				} finally {
					chooser = null;
				}
			}
		});

		lblModel = new JLabel("Retrieval model:");

		radBoolean = new JRadioButton("Boolean Model");
		radBoolean.setActionCommand("0");
		radBoolean.setSelected(true);

		radVectorSpace = new JRadioButton("Vector Space Model");
		radVectorSpace.setActionCommand("1");

		radBM25 = new JRadioButton("BM25 Model");
		radBM25.setActionCommand("2");

		bgpModel = new ButtonGroup();
		bgpModel.add(radBoolean);
		bgpModel.add(radVectorSpace);
		bgpModel.add(radBM25);

		lblTermWeight = new JLabel("Term Weight Normalization");

		radSum = new JRadioButton("Sum");
		radSum.setActionCommand("0");

		radMax = new JRadioButton("Max");
		radMax.setActionCommand("1");
		
		radNo = new JRadioButton("No");
		radNo.setActionCommand("2");
		radNo.setSelected(true);

		bgpTermWeight = new ButtonGroup();
		bgpTermWeight.add(radSum);
		bgpTermWeight.add(radMax);
		bgpTermWeight.add(radNo);
	
		lblProcessWith = new JLabel("Pre-Process With");
		radSingleThread = new JRadioButton("Single Thread");
		radSingleThread.setActionCommand("0");
		radMultiThread = new JRadioButton("Multi Thread");
		radMultiThread.setActionCommand("1");
		radMultiThread.setSelected(true);
		bgpProcessWith = new ButtonGroup();
		bgpProcessWith.add(radSingleThread);
		bgpProcessWith.add(radMultiThread);

		lineChart = ChartFactory.createLineChart("", "", "",
				createDataset(), PlotOrientation.VERTICAL, false, false, false);

		chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));

		lblTotalNumOfRecords = new JLabel("Total Num. of Lines:");
		lblTotalNumOfDoc = new JLabel("Total Num. of Documents:");
		lblValidRecord = new JLabel("Valid Record:");
		lblInvalidRecord = new JLabel("Invalid Record:");
		lblPreProcessTime = new JLabel("Pre-Process Time:");
		lblQueryTime = new JLabel("Query Time:");
		/********************************/
		
		lblRPrecision = new JLabel("R-Precision:");
		lblFPrecision = new JLabel("F-Precision:");
		lblRecall = new JLabel("Recall:");
		lblPrecision = new JLabel("Precision:");
		lblTop5Precision = new JLabel("Top 5 Precision:");
		lblTop10Precision = new JLabel("Top 10 Precision:");
		lblTop15Precision = new JLabel("Top 15 Precision:");
		lblTop20Precision = new JLabel("Top 20 Precision:");
		lblTop30Precision = new JLabel("Top 30 Precision:");
		lblTop100Precision = new JLabel("Top 100 Precision:");
		
		
		/********************************/	
		lblTotalNumOfRecordsVar = new JLabel("0");
		lblTotalNumOfDocVar = new JLabel("0");
		lblValidRecordVar = new JLabel("0");
		lblInvalidRecordVar = new JLabel("0");
		lblPreProcessTimeVar = new JLabel("0");
		lblQueryTimeVar = new JLabel("0");
		/********************************/
		
		lblRPrecisionVar = new JLabel("0");
		lblFPrecisionVar = new JLabel("0");
		lblRecallVar = new JLabel("0");
		lblPrecisionVar = new JLabel("0");
		lblTop5PrecisionVar = new JLabel("0");
		lblTop10PrecisionVar = new JLabel("0");
		lblTop15PrecisionVar = new JLabel("0");
		lblTop20PrecisionVar = new JLabel("0");
		lblTop30PrecisionVar = new JLabel("0");
		lblTop100PrecisionVar = new JLabel("0");
		
		
		
		/********************************/
		pbPreProcess = new JProgressBar();
		pbPreProcess.setValue(0);
		pbPreProcess.setStringPainted(true);
		pbPreProcess.setMinimum(0);
		pbPreProcess.setMaximum(100);
		pane.add(pbPreProcess, BorderLayout.NORTH);
		
		pbQuery = new JProgressBar();
		pbQuery.setValue(0);
		pbQuery.setStringPainted(true);
		pbQuery.setMinimum(0);
		pbQuery.setMaximum(100);
		pane.add(pbQuery, BorderLayout.NORTH);

		btnPreload = new JButton("Pre-Process");
		btnPreload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = new File(lblFileIdentifierPath.getText());
				if (f.exists()) {
					
				} else {
					JOptionPane.showMessageDialog(null, "Please select file-Identifier file first!", "Pre-Process Data", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if (preProcessThread != null) {
					if (preProcessThread.isAlive() == true) {
						JOptionPane.showMessageDialog(null, "Pre-processing the data file! Please wait until finished!", "Pre-Process Data", JOptionPane.WARNING_MESSAGE);
						return;
					} else {
						preProcessThread = new PreProcessTask(CustomMenu.cm);
						preProcessThread.start();
						preProcessTimerThread = new PreProcessTimerTask(CustomMenu.cm);
						preProcessTimerThread.start();
					}
				} else if (queryThread != null) {
					if (queryThread.isAlive() == true) {
						JOptionPane.showMessageDialog(null, "A query is currently being conducted! Please wait until finished!", "Pre-Process Data", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					preProcessThread = new PreProcessTask(CustomMenu.cm);
					preProcessThread.start();
					new PreProcessTimerTask(CustomMenu.cm).start();
				}
			}
		});

		btnCalculate = new JButton("Query");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (queryThread != null) {
					if (queryThread.isAlive() == true) {
						JOptionPane.showMessageDialog(null, "A query is currently being conducted! Please wait until finished!", "Pre-Process Data", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				if (preProcessThread != null) {
					if (preProcessThread.isAlive() == true) {
						JOptionPane.showMessageDialog(null, "Pre-processing the data file! Please wait until finished!", "Quey Process", JOptionPane.ERROR_MESSAGE);
						return;
					}
					queryThread = new QueryTask(CustomMenu.cm);
					queryThread.start();
				}

			}
		});

		pane.add(lblSourceFile);
		pane.add(btnBrowseSource);
		pane.add(lblSourcePath);

		pane.add(lblQueryFile);
		pane.add(btnBrowseQuery);
		pane.add(lblQueryPath);
		
		pane.add(lblFileIdentifier);
		pane.add(btnFileIdentifier);
		pane.add(lblFileIdentifierPath);
		
		pane.add(lblJudgerobust);
		pane.add(btnJudgerobust);
		pane.add(lblJudgerobustPath);
		
		pane.add(lblTrecEvaluationProgram);
		pane.add(btnTrecEvaluationProgram);
		pane.add(lblTrecEvaluationProgramPath);

		pane.add(lblModel);
		pane.add(radBoolean);
		pane.add(radVectorSpace);
		pane.add(radBM25);

		pane.add(lblTermWeight);
		pane.add(radSum);
		pane.add(radMax);
		pane.add(radNo);
		
		pane.add(lblProcessWith);
		pane.add(radSingleThread);
		pane.add(radMultiThread);

		pane.add(btnPreload);
		pane.add(btnCalculate);
		pane.add(chartPanel);
		pane.add(lblTotalNumOfRecords);
		pane.add(lblTotalNumOfDoc);
		pane.add(lblValidRecord);
		pane.add(lblInvalidRecord);
		pane.add(lblPreProcessTime);
		pane.add(lblQueryTime);
		/*********************************/
		
		pane.add(lblRPrecision);
		pane.add(lblFPrecision);
		pane.add(lblRecall);
		pane.add(lblPrecision);
		pane.add(lblTop5Precision);
		pane.add(lblTop10Precision);
		pane.add(lblTop15Precision);
		pane.add(lblTop20Precision);
		pane.add(lblTop30Precision);
		pane.add(lblTop100Precision);
		
		
		
		
		
		/********************************/		
		
		pane.add(lblTotalNumOfRecordsVar);
		pane.add(lblTotalNumOfDocVar);
		pane.add(lblValidRecordVar);
		pane.add(lblInvalidRecordVar);
		pane.add(lblPreProcessTimeVar);
		pane.add(lblQueryTimeVar);
		/*********************************/
		
		pane.add(lblRPrecisionVar);
		pane.add(lblFPrecisionVar);
		pane.add(lblRecallVar);
		pane.add(lblPrecisionVar);
		pane.add(lblTop5PrecisionVar);
		pane.add(lblTop10PrecisionVar);
		pane.add(lblTop15PrecisionVar);
		pane.add(lblTop20PrecisionVar);
		pane.add(lblTop30PrecisionVar);
		pane.add(lblTop100PrecisionVar);
		
		
		
		
		
		
		/********************************/
		// lblTradeType.setFont(boldFont);

		lblSourceFile.setBounds(50, 25, lblSourceFile.getPreferredSize().width, lblSourceFile.getPreferredSize().height);
		btnBrowseSource.setBounds(210, 20, 90, btnBrowseSource.getPreferredSize().height);
		lblSourcePath.setBounds(330, 25, 600, lblSourcePath.getPreferredSize().height);

		lblFileIdentifier.setBounds(50, 70, lblFileIdentifier.getPreferredSize().width, lblFileIdentifier.getPreferredSize().height);
		btnFileIdentifier.setBounds(210, 65, 90, btnFileIdentifier.getPreferredSize().height);
		lblFileIdentifierPath.setBounds(330, 70, 600, lblFileIdentifierPath.getPreferredSize().height);
		
		lblQueryFile.setBounds(50, 115, lblQueryFile.getPreferredSize().width, lblQueryFile.getPreferredSize().height);
		btnBrowseQuery.setBounds(210, 110, 90, btnBrowseQuery.getPreferredSize().height);
		lblQueryPath.setBounds(330, 115, 600, lblQueryPath.getPreferredSize().height);
		
		lblJudgerobust.setBounds(50, 160, lblJudgerobust.getPreferredSize().width, lblJudgerobust.getPreferredSize().height);
		btnJudgerobust.setBounds(210, 155, 90, btnJudgerobust.getPreferredSize().height);
		lblJudgerobustPath.setBounds(330, 160, 600, lblJudgerobustPath.getPreferredSize().height);
		
		lblTrecEvaluationProgram.setBounds(50, 205, lblTrecEvaluationProgram.getPreferredSize().width, lblTrecEvaluationProgram.getPreferredSize().height);
		btnTrecEvaluationProgram.setBounds(210, 200, 90, btnTrecEvaluationProgram.getPreferredSize().height);
		lblTrecEvaluationProgramPath.setBounds(330, 205, 600, lblTrecEvaluationProgramPath.getPreferredSize().height);

		lblModel.setBounds(50, 240, lblModel.getPreferredSize().width, lblModel.getPreferredSize().height);
		radBoolean.setBounds(50, 270, radVectorSpace.getPreferredSize().width, radVectorSpace.getPreferredSize().height);
		radVectorSpace.setBounds(50, 295, radVectorSpace.getPreferredSize().width, radVectorSpace.getPreferredSize().height);
		radBM25.setBounds(50, 320, radVectorSpace.getPreferredSize().width, radVectorSpace.getPreferredSize().height);

		lblTermWeight.setBounds(330, 240, lblTermWeight.getPreferredSize().width, lblTermWeight.getPreferredSize().height);
		radNo.setBounds(330, 270, radSum.getPreferredSize().width, radSum.getPreferredSize().height);
		radSum.setBounds(380, 270, radSum.getPreferredSize().width, radSum.getPreferredSize().height);
		radMax.setBounds(430, 270, radMax.getPreferredSize().width, radMax.getPreferredSize().height);
		
		lblProcessWith.setBounds(900, 20, lblProcessWith.getPreferredSize().width, lblProcessWith.getPreferredSize().height);
		radSingleThread.setBounds(900, 35, radSingleThread.getPreferredSize().width, radSingleThread.getPreferredSize().height);
		radMultiThread.setBounds(900, 60, radSingleThread.getPreferredSize().width, radMultiThread.getPreferredSize().height);

		btnPreload.setBounds(740, 20, 150, btnPreload.getPreferredSize().height);
		pbPreProcess.setBounds(740, 70, 150, 20);
		btnCalculate.setBounds(740, 110, 150, btnCalculate.getPreferredSize().height);
		pbQuery.setBounds(740, 160, 150, 20);
		chartPanel.setBounds(330, 310, 560, 367);

		lblTotalNumOfRecords.setBounds(50, 380, lblTotalNumOfRecords.getPreferredSize().width, lblTotalNumOfRecords.getPreferredSize().height);
		lblTotalNumOfDoc.setBounds(50, 410, lblTotalNumOfDoc.getPreferredSize().width, lblTotalNumOfDoc.getPreferredSize().height);
		lblValidRecord.setBounds(50, 440, lblValidRecord.getPreferredSize().width, lblValidRecord.getPreferredSize().height);
		lblInvalidRecord.setBounds(50, 470, lblInvalidRecord.getPreferredSize().width, lblInvalidRecord.getPreferredSize().height);
		lblPreProcessTime.setBounds(50, 500, lblPreProcessTime.getPreferredSize().width, lblPreProcessTime.getPreferredSize().height);
		lblQueryTime.setBounds(50, 530, lblQueryTime.getPreferredSize().width, lblQueryTime.getPreferredSize().height);

		/*********************************/
		lblRPrecision.setBounds(910, 320, lblRPrecision.getPreferredSize().width, lblRPrecision.getPreferredSize().height);
		lblFPrecision.setBounds(910, 350, lblFPrecision.getPreferredSize().width, lblFPrecision.getPreferredSize().height);
		lblRecall.setBounds(910, 380, lblRecall.getPreferredSize().width, lblRecall.getPreferredSize().height);
		lblPrecision.setBounds(910, 410, lblPrecision.getPreferredSize().width, lblPrecision.getPreferredSize().height);
		lblTop5Precision.setBounds(910, 440, lblTop5Precision.getPreferredSize().width, lblTop5Precision.getPreferredSize().height);
		lblTop10Precision.setBounds(910, 470, lblTop10Precision.getPreferredSize().width, lblTop10Precision.getPreferredSize().height);
		lblTop15Precision.setBounds(910, 500, lblTop15Precision.getPreferredSize().width, lblTop15Precision.getPreferredSize().height);
		lblTop20Precision.setBounds(910, 530, lblTop20Precision.getPreferredSize().width, lblTop20Precision.getPreferredSize().height);
		lblTop30Precision.setBounds(910, 560, lblTop30Precision.getPreferredSize().width, lblTop30Precision.getPreferredSize().height);
		lblTop100Precision.setBounds(910, 590, lblTop100Precision.getPreferredSize().width, lblTop100Precision.getPreferredSize().height);
		
		
		
		/********************************/

		lblTotalNumOfRecordsVar.setBounds(210, 380, 200, lblTotalNumOfRecordsVar.getPreferredSize().height);
		lblTotalNumOfDocVar.setBounds(210, 410, 200, lblTotalNumOfDocVar.getPreferredSize().height);
		lblValidRecordVar.setBounds(210, 440, 200, lblValidRecordVar.getPreferredSize().height);
		lblInvalidRecordVar.setBounds(210, 470, 200, lblInvalidRecordVar.getPreferredSize().height);
		lblPreProcessTimeVar.setBounds(210, 500, 200, lblPreProcessTimeVar.getPreferredSize().height);
		lblQueryTimeVar.setBounds(210, 530, 200, lblQueryTimeVar.getPreferredSize().height);

		/*********************************/
		
		
		lblRPrecisionVar.setBounds(1060, 320, 200, lblRPrecisionVar.getPreferredSize().height);
		lblFPrecisionVar.setBounds(1060, 350, 200, lblFPrecisionVar.getPreferredSize().height);
		lblRecallVar.setBounds(1060, 380, 200, lblRecallVar.getPreferredSize().height);
		lblPrecisionVar.setBounds(1060, 410, 200, lblPrecisionVar.getPreferredSize().height);
		lblTop5PrecisionVar.setBounds(1060, 440, 200, lblTop5PrecisionVar.getPreferredSize().height);
		lblTop10PrecisionVar.setBounds(1060, 470, 200, lblTop10PrecisionVar.getPreferredSize().height);
		lblTop15PrecisionVar.setBounds(1060, 500, 200, lblTop15PrecisionVar.getPreferredSize().height);
		lblTop20PrecisionVar.setBounds(1060, 530, 200, lblTop20PrecisionVar.getPreferredSize().height);
		lblTop30PrecisionVar.setBounds(1060, 560, 200, lblTop30PrecisionVar.getPreferredSize().height);
		lblTop100PrecisionVar.setBounds(1060, 590, 200, lblTop100PrecisionVar.getPreferredSize().height);
		
		
		
		
		/********************************/

	}

	// private void setLocationBox(String[] locationArray){
	// cmbDistrict.removeAllItems();
	// for (int i=0;i<locationArray.length;i++){
	// cmbDistrict.addItem(locationArray[i]);
	// }
	// }
	
	class GetNumRecordTask extends Thread {
		private volatile boolean stopped = false;
		CustomMenu cm;

		public GetNumRecordTask(CustomMenu c) {
			cm = c;
		}
		
		public void stops() {
			this.stopped = true;
		}

		public void run() {
			cm.lblTotalNumOfRecordsVar.setText("0");
			cm.lblTotalNumOfDocVar.setText("0");
			cm.lblValidRecordVar.setText("0");
			cm.lblInvalidRecordVar.setText("0");
			cm.lblPreProcessTimeVar.setText("0");
			cm.lblQueryTimeVar.setText("0");
			
			/************************************/
			
			cm.lblRPrecisionVar.setText("0");
			cm.lblFPrecisionVar.setText("0");
			cm.lblRecallVar.setText("0");
			cm.lblPrecisionVar.setText("0");
			cm.lblTop5PrecisionVar.setText("0");
			cm.lblTop10PrecisionVar.setText("0");
			cm.lblTop15PrecisionVar.setText("0");
			cm.lblTop20PrecisionVar.setText("0");
			cm.lblTop30PrecisionVar.setText("0");
			cm.lblTop100PrecisionVar.setText("0");

			
			/************************************/
			int x = 0;
			String line = null;

			try {
				BufferedReader br = new BufferedReader(new FileReader(cm.lblSourcePath.getText()));
				line = br.readLine();
				while (line != null) {
					if (stopped) {
						br.close();
						new ThreadLocal().remove();
						return;
					}
					x++;
					cm.lblTotalNumOfRecordsVar.setText("" + x);
					line = br.readLine();
				}
				
				br.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Browse Source File failed!", "Browse Source File", JOptionPane.WARNING_MESSAGE);
				new ThreadLocal().remove();
				return;
			}
			new ThreadLocal().remove();
		}
	}
	
	class PreProcessTask extends Thread {
		private volatile boolean stopped = false;
		CustomMenu cm;

		public PreProcessTask(CustomMenu c) {
			cm = c;
		}

		public void run() {
			cm.btnBrowseSource.setEnabled(false);
			cm.btnFileIdentifier.setEnabled(false);
			cm.btnCalculate.setEnabled(false);
			cm.radSingleThread.setEnabled(false);
			cm.radMultiThread.setEnabled(false);
			Date startTimeOfPreProcess = new Date();

			try {
				cm.invertedFile = null;
				if (cm.radMultiThread.isSelected()) {
					cm.invertedFile = IOProcess.readDocuments(cm.lblSourcePath.getText(), cm.lblFileIdentifierPath.getText(), true);
				} else if (cm.radSingleThread.isSelected()) {
					cm.invertedFile = IOProcess.readDocuments(cm.lblSourcePath.getText(), cm.lblFileIdentifierPath.getText(), false);
				}
			} catch (IOException e1) {
				return;
			} catch (InterruptedException e1) {
				return;
			}

			Date endTimeOfPreProcess = new Date();
			double preprocessSecond = (endTimeOfPreProcess.getTime() - startTimeOfPreProcess.getTime()) / 1000.0;
			cm.lblTotalNumOfDocVar.setText("" + cm.invertedFile.getN());
			cm.lblValidRecordVar.setText("" + cm.invertedFile.getNumOfData());
			cm.lblInvalidRecordVar.setText("" + (Integer.parseInt(cm.lblTotalNumOfRecordsVar.getText()) - cm.invertedFile.getNumOfData()));
			//cm.lblPreProcessTimeVar.setText(preprocessSecond + " seconds");
			cm.preprocessEndTime = preprocessSecond;
			cm.btnBrowseSource.setEnabled(true);
			cm.btnFileIdentifier.setEnabled(true);
			cm.btnCalculate.setEnabled(true);
			cm.radSingleThread.setEnabled(true);
			cm.radMultiThread.setEnabled(true);
			ThreadLocal tl = new ThreadLocal();
			tl.remove();
		}
	}
	
	class PreProcessTimerTask extends Thread {
		private volatile boolean stopped = false;
		CustomMenu cm;

		public PreProcessTimerTask(CustomMenu c) {
			cm = c;
		}
		
		public void stops() {
			this.stopped = true;
		}

		public void run() {
			if (cm.preProcessThread == null) {
				return;
			}
			
			Calendar start = Calendar.getInstance();
			while (cm.preProcessThread.isAlive() == true) {
				if (stopped) {
					return;
				}
				
				if (cm.getNumRecordThread.isAlive() == false) {
					pbPreProcess.setValue((int) ((IOProcess.currentRecordNum * 1.0) / (Integer.parseInt(cm.lblTotalNumOfRecordsVar.getText()) * 1.0) * 100 * 80 / 100)); //Set value
					pbPreProcess.repaint();
				}
				cm.lblPreProcessTimeVar.setText(((Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis())/1000) + " seconds");
				try {
					if (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis() < 1000) {
						this.sleep( 1000 - (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()));
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			cm.lblPreProcessTimeVar.setText(cm.preprocessEndTime + " seconds");
			pbPreProcess.setValue(100); //Set value
			pbPreProcess.repaint(); //Refresh graphics
			ThreadLocal tl = new ThreadLocal();
			tl.remove();
		}
	}
	
	class QueryTask extends Thread {
		CustomMenu cm;
		private volatile boolean isDone = false;

		public QueryTask(CustomMenu c) {
			cm = c;
		}
		
		public boolean isDone() {
			return this.isDone;
		}

		public void run() {
			try {
				JFileChooser chooser = new JFileChooser();

				try {
					chooser.setMultiSelectionEnabled(false);
					int option = chooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {

					} else {
						JOptionPane.showMessageDialog(null, "Select a output file to start query!", "Browse Output File", JOptionPane.WARNING_MESSAGE);
						return;
					}
				} catch (Exception ex) {

				}
				new QueryProcessTimerTask(CustomMenu.cm).start();
				
				cm.lblQueryTimeVar.setText(0 + " seconds");
				cm.btnBrowseSource.setEnabled(false);
				cm.btnBrowseQuery.setEnabled(false);
				cm.btnFileIdentifier.setEnabled(false);
				cm.radSum.setEnabled(false);
				cm.radMax.setEnabled(false);
				cm.radNo.setEnabled(false);
				cm.radBoolean.setEnabled(false);
				cm.radVectorSpace.setEnabled(false);
				cm.radBM25.setEnabled(false);
				cm.btnPreload.setEnabled(false);
				cm.btnJudgerobust.setEnabled(false);
				cm.btnTrecEvaluationProgram.setEnabled(false);
				cm.radSingleThread.setEnabled(false);
				cm.radMultiThread.setEnabled(false);
				
				
				/*****************************************/
				
				cm.lblRPrecisionVar.setEnabled(false);
				cm.lblFPrecisionVar.setEnabled(false);
				cm.lblRecallVar.setEnabled(false);
				cm.lblPrecisionVar.setEnabled(false);
				cm.lblTop5PrecisionVar.setEnabled(false);
				cm.lblTop10PrecisionVar.setEnabled(false);
				cm.lblTop15PrecisionVar.setEnabled(false);
				cm.lblTop20PrecisionVar.setEnabled(false);
				cm.lblTop30PrecisionVar.setEnabled(false);
				cm.lblTop100PrecisionVar.setEnabled(false);
				
				
				
				/*******************************************/
				
				
				//Date startTimeOfQuery = new Date();
				ArrayList<Query> al_q = IOProcess.readQuery(cm.lblQueryPath.getText());
				int numOfQuery = 0;
				Model model = null;
				if (radBoolean.isSelected()) {
					model = new BooleanModel(cm.invertedFile);
				} else if (radVectorSpace.isSelected()) {
					model = new VectorSpaceModel(cm.invertedFile);
				} else if (radBM25.isSelected()) {
					model = new BM25Model(cm.invertedFile);
				}
				
				Iterator<Query> i_q = al_q.iterator();
				while (i_q.hasNext()) {
					Query q = i_q.next();
					HashMap<Integer, Double> results = null;
					if (radSum.isSelected()) {
						results = model.search_NormalizeBySum(q);
					} else if (radMax.isSelected()) {
						results = model.search_NormalizeByMax(q);
					} else if (radNo.isSelected()) {
						results = model.search(q);
					}

					IOProcess.searchResultToFile(q.getId(), chooser.getSelectedFile().getAbsolutePath(), results, "HKPU_Group_F");
					numOfQuery++;
					cm.pbQuery.setValue((int) (numOfQuery * 1.0 / al_q.size() * 100)); //Set value
					cm.pbQuery.repaint();
				}
				
				isDone = true;
				
				TrecEval te = new TrecEval(cm.lblTrecEvaluationProgramPath.getText(), chooser.getSelectedFile().getAbsolutePath(), cm.lblJudgerobustPath.getText());
				TrecEval.XYcoordinate[] xy = te.getRecallPrecisionXY();
				DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				for (int i = 0; i < xy.length; i++) {
					dataset.addValue(xy[i].y , "", "" + xy[i].x);
				}
				
				JFreeChart lineChart = ChartFactory.createLineChart("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false);

				cm.chartPanel.setChart(lineChart);
				cm.chartPanel.repaint();
				/****************************************/
				cm.lblRPrecisionVar.setText(new DecimalFormat("0.00").format(te.getRPrecision()));
				cm.lblFPrecisionVar.setText(new DecimalFormat("0.00").format(te.getFPrecision()));
				cm.lblRecallVar.setText(new DecimalFormat("0.00").format(te.getRecall()));
				cm.lblPrecisionVar.setText(new DecimalFormat("0.00").format(te.getPrecision()));
				cm.lblTop5PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop5Precision()));
				cm.lblTop10PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop10Precision()));
				cm.lblTop15PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop15Precision()));
				cm.lblTop20PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop20Precision()));
				cm.lblTop30PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop30Precision()));
				cm.lblTop100PrecisionVar.setText(new DecimalFormat("0.00").format(te.getTop100Precision()));
				
				
				cm.lblRPrecisionVar.setEnabled(true);
				cm.lblFPrecisionVar.setEnabled(true);
				cm.lblRecallVar.setEnabled(true);
				cm.lblPrecisionVar.setEnabled(true);
				cm.lblTop5PrecisionVar.setEnabled(true);
				cm.lblTop10PrecisionVar.setEnabled(true);
				cm.lblTop15PrecisionVar.setEnabled(true);
				cm.lblTop20PrecisionVar.setEnabled(true);
				cm.lblTop30PrecisionVar.setEnabled(true);
				cm.lblTop100PrecisionVar.setEnabled(true);
				
				/****************************************/
				cm.btnBrowseSource.setEnabled(true);
				cm.btnBrowseQuery.setEnabled(true);
				cm.btnFileIdentifier.setEnabled(true);
				cm.radSum.setEnabled(true);
				cm.radMax.setEnabled(true);
				cm.radNo.setEnabled(true);
				radBoolean.setEnabled(true);
				radVectorSpace.setEnabled(true);
				radBM25.setEnabled(true);
				cm.btnPreload.setEnabled(true);
				cm.btnJudgerobust.setEnabled(true);
				cm.btnTrecEvaluationProgram.setEnabled(true);
				cm.radSingleThread.setEnabled(true);
				cm.radMultiThread.setEnabled(true);
			} catch (Exception e) {
				

				
				cm.btnBrowseSource.setEnabled(true);
				cm.btnBrowseQuery.setEnabled(true);
				cm.btnFileIdentifier.setEnabled(true);
				cm.radSum.setEnabled(true);
				cm.radMax.setEnabled(true);
				cm.radNo.setEnabled(true);
				radBoolean.setEnabled(true);
				radVectorSpace.setEnabled(true);
				radBM25.setEnabled(true);
				cm.btnPreload.setEnabled(true);
				cm.btnJudgerobust.setEnabled(true);
				cm.btnTrecEvaluationProgram.setEnabled(true);
				cm.radSingleThread.setEnabled(true);
				cm.radMultiThread.setEnabled(true);
				e.printStackTrace();
			}
			ThreadLocal tl = new ThreadLocal();
			tl.remove();
		}
	}
	
	class QueryProcessTimerTask extends Thread {
		private volatile boolean stopped = false;
		CustomMenu cm;

		public QueryProcessTimerTask(CustomMenu c) {
			cm = c;
		}
		
		public void stops() {
			this.stopped = true;
		}

		public void run() {
			if (cm.queryThread == null) {
				return;
			}
			
			Calendar start = Calendar.getInstance();
			while (cm.queryThread.isAlive() == true && !cm.queryThread.isDone()) {
				if (stopped) {
					return;
				}
				
				cm.lblQueryTimeVar.setText(((Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis())/1000) + " seconds");
				queryprocessEndTime = Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis();
				try {
					if (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis() < 1000) {
						this.sleep( 1000 - (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			cm.lblQueryTimeVar.setText((cm.queryprocessEndTime / 1000) + " seconds");
			
			ThreadLocal tl = new ThreadLocal();
			tl.remove();
		}
	}

}
