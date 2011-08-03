package br.edu.ifpi.opala.scheduled;

import java.io.IOException;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.indexing.TextIndexerImpl;
/**
 * Classe responsável por executar o backup do índice
 * 
 * @author Dannylvan
 *
 */
public class BackupJob implements Job{

	/**
	 * Executa o backup do índice de imagem e de texto
	 * @param jecContexto não é usado no método
	 **/
	public void execute(JobExecutionContext jecContexto) throws JobExecutionException {
		
//		try {
//			Schedule.backupLogger.warning("---------------- Iniciando BACKUPS  ----------------");
//			ImageIndexerImpl.getImageIndexerImpl().backupNow();
//			TextIndexerImpl.getTextIndexerImpl().backupNow();
//			Schedule.backupLogger.warning("--------------- Backups realizados com sucesso  --------------");
//		} catch (IOException e) {
//			Schedule.backupLogger.warning("------------------ Backups Falhou em  "+new Date()+"  ------------------");
//			Schedule.backupLogger.warning(e.getMessage());
//			Schedule.backupLogger.warning(e.getStackTrace().toString());
		//}
		
	}
}
