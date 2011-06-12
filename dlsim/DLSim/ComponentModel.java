package dlsim.DLSim;
import dlsim.DLSim.Util.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.*;
import java.util.*;
import dlsim.DLSim.concrete.GenericComponentView;

import java.awt.event.*;
/**
 * ComponentModel. An abstract componet, which models the behaviour of an arbitary logic gate
 * an implementinc class merely needs to set inputs and outputs in its constructor,
 * and initalise inwires and outwires
 * @version 1.0
 */
public abstract class ComponentModel implements dlsim.DLSim.Util.Observable, contextSensitiveComponent
{

    private CircuitModel circuit; // the cicuit of which this component is a member

    private Vector inwires; // A vector of wires for each input terminal

    private Vector outwires; // A vector of wires for each output terminal

    private boolean[] inputs; // The signal at each input

    private boolean[] outputs; // The signal at each output

    public abstract boolean[] doLogic(boolean[] input); // The logicoperation performed on inputs

    public abstract int getSize();

    private boolean valid=false; //false if outputs are not upto date

    private Vector observers = new Vector(); //private record of dependants

    private ComponentView myView;

    private ComponentControl myControl;

    public ComponentModel(int inputs, int outputs,CircuitModel c)
    {
        this.inputs= new boolean[inputs];
        this.outputs= new boolean[outputs];
        inwires = new Vector(2*inputs);
        outwires = new Vector(2*outputs);
        circuit=c;
        myView=new GenericComponentView(this);
        myControl=new ComponentControl(this);
        setFalse(this.inputs);
        setFalse(this.outputs);
        c.addComponent(this);
    }

    public JPopupMenu getMenu()
    {
      JPopupMenu myMenu = new JPopupMenu();
      myMenu.add(copy);
      myMenu.addSeparator();
      myMenu.add(deleteMe);
      myMenu.add(deleteOut);
      myMenu.add(deleteIn);
      return myMenu;
    }

    final ComponentModel me=this;

    public AbstractAction deleteMe = new AbstractAction("Delete")
   {
     public void actionPerformed(ActionEvent e)
     {
       Vector c = new Vector(1);
       Vector w = new Vector(2);
       c.add(me);
       UICommand.deleteComponents(c,w,me.getCircuit()).execute();
     }
   };

   public AbstractAction deleteOut = new AbstractAction("Delete Outgoing Wires")
   {
     public void actionPerformed(ActionEvent e)
     {
       Vector c = new Vector(1);
       Vector w = me.getOutputWires();
                  UICommand.deleteComponents(c,w,me.getCircuit()).execute();
     }
   };

   public AbstractAction deleteIn = new AbstractAction("Delete Incoming Wires")
   {
   public void actionPerformed(ActionEvent e)
   {
     Vector c = new Vector(1);
     Vector w = me.getInputWires();
                UICommand.deleteComponents(c,w,me.getCircuit()).execute();
   }
   };

   public AbstractAction copy = new AbstractAction("Copy")
   {
     public void actionPerformed(ActionEvent e)
     {
       Vector c = new Vector(1);
       c.add(me);
       Vector w = new Vector(1);
       UICommand.copy(c,w,me.getCircuit()).execute();
     }
   };

    private void setFalse(boolean[] ar)
    {
      for(int i=0;i<ar.length;i++)
              {
               ar[i]=false;
              }
    }
     /**
     * @link
     * @shapeType PatternLink
     * @pattern <{Observer}>
     * @clientRole   Subject
     * @supplierRole <{Observer}>
     */
    /*#ComponentView lnkComponentView;*/



    public ComponentView getView() {return myView;}


    public void setView(ComponentView w)
    {
    myView=w;
    }

    public ComponentControl getControl() {return myControl;}

    public void setControl(ComponentControl cc)
    {
    myControl=cc;
    }

        /** getNumber()
    * @return The number of inputs this component has
    */
    public int getNumberInputs() {return inputs.length;}

    /** getNumberOutputs()
    * @return The number of outputs this component has
    */
    public int getNumberOutputs() {return outputs.length;}

    /** setInput(index,value)
    *  this automatically invalidates the component if the signal has changed
    * @param index The index of the input to set
    * @param value The value to set this signal too
    */
    public void setInput(int index, boolean value)
    {
        if (value==inputs[index]) return;
        this.inValidate();
        if ( (value) ) // any high signals result in this being high
           {inputs[index] = value;}
        else // one low is not enough to set this input low. must check all others.
           {
            //enumerate other wires to see if any of them are still 'high'
            Enumeration e = this.getWiresToInput(index).elements();
            boolean OR=false;
            while (e.hasMoreElements() && !OR)
             {
              OR = (OR || ((WireModel)e.nextElement()).getValue());
             }
           inputs[index]=OR;
           }
    }


    /**
    * @param index The index of the output whose value we want
    * @return The Signal at this index
    */
    public boolean getOutput(int index) { return outputs[index]; }

    /**
    * @param index The index of the input whose value we want
    * @return The Signal at this index
    */
    public boolean getInput(int index) { return inputs[index]; }

    /**
    * Calculates the new outputs, notifies observers of change.
    */
    private void calculateOutputs()
    {

        boolean [] newoutputs = doLogic(inputs);
        if (newoutputs!=null)
        if (arrayequal(newoutputs,outputs))
                { return; }
        else
        {
            outputs=newoutputs;
            inform(); // notify dependandts that values have changed
        }
    }

    /** An operation for use on Vectors/arrays of components.
     *  calls calculate output on all members
     */
    public static Operation calculateAllOutputs()
    {
        return new Operation()
        {
            public Object op(Object o)
            {
                                ((ComponentModel)o).calculateOutputs();
                                return null;
            }
        };
    }

    public static boolean arrayequal(boolean[] a,boolean[] b)
    {
                if (a.length!=b.length) return false;
        boolean x=true;
        for (int i=0;((i<a.length) && x);i++)
                        {
                x=x&&(a[i]==b[i]);
            }
        return x;
    }

    public void addInputWire(WireModel w)
    {
                if (inwires.contains(w)) return; // only one instance of a wire
                inwires.addElement(w);
                w.send();
    }

    public void addOutputWire(WireModel w)

    {
                if (outwires.contains(w)) return; // only one instance of a wire
                outwires.addElement(w);
    }

    public void removeInputWire(WireModel w)
    {
                Vector v = getWiresToInput(w.getInputTerminalNumber());
                boolean hasOtherSignal=false;
                if (v.size()>1) // set this input to low if there are now no other input wires
                   {
                    Enumeration e = v.elements();
                    while (e.hasMoreElements() && !hasOtherSignal)
                          {
                            WireModel wire= ((WireModel)e.nextElement());
                            if ( (wire!=w) && (wire.getValue()) )
                            hasOtherSignal=true;
                          }
                   }
                if (!hasOtherSignal)
                inputs[w.getInputTerminalNumber()] = false;
                inwires.remove(w);
                inValidate();
    }

    public void removeOutputWire(WireModel w)
    {
                outwires.remove(w);
    }

    public Vector getWiresToInput(final int inputnumber)
    {
       Operation o = new Operation()
                 {
                 public Object op(Object input)
                        {
                         WireModel w = (WireModel)input;
                         if (w.getInputTerminalNumber()==inputnumber)
                         return w;
                         else
                         return null;
                        }
                 };
      return o.mapOp(inwires);
    }


    public Vector getWiresFromOutput(final int outputnumber)
    {
        Operation o = new Operation()
                 {
                 public Object op(Object input)
                        {
                         WireModel w = (WireModel)input;
                         if (w.getOutputTerminalNumber()==outputnumber)
                         return w;
                         else
                         return null;
                        }
                 };
      return o.mapOp(outwires);
    }

    public Vector getOutputWires()
    {
        return (Vector)outwires.clone();
    }

    public Vector getInputWires()
    {
        return (Vector)inwires.clone();
    }


    public boolean isValid()
    {
     return valid;
    }

    public void removeAllWires()
    {
      this.inwires=new Vector();
      this.outwires=new Vector();
    }

    public void inValidate()
    {
        if (this.valid==false) return; //already invlaid
        this.valid=false;
        circuit.invalidate(this);
    }

    public CircuitModel getCircuit()
    {
    return circuit;
    }

    public void validate()
    {
        this.valid=true; // Reset internal valid variable
        this.calculateOutputs(); // Calculate new outputs
    }

    public void attach(dlsim.DLSim.Util.Observer observer){
        observers.add(observer);
    }

    public void detach(dlsim.DLSim.Util.Observer observer){
        observers.remove(observer);
    }


    private static Operation informAll()
    {
        return new Operation()
        {
            public Object op(Object o)
            {
                                ((dlsim.DLSim.Util.Observer)o).update();
                                return null;
            }
        };
    }


    // This visits all the observers and calls 'update' on them
    public void inform()
    {
                this.informAll().applyOp(observers);
    }

    /** Called when this is removed from the circuit
     * can be overridden to close any resources this is using
     */
    public void removed()
    {
    }

    /** Called whenever this is added to the circuit *
     * can be overridden to create resources
     */
    public void added()
    {
    }



}
