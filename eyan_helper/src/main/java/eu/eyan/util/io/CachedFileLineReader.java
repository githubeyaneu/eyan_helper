package eu.eyan.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import eu.eyan.log.Log;
import eu.eyan.util.collection.MapsPlus;

/**
 * Only the start offsets going to be stored in an arraylist. 200 lines are
 * stored in a map-cache.
 */
public class CachedFileLineReader implements Iterable<String> {

	private static final int LINE_COUNT_EARLY_READ = 100;

	private FileChannel fileChannel;

	private FileInputStream fileInputStream;

	protected List<long[]> lineOffsets = Lists.newArrayList();

	protected Map<Integer, String> lineCache = MapsPlus.newMaxSizeHashMap(Runtime.getRuntime().availableProcessors() * LINE_COUNT_EARLY_READ);

	private String longestLine = "";

	private long fileLength;

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
			fileLength = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load(File file, Consumer<Integer> loadFileProgressChangedEvent) {
		synchronized (lineOffsets) {
			try {
				fileLength = file.length();
				System.out.println("Loading " + file + " " + fileLength + " bytes");
				FileLineStartOffsetReader lnr = new FileLineStartOffsetReader(file);
				long endIndex = 0;
				long startOffset = 0;
				long progressPercent = 0;
				int longestLineIndex = 0;
				long longestLineLength = 0;
				while (startOffset != -1) {
					startOffset = lnr.readLine();
					if (startOffset != -1) {
						endIndex = lnr.getOffset();
						lineOffsets.add(new long[] { startOffset, endIndex });
						if (endIndex - startOffset > longestLineLength) {
							longestLineIndex = lineOffsets.size() - 1;
							longestLineLength = endIndex - startOffset;
						}
					}

					final long newProgressPercent = fileLength == 0 ? 100 : endIndex * 100 / fileLength;
					if (progressPercent != newProgressPercent && loadFileProgressChangedEvent != null) {
						loadFileProgressChangedEvent.accept(Math.toIntExact(newProgressPercent));
						progressPercent = newProgressPercent;
					}
				}
				lnr.close();

				fileInputStream = new FileInputStream(file);
				fileChannel = fileInputStream.getChannel();
				longestLine = get(longestLineIndex);
				// if (fileLength != endIndex) { //special characters brake the
				// offsets
				// System.out.println("Length: " + fileLength + " endOffset:" +
				// endIndex + " ");
				// System.err.println("Error at loading file. There are newline problems! ");
				// }
				// System.gc();// wow... Test for big files again if it is
				// really necessary
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getLongestLine() {
		return longestLine;
	}

	public int size() {
		synchronized (lineOffsets) {
			return lineOffsets.size();
		}
	}

	public String get(int index) {
		String line = lineCache.get(index);
		if (line == null) {
			try {
				synchronized (lineOffsets) {
					for (int i = index; i < lineOffsets.size() && i < index + LINE_COUNT_EARLY_READ; i++) {
						String nextLine = readFromFile(i);
						lineCache.put(i, nextLine);
						if (i == index) {
							line = nextLine;
						}
					}
				}
			} catch (IOException e) {
				Log.error(e);
			}
		}
		return line;
	}

	private String readFromFile(int index) throws IOException {
		long start = lineOffsets.get(index)[0];
		long end = lineOffsets.get(index)[1];
		ByteBuffer dst = ByteBuffer.allocate(Math.toIntExact(end - start));
		fileChannel.read(dst, start);
		String line = new String(dst.array(), Charset.forName("UTF-8"));
		return line;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				synchronized (lineOffsets) {
					return index < lineOffsets.size();
				}
			}

			@Override
			public String next() {
				if(size() != 0) Log.debug("Line "+(index+1)+" "+(100*(index+1)/size())+"%");
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