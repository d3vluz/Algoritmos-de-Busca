package benchmark.strategy;

import algorithms.SortingAlgorithm;
import data.ArrayGenerator;
import java.util.concurrent.*;
import model.BenchmarkResult;

public class TimeoutBenchmarkStrategy implements BenchmarkStrategy,AutoCloseable {
    private final long timeoutMillis;
    private final ArrayGenerator arrayGenerator;
    private final ExecutorService executor;

    public TimeoutBenchmarkStrategy(long timeoutMillis, ArrayGenerator arrayGenerator) {
        this.timeoutMillis = timeoutMillis;
        this.arrayGenerator = arrayGenerator;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public BenchmarkResult execute(SortingAlgorithm algorithm, int size) {
        int[] array = arrayGenerator.generate(size);
        long start = System.nanoTime();
        
        Future<?> future = executor.submit(() -> algorithm.sort(array));
        
        try {
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            return new BenchmarkResult(
                algorithm.getName(),
                algorithm.getType(),
                algorithm.getThreadCount(),
                size,
                elapsedMs
            );
        } catch (TimeoutException | ExecutionException e) {
            future.cancel(true);
            return new BenchmarkResult(
                algorithm.getName(),
                algorithm.getType(),
                algorithm.getThreadCount(),
                size,
                -1
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            return new BenchmarkResult(
                algorithm.getName(),
                algorithm.getType(),
                algorithm.getThreadCount(),
                size,
                -1
            );
        }
    }

    @Override
    public void close(){
        executor.shutdown();
    }
}