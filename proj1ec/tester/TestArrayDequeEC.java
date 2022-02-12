package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest(){
        ArrayDequeSolution<Integer> L = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> BL = new StudentArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
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
                assertEquals(L.size(),BL.size());
            } else if (L.size() != 0 && operationNumber == 2){
                // get
                int randVal = StdRandom.uniform(0, L.size());
                System.out.println("get(" + L.get(randVal) + ")");
                assertEquals(L.get(randVal),BL.get(randVal));
            } else if (L.size() != 0 && operationNumber == 3){
                // removeLast removeFirst
                int Lrl = L.removeLast();
                int BLrl = BL.removeLast();
                System.out.println("removeLast(" + Lrl + ")");
                assertEquals(Lrl,BLrl);
                int Lrl2 = L.removeFirst();
                int BLrl2 = BL.removeLast();
                System.out.println("removeLast(" + Lrl2 + ")");
                assertEquals(Lrl2,BLrl2);
            }
        }
    }
}
