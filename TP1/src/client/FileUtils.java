package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author renato
 */
public class FileUtils {

    private File file;
    private Scanner fileReader;

    public FileUtils(String fileName) throws FileNotFoundException {
        this.file = new File(fileName);
        this.fileReader = new Scanner(file);
    }
    
    public String getLine() throws IOException {
        if(!hasNextLine()) {
            return null;
        }
        return fileReader.nextLine();
    }
    
    public boolean hasNextLine() {
        return fileReader.hasNextLine();
    }

}
