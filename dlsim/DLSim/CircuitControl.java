package dlsim.DLSim;

import java.util.*;
import java.awt.event.MouseEvent;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 *
 *
 * @author Matthew Leslie
 * @version 1.0
 */

public class CircuitControl extends PaintableContainerControl implements CircuitControlInterface {

  public CircuitControl(PaintableContainer pc)
  {
    super(pc);
  }

  public void deselect()
  {
    super.deselect(super.selection);
  }

  public void setSelection(Vector selection)
  {
    // translate from models into views
    Enumeration e = selection.elements();
    Vector paintables = new Vector();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      if (o instanceof ComponentModel)
      {
        paintables.add(((ComponentModel)o).getView());
      }
      if (o instanceof WireModel)
      {
        paintables.add(((WireModel)o).getView());
      }
      if (o instanceof Paintable)
      {
        paintables.add(o);
      }
    }
    super.setSelection(paintables);
  }

  public void selectAll()
  {
    super.setSelection(myContainer.getPaintables());
  }

  public Vector selectedWires()
  {
    Vector selectedWires = new Vector();
    Enumeration e =  super.selection.elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      if (o instanceof WireView)
      {
        if (o instanceof WireBundleView)
        {
        selectedWires.addAll(((WireBundleView)o).wb.getWires());
        }
        else
        {
        selectedWires.add(((WireView)o).w);
        }
      }
    }
    return selectedWires;
  }

  public Vector selectedComponents()
  {
    Vector selectedComponents = new Vector();
    Enumeration e =  super.selection.elements();
    while (e.hasMoreElements())
    {
      Object o = e.nextElement();
      if (o instanceof ComponentView)
      {
        selectedComponents.add(((ComponentView)o).getModel());
      }
    }
    Debug.out(selectedComponents.size()+" components returned as selected");
    return selectedComponents;
  }


  public void translateSelection(int x, int y)
  {
   super.translateSelection(x,y);
  }


}