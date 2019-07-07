package net.raumzeitfalle.fx.filechooser;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;

class Invoke {
    
    private Invoke() {
        
    }
    
    static void later(Runnable r) {
        Platform.runLater(r);    
    }
    
    static void laterWithDelay(Duration duration, Runnable r) {
        Platform.runLater(()->{
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt(); 
            }
            r.run();
        });
    }
    
    static void andWaitWithoutException(Runnable r) {
        try {
            andWait(r);
        } catch (InterruptedException | ExecutionException e) {
        	Thread.currentThread().interrupt();
        } 
    }
    
    static void andWait(Runnable r) throws InterruptedException, ExecutionException {
        FutureTask<?> task = new FutureTask<>(r, null);
        Platform.runLater(task);
        task.get();
    }
    
    
}
