package algorithms;


public abstract class AbstractSortingAlgorithm implements SortingAlgorithm {
    
    private final String name;
    private final String type;
    private final int threadCount;
    
    protected AbstractSortingAlgorithm(String name) {
        this.name = name;
        this.type = "Serial";
        this.threadCount = 1;
    }
    
    protected AbstractSortingAlgorithm(String name, int threadCount) {
        this.name = name;
        this.type = "Parallel";
        this.threadCount = threadCount;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public int getThreadCount() {
        return threadCount;
    }
    
    @Override
    public abstract int[] sort(int[] array);
}