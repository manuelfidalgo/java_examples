

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class AudioTrimmer {

	private static final boolean saveAudio = true;
	private static final String sourceFileName = "*.wav";
	private static final String destinationFileFolder = "/tmp/";
	private static int name = 0;

	private static final float secondsToTrim = 0.05f;

	public static InputStream trimAudio(InputStream is) {
		InputStream newInputStream = null;

		try {

			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(is);

			AudioInputStream originalStream = AudioSystem
					.getAudioInputStream(is);
			AudioFormat format = originalStream.getFormat();

			float bytesPerSecond = format.getFrameSize()
					* format.getFrameRate();

			long toSkip = (long) (secondsToTrim * bytesPerSecond);

			originalStream.skip(toSkip);

			if (saveAudio) {

				File file = saveAudio(originalStream, fileFormat);
				newInputStream = new FileInputStream(file);
			}

		} catch (Exception e) {
			System.out.println("Exception!");
			e.printStackTrace();
		}

		return newInputStream;
	}

	private static File saveAudio(AudioInputStream shortenedStream,
			AudioFileFormat fileFormat) {
		name++;
		File destinationFile = new File(destinationFileFolder + name + ".wav");

		try {

			AudioSystem.write(shortenedStream, fileFormat.getType(),
					destinationFile);
		} catch (IOException e) {
			System.err.println("Could not write file in"
					+ destinationFile.getAbsolutePath());
			e.printStackTrace();
		}
		return destinationFile;
	}

	public static void main(String[] args) throws FileNotFoundException {

		InputStream is = AudioTrimmer.class.getResourceAsStream(sourceFileName);
        is= new BufferedInputStream(new FileInputStream(sourceFileName));
		
		
		try {
			AudioTrimmer.trimAudio(is);
		} catch (Exception e) {
			System.err.println("Could not get inputStream from file "
					+ sourceFileName);
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
