package br.edu.ifpi.opala.searching;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.ifpi.opala.indexing.NearRealTimeTextIndexer;
import br.edu.ifpi.opala.utils.IndexManager;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.MetaDocumentBuilder;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.ReturnMessage;

public class NearRealTimeTextSearcherTestTest {
	
	private String TEXTO_1 			 = "Esse texto número #1 de exemplo utilizado nos testes.";
	private String TEXTO_2 			 = "Esse texto é o número #2 e ele é diferente do primeiro";
	private String TEXTO_2_DUPLICADO = "Esse texto é o número #2 e ele é diferente do primeiro";
	private String TEXTO_3 			 = "Mais um documento de texto. Esse é o número 3.";
	
	private MetaDocument METADOC_1 = new MetaDocumentBuilder().id("1").title("Título #1").author("Autor 1").build();
	private MetaDocument METADOC_2 = new MetaDocumentBuilder().id("2").title("Título #2").author("Autor 2").build();
	private MetaDocument METADOC_2_DUPLICADO = new MetaDocumentBuilder().id("2DUP").title("Título duplicado").author("Autor 2DUP").build();
	private MetaDocument METADOC_3 = new MetaDocumentBuilder().id("3").title("Título #3").build();

	private IndexManager indexManager;
	
	@Before
	public void setUpNewIndex() throws IOException {
//		File path = new File(Path.TEXT_INDEX.getValue());
//		assertTrue(Util.deleteDir(path));
//		Directory indexDir = FSDirectory.open(path);
		Directory indexDir = new RAMDirectory();
		indexManager = new IndexManager(indexDir);
	}

	@After
	public void tearDown() throws CorruptIndexException, IOException {
		indexManager.close();
	}
	

	public void indexarDocumento(IndexManager indexMagager, MetaDocument metadoc, String content) throws IOException {
		NearRealTimeTextIndexer indexer = new NearRealTimeTextIndexer(indexMagager);
		indexer.addText(metadoc, content);
	}

	private void indexarTodosDocumentos(IndexManager indexManager) throws IOException {
		indexarDocumento(indexManager, METADOC_1, TEXTO_1);
		indexarDocumento(indexManager, METADOC_2, TEXTO_2);
		indexarDocumento(indexManager, METADOC_2_DUPLICADO, TEXTO_2_DUPLICADO);
	}

	@Test
	public final void deveriaEncontarUmDocumentoQueJaFoiIndexado() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = newRetunedFieldsWithAuthorAndTitle();
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarDocumento(indexManager, METADOC_1, TEXTO_1);
		
		//quando
		SearchResult resultado = searcher.search(fields, returnedFields, 1, 10, Metadata.ID.getValue(), false);
		
		
		//então
		assertThat(resultado.getCodigo(), is(equalTo(ReturnMessage.SUCCESS)));
		assertThat(resultado.getItems().size(), is(equalTo(1)));
		assertThat(resultado.getItems().iterator().next().getField(Metadata.AUTHOR.getValue()), is(notNullValue()));
		assertThat(resultado.getItems().iterator().next().getField(Metadata.TITLE.getValue()), is(notNullValue()));
		assertThat(resultado.getItems().iterator().next().getField(Metadata.KEYWORDS.getValue()), is(nullValue()));
	}
	
	@Test
	public final void deveriaRetornarEmptySearcherQuandoNaoEncontaNadaNaBusca() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "adfhadsfasdfglhasdfjasdf3431383h123h12ih1");
		
		List<String> returnedFields = newRetunedFieldsWithAuthorAndTitle();
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult resultado = searcher.search(fields, returnedFields, 1, 10, Metadata.ID.getValue(), false);
		
		
		//então
		assertThat(resultado.getCodigo(), is(equalTo(ReturnMessage.EMPTY_SEARCHER)));
		assertThat(resultado.getItems().size(), is(equalTo(0)));
	}

	private List<String> newRetunedFieldsWithAuthorAndTitle() {
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.AUTHOR.getValue());
		returnedFields.add(Metadata.TITLE.getValue());
		return returnedFields;
	}
	
	
	/**
	 * Testa a busca com fields vazio e espera INVALID_QUERY
	 */
	@Test
	public final void deveriaRetornarInvalidQueryQuandoFieldsEhUmaListaVazia() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		List<String> returnedFields = new ArrayList<String>();

		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		//quando
		SearchResult resultado = searcher.search(fields, returnedFields, 1, 10, null, false);
		
		
		//então
		assertThat(resultado.getCodigo(), is(equalTo(ReturnMessage.INVALID_QUERY)));
	}
	
	
	/**
	 * Testa a busca com fields null e espera INVALID_QUERY
	 */
	@Test
	public void deveriaRetornarInvalidQueryQuandoFieldsEhNull() throws Exception {
		//dado
		List<String> returnedFields = new ArrayList<String>();

		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		//quando
		SearchResult resultado = searcher.search(null, returnedFields, 1, 10, null, false);
		
		
		//então
		assertThat(resultado.getCodigo(), is(equalTo(ReturnMessage.INVALID_QUERY)));
	}
	
	
	/**
	 * Testa o envio de returnedFields vazio e espera SUCCESS
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws Exception 
	 */
	@Test
	public void deveriaRetornarSuccessMesmoQuandoReturnedFieldsEstaVazio() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);

		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, returnedFields, 1, 10, "id", false);
		
		
		//entao
		assertEquals(ReturnMessage.SUCCESS, searchResult.getCodigo());
	}
	
	
	/**
	 * Testa o envio de returnedFields null e espera SUCCESS
	 */
	@Test
	public void deveriaRetornarSuccessMesmoPassandoReturnedFieldsNull() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);

		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(equalTo(ReturnMessage.SUCCESS)));
	}
	
	/**
	 * Testa a busca com um returnedField não-existente e espera NULL
	 */
	@Test
	public void deveriaRetornarSuccessQuandoEhSolicitadoUmCampoNaoExistenteNoIndice() 
	throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add("diaEmQueOAutorNasceu");
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);

		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(equalTo(ReturnMessage.SUCCESS)));
		assertThat(searchResult.getItem(0).getField("diaEmQueOAutorNasceu"), is(nullValue()));
	}

	/**
	 * Verifica se um documento marcado como duplicado tem o mesmo
	 * score que o seu anterior.
	 * Deve encontrar os documentos: "Americana_M.txt" e "Americana_M2.txt"
	 * 
	 */
	@Test
	public void deveriaMarcarOSegundoDocumentoRepetidoComoDuplicated() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarDocumento(indexManager, METADOC_2, TEXTO_2);
		indexarDocumento(indexManager, METADOC_2_DUPLICADO, TEXTO_2_DUPLICADO);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(equalTo(ReturnMessage.SUCCESS)));
		assertThat(searchResult.getItem(0).isDuplicated(), is(false));
		assertThat(searchResult.getItem(1).isDuplicated(), is(true));
		assertEquals(searchResult.getItem(0).getScore(), searchResult.getItem(1).getScore());
	}
	
	
	/**
	 * Testa a busca por campo inexistente e espera nenhum resultado
	 */
	@Test
	public void deveriaRetornarEmptySearcherQuandoBuscaEmCampoNaoExistente() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("campoQueNaoExisteNoIndice", "Um valor qualquer para ser buscado");
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(equalTo(ReturnMessage.EMPTY_SEARCHER)));
		assertThat(searchResult.getItems().size(), is(0) );
	}
	
	/**
	 * Testa busca com sort null e espera que os resultados estejam ordenados por score
	 */
	@Test
	public void deveriaOrdenarResultadosPorRelevanciaQuandoSortOnEhNull() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		indexarDocumento(indexManager, METADOC_1, TEXTO_1);
		indexarDocumento(indexManager, METADOC_2, TEXTO_2);
		indexarDocumento(indexManager, METADOC_3, TEXTO_3);
		
		//quando
		SearchResult searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(equalTo(ReturnMessage.SUCCESS)));
		assertTrue(theThreeFirstsAreOrderedByScore(searchResult));
	}

	/**
	 * TODO: Resolver problemas de ordenação na busca distribuída
	 * 
	 * Testa busca com sort em campo que não existe e espera UNSORTABLE_FIELD e que os resultados estejam ordenados por score
	 */
	@Test
	public void deveriaRetornarUnsortableFieldQuandoOrdenacaoEhFeitaEmCampoInexistente()  throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, "CampoInexistenteNoIndice", false);
		
		
		for(ResultItem item: searchResult.getItems()) {
			System.out.println(item.getId());
		}

		//entao
		assertThat(theThreeFirstsAreOrderedByScore(searchResult), is(true));
		assertThat(searchResult.getCodigo(), is(ReturnMessage.UNSORTABLE_FIELD));
	}
	
	/**
	 * Testa busca com sort em campo que existe mas não é ordenável e espera UNSORTABLE_FIELD e que os resultados estejam ordenados por score
	 */
	@Test
	public void deveriaRetornarUnsortableFieldQuandoSortOnEhUmCampoQueNaoPodeSerOrdenado() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.AUTHOR.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, Metadata.AUTHOR.getValue(), false);
		
		
		//entao
		assertThat(theThreeFirstsAreOrderedByScore(searchResult), is(true));
		assertThat(searchResult.getCodigo(), is(ReturnMessage.UNSORTABLE_FIELD));
	}

	/**
	 * Testa busca com sort em campo que existe e espera SUCCESS e que os resultados estejam ordenados pelo campo informado
	 */
	@Test
	public void deveriaOrdenarPeloCampoEspecificadoEmSortOn() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, Metadata.ID.getValue());
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(theThreeFirstsAreOrderedByID(searchResult), is(true));
	}

	/**
	 * Teste de busca informando o parâmetro reverse como true e espera SUCCESS e que os 
	 * resultados estejam ordenados inversamente por score
	 */
	@Test
	public void deveriaOrdenarPorOrdemDecrescenteQuandoReverseEhTrue() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, Metadata.ID.getValue(), true);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(theThreeFirstsAreOrderedByIDInDescendantOrder(searchResult), is(true));
	}
	/**
	 * Testa se busca retorna os resultados ordenados em ordem crescente quando o parâmetro
	 * 'reverse' é falso. 
	 */
	@Test
	public void deveriaOrdenarEmOrdemCrescenteQuandoReverseEhFalse() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, 10, Metadata.ID.getValue(), false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(theThreeFirstsAreOrderedByID(searchResult), is(true));
	}

	

	/**
	 * Teste de busca com batchStart negativo e espera SUCCESS
	 */
	@Test
	public void deveriaDesconsiderarBatchStartComValorNegativo() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, -1, 10, null);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(theThreeFirstsAreOrderedByScore(searchResult), is(true));
	}


	/**
	 * Teste de busca com batchStart maior que o da busca anterior, espera SUCCESS e que o número de resultados seja menor em 1
	 */
	@Test
	public void deveriaRetornarAQuantidadeDeHitsCorretaQuandoBatchStartAumenta() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult1 = searcher.search(fields, null, 1, 3, null);
		SearchResult searchResult2 = searcher.search(fields, null, 2, 3, null);
		
		
		int numDoc1 = searchResult1.getItems().size();
		int numDoc2 = searchResult2.getItems().size();
		
		//entao
		assertThat(searchResult1.getItem(1).getId(), is(searchResult2.getItem(0).getId()));
		assertThat(searchResult1.getItem(2).getId(), is(searchResult2.getItem(1).getId()));
		assertThat(numDoc1, is( numDoc2+1 ));
	}
	
	/**
	 * Teste de busca com batchSize menor que o número de resultados da busca anterior, espera SUCCESS e que o número de resultados seja menor em 1
	 */
	@Test
	public void deveriaRetornarMenosItensQuandoBatchSizeEhMenorUmaUnidade() throws Exception {
		
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult1 = searcher.search(fields, null, 1, 10, null);
		int numDoc1 = searchResult1.getItems().size();
		
		SearchResult searchResult2 = searcher.search(fields, null, 1, numDoc1-1, null);
		int numDoc2 = searchResult2.getItems().size();
		
		//entao
		
		assertThat(searchResult1.getItem(0).getId(), is(searchResult2.getItem(0).getId()));
		assertThat(searchResult1.getItem(1).getId(), is(searchResult2.getItem(1).getId()));
		assertThat(numDoc1-1, is( numDoc2 ));
	}
	
	/**
	 * Teste de busca com batchStart zero e espera SUCCESS
	 */
	@Test
	public void deveriaRetornarSuccessEIgnorarBatchStartQuandoEhIgualAZero() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);

		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 0, 10, null);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(theThreeFirstsAreOrderedByScore(searchResult), is(true));
	}

	/**
	 * Teste de busca com batchStart maior que o número de hits e espera nenhum resultado
	 */
	@Test
	public void deveriaRetornarEmptySearcherCasoBatchStartSejaMaiorONumeroDeHits() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1000, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.EMPTY_SEARCHER));
	}

	/**
	 * Teste de busca com batchSize negativo e espera SUCCESS
	 */
	@Test
	public void deveriaRetornarSuccessMesmoComBatchSizeComValorNegativo() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put(Metadata.CONTENT.getValue(), "texto");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		indexarTodosDocumentos(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, null, 1, -1, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.SUCCESS));
		assertThat(searchResult.getItems(), is(not(nullValue())));
		
	}

	/**
	 * Testa busca com query inválida e espera INVALID_QUERY
	 */
	@Test
	public void deveriaRetornarInvalidQueryQuandoUmCampoInvalidoEhPassadoNaBusca() throws Exception {
		//dado
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("[author", "Algum valor");
		
		List<String> returnedFields = new ArrayList<String>();
		returnedFields.add(Metadata.TITLE.getValue());
		
		TextSearcher searcher = new NearRealTimeTextSearcher(indexManager);
		
		//quando
		SearchResult searchResult = searcher.search(fields, returnedFields, 1, 10, null, false);
		
		
		//entao
		assertThat(searchResult.getCodigo(), is(ReturnMessage.INVALID_QUERY));
	}
	
	
	/**
	 * Método auxiliar da classe de testes que retorna se os três primeiros
	 * resultados estão ordenados por relevância (score)
	 * @return 
	 * 
	 * @return true se estiverem ordenados por relevância
	 */
	private boolean theThreeFirstsAreOrderedByScore(SearchResult searchResult) {
		return Float.parseFloat(searchResult.getItem(0).getScore()) 
				>= Float.parseFloat(searchResult.getItem(1).getScore())
					&& 
				Float.parseFloat(searchResult.getItem(1).getScore()) 
				>= Float.parseFloat(searchResult.getItem(2).getScore());
	}
	
	/**
	 * Método auxiliar da classe de testes que retorna se os três primeiros
	 * resultados estão ordenados por ID
	 * 
	 * @return true se estiverem ordenados por ID
	 */
	private boolean theThreeFirstsAreOrderedByID(SearchResult searchResult) {
		return searchResult.getItem(0).getId().compareTo(
				searchResult.getItem(1).getId() ) < 0
				&& searchResult.getItem(1).getId().compareTo(
						searchResult.getItem(2).getId() ) < 0;
	}
	
	/**
	 * Método auxiliar que retorna se os três primeiros
	 * resultados estão ordenados por ID em ordem decrescente
	 * 
	 * @return true se estiverem ordenados por ID
	 */
	private boolean theThreeFirstsAreOrderedByIDInDescendantOrder(SearchResult searchResult) {
		return searchResult.getItem(0).getId().compareTo(
				searchResult.getItem(1).getId() ) > 0
				&& searchResult.getItem(1).getId().compareTo(
						searchResult.getItem(2).getId() ) > 0;
	}
}
