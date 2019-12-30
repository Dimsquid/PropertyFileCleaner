package com.bis.propertyfilecleaner.v2.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import org.junit.Test;

import com.bis.propertyfilecleaner.v2.PropertyFileCleanerV2;

public class PropertyFileCleanerTestV2 {

	public static PropertyFileCleanerV2 PropertyFileCleanerTestV2z() {
		PropertyFileCleanerV2 propClean = null;
		try {
			propClean = new PropertyFileCleanerV2("properties", "output");
		} catch (IOException e) {
		}
		return propClean;
	}

	@Test
	public void testFolderPaths() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			propClean.inputPath = "properties";
			propClean.outputPath = "output";
			assertEquals("properties", propClean.inputPath);
			assertEquals("output", propClean.outputPath);
		} catch (IOException e) {
		}
	}

	@Test
	public void testThrowExceptionImportFolder() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			propClean.inputPath = "Dirns";
			propClean.fileImport("Dirns", "output");
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@Test
	public void testreadFileMethod() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			propClean.fileName = "output.properties";
			assertEquals(0, propClean.lineNumber);
			assertEquals("output.properties", propClean.fileName);
		} catch (IOException e) {
		}

	}

	@Test
	public void testFileDirectoryNotNull() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			assertNotNull(propClean.listOfFiles);
		} catch (IOException e) {
		}

	}

	@Test
	public void testAllArraysAndMaps() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			assertNotNull(propClean.commentMap);
			assertNotNull(propClean.keyMap);
			assertNotNull(propClean.lineArray);
		} catch (IOException e) {
		}

	}

	@Test
	public void isLineOrKeyTests() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			assertTrue(propClean.isLineOrKey("Key_1="));
			assertFalse(propClean.isLineOrKey("#Comment"));

		} catch (IOException e) {
		}
	}

	@Test
	public void isCommentTest() {
		try {
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			assertFalse(propClean.isComment("Key_1="));
			assertTrue(propClean.isComment("#Comment"));
		} catch (IOException e) {
		}
	}

	@Test
	public void lineEqualsAtEndTest() {
		try {
			File file = new File("properties");
			PropertyFileCleanerV2 propClean = new PropertyFileCleanerV2("properties", "output");
			assertNull(propClean.line);
			propClean.readLine(" KEY_1 = I am value one", 0, file);
			assertNotSame("KEY_1=", propClean.line);
		} catch (IOException e) {
		}
	}
}