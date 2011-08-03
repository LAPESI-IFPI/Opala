package br.edu.ifpi.opala.scheduled;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.indexing.TextIndexerImpl;
import br.edu.ifpi.opala.utils.Conversor;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Path;

/**
 * Realiza operação de inicializar os índices,
 * para isto ele cria e deleta conteúdo nos indices
 * 
 * @author Dannylvan
 * 
 */
public class InitIndex implements ServletContextListener {

	/**
	 * Não é usado
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	/**
	 * Quando o servlet é inicializado , este Método cria um índice vazio inicial
	 * de imagem e texto 
	 * @param sceInitialized - não é usado no Método 
	 */
	public void contextInitialized(ServletContextEvent sceInitialized) {
		String id = "idTeste";

		try {
			if (!IndexReader.indexExists(new SimpleFSDirectory(new File(Path.IMAGE_INDEX.getValue())))) {
				byte[] b = Base64.decodeBase64("/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigD//2Q==".getBytes());
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(id);
				ImageIndexerImpl.getImageIndexerImpl().addImage(metaDocument,Conversor.byteArrayToBufferedImage(b));
				ImageIndexerImpl.getImageIndexerImpl().delImage(id);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (!IndexReader.indexExists(new SimpleFSDirectory(new File(Path.TEXT_INDEX.getValue())))) {
				MetaDocument metaDocument = new MetaDocument();
				metaDocument.setId(id);
				TextIndexerImpl.getTextIndexerImpl().addText(metaDocument,"só pra criar o índice");
				TextIndexerImpl.getTextIndexerImpl().delText(id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
