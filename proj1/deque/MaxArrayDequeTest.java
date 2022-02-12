package deque;

import java.util.Comparator;
import static org.junit.Assert.*;
import org.junit.Test;


public class MaxArrayDequeTest {
    @Test
    public void IntegerTest(){
        MaxArrayDeque<Integer> iMax = new MaxArrayDeque<>(new IntComparator());
        for(int i = 0; i < 10; i++){
            iMax.addFirst(i);
        }
        assertEquals((Integer)9, iMax.max());

        MaxArrayDeque<Integer> iMax2 = new MaxArrayDeque<>();
        for(int i = 0; i < 10; i++){
            iMax2.addFirst(10);
        }
        assertEquals((Integer)10, iMax2.max(new IntComparator()));
    }
    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }
}
