package randomizedtest;


import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
          BuggyAList<Integer> BA = new BuggyAList<>();
          AListNoResizing<Integer> ANR = new AListNoResizing<>();
          BA.addLast(4);BA.addLast(5);BA.addLast(6);
          ANR.addLast(4);ANR.addLast(5);ANR.addLast(6);
          assertEquals(ANR.removeLast(),BA.removeLast());
          assertEquals(ANR.removeLast(),BA.removeLast());
          assertEquals(ANR.removeLast(),BA.removeLast());
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BL = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BL.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                System.out.println("size: " + L.size());
                assertEquals(L.size(),BL.size());
            } else if (L.size() != 0 && operationNumber == 2){
                // getLast
                System.out.println("getLast(" + L.getLast() + ")");
                assertEquals(L.getLast(),BL.getLast());
            } else if (L.size() != 0 && operationNumber == 3){
                // removeLast
                int Lrl = L.removeLast();
                int BLrl = BL.removeLast();
                System.out.println("removeLast(" + Lrl + ")");
                assertEquals(Lrl,BLrl);
            }
        }
    }
}
