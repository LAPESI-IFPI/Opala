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

import org.apache.lucene.index.CorruptIndexException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import util.UtilForTest;
import br.edu.ifpi.opala.indexing.TextIndexer;
import br.edu.ifpi.opala.indexing.TextIndexerImpl;
import br.edu.ifpi.opala.statistic.Statistic;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Testa a busca avançada na classe TextSearcher
 * 
 * @author Mônica
 * 
 */
public class TextSearcherTest {

	TextSearcherImpl searcher = new TextSearcherImpl();
	Map<String, String> fields = new HashMap<String, String>();
	List<String> returnedFields = new ArrayList<String>();
	SearchResult searchResult;

	/**
	 * Prepara o índice que será usado no resto da classe.
	 */
	@BeforeClass
	public static void indexTexts() {
		File textIndex = new File(Path.TEXT_INDEX.getValue());
		assertTrue(Util.deleteDir(textIndex));
		File textRepository = new File(PathTest.TEXT_REPOSITORY_TEST.getValue());
		assertTrue(textRepository.exists());
		assertTrue(UtilForTest.indexTextDirOrFile(textRepository));
	}
	
	/**
	 * Prepara os atributos para cada método
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

	@Test
	public void searchEmptyFields2() {
		searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		assertEquals(ReturnMessage.INVALID_QUERY, searchResult.getCodigo());
	}
	
	/**
	 * Testa a busca com fields null e espera INVALID_QUERY
	 */
	@Test
	public void searchNullFields() {
		searchResult = searcher.search(null, returnedFields, 1, 10, null, false);
		assertEquals(ReturnMessage.INVALID_QUERY, searchResult.getCodigo());
	}

	/**
	 * Testa o envio de returnedFields vazio e espera SUCCESS
	 */
	@Test
	public void searchEmptyReturnedFields() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,	false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}

	/**
	 * Testa o envio de returnedFields null e espera SUCCESS
	 */
	@Test
	public void searchNullReturnedFields() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, null, 1, 10, null, false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}

	/**
	 * Testa a busca com um returnedField não-existente e espera NULL
	 */
	@Test
	public void searchUnknownReturnedField() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
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
		fields.put(Metadata.CONTENT.getValue(), "Rodrigo Barbosa Reis");
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
		fields.put(Metadata.CONTENT.getValue(), "Xinforimpuladodannylvan");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());
	}

	/**
	 * Testa a busca por campo inexistente e espera nenhum resultado
	 */
	@Test
	public void searchNonExistentField() {
		fields.put("diaqueoautornasceu", "dia 1");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());
	}

	/**
	 * Testa busca com sort null e espera que os resultados estejam ordenados por score
	 */
	@Test
	public void searchNullSortOn() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(isThreeFirstsOrderedByScore());
	}

	/**
	 * Testa busca com sort em campo que não existe e espera UNSORTABLE_FIELD e que os resultados estejam ordenados por score
	 */
	@Test
	public void searchSortOnNonExistentField() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		searchResult = searcher.search(fields, returnedFields, 1, 10, "SORTONA", false);
		assertEquals(ReturnMessage.UNSORTABLE_FIELD, searchResult.getCodigo());
		assertTrue(isThreeFirstsOrderedByScore());
	}

	/**
	 * Testa busca com sort em campo que existe mas não é ordenável e espera UNSORTABLE_FIELD e que os resultados estejam ordenados por score
	 */
	@Test
	public void searchSortOnNotSortableField() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		searchResult = searcher.search(fields, returnedFields, 1, 10,
				Metadata.AUTHOR.getValue(), false);
		assertEquals(ReturnMessage.UNSORTABLE_FIELD, searchResult.getCodigo());
		assertTrue(isThreeFirstsOrderedByScore());
	}

	/**
	 * Testa busca com sort em campo que existe e espera SUCCESS e que os resultados estejam ordenados pelo campo informado
	 */
	@Test
	public void searchSortOnExistentField() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		returnedFields.add("sortable");
		searchResult = searcher.search(fields, returnedFields, 1, 10,
				"sortable");
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(isThreeFirstOrderedBySortable());
	}

	/**
	 * Teste de busca sem informar o parâmetro reverse e espera SUCCESS e que os resultados estejam ordenados por score
	 */
	@Test
	public void searchWithoutReverse() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		returnedFields.add("sortable");
		searchResult = searcher.search(fields, returnedFields, 1, 10,
				"sortable");
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(isThreeFirstOrderedBySortable());
	}

	/**
	 * Teste de busca informando o parâmetro reverse como true e espera SUCCESS e que os resultados estejam ordenados inversamente por score
	 */
	@Test
	public void searchWithReverseTrue() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		returnedFields.add("sortable");
		searchResult = searcher.search(fields, returnedFields, 1, 10,
				"sortable", true);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(isThreeFirstReverseOrderedBySortable());
	}

	/**
	 * Teste de busca informando reverse false e espera SUCCESS e que os resultados estejam ordenados por score
	 */
	@Test
	public void searchWithReverseFalse() {
		fields.put(Metadata.CONTENT.getValue(), "Dannylvan");
		returnedFields.add("sortable");
		searchResult = searcher.search(fields, returnedFields, 1, 10,
				"sortable", false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(isThreeFirstOrderedBySortable());
	}

	/**
	 * Teste de busca com batchStart negativo e espera SUCCESS
	 */
	@Test
	public void searchNegativeBatchStart() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, null, -1, 10, null, false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}


	/**
	 * Teste de busca com batchStart maior que o da busca anterior, espera SUCCESS e que o número de resultados seja menor em 1
	 */
	@Test
	public void searchIncreasingBatchStart() {
		fields.put(Metadata.CONTENT.getValue(), "para");
		searchResult = searcher.search(fields, null, 1, 19, null, false);
		int numDoc = searchResult.getItems().size();
		searchResult = searcher.search(fields, null, 2, 19, null, false);
		assertEquals(numDoc-1, searchResult.getItems().size());
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}
	
	/**
	 * Teste de busca com batchSize menor que o número de resultados da busca anterior, espera SUCCESS e que o número de resultados seja menor em 1
	 */
	@Test
	public void searchWithDecreasingBatchSize() {
		fields.put(Metadata.CONTENT.getValue(), "para");
		searchResult = searcher.search(fields, null, 1, 10, null, false);
		int numDoc = searchResult.getItems().size();
		searchResult = searcher.search(fields, null, 1, numDoc-1, null, false);
		assertEquals(numDoc-1, searchResult.getItems().size());
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}
	
	/**
	 * Teste de busca com batchStart zero e espera SUCCESS
	 */
	@Test
	public void searchZeroBatchStart() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, null, 0, 10, null, false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}

	/**
	 * Teste de busca com batchStart maior que o número de hits e espera nenhum resultado
	 */
	@Test
	public void searchBatchStartGreaterThanHitsNumber() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, null, 1000, 10, null, false);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
	}

	/**
	 * Teste de busca com batchSize negativo e espera SUCCESS
	 */
	@Test
	public void searchNegativeBatchSize() {
		fields.put(Metadata.CONTENT.getValue(), "dinâmica");
		searchResult = searcher.search(fields, null, 1, -1, null, false);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertTrue(searchResult.getItems().size() == 1);
	}

	/**
	 * Testa busca com query inválida e espera INVALID_QUERY
	 */
	@Test
	public void searchInvalidQuery() {
		fields.put("[author", "Tavares da Silva");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null,
				false);
		assertEquals(ReturnMessage.INVALID_QUERY, searchResult.getCodigo());
	}

	/**
	 * Testa se a busca se nos metadados está correta
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws CorruptIndexException 
	 */
	@Test
	public void searchMetadadosPadroes() throws CorruptIndexException, IllegalStateException, IOException {
		File textIndex = new File(Path.TEXT_INDEX.getValue());
		assertTrue(Util.deleteDir(textIndex));
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

		TextIndexer indexer = TextIndexerImpl.getTextIndexerImpl();
		assertEquals(ReturnMessage.SUCCESS, indexer.addText(metaDocumentJava, "documento Java 1 aqui java"));
		assertEquals(ReturnMessage.SUCCESS, indexer.addText(metaDocumentPython, "documento Python 2 aqui python"));
		assertEquals(ReturnMessage.SUCCESS, indexer.addText(metaDocumentRuby, "documento Ruby 3 aqui ruby"));
		assertEquals(3, Statistic.numTextDocs());
		
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
		fields.put(Metadata.KEYWORDS.getValue(), "ajdfçljdf");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());

		fields.clear();
		fields.put(Metadata.PUBLICATION_DATE.getValue(), "1900");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.EMPTY_SEARCHER, searchResult.getCodigo());
		assertEquals(0, searchResult.getItems().size());

		fields.clear();
		fields.put(Metadata.PUBLICATION_DATE.getValue(), "08/08/1998");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.TITLE.getValue(), "Python");
		searchResult = searcher.search(fields, returnedFields, 1, 10, null);
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
		assertEquals(1, searchResult.getItems().size());
		
		fields.clear();
		fields.put(Metadata.TITLE.getValue(), "çjãlqeoi");
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
	}
	
	/**
	 * Método auxiliar da classe de testes que retorna se os três primeiros
	 * resultados estão ordenados por relevância (score)
	 * 
	 * @return true se estiverem ordenados por relevância
	 */
	private boolean isThreeFirstsOrderedByScore() {
		return Float.parseFloat(searchResult.getItem(0).getScore()) > Float
				.parseFloat(searchResult.getItem(1).getScore())
				&& Float.parseFloat(searchResult.getItem(1).getScore()) > Float
						.parseFloat(searchResult.getItem(2).getScore());
	}

	/**
	 * Método auxiliar da classe de testes que retorna se os três primeiros
	 * resultados estão ordenados pelo campo "sortable"
	 * 
	 * @return true se estiverem ordenados por "sortable"
	 */
	private boolean isThreeFirstOrderedBySortable() {
		return searchResult.getItem(0).getField("sortable").compareTo(
				searchResult.getItem(1).getField("sortable")) < 0
				&& searchResult.getItem(1).getField("sortable").compareTo(
						searchResult.getItem(2).getField("sortable")) < 0;
	}

	/**
	 * Método auxiliar da classe de testes que retorna se os três primeiros
	 * resultados estão ordenados inversamente pelo campo "sortable"
	 * 
	 * @return true se estiverem ordenados inversamente por "sortable"
	 */
	private boolean isThreeFirstReverseOrderedBySortable() {
		return searchResult.getItem(0).getField("sortable").compareTo(
				searchResult.getItem(1).getField("sortable")) > 0
				&& searchResult.getItem(1).getField("sortable").compareTo(
						searchResult.getItem(2).getField("sortable")) > 0;
	}
	
	/**
	 * Testa se duas queries identicas (diferença somente em 'batchStart' e 'batchSize')
	 * retornam o mesmo valor em 'totalHits'.
	 * TotalHits deve ser o valor de documentos encontrados para a query no índice, e 
	 * não a quantidade de valores retornados.
	 * 
	 */
	@Test
	public void shouldReturnEqualValuesOfTotalHitsForEqualQueries() {
		//dado
		fields.put(Metadata.CONTENT.getValue(), "para");
		
		//quando
		searchResult = searcher.search(fields, null, 1, 2, null, false);
		SearchResult searchResult2 = searcher.search(fields, null, 2, 4, null, false);

		//entao
		assertEquals(searchResult.getTotalHits(), searchResult2.getTotalHits());
	}

	/**
	 * Método privado que verifica se o SearchResult gerado é válido. Se contém
	 * atributos nulos, coleções nulas, etc...
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
