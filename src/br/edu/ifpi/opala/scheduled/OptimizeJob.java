package br.edu.ifpi.opala.scheduled;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.indexing.TextIndexerImpl;
/**
 * Trabalho de otimizar o índice de texto e imagem
 * @author Dannylvan 
 *
 */
public class OptimizeJob implements Job{
	
	/**
	 * Substitui o metodo de Job para executar somente otimizar o índice de texto e imagem
	 * @param jecContext - não é usado
	 */
	public void execute(JobExecutionContext jecContext) throws JobExecutionException {
//		try {
//			Schedule.optimizeLogger.warning("----------------Iniciando otimização do índice----------------");
//			//ImageIndexerImpl.getImageIndexerImpl().optimize();	
//			//TextIndexerImpl.getTextIndexerImpl().optimize();
//			Schedule.optimizeLogger.warning("----------------Indices otimizados com sucesso----------------");
//		} catch (CorruptIndexException e) {
//			Schedule.optimizeLogger.warning(e.getMessage());
//			e.printStackTrace();
//		} catch (LockObtainFailedException e) {
//			Schedule.optimizeLogger.warning(e.getMessage());
//			e.printStackTrace();
//		} catch (IOException e) {
//			Schedule.optimizeLogger.warning(e.getMessage());
//			e.printStackTrace();
//		}
	}

}
