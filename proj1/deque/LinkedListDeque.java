package deque;
import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{
    //Nested-class
    private static class TNode<T> {
        public TNode<T> prev;
        public T item;
        public TNode<T> next;
        public TNode(T i, TNode<T> p, TNode<T> n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    public int size;
    public TNode<T> sentinel = new TNode<>(null, null, null);
    public LinkedListDeque(){
        size = 0;
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item){
        size += 1;
        TNode<T> newTNode = new TNode<>(item, sentinel, sentinel.next);
        sentinel.next.prev = newTNode;
        sentinel.next = newTNode;
        //newTNode = null;
    }
    public void addLast(T item){
        size += 1;
        TNode<T> newTNode = new TNode<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = newTNode;
        sentinel.prev = newTNode;
        //newTNode = null;
    }
    public T removeFirst(){
        if(size > 0){
            size -= 1;
            T first = sentinel.next.item;
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
            return first;
        }
        //System.out.println("False! There is no item to remove!");
        return null;
    }
    public T removeLast(){
        if(size > 0){
            size -= 1;
            T Last = sentinel.prev.item;
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
            return Last;
        }
        //System.out.println("False! There is no item to remove!");
        return null;
    }
    public int size(){
        return size;
    }

    public T get(int index){
        if(size > 0){
            TNode<T> getNode = sentinel.next;
            for(int i = 0; i < index; i++){
                getNode = getNode.next;
            }
            return  getNode.item;
        }
        return null;
    }
    public T getRecursive(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, TNode<T> currentNode) {
        if (index == 0) {
            return currentNode.item;
        }
        return getRecursiveHelper(index - 1, currentNode.next);
    }

    public boolean isEmpty(){
        if(size == 0){
            return true;
        }
        return false;
    }
    public void printDeque(){
        if(size > 0){
            TNode<T> printNode = sentinel.next;
            while(printNode.item != null){
                System.out.print(printNode.item + " ");
                printNode = printNode.next;
            }
            System.out.println();
        }
    }

    public Iterator<T> iterator(){
        return new LLiterator();
    }
    private class LLiterator implements Iterator<T>{
        private TNode<T> p;
        public LLiterator() {
            p = sentinel.next;
        }
        public boolean hasNext() {
            return p != sentinel;
        }
        public T next() {
            T returnItem = p.item;
            p = p.next;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }

        TNode<T> p1 = this.sentinel.next;
        TNode<T> p2 = other.sentinel.next;
        for (int i = 0; i < size; i++) {
            if (p1.item != p2.item) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }
        return true;
    }
}
