package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<>();
        AList<Integer> Op = new AList<>();
        AList<Double> timeInSeconds = new AList<>();
        N.addLast(1000);
        Op.addLast(10000);
        for(int i = 0; i < 8; i++){
            if(i != 0){
                N.addLast(2*N.get(i-1));
                Op.addLast(10000);
            }
            SLList<Integer> List = new SLList<>();
            for(int j = 0; j < N.get(i); j++){
                List.addFirst(1);
            }
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < Op.get(i); j++){
                int x = List.getLast();
            }
            timeInSeconds.addLast(sw.elapsedTime());
        }
        printTimingTable(N, timeInSeconds, Op);
    }

}
