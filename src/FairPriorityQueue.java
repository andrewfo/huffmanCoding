/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, <NAME1> (and <NAME2),
 *  this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1:
 *  UTEID:
 *  email address:
 *
 *  Student 2:
 *  UTEID:
 *  email address:
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
