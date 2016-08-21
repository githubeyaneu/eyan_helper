package eu.eyan.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileLineStartOffsetReader implements Closeable {
	private static final char EGAL = 'x';
	private FileReader reader;
	private FileInputStream fis;
	private final char[] buffer;
	private final byte[] byteBuffer;
	int bufferOffset = 0;
	int bufferLength = 0;
	int byteBufferLength = 0;
	char lastChar = EGAL;
	long nextLineLength = 0L;
	boolean finished = true;
	private long offset = 0;

	public FileLineStartOffsetReader(String path, int bufferSize) throws FileNotFoundException {
		buffer = new char[bufferSize];
		byteBuffer = new byte[bufferSize];
		reader = new FileReader(path);
		fis = new FileInputStream(path);
		finished = false;
		offset = 0;
	}

	public FileLineStartOffsetReader(File file) throws FileNotFoundException {
		buffer = new char[8192];
		byteBuffer = new byte[8192];
		reader = new FileReader(file);
		fis = new FileInputStream(file);
		finished = false;
		offset = 0;
	}

	/**
	 * @return start offset from the line. After calling this method the end
	 *         offset of the line can be read out with the getOffset():long
	 * @throws IOException
	 */
	public long readLine() throws IOException {
		while (true) {
			int nextCharInt = nextChar();
			if (nextCharInt == -1) {
				if (nextLineLength == 0) {
					return -1;
				} else {
					long ret = offset;
					offset += nextLineLength;
					nextLineLength = 0;
					return ret;
				}
			} else {
				char nextChar = (char) nextCharInt;
				if (nextChar == '\n') {
					nextLineLength += 1;
					lastChar = EGAL;
					long ret = offset;
					offset += nextLineLength;
					nextLineLength = 0;
					return ret;
				} else if (lastChar == '\r') {
					long ret = offset;
					offset += nextLineLength;
					nextLineLength = 1;
					lastChar = nextChar;
					return ret;
				} else {
					nextLineLength += 1;
					lastChar = nextChar;
				}
			}
		}
	}

	private int nextChar() throws IOException {
		if (finished) {
			return -1;
		} else if (bufferOffset == byteBufferLength) {
			bufferLength = reader.read(buffer);
			byteBufferLength = fis.read(byteBuffer);
			bufferOffset = 0;
			if (byteBufferLength == -1) {
				finished = true;
				return -1;
			}
		}
		byte c = byteBuffer[bufferOffset++];
		return c;
	}

	@Override
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
			fis.close();
		}
	}

	public long getOffset() {
		return offset;
	}
}