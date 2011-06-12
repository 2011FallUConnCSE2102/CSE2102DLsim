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

public class InputTerminalModel extends TerminalModel
{

  int id=-1;
  String name;
  boolean currentvalue=false;
  boolean getbackoldvalue=false;

  public InputTerminalModel(CircuitModel c, int id)
  {
   super(0,1,c);
   this.setID(id);
   InputTerminalModel collided = collision();
   if (collided!=null)
   {
     this.setID(findID(c));
   }
   this.setControl(new TerminalControl(this));
   this.inValidate();
  }

  InputTerminalModel collision()
   {
     Enumeration e =this.getCircuit().getComponents().elements();
     while (e.hasMoreElements())
     {
       Object o = e.nextElement();
        if (o instanceof dlsim.DLSim.concrete.InputTerminalModel)
        {
          InputTerminalModel otm = ((InputTerminalModel)o);
          if (otm.getID()==this.getID() && otm!=this) return otm;
        }
     }
     return null;
   }


 public InputTerminalModel(CircuitModel c)
  {
   super(0,1,c);
   this.setID(findID(c));
   this.setControl(new TerminalControl(this));
  }

  private int findID(CircuitModel c)
  {
    Enumeration e = c.getComponents().elements();
    int max=-1;
    while (e.hasMoreElements())
    {
     Object m =  e.nextElement();
     if (m instanceof InputTerminalModel)
        {
           if (((InputTerminalModel)m).getID()>=max)
           max = ((InputTerminalModel)m).getID();
        }
    }
    return (max+1);
  }

  public int getSize()
 {
   return 1;
  }

  public void setName(String newname)
  {
    this.name=newname;
    ((GenericComponentView)this.getView()).setLabel(name);
  }

  public String getName()
  {
   return name;
  }

  public int getID()
  {
   return id;
  }

  public void setID(int newid)
  {
  this.id=newid;
  setName("Input "+newid);
  }

  public void removed()
  {
    super.removed();
    getbackoldvalue=true;
    budgeDownInputs(this);
  }

  public void added()
  {
    if (getbackoldvalue && (collision()!=null))  budgeUpInputs(this);
  }

  private void budgeDownInputs(ComponentModel m)
  {
    Enumeration e =this.getCircuit().getComponents().elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      // reduce the number of any following inputs to fill in the gap this leaves
      if (o instanceof dlsim.DLSim.concrete.InputTerminalModel)
      {
        InputTerminalModel itm = ((InputTerminalModel)o);
        if (itm.getID()>this.getID()) itm.setID(itm.getID()-1);
      }
    }
  }

  private void budgeUpInputs(ComponentModel m)
  {
    Enumeration e =this.getCircuit().getComponents().elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      // reduce the number of any following inputs to fill in the gap this leaves
      if (o instanceof dlsim.DLSim.concrete.InputTerminalModel)
      {
        InputTerminalModel itm = ((InputTerminalModel)o);
        if ((itm.getID()>=this.getID()) )
          itm.setID(itm.getID()+1);
      }
    }
  }

  public boolean[] doLogic(boolean[] in)
  {
    return new boolean[] {currentvalue};
  }

  public void setValue(boolean v)
  {
   if (v!=currentvalue )
   {
   currentvalue=v;
   this.inValidate();
   }
  }
}

