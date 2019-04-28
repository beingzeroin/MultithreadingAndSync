import java.util.*;
import java.nio.*;
import java.io.*;
import java.util.concurrent.*;

class CopyFileScheduler {

	private final Collection<Runnable> tasks = new ArrayList<Runnable>();

	public void addTask(final Runnable task) {
		tasks.add(task);
	}

	public void executeTasks() throws InterruptedException {
		final ExecutorService threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			final CountDownLatch latch = new CountDownLatch(tasks.size());
			for (final Runnable task : tasks)
				threads.execute(new Runnable() {
					public void run() {
						try {
							task.run();
						} finally {
							latch.countDown();
						}
					}
				});
			latch.await();
		} finally {
			threads.shutdown();
		}
	}

}

class MultiThreadedCopyFileTask implements Runnable{
    private String srcFilePath;
    private String destFilePath;

    public MultiThreadedCopyFileTask(String srcFilePath, String destFilePath){
        this.srcFilePath = srcFilePath;
        this.destFilePath = destFilePath;
    }

    public void run(){
        System.out.printf("Copying file '%s' to '%s'\n", srcFilePath, destFilePath);
    }
}


public class MultiThreaded {
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
        CopyFileScheduler scheduler = new CopyFileScheduler();
        long startTime = System.nanoTime();
        for(String srcFilePath : files){
            srcFile = new File(srcFilePath);
            destFilePath =  String.join(File.separator, DST, srcFile.getName());
            MultiThreadedCopyFileTask cpt = new MultiThreadedCopyFileTask(srcFilePath, destFilePath);
            scheduler.addTask(cpt);
        }
        scheduler.executeTasks();
        long endTime = System.nanoTime();
        long durationInNano = (endTime - startTime);
        System.out.printf("TOTAL TIME TAKEN : %d nanoseconds\n", durationInNano);
        System.out.printf("TOTAL TIME TAKEN : %d microseconds\n", durationInNano/1000);
        System.out.printf("TOTAL TIME TAKEN : %d milliseconds\n", durationInNano/1000000);
    }
}