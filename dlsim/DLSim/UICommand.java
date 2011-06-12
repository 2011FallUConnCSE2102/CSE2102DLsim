package dlsim.DLSim;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.io.*;
import dlsim.DLSim.Util.staticUtils;
import dlsim.DLSim.concrete.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: Represent logged undoable/redoable commands. </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public abstract class UICommand
{
  private static Vector donecommands = new Vector();
  private static Vector undoneCommands = new Vector();
  private static Component messageComponent = UIMainFrame.currentinstance;
  private static Vector listeners=new Vector();

  abstract String name();

  abstract boolean doCommand();

  abstract boolean undoCommand();

  abstract boolean redoCommand();

  abstract boolean isUndoable();

  public static boolean canUndo()
  {
    return !donecommands.isEmpty();
  }

  public static String lastDoneCommandName()
  {
    if (donecommands.isEmpty()) return "impossible";
    return ((UICommand)donecommands.lastElement()).name();
  }

  public static String lastUnDoneCommandName()
 {
   if (undoneCommands.isEmpty()) return "impossible";
   return ((UICommand)undoneCommands.lastElement()).name();
 }

  public static boolean canRedo()
 {
   return !undoneCommands.isEmpty();
  }

  public interface CommandListener
  {
    public void commandExecuted();
  }

  /** this commandlistener will be notified whenever a comamnd is executed*/
  public static void addListener(CommandListener cl)
  {
    listeners.add(cl);
  }

  private static void notifyListeners()
  {
   Enumeration e = listeners.elements();
   while (e.hasMoreElements())
   {
     ((CommandListener)e.nextElement()).commandExecuted();
   }
  }

  public void execute()
  {
    // show a confirmation dialog if necessary


    // try and execute command
    if (!this.doCommand())
    {
     // command failed to execute - show error
     JOptionPane.showMessageDialog(messageComponent,"Can't do "+
                                   name()+this.failedAdvice(),"Warning",JOptionPane.WARNING_MESSAGE);
     return;
    }
    if (this.isUndoable())
    {
      UICommand.undoneCommands.clear();
      donecommands.add(this);
    }
    notifyListeners();
  }

  private static int cmdid=0;

  public static void undoLastCommand()
  {
    if (donecommands.isEmpty())
    {
      JOptionPane.showMessageDialog(messageComponent,"Nothing to undo");
      return;
    }
    UICommand lastcommand = (UICommand) donecommands.lastElement();


    if (!lastcommand.undoCommand())
    {
      JOptionPane.showMessageDialog(messageComponent,"Failed to undo user command "+
                                    lastcommand.name()+lastcommand.failedAdvice(),"Warning",JOptionPane.WARNING_MESSAGE);
      return;
    }

    donecommands.remove(lastcommand);
    undoneCommands.add(lastcommand);
    notifyListeners();
  }

  public static void redoLastCommand()
  {
    if (undoneCommands.isEmpty())
    {
      JOptionPane.showMessageDialog(messageComponent,"Nothing to redo");
      return;
    }
    UICommand lastcommand = (UICommand) undoneCommands.lastElement();

    if (!lastcommand.redoCommand())
    {
      JOptionPane.showMessageDialog(messageComponent,"Failed to redo user command "+
                                    lastcommand.name()+lastcommand.failedAdvice(),"Warning",JOptionPane.WARNING_MESSAGE);
      return;
    }

    undoneCommands.remove(lastcommand);
    donecommands.add(lastcommand);
    notifyListeners();
  }

 public String failedAdvice() {return "";}

  public static UICommand newFile(final CircuitModel root)
  {
    return  new UICommand()
    {
      Vector oldComponents;
      Vector oldWires;
      URL oldURL;

      public String name()
      {
       return "New Circuit";
      }

      public boolean doCommand()
      {
        oldComponents = root.getComponents();
        oldWires = root.getWires();
        oldURL=root.getFrom();
        root.clear();
        root.setFrom(null);
        return true;
      }

      public boolean redoCommand()
      {
        return this.doCommand();
      }

      public boolean undoCommand()
      {
        root.clear();
        root.setFrom(oldURL);
        root.getView().clear();
        Enumeration e = oldComponents.elements();
        while (e.hasMoreElements())
        root.addComponent((ComponentModel)e.nextElement());
         e = oldWires.elements();
        while (e.hasMoreElements())
        root.addWire((WireModel)e.nextElement());
        oldComponents=null;
        oldWires=null;
        return true;
      }

      public boolean isUndoable()
      {
        return true;
      }

    };
  }

  public static UICommand saveFile(final CircuitModel root,final File f)
  {
    return  new UICommand()
    {
      public String name() {return "Save Circuit";}

      public boolean doCommand()
      {
        CircuitMemento mem = new CircuitMemento(root);
        try {
        mem.writeToFile(f);
        root.setFrom( staticUtils.URLFromFile(f));
        return true;}
        catch (Exception e) {return false;}
      }

      public boolean redoCommand()
      {
        return false;
      }

      public boolean undoCommand()
      {
        return false;
      }

      public boolean isUndoable()
      {
        return false;
      }

    };
  }

  public static UICommand loadFile(final CircuitModel root,final File f)
 {
    return loadFile(root,staticUtils.URLFromFile(f));
  }

  public static UICommand loadFile(final CircuitModel root,final URL u)
 {
  return  new UICommand()
  {
    public String name() {return "Load Circuit";}

    private URL oldURL;
    Vector oldComponents;
    Vector oldWires;
    Vector oldSelection;
    Vector addedSelection;
    public boolean doCommand()
    {
      try {
        CircuitMemento mem = new CircuitMemento(u);
        oldComponents = root.getComponents();
        oldWires= root.getWires();
        oldURL=root.getFrom();
        root.clear();
        root.setFrom(u);
        addedSelection = mem.addToCircuit(root);
        return true;
      }
      catch (IOException e) {e.printStackTrace();return false;}
    }

    public boolean redoCommand()
    {
      root.clear();
      if (addedSelection==null) return false;
      // put back the components
      Enumeration e = addedSelection.elements();
      while (e.hasMoreElements())
      {
        Object o = e.nextElement();
        if (o instanceof ComponentModel)
        {
          root.addComponent((ComponentModel)o);
        }
      }
      //put back the wires
      e = addedSelection.elements();
      while (e.hasMoreElements())
      {
        Object o = e.nextElement();
        if (o instanceof WireModel)
        {
          root.addWire((WireModel)o);
        }
      }
      return true;
    }


    public boolean undoCommand()
    {
      root.clear();
      Enumeration e = oldComponents.elements();
        while (e.hasMoreElements())
        root.addComponent((ComponentModel)e.nextElement());
         e = oldWires.elements();
        while (e.hasMoreElements())
        root.addWire((WireModel)e.nextElement());
      root.setFrom(oldURL);
      return true;
    }

    public boolean isUndoable()
    {
      return true;
    }


  };
  }

  public static UICommand addComponent(final ComponentFactory cf,final Point location)
  {
    return  new UICommand()
    {
      public String name() {return "Add Component";}

      ComponentModel c;
      ComponentModel undone;

      public boolean doCommand()
      {
        c = cf.getComp();
        if (c==null) return false;
        c.getView().setLocation(location);
        Vector v = new Vector(2);
        v.add(c);
        c.getCircuit().getView().getControl().setSelection(v);
        return true;
      }

      public boolean redoCommand()
      {
        c.getCircuit().addComponent(c);
        return true;
      }

      public boolean undoCommand()
      {
        c.getCircuit().removeComponent(c);
        c.getCircuit().getView().getControl().deselect();
        return true;
      }

      public boolean isUndoable()
      {
        return true;
      }


    };
  }

  public static UICommand changeClockSpeed(final ClockComponentModel clk,final int newspeed)
 {
   return  new UICommand()
   {
     public String name() {return "Alter Clock Speed";}

     int oldspeed;

     public boolean doCommand()
     {
       oldspeed=clk.clkp;
       clk.clk=0;
       clk.setClockPeriod(newspeed);
       return true;
     }

     public boolean redoCommand()
     {
       return doCommand();
     }

     public boolean undoCommand()
     {
       clk.setClockPeriod(oldspeed);
       return true;
     }

     public boolean isUndoable()
     {
       return true;
     }


   };
  }

  public static UICommand changeTerminalName(final TerminalModel term,final String newname)
{
  return  new UICommand()
  {
    public String name() {return "Alter Terminal Name";}

    String oldname;

    public boolean doCommand()
    {
      oldname=term.getName();
      term.setName(newname);
      return true;
    }

    public boolean redoCommand()
   {
     return doCommand();
     }

    public boolean undoCommand()
    {
      term.setName(oldname);
      return true;
    }

    public boolean isUndoable()
    {
      return true;
    }


  };
  }

  public static UICommand changeTerminalName(final OscilloscopeComponentModel term,final String newname)
 {
   return  new UICommand()
   {
     public String name() {return "Alter Oscilloscope Name";}

     String oldname;

     public boolean doCommand()
     {
       oldname=term.getName();
       term.setName(newname);
       return true;
     }

     public boolean redoCommand()
     {
       return doCommand();
     }

     public boolean undoCommand()
     {
       term.setName(oldname);
       return true;
     }

     public boolean isUndoable()
     {
       return true;
     }


   };
   }

  public static UICommand deleteComponents(final Vector c,final Vector w,final CircuitModel m)
 {
   return  new UICommand()
   {
     public String name() {return "Delete";}
     Rectangle oldselection;

     public boolean doCommand()
     {

       Enumeration wires = w.elements();
       Debug.out("Deleting "+w.size()+" Wires");
       while (wires.hasMoreElements())
       {
         m.removeWire((WireModel)wires.nextElement());
       }
       Enumeration components = c.elements();
       Debug.out("Deleting "+c.size()+" components");
       while (components.hasMoreElements())
       {
         ComponentModel cm = (ComponentModel) components.nextElement();
         m.removeComponent(cm);
       }
       return true;
     }

     public boolean redoCommand()
     {
       return doCommand();
     }

     public boolean undoCommand()
     {
       Enumeration components = c.elements();
       while (components.hasMoreElements())
       {
         ComponentModel cm = (ComponentModel)components.nextElement();
         m.addComponent(cm);
       }
       Enumeration wires = w.elements();
       Debug.out("Reconnecting "+w.size()+" wires");
       while (wires.hasMoreElements())
       {
        WireModel w = ((WireModel)wires.nextElement());
        if (!w.getTo().getInputWires().contains(w))
        {
          w.getTo().addInputWire(w);
          Debug.out("Reconnecting wire to input");
        }
        if (!w.getFrom().getOutputWires().contains(w))
        {
          w.getFrom().addOutputWire(w);
          Debug.out("Reconnecting wire to output");
        }
         m.addWire(w);
         Debug.out("Reconnecting wire to circuit");
       }
       m.getView().getControl().setSelection(c);
       return true;
     }

     public boolean isUndoable()
     {
       return true;
     }

   };
  }

  public static UICommand addWire(final ComponentModel from,final int out,final ComponentModel to,final int in)
 {
   return  new UICommand()
   {
     public String name() {return "Add Wire";}

     WireModel w;
     CircuitModel c;

     public boolean doCommand()
     {
       c=to.getCircuit();
       w = new WireModel(from,out,to,in);
       c.addWire(w);
       return true;
     }

     public boolean redoCommand()
     {
       w.getFrom().addOutputWire(w);
       w.getTo().addInputWire(w);
       c.addWire(w);
       return true;
     }

     public boolean undoCommand()
     {
       c.removeWire(w);
       return true;
     }

     public boolean isUndoable()
     {
       return true;
     }


   };
  }

// moving is not an undoable action
/**  public static UICommand translateComponents(final Vector components,final CircuitModel c,final int x,final int y)
{
  return  new UICommand()
  {
    public String name() {return "Move Component(s)";}

    public boolean doCommand()
    {
      c.getView().getControl().translateComponents(x,y,components);
      c.getView().getControl().setSelection(components);
      return true;
    }

    public boolean redoCommand()
    {
      return doCommand();
    }

    public boolean undoCommand()
    {
     c.getView().getControl().translateComponents(-x,-y,components);
     c.getView().getControl().setSelection(components);
     return true;
    }

    public boolean isUndoable()
    {
      return true;
    }

  };
  }*/

  public static UICommand unbundle(final WireBundle b)
  {
    return  new UICommand()
{
  public String name() {return "Unbundle wires";}
  WireBundle oldBundle;
  Vector wires;


  public boolean doCommand()
  {
    // store old state
    wires = (Vector)b.getWires().clone();
    oldBundle = b;
    // do command
    b.debundle();
    return true;
  }

  public boolean redoCommand()
  {
    return doCommand();
  }

  public boolean undoCommand()
  {
    //put old state back
    for (int i=0;i<wires.size();i++)
    {
      b.addBundle( ((WireModel)wires.elementAt(i)).getBundle());
    }
    return true;
  }

  public boolean isUndoable()
  {
    return true;
  }

 };
  }

  public static UICommand bundleWires(final Vector w)
{
 return  new UICommand()
 {
   public String name() {return "Bundle together wires";}
   HashMap wireToBundle;
   HashMap wireToView;
   Vector wires;
   WireBundle b;

   public boolean doCommand()
   {
     if (wires==null) wires = (Vector)w.clone();
     wireToBundle = new HashMap();
     wireToView = new HashMap();

     // store old state
     for (int i=0;i<wires.size();i++)
     {
       WireModel w = (WireModel) wires.elementAt(i);
       wireToBundle.put(w,w.getBundle());
       wireToView.put(w,w.getView());
     }
     //change state
     WireModel w = (WireModel) wires.firstElement();
     b = w.getBundle();
     w.setBundle(b);
     for (int i=1;i<wires.size();i++)
     {
       w = (WireModel) wires.elementAt(i);
       b.addBundle(w.getBundle());
     }
     return true;
   }

   public boolean redoCommand()
   {
     return doCommand();
   }

   public boolean undoCommand()
   {
     b.debundle();
     //put old state back
     for (int i=0;i<wires.size();i++)
     {
       WireModel w = (WireModel) wires.elementAt(i);
       w.setBundle((WireBundle)wireToBundle.get(w));
       w.setView((WireView)wireToView.get(w));
     }
     //repaint
     WireModel w = (WireModel) wires.firstElement();
     return true;
   }

   public boolean isUndoable()
   {
     return true;
   }

 };
  }
  public static UICommand copy(final Vector components,final Vector wires,final CircuitModel target)
  {
    return  new UICommand()
    {
      Vector initialcomponents;
      Vector addedSelection;
      public String name() {return "Copy Component(s)";}

      public boolean doCommand()
      {
        try
        {
          CircuitMemento mem;
          Enumeration ws = ((Vector)wires.clone()).elements();
          // check this selection hasnt got any 'half wires' - i.e. wires with only one end in the selection
          // if there are any half wires, they are removed here.
          while (ws.hasMoreElements())
          {
            WireModel w = (WireModel) ws.nextElement();
            if ( !(components.contains(w.getFrom())) || !(components.contains(w.getTo())) )
            {
              // remove all instances incase there is more than one.
              while (wires.indexOf(w)!=-1)
              {
                wires.remove(w);
              }
              Debug.out("Removed halfwire,"+wires.size()+" wires remaining");
            }
          }
          initialcomponents=components;
          mem = new CircuitMemento(wires,components); //copy selection
          addedSelection= mem.addToCircuit(target);
          target.getView().getControl().setSelection(addedSelection);
          target.getView().getControl().translateSelection(15,15);
          return true;
        }
        catch (Exception e) {
          return false;
        }
      }

      public boolean redoCommand()
    {
      if (addedSelection==null) return false;
      // put back the components
      Enumeration e = addedSelection.elements();
      while (e.hasMoreElements())
      {
        Object o = e.nextElement();
        if (o instanceof ComponentModel)
        {
          target.addComponent((ComponentModel)o);
        }
      }
      //put back the wires
      e = addedSelection.elements();
      while (e.hasMoreElements())
      {
        Object o = e.nextElement();
        if (o instanceof WireModel)
        {
          target.addWire((WireModel)o);
        }
      }
      return true;
    }

      public boolean undoCommand()
      {
        Enumeration e = getComponents.mapOp(addedSelection).elements();
        while (e.hasMoreElements())
        {
         target.removeComponent((ComponentModel)e.nextElement());
        }
        target.getView().getControl().setSelection(initialcomponents);
        return true;
      }

      public boolean isUndoable()
      {
        return true;
      }

      public String failedAdvice()
      {
        return "\n Make sure you have selected terminals at both ends of any wires";
      }

    };
  }

  static dlsim.DLSim.Util.Operation getComponents = new dlsim.DLSim.Util.Operation()
  {
    public Object op(Object in)
    {
      if (in instanceof ComponentModel) return in; else return null;
    }
  };
}