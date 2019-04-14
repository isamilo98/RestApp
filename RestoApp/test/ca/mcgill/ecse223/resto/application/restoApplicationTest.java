package ca.mcgill.ecse223.resto.application;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse223.resto.model.RestoApp;

public class restoApplicationTest {
	
	private static String filename = "testdata.resto";
	
	@BeforeClass
	public static void setUpOnce() {
		RestoApplication.setFilename(filename);
	}
	
	@Before
	public void setUp() {
		// remove test file
		File f = new File(filename);
		f.delete();
		// clear all data
		RestoApp resto = RestoApplication.getRestoApp();
		resto.delete();
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
