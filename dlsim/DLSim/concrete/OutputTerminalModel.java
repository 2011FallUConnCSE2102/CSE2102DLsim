package dlsim.DLSim.concrete;
import dlsim.DLSim.*;
import dlsim.DLSim.Util.*;
import java.util.*;
/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class OutputTerminalModel extends TerminalModel
{

  boolean currentvalue=false;
   boolean getbackoldvalue=false;
  String name;
  int id=-1;

  public OutputTerminalModel(CircuitModel c, int id)
  {
   super(1,0,c);
   setID(id);
   this.setControl(new TerminalControl(this));
   if (collision()) setID(findID(c));
  }

  public OutputTerminalModel(CircuitModel c)
  {
   super(1,0,c);
   setID(findID(c));
   this.setControl(new TerminalControl(this));
  }

   private int findID(CircuitModel c)
  {
    Enumeration e = c.getComponents().elements();
    int max=-1;
    while (e.hasMoreElements())
    {
     Object m = e.nextElement();
     if (m instanceof OutputTerminalModel)
        {
           if (((OutputTerminalModel)m).getID()>=max)
           max = ((OutputTerminalModel)m).getID();
        }
    }
    return(max+1);
  }

   public String getName()
  {
   return name;
  }

  public void setID(int newid)
  {
   this.id=newid;
   setName("Output "+newid);
  }

  public int getID()
  {
   return id;
  }

  public int getSize()
 {
   return 1;
  }


  public boolean[] doLogic(boolean[] in)
  {
    currentvalue=in[0];
    return in;
  }

  public void removed()
  {
    super.removed();
    renumberOutputs(this);
  }

  boolean collision()
  {
    Enumeration e =this.getCircuit().getComponents().elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
       if (o instanceof dlsim.DLSim.concrete.OutputTerminalModel)
       {
         OutputTerminalModel otm = ((OutputTerminalModel)o);
         if (otm.getID()==this.getID() && otm!=this) return true;
       }
    }
    return false;
  }

  private void renumberOutputs(ComponentModel m)
  {
    Enumeration e =this.getCircuit().getComponents().elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      // reduce the number of any following outputs to fill in the gap this leaves
      // when removed from the circuit
      if (o instanceof dlsim.DLSim.concrete.OutputTerminalModel)
      {
        OutputTerminalModel otm = ((OutputTerminalModel)o);
        if (otm.getID()>this.getID()) otm.setID(otm.getID()-1);
      }
    }
  }

  private void budgeUpOutputs(ComponentModel m)
 {
   Enumeration e =this.getCircuit().getComponents().elements();
   while (e.hasMoreElements())
   {
     Object o = e.nextElement();
     // reduce the number of any following inputs to fill in the gap this leaves
     if (o instanceof dlsim.DLSim.concrete.OutputTerminalModel)
     {
       OutputTerminalModel otm = ((OutputTerminalModel)o);
       if ((otm.getID()>=this.getID()) && (otm!=m)) otm.setID(otm.getID()+1);
     }
   }
  }

  public void setName(String newname)
  {
    this.name=newname;
    ((GenericComponentView)this.getView()).setLabel(name);
  }

  public void added()
{
    if (getbackoldvalue  && collision() )
    budgeUpOutputs(this);
  }

  public boolean getValue()
  {
   return currentvalue;
  }
}


