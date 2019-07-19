package eu.eyan.filelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

import eu.eyan.filelist.directoryexplorer.DirectoryExplorer;
import eu.eyan.filelist.directoryexplorer.FileCallback;
import eu.eyan.filelist.directoryexplorer.FileCallbackEvent;

public class FileSearchByContent {

	public static void main(String[] args) {
		Random r = new Random();
		final String stringTosearch = String.valueOf( r.nextInt(10000000) );
		
		File f = new File("e:\\tmp\\adaptivity");
		searchFilesForContent(stringTosearch, f);

	}
	
	private static void fileFound(FileCallbackEvent e) {
		System.out.print("F: "+e.getFile().getName());
	}

	public static void searchFilesForContent(final String stringTosearch,
			File f) {
		FileCallback callback = new FileCallback() {
			@Override
			public void fileCallback(FileCallbackEvent e) {
				//System.out.print(".");
				try {
					FileReader fr = new FileReader(e.getFile());
					BufferedReader br = new BufferedReader(fr,64*1024);
					String line;
					line = br.readLine();
					while (line != null){
						if(line.contains(stringTosearch)){
							fileFound(e);
							br.close();
							return;
						}
						line = br.readLine();
					}
					br.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			@Override
			public void directoryCallBack(FileCallbackEvent e) {/*System.out.print("");*/}
			@Override
			public void progressCallBack(long progress) {
			}
		};
		DirectoryExplorer.explore(f, callback);
	}
	
}
