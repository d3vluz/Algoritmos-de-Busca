package algorithms.parallel;

import algorithms.AbstractSortingAlgorithm;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuickSortParallel extends AbstractSortingAlgorithm {
    
    private ForkJoinPool pool;
    
    public QuickSortParallel(int numThreads) {
        super("QuickSort", numThreads);
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        
        pool = new ForkJoinPool(getThreadCount());
        pool.invoke(new QuickSortTask(result, 0, result.length - 1));
        pool.shutdown();
        
        return result;
    }
    
    private class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 1000;
        
        public QuickSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }
        
        @Override
        protected void compute() {
            if (high - low < THRESHOLD) {
                quickSortSequential(array, low, high);
            } else {
                int partitionIndex = partition(array, low, high);
                invokeAll(
                    new QuickSortTask(array, low, partitionIndex - 1),
                    new QuickSortTask(array, partitionIndex + 1, high)
                );
            }
        }
        
        private void quickSortSequential(int[] array, int low, int high) {
            if (low < high) {
                int partitionIndex = partition(array, low, high);
                quickSortSequential(array, low, partitionIndex - 1);
                quickSortSequential(array, partitionIndex + 1, high);
            }
        }
        
        private int partition(int[] array, int low, int high) {
            chooseBestPivot(array, low, high);
            int pivot = array[high];
            int i = low - 1;
            
            for (int j = low; j < high; j++) {
                if (array[j] <= pivot) {
                    i++;
                    swap(array, i, j);
                }
            }
            
            swap(array, i + 1, high);
            return i + 1;
        }
        
        private void chooseBestPivot(int[] array, int low, int high) {
            if (high - low >= 2) {
                int mid = low + (high - low) / 2;
                if (array[low] > array[mid]) swap(array, low, mid);
                if (array[low] > array[high]) swap(array, low, high);
                if (array[mid] > array[high]) swap(array, mid, high);
                swap(array, mid, high);
            } else if (high > low && array[low] > array[high]) {
                swap(array, low, high);
            }
        }
        
        private void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}