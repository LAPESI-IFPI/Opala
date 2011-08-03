package br.edu.ifpi.opala.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PathTest;

public class ConversorTest {

	static File pathBTM;
	static File pathJPG;

	@BeforeClass
	public static void initializa() {
		pathBTM = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()
				+ "image (1).bmp");
		pathJPG = new File(PathTest.IMAGE_REPOSITORY_TEST.getValue()
				+ "image(1).jpg");
		Assert.assertTrue(pathBTM.exists());
		Assert.assertTrue(pathJPG.exists());
	}

	@Test
	public void fileToByteArrayTest() {

		try {
			Conversor.fileToByteArray(pathBTM);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void byteArrayToBMPTest() {

		try {
			Conversor.byteArrayToBMP(Conversor.fileToByteArray(pathBTM));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void JpgToByteArrayToJpgTest() {
		byte[] image = null;
		try {
			image = Conversor.fileToByteArray(pathJPG);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] encoded = Base64.encodeBase64(image);
		byte[] decoded = Base64.decodeBase64(encoded);
		
		File file = new File("D:/teste.jpg");
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			os.write(decoded);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertArrayEquals(image, decoded);
		
	}

}
