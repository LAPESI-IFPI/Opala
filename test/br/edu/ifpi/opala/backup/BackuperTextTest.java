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
import org.junit.After;
import org.junit.Before;
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
	private IndexManager indexManager;
	private  MetaDocument metaDocument;
	private TextIndexer indexer;
	private  File textIndex = new File(Path.TEXT_INDEX.getValue());
	private  File textIndexBackup = new File(Path.TEXT_BACKUP.getValue());
	
	InputStream is;
	String CONTENT;
	MetaDocument METADOC;
	
	@Before
	public void setUp() throws IOException {

		assertTrue(Util.deleteDir(textIndex));
		assertTrue(Util.deleteDir(textIndexBackup));

		CONTENT = "Conteúdo do document a ser indexado nos testes";
		metaDocument = new MetaDocumentBuilder().id("1")
				.title("Título do documento de teste")
				.author("Autor do documento de teste").build();

		Directory indexDir = new SimpleFSDirectory(textIndex);
		indexManager = new IndexManager(indexDir);
		indexer = new NearRealTimeTextIndexer(indexManager);
		
		
	}

	@After
	public void tearDown() throws CorruptIndexException, IllegalStateException, IOException {
		indexManager.close();
	}

	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeIndexar()
			throws IOException {
		assertEquals(ReturnMessage.SUCCESS,	indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndex);
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.addText(metaDocument, CONTENT));
		
		MetaDocument metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste pro backup");
		metaDocument.setId("doc2");
		assertEquals(ReturnMessage.SUCCESS,	indexer.addText(metaDocument, CONTENT));
		
		corruptIndex(textIndex);
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.delText("doc2"));
		assertEquals(ReturnMessage.SUCCESS, indexer.delText("doc2"));
		assertEquals(ReturnMessage.SUCCESS,	indexer.addText(metaDocument, CONTENT));
		
		corruptIndex(textIndex);
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.addText(metaDocument, CONTENT));
	}

	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeAtualizar()
			throws IOException, InterruptedException {

		assertEquals(ReturnMessage.SUCCESS,
				indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndex);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");
		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR,
				indexer.updateText(metaDocument.getId(), metadataMap));

		assertEquals(metaDocument.getTitle(), "Título do documento de teste");
	}

	@Test
	public void deveriaRestaurarOIndiceDepoisDeCorrompidoAoInvesDeDeletar()	throws IOException {

		assertEquals(ReturnMessage.SUCCESS,
				indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndex);

		assertEquals(ReturnMessage.UNEXPECTED_INDEX_ERROR, indexer.delText("1"));
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeIndexar()
			throws IOException {
		assertEquals(ReturnMessage.SUCCESS,	indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndexBackup);

		MetaDocument metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste pro backup");
		metaDocument.setId("doc2");
		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,	indexer.addText(metaDocument, CONTENT));
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeDeletar()
			throws IOException {
		assertEquals(ReturnMessage.SUCCESS,	indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndexBackup);

		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
				indexer.delText("1"));
	}

	@Test
	public void deveriaAtualizarOBackupDepoisDeCorrompidoAoInvesDeAtualizar()
			throws IOException {
		assertEquals(ReturnMessage.SUCCESS,
				indexer.addText(metaDocument, CONTENT));
		corruptIndex(textIndexBackup);
		
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");
		assertEquals(ReturnMessage.UNEXPECTED_BACKUP_ERROR,
				indexer.updateText(metaDocument.getId(), metadataMap));

		assertEquals(metaDocument.getTitle(), "Título do documento de teste");
	}

	private void corruptIndex(File index) throws IOException {
		File[] indexFiles = index.listFiles();
		for (File file : indexFiles) {
			FileWriter fw = new FileWriter(file);
			fw.write("oadsiwe");
			fw.close();
		}
	}
}
