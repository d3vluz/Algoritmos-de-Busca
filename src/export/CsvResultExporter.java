package export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import model.BenchmarkResult;

public class CsvResultExporter implements ResultExporter {
    private final String filePath;

    public CsvResultExporter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void export(List<BenchmarkResult> results) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(BenchmarkResult.getCsvHeader());
            writer.newLine();
            
            for (BenchmarkResult result : results) {
                writer.write(result.toCsvLine());
                writer.newLine();
            }
        }
    }
}