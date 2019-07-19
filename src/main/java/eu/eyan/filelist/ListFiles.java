package eu.eyan.filelist;

import java.io.File;

import eu.eyan.filelist.directoryexplorer.DirectoryExplorer;
import eu.eyan.filelist.directoryexplorer.FileCallback;
import eu.eyan.filelist.directoryexplorer.FileCallbackEvent;

public class ListFiles {

	public void listFiles() {
		File f = new File("e:\\tmp\\adaptivity");
		FileCallback callback = new FileCallback() {
			@Override
			public void fileCallback(FileCallbackEvent f) {
				System.out.println(" --- " + f.getFile().getAbsolutePath());
			}
			@Override
			public void directoryCallBack(FileCallbackEvent f) {
				System.out.println(" --- " + f.getFile().getAbsolutePath());
			}
			@Override
			public void progressCallBack(long progress) {
							
			}
		};
		DirectoryExplorer.explore(f, callback);
	}
	
	public static void main(String[] args) {
		ListFiles lf = new ListFiles();
		lf.listFiles();
	}

}



