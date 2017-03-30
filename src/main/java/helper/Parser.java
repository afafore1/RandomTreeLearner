package helper;

import java.io.File;
import java.util.HashSet;

/**
 * Created by Ayomitunde on 3/29/2017.
 */
public class Parser {
    private static final String DATA_DIR = "data/";
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
}
