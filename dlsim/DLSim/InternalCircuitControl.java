package dlsim.DLSim;

import java.awt.Graphics;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import dlsim.DLSim.Util.Observer;
import dlsim.DLSim.*;
import dlsim.DLSim.concrete.*;
import javax.swing.*;

/**
 * <p>Title: DLSim</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Matthew Leslie</p>
 * <p>Company: Keble College, Oxford</p>
 * @author Matthew Leslie
 * @version 1.0
 */

public class InternalCircuitControl extends PaintableContainerControl implements CircuitControlInterface
{
  CircuitModel m;
  ICModel comp;
  PaintableContainer pc;
  public InternalCircuitControl(PaintableContainer pc,CircuitModel m,ICModel comp)
  {
    super(pc);
    this.pc=pc;
    this.m=m;
    this.comp=comp;
  }

  public void selectAll()
  {

  }

  public Vector selectedWires()
  {
    return null;
  }

  public Vector selectedComponents()
 {
  return null;
  }

  public boolean wireIsSelected()
  {
   return false;
  }

  public void paint(Graphics g)
  {

  }
  public void doCopy()
  {

  }
  public void doDelete()
  {

  }
  public void deselect()
  {

  }
  public void translateComponents(int x, int y, Vector components)
  {

  }
  public void translateSelection(int x, int y)
  {

  }
  public Rectangle getSelectionArea()
  {
    return null;
  }
  public void setSelection(Vector Components)
  {

  }



  public void mouseClicked(MouseEvent e)
  {
    if (e.isPopupTrigger()) popups(e);
    if (e.getClickCount()<2) return;
    ComponentModel undermouse = modelAt(e.getPoint());
    if (undermouse==null) {return;}
    if (undermouse instanceof TerminalModel) return;
    undermouse.getControl().Clicked(e);
    if (comp.getInternal().isValid())
      return;
    else
      comp.inValidate();
  }

  /** Returns the component at a point */
 private ComponentModel modelAt(Point p)
 {
   Enumeration e = pc.getPaintablesAt(p).elements();
   while (e.hasMoreElements())
   {
     Object o = e.nextElement();
     if (o instanceof ComponentView) return ((ComponentView)o).getModel();
   }
   return null;
 }

 public void popups(MouseEvent e)
 {
   ComponentModel m = modelAt(e.getPoint());
   JPopupMenu jpm = new JPopupMenu();
   if (m!=null) {
     if (m instanceof ICModel)
     {
       jpm.add(((ICModel)m).open);
     }
     if (m instanceof OscilloscopeComponentModel)
     {
       jpm.add(((OscilloscopeComponentModel)m).openAction);
     }
   }
   jpm.add(UIInternalCircuitFrame.editInternal(comp.getInternal()));
   jpm.show((Component)this.m.getView(),e.getPoint().x,e.getPoint().y);
 }


  public void mousePressed(MouseEvent e)
  {
    if (e.isPopupTrigger()) popups(e);
  }
  public void mouseReleased(MouseEvent e)
  {
    if (e.isPopupTrigger()) popups(e);
  }
  public void mouseEntered(MouseEvent e)
  {

  }
  public void mouseExited(MouseEvent e)
  {

  }
  public void mouseDragged(MouseEvent e)
  {

  }
  public void mouseMoved(MouseEvent e)
  {

  }



}