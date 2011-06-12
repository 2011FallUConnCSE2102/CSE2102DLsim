package dlsim.DLSim;
import dlsim.DLSim.Util.*;
import dlsim.DLSim.Util.Queue;

import javax.swing.SwingUtilities;

/**
 * CircuitModel. A circuit is a collection of Components, and their connections.
 * @version 1.0
 */
import java.util.*;
import java.net.URL;
import java.io.*;

public class CircuitModel {

    private Vector components = new Vector();

    private Vector wires = new Vector();

    private CircuitViewInterface cv;
    private CircuitControlInterface cc;

    private Queue queue = new Queue();



    public CircuitModel()
    {
     this.setView(new EmptyView());
    }

    /** Adds this component to the circuit.
     *  will put on invalid queue if necessary, and add to the view
     *
     * @param c the component to add
     */
    public void addComponent(ComponentModel c) {
       if (c==null) return;
         c.added();
        Debug.out("added "+c);
        components.addElement(c);
        // new elements are always invalid. put in invalid queue
        if (!c.isValid()) queue.pushBack(c);
        this.getView().add(c.getView());
    }

    /** Adds this wire to the circuit.
     *  @param c the component to add
     */
    public void addWire(WireModel w) {
      if (w==null) return;
        wires.addElement(w);
        Debug.out("Wire added");
        this.getView().add(w.getView());
    }


    public void invalidate(ComponentModel m)
    {
    queue.pushBack(m);
    this.getView().requestUpdateTerminals(m.getView());
    if (myIC!=null)
        {
        myIC.inValidate();
        }
    }

    /**
     * @return true if all components in circuit are valid
     */
    public boolean isValid()
    {
     //all components in circuit are valid iff invalidqueue is empty
     return (queue.peekFront()==null || queue.size()==0 );
    }

    public void doSimulationStep()
    {
    // Declare variables
    Object o;
    ComponentModel c;
    Vector invalidwires = new Vector(wires.size());
    int componentsvalidated=0;
    int wiresvalidated;
    long time=System.currentTimeMillis();
    CircuitViewInterface view = this.getView();
    // do the simulation
    queue.pushBack(null); // put a null object on to mark the end of events
                          // occuring in this time step
    while (  (o=queue.popFront())!=null ) //go through the current top timestep
          {
           c=(ComponentModel)o;           //revalidate everything invalidated there
           componentsvalidated++;
           c.validate();
           if (c.isValid()) getView().requestUpdateTerminals(c.getView());
           invalidwires.addAll(c.getOutputWires());//keep track of which wires are connected to these components
          }
    WireModel.sendAll().applyOp(invalidwires);
    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          getView().updateTerminals();
        }
      });
    //performance information
    wiresvalidated=invalidwires.size();
    time=System.currentTimeMillis()-time;
    SimulationRunner.totalwires+=wires.size();
    SimulationRunner.totalcomponents+=components.size();
    SimulationRunner.wires_conscidered+=wiresvalidated;
    SimulationRunner.components_conscidered+=componentsvalidated;
    }



    /**
    * Clears all components from this circuit, reseting it to its intial state
    */
    public void clear()
    {
      Vector oldcomponents = components;
      components = new Vector();
      queue = new Queue();
      wires = new Vector();
      Operation remove = new Operation()
      {
        public Object op(Object in)
        {
          ((ComponentModel)in).removed();
          return null;
        }
      };
      remove.applyOp(oldcomponents);
      this.setFrom((URL)null);
      this.setIC(null);
      this.getView().clear();
    }


    /**
    * Removes the wire from the circuit, and derefrences it from its
    * components
    * @param w the WireModel to remove from the circuit
    */
    public void removeWire(WireModel w)
    {
        if (!wires.contains(w)) return;
        w.getTo().removeInputWire(w);
        w.getFrom().removeOutputWire(w);
        wires.remove(w);
        getView().remove(w.getView());
    }

    // this operation removes the wire given as input from this circuit
    private Operation removeWires()
    {
        final CircuitModel model = this;
        return new Operation()
        {
            public Object op(Object input)
            {
                model.removeWire((WireModel)input);
                return null;
            }
        };
    }



    /**
    * Removes the component, and all the wires connecting to/from it
    * from the circuitModel
    * @param m the component to remove
    */
    public void removeComponent(ComponentModel m)
    {
        if (!components.contains(m)) return; // must be present to be removed
        // remove wires to/from this component
        this.removeWires().mapOp(m.getInputWires());
        this.removeWires().mapOp(m.getOutputWires());
        //remove from components list
        components.removeElement(m);
        //make sure it isnt in the queue either
        queue.remove(m);
        //no need to paint it anymore
        getView().remove(m.getView());
        // tell it it's gone.
        m.removed();
        //all references to this should now die at the next repaint
        //allowing garbage collection
    }





    public int getNumberComponents()
    {
      int i=0;
      int ncomponents=0;
      while (i<components.size())
      {
        ncomponents+=((ComponentModel)components.elementAt(i)).getSize();
        i++;
      }
      return ncomponents;
    }
    /**
    * @return The wires in this circuit
    */
    public Vector getComponents()
    {
        return components;
    }

    /**
    * @return The wires in this circuit
    */
    public Vector getWires()
    {
        return wires;
    }


        /**
    * @return the view being used to render this circuit
    */
    public CircuitViewInterface getView()
    {
        return cv;
    }

    public void setView(CircuitViewInterface cvn)
    {
       cv=cvn;
    }



    private URL definedURL;
    private CircuitModel parent;

    /** get the file, (if any) that defines this circuit */
    public URL getFrom()
    {
     return definedURL;
    }



    /** set the file that defined this circuit
     * This is used to do dynamic linking when saving, and to check for recursion
     * when doing dynamic linking
     * Trying to set from web or LMS has no effect.
     */
    public void setFrom(URL u)
    {
      if (u==null) {definedURL=null; return;}
      // we wont refer to any urls or lmses
      if (u.getProtocol().equalsIgnoreCase("file"))
      this.definedURL=u;
      else
      this.definedURL=null;
    }

    ICModel myIC;

    /* Set the IC containng this */
    public void setIC(ICModel m)
    {
      myIC=m;
    }

    /** If this is a nested circuit, return the circuit it is nested in */
    public CircuitModel getParent()
    {
      if (myIC!=null)
      {
        return myIC.getCircuit();
      }
      return parent;
    }

    public boolean hasParent(URL u)
   {
     CircuitModel m = this;
     String s = staticUtils.getLastNameFromURL(u);
     if (u==null) return false;
     while (m!=null)
     {
       if (m.getFrom()!=null)
       {
         String r = staticUtils.getLastNameFromURL(m.getFrom());
         if (r.equals(s)) return true;
       }
       m=m.getParent();
     }
     return false;
    }

    public boolean hasIO()
    {
      Enumeration e =this.components.elements();
      while (e.hasMoreElements())
      {
        Object o = e.nextElement();
        if (o instanceof dlsim.DLSim.concrete.TerminalModel) {return true;}
      }
     return false;
    }

    public boolean hasChild(URL u)
    {
      Enumeration e =this.components.elements();
      if (u==null) return false;
      String uname = staticUtils.getLastNameFromURL(u);
      while (e.hasMoreElements())
      {
         Object c = e.nextElement();
         if (c instanceof ICModel)
         {
           ICModel icm = (ICModel)c;
           if (icm.ic.getFrom()!=null)
           {
           String icname = staticUtils.getLastNameFromURL(icm.ic.getFrom());
           if (icname.equals(uname)) return true;
           }
           if (icm.ic.hasChild(u)) return true;
         }
      }
       return false;
    }

}
