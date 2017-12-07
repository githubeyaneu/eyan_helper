package eu.eyan.filelistindex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CreateFileListInRoot {

	private static long counter=0;
	private static String[] filters = new String[]{"H","M","O","P","T"};
	private static Writer writerAll;;

	public static void main(String[] args) {
		try {
			String allFileName = "c:\\AllFileList.txt";
			FileWriter fwAll;
			fwAll = new FileWriter(allFileName, false);
			writerAll = new BufferedWriter(fwAll);
			
			File[] listRoots = File.listRoots();
			for (int i = 0; i < listRoots.length; i++) {
				File drive = listRoots[i];
				if (filter(drive)) {
					continue;
				}
				System.out.println(drive.getAbsolutePath());
				try {
					String fileName = drive.getAbsolutePath()+"fileList_"+drive.getAbsolutePath().charAt(0)+".txt";
					FileWriter fw = new FileWriter(fileName, false);
					Writer writer = new BufferedWriter(fw);
					explore(drive, writer);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			writerAll.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private static boolean filter(File pFile) {
		String absolutePath = pFile.getAbsolutePath();
		for (int i = 0; i < filters.length; i++) {
			String filter = filters[i];
			if(absolutePath.toLowerCase().startsWith(filter.toLowerCase())){
				return true;
			}
		} 
		return false;
	}

	private static void explore(File dir, Writer pWriter) throws IOException {
		write(pWriter, dir.getAbsolutePath());
		write(pWriter, "\\\r\n");
		File[] listFiles = dir.listFiles();
		if(listFiles == null){
		} else {
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				if(file.isDirectory()){
					count();
					explore(file, pWriter);
				}else {
					count();
					write(pWriter, file.getAbsolutePath());
					write(pWriter, ";"+file.getName());
					write(pWriter, ";"+file.length());
					write(pWriter, "\r\n");
				}
			}
		}
		
	}

	private static void count() {
		counter++;
		if(counter%10000 == 0){
			System.out.println(counter);
		}
	}

	private static void write(Writer pWriter, String msg) throws IOException {
		pWriter.write(msg);
		writerAll.write(msg);
	}

}



