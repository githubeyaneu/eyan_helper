package eu.eyan.util.io;


public class FileUtils {
	// Not used anymore:
	// public static String retrieveLineSeparator(File file, FileInputStream
	// fis) throws IOException {
	// char current;
	// String lineSeparator = "";
	// while (fis.available() > 0) {
	// current = (char) fis.read();
	// if ((current == '\n') || (current == '\r')) {
	// lineSeparator += current;
	// if (fis.available() > 0) {
	// char next = (char) fis.read();
	// if ((next != current) && ((next == '\r') || (next == '\n'))) {
	// lineSeparator += next;
	// }
	// }
	// return lineSeparator;
	// }
	// }
	// return "\r\n";
	// }
}