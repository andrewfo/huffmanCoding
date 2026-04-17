/*  Student information for assignment:
 *
 *  On our honor, David and Andrew,
 *  this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 1
 *
 *  Student 1: Andrew Ma
 *  UTEID: azm484
 *  email address: azm484@eid.utexas.edu
 *
 *  Student 2: David Toghanro
 *  UTEID: dt28755
 *  email address: dt28755@eid.utexas.edu
 *
 *  Grader name:
 *  Section number:
 */

import java.util.LinkedList;

/**
 * Generic fair priority queue. Equal-priority items stay behind existing ones.
 */
public class FairPriorityQueue<E extends Comparable<E>> {
    private LinkedList<E> con;

    /** Construct an empty queue. */
    public FairPriorityQueue() {
        con = new LinkedList<>();
    }

    /** Insert val after all existing items of equal or lower priority. */
    public void enqueue(E val) {
        int idx = 0;
        while (idx < con.size() && val.compareTo(con.get(idx)) >= 0) {
            idx++;
        }
        con.add(idx, val);
    }

    /**
     * Remove and return the front element.
     * @throws IllegalStateException if the queue is empty.
     */
    public E dequeue() {
        if (con.isEmpty()) {
            throw new IllegalStateException("dequeue on empty queue");
        }
        return con.removeFirst();
    }

    /**
     * Return the front element without removing it.
     * @throws IllegalStateException if the queue is empty.
     */
    public E peek() {
        if (con.isEmpty()) {
            throw new IllegalStateException("peek on empty queue");
        }
        return con.getFirst();
    }

    /** Number of items in the queue. */
    public int size() {
        return con.size();
    }

    /** True if the queue has no items. */
    public boolean isEmpty() {
        return con.isEmpty();
    }
}
