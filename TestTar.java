
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.Test;

public class TestTar {

	@Test
	public void testBinEntryExists() throws Exception {

		String filename = "./build/distribution/Core3Console3G-3.1.9-SNAPSHOT-bundle.tgz";
		FileInputStream fis = new FileInputStream(filename);
		GZIPInputStream gis = new GZIPInputStream(fis);
		TarArchiveInputStream tarInput = new TarArchiveInputStream(gis);
		TarArchiveEntry entry;
		boolean existsBin = false;
		while (null != (entry = tarInput.getNextTarEntry()) && !existsBin) {

			if (entry.getName().equals("bin/"))
				existsBin = true;

			//System.out.println(entry.getName());
		}
		tarInput.close();
		System.out.println("encontrado?" + existsBin);
	}
}
