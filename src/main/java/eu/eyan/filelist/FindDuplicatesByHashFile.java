package eu.eyan.filelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindDuplicatesByHashFile {

	private static final long MIN_SIZE = 100 * Sizes.MEGA;
	private static Map<String, List<Descriptor>> descriptors = new HashMap<String, List<Descriptor>>();

	public static void main(String[] args) {
		try {
			File f = new File("f:\\"+CreateHashList.HASH_LIST_FILENAME);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line!=null){
				processLine(line);
				line = br.readLine();
			}
			br.close();
			findDuplicates(descriptors);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void findDuplicates(Map<String, List<Descriptor>> pDescriptors) {
		Object[] keys =  ((Set<String>)pDescriptors.keySet()).toArray();
		Arrays.sort(keys, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				List<Descriptor> l1 = descriptors.get(o1);
				List<Descriptor> l2 = descriptors.get(o2);
				long diff = l1.get(0).length-l2.get(0).length;
				if (diff > 0){
					return -1;
				} else if(diff==0){
					return 0;
				} else{
					return 1;
				}
			}
		});
		long saveCounter = 0;
		for (Object string : keys) {
			List<Descriptor> list = pDescriptors.get(string);
			if(list.size()>1){
				saveCounter+=(list.size()-1)*list.get(0).length;
				System.out.println("Duplicate: ");
				for (Descriptor descriptor : list) {
					System.out.println("  "+descriptor);
				}
				System.out.println();
			}
		}
		System.out.println("Full save = "+saveCounter/2/Sizes.GIGA+"GB");
	}

	private static void processLine(String line) {
		String[] split = line.split(CreateHashList.SEPARATOR);
		if (split.length<5) {
			return;
		}
		Descriptor d = new Descriptor();
		d.type = split[0];
		d.hash = split[1];
		d.length = Long.parseLong(split[2]);
		d.file = split[3];
		
		if(d.length>MIN_SIZE){
			List<Descriptor> list = descriptors.get(d.hash);
			if (list == null) {
				list = new ArrayList<Descriptor>();
				descriptors.put(d.hash, list);
			} 
			list.add(d);
		}
	}
}

class Descriptor{
	public String file;
	public long length;
	public String hash;
	public String type;
	
	@Override
	public String toString() {
		String ret = type + ", " + length/Sizes.MEGA + "MB: \"" + file + "\" " + hash;
		return ret;
	}
}