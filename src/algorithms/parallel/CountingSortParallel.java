package algorithms.parallel;

import algorithms.AbstractSortingAlgorithm;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountingSortParallel extends AbstractSortingAlgorithm {
    
    public CountingSortParallel(int numThreads) {
        super("CountingSort", numThreads);
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        int max = findMaxParallel(result);
        
        ExecutorService executor = Executors.newFixedThreadPool(getThreadCount());
        try {
            int[][] localCounts = new int[getThreadCount()][max + 1];
            CountDownLatch countLatch = new CountDownLatch(getThreadCount());
            
            for (int t = 0; t < getThreadCount(); t++) {
                final int threadIndex = t;
                executor.submit(() -> {
                    int segmentSize = result.length / getThreadCount();
                    int start = threadIndex * segmentSize;
                    int end = (threadIndex == getThreadCount() - 1) ? result.length : start + segmentSize;
                    
                    for (int i = start; i < end; i++) {
                        localCounts[threadIndex][result[i]]++;
                    }
                    
                    countLatch.countDown();
                });
            }
            
            try {
                countLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ordenação interrompida", e);
            }
            
            int[] globalCount = new int[max + 1];
            for (int i = 0; i <= max; i++) {
                for (int t = 0; t < getThreadCount(); t++) {
                    globalCount[i] += localCounts[t][i];
                }
            }
            
            for (int i = 1; i <= max; i++) {
                globalCount[i] += globalCount[i - 1];
            }
            
            int[][] startPositions = new int[getThreadCount()][max + 1];
            for (int value = 0; value <= max; value++) {
                int startPos = value > 0 ? globalCount[value - 1] : 0;
                
                for (int t = 0; t < getThreadCount(); t++) {
                    startPositions[t][value] = startPos;
                    startPos += localCounts[t][value];
                }
            }
            
            int[] output = new int[result.length];
            CountDownLatch placementLatch = new CountDownLatch(getThreadCount());
            
            for (int t = 0; t < getThreadCount(); t++) {
                final int threadIndex = t;
                executor.submit(() -> {
                    int segmentSize = result.length / getThreadCount();
                    int start = threadIndex * segmentSize;
                    int end = (threadIndex == getThreadCount() - 1) ? result.length : start + segmentSize;
                    
                    for (int i = start; i < end; i++) {
                        int value = result[i];
                        int position = startPositions[threadIndex][value]++;
                        output[position] = value;
                    }
                    
                    placementLatch.countDown();
                });
            }
            
            try {
                placementLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ordenação interrompida", e);
            }
            
            System.arraycopy(output, 0, result, 0, result.length);
        } finally {
            executor.shutdown();
        }
        
        return result;
    }
    
    private int findMaxParallel(int[] array) {
        ExecutorService executor = Executors.newFixedThreadPool(getThreadCount());
        try {
            int[] localMaxs = new int[getThreadCount()];
            CountDownLatch latch = new CountDownLatch(getThreadCount());
            
            for (int t = 0; t < getThreadCount(); t++) {
                final int threadIndex = t;
                executor.submit(() -> {
                    int segmentSize = array.length / getThreadCount();
                    int start = threadIndex * segmentSize;
                    int end = (threadIndex == getThreadCount() - 1) ? array.length : start + segmentSize;
                    
                    int localMax = Integer.MIN_VALUE;
                    if (start < array.length) {
                        localMax = array[start];
                        for (int i = start + 1; i < end; i++) {
                            if (array[i] > localMax) {
                                localMax = array[i];
                            }
                        }
                    }
                    
                    localMaxs[threadIndex] = localMax;
                    latch.countDown();
                });
            }
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Operação interrompida", e);
            }
            
            int max = localMaxs[0];
            for (int i = 1; i < getThreadCount(); i++) {
                if (localMaxs[i] > max) {
                    max = localMaxs[i];
                }
            }
            
            return max;
        } finally {
            executor.shutdown();
        }
    }
}