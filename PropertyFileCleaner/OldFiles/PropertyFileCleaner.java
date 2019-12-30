package com.bis.propertyfilecleaner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PropertyFileCleaner {

	public String inpath = "";
	public String outpath = "";
	static ArrayList<String> commentGrabber = new ArrayList<>();
	static Map<String, String> keyList = new HashMap<>();
	static ArrayList<Integer> commentLine = new ArrayList<>();
	static ArrayList<String> commentDuplicates = new ArrayList<>();
	static Map<String, String> commentDupe = new HashMap<>();
	public String lineBeforeIt = "";
	public int lineNumber = 0;
	public String line;
	public BufferedReader in;
	public BufferedWriter out;
	public String fileName;
	public int previousLineNum;
	public String dupes = "";
	public boolean finishDupeCheck = false;

	public PropertyFileCleaner(String inputPath, String outputPath) {
		File newOutput = new File(outputPath);
		if (!newOutput.exists()) {
			newOutput.mkdir();
		}
		File inputFolder = new File(inputPath);
		File outputFolder = new File(outputPath);

		if (!inputFolder.exists()) {
			try {
				throw new FileNotFoundException("The path " + inputFolder.getAbsolutePath() + " not found");
			} catch (FileNotFoundException e) {

			}
		}
		propertyFileImporter(inputFolder, outputFolder);

		duplicateReport();
	}

	public void propertyFileImporter(File inputFolder, File outputFolder) {

		try {
			File[] listOfFiles = inputFolder.listFiles();
			for (File file : listOfFiles) {
				parseFile(file, outputFolder);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseFileOut() throws IOException {
		out.write(parseLine(line) + System.getProperty("line.separator"));
	}

	public void parseFile(File file, File outputFolder) throws IOException {

		if (file.isFile()) {
			lineNumber = 0;
			fileName = file.getName().toString();
			in = new BufferedReader(new FileReader(file));
			out = new BufferedWriter(new FileWriter(outputFolder + "/" + file.getName()));
			line = in.readLine();
			while (line != null) {
				line = dupeKey(line);
				lineNumber++;
				if (finishDupeCheck) {
					System.out.println(dupes);
					out.close();
					File fileOutput = new File(outputFolder + "/" + file.getName());
					fileOutput.delete();
					throw new RuntimeException();
				} else {
					multipleLineFormatter(line);
				}
				line = in.readLine();

			}
			in.close();
			out.close();
		}
	}

	public String parseLine(String line) {

		if (isComment(line)) {
			line = line.trim();
		} else {
			line = keyFormatter(line);
		}

		lineBeforeIt = line;
		duplicateWarning(line);

		return line;
	}

	public boolean isComment(String line) {
		return (line.trim().startsWith("#"));
	}

	public String dupeKey(String line) {
		previousLineNum = lineNumber - 1;

		if (previousLineNum == -1) {
			keyList.clear();
		}

		if (line.contains("=")) {

			String lineChange = line;
			String[] parts = lineChange.split("=");

			parts[0] = parts[0].trim();
			parts[1] = parts[1].trim();

			if (keyList.containsKey(parts[0]) && keyList.containsValue(parts[1])) {
				line = line.replace(line, "");

			} else if (keyList.containsKey(parts[0]) && !keyList.containsValue(parts[1])) {
				String keyValues = "";
				if (keyList.containsKey(parts[0])) {

					keyValues += keyList.get(parts[0]) + " on line: \n";
					keyValues += parts[1].toString() + " on line: " + lineNumber;

				}

				dupes += "\nDuplicate key " + parts[0] + " found in file " + fileName + " with values: \n" + keyValues
						+ "\n";

				finishDupeCheck = true;
			} else {
				keyList.put(parts[0], parts[1]);
			}

		}
		return line;
	}

	public String keyFormatter(String line) {
		String lineChange = line;
		String[] parts = lineChange.split("=");

		if (line.contains("=")) {
			line = parts[0].trim() + "=" + parts[1].trim();
		}
		return line;
	}

	public void multipleLineFormatter(String line) throws IOException {
		if (!line.equals(lineBeforeIt.toString())) {
			parseFileOut();
		}
	}

	public void duplicateWarning(String line) {

		if (line.startsWith("#")) {
			String commentValue = line.toString();
			commentValue = commentValue + " " + fileName;
			String lineNumberString = "";
			lineNumberString = "" + lineNumber + "";
			String existing = commentDupe.get(commentValue);
			String extraContent = lineNumberString;

			if (!commentDupe.containsKey(commentValue)) {
				commentDupe.put(commentValue, lineNumberString);
			} else {

				commentDupe.put(commentValue, existing == null ? extraContent : existing + ", " + extraContent);
				commentDuplicates.add("Duplicate Comment: " + commentValue + " found on line: "
						+ commentDupe.get(commentValue) + " from property file " + fileName);
			}
		}
	}

	public void duplicateReport() {
		for (int i = 0; i < commentDuplicates.size(); i++) {
			System.out.println(commentDuplicates.get(i));
		}

	}

}
