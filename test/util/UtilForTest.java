package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import br.edu.ifpi.opala.indexing.ImageIndexer;
import br.edu.ifpi.opala.indexing.ImageIndexerImpl;
import br.edu.ifpi.opala.indexing.TextIndexer;
import br.edu.ifpi.opala.indexing.TextIndexerImpl;
import br.edu.ifpi.opala.indexing.parser.TxtParser;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.ReturnMessage;


/**
 * 
 * Métodos úteis para a realização dos teste,
 * principalmente para manter a não interferência no índice
 * 
 * @author Dannylvan
 *
 */
public class UtilForTest {

		/**
	    * Indexa um arquivo txt ou todos os arquivos txt do diretório e
	    * dos subdiretórios, caso contrário imprime o nome dos arquivos
	    * que não puderam ser indexados. Os metadados indexados são os padrões
	    * @param file - arquivo ou diretório a ser indexado 
	    * @return - true se conseguir indexar ao menos um arquivo
	    */
		public static boolean indexTextDirOrFile(File dir) {
			boolean result = false;
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					if (indexTextDirOrFile(file))
						result = true;
				}
			}
			else {
				String name = dir.getName();
				int position = name.lastIndexOf(".");
				if (position==-1 || !name.substring(position).toUpperCase().equals(".TXT")){
					return false;
				}
				InputStream is;
				try {
					is = new FileInputStream(dir);
					String texto = new TxtParser().getContent(is);
					is.close();
					if(texto==null){
						System.out.println("O documento \""+name+"\" não pode ser EXTRAÍDO");
					} else {
						TextIndexer textIndexer = TextIndexerImpl.getTextIndexerImpl();
						MetaDocument metaDoc = createMetaDocument(name);
						if (textIndexer.addText(metaDoc, texto).equals(ReturnMessage.SUCCESS) || textIndexer.addText(metaDoc, texto).equals(ReturnMessage.DUPLICATED_ID))
							return true;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return false;
			}
			return result;
		}
		
		/**
		 * Indexa um arquivo jpeg ou todos os arquivos jpeg do diret�rio e
		 * dos subdiret�rios, caso contr�rio imprime o nome dos arquivos
	     * que não puderam ser indexados.
		 * @param file - arquivo ou diret�rio a ser indexado 
		 * @return - true se conseguir indexar ao menos um arquivo
		 */
		public static boolean indexImageDirOrFile(File dir) {
			boolean result = false;
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (File file : files) {
					if (indexImageDirOrFile(file))
						result = true;
				}
			} else {
				String name = dir.getName();
				int position = name.lastIndexOf(".");
				if (position == -1	|| !(name.substring(position).toUpperCase().equals(".JPG") || name.substring(position).toUpperCase().equals(".JPEG")))
					return false;
				ImageIndexer imageIndexer = ImageIndexerImpl.getImageIndexerImpl();
				MetaDocument metaDoc = createMetaDocument(name);
				try {
					BufferedImage buff = ImageIO.read(dir);
					if (buff != null && imageIndexer.addImage(metaDoc, buff).equals(ReturnMessage.SUCCESS)) {
						return true;
					}
				} catch (IOException e) {
					return false;
				}
				return false;
			}
			return result;
		}
		
		/**
		 * Método que cria um MetaDocument com os metadados padrões de indexação
		 * @return - MetaDocument
		 */		
		public static MetaDocument createMetaDocument(String id){
			MetaDocument md = new MetaDocument();
			md.setAuthor("AUTHOR "+id);
			md.setFormat("FORMAT "+id);
			md.setTitle("TITLE "+id);
			md.setId(id);
			md.setKeywords("Keywords "+id);
			md.setPublicationDate("10/02/2009");
			md.setType("Type "+id);
			md.setField("sortable", id, false);
			
			return md;
		}
}
