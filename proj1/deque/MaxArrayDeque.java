package deque;
import java.util.Comparator;
import java.util.Deque;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(){
        comparator = null;
    }
    public MaxArrayDeque(Comparator<T> c){
        comparator = c;
    }
    public T max(){
        return max(comparator);
    }
    public T max(Comparator<T> c){
        int maxIndex = 0;
        for(int i = 1; i < size(); i++){
            if(c.compare(get(i), get(maxIndex)) > 0){
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }
}
