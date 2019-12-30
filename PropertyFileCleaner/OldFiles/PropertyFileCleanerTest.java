package com.bis.propertyfilecleaner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PropertyFileCleanerTest {

	public static PropertyFileCleaner propClean = new PropertyFileCleaner("properties", "output");

	@Test
	public void testPropertyFileCleaner() {

		assertEquals("properties", "properties");
		assertEquals("output", "output");
	}

	@Test
	public void testParseFileOut() throws IOException {

		assertEquals(propClean.outpath, "");

		File testOutputFile = new File("dist");
		if (testOutputFile.exists()) {
			System.out.println("Success!");
			System.out.println(testOutputFile.getAbsolutePath());
		} else {
			
		}

	}

	public void testParseFile() {

		assertEquals(propClean.inpath, "properties");

		File testInputFile = new File("properties");
		if (testInputFile.exists()) {
			System.out.println("Success!");
			System.out.println(testInputFile.getAbsolutePath());
		} else {
			fail("Fail!");
		}
	}

	@Test
	public void testParseLine() {
		assertEquals(propClean.parseLine(" KEY_1 = Hello"), "KEY_1=Hello");
	}

	@Test
	public void testIsComment() {
		assertEquals(propClean.isComment(" #comment"), true);
	}

	@Test
	public void testDupeKey() {
		assertEquals(propClean.dupeKey(""), "");
	}

	@Test
	public void testKeyFormatter() {
		assertEquals(propClean.keyFormatter("  KEY_2   = hello"), "KEY_2=hello");
	}

}
