package dlsim.DLSim.Util;
import java.util.Vector;
import java.lang.Integer;
public abstract class BinOperation {

 /** foldLeft(input,object)
    * Folds this operation left onto the given vector
    * foldl op e x:xs = foldl (op e x) xs
    * @param input: The vector to fold this operation onto
    * @param e: The object to use at the rightmost edge
    */
	public  Object foldLeft(Vector input, Object e)
    {
        return foldl(input,e,0);
    }

    // private implementation of foldleft - uses pos parameter for index
    private Object foldl(Vector input, Object e,int pos)
    {
        if (pos<input.size())
        {
        	return foldl(input,op(e,input.elementAt(pos)),pos+1);
        }
        else
        {
            return e;
        }
    }


    /** foldRight(input,object)
    * Folds this operation right onto the given vector
    * foldr op e x:xs = op x (foldr op e xs)
    * @param input: The vector to fold this operation onto
    * @param e: The object to use at the rightmost edge
    */
	public  Object foldRight(Vector input, Object e)
    {
        return foldr(input,e,0);
    }

    // private implementation of foldright - uses pos parameter for index
    private Object foldr(Vector input, Object e,int pos)
    {
        if (pos<input.size())
        {
            return op(input.elementAt(pos), foldr(input,e,pos+1));
        }
        else
        {
            return e;
        }
    }

    /** op(in1, in2)
    * An abstract representation of any binary operation - such as *
    * creating an operation in this manner allows it to be used in folds
    * @param input1 first parameter
    * @param input2 second parameter
    * @returns the result of applying op to input 1 and input 2
    */
    public abstract Object op(Object input1, Object input2);

    public static BinOperation times()
    {
        return new BinOperation()
        {
            public Object op(Object input1, Object input2)
            {
                int i = ((Integer)input1).intValue();
                int j = ((Integer)input2).intValue();
				return new Integer(i*j);
            }
        };
    }
}
