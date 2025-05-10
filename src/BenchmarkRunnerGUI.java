import algorithms.SortingAlgorithm;
import algorithms.parallel.*;
import algorithms.serial.*;
import benchmark.BenchmarkRunner;
import benchmark.progress.BenchmarkProgressListener;
import benchmark.strategy.TimeoutBenchmarkStrategy;
import data.RandomArrayGenerator;
import export.CsvResultExporter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.swing.*;
import model.BenchmarkResult;

public class BenchmarkRunnerGUI {
    private JFrame frame;
    private JTextArea progressArea;
    private JProgressBar progressBar;
    private JButton runButton;
    private JButton visualizeButton;
    private JTextField samplesField;
    private JTextField sizesField;
    private JTextField timeoutField;
    private JCheckBox sameArrayCheck;
    private JTextField csvField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BenchmarkRunnerGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Benchmark de Algoritmos de Ordenação");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel configPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        
        configPanel.add(new JLabel("Número de amostras:"));
        samplesField = new JTextField("3");
        configPanel.add(samplesField);

        configPanel.add(new JLabel("Tamanhos do array (ex: 100000,500000):"));
        sizesField = new JTextField("50000,100000,600000");
        configPanel.add(sizesField);

        configPanel.add(new JLabel("Timeout (ms):"));
        timeoutField = new JTextField("15000");
        configPanel.add(timeoutField);

        configPanel.add(new JLabel("Arquivo CSV:"));
        csvField = new JTextField("results.csv");
        configPanel.add(csvField);

        sameArrayCheck = new JCheckBox("Usar mesmos arrays", true);
        configPanel.add(sameArrayCheck);
        
        mainPanel.add(configPanel, BorderLayout.NORTH);

        progressArea = new JTextArea(15, 70);
        progressArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(progressArea);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(scrollPane, BorderLayout.CENTER);
        progressPanel.add(progressBar, BorderLayout.SOUTH);
        
        mainPanel.add(progressPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        runButton = new JButton("Executar Benchmark");
        runButton.addActionListener(this::runBenchmark);
        
        visualizeButton = new JButton("Visualizar Resultados");
        visualizeButton.addActionListener(this::visualizeResults);
        visualizeButton.setEnabled(false);
        
        buttonPanel.add(runButton);
        buttonPanel.add(visualizeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void runBenchmark(ActionEvent e) {
        runButton.setEnabled(false);
        visualizeButton.setEnabled(false);
        progressArea.setText("");
        progressBar.setValue(0);

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                try {
                    RandomArrayGenerator generator = new RandomArrayGenerator(
                        Integer.parseInt(samplesField.getText()));
                    
                    TimeoutBenchmarkStrategy strategy = new TimeoutBenchmarkStrategy(
                        Long.parseLong(timeoutField.getText()), 
                        generator);

                    CsvResultExporter exporter = new CsvResultExporter(csvField.getText());
                    
                    BenchmarkProgressListener listener = new BenchmarkProgressListener() {
                        @Override
                        public void onProgressUpdate(String message) {
                            publish(message);
                        }

                        @Override
                        public void onAlgorithmStart(String algorithmName, int size) {
                            publish("Executando " + algorithmName + " com tamanho " + size);
                        }

                        @Override
                        public void onAlgorithmComplete(BenchmarkResult result) {
                            publish(result.toString());
                        }
                    };

                    BenchmarkRunner runner = new BenchmarkRunner(
                        getSelectedAlgorithms(),
                        strategy,
                        exporter,
                        listener
                    );

                    runner.runBenchmarks(getSelectedSizes());
                    publish("\nBenchmark concluído com sucesso!");
                    visualizeButton.setEnabled(true);
                } catch (Exception ex) {
                    publish("ERRO: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> progressArea.append(msg + "\n"));
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                progressBar.setValue(100);
            }
        }.execute();
    }

    private void visualizeResults(ActionEvent e) {
        new SwingWorker<Map<String, String>, String>() {
            @Override
            protected Map<String, String> doInBackground() {
                Map<String, String> imagePaths = new HashMap<>();
                try {
                    ProcessBuilder pb = new ProcessBuilder("python", "graficos.py");
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    
                    try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()))) {
                        
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("IMAGEM_PRONTA:")) {
                                String path = line.substring("IMAGEM_PRONTA:".length());
                                String algorithmName = path.replace("images/", "")
                                                         .replace(".png", "");
                                imagePaths.put(algorithmName, path);
                                publish("Gráfico gerado: " + algorithmName);
                            } else {
                                publish(line);
                            }
                        }
                    }
                    
                    int exitCode = p.waitFor();
                    if (exitCode != 0) {
                        publish("Erro no Python (código " + exitCode + ")");
                    }
                } catch (IOException | InterruptedException ex) {
                    publish("ERRO: " + ex.getMessage());
                }
                return imagePaths;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> progressArea.append(msg + "\n"));
            }

            @Override
            protected void done() {
                try {
                    Map<String, String> imagePaths = get();
                    if (!imagePaths.isEmpty()) {
                        showResultImages(imagePaths);
                    } else {
                        progressArea.append("Nenhum gráfico foi gerado\n");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    progressArea.append("Erro ao obter gráficos: " + ex.getMessage() + "\n");
                }
            }
        }.execute();
    }

    private void showResultImages(Map<String, String> imagePaths) {
        JFrame resultFrame = new JFrame("Resultados Gráficos");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(900, 650);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        imagePaths.forEach((algorithm, path) -> {
            ImageIcon icon = new ImageIcon(path);
            Image scaledImage = icon.getImage().getScaledInstance(
                800, 500, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            JScrollPane scrollPane = new JScrollPane(label);
            tabbedPane.addTab(algorithm, scrollPane);
        });

        resultFrame.add(tabbedPane);
        resultFrame.setVisible(true);
    }

    private List<SortingAlgorithm> getSelectedAlgorithms() {
        List<SortingAlgorithm> algorithms = new ArrayList<>();
        
        algorithms.add(new BubbleSortSerial());
        algorithms.add(new MergeSortSerial());
        algorithms.add(new QuickSortSerial());
        algorithms.add(new CountingSortSerial());

        for (int threads : Arrays.asList(2, 4, 8)) {
            algorithms.add(new BubbleSortParallel(threads));
            algorithms.add(new MergeSortParallel(threads));
            algorithms.add(new QuickSortParallel(threads));
            algorithms.add(new CountingSortParallel(threads));
        }
        
        return algorithms;
    }

    private List<Integer> getSelectedSizes() {
        return Arrays.stream(sizesField.getText().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}