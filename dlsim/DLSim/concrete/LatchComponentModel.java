package dlsim.DLSim.concrete;

import dlsim.DLSim.*;


/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class LatchComponentModel extends ComponentModel implements labeledComponent
{

  boolean storedvalue=false;

  public LatchComponentModel(CircuitModel c)
  {
    super(2,1,c);
    ((GenericComponentView)this.getView()).setLabel("Latch");
  }

  public String getInputName(int i)
  {
    if (i==0) return "D";
    if (i==1) return "Clock";
    return null;
  }

  public String getOutputName(int i)
 {
   if (i==0) return "Result";
   return null;
  }

  public int getSize()
  {
    return 1;
  }

  public boolean[] doLogic(boolean[] in)
  {
    if (in[1]) {storedvalue=in[0]; }
    return new boolean[] {storedvalue};
  }
}

