package isearch.util;

import java.util.Enumeration;
import java.util.Vector;

import isearch.preprocessingmodule.InvertedFile;

public class MergeInvertedFileThread extends Thread {
	
	private InvertedFile i_f = null;
	private boolean isRun = true;
	private Vector<InvertedFile> v = null;
	private ThreadLocal tl = null;

	@Deprecated
	public MergeInvertedFileThread() {
		this(new InvertedFile());
	}
	
	public MergeInvertedFileThread(InvertedFile i_f) {
		this.i_f = i_f;
		this.v = new Vector<InvertedFile>();
		this.tl = new ThreadLocal();
	}
	
	public void shutdown() {
		this.tl.remove();
	}

	public void run() {
		while (isRun == true || this.size() > 0) {
			Enumeration<InvertedFile> e = this.v.elements();
			while (e.hasMoreElements()) {
				InvertedFile mergeInstance = e.nextElement();
				this.i_f.merge(mergeInstance);
				this.v.remove(mergeInstance);
			}
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean isRun() {
		return isRun;
	}
	
	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}
	
	public void addMergeInstance(InvertedFile i_f) {
		this.v.add(i_f);
	}
	
	public InvertedFile getInvertedFile() {
		return this.i_f;
	}
	
	public int size() {
		return this.v.size();
	}

}
