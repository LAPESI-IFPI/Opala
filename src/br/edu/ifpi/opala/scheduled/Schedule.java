package br.edu.ifpi.opala.scheduled;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import br.edu.ifpi.opala.utils.Path;

/**
 * Invoca o BackupJob e OptimizeJob e seta a frequência de chamada.
 * 
 * @author Dannylvan
 * 
 */
public class Schedule implements ServletContextListener {
	
	static Logger optimizeLogger;
	static Logger backupLogger;

	/**
	 * Cria os arquivos de log de cada backup, que serão usados nos Jobs
	 */
	private void createBackupLog() {
		optimizeLogger = Logger.getLogger(OptimizeJob.class.getName());
		backupLogger = Logger.getLogger(BackupJob.class.getName());
		
		File optimizeFile = new File(Path.OPTIMIZE_LOG.getValue());
		File backupFile = new File(Path.BACKUP_LOG.getValue());
		
		new File(optimizeFile.getParent()).mkdirs();
		
		try {
			FileHandler fileOpt = new FileHandler(optimizeFile.getPath(),true);
			fileOpt.setFormatter(new SimpleFormatter());
			optimizeLogger.addHandler(fileOpt);

			FileHandler fileBack = new FileHandler(backupFile.getPath(), true);
			fileBack.setFormatter(new SimpleFormatter());
			backupLogger.addHandler(fileBack);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invoca o BackupJob e OptimizeJob
	 * 
	 * @param arg0
	 *            - não é usado
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		createBackupLog();
		SchedulerFactory schedFact = new StdSchedulerFactory();
		int frequenciaBackup=24;
		int frequenciaOptimize=2;

		Scheduler scheduler;
		try {
			frequenciaBackup = new Integer(Path.BACKUP_FREQUENCY.getValue().trim());
		} catch (Exception e) {
			backupLogger.warning("Frequência de backup informada no arquivo opala.conf é inválida: "
								+ "\"" + Path.BACKUP_FREQUENCY + "\"");
		}
		try {
			frequenciaOptimize = new Integer(Path.OPTIMIZE_FREQUENCY.getValue().trim());
		} catch (Exception e) {
			backupLogger.warning("Frequência de Optimização informada no arquivo opala.conf é inválida: "
								+ "\"" + Path.BACKUP_FREQUENCY + "\"");
		}
		
		try {
			scheduler = schedFact.getScheduler();
			scheduler.start();
			JobDetail optimizeJob = new JobDetail("optimizeJob",
												  Scheduler.DEFAULT_GROUP,
												  OptimizeJob.class);
			
			JobDetail backupJob = new JobDetail("backupJob",
												Scheduler.DEFAULT_GROUP,
												BackupJob.class);

			SimpleTrigger triggerOptmize = new SimpleTrigger("TriggerJob",
															Scheduler.DEFAULT_GROUP, new Date(), null,
															SimpleTrigger.REPEAT_INDEFINITELY,
															frequenciaOptimize * 3600000L);// 1

			SimpleTrigger triggerBack = new SimpleTrigger(	"myTriggerBack",
															Scheduler.DEFAULT_GROUP,
															new Date(), null,
															SimpleTrigger.REPEAT_INDEFINITELY,
															frequenciaBackup * 3600000L);// 1

			scheduler.scheduleJob(optimizeJob, triggerOptmize);
			scheduler.scheduleJob(backupJob, triggerBack);
		} catch (SchedulerException e) {
			Schedule.optimizeLogger.warning(e.getMessage() + "ERRO AO TENTAR INICIAR SCHEDULE");
			Schedule.backupLogger.warning(e.getMessage() + "ERRO AO TENTAR INICIAR SCHEDULE");
			e.printStackTrace();
		}
	}

	/**
	 * Não é usado
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

	}
}
