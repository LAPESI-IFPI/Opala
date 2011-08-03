package br.edu.ifpi.opala.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import util.UtilForTest;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * 
 * Teste unitários da classe TextIndexerImpl
 * 
 * @author Padua e Mônica
 * @see TextIndexerImpl
 * 
 */
public class TextIndexingTest {

	private static final BrazilianAnalyzer BRAZILIAN_ANALYZER = new BrazilianAnalyzer(Version.LUCENE_30);
	static MetaDocument metaDocument;
	private TextIndexer indexer = TextIndexerImpl.getTextIndexerImpl();

	/**
	 * Teste prepara o índice que será usado no resto da classe.
	 */
	@BeforeClass
	public static void indexTexts() {
		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc1");
		assertTrue(Util.deleteDir(new File(Path.TEXT_INDEX.getValue())));
	}

	/**
	 * Testa os retornos esperados do método addText com Sucesso
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws CorruptIndexException 
	 */
	@Test
	public void testAddText() throws CorruptIndexException, IllegalStateException, IOException {
		
		assertEquals(ReturnMessage.SUCCESS,
					 indexer.addText(metaDocument, "documento 1 aqui"));
		
		assertEquals(ReturnMessage.DUPLICATED_ID,
					 indexer.addText(metaDocument, "documento 1 aqui"));
	}

	/**
	 * Testa os retornos esperados do método updateText com Sucesso
	 */
	@Test
	public void testUpdate() throws CorruptIndexException, IOException,
	ParseException {
		
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Novo título");
		
		ReturnMessage rm = indexer.updateText("doc1", metadataMap);
		
		assertEquals(ReturnMessage.SUCCESS, rm);
		
		Directory dir = FSDirectory.open(new File(Path.TEXT_INDEX.getValue()));
		IndexSearcher is = new IndexSearcher(dir, true);
		
		TopDocs hits = is.search(new QueryParser(Version.LUCENE_30,
												Metadata.CONTENT.getValue(),
												BRAZILIAN_ANALYZER).parse("documento"), 10);
		assertEquals(1, hits.totalHits);
		Document doc = is.doc(hits.scoreDocs[0].doc);
		assertEquals("Novo título", doc.get(Metadata.TITLE.getValue()));
		is.close();
	}
	
	/**
	 * Testa os retornos esperados do método updateText sem informa um texto correto.
	 */
	@Test
	public void testUpdateInexistente() throws CorruptIndexException, IOException,
	ParseException {
		
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Novo título");
		ReturnMessage rm = indexer.updateText("ddafoc1", metadataMap);
		assertEquals(ReturnMessage.ID_NOT_FOUND, rm);
		Directory dir = FSDirectory.open(new File(Path.TEXT_INDEX.getValue()));
		IndexSearcher is = new IndexSearcher(dir, true);
		TopDocs hits = is.search(new QueryParser(Version.LUCENE_30, Metadata.CONTENT.getValue(),
				BRAZILIAN_ANALYZER).parse("documento"), 10);
		assertEquals(1,hits.totalHits);
		Document doc = is.doc(hits.scoreDocs[0].doc);
		is.close();
		assertEquals("Documento de teste", doc.get(Metadata.TITLE.getValue()));
	}
	
	/**
	 * Testa os retornos esperados do método delText com Sucesso
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	@Test
	public void testDelText() throws CorruptIndexException, IOException {
		assertEquals(indexer.delText("doc1"), ReturnMessage.SUCCESS);
		assertEquals(indexer.delText("doc1"), ReturnMessage.ID_NOT_FOUND);
	}
	
	/**
	 * Testa os retornos esperados do método delText com Sucesso
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	@Test
	public void testDelTextInexistente() throws CorruptIndexException, IOException {
		assertEquals(indexer.delText("rsre"), ReturnMessage.ID_NOT_FOUND);
	}
	
	/**
	 * Método que garante que o índice não esta sendo utilizado
	 */
	@After
	public void tearDown() throws IOException {
		assertTrue(Util.deleteDir(new File(Path.TEXT_INDEX.getValue())));
		assertTrue(UtilForTest.indexTextDirOrFile(new File(PathTest.TEXT_REPOSITORY_TEST.getValue())));
		assertEquals(indexer.addText(metaDocument, "documento 1 aqui"),
				ReturnMessage.SUCCESS);
	}

}
