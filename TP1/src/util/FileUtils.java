package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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

    public FileUtils(String fileName, boolean noRead) throws FileNotFoundException {
        this.file = new File(fileName);
        if (!noRead) {
            this.fileReader = new Scanner(file);
        }
    }

    public String getLine() throws IOException {
        if (!hasNextLine()) {
            return null;
        }
        return fileReader.nextLine();
    }

    public boolean hasNextLine() {
        return fileReader.hasNextLine();
    }

    public void writeLine(String line) throws IOException {
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, true));
        fileWriter.append(line);
        fileWriter.append('\n');
        fileWriter.close();
    }

}
