package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import br.edu.ifpi.opala.backup.TextBackuper;

public class IndexManager {

	public static final Analyzer ANALYZER = new BrazilianAnalyzer(
			Version.LUCENE_30);
	
	private IndexDeletionPolicy policy = new KeepOnlyLastCommitDeletionPolicy();
	private SnapshotDeletionPolicy snapshotter = new SnapshotDeletionPolicy(policy);
	
	private static IndexDeletionPolicy policyBackup = new KeepOnlyLastCommitDeletionPolicy();
	private static SnapshotDeletionPolicy snapshotterBackup = new SnapshotDeletionPolicy(policyBackup);
	
	private Directory indexDirectory;
	private IndexWriter indexWriter;
	private IndexWriter backupWriter;
	private SearcherManager searcherManager;
	private SearcherManager searcherManagerBackup;
	
	public IndexManager(Directory indexDirectory) throws IOException, IllegalStateException {
		this.indexDirectory = indexDirectory;

		try {
			indexWriter = new IndexWriter(indexDirectory, ANALYZER,	snapshotter, MaxFieldLength.UNLIMITED);
			searcherManager = new SearcherManager(indexWriter);
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(Path.TEXT_INDEX.getValue()));
			restoreIndex();
		} catch (IOException e) {
			System.out.println("indexManager indice");
			Util.deleteDir(new File(Path.TEXT_INDEX.getValue()));
			restoreIndex();
		}

		try {
			File backupDirectory = new File(Path.TEXT_BACKUP.getValue());
			backupWriter = new IndexWriter(new SimpleFSDirectory(backupDirectory), ANALYZER, snapshotterBackup,
					MaxFieldLength.UNLIMITED);
			searcherManagerBackup = new SearcherManager(backupWriter);
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(Path.TEXT_BACKUP.getValue()));
			updateBackup();
		} catch (IOException e) {
			System.out.println("indexManager");
			Util.deleteDir(new File(Path.TEXT_BACKUP.getValue()));
			updateBackup();
		}
	}

	public IndexSearcher getSearcher() throws IOException {
		refresh();
		return searcherManager.get();
	}

	public IndexSearcher getSearcherBackup() throws IOException {
		refreshBackup();
		return searcherManagerBackup.get();
	}

	public IndexWriter getWriter() {
		return indexWriter;
	}

	public IndexWriter getBackupWriter() {
		return backupWriter;
	}
	
	public Directory getDirectory() {
		return indexDirectory;
	}

	public void release(IndexSearcher searcher) throws IOException {
		searcherManager.release(searcher);
	}

	public void releaseBackup(IndexSearcher searcher) throws IOException {
		searcherManager.release(searcher);
	}

	public void refresh() throws IOException {
		try {
			searcherManager.maybeReopen();
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"Erro ao tentar reabrir o índice com as ultimas indexações.",
					e);
		}
	}

	public void refreshBackup() throws IOException {
		try {
			searcherManagerBackup.maybeReopen();
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"Erro ao tentar reabrir o índice de backup com as ultimas indexações.",
					e);
		}
	}

	public static void snapshotterBackup() {
		snapshotterBackup.snapshot();
	}

	public static void releaseSnapshotterBackup() {
		snapshotterBackup.release();
	}

	public void close() throws CorruptIndexException, IOException,
			IllegalStateException {
		indexWriter.close();
		backupWriter.close();
	}

	public synchronized void restoreIndex() {
		if (Path.TEXT_INDEX_AUTO_RESTORE.getValue().equals("true")) {
			try {

				try {
					snapshotterBackup.snapshot();
					new TextBackuper().restoreIndex();
				} finally {
					snapshotterBackup.release();
				}
			} catch (IOException e) {
				e.getMessage();
			}
		}
	}

	public synchronized void updateBackup() {
		try {
			try {
				snapshotter.snapshot();
				new TextBackuper().updateBackup();
			} finally {
				snapshotter.release();
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}

}
