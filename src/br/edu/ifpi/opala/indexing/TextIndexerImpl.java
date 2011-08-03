package br.edu.ifpi.opala.indexing;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import br.edu.ifpi.opala.backup.TextBackuper;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * Implementação da interface TextIndexer
 * 
 * @author Mônica
 */

public class TextIndexerImpl implements TextIndexer {

	private static final Analyzer ANALYZER = new BrazilianAnalyzer(Version.LUCENE_30);
	private final Directory DIRECTORY;
	private static TextIndexerImpl textIndexer = new TextIndexerImpl();
	private IndexWriter writer = null;

	/**
	 * Construtor usado na estruturação do Singleton
	 */
	private TextIndexerImpl() {
		File file = new File(Path.TEXT_INDEX.getValue());
		try {
			DIRECTORY = FSDirectory.open(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Ocorreu um erro ao abri o índice no caminho: "+file.getAbsolutePath());
		}
	}

	/**
	 * Método Singleton para obter apenas uma instância desta classe
	 * 
	 * @return Instâcia ínica da classe
	 */
	public static TextIndexer getTextIndexerImpl() {
		if(textIndexer == null) {
			textIndexer = new TextIndexerImpl();
		}
		return textIndexer;
	}

	/**
	 * Verifica se o id passado já existe no índice
	 * 
	 * @param id Identificador do texto.
	 * @return Retorna <code>true</code> se conseguir encontrar o arquivo
	 *  no índice e <code>false</code> caso contrário.
	 * @throws IOException 
	 */
	private boolean hasIdentifier(String id) throws IOException {
		if (IndexReader.indexExists(DIRECTORY)) {
			IndexReader reader = IndexReader.open(DIRECTORY, true);
			Term term = new Term(Metadata.ID.getValue(), id);
			TermDocs termDocs = reader.termDocs(term);
			while(termDocs.next()) {
				if (!reader.isDeleted(termDocs.doc())) {
					reader.close();
					return true;
				}
			}
			reader.close();
		}
		return false;
	}

	/**
	 * Realiza a adição de texto no índice. Cria um índice caso este ainda não
	 * exista. Caso contrário utiliza o já existente e adiciona o texto ao
	 * mesmo.
	 * 
	 * @param metaDoc Os metadados asssociados ao texto.
	 * @param content O conteúdo do texto a ser indexado.
	 */
	public synchronized ReturnMessage addText(MetaDocument metaDoc, String content) {

		try {
			if (hasIdentifier(metaDoc.getId()))
				return ReturnMessage.DUPLICATED_ID;
			
			
			metaDoc.getDocument().add(new Field(Metadata.CONTENT.getValue(), content,
												Field.Store.YES, Field.Index.ANALYZED));

			writer = new IndexWriter(DIRECTORY, ANALYZER, IndexWriter.MaxFieldLength.UNLIMITED);
			writer.addDocument(metaDoc.getDocument());
			writer.commit();
			writer.close();
		} catch (IOException e) {
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}

		return ReturnMessage.SUCCESS;
	}

	/**
	 * Remove o texto do índice. Busca pelo identificador do texto passado.
	 * 
	 * @param id O identificador ínico do texto no índece.
	 * @return true se o texto foi removido, false se o texto não for encontrado
	 *         e consequentemente não removido.
	 */
	public synchronized ReturnMessage delText(String id) {
		try {
			if (!hasIdentifier(id)) {
				return ReturnMessage.ID_NOT_FOUND;
			}
			
			writer = new IndexWriter(DIRECTORY, ANALYZER, IndexWriter.MaxFieldLength.UNLIMITED);
			writer.deleteDocuments(new Term(Metadata.ID.getValue(), id));
			writer.commit();
			writer.close();
			
			return ReturnMessage.SUCCESS;
			
		} catch (IOException e) {
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}
	}

	/**
	 * Realiza a otimização do índice.
	 */
	public synchronized void optimize() throws IOException {
		writer = new IndexWriter(DIRECTORY, ANALYZER, MaxFieldLength.UNLIMITED);
		writer.optimize();
		writer.commit();
		writer.close();
	}

	
	/**
	 * Sobreescreve os metadados de um documento que possua o id informado com
	 * os recebidos no Map.
	 * 
	 * @param id  Identificador do documento no índice.
	 * 		   metadata Map de metadados com o nome do metadado e seu novo valor.
	 * @return ReturnMessage.SUCCESS se for atualizado com sucesso,
	 * ou ReturnMessage.ID_NOT_FOUND se não for encontrado o id informado
	 */
	public synchronized ReturnMessage updateText(String id,
												Map<String, String> metadata) {
		try {
			IndexReader reader = IndexReader.open(DIRECTORY, true);
			Term term = new Term(Metadata.ID.getValue(), id);
			TermDocs termDocs = reader.termDocs(term);
			
			Document doc = null;
			while(termDocs.next()) {
				if (!reader.isDeleted(termDocs.doc())) {
					doc = reader.document(termDocs.doc());
				}
			}
			
			if (doc == null) {
				reader.close();
				return ReturnMessage.ID_NOT_FOUND;
			}

			for (Map.Entry<String, String> entry : metadata.entrySet()) {
				doc.removeField(entry.getKey());
				doc.add(new Field(	entry.getKey(), 
									entry.getValue(),
									Field.Store.YES,
									Field.Index.ANALYZED));
			}
			reader.close();
			
			writer = new IndexWriter(DIRECTORY, ANALYZER, IndexWriter.MaxFieldLength.UNLIMITED);
			writer.updateDocument(term, doc);
			writer.commit();
			writer.close();
		} catch (IOException e) {
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}
		return ReturnMessage.SUCCESS;
	}
	
	
	/**
	 * Restaura o último último backup, caso o o arquivo opala.conf esteja 
	 * configurado para fazer restauração atumomática.
	 */
	public synchronized void restoreIndex() {
		if(Path.TEXT_INDEX_AUTO_RESTORE.getValue().equals("true")){
			try {
				new TextBackuper().restoreIndex();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
