package dlsim.DLSim;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseMotionListener;

/**
 * <p>Title: DLSim</p>
 * <p>Description: CircuitView  - the graphical representation of a circuitmodel</p>
 *
 * @author Matthew Leslie
 * @version 1.0
 */

public class CircuitView extends PaintableContainer implements CircuitViewInterface {

  public static Color grid = new Color(50,100,217);
  public static Color background = new Color(0,0,192);
  CircuitModel m;

  CircuitControl cc;
  public CircuitView(CircuitModel m)
  {
    super();
    this.setBackground(background);
    cc = new CircuitControl(this);
    super.setControlClass(cc);
  }

  public void add(Paintable p)
  {
    // the paintable container takes care of this
    super.addPaintable(p);
  }

  public void remove(Paintable p)
  {
    // the paintable container takes care of this
    super.removePaintable(p);
  }


  // store dirty terminals here
  private Vector dirtyTerminals = new Vector();

  public void requestUpdateTerminals(ComponentView cv)
  {
    // make sure cant add while clearing
    synchronized (dirtyTerminals)
    {
      // add to list of dirty terminals
      dirtyTerminals.add(cv);
    }
  }

  public void updateTerminals()
  {
    synchronized (dirtyTerminals)
    {
      Enumeration enum1 = dirtyTerminals.elements();
      // immediate mode painting
      Graphics g = this.getGraphics();
      // dont paint bits outside of visible rect?
      // g.setClip(this.getVisibleRect());
      // step through list
      while (enum1.hasMoreElements())
      {
        ComponentView cv = (ComponentView) enum1.nextElement();
        cv.refreshTerminals(g);
      }
      // clear list of waiting components
      dirtyTerminals.clear();
    }
  }

  public void setControl(CircuitControlInterface cif)
  {
    if (cif instanceof PaintableContainerControl)
    {
      super.setControlClass((PaintableContainerControl)cif);
    }
    else throw  new java.lang.UnsupportedOperationException("Cant set control");
  }

  public CircuitControlInterface getControl()
  {
    return (CircuitControlInterface)super.getControlClass();
  }


}
