

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class NFSUtils {

	private static final String USER_HOME_PATH = System
			.getProperty("user.home");

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");

	private static final String CACHE_DIR_PATH = USER_HOME_PATH
			+ FILE_SEPARATOR + "audio_cache" + FILE_SEPARATOR;

	private static final String PATH_BEGIN = "/data/begin";
	private static final String MAPPED_DRIVE = "Z:";

	/**
	 * Convert path of files to match our NFS "infrastructure"
	 * 
	 * @param originalPath
	 *            the path to translate
	 * @return String the path converted
	 */
	public static String getPath(String originalPath) {

		String path = null;
		try {
			final File localFile = getFile(originalPath);
			path = localFile.getAbsolutePath();
		} catch (IOException e) {
			// TODO what we do with exception?
			e.printStackTrace();
		}

		return path;
	}

	/**
	 * Obtains the NFS path to a file
	 * @param originalPath original path to be "translated"
	 * @return the translated path
	 */
	private static String getNFSPath(String originalPath) {
		String newPath = originalPath;
		if (OSUtils.isWindows()) {
			newPath = originalPath.replace(PATH_BEGIN, MAPPED_DRIVE);
			newPath = newPath.replace("/", FILE_SEPARATOR);
		}

		return newPath;
	}
	
	protected static File getLocalFile (String originalPath){
		return new File(CACHE_DIR_PATH + originalPath);
	}

	// Obtain a local reference to the file
	// If it exists in the cache --> return a reference to it
	// else --> copy it to the cache dir and return a reference to the new file
	private static File getFile(String originalPath) throws IOException {
		final File localFile = getLocalFile(originalPath);

		if (localFile.exists() == false) {
			File remoteFile = new File(getNFSPath(originalPath));
			// copy to local cache
			FileUtils.copyFile(remoteFile, localFile);

		}
		// always return local file
		return localFile;

	}

}
