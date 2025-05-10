package algorithms.serial;

import algorithms.AbstractSortingAlgorithm;

public class CountingSortSerial extends AbstractSortingAlgorithm {
    
    public CountingSortSerial() {
        super("CountingSort");
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        int max = getMax(result);
        int[] count = new int[max + 1];
        
        for (int i = 0; i < result.length; i++) {
            count[result[i]]++;
        }
        
        for (int i = 1; i <= max; i++) {
            count[i] += count[i - 1];
        }
        
        int[] output = new int[result.length];
        for (int i = result.length - 1; i >= 0; i--) {
            output[count[result[i]] - 1] = result[i];
            count[result[i]]--;
        }
        
        System.arraycopy(output, 0, result, 0, result.length);
        return result;
    }
    
    private int getMax(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}