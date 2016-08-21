package eu.eyan.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.eyan.util.collection.MapsPlus;
import eu.eyan.util.tuple.Tuple2;

/**
 * The start and end offsets of the lines are going to be stored in a hashmap.
 * 200 lines are stored in a map-cache.
 */
public class CachedFileLineReaderHashMap implements Iterable<String> {

	private FileChannel fileChannel;

	private FileInputStream fileInputStream;

	private Map<Integer, Tuple2<Long, Long>> lineOffsets = MapsPlus.newMap();

	private Map<Integer, String> lineCache = MapsPlus.newMaxSizeHashMap(200);

	private String longestLine = "";

	public synchronized void close() {
		try {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (fileChannel != null) {
				fileChannel.close();
			}
			lineOffsets.clear();
			lineCache.clear();
			longestLine = "";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void load(File file, Consumer<Integer> loadFileProgressChangedEvent) {
		try {
			long fileLength = file.length();
			System.out.println("Loading " + file + " " + fileLength + " bytes");
			FileLineOffsetsReader lnr = new FileLineOffsetsReader(file);
			long endIndex = 0;
			Tuple2<Long, Long> line = Tuple2.of(0L, 0L);
			long progressPercent = 0;
			int longestLineIndex = 0;
			long longestLineLength = 0;
			while (line != null) {
				line = lnr.readLine();
				if (line != null) {
					lineOffsets.put(lineOffsets.size(), line);
					endIndex = line.getEnd();
					if (line.getEnd()-line.getStart() > longestLineLength){
						longestLineIndex = lineOffsets.size()-1;
						longestLineLength=line.getEnd()-line.getStart();
					}
				}
				
				final long newProgressPercent = endIndex * 100 / fileLength;
				if (progressPercent != newProgressPercent) {
					loadFileProgressChangedEvent.accept(Math.toIntExact(newProgressPercent));
					progressPercent = newProgressPercent;
				}
			}
			lnr.close();
			
			fileInputStream = new FileInputStream(file);
			fileChannel = fileInputStream.getChannel();
			longestLine = get(longestLineIndex);
			System.out.println("Loading done. " + (endIndex+1) + " bytes.");
			if (fileLength != (endIndex+1)) {
				// FIXME special characters brake the offsets
				System.out.println("Length: " + fileLength + " lines:" + (endIndex+1) + " ");
				System.err.println("Error at loading file. There are newline problems! ");
			}
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	// We dont need start and end offsets
	//	public synchronized void load(File file, Consumer<Integer> loadFileProgressChangedEvent) {
//		try {
//			long fileLength = file.length();
//			System.out.println("Loading " + file + " " + fileLength + " bytes");
//			FileLineOffsetsReader lnr = new FileLineOffsetsReader(file);
//			long endIndex = 0;
//			Tuple2<Long, Long> line = Tuple2.of(0L, 0L);
//			long progressPercent = 0;
//			int longestLineIndex = 0;
//			long longestLineLength = 0;
//			while (line != null) {
//				line = lnr.readLine();
//				if (line != null) {
//					lineOffsets.put(lineOffsets.size(), line);
//					endIndex = line.getEnd();
//					if (line.getEnd()-line.getStart() > longestLineLength){
//						longestLineIndex = lineOffsets.size()-1;
//						longestLineLength=line.getEnd()-line.getStart();
//					}
//				}
//				
//				final long newProgressPercent = endIndex * 100 / fileLength;
//				if (progressPercent != newProgressPercent) {
//					loadFileProgressChangedEvent.accept(Math.toIntExact(newProgressPercent));
//					progressPercent = newProgressPercent;
//				}
//			}
//			lnr.close();
//			
//			fileInputStream = new FileInputStream(file);
//			fileChannel = fileInputStream.getChannel();
//			longestLine = get(longestLineIndex);
//			System.out.println("Loading done. " + (endIndex+1) + " bytes.");
//			if (fileLength != (endIndex+1)) {
//				// FIXME special characters brake the offsets
//				System.out.println("Length: " + fileLength + " lines:" + (endIndex+1) + " ");
//				System.err.println("Error at loading file. There are newline problems! ");
//			}
//			System.gc();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	//Slow because of reading lines
//	public synchronized void load(File file, Consumer<Integer> loadFileProgressChangedEvent) {
//		try {
//			long fileLength = file.length();
//			System.out.println("Loading " + file + " " + fileLength + " bytes");
//			FileLinesReader lnr = new FileLinesReader(file);
//			long start = 0;
//			String line = "";
//			long progressPercent = 0;
//			while (line != null) {
//				line = lnr.readLine();
//				if (line != null) {
//					long len = line.getBytes().length;
//					lineOffsets.put(lineOffsets.size(), Tuple2.of(start, start + len));
//					start += len;
//					if (line.length() > longestLine.length())
//						longestLine = line;
//				}
//
//				final long newProgressPercent = start * 100 / fileLength;
//				if (progressPercent != newProgressPercent) {
//					loadFileProgressChangedEvent.accept(Math.toIntExact(newProgressPercent));
//					progressPercent = newProgressPercent;
//				}
//			}
//			lnr.close();
//
//			fileInputStream = new FileInputStream(file);
//			fileChannel = fileInputStream.getChannel();
//			System.out.println("Loading done. " + start + " bytes.");
//			if (fileLength != start) {
//				// FIXME special characters brake the offsets
//				System.out.println("Length: " + fileLength + " lines:" + start + " ");
//				System.err.println("Error at loading file. There are newline problems! ");
//			}
//			System.gc();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	// Not the best because does not take the newlines always correct (if cr and lf are mixed up)
	// public synchronized void load(File file, Consumer<Integer>
	// loadFileProgressChangedEvent) {
	// try {
	// long fileLength = file.length();
	// System.out.println("Loading " + file + " " + fileLength + " bytes");
	// fileInputStream = new FileInputStream(file);
	// String newLine = retrieveLineSeparator(file, fileInputStream);
	// FileReader fileReader = new FileReader(file);
	// LineNumberReader lnr = new LineNumberReader(fileReader);
	// long start = 0;
	// String line = "";
	// long progressPercent = 0;
	// while (line != null) {
	// line = lnr.readLine();
	// if (line != null) {
	// long len = line.getBytes().length + newLine.length();
	// lineOffsets.put(lineOffsets.size(), Tuple2.of(start, start + len));
	// start += len;
	// if (line.length() > longestLine.length())
	// longestLine = line;
	// }
	//
	// final long newProgressPercent = start * 100 / fileLength;
	// if (progressPercent != newProgressPercent) {
	// loadFileProgressChangedEvent.accept(Math.toIntExact(newProgressPercent));
	// progressPercent = newProgressPercent;
	// }
	// }
	// lnr.close();
	// fileChannel = fileInputStream.getChannel();
	// System.out.println("Loading done. " + start + " bytes.");
	// if(fileLength!=start){
	// // FIXME special characters brake the offsets
	// System.out.println("Length: "+ fileLength+" lines:"+start+ " ");
	// System.err.println("Error at loading file. There are newline problems!
	// ");
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public String getLongestLine() {
		return longestLine;
	}

	public int size() {
		return lineOffsets.size();
	}

	public synchronized String get(int index) {
		String line = lineCache.get(index);
		if (line == null) {
			try {
				Tuple2<Long, Long> pos = lineOffsets.get(index);
				ByteBuffer dst = ByteBuffer.allocate(Math.toIntExact(pos.getRight() - pos.getLeft()));
				fileChannel.read(dst, pos.getLeft());
				line = new String(dst.array(), Charset.forName("UTF-8"));
				lineCache.put(index, line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return line;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < lineOffsets.size();
			}

			@Override
			public String next() {
				String line = get(index);
				index++;
				return line;
			}
		};
	}

	public Matcher findFirst(String pattern) {
		Matcher matcher = Pattern.compile(pattern).matcher("");
		for (String line : this) {
			line = line.replaceAll("\r\n", "");
			matcher.reset(line);
			if (matcher.matches()) {
				return matcher;
			}
		}
		return null;
	}
}