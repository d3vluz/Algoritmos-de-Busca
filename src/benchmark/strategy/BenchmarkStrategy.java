package benchmark.strategy;

import algorithms.SortingAlgorithm;
import model.BenchmarkResult;

public interface BenchmarkStrategy {
    BenchmarkResult execute(SortingAlgorithm algorithm, int size);
}