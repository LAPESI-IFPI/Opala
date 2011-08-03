package br.edu.ifpi.opala.searching;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.edu.ifpi.opala.utils.ReturnMessage;

public class ImageSearch {

	private String indexPath;
	
	public ImageSearch(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public SearchResult search(BufferedImage image, int limit) {
		ImageSearchHits hits = null;
		Directory dir = null;
		IndexReader reader = null;
		if (limit <= 0) {
			return new SearchResult(ReturnMessage.PARAMETER_INVALID, null);
		}
		try {
			dir = FSDirectory.open(new File(indexPath));		
			if (IndexReader.indexExists(dir)) {
				reader = IndexReader.open(dir, true);
	
				if (reader.numDocs() == 0) {
					reader.close();
					return new SearchResult(ReturnMessage.EMPTY_INDEX, null);
				}
				ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(limit);
				hits = searcher.search(image, reader);
				
			} else {
				return new SearchResult(ReturnMessage.INDEX_NOT_FOUND, null);
			}

			List<ResultItem> items = new ArrayList<ResultItem>();
			for (int i = 0; i < hits.length(); i++) {
				ResultItem item = new ResultItem();
				item.setId(hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());
				item.setScore(String.valueOf(hits.score(i)));
				
				if (i > 0 && item.getScore().equals(items.get(i - 1).getScore()))
					item.setDuplicated(true);
				else
					item.setDuplicated(false);
				
				items.add(item);
			}
			
			reader.close();
			
			if (items.size() != 0) {
				return new  SearchResult(ReturnMessage.SUCCESS, items);
			} else {
				return new  SearchResult(ReturnMessage.EMPTY_SEARCHER, items);
			}
			
		} catch (IOException e) {
			return new SearchResult(ReturnMessage.UNEXPECTED_ERROR, null);
		}
	}
}