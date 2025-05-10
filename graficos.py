import os
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from typing import List, Dict

class BenchmarkVisualizer:
    def __init__(self):
        self.output_dir = "images"
        self.csv_file = "results.csv"
        self.csv_separator = ";"
        
        self.label_colors = {
            "Serial - 1 thread": "#4E79A7",
            "Parallel - 2 threads": "#F28E2B",
            "Parallel - 4 threads": "#59A14F",
            "Parallel - 8 threads": "#B07AA1",
            "timeout": "lightgray"
        }
        self.bar_width = 0.18
        self.figsize = (10, 6)
        self.fontsize = 8
        self.timeout_value = -1
        self.dpi = 120

    def load_data(self) -> pd.DataFrame:
        try:
            df = pd.read_csv(
                self.csv_file, 
                sep=self.csv_separator, 
                decimal=","
            )
            df['Tempo(ms)'] = pd.to_numeric(df['Tempo(ms)'], errors='coerce')
            df = df[df['Tempo(ms)'].notna()]
            return df
        except Exception as e:
            raise Exception(f"Erro ao ler o arquivo CSV: {e}")

    def prepare_environment(self):
        os.makedirs(self.output_dir, exist_ok=True)

    def generate_plots(self) -> List[str]:
        self.prepare_environment()
        
        try:
            df = self.load_data()
            if df.empty:
                print("AVISO: O arquivo CSV está vazio ou contém apenas dados inválidos")
                return []
        except Exception as e:
            print(f"ERRO: {e}")
            return []

        algorithms = df['Algoritmo'].unique()
        generated_images = []

        for algorithm in algorithms:
            subset = df[df['Algoritmo'] == algorithm]
            if subset.empty:
                print(f"AVISO: Nenhum dado válido encontrado para {algorithm}")
                continue
                
            self._generate_algorithm_plot(algorithm, subset)
            img_path = os.path.join(self.output_dir, f"{algorithm}.png")
            generated_images.append(img_path)
            print(f"IMAGEM_PRONTA:{img_path}")

        return generated_images

    def _generate_algorithm_plot(self, algorithm: str, data: pd.DataFrame):
        sizes = sorted(data['Tamanho'].unique())
        unique_threads = sorted(data[data['Tipo'] == 'Parallel']['Threads'].unique())
        
        plt.figure(figsize=self.figsize)
        legend_handles = {}

        serial_means = self._calculate_means(data, sizes, "Serial")
        parallel_means = {
            thread: self._calculate_means(data, sizes, "Parallel", thread)
            for thread in unique_threads
        }

        self._plot_group(
            positions=np.arange(len(sizes)) - self.bar_width,
            values=serial_means,
            label="Serial - 1 thread",
            legend_handles=legend_handles
        )

        for i, thread in enumerate(unique_threads):
            self._plot_group(
                positions=np.arange(len(sizes)) + (i * self.bar_width),
                values=parallel_means[thread],
                label=f"Parallel - {thread} threads",
                legend_handles=legend_handles
            )

        self._configure_plot(algorithm, sizes, data, legend_handles)
        plt.tight_layout()
        plt.savefig(os.path.join(self.output_dir, f"{algorithm}.png"), dpi=self.dpi)
        plt.close()

    def _calculate_means(self, data: pd.DataFrame, sizes: List[int], 
                       tipo: str, thread: int = None) -> List[float]:
        means = []
        for size in sizes:
            if tipo == "Serial":
                group_data = data[(data['Tipo'] == tipo) & (data['Tamanho'] == size)]
            else:
                group_data = data[(data['Tipo'] == tipo) & 
                                (data['Threads'] == thread) & 
                                (data['Tamanho'] == size)]
            
            if group_data.empty:
                means.append(self.timeout_value)
            else:
                valid_times = group_data['Tempo(ms)'][group_data['Tempo(ms)'] > 0]
                if not valid_times.empty:
                    means.append(valid_times.mean())
                else:
                    means.append(self.timeout_value)
        return means

    def _plot_group(self, positions: np.ndarray, values: List[float], 
                   label: str, legend_handles: Dict):
        color = self.label_colors.get(label, '#333333')
        
        if label not in legend_handles:
            bars = plt.bar(positions, values, width=self.bar_width,
                          color=color, label=label)
            legend_handles[label] = bars[0]
        else:
            bars = plt.bar(positions, values, width=self.bar_width,
                          color=color)

        for bar, val in zip(bars, values):
            if val == self.timeout_value:
                self._plot_timeout_bar(bar.get_x())
            elif val > 0:
                height = bar.get_height()
                plt.text(bar.get_x() + bar.get_width()/2., height * 1.05,
                        f"{val:.1f}", ha='center', va='bottom', 
                        fontsize=self.fontsize)

    def _plot_timeout_bar(self, x_pos: float):
        plt.bar(x_pos, 1, width=self.bar_width,
               color=self.label_colors["timeout"], 
               edgecolor='black')
        plt.text(x_pos + self.bar_width/2, 1.2, "TOUT",
                ha='center', va='bottom', 
                fontsize=self.fontsize, color='gray')

    def _configure_plot(self, algorithm: str, sizes: List[int], 
                       data: pd.DataFrame, legend_handles: Dict):
        plt.title(f"Benchmark - {algorithm}", pad=20, fontsize=12)
        plt.xlabel("Tamanho do Array", fontsize=10)
        plt.ylabel("Tempo Médio (ms)", fontsize=10)
        plt.xticks(np.arange(len(sizes)), sizes)

        if legend_handles:
            plt.legend(legend_handles.values(), legend_handles.keys(),
                      bbox_to_anchor=(1.05, 1), loc='upper left')
        
        plt.grid(axis='y', linestyle='--', alpha=0.6)

        valid_times = data[data['Tempo(ms)'] > 0]['Tempo(ms)']
        if not valid_times.empty:
            max_time = valid_times.max()
            plt.ylim(0.8, max_time * 1.5)

if __name__ == "__main__":
    visualizer = BenchmarkVisualizer()
    print("Lá vai...")
    results = visualizer.generate_plots()
    
    if results:
        print(f"Deu bom")
    else:
        print("Deu ruim.")