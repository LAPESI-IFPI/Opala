package br.edu.ifpi.opala.backup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.edu.ifpi.opala.indexing.NearRealTimeTextIndexer;
import br.edu.ifpi.opala.indexing.TextIndexer;
import br.edu.ifpi.opala.utils.IndexManager;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.MetaDocumentBuilder;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

public class BackuperTextTest {
	private static IndexManager indexManager;
	private static MetaDocument metaDocument;
	private static TextIndexer indexer;
	private static File textIndex = new File(Path.TEXT_INDEX.getValue());
	private static File textIndexBackup = new File(Path.TEXT_BACKUP.getValue());

	InputStream is;
	static String CONTENT;
	MetaDocument METADOC;

	@BeforeClass
	public static void setUp() throws IOException {
		assertTrue(Util.deleteDir(textIndex));
		assertTrue(Util.deleteDir(textIndexBackup));
		CONTENT = "Conteúdo do document a ser indexado nos testes";
		metaDocument = new MetaDocumentBuilder().id("1")
				.title("Título do documento de teste")
				.author("Autor do documento de teste").build();

		Directory indexDir = new SimpleFSDirectory(textIndex);
		indexManager = new IndexManager(indexDir);
		indexer = new NearRealTimeTextIndexer(indexManager);
		assertEquals(ReturnMessage.SUCCESS,
				indexer.addText(metaDocument, CONTENT));

	}

	@AfterClass
	public static void tearDown() throws CorruptIndexException, IllegalStateException,
			IOException {
		indexManager.close();
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeIndexar()
			throws IOException {
		System.out.println(metaDocument.getId());
		corruptIndex(textIndexBackup);
		MetaDocument metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste pro backup");
		metaDocument.setId("doc2");
		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
				indexer.addText(metaDocument, CONTENT));
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeDeletar()
			throws IOException {
		corruptIndex(textIndexBackup);

		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
				indexer.delText("1"));
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeAtualizar()
			throws IOException {
		assertEquals(ReturnMessage.SUCCESS,indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndexBackup);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");
		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
				indexer.updateText(metaDocument.getId(), metadataMap));

		assertEquals(metaDocument.getTitle(), "Título do documento de teste");
	}
	
	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeIndexar()
			throws IOException {
		corruptIndex(textIndex);
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.addText(metaDocument, CONTENT));

		}

	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeAtualizar()
			throws IOException, InterruptedException {

		
		corruptIndex(textIndex);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR,
				indexer.updateText(metaDocument.getId(), metadataMap));

		assertEquals(metaDocument.getTitle(), "Título do documento de teste");
	}

	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeDeletar()	throws IOException {

		corruptIndex(textIndex);

		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.delText("1"));
	}

	private void corruptIndex(File index) throws IOException {
		File[] indexFiles = index.listFiles();
		for (File file : indexFiles) {
			if (!file.getName().equals("write.lock")) {
			FileWriter fw = new FileWriter(file);
			fw.write("oadsiwe");
			fw.close();
			}
		}
	}
}
