package com.bis.propertyfilecleaner.v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

public class PropertyFileCleanerV2 {

	// map of unique comments and array of line numbers - map<string, list<int>>
	public Map<String, List<Integer>> commentMap = new LinkedHashMap<>();
	// map of keys and map of values and array of line number - map<string,
	// map<value, list<int>>>
	public Map<String, Map<String, List<Integer>>> keyMap = new LinkedHashMap<>();
	// lineArray list<string>
	public List<String> lineArray = new ArrayList<>();
	// warnings StringBuilder
	public StringBuilder warnings = new StringBuilder();
	// errors StringBuilder
	public StringBuilder errors = new StringBuilder();
	// bufferedreader
	public BufferedReader in;
	// filename
	public String fileName;
	// buffered writer
	public BufferedWriter out;
	// outPath
	public String inputPath;
	public String outputPath;

	public String key = "";
	public String value = "";
	public String line;
	public File[] listOfFiles;

	public int lineNumber;

	public PropertyFileCleanerV2(String inputPath, String outputPath) throws IOException {
		fileImport(inputPath, outputPath);
	}

	// file import
	public void fileImport(String inputPath, String outputPath) throws IOException {
		File inputFolder = new File(inputPath);
		if (!inputFolder.exists()) {
			try {
				throw new FileNotFoundException("The path " + inputFolder.getAbsolutePath() + " not found");
			} catch (FileNotFoundException e) {

			}
		}
		listOfFiles = inputFolder.listFiles();
		for (File file : listOfFiles) {
			readFile(file, outputPath);
		}

	}

	public void readFile(File file, String outputPath) throws IOException {
		if (file.isFile()) {
			int lineNumber = 0;
			fileName = file.getName();
			in = new BufferedReader(new FileReader(file));
			line = in.readLine();
			while (line != null) {
				lineNumber++;
				readLine(line, lineNumber, file);
				line = in.readLine();
			}
			output(file, outputPath);
			in.close();
			commentMap.clear();
			keyMap.clear();
			lineArray.clear();
		}
		System.out.println(warnings);
	}

	// read line
	public void readLine(String line, int lineNumber, File file) throws IOException {
		key = key.trim();
		value = value.trim();
		List<Integer> lineNumAddComment = new ArrayList<Integer>();
		// if comment
		if (isComment(line)) {
			// trim line
			line = line.trim();
			// if line exists in comments map
			if (commentMap.containsKey(line)) {
				// get array from comments map and add line number to it
				lineNumAddComment = commentMap.get(line);
				// if line does not exist in comments map
			} else {
				// add to comments map with new array containing current line number
				lineNumAddComment = new ArrayList<>();
			}
			// add line number to array
			lineNumAddComment.add(lineNumber);
			// add array to map
			commentMap.put(line, lineNumAddComment);
			// add formatted line to linesArray
			lineArray.add(line);
			// if not comment
			// if line is key/value
		} else if (isLineOrKey(line)) {
			String lineChange = line;
			// separate line into key and value by splitting on equals
			String inBits[] = lineChange.split("=");
			// trim key
			key = inBits[0].trim();
			// trim value
			if(inBits.length > 1) {
				value = inBits[1];
			}else {
				warnings.append("\n Key "+key+" with no value, on line "+lineNumber);
			}
			key = key.trim();
			
			line = key.trim() + "=" + value;
			// if key exists in key/value line numbers map
			if (keyMap.containsKey(key)) {
				// get map for current key from map, get key (which is actually the value you're
				// checking)
				Map<String, List<Integer>> valueMap = keyMap.get(key);
				List<Integer> lineNumAddKey;
				if (valueMap.containsKey(value)) {

					warnings.append(
							"\nDuplicate key and value:|" + line + " | found in file: " + fileName + " on lines: \n");

					for (int v = 0; v < valueMap.size(); v++) {

						for (String val : valueMap.keySet()) {
							List<Integer> lineNumbers = valueMap.get(val);

							for (int l = 0; l < lineNumbers.size(); l++) {

								warnings.append(lineNumbers.get(l) + ". " + key + "=" + val + "\n");
								warnings.append(lineNumber + ". " + line + "\n");
								warnings.trimToSize();
							}
						}
					}
					line = line.replace(line, "");
				}
				// if key is null
				if (valueMap.get(value) == null) {
					// create an array of ints
					lineNumAddKey = new ArrayList<>();
				} else {
					// add array to map
					lineNumAddKey = valueMap.get(value);
				}
				// add line number to array
				lineNumAddKey.add(lineNumber);
				// add array to map
				valueMap.put(value, lineNumAddKey);

				keyMap.put(key, valueMap);

				// if key does not exists in map
			} else if (!keyMap.containsKey(key)) {
				Map<String, List<Integer>> valueMap = new LinkedHashMap<>();
				List<Integer> lineNumAddKey = new ArrayList<>();
				lineNumAddKey.add(lineNumber);
				valueMap.put(value, lineNumAddKey);
				keyMap.put(key, valueMap);
			}
			// add formatted line to linesArray
			lineArray.add(line);
			// if line is not key/value (must be a blank)
		} else if (!isLineOrKey(line)) {
			// if last element of lines array is not equal to "\n"
			try {
				if (!lineArray.get(lineArray.size() - 1).equals("")) {
					// add "\n" to linesArray
					lineArray.add("\n".trim());
				}
			} catch (IndexOutOfBoundsException e) {

			}
		}

		// Loop map of keys and map of values and array of line number (map<string,
		// map<value, list<int>>>)
		for (int i = 0; i < keyMap.size(); i++) {
			// get value for current key - should be of type map<value, list<int>>
			keyMap.get(key);
			// if map has > 1 keys (this means we have different values for the same key)
			Map<String, List<Integer>> valueMap = keyMap.get(key);

			if (valueMap.size() > 1) {
				// add message to errors list "Duplicate key *keyname* found in file *filename*
				// with different values:"
				errors.append("\nDuplicate key " + key + " found in file " + fileName + " with different values: \n");
				// loop map keys
				for (int v = 0; v < valueMap.size(); v++) {
					// get array for current key
					for (String val : valueMap.keySet()) {
						List<Integer> lineNumbers = valueMap.get(val);

						// loop array
						for (int l = 0; l < lineNumbers.size(); l++) {
							// add message to errors list "*lineNumber*. *mapkey*" <<< line number is the
							// current element in the array, mapkey is the key of the map i.e. the current
							// "value"
							errors.append(lineNumbers.get(l) + ". " + val + "\n");
							errors.trimToSize();
						}
					}

					// if errors.trim().length > 0
					if (errors.length() > 0) {
						try {
							// throw exception stating that keys with different values were found
							// System.err.println( errors )
							System.out.println(errors);
							throw new RuntimeException("Keys with different values were found.");
						} catch (RuntimeErrorException e) {
							e.printStackTrace();
						}
						// if errors.trim().length == 0
					} else {

					}
				}

			}
		}
		if (isComment(line)) {
			if (commentMap.containsKey(line)) {
				// loop map of comments line numbers
				// get array for current comment
				// if array length is > 1
				if (lineNumAddComment.size() > 1) {
					// add message to warnings "Duplicate comment "*comment*" found on lines:
					warnings.append("\nDuplicate comment " + line + " found on lines: \n");
					// loop array of line numbers
					for (int j = 0; j < lineNumAddComment.size(); j++) {

						Integer lineNumAdds = lineNumAddComment.get(j);
						// add message to warnings
						// line number
						String checkit = lineNumAdds + ". " + line + "\n";

						warnings.append(checkit);
						warnings.trimToSize();

						// System.out.println( warnings )

					}
				}
			}
		}

	}

	public void output(File file, String outputPath) throws IOException {
		// loop lines array
		if (lineArray.contains("")) {
			for (int i = 0; i < lineArray.size(); i++) {
				for (int j = 0; j < lineArray.size(); j++) {
					if (lineArray.get(i).contains(lineArray.get(j)) && lineArray.get(j).equals("")) {
						lineArray.remove(j);
					}
				}
			}
		}
		File newOutput = new File(outputPath);
		if (!newOutput.exists()) {
			newOutput.mkdir();
		}
		File outputFolder = new File(outputPath);
		out = new BufferedWriter(new FileWriter(outputFolder + "/" + file.getName()));
		for (int la = 0; la < lineArray.size(); la++) {
			out.write(lineArray.get(la) + System.getProperty("line.separator"));
		}
		out.close();
	}

	public boolean isComment(String line) {
		return (line.startsWith("#"));
	}

	public boolean isLineOrKey(String line) {
		return (line.contains("="));
	}

	// write line to output file

	// done

	// map<string, map<string,list<int>>
	// key_1 = {I am value 1 = [1,2] }
	// key_2 = {I am value 2 = [5],
	// I am also value 2 = [6] }

	/*
	 * 
	 * line 1 key123=tedst line 2 key456=test spelt correctly line 3 key456=no spelt
	 * correctly
	 * 
	 * key123 = {tedst = [1]} key456 = {test spelt correctly = [2], {no spelt
	 * correctly = [3]}
	 * 
	 */
}
