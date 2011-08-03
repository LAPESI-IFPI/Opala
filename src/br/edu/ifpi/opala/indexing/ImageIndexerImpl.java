package br.edu.ifpi.opala.indexing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import br.edu.ifpi.opala.backup.ImageBackuper;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Classe responsável pela indexação da imagem (processo que consiste na adição,
 * remoção e/ou atualização da imagem no índice).
 * 
 * @author <a href="mailto:paduafil@gmail.com">Pádua Furtado</a>
 * 
 */
public class ImageIndexerImpl implements ImageIndexer {

	private static String IMAGE_INDEX = Path.IMAGE_INDEX.getValue();
	private static String IMAGE_BACKUP = Path.IMAGE_BACKUP.getValue();
	private static int cont = 0;
	private IndexWriter writer = null;
	private IndexWriter backupWriter = null;

	private static ImageIndexerImpl imageIndexer = new ImageIndexerImpl();

	/**
	 * Construtor que seta o tempo máximo que o índice poderá ficar aberto
	 * Utilizado na estruturação do Singleton
	 */
	private ImageIndexerImpl() {
		IndexWriter.setDefaultWriteLockTimeout(Long.MAX_VALUE);
	}

	/**
	 * Método para obter uma instância desta classe
	 * 
	 * @return uma instância única desta classe
	 */
	public static ImageIndexer getImageIndexerImpl() {
		return imageIndexer;
	}

	/**
	 * Verifica se o id passado já existe no índice
	 * 
	 * @param id
	 *            - Identificados da imagem
	 * @return Retorna True se consegui encontra o arquivo no índice. False caso
	 *         contrário.
	 * @throws IOException
	 */
	private boolean hasIdentifier(String id) throws IOException {
		IndexReader reader;
		Directory dir = null;
		dir = FSDirectory.open(new File(IMAGE_INDEX));

		if (IndexReader.indexExists(dir)) {

			reader = IndexReader.open(dir, true);

			for (int i = 0; i < reader.maxDoc(); i++) {
				if (!reader.isDeleted(i)) {
					if (reader.document(i)
							.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER)
							.stringValue().equals(id)) {
						reader.close();
						return true;
					}
				}
			}
			reader.close();
		}
		return false;
	}

	/**
	 * Realiza a adição da imagem no índice. Cria um índice caso este ainda não
	 * exista, caso contrário utiliza o já existente e adiciona a imagem ao
	 * mesmo.
	 * 
	 * @param metaDocument
	 *            metadados asssociados à imagem.
	 * @param image
	 *            conteúdo da imagem a ser indexada
	 */
	public synchronized ReturnMessage addImage(MetaDocument metaDocument,
			BufferedImage image) {
		long numDocsBackup;
		long numDocsIndex;
		
		try {
			if (image == null) {
				return ReturnMessage.PARAMETER_INVALID;
			}
			
			if (hasIdentifier(metaDocument.getId())) {
			//	return ReturnMessage.DUPLICATED_ID;
			}

			DocumentBuilder builder = DocumentBuilderFactory
					.getCEDDDocumentBuilder();
			Document doc = builder.createDocument(image, metaDocument.getId());
			List<Fieldable> fields = doc.getFields();

			for (Fieldable field : fields) {
				metaDocument.getDocument().add(field);
			}

			writer = new IndexWriter(FSDirectory.open(new File(IMAGE_INDEX)),
					new BrazilianAnalyzer(Version.LUCENE_30),
					IndexWriter.MaxFieldLength.UNLIMITED);

			writer.addDocument(metaDocument.getDocument());
			numDocsIndex = writer.numDocs();
			writer.close();
			System.out.println("add image");
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}

		try {
			backupWriter = new IndexWriter(FSDirectory.open(new File(
					IMAGE_BACKUP)), new BrazilianAnalyzer(Version.LUCENE_30),
					IndexWriter.MaxFieldLength.UNLIMITED);

			backupWriter.addDocument(metaDocument.getDocument());
			numDocsBackup = backupWriter.numDocs();
			backupWriter.close();
			System.out.println("add image backup");
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(IMAGE_BACKUP));
			updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(IMAGE_BACKUP));
			updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		}
		
		cont++;
		if(cont == 15){
			if (numDocsIndex > numDocsBackup) {
					updateBackup();
					System.out.println("backup de imagem desatualizado");
					return ReturnMessage.OUTDATED;
			}
		cont = 0;
		}

		return ReturnMessage.SUCCESS;

	}

	/**
	 * Remove a imagem do índice. Busca pelo identificador da imagem passado.
	 * 
	 * @param id
	 *            identificador único da imagem no índice.
	 * @return <code>true</code> se a imagem foi removida, false se a imagem não
	 *         foi encontrada e consequentemente não removida.
	 */
	public synchronized ReturnMessage delImage(String id) {
		IndexReader reader;
		Directory dir = null;
		IndexReader readerBackup;
		Directory dirBackup = null;
		try {

			if (!hasIdentifier(id)) {
				return ReturnMessage.ID_NOT_FOUND;
			}

			dir = FSDirectory.open(new File(IMAGE_INDEX));
			reader = IndexReader.open(dir, false);

			for (int i = 0; i < reader.maxDoc(); i++) {
				if (!reader.isDeleted(i)) {
					if (reader.document(i)
							.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER)
							.stringValue().equals(id)) {
						reader.deleteDocument(i);
					}
				}
			}
			reader.close();
			dir.close();
		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}

		try {
			dirBackup = FSDirectory.open(new File(IMAGE_BACKUP));
			readerBackup = IndexReader.open(dirBackup, false);

			for (int i = 0; i < readerBackup.maxDoc(); i++) {
				if (!readerBackup.isDeleted(i)) {
					if (readerBackup.document(i)
							.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER)
							.stringValue().equals(id)) {
						readerBackup.deleteDocument(i);
					}
				}
			}
			readerBackup.close();
			dirBackup.close();
			return ReturnMessage.SUCCESS;

		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(IMAGE_BACKUP));
			updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(IMAGE_BACKUP));
			updateBackup();
			return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
		}

	}

	/**
	 * Realiza a otimização do índice
	 */
	public synchronized void optimize() throws IOException {
		Directory dir = FSDirectory.open(new File(IMAGE_INDEX));
		IndexWriter writer = new IndexWriter(dir, new BrazilianAnalyzer(
				Version.LUCENE_30), IndexWriter.MaxFieldLength.UNLIMITED);
		writer.optimize();
		writer.close();

		Directory dirBackup = FSDirectory.open(new File(IMAGE_BACKUP));
		IndexWriter writerBackup = new IndexWriter(dirBackup,
				new BrazilianAnalyzer(Version.LUCENE_30),
				IndexWriter.MaxFieldLength.UNLIMITED);

		writerBackup.optimize();
		writerBackup.close();
	}

	/**
	 * Sobreescreve os metadados de um documento que possua o id informado com
	 * os recebidos em <code>metadata</code>
	 * 
	 * @param id
	 *            identificador do documento no índice
	 * @param metadata
	 *            objeto {@link Map} de metadados com o nome do metadado e seu
	 *            novo valor
	 * @return {@link ReturnMessage#SUCCESS} se for atualizado com sucesso, ou
	 *         {@link ReturnMessage#ID_NOT_FOUND} se não for encontrado o id
	 *         informado
	 */
	public synchronized ReturnMessage updateImage(String id,
			Map<String, String> metadata) {
		if (id.equals("") || metadata.containsKey("")) {
			return ReturnMessage.PARAMETER_INVALID;
		}
		try {
			Directory dir = FSDirectory.open(new File(IMAGE_INDEX));
			IndexSearcher is = new IndexSearcher(dir, false);	

			Query idQuery = new TermQuery(new Term(Metadata.ID.getValue(), id));
			TopDocs hits = is.search(idQuery, 5);
			if (hits.totalHits == 0	|| !is.doc(hits.scoreDocs[0].doc).get(Metadata.ID.getValue()).equals(id)) {
				is.close();
				dir.close();
				return ReturnMessage.ID_NOT_FOUND;
			}

			Document doc = is.doc(hits.scoreDocs[0].doc);
			for (Map.Entry<String, String> entry : metadata.entrySet()) {
				doc.removeField(entry.getKey());
				doc.add(new Field(entry.getKey(), entry.getValue(),
						Field.Store.YES, Field.Index.ANALYZED));
			}
			is.close();

			if (!this.delImage(id).equals(ReturnMessage.SUCCESS)) {
				return ReturnMessage.ID_NOT_FOUND;
			}

			IndexWriter writer = new IndexWriter(dir,
					new BrazilianAnalyzer(Version.LUCENE_30),
					IndexWriter.MaxFieldLength.UNLIMITED);
			writer.addDocument(doc);
			writer.close();
			System.out.println("atualizou opala");
			
			try {
				Directory dirBackup = FSDirectory.open(new File(IMAGE_BACKUP));
				IndexSearcher isBackup = new IndexSearcher(dirBackup, false);
			
				Document docBackup = isBackup.doc(hits.scoreDocs[0].doc);
				for (Map.Entry<String, String> entry : metadata.entrySet()) {
					docBackup.removeField(entry.getKey());
					docBackup.add(new Field(entry.getKey(), entry.getValue(),
							Field.Store.YES, Field.Index.ANALYZED));
				}

				isBackup.close();

				IndexWriter writerBackup = new IndexWriter(dirBackup,
						new BrazilianAnalyzer(Version.LUCENE_30),
						IndexWriter.MaxFieldLength.UNLIMITED);

				writerBackup.addDocument(docBackup);
				writerBackup.close();

			} catch (CorruptIndexException e) {
				Util.deleteDir(new File(IMAGE_BACKUP));
				updateBackup();
				return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
			} catch (IOException e) {
				Util.deleteDir(new File(IMAGE_BACKUP));
				updateBackup();
				return ReturnMessage.UNEXPECTED_BACKUP_ERROR;
			}

		} catch (CorruptIndexException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		} catch (IOException e) {
			Util.deleteDir(new File(IMAGE_INDEX));
			restoreIndex();
			return ReturnMessage.UNEXPECTED_INDEX_ERROR;
		}
		return ReturnMessage.SUCCESS;
	}

	public synchronized void restoreIndex() {
		if (Path.IMAGE_INDEX_AUTO_RESTORE.getValue().equals("true")) {
			try {
				new ImageBackuper().restoreIndex();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void updateBackup() {
		try {
			new ImageBackuper().updateBackup();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}