package algorithms;

public interface SortingAlgorithm {
    
    String getName();
    
    String getType();
    
    int getThreadCount();
    
    int[] sort(int[] array);
}