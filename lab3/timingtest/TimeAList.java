package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }
    //int[] N = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> N = new AList<>();
        N.addLast(1000);
        AList<Double> timeInSeconds = new AList<>();
        for(int i = 0; i < 8; i++){
            if(i != 0){
                N.addLast(2*N.get(i-1));
            }
            AList<Integer> List = new AList<>();
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < N.get(i); j++){
                List.addLast(1);
            }
            timeInSeconds.addLast(sw.elapsedTime());
        }
        printTimingTable(N, timeInSeconds, N);
    }
}
