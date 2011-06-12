package dlsim.DLSim;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class WireBundle
{
  Vector wiresInBundle;
  WireView wbv;
  CircuitModel myCircuit;

  /**
   * Create a wire bundle containing just this wire
   * @param w Wire
   */
  public WireBundle(WireModel w)
  {
    wiresInBundle = new Vector();
    this.setView(w.getView());
    w.setBundle(this);
    myCircuit=w.getTo().getCircuit();
    addWire(w);
  }

  /**
   * Create an empty wire bundle
   */
  public WireBundle()
  {
    wiresInBundle = new Vector();
  }

  public boolean getSelected()
  {
    return sel;
  }

  public void setSelected(boolean selected)
  {
    sel=selected;
  }

  boolean sel =false;
  /**
   * Add a bundle of wires to this one. The old bundle is destroyed
   * @param wb The wirebundle to add to this one
   */
  public void addBundle(WireBundle wb)
  {
    Enumeration wires = wb.getWires().elements();
    while (wires.hasMoreElements())
    {
      WireModel w = (WireModel)wires.nextElement();
      addWire(w);
      myCircuit=w.getTo().getCircuit();
    }

  }

  /**
   * All the wires in this bundle go back to being indivdual wires on thier own
   */
  public void debundle()
  {
    Enumeration wires = this.getWires().elements();
    while (wires.hasMoreElements())
    {
      WireModel w = (WireModel)wires.nextElement();
      w.setBundle(new WireBundle(w));
      w.setView(new WireView(w));
      w.getTo().getView().removeLocationListener(wbv);
      w.getFrom().getView().removeLocationListener(wbv);
      if (w!=getWires().firstElement())
      {
        wiresInBundle.remove(w);
      }
    }

  }

  public void setView(WireView w)
  {
    wbv=w;
  }

  public WireView getView()
  {
    return wbv;
  }

  /**
   *
   * @return  All the WireModels in this bundle
   */
  public Vector getWires()
  {
    return ((Vector)wiresInBundle.clone());
  }

  public JPopupMenu getMenu()
  {
    final WireBundle me=this;
    JPopupMenu jpm = new JPopupMenu();
    AbstractAction unb = new AbstractAction("Unbundle")
    {
      public void actionPerformed(ActionEvent e)
      {
        UICommand.unbundle(me).execute();
      }
    };

    AbstractAction del = new AbstractAction("Delete")
    {
      public void actionPerformed(ActionEvent e)
      {
        UICommand.deleteComponents(new Vector(),getWires(),myCircuit).execute();
      }
    };
    jpm.add(unb);
    return jpm;
  }

  public int size()
  {
    return wiresInBundle.size();
  }

  private void addWire(WireModel addw)
  {
    if (wiresInBundle.contains(addw)) return;
    wiresInBundle.add(addw);
    if (wiresInBundle.size()>1)
    {
      wbv = new WireBundleView(this);
      Enumeration wires = this.getWires().elements();
      while (wires.hasMoreElements())
      {
        WireModel w = (WireModel)wires.nextElement();
        w.setBundle(this);
        w.setView(wbv);
        w.getTo().getView().addLocationListener(wbv);
        w.getFrom().getView().addLocationListener(wbv);
      }
    }
  }


}