package br.edu.ifpi.opala.searching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lucene.util.Version;

import br.edu.ifpi.opala.utils.IndexManager;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.ReturnMessage;

public class NearRealTimeTextSearcher implements TextSearcher {

	private final IndexManager indexManager;

	public NearRealTimeTextSearcher(IndexManager indexManager) {
		this.indexManager = indexManager;
	} 
	
	public SearchResult search(	Map<String, String> fields,
								List<String> returnedFields,
								int batchStart,
								int batchSize,
								String sortOn) {
		return search(fields, returnedFields, batchStart, batchSize, sortOn, false);
	}

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
			
			return searchTopDocs(query, returnedFields, init, limit, sortOn, reverse);
			
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
		QueryParser queryParser = new QueryParser(	Version.LUCENE_30, 
													Metadata.CONTENT.getValue(),
													IndexManager.ANALYZER);

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
	private SearchResult searchTopDocs(	Query query, List<String> returnedFields,
										int init, int limit,
										String sortOn, boolean reverse) throws IOException {
		
		ReturnMessage message = ReturnMessage.SUCCESS;
		IndexSearcher searcher = null;
		try {
			searcher = indexManager.getSearcher();
			
			Sort sort = null;
			if (sortOn != null && !sortOn.equals("")){
				IndexReader reader = searcher.getIndexReader();
				if (reader .getFieldNames(FieldOption.ALL).contains(sortOn)){
					sort = new Sort(new SortField(sortOn, SortField.STRING, reverse));
				}else {
					message = ReturnMessage.UNSORTABLE_FIELD;
				}
			}
			
			TopDocs topDocs = null;
			if (sort != null) {
				try {
					topDocs = searcher.search(query, null, limit, sort);
					return createSearchResult(searcher, init, topDocs, returnedFields, ReturnMessage.SUCCESS);
					
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
			return createSearchResult(searcher, init, topDocs, returnedFields, message);
		}
		catch (IOException e) {
			return new SearchResult(ReturnMessage.UNEXPECTED_SEARCH_ERROR, null);
		}
		finally{
			indexManager.release(searcher);
		}
	}
	
	/**
	 * Cria um objeto {@link SearchResult} para ser retornado pela busca.
	 * Percorre o conjunto de resultados (TopDocs) a partir do início informado 
	 * (init) e cria um {@link ResultItem} para cada um deles setando o ID, score
	 * e os campos de retorno informados (se algum).
	 * 
	 * @param searcher 
	 * @param init
	 * @param hits
	 * @param returnedFields
	 * @return Um objeto {@link SearchResult}
	 * 
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private SearchResult createSearchResult(IndexSearcher searcher,
											int init,
											TopDocs topDocs,
											List<String> returnedFields,
											ReturnMessage message) throws  IOException {
		
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
