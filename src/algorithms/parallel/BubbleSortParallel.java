package algorithms.parallel;

import algorithms.AbstractSortingAlgorithm;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BubbleSortParallel extends AbstractSortingAlgorithm {
    
    public BubbleSortParallel(int numThreads) {
        super("BubbleSort", numThreads);
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        int n = result.length;
        boolean swapped = true;
        
        ExecutorService executor = Executors.newFixedThreadPool(getThreadCount());
        
        for (int phase = 0; phase < n && swapped; phase++) {
            final CountDownLatch latch = new CountDownLatch(getThreadCount());
            final boolean[] anySwapped = {false};
            final int startOffset = phase % 2;
            
            for (int t = 0; t < getThreadCount(); t++) {
                final int threadIndex = t;
                
                executor.submit(() -> {
                    int start = threadIndex * ((n - 1) / getThreadCount());
                    int end = (threadIndex == getThreadCount() - 1) ? n - 1 : (threadIndex + 1) * ((n - 1) / getThreadCount());
                    boolean threadSwapped = false;
                    
                    for (int j = startOffset + start; j < end; j += 2) {
                        if (j + 1 < n && result[j] > result[j + 1]) {
                            int temp = result[j];
                            result[j] = result[j + 1];
                            result[j + 1] = temp;
                            threadSwapped = true;
                        }
                    }
                    
                    if (threadSwapped) {
                        synchronized (anySwapped) {
                            anySwapped[0] = true;
                        }
                    }
                    
                    latch.countDown();
                });
            }
            
            try {
                latch.await();
                swapped = anySwapped[0];
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ordenação interrompida", e);
            }
        }
        
        executor.shutdown();
        return result;
    }
}