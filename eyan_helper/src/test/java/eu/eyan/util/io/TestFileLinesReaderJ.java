package eu.eyan.util.io;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import eu.eyan.util.tuple.Tuple2;

public class TestFileLinesReaderJ {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private String filepath;

	@Before
	public void setUp() throws IOException {
		File file = folder.newFile();
		filepath = file.getAbsolutePath();
		PrintWriter writer = new PrintWriter(new File(filepath));
		// 0
		writer.write("line0 rn\r\n"); // 10 0 - 10
		writer.write("line1 r\r"); // 8 10 - 18
		writer.write("line2 n\n"); // 8 18 - 26
		writer.write("line34nr\n"); // 9 26 - 35
		writer.write("\r"); // 1 35 - 36
		writer.write("line56 rrn\r"); // 11 36 - 47
		writer.write("\r\n"); // 2 47 - 49
		writer.write("line7J"); // 6 49 - 55/-1
		writer.flush();
		writer.close();
	}

	@Test
	public void testStartOffsets() throws Exception {
		FileLineStartOffsetReader flr = new FileLineStartOffsetReader(filepath, 8192);
		assertThat(flr.readLine()).isEqualTo(0);
		assertThat(flr.readLine()).isEqualTo(10);
		assertThat(flr.readLine()).isEqualTo(18);
		assertThat(flr.readLine()).isEqualTo(26);
		assertThat(flr.readLine()).isEqualTo(35);
		assertThat(flr.readLine()).isEqualTo(36);
		assertThat(flr.readLine()).isEqualTo(47);
		assertThat(flr.readLine()).isEqualTo(49);
		assertThat(flr.readLine()).isEqualTo(-1);
		assertThat(flr.readLine()).isEqualTo(-1);
		flr.close();

		flr = new FileLineStartOffsetReader(filepath, 1);
		assertThat(flr.getOffset()).isEqualTo(0);
		assertThat(flr.readLine()).isEqualTo(0);
		assertThat(flr.readLine()).isEqualTo(10);
		assertThat(flr.readLine()).isEqualTo(18);
		assertThat(flr.readLine()).isEqualTo(26);
		assertThat(flr.readLine()).isEqualTo(35);
		assertThat(flr.readLine()).isEqualTo(36);
		assertThat(flr.readLine()).isEqualTo(47);
		assertThat(flr.readLine()).isEqualTo(49);
		assertThat(flr.readLine()).isEqualTo(-1);
		assertThat(flr.readLine()).isEqualTo(-1);
		flr.close();
	}

	@Test
	public void testOffsets() throws Exception {
		FileLineOffsetsReader flr = new FileLineOffsetsReader(filepath, 8192);
		assertThat(flr.getOffset()).isEqualTo(0);
		assertNextLineOffsets(flr, 0, 9);
		assertNextLineOffsets(flr, 10, 17);
		assertNextLineOffsets(flr, 18, 25);
		assertNextLineOffsets(flr, 26, 34);
		assertNextLineOffsets(flr, 35, 35);
		assertNextLineOffsets(flr, 36, 46);
		assertNextLineOffsets(flr, 47, 48);
		assertNextLineOffsets(flr, 49, 54);
		assertThat(flr.readLine()).isNull();
		assertThat(flr.readLine()).isNull();
		flr.close();

		flr = new FileLineOffsetsReader(filepath, 1);
		assertThat(flr.getOffset()).isEqualTo(0);
		assertNextLineOffsets(flr, 0, 9);
		assertNextLineOffsets(flr, 10, 17);
		assertNextLineOffsets(flr, 18, 25);
		assertNextLineOffsets(flr, 26, 34);
		assertNextLineOffsets(flr, 35, 35);
		assertNextLineOffsets(flr, 36, 46);
		assertNextLineOffsets(flr, 47, 48);
		assertNextLineOffsets(flr, 49, 54);
		assertThat(flr.readLine()).isNull();
		assertThat(flr.readLine()).isNull();
		flr.close();
	}

	private void assertNextLineOffsets(FileLineOffsetsReader flr, int startOffset, int endOffset) throws IOException {
		Tuple2<Long, Long> offsets = flr.readLine();
		assertThat(offsets.getStart()).isEqualTo(startOffset);
		assertThat(offsets.getEnd()).isEqualTo(endOffset);
	}

	@Test
	public void testGetLine() throws Exception {
		FileLinesReader flr = new FileLinesReader(filepath, 8192);
		assertThat(flr.getOffset()).isEqualTo(0);
		assertNextLine(flr, "line0 rn\r\n", 10);
		assertNextLine(flr, "line1 r\r", 18);
		assertNextLine(flr, "line2 n\n", 26);
		assertNextLine(flr, "line34nr\n", 35);
		assertNextLine(flr, "\r", 36);
		assertNextLine(flr, "line56 rrn\r", 47);
		assertNextLine(flr, "\r\n", 49);
		assertNextLine(flr, "line7J", 55);
		assertNextLine(flr, null, 55);
		assertNextLine(flr, null, 55);
		flr.close();

		flr = new FileLinesReader(filepath, 1);
		assertThat(flr.getOffset()).isEqualTo(0);
		assertNextLine(flr, "line0 rn\r\n", 10);
		assertNextLine(flr, "line1 r\r", 18);
		assertNextLine(flr, "line2 n\n", 26);
		assertNextLine(flr, "line34nr\n", 35);
		assertNextLine(flr, "\r", 36);
		assertNextLine(flr, "line56 rrn\r", 47);
		assertNextLine(flr, "\r\n", 49);
		assertNextLine(flr, "line7J", 55);
		assertNextLine(flr, null, 55);
		assertNextLine(flr, null, 55);
		flr.close();
	}

	private void assertNextLine(FileLinesReader flr, String nextLine, int expectedOffset) throws IOException {
		assertThat(flr.readLine()).isEqualTo(nextLine);
		assertThat(flr.getOffset()).isEqualTo(expectedOffset);
	}
}