# Benchmark de Algoritmos de Ordenação: Serial vs. Paralelo

## 📌 Resumo

Este projeto implementa e compara o desempenho de quatro algoritmo de ordenação, sendo eles: Bubble Sort, **Merge Sort**, **Quick Sort** e **Counting Sort** — em versões **seriais** e **paralelas**. Ele inclui:

* Um framework completo para execução de benchmarks
* Geração de relatórios em CSV
* Visualização gráfica dos resultados

O objetivo é analisar o impacto da paralelização sob diferentes cargas e configurações de hardware.

---

## 📖 Introdução

### Algoritmos Implementados

O trabalho considera dois paradigmas de implementação:

#### 1. Algoritmos Seriais

Implementações tradicionais, executadas em uma única thread.

#### 2. Algoritmos Paralelos

Implementações multi-thread com:

* **ExecutorService** para *Bubble Sort* e *Counting Sort*
* **ForkJoinPool** para *Merge Sort* e *Quick Sort*
* Configuração com **2, 4 ou 8 threads**

---

## ⚖️ Metodologia

### Framework de Benchmark

* Geração de arrays aleatórios com tamanhos configuráveis
* Execução controlada com timeout
* Coleta de métricas de tempo de execução
* Exportação para arquivo CSV
* Visualização com gráficos comparativos

---

## 📊 Resultados e Discussão

### Comparativo Serial vs. Paralelo

* Gráficos de barras lado a lado para cada algoritmo
* Destaque para o ganho de desempenho com paralelização
* Pontos de inflexão onde a paralelização deixa de ser eficaz

### Análise de Escalabilidade

* Comportamento com diferentes números de threads (2, 4, 8)
* Eficiência para diferentes tamanhos de array

### Casos de Timeout

* Visualização clara de algoritmos que excederam o tempo limite
* Comparativo empírico da complexidade entre algoritmos

---

## 🔬 Conclusão

O projeto demonstra:

* Os benefícios da paralelização variam conforme o algoritmo
* *Merge Sort* e *Quick Sort* têm os melhores ganhos com paralelização
* *Counting Sort* é eficiente, mas limitado pelo range dos dados
* *Bubble Sort* não se adapta bem à paralelização na abordagem usada

### Contribuições Principais

* Framework reutilizável para benchmarks de ordenação
* Análise comparativa entre abordagens seriais e paralelas
* Visualização clara para identificação de padrões de desempenho

---

## 📗 Referências

* Cormen, T. H., et al. *Introduction to Algorithms*. MIT Press, 3ª edição.
* Goetz, B., et al. *Java Concurrency in Practice*. Addison-Wesley, 2006.
* Sedgewick, R., Wayne, K. *Algorithms*. Addison-Wesley, 4ª edição.
* Oracle Java Documentation: *Fork/Join Framework*
* Matplotlib Documentation: *Visualization with Python*

---

## 📚 Como Utilizar

### Execução via GUI


Arquivo principal: `BenchmarkRunnerGUI.java`

### Configurações Disponíveis

* Tamanhos dos arrays (ex: 100000, 400000, 800000)
* Tempo limite (timeout) em milissegundos
* Arquivo de saída CSV

### Passos

1. Execute a interface gráfica
2. Configure os parâmetros desejados
3. Clique em **"Executar Benchmark"**
4. Visualize os resultados na aba de gráficos

### Requisitos

* **Java 8+**
* **Python 3+** com as bibliotecas:

  * `pandas`
  * `matplotlib`
  * `numpy`

## Link do Projeto
[clique aqui](https://github.com/d3vluz/Algoritmos-de-Busca)
