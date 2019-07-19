package eu.eyan.filelist.directoryexplorer;

import java.io.File;


public class DirectoryExplorer {

	private static long counter = 0;

	public static void explore(File inputFile, FileCallback callback){
		
		explore(inputFile, callback, 0);
	}

	private static void explore(File inputFile, FileCallback callback, int depth) {
		File f = inputFile;
		if (f.exists()) {
			if (f.isDirectory()) {
				callback.directoryCallBack(new FileCallbackEvent(f));
				File[] files = f.listFiles();
				if(files!=null)
				for (File file : files) {
					if(file != null){
						explore(file, callback, depth+1);
					}
				}
			} else {
				assert f.isFile();
				callback.fileCallback(new FileCallbackEvent(f));
			}
			callback.progressCallBack(++counter );
		}
	}
}
