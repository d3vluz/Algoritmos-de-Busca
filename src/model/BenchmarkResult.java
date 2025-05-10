package model;

public final class BenchmarkResult {
    private final String algorithm;
    private final String type;
    private final int threads;
    private final int size;
    private final double timeMs;

    public BenchmarkResult(String algorithm, String type, int threads, int size, double timeMs) {
        this.algorithm = algorithm;
        this.type = type;
        this.threads = threads;
        this.size = size;
        this.timeMs = timeMs;
    }

    public String toCsvLine() {
        return String.join(";",
            algorithm,
            type,
            String.valueOf(threads),
            String.valueOf(size),
            String.format("%.2f", timeMs)
        );
    }

    public static String getCsvHeader() {
        return "Algoritmo;Tipo;Threads;Tamanho;Tempo(ms)";
    }

    @Override
    public String toString() {
        return String.format("%-12s %-8s %-8d %-10d %8.2f ms",
            algorithm, type, threads, size, timeMs);
    }
}