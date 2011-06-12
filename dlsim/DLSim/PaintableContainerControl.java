package dlsim.DLSim;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 * <p>Title: PaintableContainerControl</p>
 * <p>Description: Manages selections popups and dragging for the container</p>

 * @author M.Leslie
 * @version 1.0
 */



public class PaintableContainerControl implements MouseListener, MouseMotionListener
{

Vector undermouse;
Vector selection = new Vector();
Vector movingPaintables = new Vector();
Point dragstartpoint;
PaintableContainer myContainer;
boolean selectionRectangle;
boolean moving;
boolean floatingSelection;
boolean draggingwire;
boolean addingwire;
CircuitViewInterface.FloatingSelection myfloatingSelection;

public PaintableContainerControl(PaintableContainer pc)
{
  myContainer=pc;
}

public void mouseMoved(MouseEvent e)
{
  if (floatingSelection)
  {
     floatingPaintable.setLocation(e.getPoint());
    if (floatingPaintable.getContainer()==null)
    {
      if (myContainer.getVisibleRect().contains(floatingPaintable.getShape().getBounds()))
       myContainer.addPaintable(floatingPaintable);
    }
  }
  if (addingWire)
  {
    continueWire(e);
    return;
  }
}

public void mouseDragged(MouseEvent e)
{
  if (selectionRectangle)
  {
    continueDrag(e);
    return;
  }
  if (moving)
  {
    continueMove(e);
    return;
  }
  if (draggingwire)
  {
    continueWire(e);
    return;
  }
  else
  {
    Terminal t = this.terminalAt(e);
    if (t!=null)
    {
      startWire(t);
      draggingwire=true;
      return;
    }
    undermouse  = myContainer.getSelectablesAt(e.getPoint());
    if (undermouse.isEmpty())
    {
      // start a selection rectangle
      startDrag(e);
    }
    else
      //start moving the thing under the mouse
      startMove(e);
  }
}

public void mousePressed(MouseEvent e)
{
  if (e.isPopupTrigger())
  {
    if (checkPopUps(e)) return;
  }
}

public void mouseReleased(MouseEvent e)
{
  if (e.isPopupTrigger())
  {
   if (checkPopUps(e)) return;
  }
  if (selectionRectangle)
  {
    endDrag(e);
    return;
  }
  if (moving)
  {
    endMove(e);
    return;
  }
  if (draggingwire)
  {
    endWire(terminalAt(e));
    draggingwire=false;
  }
}

public void mouseClicked(MouseEvent e)
{
  if (e.isPopupTrigger())
  {
    if (checkPopUps(e)) return;
  }
  if (floatingSelection)
  {
    // add floating selection to thing
    myContainer.removePaintable(floatingPaintable);
    floatingSelection=false;
    Point mouse = e.getPoint();
    Point p = floatingPaintable.getLocation();
    UICommand.addComponent(myfloatingSelection,p).execute();
    return;
  }

  // wire?
  Terminal t = this.terminalAt(e);
  if (addingWire)
  {
    endWire(t);
    return;
  }
  else
  {
    if (t!=null)
    {
      startWire(t);
      return;
    }
  }

  //paintable?
  Vector undermouse = myContainer.getSelectablesAt(e.getPoint());
  // do select/deselect
  if (shiftOrControl(e))
    toggleSelection(undermouse);
  else
    setSelection(undermouse);
  // pass through event
  if (!undermouse.isEmpty())
  ((Paintable)undermouse.firstElement()).clicked(e);
}

public void mouseEntered(MouseEvent e)
{
}

public void mouseExited(MouseEvent e)
{
}


/**
 * deselects these paintables
 * @param v  a vector containg the paintables to remove from the selection
 */
public void deselect(Vector v)
{
  v = (Vector) v.clone();
  Enumeration enum1 = v.elements();
  while (enum1.hasMoreElements())
  {
    Paintable p = (Paintable) enum1.nextElement();
    p.setSelected(false);
    selection.remove(p);
  }
}

/**
 * clears the selection and selects these paintables instead
 * @param v a vector containing paintables in the new selection
 */
public void setSelection(Vector v)
{
  deselect(selection);
  addToSelection(v);
}

/**
 * selects these paintables
 * @param v a vector containing paintables to select
 */
public void addToSelection(Vector v)
{
  Enumeration enum1 = v.elements();
  while (enum1.hasMoreElements())
  {
    Paintable p = (Paintable) enum1.nextElement();
    p.setSelected(true);
    selection.add(p);
  }
}

/**
 * If these components are selected, deselect them, and vice versa
 */
public void toggleSelection(Vector v)
{
  Enumeration enum1 = v.elements();
  while (enum1.hasMoreElements())
  {
    Paintable p = (Paintable) enum1.nextElement();
    if (p.getSelected())
    {
    p.setSelected(false);
    selection.remove(p);
    }
    else
    {
    p.setSelected(true);
    selection.add(p);
    }
  }
}

private void startDrag(MouseEvent e)
{
  dragstartpoint=e.getPoint();
  selectionRectangle=true;
}

private void continueDrag(MouseEvent e)
{
  Rectangle r = boundingRect(dragstartpoint,e.getPoint());
  myContainer.scrollRectToVisible(new Rectangle(e.getX(),e.getY(),1,0));
  myContainer.setSelectionRectangle(r);
}

private void endDrag(MouseEvent e)
{
  Vector v = myContainer.getSelectablesIn(boundingRect(dragstartpoint,e.getPoint()));
  if (shiftOrControl(e))
  addToSelection(v);
    else
  setSelection(v);
  dragstartpoint=null;
  myContainer.setSelectionRectangle(null);
  selectionRectangle=false;
}

private Point movingStart;
private Rectangle selRect;

private void startMove(MouseEvent e)
{
  Paintable um = (Paintable) undermouse.firstElement();

  if (um.getSelected())
  {
     // move the whole selection
    movingPaintables = selection;
  }
  else
  {
    // just move this one
    movingPaintables = undermouse;
  }
  movingStart=myContainer.getNearestGridPoint(e.getPoint());
  // we do scrolling ourself
  myContainer.setScrolls(false);
  moving=true;
}

private void continueMove(MouseEvent e)
{
  // get the nearest grid point to where we are
  Point movingEnd=myContainer.getNearestGridPoint(e.getPoint());
  // if havent moved return
  if (movingEnd.equals(movingStart)) return;
  // calc distance moved
  int dx = movingEnd.x - movingStart.x;
  int dy = movingEnd.y - movingStart.y;
  Enumeration enum1 = movingPaintables.elements();

  //calculate bounds
  Area bounds = new Area();
  while (enum1.hasMoreElements())
  {
    Object o = enum1.nextElement();
    if (o instanceof ComponentView)
    {
      bounds.add(new Area( ((Paintable)o).getShape().getBounds()));
    }
  }
  selRect=bounds.getBounds();
  System.out.println(selRect+","+dx+","+dy);
  if ( (selRect.getMinX()+dx<0) || (selRect.getMinY()+dy<0) )
  {
    if ( (dx<0) && (selRect.getMinX()+dx<0) )
    dx = (int) -selRect.getMinX();// no going out of bounds
    if ( (dy<0) && (selRect.getMinY()+dy<0))
    dy = (int) -selRect.getMinY();
  }
  // move components
  enum1 = movingPaintables.elements();
  while (enum1.hasMoreElements())
  {
    Paintable p = (Paintable) enum1.nextElement();
    p.translate(dx,dy);
  }
  //scroll
  myContainer.makeRectangleVisible(selRect);
  movingStart=movingEnd;
}

public void translateSelection(int dx, int dy)
{
  Enumeration enum1 = selection.elements();
  while (enum1.hasMoreElements())
  {
    Paintable p = (Paintable) enum1.nextElement();
    p.translate(dx,dy);
  }
}
private void endMove(MouseEvent e)
{
  // we no longer do scrolling ourself
  myContainer.setScrolls(true);
  moving=false;
}


private boolean shiftOrControl(MouseEvent e)
{
  return ( (e.getModifiers() & MouseEvent.SHIFT_MASK)!=0) ||
      (((e.getModifiers() & MouseEvent.CTRL_MASK)!=0));
}

public static Rectangle boundingRect(Point p1, Point p2)
{
  int x1,x2,y1,y2;
  x1 = java.lang.Math.min(p1.x,p2.x);
  x2 = java.lang.Math.max(p1.x,p2.x);
  y1 = java.lang.Math.min(p1.y,p2.y);
  y2 = java.lang.Math.max(p1.y,p2.y);
  return new Rectangle(x1,y1,(x2-x1),(y2-y1));
}

private boolean checkPopUps(MouseEvent e)
{
  Vector v = myContainer.getSelectablesAt(e.getPoint());
  if (v.isEmpty())
  {
    return false; // popup not triggered
  }
  else
  {
    Paintable p = (Paintable)v.firstElement();
    JPopupMenu jpm = p.getMenu();
    if (jpm!=null)
    {
      jpm.show(myContainer,e.getX(),e.getY());
      return true;
    }
    else
    {
      return false;
    }
  }
}

Paintable floatingPaintable;

// a hack to make this more easily compatiable with DLSim
public void setFloatingSelection(CircuitViewInterface.FloatingSelection c)
 {
   if (moving || selectionRectangle) return;
   // kill old floating selection
   if (floatingSelection)
   {
    if (floatingPaintable.getContainer()!=null)
     floatingPaintable.getContainer().removePaintable(floatingPaintable);
   }
   floatingSelection=true;
   myfloatingSelection=c;
   floatingPaintable = paintableIcon(c.getIcon());
  }

// a paintable to represent the floating icon
private Paintable paintableIcon(final Icon c)
{
  return new Paintable()
  {
    Point location = new Point(0,0);
    Rectangle me = new Rectangle(0,0,c.getIconWidth(),c.getIconHeight());

    public Point getLocation()
    {
      return (Point)location.clone();
    }

    public void setLocationImpl(Point p)
    {
      Point p2 = new Point(p.x-(c.getIconWidth()/2),p.y-(c.getIconHeight()/2));
        me.setLocation(p2);
        location=p2;
    }
    public void setSelectedImpl(boolean b)  { }
    public boolean getSelected(){return false;}
    public void paint(Graphics g)
    {
      if (getContainer()!=null)
      c.paintIcon(this.getContainer(),
                  g,
                  location.x,
                  location.y);
    }
    public Shape getShape()
    {
      return me;
    }
  };
}


// stuff specific to DLSim
private boolean addingWire;
Terminal startTerminal;

private void startWire(Terminal t)
{
  if (t!=null)
  {
  addingWire=true;
  startTerminal=t;
  startTerminal.select(true);
  }
}

private void continueWire(MouseEvent e)
{
  myContainer.scrollRectToVisible(new Rectangle(e.getX(),e.getY(),1,0));
  myContainer.setSelectionLine(startTerminal.getPoint(),e.getPoint());
}

private void endWire(Terminal t)
{
  if (t!=null)
  {
    addWire(startTerminal,t);
  }
  myContainer.setSelectionLine(null,null);
  addingWire=false;
  startTerminal.select(false);
  startTerminal=null;
}


private class Terminal
{
  Terminal(ComponentModel component, int number, boolean isOutput)
  {
    this.number=number;
    this.isoutput=isOutput;
    this.component=component;
  }
  int number;
  boolean isoutput;
  ComponentModel component;

  public void select(boolean selected)
  {
    if (isoutput)
    component.getControl().selectOutput(number,selected);
    else
    component.getControl().selectInput(number,selected);
  }

  public Point getPoint()
  {
    if (isoutput)
      return component.getView().getPositionOfOutput(number);
    else
      return component.getView().getPositionOfInput(number);
  }
}

private Terminal wireStart;
private Terminal wireEnd;

private Terminal terminalAt(MouseEvent e)
{
   Vector undermouse = myContainer.getSelectablesAt(e.getPoint());
   if (undermouse.isEmpty()) return null;
   Enumeration enum1 =  undermouse.elements();
   while (enum1.hasMoreElements())
   {
     Object o = enum1.nextElement();
     if (o instanceof ComponentView)
     {
       ComponentView cv = (ComponentView) o;
       int input = cv.getInputAt(e.getPoint());
       if (input!=-1)
       {
         return new Terminal(cv.getModel(),input,false);
       }
       int output = cv.getOutputAt(e.getPoint());
       if (output!=-1)
       {
         return new Terminal(cv.getModel(),output,true);
       }
     }
   }
   return null;
}

private void addWire(Terminal start, Terminal end)
{
  if (!start.isoutput) {Terminal temp=start; start=end;end=temp;}
  if  (!( (start.isoutput) && (!end.isoutput) ) )
    {
	  dlsim.DLSim.Util.staticUtils.errorMessage("Wire must be between an input and an output");
    return;
    }
  else
  {
    UICommand.addWire(start.component,start.number,end.component,end.number).execute();
  }
}


}


