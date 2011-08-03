package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Responsável por carregar o arquivo de configuração <code>opala.conf</code>
 * 
 * @author Pádua
 * 
 */
public class Configuration {

	private static Properties prop = new Properties();
	private static Configuration conf;

	private Configuration(String confFile) {
		try {
			prop.load(new FileReader(new File(confFile)));
		} catch (FileNotFoundException e) {
			try {
				System.out.println("O arquivo opala.conf não foi encontrado. "
								+ "Crie um arquivo de configuração opala.conf no diretório: "
								+ new File("opala.conf").getCanonicalPath());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna um instância de {@link Configuration}
	 * 
	 * @return instância de {@link Configuration}
	 */
	public static Configuration getInstance() {
		if (conf == null)
			conf = new Configuration("opala.conf");
		return conf;
	}

	/**
	 * Seta o arquivo de configuração especificado
	 * 
	 * @param confFile
	 *            caminho e nome do arquivo de configuração
	 */
	public static void setConf(String confFile) {
		conf = new Configuration(confFile);
	}

	/**
	 * Retorna o valor da {@link Property} informada como parâmetro
	 * 
	 * @param property
	 *            a propriedade que se deseja obter o valor
	 * @return valor da propriedade informada
	 */
	public String getPropertyValue(Property property) {
		return prop.getProperty(property.getPropertyName()).trim();
	}

}
