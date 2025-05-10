package algorithms.parallel;

import algorithms.AbstractSortingAlgorithm;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortParallel extends AbstractSortingAlgorithm {
    
    private ForkJoinPool pool;
    
    public MergeSortParallel(int numThreads) {
        super("MergeSort", numThreads);
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        
        pool = new ForkJoinPool(getThreadCount());
        pool.invoke(new MergeSortTask(result, 0, result.length - 1));
        pool.shutdown();
        
        return result;
    }
    
    private class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;
        private static final int THRESHOLD = 1000;
        
        public MergeSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }
        
        @Override
        protected void compute() {
            if (right - left < THRESHOLD) {
                mergeSortSequential(array, left, right);
            } else {
                int middle = left + (right - left) / 2;
                invokeAll(
                    new MergeSortTask(array, left, middle),
                    new MergeSortTask(array, middle + 1, right)
                );
                merge(array, left, middle, right);
            }
        }
        
        private void mergeSortSequential(int[] array, int left, int right) {
            if (left < right) {
                int middle = left + (right - left) / 2;
                mergeSortSequential(array, left, middle);
                mergeSortSequential(array, middle + 1, right);
                merge(array, left, middle, right);
            }
        }
        
        private void merge(int[] array, int left, int middle, int right) {
            int n1 = middle - left + 1;
            int n2 = right - middle;
            
            int[] leftArray = new int[n1];
            int[] rightArray = new int[n2];
            
            for (int i = 0; i < n1; i++) {
                leftArray[i] = array[left + i];
            }
            for (int j = 0; j < n2; j++) {
                rightArray[j] = array[middle + 1 + j];
            }
            
            int i = 0, j = 0, k = left;
            while (i < n1 && j < n2) {
                if (leftArray[i] <= rightArray[j]) {
                    array[k] = leftArray[i];
                    i++;
                } else {
                    array[k] = rightArray[j];
                    j++;
                }
                k++;
            }
            
            while (i < n1) {
                array[k] = leftArray[i];
                i++;
                k++;
            }
            
            while (j < n2) {
                array[k] = rightArray[j];
                j++;
                k++;
            }
        }
    }
}