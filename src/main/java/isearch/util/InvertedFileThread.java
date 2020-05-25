package isearch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import isearch.preprocessingmodule.InvertedFile;

public class InvertedFileThread extends Thread {
	
	private InvertedFile i_f = null;
	private boolean isRun = true;
	private ArrayList<Temp> v = null;
	private int x = 0;
	private ThreadLocal tl = null;

	public InvertedFileThread() {
		this.v = new ArrayList<Temp>();
		this.i_f = new InvertedFile();
		this.tl = new ThreadLocal();
	}
	
	public InvertedFileThread(String term, int docId, int term_pos) throws NumberFormatException, IOException {
		this(new Temp(term, docId, term_pos));
	}
	
	public InvertedFileThread(Temp t) throws NumberFormatException, IOException {
		this.v = new ArrayList<Temp>();
		this.i_f = new InvertedFile();
		this.addRecord(t);
	}

	public void addRecord(String term, int docId, int term_pos) {
		this.addRecord(new Temp(term, docId, term_pos));
	}
	
	public void addRecord(Temp t) {
		this.v.add(t);
	}
	
	public boolean isRun() {
		return isRun;
	}
	
	public InvertedFile getInvertedFile() {
		return i_f;
	}

	public void setInvertedFile(InvertedFile i_f) {
		this.i_f = i_f;
	}
	
	public void shutdown() {
		this.tl.remove();
	}

	@SuppressWarnings("static-access")
	public void run() {
		
		if (this.v.isEmpty()) {
			return;
		}
		
		Collections.sort(this.v);
		
		while (true) {
			Iterator<Temp> i = this.v.iterator();
			while (i.hasNext()) {
				Temp t = i.next();
				i_f.insert(t.getTerm(), t.getDocId(), t.getTerm_pos());
				x++;
			}
			
			if (this.size() <= 0) {
				break;
			}
			
			try {
				Thread.currentThread().sleep(1000);		
			} catch (InterruptedException e1) {
				
			}
		}
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public int size() {
		return this.v.size() - x;
	}
}

class Temp implements Comparable<Temp> {
	
	private int docId, term_pos;
	private String term;
	
	public Temp(String term, int docId, int term_pos) {
		this.term = term;
		this.docId = docId;
		this.term_pos = term_pos;
	}

	public int compareTo(Integer i1, Integer i2) {
		return i1.compareTo(i2);
	}

	public int compareTo(Temp t) {
		int r = this.getTerm().compareTo(t.getTerm());
		if (r == 0) {
			r = this.compareTo(this.getDocId(), t.getDocId());
			if (r == 0) {
				r = this.compareTo(this.getTerm_pos(), t.getTerm_pos());
			}
		} 
		return r;
	}

	public int getDocId() {
		return docId;
	}

	public String getTerm() {
		return term;
	}

	public int getTerm_pos() {
		return term_pos;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setTerm_pos(int term_pos) {
		this.term_pos = term_pos;
	}
	
}