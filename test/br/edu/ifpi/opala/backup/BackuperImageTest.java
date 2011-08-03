package br.edu.ifpi.opala.backup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import br.edu.ifpi.opala.indexing.ImageIndexer;
import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.utils.Conversor;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

public class BackuperImageTest {
	static MetaDocument metaDocument;
	static BufferedImage image;
	private ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
	private File imageIndex = new File(Path.IMAGE_INDEX.getValue());
	private File imageIndexBackup = new File(Path.IMAGE_BACKUP.getValue());

	@BeforeClass
	public static void setUp() {
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		File imageRepository = new File(
				PathTest.IMAGE_REPOSITORY_TEST.getValue());
		assertTrue(imageRepository.exists());
		assertFalse(imageRepository.listFiles().length == 0);

		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc1");

		try {
			image = Conversor.byteArrayToBufferedImage(Conversor
					.fileToByteArray(new File(PathTest.IMAGE_REPOSITORY_TEST
							.getValue() + "image (6).jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() {
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBackup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBackup));
	}

	 @Test
	 public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeIndexar()
	 throws IOException {
	 assertEquals(ReturnMessage.SUCCESS,
	 indexer.addImage(metaDocument, image));
	 corruptIndex(imageIndex);
	 assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR,
	 indexer.addImage(metaDocument, image));
	 }
	
	 @Test
	 public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeAtualizar()
	 throws IOException {
	
	 assertEquals(ReturnMessage.SUCCESS,
	 indexer.addImage(metaDocument, image));
	 corruptIndex(imageIndex);
	
	 Map<String, String> metadataMap = new HashMap<String, String>();
	 metadataMap.put(Metadata.TITLE.getValue(), "Novo Título");
	 assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR,
	 indexer.updateImage("doc1", metadataMap));
	
	 ImageSearcher searcher = ImageSearcherFactory
	 .createCEDDImageSearcher(10);
	 IndexReader ir;
	
	 Directory dir = FSDirectory.open(new File(Path.IMAGE_INDEX.getValue()));
	 ir = IndexReader.open(dir, true);
	 ImageSearchHits result = searcher.search(image, ir);
	 String titulo = result.doc(0).get(Metadata.TITLE.getValue());
	 ir.close();
	 dir.close();
	
	 assertEquals(metaDocument.getTitle(), titulo);
	 }
	
	 @Test
	 public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeDeletar()
	 throws IOException {
	
	 assertEquals(ReturnMessage.SUCCESS,
	 indexer.addImage(metaDocument, image));
	 corruptIndex(imageIndex);
	
	 assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR,
	 indexer.delImage("doc1"));
	 }
	
	 @Test
	 public void deveriaRestaurarOBackupDepoisDeCorrompidoAoInvesDeIndexar()
	 throws IOException {
	 assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocument,
	 image));
	 corruptIndex(imageIndexBackup);
	
	 MetaDocument metaDocument = new MetaDocument();
	 metaDocument.setTitle("Documento de teste pro backup");
	 metaDocument.setId("doc2");
	
	 assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
	 indexer.addImage(metaDocument, image));
	 }
	
	 @Test
	 public void deveriaRestaurarOBackupDepoisDeCorrompidoAoInvesDeDeletar()
	 throws IOException {
	 assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocument,
	 image));
	 corruptIndex(imageIndexBackup);
	
	 assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
	 indexer.delImage("doc1"));
	 }
	

	
	@Test
	public void deveriaAtualizarOBackupAoVerificarDiferencasDeTamanhoDosIndices()
			throws IOException {
		considerandoQueNadaFoiIndexadoNoIndiceDeBackup();
		MetaDocument metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste pro backup");
		metaDocument.setId("doc2");

		assertEquals(ReturnMessage.OUTDATED,
				indexer.addImage(metaDocument, image));
	}


	public void considerandoQueNadaFoiIndexadoNoIndiceDeBackup()
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		IndexWriter writer = null;
		IndexWriter backupWriter = null;
		
		DocumentBuilder builder = DocumentBuilderFactory
				.getCEDDDocumentBuilder();
		Document doc = builder.createDocument(image, metaDocument.getId());
		List<Fieldable> fields = doc.getFields();
		for (Fieldable field : fields) {
			metaDocument.getDocument().add(field);
		}

		writer = new IndexWriter(FSDirectory.open(imageIndex),
				new BrazilianAnalyzer(Version.LUCENE_30),
				IndexWriter.MaxFieldLength.UNLIMITED);

		writer.addDocument(metaDocument.getDocument());
		writer.close();

		backupWriter = new IndexWriter(FSDirectory.open(imageIndexBackup),
				new BrazilianAnalyzer(Version.LUCENE_30),
				IndexWriter.MaxFieldLength.UNLIMITED);

	//	backupWriter.addDocument(metaDocument.getDocument());
		backupWriter.close();
	}

	private void corruptIndex(File index) throws IOException {
		File[] indexFiles = index.listFiles();
		for (File file : indexFiles) {
			FileWriter fw = new FileWriter(file);
			fw.write("oadsiajdu110gajoga");
			fw.close();
		}
	}
}
