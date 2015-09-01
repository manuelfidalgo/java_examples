
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {
    private static final String FILE_PATH = "example.txt";

    //read file line by line
    public void readLineByLine() throws FileNotFoundException, IOException {
        String line;
        try (InputStream fis = new FileInputStream(FILE_PATH);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);) {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    //read all lines in a single step java >=1.7 
    public void readAllLines() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH),
                StandardCharsets.UTF_8);
        for (String line : lines) {
            System.out.println(line);
        }
    }

}
