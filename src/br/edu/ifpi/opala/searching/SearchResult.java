package br.edu.ifpi.opala.searching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpi.opala.utils.ReturnMessage;

/**
 * Classe que representa o resultado de uma busca com o código de ocorrência e
 * uma lista de ResultItem.
 * 
 * @author Mônica
 * 
 */
@SuppressWarnings("serial")
public class SearchResult implements Serializable {

	private ReturnMessage codigo;
	private List<ResultItem> items = new ArrayList<ResultItem>();
	private int totalHits;

	public SearchResult(){
	}
	
	public SearchResult(ReturnMessage codigo, List<ResultItem> items) {
		this.codigo = codigo;
		this.items = items;
	}
	
	public SearchResult(ReturnMessage codigo, List<ResultItem> items, int totalHits) {
		this.codigo = codigo;
		this.items = items;
		this.totalHits = totalHits;
	}

	public ReturnMessage getCodigo() {
		return codigo;
	}

	public void setCodigo(ReturnMessage codigo) {
		this.codigo = codigo;
	}

	public List<ResultItem> getItems() {
		return items;
	}

	public void setItems(List<ResultItem> items) {
		this.items = items;
	}

	public void addItem(ResultItem item) {
		items.add(item);
	}

	public ResultItem getItem(int i) {
		return items.get(i);
	}

	public int getTotalHits() {
		return totalHits;
	}

}
