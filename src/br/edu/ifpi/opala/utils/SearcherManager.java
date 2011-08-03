package br.edu.ifpi.opala.utils;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

/**
 * Gerencia sincronização de IndexSearcher's no índice passado no construtor.
 * Classe baseada no exemplo do seção 11.2.1 do livro
 * "Lucene in Action, 2nd Edition".
 * 
 * Utility class to get/refresh searchers when you are using multiple threads.
 */

public class SearcherManager {

	private IndexSearcher currentSearcher; // A
	private IndexWriter writer;

	public SearcherManager(Directory dir) throws IOException { // 1
		currentSearcher = new IndexSearcher(IndexReader.open(dir)); // B
		warm(currentSearcher);
	}

	public SearcherManager(IndexWriter writer) throws IOException { // 2
		this.writer = writer;
		currentSearcher = new IndexSearcher(writer.getReader()); // C
		warm(currentSearcher);

		writer.setMergedSegmentWarmer( // 3
		new IndexWriter.IndexReaderWarmer() { // 3
			public void warm(IndexReader reader) throws IOException { // 3
				SearcherManager.this.warm(new IndexSearcher(reader)); // 3
			} // 3
		}); // 3
	}

	public void warm(IndexSearcher searcher) throws IOException {
		// E
	}

	private boolean reopening;

	private synchronized void startReopen() // F
			throws InterruptedException {
		while (reopening) {
			wait();
		}
		reopening = true;
	}

	private synchronized void doneReopen() { // G
		reopening = false;
		notifyAll();
	}

	public void maybeReopen() throws InterruptedException, IOException { // H

		startReopen();

		try {
			final IndexSearcher searcher = get();
			try {
				IndexReader newReader = currentSearcher.getIndexReader().reopen(); // I
				if (newReader != currentSearcher.getIndexReader()) { // I
					IndexSearcher newSearcher = new IndexSearcher(newReader); // I
					if (writer == null) { // I
						warm(newSearcher); // I
					} // I
					swapSearcher(newSearcher); // I
				}
			} finally {
				release(searcher);
			}
		} finally {
			doneReopen();
		}
	}

	public synchronized IndexSearcher get() { // J
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	public synchronized void release(IndexSearcher searcher) // K
			throws IOException {
		searcher.getIndexReader().decRef();
		
	}

	private synchronized void swapSearcher(IndexSearcher newSearcher) // L
			throws IOException {
		release(currentSearcher);
		currentSearcher = newSearcher;
	}
}

/*
 * #A Current IndexSearcher #B Create initial searcher by reading Directory 
 * #C Create initial searcher from IndexWriter's reader #D Install ourself as the
 * MergedSegmentWarmer 
 * #E Implement in subclass to warm new searcher 
 * #F Pauses until no other thread is reopening 
 * #G Finish reopen and notify other threads
 * #H Reopen searcher if there are changes 
 * #I Check index version and reopen, * warm, swap if needed 
 * #J Returns current searcher; must be matched with a call * to release 
 * #K Release searcher returned from get() #L Swaps currentSearcher
 * to new searcher
 */