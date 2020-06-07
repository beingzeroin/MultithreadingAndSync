public class ProcessorInfo{
    public static void main(String args[]){
        System.out.println("Available Cores (includes HyperThreading): " + Runtime.getRuntime().availableProcessors());
    }
}