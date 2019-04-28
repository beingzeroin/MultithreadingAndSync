import java.util.*;
import java.nio.*;
import java.io.*;

class SingleThreadedCopyFileTask{
    private String srcFilePath;
    private String destFilePath;

    public SingleThreadedCopyFileTask(String srcFilePath, String destFilePath){
        this.srcFilePath = srcFilePath;
        this.destFilePath = destFilePath;
    }

    public void run(){
        System.out.printf("Copying file '%s' to '%s'\n", srcFilePath, destFilePath);
    }
}


public class SingleThreaded{
    static String SRC = "C:\\src";
    static String DST = "Z:\\dst";
    static int FILECOUNT = 10000;
    static List<String> getFilePathsInSource(String srcDirectory){
        List<String> lst = new ArrayList<>();
        String filePath = "";
        for(int i=1;i<=FILECOUNT;i++){
            filePath = String.join(File.separator, SRC, i+".txt");
            lst.add(filePath);
        }
        return lst;
    }

    public static void main(String args[]) throws Exception{
        List<String> files = getFilePathsInSource(SRC);
        String destFilePath;
        File srcFile;
        long startTime = System.nanoTime();
        for(String srcFilePath : files){
            srcFile = new File(srcFilePath);
            destFilePath =  String.join(File.separator, DST, srcFile.getName());
            SingleThreadedCopyFileTask cpt = new SingleThreadedCopyFileTask(srcFilePath, destFilePath);
            cpt.run();
        }
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);
        System.out.printf("TOTAL TIME TAKEN : %d nanoseconds\n", durationInNano);
        System.out.printf("TOTAL TIME TAKEN : %d microseconds\n", durationInNano/1000);
        System.out.printf("TOTAL TIME TAKEN : %d milliseconds\n", durationInNano/1000000);
    }
}