import java.util.LinkedList;

public class FairPriorityQueue<E extends Comparable<E>> {
    private LinkedList<E> con;

    public FairPriorityQueue() {
        con = new LinkedList<>();
    }

    public void enqueue(E val) {
        int idx = 0;
        while (idx < con.size() && val.compareTo(con.get(idx)) >= 0) {
            idx++;
        }
        con.add(idx, val);
    }

    public E dequeue() {
        return con.removeFirst();
    }

    public E peek(E val) {
        return con.getFirst();
    }

    public int size() {
        return con.size();
    }

    public boolean isEmpty() {
        return con.isEmpty();
    }

}
