package algorithms.serial;

import algorithms.AbstractSortingAlgorithm;

public class BubbleSortSerial extends AbstractSortingAlgorithm {
    
    public BubbleSortSerial() {
        super("BubbleSort");
    }
    
    @Override
    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        int[] result = array.clone();
        int n = result.length;
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            
            if (!swapped) {
                break;
            }
        }
        
        return result;
    }
}