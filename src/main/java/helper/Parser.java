package helper;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 * Created by Ayomitunde on 3/29/2017.
 */
public class Parser {
    private static final String DATA_DIR = "data/";
    private static final String RESULT_DIR = "result/";
    private static String solutionDirectory = "/Users/ayomitundefafore/Desktop/RandomTreeLearner/target/classes/result";
    private static HashSet<File> DATA_FILES;

    static
    {
        DATA_FILES = getFiles(Parser.class, DATA_DIR);
    }

    private static HashSet<File> getFiles(Class<?> passedInClass, String dir)
    {
        HashSet<File> files = new HashSet<>();
        ClassLoader classLoader = passedInClass.getClassLoader();
        File folder = new File(classLoader.getResource(dir).getFile());
        File [] listOfFiles = folder.listFiles();
        for(File f : listOfFiles)
        {
            files.add(f);
        }
        return files;
    }

    public static File getDataFile(String fileName)
    {
        for(File file : DATA_FILES)
        {
            if(file.getName().equals(fileName)) return file;
        }
        return null;
    }



    public static void saveSolution(JSONObject jsonObject) throws IOException {
        FileWriter fileWriter = new FileWriter(solutionDirectory);
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.flush();
        fileWriter.close();
    }
}
