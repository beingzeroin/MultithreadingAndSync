import java.util.*; 
  

class SharedBuffer{
    
    private LinkedList<Integer> list; 
    private int capacity; 

    SharedBuffer(){
        list = new LinkedList<>();
        capacity = 2;
    }
    public void write(Integer value){
        list.add(value);
    }
    public Integer read(){
        return list.removeFirst();
    }

    public boolean isFull(){
        return list.size()==capacity;
    }

    public boolean isEmpty(){
        return list.size()==0;
    }
}

class Worker{

    SharedBuffer buffer;
    public Worker(){
        buffer = new SharedBuffer();
    }
    public void consume() throws Exception 
    { 
        while (true) 
        { 
            synchronized (this) 
            { 
                // consumer thread waits while buffer is empty  
                while (buffer.isEmpty()) 
                    wait(); 
               
                System.out.printf("[Consumer Awake] Consuming - %d\n", buffer.read()); 

                // Notify the Consumer
                notify(); 

                // Intentional Delay
                Thread.sleep(1000); 
            } 
        } 
    } 

    public void produce() throws Exception 
    { 
        int value = 0; 
        while (true) 
        { 
            synchronized (this) 
            { 
                // producer thread waits while buffer is full 
                while (buffer.isFull()) 
                    wait(); 

                System.out.printf("[Producer Awake] Generated - %d\n", ++value); 
                buffer.write(value); 

                // Notifies the Consumer
                notify(); 
                
                // Intentional Delay
                Thread.sleep(1000); 
            } 
        } 
    } 

}

class Consumer implements Runnable
{ 
    Worker w;
    Consumer(Worker w){
        this.w = w;
    }
    public void run()
    { 
        System.out.println("Starting Consumer");
        try
        { 
            w.consume(); 
        } 
        catch(Exception e) 
        { 
            e.printStackTrace(); 
        } 
    } 
} 

class Producer implements Runnable
{ 
    Worker w;
    Producer(Worker w){
        this.w = w;
    }
    public void run()
    { 
        System.out.println("Starting Producer");
        try{ 
            w.produce(); 
        } 
        catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
} 

public class Synchronization 
{ 
    public static void main(String[] args) throws Exception 
    { 
        Worker w = new Worker();
        Thread producer = new Thread(new Producer(w)); 
        Thread consumer = new Thread(new Consumer(w)); 

        // Start
        producer.start(); 
        consumer.start(); 
  
        // producer finishes before consumer 
        producer.join(); 
        consumer.join(); 
    } 
} 