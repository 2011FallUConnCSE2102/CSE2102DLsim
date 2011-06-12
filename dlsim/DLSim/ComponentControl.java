
/**
 * Title:        DLSim<p>
 * Description:  <p>
 * Copyright:    Copyright (c) Matthew Leslie<p>
 * Company:      Keble College, Oxford<p>
 * @author Matthew Leslie
 * @version 1.0
 */
package dlsim.DLSim;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import dlsim.DLSim.Util.*;

public class ComponentControl implements Observable {


  ComponentModel m;
  private boolean[] inputs;
  private boolean[] outputs;
  private Vector observers = new Vector();
  private boolean selected=false;


  public ComponentControl(ComponentModel m)
  {
  this.m=m;
  outputs = new boolean[m.getNumberOutputs()];
  inputs = new boolean[m.getNumberInputs()];
  }

  public boolean componentIsSelected()
  {
  return selected;
  }

  public void select(boolean selected)
  {
  this.selected=selected;
  if (selected==false) deSelectTerminals(); //deselect inputs & outputs
  }

  public void deSelectTerminals()
  {
   for (int i=0; i<inputs.length;i++)
       {
        inputs[i]=false;
       }
   for (int i=0; i<outputs.length;i++)
       {
        outputs[i]=false;
       }
  repaint();
  }


  public boolean inputIsSelected(int input)
  {
  return inputs[input];
  }

  public void deselectInputs()
  {
   for (int i=0; i<inputs.length;i++)
      {
       inputs[i]=false;
      }
    repaint();
  }

  public void deselectOutputs()
  {
   for (int i=0; i<outputs.length;i++)
      {
       outputs[i]=false;
      }
   repaint();
  }

  private void repaint()
  {
    m.getView().repaint();
  }

  public void selectInput(int input,boolean selected)
  {
  inputs[input]=selected;
  repaint();
  }

  public boolean outputIsSelected(int output)
  {
  return outputs[output];
  }

  public void selectOutput(int output,boolean selected)
  {
  outputs[output]=selected;
  repaint();
  }



  /** Events passed down from the CircuitControl */
  public void Clicked(MouseEvent e)
  {
    if (e.getClickCount()!=1) return;
   Point p = e.getPoint();
   ComponentView cv = m.getView();
   // check for terminals
   if (cv.isTerminalAt(e.getPoint()))
      {
        int i=cv.getInputAt(p); //check for inputs
        if (i!=-1)
           {
            if ((e.getModifiers() & InputEvent.CTRL_MASK)==0) deselectInputs();
            this.selectInput(i,true);
           }
        else
        {
           i=cv.getOutputAt(p); //check for outputs
           if (i!=-1)
           {
            if ((e.getModifiers() & InputEvent.CTRL_MASK)==0) deselectOutputs();
            this.selectOutput(i,true);
           }
        }
      }


  }




   /** Attach an Observer to this control
    * @param observer The class that wants to observe this control
    */
  public void attach(Observer observer)
  {
        observers.add(observer);
  }

    /** Detach an Observing class from control
    * @param observer The class that wants to detacg
    */
  public void detach(Observer observer){
        observers.remove(observer);
    }


    private static Operation informAll()
    {
        return new Operation()
        {
            public Object op(Object o)
            {
                                ((Observer)o).update();
                                return null;
            }
        };
    }


    /** This visits all the observers and calls 'update' on them */
    public void inform()
    {
                this.informAll().applyOp(observers);
    }
}