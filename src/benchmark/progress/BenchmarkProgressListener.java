package benchmark.progress;

import model.BenchmarkResult;

public interface BenchmarkProgressListener {
    void onProgressUpdate(String message);
    void onAlgorithmStart(String algorithmName, int size);
    void onAlgorithmComplete(BenchmarkResult result);
}