package eu.eyan.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import eu.eyan.util.tuple.Tuple2;

public class FileLineOffsetsReader implements Closeable {
	private FileReader reader;
	private final char[] buffer;
	int bufferOffset = 0;
	int bufferLength = 0;
	Character lastChar = '_';
	long nextLineLength = 0L;
	boolean finished = true;
	private long offset = 0;

	public FileLineOffsetsReader(String path, int bufferSize) throws FileNotFoundException {
		buffer = new char[bufferSize];
		reader = new FileReader(path);
		finished = false;
		offset = 0;
	}

	public FileLineOffsetsReader(File file) throws FileNotFoundException {
		buffer = new char[8192];
		reader = new FileReader(file);
		finished = false;
		offset = 0;
	}

	public Tuple2<Long, Long> readLine() throws IOException {
        while (true)
        {
            Character nextChar = nextChar();
            if (nextChar == null)
            {
                if (nextLineLength == 0)
                {
                    return null;
                }
                else
                {
                	Tuple2<Long, Long> ret = Tuple2.of(offset, offset+nextLineLength-1);
                	offset += nextLineLength;
                	nextLineLength=0;
            		return ret;
                }
            }
            else
            {
                if (lf(nextChar))
                {
            		nextLineLength+=1;
            		lastChar = null;
                	Tuple2<Long, Long> ret = Tuple2.of(offset, offset+nextLineLength-1);
                	offset += nextLineLength;
                	nextLineLength=0;
            		return ret;
                }
                else if (cr(lastChar))
                {
                	Tuple2<Long, Long> ret = Tuple2.of(offset, offset+nextLineLength-1);
                	offset += nextLineLength;
            		nextLineLength = 1;
            		lastChar = nextChar;
            		return ret;
                }
                else
                {
                	nextLineLength+=1;
            		lastChar = nextChar;
                }
            }
        }
    }

	private Character nextChar() throws IOException {
		if (finished) {
			return null;
		} else if (bufferOffset == bufferLength) {
			bufferLength = reader.read(buffer);
			bufferOffset = 0;
			if (bufferLength == -1) {
				finished = true;
				return null;
			}
		}
		char c = buffer[bufferOffset++];
		return c;
	}

	private boolean cr(Character c) {
		return c != null && c == '\r';
	}

	private boolean lf(Character c) {
		return c != null && c == '\n';
	}

	public long getOffset() {
		return offset;
	}

	@Override
	public void close() throws IOException {
		if (reader != null) {
			reader.close();
		}
	}

}