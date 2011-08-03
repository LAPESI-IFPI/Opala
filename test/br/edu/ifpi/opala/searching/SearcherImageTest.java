package br.edu.ifpi.opala.searching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import util.UtilForTest;
import br.edu.ifpi.opala.indexing.ImageIndexer;
import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.statistic.Statistic;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Testa o score da busca de uma imagem no índice
 * @author Mônica
 *
 */
public class SearcherImageTest{

	SearcherImageImpl searcher = new SearcherImageImpl();
	Map<String, String> fields = new HashMap<String, String>();
	List<String> returnedFields = new ArrayList<String>();
	SearchResult searchResult;
	
	/**
	 * Teste prepara o índice que será usado no resto da classe.
	 */
	@BeforeClass
	public static void indexImages() {
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageRepository = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue());
		assertTrue(imageRepository.exists());
		assertFalse(imageRepository.listFiles().length==0);
		assertTrue(UtilForTest.indexImageDirOrFile(imageRepository));
	}
	
	/**
	 * Teste que verifica se um documento marcado sem visibilidade tem
	 * o mesmo score que o seu anterior.
	 */
	@Test
	public void searchIdenticalImages() throws IOException {
		searchResult = searcher.search(ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"1.jpg")), 10);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		List<ResultItem> docs = searchResult.getItems();
		for (int i=1; i < docs.size(); i++) {
			if (docs.get(i).isDuplicated())
				assertTrue(docs.get(i-1).getScore().equals(docs.get(i).getScore()));
		}
	}
	
	/**
	 * Busca imagens pequenas de 4 kb
	 * @throws IOException 
	 */	
	@Test
	public void searchImagePequenas() throws IOException{
		searchResult= searcher.search(ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (8).jpg")), 10);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());		
	}
	
	/**
	 * Busca imagens grandes de 7 MB
	 * @throws IOException 
	 */	
	@Test
	public void searchImageGrandes() throws IOException{
		searchResult = searcher.search(ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (13).bmp")), 10);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());		
	}
	
	/**
	 * Prepara os atributos para cada Método
	 */
	@Before
	public void prepareState() {
		fields.clear();
		returnedFields.clear();
	}

	/**
	 * Testa a busca com fields vazio e espera INVALID_QUERY
	 */
	@Test
	public void searchEmptyFields() {
		searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		assertEquals(ReturnMessage.INVALID_QUERY, searchResult.getCodigo());
	}

	/**
	 * Testa o envio de returnedFields vazio e espera SUCCESS
	 */
	@Test
	public void searchEmptyReturnedFields() {
		fields.put(Metadata.TITLE.getValue(), "image");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}

	/**
	 * Testa a busca com um returnedField não-existente e espera NULL
	 */
	@Test
	public void searchUnknownReturnedField() {
		fields.put(Metadata.TITLE.getValue(), "image");
		returnedFields.add("diaemqueoautornasceu");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertNull(searchResult.getItem(0).getField("diaemqueoautornasceu"));
	}

	/**
	 * Teste que verifica se um documento marcado como duplicado tem o mesmo
	 * score que o seu anterior.
	 */
	@Test
	public void searchIdenticalDocuments() {
		fields.put(Metadata.TITLE.getValue(), "image");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertFalse(searchResult.getItem(0).isDuplicated());
		assertTrue(searchResult.getItem(1).isDuplicated());
	}

	/**
	 * Testa busca por termo não existente no índice. Espera-se que não
	 * retorne nenhum documento.
	 */
	@Test
	public void searchNone() {
		fields.put(Metadata.TITLE.getValue(), "Xinforimpuladodannylvan");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());
	}

	
	/**
	 * Testa busca com query inv�lida e espera INVALID_QUERY
	 */
	@Test
	public void searchInvalidQuery() {
		fields.put("[author", "Tavares da Silva");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.INVALID_QUERY, searchResult.getCodigo());
	}

	/**
	 * Testa se a busca s� nos metadados está correta
	 */
	@Test
	public void searchMetadadosPadroes() {
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		MetaDocument metaDocumentJava = new MetaDocument();
		metaDocumentJava.setId("1");
		metaDocumentJava.setAuthor("Java");
		metaDocumentJava.setFormat("Java");
		metaDocumentJava.setKeywords("Java Ruby");
		metaDocumentJava.setPublicationDate("08/08/1998");
		metaDocumentJava.setTitle("Java");
		metaDocumentJava.setType("Artigo");
		
		MetaDocument metaDocumentPython = new MetaDocument();
		metaDocumentPython.setId("11");
		metaDocumentPython.setAuthor("Python");
		metaDocumentPython.setFormat("Python");
		metaDocumentPython.setKeywords("Python");
		metaDocumentPython.setPublicationDate("02/02/1992");
		metaDocumentPython.setTitle("Python");
		metaDocumentPython.setType("Artigo");
		
		MetaDocument metaDocumentRuby = new MetaDocument();
		metaDocumentRuby.setId("111");
		metaDocumentRuby.setAuthor("Ruby");
		metaDocumentRuby.setFormat("Ruby");
		metaDocumentRuby.setKeywords("Ruby");
		metaDocumentRuby.setPublicationDate("02/02/1982");
		metaDocumentRuby.setTitle("Ruby");
		metaDocumentRuby.setType("Artigo");

		ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();

		try {
			assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocumentJava, ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"1.jpg"))));
			assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocumentPython, ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (8).jpg"))));
			assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocumentRuby, ImageIO.read(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (5).jpg"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals(3, Statistic.numImageDocs());
		
		fields.clear();
		fields.put(Metadata.ID.getValue(), "11");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		assertEquals(metaDocumentPython.getId(), searchResult.getItem(0).getId());

		
		fields.clear();
		fields.put(Metadata.AUTHOR.getValue(), "Ruby");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		assertEquals(metaDocumentRuby.getId(), searchResult.getItem(0).getId());
		
		fields.clear();
		fields.put(Metadata.FORMAT.getValue(), "Java");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		assertEquals(metaDocumentJava.getId(), searchResult.getItem(0).getId());
		
		fields.clear();
		fields.put(Metadata.KEYWORDS.getValue(), "Ruby");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(2, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.KEYWORDS.getValue(), "ajdf�ljdf");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());

		fields.clear();
		fields.put(Metadata.TITLE.getValue(), "Python");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.TITLE.getValue(), "�j�lqeoi");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());

		fields.clear();
		fields.put(Metadata.TYPE.getValue(), "Artigo");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(3, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.TYPE.getValue(), "Monografia");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());

		fields.clear();
		fields.put(Metadata.PUBLICATION_DATE.getValue(), "01/02/1900");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.PUBLICATION_DATE.getValue(),"08/08/1998");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
	}

	/**
	 * Método privado que verifica se o SearchResult gerado é válido. Se cont�m
	 * atributos nulos, cole��es nulas, etc...
	 */
	private void isSearchResultValid() {
		assertNotNull(searchResult);
		assertNotNull(searchResult.getCodigo());
		if (searchResult.getItems() != null) {
			assertNotNull(searchResult.getItems());
			for (ResultItem item : searchResult.getItems()) {
				assertNotNull(item.isDuplicated());
				assertNotNull(item.getScore());
				assertNotNull(item.getId());
				if (item.getFields() != null)
					for (Entry<String, String> field : item.getFields()
							.entrySet()) {
						assertNotNull(field.getKey());
						assertFalse(field.getKey().isEmpty());
						assertNotNull(field.getValue());
						assertFalse(field.getValue().isEmpty());
					}
			}
		}
	}

	/**
	 * Método que garante que o resultado da busca tem uma estrutura correta e
	 * se índice não está no estado de lock após cada teste
	 */
	@After
	public void tearDown() throws IOException {
		isSearchResultValid();
	}
	
}
