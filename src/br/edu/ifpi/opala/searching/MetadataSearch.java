package br.edu.ifpi.opala.searching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.ReturnMessage;
/**
 * Classe responsável por fazer buscas no índice de Texto.
 * @author Aécio Santos
 */
public class MetadataSearch {
	
	private static final BrazilianAnalyzer ANALYZER = new BrazilianAnalyzer(Version.LUCENE_30);
	private static final String DEFAULT_SEARCH_FIELD = Metadata.CONTENT.getValue();
	private IndexSearcher searcher;
	private String indexPath;
	private Directory dir;

	public MetadataSearch(String indexPath) {
		this.indexPath = indexPath;
	}
	
	/**
	 * Equivalente ao método search() seguinte. Porém ordenado em ordem 
	 * crescente por padrão (passa valor <code>false</code> no parametro reverse).
	 * 
	 * @param fields - Campos e valores a serem buscados
	 * @param returnedFields - Campos que serão retornados
	 * @param batchStart - A partir de que documento será retornado
	 * @param batchSize - A quantidade de documentos que serão retornados
	 * @param sortOn - Campo em que a busca será ordenada
	 * @return Um objeto <code>SearchResult</code> contendo o status da busca
	 * e os documentos encontrados.
	 * 
	 * @see SearchResult
	 */
	public SearchResult search( Map<String, String> fields,
								List<String> returnedFields,
								int batchStart, 
								int batchSize,
								String sortOn) {
		
		return search(fields, returnedFields, batchStart, batchSize, sortOn, false);
	}

	
	/**
	 * Faz uma busca no índice nos campos informados, retornando um
	 * {@link SearchResult}. <code>batchStart</code> e <code>batchSize</code>
	 * informam a partir de que resultado e quantos serão retornados,
	 * respectivamente. O <code>sortOn</code> indica que campo os resultados
	 * serão ordenados (em caso de nulo, os resultados serão ordenados por
	 * relevência). O <code>reverse</code> diz se os resultados estarão
	 * invertidos ou não.
	 * 
	 * @param fields - Campos e valores a serem buscados
	 * @param returnedFields - Campos que serão retornados
	 * @param batchStart - A partir de que documento será retornado
	 * @param batchSize - A quantidade de documentos que serão retornados
	 * @param sortOn - Campo em que a busca será ordenada
	 * @param reverse - Se os resultados virão na ordem inversa
	 * @return Um objeto <code>SearchResult</code> contendo o status da busca
	 * e os documentos encontrados.
	 * 
	 * @see SearchResult
	 */
	public SearchResult search(	Map<String, String> fields,
								List<String> returnedFields,
								int batchStart,
								int batchSize,
								String sortOn,
								boolean reverse) {
		try {
			if (fields == null || fields.size() == 0) {
				return new SearchResult(ReturnMessage.INVALID_QUERY, null);
			}

			int init = batchStart <= 0 ? 0 : batchStart-1;
			int limit = batchSize <= 0 ? (batchStart+20) : (batchStart+batchSize-1);
			
			Query query = createQuery(fields);
			
			return searchTopDocs(query, init, limit, sortOn, reverse, returnedFields);
			
		} catch (ParseException e) {
			return new SearchResult(ReturnMessage.INVALID_QUERY, null);
		} catch (IOException e) {
			return new SearchResult(ReturnMessage.UNEXPECTED_INDEX_ERROR, null);
		}
	}

	/**
	 * Cria um objeto Query com os campos informados no parâmetro.
	 * 
	 * @param fields - Map com campos como chave e seus respectivos 
	 * valores para serem buscados.
	 * @return Um objetoo {@link Query} correspondente aos campos informados.
	 * @throws ParseException
	 */
	private Query createQuery(Map<String, String> fields) throws ParseException {
		QueryParser queryParser = new QueryParser(
											Version.LUCENE_30, 
											DEFAULT_SEARCH_FIELD,
											ANALYZER);
		
		StringBuilder queryString = new StringBuilder();
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			queryString.append(entry.getKey());
			queryString.append(":\"");
			queryString.append(entry.getValue());
			queryString.append("\" ");
		}
		return queryParser.parse(queryString.toString());
	}

	/**
	 * Realiza a busca com a query, limit e ordenação passados. Retorna null se
	 * houver algum erro na abertura do índice
	 * 
	 * @param query A query a ser realizada
	 * @param init A partir de qual documento encontrado será retornado
	 * @param limit Quantos documentos serão retornados
	 * @param sortOn Campo que a o resultado será ordenado
	 * @param returnedFields Campos de metadados que serão retornados pela busca
	 * @return Objeto do tipo {@link SearchResult} contendo o status da busca e 
	 * os documentos encontrados.
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	private SearchResult searchTopDocs(Query query, int init, int limit, String sortOn, boolean reverse, List<String> returnedFields) 
	throws CorruptIndexException, IOException {
		ReturnMessage message = ReturnMessage.SUCCESS;
		dir = FSDirectory.open(new File(indexPath));
		searcher = new IndexSearcher(dir,true);

		Sort sort = null;
		if (sortOn != null && !sortOn.equals("")){
			IndexReader reader = IndexReader.open(dir,true);
			if (reader.getFieldNames(FieldOption.ALL).contains(sortOn)){
				sort = new Sort(new SortField(sortOn, SortField.STRING, reverse));
			}else {
				message = ReturnMessage.UNSORTABLE_FIELD;
			}
			reader.close();
		}
		
		try {
			
			TopDocs topDocs = null;
			if (sort != null) {
				try {
					topDocs = searcher.search(query, null, limit, sort);
					return createSearchResult(init, topDocs, returnedFields, ReturnMessage.SUCCESS);
					
				} catch (RuntimeException e) {
					// Warning: O Lucene 3.0.2 lança ArrayIndexOutOfBoundsException quando 
					// o campo é não ordenável. Pode ser mudado em versões futuras.
					if (e instanceof ArrayIndexOutOfBoundsException) {
						message = ReturnMessage.UNSORTABLE_FIELD;
					} else {
						message = ReturnMessage.UNEXPECTED_SORT_ERROR;
					}
				}
			}

			topDocs = searcher.search(query, limit);
			return createSearchResult(init, topDocs, returnedFields, message);
		
		} catch (IOException e) {
			return new SearchResult(ReturnMessage.UNEXPECTED_SEARCH_ERROR, null);
		}
		finally{
			dir.close();
			searcher.close();
		}
	}
	
	/**
	 * Cria um objeto {@link SearchResult} para ser retornado pela busca.
	 * Percorre o conjunto de resultados (TopDocs) a partir do início informado 
	 * (init) e cria um {@link ResultItem} para cada um deles setando o ID, score
	 * e os campos de retorno informados (se algum).
	 * 
	 * @param init
	 * @param hits
	 * @param returnedFields
	 * @return Um objeto {@link SearchResult}
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private SearchResult createSearchResult(int init,
											TopDocs topDocs,
											List<String> returnedFields,
											ReturnMessage message)
	throws CorruptIndexException, IOException {
		
		ScoreDoc[] hits = topDocs.scoreDocs;
		
		if (init >= hits.length) {
			return new SearchResult(ReturnMessage.EMPTY_SEARCHER, new ArrayList<ResultItem>());
		}
		
		List<ResultItem> items = new ArrayList<ResultItem>();
		
		for (int i = init; i < hits.length; i++) {
			ResultItem resultItem = new ResultItem();
			resultItem.setId(searcher.doc(hits[i].doc).get(Metadata.ID.getValue()));
			resultItem.setScore(Float.toString(hits[i].score));
			
			if (i > 0 && resultItem.getScore().equals(Float.toString(hits[i - 1].score))) {
				resultItem.setDuplicated(true);
			}
			
			Map<String, String> docFields = new HashMap<String, String>();
			
			if (returnedFields != null) {
				for (String returnedField : returnedFields) {
					if (searcher.doc(hits[i].doc).get(returnedField) != null) {
						docFields.put(returnedField, searcher.doc(hits[i].doc).get(returnedField));
					}
				}
			}
			resultItem.setFields(docFields);
			items.add(resultItem);
		}
		
		return new SearchResult(message, items, topDocs.totalHits);
	}
	
}
