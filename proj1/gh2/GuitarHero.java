package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {

    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] piano = new GuitarString[37];
        for(int i = 0; i < piano.length; i++){
            double ithFrequency = 440 * Math.pow(2,(i-24)/12);
            piano[i] = new GuitarString(ithFrequency);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                // keyIndex represents the index of key in keyboard,
                // .vauleOf converts char to String
                int keyIndex = keyboard.indexOf(String.valueOf(key));
                if (keyIndex != -1) {
                    piano[keyIndex].pluck();
                } else {
                    continue;
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for(GuitarString GS : piano){
                sample += GS.sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for(GuitarString GS : piano){
                GS.tic();
            }
        }
    }
}

