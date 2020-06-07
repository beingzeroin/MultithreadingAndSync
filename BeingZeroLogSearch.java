import java.util.*;
import java.io.*;

class Configuration{
    public static final String LOG_FOLDER_PATH = "./spring-boot-server-logs";
    public static final LogLevel LOG_LEVEL = LogLevel.INFO;
}

class FileUtility {
    public static List<File> getAllLogFiles(String folderPath){
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        return Arrays.asList(files);
    }
}

enum LogLevel{
    DEBUG(0), INFO(1), WARN(2), FATAL(3);
    int level;
    LogLevel(int level){
        this.level = level;
    }
    public int getLevel(){
        return level;
    }
}

class Logger{
    LogLevel logLevel;
    Logger(){
        logLevel = Configuration.LOG_LEVEL;
    }
    public void info(String msg){
        if(logLevel.getLevel() <= LogLevel.INFO.getLevel())
            System.out.println(msg);
    }
    public void warn(String msg){
        if(logLevel.getLevel() <= LogLevel.WARN.getLevel())
            System.out.println(msg);
    }
    public void debug(String msg){
        if(logLevel.getLevel() <= LogLevel.DEBUG.getLevel())
            System.out.println(msg);
    }
    public void fatal(String msg){
        if(logLevel.getLevel() <= LogLevel.FATAL.getLevel())
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
    long timeTaken;
    static Logger logger = new Logger();
    public TimeDecorator(ILogSearch decoratee){
        this.decoratee = decoratee;
    }
    public int logSearch(String pattern) throws Exception {
        long startTime = System.currentTimeMillis();
        int ans = decoratee.logSearch(pattern);
        long endTime = System.currentTimeMillis();
        timeTaken = endTime - startTime;
        return ans;
    } 

    public long getTimeTaken(){
        return timeTaken;
    }

}

public class BeingZeroLogSearch{
    public static void main(String args[]) throws Exception {
        Logger logger = new Logger();
        String searchTerm = "MongoSocketOpenException";
        
        searchTerm = "mongodb";

        TimeDecorator td = new TimeDecorator(new SingleThreadedLogSearch());
        int occurenceCount = td.logSearch(searchTerm);
        long timeTakenInMs = td.getTimeTaken();
        logger.info(String.format("%d ms, '%s' : '%s' occurs %d times.", timeTakenInMs, "SingleThreadedLogSearch", searchTerm, occurenceCount));
        
        td = new TimeDecorator(new MultithreadedLogSearch());
        occurenceCount = td.logSearch(searchTerm);
        timeTakenInMs = td.getTimeTaken();
        logger.info(String.format("%d ms, '%s' : '%s' occurs %d times.", timeTakenInMs, "MultithreadedLogSearch", searchTerm, occurenceCount));
    }
}