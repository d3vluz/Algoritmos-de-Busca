package benchmark.progress;

import model.BenchmarkResult;

public class ConsoleProgressListener implements BenchmarkProgressListener {
    @Override
    public void onProgressUpdate(String message) {
        System.out.println("[Progress] " + message);
    }

    @Override
    public void onAlgorithmStart(String algorithmName, int size) {
        System.out.printf("Running %s with size %d...\n", algorithmName, size);
    }

    @Override
    public void onAlgorithmComplete(BenchmarkResult result) {
        System.out.println("Completed: " + result);
    }
}