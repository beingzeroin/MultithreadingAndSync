import java.util.*;
import java.io.*;

class Configuration{
    public static final String LOG_FOLDER_PATH = "./spring-boot-server-logs";
    public static final LogLevel LOG_LEVEL = LogLevel.DEBUG;
}

class FileUtility {
    public static List<File> getAllLogFiles(String folderPath){
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        return Arrays.asList(files);
    }
}

enum LogLevel{
    DEBUG, INFO, WARN, FATAL
}
class Logger{
    LogLevel logLevel;
    Logger(){
        logLevel = Configuration.LOG_LEVEL;
    }
    public void info(String msg){
        System.out.println(msg);
    }
    public void warn(String msg){
        System.out.println(msg);
    }
    public void debug(String msg){
        if(logLevel == LogLevel.DEBUG)
            System.out.println(msg);
    }
    public void fatal(String msg){
        System.out.println(msg);
    }
}

class FileWordCount {
    static Logger logger = new Logger();
    public static int wordCount(File file, String pattern){
        int currentCount = 0;
        String filePath = file.getAbsolutePath();
        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains(pattern))
                    currentCount++;
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        logger.debug(String.format("'%s' : '%s' => %d\n", filePath, pattern, currentCount));
        return currentCount;
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
        List<File> files = FileUtility.getAllLogFiles(Configuration.LOG_FOLDER_PATH);
        for(File file : files){
            count += FileWordCount.wordCount(file, pattern);
        }
        return count;
    }
}

class MultithreadedLogSearch implements ILogSearch{
    int count;
    public MultithreadedLogSearch(){
        count = 0;
    }
    public int logSearch(String pattern) throws Exception {
        List<File> files = FileUtility.getAllLogFiles(Configuration.LOG_FOLDER_PATH);
        List<Thread> threadsList = new ArrayList<>();
        for(File file : files){
            threadsList.add(new Thread(){
                public void run(){
                    count += FileWordCount.wordCount(file, pattern);
                }
            });
        }
        for(Thread t : threadsList)
            t.start();
        for(Thread t : threadsList)
            t.join();
        return count;
    }
}

class TimeDecorator implements ILogSearch{ 
    ILogSearch decoratee;
    static Logger logger = new Logger();
    public TimeDecorator(ILogSearch decoratee){
        this.decoratee = decoratee;
    }
    public int logSearch(String pattern) throws Exception {
        long startTime = System.currentTimeMillis();
        int ans = decoratee.logSearch(pattern);
        long endTime = System.currentTimeMillis();
        logger.info("Time Taken: "+ (endTime-startTime) + " ms");
        return ans;
    } 
}

public class BeingZeroLogSearch{
    public static void main(String args[]) throws Exception {
        Logger logger = new Logger();
        ILogSearch td = new TimeDecorator(new SingleThreadedLogSearch());
        String searchTerm = "MongoSocketOpenException";
        searchTerm = "mongodb";
        logger.info("\'"+searchTerm+"\'" + " occurs "+ td.logSearch(searchTerm) + " times.");
    
        td = new TimeDecorator(new MultithreadedLogSearch());
        logger.info("\'"+searchTerm+"\'" + " occurs "+ td.logSearch(searchTerm) + " times.");
        
    }
}