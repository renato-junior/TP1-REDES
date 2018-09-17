package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author renato
 */
public class FileUtils {

    private BufferedReader fileReader;

    public FileUtils(String fileName) throws FileNotFoundException {
        this.fileReader = new BufferedReader(new FileReader(fileName));
    }
    
    public String getLine() throws IOException {
        return fileReader.readLine();
    }

}
