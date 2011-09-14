package br.edu.ifpi.opala.indexing;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.ifpi.opala.utils.IndexManager;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.MetaDocumentBuilder;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

public class NearRealTimeTextIndexerTest {

	File path = new File(Path.TEXT_INDEX.getValue());
	File pathBackup = new File(Path.TEXT_BACKUP.getValue());
	private String CONTENT = "Conteúdo do document a ser indexado nos testes";
	private MetaDocument METADOC = new MetaDocumentBuilder().id("1")
			.title("Título do documento de teste")
			.author("Autor do documento de teste").build();
	private IndexManager indexManager;

	@Before
	public void setUpNewIndex() throws IOException {
		Util.deleteDir(path);
		Util.deleteDir(pathBackup);
		// Directory indexDir = FSDirectory.open(path);
		Directory indexDir = new SimpleFSDirectory(path);
		indexManager = new IndexManager(indexDir);
	}

	@After
	public void tearDown() throws CorruptIndexException, IOException {
		indexManager.close();
	}

//	@Test
//	public void deveriaCriarVariosIndicesDistintos() throws IOException {
//
//		InputStream is;
//		String CONTENT;
//		MetaDocument METADOC;
//
//		is = new FileInputStream(new File("./sample-data/texts/alice.txt"));
//		CONTENT = new TxtParser().getContent(is);
//		is.close();
//
//		METADOC = new MetaDocumentBuilder().id("1")
//				.title("Título do documento de teste")
//				.author("Autor do documento de " + "" + "" + "teste").build();
//		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);
//		for (int i = 0; i < 100; i++) {
//
//			// quando
//		ReturnMessage message = indexer.addText(METADOC, CONTENT);
//
//			//System.out.println(i);
//		}
//
//
//	}

	@Test
	public void deveriaIndexarUmDocumentoDeTexto() throws IOException,
			InterruptedException {
		// dado
		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);

		// quando
		ReturnMessage message = indexer.addText(METADOC, CONTENT);

		// entao
		assertEquals(ReturnMessage.SUCCESS, message);
		assertEquals(1, totalDocs(indexManager));
	}

	@Test
	public void deveriaDevolverDuplicatedIdQuandoTentoIndexarUmDocumentoDuasVezes()
			throws IOException, InterruptedException {
		// dado

		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);

		// quando
		indexer.addText(METADOC, CONTENT);
		ReturnMessage message = indexer.addText(METADOC, CONTENT);

		// entao
		assertEquals(ReturnMessage.DUPLICATED_ID, message);
		assertEquals(1, totalDocs(indexManager));
	}

	@Test
	public void deveriaAtualizarUmDocumento() throws IOException,
			InterruptedException {
		// dado

		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");

		// quando
		indexer.addText(METADOC, CONTENT);
		ReturnMessage message = indexer
				.updateText(METADOC.getId(), metadataMap);

		// entao
		assertEquals(ReturnMessage.SUCCESS, message);
		assertEquals(1, totalDocs(indexManager));
	}

	@Test
	public void deveriaDevolverIdNotFoundQuantoTentaAtualizaUmDocumentoInexistente()
			throws IOException {
		// dado

		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);

		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Título atualizado");

		// quando
		ReturnMessage message = indexer.updateText("id inexistente no indice",
				metadataMap);

		// entao
		assertEquals(ReturnMessage.ID_NOT_FOUND, message);
	}

	@Test
	public void deveriaDeletarUmDocumento() throws IOException,
			InterruptedException {
		// dado

		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);
		assertEquals(ReturnMessage.SUCCESS, indexer.addText(METADOC, CONTENT));

		// quando
		ReturnMessage message = indexer.delText(METADOC.getId());

		// entao
		assertEquals(ReturnMessage.SUCCESS, message);
		assertEquals(0, totalDocs(indexManager));
	}

	@Test
	public void deveriaRetornarIdNotFoundQuandoTentaDeletarUmDocumentoInexistente()
			throws IOException {
		// dado

		TextIndexer indexer = new NearRealTimeTextIndexer(indexManager);

		// quando
		ReturnMessage message = indexer.delText("id inexistente no indice");

		// entao
		assertEquals(ReturnMessage.ID_NOT_FOUND, message);
	}

	private int totalDocs(IndexManager manager) throws IOException,
			InterruptedException {
		IndexSearcher searcher = manager.getSearcher();
		int total;
		try {
			total = searcher.getIndexReader().numDocs();
		} finally {
			manager.release(searcher);
		}
		return total;
	}

}
