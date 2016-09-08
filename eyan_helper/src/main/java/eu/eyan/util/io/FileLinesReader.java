package eu.eyan.util.io;


/**
 * Not used anymore
 *
 */
public class FileLinesReader /* implements Closeable */{
	// private final FileReader reader;
	// private final char[] buffer;
	// private int bufferOffset = 0;
	// private int bufferLength = 0;
	// private Character lastChar = '_';
	// private final StringBuffer nextLine = new StringBuffer(256);
	// private boolean finished = true;
	// private int offset = 0;
	// private String line = null;
	//
	// public FileLinesReader(String path, int bufferSize) throws
	// FileNotFoundException {
	// buffer = new char[bufferSize];
	// reader = new FileReader(path);
	// finished = false;
	// offset = 0;
	// }
	//
	// public FileLinesReader(File file) throws FileNotFoundException {
	// buffer = new char[8192];
	// reader = new FileReader(file);
	// finished = false;
	// offset = 0;
	// }
	//
	// public String readLine() throws IOException {
	// while (true) {
	// Character nextChar = nextChar();
	// if (nextChar == null) {
	// if (nextLine.length() == 0) {
	// return null;
	// } else {
	// return returnActualLine();
	// }
	// } else {
	// if (lf(nextChar)) {
	// return handleLF(nextChar);
	// } else if (cr(lastChar)) {
	// return handleCR(nextChar);
	// } else {
	// handleSingleCharacter(nextChar);
	// }
	// }
	// }
	// }
	//
	// private void handleSingleCharacter(Character nextChar) {
	// nextLine.append(nextChar);
	// lastChar = nextChar;
	// }
	//
	// private String handleCR(Character nextChar) {
	// line = nextLine.toString();
	// resetNextLine();
	// handleSingleCharacter(nextChar);
	// offset += line.length();
	// return line;
	// }
	//
	// private String handleLF(Character nextChar) {
	// nextLine.append(nextChar);
	// lastChar = null;
	// return returnActualLine();
	// }
	//
	// private String returnActualLine() {
	// line = nextLine.toString();
	// resetNextLine();
	// offset += line.length();
	// return line;
	// }
	//
	// private void resetNextLine() {
	// nextLine.setLength(0);
	// }
	//
	// private Character nextChar() throws IOException {
	// if (finished) {
	// return null;
	// } else if (bufferOffset == bufferLength) {
	// bufferLength = reader.read(buffer);
	// bufferOffset = 0;
	// if (bufferLength == -1) {
	// finished = true;
	// return null;
	// }
	// }
	// char c = buffer[bufferOffset++];
	// return c;
	// }
	//
	// private boolean cr(Character c) {
	// return c != null && c == '\r';
	// }
	//
	// private boolean lf(Character c) {
	// return c != null && c == '\n';
	// }
	//
	// public long getOffset() {
	// return offset;
	// }
	//
	// @Override
	// public void close() throws IOException {
	// if (reader != null) {
	// reader.close();
	// }
	// }
}