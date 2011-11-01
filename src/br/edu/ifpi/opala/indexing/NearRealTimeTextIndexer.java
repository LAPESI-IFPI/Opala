package br.edu.ifpi.opala.indexing;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.IndexSearcher;

import br.edu.ifpi.opala.utils.IndexManager;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Implementação da interface de indexação que utiliza um {@link IndexManager}
 * para obter as referências para o índice aberto.
 * 
 * 
 * @author aecio
 * 
 */

public class NearRealTimeTextIndexer implements TextIndexer {

	private static String TEXT_INDEX = Path.TEXT_INDEX.getValue();
	private static String TEXT_BACKUP = Path.TEXT_BACKUP.getValue();
	private final IndexManager indexManager;
	private static int cont = 0;
	
	public NearRealTimeTextIndexer(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	private boolean existsIdentifier(String id) throws IOException {
		return getDocumentByIdentifier(id) == null ? false : true;
	}

	private boolean existsIdentifierBackup(String id) throws IOException {
		return getDocumentByIdentifierBackup(id) == null ? false : true;
	}

	private Document getDocumentByIdentifier(String id) throws IOException {
		IndexSearcher searcher = null;
		try {
			searcher = indexManager.getSearcher();
			IndexReader indexReader = searcher.getIndexReader();

			Term term = new Term(Metadata.ID.getValue(), id);
			TermDocs termDocs = indexReader.termDocs(term);
			while (termDocs.next()) {
				int docUID = termDocs.doc();
				if (!indexReader.isDeleted(docUID)) {
					return indexReader.document(docUID);
				}
			}

		} finally {
			if (searcher != null)
				indexManager.release(searcher);
		}
		return null;

	}

	private Document getDocumentByIdentifierBackup(String id)
			throws IOException {
		IndexSearcher searcher = null;
		try {
			searcher = indexManager.getSearcherBackup();
			IndexReader indexReader = searcher.getIndexReader();

			Term term = new Term(Metadata.ID.getValue(), id);
			TermDocs termDocs = indexReader.termDocs(term);
			while (termDocs.next()) {
				int docUID = termDocs.doc();
				if (!indexReader.isDeleted(docUID)) {
					return indexReader.document(docUID);
				}
			}
		} finally {
			indexManager.releaseBackup(searcher);
		}
		return null;
	}

	public synchronized ReturnMessage addText(MetaDocument metaDocument, String content) {
		long numDocsBackup;
		long numDocsIndex;
		
		try {

			if (existsIdentifier(metaDocument.getId())) {
				return ReturnMessage.DUPLICATED_ID;
			}

			metaDocument.getDocument().add(
					new Field(Metadata.CONTENT.getValue(), content,
							Field.Store.YES, Field.Index.ANALYZED));

			IndexWriter writer = indexManager.getWriter();
			writer.addDocument(metaDocument.getDocument());
			writer.prepareCommit();
			writer.commit();
			numDocsIndex = writer.numDocs();
			
		} catch (NullPointerException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}
		
		try {
			if (existsIdentifierBackup((metaDocument.getId()))) {			
			}
				IndexWriter backup = indexManager.getBackupWriter();
				backup.addDocument(metaDocument.getDocument());
				backup.prepareCommit();
				backup.commit();
				numDocsBackup = backup.numDocs();
			
		} catch (NullPointerException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		}
		
		cont++;
		if(cont == 5){
			if (numDocsIndex > numDocsBackup) {
				indexManager.updateBackup();
				return ReturnMessage.OUTDATED;
			}
		cont = 0;
		}
		

		return ReturnMessage.SUCCESS;
	}

	/**
	 * Remove um documento do índice que contém o <code>id</code> recebido.
	 * Retorna {@link ReturnMessage}.SUCESS se o documento foi removido ou
	 * {@link ReturnMessage}.ID_NOT_FOUND se não existe documento com o
	 * <code>id</code> recebido no índice.
	 * 
	 * @param id
	 *            O identificador do documento a ser removido.
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public ReturnMessage delText(String id) {
		try {
			if (!existsIdentifier(id)) {
				return ReturnMessage.ID_NOT_FOUND;

			}

			IndexWriter writer = indexManager.getWriter();
			writer.deleteDocuments(new Term(Metadata.ID.getValue(), id));
			writer.optimize();
			writer.prepareCommit();
			writer.commit();
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}

		try {
			if (existsIdentifierBackup(id)) {
				IndexWriter backup = indexManager.getBackupWriter();
				backup.deleteDocuments(new Term(Metadata.ID.getValue(), id));
				backup.optimize();
				backup.prepareCommit();
				backup.commit();
			}

		} catch (NullPointerException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			e.printStackTrace();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(TEXT_BACKUP));
			indexManager.updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		}

		
		return ReturnMessage.SUCCESS;

	}

	/**
	 * Modifica os metadados do documento que possui o <code>id</code>.
	 * 
	 * @throws IOException
	 * 
	 * 
	 */
	public ReturnMessage updateText(String id, Map<String, String> metadata) {

		Term term = new Term(Metadata.ID.getValue(), id);
		try {
			Document doc = getDocumentByIdentifier(id);
			if (doc == null) {
				return ReturnMessage.ID_NOT_FOUND;
			}

			for (Map.Entry<String, String> entry : metadata.entrySet()) {
				doc.removeField(entry.getKey());
				doc.add(new Field(entry.getKey(), entry.getValue(),
						Field.Store.YES, Field.Index.ANALYZED));
			}

			IndexWriter writer = indexManager.getWriter();
			writer.updateDocument(term, doc);
			writer.optimize();
			writer.prepareCommit();
			writer.commit();
			
			try {
				Document docBackup = getDocumentByIdentifierBackup(id);
				if (docBackup != null) {
					for (Map.Entry<String, String> entry : metadata.entrySet()) {
						docBackup.removeField(entry.getKey());
						docBackup.add(new Field(entry.getKey(), entry.getValue(),
								Field.Store.YES, Field.Index.ANALYZED));
					}

					IndexWriter backup = indexManager.getBackupWriter();
					backup.updateDocument(term, docBackup);
					backup.optimize();
					backup.prepareCommit();
					backup.commit();
				}				

			} catch (NullPointerException e) {
				Util.deleteDir(new File(TEXT_BACKUP));
				indexManager.updateBackup();
				return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
			} catch (CorruptIndexException e) {
				Util.deleteDir(new File(TEXT_BACKUP));
				indexManager.updateBackup();
				return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
			} catch (IOException e) {
				Util.deleteDir(new File(TEXT_BACKUP));
				indexManager.updateBackup();
				return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
			}

		} catch (NullPointerException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(TEXT_INDEX));
			indexManager.restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} 
		
		return ReturnMessage.SUCCESS;
	}

}
