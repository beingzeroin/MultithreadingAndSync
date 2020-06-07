import java.util.*;
import java.io.*;

class Configuration{
    public static final String LOG_FOLDER_PATH = "./spring-boot-server-logs";
}

class FileUtility {
    public static List<String> getAllLogFiles(String folderPath){
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<String> filePaths = new ArrayList<>();
        for(File f : files)
            filePaths.add(f.getAbsolutePath());
        return filePaths;
    }
}


class SingleThreadedLogSearch{
    public SingleThreadedLogSearch(){
    }
    public int logSearch(String pattern) throws Exception {
        int count = 0;
        List<String> filePaths = FileUtility.getAllLogFiles(Configuration.LOG_FOLDER_PATH);
        for(String path : filePaths){
            System.out.println("Searching "+path);
            BufferedReader br = new BufferedReader(new FileReader(path));
            int currentCount = 0;
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(pattern))
                    currentCount++;
            }
            count += currentCount;
        }
        return count;
    }
}


public class BeingZeroLogSearch{
    public static void main(String args[]) throws Exception {
        SingleThreadedLogSearch stls = new SingleThreadedLogSearch();
        System.out.println(stls.logSearch("MongoSocketOpenException"));
    }
}