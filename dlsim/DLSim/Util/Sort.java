package dlsim.DLSim.Util;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

import java.util.*;

public class Sort
{
    /** The Order interface */
    public static interface Order
    {
    public boolean lessThan(Object a,Object b);
    public boolean equals(Object a,Object b);
    }

    /* Am implementation of order than defines a lexographic ordering */
    public static Order alphabeticalOrder()
    {
     return new Order()
            {
            public boolean lessThan(Object a, Object b)
                {
                 return (a.toString().compareTo(b.toString())<0);
                }
            public boolean equals(Object a,Object b)
                {
                 return(a.toString().compareTo(b.toString())==0);
                }
            };
    }


    /** The order for this sorter */
    private Order order;



   /** Creates a sorter using the supplied ordering */
    public Sort(Sort.Order order) {this.order = order; }

    /** A wrapper for sequences. Converts to array, and uses normal sort */
    public Vector sortVector(Vector v)
      {
        final Object[] seq = new Object[v.size()];
        v.copyInto(seq);  // vector back to array, for sorting.
        Vector s= new Vector(seq.length);
        this.sort(seq); // sort the array
        for (int i=0;i<seq.length;i++)
        {
          s.add(seq[i]);
        }
        return s;
      }

    /** Sorts the given array */
    public void sort(Object[] in)
    {
     quickSort(0,in.length-1,in);
    }

    /** Quicksort a[i1..i2] */
    private void quickSort(int i1, int i2, Object[] a)
    {
     if (i1<i2)
        {
        int p=partition(i1,i2,a);
        quickSort(i1,p,a);
        quickSort(p+1,i2,a);
        }

    }

    /** partition a[i1..i2] */
    private int partition(int i1, int i2, Object[] a)
    {
    Object x=a[i1];
    int i=i1-1;
    int j=i2+1;
    while (true)
        {
        do
            {
            j--;
            }
            while ( (!order.lessThan(a[j],x)) && (!order.equals(a[j],x)) );
        do
            {
            i++;
            }
            while (order.lessThan(a[i],x) );

        if(i<j)
            {
            swap(a,i,j);
            }
            else break;
        }
    return j;
    }


    /** Swap two elements of an array */
    private void swap(Object[] array, int i, int j)
    {
        Object temp=array[i];
        array[i]=array[j];
        array[j]=temp;
    }
}
