package br.edu.ifpi.opala.scheduled;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.edu.ifpi.opala.utils.Configuration;

/**
 * Realiza operação inicializar a configuração do projeto
 * feito à partir do arquivo opala.conf
 * 
 * @author Dannylvan
 * 
 */
public class InitConfiguration implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sceInitialized) {
			Configuration.setConf(sceInitialized.getServletContext()
					.getRealPath("opala.conf"));
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}

}
