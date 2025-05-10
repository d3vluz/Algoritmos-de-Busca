package export;

import java.util.List;
import model.BenchmarkResult;

public interface ResultExporter {
    void export(List<BenchmarkResult> results) throws Exception;
}