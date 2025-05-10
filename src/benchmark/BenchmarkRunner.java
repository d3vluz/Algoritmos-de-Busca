package benchmark;

import algorithms.SortingAlgorithm;
import benchmark.progress.BenchmarkProgressListener;
import benchmark.strategy.BenchmarkStrategy;
import export.ResultExporter;
import java.io.*;
import java.util.*;
import model.BenchmarkResult;

public class BenchmarkRunner {
    private final List<SortingAlgorithm> algorithms;
    private final BenchmarkStrategy strategy;
    private final ResultExporter exporter;
    private final BenchmarkProgressListener listener;

    public BenchmarkRunner(List<SortingAlgorithm> algorithms,
                         BenchmarkStrategy strategy,
                         ResultExporter exporter,
                         BenchmarkProgressListener listener) {
        this.algorithms = algorithms;
        this.strategy = strategy;
        this.exporter = exporter;
        this.listener = listener;
    }

    public void runBenchmarks(List<Integer> sizes) throws Exception {
        listener.onProgressUpdate("Iniciando benchmarks...");
        
        List<BenchmarkResult> results = new ArrayList<>();
        
        for (SortingAlgorithm algorithm : algorithms) {
            for (int size : sizes) {
                listener.onAlgorithmStart(algorithm.getName(), size);
                BenchmarkResult result = strategy.execute(algorithm, size);
                results.add(result);
                listener.onAlgorithmComplete(result);
            }
        }
        
        exporter.export(results);
        listener.onProgressUpdate("Benchmarks concluídos!");
        
        showVisualization();
    }

    private void showVisualization() {
        try {
            String pythonCmd = System.getProperty("os.name").toLowerCase().contains("win") ?
                "python.exe" : "python";
            
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, "graficos.py");
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                listener.onProgressUpdate("[Python] " + line);
            }
            
        } catch (IOException e) {
            listener.onProgressUpdate("Erro ao visualizar: " + e.getMessage());
        }
    }
}