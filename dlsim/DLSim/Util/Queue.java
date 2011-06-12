package dlsim.DLSim.Util;
import java.util.*;
import dlsim.DLSim.Debug;

public class Queue
{
        ListNode front;
        ListNode back;
        int size=0;

public Object popFront() throws EmptyQueue
    {
      ListNode n = front;
      if (n!=null)
        {
          if(front==back)// if only node
            {
             front=null;
             back=null;
            }
          else // at least 2 nodes in list
            {
              front=n.next();
              n.next().setPrev(n.prev()); //unlink from next
              n.prev().setNext(n.next()); //unlink from prev
            }
         size--;
         return n.getObject();
        }
     throw new EmptyQueue("no elements in queue");
    }


public Object popBack() throws EmptyQueue
    {
      ListNode n = back;
      if (n!=null)
        {
         if(front==back)// if only node
            {
             front=null;
             back=null;
            }
          else // at least 2 nodes in list
            {
              back=n.prev();
              n.next().setPrev(n.prev()); //unlink from next
              n.prev().setNext(n.next()); //unlink from prev
            }
         size--;
         return n.getObject();
        }
      throw new EmptyQueue("no elements in queue");
    }

public Object peekFront()
{
 if (front==null) return front;
 else return front.getObject();
}

public void pushFront(Object f)
    {
      size++;
      if (front!=null)
        {
         ListNode newnode = new ListNode(f);
         newnode.setNext(front);
         newnode.setPrev(back);
         back.setNext(newnode);
         front.setPrev(newnode);
         front=newnode;
        }
      else
        {
          firstNode(f);
        }
    }

public void pushBack(Object b)
    {
       size++;
       if (back!=null)
        {
         ListNode newnode = new ListNode(b);
         newnode.setNext(front);
         newnode.setPrev(back);
         back.setNext(newnode);
         front.setPrev(newnode);
         back=newnode;
        }
      else
        {
          firstNode(b);
        }
    }

    /**
     *Number of components in queue
     * @return size of queue
     */
public int size()
       {
        return size;
       }

       /**
        * Check if this queue is empty
        * @return true if empty
        */
public boolean empty()
    {
        return (front==null);
    }

private void firstNode(Object o)
    {
      ListNode newnode = new ListNode(o);
      newnode.setNext(newnode);
      newnode.setPrev(newnode);
      front=newnode;
      back=newnode ;
    }

    /**
     * Removes this element, if present
     */
public void remove(Object e)
{
  ListNode n = front;
  int i=0;
  int osize=size;
  while (n!=null && i<osize)
  {
    if (n.getObject()==e) //remove node
    {
      if (front==back) //only node
      {
        front=null;
        back=null;
      }
      else
      {
        //unlink
        n.prev().setNext(n.next());
        n.next().setPrev(n.prev());
        //keep pointers uptodate
        if (n==front) front=n.next();
        if (n==back) back=n.prev();
      }
      size--;
    }
    // next node
    i++;
    n=n.next();
  }
}



}

 class ListNode
{
  private ListNode pre;
  private ListNode nex;
  private Object o;
  public ListNode(Object o) {this.o=o;}
  public ListNode prev(){return pre;}
  public ListNode next(){return nex;}
  public void setNext(ListNode n){nex=n;}
  public void setPrev(ListNode p){pre=p;}
  public Object getObject() {return o;}

}
class EmptyQueue extends Error
{
  public EmptyQueue(String reason) {super(reason);}
}
