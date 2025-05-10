package data;

import java.util.Random;

public class RandomArrayGenerator implements ArrayGenerator {
    private final int maxValueMultiplier;
    private final Random random = new Random();

    public RandomArrayGenerator(int maxValueMultiplier) {
        this.maxValueMultiplier = maxValueMultiplier;
    }

    @Override
    public int[] generate(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * maxValueMultiplier);
        }
        return array;
    }
}