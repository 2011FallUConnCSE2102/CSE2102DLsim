package dlsim.DLSim;

/**
 * <p>Title: DLSim</p>
 * <p>Description: A simple priority queue, using linear time insert, and search</p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */
public class PriorityQueue
{
    private PQCell head;
    private int count = 0;

    public PriorityQueue() {
        head = new PQCell(null, Long.MAX_VALUE);
    }

    synchronized public int size() {
        return count;
    }

    synchronized public boolean empty() {
        return count == 0;
    }

    // lower priority # is on "top" of the stack, equal priority #'s give preference to objects
    // already in the queue.
    synchronized public void put(Object obj, long priority) {
        PQCell p;
        p = head.next;
        while (priority >= p.priority) p = p.next;
        insertPQCellBefore(p, new PQCell(obj, priority));
    }

    synchronized public Object get() {
        if (count == 0)
        {
            System.err.println("get from empty queue");
            return null;
        }
        else {
            Object res = head.next.obj;
            removePQCell(head.next);
            return res;
        }
    }

    synchronized public boolean remove(Object obj) {
        PQCell p = locate(obj);
        if (p == null)
            return false;
        else {
            removePQCell(p);
            return true;
        }
    }

    synchronized public Object contains(Object obj) {
        PQCell o = locate(obj);
        if (o!=null) return o.obj;
        return null;
    }

    private PQCell locate(Object obj) {
        PQCell p = head.next;
        while (p != head) {
            if (p.obj.equals(obj)) return p;
            p = p.next;
        }
        return null;
    }

    private void removePQCell(PQCell x) {
        PQCell prev = x.prev;
        PQCell next = x.next;
        prev.next = next; next.prev = prev;
        x.next = x; x.prev = x;
        count--;
    }

    private void insertPQCellBefore(PQCell x, PQCell n) {
        n.prev = x.prev; n.next = x;
        x.prev.next = n; x.prev = n;
        count++;
    }
}

class PQCell
{
    PQCell prev, next;
    long priority;
    Object obj;

    public PQCell(Object obj, long priority) {
        this.obj = obj;
        this.priority = priority;
        next = prev = this;
    }

}
