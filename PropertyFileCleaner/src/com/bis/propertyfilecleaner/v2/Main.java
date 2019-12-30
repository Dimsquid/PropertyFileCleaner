package com.bis.propertyfilecleaner.v2;

import java.io.IOException;

public class Main {

	public static boolean boolDone = false;

	public static void main(String[] args) {
		String inputPath = "";
		String outputPath = "";

		String cmd = "";

		for (String arg : args) {

			switch (cmd.toLowerCase()) {

			case "-f":
				inputPath = arg;
				break;

			case "-o":
				outputPath = arg;
				break;
			default:
				// do nothing
			}

			cmd = arg;
		}

		try {
			@SuppressWarnings("unused")
			PropertyFileCleanerV2 propv2 = new PropertyFileCleanerV2(inputPath, outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
