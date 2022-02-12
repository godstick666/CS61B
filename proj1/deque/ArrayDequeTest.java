package deque;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.princeton.cs.introcs.StdRandom;

public class ArrayDequeTest {
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        ArrayDeque<Integer> BL = new ArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BL.addLast(randVal);
                int randVal2 = StdRandom.uniform(0, 100);
                L.addLast(randVal2);
                BL.addLast(randVal2);
                System.out.println("addFirst(" + randVal2 + ")");
            } else if (operationNumber == 1) {
                // size
                System.out.println("size: " + L.size());
                assertEquals(L.size(), BL.size());
            } else if (L.size() != 0 && operationNumber == 2) {
                // get
                int randVal = StdRandom.uniform(0, L.size());
                System.out.println("get(" + randVal + ")");
                Integer getItemL = L.get(randVal);
                Integer getItemBL = BL.get(randVal);
                assertEquals(getItemL, getItemBL);
            } else if (L.size() != 0 && operationNumber == 3) {
                // removeLast
                Integer Lrl = L.removeLast();
                Integer BLrl = BL.removeLast();
                System.out.println("removeLast()");
                assertEquals(Lrl, BLrl);
            } else if (L.size() != 0 && operationNumber == 4) {
                // removeFirst
                Integer Lrl = L.removeFirst();
                Integer BLrl = BL.removeFirst();
                System.out.println("removeFirst()");
                assertEquals(Lrl, BLrl);
            } else if (operationNumber == 5) {
                // isEmpty
                assertEquals(L.isEmpty(), BL.isEmpty());
            }
        }
    }
    @Test
    public void IteratorAndEqualsTest() {
        ArrayDeque<Integer> it1 = new ArrayDeque<>();
        ArrayDeque<Integer> it2 = new ArrayDeque<>();
        for(int i = 0; i < 10; i++){
            it1.addLast(i);
        }
        for(Integer item : it1){
            it2.addLast(item);
        }
        assertEquals(true, it1.equals(it2));

        assertEquals(true, it1.equals(it1));
        assertEquals(false, it1.equals(null));
        assertEquals(false, it1.equals((Integer)1));

    }
}

