package pt.utl.ist.repox.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtil {

	public static String sanitizeToValidFilename(String name) {
		String[] invalidSymbols = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
		String sanitizedName = name;
				
		for (String currentSymbol : invalidSymbols) {
			sanitizedName = sanitizedName.replaceAll("[\\" + currentSymbol + "]", "_");
		}
		
		return sanitizedName;
	}
	
	public static void cleanDir(File dir) throws FileNotFoundException, IOException {
		if(dir.exists()) {
			File[] outputDirFiles = dir.listFiles();
			if(outputDirFiles.length > 0) {
				for (File currentFile : outputDirFiles) {
					if(currentFile.isDirectory()) {
						pt.utl.ist.util.FileUtil.deleteDir(currentFile);
					} else {
						currentFile.delete();
					}
				}
			}
		} else {
			dir.mkdir();
		}
	}
	
	public static File[] getChangedFiles(Date fromDate, File[] files) {
		List<File> changedFilesList = getChangedFilesList(fromDate, files);
		File[] changedFiles = new File[changedFilesList.size()];
		changedFilesList.toArray(changedFiles);
		
		return changedFiles;
	}
	
	public static List<File> getChangedFilesList(Date fromDate, File[] files) {
		List<File> changedFiles = new ArrayList<File>();
		
		if(files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					changedFiles.addAll(getChangedFilesList(fromDate, file.listFiles()));
				}
				else if(isFileChanged(fromDate, file)) {
					changedFiles.add(file);
				}
			}
		}
		
		return changedFiles;
	}

	public static boolean isFileChanged(Date fromDate, File file) {
		if(fromDate == null || file.lastModified() > fromDate.getTime()) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
