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

interface ILogSearch {
    public int logSearch(String pattern) throws Exception;
}
class SingleThreadedLogSearch implements ILogSearch{
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

class TimeDecorator implements ILogSearch{ 
    ILogSearch decoratee;
    public TimeDecorator(ILogSearch decoratee){
        this.decoratee = decoratee;
    }
    public int logSearch(String pattern) throws Exception {
        long startTime = System.currentTimeMillis();
        int ans = decoratee.logSearch(pattern);
        long endTime = System.currentTimeMillis();
        System.out.println("Time Taken: "+ (endTime-startTime) + " ms");
        return ans;
    } 
}

public class BeingZeroLogSearch{
    public static void main(String args[]) throws Exception {
        ILogSearch stls = new SingleThreadedLogSearch();
        ILogSearch td = new TimeDecorator(stls);
        String searchTerm = "MongoSocketOpenException";
        searchTerm = "mongodb";
        System.out.println("\'"+searchTerm+"\'" + " occurs "+ td.logSearch(searchTerm) + " times.");
    }
}