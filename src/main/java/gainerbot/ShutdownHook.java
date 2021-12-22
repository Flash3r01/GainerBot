package gainerbot;

public class ShutdownHook implements Runnable {
    @Override
    public void run() {
        if (GainerBot.jdaInstance != null){
            GainerBot.jdaInstance.shutdown();
            GainerBot.jdaInstance = null;
        }
        if (GainerBot.executorService != null){
            GainerBot.executorService.shutdown();
            GainerBot.executorService = null;
        }
        System.out.println("GainerBot has been shut down. Bye, bye!");
    }
}
