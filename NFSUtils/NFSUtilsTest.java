


import java.io.File;

import org.junit.Test;

import static org.junit.Assert.*;

public class NFSUtilsTest {

	public static final String EXISTING_FILE_PATH = "/data/begin/1.txt";

	@Test
	public void testConvertPath() {
		String newPath = NFSUtils.getPath(EXISTING_FILE_PATH);
		File f = new File(newPath);
		assertTrue("File " + newPath + " doesn't exist", f.exists());
	}

	@Test
	public void testLoadFile() {

		File localFile = NFSUtils.getLocalFile(EXISTING_FILE_PATH);

		boolean localFileDeleted = localFile.delete();
		assertTrue("local file couldn't be deleted", localFileDeleted);

		String filePath = NFSUtils.getPath(EXISTING_FILE_PATH);
		File theFile = new File(filePath);
		assertTrue("File " + theFile + " doesn't exist", theFile.exists());
	}

}
