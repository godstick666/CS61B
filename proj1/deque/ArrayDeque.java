package deque;
import java.util.Iterator;

public class ArrayDeque <Item> implements Deque<Item>, Iterable<Item>{
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** Creates an empty list. */
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        int FirstIndex = GetRealIndex(0);
        if(nextFirst < nextLast){
            System.arraycopy(items, FirstIndex, a, 0, size);
        }else if(nextFirst > nextLast){
            int sliceLength = items.length - nextFirst - 1;
            System.arraycopy(items, FirstIndex, a, 0, sliceLength);
            System.arraycopy(items, 0, a, sliceLength, size - sliceLength);
        }
        items = a;
        nextFirst = items.length - 1;
        nextLast = size;
    }
    /* Invariants:
     addFirst/Last: The next item we want to add, will go into position nextFirst/Last.
                    nextFirst -= 1 and have to be real index.
     nextFirst: After we added the item on nextFirst, it has to be update to nextFirst - 1.
                When nextFirst < 0, it should plus items.length.
     nextLast: No need to update.
    */
    @Override
    public void addFirst(Item i){
        if (size == items.length - 2) {
            resize(size * 2);
        }
        items[nextFirst] = i;
        size = size + 1;
        nextFirst -= 1;
        nextFirst = GetRealIndex(-1);
    }
    @Override
    public void addLast(Item i){
        if (size == items.length - 2) {
            resize(size * 2);
        }
        items[nextLast] = i;
        size = size + 1;
        nextLast += 1;
        nextLast = GetRealIndex(size);
    }
    @Override
    public Item removeFirst(){
        if(size > 0){
            Item x = items[GetRealIndex(0)];
            items[GetRealIndex(0)] = null;
            size -= 1;
            nextFirst += 1;
            nextFirst = GetRealIndex(-1);
            if (items.length >= 16 && size < items.length / 4 ) {
                resize(size * 2);
            }
            return x;
        }
        return null;
    }
    @Override
    public Item removeLast(){
        if(size > 0){
            Item x = items[GetRealIndex(size-1)];
            items[GetRealIndex(size-1)] = null;
            size -= 1;
            nextLast -= 1;
            nextLast = GetRealIndex(size);
            if (items.length >= 16 && size < items.length / 4 ) {
                resize(size * 2);
            }
            return x;
        }
        return null;
    }
    @Override
    public int size(){
        return size;
    }
    @Override
    public Item get(int index){
        if(index >= 0 && index < size){
            return items[GetRealIndex(index)];
        }
        return null;
    }
    private int GetRealIndex(int index){
        int RealIndex = nextFirst + 1 + index;
        if(RealIndex >= items.length){
            RealIndex -= items.length;
        }
        if(RealIndex < 0){
            RealIndex += items.length;
        }
        return RealIndex;
    }
    @Override
    public void printDeque(){
        if(size > 0){
            int printItem = nextFirst;
            while(printItem != nextLast){
                printItem += 1;
                printItem = GetRealIndex(printItem);
                System.out.print(items[printItem] + " ");
            }
            System.out.println();
        }
    }

    public Iterator<Item> iterator(){
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<Item>{
        private int wizPos;
        public ArrayDequeIterator() {
            wizPos = 0;
        }
        public boolean hasNext() {
            return wizPos < size;
        }
        public Item next() {
            Item returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }
    @Override
    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ArrayDeque<Item> other = (ArrayDeque<Item>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i ++) {
            if (other.get(i) != this.get(i)) {
                return false;
            }
        }
        return true;
    }
}
