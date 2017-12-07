package eu.eyan.filelist;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateHashList {

	public static final String HASH_LIST_FILENAME = "hashList.txt";
	public static final String SEPARATOR = ";";
	
	public static long dirLength(File file) {
		long sum = 0;
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if(listFiles!=null)
			for (int i = 0; i < listFiles.length; i++) {
				File f = file.listFiles()[i];
				if (f.isFile()) {
					sum += f.length();
				}
			}
		}
		return sum;
	}

	public static String hashSmallForDir(File dir) {
		String ret = "";
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA");
			if (dir.isDirectory()) {
				File[] listFiles = dir.listFiles();
				if(listFiles!= null)
				for (int i = 0; i < listFiles.length; i++) {
					File f = listFiles[i];
					if (f.isFile()) {
						md.update(hashSmallForFile(f).getBytes());
						md.update(Long.toBinaryString(f.length()).getBytes());
					}
				}
			}
			
			byte[] sha = md.digest();
			for (int i = 0; i < sha.length; i++) {
				String hex = Integer.toHexString(sha[i]);
				if (hex.length() == 1)
					hex = "0" + hex;
				hex = hex.substring(hex.length() - 2);
				ret += hex;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return ret;
	}

 	public static String hashSmallForFile(File file) {
		String ret = null;
		try {
			if (file.isFile()) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				
				InputStream is = new FileInputStream(file);
				
				byte[] buffer = new byte[8192];
				int read = 0;
				is.skip(file.length()/2);//TODO ???
				if ((read = is.read(buffer)) > 0)
					md.update(buffer, 0, read);
				is.close();
				
				byte[] sha = md.digest();
				
				ret = "";
				for (int i = 0; i < sha.length; i++) {
					String hex = Integer.toHexString(sha[i]);
					if (hex.length() == 1)
						hex = "0" + hex;
					hex = hex.substring(hex.length() - 2);
					ret += hex;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return ret;
	}
}
