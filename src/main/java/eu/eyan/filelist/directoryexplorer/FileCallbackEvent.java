package eu.eyan.filelist.directoryexplorer;

import java.io.File;

public class FileCallbackEvent {

	private File myFile;

	public FileCallbackEvent(File pFile) {
		myFile = pFile;
	}

	public File getFile() {
		return myFile;
	}

}
