package br.edu.ifpi.opala.indexing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;
import br.edu.ifpi.opala.utils.Conversor;
import br.edu.ifpi.opala.utils.MetaDocument;
import br.edu.ifpi.opala.utils.Metadata;
import br.edu.ifpi.opala.utils.Path;
import br.edu.ifpi.opala.utils.ReturnMessage;
import br.edu.ifpi.opala.utils.Util;

/**
 * Teste unitários da classe ImageIndexerImpl
 * 
 * @author Pádua
 * @see ImageIndexerImpl
 * 
 */
public class ImageIndexerTest {
	static MetaDocument metaDocument;
	static BufferedImage image;
	private ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();

	/**
	 * Teste prepara o índice que será usado no resto da classe.
	 */
	@BeforeClass
	public static void indexImages() {
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		File imageRepository = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue());
		assertTrue(imageRepository.exists());
		assertFalse(imageRepository.listFiles().length==0);

		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc1");

		try {
			image = Conversor.byteArrayToBufferedImage(Conversor
					.fileToByteArray(new File(PathTest.IMAGE_REPOSITORY_TEST
							.getValue()+"image (6).jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Testa os retornos esperados do método addText
	 */
	@Test
	public void testAddImageCorrompida() {
		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc4");
		
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		BufferedImage imageCorrompida = null;
		try {
			imageCorrompida = Conversor.byteArrayToBufferedImage(Conversor.fileToByteArray(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"Montanhas.jpeg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(ReturnMessage.PARAMETER_INVALID, indexer.addImage(metaDocument, imageCorrompida));
	}

	
	/**
	 * Testa os retornos esperados do método addText
	 */
	@Test
	public void testAddImageTIF() {
		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc7");
		
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		BufferedImage imageTif = null;
		try {
			imageTif = Conversor.byteArrayToBufferedImage(Conversor.fileToByteArray(new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (3).tif")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(ReturnMessage.PARAMETER_INVALID, indexer.addImage(metaDocument, imageTif));
	}
	
	/**
	 * Testa os retornos esperados do método addText
	 * @throws IOException 
	 */
	@Test
	public void testAddImage() throws IOException {
		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc1");
		
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		
		
		assertEquals(ReturnMessage.SUCCESS, indexer.addImage(metaDocument, image));
		assertEquals(ReturnMessage.DUPLICATED_ID, indexer.addImage(metaDocument, image));
	}
	
	/**
	 * Testa se os metadados de uma imagem são atualizados
	 */
	@Test
	public void testUpdate() {
		indexer.delImage("image (6).jpg");
		Map<String, String> metadataMap = new HashMap<String, String>();
		metadataMap.put(Metadata.TITLE.getValue(), "Novo Título");
		ReturnMessage rm = indexer.updateImage("doc1", metadataMap);
		assertEquals(ReturnMessage.SUCCESS, rm);
		
		ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);
		IndexReader ir;
		try {
			Directory dir = FSDirectory.open(new File(Path.IMAGE_INDEX.getValue()));
			ir = IndexReader.open(dir, true);
			ImageSearchHits result = searcher.search(image, ir);
			String titulo = result.doc(0).get(Metadata.TITLE.getValue());
			ir.close();
			dir.close();			
			assertEquals("Novo Título", titulo); 
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Testa os retornos esperados do método delText
	 * @throws IOException 
	 */
	@Test
	public void testDelImage() throws IOException {
		
		
		File imageIndex = new File(Path.IMAGE_INDEX.getValue());
		assertTrue(Util.deleteDir(imageIndex));
		File imageBaxkup = new File(Path.IMAGE_BACKUP.getValue());
		assertTrue(Util.deleteDir(imageBaxkup));
		
		MetaDocument metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc9");
		
		indexer.addImage(metaDocument,image);
		assertEquals(ReturnMessage.SUCCESS, indexer.delImage("doc9"));
		assertEquals(ReturnMessage.ID_NOT_FOUND, indexer.delImage("doc1"));
	}
	
	/**
	 * Testa a indexação de uma imagem grande
	 */
	@Test
	public void testIndexacaoImagemGrande(){
		File fileImagemGrande = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()+"image (13).bmp");
		ImageIndexer indexer = ImageIndexerImpl.getImageIndexerImpl();
		
			MetaDocument metaDocument = new MetaDocument();
			metaDocument.setId(fileImagemGrande.getName());
			ReturnMessage result = null;
			try {
				BufferedImage buf = ImageIO.read(fileImagemGrande);
				if (buf != null)
				result = indexer.addImage(metaDocument, buf);
			} catch (Exception e) {
				System.out.println(fileImagemGrande.getName()+" ERROR " + result);
				e.printStackTrace();
			}

			assertEquals(ReturnMessage.SUCCESS, result);
	}
		
	/**
	 * Documento que será utilizado nos outros testes
	 */
	@Before
	public void tearUp(){
		metaDocument = new MetaDocument();
		metaDocument.setTitle("Documento de teste");
		metaDocument.setId("doc1");
		indexer.addImage(metaDocument, image);
	}
	

}
