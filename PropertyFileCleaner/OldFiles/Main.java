package com.bis.propertyfilecleaner;

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

		@SuppressWarnings("unused")
		PropertyFileCleaner propFileCleaner = new PropertyFileCleaner(inputPath, outputPath);

	}

}
