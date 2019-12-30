package com.bis.propertyfilecleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class PropertyFileCleanerTask extends Task {

	String outDir;
	private List<FileSet> filesets = new ArrayList<>();

	@Override
	public void execute() throws BuildException {

		for (int i = 0; i < filesets.size(); i++) {

			FileSet fs = filesets.get(i);
			String foundLocation = "";

			DirectoryScanner ds = fs.getDirectoryScanner();

			for (String includedFile : ds.getIncludedFiles()) {

				String filename = includedFile.replace('\\', '/');

				filename = filename.substring(filename.lastIndexOf("/") + 1);

				String file = filename;

				if (foundLocation == "" && file.equals(filename)) {
					File base = ds.getBasedir();
					File found = new File(base, includedFile);

					foundLocation = found.getAbsolutePath();

					foundLocation = foundLocation.substring(0, foundLocation.lastIndexOf("\\") + 1);

					@SuppressWarnings("unused")
					PropertyFileCleaner propFileClean = new PropertyFileCleaner(foundLocation, this.getOutDir());
				}
			}

		}
	}

	public void setOutDir(String outDir) {
		this.outDir = outDir;
	}

	public String getOutDir() {
		return outDir;
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

}