package dlsim.DLSim.Util;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Operation
{
    /**
    * Maps this operation onto every element in the vector
    * null results are not included in the return.
    * @param input: The vector to map this operation onto
    * @return The vector that results after mapping Op onto it
    */
    public Vector mapOp(Vector input)
    {
        Vector newv = new Vector(input.size());
        Enumeration e = input.elements();
        while (e.hasMoreElements())
        {
            Object r = this.op(e.nextElement());
            if (r!=null) newv.addElement(r);
        }
        return newv;
    }


    /**
    * Maps this operation onto every element in the vector
    * null results are not included in the return.
    * @param input: The array on which to map this operation onto
    * @return The array that results after mapping Op onto it
    */
     public Object[] mapOp(Object[] input)
    {
        Object[] newv = new Object[input.length];
        int i=0;
        int n=0;
        while (i<input.length)
        {
             Object r = this.op(input[i]);
             if  (r!=null)
                 {
	                newv[n] = r;
                        n++;
                 }
             i++;
        }
        if (i==n) return newv; //if not trimming necessary, return
        Object[] newv2 = new Object[n]; //otherwise trim down to size
        System.arraycopy(newv,0,newv2,0,n);
        return newv2;
    }

    /**
    * Maps this operation onto every element in the vector
    * the result is discarded.
    * @param input: The vector to apply this operation onto
    */
	public void applyOp(Vector input)
    {
        Enumeration e = input.elements();
        while (e.hasMoreElements())
        {
			this.op(e.nextElement());
        }
    }

    public abstract Object op(Object input);


}
